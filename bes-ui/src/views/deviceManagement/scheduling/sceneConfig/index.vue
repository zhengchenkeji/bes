<template>
  <div class="app-container">
    <el-row :gutter="20" class="mb20">
      <!-- 分组数据 -->
      <el-col :span="6" :xs="24">

        <div class="head-container">
          <el-card class="wait-task-user-box-card" style="max-height: 85vh">

            <div style="max-height: 80vh;overflow-y: auto;width: 100%;min-width: 160px;">
              <el-tree
                node-key="id"
                class="filter-tree"
                :data="areaOptions"
                :props="defaultProps"
                :expand-on-click-node="false"
                :filter-node-method="filterNode"
                :default-expanded-keys="defaultExpandedKeys"
                :default-checked-keys="[1]"
                ref="areaTree"
                default-expand-all
                @node-click="handleNodeClick"
                highlight-current
              >
                <span slot-scope="{ node, data }">
                  <span class="tooltip">
                    <span style="padding-left: 10px">{{ data.name }}</span>
                  </span>
                <div v-if="node.isCurrent === true"
                     class="operation-view">

                  <el-dropdown @command="areaOperation">
                    <span class="el-dropdown-link">
                      操作<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                       <el-dropdown-item
                         v-for="(item, index) in areaButton"
                         :icon="item.icon"
                         :command="item.type">{{item.name}}</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
                </span>
              </el-tree>
            </div>
          </el-card>
        </div>

        <!-- 添加或修改区域对话框 -->
        <el-dialog :title="title" :visible.sync="areaOpen" width="500px">
          <el-form ref="areaForm" :model="areaForm" :rules="areaRules" label-width="80px">
            <el-form-item label="区域名称" prop="name">
              <el-input v-model="areaForm.name" placeholder="请输入区域名称"/>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="areaForm.remark" placeholder="请输入备注"/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitAreaForm">确 定</el-button>
            <el-button @click="areaCancel">取 消</el-button>
          </div>
        </el-dialog>
      </el-col>

      <!-- 场景 -->
      <el-col :span="18" :xs="24">
        <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="场景名称" prop="name">
            <el-input size="small" clearable v-model="queryParams.name" placeholder="请输入名称"/>
          </el-form-item>
          <el-form-item label="使能状态" prop="active">
            <el-select v-model="queryParams.active" placeholder="请选择使能状态" clearable size="small">
              <el-option
                v-for="dict in activeList"
                :key="dict.id"
                :label="dict.label"
                :value="dict.id"
                size="small"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <el-row type="flex" :gutter="10" class="mb8" style="justify-content: space-between;align-items: center;">
          <div>
            <el-col :span="1.5">
              <el-button
                type="primary"
                plain
                icon="el-icon-plus"
                size="mini"
                @click="handleAdd"
                v-hasPermi="['scheduling:scenarioConfig:add']"
              >新增
              </el-button>
            </el-col>
            <!--<el-col :span="1.5">
              <el-button
                type="success"
                plain
                icon="el-icon-edit"
                size="mini"
                :disabled="single"
                @click="handleUpdate"
                v-hasPermi="['scheduling:scenarioConfig:edit']"
              >修改
              </el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button
                type="danger"
                plain
                icon="el-icon-delete"
                size="mini"
                :disabled="multiple"
                @click="handleDelete"
                v-hasPermi="['scheduling:scenarioConfig:remove']"
              >删除
              </el-button>
            </el-col>-->
            <el-col :span="1.5">
              <el-button
                type="warning"
                plain
                icon="el-icon-download"
                size="mini"
                @click="handleExport"
                v-hasPermi="['scheduling:scenarioConfig:export']"
              >导出
              </el-button>
            </el-col>
          </div>

          <right-toolbar :showSearch.sync="showSearch" @queryTable="getsceneConfigList"></right-toolbar>
        </el-row>
        <el-table v-loading="loading" :data="sceneConfigList" @selection-change="handleSelectionChange"
                  style="max-height: 75vh;overflow-y: auto;">
          <!--<el-table-column type="selection" width="55" align="center"/>-->
          <el-table-column label="序号" width="55" type="index" align="center"/>
          <el-table-column label="场景名称" align="center" prop="name"/>
          <el-table-column label="场景别名" align="center" prop="alias"/>
          <el-table-column label="场景说明" align="center" prop="description"/>
          <el-table-column label="使能状态" align="center" prop="active" width="80">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.active"
                active-color="#2176eb"
                inactive-color="#ff4949"
                :active-value="1"
                :inactive-value="0"
                @change="swichChange($event,scope.row)">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <!--<el-button
                size="mini"
                type="text"
                icon="el-icon-search"
                @click="handleLook(scope.row)"
                v-hasPermi="['iot:device:query']"
              >详细
              </el-button>-->
              <el-button
                size="mini"
                type="text"
                icon="el-icon-search"
                @click="handleToModel(scope.row)"
                v-hasPermi="['scheduling:scenarioConfig:edit']"
              >模式
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['scheduling:scenarioConfig:edit']"
              >修改
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['scheduling:scenarioConfig:remove']"
              >删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!--<pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getsceneConfigList"
        />-->

        <!-- 添加或修改场景对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="场景名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入场景名称"/>
            </el-form-item>
            <el-form-item label="场景别名" prop="alias">
              <el-input v-model="form.alias" placeholder="请输入场景别名"/>
            </el-form-item>
            <el-form-item label="场景说明" prop="description">
              <el-input v-model="form.description" placeholder="请输入场景说明"/>
            </el-form-item>
            <el-form-item label="使能状态" prop="active">
              <el-switch v-model="form.active" :active-value="1" :inactive-value="0"
                         active-color="#2176eb" inactive-color="#ff4949"/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button
              v-hasPermi="['scheduling:scenarioConfig:edit']"
              type="primary" @click="submitForm">确 定
            </el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </el-dialog>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {
    sceneConfigAreaListInfo,
    addSceneConfigArea,
    updateSceneConfigArea,
    deleteSceneConfigArea,
    getsceneConfigList,
    addSceneConfig,
    getsceneConfig,
    updateSceneConfig,
    deleteSceneConfig
  } from '@/api/deviceManagement/scheduling/scheduling'
  import Treeselect from '@riophae/vue-treeselect'
  import '@riophae/vue-treeselect/dist/vue-treeselect.css'
  import {deleteDesignerArea} from '@/api/designerConfigure/designer'

  export default {
    name: 'index',
    components: {Treeselect},
    data() {
      return {
        areaOptions: undefined,
        defaultExpandedKeys: [0],//默认展开的数组
        defaultProps: {
          children: 'children',
          label: 'name'
        },
        areaButton: [
          {type: 'addArea', icon: 'el-icon-circle-plus', name: '新增区域'},
          {type: 'updateArea', icon: 'el-icon-edit', name: '修改区域'},
          {type: 'deleteArea', icon: 'el-icon-remove', name: '删除区域'}
        ],
        areaId: null,//区域id
        treeNodeData: {},//树节点信息
        ids: [],//删除树节点ID
        single: true,// 非单个禁用
        multiple: true,// 非多个禁用
        showSearch: true,// 显示搜索条件
        queryParams: {// 查询参数
          // pageNum: 1,
          // pageSize: 10,
          name: null,
          active: null
        },
        loading: false,
        sceneConfigList: [],//场景表格数据
        areaOpen: false,//新增/修改区域页面
        areaForm: {
          id: null,
          name: null,
          remark: null,
          parentId: null
        },
        total: 0,// 总条数
        title: '',//弹窗标题
        open: false,//新增/修改场景页面
        form: {
          id: null,
          name: null,
          alias: null,
          schedulingAreaId: null,
          description: null,
          active: null
        },
        activeList: [{id: 0, label: '关闭'}, {id: 1, label: '开启'}],
        areaRules: {// 表单校验
          name: [
            {required: true, message: '区域名称不能为空', trigger: 'change'}
          ]
        },
        rules: {// 表单校验
          name: [
            {required: true, message: '场景名称不能为空', trigger: 'bulr'}
          ],
          alias: [
            {required: true, message: '场景别名不能为空', trigger: 'bulr'}
          ]

        }

      }
    },

    // deviceManagement
    // scheduling
    // planConfig

    async created() {
      //获取区域树节点信息
      this.getScenarioConfigAreaList()
    },
    mounted() {

    },
    methods: {

      /****************************************************************左侧树***************************************************************/
      //获取区域树节点信息
      getScenarioConfigAreaList() {
        const areaId = this.$route.params && this.$route.params.areaId
        const that = this
        sceneConfigAreaListInfo().then(response => {
          if (response.code == 200) {
            if (typeof response.data != 'undefined') {
              if (response.data.length > 0) {
                this.areaOptions = []
                const data = {id: 0, name: '区域名称', children: []}
                data.children = this.handleTree(response.data, 'id', 'parentId')
                that.areaOptions.push(data)
                that.$nextTick(() => {
                  that.areaId = that.areaOptions[0].children[0].id.toString()
                  if (areaId != null && areaId != undefined) {
                    that.$refs.areaTree.setCurrentKey(areaId)
                  } else {
                    that.$refs.areaTree.setCurrentKey(that.areaId)
                  }
                  this.treeNodeData = that.$refs.areaTree.getCurrentNode()
                  let form = {}
                  form.areaId = that.areaId
                  //获取当前点击节点的所有的场景信息
                  this.getsceneConfigList(/*form*/)
                })
              }
            } else {
              this.areaOptions = []
              const data = {id: 0, name: '区域名称', children: []}
              that.areaOptions.push(data)
            }
          }
        })
      },
      // 筛选节点
      filterNode(value, data) {
        if (!value) return true
        return data.label.indexOf(value) !== -1
      },

      //节点单击事件
      handleNodeClick(data) {
        //赋值node信息
        if (this.treeNodeData.id != data.id) {
          this.treeNodeData = data
          //查询右侧场景table
          this.getsceneConfigList()
        }
        return
      },
      //区域按钮操作
      areaOperation(command) {
        const that = this
        if (command == 'addArea') {//新增区域
          this.title = '新增区域'
          this.areaOpen = true
          this.areaForm.parentId = this.treeNodeData.id
        } else if (command == 'updateArea') {//修改区域
          this.title = '修改区域'
          this.areaForm.id = this.treeNodeData.id
          this.areaForm.name = this.treeNodeData.name
          this.areaForm.remark = this.treeNodeData.remark
          this.areaOpen = true

        } else if (command == 'deleteArea') {//删除区域
          this.$confirm('确认删除当前树节点 ' + this.treeNodeData.name + ' 吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            if (this.treeNodeData.id == 0) {
              this.$modal.msgWarning('当前节点不可删除')
              return
            }
            this.ids.push(this.treeNodeData.id)
            this.getChildrenIdList(this.treeNodeData)
            deleteSceneConfigArea(this.ids).then(response => {
              if (response.code == 200) {
                that.ids.length = 0
                that.$modal.msgSuccess(response.msg)
                that.$refs.areaTree.remove(
                  that.treeNodeData
                )
                this.getsceneConfigList(/*form*/)
              } else {
                that.$modal.msgWarning(response.msg)
              }
            })
          })
        }
      },
      //获取当前节点下子节点的id数组
      getChildrenIdList(data) {

        if (typeof data.children != 'undefined' && data.children != null) {
          data.children.forEach(val => {
            this.ids.push(val.id)
            this.getChildrenIdList(val)
          })

        }
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.loading = true
        this.getsceneConfigList()
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.queryParams = {
          name: null,
          active: null
        }
        this.handleQuery()
      },

      //区域新增/修改
      submitAreaForm() {
        const that = this

        this.$refs['areaForm'].validate(valid => {
          if (valid) {
            // this.$confirm('确认提交树节点信息吗?', '提示', {
            //   confirmButtonText: '确定',
            //   cancelButtonText: '取消',
            //   type: 'warning'
            // }).then(() => {
            if (this.areaForm.id != null) {
              updateSceneConfigArea(this.areaForm).then(response => {
                if (response.code == 200) {
                  this.$refs.areaTree.getNode(this.treeNodeData.id).data.name = this.areaForm.name
                  this.$refs.areaTree.getNode(this.treeNodeData.id).data.remark = this.areaForm.remark
                  this.$modal.msgSuccess(response.msg)
                  this.areaOpen = false
                  this.areaReset()
                } else {
                  this.$modal.msgWarning(response.msg)
                }
              })
            } else {
              addSceneConfigArea(this.areaForm).then(response => {
                if (response.code == 200) {
                  let nodeData = {}
                  nodeData.id = response.data.id
                  nodeData.name = response.data.name
                  this.$refs.areaTree.append(
                    nodeData,
                    this.treeNodeData
                  )
                  this.$modal.msgSuccess(response.msg)
                  this.areaOpen = false
                  this.areaReset()
                } else {
                  this.$modal.msgWarning(response.msg)
                }
              })
            }
            // })
          }
        })
      },
      // 取消按钮
      areaCancel() {
        this.areaOpen = false
        this.areaReset()
      },
      /****************************************************************右侧列表***************************************************************/
      // 表单重置
      areaReset() {
        this.areaForm = {
          id: null,
          name: null,
          remark: null,
          parentId: null
        }
        this.resetForm('areaForm')
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length != 1
        this.multiple = !selection.length
      },
      /** 查询场景列表 */
      getsceneConfigList() {
        this.loading = true
        this.sceneConfigList = []
        this.queryParams.schedulingAreaId = this.treeNodeData.id
        getsceneConfigList(this.queryParams).then(response => {
          if (response.code == 200) {
            this.sceneConfigList = response.data
            // this.$modal.msgSuccess(response.msg)
            this.loading = false
          }
        })
      },
      /** 新增场景 */
      handleAdd() {
        this.reset()
        this.form.schedulingAreaId = this.treeNodeData.id
        this.form.active = 1
        this.title = '新增场景'
        this.open = true
      },

      /** 修改场景 */
      swichChange(active, item) {
        let params = {}
        params.id = item.id
        params.name = item.name
        params.active = active
        updateSceneConfig(params).then(response => {
          if (response.code == 200) {
            this.$modal.msgSuccess(response.msg)
            this.reset()
            this.getsceneConfigList()
          } else {
            this.$modal.msgWarning(response.msg)
          }
        })
      },
      handleUpdate(row) {
        this.reset()
        if (row.id == null && this.ids.length == 1) {
          this.form.id = this.ids[0]
        } else {
          this.form.id = row.id
        }
        getsceneConfig(this.form).then(response => {
          if (response.code == 200) {
            //表单赋值
            this.form.name = response.data.name
            this.form.alias = response.data.alias
            this.form.active = response.data.active
            this.form.description = response.data.description
            this.title = '修改场景'
            this.open = true
          } else {
            this.$modal.msgWarning(response.msg)
          }
        })
      },

      /** 删除场景 */
      handleDelete(row) {
        this.$confirm('确认删除场景信息吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let id = row.id || this.ids
          deleteSceneConfig(id).then(response => {
            if (response.code == 200) {
              this.$modal.msgSuccess(response.msg)
              this.getsceneConfigList()
            } else {
              this.$modal.msgWarning(response.msg)
              this.getsceneConfigList()
            }
          })
        })
      },

      /** 跳转模式页面 */
      handleToModel(row) {
        let url = '/iot/model/index/' + row.id + '/' + this.treeNodeData.id
        this.$router.push(url)
      },

      submitForm() {
        this.$refs['form'].validate(valid => {
          if (valid) {
            // this.$confirm('确认提交场景提示', {
            //   confirmButtonText: '确定',
            //   cancelButtonText: '取消',
            //   type: 'warning'
            // }).then(() => {
            if (this.form.id != null) {
              updateSceneConfig(this.form).then(response => {
                if (response.code == 200) {
                  this.$modal.msgSuccess(response.msg)
                  this.open = false
                  this.reset()
                  this.getsceneConfigList()
                } else {
                  this.$modal.msgWarning(response.msg)
                }
              })
            } else {
              addSceneConfig(this.form).then(response => {
                if (response.code == 200) {
                  this.$modal.msgSuccess(response.msg)
                  this.open = false
                  this.reset()
                  this.getsceneConfigList()
                } else {
                  this.$modal.msgWarning(response.msg)
                }
              })
            }
            // })
          }
        })
      },
      /** 导出按钮操作 */
      handleExport() {
        this.queryParams.schedulingAreaId = this.treeNodeData.id
        this.download('/deviceManagement/scheduling/scenarioConfig/export', {
          ...this.queryParams
        }, `计划场景列表.xlsx`)
      },
      // 取消按钮
      cancel() {
        this.open = false
        this.reset()
      },
      // 表单重置
      reset() {
        this.form = {
          id: null,
          name: null,
          alias: null,
          schedulingAreaId: null,
          description: null,
          active: null
        },
          this.resetForm('form')
      }
    }
  }
