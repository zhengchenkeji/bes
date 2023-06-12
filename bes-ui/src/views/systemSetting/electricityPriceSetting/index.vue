<template>
  <div class="app-container">
    <el-row :gutter="20" class="mb20">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch"
               label-width="68px">
        <el-form-item label="月份" prop="monthDate">
          <el-date-picker
            v-model="queryParams.monthDate"
            type="month"
            value-format="yyyy-MM-dd"
            placeholder="选择月" style="width: 202px">
          </el-date-picker>
        </el-form-item>
<!--        <el-form-item label="结束时间" prop="endTime">-->
<!--          <el-time-picker clearable-->
<!--                          v-model="queryParams.endTime"-->
<!--                          value-format="yyyy-MM-dd  HH:mm:ss"-->
<!--                          placeholder="请选择结束时间">-->
<!--          </el-time-picker>-->
<!--        </el-form-item>-->
        <!--<el-form-item label="单价" prop="price">
          <el-input
            type="number"
            v-model="queryParams.price"
            placeholder="请输入单价"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>-->
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
            v-hasPermi="['systemSetting:electricityPriceSetting:add']"
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
            v-hasPermi="['systemSetting:electricityPriceSetting:edit']"
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
            v-hasPermi="['systemSetting:electricityPriceSetting:remove']"
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
            v-hasPermi="['systemSetting:electricityPriceSetting:export']"
          >导出
          </el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="settingList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"/>
<!--        <el-table-column label="开始时间" align="center" prop="startTime" width="180">-->
<!--          <template slot-scope="scope">-->
<!--            <span>{{ parseTime(scope.row.startTime, '{h}:{i}:{s}') }}</span>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column label="结束时间" align="center" prop="endTime" width="180">-->
<!--          <template slot-scope="scope">-->
<!--            <span>{{ parseTime(scope.row.endTime, '{h}:{i}:{s}') }}</span>-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column label="月份" align="center" prop="monthDate"/>
        <el-table-column label="代理购电价格" align="center" prop="appPrice"/>
        <el-table-column label="容量补偿电价" align="center" prop="cctPrice"/>
        <el-table-column label="代理购电综合损益分摊标准" align="center" prop="ascpappPrice"/>
        <el-table-column label="电度输配电价" align="center" prop="etdpPrice"/>
        <el-table-column label="政府基金及附加" align="center" prop="gfsPrice"/>
        <el-table-column label="尖峰时段" align="center" prop="spikePrice"/>
        <el-table-column label="高峰时段" align="center" prop="peakPrice"/>
        <el-table-column label="平时段" align="center" prop="flatPrice"/>
        <el-table-column label="低谷时段" align="center" prop="troughPrice"/>
        <el-table-column label="深谷时段" align="center" prop="valleyPrice"/>
        <el-table-column label="创建人" align="center" prop="createBy"/>
<!--        <el-table-column label="修改人" align="center" prop="upadteBy"/>-->
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['systemSetting:electricityPriceSetting:edit']"
            >修改
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['systemSetting:electricityPriceSetting:remove']"
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

      <!-- 添加或修改电价设置对话框 -->
      <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
        <el-form ref="form" :model="form" :rules="rules" label-width="120px" :inline="true">

          <!--<el-form-item label="时间段" prop="startTime">
            <el-time-picker
              is-range
              v-model="startEndTime"
              value-format="yyyy-MM-dd HH:mm:ss"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              placeholder="选择时间范围">
            </el-time-picker>
          </el-form-item>-->
          <el-form-item label="月份" prop="monthDate">
            <el-date-picker
              v-model="form.monthDate"
              type="month"
              value-format="yyyy-MM-dd"
              placeholder="选择月" style="width: 202px">
            </el-date-picker>
          </el-form-item>
          <br/>
          <el-form-item label="代理购电价格" prop="appPrice">
            <el-input type="number" v-model="form.appPrice" @blur="handleBlur" placeholder="请输入代理购电价格"/>
          </el-form-item>
          <el-form-item label="容量补偿电价" prop="cctPrice">
            <el-input type="number" v-model="form.cctPrice" @blur="handleBlur" placeholder="请输入容量补偿电价"/>
          </el-form-item>
          <el-form-item label="代理购电综合损益分摊标准" prop="ascpappPrice">
            <el-input type="number" v-model="form.ascpappPrice" @blur="handleBlur" placeholder="请输入代理购电综合损益分摊标准"/>
          </el-form-item>
          <el-form-item label="电度输配电价" prop="etdpPrice">
            <el-input type="number" v-model="form.etdpPrice" @blur="handleBlur" placeholder="请输入电度输配电价"/>
          </el-form-item>
          <el-form-item label="政府基金及附加" prop="gfsPrice">
            <el-input type="number" v-model="form.gfsPrice" @blur="handleBlur" placeholder="请输入政府基金及附加"/>
          </el-form-item>
          <br/>
          <el-form-item label="尖峰时段" prop="spikePrice">
            <el-input type="number" v-model="form.spikePrice" disabled/>
          </el-form-item>
          <el-form-item label="高峰时段" prop="peakPrice">
            <el-input type="number" v-model="form.peakPrice" disabled/>
          </el-form-item>
          <el-form-item label="平时段" prop="flatPrice">
            <el-input type="number" v-model="form.flatPrice" disabled/>
          </el-form-item>
          <el-form-item label="低谷时段" prop="troughPrice">
            <el-input type="number" v-model="form.troughPrice" disabled/>
          </el-form-item>
          <el-form-item label="深谷时段" prop="valleyPrice">
            <el-input type="number" v-model="form.valleyPrice" disabled/>
          </el-form-item>
          <br/>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" placeholder="请输入备注"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </el-row>
  </div>
