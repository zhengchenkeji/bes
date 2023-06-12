<template >
  <el-tabs v-model="tabName" id="notifier" @tab-click="handleClick">
    <el-tab-pane label="已关联接收组" name="yes">
      <notifierTableList ref="notifierTableList" :link='1' />
    </el-tab-pane>
    <el-tab-pane label="未关联接收组" name="no">
      <notifierTableList2 ref="notifierTableList2" :link='0' />
    </el-tab-pane>
  </el-tabs>
</template>
<script>
import NoitifierTableList from './notifierTableList.vue'
import NoitifierTableList2 from './notifierTableList.vue'

export default {
  name: "alarmNoitifier",

  components: {
    notifierTableList: NoitifierTableList,
    notifierTableList2: NoitifierTableList2

  },
  data() {
    return {
      //  是否关联
      tabName: "yes",
      // 策略id
      tacticsId:undefined,
    };
  },
  methods: {

    // 切换选项卡时
    handleClick(tab, event) {


      if (this.tabName == "yes") {
        this.$refs.notifierTableList.queryParams.tacticsId = this.tacticsId;

        this.$nextTick(()=>{

        this.$refs.notifierTableList.loading = true;
        this.$refs.notifierTableList.queryParams.islink = 1;
        this.$refs.notifierTableList.getNotifierList();
        })
      } else {
        this.$nextTick(()=>{
        this.$refs.notifierTableList2.queryParams.tacticsId = this.tacticsId;
        this.$refs.notifierTableList2.loading = true;
        this.$refs.notifierTableList2.queryParams.islink = 0;
        this.$refs.notifierTableList2.getNotifierList();
        })


      }

    },
    /** 查询告警接收组列表 */
    getNotifierList() {
      this.$nextTick(()=>{
      this.$refs.notifierTableList.queryParams.tacticsId = this.tacticsId;
      this.$refs.notifierTableList.queryParams.islink = 1;
      this.$refs.notifierTableList.getNotifierList();
      })

    },
  },
};
</script>
<style>
#notifier .el-input {
  width: 100px;
}

.pagination-container {

  height: 35px;
}
</style>
