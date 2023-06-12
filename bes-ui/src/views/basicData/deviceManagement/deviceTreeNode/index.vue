<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="节点名称" prop="deviceNodeName">
        <el-input
          v-model="queryParams.deviceNodeName"
          placeholder="请输入节点名称"
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
          v-hasPermi="['deviceTree:node:add']"
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
          v-hasPermi="['deviceTree:node:edit']"
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
          v-hasPermi="['deviceTree:node:remove']"
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
          v-hasPermi="['deviceTree:node:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="nodeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="节点类型" align="center" prop="deviceNodeCode" show-overflow-tooltip/>
      <el-table-column label="是否节点" align="center" prop="deviceNodeIsNode" show-overflow-tooltip>
        <template slot-scope="scope">
          <dict-tag :options="dict.type.device_is_node" :value="scope.row.deviceNodeIsNode"/>
        </template>
      </el-table-column>
      <el-table-column label="节点名称" align="center" prop="deviceNodeName" show-overflow-tooltip/>
      <el-table-column label="节点功能名称" align="center" prop="deviceNodeFunName" show-overflow-tooltip/>
      <el-table-column label="在线图片路径" align="center" prop="onlineIcon" show-overflow-tooltip/>
      <el-table-column label="离线图片路径" align="center" prop="offlineIcon" show-overflow-tooltip/>
      <el-table-column label="维护url" align="center" prop="url" show-overflow-tooltip/>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip/>
      <el-table-column label="创建时间" align="center" prop="createTime" show-overflow-tooltip/>
      <el-table-column label="修改时间" align="center" prop="updateTime" show-overflow-tooltip/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['deviceTree:node:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['deviceTree:node:remove']"
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

    <!-- 添加或修改树节点定义对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="节点类型" prop="deviceNodeCode">
          <el-input v-model="form.deviceNodeCode" placeholder="请输入节点类型"/>
        </el-form-item>
        <el-form-item label="是否节点" prop="deviceNodeIsNode">
          <el-select
            v-model="form.deviceNodeIsNode"
            placeholder="请选择"
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="dict in dict.type.device_is_node"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="节点名称" prop="deviceNodeName">
          <el-input v-model="form.deviceNodeName" placeholder="请输入节点名称"/>
        </el-form-item>
        <el-form-item label="节点功能名称" prop="deviceNodeFunType">
          <el-select
            v-model="form.deviceNodeFunType"
            multiple
            filterable
            collapse-tags
            style="width: 100%"
            placeholder="请选择节点功能名称">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="在线图片路径" prop="onlineIcon">
          <el-input v-model="form.onlineIcon" placeholder="请输入在线图片路径"/>
        </el-form-item>
        <el-form-item label="离线图片路径" prop="offlineIcon">
          <el-input v-model="form.offlineIcon" placeholder="请输入离线图片路径"/>
        </el-form-item>
        <el-form-item label="维护url" prop="url">
          <el-input v-model="form.url" placeholder="请输入维护url"/>
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
  </div>
</template>

<script>
import {
  listNode,
  listData,
  getNode,
  delNode,
  addNode,
  updateNode
} from "@/api/basicData/deviceManagement/deviceTreeNode/deviceTreeNode";

export default {
  dicts: ['device_is_node'],
  name: "index",
  data() {
    return {
      // 遮罩层
      loading: true,
      //下拉框
      options: [],
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
      // 树节点定义表格数据
      nodeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceNodeName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        deviceNodeCode: [
          {required: true, message: "节点编号不能为空且长度不超过20", trigger: "blur", max: 20}
        ],
        deviceNodeIsNode: [
          {required: true, message: "是否节点不能为空", trigger: "blur"}
        ],
        deviceNodeName: [
          {required: true, message: "节点名称不能为空且长度不超过20", trigger: "blur", max: 20}
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getListData();
  },
  methods: {
    /** 查询树节点定义列表 */
    getList() {
      this.loading = true;
      listNode(this.queryParams).then(response => {
        this.nodeList = response.rows
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 下拉框 查询树节点数据 */
    getListData() {
      this.loading = true;
      this.options = []
      listData().then(response => {
        response.data.forEach((item) => {
          this.options.push({value: item.deviceNodeCode, label: item.deviceNodeName})
        })
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        deviceNodeId: null,
        deviceNodeCode: null,
        deviceNodeIsNode: null,
        deviceNodeName: null,
        deviceNodeFunType: null,
        onlineIcon: null,
        offlineIcon: null,
        url: null,
        remark: null,
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
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.deviceNodeId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加树节点定义";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const deviceNodeId = row.deviceNodeId || this.ids
      getNode(deviceNodeId).then(response => {
        this.form = response.data;
        if (this.form.deviceNodeFunType) {
          this.form.deviceNodeFunType = this.form.deviceNodeFunType.split(",")
        }
        this.open = true;
        this.title = "修改树节点定义";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          //获取节点名称和节点功能
          if (this.form.deviceNodeFunType && this.form.deviceNodeFunType.length > 0) {
            this.form.deviceNodeFunName = []
            for (let i = 0; i <= this.form.deviceNodeFunType.length - 1; i++) {
              this.options.find((item) => {
                if (item.value == this.form.deviceNodeFunType[i]) {
                  this.form.deviceNodeFunName.push(item.label);
                }
              });
            }
            this.form.deviceNodeFunName = this.form.deviceNodeFunName.toString()
            this.form.deviceNodeFunType = this.form.deviceNodeFunType.toString()
          } else {
            this.form.deviceNodeFunType = null
            this.form.deviceNodeFunName = ''
          }
          if (this.form.deviceNodeId != null) {
            updateNode(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getListData();
              this.getList();
            });
          } else {
            addNode(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getListData();
              this.getList();
            });
          }
          this.form.deviceNodeFunType = this.form.deviceNodeFunType.split(',')
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const deviceNodeIds = row.deviceNodeId || this.ids;
      this.$modal.confirm('是否确认删除？').then(function () {
        return delNode(deviceNodeIds);
      }).then(() => {
        this.getList();
        this.getListData();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('deviceTree/node/export', {
        ...this.queryParams
      }, `node_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
