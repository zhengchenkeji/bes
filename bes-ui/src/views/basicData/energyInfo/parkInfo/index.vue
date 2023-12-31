<template>
  <div class="app-container">
    <!--查询条件-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="园区名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入园区名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="园区编码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入园区编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!--操作按钮-->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['basicData:parkInfo:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['basicData:parkInfo:edit']"
        >修改
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
          v-hasPermi="['basicData:parkInfo:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['basicData:parkInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!--表格-->
    <el-table v-loading="loading" :data="parkList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="园区编号" align="center" prop="code"/>
      <el-table-column label="园区名称" align="center" prop="name"/>
      <el-table-column label="节点编码" align="center" prop="nodeCode"/>
      <el-table-column label="总面积" align="center" prop="allArea"/>
      <el-table-column label="监测面积" align="center" prop="monitorArea"/>
      <el-table-column label="供暖面积" align="center" prop="heatArea"/>
      <el-table-column label="总人口" align="center" prop="personNums"/>
      <el-table-column label="建筑时间" align="center" prop="buildTime">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.buildTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="联系人" align="center" prop="contactName"/>
      <el-table-column label="联系人电话" align="center" prop="contactPhone"/>
      <el-table-column label="联系人邮箱" align="center" prop="contactEmail"/>
      <el-table-column label="经度" align="center" prop="longitude"/>
      <el-table-column label="纬度" align="center" prop="latitude"/>
      <el-table-column label="创建时间" align="center" prop="createTime"/>
      <el-table-column label="修改时间" align="center" prop="updateTime"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['basicData:parkInfo:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['basicData:parkInfo:remove']"
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

    <!-- 添加或修改园区对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="用户名称" prop="userName">
          <el-select v-model="form.userName" style="width: 450px;">
            <el-option
              v-for="item in userNameList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="组织机构id" prop="organizationId">
          <el-select v-model="form.organizationId" style="width: 450px;">
            <el-option
              v-for="item in organizationList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="园区名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入园区名称"/>
        </el-form-item>
        <el-form-item label="节点编码" prop="nodeCode">
          <el-input v-model="form.nodeCode" placeholder="请输入节点编码"/>
        </el-form-item>
        <el-form-item label="总面积" prop="allArea">
          <el-input type="number" v-model="form.allArea" placeholder="请输入总面积"/>
        </el-form-item>
        <el-form-item label="监测面积" prop="monitorArea">
          <el-input type="number" v-model="form.monitorArea" placeholder="请输入监测面积"/>
        </el-form-item>
        <el-form-item label="供暖面积" prop="heatArea">
          <el-input type="number" v-model="form.heatArea" placeholder="请输入供暖面积"/>
        </el-form-item>
        <el-form-item label="总人口" prop="personNums">
          <el-input type="number" v-model="form.personNums" placeholder="请输入总人口"/>
        </el-form-item>
        <el-form-item label="建筑时间" prop="buildTime">
          <el-date-picker clearable style="width: 450px;"
                          v-model="form.buildTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择建筑时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="设备运行时间" prop="equipmentRuntime">
          <el-date-picker clearable style="width: 450px;"
                          v-model="form.equipmentRuntime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择设备运行时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人"/>
        </el-form-item>
        <el-form-item label="联系人电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系人电话"/>
        </el-form-item>
        <el-form-item label="联系人邮箱" prop="contactEmail">
          <el-input v-model="form.contactEmail" placeholder="请输入联系人邮箱"/>
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入经度"/>
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入纬度"/>
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
  import {
    listUser,
    listOrganization,
    listPark,
    getPark,
    delPark,
    addPark,
    updatePark
  } from '@/api/basicData/energyInfo/parkInfo/parkInfo'

  export default {
    name: 'Park',
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
        // 园区表格数据
        parkList: [],
        // 弹出层标题
        title: '',
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          userName: null,
          organizationId: null,
          name: null,
          nodeCode: null,
          allArea: null,
          monitorArea: null,
          heatArea: null,
          personNums: null,
          buildTime: null,
          equipmentRuntime: null,
          contactName: null,
          contactPhone: null,
          contactEmail: null,
          longitude: null,
          latitude: null,
          code: null
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          longitude: [/*{ required: true, message: '经度不能为空', trigger: 'blur' }
            , */{
              pattern: /^(\-|\+)?(((\d|[1-9]\d|1[0-7]\d|0{1,3})\.\d{0,6})|(\d|[1-9]\d|1[0-7]\d|0{1,3})|180\.0{0,6}|180)$/,
              message: '经度整数部分为0-180,小数部分为0到6位!'
            }],
          latitude: [/*{ required: true, message: '纬度不能为空', trigger: 'blur' }
            , */{
              pattern: /^(\-|\+)?([0-8]?\d{1}\.\d{0,6}|90\.0{0,6}|[0-8]?\d{1}|90)$/,
              message: '纬度整数部分为0-90,小数部分为0到6位!'
            }],
          contactPhone: [{ pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号' }],
          contactEmail: [{
            pattern: /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/,
            message: '请输入正确的邮箱'
          }],
          name: [{ required: true, message: '园区名称不能为空', trigger: 'blur' }],
          nodeCode: [{ required: true, message: '节点编码不能为空', trigger: 'blur' }],
          userName: [{ required: true, message: '用户不能为空', trigger: 'change' }],
          organizationId: [{ required: true, message: '组织机构不能为空', trigger: 'change' }],
          buildTime: [{ required: true, message: '建筑不能为空', trigger: 'change' }],
          equipmentRuntime: [{ required: true, message: '设备运行时间不能为空', trigger: 'change' }]

        },
        userNameList: [],
        organizationList: []
      }
    },
    created() {
      this.getUserList()
      this.getOrganizationList()
      this.getList()
    },
    methods: {
      /** 用户列表 */
      getUserList() {
        listUser(this.queryParams).then(response => {
          this.userNameList=[];
          response.data.forEach((item)=>{
            this.userNameList.push({
              id:item.id.toString(),
              name:item.name
            })
          })
        })
      },
      /** 组织机构列表 */
      getOrganizationList() {
        listOrganization(this.queryParams).then(response => {
          this.organizationList=[];
          response.data.forEach((item)=>{
            this.organizationList.push({
              id:item.id.toString(),
              name:item.name
            })
          })
        })
      },
      /** 查询园区列表 */
      getList() {
        this.loading = true
        listPark(this.queryParams).then(response => {
          this.parkList = response.rows
          this.total = response.total
          this.loading = false
        })
      },
      // 取消按钮
      cancel() {
        this.open = false
        this.reset()
      },
      // 表单重置
      reset() {
        this.form = {
          code: null,
          userName: null,
          organizationId: null,
          name: null,
          nodeCode: null,
          allArea: null,
          monitorArea: null,
          heatArea: null,
          personNums: null,
          buildTime: null,
          equipmentRuntime: null,
          contactName: null,
          contactPhone: null,
          contactEmail: null,
          createTime: null,
          updateTime: null,
          longitude: null,
          latitude: null
        }
        this.resetForm('form')
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1
        this.getList()
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm('queryForm')
        this.handleQuery()
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.code)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset()
        this.open = true
        this.title = '添加园区'
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset()
        const code = row.code || this.ids
        getPark(code).then(response => {
          this.form = response.data
          this.open = true
          this.title = '修改园区'
        })
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs['form'].validate(valid => {
          if (valid) {
            if (this.form.code != null) {
              updatePark(this.form).then(response => {
                this.$modal.msgSuccess('修改成功')
                this.open = false
                this.getList()
              })
            } else {
              addPark(this.form).then(response => {
                this.$modal.msgSuccess('新增成功')
                this.open = false
                this.getList()
              })
            }
          }
        })
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const codes = row.code || this.ids
        this.$modal.confirm('是否确认删除园区编号为"' + codes + '"的数据项？').then(function() {
          return delPark(codes)
        }).then(() => {
          this.getList()
          this.$modal.msgSuccess('删除成功')
        }).catch((response) => {
          var str = response+''
          if(str.indexOf('关联')>0){
            this.$confirm(str.substring(6,str.length), '提示', {
              confirmButtonText: '确定',
              type: 'warning'
            })
          }

        })
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('basicData/parkInfo/export', {
          ...this.queryParams
        }, `园区基本信息.xlsx`)
      }
    }
  }
</script>
