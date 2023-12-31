<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="协议名称" prop="agreementName">
        <el-input v-model="queryParams.agreementName" placeholder="请输入协议名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="协议编码" prop="agreementCode">
        <el-input v-model="queryParams.agreementCode" placeholder="请输入协议编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="协议类型" prop="agreementType">
        <el-select v-model="queryParams.agreementType" placeholder="请选择协议类型" clearable>
          <el-option v-for="dict in dict.type.communication_protocol_product" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['Agreement:agreement:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['Agreement:agreement:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['Agreement:agreement:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['Agreement:agreement:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="agreementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="协议名称" align="center" prop="agreementName" />
      <el-table-column label="协议编码" align="center" prop="agreementCode" />
      <el-table-column label="协议类型" align="center" prop="agreementType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.communication_protocol_product" :value="scope.row.agreementType" />
        </template>
      </el-table-column>
      <el-table-column label="http接口地址" align="center" prop="httpAddress" />
      <el-table-column label="mqtt订阅主题" align="center" prop="mqttAddress" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['Agreement:agreement:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['Agreement:agreement:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改设备协议对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="520px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="协议名称" prop="agreementName">
          <el-input v-model="form.agreementName" placeholder="请输入协议名称" />
        </el-form-item>
        <el-form-item label="协议编码" prop="agreementCode">
          <el-input v-model="form.agreementCode" placeholder="请输入协议编码" />
        </el-form-item>
        <el-form-item label="协议类型" prop="agreementType">
          <el-select v-model="form.agreementType" placeholder="请选择协议类型" style="width:100%!important;">
            <el-option v-for="dict in dict.type.communication_protocol_product" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>


        <el-form-item label="http接口地址" prop="httpAddress" v-if="form.agreementType==0">
          <el-input type="textarea" v-model="form.httpAddress" placeholder="请输入http接口地址" />
        </el-form-item>
        <el-form-item label="mqtt订阅地址" prop="mqttAddress"  v-if="form.agreementType==1">
          <el-input type="textarea" v-model="form.mqttAddress" placeholder="请输入mqtt订阅地址，多个用逗号分割" />
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
import { listAgreement, getAgreement, delAgreement, addAgreement, updateAgreement } from "@/api/basicData/deviceDefinition/agreement/agreement";

export default {
  name: "Agreement",
  dicts: ['communication_protocol_product'],
  data() {
    return {
      //二次弹窗标识
      check:0,
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
      // 设备协议表格数据
      agreementList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        agreementName: null,
        agreementCode: null,
        agreementType: null,
        httpAddress: null,
        mqttAddress: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        agreementName: [
                    { required: true, message: "协议名称不能为空", trigger: "blur" }
                ],
                agreementCode: [
                    { required: true, message: "协议编码不能为空", trigger: "blur" }
                ],
                agreementType: [
                    { required: true, message: "协议类型不能为空", trigger: "blur" }
                ],
                httpAddress: [
                    { required: true, message: "http接口地址不能为空", trigger: "blur" }
                ],
                mqttAddress: [
                    { required: true, message: "mqtt订阅主题不能为空", trigger: "blur" }
                ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询设备协议列表 */
    getList() {
      this.loading = true;
      listAgreement(this.queryParams).then(response => {
        this.agreementList = response.rows;
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
        agreementName: null,
        agreementCode: null,
        agreementType: null,
        httpAddress: null,
        mqttAddress: null,
        createTime: null,
        updateTime: null
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
      this.title = "添加设备协议";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAgreement(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改设备协议";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if(this.form.agreementType!=0){
              this.form.httpAddress=null;
            }

            if(this.form.agreementType!=1){
              this.form.mqttAddress=null;
            }
          if (this.form.id != null) {
            updateAgreement(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAgreement(this.form).then(response => {
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
      var that=this;
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除设备协议编号为"' + ids + '"的数据项？').then(function () {
        return delAgreement(ids,that.check);
      }).then((response) => {
        if(response.code==209){
          // //二次弹窗
          // that.$modal.confirm('协议名称为:'+response.msg+'的协议已关联产品，如需删除会将关联的产品一同删除').then(function () {
          //  return delAgreement(ids,1)
          // }).then(result=>{
          //     this.$modal.msgSuccess("删除成功");
          //   })
        this.$modal.msgError("删除失败，清先删除"+response.msg+"关联的产品信息");
        }else if(response.code==200){
        this.getList();
        this.$modal.msgSuccess("删除成功");
        }else{
        this.$modal.msgError(msg);


        }

      }).catch(e => {
        console.log(e);

      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('baseData/agreement/export', {
        ...this.queryParams
      }, `协议列表_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
