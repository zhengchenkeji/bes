<template>
  <div>
    <el-dialog :visible.sync="open" :title="title" width="1200px" :inline="true" append-to-body>
      <el-form ref="form" :model="sceneForm" :rules="rules" label-width="120px">
        <el-form-item label="场景联动名称" prop="name" :rules="[{ required: true, message: '请输入场景联动名称', trigger: 'blur' }]">
          <el-input v-model="sceneForm.name" />
        </el-form-item>
        <!----------------------------------- 触发条件-------------------------------------->
        <div class="trigger">
          <div style="font-size: 16px;margin-left: 30px;margin-bottom: 15px">
            触发条件
            <el-tooltip placement="top">
              <div slot="content">
                触发条件满足条件中任意一个即可触发
              </div>
              <i class="el-icon-question"></i>
            </el-tooltip>
          </div>

          <div class="trigger_item" style="background-color: #f5f5f6;" v-for="(item, index) in sceneForm.triggerList">
            <el-form-item :inline="true" label-width="100px" :label="'触发器:' + (index + 1)"
              style="float: left; margin-right: 10px; " :prop="'triggerList.' + index + '.triggerModeCode'"
              :rules="[{ required: true, message: '请选择触发器', trigger: 'blur' }]">
              <el-select v-model="item.triggerModeCode" placeholder="请选择触发器" style="width: 150px;">
                <el-option v-for="item in dict.type.trigger_code" :key="item.value" :label="item.label"
                  :value="item.value" />
              </el-select>
            </el-form-item>

            <!------------------------------------ 定时触发------------------------------------>
            <el-form-item style="float: left;margin-right: 10px; " label-width="0px" v-if="item.triggerModeCode == 2"
              :prop="'triggerList.' + index + '.cronExpression'"
              :rules="[{ required: true, message: '请输入cron表达式', trigger: 'blur' }]">
              <el-input style="width: 180px;" placeholder="请输入cron表达式" v-model="item.cronExpression"></el-input>
            </el-form-item>

            <!------------------------------------ 设备触发------------------------------------>

            <div v-if="item.triggerModeCode == 3">
              <el-form-item style="float: left;margin-right: 10px; " label-width="0px"
                :prop="'triggerList.' + index + '.deviceName'"
                :rules="[{ required: true, message: '请选择设备', trigger: 'blur' }]">
                <el-input style="width: 200px;" placeholder="请选择设备" v-model="item.deviceName">
                  <el-button slot="append" icon="el-icon-s-tools" @click="deviceTreeTrigger(item)"></el-button>
                </el-input>
              </el-form-item>
              <el-form-item style="float: left;margin-right: 10px; " label-width="0px" v-if="item.deviceId != null"
                :prop="'triggerList.' + index + '.deviceInstruct'"
                :rules="[{ required: true, message: '请选择设备动作', trigger: 'blur' }]">
                <el-select v-model="item.deviceInstruct" placeholder="请选择设备动作" style="width: 150px;">
                  <el-option v-for="item in deviceActionList[index]" :key="item.value" :label="item.label"
                    :value="item.value" :disabled="item.disabled" />
                </el-select>

              </el-form-item>
              <el-form-item style="float: left;margin-right: 10px; " label-width="0px" v-if="item.deviceInstruct == 3"
                :prop="'triggerList.' + index + '.operator'"
                :rules="[{ required: true, message: '请选择运算规则', trigger: 'blur' }]">
                <el-select v-model="item.operator" placeholder="请选择运算规则" style="width: 150px">
                  <el-option v-for="item in dict.type.scene_operator_type" :key="item.value" :label="item.label"
                    :value="item.value" />
                </el-select>
              </el-form-item>
              <el-form-item style="float: left;margin-right: 10px; " label-width="0px" v-if="item.deviceInstruct == 3"
                :prop="'triggerList.' + index + '.operatorValue'"
                :rules="[{ required: true, message: '请输入运算比较值', trigger: 'blur' }]">
                <el-input type="text" style="width: 150px;" placeholder="请输入运算比较值"
                  v-model="item.operatorValue"></el-input>
              </el-form-item>

            </div>


            <!------------------------------------ 场景触发------------------------------------>
            <el-form-item v-if="item.triggerModeCode == 4" style="float: left;margin-right: 10px; " label-width="0px"
              :prop="'triggerList.' + index + '.triggerSceneId'"
              :rules="[{ required: true, message: '请选择场景', trigger: 'blur' }]">
              <el-select v-model="item.triggerSceneId" placeholder="请选择场景" multiple collapse-tags style="width: 150px;">
                <el-option v-for="item in sceneList" :key="item.id.toString()" :label="item.name" :value="item.id.toString()" />
              </el-select>
            </el-form-item>
            <el-button type="text" icon="el-icon-delete" @click="deleteTrigger(item, index)">删除</el-button>
          </div>
          <el-button type="text" icon="el-icon-plus" @click="addTrigger">触发器</el-button>
        </div>

        <!----------------------------------- 执行动作-------------------------------------->
        <div class="action">
          <div style="font-size: 16px;margin-left: 30px;margin-bottom: 15px">
            执行动作
          </div>
          <div class="action_item" style="background-color: #f5f5f6" v-for="(item, index) in sceneForm.actuatorList">

            <el-form-item style="float: left; margin-right: 10px; " :label="'执行动作:' + (index + 1)" label-width="100px"
              :prop="'actuatorList.' + index + '.movementMode'"
              :rules="[{ required: true, message: '请选择动作模式', trigger: 'blur' }]">

              <el-select v-model="item.movementMode" placeholder="请选择动作模式" style="width: 150px"
                @change="movementModeChange(item)">
                <el-option v-for="item in dict.type.movement_mode" :key="item.value" :label="item.label"
                  :value="item.value" />
              </el-select>
            </el-form-item>
            <!------------------------------------ 消息通知------------------------------------>
            <span v-if="item.movementMode == 1">

              <el-form-item style="float: left;margin-right: 10px; " label-width="0px"
                :prop="'actuatorList.' + index + '.userOrDevice'"
                :rules="[{ required: true, message: '请选择用户', trigger: 'blur' }]">
                <el-input style="width: 200px;" placeholder="请选择用户" v-model="item.userOrDevice">
                  <el-button slot="append" icon="el-icon-s-tools" @click="chooseUser(item)"></el-button>
                </el-input>
              </el-form-item>

              <el-form-item style="float: left;margin-right: 10px; " label-width="0px"
                :prop="'actuatorList.' + index + '.executeType'"
                :rules="[{ required: true, message: '请选择执行类型', trigger: 'blur' }]">
                <el-select v-model="item.executeType" placeholder="请选择执行类型" style="width: 150px"
                  @change="noticeModeChange(item, index)">
                  <el-option v-for="item in dict.type.bes_notice_type" :key="item.value" :label="item.label"
                    :value="item.value" />
                </el-select>
              </el-form-item>

              <el-form-item style="float: left;margin-right: 10px; " label-width="0px"
                :prop="'actuatorList.' + index + '.noticeConfig'"
                :rules="[{ required: true, message: '请选择通知配置', trigger: 'blur' }]">
                <el-select v-model="item.noticeConfig" style="width:150px" placeholder="请选择通知配置"
                  @change="noticeTypeChange(item, index)">
                  <el-option v-for="item in noticeConfigList[index]" :key="item.code" :label="item.name"
                    :value="item.code" />
                </el-select>
              </el-form-item>
              <el-form-item style="float: left;margin-right: 10px; " label-width="0px"
                 :prop="'actuatorList.' + index + '.noticeTemplate'"
                :rules="[{ required: true, message: '请选择通知模板', trigger: 'blur' }]">
                <el-select v-model="item.noticeTemplate" placeholder="请选择通知模板" style="width: 150px">
                  <el-option v-for="item in noticeTemplateList[index]" :key="item.code" :label="item.name"
                    :value="item.code" />
                </el-select>
              </el-form-item>
              <el-form-item style="margin-top: 60px;" label-width="100px" label="选填，配置通知内容">
                <el-input type="textarea" style="width: 78%;" :rows="3" placeholder="请根据模板输入通知内容的配置，如无需配置可不填"
                  v-model="item.content"></el-input>
                <el-button type="text" icon="el-icon-delete" @click="deleteAction(item, index)">删除</el-button>

              </el-form-item>
            </span>
            <!------------------------------------ 设备输出------------------------------------>
            <span v-if="item.movementMode == 2">
              <el-form-item style="float: left;margin-right: 10px; " label-width="0px"
                :prop="'actuatorList.' + index + '.userOrDevice'"
                :rules="[{ required: true, message: '请选择设备', trigger: 'blur' }]">
                <el-input style="width: 200px;" placeholder="请选择设备" v-model="item.userOrDevice">
                  <el-button slot="append" icon="el-icon-s-tools" @click="deviceTreeAction(item)"></el-button>
                </el-input>
              </el-form-item>
              <el-form-item style="float: left;margin-right: 10px; " label-width="0px"
                :prop="'actuatorList.' + index + '.executeType'"
                :rules="[{ required: true, message: '请选择设备动作类型', trigger: 'blur' }]">
                <el-select v-model="item.executeType" placeholder="请选择设备动作类型" style="width: 150px"
                  @change="executeTypeChange(item, index)">

                  <el-option v-for="item in executeTypeDeviceList[index]" :key="item.value" :label="item.label"
                    :value="item.value" />
                  <!-- <el-option v-for="item in dict.type.execute_type_device" :key="item.value" :label="item.label"
                    :value="item.value" /> -->
                </el-select>
              </el-form-item>
              <el-form-item style="float: left;margin-right: 10px; " label-width="0px"
                v-if="item.deviceType != 1 && item.executeType == 2" :prop="'actuatorList.' + index + '.executeAttribute'"
                :rules="[{ required: true, message: '请选择设备功能', trigger: 'blur' }]">
                <el-select v-model="item.executeAttribute" style="width: 150px"
                  :placeholder="item.executeType == 1 ? '请选择设备点位' : '请选择设备功能'">
                  <el-option v-for="item in executeAttributeList[index]" :key="item.value" :label="item.label"
                    :value="item.value" />
                </el-select>
              </el-form-item>
              <span v-if="item.executeType == 1">
                <el-form-item style="float: left;margin-right: 10px; " label-width="0px"
                  :prop="'actuatorList.' + index + '.executeValue'"
                  :rules="[{ required: true, message: '请输入值', trigger: 'blur' }]">
                  <el-input style="width: 150px;" placeholder="请输入值" v-model="item.executeValue">
                  </el-input>
                </el-form-item>
              </span>
            </span>
            <el-button type="text" v-if="item.movementMode != 1" icon="el-icon-delete"
              @click="deleteAction(item, index)">删除</el-button>
          </div>
          <el-button type="text" icon="el-icon-plus" @click="addAction">执行动作</el-button>
        </div>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!-- 设备树形数据-->
    <el-drawer size='28%' title="设备树数据" :visible.sync="visible" direction="rtl">
      <el-tabs v-model="activeName">
        <el-tab-pane label="bes设备树" name="bes">
          <el-card>
            <div style="height: 75vh;overflow:auto">
              <el-tree :data="treedata" show-checkbox check-strictly node-key="id" :default-checked-keys="treeids"
                :default-expanded-keys="expandedKeys" ref="bestree" @check="handleNodeCheck"
                @check-change="handleCheckChange">
              </el-tree>
            </div>
            <div style="text-align: center;">
              <el-button type="primary" @click="savetree">保 存</el-button>
            </div>
          </el-card>
        </el-tab-pane>
        <el-tab-pane label="第三方协议" name="other">
          <el-card>
            <div style="height: 75vh;overflow:auto">
              <el-tree :data="othertreedata" show-checkbox check-strictly node-key="id" :default-checked-keys="treeids"
                :default-expanded-keys="expandedKeys" ref="othertree" @check="handleNodeCheck"
                @check-change="handleCheckChange">
              </el-tree>
            </div>
            <div style="text-align: center;">
              <el-button type="primary" @click="savetree">保 存</el-button>
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>

    </el-drawer>
    <!-- 选择用户-->
    <el-dialog :visible.sync="userOpen" title="用户信息" width="1000px" style="height: 800px;" append-to-body>
      <el-row :gutter="20">
        <!--部门数据-->
        <el-col :span="4" :xs="24">
          <div class="head-container">
            <el-input v-model="deptName" placeholder="请输入部门名称" clearable size="small" prefix-icon="el-icon-search"
              style="margin-bottom: 20px" />
          </div>
          <div class="head-container">
            <el-tree :data="deptOptions" :props="defaultProps" :expand-on-click-node="false"
              :filter-node-method="filterNode" ref="tree" default-expand-all highlight-current
              @node-click="handleNodeClick" />
          </div>
        </el-col>
        <!--用户数据-->
        <el-col :span="20" :xs="24">
          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
            <el-form-item label="用户名称" prop="userName">
              <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable style="width: 240px"
                @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable style="width: 240px"
                @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="loading" :data="userList" ref="userTable" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="用户编号" align="center" key="userId" prop="userId" />
            <el-table-column label="用户名称" align="center" key="userName" prop="userName" :show-overflow-tooltip="true" />
            <el-table-column label="用户昵称" align="center" key="nickName" prop="nickName" :show-overflow-tooltip="true" />
            <el-table-column label="部门" align="center" key="deptName" prop="dept.deptName"
              :show-overflow-tooltip="true" />
            <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber" width="120" />
          </el-table>
          <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize" @pagination="getList" />
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUserForm">确 定</el-button>
        <el-button @click="cancelUser">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { treeselect } from "@/api/system/dept";
