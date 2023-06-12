<template>
  <div class="app-container">
    <TextBroadcast :alertShow=true ref="textAudio" />

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="templatename">
        <el-input v-model="queryParams.templatename" placeholder="请输入模板名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item label="通知类型" prop="noticetype">
        <el-select v-model="queryParams.noticetype" placeholder="请选择通知类型" clearable>
          <el-option v-for="dict in dict.type.bes_notice_type" :key="dict.value" :label="dict.label" :value="dict.value"
             />
        </el-select>
      </el-form-item>
      <el-form-item label="是否报警模板" prop="isAlarm" label-width="120px">
        <el-select v-model="queryParams.isAlarm" placeholder="请选择通知类型" clearable>
          <el-option v-for="dict in dict.type.athena_bes_yes_no" :key="dict.value" :label="dict.label" :value="dict.value"
             />
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
          v-hasPermi="['noticeManage:template:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['noticeManage:template:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['noticeManage:template:remove']">删除</el-button>
      </el-col>

      <!-- <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['noticeManage:template:export']">导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模板id" align="center" prop="id" />
      <el-table-column label="模板名称" align="center" prop="templatename" />

      <el-table-column label="通知类型" align="center" prop="noticetype">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bes_notice_type" :value="scope.row.noticetype" />
        </template>
      </el-table-column>
      <el-table-column label="是否报警模板" align="center" prop="isAlarm">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.athena_bes_yes_no" :value="scope.row.isAlarm" />
        </template>
      </el-table-column>
      <el-table-column label="服务厂商" align="center" prop="servicefactory" :formatter="serviceFactoryFormatter">

      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['noticeManage:template:edit']">修改</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-set-up" @click="handleDebug(scope.row)"
            v-hasPermi="['system:config:debug']">调试</el-button> -->
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['noticeManage:template:remove']">删除</el-button>
          <!-- <el-button size="mini" type="text" icon="el-icon-tickets" @click="openSmsLog(scope.row)"
            v-hasPermi="['noticeManage:template:remove']">通知记录</el-button> -->
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />

    <!-- 添加或修改通知模板配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="模板名称" prop="templatename">
          <el-input v-model="form.templatename" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="通知类型" prop="noticetype">
          <el-select v-model="form.noticetype" placeholder="请选择通知类型" @change="noticeTypeChange"
            style="width:100%!important;">
            <el-option v-for="dict in dict.type.bes_notice_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <div v-if="form.noticetype != 3">

          <el-form-item label="服务厂商" prop="servicefactory">
            <el-select v-model="form.servicefactory" placeholder="请选择服务厂商" style="width:100%!important;">
              <el-option v-for="dict in serviceFactoryList" :key="dict.value" :label="dict.label"
                :value="dict.value"></el-option>
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="是否报警模板" prop="isAlarm">
          <el-select v-model="form.isAlarm" placeholder="是否报警模板" style="width:100%!important;" @change="IsAlarmChange">
            <el-option v-for="dict in dict.type.athena_bes_yes_no" :key="dict.value" :label="dict.label"
              :value="parseInt(dict.value)"></el-option>
          </el-select>
        </el-form-item>
        <div v-if="form.servicefactory == 21">

          <el-form-item label="短信模板编码" prop="templatecode">
            <el-input v-model="form.templatecode" placeholder="请输入短信模板编码" />
          </el-form-item>
          <el-form-item label="短信模板签名" prop="templatesign">
            <el-input v-model="form.templatesign" placeholder="请输入短信模板签名" />
          </el-form-item>
          <el-form-item label="短信通知模板" prop="content" v-if="form.isAlarm==1">
            <el-input type="textarea" :rows="2" placeholder='请输入播报内容，需变更内容请使用#{XXX}代替。' v-model="form.content">
            </el-input>
          </el-form-item>
        </div>

        <div v-if="form.noticetype == 3">
          <el-form-item label="播报内容配置" prop="content">
            <el-input type="textarea" :rows="2" placeholder='请输入播报内容，需变更内容请使用#{XXX}代替。' v-model="form.content">
            </el-input>
          </el-form-item>
        </div>

        <div v-if="form.servicefactory == 11">
          <el-form-item label="附件">
            <el-upload class="upload-demo" ref="upload" action="" :before-upload="beforeUpload" :http-request="uploadfile"
              :on-preview="previewfile" :on-remove="renovefile"
              accept=".bmp,.gif,.jpg,.jpeg,.png,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.html,.htm,.txt,.rar,.zip,.gz,.bz2,.mp4,.avi,.rmvb,.pdf"
              :file-list="fileList" :auto-upload="true">
              <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
              <!-- <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">上传到服务器</el-button> -->
              <div slot="tip" class="el-upload__tip">只能上传不超过5MB的文件</div>
            </el-upload>
          </el-form-item>
          <el-form-item label="邮箱主题配置" prop="title">
            <el-input v-model="form.title" placeholder="请输入邮箱主题配置" />
          </el-form-item>
          <el-form-item label="邮件内容配置">
            <editor v-model="form.content" :urlBoolean="true" :min-height="192"
              placeholder="请输入邮件内容需要变更的内容请使用#{name}标注。列：#{name},你好" />
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 点击调试时打开 -->
    <el-dialog :title="title" :visible.sync="debugOpen" width="700px" append-to-body>
      <el-form ref="debugform" :model="debugform" :rules="rulesdebug" label-width="120px">
        <el-form-item label="模板名称" prop="templatename">
          <el-input v-model="templatename" placeholder="请输入模板名称" :disabled="true" />
        </el-form-item>
        <el-form-item label="选择配置" prop="configId">
          <el-select v-model="debugform.configId" placeholder="请选择配置类型" @change="noticeTypeChange"
            style="width:100%!important;">
            <el-option v-for="dict in noticeConfigList" :key="dict.id" :label="dict.name" :value="dict.id"></el-option>
          </el-select>
        </el-form-item>

        <div v-if="debugform.type == 1 || debugform.type == 2">
          <el-form-item label="接收人" prop="recipient">
            <el-input v-model="debugform.recipient" placeholder="请输入接收人" />
          </el-form-item>
          <el-form-item label="变量" prop="content">
            <el-input type="textarea" :rows="2"
              placeholder='请输入(模板中或阿里短信模板)定义的变量使用使用大括号包起来，多个变量使用逗号分割列如：{"name":"张三","age":"18"}。注意：所有符号请使用英文符号！'
              v-model="debugform.content">
            </el-input>
          </el-form-item>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitDebugForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 打开通知日志 -->
    <el-dialog title="通知记录" :visible.sync="noticeLogOpen" width="850px">
      <noticeLogList ref="noticeLog" />
    </el-dialog>

  </div>
