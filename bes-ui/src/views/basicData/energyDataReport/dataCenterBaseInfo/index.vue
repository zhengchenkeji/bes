<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <!--<el-form-item label="ip地址" prop="dataCenterIp">
        <el-input
          v-model="queryParams.dataCenterIp"
          placeholder="请输入数据中心ip地址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="端口号" prop="dataCenterPort">
        <el-input
          v-model="queryParams.dataCenterPort"
          placeholder="请输入数据中心端口号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
      <el-form-item label="名称" prop="dataCenterName">
        <el-input
          v-model="queryParams.dataCenterName"
          placeholder="请输入数据中心名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="dataCenterType">
        <el-select v-model="queryParams.dataCenterType" placeholder="类型" clearable>
          <el-option
            v-for="dict in dict.type.data_center_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="联系人" prop="dataCenterContact">
        <el-input
          v-model="queryParams.dataCenterContact"
          placeholder="请输入联系人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="dataCenterTel">
        <el-input
          v-model="queryParams.dataCenterTel"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['dataCenterBaseInfo:info:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['dataCenterBaseInfo:info:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['dataCenterBaseInfo:info:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dataCenterBaseInfo:info:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="infoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!--<el-table-column label="编号" align="center" prop="id" />-->
      <el-table-column label="代码" align="center" prop="dataCenterId" />
      <el-table-column label="名称" align="center" prop="dataCenterName" />
      <el-table-column label="ip地址" align="center" prop="dataCenterIp" />
      <el-table-column label="端口号" align="center" prop="dataCenterPort" />
      <el-table-column label="类型" align="center" prop="dataCenterType" >
        <template slot-scope="scope">
          <dict-tag :options="dict.type.data_center_type" :value="scope.row.dataCenterType"/>
        </template>
      </el-table-column>
      <el-table-column label="主管单位" align="center" prop="dataCenterManager" />
      <el-table-column label="联系人" align="center" prop="dataCenterContact" />
      <el-table-column label="联系电话" align="center" prop="dataCenterTel" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="200" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dataCenterBaseInfo:info:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dataCenterBaseInfo:info:remove']"
          >删除</el-button>
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

    <!-- 添加或修改数据中心信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="代码" prop="dataCenterId" >
          <el-input v-model="form.dataCenterId" placeholder="请输入代码" />
        </el-form-item>
        <el-form-item label="名称" prop="dataCenterName">
          <el-input v-model="form.dataCenterName" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="ip地址" prop="dataCenterIp">
          <el-input v-model="form.dataCenterIp" placeholder="请输入ip地址" />
        </el-form-item>
        <el-form-item label="端口号" prop="dataCenterPort">
          <el-input v-model="form.dataCenterPort" placeholder="请输入端口号" />
        </el-form-item>
        <el-form-item label="类型" prop="dataCenterType">
          <el-select
            v-model="form.dataCenterType"
            placeholder="请选择"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="dict in dict.type.data_center_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="主管单位" prop="dataCenterManager">
          <el-input v-model="form.dataCenterManager" placeholder="请输入主管单位名称" />
        </el-form-item>
        <el-form-item label="描述" prop="dataCenterDesc">
          <el-input v-model="form.dataCenterDesc" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="联系人" prop="dataCenterContact">
          <el-input v-model="form.dataCenterContact" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="dataCenterTel">
          <el-input v-model="form.dataCenterTel" placeholder="请输入联系电话" />
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
import { listInfo, getInfo, delInfo, addInfo, updateInfo } from "@/api/basicData/energyDataReport/dataCenterBaseInfo/dataCenterBaseInfo";


export default {
  name: "Info",
  dicts: ['data_center_type'],
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
      // 数据中心信息表格数据
      infoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dataCenterIp: null,
        dataCenterPort: null,
        dataCenterId: null,
        dataCenterName: null,
        dataCenterType: null,
        dataCenterManager: null,
        dataCenterDesc: null,
        dataCenterContact: null,
        dataCenterTel: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        dataCenterIp: [
          { required: true, message: "ip地址不能为空", trigger: "blur" },
            { required: true, message: '请输入正确的IP地址', validator: this.validateIP, trigger: 'blur' }
        ],
        dataCenterPort: [
          { required: true, message: "端口号不能为空", trigger: "blur" },
            {required: true, message: "只能为数字且长度小于等于5", trigger: "blur", pattern: /^\d{1,5}$/}
        ],
        dataCenterId: [
          { required: true, message: "代码不能为空", trigger: "blur" }
        ],
        dataCenterName: [
          { required: true, message: "名称不能为空", trigger: "blur" }
        ],
        dataCenterType: [
          { required: true, message: "类型不能为空", trigger: "change" }
        ],
        dataCenterManager: [
          { required: true, message: "主管单位不能为空", trigger: "blur" }
        ],
        dataCenterDesc: [
          { required: true, message: "描述不能为空", trigger: "blur" }
        ],
        dataCenterContact: [
          { required: true, message: "联系人不能为空", trigger: "blur" }
        ],
        dataCenterTel: [
          { required: true, message: "联系电话不能为空", trigger: "blur" },
            { pattern:/^((0\d{2,3}-\d{7,8})|(1[34578]\d{9}))$/,message: "请输入正确的联系方式"}
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
      validateIP(rule, value, callback) {
          if (value === '' || typeof value === 'undefined' || value == null) {
              callback(new Error('请输入正确的IP地址'))
          } else {
              const reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
              if ((!reg.test(value)) && value !== '') {
                  callback(new Error('请输入正确的IP地址'))
              } else {
                  callback()
              }
          }
      },
    /** 查询数据中心信息列表 */
    getList() {
      this.loading = true;
      listInfo(this.queryParams).then(response => {
        this.infoList = response.rows;
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
        dataCenterIp: null,
        dataCenterPort: null,
        dataCenterId: null,
        dataCenterName: null,
        dataCenterType: null,
        dataCenterManager: null,
        dataCenterDesc: null,
        dataCenterContact: null,
        dataCenterTel: null,
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加数据中心信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getInfo(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改数据中心信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateInfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addInfo(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除数据中心信息编号为"' + ids + '"的数据项？').then(function() {
        return delInfo(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('basicData/dataCenterBaseInfo/export', {
        ...this.queryParams
      }, `数据中心基本信息_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
