<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>

      <el-form-item label="状态" prop="scenestatus">
        <el-select v-model="queryParams.sceneStatus" clearable placeholder="请选择状态">
          <el-option v-for="dict in dict.type.scene_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['safetyWarning:AlarmNotifier:add']">新增场景联动
        </el-button>
      </el-col>

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!--<div v-for="(item,index) of sceneList">
        <el-row :gutter="12">
          <el-col :span="5">
            <el-card shadow="always" style="height: 230px;">
              <div>
                <el-row>
                  <el-col :span="3">
                    <i class="el-icon-set-up" style="font-size: 32px; "></i>
                  </el-col>
                  <el-col :span="21">
                    <el-row style=" margin-bottom: 5px; font-size: 22px;"><span
                      style="margin-left: 20px;">{{item.name}}</span></el-row>
                  </el-col>
                </el-row>
                <hr/>
                <el-row>
                  <el-col :span="3">
                    &nbsp;
                  </el-col>
                  <el-col :span="21">
                    <el-row
                      style="margin-bottom: 5px; margin-top: 20px;     color: rgba(0,0,0,.65);font-size: 14px; font-variant: tabular-nums;line-height: 1.5;list-style: none;">
                      <el-col :span="12" style=" ">触发方式</el-col>
                      <el-col :span="12" style=" ">场景状态</el-col>
                    </el-row>
                  </el-col>
                </el-row>


                <el-row>
                  <el-col :span="3">&nbsp;
                  </el-col>
                  <el-col :span="21">
                    <el-row style="margin-bottom: 5px; margin-top: 30px; font-size: 14px;;">
                      <el-col :span="12" style=" ">{{item.triggerMode}}</el-col>
                      <el-col :span="12" style=" ">
                          <dict-tag :options="dict.type.scene_status" :value="item.sceneStatus"/>
                      </el-col>
                    </el-row>
                  </el-col>
                </el-row>
                <hr/>
                <el-row >
                  <el-col :span="8">
                    <el-button type="text" icon="el-icon-edit-outline" style="height: 10px; margin-bottom: 20px;"
                               @click="handleUpdate(item)">修改
                    </el-button>
                  </el-col>
                  <el-col :span="8">
                    <el-button type="text" icon="el-icon-delete"
                               @click="devicePowerBtn(item)">删除
                    </el-button>
                  </el-col>
                  <el-col :span="8">
                    <el-button type="text" icon="el-icon-switch-button" disabled
                               @click="devicePowerBtn(item)">开启
                    </el-button>
                  </el-col>
                </el-row>
              </div>
            </el-card>
          </el-col>    
        </el-row>
      </div> -->

    <el-table v-loading="loading" :data="sceneList">
      <el-table-column label="场景名称" align="center" prop="name" show-overflow-tooltip />
      <el-table-column label="触发方式" align="center" prop="triggerMode" show-overflow-tooltip />
      <el-table-column label="场景状态" align="center" prop="sceneStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.scene_status" :value="scope.row.sceneStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button :disabled="scope.row.sceneStatus == 1" size="mini" type="text" icon="el-icon-edit"
            @click="handleUpdate(scope.row)">修改
          </el-button>
          <el-button :disabled="!scope.row.triggerMode.includes('手动触发')" size="mini" type="text"
            icon="el-icon-caret-right" @click="handleExecute(scope.row)">执行
          </el-button>
          <el-button :disabled="scope.row.sceneStatus == 0" size="mini" type="text" icon="el-icon-video-pause"
            @click="handleStop(scope.row)">停止
          </el-button>
          <el-button :disabled="scope.row.sceneStatus == 1" size="mini" type="text" icon="el-icon-video-play"
            @click="handleStart(scope.row)">启动
          </el-button>
          <el-button  size="mini" type="text" icon="el-icon-s-operation"  @click="openlog(scope.row)">
            联动日志
          </el-button>
          <el-button :disabled="scope.row.sceneStatus == 1" size="mini" type="text" icon="el-icon-delete"
            @click="handleDelete(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
      @pagination="getList" />
    <Scenelinkage ref="scene"></Scenelinkage>
  </div>
</template>
<script>
import { listSceneDic } from "../../../api/ruleEngine/scenelog/sceneLog";
import Scenelinkage from "./component/scenelinkage";
import { getInfo, getList, del, changeStatus, execute } from "@/api/ruleEngine/sceneLink/sceneLink";

export default {
  name: "AlarmNotifier",
  components: { Scenelinkage },
  dicts: ['scene_status'],

  data() {
    return {
      //多选默认不选中
      checked: false,
      // 遮罩层
      loading: true,
      // 岗位选项
      postOptions: [],
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 告警接收组表格数据
      sceneList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        sceneStatus: null,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {

    /**
     * 查看联动日志
     * @param {*} row 
     */
    openlog(row){
    // var sceneid= row.id;
    const sceneid = row.id;
        this.$router.push({ path: '/ruleEngine/scenelog', query: { sceneid: sceneid} })
    },
    /** 查询场景列表 */
    getList() {
      this.loading = true;
      getList(this.queryParams).then(response => {
        this.sceneList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    changeStatus() {

    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.handleQuery();
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.$refs.scene.getSceneAll();

      this.$refs.scene.emptyForm();
      this.$refs.scene.title = "新增场景联动";
      this.$refs.scene.open = true;

    },
    /****************  修改按钮操作 *****************/
    handleUpdate(row) {
      getInfo(row.id).then((res) => {
        // console.log(res.data,"-------------")
        this.$refs.scene.getData(res.data)
        this.$refs.scene.getSceneAll(row);

      })
      this.$refs.scene.title = "修改场景联动";
      this.$refs.scene.open = true;
    },
    /********************** 执行按钮操作 *****************/
    handleExecute(row) {
      this.$modal.confirm("确认要执行吗？").then(function () {
        return execute(row.id);
      }).then(() => {
        this.$modal.msgSuccess("执行成功!");
      }).catch(function () {
        this.$modal.msgError("执行失败!");
      });
    },
    /********************** 停止按钮操作 *****************/
    handleStop(row) {
      this.$modal.confirm("确认要停止吗？").then(function () {
        return true;
      }).then(() => {

        row.sceneStatus = 0;
        changeStatus(row).then(response => {
          this.$modal.msgSuccess("停止成功!");
        }).catch(function () {
          row.sceneStatus = 1;
          this.$modal.msgError("停止失败!");
        });
        row.sceneStatus = 0;
      })
    },

    /********************** 启动按钮操作 *****************/
    handleStart(row) {
      this.$modal.confirm("确认要启动吗？").then(function () {
        return true;
      }).then(() => {
        row.sceneStatus = 1;
        changeStatus(row).then(response => {
          this.$modal.msgSuccess("启动成功!");
        }).catch(function () {
          row.sceneStatus = 0;
          this.$modal.msgError("启动失败!");
        });

      })
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal.confirm('是否确认删除选中的场景配置？').then(function () {
        return del(id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功!");
      }).catch(() => {
        this.$modal.msgError("删除失败!");
      });
    },

  }
};
</script>
<style></style>
