<template>
  <div>
    <button style="cursor: pointer;" :style="styles"
            class="v-button">{{ propValue }}
    </button>

    <!-- 添加或修改设备对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-row v-for="(item, index) in pointList" :key="index" style="margin-left: 0.5vw">
        <el-col :span="24">

          <el-form :inline="true" :model="item" class="demo-ruleForm" ref="formInline">
            <!--BES-->
            <div v-if="item.pointType == '0'">
              <el-form-item label="提示">
                <el-input v-model="item.nickName" :disabled="true" style="width: 180px"></el-input>
              </el-form-item>
              <el-form-item label="数值">
                <el-input v-model="item.value" :disabled="true" style="width: 178px"></el-input>
              </el-form-item>
              <el-button type="primary" @click="echoTreeInfo(item)">详情</el-button>
            </div>
            <!--第三方-->
            <div v-if="item.pointType == '1'">
              <el-form-item label="功能">
                <el-input v-model="item.nickName" :disabled="true" style="width: 300px"></el-input>
              </el-form-item>
              <el-button type="primary" @click="openSettingDialog(item.id)">配置参数</el-button>
              <el-button type="primary" @click="echoTreeInfo(item)">详情</el-button>
            </div>
          </el-form>
        </el-col>
      </el-row>
      <el-form :inline="true" label-width="80px" style="text-align: center">
        <el-form-item>
          <el-button type="primary" @click="submitItem">下发</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="disItem">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 功能参数配置 -->
    <el-dialog :title="titleSet" :visible.sync="openSet" width="600px" append-to-body>
      <!-- 循环遍历参数列表 -->
      <el-row v-for="(item, index) in paramsSetList" :key="index" style="margin-left: 0.5vw">
        <el-col :span="24" v-if="item.msg == null || item.msg == undefined">
          <el-form :inline="true" :model="item" class="demo-ruleForm" ref="formInline">
            <el-form-item label="参数">
              <el-input v-model="item.name" :disabled="true" style="width: 200px"></el-input>
            </el-form-item>
            <!-- 有枚举参数则下拉框 -->
            <el-form-item label="数值"
                          v-if="item.enumDetail != null && item.enumDetail != undefined && item.enumDetail.length > 0">
              <el-select v-model="item.sendValue" placeholder="请选枚举参数" style="width: 200px">
                <el-option
                  v-for="(valueItem,indexItem) in item.enumDetail"
                  :key="'dataItem'+indexItem+valueItem.id"
                  :label="valueItem.dataValue+':'+valueItem.info"
                  :value="valueItem.dataValue">
                </el-option>
              </el-select>
            </el-form-item>
            <!-- 无枚举参数则输入框 -->
            <el-form-item label="数值" v-else>
              <el-input v-model="item.sendValue" style="width: 200px"></el-input>
            </el-form-item>
          </el-form>
        </el-col>

        <el-col :span="24" v-else>
          <el-form :inline="true" :model="item" class="demo-ruleForm" ref="formInline">
            <el-form-item label="指令">
              <el-input v-model="item.msg" style="width: 500px"
                        :disabled="item.msg.indexOf('YYMMDDHHmmSS') > -1"></el-input>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <el-form :inline="true" label-width="80px" style="text-align: center">
        <el-form-item>
          <el-button type="primary" @click="submitSet">确定</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="disSet">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>


    <el-drawer title="bes设备树" append-to-body :visible.sync="treeOpenBes" :direction="treeDirection">
      <div style="margin-left: 15px;margin-right: 15px;">
        <el-tree
          class="filter-tree"
          :default-expand-all="false"
          :data="deviceTree"
          :props="deviceTreeProps"
          :highlight-current="true"
          node-key="deviceTreeId"
          :default-expanded-keys="defaultExpandedNode"
          :current-node-key="defaultCurrentNodeKey"
          ref="tree"
        />
      </div>
    </el-drawer>

    <el-drawer title="第三方协议" append-to-body :visible.sync="treeOpen" :direction="treeDirection"
               :before-close="drawerBeforeClose">
      <div style="max-height: 81.5vh;overflow-y: auto;">
        <el-tree
          class="filter-tree"
          :default-expand-all="false"
          :data="otherDeviceTree"
          :props="otherDeviceTreeProps"
          :expand-on-click-node="false"
          :check-strictly="true"
          node-key="doubleId"
          :default-expanded-keys="otherDefaultExpandedNode"
          ref="otherTree"
          highlight-current
        />
      </div>
    </el-drawer>

  </div>

</template>

