<template>
  <div>
    <div :key="keyValue">
      <img v-if="propValue == '255' || (propValue >= '1' && propValue<= '100')" src="@/assets/designer/downLampOn.svg"
           style="width: 100%;height: 100%;cursor: pointer;">
      <img v-else src="@/assets/designer/downLampClose.svg" style="width: 100%;height: 100%;cursor: pointer; ">
    </div>


      <!--    <button-->
      <!--      class="v-light">{{ propValue }}-->
      <!--    </button>-->


      <!-- 添加或修改设备对话框 -->
      <el-dialog  :title="title" :visible.sync="open" width="700px" :append-to-body="true" modal-append-to-body>
        <el-row v-for="(item, index) in pointList" :key="index" style="margin-left: 0.5vw">
          <el-col :span="24">
            <el-form :inline="true" :model="item" class="demo-ruleForm" ref="formInline">
              <el-form-item label="点位" v-if="pointTypes == pointType.BES_POINT_TYPE">
                <el-input v-model="item.nickName" :disabled="true" style="width: 20vw"></el-input>
              </el-form-item>
              <el-form-item label="点位" v-if="pointTypes == pointType.OTHER_POINT_TYPE">
                <el-input v-model="item.nickName" :disabled="true" style="width: 10vw"></el-input>
              </el-form-item>
              <el-form-item v-if="pointTypes == pointType.OTHER_POINT_TYPE" label="数值">
                <el-select v-model="item.value" placeholder="请选择状态" :clearable="false" size="small">
                  <el-option
                    v-for="itemDevice in selectValueList"
                    :key="itemDevice.value"
                    :label="itemDevice.label"
                    :value="itemDevice.value"
                    size="small"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="echoTreeInfo(item)">详情</el-button>
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
        <el-form :inline="true" label-width="80px" style="text-align: center">
          <el-form-item>
            <el-button type="primary" @click="submitItem">下发</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="disItem">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>

      <el-drawer title="设备树" append-to-body :visible.sync="lightTreeOpenBes" :direction="treeDirection">
        <div style="margin-left: 15px;margin-right: 15px;">
          <el-tree
            class="filter-tree"
            :default-expand-all="false"
            :data="deviceTree"
            :props="deviceTreeProps"
            :highlight-current="true"
            node-key="deviceTreeId"
            :default-expanded-keys="defaultExpandedNode"
            :current-node-key="defaultCurrentNodeKey"
            ref="tree"
          />
        </div>
      </el-drawer>

      <el-drawer title="第三方协议" append-to-body :visible.sync="lightTreeOpen" :direction="treeDirection"
                 :before-close="drawerBeforeClose">
        <div style="max-height: 81.5vh;overflow-y: auto;">
          <el-tree
            class="filter-tree"
            :default-expand-all="false"
            :data="otherDeviceTree"
            :props="otherDeviceTreeProps"
            :expand-on-click-node="false"
            :check-strictly="true"
            node-key="doubleId"
            :default-expanded-keys="otherDefaultExpandedNode"
            ref="otherTree"
            highlight-current
          />
        </div>
      </el-drawer>


  </div>
</template>

