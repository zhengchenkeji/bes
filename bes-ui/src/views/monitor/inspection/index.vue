<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="巡检名称" prop="jobName">
        <el-input
          v-model="queryParams.jobName"
          placeholder="请输入巡检名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="巡检状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择巡检状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_job_status"
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['monitor:job:add']"
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
          v-hasPermi="['monitor:job:edit']"
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
          v-hasPermi="['monitor:job:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-s-operation"
          size="mini"
          @click="handleJobLog"
          v-hasPermi="['monitor:job:query']"
        >日志</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="jobList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="任务名称" align="center" prop="jobName" :show-overflow-tooltip="true" />

      <el-table-column label="cron执行表达式" align="center" prop="cronExpression" :show-overflow-tooltip="true" />
      <el-table-column label="点位" width="100" align="center" prop="id" >
        <template slot-scope="scope">
        <el-button
          size="mini"
          type="text"
          icon="el-icon-view"
          @click="getPointList(scope.row)"
        >查看点位</el-button>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['monitor:job:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['monitor:job:remove']"
          >删除</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)" v-hasPermi="['monitor:job:changeStatus', 'monitor:job:query']">
            <span class="el-dropdown-link">
              <i class="el-icon-d-arrow-right el-icon--right"></i>更多
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="handleRun" icon="el-icon-caret-right"
                                v-hasPermi="['monitor:job:changeStatus']">执行一次</el-dropdown-item>
              <el-dropdown-item command="handleView" icon="el-icon-view"
                                v-hasPermi="['monitor:job:query']">任务详细</el-dropdown-item>
              <el-dropdown-item command="handleJobLog" icon="el-icon-s-operation"
                                v-hasPermi="['monitor:job:query']">调度日志</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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
    <!-- 设备树形数据-->
    <el-drawer size='28%'  :title="drawertitle" :visible.sync="visible"  direction="rtl">
      <div style="height: 68vh;overflow:auto">
      <el-tree
        :data="treedata"
        :props="props"
        show-checkbox
        node-key="id"
        :default-checked-keys="treeids"
        ref="tree"
        @check-change="handleCheckChange">
      </el-tree>
      </div>
      <div style="text-align: center;">
        <el-button type="primary" @click="savetree" >保存</el-button>
      </div>
    </el-drawer>
    <!-- 添加或修改定时任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务名称" prop="jobName">
              <el-input v-model="form.jobName" placeholder="请输入任务名称" />
            </el-form-item>
          </el-col>
