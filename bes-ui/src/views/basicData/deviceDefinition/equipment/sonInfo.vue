<template>
  <div class="app-container">
    <span @click="handleClose()" style="font-size: 15px">
      <i class="el-icon-back"></i>
      返回
    </span>
    &nbsp;&nbsp;
    <span style="color: #BABABA">
        |
    </span>
    &nbsp;&nbsp;
    <span style="font-size: 18px">{{this.equipmentNameNew}}</span>
    &nbsp;&nbsp;
    <span style="font-size: 16px;color: #BABABA">{{this.equipmentCodeNew}}</span>
    <br/>
    <br/>
    <el-tabs v-model="activeName" style="height: 80.4vh;">
      <el-tab-pane label="设备详情" name="first">
        <br/>
        <el-descriptions title="" direction="vertical" :column="4" border>
          <el-descriptions-item label="设备编号"> {{equipmentInfo.code}}</el-descriptions-item>
          <el-descriptions-item label="设备名称"> {{equipmentInfo.name}}</el-descriptions-item>
          <el-descriptions-item label="品类"> {{equipmentInfo.categoryName}}</el-descriptions-item>
          <el-descriptions-item label="所属产品"> {{equipmentInfo.productName}}</el-descriptions-item>
          <el-descriptions-item label="物联类型"> {{equipmentInfo.iotName}}</el-descriptions-item>
          <el-descriptions-item label="网关/模块通讯协议"> {{equipmentInfo.communicationParent}}</el-descriptions-item>
          <el-descriptions-item label="设备接入"> {{equipmentInfo.message}}</el-descriptions-item>
          <el-descriptions-item label="网关/模块数据接入"> {{equipmentInfo.dataAccessName}}</el-descriptions-item>
          <el-descriptions-item label="网关/模块消息协议"> {{equipmentInfo.messageParent}}</el-descriptions-item>
          <el-descriptions-item label="网关/模块ip"> {{equipmentInfo.ipAddressParent}}</el-descriptions-item>
          <el-descriptions-item label="网关/模块端口"> {{equipmentInfo.portNumParent}}</el-descriptions-item>
          <el-descriptions-item label="设备状态">
            <el-tag size="small" v-if="equipmentInfo.state == '0'" type="info">离线</el-tag>
            <el-tag size="small" v-if="equipmentInfo.state == '1'">在线</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间"> {{equipmentInfo.createTime}}</el-descriptions-item>
          <el-descriptions-item label="创建者"> {{equipmentInfo.createName}}</el-descriptions-item>
          <el-descriptions-item label="更新时间"> {{equipmentInfo.updateTime}}</el-descriptions-item>
          <el-descriptions-item label="更新者"> {{equipmentInfo.updateName}}</el-descriptions-item>
          <el-descriptions-item label="物模型"><span style="color: #1c92e0" @click="handleModel">点击查看物模型</span></el-descriptions-item>
          <el-descriptions-item label="设备说明"> {{equipmentInfo.remark}}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
      <el-tab-pane label="实时数据" name="second">
        <br/>
        <el-switch v-if="actualTimeList.length>0"
                   style="position:absolute;z-index: 10;top:30px;right:10px;"
                   v-model="swtichOn"
                   inactive-text="实时更新"
                   active-color="#13ce66"
                   inactive-color="#ff4949"
                   @change="changeSwtich">
        </el-switch>
        <el-descriptions title="" direction="vertical" :column="4" border>
          <el-descriptions-item :label="item.label" v-for="(item,index) of actualTimeList">
            {{item.value}}&nbsp;{{item.unit}}
            <span style="float: right;">更新时间：{{item.time}}</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
      <el-tab-pane label="历史数据" name="third">
        <br/>
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
                 label-width="68px">
          <el-form-item>
            <el-date-picker
              size="mini"
              v-model="daterangeDate"
              value-format="yyyy-MM-dd"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="default" size="mini" @click="getHistoryList">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="loadingHistory" :data="historyDataList" :header-cell-style="{background:'#FAFAFA'}">
          <el-table-column :label="item.label" align="center" :prop="item.code" v-for="(item,index) of actualTimeListTable"/>
        </el-table>
        <!--<pagination v-show="totalHistory > 0" :total="totalHistory" :page.sync="queryParamsHistory.pageNum" :limit.sync="queryParams.pageSize"
                    @pagination="getHistoryList" />-->
      </el-tab-pane>
      <el-tab-pane label="报警历史" name="fourth">
        <br/>
        <el-table v-loading="loadingWarn" :data="warnDataList" :header-cell-style="{background:'#FAFAFA'}">
          <el-table-column label="预警类型" align="center" prop="earlyWarnType"/>
          <el-table-column label="发生时间" align="center" prop="happenTime"/>
          <el-table-column label="恢复时间" align="center" prop="resumeTime"/>
          <el-table-column label="告警状态" align="center" prop="warnType"/>
          <el-table-column label="告警名称" align="center" prop="warnName"/>
          <el-table-column label="告警内容" align="center" prop="warnInfo"/>
          <el-table-column label="数据项名称" align="center" prop="itemDataName"/>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <el-dialog :title="title" :visible.sync="openModel" width="520px" append-to-body>
      <el-input v-model="jsonModel" autosize type="textarea" readonly/>
    </el-dialog>
  </div>
