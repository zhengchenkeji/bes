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
    <span style="font-size: 18px">{{this.equipmentName}}</span>
    &nbsp;&nbsp;
    <span style="font-size: 16px;color: #BABABA">{{this.equipmentCode}}</span>
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
          <el-descriptions-item label="通讯协议"> {{equipmentInfo.communication}}</el-descriptions-item>
          <el-descriptions-item label="数据接入"> {{equipmentInfo.dataAccessName}}</el-descriptions-item>
          <el-descriptions-item label="消息协议"> {{equipmentInfo.message}}</el-descriptions-item>
          <el-descriptions-item label="ip地址"> {{equipmentInfo.ipAddress}}</el-descriptions-item>
          <el-descriptions-item label="端口"> {{equipmentInfo.portNum}}</el-descriptions-item>
          <el-descriptions-item label="设备状态">
            <el-tag size="small" v-if="equipmentInfo.state == '0'" type="info">离线</el-tag>
            <el-tag size="small" v-if="equipmentInfo.state == '1'">在线</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间"> {{equipmentInfo.createTime}}</el-descriptions-item>
          <el-descriptions-item label="创建者"> {{equipmentInfo.createName}}</el-descriptions-item>
          <el-descriptions-item label="更新时间"> {{equipmentInfo.updateTime}}</el-descriptions-item>
          <el-descriptions-item label="更新者"> {{equipmentInfo.updateName}}</el-descriptions-item>
          <el-descriptions-item label="物模型"><span style="color: #1c92e0" @click="handleModel">点击查看物模型</span>
          </el-descriptions-item>
          <el-descriptions-item label="设备说明"> {{equipmentInfo.remark}}</el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
      <el-tab-pane label="子设备" name="second" v-if="haveSon">

        <br/>
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
                 label-width="68px" style="width: 97%">
          <el-form-item label="设备编号" prop="code">
            <el-input
              v-model="queryParams.code"
              placeholder="请输入设备编号"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="设备名称" prop="name">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入设备名称"
              clearable
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="品类" prop="categoryId">
            <el-select v-model="queryParams.categoryId" filterable placeholder="请选择品类" clearable>
              <el-option
                v-for="item in categoryList"
                :key="item.id"
                :label="item.categoryName"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8" style="width: 97%">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
              v-hasPermi="['baseData:equipment:add']"
            >新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="el-icon-edit"
              size="mini"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['baseData:equipment:edit']"
            >修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['baseData:equipment:remove']"
            >删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="info"
              plain
              icon="el-icon-upload2"
              size="mini"
              @click="handleExportIn"
              v-hasPermi="['baseData:equipment:export']"
            >导入
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              icon="el-icon-download"
              size="mini"
              @click="handleExport"
              v-hasPermi="['baseData:equipment:export']"
            >导出
            </el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="equipmentList" @selection-change="handleSelectionChange"
                  :header-cell-style="{background:'#FAFAFA'}">
          <el-table-column type="selection" width="55" align="center"/>
          <el-table-column label="设备编号" align="center" prop="code"/>
          <el-table-column label="设备名称" align="center" prop="name"/>
          <el-table-column label="设备状态" align="center" prop="state">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.state == '1'">在线</el-tag>
              <el-tag type="info" v-else>离线</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="设备地址" align="center" prop="sonAddress"/>
          <el-table-column label="离线报警" align="center" prop="">
            <template slot-scope="scope">
              <el-switch v-model="scope.row.offlineAlarm"
                         active-color="#13ce66"
                         inactive-color="#ff4949"
                         active-value="1"
                         inactive-value="0"
                         @change="changeAlarmSwtich($event,scope.row)">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime"/>
          <el-table-column label="修改时间" align="center" prop="updateTime"/>
          <el-table-column label="备注" align="center" prop="remark"/>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-view"
                @click="handleUpdateSonInfo(scope.row)"
                v-hasPermi="['baseData:equipment:edit']"
              >查看
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
                v-hasPermi="['baseData:equipment:edit']"
              >修改
              </el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['baseData:equipment:remove']"
              >删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />

        <!-- 添加或修改物联设备对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="园区" prop="parkCode">
              <el-select v-model="form.parkCode" filterable placeholder="请选择园区" style="width: 380px" clearable>
                <el-option
                  v-for="item in parkList"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="产品" prop="productId">
              <el-select v-model="form.productId" @change="productChange(form.productId)" filterable placeholder="请选择产品" style="width: 300px" clearable>
                <el-option
                  v-for="item in productList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
              <el-button
                style="margin-left: 0.3vw"
                type="primary"
                plain
                size="mini"
                @click="dataItemUpdateNameClick(form.productId)"
              >数据项
              </el-button>
            </el-form-item>
            <el-form-item label="设备编号" prop="code">
              <el-input v-model="form.code" placeholder="请输入设备编号"/>
            </el-form-item>
            <el-form-item label="设备名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入设备名称"/>
            </el-form-item>
            <el-form-item label="设备地址" prop="sonAddress">
              <el-input type="number" v-model="form.sonAddress" placeholder="请输入设备地址"/>
            </el-form-item>
            <el-form-item label="是否电表" prop="meterState">
              <el-radio v-model="form.meterState" label="0" style="width: 150px">否</el-radio>
              <el-radio v-model="form.meterState" label="1" style="width: 150px">是</el-radio>
            </el-form-item>
            <el-form-item label="离线报警" prop="offlineAlarm">
              <el-radio v-model="form.offlineAlarm" label="0" style="width: 150px">否</el-radio>
              <el-radio v-model="form.offlineAlarm" label="1" style="width: 150px">是</el-radio>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入备注"/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </el-dialog>

        <!--数据项重命名对话框-->
        <el-dialog :title="dataItemUpdateNameTitle" :visible.sync="dataItemUpdateNameOpen" width="600px" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="80px">
              <div style="margin: 10px" v-for="(item, index) in productItemDataList" :key="index">
                <el-input v-model="item.name" placeholder="数据项名称"
                          style="width: 180px;margin-left: 100px"></el-input>
                <span style="margin-left: 2px">:</span>
                <el-input v-model="item.itemDataCustomName" placeholder="重命名" style="width: 180px;margin-left: 2px"></el-input>
              </div>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="dataItemUpdateNameSubmitForm">确 定</el-button>
            <el-button @click="dataItemUpdateNameCancel">取 消</el-button>
          </div>
        </el-dialog>
        <!-- 导入对话框 -->
        <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
          <el-upload
            ref="upload"
            :limit="1"
            accept=".xlsx, .xls"
            :headers="upload.headers"
            :action="upload.url + '?updateSupport=' + upload.updateSupport"
            :disabled="upload.isUploading"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
            :auto-upload="false"
            drag
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip text-center" slot="tip">
              <span>仅允许导入xls、xlsx格式文件。</span>
              <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;"
                       @click="importTemplate">下载模板
              </el-link>
            </div>
          </el-upload>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitFileForm">确 定</el-button>
            <el-button @click="upload.open = false">取 消</el-button>
          </div>
        </el-dialog>
      </el-tab-pane>
      <el-tab-pane label="实时数据" name="third" v-if="!haveSon">
        <br/>
        <el-switch v-if="actualTimeList.length>0"
                   style="position:absolute;z-index: 10;top:30px;right:10px;"
                   v-model="swtichOn"
                   inactive-text="实时更新"
                   active-color="#13ce66"
                   inactive-color="#ff4949"
                   @change="changeSwtich">
        </el-switch>
        <el-descriptions title="" direction="vertical" :column="4" border v-loading="loadingItemData">
          <el-descriptions-item :label="item.label" v-for="(item,index) of actualTimeList">
            {{item.value}}&nbsp;{{item.unit}}
            <span style="float: right;">更新时间：{{item.time}}</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
      <el-tab-pane label="历史数据" name="fourth" v-if="!haveSon">
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
              end-placeholder="结束日期"
              :clearable="false">
            </el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="default" size="mini" @click="getHistoryList">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="loadingHistory" :data="historyDataList" :header-cell-style="{background:'#FAFAFA'}">
          <el-table-column :label="item.label" align="center" :prop="item.code"
                           v-for="(item,index) of actualTimeListTable"/>
        </el-table>
        <!--        <pagination v-show="totalHistory > 0" :total="totalHistory" :page.sync="queryParamsHistory.pageNum" :limit.sync="queryParams.pageSize"-->
        <!--                    @pagination="getHistoryList" />-->
      </el-tab-pane>
      <el-tab-pane label="报警历史" name="fifth" v-if="!haveSon">
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
    getEquipment, getEquipmentActualTimeList, getEquipmentActualTimeListPreserve,
    getEquipmentInfo,
    listEquipmentSon,
    updateEquipment,updateAthenaBesEquipmentOfflineAlarm,
    getHistoryList, listAllPark, getWarnDataList
  } from "@/api/basicData/deviceDefinition/equipment/equipment";
  import {listAllProduct, listCategory, listProductItemDataNoPaging} from "@/api/basicData/deviceDefinition/product/product";
  import {getToken} from "@/utils/auth";
  import {mapState} from 'vuex'
  import axios from "axios";
  import Template from "@/views/noticeManage/noticeTemplate/index";

  export default {
    name: "info",
    components: {Template},
    data() {
      return {
        //tabs默认项
        activeName: 'first',
        //设备名称
        equipmentName: null,
        //设备编码
        equipmentCode: null,
        //设备ID
        equipmentId: null,
        //是否有子设备
        haveSon: false,
        //设备详情数据
        equipmentInfo: {},
        // 遮罩层
        loading: true,
        loadingWarn: true,
        loadingHistory: true,
        loadingItemData: true,
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
        totalHistory: 0,
        //园区
        parkList: [],
        // 物联设备表格数据
        equipmentList: [],
        productList: [],
        categoryList: [],
        //实时数据
        actualTimeList: [],
        actualTimeListTable: [],
        //实时更新开关
        swtichOn: false,
        //历史数据
        historyDataList: [],
        //历史数据时间选择
        daterangeDate: [new Date().getFullYear() + "-" + (new Date().getMonth() + 1) + "-" + (new Date().getDate() - 1), new Date().getFullYear() + "-" + (new Date().getMonth() + 1) + "-" + new Date().getDate()],
        //报警历史
        warnDataList: [],
        // 弹出层标题
        title: "",
        //数据项重命名对话框标题
        dataItemUpdateNameTitle: '配置数据项名称',
        // 是否显示弹出层
        open: false,
        //数据项重命名对话框是否显示
        dataItemUpdateNameOpen: false,
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
          pId: null,
        },
        // 查询参数
        queryItemDataParams: {
          pageNum: 1,
          pageSize: 10,
          productId: null,
        },
        queryParamsHistory: {
          pageNum: 1,
          pageSize: 10,
          id: null,
          startDate: null,
          endDate: null,
        },
        queryParamsWarn: {
          id: null,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          parkCode: [
            {required: true, message: "请选择园区", trigger: "change"}
          ],
          productId: [
            {required: true, message: "产品不能为空", trigger: "blur"}
          ],
          code: [
            {required: true, message: "设备编号不能为空", trigger: "blur"}
          ],
          name: [
            {required: true, message: "设备名称不能为空", trigger: "blur"}
          ],
          sonAddress: [
            {required: true, message: "设备地址不能为空", trigger: "blur"}, {pattern: /^\+?[1-9][0-9]*$/, message: "只能输入正整数",}
          ],
          meterState: [
            {required: true, message: "请选择是否为电表", trigger: "change"}
          ],
        },
        // 导入参数
        upload: {
          // 是否显示弹出层（
          open: false,
          // 弹出层标题
          title: "",
          // 是否禁用上传
          isUploading: false,
          // 是否更新已经存在的数据
          updateSupport: 0,
          // 设置上传的请求头部
          headers: {Authorization: "Bearer " + getToken()},
          // 上传的地址
          url: process.env.VUE_APP_BASE_API + "/baseData/equipment/importData"
        },

        //产品所对应的数据项
        productItemDataList: [],
      }

    },
    created() {
      this.equipmentName = this.$route.params.equipmentName
      this.equipmentCode = this.$route.params.equipmentCode
      this.equipmentId = this.$route.params.equipmentId
      this.haveSon = this.$route.params.haveSon
      this.activeName = this.$route.params.activeName != undefined ? this.$route.params.activeName : 'first'
      this.getEquipmentInfo()
      this.getProductList()
      this.getCategoryList()
      this.getList()
      this.getParkList()
      this.getEquipmentActualTimeList()
      this.getHistoryList()
      this.getWarnDataList()
    },
    computed: {
      ...mapState({
        modbusServerDeviceStstus: state => state.websocket.modbusServerDeviceStstus,
      })
    },
    watch: {
      //modbus设备在线状态
      modbusServerDeviceStstus(res) {
        let data = res.data;
        let state = res.state;

        this.equipmentList.forEach(value => {
          if (value.id == data.id) {
            value.state = state.toString();
          }
        })
      }
    },
    methods: {
      /** 获取实时数据信息 */
      getEquipmentActualTimeList() {
        this.loadingItemData = true
        var addData = {"code": "time", "label": "时间"}
        getEquipmentActualTimeList(this.equipmentId).then(response => {
          console.log(response.data)
          this.actualTimeList = response.data;
          this.loadingItemData = false
        });
        getEquipmentActualTimeListPreserve(this.equipmentId).then(response => {
          response.data.unshift(addData);
          this.actualTimeListTable = response.data
        });
      },
      /** 获取历史数据信息 */
      getHistoryList() {
        this.loadingHistory = true;
        if (this.daterangeDate != null && this.daterangeDate != undefined && this.daterangeDate.length > 0) {
          this.queryParamsHistory.startDate = this.daterangeDate[0]
          this.queryParamsHistory.endDate = this.daterangeDate[1]
        } else {
          this.queryParamsHistory.startDate = null
          this.queryParamsHistory.endDate = null
        }
        this.queryParamsHistory.id = this.equipmentId
        getHistoryList(this.queryParamsHistory).then(response => {
          this.historyDataList = response.rows;
          this.totalHistory = response.total;
          this.loadingHistory = false;
        });
      },
      /** 获取报警历史信息 */
      getWarnDataList() {
        this.loadingWarn = true;
        this.queryParamsWarn.id = this.equipmentId
        getWarnDataList(this.queryParamsWarn).then(response => {
          this.warnDataList = response.rows
          this.loadingWarn = false;
        })
      },
      NullDataFormat(row) {
        if (row == null || row == undefined || row.value == undefined || row.value == null) {
          return 0;
        }
      },
      /** 跳转详情页 */
      handleUpdateSonInfo(row) {
        this.$router.push({
          name: 'equipmentSonInfo',
          params: {
            haveSon: this.haveSon,
            equipmentId: this.equipmentId,
            equipmentIdNew: row.id,
            equipmentName: this.equipmentName,
            equipmentCode: this.equipmentCode,
          }
        })
      },
      /** 获取设备详情 */
      getEquipmentInfo() {
        getEquipmentInfo(this.equipmentId).then(response => {
          this.equipmentInfo = response.data;
        });
      },
      /** 查询产品定义列表 */
      getProductList() {
        let param = {
          iotType: "1"
        }
        listAllProduct(param).then(response => {
          this.productList = response.data;
        });
      },
      /** 查询品类列表 */
      getCategoryList() {
        listCategory().then(response => {
          this.categoryList = response.data;
        });
      },
      /** 查询园区列表 */
      getParkList() {
        listAllPark().then(response => {
          this.parkList = response;
        });
      },
      /** 查询物联设备列表 */
      getList() {
        this.loading = true;
        if (this.equipmentName == null || this.equipmentName == undefined) {
          getEquipment(this.equipmentId).then(response => {
            this.equipmentName = response.data.name;
            this.equipmentCode = response.data.code;
            if (response.data.iotName != '网关设备') {
              this.haveSon = false
            } else {
              this.haveSon = true
            }

            this.queryParams.pId = this.equipmentId
            listEquipmentSon(this.queryParams).then(response => {
              this.equipmentList = response.data;
              this.total = response.total;
              this.loading = false;
            });
            this.getEquipmentActualTimeList()
          })

        } else {
          this.queryParams.pId = this.equipmentId
          listEquipmentSon(this.queryParams).then(response => {
            this.equipmentList = response.data;
            this.total = response.total;
            this.loading = false;
          });
        }

      },
      // 取消按钮
      cancel() {
        this.open = false;
        //数据项模态框清空数据
        this.productItemDataList.length = 0;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          id: null,
          parkCode: null,
          productId: null,
          code: null,
          name: null,
          state: null,
          ipAddress: null,
          sonAddress: null,
          meterState: null,
          portNum: null,
          remark: null,
          createTime: null,
          createName: null,
          updateTime: null,
          updateName: null,
          offlineAlarm: '1',
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
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
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
          this.getItemDataList(this.form.id, this.form.productId);
          this.open = true;
          this.title = "修改物联设备";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              this.form.itemDataList = this.productItemDataList;
              updateEquipment(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                //数据项模态框清空数据
                this.productItemDataList.length = 0;
                this.getList();
              });
            } else {
              this.form.pId = this.equipmentId
              this.form.itemDataList = this.productItemDataList;
              addEquipment(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                //数据项模态框清空数据
                this.productItemDataList.length = 0;
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
      /** 导出按钮操作 */
      handleExport() {
        this.download('baseData/equipment/export', {
          ...this.queryParams
        }, `物联设备_${new Date().getTime()}.xlsx`)
      },
      /** 返回按钮操作 */
      handleClose() {
        const obj = {
          name: 'equipment'
        }
        this.$tab.closeOpenPage(obj)
      },
      /*********************导入*******************/
      /** 导入按钮操作 */
      handleExportIn() {
        this.upload.title = "物联设备导入";
        this.upload.open = true;
      },
      /** 下载模板操作 */
      importTemplate() {
        this.download('baseData/equipment/exportModel', {
          ...this.queryParams
        }, `物联设备定义模板.xlsx`)
      },
      // 文件上传中处理
      handleFileUploadProgress(event, file, fileList) {
        this.upload.isUploading = true;
      },
      // 文件上传成功处理
      handleFileSuccess(response, file, fileList) {
        this.upload.open = false;
        this.upload.isUploading = false;
        this.$refs.upload.clearFiles();
        this.$alert(response.msg, "导入结果", {dangerouslyUseHTMLString: true});
        this.getList();
      },
      // 提交上传文件
      submitFileForm() {
        this.$refs.upload.submit();
      },
      //查看物模型
      handleModel() {
        getEquipmentInfo(this.equipmentId).then(response => {
          let modelVO = response.data;
          this.title = '设备物模型'
          this.jsonModel = JSON.stringify(modelVO, null, '\t')
          this.openModel = true
        });
      },
      changeAlarmSwtich(value, data) {
        this.reset()
        this.form.id = data.id
        this.form.offlineAlarm = value
        updateAthenaBesEquipmentOfflineAlarm(this.form).then(response => {
          this.$modal.msgSuccess("修改成功");
        });
      },
      //实时更新
      changeSwtich() {
        //推上来的数据实时展示

      },

      //点击数据项按钮弹出数据项重命名弹窗
      dataItemUpdateNameClick(productId) {
        if (productId == null) {
          this.$modal.msgWarning("请先选择产品");
          return
        }
        if (this.productItemDataList.length == 0) {
          this.$modal.msgWarning("当前产品未配置数据项");
          return
        }

        this.dataItemUpdateNameOpen = true;
      },
      //获取产品所对应的数据项
      getItemDataList(equipmentId, productId) {

        this.queryItemDataParams.productId = productId;
        this.queryItemDataParams.equipmentId = equipmentId;
        listProductItemDataNoPaging(this.queryItemDataParams).then(response => {
          if (response.data.length > 0) {
            for (let i = 0; i < response.data.length; i++) {
              let param = {};
              param.itemDataId = response.data[i].id;
              param.name = response.data[i].name;
              param.itemDataCustomName = response.data[i].itemDataCustomName;
              this.productItemDataList.push(param);
            }
          }
        });
      },
      //关闭数据项重命名弹窗
      dataItemUpdateNameCancel() {
        this.dataItemUpdateNameOpen = false;
      },
      //配置数据项提交逻辑
      dataItemUpdateNameSubmitForm() {
        this.dataItemUpdateNameOpen = false;
        // addEquipment(this.form).then(response => {
        //   this.$modal.msgSuccess("新增成功");
        //   this.open = false;
        // });
      },
      productChange(productId) {
        this.productItemDataList.length = 0;
        this.getItemDataList(null,productId);
      }
    }
  }
</script>

<style scoped>

</style>
