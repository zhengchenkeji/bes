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

    <div style="width:85%;float:right;height:90%;">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="能耗编号" prop="code">
          <el-input
            v-model="queryParams.code"
            placeholder="请输入能耗编号"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="能耗名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入能耗名称"
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
            v-hasPermi="['basicData:acquisitionParam:add']"
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
            v-hasPermi="['basicData:acquisitionParam:edit']"
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
            v-hasPermi="['basicData:acquisitionParam:remove']"
          >删除
          </el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="paramsList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
        <!--<el-table-column label="${comment}" align="center" prop="id" />-->
        <el-table-column label="能耗编号" align="center" prop="code"/>
        <el-table-column label="能耗名称" align="center" prop="name"/>
        <el-table-column label="能源类型" align="center" prop="energyCode"/>
        <el-table-column label="偏移地址" align="center" prop="offsetAddress">
          <template slot-scope="scope">
            <span>{{Number(scope.row.offsetAddress).toString(16)}}</span>
          </template>
        </el-table-column>
        <el-table-column label="编码规则" align="center" prop="dataEncodeType">
          <template slot-scope="scope">
            <span v-if="scope.row.dataEncodeType == 0">bcd编码</span>
            <span v-if="scope.row.dataEncodeType == 1">dec编码</span>
            <span v-if="scope.row.dataEncodeType == 2">其他</span>
          </template>
        </el-table-column>
        <el-table-column label="数据类型" align="center" prop="dataType">
          <template slot-scope="scope">
            <span v-if="scope.row.dataType == 0">int</span>
            <span v-if="scope.row.dataType == 1">float</span>
            <span v-if="scope.row.dataType == 2">double</span>
          </template>
        </el-table-column>
        <el-table-column label="解码顺序" align="center" prop="codeSeq">
          <template slot-scope="scope">
            <span v-if="scope.row.codeSeq == 0">12</span>
            <span v-if="scope.row.codeSeq == 1">21</span>
            <span v-if="scope.row.codeSeq == 2">1234</span>
            <span v-if="scope.row.codeSeq == 3">4321</span>
            <span v-if="scope.row.codeSeq == 4">2143</span>
            <span v-if="scope.row.codeSeq == 5">3412</span>
            <span v-if="scope.row.codeSeq == 6">123456</span>
            <span v-if="scope.row.codeSeq == 7">12345678</span>
          </template>
        </el-table-column>
        <el-table-column label="单位" align="center" prop="unit"/>
        <el-table-column label="小数点位置" align="center" prop="pointLocation"/>
        <el-table-column label="数据长度" align="center" prop="dataLength"/>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['basicData:acquisitionParam:edit']"
            >修改
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['basicData:acquisitionParam:remove']"
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

    <!-- 添加或修改采集参数对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="能耗编号" prop="code" v-if="this.form.id != null">
          <el-input v-model="form.code" placeholder="请输入能耗名称" disabled/>
        </el-form-item>
        <el-form-item label="能耗名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入能耗名称"/>
        </el-form-item>
        <el-form-item label="偏移地址" prop="offsetAddress">
          <el-input v-model="form.offsetAddress" placeholder="请输入偏移地址"/>
        </el-form-item>
        <el-form-item label="编码规则" prop="dataEncodeType">
          <el-select v-model="form.dataEncodeType" style="width: 360px">
            <el-option
              v-for="item in dataEncodeTypeList"
              :key="item.id"
              :label="item.value"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据类型" prop="dataType">
          <el-select v-model="form.dataType" style="width: 360px">
            <el-option
              v-for="item in dataTypeList"
              :key="item.id"
              :label="item.value"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="寄存器数量" prop="registerNumber">
          <el-select v-model="form.registerNumber" style="width: 360px" @change="changeCodeSeq">
            <el-option
              v-for="item in registerNumberList"
              :key="item.id"
              :label="item.value"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="解码顺序" prop="codeSeq">
          <el-select v-model="form.codeSeq" style="width: 360px">
            <el-option
              v-for="item in codeSeqList"
              :key="item.id"
              :label="item.value"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位"/>
        </el-form-item>
        <el-form-item label="小数点位置" prop="pointLocation">
          <el-input type="number" v-model="form.pointLocation" placeholder="请输入小数点位置"/>
        </el-form-item>
        <el-form-item label="数据长度" prop="dataLength">
          <el-input type="number" v-model="form.dataLength" placeholder="请输入数据长度"/>
        </el-form-item>
        <el-form-item label="园区" prop="parkCode">
          <el-select v-model="form.parkCode" style="width: 360px">
            <el-option
              v-for="item in parkCodeList"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="form.remarks" placeholder="请输入数据长度"/>
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
    getParkCodeList,
    leftTree,
    listParams,
    getParams,
    delParams,
    addParams,
    updateParams
  } from '@/api/basicData/energyCollection/acquisitionParam/acquisitionParam'
  import {Message} from 'element-ui'

  export default {
    name: 'Params',
    data() {
      return {
        defaultProps: {
          id: 'id',
          label: 'text'
        },
        energyCode: '',
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
        // 采集参数表格数据
        paramsList: [],
        // 弹出层标题
        title: '',
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          code: null,
          energyCode: null,
          name: null,
          offsetAddress: null,
          dataLength: null,
          dataEncodeType: null,
          unit: null,
          pointLocation: null,
          dataType: null,
          codeSeq: null,
          parkCode: null
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          code: [
            {required: true, message: '能耗编号不能为空', trigger: 'blur'}
          ],
          /*energyCode: [
            { required: true, message: '能源类型FK(energy_type.F_code)不能为空', trigger: 'blur' }
          ],*/
          name: [
            {required: true, message: '能耗名称不能为空', trigger: 'blur'}
          ],
          offsetAddress: [
            {required: true, message: '偏移地址不能为空', trigger: 'blur'}
          ],
          dataLength: [
            {required: true, message: '数据长度不能为空', trigger: 'blur'},
            {
              pattern: /^\+?[0-9]\d*$/,
              message: '请输入正确的数据长度'
            }
          ],
          dataEncodeType: [
            {required: true, message: '编码规则不能为空', trigger: 'change'}
          ],
          unit: [
            {required: true, message: '单位不能为空', trigger: 'blur'}
          ],
          pointLocation: [
            {required: true, message: '小数点位置不能为空', trigger: 'blur'},
            {
              pattern: /^\+?[0-9]\d*$/,
              message: '请输入正确的小数点位置'
            }
          ],
          dataType: [
            {required: true, message: '数据类型不能为空', trigger: 'change'},
          ],
          codeSeq: [
            {required: true, message: '解码顺序不能为空', trigger: 'change'}
          ],
          registerNumber: [
            {required: true, message: '寄存器不能为空', trigger: 'change'}
          ],
          parkCode: [
            {required: true, message: '园区不能为空', trigger: 'change'}
          ]
        },
        //能源树
        energyTree: [],
        //编码规则
        // { id: '', value: '请选择编码规则' },
        dataEncodeTypeList: [{id: 0, value: 'bcd编码'}, {
          id: 1,
          value: 'dec编码'
        }, {id: 2, value: '其他'}],
        //数据类型
        //{id: '', value: '请选择数据类型'},
        dataTypeList: [{id: 0, value: 'int'}, {id: 1, value: 'float'}, {
          id: 2,
          value: 'double'
        }],
        //寄存器数量
        //{id: '', value: '请选择寄存器数量'},
        registerNumberList: [{id: 1, value: 1}, {id: 2, value: 2}, {
          id: 3,
          value: 3
        }, {id: 4, value: 4}],
        //解码顺序
        //{id: '', value: '请选择解码顺序'},
        codeSeqList: [{id: 0, value: '12'}, {id: 1, value: '21'}],
        //园区
        parkCodeList: []
      }
    },
    created() {
      this.getTreeList()
      this.getParkCodeList()
    },
    methods: {
      //获取园区列表
      getParkCodeList() {
        getParkCodeList().then(response => {
          this.parkCodeList = response
        })
      },
      //根据寄存器改变解码顺序
      changeCodeSeq(e) {
        this.form.codeSeq = ''
        if (e == 1) {
          //{id: '', value: '请选择解码顺序'},
          this.codeSeqList = [{id: 0, value: '12'}, {id: 1, value: '21'}]
        }
        if (e == 2) {
          //{id: '', value: '请选择解码顺序'},
          this.codeSeqList = [{id: 2, value: '1234'}, {id: 3, value: '4321'}, {
            id: 4,
            value: '2143'
          }, {id: 5, value: '3412'}]
        }
        if (e == 3) {
          //{id: '', value: '请选择解码顺序'},
          this.codeSeqList = [{id: 6, value: '123456'}]
        }
        if (e == 4) {
          //{id: '', value: '请选择解码顺序'},
          this.codeSeqList = [{id: 7, value: '12345678'}]
        }
      },
      //点击树结构加载右侧列表
      queryRightTab(data) {
        this.energyCode = data.id
        this.getList()
      },
      //加载树结构
      getTreeList() {
        leftTree().then(response => {
          this.energyTree = response.data
          var key = response.data[0].id
          this.energyCode = key
          this.$nextTick(() => {
            this.$refs['tree'].setCurrentKey(key)
          })
          //取选中的点Id为查询条件
          this.getList()
        })
      },
      /** 查询采集参数列表 */
      getList() {
        this.loading = true
        this.queryParams.energyCode = this.energyCode
        listParams(this.queryParams).then(response => {
          this.paramsList = response.rows
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
          code: null,
          energyCode: null,
          name: null,
          offsetAddress: null,
          dataLength: null,
          dataEncodeType: null,
          unit: null,
          pointLocation: null,
          dataType: null,
          codeSeq: null,
          createTime: null,
          updateTime: null,
          parkCode: null,
          remarks: null
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
        this.title = '添加采集参数'
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset()
        const id = row.id || this.ids
        getParams(id).then(response => {
          if (0 <= response.data.codeSeq && response.data.codeSeq <= 1) {
            response.data.registerNumber = 1
          } else if (2 <= response.data.codeSeq && response.data.codeSeq <= 5) {
            response.data.registerNumber = 2
          } else if (response.data.codeSeq == 6) {
            response.data.registerNumber = 3
          } else if (response.data.codeSeq == 7) {
            response.data.registerNumber = 4
          }
          this.changeCodeSeq(response.data.registerNumber)

          response.data.offsetAddress = Number(response.data.offsetAddress).toString(16);

          this.form = response.data
          this.open = true
          this.title = '修改采集参数'
        })
      },
      /** 提交按钮 */
      submitForm() {
        this.form.energyCode = this.energyCode
        this.$refs['form'].validate(valid => {
          if (valid) {
            let aa = this.checkhex(this.form.offsetAddress);

            var fPydz = parseInt(this.form.offsetAddress, 16) // 十六进制转换十进制
            if (isNaN(fPydz)) {
              Message.error('请输入十六进制的偏移地址')
              return
            }

            this.form.offsetAddress = fPydz;

            if (this.form.id != null) {
              updateParams(this.form).then(response => {
                this.$modal.msgSuccess('修改成功')
                this.open = false
                this.getList()
              })
            } else {
              addParams(this.form).then(response => {
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
        const ids = row.id || this.ids
        this.$modal.confirm('是否确认删除采集参数编号为"' + row.code + '"的数据项？').then(function () {
          return delParams(ids)
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
        this.download('system/params/export', {
          ...this.queryParams
        }, `params_${new Date().getTime()}.xlsx`)
      },
      /* 判断是否为16进制 */
      checkhex(hex) {
        console.log(hex);
        let reghex = /^[A-Fa-f0-9]{1,4}$/;
        if (reghex.test(hex)) {
          console.log(true);
          return true;
        } else {
          console.log(false);
          return false;
        }

      },

      /* 判断是否为十进制 */
      checkdec(dec) {
        console.log(dec);
        let regdec = /^-{0,1}\d*\.{0,1}\d+$/;
        if (regdec.test(dec)) {
          console.log(true);
          return true;
        } else {
          console.log(false);
          return false;
        }

      },
    }
  }
</script>
