<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模式名称" prop="name">
        <el-input size="small" clearable v-model="queryParams.name" placeholder="请输入模式名称"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['scheduling:sceneConfig:model:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-setting"
          size="mini"
          @click="handleConfig(null,'1')"
        >批量配置
        </el-button>
      </el-col>
      <!--      <el-col :span="1.5">-->
      <!--        <el-button-->
      <!--          type="success"-->
      <!--          plain-->
      <!--          icon="el-icon-edit"-->
      <!--          size="mini"-->
      <!--          :disabled="single"-->
      <!--          @click="handleUpdate"-->
      <!--          v-hasPermi="['scheduling:sceneConfig:model:edit']"-->
      <!--        >修改-->
      <!--        </el-button>-->
      <!--      </el-col>-->
      <!--      <el-col :span="1.5">-->
      <!--        <el-button-->
      <!--          type="danger"-->
      <!--          plain-->
      <!--          icon="el-icon-delete"-->
      <!--          size="mini"-->
      <!--          :disabled="multiple"-->
      <!--          @click="handleDelete"-->
      <!--          v-hasPermi="['sscheduling:sceneConfig:model:remove']"-->
      <!--        >删除-->
      <!--        </el-button>-->
      <!--      </el-col>-->
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['scheduling:sceneConfig:model:export']"
        >导出
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-close"
          size="mini"
          @click="handleClose"
        >关闭
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getSceneModelList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange"
              style="max-height: 75vh;overflow-y: auto;">
      <!--<el-table-column type="selection" width="55" align="center"/>-->
<!--      <el-table-column label="序号" align="center" prop="id"/>-->
      <el-table-column label="模式ID" align="center" prop="modelId"/>
      <el-table-column label="模式名称" align="center" prop="name"/>
      <el-table-column label="创建人" align="center" prop="createBy"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="修改人" align="center" prop="updateBy"/>
      <el-table-column label="修改时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['scheduling:sceneConfig:model:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-setting"
            @click="handleConfig(scope.row.id,'2')"
            v-hasPermi="['scheduling:sceneConfig:model:getSceneModelPoint']"
          >配置
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleSync(scope.row)"
            v-hasPermi="['scheduling:sceneConfig:model:sync']"
          >同步
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-sort"
            @click="handleContrast(scope.row)"
            v-hasPermi="['scheduling:sceneConfig:model:contrast']"
          >对比
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['scheduling:sceneConfig:model:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改场景模式对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="模式名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入模式名称"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button
          v-hasPermi="['scheduling:sceneConfig:model:edit']"
          type="primary" @click="submitForm">确 定
        </el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>


    <!-- 配置模式点位对话框 -->
    <el-dialog :title="pointConfigTitle" :visible.sync="pointConfigOpen" width="1000px" append-to-body
               class="abow_dialogCollMethod">
      <el-col :span="8" style="float: left;">
        <!--<device-tree style="max-height: 70.4vh;overflow-y: auto;"
                     ref="deviceTree"
                     :lazy="lazy"
                     @loadNode="loadNode"
                     :itemShow="itemShow"
                     :tree-expand-all="treeExpandAll"
                     :default-expanded-keys="defaultExpandedKeys"
                     :tree-node-key="treeNodeKey"
                     :expand-on-click-node="false"
                     :highlight-current="true"
                     @handleNodeClick='handleNodeClick'
        >
        </device-tree>-->
        <el-tree style="max-height: 70.4vh;overflow-y: auto;"
                 :data="treedata"
                 :props="props"
                 node-key="id"
                 :default-expanded-keys="defaultExpandedKeys"
                 ref="tree"
                 @node-click="handleNodeClick">
        </el-tree>
      </el-col>
      <el-col :span="14" style="float: right;">
        <el-button @click="changePointValue" v-show="isAllUpdate == '2'">一键值配置</el-button>
        <el-table v-loading="pointConfigLoading" :data="pointConfigTable" style="max-height: 70.4vh;overflow-y: auto;">
          <el-table-column label="点位名称" align="center" prop="sysName"/>
          <el-table-column label="点位别名" align="center" prop="alias"/>
          <el-table-column label="点位值" align="center" prop="pointValue" v-if="isAllUpdate == '2'">
            <template slot-scope="scope">
              <el-input v-model="scope.row.pointValue"
                        @focus="focusEvent(scope.row,scope.$index,scope.column)"
                        v-focus></el-input>
              <!--@blur="blurEvent(scope.row,scope.$index,scope.column)"-->
              <!--v-if="scope.row.isValSelected"-->
              <!--              <p @click="cellClick(scope.row, scope.column)" v-else>{{scope.row.pointValue}}</p>-->
            </template>
          </el-table-column>
          <el-table-column label="点位单位" align="center" prop="pointUnit"/>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handlePointConfigDelete(scope.row)"
                v-hasPermi="['scheduling:scenarioConfig:deleteSceneConfigArea']"
              >删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <div slot="footer" class="dialog-footer">
        <el-button
          v-hasPermi="['scheduling:sceneConfig:model:addSceneModelPoint']"
          type="primary" style="position: absolute;right: 55vh;bottom: 10px;" @click="submitPointForm">确 定
        </el-button>
        <el-button style="position: absolute;right: 45vh;bottom: 10px;" @click="cancelPoint">取 消</el-button>
      </div>
    </el-dialog>

    <!--  ************************************************************数据对比************************************** -->
    <el-dialog title="数据对比" :visible.sync="visibleData" width="800px" append-to-body @close="clearParmas()">
      <div class="box">
        <el-row>
          <el-col :span="11" style="background-color: #ebf6fb;text-align: center">
            <el-button style="margin-bottom: 15px;margin-top: 10px">上位机数据</el-button>
            <!-- *******************************上位机模式数据对比***************************************************** -->
            <el-form ref="upperFormData" :inline="true" :model="upperFormData" label-width="90px">
              <el-form-item label="场景ID:" prop="sceneId">
                <el-input v-model="upperFormData.sceneId" readonly/>
              </el-form-item>
              <el-form-item label="场景名称:" prop="sceneName">
                <el-input v-model="upperFormData.sceneName" readonly/>
              </el-form-item>
              <el-form-item label="场景别名:" prop="sceneAlias">
                <el-input v-model="upperFormData.sceneAlias" readonly/>
              </el-form-item>
              <el-form-item label="是否使能:" prop="sceneActive">
                <el-input v-model="upperFormData.sceneActive == 0 ? '否' : '是'" readonly/>
              </el-form-item>
              <el-form-item label="模式ID:" prop="modelId">
                <el-input v-model="upperFormData.modelId" readonly/>
              </el-form-item>
              <el-form-item label="模式名称:" prop="modelName">
                <el-input v-model="upperFormData.modelName" readonly/>
              </el-form-item>
              <el-form-item label="点位ID:" prop="pointId">
                <el-input v-model="upperFormData.pointId" readonly/>
              </el-form-item>
              <el-form-item label="值:" prop="pointValue">
                <el-input v-model="upperFormData.pointValue" readonly/>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="2" style="height: 1px"></el-col>
          <el-col :span="11" style="background-color: #ebf6fb;text-align: center">
            <el-button style="margin-bottom: 15px;margin-top: 10px">下位机数据</el-button>
            <!-- *******************************下位机模式数据对比***************************************************** -->
            <el-form ref="underFormData" :inline="true" :model="underFormData" label-width="90px">
              <el-form-item label="场景ID:" prop="id">
                <el-input v-model="underFormData.id" readonly class="error_input"
                          v-if="upperFormData.sceneId != underFormData.id"/>
                <el-input v-model="underFormData.id" readonly v-else/>
              </el-form-item>
              <el-form-item label="场景名称:" prop="name">
                <el-input v-model="underFormData.name" readonly class="error_input"
                          v-if="upperFormData.sceneName != underFormData.name"/>
                <el-input v-model="underFormData.name" readonly v-else/>
              </el-form-item>
              <el-form-item label="场景别名:" prop="alias">
                <el-input v-model="underFormData.alias" readonly class="error_input"
                          v-if="upperFormData.sceneAlias != underFormData.alias"/>
                <el-input v-model="underFormData.alias" readonly v-else/>
              </el-form-item>
              <el-form-item label="是否使能:" prop="active">
                <el-input v-model="upperFormData.active == 0 ? '否' : '是'" readonly class="error_input"
                          v-if="upperFormData.active != underFormData.sceneActive"/>
                <el-input v-model="upperFormData.active == 0 ? '否' : '是'" readonly v-else/>
              </el-form-item>
              <el-form-item label="模式ID:" prop="modelId">
                <el-input v-model="underFormData.modelId" readonly class="error_input"
                          v-if="upperFormData.modelId != underFormData.modelId"/>
                <el-input v-model="underFormData.modelId" readonly v-else/>
              </el-form-item>
              <el-form-item label="模式名称:" prop="modelName">
                <el-input v-model="underFormData.modelName" readonly class="error_input"
                          v-if="upperFormData.modelName != underFormData.modelName"/>
                <el-input v-model="underFormData.modelName" readonly v-else/>
              </el-form-item>
              <el-form-item label="点位ID:" prop="pointId">
                <el-input v-model="underFormData.pointId" readonly class="error_input"
                          v-if="upperFormData.pointId != underFormData.pointId"/>
                <el-input v-model="underFormData.pointId" readonly v-else/>
              </el-form-item>
              <el-form-item label="值:" prop="pointValue">
                <el-input v-model="underFormData.pointValue" readonly class="error_input"
                          v-if="upperFormData.pointValue != underFormData.pointValue"/>
                <el-input v-model="underFormData.pointValue" readonly v-else/>
              </el-form-item>
            </el-form>

          </el-col>
        </el-row>
      </div>
    </el-dialog>


  </div>
</template>

<script>
  import {
    getSceneModelList,
    addSceneModel,
    updateSceneModel,
    deleteSceneModel,
    getSceneModel,
    addSceneModelPoint,
    getSceneModelPoint,
    modelPointSync,
    modelPointContrast
  } from '@/api/deviceManagement/scheduling/sceneModel'
  import {
    meterTreeSelect
  } from "@/api/basicData/safetyWarning/alarmTactics/alarmTactics"
  import {listEnergy, listVpoint, getDataInfoParam} from '@/api/basicData/deviceManagement/deviceTree/deviceTreePoint'
  import {
    listTree
  } from '@/api/basicData/deviceManagement/deviceTree/deviceTree'
  import deviceTree from '@/views/basicData/deviceManagement/deviceTree/tree'
  import {deviceTreeSettings} from '../../../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings'
  import {mapState} from 'vuex'

  export default {
    name: 'model',
    components: {
      deviceTree
    },
    data() {
      return {
        defaultExpandedKeys: [],
        treedata: [],//树结构数据
        props: {
          children: 'children',
          label: 'label'
        },
        nodeTypeValue: '',
        deviceTreeSettings: deviceTreeSettings,
        //场景ID
        sceneId: null,
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 场景模式表格数据
        dataList: [],
        // 弹出层标题
        title: '',
        // 是否显示弹出层
        open: false,
        // 配置点位弹出层标题
        pointConfigTitle: '',
        // 配置点位弹出层
        pointConfigOpen: false,
        pointConfigLoading: false,
        pointConfigTable: [],
        pointConfigId: null,
        lazy: true,//懒加载
        //树查询
        queryTreeParams: {
          deviceTreeFatherId: -1
        },
        //点位查询
        queryPointParams: {},
        isAllUpdate: null,//批量配置
        //同步点位
        modelSyncParams: {},
        //数据对比
        modelContrastParams: {},
        //数据对比框弹出
        visibleData: false,
        upperFormData: {},//上位机数据
        underFormData: {},//下位机数据
        //树组件
        itemShow: true,
        treeExpandAll: false,
        treeNodeKey: 'id',
        //点位
        pointArr: [deviceTreeSettings.Vpoint, deviceTreeSettings.AI, deviceTreeSettings.AO,
          deviceTreeSettings.DI, deviceTreeSettings.DO],
        noVpointArr: [deviceTreeSettings.AI, deviceTreeSettings.AO,
          deviceTreeSettings.DI, deviceTreeSettings.DO],
        pointNameArr: ['AI节点', 'AO节点', 'DI节点', 'DO节点'],
        accuracyList: [{id: 0, value: 0}, {id: 1, value: 1}, {id: 2, value: 2}, {id: 3, value: 3}, {
          id: 4,
          value: 4
        },
          {id: 5, value: 5}, {id: 6, value: 6}],//精度
        sinnalTypeList: [{id: 0, value: '0-10V'}, {id: 1, value: '0-20mA'}, {id: 2, value: '4-20mA'}],//信号类型
        vNodeTypeList: [],//虚点类型
        energyList: [],//能耗类型
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          dictName: undefined,
          dictType: undefined,
          status: undefined
        },
        // 表单参数d
        form: {},
        // 表单校验
        rules: {
          name: [
            {required: true, message: '模式名称不能为空', trigger: 'blur'}
          ]
        }
      }
    },
    //自定义指令
    directives: {
      focus: {
        inserted: function (el) {
          el.querySelector('input').focus()
        }
      }
    },
    computed: {
      ...mapState({
        //模块信息
        model_websocket: state => state.websocket.sceneModelData,
        //照明模块信息
        model_websocket_ldc: state => state.websocket.sceneModelDataLDC
      })
    },
    watch: {
      //模块信息
      model_websocket(data) {
        if (data == null) {
          return
        }
        let pointId = null
        let pointValue = null
        this.underFormData = data
        this.underFormData.modelId = data.controlMode.id
        this.underFormData.modelName = data.controlMode.name
        if (data.controlMode.controlPoint != null && data.controlMode.controlPoint.length > 0) {
          data.controlMode.controlPoint.forEach(item => {
            if (pointId == null) {
              pointId = item.pointID
              pointValue = item.runValue
            } else {
              pointId = pointId + ',' + item.pointID
              pointValue = pointValue + ',' + item.runValue
            }
          })
        }
        this.underFormData.pointId = pointId
        this.underFormData.pointValue = pointValue

        this.$nextTick(() => {
          this.visibleData = true
        })
      },
      // model_websocket_ldc(data) {
      //   if (data == null) {
      //     return
      //   }
      //   let pointId = null
      //   let pointValue = null
      //   this.underFormData = data
      //   this.underFormData.modelId = data.controlMode.id
      //   this.underFormData.modelName = data.controlMode.name
      //   if (data.controlMode.controlPoint != null && data.controlMode.controlPoint.length > 0) {
      //     data.controlMode.controlPoint.forEach(item => {
      //       if (pointId == null) {
      //         pointId = item.pointID
      //         pointValue = item.runValue
      //       } else {
      //         pointId = pointId + ',' + item.pointID
      //         pointValue = pointValue + ',' + item.runValue
      //       }
      //     })
      //   }
      //   this.underFormData.pointId = pointId
      //   this.underFormData.pointValue = pointValue
      //
      //   this.$nextTick(() => {
      //     this.visibleData = true
      //   })
      // },
      model_websocket_ldc(data) {
        if (data == null) {
          return
        }
        let pointId = null
        let pointValue = null
        this.underFormData = data
        this.underFormData.modelId = data.controlMode.id
        this.underFormData.modelName = data.controlMode.name
        if (data.controlMode.controlPoint != null && data.controlMode.controlPoint.length > 0) {
          data.controlMode.controlPoint.forEach(item => {
            if (pointId == null) {
              pointId = item.pointID
              pointValue = item.runValue
            } else {
              pointId = pointId + ',' + item.pointID
              pointValue = pointValue + ',' + item.runValue
            }
          })
        }
        this.underFormData.pointId = pointId
        this.underFormData.pointValue = pointValue

        this.$nextTick(() => {
          this.visibleData = true
        })
      },
    },

    created() {
      const sceneId = this.$route.params && this.$route.params.sceneId
      this.sceneId = sceneId
      this.getSceneModelList()
    },
    methods: {
      //点击文本触发，显示input框，隐藏p标签
      cellClick(row, column) {
        if (column.label == '点位值') {
          // row.isValSelected = !row.isValSelected
        }
        this.pointConfigOpen = true
      },
      //点击输入框聚焦，存储当前值
      focusEvent(row, index, column) {
        if (column.label == '点位值') {
          row.oldPointValue = row.pointValue
        }
        this.pointConfigOpen = true
      },
      //一键值配置
      changePointValue() {
        var value = null
        for (let i = 0; i < this.pointConfigTable.length; i++) {
          if (this.pointConfigTable[i].pointValue != null && this.pointConfigTable[i].pointValue != undefined) {
            value = this.pointConfigTable[i].pointValue
            break
          }
        }
        if (value == null) {
          this.$modal.msgWarning('请输入一个值')
          return
        }
        this.pointConfigTable.forEach(item => {
          item.pointValue = value
        })
      },
      // //输入框失去焦点触发,此处用提示框提示修改
      // blurEvent(row, curIndex, column) {
      //   if (column.label == '点位值') {
      //     // row.isValSelected = !row.isValSelected
      //     // if (row.pointValue !== row.oldPointValue) {
      //     if(typeof row.pointValue != 'number' || isNaN(row.pointValue)){
      //       this.$modal.msgWarning('请输入正确值')
      //     }
      //   }
      //   this.pointConfigOpen = true
      // },
      /*配置按钮操作*/
      handleConfig(id, isAll) {
        this.isAllUpdate = isAll
        if (this.dataList == undefined || this.dataList.length == 0) {
          this.$modal.msgWarning('请添加模式')
          return
        }
        if (id == null || id == undefined) {
          id = this.dataList[0].id
        }
        meterTreeSelect().then(response => {
          response.data.forEach(val => {
            val.id = val.deviceTreeId;
            val.label = val.sysName;
          })
          this.treedata = this.handleTree(response.data, "deviceTreeId", "deviceTreeFatherId");
          this.defaultExpandedKeys = []
          this.queryPointParams.sceneModelId = id
          this.pointConfigTitle = '配置模式点位'
          this.pointConfigOpen = true
          this.pointConfigLoading = true
          getSceneModelPoint(this.queryPointParams).then(response => {
            if (response.code == 200) {
              if (response.data != null && response.data.length > 0) {
                if (this.isAllUpdate == '1') {
                  response.data.forEach(item => {
                    item.pointValue = null
                    // if (item.pointValue == null) {
                    //   item.pointValue = '请修改点位值'
                    // }
                    // item.isValSelected = false
                  })
                }
                this.pointConfigTable = response.data
                if (response.data.length > 0) {
                  this.pointConfigId = response.data[0].pointId
                  this.defaultExpandedKeys.push(this.pointConfigId)
                }
                //设备树回显第一个点位
                this.$nextTick(() => {
                  const nodes = this.$refs.tree.getNode(this.pointConfigId)
                  this.$refs.tree.setCurrentKey(nodes)
                  // const nodes = this.$refs.deviceTree.$refs.Tree.getNode(pointConfigId)
                  // this.$refs.deviceTree.$refs.Tree.setCurrentKey(nodes)
                  // this.$refs.deviceTree.$refs.Tree.setCurrentKey(pointId)
                })
              } else {
                this.pointConfigTable = []
              }
            }
          })
          this.pointConfigLoading = false
        })

      },
      /*同步按钮操作*/
      handleSync(row) {
        //点位同步接口
        this.modelSyncParams.sceneId = row.sceneId
        this.modelSyncParams.id = row.id
        modelPointSync(this.modelSyncParams).then(response => {
          if (response.code == 200) {
            this.$modal.msgSuccess(response.msg)
          }
        })
      },
      /*对比按钮操作*/
      handleContrast(row) {
        //查询上位机点位信息
        this.modelContrastParams.sceneId = row.sceneId
        this.modelContrastParams.id = row.id
        this.modelContrastParams.modelId = row.modelId
        modelPointContrast(this.modelContrastParams).then(response => {
          if (response.code == 200) {
            this.DataContrastShow(response.data)
          }
        })
      },
      DataContrastShow(data) {
        let cData = {
          sceneId: data.sceneId,//场景ID
          sceneName: data.sceneName,//场景名称
          sceneAlias: data.sceneAlias,//场景别名
          sceneActive: data.sceneActive,//场景使能
          modelId: data.modelId,//模式ID
          modelName: data.modelName,//模式名称
          pointId: data.pointId,//点ID
          pointValue: data.pointValue//点值
        }
        this.upperFormData = cData
        this.visibleData = true
      },
      //清除数据对比
      clearParmas() {
        this.underFormData = {}
      },
      // //加载树
      // async loadNode(node, resolve) {
      //   let that = this
      //   if (node.level === 0) {
      //     let tags = []
      //
      //     await listTree(that.queryTreeParams).then(response => {
      //       tags = response.data
      //       tags.forEach((item, index) => {
      //         // 节点需要构建为 tree 的结构
      //         item.name = item.sysName
      //         item.id = item.deviceTreeId
      //         item.leaf = false
      //       })
      //       return resolve(tags)
      //     })
      //
      //   } else if (node.level >= 0) {
      //     let tags = []
      //     that.queryTreeParams.deviceTreeFatherId = node.data.deviceTreeId
      //     await listTree(that.queryTreeParams).then(response => {
      //       tags = response.data
      //       tags.forEach((item, index) => {
      //         // 节点需要构建为 tree 的结构
      //         item.name = item.sysName
      //         item.id = item.deviceTreeId
      //       })
      //       return resolve(tags)
      //     })
      //   } else {
      //     return resolve([]) // 防止该节点没有子节点时一直转圈的问题出现
      //   }
      //   this.$nextTick(() => {
      //     // 循环展开节点
      //
      //     for (let j = 0; j < node.childNodes.length; j++) { // 循环子节点
      //       // 查找相同id节点使之其一层一层展开
      //       if (pointConfigId != null && pointConfigId != undefined && pointConfigId == node.childNodes[j].data.id) {
      //         node.childNodes[j].expand(); // 会重新调用 懒加载方法 treeLoadNode
      //         break;
      //       }
      //     }
      //   })
      // },
      // 选中点位添加至右侧列表
      handleNodeClick(data) {
        //是点位且有信息
        if (this.pointArr.indexOf(data.deviceNodeId.toString()) > -1 && this.pointNameArr.indexOf(data.name) == -1) {
          let pointMap = {
            pointId: data.id,
            alias: data.sysName,
            sysName: data.redisSysName,
            pointValue: '',
            pointUnit: data.engineerUnit,
            pointControllerId: data.controllerId,
            sceneModelId: this.queryPointParams.sceneModelId
          }
          //右侧没有数据则直接添加
          if (this.pointConfigTable.length == 0) {
            this.pointConfigTable.push(pointMap)
            this.pointConfigOpen = true
            return
          }
          //右侧列表有数据则验重
          let checArr = []
          this.pointConfigTable.forEach(item => {
            checArr.push(item.pointId)
          })
          //是否跨控制器
          if (checArr.indexOf(pointMap.pointId) == -1) {
            if (this.pointConfigTable[0].pointControllerId == pointMap.pointControllerId) {
              this.pointConfigTable.push(pointMap)
            } else {
              this.$modal.msgWarning('不能跨控制器添加点位')
            }
          } else {
            this.$modal.msgWarning('请勿重复添加')
          }
        } else {
          this.$modal.msgWarning('请选择正确的点位')
        }
        this.pointConfigOpen = true
        return
      },
      //提交模式点位配置
      submitPointForm() {
        let isValue
        if (this.isAllUpdate != '1') {
          this.pointConfigTable.forEach(item => {
            if (item.pointValue == '' || item.pointValue == null) {
              isValue = true
            } else {
              isValue = isNaN(item.pointValue)
            }
          })
          if (isValue) {
            this.$modal.msgWarning('请修改正确的点位值')
            this.pointConfigOpen = true
            return
          }
        }
        if (this.pointConfigTable.length == 0) {
          this.$modal.msgWarning('请选择点位')
          this.pointConfigOpen = true
          return
        }
        this.$confirm('确认配置该点位吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let list = []
          let addList = {}
          this.pointConfigTable.forEach(item => {
            let mapInfo = JSON.stringify(item)
            list.push(mapInfo)
          })
          list = list.join('-#-')
          addList['lists'] = list
          addList['isAllUpdate'] = this.isAllUpdate
          addSceneModelPoint(addList).then(response => {
            this.$modal.msgSuccess(response.msg)
            this.pointConfigOpen = false
            return
          })

        })
      },
      /** 删除点位操作 */
      handlePointConfigDelete(row) {
        this.pointConfigTable.some((item, i) => {
          if (item.pointId === row.pointId) {
            this.pointConfigTable.splice(i, 1)
            this.pointConfigOpen = true
            return
          }
        })
      },
      //取消配置弹窗
      cancelPoint() {
        this.pointConfigOpen = false
        this.pointConfigTable = []
      },
      //查询场景模式
      getSceneModelList() {
        this.queryParams.sceneId = this.sceneId
        getSceneModelList(this.queryParams).then(response => {
          if (response.code == 200) {
            this.dataList = response.data
            // this.$modal.msgSuccess(response.msg)
            this.loading = false
          }
        })
      },
      // 取消按钮
      cancel() {
        this.open = false
        this.reset()
      },
      // 表单重置
      reset() {
        this.form = {}
        this.resetForm('form')
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.getSceneModelList()
      },
      /** 返回按钮操作 */
      handleClose() {
        const sceneId = this.$route.params && this.$route.params.sceneId
        const areaId = this.$route.params && this.$route.params.areaId
        const obj = {
          name: 'scenarioConfig',
          params: {
            sceneId: sceneId,
            areaId: areaId
          }
        }
        this.$tab.closeOpenPage(obj)
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm('queryForm')
        this.handleQuery()
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset()
        this.form.sceneId = this.sceneId
        this.open = true
        this.title = '新增场景模式'
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length != 1
        this.multiple = !selection.length
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset()
        if (row.id == null && this.ids.length == 1) {
          this.form.id = this.ids[0]
        } else {
          this.form.id = row.id
        }
        getSceneModel(this.form).then(response => {
          response.data.params = null
          this.form = response.data
          this.open = true
          this.title = '修改场景模式'
        })
      },
      /** 提交按钮 */
      submitForm: function () {
        this.$refs['form'].validate(valid => {
          if (valid) {
            // this.$confirm('确认提交场景模式信息吗?', '提示', {
            //   confirmButtonText: '确定',
            //   cancelButtonText: '取消',
            //   type: 'warning'
            // }).then(() => {
            if (this.form.id != undefined || this.form.id != null) {
              this.form.createTime = null
              updateSceneModel(this.form).then(response => {
                this.$modal.msgSuccess('修改成功')
                this.open = false
                this.getSceneModelList()
              })
            } else {
              addSceneModel(this.form).then(response => {
                this.$modal.msgSuccess('新增成功')
                this.open = false
                this.getSceneModelList()
              })
            }
            // })
          }
        })
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const dictCodes = row.id || this.ids
        this.$modal.confirm('确认删除场景模式信息吗?').then(function () {
          return deleteSceneModel(dictCodes)
        }).then(() => {
          this.getSceneModelList()
          this.$modal.msgSuccess('删除成功')
        }).catch(() => {
        })
      },
      /** 导出按钮操作 */
      handleExport() {
        if (this.dataList == undefined || this.dataList.length == 0) {
          this.$modal.msgWarning('无数据')
          return
        }
        this.download('/deviceManagement/scheduling/scenarioConfig/model/export', {
          ...this.queryParams
        }, `计划场景模式列表.xlsx`)
      }

    }
  }