<script>
  // import OnEvent from '../common/OnEvent'
  import {
    debugPointListInfo,
  } from '@/api/basicData/deviceManagement/deviceTree/deviceTreePoint'
  import {mapState} from "vuex";
  import eventBus from "@/utils/designer/eventBus";
  import screenfull from "screenfull";
  import {selectItemDataList, queryItemDataInfoByFunctionId} from "@/api/basicData/deviceDefinition/product/product";
  import {pointType} from "@/store/modules/pointType";

  export default {
    // extends: OnEvent,
    props: {
      propValue: {
        type: String,
        required: true,
        default: '',
      },
      linkage: {
        type: Object,
        default: () => {
        },
      },
      element: {
        type: Object,
        default: () => {
        },
      },
      styles: {
        type: Object,
        default: () => {
        },
      }
    },
    computed: {
      ...mapState([
        'editMode',
        'curComponent',
        'isUnsubscribe'
      ]),
      deviceTree() {//设备树
        let aa = this.$store.state.deviceTreeData
        this.deviceTreeList = [];
        this.deviceTreeList.push(aa);
        return this.deviceTreeList
      },
      defaultCurrentNodeKey() {
        let array = []
        if (this.pointList.length > 0 && this.pointList[0].id != null && this.pointList[0].id != undefined && this.pointList[0].id != '') {
          return this.pointList[0].id
        } else {
          return 0
        }
      },
      otherDeviceTree() {
        let bb = this.$store.state.otherDeviceTreeData
        this.otherDeviceTreeList = [];
        this.otherDeviceTreeList.push(bb);
        return this.otherDeviceTreeList
      },
    },
    data() {
      return {
        //tab显示
        activeName: 'first',
        // 是否显示弹出层
        open: false,
        // 是否显示功能参数配置弹窗
        openSet: false,
        //设备树回显
        treeOpen: false,
        treeOpenBes: false,
        //树Props
        deviceTreeProps: {
          children: "children",
          label: "sysName"
        },
        //树Props
        otherDeviceTreeProps: {
          children: "children",
          label: "name"
        },
        direction: 'rtl',
        treeDirection: 'rtl',
        otherDefaultExpandedNode: [],
        otherPointList: [],
        defaultExpandedNode:[],
        // 弹出层标题
        title: "",
        // 功能参数配置弹窗标题
        titleSet: "",
        pointList: [],//点位list
        paramsSetList: [],//参数配置list
        indexParamsSetList: [],//所有功能参数配置list
        value: '',
      }
    },
    created() {

      setTimeout(() => {
        if (this.editMode != 'edit') {

          //清空所有功能参数
          this.indexParamsSetList = []
          let linkagePoint = this.element.linkage.data[0].point;
          if (linkagePoint.length > 0) {
            linkagePoint.forEach((value, index) => {
              let param = {};
              param.equipmentId = value.pId;
              param.pointType = value.pointType;
              param.nickName = value.nickName;
              param.value = value.value;
              param.valueList = [];
              param.sysName = value.sysName;
              param.workMode = 0;
              if (value.pointType == pointType.BES_POINT_TYPE) {
                param.id = value.id;
              } else {
                param.id = value.sysName;

              }

              //添加功能
              this.pointList.push(param)

              //排序
              function objectSort(property) {
                return function (Obj1, Obj2) {
                  return Obj1[property] - Obj2[property]
                }
              }

              this.pointList.sort(objectSort("id"));

            })
            //排序后查询参数信息
            this.pointList.forEach((iteam, index) => {
              if (iteam.id != '' && iteam.pointType == pointType.OTHER_POINT_TYPE) {
                //查询数据项及相应参数配置
                const params = {
                  id: iteam.sysName,
                  equipmentId: iteam.equipmentId
                }
                selectItemDataList(params).then(response => {
                  if (response.msg.indexOf('*') > -1) {
                    let add = {
                      index: index,
                      id: iteam.id,
                      msg: response.msg,
                      data: response.data
                    }
                    //添加功能参数信息
                    this.indexParamsSetList.push(add)
                  } else {
                    let add = {
                      id: iteam.id,
                      index: index,
                      data: response.data
                    }
                    //添加功能参数信息
                    this.indexParamsSetList.push(add)
                  }
                })
              }
            })
          }
        }
      },500)



      if (this.linkage?.data?.length) {
        eventBus.$on('v-click', this.onClick)
        eventBus.$on('v-hover', this.onHover)
      }
    },
    methods: {
      drawerBeforeClose(done) {
        this.otherDefaultExpandedNode = [];
        this.otherPointList = [];
        this.$refs.otherTree.setCheckedKeys(this.otherPointList)
        done()
      },
      //详情 弹出设备树回显点位
      echoTreeInfo(item) {
        if (item.pointType == pointType.OTHER_POINT_TYPE) {
          //第三方
          let id = item.equipmentId + (item.id / 100)
          this.otherDefaultExpandedNode.push(id);
          this.otherPointList.push(id)
          this.treeOpen = true
          this.$nextTick(() => {
            //给每个id对应的check选中
            this.$refs.otherTree.setCurrentKey(id)
          })
        } else {
          //bes
          this.treeOpenBes = true
          this.defaultExpandedNode.push(item.id)
          this.$nextTick(() => {
            //给每个id对应的check选中
            this.$refs.tree.setCurrentKey(item.id)
          })
        }

      },
      //打开功能参数配置弹窗
      openSettingDialog(id) {
        this.paramsSetList = []
        this.pointList.forEach(value => {
          if (value.id == id) {
            //查询数据项及相应参数配置
            this.indexParamsSetList.forEach(item => {
              if (item.id == id) {
                if (item.msg != null && item.msg != undefined && item.msg.indexOf('*') > -1) {
                  //替换功能参数信息
                  this.titleSet = '功能指令配置'
                  this.paramsSetList.push(item)
                  this.openSet = true
                } else {
                  //修改功能参数信息
                  this.titleSet = '功能参数配置'
                  item.data.forEach(iteam => {
                    this.paramsSetList.push(iteam)
                  })
                  this.openSet = true
                }
              }
            })
          }
        })
      },
      //提交功能参数配置弹窗
      submitSet() {
        let checkBoolean = false
        this.paramsSetList.forEach(iteam => {
          if (iteam.msg != null && iteam.msg != undefined) {
            if (iteam.msg == '' || iteam.msg.length == 0) {
              this.$modal.msgError("请配置指令");
              checkBoolean = true
            }
          } else {
            if (iteam.sendValue == null || iteam.sendValue == undefined || iteam.sendValue.length == 0) {
              this.$modal.msgError("请配置参数数值");
              checkBoolean = true
            }
          }
        })
        this.openSet = checkBoolean;
      },
      //关闭功能参数配置弹窗
      disSet() {
        //关闭弹窗
        this.paramsSetList = []
        this.openSet = false;
      },
      onClick(componentId) {
        const data = this.linkage.data.filter(item => item.id === componentId && item.event === 'v-click')
        this.changeStyle(data)
        //传递父组件,打开相应弹窗
        this.openInout(componentId, this.element)
      },

      onHover(componentId) {
        const data = this.linkage.data.filter(item => item.id === componentId && item.event === 'v-hover')
        this.changeStyle(data)
      },

      changeStyle(data = []) {
        data.forEach(item => {
          item.style.forEach(e => {
            if (e.key) {
              this.element.style[e.key] = e.value
            }
          })
        })
      },

      openInout(componentId, data) {
        if (data.id == componentId && data.component == "VButton") {
          if (!screenfull.isFullscreen) {
            this.title = '下发'
            this.open = true
            this.pointList.forEach(value => {
              if (value.pointType == pointType.OTHER_POINT_TYPE) {
                //第三方
                // this.pointList.forEach(value => {
                //查询数据项及相应参数配置
                const params = {
                  id: value.id,
                  equipmentId: value.equipmentId
                }
                selectItemDataList(params).then(response => {
                  this.indexParamsSetList.forEach(item => {
                    if (item.id == value.id) {
                      if (response.msg != null && response.msg != undefined && response.msg.indexOf('*') > -1) {
                        //替换功能参数信息
                        item.data = response.data
                        item.msg = response.msg
                        item.id = value.id
                      } else {
                        //修改功能参数信息
                        item.data = response.data
                        item.id = value.id
                      }
                    }
                    // this.open = true
                  })
                })
                // })
              }
            })
          }
        }
      },

      //下发
      submitItem() {
        const that = this;
        //组装下发值
        that.pointList.forEach((item) => {
          if(item.pointType == pointType.OTHER_POINT_TYPE){
            item.valueList = []
            let addArr = []
            that.indexParamsSetList.forEach((value, index2) => {
              if (item.id == value.id) {
                if (that.indexParamsSetList[index2].msg != null && that.indexParamsSetList[index2].msg != undefined && that.indexParamsSetList[index2].msg != '') {
                  if (that.indexParamsSetList[index2].msg.indexOf('YYMMDD') > -1) {
                    //设置时间
                    //直接跳过
                    item.valueList = []
                  } else {
                    //指令下发
                    if (that.indexParamsSetList[index2].data.length > 0) {
                      //根据指令获取值
                      let msg = null;
                      msg = that.indexParamsSetList[index2].msg
                      //获取到指令数据
                      msg = msg.substring(13);
                      let dataArr = []
                      while (msg.length >= 4) {
                        let add = null;
                        add = msg.substring(0, 4);
                        dataArr.push(add)
                        msg = msg.substring(4);
                      }
                      //赋值
                      if (that.indexParamsSetList[index2].data[0].paramsType == '1' || that.indexParamsSetList[index2].data[0].paramsType == '2') {
                        //1.高低位
                        if (that.indexParamsSetList[index2].data.length == 1) {
                          //补位操作
                          item.valueList.push({value: "0"})
                        }
                        that.indexParamsSetList[index2].data.forEach((index2, index1) => {
                          let addMap = {
                            //value: parseInt(dataArr[index1], 16),//下发值
                            //十六进制转十进制
                            value: parseInt(dataArr[index1], 16),//下发值
                          }
                          //先高位参数后低位参数
                          if (iteam.paramsType == '1') {
                            item.valueList.unshift(addMap)
                          } else {
                            item.valueList.push(addMap)
                          }
                        })

                      } else if (that.indexParamsSetList[index2].data[0].dataType == "7"){
                        //2.结构体
                        that.indexParamsSetList[index2].data.forEach((iteam, index1) => {
                          //往头部添加
                          addArr.unshift(dataArr[index1]);
                        })
                        //获取到结构体下发值
                        let sendValue = addArr.join('');
                        let addMap = {
                          //二进制转换成十进制
                          value: parseInt(sendValue, 10),//下发值
                        }
                        item.valueList.push(addMap)
                      } else {
                        dataArr.forEach((value1, index) => {
                          let addMap = {
                            //十六进制转十进制
                            value: parseInt(dataArr[index], 16),//下发值
                          }
                          item.valueList.push(addMap)
                        })


                      }
                    } else {
                      that.$modal.msgError("功能指令未关联数据项");
                    }
                  }
                } else {
                  //数据项下发
                  if (this.indexParamsSetList[index2].data[0].paramsType == '3') {
                    this.indexParamsSetList[index2].data.forEach((iteam) => {
                      let addMap = {
                        // value: parseInt(iteam.sendValue, 16),//下发值
                        value: iteam.sendValue,//下发值
                      }
                        item.valueList.push(addMap)
                    })

                  }
                  if (this.indexParamsSetList[index2].data[0].paramsType != '4' && this.indexParamsSetList[index2].data[0].paramsType != '3') {
                    //1.高低位
                    if (this.indexParamsSetList[index2].data.length == 1) {
                      //补位操作
                      item.valueList.push({value: "0"})
                    }
                    this.indexParamsSetList[index2].data.forEach((iteam) => {
                      let addMap = {
                        // value: parseInt(iteam.sendValue, 16),//下发值
                        value: iteam.sendValue,//下发值
                      }
                      //先高位参数后低位参数
                      if (iteam.paramsType == '1') {
                        item.valueList.unshift(addMap)
                      } else {
                        item.valueList.push(addMap)
                      }
                    })
                  } else {
                    //2.结构体
                    this.indexParamsSetList[index2].data.forEach((iteam, index) => {
                      //往头部添加
                      addArr.unshift(iteam.sendValue + '')
                    })
                    //获取到结构体下发值
                    let sendValue = addArr.join('');
                    let addMap = {
                      //二进制转换成十进制
                      value: parseInt(sendValue, 2),//下发值
                    }
                    item.valueList.push(addMap)
                  }
                }
              }
            })
          }
        })

        debugPointListInfo(JSON.stringify(this.pointList)).then(responent => {
          this.open = false;
          this.$modal.msgSuccess(responent.msg);
          //下发完成立即查询点位状态
          this.pointList.forEach((iteam, index) => {
            if (iteam.pointType == pointType.OTHER_POINT_TYPE) {
              let params = {
                id: iteam.sysName,
                equipmentId: iteam.equipmentId,
              }
              queryItemDataInfoByFunctionId(params)
            }
          })
        })
      },
      //取消
      disItem() {
        this.open = false
      },
    }
  }
</script>

<style lang="scss" scoped>
  .v-button {
    display: inline-block;
    line-height: 1;
    white-space: nowrap;
    cursor: pointer;
    background: #fff;
    border: 1px solid #dcdfe6;
    color: #606266;
    -webkit-appearance: none;
    text-align: center;
    box-sizing: border-box;
    outline: 0;
    margin: 0;
    transition: .1s;
    font-weight: 500;
    width: 100%;
    height: 100%;
    font-size: 14px;

    &:active {
      color: #3a8ee6;
      border-color: #3a8ee6;
      outline: 0;
    }

    &:hover {
      background-color: #ecf5ff;
      color: #3a8ee6;
    }
  }

  .organization_configuration {

    .el-tree--highlight-current
    .el-tree-node.is-current
    > .el-tree-node__content {
      // 设置颜色
      background-color: rgba(135, 206, 235, 0.2); // 透明度为0.2的skyblue，作者比较喜欢的颜色
      color: #409eff; // 节点的字体颜色
      font-weight: bold; // 字体加粗
    }
  }

</style>