<!--          <el-col :span="24">-->
<!--            <el-form-item prop="invokeTarget">-->
<!--              <span slot="label">-->
<!--                调用方法-->
<!--                <el-tooltip placement="top">-->
<!--                  <div slot="content">-->
<!--                    Bean调用示例：ryTask.ryParams('ry')-->
<!--                    <br />Class类调用示例：com.ruoyi.quartz.task.RyTask.ryParams('ry')-->
<!--                    <br />参数说明：支持字符串，布尔类型，长整型，浮点型，整型-->
<!--                  </div>-->
<!--                  <i class="el-icon-question"></i>-->
<!--                </el-tooltip>-->
<!--              </span>-->
<!--              <el-input v-model="form.invokeTarget" placeholder="请输入调用目标字符串" />-->
<!--            </el-form-item>-->
<!--          </el-col>-->

          <el-col :span="24">
            <el-form-item label="cron表达式" prop="cronExpression">
              <el-input v-model="form.cronExpression" placeholder="请输入cron执行表达式">
                <template slot="append">
                  <el-button type="primary" @click="handleShowCron">
                    生成表达式
                    <i class="el-icon-time el-icon--right"></i>
                  </el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="设备" prop="equipmentName">
              <el-input  v-model="form.equipmentName"   readonly  placeholder="请选择设备">
                <template slot="append">
                  <el-button type="primary" @click="selectDevice">
                    请选择设备
                    <i class="el-icon-time el-icon--right"></i>
                  </el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input  v-model="form.remark"    placeholder="备注">
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="执行策略" prop="misfirePolicy">
              <el-radio-group v-model="form.misfirePolicy" size="small">
                <el-radio-button label="1">立即执行</el-radio-button>
                <el-radio-button label="2">执行一次</el-radio-button>
                <el-radio-button label="3">放弃执行</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否并发" prop="concurrent">
              <el-radio-group v-model="form.concurrent" size="small">
                <el-radio-button label="0">允许</el-radio-button>
                <el-radio-button label="1">禁止</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_job_status"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- Cron表达式生成器 -->
    <el-dialog title="Cron表达式生成器" :visible.sync="openCron" append-to-body destroy-on-close class="scrollbar">
      <crontab @hide="openCron=false" @fill="crontabFill" :expression="expression"></crontab>
    </el-dialog>
    <!--查看点位-->
    <el-dialog title="点位" :visible.sync="pointTableVisible">
      <el-table :data="pointListData">
        <el-table-column property="pointAllName" label="点" ></el-table-column>
        <el-table-column property="pointPsysName" label="父节点" ></el-table-column>
      </el-table>
      <pagination
        v-show="PointTotal>0"
        :total="PointTotal"
        :page.sync="pointqueryParams.pageNum"
        :limit.sync="pointqueryParams.pageSize"
        @pagination="pointListData"
      />

    </el-dialog>
    <!-- 任务日志详细 -->
    <el-dialog title="任务详细" :visible.sync="openView" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="120px" size="mini">
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务编号：">{{ form.jobId }}</el-form-item>
            <el-form-item label="任务名称：">{{ form.jobName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务分组：">{{ jobGroupFormat(form) }}</el-form-item>
            <el-form-item label="创建时间：">{{ form.createTime }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="cron表达式：">{{ form.cronExpression }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次执行时间：">{{ parseTime(form.nextValidTime) }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="调用目标方法：">{{ form.invokeTarget }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="点位：">{{ form.equipmentName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务状态：">
              <div v-if="form.status == 0">正常</div>
              <div v-else-if="form.status == 1">暂停</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否并发：">
              <div v-if="form.concurrent == 0">允许</div>
              <div v-else-if="form.concurrent == 1">禁止</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行策略：">
              <div v-if="form.misfirePolicy == 3">默认策略</div>
              <div v-else-if="form.misfirePolicy == 1">立即执行</div>
              <div v-else-if="form.misfirePolicy == 2">执行一次</div>
              <div v-else-if="form.misfirePolicy == 3">放弃执行</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openView = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>
<script>
  import { getDeviceTreee,listDevicejob,addDevicejob,updateJob,getPointList,getDeviceJob,getChcekNodes,delDeviceJob,changeJobStatus,runJob } from "@/api/monitor/inspection";
  import Crontab from '@/components/Crontab'
  export default {
    components: { Crontab },
    name: "Job",
    dicts: ['sys_job_group', 'sys_job_status'],
    data() {
      return {
        pointTableVisible:false,//点位列表默认不展示
        treedata:[],
        treeids:[],//选中设备的id
        pointListData:[],//点位数据
        equipmentNameStr:"",
        props: {
          children: 'children',
          label: 'label'
        },
        updatejobid:null,
        count: 1,
        //抽屉层
        visible:false,
        //抽屉标题
        drawertitle:"",
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 选中数组
        jobNames: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        // 点位总条数
        PointTotal: 0,
        // 定时任务表格数据
        jobList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 是否显示详细弹出层
        openView: false,
        // 是否显示Cron表达式弹出层
        openCron: false,
        // 传入的表达式
        expression: "",
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          jobName: undefined,
          status: undefined,
          remark: ""
        },
        pointqueryParams: {//查询参数
          pageNum: 1,
          pageSize: 10,
          id: undefined,
        },
        // 表单参数
        form: {
          equipmentName:"",
        },
        // 表单校验
        rules: {
          jobName: [
            { required: true, message: "任务名称不能为空", trigger: "blur" }
          ],
          // invokeTarget: [
          //   { required: true, message: "调用目标字符串不能为空", trigger: "blur" }
          // ],
          cronExpression: [
            { required: true, message: "cron执行表达式不能为空"}
          ],
          equipmentName: [
            { required: true, message: "设备不能为空" }
          ]
        }
      };
    },
    created() {
      this.getList();
      this.getDeviceTreeList()

    },
    computed:{
      equipmentName: function () {
        return this.form.equipmentName
      },
    },
    methods: {
      // 任务组名字典翻译
      jobGroupFormat(row, column) {
        return this.selectDictLabel(this.dict.type.sys_job_group, row.jobGroup);
      },
      getChcekNodes(){
        if(this.updatejobid!=null) {
          getChcekNodes(this.updatejobid).then(response => {
            this.treeids=response.data;
            this.$refs.tree.setCheckedKeys([]);
            this.updatejobid=null;
            console.log(this.treeids);
            // console.log(response.data)
          })
        }
      },
      getPointList(row){
        this.pointqueryParams.id=row.jobId;
        getPointList(this.pointqueryParams).then(response=>{
          this.pointListData=response.rows;
          this.PointTotal= response.total;
          this.pointTableVisible=true;
        })
      },
      /**选择设备保存方法*/
      savetree(){
        this.$set(this.form,'equipmentName',this.equipmentNameStr);

        this.visible=false;
      },
      /*获取树节点*/
      getDeviceTreeList(){
        getDeviceTreee().then(response=>{
          if(response.code==200){
            response.data.forEach(val => {
              if (!val.energyNode) {
                val.disabled = true
              }
              val.id=val.deviceTreeId;
              val.label=val.sysName;
            })
            this.treedata=this.handleTree(response.data, "deviceTreeId", "deviceTreeFatherId");
          }
        })
      },

      /**********树节点 选中事件***********/
      handleCheckChange(data, checked, indeterminate) {
        let res = this.$refs.tree.getCheckedNodes()
        this.treeids=[];
        this.equipmentNameStr="";
        res.forEach((item) => {
          if(item.children==null){
            this.treeids.push(item.id);
            this.equipmentNameStr=this.equipmentNameStr+item.label+',';
          }
        })
        this.equipmentNameStr=this.equipmentNameStr.substr(0,this.equipmentNameStr.length-1);
      },

      /**选择设备*/
      selectDevice(){
        // /*获取所有已选中的节点*/
        this.getChcekNodes();
        this.visible=true;
      },
      /** 查询定时任务列表 */
      getList() {
        this.loading = true;
        listDevicejob(this.queryParams).then(response => {
          this.jobList = response.rows;
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
          jobId: undefined,
          jobName: undefined,
          cronExpression: undefined,
          misfirePolicy: 3,
          concurrent: 1,
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
        this.ids = selection.map(item => item.jobId);
        this.jobNames=selection.map(item => item.jobName);
        this.single = selection.length != 1;
        this.multiple = !selection.length;
      },

      /** 任务详细信息 */
      handleView(row) {
        getDeviceJob(row.jobId).then(response => {
          this.form = response.data;
          this.openView = true;
        });
      },

      // 更多操作触发
      handleCommand(command, row) {
        switch (command) {
          case "handleRun":
            this.handleRun(row);
            break;
          case "handleView":
            this.handleView(row);
            break;
          case "handleJobLog":
            this.handleJobLog(row);
            break;
          default:
            break;
        }
      },

      // 任务状态修改
      handleStatusChange(row) {
        let text = row.status === "0" ? "启用" : "停用";
        this.$modal.confirm('确认要"' + text + '""' + row.jobName + '"任务吗？').then(function() {
          return changeJobStatus(row.jobId, row.status,row.jobGroup);
        }).then(() => {
          this.$modal.msgSuccess(text + "成功");
        }).catch(function() {
          row.status = row.status === "0" ? "1" : "0";
        });
      },

      /* 立即执行一次 */
      handleRun(row) {
        this.$modal.confirm('确认要立即执行一次"' + row.jobName + '"任务吗？').then(function() {
          return runJob(row.jobId, row.jobGroup);
        }).then(() => {
          this.$modal.msgSuccess("执行成功");
        }).catch(() => {});
      },

      /** cron表达式按钮操作 */
      handleShowCron() {
        this.expression = this.form.cronExpression;
        this.openCron = true;
      },
      /** 确定后回传值 */
      crontabFill(value) {
        this.form.cronExpression = value;
      },
      /** 任务日志列表查询 */
      handleJobLog(row) {
        const jobId = row.jobId || 0;
        const jobGroup="SYNCHRONIZEDEVICETASKS";
        this.$router.push({ path: '/monitor/job-log/index', query: { jobId: jobId,jobGroup:jobGroup } })
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.updatejobid=null;
        // this.treeids=[];
        this.reset();
        this.open = true;
        this.title = "添加任务";
        this.$refs.tree.setCheckedKeys([]);

      },

      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const jobId = row.jobId || this.ids;
        this.updatejobid=jobId;
        getDeviceJob(jobId).then(response => {
          this.form = response.data;
          this.treeids=response.data.treeids
          this.open = true;
          this.title = "修改任务";
        });
      },
      /** 提交按钮 */
      submitForm: function() {
        this.form.treeids=this.treeids;

        this.$set(this.form,'jobGroup',"INSPECTION");
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.jobId != undefined) {
              updateJob(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addDevicejob(this.form).then(response => {
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
        console.log(row)
        const jobIds = row.jobId || this.ids;
        const Names = row.jobName || this.jobNames;

        this.$modal.confirm('是否确认删除定时任务名称为"' + Names + '"的数据项？').then(function() {
          return delDeviceJob(jobIds);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
      },
    }
  };
</script>
