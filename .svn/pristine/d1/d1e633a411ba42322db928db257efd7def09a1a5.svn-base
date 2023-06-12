<template>
  <el-row :gutter="20" class="shuttleBox">
    <el-col :span="11">
      <el-button   type="primary">{{ textLfet }}</el-button>
      <el-input
        class="input"
        v-if="showSearch"
        v-model="searchLeft"
        placeholder="请输入搜索内容"
      ></el-input>
      <el-table :data="leftData" height="430" :row-style="{height: '56px'}" @selection-change="handleLeftSelectionChange">
        <el-table-column
          type="selection"
          width="50"
          align="center"
          fixed="left"
        />
        <div v-for="(item, index) in columns" :key="index">
          <el-table-column
            v-if="item.visible"
            :label="item.label"
            align="center"
            :prop="item.key"
          />
        </div>
      </el-table>
    </el-col>
    <el-col :span="2" class="btns">
      <el-button
        class="btn"
        :disabled="leftMultiple"
        type="primary"
        icon="el-icon-right"
        @click="moveToRight"
        circle
      ></el-button>
      <el-button
        class="btn"
        :disabled="rightMultiple"
        type="primary"
        @click="moveToLeft"
        icon="el-icon-back"
        circle
      ></el-button>
    </el-col>
    <el-col :span="11">
      <el-button type="primary">{{ textRight }}</el-button>
      <el-input
        class="input"
        v-if="showSearch"
        v-model="searchRight"
        placeholder="请输入搜索内容"
      ></el-input>
      <el-table
        :data="rightData"
       height="430"
        @selection-change="handleRightSelectionChange"
      >
        <el-table-column
          type="selection"
          width="50"
          align="center"
          fixed="left"
        />
        <div v-for="(item, index) in columns" :key="index">
          <el-table-column
            v-if="item.visible"
            :label="item.label"
            align="center"
            :prop="item.key"
          />
        </div>
      </el-table>
    </el-col>
  </el-row>
</template>

<script>
export default {
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
          key: "branchId",
          label: "branchId",
          visible: false,
        },
        {
          key: "branchCode",
          label: "支路编号",
          visible: true,
        },
        {
          key: "branchName",
          label: "支路名称",
          visible: true,
        },
      ],
    };
  },

  methods: {
    /** 加载左侧数据 */
    initLeft() {
      this.leftDataList=[]
      for (let i = 0; i < this.dataList.length; i++) {
        if (!this.value.some((item) => item.branchId == this.dataList[i][this.keyName])) {
          this.leftDataList.push(this.dataList[i]);
        }
      }
      this.leftData = [...this.leftDataList];
    },
    /** 加载右侧数据 */
    initRight() {
      this.rightIds=[]
      this.value.forEach((item)=>{
        this.rightIds.push(item.branchId)
      });
      this.rightDataList=this.value
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
          this.rightDataList.push({...this.leftDataList[i]});
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
          this.leftDataList.push(this.rightDataList[i]);
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
