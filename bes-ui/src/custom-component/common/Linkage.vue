<template>
  <el-collapse-item title="组件联动" name="linkage" class="linkage-container">
    <el-form>
      <div v-for="(item, index) in linkage.data" :key="index" class="linkage-component">
        <div class="div-guanbi" v-if="index != 0" @click="deleteLinkageData(index)">
          <span class="iconfont icon-icon_guanbi"></span>
        </div>
        <!--<el-select v-model="item.id" placeholder="请选择联动组件" class="testtest">
          <el-option
            v-for="(component, i) in componentData"
            :key="component.id"
            :value="component.id"
            :label="component.label"
          >
            <div @mouseenter="onEnter(i)" @mouseout="onOut(i)">{{ component.label }}</div>
          </el-option>
        </el-select>
        <el-select v-model="item.event" placeholder="请选择监听事件">
          <el-option
            v-for="e in eventOptions"
            :key="e.value"
            :value="e.value"
            :label="e.label"
          ></el-option>
        </el-select>
        <p>事件触发时，当前组件要修改的属性</p>
        <div v-for="(e, i) in item.style" :key="i" class="attr-container">
          <el-select v-model="e.key" @change="e.value = ''">
            <el-option
              v-for="attr in Object.keys(curComponent.style)"
              :key="attr"
              :value="attr"
              :label="styleMap[attr]"
            ></el-option>
          </el-select>
          <el-color-picker v-if="isIncludesColor(e.key)" v-model="e.value" show-alpha></el-color-picker>
          <el-select v-else-if="selectKey.includes(e.key)" v-model="e.value">
            <el-option
              v-for="option in optionMap[e.key]"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
          <el-input
            v-else
            v-model.number="e.value"
            type="number"
            placeholder="请输入"
          />
          <span class="iconfont icon-guanbi" @click="deleteData(item.style, i)"></span>
        </div>
        <el-button @click="addAttr(item.style)">添加属性</el-button>-->
        <p>功能配置</p>
        <div v-for="(e, i) in item.point" :key="i" class="attr-container">
          <el-form ref="form" label-width="40px">
            <el-form-item label="功能">
              <el-input
                ref="pointId"
                v-model="e.nickName"
                clearable
                size="small"
                type="string"
                placeholder="请输入"
                @focus="configPoint(e)"
              />
            </el-form-item>

            <el-form-item v-if="booPointValue == true && e.pointType == '0'" label="值">
              <el-input
                v-model.number="e.value"
                type="number"
                placeholder="请输入"
              />
            </el-form-item>

            <el-form-item v-if="component == 'VText'" label="单位">
              <el-input
                v-model="e.unit"
                type="string"
                placeholder="默认空"
              />
            </el-form-item>
            <!--            <el-button v-if="onlyOneRow == false"  type="text" @click="addAttrPoint(item.point)">关联点位</el-button>-->
            <!--            <span class="iconfont icon-guanbi" v-if="component == true" @click="deleteData(item.point, i)"></span>-->
          </el-form>
        </div>

        <!--        <el-button v-if="onlyOneRow == false" @click="addAttrPoint(item.point)">关联点位</el-button>-->
      </div>
      <!--      <el-button style="margin-bottom: 10px;" v-if="component == true"  @click="addComponent">添加组件</el-button>-->
      <!--<p>过渡时间（秒）</p>
      <el-input v-model="linkage.duration" class="input-duration" placeholder="请输入"></el-input>-->
    </el-form>

    <el-drawer
      title="配置"
      :visible.sync="drawer"
      :direction="direction"
      :before-close="drawerBeforeClose"
      @opened="opened()"
    >
      <el-tabs v-model="activeName">
        <el-tab-pane label="bes设备树" name="first">
          <el-card>
            <div style="max-height: 81.5vh;overflow-y: auto;">
              <el-tree
                class="filter-tree"
                show-checkbox
                :default-expand-all="false"
                :data="deviceTree"
                :props="deviceTreeProps"
                :expand-on-click-node="false"
                node-key="deviceTreeId"
                :check-strictly="true"
                @check="choosePoint"
                :default-expanded-keys="besDefaultExpandedNode"
                ref="tree"

              />
            </div>
          </el-card>
        </el-tab-pane>
        <el-tab-pane label="第三方协议" name="second">
          <el-card>
            <div style="max-height: 81.5vh;overflow-y: auto;">
              <el-tree
                class="filter-tree"
                show-checkbox
                :default-expand-all="false"
                :data="otherDeviceTree"
                :props="otherDeviceTreeProps"
                :expand-on-click-node="false"
                :check-strictly="true"
                @check="choosePoint"
                node-key="doubleId"
                :default-expanded-keys="otherDefaultExpandedNode"
                ref="otherTree"
              />
              <!--@node-expand="linkExpandNode"-->
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>

    </el-drawer>
  </el-collapse-item>