</script>

<style lang="scss" scoped>
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }

  .el-icon-arrow-down {
    font-size: 12px;
  }

  .el-tree {
    display: inline-block;
    min-width: 100%;

  }

  .tooltip {
    margin-right: 5px;
    font-size: 13px;
    border-radius: 4px;
    box-sizing: border-box;
    white-space: nowrap;
    padding: 4px;
  }

  .operation-view {
    display: inline-block;
    padding: 0px 5px;
    margin-left: 5px;
    color: #777777;
  }

  .time {
    font-size: 13px;
    color: #999;
  }

  .bottom {
    margin-top: 13px;
    line-height: 12px;
  }

  .button {
    padding: 0;
    float: right;
  }

  .image {
    width: 100%;
    display: block;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }

  .clearfix:after {
    clear: both
  }

  .wait-task-user-box-card {
    height: calc(96vh - 60px - 10px);
  }

  #pane-1 .el-descriptions {
    margin-bottom: 25px;
  }

  .el-scrollbar__wrap {
    overflow-x: hidden;
  }

  .el-drawer__wrapper > > > .el-descriptions-item__cell {
    width: 50%;
  }

  .el-drawer__wrapper > > > .input-with-select .el-input__inner {
    width: 100%;
  }

  .el-drawer__wrapper > > > .input-with-select .el-input-group__append {
    width: 25%;
  }

  .theme-blue {
    .tooltip {
      margin-right: 5px;
      font-size: 13px;
      border-radius: 4px;
      box-sizing: border-box;
      white-space: nowrap;
      padding: 4px;
      color: white;
    }
  }

  .theme-white {
    .tooltip {
      margin-right: 5px;
      font-size: 13px;
      border-radius: 4px;
      box-sizing: border-box;
      white-space: nowrap;
      padding: 4px;
      color: #999;
    }
  }

  .theme-blue .text_white {
    color: white;
  }

  .theme-white .text_white {
    color: #999;
  }

  .input-with-select_title {
    margin: 15px 0;
    color: #000;
  }

  .addDom {
    width: 61%;
  }

  .attributeDropdown {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 3.35vh;
  }

  .theme-blue .addDom .el-icon-question {
    color: white;
  }

  .theme-light, .theme-dark .addDom .el-icon-question {
    color: rgb(204, 204, 204);
  }

  .attributeDropdown {
    width: 20%;
    float: left;
  }

  .attribute {
    width: 80%;
    float: left;
  }

  .cusNumber {
    padding: 0 15px;
    color: #4cbdff;
    font-size: 17px;
    font-weight: bold;
  }
</style>
