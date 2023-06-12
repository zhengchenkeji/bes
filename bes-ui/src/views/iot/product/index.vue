<template>
  <div class="app-container">

    <!-- 主页 -->
    <div v-show="homePageShow">

      <!-- 条件搜索 -->
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="产品名称" prop="productName">
          <el-input
            v-model="queryParams.productName"
            placeholder="请输入产品名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="产品编号" prop="productCode">
          <el-input
            v-model="queryParams.productCode"
            placeholder="请输入产品编号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="所属品类" prop="categoryId">
          <el-input
            v-model="queryParams.categoryId"
            placeholder="请输入所属品类"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="节点类型" prop="nodeType">
          <el-select v-model="queryParams.nodeType" placeholder="请选择节点类型" clearable>
            <el-option
              v-for="dict in dict.type.iot_product_node_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态" prop="publishState">
          <el-select v-model="queryParams.publishState" placeholder="请选择发布状态" clearable>
            <el-option
              v-for="dict in dict.type.iot_publish_state"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 新增、修改、删除、导出功能按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['iot:product:add']"
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
            v-hasPermi="['iot:product:edit']"
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
            v-hasPermi="['iot:product:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['iot:product:export']"
          >导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 产品列表 -->
      <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" width="55" type="index" align="center" />
        <el-table-column label="产品名称" align="center" prop="productName" />
        <el-table-column label="产品编号" align="center" prop="productCode" />
        <el-table-column label="所属品类" align="center" prop="category.categoryName" />
        <el-table-column label="消息协议" align="center" prop="protocolId">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.iot_message_protocol" :value="scope.row.protocolId"/>
          </template>
        </el-table-column>
        <el-table-column label="传输协议" align="center" prop="transportProtocol">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.iot_transport_protocol" :value="scope.row.transportProtocol"/>
          </template>
        </el-table-column>
        <el-table-column label="节点类型" align="center" prop="nodeType">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.iot_product_node_type" :value="scope.row.nodeType"/>
          </template>
        </el-table-column>

        <el-table-column label="发布状态" align="center">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.publishState"
              :active-value=1
              :inactive-value=0
              @change="handlePublishStateChange(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>

        <el-table-column label="产品描述" align="center" prop="description" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleDetails(scope.row)"
              v-hasPermi="['iot:product:list']"
            >查看</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['iot:product:edit']"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['iot:product:remove']"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />

      <!-- 添加或修改产品对话框 -->
      <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="产品名称" prop="productName">
            <el-input v-model="form.productName" placeholder="请输入产品名称" />
          </el-form-item>
          <el-form-item label="产品编号" prop="productCode">
            <el-input v-model="form.productCode" placeholder="请输入产品编号" />
          </el-form-item>
          <el-form-item label="所属品类" prop="categoryId">
            <treeselect
              :show-count="true"
              v-model="form.categoryId"
              :options="categoryOptions"
              :normalizer="normalizer"
              placeholder="请选择所属品类" />
          </el-form-item>
          <el-form-item label="消息协议" prop="protocolId">
            <el-select v-model="form.protocolId" placeholder="请选择消息协议" class="select-width">
              <el-option
                v-for="dict in dict.type.iot_message_protocol"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="传输协议" prop="transportProtocol">
            <el-select v-model="form.transportProtocol" placeholder="请选择传输协议" class="select-width">
              <el-option
                v-for="dict in dict.type.iot_transport_protocol"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="节点类型" prop="nodeType">
            <el-radio-group v-model="form.nodeType">
              <el-radio
                v-for="dict in dict.type.iot_product_node_type"
                :key="dict.value" :label="parseInt(dict.value)"
              >{{dict.label}}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="产品描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </div>

    <!-- 详情页 -->
    <div v-show="!homePageShow">
      <el-page-header @back="goBackHomePage" content="详情页面">
      </el-page-header>
    </div>

  </div>
</template>

<script>
import { listProduct, getProduct, delProduct, addProduct, updateProduct } from "@/api/iot/product";
import { listCategory } from "@/api/iot/category";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "Product",
  components: {
    Treeselect
  },
  dicts: ['iot_transport_protocol', 'iot_product_node_type', 'iot_message_protocol', 'iot_publish_state'],
  data() {

    const validateNodeType = (rule, value, callback) => {
      if (value) {
        if (value === 0) {
          callback(new Error())
        } else {
          callback();
        }
      } else {
        callback(new Error());
      }
    }

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
      // 产品表格数据
      productList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 品类树选项
      categoryOptions: undefined,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        productName: null,
        productCode: null,
        categoryId: null,
        nodeType: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        productName: [
          { required: true, message: "产品名称不能为空", trigger: "blur" }
        ],
        productCode: [
          { required: true, message: "产品编号不能为空", trigger: "blur" }
        ],
        categoryId: [
          { required: true, message: "所属品类不能为空", trigger: "blur" }
        ],
        protocolId: [
          { required: true, message: "消息协议不能为空", trigger: "change" }
        ],
        transportProtocol: [
          { required: true, message: "传输协议不能为空", trigger: "change" }
        ],
        nodeType: [
          { required: true, validator: validateNodeType,  message: "节点类型不能为空", trigger: "change" }
        ]
      },
      homePageShow: true
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询产品列表 */
    getList() {
      this.loading = true;
      listProduct(this.queryParams).then(response => {
        this.productList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 转换品类数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        label: node.categoryName
      };
    },
    /** 查询品类下拉树结构 */
    getTreeselect() {
      listCategory().then(response => {
        this.categoryOptions = this.handleTree(response.data);
      });
    },
     /** 发布状态修改 */
    handlePublishStateChange(row) {
      let text = row.publishState === 1 ? "发布" : "取消发布";
      this.$modal.confirm(`确认要${text}${row.productName}产品吗？`).then(function() {
        return updateProduct(row);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.publishState = row.publishState === 1 ? 0 : 1;
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
        productName: null,
        productCode: null,
        categoryId: null,
        protocolId: null,
        publishState: 1,
        transportProtocol: null,
        nodeType: null,
        description: null,
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
      this.getTreeselect();
      this.open = true;
      this.title = "添加产品";
    },
    handleDetails(row) {
      this.homePageShow = false;
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      const id = row.id || this.ids
      getProduct(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改产品";
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
      this.$modal.confirm('是否确认删除产品编号为"' + ids + '"的数据项？').then(function() {
        return delProduct(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('iot/product/export', {
        ...this.queryParams
      }, `product_${new Date().getTime()}.xlsx`)
    },

    /** 返回主页 */
    goBackHomePage() {
      this.homePageShow = true;
    }
  }
};
</script>
