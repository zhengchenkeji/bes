<template>
  <div>
    <img v-if="propValue == '255'" src="@/assets/designer/吊灯_开.svg" style="width: 100%;height: 100%;">
    <img v-else src="@/assets/designer/吊灯_关.svg" style="width: 100%;height: 100%; ">
    <!--    <button-->
    <!--      class="v-light">{{ propValue }}-->
    <!--    </button>-->


    <!-- 添加或修改设备对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-row v-for="(item, index) in pointList" :key="index" style="margin-left: 0.5vw">
        <el-col :span="24">
          <el-form :inline="true" :model="item" class="demo-ruleForm" ref="formInline">
            <el-form-item label="点位">
              <el-input v-model="item.nickName" :disabled="true" style="width: 10vw"></el-input>
            </el-form-item>
            <el-form-item label="数值">
              <el-select v-model="item.value" placeholder="请选择状态" :clearable="false" size="small" :disabled="true">
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
              <el-button type="primary" @click="echoTreeInfo">详情</el-button>
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

    <el-drawer title="设备树" append-to-body :visible.sync="treeOpen" :direction="treeDirection">
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
  import {debugPointListInfo} from "@/api/basicData/deviceManagement/deviceTree/deviceTreePoint";
  import {deviceTreeSettings} from "../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings";

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
    },
    data() {
      return {
        // 是否显示弹出层
        open: false,
        treeOpen: false,
        // 弹出层标题
        title: "",
        pointList: [],
        //弹框下拉
        selectValueList: [{value: '0', label: "开"}, {value: '255', label: "关"}],
        //树Props
        deviceTreeProps: {
          children: "children",
          label: "sysName"
        },
        direction: 'rtl',
        treeDirection: 'rtl',
      }
    },
    computed: {
      ...mapState([
        'editMode',
        'curComponent',
        'isUnsubscribe'
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
      }
    },
    created() {

      if (this.editMode != 'edit') {
        let id = this.element.linkage.data[0].point[0].id

        let param = {};
        param.id = this.element.linkage.data[0].point[0].id;
        param.nickName = this.element.linkage.data[0].point[0].nickName;
        param.value = this.element.linkage.data[0].point[0].value;
        if (param.value == '' || param.value == null && param.value == undefined) {
          param.value = '255'
        }
        this.pointList.push(param)
        //首先获取当前点位的实时数据
        this.getRealTimeData(id);
      }

      if (this.linkage?.data?.length) {
        eventBus.$on('v-click', this.onClick)
        eventBus.$on('v-hover', this.onHover)

        //遍历所有的组件

      }
    },
    mounted() {
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
    },
    methods: {
      handleClose(done) {//关闭新增页面
        this.$confirm('确认关闭？')
          .then(_ => {
            done()
          })
          .catch(_ => {
          })
      },
      //获取当前点位的实时数据
      getRealTimeData(id) {
        if (id != "") {
          let param = {};
          param.id = id;
          getRealTimeData(param).then(res => {
            if (res.code == 200) {
              if (res.data.initVal != null && res.data.initVal != undefined && res.data.initVal != '') {
                this.element.propValue = (res.data.initVal).toString()
              } else {
                this.element.propValue = '255'
              }
              pubsub.on(id, this.VLightWebsocket, 'VLightWebsocket')
            }
          })
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
        if (data.id == componentId && data.component == "CommunicationLight") {
          if (!screenfull.isFullscreen) {
            this.title = '通讯灯调试'
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
      echoTreeInfo() {
        // let key = this.pointList[0].id
        // console.log('this.$refs[tree].getCurrentKey()')
        // console(this.$refs['tree'].getCurrentKey())
        // this.$refs['tree'].setCurrentKey(key)
        this.treeOpen = true
      },
      //下发
      submitItem() {
        this.pointList.forEach(item => {
          if (item.value == '0') {
            item.value == '255'
          } else {
            item.value == '0'
          }
        })
        debugPointListInfo(JSON.stringify(this.pointList)).then(responent => {
          this.open = false;
        })
      },
      disItem() {
        this.open = false
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
