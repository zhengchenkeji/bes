<template>
  <div class="app-container">

    <el-row style="height: 86.2vh">
      <el-col :span="5" style="border-right: 3px #f6f6f6 solid;height: 100%;padding: 10px;overflow-y: auto">
        <el-form :model="queryForm" ref="queryForm" size="small" label-width="80px"
                 style="border-bottom: 2px #f6f6f6 solid;">
          <el-form-item label="园区" prop="parkCode">
            <el-select v-model="queryForm.parkCode" @change="changePark">
              <el-option
                v-for="item in parkList"
                :key="item.code"
                :label="item.name"
                :value="item.code">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="能源类型" prop="energyType">
            <el-select v-model="queryForm.energyType" @change="getBranchList">
              <el-option
                v-for="item in energyTypeList"
                :key="item.code"
                :label="item.name"
                :value="item.code">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <el-input
          placeholder="搜索支路"
          @input="filter"
          size="small"
          v-model="filterTree" style="margin-top: 10px">
        </el-input>

        <el-tree :data="branchList" node-key="id"
                 :filter-node-method="filterNode"
                 @node-click="treeNodeClick"
                 :highlight-current="true"
                 :expand-on-click-node="false"
                 ref="treeForm" style="margin-top: 10px">
        </el-tree>
      </el-col>

      <!--**************************************************右侧图表*******************************************************-->
      <el-col :span="19" style="height: 100%;overflow-y: auto;padding: 10px;">
        <div>
          <el-form :inline="true" :model="queryForm" ref="queryForm">
            <el-form-item label="采集参数">
              <el-select v-model="queryForm.paramsId" placeholder="请配置支路电表及采集参数" :clearable="false" size="small"
                         @change="queryMeterparams()">
                <el-option
                  v-for="paramInfo in parametersList"
                  :key="paramInfo.id"
                  :label="paramInfo.name"
                  :value="paramInfo.id"
                  size="small"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="日期">
              <el-select v-model="queryForm.dataType" placeholder="请选择日期" :clearable="false" size="small">
                <!--@change="changeTableName()"-->
                <el-option
                  v-for="dateType in dateTypeList"
                  :key="dateType.id"
                  :label="dateType.name"
                  :value="dateType.id"
                  size="small"
                />
              </el-select>
            </el-form-item>
            <el-form-item v-if="queryForm.dataType == 1">
              <el-date-picker v-model="queryForm.dayDate"
                              type="date"
                              placeholder="选择年月日"
                              value-format="yyyy-MM-dd"
                              :clearable="false"
                              :picker-options="pickerOptions">
              </el-date-picker>
            </el-form-item>
            <el-form-item v-if="queryForm.dataType == 2">
              <el-date-picker v-model="queryForm.monDate"
                              type="month"
                              placeholder="选择年月"
                              value-format="yyyy-MM"
                              :clearable="false"
                              :picker-options="pickerOptions">
              </el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-refresh-right" type="primary" @click="queryAllData">查询</el-button>
              <el-button style="margin-left:0.5rem;" @click="dcMax" class="view" type="primary"
                         icon="el-icon-paperclip">
                导出
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        <div>
          <el-table v-loading="loading" :data="tableMaxData"
                    :header-cell-style="{background:'#01ada8',color:'#FFFFFF'}"
                    class="className"
                    ><!--height="100%"-->
            <el-table-column label="电表名称" align="center" min-width="140px" prop="name" /><!--fixed-->
            <el-table-column v-for="(item,index) in tableColumnIteam" :label="item.name" align="center"
                             :key="index">
              <el-table-column label="最大值" align="center">
                <el-table-column align="center" label="数值" :prop="'maxInfo'+item.id+item.type">
                </el-table-column>
                <el-table-column align="center" label="时间" min-width="180px" :prop="'maxTime'+item.id+item.type">
                </el-table-column>
              </el-table-column>
              <el-table-column label="最小值" align="center">
                <el-table-column align="center" label="数值" :prop="'minInfo'+item.id+item.type">
                </el-table-column>
                <el-table-column align="center" label="时间" min-width="180px" :prop="'minTime'+item.id+item.type">
                </el-table-column>
              </el-table-column>
              <el-table-column align="center" label="平均值" :prop="'avgInfo'+item.id+item.type">
              </el-table-column>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

    </el-row>


  </div>
