<template>
  <div class="app-container">
    <TextBroadcast :alertShow=true ref="textAudio" />

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="配置名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入通知配置名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="通知类型" prop="noticetype">
        <el-select v-model="queryParams.noticetype" placeholder="请选择通知类型" clearable>
          <el-option v-for="dict in dict.type.bes_notice_type" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="服务厂商" prop="servicefactory">
        <el-select v-model="queryParams.servicefactory" placeholder="请选择服务厂商" clearable>
          <el-option v-for="dict in dict.type.bes_notice_email_factory" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['system:config:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
          v-hasPermi="['system:config:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['system:config:remove']">删除</el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['system:config:export']">导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="通知配置id" align="center" prop="id" />
      <el-table-column label="通知配置名称" align="center" prop="name" />
      <el-table-column label="通知类型" align="center" prop="noticetype">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.bes_notice_type" :value="scope.row.noticetype" />
        </template>
      </el-table-column>
      <el-table-column label="服务厂商" align="center" prop="servicefactory" :formatter="serviceFactoryFormatter">
        <!-- <template slot-scope="scope">
          <dict-tag :options="allServiceFactoryList" :value="scope.row.servicefactory" />
        </template> -->
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['system:config:edit']">修改</el-button>

          <el-button size="mini" type="text" icon="el-icon-set-up" @click="handleDebug(scope.row)"
            v-hasPermi="['system:config:debug']">调试</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['system:config:remove']">删除</el-button>

          <el-button size="mini" type="text" icon="el-icon-tickets" @click="openSmsLog(scope.row)"
            v-hasPermi="['noticeManage:template:remove']">通知记录</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 添加或修改通知配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="通知类型" prop="noticetype">
          <el-select v-model="form.noticetype" placeholder="请选择通知类型" @change="noticeTypeChange"
            style="width:100%!important;">
            <el-option v-for="dict in dict.type.bes_notice_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="服务厂商" prop="servicefactory">
          <el-select v-model="form.servicefactory" placeholder="请选择服务厂商" style="width:100%!important;">
            <el-option v-for="dict in serviceFactoryList" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>


        <div v-if="form.servicefactory == 11">
          <!-- 邮件配置 -->
          <el-form-item label="发件人邮箱地址" prop="fromemail">
            <el-input v-model="form.fromemail" placeholder="请输入发件人邮箱地址" />
          </el-form-item>
          <el-form-item label="发件人邮箱密码" prop="fromemailpwd">
            <el-input v-model="form.fromemailpwd" placeholder="请输入发件人邮箱密码" />
          </el-form-item>
          <el-form-item label="邮箱服务地址" prop="emailServerHost">
            <el-input v-model="form.emailServerHost" placeholder="请输入邮箱服务地址" />
          </el-form-item>
          <el-form-item label="邮箱服务端口" prop="emailServerPort">
            <el-input v-model="form.emailServerPort" placeholder="请输入邮箱服务端口" />
          </el-form-item>
        </div>



        <div v-if="form.servicefactory == 21">
          <!-- 阿里云短信配置 -->
          <el-form-item label="regionid" prop="regionid">
            <el-input v-model="form.regionid" placeholder="请输入regionid" />
          </el-form-item>
          <el-form-item label="accesskeyid" prop="accesskeyid">
            <el-input v-model="form.accesskeyid" placeholder="请输入accesskeyid" />
          </el-form-item>
          <el-form-item label="secret" prop="secret">
            <el-input v-model="form.secret" placeholder="请输入secret" />
          </el-form-item>
        </div>
        <div v-if="form.servicefactory == 31">
          <!-- 阿里云语音配置 -->

          <el-form-item label="accesskeyid" prop="accesskeyid">
            <el-input v-model="form.accesskeyid" placeholder="请输入accesskeyid" />
          </el-form-item>
          <el-form-item label="secret" prop="secret">
            <el-input v-model="form.secret" placeholder="请输入secret" />
          </el-form-item>
          <el-form-item label="appkey" prop="appkey">
            <el-input v-model="form.appkey" placeholder="请输入appkey" />
          </el-form-item>
        </div>
        <div v-if="form.servicefactory == 32">
          <!-- 百度语音配置 -->

          <el-form-item label="token" prop="token">
            <el-input v-model="form.token" placeholder="请输入token" />
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
        <el-form-item label="配置名称" prop="configname">
          <el-input v-model="configname" placeholder="请输入配置名称" :disabled="true" />
        </el-form-item>


        <div v-if="debugform.type == 1 || debugform.type == 2">
          <el-form-item label="选择模板" prop="templateId">
            <el-select v-model="debugform.templateId" placeholder="请选择通知类型" @change="noticeTypeChange"
              style="width:100%!important;">
              <el-option v-for="dict in noticeTemplateList" :key="dict.id" :label="dict.templatename"
                :value="dict.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="接收人" prop="recipient">
            <el-input v-model="debugform.recipient" placeholder="请输入接收人" />
          </el-form-item>

          <el-form-item label="变量" prop="content">
            <el-input type="textarea" :rows="2"
              placeholder='请输入模板中定义的变量使用使用大括号包起来，多个变量使用逗号分割列如：{"name":"张三","age":"18"}。注意：所有符号请使用英文符号！'
              v-model="debugform.content">
            </el-input>
          </el-form-item>
        </div>
        <div v-if="debugform.type == 3">
          <el-form-item label="播报内容" prop="content">
            <el-input type="textarea" :rows="2" placeholder='请输入语音播报的内容！' v-model="debugform.content">
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
import { addNoticeConfig, listNoticeConfig, updateNoticeConfig, delNoticeConfig, getNoticeConfig, getAllServiceFactory, getNoticeTemplatebyConfig, debugingConfig } from "@/api/noticeManage/noticeConfig";
import TextBroadcast from '@/components/TextBroadcast/index'
import noticeLogList from '../component/noticeLogList.vue'

export default {
  name: "Config",

  dicts: ['bes_notice_type', 'bes_notice_email_factory', 'bes_notice_sms_factory', 'bes_notice_voice_factory'],
  components: {
    TextBroadcast,
    noticeLogList

  },
  data() {
    // 邮箱校验
    var checkEmail = (rule, value, callback) => {
      if (!value) {
        return callback()
      }
      if (value) {
        var reg = /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/
        if (!reg.test(value)) {
          callback(new Error('当前邮箱格式输入错误!'))
        } else {
          callback()
        }
      }
    }
    //端口校验
    var checkPort = (rule, value, callback) => {
      if (!value) {
        return callback()
      }
      if (value) {
        var reg = /^[0-9]*$/;
        if (reg.test(value)) {
          callback(new Error('端口只允许输入数字'))
        } else {
          callback()
        }
      }
    }
    return {
      noticeLogOpen: false,

      configname: "",
      // 服务厂商列表
      serviceFactoryList: [],
      // 全部服务厂商列表
      allServiceFactoryList: [],
      // 模板列表
      noticeTemplateList: [],
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
      // 通知配置表格数据
      configList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示调试窗口
      debugOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        noticetype: null,
        servicefactory: null,
      },
      // 调试参数
      debugform: {},
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "配置名称不能为空", trigger: "blur" }
        ],
        servicefactory: [
          { required: true, message: "服务厂商不能为空", trigger: "blur" }
        ],
        noticetype: [
          { required: true, message: "通知类型不能为空", trigger: "blur" }
        ],

        // 邮箱
        fromemail: [
          { required: true, message: "发件人邮箱地址不能为空", trigger: "blur" }
          , { validator: checkEmail }
        ],
        fromemailpwd: [
          { required: true, message: "发件人邮箱密码不能为空", trigger: "blur" }
        ],
        emailServerPort: [
          { required: true, message: "邮箱服务端口不能为空", trigger: "blur" }
          // ,{validator: checkPort}

        ],
        emailServerHost: [
          { required: true, message: "邮箱服务地址不能为空", trigger: "blur" }
        ],
        // 语音播报 +短信
        regionid: [
          { required: true, message: "阿里云regionid不能为空", trigger: "blur" }
        ],
        accesskeyid: [
          { required: true, message: "阿里云accesskeyid不能为空", trigger: "blur" }
        ],
        secret: [
          { required: true, message: "阿里云secret不能为空", trigger: "blur" }
        ],
        token: [
          { required: true, message: "百度云token不能为空", trigger: "blur" }
        ],
        appkey: [
          { required: true, message: "阿里云appkey不能为空", trigger: "blur" }

        ]


      },
      rulesdebug: {
        templateId: [
          { required: true, message: "模板不能为空", trigger: "blur" }
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
    openSmsLog(row) {
      this.noticeLogOpen = true;
      this.$nextTick(() => {
        console.log(this.$refs.noticeLog);
        this.$refs.noticeLog.configId = row.id;
        this.$refs.noticeLog.getNoticeLogList();
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
    AllServiceFactory() {
      getAllServiceFactory().then(response => {
        this.allServiceFactoryList = response.data;
      })
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

    /** 查询通知配置列表 */
    getList() {
      this.loading = true;
      listNoticeConfig(this.queryParams).then(response => {
        this.configList = response.rows;
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
        name: null,
        noticetype: null,
        servicefactory: null,
        regionid: null,
        accesskeyid: null,
        secret: null,
        appkey: null,
        fromemail: null,
        fromemailpwd: null,
        recipient: null,
        createTime: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    resetDebugForm() {
      this.debugform = {};
      this.resetForm("debugform")
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
      this.title = "添加通知配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getNoticeConfig(id).then(response => {
        this.form = response.data;
        this.changeServiceFactory(this.form.noticetype)

        this.open = true;
        this.title = "修改通知配置";
      });
    },
    // 调试操作按钮
    handleDebug(row) {

      getNoticeTemplatebyConfig(row).then(response => {

        this.resetDebugForm();
        // console.log(row,'-----------------------------');
        this.configname = row.name;
        this.debugform.configId = row.id;
        this.debugform.type = row.noticetype;
        this.debugform.factory = row.servicefactory;
        this.debugform.appkey=row.appkey;
        // console.log(response, "====================");
        this.noticeTemplateList = response.data;
        this.debugOpen = true;
        this.title = "调试通知配置";
      });
    },
    /** 提交调试按钮 */
    submitDebugForm() {
      this.$refs["debugform"].validate(valid => {
        if (valid) {
          debugingConfig(this.debugform).then(response => {
            console.log(response);
            this.$modal.msgSuccess("指令发送成功");
            this.debugOpen = false;

            if (this.debugform.type == 3) {
              this.$refs.textAudio.playoneAudio(response.msg, this.debugform.content, this.debugform.factory, this.debugform.appkey)
            }
          });
        }
      });
    },
    /** 提交按钮 */
    submitForm() {

      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateNoticeConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addNoticeConfig(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除通知配置编号为"' + ids + '"的数据项？').then(function () {
        return delNoticeConfig(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/config/export', {
        ...this.queryParams
      }, `config_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
