<template>
  <el-row :gutter="20" class="shuttleBox">
    <el-col :span="8">
      <el-button type="primary">{{ textLfet }}</el-button>
      <el-tabs v-model="tagName" @tab-click="handleClick">
        <el-tab-pane label="bes设备" name="0">
        </el-tab-pane>
        <el-tab-pane label="第三方设备" name="1">
        </el-tab-pane>
      </el-tabs>
      <el-input class="input" v-if="showSearch" v-model="searchLeft" placeholder="请输入搜索内容"></el-input>
      <el-table :data="leftData" height="430" :row-style="{ height: '56px' }"
        @selection-change="handleLeftSelectionChange">
        <el-table-column type="selection" width="50" align="center" fixed="left" />
        <div v-for="(item, index) in columns" :key="index">
          <el-table-column v-if="item.visible" :label="item.label" align="center" :prop="item.key" />
        </div>
        <el-table-column  label="设备类型" align="center" prop="deviceType"  :formatter="handleTypeName"/>
      </el-table>
    </el-col>
    <el-col :span="2" class="btns">
      <el-button class="btn" :disabled="leftMultiple" type="primary" icon="el-icon-right" @click="moveToRight"
        circle></el-button>
      <el-button class="btn" :disabled="rightMultiple" type="primary" @click="moveToLeft" icon="el-icon-back"
        circle></el-button>
    </el-col>
    <el-col :span="14">
      <el-button type="primary">{{ textRight }}</el-button>
      <el-input class="input" v-if="showSearch" v-model="searchRight" placeholder="请输入搜索内容"></el-input>
      <el-table :data="rightData" height="430" @selection-change="handleRightSelectionChange">
        <el-table-column type="selection" width="50" align="center" fixed="left" />
        <div v-for="(item, index) in columns" :key="index">
          <el-table-column v-if="item.visible" :label="item.label" align="center" :prop="item.key" />
        </div>
        <el-table-column  label="设备类型" align="center" prop="deviceType"  :formatter="handleTypeName"/>

        <el-table-column label="采集参数" align="center" prop="electricParam" width="200">
          <template slot-scope="scope">


            <el-select v-model="scope.row.electricParam" style="width: 100%" v-if="scope.row.deviceType==0">
              <!--:disabled="scope.row.type == '1'"-->
              <el-option v-for="item in scope.row.electricParamsList" :key="item.code" :label="item.name"
                :value="item.code" />
            </el-select>

            <el-select v-model="scope.row.electricParam" style="width: 100%" multiple collapse-tags v-if="scope.row.deviceType==1">
              <!--:disabled="scope.row.type == '1'"-->
              <el-option v-for="item in scope.row.electricParamsList" :key="item.id||item.code" :label="item.name"
                :value="item.id||item.code" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="运算符" align="center" prop="operator">
          <template slot-scope="scope">
            <el-select v-model="scope.row.operator" style="width: 100%">
              <el-option v-for="dict in dict.type.sys_operator" :key="dict.value" :label="dict.label"
                :value="dict.value" />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
    </el-col>
  </el-row>
</template>

<script>
import { type } from 'os';
import { debuggerEditPointValue } from '../../../../../api/basicData/deviceManagement/deviceTree/deviceTreePoint'

