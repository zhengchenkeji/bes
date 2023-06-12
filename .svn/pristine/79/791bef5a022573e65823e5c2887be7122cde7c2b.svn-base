<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="策略名称" prop="alarmTacticsName">
        <el-input
          v-model="queryParams.alarmTacticsName"
          placeholder="请输入告警策略名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="告警值" prop="alarmValue">
        <el-input
          v-model="queryParams.alarmValue"
          placeholder="请输入告警值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="告警描述" prop="description">
        <el-input
          v-model="queryParams.description"
          placeholder="请输入告警描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="告警时间">
        <el-date-picker
          clearable
          v-model="pickerTime"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="至"
          start-placeholder="开始日期"
          :picker-options="pickerOptions"
          end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="告警类型" prop="alarmTypeId">
        <el-select v-model="queryParams.alarmTypeId"
                   @change="alarmTypeChange"
                   placeholder="请选择告警类型"
                   clearable>
          <el-option
            v-for="dict in dict.type.alarm_tactics_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
            @keyup.enter.native="handleQuery"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="告警策略" prop="alarmTacticsId"  v-show="queryParams.alarmTypeId!=5">
        <el-select v-model="queryParams.alarmTacticsId"
                   :placeholder="queryParams.alarmTypeId ?'请选择告警策略！':'请先选择告警类型！'" clearable
        >
          <el-option
            v-for="item in alarmTacticsList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
            @keyup.enter.native="handleQuery"
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
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['alarmHistoricalData:data:remove']"
        >删除</el-button>
      </el-col>
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="warning"-->
<!--          plain-->
<!--          icon="el-icon-download"-->
<!--          size="mini"-->
<!--          @click="handleExport"-->
<!--          v-hasPermi="['alarmHistoricalData:data:export']"-->
<!--        >导出</el-button>-->
<!--      </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="告警策略名称" align="center" prop="alarmTacticsName"  show-overflow-tooltip/>
      <el-table-column label="告警值" align="center" prop="alarmValue"  show-overflow-tooltip/>
      <el-table-column label="告警描述" align="center" prop="description"  show-overflow-tooltip/>
      <el-table-column label="告警时间" align="center" prop="alarmTime" width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.alarmTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="告警类型" align="center" prop="alarmTypeId" show-overflow-tooltip>
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_tactics_type" :value="scope.row.alarmTypeId"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleLook(scope.row)"
            v-hasPermi="['alarmHistoricalData:data:list']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['alarmHistoricalData:data:remove']"
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

    <!-- 添加或修改告警历史数据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="策略名称" prop="alarmTacticsName">
          <el-input v-model="form.alarmTacticsName" readonly />
        </el-form-item>
        <el-form-item label="告警值" prop="alarmValue">
          <el-input v-model="form.alarmValue" readonly  />
        </el-form-item>
        <el-form-item label="告警描述" prop="description">
          <el-input v-model="form.description" readonly  />
        </el-form-item>
        <el-form-item label="告警类型 " prop="alarmTypeId">
          <el-select v-model="form.alarmTypeId"  style="width: 100%" disabled>
            <el-option
              v-for="dict in dict.type.alarm_tactics_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="告警时间" prop="alarmTime">
          <el-date-picker
            readonly
            v-model="form.alarmTime"
            type="datetime"
            style="width: 100%;"
            value-format="yyyy-MM-dd HH:mm:ss"
           >
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listData, delData,getDataByAlarmTypeId } from "@/api/basicData/safetyWarning/alarmHistoricalData/data";

export default {
  name: "Data",
  dicts: ['alarm_tactics_type'],
  data() {
    return {
      //告警配置
      alarmTacticsList:[],
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
      // 告警历史数据表格数据
      dataList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        alarmTacticsName: null,
        alarmTacticsId: null,
        alarmValue: null,
        description: null,
        startTime: null,
        endTime: null,
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      },
      //时间
      pickerTime: [],

      // 表单参数
      form: {},

    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询告警实时数据列表 */
    alarmTypeChange() {
      if (this.queryParams.alarmTypeId  && this.queryParams.alarmTypeId!=5){
        getDataByAlarmTypeId(
          {alarmTypeId: this.queryParams.alarmTypeId}
        ).then((res) => {
          this.alarmTacticsList = res.data
          this.queryParams.alarmTacticsId = null
        })
      }else {
        this.alarmTacticsList = []
        this.queryParams.alarmTacticsId = null
      }
    },
    /** 查询告警历史数据列表 */
    getList() {
      this.loading = true;
      if (this.pickerTime){
        this.queryParams.startTime=this.pickerTime[0]
        this.queryParams.endTime=this.pickerTime[1]
      }else {
        this.queryParams.startTime=null;
        this.queryParams.endTime=null;
      }
      listData(this.queryParams).then(response => {
        this.dataList = response.rows;
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
        description: null,
        alarmTime: null,
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
      this.pickerTime=[];
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    /** 查看按钮操作 */
    handleLook(row) {
      this.reset();
      this.form=row
      this.open = true;
      this.title = "查看历史数据";
    },
    // /** 提交按钮 */
    // submitForm() {
    //   this.$refs["form"].validate(valid => {
    //     if (valid) {
    //       if (this.form.id != null) {
    //         updateData(this.form).then(response => {
    //           this.$modal.msgSuccess("修改成功");
    //           this.open = false;
    //           this.getList();
    //         });
    //       } else {
    //         addData(this.form).then(response => {
    //           this.$modal.msgSuccess("新增成功");
    //           this.open = false;
    //           this.getList();
    //         });
    //       }
    //     }
    //   });
    // },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除告警历史数据？').then(function() {
        return delData(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('alarmHistoricalData/data/export', {
        ...this.queryParams
      }, `data_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