</template>

<script>
  import {
    getAllPark,
    getEnergyType,
    queryMaxData,
    getCheckMeterParamsList,
    exportMaxPowerTable,
    queryMeterConfigparams,
  } from "@/api/electricPowerTranscription/extremeValueReport/extremeValueReport"
  import {
    treeSelect
  } from "@/api/basicData/energyInfo/branchConfig/config";
  import {timer} from "@/api/tool/timer";


  export default {
    name: "index",
    data() {
      return {
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > Date.now();
          },
        },
        timerId: null,
        queryForm: {
          parkCode: '', //园区
          branchId: '', //支路id
          branchName: '', //支路名称 用于展示
          energyType: '', //能源类型//
          paramsId: '',
          dataType: 1,
          paramsIdStr: [],
          dayDate: new Date().getFullYear().toString() + '-' + (new Date().getMonth() < 10 ? ('0' + (new Date().getMonth() + 1)) : (new Date().getMonth() + 1)).toString() + '-' + (new Date().getDate() < 10 ? ('0' + new Date().getDate()) : new Date().getDate()).toString(),
          monDate: new Date().getFullYear().toString() + '-' + (new Date().getMonth() < 10 ? ('0' + (new Date().getMonth() + 1)) : (new Date().getMonth() + 1)).toString(),
        },
        parkList: [],
        energyTypeList: [],

        metersList: [],//电表列表
        metersParamList: [],//电表对应参数
        parametersList: [],//采集参数列表
        dateTypeList: [{id: 1, name: '日'}, {id: 2, name: '月'}],

        filterTree: '',
        branchList: [],

        //右侧列表
        loading: false,
        tableColumnIteam: [],
        paramsName: '',
        tableMaxData: [],
        // tableMaxData: [{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'},{name:'1'}],
      };
    },
    created() {
      //查询所有园区
      this.getAllPark()
    },
    beforeDestroy() {
      if (this.timerId) { //如果定时器还在运行 或者直接关闭，不用判断
        clearInterval(this.timerId); //关闭
        this.timerId = null
      }
    },
    methods: {
      //查询所有园区
      getAllPark() {
        getAllPark().then(response => {
          this.parkList = response
          if (this.parkList.length > 0) {
            this.queryForm.parkCode = this.parkList[0].code
            //获取能源类型
            this.getEnergyType()
          } else {
            this.queryForm.parkCode = ''
          }
          // this.getBranchList()

        });
      },
      //切换园区
      changePark() {
        //获取能源类型
        this.getEnergyType()
      },
      //获取能源类型
      getEnergyType() {
        let params = {parkCode: this.queryForm.parkCode}
        getEnergyType(params).then(response => {
          this.energyTypeList = response
          if (this.energyTypeList.length > 0) {
            this.queryForm.energyType = this.energyTypeList[0].code
          } else {
            this.queryForm.energyType = ''
          }
          this.getBranchList()

        })
      },
      //清空搜索框
      reastfilterTree() {
        this.filterTree = ''
      },
      //获取支路
      getBranchList() {
        this.reastfilterTree()
        if (this.queryForm.parkCode == '' || this.queryForm.energyType == '') {
          this.branchList = []
          this.queryForm.branchId = ''
          this.queryForm.branchName = ''
        } else {
          treeSelect({
            parkCode: this.queryForm.parkCode,
            energyCode: this.queryForm.energyType
          }).then(response => {
            this.branchList = response.data
            if (response.data.length > 0) {
              this.queryForm.branchId = this.branchList[0].id
              this.queryForm.branchName = this.branchList[0].label
              this.$nextTick(() => {
                this.$refs.treeForm.setCurrentKey(this.branchList[0].id)
              })
              //查询所有采集参数
              this.getCheckMeterParamsList()
            } else {
              this.queryForm.branchId = ''
              this.queryForm.branchName = ''
              this.tableMaxData = []
              this.$modal.msgWarning("当前查询条件无支路")
              return
            }
          })
        }
      },
      //查询所有采集参数
      getCheckMeterParamsList() {
        this.queryForm.paramsIdStr = []
        getCheckMeterParamsList(this.queryForm).then(response => {
          if (response.code == 200) {
            if(response.data.length > 0){
              this.parametersList = response.data
              this.queryForm.paramsId = this.parametersList[0].id
              this.paramsName = this.parametersList[0].name
              //查询表格名称
              this.queryMeterparams()
            }else{
              this.parametersList = []
              this.queryForm.paramsId = null
              this.paramsName = null
              this.metersParamList = []
              this.tableColumnIteam = []
              this.tableMaxData = []
            }
          }
        });
      },
      //查询表格名称
      queryMeterparams() {
        this.loading = true
        this.queryForm.paramsIdStr = []
        queryMeterConfigparams(this.queryForm).then(response => {
          if (response.code == 200) {
            this.metersParamList = response.data
            this.tableColumnIteam = response.data
            // //查询右侧表格
            this.queryMaxData()
            this.timerQuery()
          }else{
            this.loading = false
          }
        });
      },
      //定时器一分钟查询一次
      timerQuery() {
        if (this.timerId != null) {
          clearInterval(this.timerId);
          this.timerId = null
        }
        this.timerId = setInterval(this.queryMaxData, timer.timerTime)
      },
      //支路下拉框查询方法
      filter(val) {
        this.$refs.treeForm.filter(val);
      },

      //支路树筛选
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },

      //点击支路树给查询form赋值
      treeNodeClick(data) {
        this.queryForm.branchId = data.id
        this.queryForm.branchName = data.label
        //查询采集参数
        this.getCheckMeterParamsList()
        // this.queryMaxData()
      },
      /*changeTableName() {

      },*/
      queryAllData() {
        if (this.queryForm.branchId == null || this.queryForm.branchId == '') {
          this.$modal.msgWarning("请选择支路")
          return
        }
        this.queryMaxData()
      },
      //逐日极值右侧数据
      queryMaxData() {
        this.loading = true
        let paramsIdStr = []
        this.tableColumnIteam.forEach(iteam => {
          paramsIdStr.push({id:iteam.id,type:iteam.type})
        })
        this.queryForm.paramsIdStr = paramsIdStr
        queryMaxData(JSON.stringify(this.queryForm)).then(response => {
          this.tableMaxData = []
          if (response.data.length > 0) {
            this.tableMaxData = response.data
          }else{
            this.$modal.msgWarning("无数据")
          }
          this.loading = false
        })
      },
      //导出表格
      dcMax() {
        if (this.tableMaxData.length === 0) {
          this.$message({
            showClose: true,
            message: '暂无数据，导出失败',
            type: 'error',
            duration: 1500
          });
          return;
        }
        let str = '序号-电表名称'
        this.tableColumnIteam.forEach(iteam => {
          str = str + '-' + iteam.name
        });
        this.$modal.confirm('是否确认导出极值报表数据？').then(() => {
          this.queryForm.tableColumnexport = str
          exportMaxPowerTable(this.queryForm).then(response => {
            const blob = new Blob([response]);
            const fileName = '极值报表数据.xls';
            const elink = document.createElement('a');// 创建a标签
            elink.download = fileName;// 为a标签添加download属性
            // a.download = fileName; //命名下载名称
            elink.style.display = 'none';
            elink.href = URL.createObjectURL(blob);
            document.body.appendChild(elink);
            elink.click();// 点击下载
            URL.revokeObjectURL(elink.href); // 释放URL 对象
            document.body.removeChild(elink);// 释放标签
          }).catch(err => {
            // console.log(err);
          });
        });
      },

      tableHeader({row, rowIndex}) {
        return 'font-size:14px;color:#797979;background: #fff;'

      }
    },
    watch: {
      // //监听查询的支路
      // 'queryForm.branchId': {
      //   deep: true,
      //   handler: function (val) {
      //     this.queryData()
      //   }
      // }
    },
  };
</script>
<style lang="scss" scoped>
  /* el-table 列数据为空自动显示 -- */
  .className :empty::before{
    content:'--';
    color:gray;
  }
</style>
