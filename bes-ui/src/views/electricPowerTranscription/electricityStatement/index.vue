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

        </el-form>
        <el-input
          placeholder="搜索支路"
          @input="filter"
          size="small"
          v-model="filterTree" style="margin-top: 10px">
        </el-input>
        <div style="margin-top: 10px ">
          <el-checkbox v-model="selectAll">全选</el-checkbox>
          <el-checkbox v-model="cascade">是否级联</el-checkbox>
        </div>
        <el-tree :data="branchList"  node-key="id"
                 :check-strictly="!cascade"
                 show-checkbox
                 :filter-node-method="filterNode"
                 @check="treeNodeClick"
                 ref="tree" style="margin-top: 10px">
        </el-tree>
      </el-col>
      <el-col :span="19" style="height: 100%;padding: 10px;">
        <el-form :model="queryForm" ref="queryForm" inline size="small" >
          <el-form-item prop="date">
            <el-date-picker
              v-model="queryForm.date"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="选择日期">
            </el-date-picker>
          </el-form-item>
          <el-form-item prop="time">
            <el-time-select
              v-model="queryForm.time"
              :picker-options="{
                start: '00:00',
                step: '01:00',
                end: '23:00'
              }"
              placeholder="选择时间">
            </el-time-select>
          </el-form-item>
          <el-form-item >
            <el-button type="primary" icon="el-icon-search" size="mini" @click="queryData">查询</el-button>
            <el-button type="primary" icon="el-icon-link" size="mini" @click="exportTable">导出</el-button>
          </el-form-item>
        </el-form>

        <el-table  :data="table.dataList" height="90%" style="overflow-y: auto" :header-cell-style="{background:'#01ada8',color:'#FFFFFF'}">
          <el-table-column  label="支路名称" align="center" prop="branchName"/>
          <el-table-column  label="电表名称" align="center" prop="meterName"/>
          <template v-for="item in table.column">
            <el-table-column
              :key="item.name"
              :label="item.name"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{ scope.row[item.code] }}</span>
              </template>
            </el-table-column>
          </template>
        </el-table>


      </el-col>

    </el-row>


  </div>
</template>

<script>
    import {getAllPark,getEnergyType,queryData,exportTable} from "@/api/electricPowerTranscription/electricityStatement/electricityStatement"
    import {
        treeSelect
    } from "@/api/basicData/energyInfo/branchConfig/config";
    import {parseTimeFormat} from "../../../utils/date";

    export default {
        name: "electricityStatement",
        data() {
            return {
                queryForm:{
                    parkCode:'', //园区
                    branchId:[], //支路id
                    branchName:'', //支路名称 用于展示
                    date: parseTimeFormat(new Date(), '{y}-{m}-{d}'),
                    time:'00:00',
                },
                parkList:[],


                filterTree:'',
                branchList:[],
                //是否全选
                selectAll: false,
                //是否级联
                cascade: true,



                table:{
                    dataList:[],
                    column:[],
                },


            };
        },
        created() {
            //查询所有园区
            this.getAllPark()
            //获取能源类型
            this.getEnergyType()
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

            //获取支路
            getBranchList(){
                if (this.queryForm.parkCode == ''){
                    this.branchList = []
                    this.queryForm.branchId = ''
                    this.queryForm.branchName = ''
                } else {
                    treeSelect({
                        parkCode:this.queryForm.parkCode,
                        energyCode:'01000'
                    }).then(response => {
                        this.branchList = response.data
                    }).then(res =>{
                        if (this.branchList.length > 0){
                            this.$refs.tree.setCheckedKeys([this.branchList[0].id])
                            this.treeNodeClick()
                        } else {
                            this.queryForm.branchId = ''
                            this.queryForm.branchName = ''
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
            treeNodeClick(){
                this.queryForm.branchId = this.$refs.tree.getCheckedKeys()
            },


            queryData(){
                this.table.column = []
                this.table.dataList = []

                if (this.queryForm.branchId.length < 1 ){
                    return this.$modal.msgError("请选择支路");
                } else if (this.queryForm.date == null || this.queryForm.date == ''){
                    return this.$modal.msgError("请选择日期");
                } else if (this.queryForm.time == null || this.queryForm.time == ''){
                    return this.$modal.msgError("请选择时间");
                }
                queryData({
                    branchIds:this.queryForm.branchId,
                    dateTime:this.queryForm.date + ' ' + this.queryForm.time
                }).then(response => {
                    var result = response.data
                    //表格数据
                    this.table.dataList = result.resultData
                    this.table.column = result.column

                })
            },
            exportTable(){
                if (this.queryForm.branchId.length < 1 ){
                    return this.$modal.msgError("请选择支路");
                } else if (this.queryForm.date == null || this.queryForm.date == ''){
                    return this.$modal.msgError("请选择日期");
                } else if (this.queryForm.time == null || this.queryForm.time == ''){
                    return this.$modal.msgError("请选择时间");
                }
                exportTable({
                    branchIds:this.queryForm.branchId,
                    dateTime:this.queryForm.date + ' ' + this.queryForm.time
                }).then(response => {
                    const blob = new Blob([response]);
                    const fileName = '电力集抄.xls';
                    const elink = document.createElement('a');// 创建a标签
                    elink.download = fileName;// 为a标签添加download属性
                    // a.download = fileName; //命名下载名称
                    elink.style.display = 'none';
                    elink.href = URL.createObjectURL(blob);
                    document.body.appendChild(elink);
                    elink.click();// 点击下载
                    URL.revokeObjectURL(elink.href); // 释放URL 对象
                    document.body.removeChild(elink);// 释放标签
                })
            },

            tableHeader ({row,rowIndex}){
                return 'font-size:14px;color:#797979;background: #fff;'

            }
        },
        watch: {
            //监听全选
            selectAll() {
                if (this.selectAll) {
                    this.cascade = true
                    //全选
                    this.$nextTick(() => {
                        this.$refs.tree.setCheckedNodes(this.branchList);
                        this.treeNodeClick();
                    })
                } else {
                    //取消
                    this.$refs.tree.setCheckedKeys([]);
                    this.treeNodeClick();
                }
            },
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
