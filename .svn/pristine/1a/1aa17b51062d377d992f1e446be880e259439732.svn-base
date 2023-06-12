<template>
  <div class="app-container">

    <el-row style="height: 84vh">
      <el-col :span="5" style="border-right: 3px #f6f6f6 solid;height: 100%;padding: 10px;overflow-y: auto">
        <el-form ref="form" label-width="80px">
          <el-form-item label="选择园区:" prop="jobName">
            <el-select v-model="queryParams.parkCode" placeholder="请选择"
                       @change="byParkGetData"
            >
              <el-option
                v-for="item in parkList"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="能源类型:">
            <el-select v-model="queryParams.energyCode" placeholder="请选择"
                       @change="byEnergyGetData"
            >
              <el-option
                v-for="item in energyList"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-divider></el-divider>
          <el-input placeholder="请输入搜索内容"
                    v-model="filterText"
                    style="margin-bottom: 10px "
          >
          </el-input>
          <div class="head-container" style="padding-top: 10px">
            <el-tree
              class="flow-tree"
              :default-expanded-keys="defaultExpandedKeys"
              :data="branchConfigData"
              node-key="id"
              :highlight-current="true"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              @node-click="handleNodeClick"
              ref="tree">
            </el-tree>
          </div>
        </el-form>
      </el-col>
      <el-col :span="19" style="height: 100%;padding: 10px;">
        <el-form ref="queryForm" size="small" :inline="true" label-width="80px">
          <el-form-item label="电表名称">
            <el-select v-model="queryParams.meter"
                       @change="meterChange"
                       placeholder="支路下电表为空！"
            >
              <!--              :key="item.deviceTreeId"-->
              <el-option
                v-for="item in meterList"
                :key="item.id"
                :label="item.alias"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="日期">
            <el-date-picker
              v-model="pickerTime"
              type="daterange"
              value-format="yyyy-MM-dd"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :clearable="false"
              :picker-options="pickerOptions"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item label="采集参数">
            <el-select multiple v-model="queryParams.paramsId"
                       collapse-tags
                       placeholder="采集参数为空！"
            >
              <el-checkbox
                v-model="check"
                @change="selectAll"
                class="check"
              >
                全选
              </el-checkbox>
              <el-option
                v-for="paramInfo in parametersList"
                :key="paramInfo.code"
                :label="paramInfo.name"
                :value="paramInfo.code"
                size="small"
              />
            </el-select>
          </el-form-item>
          <div>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-paperclip" @click="handleExport">导出</el-button>
              <el-button type="primary" icon="el-icon-paperclip" @click="handleExportBatch">批量导出</el-button>
            </el-form-item>
          </div>

        </el-form>

        <div style="height: 90%;">
          <el-table :data="dataList"
                    style="width: 100%"
                    v-loading="loading"
                    height="100%"
                    :header-cell-style="{background:'#01ada8',color:'#FFFFFF'}"
          >
            <el-table-column v-for="item in columnList"
                             :label="item.name"
                             :prop="item.code"
                             show-overflow-tooltip/>
          </el-table>
        </div>

      </el-col>
    </el-row>


  </div>
</template>

