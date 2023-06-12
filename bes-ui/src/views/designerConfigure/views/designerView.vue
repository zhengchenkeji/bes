<template>
  <div>
    <div ref="page"
         :style="{height:max_height,overflow: overflow}">
      <!--         :style="topNav?-->
      <!--         '{max-height:max_height}'`max-height(${max_height}) ;overflow-y: hidden;overflow-x: hidden;height: 89vh;`:-->
      <!--         `max-height(${max_height}) ;overflow-y: hidden;overflow-x: hidden;height: 89vh;`">-->
      <div ref="container" @mousemove="onMousemove" class="bg">
        <div class="canvas-container" ref="content"
             @wheel="onWheel" @mousedown="onMouseDown" @mouseup="onMouseup"
             :style="pageName=='viewsIndex'?{transform: `scale(${scale}) translate(${translate.x}px, ${translate.y}px)`}:
             {transform: `scale(${scale}) translate(${translate.x}px, ${translate.y}px)`,position: 'absolute'}">
          <!--transformOrigin: '10% 20%',  ,left:offsetX + 'px', top: offsetY + 'px'  position: 'absolute'-->
          <div
            class="canvas"
            :style="{
                    ...getCanvasStyle(canvasStyleData),
                    width: changeStyleWithScale(canvasStyleData.width) + 'px',
                    height: changeStyleWithScale(canvasStyleData.height) + 'px',
                }"
          >
            <ComponentWrapper
              v-for="(item, index) in copyData"
              :key="(index + keyValue)"
              :config="item"
              :keyValue="(index + keyValue)"
            />


          </div>

          <!--          <div style="position:absolute; ">-->

          <!--            <el-button @click="back">返回</el-button>-->
          <!--            <el-button @click="screenfullDiv">全屏</el-button>-->
          <!--          </div>-->
        </div>
      </div>
      <suspension-ball v-if="ShowBack == true" @back="goBackHomePage" @screenfullDiv="screenfullDiv"
                       @refreshPage="refreshPage"></suspension-ball>
    </div>

  </div>

</template>

