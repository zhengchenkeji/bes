<template>
  <div class="app-container">
    <el-row>
      <!--分项数据-->
      <el-col style="padding-right: 10px" :span="4" :xs="24">
        <div class="head-container" style="padding-top: 10px">
          <el-tree
            class="flow-tree"
            :data="subitemData"
            :props="defaultProps"
            :expand-on-click-node="false"
            :default-expanded-keys="defaultExpandedKeys"
            highlight-current
            ref="tree"
            node-key="id"
            @node-click="handleNodeClick"
          />
        </div>
      </el-col>
      <el-col :span="20" :xs="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
                 label-width="68px">
          <el-form-item label="分项编号" prop="subitemCode">
            <el-input
              v-model="queryParams.subitemCode"
              placeholder="请输入分项编号"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="分项名称" prop="subitemName">
            <el-input
              v-model="queryParams.subitemName"
              placeholder="请输入分项名称"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="所属园区" prop="branchName">
            <el-select
              v-model="queryParams.parkCode"
              style="width: 100%"
              @change="byParkGetData"
            >
              <el-option
                v-for="item in parkList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="所属能源" prop="branchName">
            <el-select
              v-model="queryParams.energyCode"
              style="width: 100%"
              @change="byEnergyGetData"
            >
              <el-option
                v-for="item in energyList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="所属建筑" prop="buildingId">
            <el-select
              v-model="queryParams.buildingId"
              style="width: 100%"
              @change="byBuildingData"
            >
              <el-option
                v-for="item in buildingList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
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
              v-hasPermi="['subitemConfig:config:add']"
              v-show="energyList.length>0 && parkList.length>0 && buildingList.length>0"
            >新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="addBatch"
              v-hasPermi="['subitemConfig:config:add']"
              v-show="energyList.length>0 && parkList.length>0 && buildingList.length>0"
            >批量新增
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
              v-hasPermi="['subitemConfig:config:remove']"
            >删除
            </el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="configList" height="60vh" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center"/>
          <el-table-column label="分项编号" align="center" prop="subitemCode" show-overflow-tooltip/>
          <el-table-column label="分项名称" align="center" prop="subitemName" show-overflow-tooltip/>
          <el-table-column label="建筑能耗代码" align="center" prop="buildingEnergyCode" show-overflow-tooltip/>
          <el-table-column label="所属能源" align="center" prop="energyCode" show-overflow-tooltip>
            <template slot-scope="scope">
              {{ getSelectData(scope.row.energyCode, energyList) }}
            </template>
          </el-table-column>
          <el-table-column label="所属园区" align="center" prop="parkCode" show-overflow-tooltip>
            <template slot-scope="scope">
              {{ getSelectData(scope.row.parkCode, parkList) }}
            </template>
          </el-table-column>
          <el-table-column label="所属建筑" align="center" prop="buildingId" show-overflow-tooltip>
            <template slot-scope="scope">
              {{ getSelectData(scope.row.buildingId, buildingList) }}
            </template>
          </el-table-column>
          <el-table-column label="级联配置" align="center" prop="cascaded" show-overflow-tooltip>
            <template slot-scope="scope">
              <dict-tag :options="dict.type.sys_cascaded" :value="scope.row.cascaded"/>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-setting"
                @click="handleBranch(scope.row)"
                v-hasPermi="['branchConfig:config:list']"
              >包含支路
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['subitemConfig:config:edit']"
              >修改
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['subitemConfig:config:remove']"
              >删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />

        <!-- 添加或修改分项拓扑配置对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="120px">
            <el-form-item label="分项编号" v-show="addOrUpdate" prop="subitemCode">
              <el-input v-model="form.subitemCode" disabled/>
            </el-form-item>
            <el-form-item label="分项名称" prop="subitemName">
              <el-input v-model="form.subitemName" maxlength="25" show-word-limit placeholder="请输入分项名称"/>
            </el-form-item>
            <el-form-item label="建筑能耗代码" prop="buildingEnergyCode">
              <el-input v-model="form.buildingEnergyCode" maxlength="10" show-word-limit placeholder="请输入建筑能耗代码"/>
            </el-form-item>
            <el-form-item label="级联配置" prop="cascaded">
              <el-select
                v-model="form.cascaded"
                placeholder="请选择"
                style="width: 100%"
              >
                <el-option
                  v-for="dict in dict.type.sys_cascaded"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </el-dialog>

        <!-- 包含支路 -->
        <el-dialog title="包含支路" :visible.sync="branchOpen" width="800px" append-to-body>
          <ShuttleBox
            v-model="branchValue"
            :dataList="list"
            ref="child"
            keyName="branchId"
            @change="change"
          ></ShuttleBox>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitBranch">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </el-dialog>
      </el-col>
    </el-row>


  </div>
</template>

<script>
import {
  listConfig,
  getConfig, delConfig, addConfig, updateConfig,
  treeSelect,
  nodeListBranchById,
  saveNodeListBranch,
  addConfigBatch
} from "@/api/basicData/energyInfo/subitemConfig/config";
import {
  listType,
  listPark,
  buildingList,
  nodeListBranch,
} from "@/api/basicData/energyInfo/branchConfig/config";
import ShuttleBox from "../subitemConfig/component/shuttleBox";

