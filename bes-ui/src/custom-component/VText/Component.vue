<!-- eslint-disable vue/no-v-html -->
<template>
  <div
    v-if="editMode == 'edit'"
    class="v-text"
    @keydown="handleKeydown"
    @keyup="handleKeyup"
  >
    <!-- tabindex >= 0 使得双击时聚焦该元素 -->
    <div
      ref="text"
      :contenteditable="canEdit"
      :class="{ 'can-edit': canEdit }"
      tabindex="0"
      :style="{ verticalAlign: element.style.verticalAlign }"
      @dblclick="setEdit"
      @paste="clearStyle"
      @mousedown="handleMousedown"
      @blur="handleBlur"
      @input="handleInput"
      v-html="element.propValue"
    ></div>
  </div>
  <div v-else class="v-text preview">
    <div :style="{ verticalAlign: element.style.verticalAlign }" v-html="element.propValue"></div>
  </div>
</template>

<script>
  import {mapState} from 'vuex'
  import {keycodes} from '@/utils/designer/shortcutKey.js'
  import request from '@/utils/request'
  import OnEvent from '../common/OnEvent'
  import eventBus from '@/utils/designer/eventBus'

  import pubsub from '@/store/modules/PubSub'
  import {getRealTimeData} from "@/api/basicData/deviceManagement/deviceTree/deviceTree";
  import {pointType} from "@/store/modules/pointType";
  import {timer} from "@/api/tool/timer";
  import {debugPointRealTimeInfo} from "@/api/basicData/deviceManagement/deviceTree/deviceTreePoint";

  export default {
    extends: OnEvent,
    props: {
      propValue: {
        type: String,
        required: true,
        default: '',
      },
      request: {
        type: Object,
        default: () => {
        },
      },
      element: {
        type: Object,
        default: () => {
        },
      },
      linkage: {
        type: Object,
        default: () => {
        },
      },
    },
    data() {
      return {
        canEdit: false,
        ctrlKey: 17,
        isCtrlDown: false,
        cancelRequest: null,
        dataIndexArray: [],//实时数据每个寄存器(高低位和结构体也算一条)所对应的实时数据开始地址以及结束地址
        pointTypes: "0",
        //定时器
        timer: null,
        //设备树回显
        treeOpen: false,
        //树Props
        otherDeviceTreeProps: {
          children: "children",
          label: "name"
        },
        direction: 'rtl',
        treeDirection: 'rtl',
        otherDefaultExpandedNode: [],
        otherPointList: [],
      }
    },
    computed: {
      ...mapState([
        'editMode',
        'curComponent',
        'isUnsubscribe',
        'pointRealTimeData'
      ]),
      otherDeviceTree() {
        let bb = this.$store.state.otherDeviceTreeData
        this.otherDeviceTreeList = [];
        this.otherDeviceTreeList.push(bb);
        return this.otherDeviceTreeList
      },
    },
    watch: {
      editMode: {
        handler(val) {

        },
        deep: true

      }
    },
    created() {
      // 注意，修改时接口属性时不会发数据，在预览时才会发
      // 如果要在修改接口属性的同时发请求，需要 watch 一下 request 的属性
      //whj暂时注掉
      // if (this.request) {
      //     // 第二个参数是要修改数据的父对象，第三个参数是修改数据的 key，第四个数据修改数据的类型
      // this.cancelRequest = request(this.request, this.element, 'propValue', 'string')
      // }
      setTimeout(() => {
        if (this.editMode != 'edit') {

          //获取点位类型  pointType,  0:bes  1:第三方
          this.pointTypes = this.element.linkage.data[0].point[0].pointType

          let id = null;
          let equipmentId = null;
          if (this.pointTypes == pointType.BES_POINT_TYPE) {
            id = this.element.linkage.data[0].point[0].id
          } else if (this.pointTypes == pointType.OTHER_POINT_TYPE) {
            id = this.element.linkage.data[0].point[0].sysName

            equipmentId =  this.element.linkage.data[0].point[0].pId

            if (this.timer != null) {
              clearInterval(this.timer);
              this.timer = null
            }
            // this.timer = setInterval(()=>{
            //   this.timingGetRealData(this.pointTypes,id,equipmentId)
            // }, 1000 * 10)
          }

          if (typeof id == 'undefined' || id == null) {
            return
          }
          //首先获取当前点位的实时数据
          this.getRealTimeData(this.pointTypes,id,equipmentId);

        }
      },500)


      eventBus.$on('componentClick', this.onComponentClick)

    },
    beforeDestroy() {
      console.log("文文本页面销毁了")
      // 组件销毁时取消请求
      //whj暂时注掉
      //   this.request && this.cancelRequest()
      eventBus.$off('componentClick', this.onComponentClick)
      if (this.timer) { //如果定时器还在运行 或者直接关闭，不用判断
        clearInterval(this.timer); //关闭
      }
      // if (typeof this.editMode == 'undefined' || this.editMode == 'edit' || this.isUnsubscribe == false) {
      //   this.$store.commit('setIsUnsubscribe', true);
      //   return
      // }
      //
      //   let id = this.element.linkage.data[0].point[0].id
      //   if (id != "") {
      //     console.log(id)
      //     pubsub.remove(id,'VtextWebsocket')
      // }

    },
    methods: {
      drawerBeforeClose(done) {
        this.otherDefaultExpandedNode = [];
        this.otherPointList = [];
        this.$refs.otherTree.setCheckedKeys(this.otherPointList)
        done()
      },
      //详情 弹出设备树回显点位
      echoTreeInfo(item) {
        let id = item.equipmentId + (item.id / 100)
        this.otherDefaultExpandedNode.push(id);
        this.otherPointList.push(id)
        this.treeOpen = true
        this.$nextTick(() => {
          //给每个id对应的check选中
          this.$refs.otherTree.setCurrentKey(id)
        })
      },
      //获取当前点位的实时数据
      getRealTimeData(pointTypes,id,equipmentId) {
        debugger
        if (id != "") {
          let param = {};
          param.id = id;//功能id
          param.pointType = pointTypes;
          param.equipmentId = equipmentId;//设备id
          if (this.pointRealTimeData == null || this.pointRealTimeData.length == 0) {
            getRealTimeData(param).then(res => {
              // debugger
              if (res.code == 200) {
                this.element.propValue = '';
                for (let i = 0; i < res.data.length; i++) {
                  let id = Object.keys(res.data[i])[0];

                  if (Object.values(res.data[i])[0].value != null && Object.values(res.data[i])[0].value != undefined && Object.values(res.data[i])[0].value != '') {

                    let params = {};
                    params.id = id;
                    params.value = (Object.values(res.data[i])[0].value).toString();
                    if (Object.values(res.data[i])[0].unit == null) {
                      params.unit = '';
                    } else {
                      params.unit =  (Object.values(res.data[i])[0].unit).toString();
                    }

                    this.dataIndexArray.push(params)

                  }
                  // debugger
                  pubsub.on(id, (data) => {
                    this.VtextWebsocket(data)
                  }, 'VtextWebsocket')
                }

                if ( this.dataIndexArray.length == 0) {
                  this.element.propValue = '0'
                } else {
                  for (let i = 0; i < this.dataIndexArray.length; i++) {
                    this.element.propValue = this.element.propValue + this.dataIndexArray[i].value + this.dataIndexArray[i].unit
                  }
                }

              }
            })
          } else {
            for (let i = 0; i < this.pointRealTimeData.length; i++) {

              this.element.propValue = '';


              if (pointTypes == pointType.BES_POINT_TYPE) {

                if (id == this.pointRealTimeData[i].functionId) {

                  let params = {};
                  params.id = this.pointRealTimeData[i].id;
                  params.value = this.pointRealTimeData[i].value.toString();
                  if (this.pointRealTimeData[i].unit == null) {
                    params.unit = '';
                  } else {
                    params.unit =  this.pointRealTimeData[i].unit.toString();
                  }

                  this.dataIndexArray.push(params)

                  // this.element.propValue = this.element.propValue + this.pointRealTimeData[i].value + this.pointRealTimeData[i].unit;

                  pubsub.on(this.pointRealTimeData[i].id, (data) => {
                    this.VtextWebsocket(data)
                  }, 'VtextWebsocket')
                }

              } else if (pointTypes == pointType.OTHER_POINT_TYPE) {
                if (id == this.pointRealTimeData[i].functionId && pointTypes == this.pointRealTimeData[i].pointType && equipmentId == this.pointRealTimeData[i].equipmentId) {

                  let params = {};
                  params.id = this.pointRealTimeData[i].id;
                  params.value = this.pointRealTimeData[i].value.toString();
                  if (this.pointRealTimeData[i].unit == null) {
                    params.unit = '';
                  } else {
                    params.unit =  this.pointRealTimeData[i].unit.toString();
                  }

                  this.dataIndexArray.push(params)
                  debugger
                  // this.element.propValue = this.element.propValue + this.pointRealTimeData[i].value + this.pointRealTimeData[i].unit;
                  pubsub.on(this.pointRealTimeData[i].id, (data) => {
                    this.VtextWebsocket(data)
                  }, 'VtextWebsocket')
                }
              }

            }
            if ( this.dataIndexArray.length == 0) {
              this.element.propValue = '0'
            } else {
              for (let i = 0; i < this.dataIndexArray.length; i++) {
                this.element.propValue = this.element.propValue + this.dataIndexArray[i].value + this.dataIndexArray[i].unit
              }
            }
          }

        }
      },
      VtextWebsocket(data) {
        this.element.propValue = '';
        for (let i = 0; i < this.dataIndexArray.length; i++) {
          if (data.modbusId == this.dataIndexArray[i].id) {
            this.dataIndexArray[i].value = (data.value).toString();
          }
        }
        for (let i = 0; i < this.dataIndexArray.length; i++) {
          this.element.propValue = this.element.propValue + this.dataIndexArray[i].value + this.dataIndexArray[i].unit
        }
        // this.element.propValue = (data.value).toString()
      },
      onComponentClick() {
        // 如果当前点击的组件 id 和 VText 不是同一个，需要设为不允许编辑 https://github.com/woai3c/visual-drag-demo/issues/90
        if (this.curComponent.id !== this.element.id) {
          this.canEdit = false
        }
      },

      handleInput(e) {
        this.$emit('input', this.element, e.target.innerHTML)
      },

      handleKeydown(e) {
        // 阻止冒泡，防止触发复制、粘贴组件操作
        this.canEdit && e.stopPropagation()
        if (e.keyCode == this.ctrlKey) {
          this.isCtrlDown = true
        } else if (this.isCtrlDown && this.canEdit && keycodes.includes(e.keyCode)) {
          e.stopPropagation()
        } else if (e.keyCode == 46) { // deleteKey
          e.stopPropagation()
        }
      },

      handleKeyup(e) {
        // 阻止冒泡，防止触发复制、粘贴组件操作
        this.canEdit && e.stopPropagation()
        if (e.keyCode == this.ctrlKey) {
          this.isCtrlDown = false
        }
      },

      handleMousedown(e) {
        if (this.canEdit) {
          e.stopPropagation()
        }
      },

      clearStyle(e) {
        e.preventDefault()
        const clp = e.clipboardData
        const text = clp.getData('text/plain') || ''
        if (text !== '') {
          document.execCommand('insertText', false, text)
        }

        this.$emit('input', this.element, e.target.innerHTML)
      },

      handleBlur(e) {
        this.element.propValue = e.target.innerHTML || '&nbsp;'
        const html = e.target.innerHTML
        if (html !== '') {
          this.element.propValue = e.target.innerHTML
        } else {
          this.element.propValue = ''
          this.$nextTick(() => {
            this.element.propValue = '&nbsp;'
          })
        }
        this.canEdit = false
      },

      setEdit() {
        if (this.element.isLock) return

        this.canEdit = true
        // 全选
        this.selectText(this.$refs.text)
      },

      selectText(element) {
        const selection = window.getSelection()
        const range = document.createRange()
        range.selectNodeContents(element)
        selection.removeAllRanges()
        selection.addRange(range)
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
    },
  }
</script>

<style lang="scss" scoped>
  .v-text {
    width: 100%;
    height: 100%;
    display: table;

    div {
      display: table-cell;
      width: 100%;
      height: 100%;
      outline: none;
      word-break: break-all;
      padding: 4px;
    }

    .can-edit {
      cursor: text;
      height: 100%;
    }
  }

  .preview {
    user-select: none;
  }
</style>
