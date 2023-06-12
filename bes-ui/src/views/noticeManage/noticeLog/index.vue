<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="通知类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择通知类型" clearable @change="typechange">
          <el-option v-for="dict in dict.type.bes_notice_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="是否成功" prop="isSuccess">
        <el-select v-model="queryParams.isSuccess" placeholder="请选择是否发送成功" clearable>
          <el-option v-for="dict in dict.type.athena_bes_yes_no" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>

      <el-form-item label="通知配置" prop="noticeConfig">
        <el-select v-model="queryParams.noticeConfig" placeholder="请选择通知配置" clearable @change="configchange">
          <el-option v-for="dict in selectNoticeConfigList" :key="dict.code" :label="dict.name" :value="dict.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="通知模板" prop="noticeTemplate">
        <el-select v-model="queryParams.noticeTemplate" placeholder="请选择通知模板" clearable>
          <el-option v-for="dict in selectNoticeTemplateList" :key="dict.code" :label="dict.name" :value="dict.code" />
        </el-select>
      </el-form-item>


      <el-form-item label="通知时间" prop="noticeLogTimes">
                <el-date-picker v-model="queryParams.noticeLogTimes" type="daterange" range-separator="至"
                    start-placeholder="开始日期" end-placeholder="结束日期">
                </el-date-picker>
            </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:log:add']"
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
          v-hasPermi="['system:log:edit']"
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
          v-hasPermi="['system:log:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:log:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row> -->

    <el-table v-loading="loading" :data="logList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="${comment}" align="center" prop="id" /> -->
      <el-table-column label="通知类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bes_notice_type" :value="scope.row.type" />
        </template>
      </el-table-column>

      <el-table-column label="是否发送成功" align="center" prop="isSuccess">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.athena_bes_yes_no" :value="scope.row.isSuccess" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="发送内容" align="center" prop="sendText" /> -->
      <!-- <el-table-column label="消息体" align="center" prop="sendJson" /> -->
      <el-table-column label="通知配置" align="center" prop="noticeConfig" :formatter="noticeConfigFormatter">
        <!-- <template slot-scope="scope">
          <dict-tag :options="noticeconfigList" :value="scope.row.noticeConfig"/>
        </template> -->
      </el-table-column>
      <el-table-column label="通知模板" align="center" prop="noticeTemplate" :formatter="noticeTeaplateAllFormatter">

      </el-table-column>
      <!-- <el-table-column label="所属业务id" align="center" prop="ywId" /> -->
      <!-- <el-table-column label="业务表名" align="center" prop="ywTable" /> -->
      <el-table-column label="接收者" align="center" prop="recipient" />
      <el-table-column label="响应的第三方id" align="center" prop="responseId" />
      <!-- <el-table-column label="响应消息体" align="center" prop="response" /> -->
      <el-table-column label="发送时间" align="center" prop="addTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sendTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['system:log:edit']">查看</el-button>
          <!-- <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:log:remove']"
          >删除</el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改消息发送日志对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择通知类型">
            <el-option v-for="dict in dict.type.bes_notice_type" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发送时间" prop="sendTime">
          <el-date-picker clearable v-model="form.sendTime" type="date" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择发送时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否发送成功" prop="isSuccess">
          <el-select v-model="form.isSuccess" placeholder="请选择是否发送成功">
            <el-option v-for="dict in dict.type.athena_bes_yes_no" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
       
        <el-form-item label="通知配置" prop="noticeConfig">
          <el-select v-model="form.noticeConfig" placeholder="请选择通知配置">
            <el-option v-for="dict in noticeconfigList" :key="dict.code" :label="dict.name"
              :value="parseInt(dict.code)"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="通知模板" prop="noticeTemplate">
          <el-select v-model="form.noticeTemplate" placeholder="请选择通知模板">
            <el-option v-for="dict in noticeTemplateAllList" :key="dict.id" :label="dict.templatename"
              :value="parseInt(dict.id)"></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="所属业务id" prop="ywId">
          <el-input v-model="form.ywId" placeholder="请输入所属业务id" />
        </el-form-item> -->
        <el-form-item label="业务表名" prop="ywTable">
          <el-input v-model="form.ywTable" placeholder="请输入业务表名" />
        </el-form-item>
        <el-form-item label="接收者" prop="recipient">
          <el-input v-model="form.recipient" placeholder="请输入接收者" />
        </el-form-item>
        <el-form-item label="响应的第三方id" prop="responseId">
          <el-input v-model="form.responseId" placeholder="请输入响应的第三方id" />
        </el-form-item>
        <el-form-item label="发送内容" prop="sendText">
          <el-input v-model="form.sendText" placeholder="请输入发送内容" type="textarea"  />
        </el-form-item>
        <el-form-item label="消息体" prop="sendJson">
          <el-input v-model="form.sendJson" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="响应消息体" prop="response">
          <el-input v-model="form.response" placeholder="请输入响应消息体" type="textarea"  />
        </el-form-item>
        <!-- <el-form-item label="添加时间" prop="addTime">
          <el-date-picker clearable v-model="form.addTime" type="date" value-format="yyyy-MM-dd" placeholder="请选择添加时间">
          </el-date-picker>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getNoticeLogList, getNoticeConfigListByType, getNoticeTemplateListByConfig, listTemplateall, getlogInfo } from "@/api/noticeManage/noticelog";

