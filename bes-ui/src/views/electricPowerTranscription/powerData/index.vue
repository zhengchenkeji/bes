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
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <!--**************************************************日原始数据*******************************************************-->
          <el-tab-pane label="日原始数据" name="first" :lazy="true">
            <br/>
            <br/>
            <div>
              <el-form :inline="true" :model="queryForm" ref="queryForm">
                <el-form-item label="电表名称">
                  <el-select v-model="queryForm.meterId" placeholder="请选择电表" :clearable="false" size="small"
                             @change="changeMeterType">
                    <el-option
                      v-for="meterInfo in metersList"
                      :key="meterInfo.id"
                      :label="meterInfo.name"
                      :value="meterInfo.id"
                      size="small"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="日期">
                  <el-date-picker
                    clearable
                    v-model="startEndDate"
                    type="daterange"
                    value-format="yyyy-MM-dd"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    :clearable="false"
                    :picker-options="pickerOptions">
                  </el-date-picker>
                </el-form-item>
                <el-form-item label="采集参数">
                  <el-select v-model="queryForm.paramsId" placeholder="请选择采集参数" :clearable="false" size="small"
                             @change="queryMeterparams">
                    <el-option
                      v-for="paramInfo in parametersList"
                      :key="paramInfo.id"
                      :label="paramInfo.name"
                      :value="paramInfo.id"
                      size="small"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button icon="el-icon-refresh-right" type="primary" @click="queryAllData">查询</el-button>
                  <el-button style="margin-left:0.5rem;" @click="dcDay" class="view" type="primary"
                             icon="el-icon-paperclip"
                             v-show="showPart=='sj'">导出
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
            <el-button style="margin-left: 5px" @click="showPartMethods('tb')">图表</el-button>
            <el-button style="margin-left: 5px" @click="showPartMethods('sj')">数据</el-button>
            <br/>
            <br/>
            <div style="width: 100%; height: 500px" v-show="showPart == 'tb'">
              <day-chart id="dayChart" :data="echartsDayData"/>
            </div>
            <div v-show="showPart != 'tb'" style="height: 550px">
              <el-table border v-loading="loading" :data="tableData" height="100%"
                        :header-cell-style="{background:'#01ada8',color:'#FFFFFF'}">
                <el-table-column label="采集时间" align="center" prop="CJSJ"/>
                <el-table-column v-for="(item,index) in tableColumnIteam" :label="item.name" align="center"
                                 :key="index" :prop="'dataInfo'+item.id+item.type"/>
              </el-table>
            </div>
          </el-tab-pane>
          <!--**************************************************逐日极值数据*******************************************************-->
          <el-tab-pane label="逐日极值数据" name="second" :lazy="true">
            <br/>
            <br/>
            <div>
              <el-form :inline="true" :model="queryForm" ref="queryForm">
                <el-form-item label="电表名称">
                  <el-select v-model="queryForm.meterId" placeholder="请选择电表" :clearable="false" size="small"
                             @change="changeMeterType">
                    <el-option
                      v-for="meterInfo in metersList"
                      :key="meterInfo.id"
                      :label="meterInfo.name"
                      :value="meterInfo.id"
                      size="small"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="日期">
                  <el-date-picker
                    clearable
                    v-model="startEndDate"
                    type="daterange"
                    value-format="yyyy-MM-dd"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    :clearable="false"
                    :picker-options="pickerOptions">
                  </el-date-picker>
                </el-form-item>
                <el-form-item label="采集参数">
                  <el-select v-model="queryForm.paramsId" placeholder="请选择采集参数" :clearable="false" size="small"
                             @change="queryMeterparams">
                    <el-option
                      v-for="paramInfo in parametersList"
                      :key="paramInfo.id"
                      :label="paramInfo.name"
                      :value="paramInfo.id"
                      size="small"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button icon="el-icon-refresh-right" type="primary" @click="queryAllData">查询</el-button>
                  <el-button style="margin-left:0.5rem;" @click="dcMax" class="view" type="primary"
                             icon="el-icon-paperclip"
                             v-show="showPart=='sj'">导出
                  </el-button>
                </el-form-item>
                <br/>
                <el-form-item label="采集参数分项">
                  <el-radio-group v-model="queryForm.paramsItemId" placeholder="请选择采集参数" :clearable="false" size="small"
                                  @change="changeRadio">
                    <el-radio :label="paramInfo.id" v-for="paramInfo in metersParamList">{{paramInfo.name}}</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-form>
            </div>
            <el-button style="margin-left: 5px" @click="showPartMethods('tb')">图表</el-button>
            <el-button style="margin-left: 5px" @click="showPartMethods('sj')">数据</el-button>
            <br/>
            <br/>
            <div style="width: 100%; height: 500px" v-show="showPart == 'tb'">
              <max-chart id="maxChart" :data="echartsMaxData"/>
            </div>
            <div v-show="showPart != 'tb'" style="height: 500px">
              <el-table border v-loading="loading" :data="tableMaxData" height="100%"
                        :header-cell-style="{background:'#01ada8',color:'#FFFFFF'}">
                <el-table-column label="采集时间" align="center" prop="CJSJ"/>
                <el-table-column :label="paramsName" align="center">
                  <el-table-column label="最大值" align="center">
                    <el-table-column align="center" label="数值" prop="maxInfo"></el-table-column>
                    <el-table-column align="center" label="时间" min-width="180px"
                                     prop="maxVTime"></el-table-column>
                  </el-table-column>
                  <el-table-column label="最小值" align="center">
                    <el-table-column align="center" label="数值" prop="minInfo"></el-table-column>
                    <el-table-column align="center" label="时间" min-width="180px"
                                     prop="minVTime"></el-table-column>
                  </el-table-column>
                  <el-table-column align="center" label="平均值" prop="avgInfo"></el-table-column>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-col>

    </el-row>


  </div>
