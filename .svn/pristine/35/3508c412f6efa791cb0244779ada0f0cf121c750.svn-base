<template>
  <div class="white-body-view">


    <!-- 新增干线、支线 -->
    <el-drawer size='35%'  :title="title" :visible.sync="visible" direction="rtl">

      <el-tree
        :props="props"
        :load="loadNode"
        lazy
        show-checkbox
        @check-change="handleCheckChange">
      </el-tree>
    </el-drawer>
  </div>
</template>

<script>
  // import {
  //   insertLightingTree, updateLightingTree
  // } from '@/api/basicData/deviceManagement/deviceTree/deviceTreePoint'
  // import { deviceTreeSettings } from '../../../../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings'
  export default {

    created() {

      this.queryslaveAddressList()
    },
    data() {
      // 别名校验方法
      var checkinput = (rule, value, callback) => {
        if (!value) {
          return callback()
        }
        if (value) {
          setTimeout(() => {
            var reg = /^[\u4E00-\u9FA5A-Za-z0-9_]+$/;
            if (!reg.test(value)) {
              callback(new Error('只能输入英文、数字、下划线!'))
            } else {
              callback()
            }
          }, 100)
        }
      }
      return {
        //数值未修改 保存按钮禁用
        visible: false,//右侧弹窗是否弹出
        queryform: {},
        form: {
          name: '',
          region: '',
          date1: '',
          date2: '',
          delivery: false,
          type: [],
          resource: '',
          desc: ''
        },
        props: {
          label: 'name',
          children: 'zones'
        },
        count: 1
      }
    },
    methods: {
      handleCheckChange(data, checked, indeterminate) {
        // console.log(data, checked, indeterminate);
      },
      handleNodeClick(data) {
        // console.log(data);
      },
      loadNode(node, resolve) {
        if (node.level === 0) {
          return resolve([{ name: 'region1' }, { name: 'region2' }]);
        }
        if (node.level > 3) return resolve([]);

        var hasChild;
        if (node.data.name === 'region1') {
          hasChild = true;
        } else if (node.data.name === 'region2') {
          hasChild = false;
        } else {
          hasChild = Math.random() > 0.5;
        }

        setTimeout(() => {
          var data;
          if (hasChild) {
            data = [{
              name: 'zone' + this.count++
            }, {
              name: 'zone' + this.count++
            }];
          } else {
            data = [];
          }

          resolve(data);
        }, 500);
      }
    }
  }
</script>
<style lang="scss" scoped>
  .white-body-view {
    width: 100%;
    min-width: 320px;
  }
</style>
