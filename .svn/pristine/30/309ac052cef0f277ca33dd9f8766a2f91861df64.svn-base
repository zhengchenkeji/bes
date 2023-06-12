<template>
  <div class="app-container">
    <!--查询条件-->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="模块型号" prop="pointSet">
        <el-input
          v-model="queryParams.moduleCode"
          placeholder="请输入模块型号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模块描述" prop="pointSet">
        <el-input
          v-model="queryParams.description"
          placeholder="请输入模块描述"
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
          v-hasPermi="['basicData:moduleType:add']"
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
          v-hasPermi="['basicData:moduleType:edit']"
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
          v-hasPermi="['basicData:moduleType:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!--右侧表格-->
    <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <!--<el-table-column label="${comment}" align="center" prop="id" />-->
      <el-table-column label="模块型号" align="center" prop="moduleCode"/>
      <el-table-column label="类型代码" align="center" prop="typeCode"/>
      <el-table-column label="模块描述" align="center" prop="description"/>
      <el-table-column label="模块点数" align="center" prop="pointSetNumber"/>
      <el-table-column label="模块点集合" align="center" prop="pointSet"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['basicData:moduleType:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['basicData:moduleType:remove']"
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

    <!-- 添加或修改模块类型定义对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body class="abow_dialog">
      <div style="width:65%;float:left;height:80%;overflow-y: auto;">
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="模块型号" prop="moduleCode">
            <el-input v-model="form.moduleCode" placeholder="请输入模块型号"/>
          </el-form-item>
          <!--        下位机区分温控器-->
          <el-form-item label="类型代码" prop="typeCode">
            <el-input type="number" v-model="form.typeCode" placeholder="请输入类型代码"/>
          </el-form-item>
          <el-form-item label="模块点数" prop="pointSetNumber">
            <el-input type="number" :min="0" :max="40" v-model="form.pointSetNumber" placeholder="请输入模块点数"
                      @input="changePointSetNUmber"/>
          </el-form-item>
          <el-form-item label="模块描述" prop="description">
            <el-input v-model="form.description" placeholder="请输入模块描述"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button style="margin-left: 42%;margin-bottom: 10px" type="primary" @click="submitForm">确 定</el-button>
          <el-button style="margin-left: 5%;margin-bottom: 10px" @click="cancel">取 消</el-button>
        </div>
      </div>
      <div style="width:35%;float:right;height:90%;overflow-y: auto;" v-if="form.pointSetNumber>0">
        <el-table :data="pointSetNumberList" :height="tableHeight">
          <el-table-column label="模块点数" align="center" prop="sort"/>
          <el-table-column label="模块点类型" align="center" prop="pointSetValue">
            <template slot-scope="scope">
              <el-select v-model="scope.row.value" @change="changePointSetValue($event,scope.row.sort)">
                <el-option
                  v-for="item in pointSetList"
                  :key="item.id"
                  :label="item.value"
                  :value="item.id">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listPoint,
    listModuleType,
    getModuleType,
    delModuleType,
    addModuleType,
    updateModuleType
  } from '@/api/basicData/deviceManagement/moduleType/moduleType'
  import { Message, MessageBox, Notification, Loading } from 'element-ui'

  export default {
    name: 'ModuleType',
    data() {
      return {
        tableHeight:'',
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
        // 模块类型定义表格数据
        typeList: [],
        // 弹出层标题
        title: '',
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          moduleCode: null,
          typeCode: null,
          pointSet: null,
          description: null
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          moduleCode: [
            { required: true, message: '模块型号不能为空', trigger: 'blur' }
          ],
          typeCode: [
            { required: true, message: '类型代码不能为空', trigger: 'blur' },
            { pattern: /^\+?[1-9]\d*$/, message: '请输入正确的类型代码' }
          ],
          pointSet: [
            { required: true, message: '模块类型；0 AI;1 AO;2 DI; 3  DO;4 UI;5 UX不能为空', trigger: 'blur' }
          ],
          pointSetNumber: [
            { required: true, message: '模块点数不能为空', trigger: 'blur' },
            { pattern: /^\+?[1-9]\d*$/, message: '请输入正确的点位数' }
          ],
          createTime: [
            { required: true, message: '创建时间不能为空', trigger: 'blur' }
          ]
        },
        //点类型列表
        pointSetList: [/*{ 'id': '', 'value': '请选择' }, { 'id': '1', 'value': 'AI' }, { 'id': '2', 'value': 'AO' }, {
          'id': '3',
          'value': 'DI'
        }, { 'id': '4', 'value': 'DO' }, { 'id': '5', 'value': 'UI' }, { 'id': '6', 'value': 'UX' }*/],
        //模块点列表
        pointSetNumberList: []
      }
    },
    created() {
      this.getList()
      this.getTableHeight()
      this.getPointSetList();
    },
    mounted() {
      //挂载window.onresize事件(动态设置table高度) 3
      let _this = this;
      window.onresize = () =>{
        if(_this.resizeFlag){
          clearTimeout(_this.resizeFlag);
        }
        _this.resizeFlag = setTimeout(() => {
          _this.getTableHeight();
          _this.resizeFlag = null;
        }, 100);

      };
    },
    methods: {
      //查询点类型列表
      getPointSetList(){
        listPoint(this.queryParams).then(response => {
          this.pointSetList = response.data
        })
      },
      //表格自适应高度
      getTableHeight(){
        let tableH = 210;//距离页面下方的高度
        let tableHeightDetil = window.innerHeight - tableH;
        if(tableHeightDetil <= 300){
          this.tableHeight = 300;
        } else {
          this.tableHeight = window.innerHeight - tableH;
        }
      },
      //输入点位生成右侧点位列表
      changePointSetNUmber(e) {
        if (e > 0) {
          this.pointSetNumberList = []
          for (var i = 1; i <= e; i++) {
            var map = { 'sort': i, 'value': '' }
            this.pointSetNumberList.push(map)
          }
        }
      },
      //修改点位信息
      changePointSetValue(event, sort) {
        this.pointSetNumberList.forEach(item => {
          if (sort == item.sort) {
            item.value = event
          }
        })
      },
      /** 查询模块类型定义列表 */
      getList() {
        this.loading = true
        listModuleType(this.queryParams).then(response => {
          this.typeList = response.rows
          this.typeList.forEach(item => {
            item.pointSetNumber = item.pointSet.length
          })
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
          id: null,
          moduleCode: null,
          typeCode: null,
          pointSet: null,
          description: null,
          createTime: null,
          updateTime: null,
          pointSetNumber: 0
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
        this.queryParams.description = ''
        this.queryParams.moduleCode = ''
        this.handleQuery()
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.getPointSetList();
        this.reset()
        this.open = true
        this.title = '添加模块类型定义'
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.getPointSetList();
        this.reset()
        const id = row.id || this.ids
        getModuleType(id).then(response => {
          this.form = response.data
          this.form.pointSetNumber = this.form.pointSet.length
          if (this.form.pointSetNumber > 0) {
            this.pointSetNumberList = []
            for (var i = 1; i <= this.form.pointSetNumber; i++) {
              var map = { 'sort': i, 'value': this.form.pointSet.substring(i - 1, i) }
              this.pointSetNumberList.push(map)
            }
          }
          this.open = true
          this.title = '修改模块类型定义'
        })
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs['form'].validate(valid => {
          if (valid) {
            var pointSet = ''
            this.pointSetNumberList.forEach(item => {
              pointSet = pointSet + item.value
            })
            if (pointSet == '' || pointSet.length<this.pointSetNumberList.length) {
              Message.error('请检查右侧点位是否配置')
              this.open = true
            } else {
              this.form.pointSet = pointSet
              if (this.form.id != null) {
                updateModuleType(this.form).then(response => {
                  this.$modal.msgSuccess('修改成功')
                  this.open = false
                  this.getList()
                })
              } else {
                addModuleType(this.form).then(response => {
                  this.$modal.msgSuccess('新增成功')
                  this.open = false
                  this.getList()
                })
              }
            }
          }
        })
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids
        this.$modal.confirm('是否确认删除模块类型定义id为"' + ids + '"的数据项？').then(function() {
          return delModuleType(ids)
        }).then(() => {
          this.getList()
          this.$modal.msgSuccess('删除成功')
        }).catch(() => {
        })
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('system/type/export', {
          ...this.queryParams
        }, `type_${new Date().getTime()}.xlsx`)
      }
    }
  }
</script>
<style lang="scss" scoped>
  .abow_dialog {
    display: flex;
    justify-content: center;
    overflow: hidden;
    overflow-y: auto;
    margin-bottom: 20px !important;

    .el-dialog {
      /*height: 80%;*/
      margin-bottom: 20px !important;
      overflow: hidden;
      overflow-y: auto;

      .el-dialog__body {
        margin-bottom: 20px !important;
        position: absolute;
        left: 0;
        top: 54px;
        right: 0;
        padding: 0;
        z-index: 1;
        overflow: hidden;
        overflow-y: auto;
      }
    }
  }
</style>