</template>

<script>
  import {pointType} from '@/store/modules/pointType'
  import {styleMap, optionMap, selectKey} from '@/utils/designer/attr'

  let id = 0;
  export default {
    computed: {
      linkage() {
        return this.$store.state.curComponent.linkage
      },
      onlyOneRow() {//是否只添加一个点位
        return this.$store.state.curComponent.onlyOneRow
      },
      booPointValue() {//是否配置点位值
        return this.$store.state.curComponent.booPointValue
      },
      // component() {//组件名称
      //   let component = this.$store.state.curComponent.component;
      //   if (component == 'VText') {
      //     return false
      //   } else {
      //     return true
      //   }
      // },
      component() {//组件名称
        return this.$store.state.curComponent.component;

      },
      componentData() {
        return this.$store.state.componentData
      },
      curComponent() {
        return this.$store.state.curComponent
      },

      deviceTree() {//设备树
        let aa = this.$store.state.deviceTreeData

        this.deviceTreeList = [];
        this.deviceTreeList.push(aa);

        return this.deviceTreeList
      },
      otherDeviceTree() {
        let bb = this.$store.state.otherDeviceTreeData
        this.otherDeviceTreeList = [];
        this.otherDeviceTreeList.push(bb);

        // //遍历设备树
        // if(this.$store.state.isLight){
        //   this.otherDeviceTreeList = this.forEachChildren(this.otherDeviceTreeList);
        // }

        return this.otherDeviceTreeList
      },
    },
    data() {
      return {
        optionMap,
        selectKey,
        styleMap,
        eventOptions: [
          {label: '点击', value: 'v-click'},
          {label: '悬浮', value: 'v-hover'},
        ],
        oldOpacity: '',
        oldBackgroundColor: '',
        drawer: false,
        direction: 'rtl',

        deviceTreeList: undefined,
        otherDeviceTreeList: undefined,
        deviceTreeProps: {
          children: "children",
          label: "sysName"
        },
        otherDeviceTreeProps: {
          children: "children",
          label: "name"
        },
        besPointList: [],
        otherPointList: [],
        besDefaultExpandedNode: [],
        otherDefaultExpandedNode: [],

        activeName: 'first',

        clickInputPoint: null,

      }
    },
    methods: {
      // //树节点展开事件
      // linkExpandNode(data, node, e) {
      //   if (data.children != null && data.children != undefined && data.children.length > 0) {
      //     data.children.forEach(iteam => {
      //       //只有读写类型且只有一个寄存器的功能可以选择
      //       if (iteam.readType == '1' && iteam.haveParams == '0') {
      //         this.$set(iteam, 'disabled', false);
      //       } else {
      //         //禁用复选框
      //         this.$set(iteam, 'disabled', true);
      //       }
      //     });
      //   }
      // },
      //树节点选择事件
      choosePoint(data, e) {
        const that = this;
        if (this.onlyOneRow) {
          //获取当前被选中的节点
          let checkPoint = null;
          switch (this.activeName) {
            case "first":
              checkPoint = this.$refs.otherTree.getCheckedNodes();
              if (checkPoint.length > 0) {
                this.$modal.msgWarning("第三方协议已添加");
                this.$refs.tree.setCheckedKeys([]);
                return;
              }
              break;
            case "second":
              checkPoint = this.$refs.tree.getCheckedNodes();
              if (checkPoint.length > 0) {
                this.$modal.msgWarning("bes设备树已添加");
                this.$refs.otherTree.setCheckedKeys([]);
                return;
              }
              break;
          }
          if (e.checkedKeys.length > 1) {
            this.$modal.msgWarning("只能选择一个点位");
            if (this.activeName == "first") {
              for (let i = 0; i < e.checkedKeys.length; i++) {
                if (e.checkedKeys[i] != data.deviceTreeId) {
                  this.$refs.tree.setCheckedKeys([e.checkedKeys[i]]);
                  this.$refs.otherTree.setCheckedKeys([]);
                  return;
                }
              }
            } else {
              for (let i = 0; i < e.checkedKeys.length; i++) {
                if (e.checkedKeys[i] != data.doubleId) {
                  this.$refs.otherTree.setCheckedKeys([e.checkedKeys[i]]);
                  this.$refs.tree.setCheckedKeys([]);
                  return;
                }
              }
            }
          }
        }
        if (e.checkedKeys.length == 0) {
          let arr = []
          if (this.activeName === "first") {
            //bes树为空 删除所有BES点位
            this.linkage.data[0].point.forEach(iteam => {
              if (iteam.pointType == pointType.OTHER_POINT_TYPE) {
                arr.push(iteam)
              }
            })
          } else if (this.activeName === "second") {
            //第三方树为空 删除所有第三方点位
            this.linkage.data[0].point.forEach(iteam => {
              if (iteam.pointType == pointType.BES_POINT_TYPE) {
                arr.push(iteam)
              }
            })
          }
          that.linkage.data[0].point = arr
          if (arr.length == 0) {
            that.linkage.data[0].point.push({
              pointType: '',
              pId: '',
              id: '',
              sysName: '',
              nickName: '',
              value: '',
              unit: ''
            })
          }
          return;
        }
        if (typeof data.deviceTreeId == 'undefined') {//第三方协议
          if (e.checkedKeys.includes(data.doubleId)) {//判断点击的新增还是添加 如果有的话,则是选中
            if (this.$refs.tree.getCheckedNodes().length > 0) {
              that.linkage.data[0].point.push({
                pointType: pointType.OTHER_POINT_TYPE,
                pId: data.pId,
                id: data.doubleId,
                sysName: data.functionId,
                nickName: data.functionName,
                value: '',
                unit: ''
              })
            } else {
              if (e.checkedKeys.length == 1) {
                this.linkage.data[0].point[0].pointType = pointType.OTHER_POINT_TYPE;
                this.linkage.data[0].point[0].pId = data.pId
                this.linkage.data[0].point[0].id = data.doubleId
                this.linkage.data[0].point[0].sysName = data.functionId
                this.linkage.data[0].point[0].nickName = data.functionName
              } else {
                that.linkage.data[0].point.push({
                  pointType: pointType.OTHER_POINT_TYPE,
                  pId: data.pId,
                  id: data.doubleId,
                  sysName: data.functionId,
                  nickName: data.functionName,
                  value: '',
                  unit: ''
                })
              }
            }

          } else {//取消选中
            that.linkage.data[0].point.some((item, i) => {
              if (item.id === data.doubleId) {
                that.linkage.data[0].point.splice(i, 1)
              }
            })
          }
        } else {//bes设备树
          if (e.checkedKeys.includes(data.deviceTreeId)) {//判断点击的新增还是添加 如果有的话,则是选中
            if (this.$refs.otherTree.getCheckedNodes().length > 0) {
              that.linkage.data[0].point.push({
                pointType: pointType.BES_POINT_TYPE,
                id: data.deviceTreeId,
                sysName: data.redisSysName,
                nickName: data.sysName,
                value: '',
                unit: ''
              })
            } else {
              if (e.checkedKeys.length == 1) {
                this.linkage.data[0].point[0].pointType = pointType.BES_POINT_TYPE;
                this.linkage.data[0].point[0].id = data.deviceTreeId
                this.linkage.data[0].point[0].sysName = data.redisSysName
                this.linkage.data[0].point[0].nickName = data.sysName
              } else {
                that.linkage.data[0].point.push({
                  pointType: pointType.BES_POINT_TYPE,
                  id: data.deviceTreeId,
                  sysName: data.redisSysName,
                  nickName: data.sysName,
                  value: '',
                  unit: ''
                })
              }
            }

          } else {//取消选中
            that.linkage.data[0].point.some((item, i) => {
              if (item.id === data.deviceTreeId) {
                that.linkage.data[0].point.splice(i, 1)
              }
            })
          }
        }
        if (that.linkage.data[0].point.length == 0) {
          that.linkage.data[0].point.push({
            pointType: '',
            pId: '',
            id: '',
            sysName: '',
            nickName: '',
            value: '',
            unit: ''
          })
        }
      },

      onEnter(index) {
        this.oldOpacity = this.componentData[index].style.opacity
        this.oldBackgroundColor = this.componentData[index].style.backgroundColor
        this.componentData[index].style.opacity = '.3'
        this.componentData[index].style.backgroundColor = '#409EFF'
      },

      onOut(index) {
        this.componentData[index].style.opacity = this.oldOpacity
        this.componentData[index].style.backgroundColor = this.oldBackgroundColor
      },

      isIncludesColor(str) {
        return str.toLowerCase().includes('color')
      },

      //添加属性
      addAttr(style) {
        style.push({key: '', value: ''})
      },
      //关联点位
      addAttrPoint(point) {
        point.push({id: '', value: '', unit: ''})
      },

      //配置点位
      configPoint(e) {
        this.clickInputPoint = e;

        if (!this.drawer) {
          this.$refs.pointId.forEach(value => {
            value.blur();
          })
          this.drawer = true;
        }
      },

      opened() {
        let that = this;
        //首先展开选中的节点
        //显示已选中的节点
        this.linkage.data[0].point.forEach(value => {
          if (value.pointType == pointType.BES_POINT_TYPE) {//bes设备树点位
            that.besDefaultExpandedNode.push(value.id);
            that.besPointList.push(value.id)
          } else {
            that.otherDefaultExpandedNode.push(value.id);
            that.otherPointList.push(value.id)
          }
        })
        this.$refs.tree.setCheckedKeys(that.besPointList)
        this.$refs.otherTree.setCheckedKeys(that.otherPointList)
        if (that.clickInputPoint.pointType == pointType.BES_POINT_TYPE) {
          that.activeName = "first"
        } else {
          that.activeName = "second"
        }
      },

      drawerBeforeClose(done) {
        this.besDefaultExpandedNode = [];
        this.otherDefaultExpandedNode = [];
        this.besPointList = [];
        this.otherPointList = [];
        this.$refs.tree.setCheckedKeys(this.besPointList)
        this.$refs.tree.setCheckedKeys(this.otherPointList)
        done()
      },

      //添加组件
      addComponent() {
        this.linkage.data.push({
          id: '',
          event: '',
          style: [{key: '', value: ''}],
          point: [{pointType: '', pId: '', id: '', sysName: '', nickName: '', value: '', unit: ''}]
        })
      },

      deleteData(style, index) {
        style.splice(index, 1)
      },

      deleteLinkageData(index) {
        this.linkage.data.splice(index, 1)
      },
    }
    ,
  }
</script>

<style lang="scss" scoped>
  .linkage-container {
    .linkage-component {
      margin: 5px 0;
      border: 1px solid #ddd;
      padding: 10px;
      position: relative;
      padding-top: 24px;

      .div-guanbi {
        cursor: pointer;
        position: absolute;
        right: 10px;
        top: 3px;
        color: #888;
        border: 1px solid #ddd;
        border-radius: 50%;
        width: 18px;
        height: 18px;
        display: flex;
        align-items: center;
        justify-content: center;

        .iconfont {
          font-size: 12px;
        }
      }
    }

    .el-select {
      margin-bottom: 10px;
    }

    .attr-container {
      /*display: flex;*/
      align-items: center;
      justify-content: space-between;
      margin: 10px 0;

      .el-select {
        margin-bottom: 0;
      }

      & > div {
        width: 97px;
      }

      .icon-guanbi {
        cursor: pointer;
      }
    }
  }
</style>
