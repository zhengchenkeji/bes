<!-- 功能定义 -->
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <!-- <el-form-item label="所属领域" prop="field">
        <el-select v-model="queryParams.field" placeholder="请选择领域id" clearable size="small">
          <el-option
            v-for="dict in dict.type.iot_category_field"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item> -->
      <el-form-item label="所属品类" prop="categoryId">
        <el-input ref="categoryName" clearable size="small" v-model="queryParams.categoryName" @click.native="handleCategoryFocus()"
        placeholder="请选择所属品类"/>
      </el-form-item>
      <el-form-item label="功能类型" prop="functionType">
        <el-select v-model="queryParams.functionType" placeholder="请选择功能类型" clearable size="small">
          <el-option v-for="dict in dict.type.iot_function_function_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="功能名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入功能名称" clearable size="small"
                  @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item label="数据类型" prop="dataType">
        <el-select v-model="queryParams.dataType" placeholder="请选择数据类型" clearable size="small">
          <el-option v-for="dict in dict.type.iot_function_data_type" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="读写类型" prop="readWriteType">
        <el-select v-model="queryParams.readWriteType" placeholder="请选择读写类型" clearable size="small">
          <el-option v-for="dict in dict.type.iot_function_read_write_type" :key="dict.value" :label="dict.label"
                     :value="dict.value"/>
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
                   v-hasPermi="['iot:function:add']">新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
                   v-hasPermi="['iot:function:edit']">修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple"
                   @click="handleDelete" v-hasPermi="['iot:function:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini"
                   @click="handleExport" v-hasPermi="['iot:function:export']">导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="functionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" width="50" align="center" type="index"/>
      <el-table-column label="所属品类" align="center" prop="categoryName"/>
      <el-table-column label="功能类型" align="center" prop="functionType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.iot_function_function_type" :value="scope.row.functionType"/>
        </template>
      </el-table-column>
      <el-table-column label="功能名称" align="center" prop="name"/>
      <el-table-column label="标识符" align="center" prop="identifier"/>
      <el-table-column label="读写类型" align="center" prop="readWriteType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.iot_function_read_write_type" :value="scope.row.readWriteType"/>
        </template>
      </el-table-column>
      <el-table-column label="数据类型" align="center" prop="dataType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.iot_function_data_type" :value="scope.row.dataType"/>
        </template>
      </el-table-column>
      <el-table-column label="单位" align="center" prop="unit"/>
      <el-table-column label="取值范围" align="center" prop="valueRangeUpper"/>
      <el-table-column label="取值范围" align="center" prop="valueRangeFloor"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-plus" @click="fv_analysisOpen(scope.row)"
                     v-hasPermi="['iot:value:add']">协议配置
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-plus" @click="fv_handleOpen(scope.row)"
                     v-hasPermi="['iot:value:add']">功能值定义
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['iot:function:edit']">修改
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['iot:function:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 添加或修改功能定义对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="所属品类" prop="categoryId" clearable>
          <el-input ref="categoryName" size="small" v-model="form.categoryName" @focus="handleCategoryFocus()"
          placeholder="请选择所属品类" />
        </el-form-item>
        <el-form-item label="功能类型" prop="functionType">
          <el-select v-model="form.functionType" placeholder="请选择功能类型" class="select-width">
            <el-option v-for="dict in dict.type.iot_function_function_type" :key="dict.value" :label="dict.label"
                       :value="parseInt(dict.value)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="功能名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入功能名称"/>
        </el-form-item>
        <el-form-item label="标识符" prop="identifier">
          <el-input v-model="form.identifier" placeholder="请输入标识符"/>
        </el-form-item>
        <el-form-item label="读写类型" prop="readWriteType">
          <el-select v-model="form.readWriteType" placeholder="请选择读写类型" class="select-width">
            <el-option v-for="dict in dict.type.iot_function_read_write_type" :key="dict.value" :label="dict.label"
                       :value="parseInt(dict.value)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="数据类型" prop="dataType">
          <el-select v-model="form.dataType" placeholder="请选择数据类型" class="select-width">
            <el-option v-for="dict in dict.type.iot_function_data_type" :key="dict.value" :label="dict.label"
                       :value="parseInt(dict.value)"/>
          </el-select>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位"/>
        </el-form-item>
        <el-form-item label="取值范围上限" prop="valueRangeUpper">
          <el-input v-model="form.valueRangeUpper" placeholder="请输入取值范围（上限）"/>
        </el-form-item>
        <el-form-item label="取值范围下限" prop="valueRangeFloor">
          <el-input v-model="form.valueRangeFloor" placeholder="请输入取值范围（下限）"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="协议配置" :visible.sync="xy.analysis" width="500px" append-to-body>
      <el-form ref="xyform" :model="form" :rules="rules" label-width="110px">

        <el-form-item label="协议起始位" prop="analysisStart">
          <el-input v-model="form.analysisStart" type="number" ref="start" placeholder="请输入协议起始位"/>
        </el-form-item>

        <el-form-item label="协议终止位" prop="analysisEnd">
          <el-input v-model="form.analysisEnd" type="number" ref="end" placeholder="请输入协议终止位" :style="{width: '60%'}" />
          <el-button type="text" @click="endAdd('2')" style="padding-left: 1vw;">+2</el-button>
          <el-button type="text" @click="endAdd('4')">+4</el-button>
          <el-button type="text" @click="endAdd('8')">+8</el-button>
        </el-form-item>

        <el-form-item label="协议处理类型" prop="analysisType">
          <el-select v-model="form.analysisType" placeholder="请选择协议处理类型" clearable :style="{width: '100%'}">
            <el-option
              v-for="item in xy.AnalysisList"
              :key="item.analysisType"
              :label="item.typeName"
              :value="item.analysisType"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="是否解析" prop="analysisOpen">
          <el-radio-group v-model="form.analysisOpen" size="small">
            <el-radio-button label="0">启用</el-radio-button>
            <el-radio-button label="1">停用</el-radio-button>
          </el-radio-group>
        </el-form-item>


      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitxyForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!--抽屉-->
    <el-drawer title="功能值定义" :visible.sync="fv.drawer" :direction="fv.direction"
               :before-close="fv_handleClose">
      <div style="margin-left: 15px;margin-right: 15px;">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5" style="padding-left: 7px;">
            <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="fv_handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5" style="float: right;padding-right: 7px;">
            <el-button type="primary" @click="fv_handleSave">保 存</el-button>
          </el-col>
        </el-row>
        <el-table v-loading="loading" :data="fv.valueList">
          <el-table-column label="序号" width="55" align="center" type="index"/>
          <el-table-column label="功能名称" align="center" prop="functionName"/>
          <el-table-column label="功能值" align="center" prop="value">
            <template slot-scope="scope">
              <el-input placeholder="请输入内容" v-show="scope.row.show" v-model="scope.row.value"></el-input>
              <span v-show="!scope.row.show">{{ scope.row.value }}</span>
            </template>
          </el-table-column>
          <el-table-column label="名称" align="center" prop="name">
            <template slot-scope="scope">
              <el-input placeholder="请输入内容" v-show="scope.row.show" v-model="scope.row.name"></el-input>
              <span v-show="!scope.row.show">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column label="备注" align="center" prop="comments">
            <template slot-scope="scope">
              <el-input placeholder="请输入内容" v-show="scope.row.show" v-model="scope.row.comments"></el-input>
              <span v-show="!scope.row.show">{{ scope.row.comments }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-delete" @click="fv_handleDelete(scope)">删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>
    <!-- 所属品类抽屉 -->
    <el-drawer title="所属品类" :visible.sync="drawerCategory.visible" :direction="drawerCategory.direction"
    :modal="drawerCategory.modal" :before-close="category_handleClose" @open="handleDrawerCategoryOpen">
      <div style="margin-left: 15px; margin-right: 15px">
        <div style="margin-bottom: 15px;">
          <!-- 选择下拉框和搜索 -->
          <el-row>
            <el-col>
              <el-input placeholder="请输入品类名称" v-model="category.queryParams.categoryName">
                <el-button slot="append" icon="el-icon-search" @click="handleCategoryNameClick"></el-button>
              </el-input>
            </el-col>
          </el-row>
        </div>
        <el-table
          :data="category.categoryList"
          style="width: 100%"
          v-loading="loading">
          <el-table-column label="序号" width="55" align="center" type="index" />
          <el-table-column label="所属品类" align="center" prop="categoryId">
            <template slot-scope="scope">
              {{scope.row.categoryName}}
              <el-button style="color: #CCCCCCFF" type="text"></el-button>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button :disabled="form.categoryId == scope.row.id || queryParams.categoryId == scope.row.id" @click="categoryHandleClick(scope.row)"
              type="text" size="small">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 品类列表分页 -->
        <pagination
          v-show="category.total>0"
          :total="category.total"
          :page.sync="category.queryParams.pageNum"
          :limit.sync="category.queryParams.pageSize"
          @pagination="getListCategory"
          layout="prev, pager, next"
        />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import {
  listFunction,
  getFunction,
  delFunction,
  addFunction,
  updateFunction,
  exportFunction
} from "@/api/iot/function";
// 所属品类
import {
  listCategory,
} from "@/api/iot/category";
// 功能值定义
import {delValue, listValue, saveFvData} from "@/api/iot/value";
// 功能定义_协议解析处理类型
import {getAnalysis} from "@/api/iot/analysis"

export default {
  name: "Function",
  dicts: [
    'iot_function_function_type', // 功能类型字典
    'iot_function_data_type', // 数据类型字典
    'iot_function_read_write_type', // 读写类型字典
  ],
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
      // 功能定义表格数据
      functionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 品类List
      categoryList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        categoryId: null,
        categoryName: null,
        functionType: null,
        name: null,
        identifier: null,
        dataType: null,
        unit: null,
        valueRangeUpper: null,
        valueRangeFloor: null,
        readWriteType: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        categoryId: [{
          required: true,
          message: "所属品类不能为空",
          trigger: "change"
        }],
        functionType: [{
          required: true,
          message: "功能类型不能为空",
          trigger: "change"
        }],
        name: [{
          required: true,
          message: "功能名称不能为空",
          trigger: "blur"
        }],
        identifier: [{
          required: true,
          message: "标识符不能为空",
          trigger: "blur"
        }],
        readWriteType: [{
          required: true,
          message:"读写类型不能为空",
          trigger: "change"
        }],
        dataType: [{
          required: true,
          message: "数据类型不能为空",
          trigger: "change"
        }],
      },
      // 功能值定义
      fv: {
        drawer: false,
        direction: 'rtl',
        valueList: [],
        originalValueList: [],// 原始值
        functionKey: '',
        name: '',
      },
      // 协议配置
      xy: {
        analysis: false,
        // 协议处理类型
        AnalysisList:[],
      },
      drawerCategory: {
        visible: false,
        direction: 'rtl',
        modal: false
      },
      // 品类抽屉定义
      category: {
        drawer: false,
        direction: 'rtl',
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          categoryName: undefined
        },
        total: 0,
        // 品类表格数据
        categoryList: []
      },
    };
  },
  async created() {
    this.getList();
    getAnalysis().then(res=>{
      this.xy.AnalysisList = res.rows
    })
  },
  methods: {
    /** 协议终止位添加操作 */
    endAdd(addValue) {
      let start = this.$refs.start.value;
      if(start==undefined){
        this.$modal.msgWarning("协议起始位不能为空")
      }else {
        this.form.analysisEnd = ((start*1)+(addValue*1))
      }
    },
    /** 查询功能定义列表 */
    getList() {
      this.loading = true;
      listFunction(this.queryParams).then(response => {
        this.functionList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleCategoryFocus(){
      if (!this.drawerCategory.visible) {
        // 去除所属品类焦点
        this.$refs.categoryName.blur();
        // 显示“选择品类”抽屉
        this.drawerCategory.visible = true;
      }
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.xy.analysis = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        categoryId: null,
        categoryName: null,
        functionType: null,
        name: null,
        identifier: null,
        dataType: null,
        unit: null,
        valueRangeUpper: null,
        valueRangeFloor: null,
        readWriteType: null,
        analysisStart: null,
        createTime: null,
        analysisEnd: null,
        updateTime: null,
        analysisType: null,
        analysisOpen: null
      };
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        field: null,
        categoryId: null,
        categoryName: null,
        functionType: null,
        name: null,
        identifier: null,
        dataType: null,
        unit: null,
        valueRangeUpper: null,
        valueRangeFloor: null,
        readWriteType: null,
      },
      this.resetForm("form");
      this.resetForm("xyform");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.reset();
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {

      this.reset();
      this.open = true;
      // 区分两个品类
      this.title = "添加功能定义";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getFunction(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改功能定义";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateFunction(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addFunction(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    // 协议修改
    submitxyForm(){
      this.$refs["xyform"].validate(valid => {
        if (valid) {
          updateFunction(this.form).then(response => {
            this.$modal.msgSuccess("协议修改成功");
            this.xy.analysis = false;
            this.getList();
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除功能定义编号为"' + ids + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return delFunction(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/iot/function/export', {
        ...this.queryParams
      }, `log_${new Date().getTime()}.xlsx`)
    },
    /** 抽屉开启 */
    fv_handleOpen(row) {
      this.getFvListValue(row.id);
      this.fv.name = row.name;
      this.fv.functionKey = row.id;
      this.fv.drawer = true
    },
    /** 协议配置抽屉 */
    fv_analysisOpen(row) {
      this.reset();
      const id = row.id || this.ids
      getFunction(id).then(response => {
        this.form = response.data;
        this.xy.analysis = true
      });
    },
    // 选择所属品类点击事件
    categoryHandleClick(item){
      if(this.open){
        this.form.categoryId = item.id;
        this.form.categoryName = item.categoryName;
      }else{
        this.queryParams.categoryId = item.id;
        this.queryParams.categoryName = item.categoryName;
      }
      this.$forceUpdate();
    },
    /** 品类名称输入框点击搜索 **/
    handleCategoryNameClick(row){
      this.category.queryParams.pageNum = 1;;
      this.getListCategory();
    },
    /** Drawer 打开的回调 **/
    handleDrawerCategoryOpen() {
      this.getListCategory();
    },
    getListCategory() {
      listCategory(this.category.queryParams).then(response => {
        this.category.categoryList = response.rows;
        this.category.total = response.total;
      });
    },
    /*品类选择抽屉关闭方法*/
    category_handleClose(done){
      done();
      this.form.categoryId = null;
      this.queryParams.categoryId = null;
      // this.category.queryParams['categoryId'] = '';
      this.category.queryParams['categoryName'] = '';
      this.getListCategory();
      this.$forceUpdate();
    },
    /** 刷新抽屉数据 */
    getFvListValue(id){
      let query = {
        pageNum: null,
        functionKey: id
      }
      listValue(query).then(response => {
        let list = response.rows;
        if(list.length == 0){
          this.fv.valueList = []
        }
        this.fv.originalValueList = list;
        list.forEach(element => {
          element ["show"] = true
        })
        this.fv.valueList = list
      });
    },
    /** 抽屉关闭 */
    fv_handleClose(done) {
      done();
    },

    /** 抽屉新增按钮 */
    fv_handleAdd() {
      let name = this.fv.name;
      let functionKey = this.fv.functionKey;
      let map = {functionKey: functionKey, value: '', functionName: name, comments: '', show: true};
      this.fv.valueList.push(map);
    },
    /** 抽屉删除按钮 */
    fv_handleDelete(scope) {
      delValue(scope.row.id);
      this.$modal.msgSuccess("删除成功");
      this.getFvListValue(this.fv.functionKey);
    },
    /** 抽屉保存 */
    fv_handleSave(){
      let form = {
        saveFvDataList: this.fv.valueList
      }
      saveFvData(form).then(response => {
        this.$modal.msgSuccess("功能值定义成功！");
        this.getFvListValue(this.fv.functionKey);
      });
    }
  }
};
</script>
<style scoped lang="css">
@import '../css/iot.css';

/* ::v-deep .el-drawer {
  width: 40% !important;
} */
</style>
