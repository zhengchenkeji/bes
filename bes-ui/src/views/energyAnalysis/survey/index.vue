<template>
  <div class="app-container">

    <!--顶部查询form-->
    <el-form :model="queryForm" ref="queryForm" size="small" :inline="true" label-width="20px" style="height: 5vh;">
      <el-form-item label="" prop="parkCode">
        <el-select v-model="queryForm.parkCode" @change="getBranchList">
          <el-option
            v-for="item in parkList"
            :key="item.code"
            :label="item.name"
            :value="item.code">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="-" prop="branchId">
        <!--<el-select v-model="queryForm.branchId">
          <el-option
            v-for="item in branchList"
            :key="item.id"
            :label="item.label"
            :value="item.id">
          </el-option>
        </el-select>-->
        <el-select v-model="queryForm.branchName" ref="selectBranchId" filterable :filter-method="filter" @change="treeNodeClick">
          <el-option :value="branchList"  class="select-tree">

            <el-tree :data="branchList"  node-key="id"
                     :filter-node-method="filterNode"
                     @node-click="treeNodeClick"
                     highlight-current
                     :expand-on-click-node="false"
                     ref="treeForm">
            </el-tree>
          </el-option><!--这个必不可少否则显示不出来下拉数据-->

        </el-select>
      </el-form-item>
      <el-form-item label="" prop="energyType">
        <el-select v-model="queryForm.energyType"  @change="getBranchList">
          <el-option
            v-for="item in energyTypeList"
            :key="item.code"
            :label="item.name"
            :value="item.code">
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>


    <!--中间 环比 能耗趋势-->
    <el-row style="height: 42vh">
      <!--环比-->
      <el-col :span="10" class="left">
        <div class="titl">
          <span class="signLine"></span>环比
        </div>

        <div class="content">

          <div class="item">
            <span class="number">{{ringRatio.today}}</span>
            <span class="name">今日用能(kW·h)</span>
          </div>
          <div class="item">
            <span class="number">{{ringRatio.yesterday}}</span>
            <span class="name">昨日同期(kW·h)</span>
          </div>
          <div class="item">
            <div class="trend">
              <div class="up">{{ringRatio.dayPercentage}}</div>
              <div class="down">{{ringRatio.dayDifference}}
                <span style="font-size: 12px;">kW·h</span>
              </div>
            </div>
            <span class="name">趋势</span>
          </div>

          <div class="item">
            <span class="number">{{ringRatio.month}}</span>
            <span class="name">当月用能(kW·h)</span>
          </div>
          <div class="item">
            <span class="number">{{ringRatio.lastMonth}}</span>
            <span class="name">上月同期(kW·h)</span>
          </div>
          <div class="item">
            <div class="trend">
              <div class="up">{{ringRatio.monthPercentage}}</div>
              <div class="down">{{ringRatio.monthDifference}}
                <span style="font-size: 12px;">kW·h</span>
              </div>
            </div>
            <span class="name">趋势</span>
          </div>

          <div class="item">
            <span class="number">{{ringRatio.year}}</span>
            <span class="name">今年用能(kW·h)</span>
          </div>
          <div class="item">
            <span class="number">{{ringRatio.lastYear}}</span>
            <span class="name">去年同期(kW·h)</span>
          </div>
          <div class="item">
            <div class="trend">
              <div class="up">{{ringRatio.yearPercentage}}</div>
              <div class="down">{{ringRatio.yearDifference}}
                <span style="font-size: 12px;">kW·h</span>
              </div>
            </div>
            <span class="name">趋势</span>
          </div>

        </div>
      </el-col>

      <!--能耗趋势-->
      <el-col :span="14" class="right">
        <div class="titl">
          <span class="signLine"></span>能耗趋势
          <div class="btn">
            <el-radio-group v-model="trendBtn" size="mini" @change="queryTrendData">
              <el-radio-button label="0">日</el-radio-button>
              <el-radio-button label="1">月</el-radio-button>
              <el-radio-button label="2">年</el-radio-button>
            </el-radio-group>
            <!--<el-button size="mini">当日</el-button>
            <el-button size="mini">当月</el-button>
            <el-button size="mini">当年</el-button>-->
          </div>
        </div>

        <div class="content">
          <trend-chart :data="trendData"/>
        </div>
      </el-col>
    </el-row>


    <el-row style="height: 39vh">
      <!--能耗排行-->
      <el-col :span="24" class="left">
        <div class="titl">
          <span class="signLine"></span>能耗排行
          <div class="btn">
            <el-radio-group v-model="rankBtn" size="mini" @change="queryEnergyRank">
              <el-radio-button label="0">日</el-radio-button>
              <el-radio-button label="1">月</el-radio-button>
              <el-radio-button label="2">年</el-radio-button>
            </el-radio-group>
            <!--<el-button size="mini">当日</el-button>
            <el-button size="mini">当月</el-button>
            <el-button size="mini">当年</el-button>-->
          </div>
        </div>
        <div class="content">
          <rank-chart :data="rankData" />
        </div>
      </el-col>

      <!--功率峰值-->
      <!--<el-col :span="5" class="right">
        <div class="titl">
          <span class="signLine"></span>功率峰值
        </div>
        <div class="content" style="padding: 10px">

          <div class="max_power">
            <div class="max_power_num">{{maxPower.todayNum}}</div>
            <div class="max_power_time">{{maxPower.todayTime}}</div>
            <div class="max_power_name">当日(kW)</div>
          </div>

          <div class="max_power">
            <div class="max_power_num">{{maxPower.yesterdayNum}}</div>
            <div class="max_power_time">{{maxPower.yesterdayTime}}</div>
            <div class="max_power_name">昨日(kW)</div>
          </div>

        </div>
      </el-col>-->
    </el-row>



  </div>