</template>

<script>
  import dayChart from './dayChart'
  import maxChart from './maxChart'
  import {
    getAllPark,
    getEnergyType,
    queryData,
    queryDataTable,
    queryMaxData,
    getCheckMeterList,
    queryMeterparams,
    getCheckMeterParamsList,
    exportPowerTable,
    exportMaxPowerTable,
  } from "@/api/electricPowerTranscription/powerData/powerData"
  import {
    treeSelect
  } from "@/api/basicData/energyInfo/branchConfig/config";
  import {timer} from "@/api/tool/timer";


  export default {
    name: "index",
    components: {dayChart, maxChart},
    data() {
      return {
        timerId: null,
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() > Date.now();
          },
        },
        activeName: 'first',
        queryForm: {
          parkCode: '', //园区
          branchId: '', //支路id
          branchName: '', //支路名称 用于展示
          energyType: '', //能源类型//
          meterId: '',
          meterType: '',
          paramsId: '',
          startTime: '',
          endTime: '',
          paramsItemId: '',
        },
        startEndDate: [new Date().getFullYear().toString() + '-' + (new Date().getMonth() < 10 ? ('0' + (new Date().getMonth() + 1)) : (new Date().getMonth() + 1)).toString() + '-' + (new Date().getDate() < 10 ? ('0' + new Date().getDate()) : new Date().getDate()).toString(), new Date().getFullYear().toString() + '-' + (new Date().getMonth() < 10 ? ('0' + (new Date().getMonth() + 1)) : (new Date().getMonth() + 1)).toString() + '-' + (new Date().getDate() < 10 ? ('0' + new Date().getDate()) : new Date().getDate()).toString()],
        parkList: [],
        energyTypeList: [],

        metersList: [],//电表列表
        parametersList: [],//采集参数列表
        metersParamList: [],//采集参数分项列表

        filterTree: '',
        branchList: [],

        //日原始数据
        showPart: 'tb',
        echartsDayData: {
          paramData: [],
        },
        //日原始数据
        loading: false,
        tableData: [],
        tableColumnIteam: [],
        paramsName: '',

        //逐日极值数据
        echartsMaxData: {
          xData: [],
          maxInfo: [],
          minInfo: [],
          avgInfo: [],
        },
        tableMaxData: [],
      };
    },
    created() {
      //查询所有园区
      this.getAllPark()
    },
    beforeDestroy() {
      if (this.timerId) {
        clearInterval(this.timerId);
        this.timerId = null;
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

      //清空搜索框、查询条件、右侧数据
      reastfilterTree() {
        this.filterTree = ''
        this.queryForm.branchId = ''
        this.queryForm.branchName = ''
        this.tableData = []
        this.echartsDayData.paramData = []
        this.tableMaxData = []
        this.echartsMaxData.maxInfo = []
        this.echartsMaxData.avgInfo = []
        this.echartsMaxData.minInfo = []
        this.parametersList = []
        this.metersList = []
        this.queryForm.meterId = ''
        this.queryForm.paramsId = ''
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
              //查询电表下拉框
              this.getCheckMeterList()
            } else {
              this.queryForm.branchId = ''
              this.queryForm.branchName = ''
              this.tableData = []
              this.echartsDayData.paramData = []
              this.tableMaxData = []
              this.echartsMaxData.maxInfo = []
              this.echartsMaxData.avgInfo = []
              this.echartsMaxData.minInfo = []
              this.$modal.msgWarning("当前查询条件无支路")
              return
            }
          })
        }
      },

      //查询电表下拉框
      getCheckMeterList() {
        getCheckMeterList(this.queryForm).then(response => {
          if (response.code == 200) {
            //电表下拉框赋值
            this.metersList = response.data
            this.queryForm.meterId = this.metersList[0].id
            this.queryForm.meterType = this.metersList[0].type
            //第三方设备绑定的数据项
            this.queryForm.electricParam = this.metersList[0].electricParam
            //查询采集参数下拉框
            this.getMeterParams()
          }
        });
      },

      //查询采集参数下拉框
      getMeterParams() {
        getCheckMeterParamsList(this.queryForm).then(response => {
          if (response.code == 200) {
            if (response.data != null && response.data.length > 0) {
              //采集参数下拉框赋值
              this.parametersList = response.data
              this.queryForm.paramsId = this.parametersList[0].id
              //清空逐日极值分项
              this.metersParamList = []
              //清空日原始数据表格
              this.tableColumnIteam = []
              //选中的逐日极值数据ID
              this.queryForm.paramsItemId = ''
              //选中的逐日极值数据NAME
              this.paramsName = ''
              response.data.forEach(iteam => {
                //是否选中采集参数
                if (this.queryForm.paramsId == null || this.queryForm.paramsId == undefined || this.queryForm.paramsId == '' || this.queryForm.paramsId == '0') {
                  //去掉全部选项
                  if (iteam.id != '0') {
                    //逐日极值采集参数分项列表
                    this.metersParamList.push(iteam)
                    //日原始数据表格
                    this.tableColumnIteam.push(iteam)
                    if (this.queryForm.paramsItemId == '' || this.paramsName == '') {
                      //逐日极值采集参数分项 默认选中
                      this.queryForm.paramsItemId = iteam.id
                      //逐日极值表格 默认选中
                      this.paramsName = iteam.name
                    }
                  }
                } else {
                  if (iteam.id == this.queryForm.paramsId) {
                    //逐日极值采集参数分项列表
                    this.metersParamList.push(iteam)
                    //日原始数据表格
                    this.tableColumnIteam.push(iteam)
                    //逐日极值采集参数分项 默认选中
                    this.queryForm.paramsItemId = iteam.id
                    //逐日极值表格 默认选中
                    this.paramsName = iteam.name
                  }
                }
              })
            } else {
              this.parametersList = []
              this.queryForm.paramsId = null
              return;
            }
            //查询日原始表格和逐日极值tab
            this.queryData()
            this.queryMaxData()
            this.timerQuery()
          }
        });
      },

      //切换采集参数
      queryMeterparams(value) {
        if (this.queryForm.branchId == null || this.queryForm.branchId == '') {
          this.$modal.msgWarning("请选择支路")
          return
        }
        if (this.queryForm.meterId == null || this.queryForm.meterId == '') {
          this.$modal.msgWarning("请选择电表")
          return
        }
        this.tableColumnIteam = []
        //修改日原始数据表格数据
        if(value == '0'){
          this.parametersList.forEach(item=>{
            if(item.id != '0'){
              this.tableColumnIteam.push(item)
              //逐日极值采集参数分项 默认选中
              this.queryForm.paramsItemId = item.id
              //逐日极值表格 默认选中
              this.paramsName = item.name
            }
          })
        }else{
          this.parametersList.forEach(item=>{
            if(item.id == value){
              this.tableColumnIteam.push(item)
              //逐日极值采集参数分项 默认选中
              this.queryForm.paramsItemId = item.id
              //逐日极值表格 默认选中
              this.paramsName = item.name
            }
          })
        }
        //查询右侧表格
        this.queryData()
        this.queryMaxData()
        this.timerQuery()

      },

      //切换电表
      changeMeterType(value) {
        this.metersList.forEach(item => {
          if (item.id == value) {
            this.queryForm.meterId = item.id
            this.queryForm.meterType = item.type
            this.queryForm.electricParam = item.electricParam
          }
        })
        this.getMeterParams()
      },

      //定时器一分钟查询一次
      timerQuery() {
        if (this.timerId != null) {
          clearInterval(this.timerId);
          this.timerId = null
        }
        this.timerId = setInterval(this.queryrightData, timer.timerTime)
      },
      queryrightData() {
        if (this.queryForm.branchId == null || this.queryForm.branchId == '') {
          this.$modal.msgWarning("请选择支路")
          return
        }
        if (this.queryForm.meterId == null || this.queryForm.meterId == '') {
          this.$modal.msgWarning("请选择电表")
          return
        }
        //查询右侧图表数据
        this.queryData()
        this.queryMaxData()
      },
      changeRadio(value) {
        let mapInfo = this.metersParamList.find((item) => {
          return item.id == value
        })
        if (mapInfo != null && typeof mapInfo != "undefined") {
          this.paramsName = mapInfo.name
          this.queryMaxData()
        }
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
        this.tableData = []
        this.echartsDayData.paramData = []
        this.tableMaxData = []
        this.echartsMaxData.maxInfo = []
        this.echartsMaxData.avgInfo = []
        this.echartsMaxData.minInfo = []
        this.parametersList = []
        this.metersList = []
        this.queryForm.meterId = ''
        this.queryForm.paramsId = ''
        this.getCheckMeterList()
      },
      handleClick(tab, event) {
        // console.log(tab, event);
      },
      queryAllData() {
        if (this.queryForm.branchId == null || this.queryForm.branchId == '') {
          this.$modal.msgWarning("请选择支路")
          return
        }
        if (this.queryForm.meterId == null || this.queryForm.meterId == '') {
          this.$modal.msgWarning("请选择电表")
          return
        }
        this.queryData()
        this.queryMaxData()
      },
      //日原始数据
      queryData() {
        //开始时间，结束时间
        if (this.startEndDate.length == 0) {
          this.$modal.msgWarning("请选择开始日期与结束日期")
          return
        }
        this.queryForm.startTime = this.startEndDate[0]
        this.queryForm.endTime = this.startEndDate[1]
        //组装采集参数
        let paramsIdStr = []
        if(this.queryForm.paramsId == '0'){
          //如果采集参数是全部则全选
          this.parametersList.forEach(iteam => {
            if(iteam.id != '0'){
              paramsIdStr.push(iteam.id)
            }
          })
        }else{
          //如果采集参数是单个则支选择单个参数
          this.parametersList.forEach(iteam => {
            if(this.queryForm.paramsId == iteam.id){
              paramsIdStr.push(iteam.id)
            }
          })
        }
        this.queryForm.paramsIdStr = paramsIdStr.join("-")
        //电表类型
        this.queryForm.meterType = this.parametersList[0].type
        //电表系统名称
        this.metersList.forEach(iteam => {
          if (iteam.id == this.queryForm.meterId) {
            this.queryForm.sysName = iteam.sysName
          }
        })
        //清空echarts数据
        this.echartsDayData.paramData = []
        //查询echarts数据
        queryData(this.queryForm).then(response => {
          if (response.data != null && response.data.length > 0) {
            this.echartsDayData.paramData = response.data
          } else {
            this.$modal.msgWarning("无数据")
          }
        })
        //查询表格数据
        queryDataTable(this.queryForm).then(response => {
          this.tableData = response.data.reverse();
        })
      },

      //逐日极值右侧数据
      queryMaxData() {
        if (this.startEndDate.length == 0) {
          this.$modal.msgWarning("请选择开始日期与结束日期")
          return
        }
        this.queryForm.startTime = this.startEndDate[0]
        this.queryForm.endTime = this.startEndDate[1]
        //系统名称
        this.metersList.forEach(iteam => {
          if (iteam.id == this.queryForm.meterId) {
            this.queryForm.sysName = iteam.sysName
          }
        })
        //电表类型
        this.queryForm.meterType = this.parametersList[0].type
        //清空echarts
        this.echartsMaxData.maxInfo = []
        this.echartsMaxData.minInfo = []
        this.echartsMaxData.avgInfo = []
        this.echartsMaxData.xData = []
        //查询echarts
        queryMaxData(this.queryForm).then(response => {
          var result = response.data
          if (result != null) {
            result.forEach(item => {
              this.echartsMaxData.xData.push(item.CJSJ)
              this.echartsMaxData.maxInfo.push(item.maxInfo)
              this.echartsMaxData.minInfo.push(item.minInfo)
              this.echartsMaxData.avgInfo.push(item.avgInfo)
            })
          } else {
            this.$modal.msgWarning("无数据")
          }
          this.tableMaxData = response.data.reverse();
        })
      },
      //导出表格
      dcDay() {
        if (this.tableData.length === 0) {
          this.$message({
            showClose: true,
            message: '暂无数据，导出失败',
            type: 'error',
            duration: 1500
          });
          return;
        }
        let str = '序号-采集时间'
        this.tableColumnIteam.forEach(iteam => {
          str = str + '-' + iteam.name
        });
        this.$modal.confirm('是否确认导出日原始数据？').then(() => {
          this.queryForm.tableColumnexport = str
          exportPowerTable(this.queryForm).then(response => {
            const blob = new Blob([response]);
            const fileName = '日原始数据.xls';
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
        this.$modal.confirm('是否确认导出逐日极值数据？').then(() => {
          this.queryForm.tableColumnexport = this.paramsName;
          exportMaxPowerTable(this.queryForm).then(response => {
            const blob = new Blob([response]);
            const fileName = '逐日极值数据.xls';
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
      showPartMethods(value) {
        this.showPart = value
      },

      tableHeader({row, rowIndex}) {
        return 'font-size:14px;color:#797979;background: #fff;'

      }
    },
  };
</script>
<style lang="scss" scoped>

</style>