<script>
  import {
    listType,
    listPark,
    treeSelect,
    nodeListMeter,
  } from "@/api/basicData/energyInfo/branchConfig/config";
  import {
    getMeterParams,
    getDataByParamsCode
  } from "@/api/electricPowerTranscription/param/param";

  import {parseTimeFormat} from "@/utils/date";

  export default {
    name: "index",
    data() {
      return {
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > Date.now();
          },
        },
        //遮罩层
        loading: false,
        //默认展开的数组
        defaultExpandedKeys: [],
        //选中的数据
        checkedKey: null,
        value: '',
        filterText: '',
        //电表集合
        meterList: [],
        //电表采集参数
        parametersList: [],
        //列名称
        columnList: [],
        //排行列表
        dataList: [],
        //能源列表
        energyList: [],
        //园区列表
        parkList: [],
        //支路数据
        branchConfigData: [],
        //日期
        pickerTime: [parseTimeFormat(new Date(), '{y}-{m}-{d}'), parseTimeFormat(new Date(), '{y}-{m}-{d}')],
        // 查询参数
        queryParams: {
          meter: null,
          paramsId: [],
          pageNum: 1,
          pageSize: 10,
          energyCode: null,
          parkCode: "",
          startTime: null,
          endTime: null,
          code: 0,
          branchId: 0,
          deviceType: null,
        },
      };
    },
    computed: {
      check: {
        get() {
          if (this.queryParams.paramsId.length === this.parametersList.length) {
            return true
          }
          return false
        },
        set() {
        }
      },
    },

    created() {
      this.getParkAndEnergy();
    },
    watch: {
      //树搜索
      filterText(val) {
        this.$refs.tree.filter(val);
      },
    },

    methods: {
      /******全选*******/
      selectAll(checked) {
        if (checked) {
          this.queryParams.paramsId = this.parametersList.map(d => d.code)
        } else {
          this.queryParams.paramsId = []
        }
      },
      /******树节点选中事件*******/
      handleNodeClick(data) {
        this.checkedKey = data.id
        this.handleMeter();
      },
      /*********能耗数据查询************/
      getSelectDataBranch() {
        this.loading = true
        this.queryParams.startTime = this.pickerTime[0]
        this.queryParams.endTime = this.pickerTime[1]
        getDataByParamsCode(this.queryParams).then((res) => {
          this.dataList = res.data
          this.loading = false
        })
      },

      /******搜索按钮*******/
      handleQuery() {
        //如果电能参数为空
        if (this.queryParams.paramsId.length > 0) {
          this.columnList = []
          this.columnList.push({name: "采集时间", code: "time"})
          this.parametersList.forEach(item => {
            if (this.queryParams.paramsId.includes(item.code)) {
              this.columnList.push(item)
            }
          })
          this.getSelectDataBranch();
        } else {
          this.columnList = [];
          this.dataList = [];
        }
      },

      /******园区下拉框*******/
      byParkGetData() {
        this.filterText = ''
        this.queryParams.paramsId = []
        this.getListType()
      },
      /*******能源下拉框******/
      byEnergyGetData() {
        this.filterText = ''
        this.queryParams.paramsId = []
        this.getTreeselect()
      },
      /** 查询支路拓扑配置下拉树结构 */
      getTreeselect() {
        treeSelect(this.queryParams).then(response => {
          this.branchConfigData = response.data;
          if (this.branchConfigData && this.branchConfigData.length > 0) {
            this.defaultExpandedKeys = []
            this.defaultExpandedKeys.push(this.branchConfigData[0].id);
            this.$nextTick(() => {
              this.$refs.tree.setCurrentKey(this.branchConfigData[0].id);
              this.checkedKey = this.$refs.tree.getCurrentKey();
              this.handleMeter();
            })
          } else {
            this.clearData();
          }
        });
      },

      /** 清空数据 */
      clearData() {
        //如果支路为空 则清空所有数据
        this.checkedKey = null;
        this.dataList = [];
        this.queryParams.paramsId = [];
        this.parametersList = [];
        this.meterList = [];
        this.queryParams.meter = []
        this.columnList = [];
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
          } else {
            this.queryParams.parkCode = null
            this.queryParams.energyCode = null
            this.branchConfigData = []
            this.clearData();
            this.loading = false
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
          } else {
            this.queryParams.energyCode = null
            this.branchConfigData = []
            this.clearData();
            this.loading = false
          }
        });
      },
      /*****支路搜索*******/
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },
      /************切换电表*****************/
      meterChange(value) {
        this.meterList.forEach(item => {
          if (item.id == value) {
            this.queryParams.deviceType = item.deviceType
          }
        })
        this.getMeterParams();
      },
      /** 支路下电表查看操作 */
      handleMeter() {
        //获取节点下电表数据
        nodeListMeter({
          branchId: this.checkedKey,
          energyCode: this.queryParams.energyCode,
        }).then(response => {
          this.meterList = response.data
          if (this.meterList && this.meterList.length > 0) {
            this.queryParams.meter = this.meterList[0].id;
            this.queryParams.deviceType = this.meterList[0].deviceType;
            this.getMeterParams();
          } else {
            this.queryParams.meter = null;
            this.columnList = [];
            this.dataList = [];
            this.queryParams.paramsId = [];
            this.parametersList = [];
          }
        });
      },

      /************获取电表采集参数*************/
      getMeterParams() {
        getMeterParams({
          meterId: this.queryParams.meter
        }).then((res) => {
          this.parametersList = res.data
          if (this.parametersList && this.parametersList.length > 0) {
            this.columnList = []
            this.columnList.push({name: "采集时间", code: "time"})
            this.parametersList.forEach(item => {
              this.columnList.push(item)
            })
            this.queryParams.paramsId = this.parametersList.map(item => item.code)
            this.getSelectDataBranch();
          } else {
            this.columnList = [];
            this.dataList = [];
            this.queryParams.paramsId = [];
          }
          this.loading = false
        })
      },
      /** 导出按钮操作 */
      handleExport() {
        if (!this.meterList || this.meterList.length <= 0) {
          this.$message.warning("电表为空！无法导出！")
          return;
        }
        if (!this.columnList || this.columnList.length <= 0) {
          this.$message.warning("列数为空！无法导出！")
          return;
        }
        if (!this.columnList || this.columnList.length <= 1) {
          this.$message.warning("列数为空！无法导出！")
          return;
        }

        this.$modal.confirm('是否确认导出电参数报表数据？').then(() => {
          //以当前表格查询列为基准
          var paramId = []
          this.columnList.forEach(item => {
            paramId.push(item.code)
          })
          this.download('/electricPowerTranscription/param/export', {
            paramId: paramId,
            startTime: this.queryParams.startTime,
            endTime: this.queryParams.endTime,
            meter: this.queryParams.meter,
            deviceType: this.queryParams.deviceType
          }, `电参数数据表.xlsx`)
        });
      },
      /** 批量导出按钮操作 */
      handleExportBatch() {
        if (!this.meterList || this.meterList.length <= 0) {
          this.$message.warning("电表为空！无法导出！")
          return
        }
        var ids = []
        this.meterList.forEach(item => {
          ids.push(item.id)
        })
        this.$modal.confirm('是否确认批量导出电参数报表数据？').then(() => {
          this.download('/electricPowerTranscription/param/exportBatch', {
            meterIds: ids,
            startTime: this.queryParams.startTime,
            endTime: this.queryParams.endTime,
          }, `支路下所有电表数据表.xlsx`)
        });
      }
    },


  };
</script>
<style lang="scss" scoped>
  /***滚动条样式***/
  .flow-tree {
    overflow: auto;
  }

  ::v-deep .el-tree-node > .el-tree-node__children {
    overflow: visible;
  }

  .check {
    margin-left: 20px;
    font-size: 14px;
  }
</style>
