<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="告知方式 " prop="informManner">
        <el-select v-model="queryParams.informManner" placeholder="请选择告知方式 " clearable>
          <el-option
            v-for="dict in dict.type.alarm_inform_manner"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="告警策略 " prop="alarmTacticsId">
        <el-select v-model="queryParams.alarmTacticsId" placeholder="请选择所属告警策略 " clearable>
          <el-option
            v-for="tactics in AlarmTacticsDicData"
            :key="tactics.code"
            :label="tactics.name"
            :value="tactics.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item label-width="100px" label="是否发送成功" prop="isSendSucceed">
        <el-select v-model="queryParams.isSendSucceed" placeholder="请选择是否发送成功" clearable>
          <el-option
            v-for="dict in dict.type.athena_bes_yes_no"
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
  <el-row>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['alarmTactics:alarmTactics:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="alarmTacticsList" @selection-change="handleSelectionChange" style="overflow:auto;height: 583px;">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <!-- <el-table-column label="主键" align="center" prop="id" /> -->
      <el-table-column label="所属告警策略" align="center" prop="alarmTacticsId" >
        <template slot-scope="scope">
          <span>{{ getdicname(AlarmTacticsDicData,scope.row.alarmTacticsId) }}</span>
        </template>

      </el-table-column>
      <el-table-column label="告知方式 " align="center" prop="informManner">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_inform_manner" :value="scope.row.informManner"/>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="sendTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.sendTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否发送成功" align="center" prop="isSendSucceed">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.athena_bes_yes_no" :value="scope.row.isSendSucceed"/>
        </template>
      </el-table-column>
      <el-table-column label="告警信息" align="center" prop="alarmMessage" />
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>
<script>
import { listAlarmTactics, getAlarmTactics, delAlarmTactics, addAlarmTactics, updateAlarmTactics } from "@/api/basicData/safetyWarning/notificationRecord/notificationRecord";
import {getAlarmTacticsDicData } from "@/api/basicData/safetyWarning/alarmTactics/alarmTactics";

export default {
  name: "AlarmTactics",
  dicts: ['alarm_inform_manner', 'athena_bes_yes_no'],
  data() {
    return {
      // 遮罩层
      loading: false,
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
      // 告警通知记录表格数据
      alarmTacticsList: [],
      AlarmTacticsDicData:[],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        alarmTacticsId: null,
        informManner: null,
        sendTime: null,
        isSendSucceed: null,
        alarmMessage: null,
      }
    };
  },
  created() {
    this.getList();
    // this.getTacticsDicData();


    getAlarmTacticsDicData().then(response=>{
        this.AlarmTacticsDicData=response.data;
      })
  },
  methods: {
    // 查询所有的告警策略

    /** 查询告警通知记录列表 */
    getList() {
      this.loading = true;
      listAlarmTactics(this.queryParams).then(response => {
        this.alarmTacticsList = response.rows;
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
        informManner: null,
        sendTime: null,
        isSendSucceed: null,
        alarmMessage: null,
        createTime: null
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

    /** 导出按钮操作 */
    handleExport() {
      this.download('/safetyWarning/NotificationRecord/export', {
        ...this.queryParams
      }, `告警通知记录.xlsx`)
    },

    getdicname(list,code){
      var value = ''
      list.map(i => {
        if (i.code == code) {
          value = i.name
          return value;
        }
      })
      return value;
    }
  }

};
</script>