</template>

<script>
  import {
    listSetting,
    getSetting,
    delSetting,
    addSetting,
    updateSetting,
  } from "@/api/systemSetting/electricityPriceSetting/electricityPriceSetting";

  export default {
    name: "index",
    data() {
      return {
        /*startEndTime: [new Date().getFullYear().toString() + '-' + (new Date().getMonth() < 10 ? ('0' + (new Date().getMonth() + 1)) : (new Date().getMonth() + 1)).toString() + '-' + (new Date().getDate() < 10 ? ('0' + new Date().getDate()) : new Date().getDate()).toString() + ' ' + (new Date().getHours() >= 10 ? new Date().getHours() : ('0' + new Date().getHours())) + ':' + (new Date().getMinutes() >= 10 ? new Date().getMinutes() : ('0' + new Date().getMinutes())) + ':' + (new Date().getSeconds() >= 10 ? new Date().getSeconds() : ('0' + new Date().getSeconds()))
          , new Date().getFullYear().toString() + '-' + (new Date().getMonth() < 10 ? ('0' + (new Date().getMonth() + 1)) : (new Date().getMonth() + 1)).toString() + '-' + (new Date().getDate() < 10 ? ('0' + new Date().getDate()) : new Date().getDate()).toString() + ' ' + (new Date().getHours() >= 10 ? new Date().getHours() : ('0' + new Date().getHours())) + ':' + (new Date().getMinutes() >= 10 ? new Date().getMinutes() : ('0' + new Date().getMinutes())) + ':' + (new Date().getSeconds() >= 10 ? new Date().getSeconds() : ('0' + new Date().getSeconds()))],//时间选择*/
        // 遮罩层
        loading: false,
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
        // 电价设置表格数据
        settingList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          monthDate: null,
          // endTime: null,
          // upadteBy: null,
        },
        // 表单参数
        form: {
          monthDate: null,
          appPrice: null,
          cctPrice: null,
          ascpappPrice: null,
          etdpPrice: null,
          gfsPrice: null,
          spikePrice: null,
          peakPrice: null,
          flatPrice: null,
          troughPrice: null,
          valleyPrice: null,
          remark: null,
          createBy: null,
          createTime: null,
          upadteBy: null,
          updateTime: null
        },
        // 表单校验
        rules: {
          monthDate: [
            {required: true, message: '代理购电价格不能为空', trigger: 'blur'},
          ],
          appPrice: [
            {required: true, message: '代理购电价格不能为空', trigger: 'blur'},
            {
              // pattern: /^([1-9][\d]*|0)(\.[\d]+)?$/,
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的代理购电价格'
            }
          ],
          cctPrice: [
            {required: true, message: '容量补偿电价不能为空', trigger: 'blur'},
            {
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的容量补偿电价'
            }
          ],
          ascpappPrice: [
            {required: true, message: '代理购电综合损益分摊标准不能为空', trigger: 'blur'},
            {
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的代理购电综合损益分摊标准'
            }
          ],
          etdpPrice: [
            {required: true, message: '电度输配电价不能为空', trigger: 'blur'},
            {
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的电度输配电价'
            }
          ],
          gfsPrice: [
            {required: true, message: '政府基金及附加不能为空', trigger: 'blur'},
            {
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的政府基金及附加'
            }
          ],
          spikePrice: [
            {required: true, message: '尖峰单价不能为空', trigger: 'blur'},
            {
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的单价'
            }
          ],
          peakPrice: [
            {required: true, message: '高峰单价不能为空', trigger: 'blur'},
            {
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的单价'
            }
          ],
          flatPrice: [
            {required: true, message: '平时段单价不能为空', trigger: 'blur'},
            {
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的单价'
            }
          ],
          troughPrice: [
            {required: true, message: '低谷时段单价不能为空', trigger: 'blur'},
            {
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的单价'
            }
          ],
          valleyPrice: [
            {required: true, message: '深谷时段单价不能为空', trigger: 'blur'},
            {
              pattern:/^(\-|\+?)\d+(\.\d+)?$/,
              message: '请输入正确的单价'
            }
          ],
        }
      };
    },
    created() {
      this.getList();
    },
    methods: {
      handleBlur() {
        //自动生成尖峰平谷 this.form.appPrice == 0 ||
        if (this.form.appPrice == null || this.form.appPrice == undefined ||
          this.form.cctPrice == null || this.form.cctPrice == undefined ||
          this.form.ascpappPrice == null || this.form.ascpappPrice == undefined ||
          this.form.etdpPrice == null || this.form.etdpPrice == undefined ||
          this.form.gfsPrice == null || this.form.gfsPrice == undefined ) {
          // this.open = true;
          return;
        } else {
          this.form.appPrice = Number(this.form.appPrice)
          this.form.cctPrice = Number(this.form.cctPrice)
          this.form.ascpappPrice = Number(this.form.ascpappPrice)
          this.form.etdpPrice = Number(this.form.etdpPrice)
          this.form.gfsPrice = Number(this.form.gfsPrice)
          //尖峰 2*200% + 3*200% + 4 + 5 + 6
          var spikePriceVar = (Number(this.form.appPrice) * 2) + (Number(this.form.cctPrice) * 2) + Number(this.form.ascpappPrice) + Number(this.form.etdpPrice) + Number(this.form.gfsPrice);
          if(spikePriceVar.toString().indexOf('.') != -1 &&  spikePriceVar.toString().split('.')[1].length > 10){
            this.form.spikePrice = Number(spikePriceVar).toFixed(10);
          }else{
            this.form.spikePrice = Number(spikePriceVar)
          }
          //高峰 2*170% + 3*170% + 4 + 5 + 6
          var peakPriceVar = (Number(this.form.appPrice) * 1.7) + (Number(this.form.cctPrice) * 1.7) + Number(this.form.ascpappPrice) + Number(this.form.etdpPrice) + Number(this.form.gfsPrice);
          if(peakPriceVar.toString().indexOf('.') != -1 && peakPriceVar.toString().split('.')[1].length > 10){
            this.form.peakPrice = Number(peakPriceVar).toFixed(10);
          }else{
            this.form.peakPrice = Number(peakPriceVar)
          }
          //平 2 + 3 + 4 + 5 + 6
          var flatPriceVar = Number(this.form.appPrice) + Number(this.form.cctPrice) + Number(this.form.ascpappPrice) + Number(this.form.etdpPrice) + Number(this.form.gfsPrice);
          if(flatPriceVar.toString().indexOf('.') != -1 && flatPriceVar.toString().split('.')[1].length > 10){
            this.form.flatPrice = Number(flatPriceVar).toFixed(10);
          }else{
            this.form.flatPrice = Number(flatPriceVar)
          }
          //低谷 2*30% + 3*30% + 4 + 5 + 6
          var troughPriceVar = (Number(this.form.appPrice) * 0.3) + (Number(this.form.cctPrice) * 0.3) + Number(this.form.ascpappPrice) + Number(this.form.etdpPrice) + Number(this.form.gfsPrice);
          if(troughPriceVar.toString().indexOf('.') != -1 && troughPriceVar.toString().split('.')[1].length > 10){
            this.form.troughPrice = Number(troughPriceVar).toFixed(10);
          }else{
            this.form.troughPrice = Number(troughPriceVar)
          }
          //深谷 2*10% + 3*10% + 4 + 5 + 6
          var valleyPriceVar = (Number(this.form.appPrice) * 0.1) + (Number(this.form.cctPrice) * 0.1) + Number(this.form.ascpappPrice) + Number(this.form.etdpPrice) + Number(this.form.gfsPrice);
          if(valleyPriceVar.toString().indexOf('.') != -1 && valleyPriceVar.toString().split('.')[1].length > 10){
            this.form.valleyPrice = Number(valleyPriceVar).toFixed(10);
          }else{
            this.form.valleyPrice = Number(valleyPriceVar)
          }
        }

      },
      /** 查询电价设置列表 */
      getList() {
        this.loading = true;
        listSetting(this.queryParams).then(response => {
          this.settingList = response.rows;
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
          monthDate: null,
          appPrice: null,
          cctPrice: null,
          ascpappPrice: null,
          etdpPrice: null,
          gfsPrice: null,
          spikePrice: null,
          peakPrice: null,
          flatPrice: null,
          troughPrice: null,
          valleyPrice: null,
          remark: null,
          createBy: null,
          createTime: null,
          upadteBy: null,
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
        this.title = "添加电价设置";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids
        getSetting(id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改电价设置";
        });
      },
      /** 提交按钮 */
      submitForm() {
        // if (this.startEndTime.length != 2) {
        //   this.$modal.msgWarning("请选择时间段");
        //   this.open = true;
        //   return
        // }
        // this.form.startTime = this.startEndTime[0]
        // this.form.endTime = this.startEndTime[1]
        // this.form.ruleId = this.treeNodeData.id
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              updateSetting(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addSetting(this.form).then(response => {
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
        this.$modal.confirm('是否确认删除电价设置编号为"' + ids + '"的数据项？').then(function () {
          return delSetting(ids);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {
        });
      },
      /** 导出按钮操作 */
      handleExport() {
        this.download('systemSetting/electricityPriceSetting/export', {
          ...this.queryParams
        }, `电价配置_${new Date().getTime()}.xlsx`)
      }
    }
  };
</script>
<style lang="scss" scoped>
  .el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
  }

  .el-icon-arrow-down {
    font-size: 12px;
  }

  .el-tree {
    display: inline-block;
    min-width: 100%;

  }

  .tooltip {
    margin-right: 5px;
    font-size: 13px;
    border-radius: 4px;
    box-sizing: border-box;
    white-space: nowrap;
    padding: 4px;
  }

  .operation-view {
    display: inline-block;
    padding: 0px 5px;
    margin-left: 5px;
    color: #777777;
  }

  .time {
    font-size: 13px;
    color: #999;
  }

  .bottom {
    margin-top: 13px;
    line-height: 12px;
  }

  .button {
    padding: 0;
    float: right;
  }

  .image {
    width: 100%;
    display: block;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }

  .clearfix:after {
    clear: both
  }

  .wait-task-user-box-card {
    height: calc(96vh - 60px - 10px);
  }

  #pane-1 .el-descriptions {
    margin-bottom: 25px;
  }

  .el-scrollbar__wrap {
    overflow-x: hidden;
  }

  .el-drawer__wrapper > > > .el-descriptions-item__cell {
    width: 50%;
  }

  .el-drawer__wrapper > > > .input-with-select .el-input__inner {
    width: 100%;
  }

  .el-drawer__wrapper > > > .input-with-select .el-input-group__append {
    width: 25%;
  }

  .theme-blue {
    .tooltip {
      margin-right: 5px;
      font-size: 13px;
      border-radius: 4px;
      box-sizing: border-box;
      white-space: nowrap;
      padding: 4px;
      color: white;
    }
  }

  .theme-white {
    .tooltip {
      margin-right: 5px;
      font-size: 13px;
      border-radius: 4px;
      box-sizing: border-box;
      white-space: nowrap;
      padding: 4px;
      color: #999;
    }
  }

  .theme-blue .text_white {
    color: white;
  }

  .theme-white .text_white {
    color: #999;
  }

  .input-with-select_title {
    margin: 15px 0;
    color: #000;
  }

  .addDom {
    width: 61%;
  }

  .attributeDropdown {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 3.35vh;
  }

  .theme-blue .addDom .el-icon-question {
    color: white;
  }

  .theme-light, .theme-dark .addDom .el-icon-question {
    color: rgb(204, 204, 204);
  }

  .attributeDropdown {
    width: 20%;
    float: left;
  }

  .attribute {
    width: 80%;
    float: left;
  }

  .cusNumber {
    padding: 0 15px;
    color: #4cbdff;
    font-size: 17px;
    font-weight: bold;
  }
</style>

