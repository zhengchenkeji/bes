<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      
      <el-form-item label="报警名称" prop="alarmName">
        <el-input v-model="queryParams.alarmName" placeholder="请输入报警名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="是否处理" prop="status">
        <el-select v-model="queryParams.status"  placeholder="是否处理"  >
                <el-option v-for="dict in dict.type.bes_alarm_handle" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
      </el-form-item>
      <el-form-item label="告警等级" prop="level">
      
        <el-select v-model="queryParams.level" style="width: 200px;" placeholder="告警等级" >
                <el-option v-for="dict in dict.type.alarm_level" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
      </el-form-item>
      <el-form-item label="告警类型 " prop="alarmTypeId">
        <el-select v-model="queryParams.alarmTypeId" style="width: 200px;" placeholder="告警类型 " >
                <el-option v-for="dict in dict.type.alarm_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
          v-hasPermi="['safetyWarning:alarmWorkOrder:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="alarmWorkOrderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!-- <el-table-column label="主键" align="center" prop="id" /> -->
      <el-table-column label="所属告警策略" align="center" prop="alarmName" />
      <el-table-column label="告警值" align="center" prop="alarmValue" />
      <el-table-column label="第一次产生时间" align="center" prop="firstTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.firstTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后一次产生时间" align="center" prop="lastTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="告警次数" align="center" prop="amount" />
      <!-- <el-table-column label="确认状态" align="center" prop="confirmState" /> -->
      <!-- <el-table-column label="告警位置" align="center" prop="azwz" /> -->
      <!-- <el-table-column label="报警名称" align="center" prop="alarmName" /> -->
      <el-table-column label="计划值" align="center" prop="planVal" />
      <!-- <el-table-column label="提示信息 " align="center" prop="promptMsg" /> -->
      <el-table-column label="告警等级" align="center" prop="level">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_level" :value="scope.row.level" />
        </template>
      </el-table-column>
      <!-- <el-table-column label="所属告警类型 " align="center" prop="alarmTypeId" /> -->
      <el-table-column label="所属告警类型" align="center" prop="alarmTypeId">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_type" :value="scope.row.alarmTypeId" />

        </template>
      </el-table-column>
      <!-- <el-table-column label="所属用户id" align="center" prop="userId" />
      <el-table-column label="处理人编码" align="center" prop="updateCode" /> -->
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">


          <dict-tag style="color: red;" v-if="scope.row.status == 0" :options="dict.type.bes_alarm_handle"
            :value="scope.row.status" />
          <dict-tag style="color: green;" v-if="scope.row.status == 1" :options="dict.type.bes_alarm_handle"
            :value="scope.row.status" />

        </template>
      </el-table-column>
      <el-table-column label="处理人" align="center" prop="updateName" />

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" v-if="scope.row.status==0" @click="handleUpdate(scope.row)"
            >处理</el-button>
            <el-button size="mini" type="text" icon="el-icon-view" v-if="scope.row.status==1" @click="handleUpdate(scope.row)"
            >查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <!-- 添加或修改告警工单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="720px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="报警名称" prop="alarmName">
              <el-input v-model="form.alarmName" placeholder="请输入报警名称" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="告警次数" prop="amount">
              <el-input v-model="form.amount" placeholder="请输入告警次数" readonly />
            </el-form-item>
          </el-col>

        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="计划值" prop="planVal">
              <el-input v-model="form.planVal" placeholder="请输入计划值" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="告警值" prop="alarmValue">
              <el-input v-model="form.alarmValue" placeholder="请输入告警值" readonly />
            </el-form-item>
          </el-col>

        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="首次触发时间" prop="firstTime">
              <el-input v-model="form.firstTime" placeholder="第一次产生时间" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最后触发时间" prop="lastTime">
              <el-input v-model="form.lastTime" placeholder="最后一次产生时间" readonly />
            </el-form-item>
          </el-col>
        </el-row>


        <el-row>
          <el-col :span="12">
            <el-form-item label="告警等级" prop="level">
              <!-- <el-input v-model="form.level" placeholder="请输入告警等级 1：一般、2：较大、3：严重" /> -->
              <el-select v-model="form.level" style="width: 200px;" placeholder="告警等级" disabled="true" >
                <el-option v-for="dict in dict.type.alarm_level" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属告警类型" prop="alarmTypeId">
              <!-- <el-input v-model="form.alarmTypeId" placeholder="请输入所属告警类型 1:电表  2:支路  3:分户 4:分项 5:设备报警" /> -->
              <el-select v-model="form.alarmTypeId" style="width: 200px;" placeholder="告警等级 "  disabled="true">
                <el-option v-for="dict in dict.type.alarm_type" :key="dict.value" :label="dict.label"
                  :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="告警位置" prop="azwz">
              <el-input v-model="form.azwz" placeholder="请输入告警位置" readonly />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="提示信息 " prop="promptMsg">
              <el-input v-model="form.promptMsg" placeholder="请输入提示信息" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.status==1">
          <el-col :span="12">
            <el-form-item label="处理人" prop="promptMsg">
              <el-input v-model="form.updateName" placeholder="处理人" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理时间" prop="promptMsg">
              <el-input v-model="form.updateTime" placeholder="处理时间" readonly />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="备注信息 " prop="remark">
              <el-input v-model="form.remark"  type="textarea" :readonly="form.status==1" placeholder="处理时请输入备注信息" />
            </el-form-item>
          </el-col>
        </el-row>


        <!-- <el-form-item label="所属用户id" prop="userId">
          <el-input v-model="form.userId" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="处理人编码" prop="updateCode">
          <el-input v-model="form.updateCode" placeholder="请输入处理人编码" />
        </el-form-item>
        <el-form-item label="处理人" prop="updateName">
          <el-input v-model="form.updateName" placeholder="请输入处理人" />
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer" v-if="form.status==0">
        <el-button type="primary" @click="submitForm">处 理</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
      <div slot="footer" class="dialog-footer" v-if="form.status==1">
        <el-button type="primary" @click="cancel">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAlarmWorkOrder, getAlarmWorkOrder, addAlarmWorkOrder, updateAlarmWorkOrder } from "@/api/basicData/safetyWarning/alarmWorkOrder/alarmWorkOrder";

export default {
  name: "AlarmWorkOrder",
  dicts: ['alarm_level', 'athena_bes_yes_no', 'bes_alarm_handle', 'alarm_type'],
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
      // 告警工单表格数据
      alarmWorkOrderList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        alarmTacticsId: null,
        alarmValue: null,
        firstTime: null,
        lastTime: null,
        amount: null,
        confirmState: null,
        azwz: null,
        alarmName: null,
        planVal: null,
        promptMsg: null,
        level: null,
        alarmTypeId: null,
        userId: null,
        updateCode: null,
        updateName: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        remark: [
          { required: true, message: "备注信息不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询告警工单列表 */
    getList() {
      this.loading = true;
      listAlarmWorkOrder(this.queryParams).then(response => {
        this.alarmWorkOrderList = response.rows;
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
        alarmTacticsId: null,
        alarmValue: null,
        firstTime: null,
        lastTime: null,
        amount: null,
        confirmState: null,
        azwz: null,
        alarmName: null,
        planVal: null,
        promptMsg: null,
        level: null,
        alarmTypeId: null,
        userId: null,
        createTime: null,
        updateTime: null,
        updateCode: null,
        updateName: null,
        status: "0"
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

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getAlarmWorkOrder(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "处理告警工单";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAlarmWorkOrder(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAlarmWorkOrder(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('safetyWarning/alarmWorkOrder/export', {
        ...this.queryParams
      }, `alarmWorkOrder_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