<script>
  import screenfull from "screenfull";
  import {getCanvasStyle, getStyle} from '@/utils/designer/style'
  import {mapState} from 'vuex'
  import ComponentWrapper from '@/components/designer/Editor/ComponentWrapper'
  import {changeStyleWithScale} from '@/utils/designer/translate'
  import {deepCopy} from '@/utils/designer/utils'
  import {seleteDesignerAreaPage} from "@/api/designerConfigure/designer";
  import {setDefaultcomponentData} from "@/store/designer/snapshot";
  import pubsub from "@/store/modules/PubSub";

  import suspensionBall from "@/views/designerConfigure/views/suspensionBall";
  import {getAllRealTimeData} from "@/api/basicData/deviceManagement/deviceTree/deviceTree";
  import {pointType} from "@/store/modules/pointType";
  import {debugPointListRealTimeInfo} from "@/api/basicData/deviceManagement/deviceTree/deviceTreePoint";

  export default {
    components: {ComponentWrapper, suspensionBall},
    name: "index",
    props: {
      areaId: {
        type: String,
        default: "",
      },
      pageId: {
        type: String,
        default: "",
      },
      isScreenshot: {
        type: Boolean,
        default: false,
      },
      keyValue: {
        type: Object,
        required: true,
        default: () => {
        },
      },
      ShowBack: {
        type: Boolean,
        default: true,
      },
      pageName: {
        type: String,
        default: "",
      }
    },

    data() {
      return {
        overflow: 'hidden',
        height: "89vh",
        max_height: "84.5vh",
        // topNav: this.$store.state.settings.topNav,
        copyData: [],
        // areaId: "1",//区域id
        // pageId:"1",//页面id
        form: {},
        scale: 1,
        offsetX: 0,
        offsetY: 0,
        isDragging: false,
        startX: 0,
        startY: 0,
        lastX: 0,
        lastY: 0,
        translate: {
          x: 0,
          y: 0
        },
        moveStart: {},
        pointType: pointType,
        timer: null,//定时器
      }
    },
    computed: {
      ...mapState([
        'componentData',
        'canvasStyleData',
        'ToViewPage'
      ]),
      topNav: {
        get() {
          return this.$store.state.settings.topNav
        }
      },
      key() {
        return this.$route.path
      },
    },
    created() {
      // this.areaId = this.$route.query && this.$route.query.areaId;
      // this.pageId = this.$route.query && this.$route.query.pageId;
      // this.form.id = parseInt(this.pageId);
      // this.form.areaId = parseInt(this.areaId);
      // this.getAreaDesignerPage(this.form);

    },

    watch: {
      pageId: function (newVal, oldVal) {

      },
      pageName(val) {

        if (val == 'viewsIndex') {
          this.max_height = '80.5vh'
        }
      }
    },

    beforeDestroy() {
      this.$store.commit('setToViewPage', false)
      this.$store.commit('setPointRealTimeData', [])
      pubsub.destructionAllDesignerSub();//清除设计器所有订阅
      if (this.timer) { //如果定时器还在运行 或者直接关闭，不用判断
        clearInterval(this.timer); //关闭
      }
    },
    methods: {
      getStyle,
      getCanvasStyle,

      changeStyleWithScale,

      //获取当前区域的设计器页面
      getAreaDesignerPage(form) {


        this.$store.commit('setToViewPage', true)
        let that = this;
        seleteDesignerAreaPage(form).then(response => {

          this.scale = 1;
          this.translate.x = 0;
          this.translate.y = 0;

          if (response.code == 200) {
            let canvasData = JSON.parse(response.data[0].canvasData);
            let canvasStyle = JSON.parse(response.data[0].canvasStyle);

            setDefaultcomponentData(canvasData);
            that.$store.commit('setComponentData', canvasData);
            that.$store.commit('setCanvasStyle', canvasStyle);

            let aaa = deepCopy(this.componentData);
            console.log(aaa)
            this.$set(that, 'copyData', deepCopy(this.componentData))

            //解析canvasData数据,判断如果是第三方协议,那就走定时器,5秒一发请求实时数据
            console.log(canvasData)
            if (canvasData == null || canvasData.length == 0) {
              return
            }

            let pointList = [];
            for (let i = 0; i < canvasData.length; i++) {
              if (canvasData[i].component == "VText" || canvasData[i].component == "LineShape" || canvasData[i].component == "Light") {
                let pointTypes = canvasData[i].linkage.data[0].point[0].pointType;
                let id = null;
                if (typeof pointTypes == 'undefined' || pointTypes == "") {
                  continue;
                }
                if (pointTypes == this.pointType.BES_POINT_TYPE) {
                  id = canvasData[i].linkage.data[0].point[0].id//点位id
                } else if (pointTypes == this.pointType.OTHER_POINT_TYPE) {
                  id = canvasData[i].linkage.data[0].point[0].sysName//功能id
                }
                let equipmentId = canvasData[i].linkage.data[0].point[0].pId;
                let pointType = canvasData[i].linkage.data[0].point[0].pointType;

                if (id == "") {
                  continue;
                }
                let param = {};
                param.id = id;
                param.equipmentId = equipmentId;
                param.pointType = pointType;
                // param.component = canvasData[i].component;
                pointList.push(param);
              }
            }
            if (pointList.length == 0) {
              return;
            }
            this.getRealTimeDataAll(pointList);
            //定时获取点位实时数据

          }
        })
      },
      //获取当前点位的实时数据
      getRealTimeDataAll(pointList) {
        getAllRealTimeData(JSON.stringify(pointList)).then(res => {
          if (res.code == 200) {
            let idList = [];
            let dataIndexArray = [];
            for (let i = 0; i < res.data.length; i++) {
              let id = Object.keys(res.data[i])[0];
              idList.push(id);
              if (Object.values(res.data[i])[0].value != null && Object.values(res.data[i])[0].value != undefined && Object.values(res.data[i])[0].value != '') {

                let params = {};
                params.equipmentId = Object.values(res.data[i])[0].equipmentId;//设备id
                params.functionId = Object.values(res.data[i])[0].id;//功能id
                params.pointType = Object.values(res.data[i])[0].pointType;
                params.id = id;
                params.value = (Object.values(res.data[i])[0].value).toString();
                if (Object.values(res.data[i])[0].unit == null) {
                  params.unit = '';
                } else {
                  params.unit = (Object.values(res.data[i])[0].unit).toString();
                }

                dataIndexArray.push(params)

              }
            }


            // const removeDuplicateObj = (arr) => {
            //   let obj = {};
            //   arr = arr.reduce((newArr, next) => {
            //     obj[next.id] && obj[next.equipmentId] && obj[next.functionId] && obj[next.pointType]  ? "" : (obj[next.id] = true && obj[next.equipmentId] = true && obj[next.functionId] = true && obj[next.pointType] = true && newArr.push(next));
            //     return newArr;
            //   }, []);
            //   return arr;
            // };
            // removeDuplicateObj(dataIndexArray);

            const map = new Map();
            const newArr = dataIndexArray.filter(v => !map.has(v.id + ":" + v.equipmentId + ":" + v.functionId + ":" + v.pointType) && map.set(v.id + ":" + v.equipmentId + ":" + v.functionId + ":" + v.pointType, 1));
            console.log(newArr);

            this.$store.commit('setPointRealTimeData', newArr);
            pubsub.on(idList, (data) => {
              this.VLightWebsocket1(data)
            }, 'VLightWebsocket1')


            setTimeout(() => {
              //定时器开始执行
              this.timerBegin(pointList);
            }, 100)


          }
        })
      },

      //定时器开始执行
      timerBegin(pointList) {
        if (this.timer != null) {
          clearInterval(this.timer);
          this.timer = null
        }
        this.timingGetRealData(pointList);

        this.timer = setInterval(() => {
          this.timingGetRealData(pointList)
        }, 1000 * 10)
      }
      ,

      //定时下发获取实时数据
      timingGetRealData(pointList) {
        debugPointListRealTimeInfo(JSON.stringify(pointList)).then(responent => {

        })
      }
      ,
      VLightWebsocket1(data) {

      }
      ,
      back() {
        this.$emit('back');
      }
      ,
      screenfullDiv() {
        if (screenfull.isEnabled) {
          screenfull.toggle(this.$refs.page);
        }
      }
      ,
      goBackHomePage() {
        this.$emit('back');
      }
      ,
      onWheel(event) {

        const {deltaX, deltaY} = event;
        const scaleDelta = 0.05;
        const newScale = deltaY > 0 ? this.scale - scaleDelta : this.scale + scaleDelta;
        this.scale = Math.max(newScale, 0.1);
      }
      ,
      onMouseDown(event) {

        if (event.button === 0) {
          event.preventDefault();
          this.isDragging = true;
          // this.startX = this.lastX = event.pageX;
          // this.startY = this.lastY = event.pageY;

          this.moveStart.x = event.clientX
          this.moveStart.y = event.clientY

        }
      }
      ,
      onMousemove(event) {


        event.preventDefault();   //阻止默认行为，防止拖动文字有BUG
        if (this.isDragging) {
          // const deltaX = event.pageX - this.lastX;
          // const deltaY = event.pageY - this.lastY;
          // this.offsetX += deltaX;
          // this.offsetY += deltaY;
          // this.lastX = event.pageX;
          // this.lastY = event.pageY;

          this.translate.x += (event.clientX - this.moveStart.x) / this.scale
          this.translate.y += (event.clientY - this.moveStart.y) / this.scale

          console.log("x" + event.clientX + "-------" + this.moveStart.x + "------" + this.translate.x)
          console.log("y" + event.clientY + "-------" + this.moveStart.y + "------" + this.translate.y)
          // this.translate.x = event.clientX  / this.scale
          // this.translate.y = event.clientY / this.scale

          this.moveStart.x = event.clientX
          this.moveStart.y = event.clientY
        }
      }
      ,
      onMouseup(event) {
        if (this.isDragging && event.button === 0) {
          event.preventDefault();
          this.isDragging = false;
        }
      }
      ,
      onMouseleave() {
        if (this.isDragging) {
          this.isDragging = false;
        }
      }
      ,
      refreshPage() {

      }
    },
  }
</script>


<style lang="scss" scoped>
  .bg {
    /*width: 100%;*/
    height: 100%;
    /*top: 0;*/
    /*left: 0;*/
    /*position: fixed;*/
    /*background: rgb(0, 0, 0, .5);*/
    display: flex;
    /*align-items: center;*/
    /*justify-content: center;*/
    /*overflow: auto;*/
    /*padding: 20px;*/
    z-index: -11111;

    .canvas-container {
      width: calc(100% - 40px);
      height: calc(100% - 120px);
      /*overflow: auto;*/

      .canvas {
        background: #fff;
        position: relative;
        margin: auto;
      }
    }

    .close {
      position: absolute;
      right: 20px;
      top: 20px;
    }
  }
</style>
