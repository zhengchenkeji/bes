<template>
    <div class="alarm-table" style="margin-top: 20px">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
            <el-form-item label="状态" prop="isSuccess">
                <el-select v-model="queryParams.isSuccess" placeholder="请选择状态" clearable>
                    <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="通知时间" prop="noticeLogTime">
                <el-date-picker v-model="queryParams.noticeLogTime" type="daterange" range-separator="至"
                    start-placeholder="开始日期" end-placeholder="结束日期">
                </el-date-picker>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="getNoticeLogList">搜索</el-button>
                <!-- <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button> -->
            </el-form-item>
        </el-form>
        <el-table v-loading="loading" :data="logList" ref="NotifierTable">

            <el-table-column label="通知发送时间" align="center" prop="sendTime" />
            <!-- <el-table-column label="状态" align="center" prop="isSuccess" /> -->
            <el-table-column label="通知类型" align="center" prop="isSuccess">
                <template slot-scope="scope">
    
                    <el-button v-if="scope.row.isSuccess==1" size="mini"  type="success" round>成功</el-button>
                    <el-button v-if="scope.row.isSuccess==0" size="mini" type="danger" round>失败</el-button>

                </template>
            </el-table-column>
            <el-table-column label="接收人" align="center" prop="recipient" />
            <el-table-column label="发送消息体" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-view" @click="openSendbody(scope.row)"
                        v-hasPermi="['alarmTactics:alarmTactics:edit']">查看消息体</el-button>
                </template>
            </el-table-column>

            <el-table-column label="响应消息体" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-view" @click="openResonseBody(scope.row)"
                        v-hasPermi="['alarmTactics:alarmTactics:edit']">查看消息体</el-button>
                </template>
            </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
            @pagination="getNoticeLogList" />

        <el-dialog title="消息体详情" :append-to-body="true" :visible.sync="openContent" width="500px">
            <div style="height:100px">
                <el-input type="textarea" v-model="context" :autosize="{ minRows: 4, maxRows: 4 }"></el-input>

            </div>
        </el-dialog>
    </div>
</template>
<script>

import { getNoticeLogList } from "@/api/noticeManage/noticeConfig";
import { isContext } from "vm";

export default {
    name: "noticeLogList",
    dicts: ['bes_notice_type'],
    props: ["link"],
    data() {
        return {
            statusOptions: [{
                value: '1',
                label: '成功'
            }, {
                value: '0',
                label: '失败'
            }],
            openContent: false,
            context: "",
            logList: [],
            // 遮罩层
            loading: true,
            configId: null,
            // 显示搜索条件
            showSearch: true,
            // 总条数
            total: 0,
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                isSuccess: null,
                noticeLogTime: null
            },
        };
    },
    methods: {

        openSendbody(row) {

            this.context = row.sendJson
            this.openContent = true;

        },
        openResonseBody(row) {
            this.context = row.response

            this.openContent = true;

        },
        /** 查询通知日志 */
        getNoticeLogList() {
            var params =
            {
                noticeConfig: this.configId,
                noticeLogTime: this.queryParams.noticeLogTime + "",
                isSuccess: this.queryParams.isSuccess,
                pageNum: this.queryParams.pageNum,
                pageSize: this.queryParams.pageSize

            }
            getNoticeLogList(params).then((response) => {
                this.logList = response.rows;
                this.loading = false



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
