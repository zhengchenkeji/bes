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

        <!--**************************************************日期*******************************************************-->
        <div>
          <el-button style="margin-right: 5px" @click="subDay"><</el-button>
          <el-date-picker
            v-model="queryForm.dayDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            :clearable="false"
            :picker-options="pickerOptions"
            @change="queryData(0)">
          </el-date-picker>
          <el-button style="margin-left: 5px" @click="addDay"></el-button>
        </div>
        <div style="height: 40%">
          <div style="height: 100%">
            <day-chart :data="echartsDayData"/><!--echartsData-->
          </div>
        </div>
        <!--**************************************************月份*******************************************************-->
        <div>
          <el-button style="margin-right: 5px" @click="subMon"><</el-button>
          <el-date-picker v-model="queryForm.monDate"
                          type="month"
                          placeholder="选择年月"
                          value-format="yyyy-MM"
                          :picker-options="pickerOptions"
                          :clearable="false"
                          @change="queryData(1)">
          </el-date-picker>
          <el-button style="margin-left: 5px" @click="addMon"></el-button>
        </div>
        <div style="height: 40%">
          <div style="height: 100%">
            <month-chart :data="echartsMonthData"/>
          </div>
        </div>
        <!--**************************************************年份*******************************************************-->
        <div>
          <el-button style="margin-right: 5px" @click="subYear"><</el-button>
          <el-date-picker
            v-model="queryForm.yearDate"
            type="year"
            placeholder="选择年"
            value-format="yyyy"
            :clearable="false"
            :picker-options="pickerOptions"
            @change="queryData(2)">
          </el-date-picker>
          <el-button style="margin-left: 5px" @click="addYear"></el-button>
        </div>
        <div style="height: 40%">
          <div style="height: 100%">
            <year-chart :data="echartsYearData"/>
          </div>
        </div>
      </el-col>

    </el-row>


  </div>
</template>

