<template>
  <div class="app-container">
    <!--查询条件-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="能源类型名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入能源类型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="能源类型编号" prop="name">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入能源类型编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!--操作按钮-->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['basicData:energyType:add']"
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
          v-hasPermi="['basicData:energyType:edit']"
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
          v-hasPermi="['basicData:energyType:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['basicData:energyType:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!--右侧表格-->
    <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="能源类型编号" align="center" prop="code" />
      <el-table-column label="能源类型名称" align="center" prop="name" />
      <el-table-column label="单价" align="center" prop="price" />
      <el-table-column label="耗煤量" align="center" prop="coalAmount" />
      <el-table-column label="二氧化碳" align="center" prop="co2" />
      <el-table-column label="单位" align="center" prop="unit" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="修改时间" align="center" prop="updateTime" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['basicData:energyType:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['basicData:energyType:remove']"
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

    <!-- 添加或修改能源类型对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="能源类型编号" prop="code">
          <el-input v-model="form.code" placeholder="请输入能源类型编号称" />
        </el-form-item>
        <el-form-item label="能源类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入能源类型名称" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input v-model="form.price" placeholder="请输入单价" />
        </el-form-item>
        <el-form-item label="耗煤量" prop="coalAmount">
          <el-input v-model="form.coalAmount" placeholder="请输入耗煤量" />
        </el-form-item>
        <el-form-item label="二氧化碳" prop="co2">
          <el-input v-model="form.co2" placeholder="请输入二氧化碳" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位" />
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
import { listType, getType, delType, addType, updateType } from "@/api/basicData/energyInfo/energyType/energyType";

export default {
  name: "Type",
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
      // 能源类型表格数据
      typeList: [],
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
        coalAmount: null,
        co2: null,
        unit: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        code: [
          { required: true, message: "能源类型编号不能为空", trigger: "blur" },
          {
            pattern: /^[0-9]*$/,
            message: '请输入正确的能源类型编号'
          }
        ],
        name: [
          { required: true, message: "能源类型名称不能为空", trigger: "blur" }
        ],
        coalAmount: [
          { required: true, message: "耗煤量不能为空", trigger: "blur" },
          { pattern: /^[0-9,.]*$/, message: '请输入正确的耗煤量' }
        ],
        co2: [
          { required: true, message: "二氧化碳不能为空", trigger: "blur" },
          { pattern: /^[0-9,.]*$/, message: '请输入正确的二氧化碳' }
        ],
        price: [
          { required: true, message: "单价不能为空", trigger: "blur" },
          { pattern: /^[0-9,.]*$/, message: '请输入正确的单价' }
        ],
        unit: [
          { required: true, message: "单位不能为空", trigger: "blur" }
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
    /** 查询能源类型列表 */
    getList() {
      this.loading = true;
      listType(this.queryParams).then(response => {
        this.typeList = response.rows;
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
        guid: null,
        code: null,
        name: null,
        price: null,
        coalAmount: null,
        co2: null,
        unit: null,
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
      this.queryParams.code = ''
      this.queryParams.name = ''
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.guid)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加能源类型";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const guid = row.guid || this.ids
      getType(guid).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改能源类型";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.guid != null) {
            updateType(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addType(this.form).then(response => {
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
      const guids = row.guid || this.ids;
      this.$modal.confirm('是否确认删除能源类型编号为"' + row.code + '"的数据项？').then(function() {
        return delType(guids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch((response) => {
        var str = response+''
        if(str.indexOf('关联')>0) {
          this.$confirm(str.substring(6, str.length), '提示', {
            confirmButtonText: '确定',
            type: 'warning'
          })
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('basicData/energyType/export', {
        ...this.queryParams
      }, `能耗类型定义.xlsx`)
    }
  }
};
</script>
