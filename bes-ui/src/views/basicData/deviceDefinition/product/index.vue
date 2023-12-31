<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="产品名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入产品名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品编号" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入产品编号"
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
      <el-form-item label="启用状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择启用状态" clearable>
          <el-option
            v-for="item in stateList"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="通讯协议" prop="communicationProtocol">
        <el-select v-model="queryParams.communicationProtocol" placeholder="请选择通讯协议" clearable>
          <el-option
            v-for="item in communicationProtocolList"
            :key="item.value"
            :label="item.label"
            :value="item.value">
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
          v-hasPermi="['baseData:product:add']"
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
          v-hasPermi="['baseData:product:edit']"
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
          v-hasPermi="['baseData:product:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['baseData:product:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange"
              :header-cell-style="{background:'#FAFAFA'}">
      <el-table-column type="selection" width="55" align="center"/>
      <!--      <el-table-column label="主键" align="center" prop="id" />-->
      <el-table-column label="产品名称" align="center" prop="name"/>
      <el-table-column label="产品编号" align="center" prop="code"/>
      <el-table-column label="品类" align="center" prop="categoryName"/>
      <el-table-column label="启用状态" align="center" prop="state">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.state == '1'">正常</el-tag>
          <el-tag type="info" v-else>停用</el-tag>
          <!--          <el-button size="mini" type="primary" v-if="scope.row.state == '1'">正常</el-button>-->
          <!--          <el-button size="mini" type="danger" v-else>停用</el-button>-->
        </template>
      </el-table-column>
      <el-table-column label="物联类型" align="center" prop="iotTypeName"/>
      <el-table-column label="通讯协议" align="center" prop="communicationProtocolName"/>
      <el-table-column label="消息协议" align="center" prop="messageProtocolName"/>
      <el-table-column label="产品描述" align="center" prop="productDescribe"/>
      <!--      <el-table-column label="创建人" align="center" prop="createName" />-->
      <!--      <el-table-column label="修改人" align="center" prop="updateName" />-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-setting"
            @click="handleSetting(scope.row)"
            v-hasPermi="['baseData:product:edit']"
          >配置
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['baseData:product:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['baseData:product:remove']"
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

    <!-- 添加或修改产品定义对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入产品名称"/>
        </el-form-item>
        <el-form-item label="产品编号" prop="code">
          <el-input v-model="form.code" placeholder="请输入产品编号"/>
        </el-form-item>
        <el-form-item label="品类" prop="categoryId">
          <el-select v-model="form.categoryId" filterable placeholder="请选择品类" style="width: 380px" clearable>
            <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="启用状态" prop="state">
          <el-radio v-model="form.state" label="0" style="width: 150px">停用</el-radio>
          <el-radio v-model="form.state" label="1" style="width: 150px">正常</el-radio>
        </el-form-item>
        <el-form-item label="物联类型" prop="iotType">
          <el-select v-model="form.iotType" placeholder="请选择物联类型" style="width: 380px" clearable>
            <el-option
              v-for="item in iotTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="通讯协议" prop="communicationProtocol">
          <el-select v-model="form.communicationProtocol" placeholder="请选择通讯协议" style="width: 380px"
                     @change="getMessageProtocolList" clearable>
            <el-option
              v-for="item in communicationProtocolList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据接入" prop="dataAccess" v-if="showDataAccess">
          <el-radio v-model="form.dataAccess" label="0" style="width: 150px">设备上传</el-radio>
          <el-radio v-model="form.dataAccess" label="1" style="width: 150px">轮询采集</el-radio>
        </el-form-item>
        <el-form-item label="消息协议" prop="messageProtocol" v-if="messageShow">
          <el-select v-model="form.messageProtocol" placeholder="请选择消息协议" style="width: 380px" clearable>
            <el-option
              v-for="item in messageProtocolList"
              :key="item.id"
              :label="item.agreementName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="产品描述" prop="productDescribe">
          <el-input v-model="form.productDescribe" placeholder="请输入产品描述"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listIotTypeId,
    listIotType,
    listCategory,
    listProduct,
    getProduct,
    delProduct,
    addProduct,
    updateProduct,
    getAllMessageIdList,
    getAllMessageList,
    getMessageListById
  } from "@/api/basicData/deviceDefinition/product/product";

  export default {
    name: "Product",
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
        // 产品定义表格数据
        productList: [],
        // 品类定义数据
        categoryList: [],
        //是都启用
        stateList: [{value: 0, label: '停用'}, {value: 1, label: '正常'}],
        // 物联类型数据
        iotTypeList: [],
        // 通讯协议数据
        communicationProtocolList: [],
        // 消息协议数据
        messageProtocolList: [],
        messageShow: true,
        showDataAccess: false,
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          name: null,
          code: null,
          state: null,
          categoryId: null,
          iotType: null,
          communicationProtocol: null,
          messageProtocol: null,
          productDescribe: null,
          createName: null,
          updateName: null
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          name: [{required: true, message: '请输入产品名称', trigger: 'blur'}],
          code: [{required: true, message: '请输入产品名称', trigger: 'blur'}],
          categoryId: [{required: true, message: '请选择品类', trigger: 'change'}],
          state: [{required: true, message: '请选择启用状态', trigger: 'change'}],
          iotType: [{required: true, message: '请选择物联类型', trigger: 'change'}],
          dataAccess:[{required: true, message: '请选择数据接入', trigger: 'change'}],
          // communicationProtocol: [{required: true, message: '请选择通讯协议', trigger: 'change'}],
          // messageProtocol: [{required: true, message: '请选择消息协议', trigger: 'change'}],
        }
      };
    },
    created() {
      this.getCommunicationProtocolList()
      this.getIotTypeList()
      this.getCategoryList()
      this.getList();
    },
    methods: {
      /** 查询通讯协议列表 */
      getCommunicationProtocolList() {
        let param = {
          dictType: 'communication_protocol_product'
        }
        getAllMessageList(param).then(response => {
          this.communicationProtocolList = response.data;
        });
      },
      /** 查询消息协议列表 */
      getMessageProtocolList(id) {
        this.form.messageProtocol = null;
        this.communicationProtocolList.forEach(item => {
          if (item.value == id && item.label == 'tcp') {
            this.messageShow = false;
          }
          if (item.value == id && item.label != 'tcp') {
            this.messageShow = true;
          }
          if (item.value == id && (item.label == 'http' || item.label == 'tcp')) {
            this.showDataAccess = true;
          }
          if (item.value == id && item.label != 'tcp' && item.label != 'http') {
            this.showDataAccess = false;
          }
        })
        if (this.messageShow) {
          let param = {
            // dictType: 'message_protocol_product',
            id: id
          }
          getMessageListById(param).then(response => {
            this.messageProtocolList = response.data;
          });
          // getAllMessageIdList(param).then(response => {
          //   this.messageProtocolList = response.data;
          // });
        }

      },
      /** 查询物联类型列表 */
      getIotTypeList() {
        let param = {
          dictType: 'iot_type'
        }
        getAllMessageList(param).then(response => {
          this.iotTypeList = response.data;
        });
      },
      /** 查询品类定义列表 */
      getCategoryList() {
        listCategory().then(response => {
          this.categoryList = response.data;
        });
      },
      /** 查询产品定义列表 */
      getList() {
        this.loading = true;
        listProduct(this.queryParams).then(response => {
          this.productList = response.data;
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
          name: null,
          code: null,
          state: null,
          categoryId: null,
          iotType: null,
          communicationProtocol: null,
          dataAccess: null,
          messageProtocol: null,
          productDescribe: null,
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
        this.form.state = '1'
        this.messageShow = true;
        this.open = true;
        this.title = "添加产品定义";
      },
      /** 配置按钮操作 */
      handleSetting(row) {
        console.log(row);
        // 跳转配置页面
        // let url = '/basicData/setting/index/' + row.id
        // this.$router.push(url,{})
        this.$router.push({
          name: 'settingConfig',
          params: {
            productId: row.id,
            productName: row.name,
            productCode: row.code,
            productAgreement: row.communicationProtocol
          }
        })

      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids
        getProduct(id).then(response => {
          if (response.data.communicationProtocol != null) {
            let param = {
              // dictType: 'message_protocol_product',
              id: response.data.communicationProtocol
            }
            //  listIotTypeId
            getMessageListById(param).then(response2 => {
              this.messageProtocolList = response2.data;
              this.form = response.data;
              if (response.data.communicationProtocol == '2') {
                this.messageShow = false;
              } else {
                this.messageShow = true;
              }
              if(response.data.communicationProtocol == '0' || response.data.communicationProtocol == '2'){
                this.showDataAccess = true;
              }else{
                this.showDataAccess = false;
              }
              this.open = true;
              this.title = "修改产品定义";
            });
          } else {
            this.form = response.data;
            this.open = true;
            this.title = "修改产品定义";
          }
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              updateProduct(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addProduct(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除产品定义编号为"' + ids + '"的数据项？').then(function () {
          return delProduct(ids);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {
        });
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('baseData/product/export', {
          ...this.queryParams
        }, `产品定义_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