</template>

<script>
  import trendChart from './trendChartNew'
  import rankChart from './rankChart'
  import {getAllPark,getEnergyType,queryRingRatioData,queryTrendData,queryRankData} from "@/api/energyAnalysis/survey/survey"
  import {
    treeSelect
  } from "@/api/basicData/energyInfo/branchConfig/config";
  import {timer} from "@/api/tool/timer";

  export default {
    name: "BuildBaseInfo",
    components:{trendChart,rankChart},
    data() {
      return {
          timerId: null,

        //查询
        queryForm:{
          parkCode:'', //园区
          branchId:'', //支路id
          branchName:'', //支路名称 用于展示
          energyType:'', //能源类型
        },
        //园区列表
        parkList:[],
        //支路列表
        branchList:[
          {id:1,label:'1',children:[
              {id:2,label:'2-1',children:[
                  {id:3,label:'3-1'},
                  {id:4,label:'3-2'}
                ]},
              {id:5,label:'2-2'}
            ]}
        ],
        //能源类型
        energyTypeList:[],

        //环比
        ringRatio:{
          today:'0',  //今日用能
          yesterday:'0', //昨日同期
          dayDifference:'0', //日差值
          dayPercentage:'--', //日差值百分比

          month:'0', //当月用能
          lastMonth:'0', //上月同期
          monthDifference:'0', //月差值
          monthPercentage:'--', //月差值百分比

          year:'0', //今年用能
          lastYear:'0', //去年同期
          yearDifference:'0', //年差值
          yearPercentage:'--', //年差值百分比
        },

        //能耗趋势按钮 0日 1月 2 年
        trendBtn:'0',

        //能耗趋势echarts数据
        trendData:{
          //支路名称
          legend:[],
          //x轴单位
          xUnit:'时',
          //x轴
          xData:[],
          //y轴
          thisTimeData:[],
          lastTimeData:[],
        },


        //能耗排行按钮  0日 1月 2年
        rankBtn:'0',
        //能耗排行数据
        rankData:{
          xData:[],
          yData:[]
        },
        //日用电功率曲线echarts数据
        /*powerData:{
          xData:["1", "2", "3", "4", "5", "6", "7","8","9","10","11","12","13","14","15","16","17","18","19","20",
            "21","22","23","24","25","26","27","28","29","30"],
          yesterday: [240, 240,  '--',  '--',561, 548,338,119,547,573,573,519,511,364,242,557,565,513,551,561,335,125,563,577,571,583,542,320,228,553.6],
          today:[222, 220, 566, 524, 461, 348,438,319,247,173,573,519,511,364,242,557,565,513,551,561,335,125,563,577,571,583,542,320,228,553.6],
        },*/

        //功率峰值
        maxPower:{
          todayNum: null, //当日峰值
          todayTime: null, //当日峰值时间

          yesterdayNum: null, //昨日峰值
          yesterdayTime: null, //昨日峰值时间
        },

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

      //查询环比
      queryRingRatioData(){
          this.ringRatio = {
              today:'0',  //今日用能
              yesterday:'0', //昨日同期
              dayDifference:'0', //日差值
              dayPercentage:'--', //日差值百分比

              month:'0', //当月用能
              lastMonth:'0', //上月同期
              monthDifference:'0', //月差值
              monthPercentage:'--', //月差值百分比

              year:'0', //今年用能
              lastYear:'0', //去年同期
              yearDifference:'0', //年差值
              yearPercentage:'--', //年差值百分比
          }
          if (!this.queryForm.branchId){
              return
          }

          queryRingRatioData({
              branchId:this.queryForm.branchId,
          }).then(response => {
              this.timerQuery()
              this.ringRatio = response.data
          })

      },
      //查询能耗趋势
      queryTrendData(){
        this.trendData.name = this.queryForm.branchName
        this.trendData.xUnit = ''
        this.trendData.legend = []
        this.trendData.xData = []
        this.trendData.thisTimeData = []
        this.trendData.lastTimeData = []

        //组装x坐标轴、单位、名称
        var timeList = ["00","01","02","03","04","05","06","07","08","09","10","11","12",
          "13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"];
        if (this.trendBtn == '0'){
          this.trendData.xUnit = "时"
          this.trendData.legend = ['昨日','今日']
          //坐标轴直接为24小时
          this.trendData.xData = timeList.splice(0,24)
        } else if (this.trendBtn == '1'){
          this.trendData.xUnit = "日"
          this.trendData.legend = ['上月','本月']
          //今天 的日数字
          var date = new Date()
          var daysNum = date.getDate()

          var year = date.getFullYear()
          var month = date.getMonth()

          //生成上月最后一天的日期
          var lastMonth = new Date(year,month,0)
          //上月的天数
          var lastMonthDays = lastMonth.getDate()

          //如果上月天数多则 x轴为上月的
          //如果当前日比上月天数多,x轴则为本月的(如 现在是3月29日,2月份只有28天,则x轴为1-29)
          if(lastMonthDays > daysNum){
            this.trendData.xData = timeList.splice(1,lastMonthDays)
          } else {
            this.trendData.xData = timeList.splice(1,daysNum)
          }
        } else if (this.trendBtn == '2'){
          this.trendData.xUnit = "月"
          this.trendData.legend = ['去年','今年']
          //按年的话 x轴直接为12个月
          this.trendData.xData = timeList.splice(1,12)
        }

          if (!this.queryForm.branchId) {
              return
          }

          //查询数据
          queryTrendData({
              branchId:this.queryForm.branchId,
              trendType:this.trendBtn
          }).then(response => {
              var thisTimeData = []
              var lastTimeData = []
              //根据x轴组装数据,没有的填充'--'
              this.trendData.xData.forEach(item =>{
                  if (response.data.thisTimeData[item] != null){
                      thisTimeData.push(response.data.thisTimeData[item])
                  } else {
                      thisTimeData.push('--')
                  }
                  if (response.data.lastTimeData[item] != null && response.data.lastTimeData[item] !== undefined){
                      lastTimeData.push(response.data.lastTimeData[item])
                  } else {
                      lastTimeData.push('--')
                  }
              })
              this.trendData.thisTimeData = thisTimeData
              this.trendData.lastTimeData = lastTimeData

              this.timerQuery()

          })


      },
      queryEnergyRank(){
          this.rankData.xData = []
          this.rankData.yData = []
          if (!this.queryForm.branchId) {
              return
          }
          queryRankData({
              branchId: this.queryForm.branchId,
              rankType: this.rankBtn
          }).then(response => {
              var data = response.data
              if (data.length > 0) {
                  data.forEach(item => {
                      this.rankData.xData.push(item.dataValue)
                      this.rankData.yData.push(item.branchName)
                  })
              }

              this.timerQuery()

          })


      },

        //定时器一分钟查询一次
        timerQuery() {
            if(this.timerId){
                clearInterval(this.timerId);
                this.timerId = null
            }
            this.timerId=setInterval(this.timerQueryData, timer.timerTime);
        },
        timerQueryData(){
            //查询环比
            this.queryRingRatioData()
            //查询能耗趋势
            this.queryTrendData()
            //查询能耗排行
            this.queryEnergyRank()
        },
    },
    watch: {
      //监听查询的支路
      'queryForm.branchId':{
        deep: true,
        handler: function(val){
          //查询环比
            this.queryRingRatioData()
            //查询能耗趋势
            this.queryTrendData()
            //查询能耗排行
            this.queryEnergyRank()

        }
      }
    },
  };
</script>
<style lang="scss" scoped>
  .left{
    height: 100%;
  }
  .right{
    height: 100%;
  }

  .titl{
    margin-bottom: 5px;
    font-size: 16px;
    padding: 10px 8px 8px 8px;
    font-weight: 400;
    .signLine {
      display: inline-block;
      width: 4px;
      height: 15px;
      background-color: #409eff;
      margin-right: 6px;
      vertical-align: -2px;
    }
    .btn{
      float: right;
      margin-right: -7px;
    }
  }
.theme-blue {
  .signLine{
    background-color: #96d4ec;
  }
  .titl{
    color: white;
  }
  .item{
    border: 1px solid #FFFFFF !important;
  }
  .number{
    color: white;
  }
  .up{
    color: white;
  }
  .down{
    color: white;
    margin-top: 5px;
    padding-top: 5px;
    border-top: 1px solid #FFFFFF !important;
    font-size: 20.16px;
  }
}
  .content{
    display: flex;
    flex-wrap: wrap;
    height: 37vh;
    .item{
      width: 32%;
      height: 32%;
      margin: 2px;
      border: 1px solid #b2b2b2;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      .number{
        margin-top: 5px;
        font-size: 25.2px;
      }
      .name{
        font-size: 14px;
        color: #b2b2b2;
        margin-top: 5px;
      }
      .trend{
        margin-top: 5px;
        width: 100%;
        padding: 0 13px 0 13px;
        display: flex;
        flex-direction: column;
        text-align: center;

        .up{
          font-size: 17.64px;
        }
        .down{
          margin-top: 5px;
          padding-top: 5px;
          border-top: 1px solid #191919;
          font-size: 20.16px;
        }
      }
    }
  }
  .max_power{
    margin-bottom: 10px;
    height: 47%;
    width: 100%;
    display: flex;
    flex-direction: column;
    border: 1px solid #01ada8;
    .max_power_num{
      height: 35%;
      display: -webkit-box;
      display: -ms-flexbox;
      display: flex;
      -webkit-box-align: end;
      -ms-flex-align: end;
      align-items: flex-end;
      -webkit-box-pack: center;
      -ms-flex-pack: center;
      justify-content: center;
      font-size: 30px;
    }
    .max_power_time{
      height: 35%;
      display: -webkit-box;
      display: -ms-flexbox;
      display: flex;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      -webkit-box-pack: center;
      -ms-flex-pack: center;
      justify-content: center;
    }
    .max_power_name{
      height: 30%;
      background-color: #01ada8;
      color: #fff;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }

  .select-tree {
    max-height: 200px;
    overflow-y: auto;
    background-color: white;
    padding: 0;
    height: 100%;

  }
</style>
