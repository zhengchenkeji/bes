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
          <div style="margin-bottom: 10px ">
            <el-checkbox v-model="selectAll">全选</el-checkbox>
            <el-checkbox v-model="cascade">是否级联</el-checkbox>
          </div>
          <div class="head-container" style="padding-top: 10px">
            <el-tree
              class="flow-tree"
              :default-expanded-keys="defaultExpandedKeys"
              :data="branchConfigData"
              node-key="id"
              :check-strictly="!cascade"
              show-checkbox
              :filter-node-method="filterNode"
              @check="handleNodeClick"
              ref="tree">
            </el-tree>
          </div>
        </el-form>
      </el-col>
      <el-col :span="19" style="height: 100%;padding: 10px;">
        <el-tabs type="border-card">
          <el-tab-pane label="能耗" style="height: 100%">
            <el-form ref="queryForm" size="small" :inline="true" label-width="80px">
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
              <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
              </el-form-item>
            </el-form>

            <div style="height: 90%;">
              <el-table :data="dataList"
                        style="width: 100%"
                        v-loading="loading"
                        height="100%"
                        :header-cell-style="{background:'#01ada8',color:'#FFFFFF'}"
              >
                <el-table-column label="支路节点" align="center" prop="id" show-overflow-tooltip>
                  <template slot-scope="scope">
                    {{ getSelectData(scope.row.id, checkedKeys) }}
                  </template>
                </el-table-column>
                <el-table-column label="支路排名" align="center" type="index" width="80" show-overflow-tooltip/>
                <el-table-column label="能耗值(kW·h)" align="center" prop="value" show-overflow-tooltip/>
              </el-table>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-col>

    </el-row>


  </div>
</template>

<script>
import {
  listType,
  listPark,
  treeSelect,
} from "@/api/basicData/energyInfo/branchConfig/config";
import {
  selectRankingBranch
} from "@/api/energyAnalysis/ranking/ranking";

import {parseTimeFormat} from "@/utils/date";
import {timer} from "@/api/tool/timer";

export default {
  name: "branchIndex",
  data() {
    return {
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      },
      //定时器
      timer: null,
      //遮罩层
      loading: true,
      //默认展开的数组
      defaultExpandedKeys: [],
      //选中的数组
      checkedKeys: [],
      //是否全选
      selectAll: false,
      //是否级联
      cascade: true,
      value: '',
      filterText: '',
      //排行列表
      dataList: [],
      //能源列表
      energyList: [],
      //园区列表
      parkList: [],
      branchConfigData: [],
      //日期
      pickerTime: [new Date().getFullYear() + "-" + (new Date().getMonth() + 1) + "-" + "01", parseTimeFormat(new Date(), '{y}-{m}-{d}')],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        energyCode: null,
        parkCode: "",
        startTime: null,
        endTime: null,
        code: 0,
        branchId: 0,
      },
    };
  },
  created() {
    this.getParkAndEnergy();
    this.timer = setInterval(this.getSelectRankingBranch, timer.timerTime);
  },

  beforeDestroy() {
    if (this.timer) { //如果定时器还在运行 或者直接关闭，不用判断
      clearInterval(this.timer); //关闭
    }
  },

  watch: {
    //树搜索
    filterText(val) {
      this.$refs.tree.filter(val);
    },
    //全选
    selectAll() {
      if (this.selectAll) {
        this.cascade = true
        //全选
        this.$nextTick(() => {
          this.$refs.tree.setCheckedNodes(this.branchConfigData);
          this.checkedKeys = this.$refs.tree.getCheckedNodes();
          this.getSelectRankingBranch();
        })
      } else {
        //取消
        this.$refs.tree.setCheckedKeys([]);
        this.checkedKeys = []
        this.getSelectRankingBranch();
      }
    }
  },

  methods: {
    /******树节点选中事件*******/
    handleNodeClick() {
      this.checkedKeys = this.$refs.tree.getCheckedNodes();
      this.$nextTick(() => {
        this.getSelectRankingBranch();
      })
    },
    /*********能耗排行查询************/
    getSelectRankingBranch() {
      this.loading = true;
      //判断选中的节点是否为空
      if (this.checkedKeys && this.checkedKeys.length > 0) {
        var ids = [];
        this.checkedKeys.forEach(item => {
          ids.push(item.id)
        })
        const data = {
          ids: ids,
          startTime: this.pickerTime[0],
          endTime: this.pickerTime[1],
        }
        selectRankingBranch(data).then((res) => {
          this.dataList = res.data
          this.loading = false;
        })
      } else {
        this.dataList = []
        this.loading = false;
      }
    },

    /******搜索按钮*******/
    handleQuery() {
      this.getSelectRankingBranch();
    },
    /******园区下拉框*******/
    byParkGetData() {
      this.filterText = ''
      this.selectAll = false
      this.$nextTick(() => {
        this.getListType();
      })
    },
    /*******能源下拉框******/
    byEnergyGetData() {
      this.filterText = ''
      this.selectAll = false
      this.$nextTick(() => {
        this.getTreeselect()
      })
    },
    /** 查询支路拓扑配置下拉树结构 */
    getTreeselect() {
      treeSelect(this.queryParams).then(response => {
        this.branchConfigData = response.data;
        if (this.branchConfigData && this.branchConfigData.length > 0) {
          this.defaultExpandedKeys = []
          this.defaultExpandedKeys.push(this.branchConfigData[0].id);
          this.$nextTick(() => {
            this.$refs.tree.setCheckedKeys(this.defaultExpandedKeys);
            this.checkedKeys = this.$refs.tree.getCheckedNodes();
            this.getSelectRankingBranch();
          })
        } else {
          this.checkedKeys = [];
          this.getSelectRankingBranch();
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
        } else {
          this.queryParams.parkCode = null
          this.queryParams.energyCode = null
          this.branchConfigData=[]
          this.loading=false
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
          this.branchConfigData=[]
          this.loading=false
        }
      });
    },
    /*****支路搜索*******/
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },

    /** 根据分类id显示分类名称 */
    getSelectData(value, list) {
      let label = '';
      if (value) {
        if (list) {
          list.forEach((item) => {
            if (item.id == value) {
              label = item.label;
            }
          });
        }
      }
      return label;
    },
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
</style>
