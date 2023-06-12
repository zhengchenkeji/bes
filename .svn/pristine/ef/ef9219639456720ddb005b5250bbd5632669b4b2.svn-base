<template>
  <div class="app-container">

    <el-row>
      <!--支路数据-->
      <el-col style="padding-right: 10px" :span="4" :xs="24">


        <div class="head-container" style="padding-top: 10px">
          <el-tree
            class="flow-tree"
            :data="branchConfigData"
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
          <el-form-item label="支路编号" prop="branchCode">
            <el-input
              v-model="queryParams.branchCode"
              placeholder="请输入支路编号"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="支路名称" prop="branchName">
            <el-input
              v-model="queryParams.branchName"
              placeholder="请输入支路名称"
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
              @change="byEnergyGetData"
              style="width: 100%"
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
              v-hasPermi="['branchConfig:config:add']"
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
              v-hasPermi="['branchConfig:config:remove']"
            >删除
            </el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="configList" height="60vh" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center"/>
          <el-table-column label="支路编号" align="center" prop="branchCode" show-overflow-tooltip/>
          <el-table-column label="支路名称" align="center" prop="branchName" show-overflow-tooltip/>
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
          <el-table-column label="额定功率" align="center" prop="ratedPower" show-overflow-tooltip/>
          <el-table-column label="安装位置" align="center" prop="position" show-overflow-tooltip/>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-setting"
                @click="handleMeter(scope.row)"
                v-hasPermi="['branchConfig:config:meter']"
              >包含设备
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['branchConfig:config:edit']"
              >修改
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['branchConfig:config:remove']"
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

        <!-- 添加或修改支路拓扑配置对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="120px">
            <el-form-item label="支路名称" prop="branchName">
              <el-input v-model="form.branchName" maxlength="25" show-word-limit placeholder="请输入支路名称"/>
            </el-form-item>
            <el-form-item label="支路编号" v-show="addOrUpdate" prop="branchCode">
              <el-input v-model="form.branchCode" disabled/>
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
            <el-form-item label="额定功率" prop="ratedPower">
              <el-input v-model="form.ratedPower" maxlength="12" show-word-limit placeholder="请输入额定功率"/>
            </el-form-item>
            <el-form-item label="安装位置" prop="position">
              <el-input v-model="form.position" maxlength="25" show-word-limit placeholder="请输入安装位置"/>
            </el-form-item>
            <el-form-item label="采集参数配置" prop="position">
              <el-select v-model="form.paramsIdArr" placeholder="请选择" style="width: 100%" multiple collapse-tags
                         filterable>
                <el-option
                  v-for="item in paramsIdList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </el-dialog>

        <!-- 包含设备 -->
        <el-dialog title="包含设备" :visible.sync="meterOpen" width="1150px" append-to-body>
          <ShuttleBox
            v-model="metervalue"
            :dataList="list"
            :otherList="otherList"
            keyName="meterId"
            ref="child"
            @change="change"
          ></ShuttleBox>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitMeter">确 定</el-button>
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
    listType,
    listPark,
    getConfig,
    delConfig,
    getMessage,
    addConfig,
    updateConfig,
    treeSelect,
    listMeter,
    nodeListMeter,
    saveNodeListMeter,
    getEquipmentListByBranch,
    buildingList,
  } from "@/api/basicData/energyInfo/branchConfig/config";
  import ShuttleBox from "./component/shuttleBox";
  import {listParams} from "@/api/systemSetting/parameterConfiguration/parameterConfiguration";

  export default {
    name: "Config",
    components: {ShuttleBox},
    dicts: ['sys_cascaded'],
    data() {
      return {
        //第三方电表数据
        otherList: [],
        //穿梭框数据
        list: [],
        //默认展开的数组
        defaultExpandedKeys: [],
        // 级联默认值
        cascaded: "",
        //右侧选中数据
        metervalue: [],
        //右侧列表数据
        rightDate: [],
        //当前节点数据
        nodeBranch: {},

        //下拉树数据
        branchConfigData: [],
        defaultProps: {
          children: 'children',
          label: 'label'
        },
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        //false为新增  true为修改
        addOrUpdate: false,
        //能源列表
        energyList: [],
        //园区列表
        parkList: [],
        //建筑列表
        buildingList: [],
        //部门名称
        // branchName: null,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 支路拓扑配置表格数据
        configList: [],
        //采集参数配置List
        paramsIdList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 是否显示设备弹出层
        meterOpen: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          energyCode: null,
          parkCode: "",
          branchCode: null,
          branchId: 0,
          branchName: null,
          buildingId: null,
          code: 0,
        },

        // 表单参数
        form: {
          paramsIdArr: [],
          paramsId: null,
        },
        // 表单校验
        rules: {
          branchName: [
            {required: true, message: "支路名称不能为空", trigger: "blur"}
          ],
          buildingId: [
            {required: true, message: "所属建筑不能为空", trigger: "blur"}
          ],
          cascaded: [
            {required: true, message: "级联配置", trigger: "blur"}
          ],
          ratedPower: [
            {message: "只允许输入数字", trigger: "blur", pattern: /^([1-9][\d]*|0)(\.[\d]+)?$/,}
          ]

        }
      };
    },
    created() {
      this.getParkAndEnergy();
      this.initParameter();
      this.getParamsIdList();
    },


    methods: {
      /******获取采集参数配置下拉框*******/
      getParamsIdList() {
        listParams().then(response => {
          this.paramsIdList = response.rows
        });
      },
      /******园区下拉框*******/
      byParkGetData() {
        this.queryParams.code = 0;
        this.queryParams.branchId = 0
        this.getListType();
        this.getBuildingList();
      },
      /*******能源下拉框******/
      byEnergyGetData() {
        this.queryParams.code = 0;
        this.queryParams.branchId = 0
        this.getTreeselect()
        this.getList()
        this.getMeterData();
      },
      /** 加载默认参数 */
      initParameter() {
        this.getConfigKey("sys_cascaded").then(reponse => {
          this.cascaded = response.msg
        })
      },
      /** 获取电表数据 */
      getMeterData() {
        // 获取bes 电表
        listMeter({
          energyCode: this.queryParams.energyCode,
          parkCode: this.queryParams.parkCode,
        }).then(response => {
          this.list = response.data
        });
        // 获取第三方电表
        getEquipmentListByBranch({
          energyCode: this.queryParams.energyCode,
          parkCode: this.queryParams.parkCode,
        }).then(response => {
          this.otherList = response.data

        });

      },
      /** 穿梭框  数据改变时 */
      change(val) {
        this.rightDate = val
      },
      /** 获取园区列表及能源列表 */
      getParkAndEnergy() {
        listPark().then(response => {
          response.forEach(item => {
            this.parkList.push({value: item.code, label: item.name})
          });
          if (this.parkList.length > 0) {
            this.queryParams.parkCode = this.parkList[0].value
            this.getBuildingList();
            this.getListType();
          } else {
            this.queryParams.parkCode = null
            this.queryParams.energyCode = null
            this.loading = false;
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
            this.getTreeselect()
            this.getList()
            this.getMeterData();
          } else {
            this.queryParams.energyCode = null
            this.configList = []
            this.branchConfigData = []
            this.loading = false;
          }
        });
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
      // /** 筛选节点 */
      // filterNode(value, data) {
      //   if (!value) return true;
      //   return data.label.indexOf(value) !== -1;
      // },
      /** 节点单击事件 */
      handleNodeClick(data) {
        this.queryParams.branchId = data.id;
        this.handleQueryTree();
      },
      /** 树节点查询 */
      handleQueryTree() {
        this.queryParams.code = 0;
        this.queryParams.pageNum = 1;
        this.getList();
      },

      /** 查询支路拓扑配置下拉树结构 */
      getTreeselect() {
        treeSelect(this.queryParams).then(response => {
          this.branchConfigData = response.data;
          if (this.branchConfigData && this.branchConfigData.length > 0 &&
            this.branchConfigData[0].children && this.branchConfigData[0].children.length > 0
            && this.branchConfigData[0].children[0].id) {
            this.defaultExpandedKeys = []
            this.defaultExpandedKeys.push(this.branchConfigData[0].children[0].id);
          }
        });
      },

      /** 查询支路拓扑配置列表 */
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
        this.meterOpen = false;
        this.reset();
      },
      /** 表单重置 */
      reset() {
        this.form = {
          parentId: this.queryParams.branchId,
          branchId: null,
          branchCode: null,
          branchName: null,
          energyCode: this.queryParams.energyCode,
          parkCode: this.queryParams.parkCode,
          buildingId: null,
          cascaded: this.cascaded,
          ratedPower: null,
          position: null,
          paramsIdArr: [],
          paramsId: null,
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.queryParams.code = 1
        if (this.queryParams.parkCode && this.queryParams.energyCode) {
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
        this.ids = selection.map(item => item.branchId)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        if (this.queryParams.branchId == 0 && this.branchConfigData.length > 0) {
          this.$message.warning("请先选中节点再进行新增操作！")
          return
        }
        this.addOrUpdate = false
        this.reset();
        this.open = true;
        this.title = "添加支路拓扑配置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.addOrUpdate = true
        this.reset();
        const branchId = row.branchId || this.ids
        getConfig(branchId).then(response => {
          let paramsIdArr = []
          if (response.data.paramsId != null && response.data.paramsId != undefined && response.data.paramsId.length > 0) {
            paramsIdArr = response.data.paramsId.split(',')
            if (paramsIdArr.length > 0) {
              paramsIdArr = paramsIdArr.map(item => {
                return +item;
              });
            }
          }
          response.data.paramsIdArr = paramsIdArr

          this.form = response.data;
          this.open = true;
          this.title = "修改支路拓扑配置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.paramsIdArr.length > 0) {
              this.form.paramsId = this.form.paramsIdArr.join(",")
            } else {
              this.form.paramsId = ''
            }
            if (this.form.branchId != null) {
              updateConfig(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.$refs.tree.getNode(response.data.branchId).data.label = response.data.branchName
                this.getList();
              });
            } else {
              addConfig(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.$refs.tree.append(
                  {id: response.data.branchId, label: response.data.branchName},
                  this.queryParams.branchId
                )
                this.queryParams.branchId = response.data.branchId;
                this.$nextTick(() => {
                  this.$refs.tree.setCurrentKey(response.data.branchId)
                  this.$refs.tree.getNode(response.data.branchId).expand();
                })
                this.getList();
              });
            }
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const branchIds = row.branchId || this.ids;
        this.$modal.confirm('是否确认删除？').then(function () {
          return getMessage(branchIds);
        }).then((res) => {
          if (res.data) {
            delConfig(branchIds).then(() => {
              //批量删除
              if (!row.branchId) {
                if (this.branchConfigData.length >= 1) {
                  this.$refs.tree.setCurrentKey(this.branchConfigData[0].id)
                }
                this.queryParams.branchId = 0
                this.$nextTick(() => {
                  branchIds.forEach(item => {
                    this.$refs.tree.remove(item)
                  })
                })
              }
              //单个删除
              else {
                this.$refs.tree.setCurrentKey(this.$refs.tree.getNode(row.branchId).parent)
                this.queryParams.branchId = this.$refs.tree.getNode(row.branchId).parent.data.id ? this.$refs.tree.getNode(row.branchId).parent.data.id : 0
                this.$nextTick(() => {
                  this.$refs.tree.remove(row.branchId)
                })
              }
              this.getList();
              this.$modal.msgSuccess("删除成功");
            })
          } else {
            this.$modal.confirm(res.msg).then(function () {
              return delConfig(branchIds);
            }).then(() => {
              //批量删除
              if (!row.branchId) {
                if (this.branchConfigData.length >= 1) {
                  this.$refs.tree.setCurrentKey(this.branchConfigData[0].id)
                }
                this.queryParams.branchId = 0
                this.$nextTick(() => {
                  branchIds.forEach(item => {
                    this.$refs.tree.remove(item)
                  })
                })
              }
              //单个删除
              else {
                this.$refs.tree.setCurrentKey(this.$refs.tree.getNode(row.branchId).parent)
                this.queryParams.branchId = this.$refs.tree.getNode(row.branchId).parent.data.id ? this.$refs.tree.getNode(row.branchId).parent.data.id : 0
                this.$nextTick(() => {
                  this.$refs.tree.remove(row.branchId)
                })
              }
              this.getList();
              this.$modal.msgSuccess("删除成功");
            })
          }
        })
      },
      /** 包含电表查看操作 */
      handleMeter(row) {
        this.nodeBranch = row;
        //获取节点下电表数据
        nodeListMeter({
          branchId: row.branchId,
          energyCode: this.queryParams.energyCode,
        }).then(response => {
          this.metervalue = response.data
          this.metervalue.forEach(item => {

            if (item.deviceType == "1" && item.electricParam != null) {
              item.electricParam = item.electricParam.split(",")
            }
          })
          console.log(1111111, this.metervalue)
          this.meterOpen = true
          //加载数据
          this.$nextTick(() => {
            this.$refs.child.initRight();
            this.$refs.child.initLeft();
            this.$refs.child.searchLeft = "";
            this.$refs.child.searchRight = "";
          })
        });
      },
      /** 包含电表确定操作 */
      submitMeter() {
        // 处理多选操作
        this.rightDate.forEach(item => {
          if (item.deviceType == "1") {

            item.electricParam = item.electricParam + "";
          }
        })

        const data = {
          meterList: this.rightDate,
          branchId: this.nodeBranch.branchId,
          fatherId: this.nodeBranch.parentId,
          parkCode: this.queryParams.parkCode,
          energyCode: this.queryParams.energyCode,
        }
        for (let i = 0; i < this.rightDate.length; i++) {
          if (this.rightDate[i].electricParam == null || this.rightDate[i].electricParam == '') {
            this.$modal.msgError(this.rightDate[i].alias + "未选择采集参数,无法添加！");
            return
          }
        }
        saveNodeListMeter(data).then(data => {
          this.$modal.msgSuccess("保存成功！");
          this.meterOpen = false;
        })
      },

      // /** 导出按钮操作 */
      // handleExport() {
      //   this.download('branchConfig/config/export', {
      //     ...this.queryParams
      //   }, `config_${new Date().getTime()}.xlsx`)
      // },
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
  }
  ;
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