</script>
<style lang="scss" scoped>
  .abow_dialogCollMethod {
    display: flex;
    justify-content: center;
    overflow: hidden;
    overflow-y: auto;
    margin-bottom: 20px !important;

    .el-dialog {
      /*height: 80%;*/
      margin-bottom: 20px !important;
      overflow: hidden;
      overflow-y: auto;

      .el-dialog__body {
        margin-bottom: 20px !important;
        position: absolute;
        left: 0;
        top: 54px;
        right: 0;
        padding: 0;
        z-index: 1;
        overflow: hidden;
        overflow-y: auto;
      }
    }
  }

  .box {
    // 必须有高度 overflow 为自动
    overflow: auto;
    height: 490px;
    padding: 0px 30px 11px 27px;

    // 滚动条的样式,宽高分别对应横竖滚动条的尺寸
    &::-webkit-scrollbar {
      width: 3px;
    }

    // 滚动条里面默认的小方块,自定义样式
    &::-webkit-scrollbar-thumb {
      background: #8798AF;
      border-radius: 2px;
    }

    // 滚动条里面的轨道
    &::-webkit-scrollbar-track {
      background: transparent;
    }
  }

  .error_input {
    ::v-deep .el-input__inner {
      border-color: red;
      color: red;
    }
  }
</style>
