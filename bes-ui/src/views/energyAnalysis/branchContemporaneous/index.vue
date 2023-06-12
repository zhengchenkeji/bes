<template>
  <div class="app-container">

    <el-row style="height: 86.2vh">
      <el-col :span="5" style="border-right: 3px #f6f6f6 solid;height: 100%;padding: 10px;overflow-y: auto">
        <el-form :model="queryForm" ref="queryForm" size="small" label-width="80px" style="border-bottom: 2px #f6f6f6 solid;">
          <el-form-item label="园区" prop="parkCode">
            <el-select v-model="queryForm.parkCode" @change="getBranchList" >
              <el-option
                v-for="item in parkList"
                :key="item.code"
                :label="item.name"
                :value="item.code">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="能源类型" prop="energyType">
            <el-select v-model="queryForm.energyType"  @change="getBranchList">
              <el-option
                v-for="item in energyTypeList"
                :key="item.code"
                :label="item.name"
                :value="item.code">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="年份" prop="yearDate">
            <el-date-picker
              v-model="queryForm.yearDate"
              type="year"
              placeholder="选择年"
              value-format="yyyy"
              :clearable="false"
            @change="queryData">
            </el-date-picker>
          </el-form-item>
        </el-form>
        <el-input
          placeholder="搜索支路"
          @input="filter"
          size="small"
          v-model="filterTree" style="margin-top: 10px">
        </el-input>

        <el-tree :data="branchList"  node-key="id"
                 :filter-node-method="filterNode"
                 @node-click="treeNodeClick"
                 highlight-current
                 :expand-on-click-node="false"
                 ref="treeForm" style="margin-top: 10px">
        </el-tree>
      </el-col>
      <el-col :span="19" style="height: 100%;padding: 10px;">
        <div style="height: 50%">
          <contemporaneous-chart :data="echartsData" />
        </div>
        <div style="height: 50%">
          <el-table
            :data="tableData"
            height="100%"
            border
            :header-cell-style="{background:'#01ada8',color:'#FFFFFF'}"
            style="width: 100%">
            <el-table-column
              prop="month"
              label="本期时间">
            </el-table-column>
            <el-table-column
              prop="thisData"
              label="本期能耗(kW·h)">
            </el-table-column>
            <el-table-column
              prop="lastData"
              label="同比能耗(kW·h)">
            </el-table-column>
            <el-table-column
              prop="ratio"
              label="同比(%)">
            </el-table-column>
          </el-table>
        </div>
      </el-col>

    </el-row>


  </div>
</template>

<script>
  import contemporaneousChart from './contemporaneousChart'
  import {getAllPark,getEnergyType,queryData} from "@/api/energyAnalysis/branchContemporaneous/branchContemporaneous"
  import {
    treeSelect
  } from "@/api/basicData/energyInfo/branchConfig/config";
  import {timer} from "@/api/tool/timer";


  export default {
    name: "branchContemporaneous",
    components:{contemporaneousChart},
    data() {
      return {
          timerId: null,

          queryForm:{
          parkCode:'', //园区
          branchId:'', //支路id
          branchName:'', //支路名称 用于展示
          energyType:'', //能源类型
          yearDate:new Date().getFullYear().toString(),
        },
        parkList:[],
        energyTypeList:[],

        filterTree:'',
        branchList:[],

        echartsData:{
          xData:['01','02','03','04','05','06','07','08','09','10','11','12'],
          thisData:[],
          lastData:[],
        },
        tableData:[

        ],


      };
    },
    created() {
      //查询所有园区
      this.getAllPark()
      //获取能源类型
      this.getEnergyType()
    },
    beforeDestroy() {
      if(this.timerId) { //如果定时器还在运行 或者直接关闭，不用判断
          clearInterval(this.timerId); //关闭
          this.timerId = null
      }
    },
    methods: {

      //查询所有园区
      getAllPark(){
        getAllPark().then(response => {
          this.parkList = response
          if (this.parkList.length>0){
            this.queryForm.parkCode = this.parkList[0].code
          } else {
            this.queryForm.parkCode = ''
          }
          this.getBranchList()

        });
      },
      //获取能源类型
      getEnergyType(){
        getEnergyType().then(response => {
          this.energyTypeList = response
          if (this.energyTypeList.length>0){
            this.queryForm.energyType=this.energyTypeList[0].code
          } else {
            this.queryForm.energyType = ''
          }
          this.getBranchList()

        })
      },

      //获取支路
      getBranchList(){
        if (this.queryForm.parkCode == '' || this.queryForm.energyType == ''){
          this.branchList = []
          this.queryForm.branchId = ''
          this.queryForm.branchName = ''
        } else {
          treeSelect({
            parkCode:this.queryForm.parkCode,
            energyCode:this.queryForm.energyType
          }).then(response => {
            this.branchList = response.data
          }).then(res =>{
            if (this.branchList.length > 0){
              this.queryForm.branchId = this.branchList[0].id
              this.queryForm.branchName = this.branchList[0].label
              this.$refs.treeForm.setCurrentKey(this.branchList[0].id)
            } else {
              this.queryForm.branchId = ''
              this.queryForm.branchName = ''
                this.$message.error('未查询到支路')
            }
          })
        }
      },

      //支路下拉框查询方法
      filter(val){
        this.$refs.treeForm.filter(val);
      },

      //支路树筛选
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },

      //点击支路树给查询form赋值
      treeNodeClick(data){
        this.queryForm.branchId = data.id
        this.queryForm.branchName = data.label
      },


      queryData(){
        this.echartsData.thisData = []
        this.echartsData.lastData = []
          this.tableData = []
          if (!this.queryForm.branchId){
              return
          }
        queryData({
          branchId:this.queryForm.branchId,
          dateTime:this.queryForm.yearDate
        }).then(response => {
            this.timerQuery()

            var result = response.data

          //表格数据
          this.tableData = result

          //echarts数据
          result.forEach(item =>{
            this.echartsData.thisData.push(item.thisData)
            this.echartsData.lastData.push(item.lastData)
          })


        })
      },

        //定时器一分钟查询一次
        timerQuery() {
            if(this.timerId){
                clearInterval(this.timerId);
                this.timerId = null
            }
            this.timerId=setInterval(this.queryData, timer.timerTime);
        },

      tableHeader ({row,rowIndex}){
        return 'font-size:14px;color:#797979;background: #fff;'

      }
    },
    watch: {
      //监听查询的支路
      'queryForm.branchId':{
        deep: true,
        handler: function(val){
          this.queryData()
        }
      }
    },
  };
</script>
<style lang="scss" scoped>

</style>
