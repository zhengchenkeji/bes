<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['system:params:add']"
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
          v-hasPermi="['system:params:edit']"
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
          v-hasPermi="['system:params:remove']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="paramsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="名称" align="center" prop="name"/>
      <el-table-column label="创建人" align="center" prop="createBy"/>
      <el-table-column label="创建时间" align="center" prop="createTime"/>
      <el-table-column label="修改人" align="center" prop="updateBy"/>
      <el-table-column label="修改时间" align="center" prop="updateTime"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:params:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-setting"
            @click="handleConfig(scope.row)"
            v-hasPermi="['system:params:edit']"
          >配置
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:params:remove']"
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

    <!-- 添加或修改主采集参数对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 包含能耗参数对话框 -->
    <el-dialog title="包含能耗参数" :visible.sync="openParam" width="1000px" append-to-body class="abow_dialogCollMethod">
      <el-col :span="8" style="float: left;">
        <el-button type="primary" style="margin-left:100px;">未选择</el-button>
        <el-tabs v-model="tagName" @tab-click="handleClick">
          <el-tab-pane label="bes设备" name="0">
          </el-tab-pane>
          <el-tab-pane label="第三方设备" name="1">
          </el-tab-pane>
        </el-tabs>
        <el-form style="margin-top: 10px" :model="queryParamsRlgl" ref="queryParamsRlgl" size="small" :inline="true"
                 label-width="110px">
          <el-form-item prop="keywordsNo">
            <el-input style="width: 220px"
                      v-model="queryParamsRlgl.keywordsNo"
                      placeholder="请输入参数名称"
                      clearable
            />
            <el-button type="primary" icon="el-icon-search" size="mini" style="margin-left: 10px" @click="listNoCheck">
              搜索
            </el-button>
          </el-form-item>
        </el-form>
        <br/>
        <el-table v-loading="loadingParam" :data="leftTabData" style="height:90%;overflow-y: auto;"
                  :height="tableHeight">
          <el-table-column label="参数名称" align="center" prop="name"/>
          <el-table-column label="能源类型" align="center" prop="energyName"/>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="insertElectricCollRlgl(scope.row)">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <el-button type="primary" style="margin-left:10px;position: absolute;right: 55%;bottom: 45%;" icon="el-icon-right"
                 circle
                 @click="allCheckList">
      </el-button>
      <el-button type="primary" style="margin-left:10px;position: absolute;right: 55%;bottom: 65%;" icon="el-icon-back"
                 circle
                 @click="allNoCheckList">
      </el-button>
      <el-col :span="12" style="float: right">
        <el-button type="primary" style="margin-left:130px;">已选择</el-button>
        <el-form style="margin-top: 10px" :model="queryParamsRlgl" ref="queryParamsRlgl" size="small" :inline="true"
                 label-width="110px">
          <el-form-item prop="keywordsCheck">
            <el-input style="width: 350px"
                      v-model="queryParamsRlgl.keywordsCheck"
                      placeholder="请输入参数名称"
                      clearable
            />
            <el-button type="primary" icon="el-icon-search" size="mini" style="margin-left: 10px" @click="listCheck">
              搜索
            </el-button>
          </el-form-item>
        </el-form>
        <br/>
        <el-table v-loading="loadingParam" :data="checkList" style="height:90%;overflow-y: auto;" :height="tableHeight">
          <el-table-column label="参数名称" align="center" prop="name"/>
          <el-table-column label="能源类型" align="center" prop="energyName"/>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="deleteElectricCollRlgl(scope.row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
      <div slot="footer" class="dialog-footer">
        <el-button style="position: absolute;right: 10px;bottom: 10px;" @click="cancelParam">关闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  import {
    listParams, getParams, delParams, addParams, updateParams, listNoCheckList, listNoCheckListOther,
    listCheckList, insertParamConfigRlgl, delParamConfigRlgl, allCheckList, allNoCheckList
  } from "@/api/systemSetting/parameterConfiguration/parameterConfiguration";

  export default {
    name: "Params",
    data() {
      return {
        //弹窗未选择tab
        tagName: "0",
        /******************/
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
        // 主采集参数表格数据
        paramsList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          name: null,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          name: [{required: true, message: '名称不能为空', trigger: 'blur'}],
        },
        loadingParam: false,
        openParam: false,
        queryParamsRlgl: {
          electricCode: null,
          electricId: null,
          collCode: null,
          collId: null,
          statisticalParam: null,
          isRate: null,
          keywords: null,
          keywordsNo: null,
          keywordsCheck: null,
        },
        formRlgl: {},
        checkList: [],
        noCheckList: [],
        noCheckListOther: [],
        leftTabData: [],
        tableHeight: '',
      };
    },
    created() {
      this.getList();
      this.getTableHeight()
    },
    mounted() {
      //挂载window.onresize事件(动态设置table高度) 3
      let _this = this;
      window.onresize = () => {
        if (_this.resizeFlag) {
          clearTimeout(_this.resizeFlag);
        }
        _this.resizeFlag = setTimeout(() => {
          _this.getTableHeight();
          _this.resizeFlag = null;
        }, 100);

      };
    },
    methods: {
      /** 查询主采集参数列表 */
      handleClick() {
        if (this.tagName == '0') {
          this.leftTabData = this.noCheckList
        } else {
          this.leftTabData = this.noCheckListOther
        }
      },
      /** 查询主采集参数列表 */
      getList() {
        this.loading = true;
        listParams(this.queryParams).then(response => {
          this.paramsList = response.rows;
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
          name: null,
          createBy: null,
          createTime: null,
          updateBy: null,
          updateTime: null
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
        this.title = "添加主采集参数";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids
        getParams(id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改主采集参数";
        });
      },
      //表格自适应
      getTableHeight() {
        let tableH = 380;//距离页面下方的高度
        let tableHeightDetil = window.innerHeight - tableH;
        if (tableHeightDetil <= 300) {
          this.tableHeight = 300;
        } else {
          this.tableHeight = window.innerHeight - tableH;
        }
      },
      cancelParam() {
        this.openParam = false
      },
      /** 配置按钮操作 */
      handleConfig(row) {
        this.tagName = '0'
        this.queryParamsRlgl.keywordsCheck = null
        this.queryParamsRlgl.keywordsNo = null
        this.queryParamsRlgl.keywords = null
        this.queryParamsRlgl.paramId = row.id
        this.openParam = true
        this.loadingParam = true
        this.queryParamsRlgl.deviceType = '0'
        //BES
        listNoCheckList(this.queryParamsRlgl).then(response => {
          this.noCheckList = response
          this.leftTabData = response
          this.queryParamsRlgl.deviceType = '1'
          //已选择
          listCheckList(this.queryParamsRlgl).then(response => {
            this.checkList = response
            //第三方
            listNoCheckListOther(this.queryParamsRlgl).then(response => {
              this.noCheckListOther = response
              this.loadingParam = false
            })
          })
        })
      },
      //选中能耗信息
      insertElectricCollRlgl(row) {
        this.$confirm('确认选中该采集参数？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loadingParam = true
          this.formRlgl.paramId = this.queryParamsRlgl.paramId
          this.formRlgl.paramsId = row.id
          this.formRlgl.deviceType = row.deviceType
          //设置参数类型
          this.queryParamsRlgl.deviceType = row.deviceType
          insertParamConfigRlgl(this.formRlgl).then(response => {
            if (this.tagName == '0') {
              listNoCheckList(this.queryParamsRlgl).then(response => {
                this.noCheckList = response
                this.leftTabData = response
              })
            } else {
              listNoCheckListOther(this.queryParamsRlgl).then(response => {
                this.noCheckListOther = response
                this.leftTabData = response
              })
            }
            listCheckList(this.queryParamsRlgl).then(response => {
              this.checkList = response
            })
          })
          this.loadingParam = false
        })
      },
      //取消能耗信息
      deleteElectricCollRlgl(row) {
        this.$confirm('确认移除该采集参数？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loadingParam = true
          this.formRlgl.paramId = this.queryParamsRlgl.paramId
          this.formRlgl.paramsId = row.id
          //删除选中的
          delParamConfigRlgl(this.formRlgl).then(res => {
            //如果删除的是BES
            if (row.deviceType == '0') {
              //设置参数类型
              this.queryParamsRlgl.deviceType = '0'
              //更新未选中
              listNoCheckList(this.queryParamsRlgl).then(response => {
                this.noCheckList = response
                //当前显示的tab
                if (this.tagName == '0') {
                  this.leftTabData = this.noCheckList
                } else {
                  this.leftTabData = this.noCheckListOther
                }
                //查询已选择
                listCheckList(this.queryParamsRlgl).then(response1 => {
                  this.checkList = response1
                  //取消加载
                  this.loadingParam = false
                })
              })
            } else {
              //删除的是第三方
              //设置参数类型
              this.queryParamsRlgl.deviceType = '1'
              listNoCheckListOther(this.queryParamsRlgl).then(response => {
                this.noCheckListOther = response
                //当前显示的tab
                if (this.tagName == '0') {
                  this.leftTabData = this.noCheckList
                } else {
                  this.leftTabData = this.noCheckListOther
                }
                //查询已选择
                listCheckList(this.queryParamsRlgl).then(response1 => {
                  this.checkList = response1
                  //取消加载
                  this.loadingParam = false
                })
              })
            }
          })
        })
      },
      //全部选中能耗信息
      allCheckList() {
        if (this.leftTabData.length == 0) {
          return
        }
        this.loadingParam = true
        this.queryParamsRlgl.deviceType = this.tagName
        var _this = this
        allCheckList(this.queryParamsRlgl).then(response => {
          if (response.code == 200) {
            if (this.tagName == '0') {
              this.noCheckList = []
              this.leftTabData = this.noCheckList
            } else {
              this.noCheckListOther = []
              this.leftTabData = this.noCheckListOther
            }
            setTimeout(() => {
              // _this.queryParamsRlgl.collCode = _this.collCode
              listCheckList(_this.queryParamsRlgl).then(response => {
                _this.checkList = response
                _this.loadingParam = false
              })
            }, 2000)
          }
        })
      },
      //取消能耗信息
      allNoCheckList() {
        if (this.checkList.length == 0) {
          return
        }
        this.loadingParam = true
        this.queryParamsRlgl.deviceType = ''
        var _this = this
        //全部删除
        allNoCheckList(this.queryParamsRlgl).then(response => {
          if (response.code == 200) {
            this.checkList = []
            setTimeout(() => {
              _this.queryParamsRlgl.deviceType = '0'
              //查询BES
              listNoCheckList(_this.queryParamsRlgl).then(response => {
                _this.noCheckList = response
                _this.queryParamsRlgl.deviceType = '1'
                //查询第三方
                listNoCheckList(_this.queryParamsRlgl).then(response1 => {
                  _this.noCheckListOther = response1
                  if (_this.tagName == '0') {
                    _this.leftTabData = _this.noCheckList
                  } else {
                    _this.leftTabData = _this.noCheckListOther
                  }
                  _this.loadingParam = false
                })
              })
            }, 2000)
          }
        })
      },
      //查询取消能耗信息
      listNoCheck() {
        this.loadingParam = true
        this.queryParamsRlgl.keywords = this.queryParamsRlgl.keywordsNo
        this.queryParamsRlgl.deviceType = '0'
        //查询BES
        listNoCheckList(this.queryParamsRlgl).then(response => {
          this.noCheckList = response
          this.queryParamsRlgl.deviceType = '1'
          //查询第三方
          listNoCheckList(this.queryParamsRlgl).then(response1 => {
            this.noCheckListOther = response1
            //展示
            if (this.tagName == '0') {
              this.leftTabData = this.noCheckList
              this.loadingParam = false
            } else {
              this.leftTabData = this.noCheckListOther
              this.loadingParam = false
            }
          })
        })
      },
      //查询选中能耗信息
      listCheck() {
        this.queryParamsRlgl.keywords = this.queryParamsRlgl.keywordsCheck
        listCheckList(this.queryParamsRlgl).then(response => {
          this.checkList = response
        })
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              updateParams(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addParams(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除主采集参数编号为"' + ids + '"的数据项？').then(function () {
          return delParams(ids);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {
        });
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('system/params/export', {
          ...this.queryParams
        }, `params_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
<style lang="scss" scoped>
  .abow_dialogCollMethod {
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