export default {
  name: "Config",
  components: {ShuttleBox},
  dicts: ['sys_cascaded'],
  data() {
    return {
      //穿梭框数据
      list: [],
      //默认展开的数组
      defaultExpandedKeys: [],
      // 级联默认值
      cascaded: "",
      //右侧选中数据
      branchValue: [],
      //右侧列表数据
      rightDate: [],
      // 是否显示设备弹出层
      branchOpen: false,
      //当前节点数据
      nodeData: {},

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
      // 分项拓扑配置表格数据
      configList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,

      //能源列表
      energyList: [],
      //园区列表
      parkList: [],
      //建筑列表
      buildingList: [],
      //false为新增  true为修改
      addOrUpdate: false,
      //下拉树数据
      subitemData: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // 查询参数
      queryParams: {
        code: 0,
        pageNum: 1,
        pageSize: 10,
        subitemCode: null,
        subitemName: null,
        energyCode: null,
        parkCode: null,
        parentId: null,
        buildingId: null,
        cascaded: null,
        subitemId: 0,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        subitemName: [
          {required: true, message: "分项名称不能为空", trigger: "blur", }
        ],
        buildingEnergyCode: [
          {required: true, message: "建筑能耗代码不能为空且由数字和字母组成", trigger: "blur", pattern: /^[A-Za-z0-9]+$/}
        ],
        energyCode: [
          {required: true, message: "所属能源不能为空", trigger: "blur"}
        ],
        parkCode: [
          {required: true, message: "所属园区不能为空", trigger: "blur"}
        ],
        buildingId: [
          {required: true, message: "所属建筑不能为空", trigger: "blur"}
        ],
        cascaded: [
          {required: true, message: "级联配置不能为空", trigger: "blur"}
        ],
      }
    };
  },

  created() {
    this.getParkAndEnergy();
    this.initParameter();
  },
  methods: {
    /******园区下拉框*******/
    byParkGetData() {
      this.queryParams.code = 0
      this.queryParams.subitemId = 0
      this.getListTypeAndBuilding();
    },
    /*******能源下拉框******/
    byEnergyGetData() {
      this.queryParams.code = 0
      this.queryParams.subitemId = 0
      this.getBranchData()
      this.getListData()
    },
    /*******建筑下拉框******/
    byBuildingData() {
      this.queryParams.code = 0
      this.queryParams.subitemId = 0
      this.getListData()
    },

    /** 加载默认参数 */
    initParameter() {
      this.getConfigKey("sys_cascaded").then(response => {
        this.cascaded = response.msg
      })
    },
    /** 获取支路数据 */
    getBranchData() {
      nodeListBranch({
        energyCode: this.queryParams.energyCode,
        parkCode: this.queryParams.parkCode,
      }).then(response => {
        this.list = response.data
      });
    },
    /** 穿梭框  数据改变时 */
    change(val) {
      this.rightDate = val
    },
    /** 包含支路查看操作 */
    handleBranch(row) {
      this.nodeData = row;
      //获取节点支路数据
      nodeListBranchById({
        subitemId: row.subitemId,
      }).then(response => {
        this.branchValue = response.data
        this.branchOpen = true
        //加载数据
        this.$nextTick(() => {
          this.$refs.child.initRight();
          this.$refs.child.initLeft();
          this.$refs.child.searchLeft = "";
          this.$refs.child.searchRight = "";
        })
      });
    },

    /** 包含支路确定操作 */
    submitBranch() {
      var list=[]
      this.rightDate.forEach(item=>{
        list.push(item.branchId)
      })
      const data = {
        branchList: list,
        subitemId: this.nodeData.subitemId,
        fatherId: this.nodeData.parentId,
        parkCode: this.queryParams.parkCode,
        energyCode: this.queryParams.energyCode,
      }
      saveNodeListBranch(data).then(data => {
        this.$modal.msgSuccess("保存成功！");
        this.branchOpen = false;
      })
    },

    /** 节点单击事件 */
    handleNodeClick(data) {
      this.queryParams.subitemId = data.id;
      this.handleQueryTree();
    },
    /** 树节点查询 */
    handleQueryTree() {
      this.queryParams.pageNum = 1;
      this.queryParams.code = 0
      this.getList();
    },
    /** 查询分项计量拓扑配置下拉树结构 */
    getTreeselect() {
      treeSelect(this.queryParams).then(response => {
        this.subitemData = response.data;
        if (this.subitemData && this.subitemData.length > 0 &&
          this.subitemData[0].children && this.subitemData[0].children.length > 0 &&
          this.subitemData[0].children[0].id) {
          this.defaultExpandedKeys = []
          this.defaultExpandedKeys.push(this.subitemData[0].children[0].id);
        }
      });
    },

    /** 获取园区列表及能源列表及建筑列表 */
    async getParkAndEnergy() {
      listPark().then(response => {
        response.forEach(item => {
          this.parkList.push({value: item.code, label: item.name})
        });
        if (this.parkList.length > 0) {
          this.queryParams.parkCode = this.parkList[0].value
          this.getListTypeAndBuilding();
        } else {
          this.queryParams.parkCode = null
          this.queryParams.energyCode = null
          this.queryParams.buildingId = null
          this.loading=false;
        }
      });
    },
    /*******获取能源列表及建筑******/
    async getListTypeAndBuilding() {
      this.energyList = []
      await  listType({
        parkCode: this.queryParams.parkCode
      }).then(response => {
        response.data.forEach(item => {
          this.energyList.push({value: item.code, label: item.name})
        });
        if (this.energyList.length > 0) {
          this.queryParams.energyCode = this.energyList[0].value
          this.getBranchData()
        } else {
          this.queryParams.energyCode = null
        }
      });
      this.buildingList = []
      await buildingList({parkCode: this.queryParams.parkCode}).then(response => {
        response.data.forEach(item => {
          this.buildingList.push({value: item.id, label: item.buildName})
        });
        if (this.buildingList.length > 0) {
          this.queryParams.buildingId = this.buildingList[0].value
        } else {
          this.queryParams.buildingId = null
        }
      })
      this.getListData()
    },
    /******判断是否查询数据******/
    getListData(){
      if ( this.queryParams.buildingId==null || this.queryParams.energyCode == null ){
        this.configList = []
        this.subitemData = []
        this.loading=false
      }else {
        this.getTreeselect()
        this.getList()
      }
    },

    /** 查询分项拓扑配置列表 */
    getList() {
      this.loading = true;
      listConfig(this.queryParams).then(response => {
        this.configList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.branchOpen = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        subitemId: null,
        subitemCode: null,
        subitemName: null,
        energyCode: this.queryParams.energyCode,
        parkCode: this.queryParams.parkCode,
        parentId: this.queryParams.subitemId,
        buildingId: this.queryParams.buildingId,
        cascaded: this.cascaded,
        buildingEnergyCode: null,
        createTime: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.queryParams.code = 1
      if (this.queryParams.parkCode && this.queryParams.energyCode && this.queryParams.buildingId){
        this.getList();
      }
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.subitemId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      if (this.queryParams.subitemId == 0 && this.subitemData.length > 0) {
        this.$message.warning("请先选中节点再进行新增操作！")
        return
      }
      this.addOrUpdate = false
      this.reset();
      this.open = true;
      this.title = "添加分项拓扑配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.addOrUpdate = true
      this.reset();
      const subitemId = row.subitemId || this.ids
      getConfig(subitemId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改分项拓扑配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.subitemId != null) {
            updateConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.$refs.tree.getNode(response.data.subitemId).data.label = response.data.subitemName
              this.getList();
            });
          } else {
            addConfig(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.$refs.tree.append(
                {id: response.data.subitemId, label: response.data.subitemName},
                this.queryParams.subitemId
              )
              this.queryParams.subitemId = response.data.subitemId;
              this.$nextTick(() => {
                this.$refs.tree.setCurrentKey(response.data.subitemId)
                this.$refs.tree.getNode(response.data.subitemId).expand();
              })
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const subitemIds = row.subitemId || this.ids;
      this.$modal.confirm('是否确认删除？').then(function () {
        return delConfig(subitemIds);
      }).then(() => {
        if (!row.subitemId) {
          if (this.subitemData.length >= 1) {
            this.$refs.tree.setCurrentKey(this.subitemData[0].id)
          }
          this.queryParams.subitemId = 0
          this.$nextTick(() => {
            subitemIds.forEach(item => {
              this.$refs.tree.remove(item)
            })
          })
        } else {
          this.$refs.tree.setCurrentKey(this.$refs.tree.getNode(row.subitemId).parent)
          this.queryParams.subitemId = this.$refs.tree.getNode(row.subitemId).parent.data.id ? this.$refs.tree.getNode(row.subitemId).parent.data.id : 0
          this.$nextTick(() => {
            this.$refs.tree.remove(row.subitemId)
          })
        }
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /********批量新增***********/
    addBatch() {
      var data = this.queryParams
      this.$modal.confirm('是否确认进行批量新增？新增时会先删除先前模板数据！').then(() => {
        addConfigBatch(data).then(response => {
          this.$modal.msgSuccess("批量新增成功！");
          this.getList();
          this.getTreeselect();
          this.queryParams.subitemId = 0
        });
      })
    },

    /** 根据分类id显示分类名称 */
    getSelectData(value, list) {
      let label = '';
      if (value) {
        if (list) {
          list.forEach((item) => {
            if (item.value == value) {
              label = item.label;
            }
          });
        }
      }
      return label;
    },
  }
};
</script>
<style scoped lang="scss">
::v-deep .el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
  background-color: rgb(64, 158, 255) !important;
  color: white;
}

.flow-tree {
  overflow: auto;
}

::v-deep .el-tree-node > .el-tree-node__children {
  overflow: visible;
}
</style>
