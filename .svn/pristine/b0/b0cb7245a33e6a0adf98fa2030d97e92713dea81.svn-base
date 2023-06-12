<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="组名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入组名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="部门" prop="deptId">
              <treeselect v-model="queryParams.deptId" :options="deptOptions" :show-count="true" style="width:200px" placeholder="请选择部门" />
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
          v-hasPermi="['safetyWarning:AlarmNotifier:add']"
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
          v-hasPermi="['safetyWarning:AlarmNotifier:edit']"
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
          v-hasPermi="['safetyWarning:AlarmNotifier:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['safetyWarning:AlarmNotifier:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="AlarmNotifierList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="主键" align="center" prop="id" /> -->
      <el-table-column label="组名" align="center" prop="name" />
      <el-table-column label="部门" align="center" prop="deptName" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['safetyWarning:AlarmNotifier:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['safetyWarning:AlarmNotifier:remove']"
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

    <!-- 添加或修改告警接收组对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" width="430px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="130px">
        <el-form-item label="组名" prop="name">
          <el-input v-model="form.name" placeholder="请输入组名" />
        </el-form-item>

        <el-form-item label="所属部门" prop="deptId">
              <treeselect v-model="form.deptId" :options="deptOptions" :show-count="true"  placeholder="请选择所属部门" />
        </el-form-item>
        <el-form-item label="用户" prop="userList">
          <!-- <el-input v-model="form.post" placeholder="请输入岗位" /> -->
          <el-select v-model="form.userList" multiple placeholder="请选择用户" @change="changeUserList" style="width: 260px;">
                <el-option
                  v-for="item in userOptions"
                  :key="item.userId"
                  :label="item.nickName"
                  :value="item.userId"
                ></el-option>
              </el-select>
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
import { listAlarmNotifier, getAlarmNotifier, delAlarmNotifier, addAlarmNotifier, updateAlarmNotifier,getUserList } from "@/api/basicData/safetyWarning/alarmNotifier/alarmNotifier";
import { listPost} from "@/api/system/post";
import { treeselect } from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import toInteger from "lodash/toInteger";
export default {
  name: "AlarmNotifier",
  components: { Treeselect },
  data() {
    return {
      // 部门树选项
      deptOptions: [],
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 岗位选项
      postOptions: [],
        //用户选项
        userOptions:[],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 告警接收组表格数据
      AlarmNotifierList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        deptId:null

      },
      // 表单参数
      form: {
          name:null,
          deptId:null,
          userId:null,
          userList: [],

      },
      // 表单校验
      rules: {
        name: [
          { required: true, message: "姓名不能为空", trigger: "blur" }
        ],
        deptId: [
          { required: true, message: "所属部门不能为空", trigger: "change" }
        ],
        userList: [
          { required: true, message: "用户不能为空", trigger: "change" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
      // 获取岗位信息
      listPost().then(response => {
        this.postOptions = response.rows;
      });

      // 初始化部门选择
      this.getTreeselect();
  },
    watch: {
        // 监听deptId
        'form.deptId': 'getUserList'
    },
  methods: {

      /**获取用户列表*/
      getUserList(){
          this.userOptions = []
          if (this.form.deptId != null && this.form.deptId !== ''){
              getUserList({
                  deptId:this.form.deptId
              }).then(response => {
                  this.userOptions = response.data;
              });
          }
      },
      changeUserList(value){
          this.$forceUpdate(); // 调用此函数方法,强制刷新

      },

    /** 查询告警接收组列表 */
    getList() {
      this.loading = true;
      listAlarmNotifier(this.queryParams).then(response => {
        this.AlarmNotifierList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
     /** 查询部门下拉树结构 */
     getTreeselect() {
      treeselect().then(response => {
        this.deptOptions = response.data;
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
        deptId: null,
          userId:null,
          userList: [],
          createTime: '',
        updateTime: '',

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
      this.title = "添加告警接收组";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAlarmNotifier(id).then(response => {
          this.form = response.data;
          this.form.userList = []
          var userArray =[]

          if (this.form.userId != null && this.form.userId != ''){
              userArray = this.form.userId.split(',')
              for (let i = 0; i < userArray.length; i++) {
                  this.form.userList.push(userArray[i]*1)
              }
          }
          this.open = true;
          this.title = "修改告警接收组";
      });

    },
    /** 提交按钮 */
    submitForm() {
      // 字符串转为数组
      this.form.userId=this.form.userList+"";
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAlarmNotifier(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).catch(err=>{
                this.$modal.msgError("修改失败");
            });
          } else {
            addAlarmNotifier(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
                this.getList();
            }).catch(err=>{
                this.$modal.msgError("新增失败");
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除选中的告警接收组？').then(function() {
        return delAlarmNotifier(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('safetyWarning/AlarmNotifier/export', {
        ...this.queryParams
      }, `告警接收组.xlsx`)
    }
  }
};
</script>