export default {
  dicts: ['sys_operator'],
  model: {
    prop: "value",
    event: "change",
  },
  watch: {
    searchLeft: {
      handler: function () {
        this.handleQueryLeft();
      },
      deep: true,
    },
    searchRight: {
      handler: function () {
        this.handleQueryRight();
      },
      deep: true,
    },
  },
  props: {
    //绑定数据
    value: {
      type: Array,
      default: () => {
        return [];
      },
    },
    //主键key值
    keyName: {
      type: String,
      default: "id",
    },

    //左侧提示语句
    textLfet: {
      type: String,
      default: "未选择",
    },
    //右侧提示语句
    textRight: {
      type: String,
      default: "已选择",
    },
    dataList: {
      type: Array,
      default: () => {
        return [];
      },
    },
    // 电三方电表数据
    otherList: {
      type: Array,
      default: () => {
        return [];
      },
    },
    /**
     * 是否显示搜索框
     */
    showSearch: {
      type: Boolean,
      default: true,
    },
  },

  data() {
    return {
      tagName: "0",
      //运算符默认值
      operatorAdd: "",
      //左侧搜索
      searchLeft: "",
      //左侧ID集合
      leftIds: [],
      //按钮是否显示
      leftMultiple: true,
      //左侧集合
      leftDataList: [],
      //左侧数据
      leftData: [],

      searchRight: "",
      //右侧ID集合
      rightIds: [],
      rightMultiple: true,
      rightDataList: [],
      rightData: [],

      //列字段
      columns: [
        {
          key: "meterId",
          label: "meterId",
          visible: false,
        },
        {
          key: "sysName",
          label: "电表编号",
          visible: true,
        },
        {
          key: "alias",
          label: "电表别名",
          visible: true,
        },

      ],
    };
  },
  mounted() {
    this.initOperator();
  },
  methods: {
    //处理字典数据
    handleTypeName(row, column, cellValue, index){
      if(cellValue==0){

        return "bes设备"
      }else{
        return "第三方设备"

      }
    },
    //切换数据时的处理
    handleClick(tab, event) {
      this.initLeft();
    },
    /** 加载参数 */
    initOperator() {
      this.getConfigKey("sys_operator_add").then(response => {
        this.operatorAdd = response.msg
      })
    },
    /** 加载左侧数据 */
    initLeft() {

      this.leftDataList = []
      // 处理bes电表
      if (this.tagName == "0") {

        for (let i = 0; i < this.dataList.length; i++) {
          if (!this.value.some((item) => item.meterId == this.dataList[i][this.keyName])) {
            this.leftDataList.push({
              meterId:this.dataList[i].meterId,
              sysName:this.dataList[i].sysName,
              alias:this.dataList[i].alias,
              deviceType:0,
              electricParamsList:this.dataList[i].electricParamsList,
            });
          }
        }
      } else {

        // 处理第三方电表
        for (let i = 0; i < this.otherList.length; i++) {


          if (!this.value.some((item) => item.meterId == this.otherList[i].id)) {
            this.leftDataList.push({
              meterId:this.otherList[i].id,
              sysName:this.otherList[i].id,
              alias:this.otherList[i].name,
              deviceType:1,
              electricParamsList:this.otherList[i].itemDataList
            });
          }
        }
      }
      this.leftData = [...this.leftDataList];

    },
    /** 加载右侧数据 */
    initRight() {
      this.rightIds = []
      this.value.forEach((item) => {
        this.rightIds.push(item.meterId)
      });
      this.rightDataList = this.value
      console.log(this.rightDataList)
      this.rightData = [...this.rightDataList];
      this.$emit(
        "change",
        this.rightDataList
      );
    },
    /** left多选框选中数据 */
    handleLeftSelectionChange(selection) {
      this.leftIds = selection.map((item) => item[this.keyName]);
      this.leftMultiple = !selection.length;
    },
    /** right多选框选中数据 */
    handleRightSelectionChange(selection) {
      this.rightIds = selection.map((item) => item[this.keyName]);
      this.rightMultiple = !selection.length;
    },
    /** 数据向右边转移 */
    moveToRight() {
      for (let i = 0; i < this.leftDataList.length; i++) {
        let a = this.leftIds.findIndex(
          (item) => item == this.leftDataList[i][this.keyName]
        );
        if (a !== -1) {
          //运算符 operator 默认为 加
          this.rightDataList.push({ ...this.leftDataList[i], operator: this.operatorAdd });
          this.$delete(this.leftDataList, i);
          i--;
        }
      }
      this.leftData = this.setData(this.leftDataList, this.searchLeft);
      this.rightData = this.setData(this.rightDataList, this.searchRight);
      this.$emit(
        "change",
        this.rightDataList
      );
    },
    /** 数据向左边转移 */
    moveToLeft() {
      for (let i = 0; i < this.rightDataList.length; i++) {
        let a = this.rightIds.findIndex(
          (item) => item == this.rightDataList[i][this.keyName]
        );
        if (a !== -1) {
          if(this.tagName==this.rightDataList[i].deviceType){
            this.leftDataList.push(this.rightDataList[i]);

          }
          this.$delete(this.rightDataList, i);
          i--;
        }
      }
      this.leftData = this.setData(this.leftDataList, this.searchLeft);
      this.rightData = this.setData(this.rightDataList, this.searchRight);
      this.$emit(
        "change",
        this.rightDataList
      );
    },
    /** 左侧查询 */
    handleQueryLeft() {
      this.leftData = this.setData(this.leftDataList, this.searchLeft);
    },
    /** 右侧查询 */
    handleQueryRight() {
      this.rightData = this.setData(this.rightDataList, this.searchRight);
    },
    /** 添加数据 */
    setData(dataList, search) {
      if (search) {
        let list = [];
        for (let i = 0; i < dataList.length; i++) {
          if (
            this.columns.some((item) => {
              if (typeof dataList[i][item.key] == "string") {
                if (dataList[i][item.key].toLowerCase().includes(search.toLowerCase())) {
                  return true
                }
              } else if (typeof dataList[i][item.key] == "number") {
                if (dataList[i][item.key] === parseInt(search)) {
                  return true
                }
              }
            }
            )
          ) {
            list.push(dataList[i]);
          }
        }
        return list;
      } else {
        return dataList;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.shuttleBox {
  height: 560px;
  text-align: center;

  .input {
    padding: 20px 5px;
  }

  .btns {
    height: 90%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .btn {
      margin: 20px 0;
    }
  }
}
</style>
