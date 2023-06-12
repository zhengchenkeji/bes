<template>
  <div class="app-container">
    <div style="width:15%;float:left;height:90%;overflow-y: auto;">

      <el-tree
        ref="tree"
        :props="defaultProps"
        :data="energyTree"
        node-key="id"
        default-expand-all
        highlight-current
        :check-on-click-node="true"
        :expand-on-click-node="false"
        @node-click="queryRightTab"
        style="max-height: 94%;overflow: auto;overflow-y: auto;overflow-x: auto;">
      </el-tree>
    </div>

    <div style="width:85%;float:right;">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="110px">
        <el-form-item label="采集方案名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入采集方案名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="采集方案编号" prop="code">
          <el-input
            v-model="queryParams.code"
            placeholder="请输入采集方案编号"
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
            v-hasPermi="['basicData:collMethod:add']"
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
            v-hasPermi="['basicData:collMethod:edit']"
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
            v-hasPermi="['basicData:collMethod:remove']"
          >删除
          </el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
      <!--右侧表格-->
      <el-table v-loading="loading" :data="methodList" @selection-change="handleSelectionChange"
                style="max-height: 65vh;overflow-y: auto;">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="编号" align="center" prop="code"/>
        <el-table-column label="名称" align="center" prop="name"/>
        <el-table-column label="园区编号" align="center" prop="parkCode"/>
        <el-table-column label="能源类型" align="center" prop="energyCode"/>
        <el-table-column label="创建时间" align="center" prop="createTime"/>
        <el-table-column label="修改时间" align="center" prop="updateTime"/>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit-outline"
              @click="handleParam(scope.row)"
              v-hasPermi="['basicData:collMethod:edit']"
            >包含采集参数
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['basicData:collMethod:edit']"
            >修改
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['basicData:collMethod:remove']"
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
    </div>
    <!-- 添加或修改采集方案对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称"/>
        </el-form-item>
        <el-form-item label="能源类型" prop="energyCode">
          <el-select v-model="form.energyCode" style="width: 380px;">
            <el-option
              v-for="item in energyList"
              :key="item.F_NYBH"
              :label="item.F_NYMC"
              :value="item.F_NYBH">
            </el-option>
          </el-select>
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
        <el-button   type="primary" style="margin-left:100px;">未选择</el-button>
        <el-form :model="queryParamsRlgl" ref="queryParamsRlgl" size="small" :inline="true" label-width="110px">
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
        <el-table v-loading="loadingParam" :data="noCheckList" style="height:90%;overflow-y: auto;"
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
      <el-button type="primary" style="margin-left:10px;position: absolute;right: 55%;bottom: 45%;" icon="el-icon-right" circle
                 @click="allCheckList">
      </el-button>
      <el-button type="primary" style="margin-left:10px;position: absolute;right: 55%;bottom: 65%;" icon="el-icon-back"
                 circle @click="allNoCheckList">
      </el-button>
      <el-col :span="12" style="float: right">
        <el-button   type="primary" style="margin-left:130px;">已选择</el-button>
        <el-form :model="queryParamsRlgl" ref="queryParamsRlgl" size="small" :inline="true" label-width="110px">
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
          <el-table-column label="统计参数" align="center" prop="statisticalParam">
            <template slot-scope="scope">
              <el-select v-model="scope.row.statisticalParam" @change="changeRlglVlaue($event,scope.row)">
                <el-option
                  v-for="item in statisticalParamList"
                  :key="item.id"
                  :label="item.value"
                  :value="item.id">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="变化" align="center" prop="isRate">
            <template slot-scope="scope">
              <el-select v-model="scope.row.isRate" @change="changeRlglVlaue($event,scope.row)">
                <el-option
                  v-for="item in isRatList"
                  :key="item.id"
                  :label="item.value"
                  :value="item.id">
                </el-option>
              </el-select>
            </template>
          </el-table-column>
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
    listEnergy, leftTree, listNoCheckList, listCheckList, changeRlglVlaue,
    listMethod, allCheckList, allNoCheckList, insertElectricCollRlgl, deleteElectricCollRlgl,
    getMethod,
    delMethod,
    addMethod,
    updateMethod
  } from '@/api/basicData/energyCollection/collMethod/collMethod'

  export default {
    name: 'Method',
    data() {
      return {
        tableHeight: '',
        defaultProps: {
          id: 'id',
          label: 'text',
          children: 'nodes'
        },
        energyCode: '',
        parkCode: '',
        collCode: '',
        collId: '',
        energyTree: [],
        energyList: [],
        noCheckList: [],
        checkList: [],
        isRatList: [{'id': '0', 'value': '否'}, {'id': '1', 'value': '是'}],
        statisticalParamList: [{'id': '0', 'value': '否'}, {'id': '1', 'value': '是'}],
        // 遮罩层
        loading: true,
        loadingParam: false,
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
        // 采集方案表格数据
        methodList: [],
        // 弹出层标题
        title: '',
        // 是否显示弹出层
        open: false,
        openParam: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          name: null,
          parkCode: null,
          energyCode: null
        },
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
          parkCode: null,
        },
        // 表单参数
        form: {},
        formRlgl: {},
        // 表单校验
        rules: {
          name: [{required: true, message: '采集方案名称不能为空', trigger: 'blur'}],
          energyCode: [{required: true, message: '能源类型不能为空', trigger: 'change'}]
        }
      }
    },
    created() {
      this.getTreeList()
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
      //修改能耗信息
      changeRlglVlaue(event, row) {
        this.loadingParam = true
        this.formRlgl.collCode = this.collCode
        this.formRlgl.collId = this.collId
        this.formRlgl.electricCode = row.code
        this.formRlgl.statisticalParam = row.statisticalParam
        this.formRlgl.isRate = row.isRate
        this.formRlgl.id = row.id
        // console.log('row.statisticalParam:' + row.statisticalParam)
        // console.log('row.statisticalParam:' + row.isRate)
        changeRlglVlaue(this.formRlgl).then(response => {
          this.loadingParam = true
          listNoCheckList(this.queryParamsRlgl).then(response => {
            this.noCheckList = response
            this.loadingParam = false
          })
          listCheckList(this.queryParamsRlgl).then(response => {
            this.checkList = response
            this.loadingParam = false
          })
        }).catch(response => {
          this.loadingParam = true
          listNoCheckList(this.queryParamsRlgl).then(response => {
            this.noCheckList = response
            this.loadingParam = false
          })
          listCheckList(this.queryParamsRlgl).then(response => {
            this.checkList = response
            this.loadingParam = false
          })
        });
      },
      //选中能耗信息
      insertElectricCollRlgl(row) {
        this.$confirm('确认选中该采集参数？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // console.log(row)
          this.loadingParam = true
          this.formRlgl.collCode = this.collCode
          this.formRlgl.collId = this.collId
          this.formRlgl.electricCode = row.code
          this.formRlgl.electricId = row.id
          this.formRlgl.statisticalParam = '0'
          this.queryParamsRlgl.collCode = this.collCode
          this.formRlgl.id = row.id
          insertElectricCollRlgl(this.formRlgl).then(response => {
            listNoCheckList(this.queryParamsRlgl).then(response => {
              this.noCheckList = response
            })
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
          this.formRlgl.collCode = this.collCode
          this.formRlgl.electricCode = row.code
          this.formRlgl.id = row.id
          this.formRlgl.statisticalParam = '0'
          this.queryParamsRlgl.collCode = this.collCode
          deleteElectricCollRlgl(this.formRlgl).then(response => {
            listNoCheckList(this.queryParamsRlgl).then(response => {
              this.noCheckList = response
            })
            listCheckList(this.queryParamsRlgl).then(response => {
              this.checkList = response
            })
          })
          this.loadingParam = false
        })
      },
      //全部选中能耗信息
      allCheckList() {
        if (this.noCheckList.length == 0) {
          return
        }
        this.loadingParam = true
        this.queryParamsRlgl.collCode = this.collCode
        this.queryParamsRlgl.collId = this.collId
        var _this = this
        allCheckList(this.queryParamsRlgl).then(response => {
          if (response.code == 200) {
            this.noCheckList = []
            setTimeout(() => {
              _this.queryParamsRlgl.collCode = _this.collCode
              listCheckList(_this.queryParamsRlgl).then(response => {
                _this.checkList = response
                _this.loadingParam = false
              })
              listNoCheckList(_this.queryParamsRlgl).then(response => {
                _this.noCheckList = response
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
        this.queryParamsRlgl.collCode = this.collCode
        var _this = this
        allNoCheckList(this.queryParamsRlgl).then(response => {
          if (response.code == 200) {
            this.checkList = []
            setTimeout(() => {
              _this.queryParamsRlgl.collCode = _this.collCode
              listNoCheckList(_this.queryParamsRlgl).then(response => {
                _this.noCheckList = response
                _this.loadingParam = false
              })
              listCheckList(_this.queryParamsRlgl).then(response => {
                _this.checkList = response
                _this.loadingParam = false
              })
            }, 2000)
          }
        })
      },
      //查询能耗类型列表
      getEnergyTypeList() {
        this.queryParams.parkCode = this.parkCode
        listEnergy(this.queryParams).then(response => {
          this.energyList = response
        })
      },
      //查询左侧树
      getTreeList() {
        leftTree().then(response => {
          this.energyTree = response.data
          if (response.data.length > 0) {
            var key = response.data[0].id
            this.parkCode = key.substring(3, key.length)
            this.$nextTick(() => {
              this.$refs['tree'].setCurrentKey(key)
            })
            //取选中的点Id为查询条件
            this.getList()
          }
          this.loading = false
        })
      },
      //点击树节点接在右侧列表
      queryRightTab(data) {
        //console.log('data.id:'+data.id)
        var key = data.id
        if (key.indexOf('_') > 0) {
          this.parkCode = key.substring(3, key.length)
          this.energyCode = ''
        } else {
          var pkey = data.pid
          this.parkCode = pkey.substring(3, pkey.length)
          this.energyCode = key
        }

        this.getList()
      },
      /** 查询采集方案列表 */
      getList() {
        this.loading = true
        this.queryParams.parkCode = this.parkCode
        this.queryParams.energyCode = this.energyCode
        listMethod(this.queryParams).then(response => {
          this.methodList = response.rows
          this.total = response.total
          this.loading = false
        })
      },
      // 取消按钮
      cancel() {
        this.open = false
        this.reset()
      },
      cancelParam() {
        this.openParam = false
      },
      // 表单重置
      reset() {
        this.form = {
          code: null,
          name: null,
          parkCode: null,
          energyCode: null,
          createTime: null,
          updateTime: null
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
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset()
        this.open = true
        this.title = '添加采集方案'
        this.getEnergyTypeList()
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset()
        this.getEnergyTypeList()
        const code = row.id || this.ids
        getMethod(code).then(response => {
          this.form = response.data
          this.open = true
          this.title = '修改采集方案'
        })
      },
      /** 包含能耗参数 */
      handleParam(row) {
        this.queryParamsRlgl.keywordsCheck = null
        this.queryParamsRlgl.keywordsNo = null
        this.queryParamsRlgl.keywords = null
        this.collCode = row.code
        this.collId = row.id
        this.queryParamsRlgl.collCode = this.collCode
        this.queryParamsRlgl.collId = this.collId
        this.queryParamsRlgl.parkCode = this.parkCode
        listNoCheckList(this.queryParamsRlgl).then(response => {
          this.noCheckList = response
        })
        listCheckList(this.queryParamsRlgl).then(response => {
          this.checkList = response
        })
        this.openParam = true
      },
      //查询取消能耗信息
      listNoCheck() {
        this.queryParamsRlgl.keywords = this.queryParamsRlgl.keywordsNo
        // console.log('this.queryParamsRlgl.keywords:'+this.queryParamsRlgl.keywords)
        listNoCheckList(this.queryParamsRlgl).then(response => {
          this.noCheckList = response
        })
      },
      //查询选中能耗信息
      listCheck() {
        this.queryParamsRlgl.keywords = this.queryParamsRlgl.keywordsCheck
        // console.log('this.queryParamsRlgl.keywords:'+this.queryParamsRlgl.keywords)
        listCheckList(this.queryParamsRlgl).then(response => {
          this.checkList = response
        })
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs['form'].validate(valid => {
          if (valid) {
            if (this.form.code != null) {
              updateMethod(this.form).then(response => {
                this.$modal.msgSuccess('修改成功')
                this.open = false
                this.getList()
              })
            } else {
              this.form.parkCode = this.parkCode
              addMethod(this.form).then(response => {
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
        const codes = row.id || this.ids
        this.$modal.confirm('是否确认删除采集方案编号为"' + codes + '"的数据项？').then(function () {
          return delMethod(codes)
        }).then(() => {
          this.getList()
          this.$modal.msgSuccess('删除成功')
        }).catch((response) => {
          // var str = response + ''
          // if (str.indexOf('关联') > 0) {
          //   this.$confirm(str.substring(6, str.length), '提示', {
          //     confirmButtonText: '确定',
          //     type: 'warning'
          //   })
          // }
        })
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('system/method/export', {
          ...this.queryParams
        }, `method_${new Date().getTime()}.xlsx`)
      }
    }
  }
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