import { listUser } from "@/api/system/user";
import { listOrder, edit, listSceneDic, getEquipmentListByScene, getbesDeviceList, allEquipmentFunctionTree, getNoticeConfigListByType, getNoticeTemplateListByConfig } from "@/api/ruleEngine/sceneLink/sceneLink";

export default {
  dicts: ['trigger_code', 'movement_mode', 'bes_notice_type', 'execute_type_device', "scene_operator_type"],
  name: "scenelinkage",
  props: {},
  data() {
    return {
      otherFunctionTreeData: [],
      otherItemTreeData: [],
      expandedKeys: [],
      executeTypeDeviceList: [],//执行动作设备执行动作列表
      activeName: "bes",
      open: false,//是否显示
      actionOrTrigger: null,//true 触发器 false 执行动作
      triggerData: {},//循环 选中的触发器数据
      actionData: {},//循环 选中的执行动作数据
      userOpen: false,
      title: "",
      // 用户查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        deptId: undefined
      },
      //场景联动表单
      sceneForm: {
        id: null,//场景联动Id
        name: null,//场景联动名称
        triggerMode: null,//触发方式
        sceneStatus: 1,//默认状态为开启
        //触发器集合
        triggerList: [{
          triggerModeCode: null, //触发器类型
          triggerDevice: null, //触发器设备
          triggerDeviceId: null,//触发器设备ID
          deviceAction: null,//设备动作
          cronExpression: null,//cron 表达式
          triggerSceneId: []//场景
          , deviceInstruct: null //设备触发动作
          , deviceName: null//触发设备名称
          , operator: null
          , operatorValue: null
        }],
        //执行动作集合
        actuatorList: [{
          movementMode: null,//动作模式
          userOrDevice: null,//用户,设备显示列表
          userIds: [],//用户集合
          executeType: null,//执行类型
          noticeConfig: null,//通知配置
          noticeTemplate: null,//通知模板
          treeId: null,//设备ID
          executeAttribute: null,//设备点位
          executeValue: null,//下发值
          content: null//通知内容
        }],
      },
      treedata: [],//设备树数据
      othertreedata: [],
      treeids: [],//选中设备的id
      props: { multiple: true, value: "id" },
      // 部门树选项
      deptOptions: undefined,
      defaultProps: {
        children: "children",
        label: "label"
      },
      userSelectList: [],//用户选中时数据
      deptName: "",//部门名称
      userList: [],//用户列表
      // 总条数
      total: 0,
      // 遮罩层
      loading: true,
      options: [],
      visible: false, //设备树界面

      //场景列表
      sceneList: [],
      //触发器设备动作类型
      deviceActionList: [],

      noticeConfigList: [],//通知配置
      noticeTemplateList: [], //通知模板
      //点位列表
      executeAttributeList: [],
      //功能列表
      functionList: [],

      rules: {},
    }
  },
  created() {
    this.getDeviceTreeList();
    this.getList();
    this.getTreeselect();
    // this.getSceneAll();
  },
  watch: {
    // 根据名称筛选部门树
    deptName(val) {
      this.$refs.tree.filter(val);
    }
  },
  methods: {
    /******************获取场景配置***********/
    getSceneAll(row) {
      listSceneDic(row).then((res) => {

        this.sceneList = res.rows
      })
    },
    /******************获取处理数据***********/
    getData(data) {

      //格式化数据
      data.actuatorList.forEach((item, index) => {
        this.noticeModeChange(item, index, true)
        // this.noticeTypeChange(item, index, true)
        this.executeTypeChange(item, index, true)




        item.movementMode = item.movementMode.toString()
        if (item.userIds) {
          item.userIds = item.userIds.split(',')
        }
      })
      //处理触发器数据
      data.triggerList.forEach((item, index) => {

        if (item.deviceInstruct == 3) {
          this.deviceActionList[index] = [{ label: "属性", value: "3" }]
        } else {
          this.deviceActionList[index] = [{ label: "上线", value: "1" }, { label: "离线", value: "2" }]

        }

        if (item.triggerSceneId) {
          item.triggerSceneId = item.triggerSceneId.split(',')
        }
      })
      this.$nextTick(() => {
        this.sceneForm = data
      })
    },
    /******************表单确认操作***********/
    submitForm() {

      //校验
      this.$refs["form"].validate(valid => {
        if (valid) {
          var text = []
          //翻译字典 存入触发方式
          this.sceneForm.triggerList.forEach(item => {
            text.push(this.selectDictLabel(this.dict.type.trigger_code, item.triggerModeCode))
          })
          this.sceneForm.triggerMode = text.toString()
          //格式化数据
          this.sceneForm.actuatorList.forEach((item) => {
            item.userIds = item.userIds.toString()
          })
          this.sceneForm.triggerList.forEach((item) => {
            item.triggerSceneId = item.triggerSceneId.toString()
          })
          //修改
          if (this.sceneForm.id != null) {
            edit(this.sceneForm).then((res) => {

              this.$message.success("修改成功！")
              this.$parent.getList();
            })
          }
          //新增
          else {
            //格式化数据
            this.sceneForm.actuatorList.forEach((item) => {
              item.userIds = item.userIds.toString()
            })
            this.sceneForm.triggerList.forEach((item) => {
              item.triggerSceneId = item.triggerSceneId.toString()
            })
            listOrder(this.sceneForm).then((res) => {
              this.$message.success("新增成功！")
              this.$parent.getList();
            })
          }
          this.open = false
        }
      })

    },
    /******************表单确认操作***********/
    emptyForm() {
      this.sceneForm = {
        id: null,//场景联动Id
        name: null,
        triggerMode: null,//触发方式
        sceneStatus: 1,//默认状态为开启
        //触发器集合
        triggerList: [{
          triggerModeCode: null, //触发器类型
          triggerDevice: null, //触发器设备
          triggerDeviceId: null,//触发器设备ID
          deviceAction: null,//设备动作
          cronExpression: null,//cron 表达式
          triggerSceneId: []//场景
          , deviceInstruct: null //设备触发动作
          , deviceName: null//触发设备名称
          , operator: null
          , operatorValue: null
        }],
        //执行动作集合
        actuatorList: [{
          movementMode: null,//动作模式
          userOrDevice: null,//用户，设备回显列表
          userIds: [],//用户集合
          executeType: null,//执行类型
          noticeConfig: null,//通知配置
          noticeTemplate: null,//通知模板
          treeId: null,//设备ID
          executeAttribute: null,//设备点位
          content: null,//内容
          executeValue: null//下发值
        }],
      }
    },
    /******************表单取消操作***********/
    cancel() {
      this.open = false
    },
    /******************新加触发器***********/
    addTrigger() {
      this.sceneForm.triggerList.push({
        triggerModeCode: null,
        triggerDevice: null,
        triggerDeviceId: null,
        deviceAction: null,
        cronExpression: null,
        triggerSceneId: []
        , deviceInstruct: null //设备触发动作
        , deviceName: null//触发设备名称
        , operator: null
        , operatorValue: null
      })
    },
    /******************删除触发器***********/
    deleteTrigger(data, index) {
      this.$modal.confirm('是否确认删除触发器？',).then(function () {
        return true
      }).then(() => {
        this.$delete(this.sceneForm.triggerList, index)
      }).catch(() => {
      })
    },
    /******************新加执行动作***********/
    addAction() {

      this.sceneForm.actuatorList.push({
        movementMode: null,
        userOrDevice: null,
        userIds: [],
        executeType: null,
        noticeConfig: null,
        noticeTemplate: null,
        treeId: null,
        content: null,
        executeAttribute: null,
        executeValue: null
      })
    },
    /******************删除执行动作***********/
    deleteAction(data, index) {
      this.$modal.confirm('是否确认删除执行动作？',).then(function () {
        return true
      }).then(() => {
        this.$delete(this.sceneForm.actuatorList, index)
      }).catch(() => {
      })
    },
    /******************打开触发条件设备树***********/
    deviceTreeTrigger(data) {
      this.othertreedata = this.otherItemTreeData;
      this.treeids = [];
      this.triggerData = data;
      this.actionOrTrigger = true
      this.visible = true;


      if (data.deviceId) {
        this.treeids = data.deviceId.split(',')
        //bes
        this.expandedKeys = this.getparentlist(data.deviceId, this.treedata);
        if (this.expandedKeys == null) {
          //other
          this.activeName = "other"
          this.expandedKeys = this.getparentlist(data.deviceId, this.othertreedata);
          this.$nextTick(() => {

            this.$refs.othertree.setCheckedKeys([])
            this.$refs.othertree.setCheckedKeys(this.treeids)
          })
        } else {
          this.activeName = "bes"
          this.$nextTick(() => {

            this.$refs.bestree.setCheckedKeys([])
            this.$refs.bestree.setCheckedKeys(this.treeids)
          })
        }
      }
      // const arrayKeys = this.findPathbyId(this.treedata, data.deviceId)
    },

    //数组类型
    // 查找父级函数函数
    // 查询传入节点的父节点
    getparentlist(code, tree) {
      let arr = [] //要返回的数组
      for (let i = 0; i < tree.length; i++) {
        let item = tree[i]
        arr = []
        arr.push(item.id) //保存当前节点id

        if (code == item.id) { //判断当前id是否是默认id

          // this.controllerId = item.controllerId
          return arr //是则退出循环、返回数据
        } else { //否则进入下面判断，判断当前节点是否有子节点数据
          if (item.children && item.children.length > 0) {
            //合并子节点返回的数据
            arr = arr.concat(this.getparentlist(code, item.children))
            if (arr.includes(parseInt(code))) { //如果当前数据中已包含默认节点，则退出循环、返回数据
              // this.controllerId=item.controllerId
             return arr
            }
          }
        }
      }
    },

    /******************打开执行动作设备树***********/
    deviceTreeAction(data) {
      this.treeids = [];

      this.othertreedata = this.otherFunctionTreeData;
      this.actionData = data;
      this.actionOrTrigger = false
      this.visible = true;
      if (data.treeId) {
        this.treeids = data.treeId.split(',')

        //bes
        this.expandedKeys = this.getparentlist(data.treeId, this.treedata);
        if (this.expandedKeys == null) {
          //other

          this.activeName = "other"
          this.expandedKeys = this.getparentlist(data.treeId, this.othertreedata);
          this.$nextTick(() => {

            this.$refs.othertree.setCheckedKeys([])
            this.$refs.othertree.setCheckedKeys(this.treeids)
          })
        } else {
          this.activeName = "bes"
          this.$nextTick(() => {
            this.$refs.bestree.setCheckedKeys([])
            this.$refs.bestree.setCheckedKeys(this.treeids)
          })

        }
      }

    },
    /**********树节点 状态改变事件***********/
    handleCheckChange(data, checked, indeterminate) {
    },
    /**********树节点 选中事件***********/
    handleNodeCheck(data, checked) {


      this.$nextTick(() => {

        this.$refs.bestree.setCheckedKeys([]);

        this.$refs.othertree.setCheckedKeys([]);


        if (this.treeids.length == 0) {
          this.treeids.push(data.id);
        } else {
          this.treeids = [];

          this.treeids.push(data.id);

          if (this.activeName == "bes") {
            this.$refs.bestree.setCheckedKeys(this.treeids);

          } else {
            this.$refs.bestree.setCheckedKeys(this.treeids);

          }

        }
      })
      // checked.checkedKeys.forEach((item) => {
      //   if (data.deviceTreeId != item) {
      //     this.$refs.tree.setChecked(item, false)
      //   }
      // })


    },
    /**************选择设备保存方法*************/
    savetree() {

      if (this.actionOrTrigger) {
        //触发器
        if (this.treeids.length > 0) {

          let tree = this.$refs.bestree.getCheckedNodes()[0] || this.$refs.othertree.getCheckedNodes()[0]
          this.triggerData.deviceName = tree.label
          this.triggerData.deviceId = tree.id
          if (this.activeName == "bes") {
            this.triggerData.deviceType = 0;
          } else {
            this.triggerData.deviceType = 1;
          }
          if (tree.children == null) {
            //代表是点位
            this.deviceActionList[this.sceneForm.triggerList.length - 1] = [{ label: "属性", value: "3" }]


          } else {
            this.deviceActionList[this.sceneForm.triggerList.length - 1] = [{ label: "上线", value: "1" }, { label: "下线", value: "2" }]
          }

          this.triggerData.deviceInstruct = null
          this.triggerData.operator = null
          this.triggerData.operatorValue = null

        } else {
          this.triggerData.triggerDevice = ""
          this.triggerData.triggerDeviceId = null
        }


      } else {
        //执行动作
        if (this.treeids.length > 0) {
          let tree = this.$refs.bestree.getCheckedNodes()[0] || this.$refs.othertree.getCheckedNodes()[0]

          this.actionData.userOrDevice = tree.label
          this.actionData.treeId = tree.id
          this.executeTypeDeviceList[this.sceneForm.actuatorList.length - 1] = [{ label: "调用功能", value: "2" }]
          if (this.activeName == "bes") {
            this.actionData.deviceType = 0;
            // this.noticeConfigList[index] = [{ label: "短信类型", value: "1" }]

            if (tree.children == null) {
              this.executeTypeDeviceList[this.sceneForm.actuatorList.length - 1] = [{ label: "设置属性", value: "1" }]
            }


          } else {
            this.actionData.deviceType = 1;
          }
        } else {
          this.actionData.userOrDevice = ""
          this.actionData.treeId = null
        }
      }

      this.visible = false;
    },
    /**************执行类型改变时（消息通知）*************/
    noticeModeChange(item, index, update) {
      // this.noticeConfigList[index] = [];
      if (!update) {
        item.noticeConfig = null
        item.noticeTemplate = null
        this.noticeConfigList[index] = [];
      }
      if (item.movementMode == 1) {
        const param = {
          noticeType: item.executeType
        }
        getNoticeConfigListByType(param).then(response => {
          // this.$nextTick(() => {
          // this.noticeConfigList[index] = response.data;
          this.$set(this.noticeConfigList, index, response.data)
          if (update) {
            this.noticeTypeChange(item, index, update);
          }

          // })
        })
      }


    },
    /**************通知配置改变时*************/
    noticeTypeChange(item, index, update) {

      if (!update) {
        item.noticeTemplate = null
        this.noticeTemplateList[index] = [];

      }
      // if (item.executeType == 1 || item.executeType == 2) {

        const param = {
          configId: item.noticeConfig
        }
        getNoticeTemplateListByConfig(param).then(response => {
          // this.noticeTemplateList[index] = response.data;
          this.$set(this.noticeTemplateList, index, response.data)

        })

      // }
    },
    /**************执行类型改变时（设备）*************/
    executeTypeChange(item, index, update) {
      if (!update) {
        item.executeAttribute = null
        item.executeValue = null
      }
      if (item.executeType == 1) {
        this.executeTypeDeviceList[index] = [{ label: "设置属性", value: "1" }]

        this.executeAttributeList[index] = [{ label: "开关", value: "1" }]
      } else if (item.executeType == 2) {
        this.executeTypeDeviceList[index] = [{ label: "调用功能", value: "2" }]

        this.executeAttributeList[index] = [{ label: "重启", value: "2" }, { label: "同步数据", value: "3" }, { label: "同步时间", value: "4" }]
      }
    },

    /**************动作模式改变时*************/
    movementModeChange(item) {

      //切换时 清空旧数据
      item.executeType = null
      item.userOrDevice = null
      item.userIds = []
      item.noticeConfig = null
      item.noticeTemplate = null
      item.treeId = null
      item.executeAttribute = null
      item.executeValue = null
    },
    /**************获取树节点*************/
    getDeviceTreeList() {
      getbesDeviceList().then(response => {

        if (response.code == 200) {
          response.data.forEach(val => {
            if (val.energyNode) {
              val.disabled = true
            }
            val.id = val.deviceTreeId;
            val.label = val.sysName;
          })
          this.treedata = this.handleTree(response.data, "deviceTreeId", "deviceTreeFatherId");
        }
      })

      getEquipmentListByScene().then(response => {
        this.otherItemTreeData = this.handleTree(response.data, "id", "pid");
      })

      allEquipmentFunctionTree().then(response => {
        this.otherFunctionTreeData = this.handleTree(response.data, "id", "pid")

      })
    },


    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then(response => {
        this.deptOptions = response.data;
      });
    },
    /** 筛选节点 */
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    /** 节点单击事件 */
    handleNodeClick(data) {
      this.queryParams.deptId = data.id;
      this.handleQuery();
    },
    /** 搜索 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      listUser(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.userList = response.rows;
        this.total = response.total;
        this.loading = false;
      }
      );
    },
    /** 查询用户列表 */
    chooseUser(item) {
      this.actionData = item
      this.userOpen = true
      this.$nextTick(() => {
        if (this.actionData.userIds.length > 0) {
          this.userList.forEach(item => {
            if (this.actionData.userIds.includes(item.userId.toString())) {
              this.$refs.userTable.toggleRowSelection(item, true)
            }
          })
        }
      })
    },

    /************用户确认按钮*****************/
    submitUserForm() {
      var text = []
      var userIds = []
      this.userSelectList.forEach((item) => {
        text.push(item.userName)
        userIds.push(item.userId.toString())
      })
      this.actionData.userIds = userIds
      this.actionData.userOrDevice = text.toString()
      this.userOpen = false

    },
    /*********************验证*****************/
    verification() {
      if (this.sceneForm.name == null) {
        this.$message.warning("请输入场景联动名称！")
        return false;
      }
      if (this.sceneForm.triggerList.length <= 0) {
        this.$message.warning("至少有一种触发器！")
        return false;
      }
      if (this.sceneForm.actuatorList.length <= 0) {
        this.$message.warning("至少有一种执行动作！")
        return false;
      }
      for (let i = 0; i < this.sceneForm.triggerList.length; i++) {
        var item = this.sceneForm.triggerList[i]
        if (item.triggerModeCode == null) {
          this.$message.warning("请选择触发器！")
          return false;
        }
        else {
          //定时触发
          if (item.triggerModeCode == 2) {
            if (item.cronExpression == null) {
              this.$message.warning("请输入cron表达式！")
              return false;
            }
          }
          //设备触发
          else if (item.triggerModeCode == 3) {
            if (item.triggerDevice == null) {
              this.$message.warning("请选择触发器设备！")
              return false;
            }
            if (item.deviceAction == null) {
              this.$message.warning("请选择设备动作！")
              return false;
            }
          }
          //场景触发
          else if (item.triggerModeCode == 4) {
            if (item.triggerSceneId.length <= 0) {
              this.$message.warning("请选择场景！")
              return false;
            }
          }
        }
      }

      for (let i = 0; i < this.sceneForm.actuatorList.length; i++) {
        var item = this.sceneForm.actuatorList[i]
        if (item.movementMode == null) {
          this.$message.warning("请选择动作模式！")
          return false;
        }
        else {
          //消息通知
          if (item.movementMode == 1) {
            if (item.userOrDevice == null) {
              this.$message.warning("请选择用户列表！")
              return false;
            }
            if (item.executeType == null) {
              this.$message.warning("请选择执行类型")
              return false;
            }
            if (item.noticeConfig == null) {
              this.$message.warning("请选择通知配置")
              return false;
            }
            if (item.noticeTemplate == null) {
              this.$message.warning("请选择通知模板")
              return false;
            }
            if (item.content == null) {
              this.$message.warning("请输入通知内容")
              return false;
            }
          }
          //设备输出
          else if (item.movementMode == 2) {
            if (item.userOrDevice == null) {
              this.$message.warning("请选择设备！")
              return false;
            }
            if (item.executeType == null) {
              this.$message.warning("请选择执行类型")
              return false;
            }
            if (item.executeAttribute == null) {
              this.$message.warning("请选择设备点位")
              return false;
            }
            if (item.executeValue == null) {
              this.$message.warning("请输入下发值")
              return false;
            }
          }
        }
      }
      return true;
    },

    /************用户取消按钮*****************/
    cancelUser() {
      this.userOpen = false
    },
    /** 用户多选框选中数据 */
    handleSelectionChange(selection) {
      this.userSelectList = selection
    },
  }
}
</script>

<style scoped lang="scss">
.trigger_item {
  padding-top: 10px;
  padding-bottom: 10px;
}

::v-deep .trigger .el-form-item {
  margin-bottom: 0px !important;
}

.action_item {
  padding-top: 10px;
  padding-bottom: 10px;
}

::v-deep .action .el-form-item {
  margin-bottom: 0px !important;
}
</style>