<script>
  import {
    getRealTimeData
  } from '@/api/basicData/deviceManagement/deviceTree/deviceTree'

  // import OnEvent from '../common/OnEvent'
  import eventBus from "@/utils/designer/eventBus";
  import pubsub from "@/store/modules/PubSub";
  import {mapState} from "vuex";
  import screenfull from "screenfull";
  import {
    debugPointListInfo,
    debugPointRealTimeInfo
  } from "@/api/basicData/deviceManagement/deviceTree/deviceTreePoint";
  import {deviceTreeSettings} from "../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings";
  import {pointType} from "@/store/modules/pointType";
  import {timer} from "@/api/tool/timer";

  export default {
    // extends: OnEvent,
    props: {
      propValue: {
        type: String,
        default: '',
      },
      linkage: {
        type: Object,
        default: () => {
        },
      },
      element: {
        type: Object,
        default: () => {
        },
      },
      keyValue: {
        type: Object,
        required: true,
        default: () => {
        },
      }
    },
    data() {
      return {
        // 是否显示弹出层
        open: false,
        lightTreeOpenBes: false,
        // 弹出层标题
        title: "",
        pointList: [],
        //弹框下拉
        selectValueList: [{value: '1', label: "开"}, {value: '0', label: "关"}],
        //树Props
        deviceTreeProps: {
          children: "children",
          label: "sysName"
        },
        lightTreeOpen: false,
        //第三方树Props
        otherDeviceTreeProps: {
          children: "children",
          label: "name"
        },
        otherDefaultExpandedNode: [],
        otherPointList: [],
        direction: 'rtl',
        treeDirection: 'rtl',
        //定时器
        timer: null,

        pointType: pointType,
        pointTypes: "0",
      }
    },
    computed: {
      ...mapState([
        'editMode',
        'curComponent',
        'isUnsubscribe',
        'pointRealTimeData'
      ]),
      deviceTree() {//设备树
        let aa = this.$store.state.deviceTreeData
        this.deviceTreeList = [];
        this.deviceTreeList.push(aa);
        return this.deviceTreeList
      },
      defaultExpandedNode() {
        let array = []
        if (this.pointList.length > 0 && this.pointList[0].id != null && this.pointList[0].id != undefined && this.pointList[0].id != '') {
          array.push(this.pointList[0].id)
          return array
        } else {
          array.push(0)
          return array
        }
      },
      defaultCurrentNodeKey() {
        let array = []
        if (this.pointList.length > 0 && this.pointList[0].id != null && this.pointList[0].id != undefined && this.pointList[0].id != '') {
          return this.pointList[0].id
        } else {
          return 0
        }
      },
      otherDeviceTree() {
        let bb = this.$store.state.otherDeviceTreeData
        this.otherDeviceTreeList = [];
        this.otherDeviceTreeList.push(bb);
        return this.otherDeviceTreeList
      },
    },
    created() {



      if (this.linkage?.data?.length) {
        eventBus.$on('v-click', this.onClick)
        eventBus.$on('v-hover', this.onHover)
      }
    },
    mounted() {

      setTimeout(() => {
        if (this.editMode != 'edit') {
          //获取点位类型  pointType,  0:bes  1:第三方
          this.pointTypes = this.element.linkage.data[0].point[0].pointType

          let id = null;
          let equipmentId = null;
          if (this.pointTypes == pointType.BES_POINT_TYPE) {
            id = this.element.linkage.data[0].point[0].id//点位id
          } else if (this.pointTypes == pointType.OTHER_POINT_TYPE) {
            id = this.element.linkage.data[0].point[0].sysName//功能id

            //获取功能所对应的数据项的枚举参数
            equipmentId = this.element.linkage.data[0].point[0].pId//设备id
            //定时下发获取实时数据

            if (this.timer != null) {
              clearInterval(this.timer);
              this.timer = null
            }
            // this.timer = setInterval(() => {
            //   this.timingGetRealData(this.pointTypes, id, equipmentId)
            // }, 1000 * 10)
          }

          if (typeof id != 'undefined' && id != null) {
            let param = {};
            param.id = id;
            param.equipmentId = equipmentId;
            param.pointType = this.pointTypes;
            param.nickName = this.element.linkage.data[0].point[0].nickName;

            let valueList = [];
            let valueParam = {};
            valueParam.id = 1;
            valueParam.value = this.element.linkage.data[0].point[0].value;
            valueList.push(valueParam)
            param.valueList = valueList;
            param.value = this.element.linkage.data[0].point[0].value;
            param.workMode = 0;

            if (param.value == '' || param.value == null && param.value == undefined) {

              param.value = '0'
            }
            this.pointList.push(param)
            //首先获取当前点位的实时数据
            this.getRealTimeData(this.pointTypes, id, equipmentId);
          }
        }
      },500 )



      const {data, duration} = this.linkage || {}
      if (data?.length) {
        this.$el.style.transition = `all ${duration}s`
      }
    },
    beforeDestroy() {
      if (this.linkage?.data?.length) {
        eventBus.$off('v-click', this.onClick)
        eventBus.$off('v-hover', this.onHover)
      }
      if (this.timer) { //如果定时器还在运行 或者直接关闭，不用判断
        clearInterval(this.timer); //关闭
      }
    },
    methods: {
      drawerBeforeClose(done) {
        this.otherDefaultExpandedNode = [];
        this.otherPointList = [];
        this.$refs.otherTree.setCheckedKeys(this.otherPointList)
        done()
      },
      handleClose(done) {//关闭新增页面
        this.$confirm('确认关闭？')
          .then(_ => {
            done()
          })
          .catch(_ => {
          })
      },
      //获取当前点位的实时数据
      getRealTimeData(pointTypes, id, equipmentId) {
        if (id != "") {
          let param = {};
          param.id = id;//功能id
          param.pointType = pointTypes;
          param.equipmentId = equipmentId;//设备id

          if (this.pointRealTimeData == null || this.pointRealTimeData.length == 0) {
            getRealTimeData(param).then(res => {
              if (res.code == 200) {
                let id = Object.keys(res.data[0])[0];
                if (Object.values(res.data[0])[0].value != null && Object.values(res.data[0])[0].value != undefined && Object.values(res.data[0])[0].value != '') {
                  this.element.propValue = (Object.values(res.data[0])[0].value).toString()
                } else {
                  this.element.propValue = '0'
                }
                // debugger
                pubsub.on(id, (data) => {
                  this.VLightWebsocket(data)
                }, 'VLightWebsocket')
              }
            })
          } else {
            for (let i = 0; i < this.pointRealTimeData.length; i++) {

              if (pointTypes == pointType.BES_POINT_TYPE) {
                if (id == this.pointRealTimeData[i].functionId) {
                  this.element.propValue = this.pointRealTimeData[i].value;
                  pubsub.on(this.pointRealTimeData[i].id, (data) => {
                    this.VLightWebsocket(data)
                  }, 'VLightWebsocket')
                }

              } else if (pointTypes == pointType.OTHER_POINT_TYPE) {
                if (id == this.pointRealTimeData[i].functionId && pointTypes == this.pointRealTimeData[i].pointType && equipmentId == this.pointRealTimeData[i].equipmentId) {
                  this.element.propValue = this.pointRealTimeData[i].value;
                  pubsub.on(this.pointRealTimeData[i].id, (data) => {
                    this.VLightWebsocket(data)
                  }, 'VLightWebsocket')
                }
              }

            }
          }
        }
      },
      VLightWebsocket(data) {
        this.element.propValue = (data.value).toString()
      },

      changeStyle(data = []) {
        data.forEach(item => {
          item.style.forEach(e => {
            if (e.key) {
              this.element.style[e.key] = e.value
            }
          })
        })
      },

      openInout(componentId, data) {
        if (data.id == componentId && data.component == "Light") {
          if (this.pointList.length == 0) {
            this.$modal.msgWarning("当前按钮未绑定功能或者点位");
            return
          }
          if (!screenfull.isFullscreen) {
            this.title = '灯光调试'
            if (this.open) {
              return;
            }
            this.open = true
          }
        }
      },

      onClick(componentId) {
        const data = this.linkage.data.filter(item => item.id === componentId && item.event === 'v-click')
        this.changeStyle(data)

        //传递父组件,打开相应弹窗
        this.openInout(componentId, this.element)
      },

      onHover(componentId) {
        const data = this.linkage.data.filter(item => item.id === componentId && item.event === 'v-hover')
        this.changeStyle(data)
      },
      //详情 弹出设备树回显点位
      echoTreeInfo(item) {
        // if (this.pointTypes == pointType.OTHER_POINT_TYPE) {
        //   //第三方
        //   let id = this.pointList[0].equipmentId + (this.pointList[0].id / 100)
        //   this.otherDefaultExpandedNode.push(id);
        //   this.otherPointList.push(id)
        //   this.lightTreeOpen = true
        //   this.$nextTick(() => {
        //     //给每个id对应的check选中
        //     this.$refs.otherTree.setCurrentKey(id)
        //   })
        // } else {
        //   //bes
        //   this.lightTreeOpenBes = true
        // }
        if (item.pointType == pointType.OTHER_POINT_TYPE) {
          //第三方
          let id = item.equipmentId + (item.id / 100)
          this.otherDefaultExpandedNode.push(id);
          this.otherPointList.push(id)
          this.lightTreeOpen = true
          this.$nextTick(() => {
            //给每个id对应的check选中
            this.$refs.otherTree.setCurrentKey(id)
          })
        } else {
          //bes
          this.lightTreeOpenBes = true
          this.defaultExpandedNode.push(item.id)
          this.$nextTick(() => {
            //给每个id对应的check选中
            this.$refs.tree.setCurrentKey(item.id)
          })
        }
      },
      //下发
      submitItem() {
        const that = this;
        that.pointList.forEach(item => {
          item.valueList[0].value = item.value;
          // if (item.value == '0') {
          //   item.value == '255'
          // } else {
          //   item.value == '0'
          // }
        })
        debugPointListInfo(JSON.stringify(that.pointList)).then(responent => {
          this.open = false;
          // this.timer = setTimeout(()=>{
          for (let i = 0; i < that.pointList.length; i++) {
            let pointType = this.pointList[i].pointType;
            let id = this.pointList[i].id;
            let equipmentId = this.pointList[i].equipmentId;
            that.timingGetRealData(pointType, id, equipmentId);
          }
          // }, 1000 * 3)


        })
      },
      disItem() {
        this.open = false
      },
      //定时下发获取实时数据
      timingGetRealData(pointType, id, pId) {
        let param = {};
        param.pointType = pointType;
        param.id = id;
        param.equipmentId = pId;
        debugPointRealTimeInfo(param).then(responent => {

        })
      },
    }
  }
</script>

<style lang="scss" scoped>
  .v-button {
    display: inline-block;
    line-height: 1;
    white-space: nowrap;
    cursor: pointer;
    background: #fff;
    border: 1px solid #dcdfe6;
    color: #606266;
    -webkit-appearance: none;
    text-align: center;
    box-sizing: border-box;
    outline: 0;
    margin: 0;
    transition: .1s;
    font-weight: 500;
    width: 100%;
    height: 100%;
    font-size: 14px;

    &:active {
      color: #3a8ee6;
      border-color: #3a8ee6;
      outline: 0;
    }

    &:hover {
      background-color: #ecf5ff;
      color: #3a8ee6;
    }
  }
</style>
