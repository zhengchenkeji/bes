<template>
    <div class="alarm-table" style="margin-top: 20px">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
            label-width="68px">
            <el-form-item label="姓名" prop="name">
                <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter.native="getNotifierList" />
            </el-form-item>
            <el-form-item label="公司名称" prop="company">
                <el-input v-model="queryParams.company" placeholder="请输入公司名称" clearable
                    @keyup.enter.native="getNotifierList" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="getNotifierList">搜索</el-button>
                <!-- <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button> -->
            </el-form-item>
        </el-form>
        <el-table v-loading="loading" :data="AlarmNotifierList" ref="NotifierTable">
            <el-table-column label="姓名" align="center" prop="name" />
            <el-table-column label="部门" align="center" prop="deptName" />
            <!-- <el-table-column label="公司联系电话" align="center" prop="companyTel" />
            <el-table-column label="所属组织" align="center" prop="deptname" /> -->
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">

                    <el-button v-if="link==1"  size="mini" type="text" icon="el-icon-delete" @click="removelink(scope.row)"
                        v-hasPermi="['alarmTactics:alarmTactics:edit']">移出当前告警组</el-button>

                    <el-button v-if="link==0"  size="mini" type="text" icon="el-icon-circle-plus-outline" @click="addlink(scope.row)"
                        v-hasPermi="['alarmTactics:alarmTactics:edit']">关联告警策略</el-button>
                </template>
            </el-table-column>
        </el-table>
        <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
            @pagination="getNotifierList" />
    </div>
</template>
<script>

import { listAlarmNotifier } from "@/api/basicData/safetyWarning/alarmNotifier/alarmNotifier";

import { saveTacticsNoitifierLink,delTacticsNoitifierLink } from "@/api/basicData/safetyWarning/alarmTactics/alarmTactics";

export default {
    name: "notifierTableList",
    props:["link"],
    data() {
        return {
            // 遮罩层
            loading: true,
            tacticsId:undefined,
            //  是否关联
            tabName: "yes",
            // 告警策略表格数据
            AlarmNotifierList: [],
            // 告警接收组 总条目
            notifierTotal: 0,
            // 显示搜索条件
            showSearch: true,
            // 总条数
            total: 0,
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                name: null,
                homeAddress: null,
                tel: null,
                email: null,
                company: null,
                companyTel: null,
                post: null,
                groupId: null,
                tacticsId: null,
                islink: 1,
            },
        };
    },
    methods: {

        // 移除 关联关系
        removelink(row){
          const link={
            alarmTacticsId:this.queryParams.tacticsId,
            id:row.id
          }
          console.log(row.id);
            delTacticsNoitifierLink(link).then(response=>{
                console.log(response);
                this.getNotifierList();
            })
        },
        // 增加关联关系
        addlink(row){

            const link={
                alarmTacticsId:this.queryParams.tacticsId,
                alarmNotifierId:row.id
            }
            saveTacticsNoitifierLink(link).then(response=>{
                console.log(response);
                this.getNotifierList()
            })


        },
        /** 查询告警接收组列表 */
        getNotifierList() {

            listAlarmNotifier(this.queryParams).then((response) => {
                this.AlarmNotifierList = response.rows;
                this.total = response.total;
                console.log("ceshi1-----------"+this.loading)

                this.loading=false
                console.log("ceshi-----------"+this.loading)

            });
        },
    },
};
</script>
<style>
.alarm-table .el-input {
    width: 100px;
}
</style>
