<template>
  <div class="white-body-view">
    <el-tree id="my-tree"

             ref="Tree"
             class="tree-view structure-tree scroll-bar flow-tree"
             :lazy="lazy"
             :load="loadNode"
             highlight-current
             :default-expand-all="treeExpandAll"
             :props="defaultProps"
             check-strictly
             :default-expanded-keys="defaultExpandedKeys"
             :node-key="treeNodeKey"
             @node-click="handleNodeClick"
             :auto-expand-parent="false"
             :expand-on-click-node="false">
      <!--@node-collapse="destroyPubSub"-->
      <span slot-scope="{ node, data }"
            class="custom-tree-node">
        <span class="tooltip">
          <img class="nodeImage" v-if="data.deviceTreeStatus == 1" :src="icon.on">
          <img class="nodeImage" v-if="data.deviceTreeStatus == 0" :src="icon.off">
          <span style="padding-left: 10px" class="add-f-s-14">{{ data.name }}</span>

        </span>


        <div v-if="node.isCurrent === true&&itemShow===true"
             class="operation-view">

          <el-button v-for="(item, index) in button"
                     :key="index"
                     type="primary"
                     :title="item.name"
                     plain
                     round
                     icon="el-icon-plus"
                     size="mini"
                     style="padding: 4px 15px;"
                     @click.stop="add(item,data)">{{item.name}}</el-button>
        </div>
      </span>
    </el-tree>
  </div>
</template>

<script>
  import { mapState } from 'vuex'
  import { deviceTreeSettings } from '../../../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings'

  export default {
    props: {
      // 列表数据
      treeData: {
        type: Array,
        default: function() {
          return []
        }
      },
      // 树节点是否默认展开
      treeExpandAll: {
        type: Boolean,
        default: false
      },
      //默认展开的级数数组
      defaultExpandedKeys: {
        type: Array,
        default: []
      },

      // 树节点唯一标识
      treeNodeKey: {
        type: String,
        default: ''
      },

      // 子节点展示新增删除编辑图标
      itemShow: {
        type: Boolean,
        default: true
      },

      //是否懒加载
      lazy: {
        type: Boolean,
        default: false
      },

      button: {
        type: Array,
        default: function() {
          return []
        }
      }
    },

    data() {
      return {
        pointArr: [deviceTreeSettings.AI, deviceTreeSettings.AO, deviceTreeSettings.DI, deviceTreeSettings.DO, deviceTreeSettings.model],//虚点+模块点
        deviceTreeSettings: deviceTreeSettings,//设备树node设置js
        icon: {
          on: require('@/api/basicData/deviceManagement/deviceTree/on.png'),
          off: require('@/api/basicData/deviceManagement/deviceTree/off.png')
        },
        defaultProps: {
          id: 'Id',
          children: 'children',
          label: 'name',
          isLeaf: 'leaf',
          deviceNodeId: 'deviceNodeId'
        },
        selectItem: {},
        buttons: []
      }
    },
    computed: {
      ...mapState({
        deviceTreeStatus_websocket: state => state.websocket.deviceTreeStatus
      })
    },

    watch: {
      treeExpandAll(e) {
        this.treeExpandAll = e
      },
      getButtonList() {
        this.buttons = this.pets
      },

      //设备树在线离线状态实时显示
      deviceTreeStatus_websocket(data) {
        if (!Array.isArray(data)) {
          return;
        }

        let click
        let nodeId
        data.forEach(item => {
          click = this.$refs.Tree.getNode(item.deviceTreeId)
          if (click != null) {
            click.data.deviceTreeStatus = item.deviceTreeStatus
            nodeId = item.deviceNodeId.toString()
            //模块错误回调实时改变右侧表单状态
            if(this.pointArr.indexOf(nodeId) > -1){
              this.$emit('changeState', item)
            }
          }
        })
      }
    },
    mounted() {
    },
    methods: {
      // 添加新增按钮
      handleAdd(data) {
        this.$emit('addItem', data)
      },
      add(data, node) {
        this.$emit('addItem', data, node)
      },
      //懒加载方法
      loadNode(data, resolve) {
        this.$emit('loadNode', data, resolve)
      },
      // 点击删除按钮
      handleDelete(data) {
        this.$emit('deleteItem', data)
      },

      // 点击编辑按钮
      handleEdit(data) {
        this.selectItem = data
        this.$emit('editItem', JSON.parse(JSON.stringify(data)))
      },

      // ============== 组件内事件 结束=============

      // ============== -----------------------------------父组件回调事件 开始=============

      // 添加新记录，树形列表回显
      treeAddItem(data) {
        this.$refs.tree.append(data, data.parentUid)
      },
      //节点被关闭时触发的事件
      destroyPubSub(){
        this.$emit('destroyPubSub')
      },
      // 选中事件
      handleNodeClick(data) {

        this.$emit('handleNodeClick', data)
      },

      // 删除节点
      treeDeleteItem(val) {
        this.$refs.tree.remove(val)
      },

      // 修改记录，树形列表回显
      treeEditItem(val) {
        Object.assign(this.selectItem, val)
        this.selectItem = {}
      }

      // ============== 父组件回调事件 结束=============

    }
  }
</script>
<style lang="scss" scoped>
  .white-body-view {
    width: 100%;
    min-width: 320px;
  }

  .custom-tree-node {
    .nodeImage {
      width: 15px;
      vertical-align: middle;
    }
  }

  .structure-tree {
    .el-scrollbar .el-scrollbar__wrap {
      overflow-x: hidden;
    }

    #my-tree .el-tree > .el-tree-node {
      min-width: 100%;
      display: inline-block;
    }

    .el-tree-node__content {
      margin-bottom: 10px;
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

    .small-operation-btn {
      margin: 0px 3px;
    }
  }

  .el-icon-plus:hover {
    color: #1c92e0;
  }

  .el-icon-edit:hover {
    color: #1c92e0;
  }

  .el-icon-delete:hover {
    color: #1c92e0;
  }

  ::v-deep .el-tree {
    color: #333333;
  }
  .flow-tree{
    overflow: auto;
  }
  ::v-deep .el-tree-node > .el-tree-node__children {
    overflow: visible;
  }
</style>
