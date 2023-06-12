<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="场景信息" prop="sceneId">
        <el-select v-model="queryParams.sceneId" placeholder="请选择场景"  style="width: 150px;">
                <el-option v-for="item in sceneList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
      </el-form-item>
      <el-form-item label="触发方式" prop="triggerModeCode">
        <el-select v-model="queryParams.triggerModeCode" style="width: 200px;" placeholder="请选择触发方式">
                <el-option v-for="dict in dict.type.trigger_code" :key="dict.value" :label="dict.label"
                  :value="parseInt(dict.value)"></el-option>
          </el-select>
      </el-form-item>
      <!-- <el-form-item label="触发器ID" prop="triggerId">
        <el-input
          v-model="queryParams.triggerId"
          placeholder="请输入触发器ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item> -->
      
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- <el-row :gutter="10" class="mb8">
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
      <el-table-column label="编号" align="center" prop="id" width="180" />
      <!-- <el-table-column label="场景ID" align="center" prop="sceneId" /> -->
      <el-table-column label="场景名称" align="center" prop="sceneName"  width="180"/>
      <!-- <el-table-column label="触发方式" align="center" prop="triggerModeCode" /> -->
      <el-table-column label="触发方式 " align="center" prop="triggerModeCode" width="180">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.trigger_code" :value="scope.row.triggerModeCode" />
        </template>
      </el-table-column>
      <el-table-column label="触发内容" align="center" prop="triggerContent" />
      <!-- <el-table-column label="触发器ID" align="center" prop="triggerId" /> -->
      <!-- <el-table-column label="触发时的执行器ids ，多个按照逗号分割" align="center" prop="actuatorIds" /> -->
      <!-- <el-table-column label="执行内容" align="center" prop="actuatorContent" /> -->
      <el-table-column label="执行时间" align="center" prop="executeTime" width="230">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.executeTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
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

    <!-- 添加或修改场景联动-执行日志对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="场景名称" prop="sceneName">
          <el-input v-model="form.sceneName" placeholder="请输入场景名称" />
        </el-form-item>
        <el-form-item label="触发方式" prop="triggerModeCode">
          <el-input v-model="form.triggerModeCode" placeholder="请输入触发方式" />
        </el-form-item>
        <el-form-item label="触发内容">
          <editor v-model="form.triggerContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="触发器ID" prop="triggerId">
          <el-input v-model="form.triggerId" placeholder="请输入触发器ID" />
        </el-form-item>
        <el-form-item label="触发时的执行器ids ，多个按照逗号分割" prop="actuatorIds">
          <el-input v-model="form.actuatorIds" placeholder="请输入触发时的执行器ids ，多个按照逗号分割" />
        </el-form-item>
        <el-form-item label="执行内容">
          <editor v-model="form.actuatorContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="执行时间" prop="executeTime">
          <el-date-picker clearable
            v-model="form.executeTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择执行时间">
          </el-date-picker>
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
import { listLog, getLog, delLog, addLog, updateLog,listSceneDic,getNoticeConfigListByType,getNoticeTemplateListByConfig } from "@/api/ruleEngine/scenelog/sceneLog";

export default {
  dicts: ['trigger_code'],
  name: "sceneLog",
  data() {
    return {
      sceneList:[],
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
      // 场景联动-执行日志表格数据
      logList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        sceneId: null,
        sceneName: null,
        triggerModeCode: null,
        triggerContent: null,
        triggerId: null,
        actuatorIds: null,
        actuatorContent: null,
        executeTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {

    const sceneid = this.$route.query.sceneid;
    if(sceneid){
      this.queryParams.sceneId=parseInt(sceneid);
    }
    this.getList();
    this.getlistSceneDic();
  },
  methods: {

    
    getlistSceneDic(){
      listSceneDic().then(response=>{
        this.sceneList=response.rows;
      })
    },
    /** 查询场景联动-执行日志列表 */
    getList() {
      this.loading = true;
      listLog(this.queryParams).then(response => {
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
        sceneId: null,
        sceneName: null,
        triggerModeCode: null,
        triggerContent: null,
        triggerId: null,
        actuatorIds: null,
        actuatorContent: null,
        executeTime: null
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
      this.title = "添加场景联动-执行日志";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getLog(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改场景联动-执行日志";
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
      this.$modal.confirm('是否确认删除场景联动-执行日志编号为"' + ids + '"的数据项？').then(function() {
        return delLog(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
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
