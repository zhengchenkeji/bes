<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
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

    <el-row :gutter="10" class="mb8">
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
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="warning"-->
<!--          plain-->
<!--          icon="el-icon-loading"-->
<!--          size="mini"-->
<!--          @click="handleHTTPTest"-->
<!--        >HTTP测试-->
<!--        </el-button>-->
<!--      </el-col>-->
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
      <el-table-column label="ip地址" align="center" prop="ipAddress"/>
      <el-table-column label="端口" align="center" prop="portNum"/>
      <el-table-column label="通讯协议" align="center" prop="communication"/>
      <el-table-column label="消息协议" align="center" prop="message"/>
      <!--      <el-table-column label="创建时间" align="center" prop="createTime"/>-->
      <!--      <el-table-column label="修改时间" align="center" prop="updateTime"/>-->
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" min-width="150">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.iotName == '直连设备'"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleUpdateInfo(scope.row)"
            v-hasPermi="['baseData:equipment:edit']"
          >查看
          </el-button>
          <el-button
            v-if="scope.row.iotName == '网关子设备'"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleUpdateSonInfo(scope.row)"
            v-hasPermi="['baseData:equipment:edit']"
          >查看
          </el-button>
          <el-button
            v-if="scope.row.iotName == '网关设备'"
            size="mini"
            type="text"
            icon="el-icon-setting"
            @click="handleUpdateSon(scope.row)"
            v-hasPermi="['baseData:equipment:edit']"
          >子设备({{scope.row.sonNum}})
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
          <el-select v-model="form.productId" filterable placeholder="请选择产品" style="width: 380px" clearable>
            <el-option
              v-for="item in productList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备编号" prop="code">
          <el-input v-model="form.code" placeholder="请输入设备编号"/>
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称"/>
        </el-form-item>
        <el-form-item label="网络类型" prop="networkType" v-if="form.pId == null">
          <el-radio v-model="form.networkType" label="0" style="width: 150px">客户端</el-radio>
          <el-radio v-model="form.networkType" label="1" style="width: 150px">服务器</el-radio>
        </el-form-item>
        <el-form-item label="ip地址" prop="ipAddress" v-if="form.pId == null">
          <el-input v-model="form.ipAddress" placeholder="请输入ip地址"/>
        </el-form-item>
        <el-form-item label="端口" prop="portNum" v-if="form.pId == null">
          <el-input v-model="form.portNum" placeholder="请输入端口"/>
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
  </div>
</template>

<script>
  import {
    listEquipment,
    getEquipment,
    delEquipment,
    addEquipment,
    updateEquipment,
    pollingEquipment,
    listAllPark,
  } from "@/api/basicData/deviceDefinition/equipment/equipment";
  import {listAllProduct, listCategory} from "@/api/basicData/deviceDefinition/product/product";
  import {getToken} from "@/utils/auth";
  import {mapState} from 'vuex'
  import axios from "axios";

  export default {
    name: "Equipment",
    data() {
      return {
        // 遮罩层
        loading: true,
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
        //园区
        parkList: [],
        // 物联设备表格数据
        equipmentList: [],
        productList: [],
        categoryList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          code: null,
          name: null,
          categoryId: null,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          parkCode: [
            {required: true, message: "园区不能为空", trigger: "change"}
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
          ipAddress: [
            {required: true, message: "ip地址不能为空", trigger: "blur"}
          ],
          portNum: [
            {required: true, message: "端口不能为空", trigger: "blur"}
          ],
          networkType: [
            {required: true, message: "请选择网络类型", trigger: "change"}
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
      };
    },
    created() {
      this.getProductList()
      this.getCategoryList()
      this.getList()
      this.getParkList()
    },
    computed: {
      ...mapState({
        modbusServerDeviceStstus : state => state.websocket.modbusServerDeviceStstus,
        modbusDeviceControllerStstus : state => state.websocket.modbusDeviceControllerStstus,

      }
      )
    },
    watch: {
      //modbus网关设备在线状态
      modbusDeviceControllerStstus(res) {
        let ip = res.ip;
        let post = res.post;
        let state = res.state;
        this.equipmentList.forEach(value => {
          if (value.ipAddress == ip && post == value.portNum) {
            value.state = state
          }
        })

      },
      //modbus设备在线状态

      modbusServerDeviceStstus(res){


        this.equipmentList.forEach(value => {
          console.log(value.id,res.data.id)
          if(value.id==res.data.id){
            console.log(res.state)
            value.state = res.state
          }

        })
      }
    },
    methods: {
      /** 跳转详情页 */
      handleUpdateInfo(row) {
        this.$router.push({
          name: 'equipmentInfo',
          params: {equipmentId: row.id, equipmentName: row.name, equipmentCode: row.code, haveSon: false,}
        })
      },
      /** 跳转带有子设备详情页面 */
      handleUpdateSon(row) {
        this.$router.push({
          name: 'equipmentInfo',
          params: {equipmentId: row.id, equipmentName: row.name, equipmentCode: row.code, haveSon: true,}
        })
      },
      /** 跳转子设备页面 */
      handleUpdateSonInfo(row) {
        this.$router.push({
          name: 'equipmentSonInfo',
          params: {equipmentIdNew: row.id, equipmentId: row.pId}
        })
      },
      /** 查询园区列表 */
      getParkList() {
        listAllPark().then(response => {
          this.parkList = response;
        });
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
        this.loading = true;
        listEquipment(this.queryParams).then(response => {
          this.equipmentList = response.data;
          this.total = response.total;
          this.loading = false;
        });
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
          parkCode: null,
          productId: null,
          code: null,
          name: null,
          state: null,
          ipAddress: null,
          portNum: null,
          networkType: null,
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
      /** 导出按钮操作 */
      handleExport() {
        this.download('baseData/equipment/export', {
          ...this.queryParams
        }, `物联设备_${new Date().getTime()}.xlsx`)
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
      // HTTP轮询测试
      handleHTTPTest() {
        axios({
          method: "get",
          url: "http://127.0.0.1:8080/HTTPCommunication/HttpPolling",
        })
      },
    }
  };
</script>
