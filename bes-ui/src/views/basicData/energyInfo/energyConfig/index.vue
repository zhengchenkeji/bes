<template>
  <div class="app-container">
    <div style="width:15%;float:left;height:90%;overflow-y: auto;">
 
      <el-tree
        ref="tree"
        :props="defaultProps"
        :data="parkTree"
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
      <!--操作按钮-->
      <el-row :gutter="10" class="mb8" style="margin-left: 5px">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-check"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['basicData:energyConfig:add']"
          >保存
          </el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!--右侧表格-->
      <el-table v-loading="loading" :data="typeList" style="margin-left: 5px">
        <el-table-column label="选择" align="center" prop="rlgl">
          <template slot-scope="scope">
            <el-button type="success" v-if="scope.row.rlgl" @click="changeTableRowValue(scope)">已选择</el-button>
            <el-button type="danger" v-if="!scope.row.rlgl" @click="changeTableRowValue(scope)">已取消</el-button>
          </template>
        </el-table-column>
        <el-table-column label="能源类型编号" align="center" prop="code"/>
        <el-table-column label="能源类型名称" align="center" prop="name"/>
        <el-table-column label="单价" align="center" prop="price"/>
        <el-table-column label="耗煤量" align="center" prop="coalAmount"/>
        <el-table-column label="二氧化碳" align="center" prop="co2"/>
      </el-table>

      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </div>
  </div>
</template>

<script>
  import {leftTree, listConfig, addConfig, getAllList} from '@/api/basicData/energyInfo/energyType/energyType'

  export default {
    name: 'Type',
    data() {
      return {
        defaultProps: {
          id: 'id',
          label: 'text'
        },
        parkTree: [],
        parkCode: '',
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
        // 能源类型表格数据
        typeList: [],
        // 弹出层标题
        title: '',
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          name: null,
          code: null,
          coalAmount: null,
          co2: null,
          unit: null
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {}
      }
    },
    created() {
      this.getTreeList()
    },
    methods: {
      //修改表格信息
      changeTableRowValue(data) {
        data.row.rlgl = !data.row.rlgl
      },
      //加载左侧树
      getTreeList() {
        leftTree().then(response => {
          this.parkTree = response.data
          var key = response.data[0].id
          this.parkCode = key
          this.$nextTick(() => {
            this.$refs['tree'].setCurrentKey(key)
          })
          //取选中的点Id为查询条件
          this.getList()
        })
      },
      //点击树加载右侧列表
      queryRightTab(data) {
        this.parkCode = data.id
        this.getList()
      },
      /** 查询能源类型列表 */
      getList() {
        this.loading = true
        this.queryParams.code = this.parkCode
        listConfig(this.queryParams).then(response => {
          this.typeList = response.rows
          this.total = response.total
          this.loading = false
        })
      },
      /** 保存按钮操作 */
      handleAdd() {
        this.loading = true;
        let array = []
        for (var i = 0; i < this.typeList.length; i++) {
          let obj = {
            parkCode: this.parkCode,
            energyCode: this.typeList[i].code,
            check: this.typeList[i].rlgl == true ? '1' : '0',
          }
          array.push(obj)
        }
        addConfig({energyConfigList: array}).then(response => {
          if (response.code == 200) {
            this.$modal.msgSuccess('保存成功')
            this.getList()
          }
        }).catch(response => {
            this.getList()
          }
        )
        this.loading = false;
      },
    }
  }
</script>
