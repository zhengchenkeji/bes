<template>
    <div class="app-container">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="98px">
            <el-form-item label="品类标识" prop="categoryMark">
                <el-input v-model="queryParams.categoryMark" placeholder="请输入品类标识" clearable
                    @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="品类名称" prop="categoryName">
                <el-input v-model="queryParams.categoryName" placeholder="请输入品类名称" clearable
                    @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="是否物联设备" prop="iotQuipment">
                <el-select v-model="queryParams.iotQuipment" placeholder="请选择是否物联设备" clearable>
                    <el-option v-for="dict in dict.type.athena_bes_yes_no" :key="dict.value" :label="dict.label"
                        :value="dict.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="是否关键设备" prop="cruxQuipment">

                <el-select v-model="queryParams.cruxQuipment" placeholder="请选择是否关键设备" clearable>
                    <el-option v-for="dict in dict.type.athena_bes_yes_no" :key="dict.value" :label="dict.label"
                        :value="dict.value" />
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
                    v-hasPermi="['system:category:add']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
                    v-hasPermi="['system:category:edit']">修改</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
                    v-hasPermi="['system:category:remove']">删除</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
                    v-hasPermi="['system:category:export']">导出</el-button>
            </el-col>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="categoryList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="标识" align="center" prop="id" />
            <el-table-column label="品类标识" align="center" prop="categoryMark" />
            <el-table-column label="品类名称" align="center" prop="categoryName" />
            <el-table-column label="是否物联设备" align="center" prop="iotQuipment">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.athena_bes_yes_no" :value="scope.row.iotQuipment" />
                </template>
            </el-table-column>
            <el-table-column label="是否关键设备" align="center" prop="cruxQuipment">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.athena_bes_yes_no" :value="scope.row.cruxQuipment" />
                </template>
            </el-table-column>
            <el-table-column label="备注" align="center" prop="remark" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                    <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
                        v-hasPermi="['system:category:edit']">修改</el-button>
                    <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
                        v-hasPermi="['system:category:remove']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
            @pagination="getList" />

        <!-- 添加或修改品类对话框 -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
            <el-form ref="form" :model="form" :rules="rules" label-width="110px">
                <el-form-item label="品类标识" prop="categoryMark">
                    <el-input v-model="form.categoryMark" placeholder="请输入品类标识" />
                </el-form-item>
                <el-form-item label="品类名称" prop="categoryName">
                    <el-input v-model="form.categoryName" placeholder="请输入品类名称" />
                </el-form-item>
                <el-form-item label="是否物联设备" prop="iotQuipment">
                    <el-select v-model="form.iotQuipment" placeholder="请选择是否物联设备" style="width:100%!important;">
                        <el-option v-for="dict in dict.type.athena_bes_yes_no" :key="dict.value" :label="dict.label"
                            :value="parseInt(dict.value)"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否关键设备" prop="cruxQuipment">
                    <el-select v-model="form.cruxQuipment" placeholder="请选择是否关键设备" style="width:100%!important;">
                        <el-option v-for="dict in dict.type.athena_bes_yes_no" :key="dict.value" :label="dict.label"
                            :value="parseInt(dict.value)"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listCategory, getCategory, delCategory, addCategory, updateCategory } from "@/api/basicData/deviceDefinition/category/category";

export default {
    name: "Category",
    dicts: ['athena_bes_yes_no'],
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
            // 品类表格数据
            categoryList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                categoryMark: null,
                categoryName: null,
                iotQuipment: null,
                cruxQuipment: null,
            },
            // 表单参数
            form: {},
            // 表单校验
            rules: {
                categoryMark: [
                    { required: true, message: "品类标识不能为空", trigger: "blur" }
                ],
                categoryName: [
                    { required: true, message: "品类名称不能为空", trigger: "blur" }
                ],
                iotQuipment: [
                    { required: true, message: "是否物联设备不能为空", trigger: "change" }
                ],
            }
        };
    },
    created() {
        this.getList();
    },
    methods: {
        /** 查询品类列表 */
        getList() {
            this.loading = true;
            listCategory(this.queryParams).then(response => {
                this.categoryList = response.rows;
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
                categoryMark: null,
                categoryName: null,
                iotQuipment: null,
                cruxQuipment: null,
                remark: null,
                createTime: null,
                createBy: null,
                updateTime: null,
                updateBy: null
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
            this.single = selection.length !== 1
            this.multiple = !selection.length
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.open = true;
            this.title = "添加品类";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.reset();
            const id = row.id || this.ids
            getCategory(id).then(response => {
                this.form = response.data;
                this.open = true;
                this.title = "修改品类";
            });
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.form.id != null) {
                        updateCategory(this.form).then(response => {
                            this.$modal.msgSuccess("修改成功");
                            this.open = false;
                            this.getList();
                        });
                    } else {
                        addCategory(this.form).then(response => {
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
            this.$modal.confirm('是否确认删除品类编号为"' + ids + '"的数据项？').then(function () {
                return delCategory(ids);
            }).then(() => {
                this.getList();
                this.$modal.msgSuccess("删除成功");
            }).catch(() => { });
        },
        /** 导出按钮操作 */
        handleExport() {
            this.download('baseData/category/export', {
                ...this.queryParams
            }, `品类定义_${new Date().getTime()}.xlsx`)
        }
    }
};
</script>
  