<script>
  import dayChart from './dayChart'
  import monthChart from './monthChart'
  import yearChart from './yearChart'
  import {getAllPark, getEnergyType, queryData} from "@/api/energyAnalysis/IndividualAccount/IndividualAccount"
  import {
    treeSelect
  } from "@/api/basicData/energyInfo/householdConfig/config";
  import {timer} from "@/api/tool/timer";


  export default {
    name: "index",
    components: {dayChart, monthChart, yearChart},
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
          energyType: '', //能源类型
          dayDate: new Date(),//
          monDate: new Date(),//
          yearDate: new Date(),
        },
        parkList: [],
        energyTypeList: [],

        filterTree: '',
        branchList: [],

        echartsDayData: {
          xData: [],
          today: [],
          yesterday: [],
        },
        echartsMonthData: {
          xData: [],
          today: [],
          yesterday: [],
        },
        echartsYearData: {
          xData: [],
          today: [],
          yesterday: [],
        },
      };
    },
    created() {
      //查询所有园区
      this.getAllPark()
    },
    beforeDestroy() {
      if(this.timerId) { //如果定时器还在运行 或者直接关闭，不用判断
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
          this.getBranchList()

        });
      },
      //切换园区
      changePark(){
        //获取能源类型
        this.getEnergyType()
      },
      //获取能源类型
      getEnergyType() {
        let params = {parkCode:this.queryForm.parkCode}
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

      //获取支路
      getBranchList() {
        //清空搜索框
        this.filterTree = ''
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
              //查询右侧图表数据
              this.queryData(0)
              this.queryData(1)
              this.queryData(2)
              this.timerQuery()
            } else {
              this.queryForm.branchId = ''
              this.queryForm.branchName = ''
              this.queryForm.branchId = ''
              this.queryForm.branchName = ''
              this.echartsDayData.today = []
              this.echartsDayData.yesterday = []
              this.echartsDayData.xData = []
              this.echartsMonthData.today = []
              this.echartsMonthData.yesterday = []
              this.echartsMonthData.xData = []
              this.echartsYearData.today = []
              this.echartsYearData.yesterday = []
              this.echartsYearData.xData = []
              // this.$modal.msgWarning("当前查询条件无支路")
              return
            }
          })
        }
      },
      //定时器一分钟查询一次
      timerQuery() {
        if(this.timerId){
          clearInterval(this.timerId);
          this.timerId = null
        }
        this.timerId=setInterval(this.queryRightData, timer.timerTime);
      },
      queryRightData(){
        //查询右侧图表数据
        this.queryData(0)
        this.queryData(1)
        this.queryData(2)
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
        this.queryData(0)
        this.queryData(1)
        this.queryData(2)
      },
      queryData(type) {
        if(this.queryForm.branchId == null || this.queryForm.branchId == ''){
          // this.$modal.msgWarning("请选择支路")
          return
        }
        let Param = {
          ZLID: this.queryForm.branchId,
          DNBH: this.queryForm.energyType,
          type: type
        }
        console.log(typeof this.queryForm.dayDate)
        if (typeof this.queryForm.dayDate != "string") {
          if (type == 0) {
            Param['CJSJ'] = this.queryForm.dayDate.getFullYear().toString() + '-' + (this.queryForm.dayDate.getMonth() < 10 ? ('0' + (this.queryForm.dayDate.getMonth() + 1)) : (this.queryForm.dayDate.getMonth() + 1)).toString() + '-' + (this.queryForm.dayDate.getDate() < 10 ? ('0' + this.queryForm.dayDate.getDate()) : this.queryForm.dayDate.getDate()).toString()
          } else if (type == 1) {
            Param['CJSJ'] = this.queryForm.monDate.getFullYear().toString() + '-' + (this.queryForm.monDate.getMonth() < 10 ? ('0' + (this.queryForm.monDate.getMonth() + 1)) : (this.queryForm.monDate.getMonth() + 1)).toString()
          } else {
            Param['CJSJ'] = this.queryForm.yearDate.getFullYear().toString()
          }
        } else {
          if (type == 0) {
            Param['CJSJ'] = this.queryForm.dayDate
          } else if (type == 1) {
            Param['CJSJ'] = this.queryForm.monDate
          } else {
            Param['CJSJ'] = this.queryForm.yearDate
          }
        }
        queryData(Param).then(response => {
          var result = response.data
          if (type == 0) {
            this.echartsDayData.today = []
            this.echartsDayData.yesterday = []
            this.echartsDayData.xData = []
            if (result != null) {
              result.forEach(item => {
                this.echartsDayData.xData.push(item.CJSJ)
                this.echartsDayData.yesterday.push(item.beforeData)
                this.echartsDayData.today.push(item.nowData)
              })
            }
          } else if (type == 1) {
            this.echartsMonthData.today = []
            this.echartsMonthData.yesterday = []
            this.echartsMonthData.xData = []
            if (result != null) {
              result.forEach(item => {
                this.echartsMonthData.xData.push(item.CJSJ)
                this.echartsMonthData.yesterday.push(item.beforeData)
                this.echartsMonthData.today.push(item.nowData)
              })
            }
          } else {
            this.echartsYearData.today = []
            this.echartsYearData.yesterday = []
            this.echartsYearData.xData = []
            if (result != null) {
              result.forEach(item => {
                this.echartsYearData.xData.push(item.CJSJ)
                this.echartsYearData.yesterday.push(item.beforeData)
                this.echartsYearData.today.push(item.nowData)
              })
            }
          }
        })
      },

      subDay(){
        let dateType = new Date(this.queryForm.dayDate);
        dateType = dateType
        dateType=dateType.setDate(dateType.getDate()-1);
        dateType=new Date(dateType);
        this.queryForm.dayDate = dateType
        this.queryData(0)
      },
      addDay(){
        let datenow = new Date()
        let dateType = new Date(this.queryForm.dayDate);
        if(datenow.getFullYear() == dateType.getFullYear() && datenow.getMonth() == dateType.getMonth() && datenow.getDate() == dateType.getDate()){
           return
        }
        dateType=dateType.setDate(dateType.getDate()+1);
        dateType=new Date(dateType);
        this.queryForm.dayDate = dateType
        this.queryData(0)
      },
      subMon(){
        let dateType = new Date(this.queryForm.monDate);
        dateType = dateType
        dateType=dateType.setMonth(dateType.getMonth()-1);
        dateType=new Date(dateType);
        this.queryForm.monDate = dateType
        this.queryData(1)
      },
      addMon(){
        let datenow = new Date()
        let dateType = new Date(this.queryForm.monDate);
        if(datenow.getFullYear() == dateType.getFullYear() && datenow.getMonth() == dateType.getMonth()){
          return
        }
        dateType=dateType.setMonth(dateType.getMonth()+1);
        dateType=new Date(dateType);
        this.queryForm.monDate = dateType
        this.queryData(1)
      },
      subYear(){
        let dateType = new Date(this.queryForm.yearDate);
        dateType = dateType
        dateType=dateType.setFullYear(dateType.getFullYear()-1);
        dateType=new Date(dateType);
        this.queryForm.yearDate = dateType
        this.queryData(2)
      },
      addYear(){
        let datenow = new Date()
        let dateType = new Date(this.queryForm.yearDate);
        if(datenow.getFullYear() == dateType.getFullYear()){
          return
        }
        dateType=dateType.setFullYear(dateType.getFullYear()+1);
        dateType=new Date(dateType);
        this.queryForm.yearDate = dateType
        this.queryData(2)
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

</style>