</template>

<script>
  import {
    addEquipment, delEquipment,
    getEquipment,
    getEquipmentInfo,
    getEquipmentActualTimeList,getEquipmentActualTimeListPreserve,
    listEquipment,
    updateEquipment, listEquipmentSon, getHistoryList, getWarnDataList,
  } from "@/api/basicData/deviceDefinition/equipment/equipment";
  import {listAllProduct, listCategory} from "@/api/basicData/deviceDefinition/product/product";
  import {mapState} from 'vuex'
  import axios from "axios";

  export default {
    name: "info",
    data() {
      return {
        //tabs默认项
        activeName: 'first',
        //设备名称
        equipmentName: null,
        equipmentNameNew: null,
        //设备编码
        equipmentCode: null,
        equipmentCodeNew: null,
        //设备ID
        equipmentId: null,
        equipmentIdNew: null,
        //是否有子设备
        haveSon: false,
        //实时更新开关
        swtichOn: false,
        //设备详情数据
        equipmentInfo: {},
        //实时数据
        actualTimeList: [/*{value:'11.45',label:"正向有功",code:'zxyg',unit:'kWh',time:'2023-02-28 10:05:00'},
          {value:'11.45',label:"反向有功",code:'fxyg',unit:'kWh',time:'2023-02-28 10:05:00'},
          {value:'11.45',label:"环境温度",code:'hjwd',unit:'℃',time:'2023-02-28 10:05:00'},
          {value:'11.45',label:"环境湿度",code:'dhsd',unit:'％',time:'2023-02-28 10:05:00'},
          {value:'11.45',label:"电流",code:'dl',unit:'A',time:'2023-02-28 10:05:00'},
          {value:'11.45',label:"电压",code:'dy',unit:'V',time:'2023-02-28 10:05:00'}*/],
        actualTimeListTable: [],
        // 遮罩层
        loading: false,
        loadingWarn: false,
        loadingHistory: false,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        totalHistory:0,
        // 物联设备表格数据
        equipmentList: [],
        productList: [],
        categoryList: [],
        historyDataList: [],
        warnDataList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        //物模型
        openModel: false,
        jsonModel: '',
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          code: null,
          name: null,
          categoryId: null,
        },
        daterangeDate: [new Date().getFullYear() + "-" + (new Date().getMonth() + 1) + "-" + (new Date().getDate() - 1),new Date().getFullYear() + "-" + (new Date().getMonth() + 1) + "-" + new Date().getDate()],
        queryParamsHistory: {
          pageNum: 1,
          pageSize: 10,
          id: null,
          startDate: null,
          endDate: null,
        },
        queryParamsWarn:{
          id: null,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          productId: [
            {required: true, message: "产品不能为空", trigger: "blur"}
          ],
          code: [
            {required: true, message: "设备编号不能为空", trigger: "blur"}
          ],
          name: [
            {required: true, message: "设备名称不能为空", trigger: "blur"}
          ],
          ipAddress: [
            {required: true, message: "ip地址不能为空", trigger: "blur"}
          ],
          portNum: [
            {required: true, message: "端口不能为空", trigger: "blur"}
          ],
        }
      }

    },
    created() {
      this.equipmentName = this.$route.params.equipmentName
      this.equipmentCode = this.$route.params.equipmentCode
      this.equipmentId = this.$route.params.equipmentId
      this.haveSon = this.$route.params.haveSon
      this.equipmentIdNew = this.$route.params.equipmentIdNew
      this.getEquipmentInfo()
      this.getEquipmentActualTimeList()
      this.getProductList()
      this.getCategoryList()
      this.getList();
      this.getHistoryList();
      this.getWarnDataList()
    },
    computed: {
      ...mapState({
        modbusServerDeviceRealTimeData: state => state.websocket.modbusServerDeviceRealTimeData,
      })
    },
    watch: {
      //modbus设备在线状态
      modbusServerDeviceRealTimeData(res) {
        if (!this.swtichOn) {
          return
        }
        if (res.length > 0) {
          this.actualTimeList.forEach(value => {
            res.forEach(value1 => {
              if (value.code == value1.dataItemNum) {
                value.value = value1.dataValue;
                value.time = this.getRealTime();
              }
            })
          })
        }
      }
    },
    methods: {
      //格式化时间
      getRealTime() {
        let myDate = new Date();	//创建Date对象
        let Y = myDate.getFullYear();   //获取当前完整年份
        let M = myDate.getMonth() + 1;  //获取当前月份
        let D = myDate.getDate();   //获取当前日1-31
        let H = myDate.getHours();  //获取当前小时
        let i = myDate.getMinutes();    //获取当前分钟
        let s = myDate.getSeconds();    //获取当前秒数
        // 月份不足10补0
        if(M < 10){
          M = '0' + M;
        }
        // 日不足10补0
        if(D < 10){
          D = '0' + D;
        }
        // 小时不足10补0
        if(H < 10){
          H = '0' + H;
        }
        // 分钟不足10补0
        if(i < 10){
          i = '0' + i;
        }
        // 秒数不足10补0
        if(s < 10){
          s = '0' +s;
        }
        // 拼接日期分隔符根据自己的需要来修改
        let nowDate = Y+'-'+M+'-'+D+' '+H+':'+i+':'+s;
        return nowDate;
      },
      /** 获取设备详情 */
      getEquipmentInfo() {
        getEquipmentInfo(this.equipmentIdNew).then(response => {
          this.equipmentInfo = response.data;
        });
      },
      /** 获取实时数据信息 */
      getEquipmentActualTimeList() {
        // getEquipmentActualTimeList(this.equipmentIdNew).then(response => {
        //   this.actualTimeList = response.data;
        // });
        var addData = {"code": "time", "label": "时间"}
        getEquipmentActualTimeList(this.equipmentIdNew).then(response => {
          this.actualTimeList = response.data;
        });
        getEquipmentActualTimeListPreserve(this.equipmentId).then(response => {
          response.data.unshift(addData);
          this.actualTimeListTable = response.data
        });
      },
      /** 获取历史数据信息 */
      getHistoryList(){
        this.loadingHistory = true;
        if(this.daterangeDate != null && this.daterangeDate != undefined && this.daterangeDate.length>0){
          this.queryParamsHistory.startDate = this.daterangeDate[0]
          this.queryParamsHistory.endDate = this.daterangeDate[1]
        }else{
          this.queryParamsHistory.startDate = null
          this.queryParamsHistory.endDate = null
        }
        this.queryParamsHistory.id = this.equipmentIdNew
        getHistoryList(this.queryParamsHistory).then(response => {
          this.historyDataList = response.rows;
          this.totalHistory = response.total;
          this.loadingHistory = false;
        });
      },
      getWarnDataList(){
        this.loadingWarn = true;
        this.queryParamsWarn.id = this.equipmentIdNew
        getWarnDataList(this.queryParamsWarn).then(response => {
          this.warnDataList = response.rows
          this.loadingWarn = false;
        })

      },
      /** 查询产品定义列表 */
      getProductList() {
        listAllProduct().then(response => {
          this.productList = response.data;
        });
      },
      /** 查询品类列表 */
      getCategoryList() {
        listCategory().then(response => {
          this.categoryList = response.data;
        });
      },
      /** 查询物联设备列表 */
      getList() {
        if (this.equipmentNameNew == null || this.equipmentNameNew == undefined) {
          getEquipment(this.equipmentIdNew).then(response => {
            this.equipmentNameNew = response.data.name;
            this.equipmentCodeNew = response.data.code;
            if (response.data.iotName != '网关设备') {
              this.haveSon = false
            } else {
              this.haveSon = true
            }
          })
        }
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          id: null,
          productId: null,
          code: null,
          name: null,
          state: null,
          ipAddress: null,
          portNum: null,
          remark: null,
          createTime: null,
          createName: null,
          updateTime: null,
          updateName: null
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加物联设备";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids
        getEquipment(id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改物联设备";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              updateEquipment(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addEquipment(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            }
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认删除物联设备编号为"' + ids + '"的数据项？').then(function () {
          return delEquipment(ids);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {
        });
      },
      /** 导入按钮操作 */
      handleExportIn() {

      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('baseData/equipment/export', {
          ...this.queryParams
        }, `物联设备_${new Date().getTime()}.xlsx`)
      },
      /** 返回按钮操作 */
      handleClose() {
        const obj = {
          name: 'equipmentInfo',
          params: {
            equipmentId: this.equipmentId,
            equipmentName: this.equipmentName,
            equipmentCode: this.equipmentCode,
            haveSon: true,
            activeName: 'second',
          }
        }
        this.$tab.closeOpenPage(obj)
      },
      //查看物模型
      handleModel() {
        getEquipmentInfo(this.equipmentIdNew).then(response => {
          let modelVO = response.data;
          this.title = '设备物模型'
          this.jsonModel = JSON.stringify(modelVO, null, '\t')
          this.openModel = true
        });
      },
      //实时更新
      changeSwtich(){
        // axios({
        //   method: "get",
        //   url: "http://127.0.0.1:8080/HTTPCommunication/HttpPolling",
        // }).then(response => {
        //   this.getEquipmentActualTimeList()
        //   this.getHistoryList()
        // })
      },
    }
  }
</script>

<style scoped>

</style>