</template>

<script>
import { listTemplate, getTemplate, delTemplate, addTemplate, updateTemplate, getNoticeConfigbyTemplate, debugingTemplate } from "@/api/noticeManage/noticeTemplate";
import { getAllServiceFactory } from "@/api/noticeManage/noticeConfig";
import TextBroadcast from '@/components/TextBroadcast/index'
import noticeLogList from '../component/noticeLogList.vue'

export default {
  name: "Template",
  dicts: ['bes_notice_type', 'bes_notice_email_factory', 'bes_notice_sms_factory', 'bes_notice_voice_factory','athena_bes_yes_no'],
  components: {
    TextBroadcast,
    noticeLogList
  },
  data() {
    return {
      noticeLogOpen: false,
      files: [],
      //文件上传列表
      fileList: [],
      //文件
      // 通知配置集合
      noticeConfigList: null,
      // 配置名称
      templatename: "",
      // 是否显示调试窗口
      debugOpen: false,
      // 服务厂商列表
      serviceFactoryList: [],
      // 全部服务厂商列表
      allServiceFactoryList: [],
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
      // 通知模板配置表格数据
      templateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templatename: null,
        noticetype: null,
      },
      // 调试参数
      debugform: {},
      // 表单参数
      form: {},
      // 表单校验
      rules: {

        handleNoticeList: [],
        templatename: [
          { required: true, message: "模板名称不能为空", trigger: "blur" }
        ],
        noticetype: [
          { required: true, message: "通知类型不能为空", trigger: "blur" }
        ],
        servicefactory: [
          { required: true, message: "服务厂商不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        templatecode: [
          { required: true, message: "短信模板编码不能为空", trigger: "blur" }
        ],
        templatesign: [
          { required: true, message: "短信模板签名不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "邮箱内容配置不能为空", trigger: "blur" }
        ],
        title: [
          { required: true, message: "邮箱主题配置不能为空", trigger: "blur" }
        ],
        isAlarm:[
          {required:true,message:"是否报警模板不允许为空",trigger:"blur"}
        ]
      },
      rulesdebug: {
        configId: [
          { required: true, message: "配置不能为空", trigger: "blur" }
        ],
        recipient: [
          { required: true, message: "接收人不能为空", trigger: "blur" }
        ]

      }
    };
  },
  created() {
    this.AllServiceFactory();
    this.getList();
  },
  methods: {

    IsAlarmChange(){
     console.log(this.form.isAlarm);
     if(this.form.isAlarm==1){
      this.form.content="报警名称:#{name},触发方式:#{triggerMode},报警描述:#{msg},报警值:#{val},计划值:#{planVal}"

     }
    },
    openSmsLog(row) {
      this.noticeLogOpen = true;
      this.$nextTick(() => {

        console.log(this.$refs.noticeLog);

        this.$refs.noticeLog.ywid = row.id;
        this.$refs.noticeLog.getNoticeLogList();
      });

    },
    renovefile(file, fileList) {

      this.fileList = fileList;
    },
    previewfile(file) {
      console.log(file);
      this.$download.resource(file.url);

    },
    beforeUpload(file) {
      console.log(file)
      if (file.size / 1024 / 1024 > 5) {
        this.$modal.msgError("文件大小超出！");
        return false;
      } else {
        return true;
      }
    },

    uploadfile(file) {
      console.log(file.file.size / 1024 / 1024)
      console.log(file.file)
      this.files.push(file.file)
      console.log(this.fileList)
      if (file.file.size / 1024 / 1024 > 4) {
        alert("文件超出")
      }
    },
    /** 提交调试按钮 */
    submitDebugForm() {
      debugingTemplate(this.debugform).then(response => {
        console.log(response);
        this.$modal.msgSuccess("指令发送成功");
        this.debugOpen = false;
        if (this.debugform.type == 3) {
          this.$refs.textAudio.playoneAudio(response.msg, this.debugform.content, this.debugform.factory)
        }
      });
    },
    serviceFactoryFormatter(row, column, cellValue, index) {
      var label = "";
      this.allServiceFactoryList.forEach(item => {
        if (item.value == cellValue) {
          label = item.label;
        }
      })
      return label;
    },
    /** 根据通知配置 */
    noticeTypeChange() {
      this.form.servicefactory = "";

      this.changeServiceFactory(this.form.noticetype)
    },
    changeServiceFactory(type) {
      if (type == 1) {
        this.serviceFactoryList = this.dict.type.bes_notice_sms_factory;
      } else if (type == 2) {
        this.serviceFactoryList = this.dict.type.bes_notice_email_factory;
      } else if (type == 3) {
        this.serviceFactoryList = this.dict.type.bes_notice_voice_factory;
      }
    },
    resetDebugForm() {
      this.debugform = {};
      this.resetForm("debugform")
    },
    // 调试操作按钮
    handleDebug(row) {

      row.content = "";
      getNoticeConfigbyTemplate(row).then(response => {
        this.resetDebugForm()
        console.log(row, '-----------------------------');
        this.templatename = row.templatename;
        this.debugform.templateId = row.id;
        this.debugform.type = row.noticetype;
        this.debugform.factory = row.servicefactory;
        // // console.log(response, "====================");
        this.noticeConfigList = response.data;
        this.debugOpen = true;
        this.title = "调试通知模板";
      });
    },
    AllServiceFactory() {
      getAllServiceFactory().then(response => {
        this.allServiceFactoryList = response.data;
      })
    },
    /** 查询通知模板配置列表 */
    getList() {
      this.loading = true;
      listTemplate(this.queryParams).then(response => {
        this.templateList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.debugOpen = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        templatename: null,
        noticetype: null,
        servicefactory: null,
        templatecode: null,
        templatesign: null,
        filePath: null,
        content: null,
        createTime: null,
        updateTime: null
      };
      this.fileList = [];
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
      this.changeServiceFactory(this.form.noticetype)

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
      this.title = "添加通知模板配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {

      this.reset();
      const id = row.id || this.ids
      getTemplate(id).then(response => {
        if (response.data.filePath != null && response.data.filePath.length != "") {
          let files = response.data.filePath.split(";")
          for (let index = 0; index < files.length; index++) {
            let fpath = files[index];
            if (fpath.length > 0) {
              var fname = fpath.substring(fpath.lastIndexOf('/') + 1, fpath.length);
              let file = {
                name: fname,
                url: fpath
              }
              this.fileList.push(file)
            }
          }
        }

        this.form = response.data;
        this.changeServiceFactory(this.form.noticetype)

        this.open = true;
        this.title = "修改通知模板配置";
      });
    },
    /** 提交按钮 */
    submitForm() {

      // this.$refs.upload.submit();
      let formData = new FormData();
      this.files.forEach((file) => {
        formData.append("files", file)
      })
      formData.append("path", process.env.VUE_APP_BASE_API)
      // 解析文件

      this.form.filePath = "";
      for (let i = 0; i < this.fileList.length; i++) {
        this.form.filePath = this.form.filePath + this.fileList[i].url + ";"
      }

      console.log(this.form.filePath);
      this.$refs["form"].validate(valid => {
        if (valid) {

          formData.append("formJson", JSON.stringify(this.form))
          if (this.form.id != null) {

            updateTemplate(formData).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTemplate(formData).then(response => {
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
      this.$modal.confirm('是否确认删除通知模板配置编号为"' + ids + '"的数据项？').then(function () {
        return delTemplate(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('noticeManage/template/export', {
        ...this.queryParams
      }, `template_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
