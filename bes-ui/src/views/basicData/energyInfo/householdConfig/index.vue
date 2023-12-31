<template>
  <div class="app-container">
    <el-row>
      <!--分户数据-->
      <el-col style="padding-right: 10px" :span="4" :xs="24">
       
        <div class="head-container" style="padding-top: 10px">
          <el-tree
            class="flow-tree"
            :data="householdData"
            :props="defaultProps"
            highlight-current
            :default-expanded-keys="defaultExpandedKeys"
            :expand-on-click-node="false"
            ref="tree"
            node-key="id"
            @node-click="handleNodeClick"
          />
        </div>
      </el-col>

      <el-col :span="20" :xs="24">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
                 label-width="68px">
          <el-form-item label="分户编号" prop="householdCode">
            <el-input
              v-model="queryParams.householdCode"
              placeholder="请输入分户编号"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="分户名称" prop="householdName">
            <el-input
              v-model="queryParams.householdName"
              placeholder="请输入分户名称"
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
              v-hasPermi="['householdConfig:config:add']"
              v-show="energyList.length>0 && parkList.length>0"
            >新增
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
              v-hasPermi="['householdConfig:config:remove']"
            >删除
            </el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="configList" height="60vh" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center"/>
          <el-table-column label="分户编号" align="center" prop="householdCode" show-overflow-tooltip/>
          <el-table-column label="分户名称" align="center" prop="householdName" show-overflow-tooltip/>
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
          <el-table-column label="人数" align="center" prop="population" show-overflow-tooltip/>
          <el-table-column label="面积" align="center" prop="area" show-overflow-tooltip/>
          <el-table-column label="户型" align="center" prop="houseType" show-overflow-tooltip/>
          <el-table-column label="所属位置" align="center" prop="location" show-overflow-tooltip/>
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
                v-hasPermi="['householdConfig:config:edit']"
              >修改
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['householdConfig:config:remove']"
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
        <!-- 添加或修改分户计量拓扑配置对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">

            <el-form-item label="分户编号" v-show="addOrUpdate" prop="householdCode">
              <el-input v-model="form.householdCode" disabled/>
            </el-form-item>
            <el-form-item label="分户名称" prop="householdName">
              <el-input v-model="form.householdName" maxlength="25" show-word-limit placeholder="请输入分户名称"/>
            </el-form-item>
            <el-form-item label="所属建筑" prop="buildingId">
              <el-select
                v-model="form.buildingId"
                placeholder="请选择所属建筑"
                style="width: 100%"
              >
                <el-option
                  v-for="item in buildingList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
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
            <el-form-item label="人数" prop="population">
              <el-input v-model="form.population" maxlength="9" show-word-limit placeholder="请输入人数"/>
            </el-form-item>
            <el-form-item label="面积" prop="area">
              <el-input v-model="form.area" maxlength="12" show-word-limit placeholder="请输入面积"/>
            </el-form-item>
            <el-form-item label="户型" prop="houseType">
              <el-input v-model="form.houseType" maxlength="12" show-word-limit placeholder="请输入户型"/>
            </el-form-item>
            <el-form-item label="所属位置" prop="location">
              <el-input v-model="form.location" maxlength="50" show-word-limit placeholder="请输入所属位置"/>
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
  getConfig,
  delConfig,
  addConfig,
  updateConfig,
  treeSelect,
  nodeListBranchById,
  saveNodeListBranch
} from "@/api/basicData/energyInfo/householdConfig/config";
import {
  listType,
  listPark,
  buildingList,
  nodeListBranch,
} from "@/api/basicData/energyInfo/branchConfig/config";
import ShuttleBox from "../householdConfig/component/shuttleBox";

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
      //false为新增  true为修改
      addOrUpdate: false,
      // 分户计量拓扑配置表格数据
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
      //下拉树数据
      householdData: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // 查询参数
      queryParams: {
        //查询标识
        code: 0,
        pageNum: 1,
        pageSize: 10,
        householdId: 0,
        energyCode: null,
        householdCode: null,
        householdName: null,
        parkCode: null,
        buildingId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        householdName: [
          {required: true, message: "分户名称不能为空", trigger: "blur"}
        ],
        buildingId: [
          {required: true, message: "所属建筑不能为空", trigger: "blur"}
        ],
        cascaded: [
          {required: true, message: "级联配置不能为空", trigger: "blur"}
        ],
        population: [
          {required: true, message: "人数不能为空且只能输入数字", trigger: "blur", pattern: /^[0-9]*$/}
        ],
        area: [
          {required: true, message: "面积不能为空", trigger: "blur"},
          {message: "只能输入数字,小数点只能有后两位", trigger: "blur", pattern: /^[0-9]+(\.[0-9]{1,2})?$/}
        ],
        houseType: [
          {required: true, message: "户型不能为空", trigger: "change"}
        ],
        location: [
          {required: true, message: "所属位置不能为空", trigger: "blur"}
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
      this.queryParams.householdId = 0
      this.getListType();
      this.getBuildingList();
    },
    /*******能源下拉框******/
    byEnergyGetData() {
      this.queryParams.code = 0
      this.queryParams.householdId = 0
      this.getTreeselect()
      this.getList()
      this.getBranchData()
    },
    /** 获取建筑列表 */
    getBuildingList() {
      this.buildingList = []
      buildingList({parkCode: this.queryParams.parkCode}).then(response => {
        response.data.forEach(item => {
          this.buildingList.push({value: item.id, label: item.buildName})
        });
        if (this.buildingList.length > 0) {
          this.queryParams.buildingId = null
        } else {
          this.queryParams.buildingId = null
        }
      })
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
        householdId: row.householdId,
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
        householdId: this.nodeData.householdId,
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
      this.queryParams.householdId = data.id;
      this.handleQueryTree();
    },
    /** 树节点查询 */
    handleQueryTree() {
      this.queryParams.code = 0
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 查询分户计量拓扑配置下拉树结构 */
    getTreeselect() {
      treeSelect(this.queryParams).then(response => {
        this.householdData = response.data;

        if (this.householdData && this.householdData.length > 0 &&
          this.householdData[0].children && this.householdData[0].children.length > 0 &&
          this.householdData[0].children[0].id) {
          this.defaultExpandedKeys = []
          this.defaultExpandedKeys.push(this.householdData[0].children[0].id);
        }
      });
    },

    /** 获取园区列表及能源列表 */
    getParkAndEnergy() {
      listPark().then(response => {
        response.forEach(item => {
          this.parkList.push({value: item.code, label: item.name})
        });
        if (this.parkList.length > 0) {
          this.queryParams.parkCode = this.parkList[0].value
          this.getListType();
          this.getBuildingList();
        } else {
          this.queryParams.parkCode = null
          this.queryParams.energyCode = null
          this.loading=false;
        }
      });
    },
    /*******获取能源列表******/
    getListType() {
      this.energyList = []
      listType({
        parkCode: this.queryParams.parkCode
      }).then(response => {
        response.data.forEach(item => {
          this.energyList.push({value: item.code, label: item.name})
        });
        if (this.energyList.length > 0) {
          this.queryParams.energyCode = this.energyList[0].value
          this.getBranchData()
          this.getTreeselect()
          this.getList()
        } else {
          this.queryParams.energyCode = null
          this.configList = []
          this.householdData = []
          this.loading=false
        }
      });
    },
    /** 查询分户计量拓扑配置列表 */
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
        householdId: null,
        energyCode: this.queryParams.energyCode,
        householdCode: null,
        householdName: null,
        parentId: this.queryParams.householdId,
        parkCode: this.queryParams.parkCode,
        buildingId: null,
        cascaded: this.cascaded,
        population: null,
        area: null,
        houseType: null,
        location: null,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.queryParams.code = 1
      if (this.queryParams.parkCode && this.queryParams.energyCode){
        this.getList();
      }
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.householdId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      if (this.queryParams.householdId == 0 && this.householdData.length > 0) {
        this.$message.warning("请先选中节点再进行新增操作！")
        return
      }
      this.addOrUpdate = false
      this.reset();
      this.open = true;
      this.title = "添加分户拓扑配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.addOrUpdate = true
      this.reset();
      const id = row.householdId || this.ids
      getConfig(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改分户计量拓扑配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.householdId != null) {
            updateConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.$refs.tree.getNode(response.data.householdId).data.label = response.data.householdName
              this.getList();
            });
          } else {
            addConfig(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.$refs.tree.append(
                {id: response.data.householdId, label: response.data.householdName},
                this.queryParams.householdId
              )
              this.queryParams.householdId = response.data.householdId;
              this.$nextTick(() => {
                this.$refs.tree.setCurrentKey(response.data.householdId)
                this.$refs.tree.getNode(response.data.householdId).expand();
              })
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.householdId || this.ids;
      this.$modal.confirm('是否确认删除？').then(function () {
        return delConfig(ids);
      }).then(() => {
        if (!row.householdId) {
          if (this.householdData.length >= 1) {
            this.$refs.tree.setCurrentKey(this.householdData[0].id)
          }
          this.queryParams.householdId = 0
          this.$nextTick(() => {
            ids.forEach(item => {
              this.$refs.tree.remove(item)
            })
          })
        } else {
          this.$refs.tree.setCurrentKey(this.$refs.tree.getNode(row.householdId).parent)
          this.queryParams.householdId = this.$refs.tree.getNode(row.householdId).parent.data.id ? this.$refs.tree.getNode(row.householdId).parent.data.id : 0
          this.$nextTick(() => {
            this.$refs.tree.remove(row.householdId)
          })
        }
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
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

    // /** 导出按钮操作 */
    // handleExport() {
    //   this.download('householdConfig/config/export', {
    //     ...this.queryParams
    //   }, `config_${new Date().getTime()}.xlsx`)
    // }
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
