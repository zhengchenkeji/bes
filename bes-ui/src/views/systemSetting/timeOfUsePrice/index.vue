<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">

      <el-col :span="1.5" >
        <el-button
          type="success"
          v-if="!isEdit"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="handleUpdate"
          v-hasPermi="['electricPowerTranscription:timeOfUsePrice:update']"
        >修改</el-button>

        <el-button
          type="primary"
          plain
          v-if="isEdit"
          icon="el-icon-success"
          size="mini"
          @click="submit"
          v-hasPermi="['electricPowerTranscription:timeOfUsePrice:update']"
        >保存</el-button>

        <el-button
          type="danger"
          plain
          v-if="isEdit"
          icon="el-icon-error"
          size="mini"
          @click="getList"
          v-hasPermi="['electricPowerTranscription:timeOfUsePrice:update']"
        >取消</el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button
          type="warning"
          v-if="!isEdit"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['systemSetting:electricityPriceSeason:exportTable']"
        >导出</el-button>
      </el-col>

    </el-row>

    <!--表格-->
    <el-table v-loading="loading" :data="table.row">
      <!--<el-table-column type="selection" width="55" align="center"/>-->
      <el-table-column label="" prop="time"  align="center" />
      <el-table-column v-for="item in table.column" :label="item.name" :prop="item.code" align="center">
        <template slot-scope="scope">
          <el-select
            v-if="scope.row.isEdit"
            v-model="scope.row[item.code]"
            placeholder="请选择"
          >
            <el-option
              v-for="dict in dict.type.electricity_price_set_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
          <span v-else>{{ getLabel(scope.row[item.code]) }}</span>
        </template>
      </el-table-column>
    </el-table>

  </div>
</template>

<script>
  import {
    list,update,exportTable
  } from "@/api/systemSetting/timeOfUsePrice/timeOfUsePrice";

  export default {
    name: 'Park',
      dicts: ['electricity_price_set_type'],
      data() {
      return {
          //是否为编辑状态
          isEdit: false,

          // 遮罩层
        loading: true,

        // 表格数据
        table:{
          column:[],
          row:[],
        },
      }
    },
    created() {
        this.getList();
    },
    methods: {
        getList(){
            this.loading = true;
            this.isEdit = false
            list().then(response => {
                this.loading = false;
                if (response.code == 200) {
                    let data = response.data;
                    this.table.column = data.columnList
                    this.table.row = data.dataList
                } else {
                    this.$modal.msgWarning(response.msg);
                }
            });
        },
        getLabel(code){
            return this.selectDictLabel(this.dict.type.electricity_price_set_type, code);
        },

        handleUpdate(){
            this.isEdit = true
            if (this.table.row.length > 0){
                this.table.row.forEach(item => {
                    item.isEdit = true
                })
            }

        },

        submit(){
            if (this.table.row.length > 0){
                //判断选择框是否全部都选择了值
                for (let i=0; i<this.table.row.length; i++){
                    for (let j=0;j<this.table.column.length;j++){
                        if (this.table.row[i][this.table.column[j].code] == null || this.table.row[i][this.table.column[j].code] === ''){
                            this.$modal.msgError("请完善所有的分时电价类型");
                            return;
                        }
                    }
                }
            } else {
                return
            }

            update({
                dataList:this.table.row,
                column:this.table.column
            }).then(response => {
                this.isEdit = false
                this.getList();
            });
        },

      /** 导出按钮操作 */
      handleExport() {

          exportTable().then(response => {
              const blob = new Blob([response]);
              const fileName = '分时电价.xls';
              const elink = document.createElement('a');// 创建a标签
              elink.download = fileName;// 为a标签添加download属性
              // a.download = fileName; //命名下载名称
              elink.style.display = 'none';
              elink.href = URL.createObjectURL(blob);
              document.body.appendChild(elink);
              elink.click();// 点击下载
              URL.revokeObjectURL(elink.href); // 释放URL 对象
              document.body.removeChild(elink);// 释放标签
          })
      }
    }
  }
</script>
