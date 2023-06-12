<template>
  <div class="app-container">
    <el-row :gutter="20" class="mb20">
      <!-- 分组数据 -->
      <el-col :span="10" :xs="24">
        <div class="head-container">
          <el-card class="wait-task-user-box-card">
            <div slot="header"
                 class="clearfix">
              <el-button
                v-hasPermi="['basicData:deviceTree:insertDeviceTreee']"
                size="mini"
                type="primary"
                @click="addParkRootNode"
              >添加园区根节点
              </el-button>
              <el-button
                type="info"
                plain
                icon="el-icon-upload2"
                size="mini"
                @click="handleImport"
                v-hasPermi="['basicData:deviceTree:import']"
              >导入</el-button>
              <el-button size="mini"
                         type="info"
                         @click="resetQuery"
                         style="margin-right:10px">刷新
              </el-button>
            </div>
            <div class="scroll-bar item" style="max-height: 70.4vh;overflow-y: auto;">
              <device-tree v-if="refreshTable"
                           :ref="tree"
                           :lazy="lazy"
                           @loadNode="loadNode"
                           :itemShow="itemShow"
                           :tree-expand-all="treeExpandAll"
                           :default-expanded-keys="defaultExpandedKeys"
                           :tree-node-key="treeNodeKey"
                           :expand-on-click-node="false"
                           :button="button"
                           @handleNodeClick='handleNodeClick'
                           @addItem="addTreeItem"
                           @deleteItem="deleteTreeItem"
                           @changeState="changeState"
              ><!--@destroyPubSub="destroyPubSub"-->
              </device-tree>

            </div>
          </el-card>
        </div>
      </el-col>
      <!-- 设备数据 -->
      <el-col :span="14" :xs="24">
        <el-card class="wait-task-user-box-card" style="max-height: 85vh;overflow-y: auto;">

          <sys-info v-loading="loading" :treeNodeMsgs="treeNodeMsg"
                    v-show="deviceNodeCode == deviceTreeSettings.parkRootNode ||
                    deviceNodeCode == deviceTreeSettings.buildingAuto ||
                    deviceNodeCode == deviceTreeSettings.smartLighting ||
                    deviceNodeCode == deviceTreeSettings.energyConCol ||
                    deviceNodeCode == deviceTreeSettings.Bus ||
                    deviceNodeCode == deviceTreeSettings.VpointNoProperty ||
                    deviceNodeCode == deviceTreeSettings.line"
                    @addTreeNode="addTreeNode"
                    @updateTreeNode="updateTreeNode"
                    @ReloadNode="ReloadNode" ref="SysInfoMsg"/>
          <controllerInfo v-loading="loading"
                          :treeNodeMsgs="treeNodeMsg"
                          ref="controllerInfoMsg"
                          :deviceNodeCode="deviceNodeCode"
                          v-show="deviceNodeCode == deviceTreeSettings.DDCNode ||
                   deviceNodeCode == deviceTreeSettings.illumine ||
                   deviceNodeCode == deviceTreeSettings.collector"
                          @addTreeNode="addTreeNode"
                          @updateTreeNode="updateTreeNode"
                          @ReloadNode="ReloadNode">

          </controllerInfo>
          <Point v-loading="loading" v-show="deviceNodeCode == deviceTreeSettings.AI || deviceNodeCode == deviceTreeSettings.AO || deviceNodeCode == deviceTreeSettings.DI ||
            deviceNodeCode == deviceTreeSettings.DO || deviceNodeCode == deviceTreeSettings.UI || deviceNodeCode == deviceTreeSettings.UX ||
            deviceNodeCode == deviceTreeSettings.Vpoint"
                 :treeNodeMsgs="treeNodeMsg" :nodeType="deviceNodeCode" @ReloadNode="ReloadNode"
                 @addTreeNode="addTreeNode" @updateTreeNode="updateTreeNode" @updateTreeNodePoint="updateTreeNodePoint"
                 @deleteTreeNodePoint="deleteTreeNodePoint" @debuggerTreeNode="debuggerTreeNode"
                 ref="PointInfoMsg"></Point><!--v-loading="loading"-->

          <ModuleInfo v-loading="loading" v-show="deviceNodeCode == deviceTreeSettings.model"
                      :treeNodeMsgs="treeNodeMsg" :nodeType="deviceNodeCode" @ReloadNode="ReloadNode"
                      @addTreeNode="addTreeNode" @updateTreeNode="updateTreeNode"
                      @deleteTreeNodePoint="deleteTreeNodePoint"
                      ref="ModuleInfoMsg"></ModuleInfo><!--v-loading="loading"-->

          <debugger-page @ReloadNode="ReloadNode" @updateTreeNode="updateTreeNode" :pointType="pointType"
                         ref="debuggerPageInfo"></debugger-page>

          <!--能耗采集总线-->
          <bus-info v-loading="loading"
                    :treeNodeMsg="treeNodeMsg"
                    v-show="deviceNodeCode == deviceTreeSettings.energyBus"
                    @addTreeNode="addTreeNode"
                    @updateTreeNode="updateTreeNode"
                    @ReloadNode="ReloadNode"
                    ref="busInfoMsg"></bus-info>
          <!--能耗采集电表-->
          <meter-info v-loading="loading"
                      :treeNodeMsg="treeNodeMsg"
                      v-show="deviceNodeCode == deviceTreeSettings.ammeter"
                      @addTreeNode="addTreeNode"
                      @updateTreeNode="updateTreeNode"
                      @ReloadNode="ReloadNode"
                      ref="meterInfoMsg"></meter-info>

          <!-- 干线耦合器-->
          <coupler v-loading="loading"
                   v-show="deviceNodeCode == deviceTreeSettings.GXOHQ"
                   :lineType="deviceNodeCode"
                   :treeNodeMsgs="treeNodeMsg"
                   @addTreeNode="addTreeNode"
                   @updateTreeNode="updateTreeNode"
                   @ReloadNode="ReloadNode"
                   ref="TrunkLine">
          </coupler>


          <!--  支线耦合器 -->
          <coupler v-loading="loading"
                   v-show="deviceNodeCode == deviceTreeSettings.ZXOHQ"
                   :treeNodeMsgs="treeNodeMsg"
                   :lineType="deviceNodeCode"
                   @updateTreeNode="updateTreeNode"
                   @addTreeNode="addTreeNode"
                   @ReloadNode="ReloadNode"
                   ref="BranchLine">
          </coupler>

        </el-card>
      </el-col>


    </el-row>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport + '&fold=sbdyTypeImportExcel'"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" /> 是否更新已经存在的用户数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listTree,
    getTreeNodeManage,
    deleteTreeNode
  } from '@/api/basicData/deviceManagement/deviceTree/deviceTree'
  import { subscribe } from '@/api/basicData/pubsubmanage/pubsubmanage'
  import deviceTree from './tree'
  import sysInfo from './sysInfo'
  import controllerInfo from './controllerInfo'
  import Point from './imaginaryPoint'

  import coupler from './lighting/coupler.vue'
  import busInfo from './busInfo'
  import meterInfo from './meterInfo'
  import ipAddressInput from '../../../../components/InputIP/ipAddressInput'
  import { deviceTreeSettings } from '../../../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings'
  import pubsub from '@/store/modules/PubSub'
  import ModuleInfo from './moduleInfo'
  import debuggerPage from './debuggerPage'
  import { mapState } from 'vuex'
  import {getToken} from "@/utils/auth";

  export default {
    name: 'DeviceTree',
    components: {
      ModuleInfo,
      debuggerPage,
      controllerInfo,
      sysInfo,
      deviceTree,
      ipAddressInput,
      Point,
      busInfo,
      meterInfo,
      coupler
    },
    data() {
      return {
        // 用户导入参数
        upload: {
          // 是否显示弹出层（用户导入）
          open: false,
          // 弹出层标题（用户导入）
          title: "",
          // 是否禁用上传
          isUploading: false,
          // 是否更新已经存在的用户数据
          updateSupport: 0,
          // 设置上传的请求头部
          headers: { Authorization: "Bearer " + getToken() },
          // 上传的地址
          url: process.env.VUE_APP_BASE_API + "/basicData/ExcelImport/importData"
        },
        checkedId: null,//当前选中的节点ID
        //获取单位分割字符
        splitCode: '',
        deviceTreeSettings: deviceTreeSettings,
        pointArr: [deviceTreeSettings.Vpoint, deviceTreeSettings.AI, deviceTreeSettings.AO,
          deviceTreeSettings.DI, deviceTreeSettings.DO, deviceTreeSettings.UI, deviceTreeSettings.UX],//虚点+模块+模块点
        insertNodeId: null,//添加的节点id
        tableHeight: '',
        // 遮罩层
        loading: true,
        // 表单参数
        form: {},
        checked: false,
        itemShow: true,
        treeExpandAll: false,
        refreshTable: true,
        defaultExpandedKeys: [5294],//默认展开的数组
        treeNodeKey: 'id',
        tree: 'deviceTree',
        lazy: true,
        //当前选中节点的信息
        treeNodeMsg: {},
        treeNodeData: {},
        // 查询参数
        queryParams: {
          // deviceType: 1,
          deviceTreeFatherId: -1
        },
        treeData: [],
        //节点按钮的数组
        deviceNodeCode: '',
        pointType: '',//点类型
        sysInfoAdd: false,//楼控/照明/能耗新增按钮是否显示
        button: [],
        positionBoolean: false,//是否定位节点
        addNodeData: {},//新增的节点数据
        pubsubList: []//当前订阅点位
      }
    },
    beforeDestroy() {
      //页面销毁取消订阅
      this.destroyPubSub()
      this.pubsubList = []
    },
    created() {
      // this.selectTree(this.queryParams)
    },
    mounted() {
      // this.getTreeselect()
    },
    computed: {
      ...mapState({})
    },
    watch: {},
    methods: {
      resetQuery() {
        // this.getTreeselect()
      },

      async loadNode(node, resolve) {

        let that = this
        if (node.data != null && node.data.deviceNodeId == deviceTreeSettings.DDCNode) {
          this.$refs.PointInfoMsg.controllerId = node.data.deviceTreeId
          this.$refs.ModuleInfoMsg.controllerId = node.data.deviceTreeId
        }

        if (node.level === 0) {
          let tags = []

          await listTree(that.queryParams).then(response => {
            tags = response.data
            tags.forEach((item, index) => {

              // 节点需要构建为 tree 的结构
              item.name = item.sysName
              item.id = item.deviceTreeId
              item.leaf = false
            })
            return resolve(tags)
          })

          //定位到第一个根节点下的子节点
          this.$nextTick(() => {
            const nodes = this.$refs.deviceTree.$refs.Tree.getNode(tags[0].id)
            this.$refs.deviceTree.$refs.Tree.setCurrentKey(nodes)
            this.handleNodeClick(tags[0])
          })

        } else if (node.level >= 0) {

          let tags = []
          that.queryParams.deviceTreeFatherId = node.data.deviceTreeId
          await listTree(that.queryParams).then(response => {
            tags = response.data
            tags.forEach((item, index) => {

              // 节点需要构建为 tree 的结构
              item.name = item.sysName
              item.id = item.deviceTreeId
              if (item.deviceNodeId == deviceTreeSettings.AI
                || item.deviceNodeId == deviceTreeSettings.AO
                || item.deviceNodeId == deviceTreeSettings.DI
                || item.deviceNodeId == deviceTreeSettings.DO
                || item.deviceNodeId == deviceTreeSettings.UI
                || item.deviceNodeId == deviceTreeSettings.UX
                || item.deviceNodeId == deviceTreeSettings.Vpoint
              ) {
                pubsub.on(item.id, (data) => {
                  that.besPointDebugger_websocket(data)
                }, item.name)
                let iteam = { id: item.id, name: item.name }
                this.pubsubList.push(iteam)
                item.name = (item.runVal == null ? '' : item.runVal) + (item.engineerUnit == null ? '' : item.engineerUnit) + ' | ' + item.sysName
              }

            })
            return resolve(tags)
          })

          if (this.positionBoolean) {//是否定位节点
            //定位到新增的节点
            this.$nextTick(() => {
              const nodes = this.$refs.deviceTree.$refs.Tree.getNode(this.addNodeData.id)
              this.$refs.deviceTree.$refs.Tree.setCurrentKey(nodes)
              this.handleNodeClick(this.addNodeData)
            })
            this.positionBoolean = false
          }
        } else {
          return resolve([]) // 防止该节点没有子节点时一直转圈的问题出现
        }
      },
      destroyPubSub() {
        if (this.pubsubList.length > 0) {
          this.pubsubList.forEach(item => {
            pubsub.remove(item.id, item.name)
          })
        }

      },
      besPointDebugger_websocket(data) {
        if (typeof data == null) {
          return
        }
        // console.log(data)
        const nodes = this.$refs.deviceTree.$refs.Tree.getNode(data.id)
        if (nodes.data.alias == null) {
          nodes.data.alias = nodes.data.sysName
        }
        this.$refs.PointInfoMsg.queryFormDO.faultState = data.faultState
        nodes.data.name = (data.value == null ? '' : data.value) + (data.unit == null ? '' : data.unit) + ' | ' + nodes.data.alias
        this.$refs.deviceTree.$refs.Tree.getNode(data.id).data = nodes.data
        // console.log(this.$refs.deviceTree.$refs.Tree.getNode(data.id).data.name)
        this.$nextTick(() => {
          if (data.id == this.checkedId) {
            this.handleNodeClick(nodes.data)
          }
        })
      },
      //重新加载当前选中节点
      ReloadNode() {
        this.$nextTick(() => {
          if ((this.treeNodeMsg.deviceNodeId == null || this.treeNodeMsg.deviceNodeId == undefined) && this.treeNodeMsg.nodeType != null) {
            this.treeNodeMsg.deviceNodeId = this.treeNodeMsg.nodeType
          }
          this.deviceNodeCode = this.treeNodeMsg.deviceNodeId.toString()
        })
      },

      // 选中每一个子节点---对应右边明细
      handleNodeClick(data) {
        const that = this
        that.checkedId = data.deviceTreeId
        if (null != data.deviceNodeId) {
          that.deviceNodeCode = data.deviceNodeId.toString()
        } else {
          that.deviceNodeCode = data.nodeType
        }
        //去除校验
        that.$refs.controllerInfoMsg.$refs.treeNodeMsg.clearValidate()
        //清除 时间数据
        that.$refs.controllerInfoMsg.controllerTime = null

        if (data.deviceNodeId == deviceTreeSettings.DDCNode) {
          that.$refs.PointInfoMsg.controllerId = data.deviceTreeId
          that.$refs.ModuleInfoMsg.controllerId = data.deviceTreeId
        }
        that.$refs.PointInfoMsg.fatherId = data.id + ''
        that.$refs.ModuleInfoMsg.fatherId = data.id + ''
        that.$refs.SysInfoMsg.deviceTreeFatherId = data.id
        // that.treeNodeMsg = data
        // deviceNodeId
        let nodeManage = {}
        nodeManage.deviceNodeCode = that.deviceNodeCode
        //根据节点类型加载相应的按钮
        that.button.length = 0
        if (data.deviceNodeId != 2 && data.deviceNodeFunType != '' && data.deviceNodeFunName != '') {
          let deviceNodeFunType = data.deviceNodeFunType.split(',')
          let deviceNodeFunName = data.deviceNodeFunName.split(',')
          that.button.length = 0
          for (let i = 0; i < deviceNodeFunType.length; i++) {
            let manage = {}
            manage.id = deviceNodeFunType[i]
            manage.name = deviceNodeFunName[i]
            that.button.push(manage)
          }
        }
        if (data.deviceNodeFunType == '' && data.deviceNodeFunName == '') {
          that.button = []
          this.$refs.deviceTree.$refs.Tree.getNode(data.id).data.deviceNodeFunType = data.deviceNodeFunType
          this.$refs.deviceTree.$refs.Tree.getNode(data.id).data.deviceNodeFunName = data.deviceNodeFunName
        }
        //获取当前点击节点的详细信息
        this.getTreeNodeManage(data)
      },

      //获取当前点击节点的详细信息
      getTreeNodeManage(data) {
        let param = {}
        let that = this
        param.deviceTreeId = data.deviceTreeId
        if (null != data.deviceNodeId) {
          param.deviceNodeId = data.deviceNodeId
        } else {
          param.deviceNodeId = data.nodeType
        }
        param.sysName = data.sysName
        param.deviceType = data.deviceType
        param.deviceTreeFatherId = data.deviceTreeFatherId
        param.deviceNodeFunName = data.deviceNodeFunName
        param.deviceNodeFunType = data.deviceNodeFunType
        param.url = data.url
        this.loading = true
        getTreeNodeManage(param).then(response => {
          if (data.deviceNodeId == 2) {
            let deviceNodeFunType = response.data.deviceNodeFunType.split(',')
            let deviceNodeFunName = response.data.deviceNodeFunName.split(',')
            that.button.length = 0
            for (let i = 0; i < deviceNodeFunType.length; i++) {
              let manage = {}
              manage.id = deviceNodeFunType[i]
              manage.name = deviceNodeFunName[i]
              that.button.push(manage)
            }
          }

          this.loading = false
          if (response.code == 200) {//获取节点成功
            if (null != response.data) {
              if (response.data.deviceNodeId != null && response.data.nodeType == null) {
                response.data.nodeType = response.data.deviceNodeId
              }
              that.treeNodeMsg = response.data
              //解除双向绑定  给分割字符赋值
              this.splitCode = that.treeNodeMsg.engineerUnit == null ? undefined : JSON.parse(JSON.stringify(that.treeNodeMsg.engineerUnit))
              if (response.data.nodeType != null && response.data.nodeType.toString() != deviceTreeSettings.line) {
                that.treeNodeMsg.portNum = this.getaddress(response.data)
              }
              // 判断是否是照明模块
              if (response.data.type == 1) {
                this.$refs.ModuleInfoMsg.checkprefix(response.data.lightingAddress)
              } else if (response.data.type == 0) {
                this.$refs.ModuleInfoMsg.checkprefix('DDC')
              } else {
                this.$refs.ModuleInfoMsg.checkprefix(null)
              }

              // that.treeNodeMsg.portNum=this.getaddress(response.data);
              // 判断当前节点id是否为空 方便在子节点中调用
              if (that.treeNodeMsg.deviceTreeId == null) {
                that.treeNodeMsg.deviceTreeId = response.data.treeId
              }
            }
          }
        })

      },
      /*******************************************新增园区根节点*********************************************************/
      addParkRootNode() {
        this.$refs.SysInfoMsg.deviceTreeFatherId = '-1'//定义一个顶级的根节点
        this.$refs.SysInfoMsg.deviceNodeId = deviceTreeSettings.parkRootNode
        this.deviceNodeCode = deviceTreeSettings.parkRootNode
        this.$refs.SysInfoMsg.visible = true
      },
      /*******************************************处理通讯地址*********************************************************/
      getaddress(data) {
        if (data.nodeType == deviceTreeSettings.GXOHQ) {
          return data.trunkNum
        } else if (data.nodeType == deviceTreeSettings.ZXOHQ) {
          this.$refs.BranchLine.checkprefix(this.treeNodeMsg.trunkNum + '.')

          return data.branchNum
        }
      },

      /*******************************************树节点按钮点击方法*********************************************************/
      addTreeItem(data, node) {
        this.treeNodeData = node

        if (data.id == deviceTreeSettings.deleteNode) {//删除树节点操作
          this.deleteTreeNode(node)
          return
        }

        if (data.id == deviceTreeSettings.AODebugger || data.id == deviceTreeSettings.DODebugger || data.id == deviceTreeSettings.vpointDebugger) {//逻辑点调试
          this.debuggerTreeNode(data.id, node)
          return
        }

        if (data.id == deviceTreeSettings.DDCNode ||
          data.id == deviceTreeSettings.collector ||
          data.id == deviceTreeSettings.ZMDDC
        ) {//添加控制器节点
          this.deviceNodeCode = data.id
          //显示修改框
          this.$refs.controllerInfoMsg.visible = true
          //将修改新增标识  改为新增
          this.$refs.controllerInfoMsg.addOrUpdate = false
          this.$nextTick(() => {
            //格式化数据
            this.$refs.controllerInfoMsg.form = {
              sysName: '',//系统名称
              alias: '',//采集器别名
              currentIp: '',//当前通讯ip
              ip: '....',//ip地址
              gateWay: '....',//默认网关
              mask: '....',//子网掩码
              serverIp: '....',//服务ip地址
              serverPort: '',//服务端口
              location: '',//安装位置
              zone: '',//归属区域
              synchState: 0,//同步状态 0：未同步、 1：已同步
              errorState: 0,//异常状态 0：正常、1：异常
              onlineState: 0,//在线状态0：不在线、 1：在线
              active: 1,//使能状态  0：不使能、1：使能  默认为1 使能
              description: '',//描述
              collectPeriod: this.$refs.controllerInfoMsg.collectOruploadOptions.length>0 ? this.$refs.controllerInfoMsg.collectOruploadOptions[0].value:null,//采集周期： 分钟（只有能耗采集器有）默认 数组第一位
              uploadPeriod: this.$refs.controllerInfoMsg.collectOruploadOptions.length>0 ? this.$refs.controllerInfoMsg.collectOruploadOptions[0].value:null,//上传周期：分钟（只有能耗采集器有）默认 数组第一位
              savePeriod: this.$refs.controllerInfoMsg.savePeriodOptions[0].value//保存周期   默认 数组第一位
            }

            //去除校验
            this.$refs.controllerInfoMsg.$refs.form.clearValidate()
          })

        }

        /*添加总线或虚点子节点，只能添加一个*/

        if (data.id == deviceTreeSettings.Bus || data.id == deviceTreeSettings.VpointNoProperty || data.id == deviceTreeSettings.line//总线节点20 虚点节点22
          || data.id == deviceTreeSettings.buildingAuto// 楼宇自控节点
          || data.id == deviceTreeSettings.smartLighting//智能照明节点
          || data.id == deviceTreeSettings.energyConCol //能耗采集节点
        ) {
          this.$refs.SysInfoMsg.insertParams.nickName = '虚点'
          if (data.id == deviceTreeSettings.Bus) {
            this.$refs.SysInfoMsg.insertParams.nickName = '总线'
          }
          if (data.id == deviceTreeSettings.line) {
            this.$refs.SysInfoMsg.insertParams.nickName = ''
          }

          if (data.id == deviceTreeSettings.buildingAuto) { //楼宇自控节点
            this.$refs.SysInfoMsg.insertParams.nickName = ''
          }
          if (data.id == deviceTreeSettings.smartLighting) { //智能照明节点
            this.$refs.SysInfoMsg.insertParams.nickName = ''
          }
          if (data.id == deviceTreeSettings.energyConCol) { //能耗采集节点
            this.$refs.SysInfoMsg.insertParams.nickName = ''
          }

          this.$refs.SysInfoMsg.deviceNodeId = data.id
          this.deviceNodeCode = data.id
          this.$refs.SysInfoMsg.visible = true

        }

        /*添加虚点、模块点*/
        if (this.pointArr.indexOf(data.id) > -1) {
          this.$refs.PointInfoMsg.addPoint = true
          if (data.id == deviceTreeSettings.AI || data.id == deviceTreeSettings.DI || data.id == deviceTreeSettings.AO) {
            this.$refs.PointInfoMsg.UIUX = true
            // this.$refs.PointInfoMsg.formModelPoint = node
            this.$refs.PointInfoMsg.formModelPoint.nodeType = data.id
            this.$refs.PointInfoMsg.formModelPoint.treeId = node.deviceTreeId
            this.$refs.PointInfoMsg.formModelPoint.sysName = this.treeNodeMsg.sysName
            if (data.id == deviceTreeSettings.AI) {
              this.$refs.PointInfoMsg.formModelPoint.DOType = 'AI节点'
            } else if (data.id == deviceTreeSettings.DI) {
              this.$refs.PointInfoMsg.formModelPoint.DOType = 'DI节点'
            } else {
              this.$refs.PointInfoMsg.formModelPoint.DOType = 'AO节点'
            }
          }

          this.deviceNodeCode = data.id
          this.$refs.SysInfoMsg.deviceNodeId = data.id
          this.$refs.PointInfoMsg.nodeTypeValue = data.id
          this.$refs.PointInfoMsg.visible = true
        } else {
          this.$refs.PointInfoMsg.UIUX = false
        }
        //添加model
        if (deviceTreeSettings.model == data.id) {

          this.$refs.ModuleInfoMsg.queryModuleType()
          this.$refs.ModuleInfoMsg.queryFormModel = {}
          let deviceTypeStr
          deviceTypeStr = this.treeNodeData.deviceType.toString()
          //判断是不是照明的子节点
          if (this.treeNodeMsg.nodeType == 29) {
            this.$refs.ModuleInfoMsg.modelForm.type = 1
            // 如果父节点是支线耦合器
            this.$refs.ModuleInfoMsg.checkprefix(this.treeNodeMsg.trunkNum + '.' + this.treeNodeMsg.branchNum + '.')

          } else if (this.treeNodeMsg.deviceType == 2) {
            // 如果父节点是照明控制器
            this.treeNodeMsg.trunkNum = 1
            this.treeNodeMsg.branchNum = 1
            this.$refs.ModuleInfoMsg.checkprefix('1.1.')
            this.$refs.ModuleInfoMsg.modelForm.type = 1
          } else if (deviceTypeStr == deviceTreeSettings.DDCNode) {//DDC模块
            this.$refs.ModuleInfoMsg.modelForm.type = 0
            this.$refs.ModuleInfoMsg.checkprefix('DDC')
          }
          if (this.treeNodeMsg.portNum == null) {
            this.$refs.ModuleInfoMsg.modelForm.type = 1
            this.$refs.ModuleInfoMsg.checkprefix(null)
          }

          this.deviceNodeCode = data.id
          this.$refs.SysInfoMsg.deviceNodeId = data.id
          this.$refs.ModuleInfoMsg.nodeTypeValue = data.id
          this.$refs.ModuleInfoMsg.visible = true
        }

        /*能耗总线*/
        if (data.id == deviceTreeSettings.energyBus) {
          this.deviceNodeCode = data.id
          this.$refs.busInfoMsg.visible = true
        }
        /*能耗电表*/
        if (data.id == deviceTreeSettings.ammeter) {
          this.deviceNodeCode = data.id
          this.$refs.meterInfoMsg.visible = true
          this.$refs.meterInfoMsg.form.commPort = this.treeNodeMsg.port
        }

        /*干线耦合器*/
        if (data.id == deviceTreeSettings.GXOHQ) {
          this.$refs.TrunkLine.reset()
          this.deviceNodeCode = data.id
          this.$refs.TrunkLine.visible = true
          this.$nextTick(() => {
            this.$refs.TrunkLine.$refs.insertParams.clearValidate()
          })
          this.$refs.TrunkLine.lineType = data.id
        }
        //支线耦合器
        if (data.id == deviceTreeSettings.ZXOHQ) {
          this.$refs.BranchLine.reset()
          if (this.treeNodeMsg.portNum != null) {
            this.$refs.BranchLine.checkprefix(this.treeNodeMsg.portNum + '.')
          } else {
            this.$refs.BranchLine.checkprefix(null)
          }
          this.deviceNodeCode = data.id
          this.$refs.BranchLine.lineType = data.id
          this.$refs.BranchLine.visible = true
          this.$nextTick(() => {
            this.$refs.BranchLine.$refs.insertParams.clearValidate()
          })
        }
      },

      /*******************************************新增节点操作*********************************************************/
      addTreeNode(nodeData) {
        /****gaojikun 添加完节点显示右侧详情****/
        this.$refs.PointInfoMsg.addPoint = false

        /****王红杰  给新增的节点赋值给指定对象****/
        this.addNodeData = nodeData

        /***qindehua  展示别名*/
        nodeData.name = nodeData.alias
        nodeData.id = nodeData.deviceTreeId
        /***gaojikun  新增时 状态默认为在线状态*/
        nodeData.leaf = false

        if (nodeData.nodeType != undefined && nodeData.nodeType.toString() == deviceTreeSettings.parkRootNode) {
          nodeData.leaf = true
          this.$refs.deviceTree.$refs.Tree.append(nodeData, '')
          return
        } else {
          this.$refs.deviceTree.$refs.Tree.append(
            nodeData,
            this.treeNodeData
          )
        }
        this.refreshNodeBy(this.$refs.deviceTree.$refs.Tree.getNode(this.treeNodeData.id))
      },

      //对子节点进行更新
      refreshNodeBy(id) {
        let node = this.$refs.deviceTree.$refs.Tree.getNode(id) // 通过节点id找到对应树节点对象
        node.loaded = false
        this.positionBoolean = true
        node.expand() // 主动调用展开节点方法，重新查询该节点下的所有子节点
      },

      /*******************************************qindehua 修改节点操作*********************************************************/
      updateTreeNode(nodeData) {
        if (nodeData.nodeType == deviceTreeSettings.AI
          || nodeData.nodeType == deviceTreeSettings.AO
          || nodeData.nodeType == deviceTreeSettings.DI
          || nodeData.nodeType == deviceTreeSettings.DO
          || nodeData.nodeType == deviceTreeSettings.Vpoint) {
          this.$nextTick(() => {
            this.handleNodeClick(nodeData)
          })
          var name = ''
          var val = (this.$refs.deviceTree.$refs.Tree.getNode(nodeData.deviceTreeId).data.name + '').split(' | ')[0]
          var str = val == '' ? (nodeData.initVal == null ? '' : nodeData.initVal) : val
          //分割字符  为设备树修改名称
          if (nodeData.engineerUnit) {
            if (this.splitCode) {
              var str = val == '' ? (nodeData.initVal == null ? '' : nodeData.initVal) : (this.$refs.deviceTree.$refs.Tree.getNode(nodeData.deviceTreeId).data.name + '').split(this.splitCode + '')[0]
              name = str + nodeData.engineerUnit + ' | ' + nodeData.nickName
            } else {
              name = str + nodeData.engineerUnit + ' | ' + nodeData.nickName
            }
          } else {
            name = str + ' | ' + nodeData.nickName
          }
          this.$refs.deviceTree.$refs.Tree.getNode(nodeData.deviceTreeId).data = nodeData
          this.$refs.deviceTree.$refs.Tree.getNode(nodeData.deviceTreeId).data.name = name
        } else {
          this.$refs.deviceTree.$refs.Tree.getNode(nodeData.deviceTreeId).data.name = nodeData.alias
        }
      },
      /*******************************************gaojikun 新增模块点节点操作*********************************************************/
      updateTreeNodePoint(nodeData) {
        this.$refs.PointInfoMsg.UIUX = false
        this.$refs.deviceTree.$refs.Tree.getNode(nodeData.deviceTreeId).data = nodeData
        this.$refs.deviceTree.$refs.Tree.getNode(nodeData.deviceTreeId).data.name =
          (nodeData.runVal == null ? '' : nodeData.runVal) + (nodeData.engineerUnit == null ? '' : nodeData.engineerUnit) + ' | ' + nodeData.nickName
        const node = this.$refs.deviceTree.$refs.Tree.getNode(nodeData.deviceTreeId)
        //定位到新增的节点
        this.$nextTick(() => {
          this.$refs.deviceTree.$refs.Tree.setCurrentKey(node)
          this.handleNodeClick(nodeData)
        })
      },
      /*******************************************删除树节点操作*********************************************************/
      deleteTreeNodePoint(node) {//删除UIUX点位
        this.$refs.deviceTree.$refs.Tree.remove(
          node,
          this.treeNodeData
        )
      },
      deleteTreeNode(node) {
        let that = this
        // if (this.pubsubList.length > 0) {
        //   this.pubsubList.forEach(item => {
        //     if(item.id == node.deviceTreeId || item.name == node.sysName){
        //       pubsub.remove(item.id, item.name)
        //     }
        //   })
        // }
        that.$confirm('该操作会删除包含的子节点,确认要删除当前节点 ' + node.sysName + ' 吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let param = {}
          param.deviceTreeId = node.deviceTreeId
          param.deviceNodeId = node.deviceNodeId
          param.sysName = node.sysName
          param.deviceType = node.deviceType
          param.deviceTreeFatherId = node.deviceTreeFatherId
          param.deviceNodeFunName = node.deviceNodeFunName
          param.deviceNodeFunType = node.deviceNodeFunType
          param.url = node.url
          if (node.deviceNodeId == deviceTreeSettings.AI
            || node.deviceNodeId == deviceTreeSettings.AO
            || node.deviceNodeId == deviceTreeSettings.DI
            || node.deviceNodeId == deviceTreeSettings.DO
            || node.deviceNodeId == deviceTreeSettings.Vpoint) {
            param.deleteAll = true
          }
          deleteTreeNode(param).then(response => {
            //若关联，需要二次确认
            if (response.code == 209) {
              that.$confirm(response.msg + ',确认要删除当前节点 ' + node.sysName + ' 吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                let paramTwo = {}
                paramTwo.deviceTreeId = node.deviceTreeId
                paramTwo.deviceNodeId = node.deviceNodeId
                paramTwo.sysName = node.sysName
                paramTwo.deviceType = node.deviceType
                paramTwo.deviceTreeFatherId = node.deviceTreeFatherId
                paramTwo.deviceNodeFunName = node.deviceNodeFunName
                paramTwo.deviceNodeFunType = node.deviceNodeFunType
                paramTwo.url = node.url
                paramTwo.deleteAll = true
                deleteTreeNode(paramTwo).then(responseTwo => {
                  if (responseTwo.code == 200) {//删除成功
                    this.$modal.msgSuccess(responseTwo.msg)
                    //删除el-tree树节点
                    if (node.deviceNodeId != deviceTreeSettings.AI
                      && node.deviceNodeId != deviceTreeSettings.AO
                      && node.deviceNodeId != deviceTreeSettings.DI
                      && node.deviceNodeId != deviceTreeSettings.DO
                      && node.deviceNodeId != deviceTreeSettings.UI
                      && node.deviceNodeId != deviceTreeSettings.UX) {
                      let parentNodee = that.$refs.deviceTree.$refs.Tree.getNode(that.treeNodeData.id).parent
                      this.$nextTick(() => {
                        this.$refs.deviceTree.$refs.Tree.setCurrentKey(parentNodee)
                        this.handleNodeClick(parentNodee.data)
                      })
                      //删除el-tree树节点
                      that.$refs.deviceTree.$refs.Tree.remove(
                        node,
                        that.treeNodeData
                      )
                    } else {
                      this.treeNodeMsg = responseTwo.data
                      let pointName = ' | DO节点'
                      if (responseTwo.data.deviceNodeId == deviceTreeSettings.AI) {
                        pointName = ' | AI节点'
                      } else if (responseTwo.data.deviceNodeId == deviceTreeSettings.AO) {
                        pointName = ' | AO节点'
                      } else if (responseTwo.data.deviceNodeId == deviceTreeSettings.DI) {
                        pointName = ' | DI节点'
                      } else if (responseTwo.data.deviceNodeId == deviceTreeSettings.UI) {
                        pointName = ' | UI节点'
                      } else if (responseTwo.data.deviceNodeId == deviceTreeSettings.UX) {
                        pointName = ' | UX节点'
                      }
                      responseTwo.data.name = pointName
                      responseTwo.data.deviceTreeId = responseTwo.data.treeId
                      responseTwo.data.id = responseTwo.data.treeId
                      responseTwo.data.left = true
                      responseTwo.data.deviceNodeId = responseTwo.data.nodeType
                      responseTwo.data.alias = pointName
                      this.$refs.deviceTree.$refs.Tree.getNode(nodeData.deviceTreeId).data = responseTwo.data
                    }
                  } else if (responseTwo.code == 0) {
                    this.$modal.msgWarning(responseTwo.msg)
                  }
                })
              })
            } else if (response.code == 200) {//删除成功
              this.$modal.msgSuccess(response.msg)
              if (node.deviceNodeId != deviceTreeSettings.AI
                && node.deviceNodeId != deviceTreeSettings.AO
                && node.deviceNodeId != deviceTreeSettings.DI
                && node.deviceNodeId != deviceTreeSettings.DO
                && node.deviceNodeId != deviceTreeSettings.UI
                && node.deviceNodeId != deviceTreeSettings.UX) {
                //定位到删除节点的父节点
                let parentNode = that.$refs.deviceTree.$refs.Tree.getNode(that.treeNodeData.id).parent
                this.$nextTick(() => {
                  this.$refs.deviceTree.$refs.Tree.setCurrentKey(parentNode)
                  this.handleNodeClick(parentNode.data)
                })
                //删除el-tree树节点
                that.$refs.deviceTree.$refs.Tree.remove(
                  node,
                  that.treeNodeData
                )

              } else {
                response.data.deviceNodeId = response.data.nodeType
                response.data.deviceTreeId = response.data.treeId
                response.data.id = response.data.treeId
                let pointName = ' | DO节点'
                if (response.data.nodeType == deviceTreeSettings.AI) {
                  pointName = ' | AI节点'
                } else if (response.data.nodeType == deviceTreeSettings.AO) {
                  pointName = ' | AO节点'
                } else if (response.data.nodeType == deviceTreeSettings.DI) {
                  pointName = ' | DI节点'
                } else if (response.data.nodeType == deviceTreeSettings.UI) {
                  pointName = ' | UI节点'
                } else if (response.data.nodeType == deviceTreeSettings.UX) {
                  pointName = ' | UX节点'
                }

                response.data.name = pointName
                response.data.left = true
                response.data.alias = pointName
                this.$refs.deviceTree.$refs.Tree.getNode(response.data.treeId).data = response.data
                this.treeNodeMsg = response.data
                this.$nextTick(() => {
                  //加载新增的节点
                  this.handleNodeClick(response.data)
                })
              }
            } else if (response.code == 0) {
              this.$modal.msgWarning(response.msg)
            }
          })
        }).catch(() => {
        })
      },
      /*******************************************调试树节点操作*********************************************************/
      debuggerTreeNode(id, node) {
        //订阅客户端
        let event = { event: node.deviceTreeId }
        subscribe(event).then(res => {
          // console.log('添加订阅')
          //区分点位类型
          if (id == deviceTreeSettings.vpointDebugger || id == deviceTreeSettings.vpointValue) {
            this.pointType = id
            this.$refs.debuggerPageInfo.form.treeId = node.deviceTreeId
            this.$refs.debuggerPageInfo.form.unit = this.$refs.PointInfoMsg.queryForm.engineerUnit
            this.$refs.debuggerPageInfo.form.sysName = this.$refs.PointInfoMsg.queryForm.sysName
            this.$refs.debuggerPageInfo.form.initValue = Number(this.$refs.PointInfoMsg.queryForm.initVal)
            this.$refs.debuggerPageInfo.form.workMode = this.$refs.PointInfoMsg.queryForm.workMode
            this.$refs.debuggerPageInfo.form.accuracy = this.$refs.PointInfoMsg.queryForm.accuracy
            if (id == deviceTreeSettings.vpointDebugger) {
              if(!this.$refs.PointInfoMsg.saveBoolean){
                this.$modal.confirm("数据已修改，请先保存！")
              }else {
                this.$refs.debuggerPageInfo.visible = true
                this.$refs.debuggerPageInfo.editDebuggerInfo(false,id)
              }
            } else {
              this.$refs.debuggerPageInfo.ruleForms = { items: [{ description: '', initVal: '' }] }
              // this.$refs.debuggerPageInfo.visibleData = true
              this.$refs.debuggerPageInfo.editDebuggerInfo(true,id)
            }
          } else {
            this.pointType = id
            this.$refs.debuggerPageInfo.form.treeId = node.deviceTreeId
            this.$refs.debuggerPageInfo.form.unit = this.$refs.PointInfoMsg.queryFormDO.engineerUnit
            this.$refs.debuggerPageInfo.form.sysName = this.$refs.PointInfoMsg.queryFormDO.sysName
            this.$refs.debuggerPageInfo.form.initValue = Number(this.$refs.PointInfoMsg.queryFormDO.initVal)
            this.$refs.debuggerPageInfo.form.workMode = this.$refs.PointInfoMsg.queryFormDO.workMode
            this.$refs.debuggerPageInfo.form.accuracy = this.$refs.PointInfoMsg.queryFormDO.accuracy
            if (id == deviceTreeSettings.AODebugger || id == deviceTreeSettings.DODebugger) {
              if(!this.$refs.PointInfoMsg.saveBooleanDO){
                this.$modal.confirm("数据已修改，请先保存！")
              }else {
                this.$refs.debuggerPageInfo.visible = true
                this.$refs.debuggerPageInfo.editDebuggerInfo(false,id)
              }
            } else {
              this.$refs.debuggerPageInfo.ruleForms = { items: [{ description: '', initVal: '' }] }
              // this.$refs.debuggerPageInfo.visibleData = true
              this.$refs.debuggerPageInfo.editDebuggerInfo(true,id)
            }
          }
        })
      },

      // ---删除树节点
      deleteTreeItem(data) {
        this.$confirm('确认要删除当前节点 ' + data.name + ' 吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            // 删除树节点
            DeleteTreeselect({ uid: data.uid }).then(response => {
              this.$message({ type: 'success', message: '删除成功' })
              this.clean()
              this.$refs.customTree.treeDeleteItem(data)
            })
          })
          .catch(() => {
          })
      },
      //模块错误回调实时改变右侧表单状态
      changeState(data) {
        if (data.deviceNodeId.toString() == deviceTreeSettings.model) {
          this.$refs.ModuleInfoMsg.queryFormModel.onlineState = data.deviceTreeStatus
        }
      },
      /** 导入按钮操作 */
      handleImport() {
        this.upload.title = "用户导入";
        this.upload.open = true;
      },
      /** 下载模板操作 */
      importTemplate() {

        const iframe = document.createElement("iframe");
        //考虑线上环境是否能找到文件所在路径，
        //就得拿到路由的base（配置单页应用的基本路径）来进行判断
        if (this.$router.history.base) {
          iframe.src =
            this.$router.history.base + "/static/excel/楼宇模板.xls";
        } else {
          iframe.src = "/static/excel/楼宇模板.xls";
        }
        iframe.style.display = "none"; // 防止影响页面
        iframe.style.height = 0; // 防止影响页面
        document.body.appendChild(iframe); // 必写，iframe挂在到dom树上才会发请求
        // 定时删除节点
        setTimeout(() => {
          document.body.removeChild(iframe);
        }, 2000);
      },
      // 提交上传文件
      submitFileForm() {
        this.$refs.upload.submit();
      },
      // 文件上传中处理
      handleFileUploadProgress(event, file, fileList) {
        this.upload.isUploading = true;
      },
      // 文件上传成功处理
      handleFileSuccess(response, file, fileList) {
        this.upload.open = false;
        this.upload.isUploading = false;
        this.$refs.upload.clearFiles();
        this.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });

        //刷新树
        let resloveTags = []
        let resloveQueryParams = {
          deviceTreeFatherId: -1
        }
        listTree(resloveQueryParams).then(response => {
          resloveTags = response.data
          resloveTags.forEach((item, index) => {

            // 节点需要构建为 tree 的结构
            item.name = item.sysName
            item.id = item.deviceTreeId
            item.leaf = false

            //刷新所有根节点
            const nodes = this.$refs.deviceTree.$refs.Tree.getNode(item.deviceTreeId)
            nodes.loadData();
          })

          //定位到第一个根节点下的子节点
          this.$nextTick(() => {
            const nodes = this.$refs.deviceTree.$refs.Tree.getNode(resloveTags[0].id)
            this.$refs.deviceTree.$refs.Tree.setCurrentKey(nodes)
            this.handleNodeClick(resloveTags[0])



          })

        })




        // this.getList();
      },
    }
  }
</script>
<style lang="scss" scoped>
  .wait-task-user-box-card {
    height: calc(92vh - 60px - 10px);
  }
</style>
