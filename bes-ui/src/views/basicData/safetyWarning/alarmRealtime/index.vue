<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="130px">
      <el-form-item label="告警值" prop="alarmValue">
        <el-input
          v-model="queryParams.alarmValue"
          placeholder="请输入告警值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="告警位置" prop="azwz">
        <el-input
          v-model="queryParams.azwz"
          placeholder="请输入告警位置"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="报警名称" prop="alarmName">
        <el-input
          v-model="queryParams.alarmName"
          placeholder="请输入报警名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="计划值" prop="planVal">
        <el-input
          v-model="queryParams.planVal"
          placeholder="请输入计划值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提示信息 " prop="promptMsg">
        <el-input
          v-model="queryParams.promptMsg"
          placeholder="请输入提示信息 "
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="告警等级 " prop="level">
        <el-select v-model="queryParams.level" placeholder="请选择告警等级" clearable>
          <el-option
            placeholder="请输入告警等级"
            v-for="dict in dict.type.alarm_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
            @keyup.enter.native="handleQuery"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="告警类型" prop="alarmTypeId">
        <el-select v-model="queryParams.alarmTypeId"
                   @change="alarmTypeChange"
                   placeholder="请选择告警类型" clearable>
          <el-option
            v-for="dict in dict.type.alarm_tactics_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
            @keyup.enter.native="handleQuery"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="告警策略" prop="alarmTacticsId"><!--v-show="queryParams.alarmTypeId!=5"-->
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

      <el-form-item label="最后一次产生时间">
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

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">

      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="multiple"
          @click="handleUpdateAlarm"
          v-hasPermi="['alarmRealtime:data:edit']"
        >处理报警
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
          v-hasPermi="['alarmRealtime:data:remove']"
        >删除
        </el-button>
      </el-col>
      <!--      <el-col :span="1.5">-->
      <!--        <el-button-->
      <!--          type="warning"-->
      <!--          plain-->
      <!--          icon="el-icon-download"-->
      <!--          size="mini"-->
      <!--          @click="handleExport"-->
      <!--          v-hasPermi="['alarmRealtime:data:export']"-->
      <!--        >导出-->
      <!--        </el-button>-->
      <!--      </el-col>-->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="dataList" height="55vh" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="告警策略" align="center" prop="alarmTacticsName" show-overflow-tooltip/>
      <el-table-column label="告警值" align="center" prop="alarmValue" show-overflow-tooltip/>
      <el-table-column label="第一次产生时间" align="center" prop="firstTime" width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.firstTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后一次产生时间" align="center" prop="lastTime" width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="告警次数" align="center" prop="amount" show-overflow-tooltip/>
      <el-table-column label="告警位置" align="center" prop="azwz" show-overflow-tooltip/>
      <el-table-column label="报警名称" align="center" prop="alarmName" show-overflow-tooltip/>
      <el-table-column label="计划值" align="center" prop="planVal" show-overflow-tooltip/>
      <el-table-column label="提示信息 " align="center" prop="promptMsg" show-overflow-tooltip/>
      <el-table-column label="告警等级 " align="center" prop="level" show-overflow-tooltip>
        <template slot-scope="scope">
          <dict-tag :options="dict.type.alarm_level" :value="scope.row.level"/>
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
            v-hasPermi="['alarmRealtime:data:list']"
          >查看
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['alarmRealtime:data:remove']"
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

    <!-- 查看实时数据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" label-width="140px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="告警策略" prop="alarmTacticsName">
              <el-input v-model="form.alarmTacticsName" readonly placeholder="请输入告警策略"/>
            </el-form-item>
            <el-form-item label="告警次数" prop="amount">
              <el-input v-model="form.amount" readonly placeholder="请输入告警次数"/>
            </el-form-item>

            <el-form-item label="计划值" prop="planVal">
              <el-input v-model="form.planVal" readonly placeholder="请输入计划值"/>
            </el-form-item>
            <el-form-item label="告警位置" prop="azwz">
              <el-input v-model="form.azwz" readonly placeholder="请输入告警位置"/>
            </el-form-item>
            <el-form-item label="告警等级" prop="level">
              <el-select v-model="form.level" disabled style="width: 100%;">
                <el-option
                  v-for="dict in dict.type.alarm_level"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="告警类型 " prop="alarmTypeId">
              <el-select v-model="form.alarmTypeId" disabled style="width: 100%;">
                <el-option
                  v-for="dict in dict.type.alarm_tactics_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="告警值" prop="alarmValue">
              <el-input v-model="form.alarmValue" readonly placeholder="请输入告警值"/>
            </el-form-item>
            <el-form-item label="报警名称" prop="alarmName">
              <el-input v-model="form.alarmName" readonly placeholder="请输入报警名称"/>
            </el-form-item>
            <el-form-item label="提示信息 " prop="promptMsg">
              <el-input v-model="form.promptMsg" readonly placeholder="请输入提示信息 "/>
            </el-form-item>
            <el-form-item label="第一次产生时间" prop="firstTime">
              <el-date-picker readonly
                              v-model="form.firstTime"
                              type="datetime"
                              style="width: 100%;"
                              value-format="yyyy-MM-dd HH:mm:ss"
              >
              </el-date-picker>
            </el-form-item>
            <el-form-item label="最后一次产生时间" prop="lastTime">
              <el-date-picker
                readonly
                v-model="form.lastTime"
                type="datetime"
                style="width: 100%;"
                value-format="yyyy-MM-dd HH:mm:ss"
              >
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listData,
    delData,
    updateAlarm,
    getDataByAlarmTypeId
  } from "@/api/basicData/safetyWarning/alarmRealtime/data";
  import {mapState} from 'vuex'

  export default {
    name: "Data",
    dicts: ['alarm_level', 'alarm_tactics_type'],
    computed: {
      ...mapState({
        //报警
        alarmList: state => state.websocket.alarmList,
      }),
    },
    data() {
      return {
        //报警策略
        alarmTacticsList: [],
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 选中的数据
        realTimeList: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 告警实时数据表格数据
        dataList: [],
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
          initVal: null,
          planVal: null,
          promptMsg: null,
          startTime: null,
          endTime: null,
          level: null,
          alarmTypeId: null
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
    watch: {
      alarmList(data) {
        if (data === null) {
          return
        }
        this.getList();
        this.$store.commit('ALARM_LIST', null)
      },
    },
    methods: {
      /** 查询告警实时数据列表 */
      alarmTypeChange() {
        if (this.queryParams.alarmTypeId /*&& this.queryParams.alarmTypeId != 5*/) {
          getDataByAlarmTypeId(
            {alarmTypeId: this.queryParams.alarmTypeId}
          ).then((res) => {
            this.alarmTacticsList = res.data
            this.queryParams.alarmTacticsId = null
          })
        } else {
          this.alarmTacticsList = []
          this.queryParams.alarmTacticsId = null
        }
      },
      /** 查询告警实时数据列表 */
      getList() {
        this.loading = true;
        if (this.pickerTime) {
          this.queryParams.startTime = this.pickerTime[0]
          this.queryParams.endTime = this.pickerTime[1]
        } else {
          this.queryParams.startTime = null;
          this.queryParams.endTime = null;
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
          firstTime: null,
          lastTime: null,
          amount: null,
          confirmState: null,
          createTime: null,
          updateTime: null,
          azwz: null,
          alarmName: null,
          initVal: null,
          planVal: null,
          promptMsg: null,
          alarmTime: null,
          level: null,
          alarmTypeId: null
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
        this.pickerTime = [];
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.realTimeList = selection;
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      // /** 新增按钮操作 */
      // handleAdd() {
      //   this.reset();
      //   this.open = true;
      //   this.title = "添加告警实时数据";
      // },
      /** 查看按钮操作 */
      handleLook(row) {
        this.reset();
        this.form = row
        this.open = true;
        this.title = "查看实时数据";
      },
      handleUpdateAlarm() {
        const data = this.realTimeList;
        this.$modal.confirm('是否处理告警实时数据？').then(function () {
          return updateAlarm(data);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("处理成功！");
        });
      },

      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$modal.confirm('是否确认删除告警实时数据？').then(function () {
          return delData(ids);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {
        });
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('alarmRealtime/data/export', {
          ...this.queryParams
        }, `data_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