export default {
  name: "Log",
  dicts: ['sys_user_sex', 'athena_bes_yes_no', 'bes_notice_type', 'sys_show_hide'],
  data() {
    return {
      noticeconfigList: [],
      noticeTemplateAllList: [],
      selectNoticeConfigList: [],
      selectNoticeTemplateList: [],

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
      // 消息发送日志表格数据
      logList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: null,
        sendTime: null,
        isSuccess: null,
        sendText: null,
        sendJson: null,
        noticeConfig: null,
        noticeTemplate: null,
        noticeLogTimes:null,
        noticeLogTime:null,
        ywId: null,
        ywTable: null,
        recipient: null,
        responseId: null,
        response: null,
        addTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getNoticeConfigListByType();
    this.listTemplateall();

    this.getList();

  },

  methods: {

    getlogInfo(id) {
      const param = {
        logId: id
      }
      getlogInfo(param).then(response => {
        console.log(response.data);
      })
    },
    configchange() {
      const param = {
        configId: this.queryParams.noticeConfig
      }
      getNoticeTemplateListByConfig(param).then(response => {
        this.selectNoticeTemplateList = response.data

      })
    },
    typechange() {
      this.queryParams.noticeConfig=null;
      this.queryParams.noticeTemplate=null;

      const param = {
        noticeType: this.queryParams.type
      }
      console.log(param)

      getNoticeConfigListByType(param).then(response => {
        // console.log(response.data);
        // this.noticeconfigList=response.data;
        this.selectNoticeConfigList = response.data
      })

    },
    noticeTeaplateAllFormatter(row, column, cellValue, index) {
      var label = "";
      this.noticeTemplateAllList.forEach(item => {
        if (item.id == cellValue) {
          label = item.templatename;
        }
      })
      return label;
    },
    noticeConfigFormatter(row, column, cellValue, index) {
      console.log(this.noticeconfigList);
      var label = "";
      this.noticeconfigList.forEach(item => {
        if (item.code == cellValue) {
          label = item.name;
        }
      })
      return label;
    },
    listTemplateall() {
      listTemplateall().then(response => {
        this.noticeTemplateAllList = response.rows;
      })

    },
    getNoticeConfigListByType() {

      getNoticeConfigListByType().then(response => {
        // console.log(response.data);
        this.noticeconfigList = response.data;
        this.selectNoticeConfigList = response.data
      })
    },
    /** 查询消息发送日志列表 */
    getList() {
      this.loading = true
      this.queryParams.noticeLogTime=this.queryParams.noticeLogTimes+"";
      getNoticeLogList(this.queryParams).then(response => {
        this.logList = response.rows;
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
        type: null,
        sendTime: null,
        isSuccess: null,
        sendText: null,
        sendJson: null,
        noticeConfig: null,
        noticeTemplate: null,
        ywId: null,
        ywTable: null,
        recipient: null,
        responseId: null,
        response: null,
        addTime: null,
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
      this.title = "添加消息发送日志";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const param = {
        logId: parseInt(row.id)
      }
      console.log(param, row);
      this.reset();
      getlogInfo(param).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "查看消息发送日志";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLog(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addLog(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除消息发送日志编号为"' + ids + '"的数据项？').then(function () {
        return delLog(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/log/export', {
        ...this.queryParams
      }, `log_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
