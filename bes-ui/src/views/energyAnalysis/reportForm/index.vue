<template>
  <div class="app-container">
    <el-row style="height: 86.2vh">
      <el-col :span="5" style="border-right: 3px #f6f6f6 solid;height: 100%;padding: 10px;overflow-y: auto">
        <el-form ref="form">
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
          <el-tree
            class="filter-tree"
            :default-expanded-keys="defaultExpandedKeys"
            :data="branchConfigData"
            node-key="id"
            @check="handleCheckChange"
            :check-strictly="!cascade"
            show-checkbox
            :filter-node-method="filterNode"
            ref="tree">
          </el-tree>

        </el-form>
      </el-col>

      <el-col :span="19" style="height: 100%;">
        <div  style="margin-left: 20px;">
        <el-form >
          <el-form-item label="选择日期:" prop="jobName">
            <el-date-picker style="width: 40%" :clearable="false"
              v-model="queryParams.time"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
             >
            </el-date-picker>
            <el-button type="primary" @click="getReportFormList"  icon="el-icon-search" style="margin-left: 10px">搜索</el-button>
            <el-button type="primary" @click="handleExport"><i class="el-icon-download el-icon--left"></i>导出</el-button>
<!--            <el-date-picker-->
<!--              v-model="queryParams.time"-->
<!--              type="datetimerange"-->
<!--              range-separator="至"-->
<!--              start-placeholder="开始日期"-->
<!--              end-placeholder="结束日期">-->
<!--            </el-date-picker>-->
          </el-form-item>
        </el-form>
        <el-table
          v-loading="loading"
          :header-cell-style="{background:'#01ada8',color:'#FFFFFF'}"
          :data="tableData"
          border
          style="width: 100%">
          <el-table-column
            prop="energyname"
            label="能源名称"
            width="180">
          </el-table-column>
          <el-table-column
            prop="startTime"
            label="起始时间"
            width="180">
          </el-table-column>
          <el-table-column
            prop="startValue"
            label="起始数值()"
            v-bind:label="startLabel"
          >
          </el-table-column>
          <el-table-column
            prop="endTime"
            label="截止时间"
            width="180">
          </el-table-column>
          <el-table-column
            prop="endValue"
            v-bind:label="endLabel"
            label="截止数值">
          </el-table-column>
          <el-table-column
            prop="difference"
            label="差值"
            v-bind:label="differenceLabel"
            width="180">
          </el-table-column>
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
  } from "@/api/basicData/energyInfo/branchConfig/config";

  import {
    getReportFormList
  } from "@/api/energyAnalysis/roportForm/reportForm";
  import {param} from "../../../utils";
  import {timer} from "@/api/tool/timer";

  export default {
    name: "index",
    data() {
      return {
        startLabel:"",
        endLabel:"",
        differenceLabel:"",
        tableData: [],
        loading:false,
        time:null,
        //默认展开的数组
        defaultExpandedKeys: [],
        selectAll: false,
        cascade: false,
        filterText: '',
        //定时器
        timer: null,
        //能源列表
        energyList: [],
        //园区列表
        parkList: [],
        branchConfigData: [],
        // 查询参数
        queryParams: {
          energyCode: null,
          parkCode: "",
          branchIds: [20],
          time:  null,
        },
      };
    },
    created() {
      var now=new Date();
      // this.$set(this.queryParams,'time',[new Date(now.getFullYear(), now.getMonth(), now.getDate()), new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59,59)]);
      this.queryParams.time=[new Date(now.getFullYear(), now.getMonth(), now.getDate()), new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59,59)]
      this.getParkAndEnergy();

      this.timer = setInterval(this.getReportFormList, timer.timerTime);
      // this.getReportFormList()
      // this.getTreeselect();
    },
    beforeDestroy() {
      if (this.timer) { //如果定时器还在运行 或者直接关闭，不用判断
        clearInterval(this.timer); //关闭
      }
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      },
      selectAll() {
        // console.log(this.$refs.tree.getCheckedKeys())
        if (this.selectAll) {

          this.cascade = true
          //全选
          this.$nextTick(() => {
            this.$refs.tree.setCheckedNodes(this.branchConfigData);
            this.queryParams.branchIds=this.$refs.tree.getCheckedKeys();
            this.getReportFormList();
          })
        } else {
          //取消
          this.$refs.tree.setCheckedKeys([]);
          this.queryParams.branchIds=this.$refs.tree.getCheckedKeys();
          this.getReportFormList();
        }
      }
    },
    methods: {
      /**表单重置*/
      reset(){
        this.selectAll=false;
        this.cascade=false;
        this.filterText="";

      },
      // timechange(){
      //   this.getReportFormList();
      // },
      handleCheckChange(data, checked) {
        this.queryParams.branchIds=checked.checkedKeys;
        this.getReportFormList();
      },
      /******获取报表数据*******/
      getReportFormList(){
        const that=this;
        if(this.queryParams.time==null||this.queryParams.time==""){
          this.$alert('日期不允许为空！', '错误', {
            confirmButtonText: '确定',
            callback: action => {
              var now=new Date();
              that.queryParams.time=[new Date(now.getFullYear(), now.getMonth(), now.getDate()), new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59,59)]
              return;
            }
          });
        }else{

          this.loading=true;

        var params={
          time:this.queryParams.time+"",
          branchIds:this.queryParams.branchIds+""
        }
        // params.branchIds=params.branchIds+"";
        // params.time=params.time+"";
          getReportFormList(params).then(response=>{
            this.tableData=response.data;
            this.loading=false;
          })
        }
      },
      /******园区下拉框*******/
      byParkGetData() {
        this.listType();
        // this.getTreeselect()

        this.reset();
      },
      /*******能源下拉框******/
      byEnergyGetData() {
        this.getTreeselect()
        this.reset();

      },
      /** 查询支路拓扑配置下拉树结构 */
      async getTreeselect() {
        this.tableData=[];
        this.changeTableLabel();

        // treeSelect(this.queryParams).then(response => {
        //   this.branchConfigData = response.data;
        //   if (this.branchConfigData && this.branchConfigData.length > 0 &&
        //     this.branchConfigData[0].children && this.branchConfigData[0].children.length > 0
        //     && this.branchConfigData[0].children[0].id) {
        //     this.defaultExpandedKeys = []
        //     this.defaultExpandedKeys.push(this.branchConfigData[0].children[0].id);
        //   }
        // });
         await  treeSelect(this.queryParams).then(response => {
            this.branchConfigData = response.data;
           this.queryParams.branchIds=[];
           if (this.branchConfigData && this.branchConfigData.length > 0) {
              this.defaultExpandedKeys = []
              this.defaultExpandedKeys.push(this.branchConfigData[0].id);
              this.$nextTick(()=>{
                this.$refs.tree.setCheckedKeys(this.defaultExpandedKeys);
                this.checkedKeys=this.$refs.tree.getCheckedNodes();
                this.queryParams.branchIds = this.defaultExpandedKeys;
                this.getReportFormList();
              })
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

            this.listType();


          } else {
            this.queryParams.parkCode = null
          }
        });


      },
      listType(){
        this.energyList=[];
        const energytypeQuery = {
          parkCode:  this.queryParams.parkCode
        };
        listType(energytypeQuery).then(response => {
          response.data.forEach(item => {
            this.energyList.push({value: item.code, label: item.name})
          });
          if (this.energyList.length > 0) {
            this.queryParams.energyCode = this.energyList[0].value

            this.getTreeselect()

          } else {
            this.queryParams.energyCode = null
          }
        });
      },
      /*****支路搜索*******/
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },
      /** 导出按钮操作 */
      handleExport() {

        var params={
          time:this.queryParams.time+"",
          branchIds:this.queryParams.branchIds+""
        }

        this.download('/energyAnalysis/reportForm/export', {
          ...params
        }, `集抄报表.xlsx`)
      },

      /**根据选择的能源类型处理表头*/
      changeTableLabel(){

        if(this.queryParams.energyCode=="01000"){
          this.startLabel="起始数值(kwh)";
          this.endLabel="截止数值(kwh)";
          this.differenceLabel="差值(kwh)";

        }else if(this.queryParams.energyCode=="02000"){

          this.startLabel="起始数值(m³)";
          this.endLabel="截止数值(m³)";
          this.differenceLabel="差值(m³)";
        }else if(this.queryParams.energyCode=="03000"){

          this.startLabel="起始数值(m³)";
          this.endLabel="截止数值(m³)";
          this.differenceLabel="差值(m³)";
        }else if(this.queryParams.energyCode=="04000"){
          this.startLabel="起始数值(m³)";
          this.endLabel="截止数值(m³)";
          this.differenceLabel="差值(m³)";

        }else if(this. queryParams.energyCode=="05000"){

          this.startLabel="起始数值(m³)";
          this.endLabel="截止数值(m³)";
          this.differenceLabel="差值(m³)";
        }else{
          this.startLabel="起始数值";
          this.endLabel="截止数值";
          this.differenceLabel="差值";
        }
      }
    },



  };
</script>
<style lang="scss" scoped>


</style>
