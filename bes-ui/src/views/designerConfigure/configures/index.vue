<template>
  <div class="app-container">

    <div v-if="homePageShow == 1">
      <el-row :gutter="20">
        <!-- 分组数据 -->
        <el-col :span="6" :xs="24">

          <div class="head-container">
            <el-card class="wait-task-user-box-card">
              <!--<el-input
                v-model="grounpName"
                placeholder="请输入分组名称"
                clearable
                size="small"
                prefix-icon="el-icon-search"
                style="margin-bottom: 20px"
              />-->
              <el-form ref="queryForm" size="small" label-width="40px"
                       style="border-bottom: 2px #f6f6f6 solid;">
                <el-form-item label="园区" prop="parkCode">
                  <el-select v-model="parkCode"
                             @change="parkChange(parkCode)"
                             placeholder="请选择" style="margin-bottom: 2px;width: 17vw">
                    <el-option
                      v-for="item in parkList"
                      :key="item.code"
                      :label="item.name"
                      :value="item.code">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>


              <div style="max-height: 70.4vh;overflow-y: auto;width: 100%;min-width: 160px;">
                <el-tree
                  node-key="id"
                  class="filter-tree"
                  :data="areaOptions"
                  :props="defaultProps"
                  :expand-on-click-node="false"
                  :filter-node-method="filterNode"
                  :default-expanded-keys="defaultExpandedKeys"
                  ref="areaTree"
                  @node-click="handleNodeClick"
                  highlight-current
                >
                <span slot-scope="{ node, data }">
                  <span class="tooltip">
                    <span style="padding-left: 10px">{{ data.name }}</span>
                  </span>
                <div v-hasPermi="['designerConfigures:config:addOrdelete']"
                     v-if="node.isCurrent === true"
                     class="operation-view">

                  <el-dropdown @command="areaOperation">
                    <span class="el-dropdown-link">
                      操作<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                       <el-dropdown-item
                         v-for="(item, index) in areaButton"
                         :icon="item.icon"
                         :command="item.type">{{item.name}}</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                  <!-- <el-button v-for="(item, index) in button"
                              :key="index"
                              type="primary"
                              :title="item.name"
                              plain
                              round
                              icon="el-icon-plus"
                              size="mini"
                              style="padding: 1px 10px;"
                              @click.stop="add(item,data)">{{item.name}}
                   </el-button>-->
                </div>
                </span>
                </el-tree>
              </div>
            </el-card>
          </div>
        </el-col>
        <el-col :span="18" :xs="24">
          <div style="max-height: 80.4vh;overflow-y: auto;">
            <el-col :span="6" style="padding-top: 2vh" v-for="(item, index) in pageList" :key="item.id"
            >
              <el-card :body-style="{ padding: '0px' }">
                <img v-if="item.uploadDesignerScreenshot != null" :src="item.uploadDesignerScreenshot"
                     style="height: 20vh;" class="image" @click="intoExhibitionPage(item)">
                <el-empty v-else :image-size="150" style="padding: initial;height: 20vh;"></el-empty>

                <div style="padding: 14px;text-align: center">
                  <div style="height: 1vh">
                    <span>{{item.name}}</span>
                  </div>

                  <div class="bottom clearfix">
                    <!--                  <time class="time">{{ currentDate }}</time>-->
                    <!--<el-button
                      v-if="item.canvasStyle != null"
                      type="text"
                      style="float: left"
                      class="button"
                      @click="intoExhibitionPage(item)">查看
                    </el-button>-->
                    <!--                  <el-button v-if="item.uploadDesignerScreenshot == null" type="text" class="button"-->
                    <!--                             @click="intoDesignerPage(item)">配置-->
                    <!--                  </el-button>-->

                    <el-dropdown size="mini" style="float: right" @command="(command)=>{pageOperation(command,item)}">
                      <el-button type="primary"
                                 style="width:4vw;height: 3vh;float: right;font-size: 12px;text-align: center;padding:4px 0"
                                 v-hasPermi="['designerConfigures:config:update']"
                      >
                        操作<i class="el-icon-arrow-down el-icon--right"></i>
                      </el-button>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item
                          v-for="(item, index) in pageButton"
                          :icon="item.icon"
                          :command="item.type">{{item.name}}
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                    <!--                  <el-button v-else type="text" class="button" @click="intoDesignerPage(item)">修改</el-button>-->
                  </div>
                </div>
              </el-card>
            </el-col>
          </div>
        </el-col>

        <!-- 添加或修改区域对话框 -->
        <el-dialog :title="title" :visible.sync="areaOpen" width="500px">
          <el-form ref="areaForm" :model="areaForm" :rules="areaRules" label-width="80px">
            <el-form-item label="区域名称" prop="name">
              <el-input v-model="areaForm.name" placeholder="请输入区域名称"/>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="areaForm.remark" placeholder="请输入备注"/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitAreaForm">确 定</el-button>
            <el-button @click="areaCancel">取 消</el-button>
          </div>
        </el-dialog>

        <!-- 添加或页面设备对话框 -->
        <el-dialog :title="title" :visible.sync="pageOpen" width="500px">
          <el-form ref="pageForm" :model="pageForm" :rules="pageRules" label-width="80px">
            <el-form-item label="页面名称" prop="name">
              <el-input v-model="pageForm.name" placeholder="请输入页面名称" maxlength="10" show-word-limit/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitPageForm">确 定</el-button>
            <el-button @click="pageCancel">取 消</el-button>
          </div>
        </el-dialog>

        <!-- 复制页面对话框 -->
        <el-dialog :title="CopyTitle" :visible.sync="copyPageOpen" width="500px">
          <div style="height: 50.4vh;overflow-y: auto;width: 100%;min-width: 160px;">
            <el-tree
              node-key="id"
              show-checkbox
              class="filter-tree"
              :data="areaOptions"
              :check-strictly="true"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              :default-expanded-keys="defaultExpandedKeys"
              @check="copyHandleNodeClick"
              ref="areaCopyTree"
              highlight-current
            >
            </el-tree>
          </div>
          <el-form ref="pageForm" :model="pageForm" :rules="pageRules" label-width="80px">
            <el-form-item label="页面名称" prop="name">
              <el-input v-model="pageForm.name" placeholder="请输入复制的页面名称" maxlength="10" show-word-limit/>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitCopyPageForm">确 定</el-button>
            <el-button @click="copyPageCancel">取 消</el-button>
          </div>
        </el-dialog>
      </el-row>
    </div>
    <div v-else-if="homePageShow == 2">
      <div class="home">
        <Toolbar :area-id="areaId" :page-id="pageId" :name="name" @back="goBackHomePage"/>
        <main>
          <!-- 左侧组件列表 -->
          <section class="left">
            <ComponentList/>
            <RealTimeComponentList/>
          </section>
          <!-- 中间画布 -->
          <section class="center">
            <div id="body">
              <div
                id="content"
                class="content"
                @drop="handleDrop"
                @dragover="handleDragOver"
                @mousedown="handleMouseDown"
                @mouseup="deselectCurComponent"
              >
                <Editor/>
              </div>

            </div>
          </section>
          <!-- 右侧属性列表 -->
          <section class="right">
            <el-tabs v-if="curComponent" v-model="activeName" style="max-height: 80vh">
              <el-tab-pane label="属性" name="attr">
                <component :is="curComponent.component + 'Attr'"/>
              </el-tab-pane>
              <el-tab-pane label="动画" name="animation" style="padding-top: 20px;">
                <AnimationList/>
              </el-tab-pane>
              <el-tab-pane label="事件" name="events" style="padding-top: 20px;">
                <EventList/>
              </el-tab-pane>
            </el-tabs>
            <CanvasAttr v-else></CanvasAttr>
          </section>
        </main>
      </div>
    </div>
    <div v-else-if="homePageShow == 3">
      <!-- 中间画布 -->
      <div style="position: relative">
        <section>
          <designer-view ref="designerViewPage" :area-id="areaId" :page-id="pageId" :keyValue="key" :page-name="viewsIndex"
                         @back="goBackHomePage"/>
        </section>
      </div>

    </div>

  </div>
</template>

<script>
  import {
    addDesignerArea,
    addDesignerAreaPage,
    copyDesignerScreenshot,
    deleteDesignerArea,
    deleteDesignerAreaPage,
    designerAreaListInfo,
    seleteDesignerAreaPage,
    updateDesignerArea,
    updateDesignerAreaPage
  } from "@/api/designerConfigure/designer"
  import {setDefaultcomponentData} from "@/store/designer/snapshot";
  import Treeselect from "@riophae/vue-treeselect";
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";
  import vueJsonEditor from 'vue-json-editor';

  import CircleShapeAttr from '@/custom-component/CircleShape/Attr'
  import PictureAttr from '@/custom-component/Picture/Attr'
  import VTextAttr from '@/custom-component/VText/Attr'
  import VButtonAttr from '@/custom-component/VButton/Attr'
  import GroupAttr from '@/custom-component/Group/Attr'
  import RectShapeAttr from '@/custom-component/RectShape/Attr'
  import LineShapeAttr from '@/custom-component/LineShape/Attr'
  import LineShapeSVGAttr from '@/custom-component/LineShapeSVG/Attr'
  import VTableAttr from '@/custom-component/VTable/Attr'
  import SVGTriangleAttr from '@/custom-component/svgs/SVGTriangle/Attr'
  import SVGStarAttr from '@/custom-component/svgs/SVGStar/Attr'
  import LightAttr from '@/custom-component/Light/Attr'
  import DownLightAttr from '@/custom-component/DownLight/Attr'
  import CommunicationLightAttr from '@/custom-component/CommunicationLight/Attr'
  import HallLightAttr from '@/custom-component/HallLight/Attr'
  import DraughtFanAttr from '@/custom-component/DraughtFan/Attr'

  import Editor from '@/components/designer/Editor/designerIndex'
  import ComponentList from '@/components/designer/ComponentList' // 左侧列表组件
  import AnimationList from '@/components/designer/AnimationList' // 右侧动画列表
  import EventList from '@/components/designer/EventList' // 右侧事件列表
  import componentList from '@/custom-component/component-list' // 左侧列表数据
  import Toolbar from '@/components/designer/Toolbar'
  import {deepCopy} from '@/utils/designer/utils'
  import {mapState} from 'vuex'
  import generateID from '@/utils/designer/generateID'
  import RealTimeComponentList from '@/components/designer/RealTimeComponentList'
  import CanvasAttr from '@/components/designer/CanvasAttr'
  import {changeComponentSizeWithScale} from '@/utils/designer/changeComponentsSizeWithScale'
  import pubsub from "@/store/modules/PubSub";

  import designerView from "@/views/designerConfigure/views/designerView"

  import store from "@/store";
  import {allEquipmentFunctionTree, allListTree} from '@/api/basicData/deviceManagement/deviceTree/deviceTree'
  import {deviceTreeSettings} from '@/api/basicData/deviceManagement/deviceTree/deviceTreeSettings'
  import {getAllPark} from "@/api/electricPowerTranscription/powerData/powerData";

  export default {
    name: "DesignerConfigure",
    components: {
      Treeselect,
      vueJsonEditor,
      Editor,
      ComponentList,
      AnimationList,
      EventList,
      Toolbar,
      RealTimeComponentList,
      CanvasAttr,
      CircleShapeAttr,
      PictureAttr,
      VTextAttr,
      VButtonAttr,
      GroupAttr,
      RectShapeAttr,
      LineShapeAttr,
      VTableAttr,
      SVGTriangleAttr,
      SVGStarAttr,
      LineShapeSVGAttr,
      LightAttr,
      designerView,
      DownLightAttr,
      CommunicationLightAttr,
      HallLightAttr,
      DraughtFanAttr,
    },
    data() {
      return {
        viewsIndex: 'configuresIndex',
        parkList: [],
        copyId: null,
        copytreeData: [],
        copyTreeid: null,
        parkCode: null,
        homePageShow: 1,
        activeName: 'attr',
        reSelectAnimateIndex: undefined,
        scale: 1,
        areaId: "1",//区域id
        pageId: "1",//页面id
        name: "页面",//页面名称
        form: {},
        // 分组名称
        grounpName: undefined,
        // 分组树选项
        defaultExpandedKeys: [0],//默认展开的数组
        areaOptions: undefined,
        currentDate: new Date(),
        defaultProps: {
          children: "children",
          label: "name"
        },
        treeNodeData: {},
        areaButton: [
          {type: 'addArea', icon: 'el-icon-circle-plus', name: '新增区域'},
          {type: 'updateArea', icon: 'el-icon-edit', name: '修改区域'},
          {type: 'addPage', icon: 'el-icon-circle-plus', name: '新增页面'},
          {type: 'deleteArea', icon: 'el-icon-remove', name: '删除区域'},
        ],
        pageButton: [
          {type: 'updatePage', icon: 'el-icon-edit', name: '修改名称'},
          {type: 'addPage', icon: 'el-icon-circle-plus', name: '配置页面'},
          {type: 'copyPage', icon: 'el-icon-remove', name: '复制页面'},
          {type: 'deletePage', icon: 'el-icon-remove', name: '删除页面'},

        ],

        pageList: [],//设计器页面列表

        areaOpen: false,//区域新增或修改的对话框
        pageOpen: false,//页面新增或修改的对话框
        copyPageOpen: false,
        title: '区域配置',
        CopyTitle: "复制页面",
        areaForm: {
          id: null,
          name: null,
          parkCode: null,
          remark: null,
        },
        pageForm: {
          id: null,
          areaId: null,
          name: null,
        },
        areaPageForm: {},
        // 表单校验
        areaRules: {
          name: [
            {required: true, message: "区域名称不能为空", trigger: "change"}
          ],
          parkCode: [
            {required: true, message: "园区不能为空", trigger: "change"}
          ],
        },
        pageRules: {
          name: [
            {required: true, message: "页面名称不能为空", trigger: "change"}
          ]
        },
        deleteIdList: [],//删除时id数组
      }
    },
    computed: {
      ...mapState([
        'componentData',
        "curComponent",
        'isClickComponent',
        'canvasStyleData',
        'editor',
        'editMode',
        'ToViewPage',
      ]),
      key() {
        return this.$route.path
      }
    },

    async created() {
      //查询所有园区
      this.getAllPark()
      //获取设备树
      this.getDeviceTree();
      //获取第三方设备树
      this.getOtherDeviceTree();


    },
    mounted() {
      window.addEventListener('beforeunload', (e) => this.beforeunloadHandler(e));
      window.addEventListener('unload', this.updateHandler);

    },

    methods: {
//查询所有园区
      getAllPark() {
        getAllPark().then(response => {
          this.parkList = response
          if (this.parkList.length > 0) {
            this.parkCode = this.parkList[0].code
            //获取区域树节点信息
            this.getDesignerAreaList(this.parkCode);
          } else {
            this.parkCode = ''
          }
        });
      },
      //园区下拉框改变事件
      parkChange(data) {
        this.getDesignerAreaList(data);
      },
      beforeunloadHandler(e) {

        if (this.homePageShow == 2) {
          e = e || window.event;
          if (e) {
            e.returnValue = '关闭提示';
          }
          return '关闭提示';
        } else if (this.homePageShow == 3) {

          pubsub.destructionAllDesignerSub();
          return '关闭提示';

        }

      },
      updateHandler() {
      },

      //获取区域树节点信息
      getDesignerAreaList(parkCode) {
        const that = this;
        let designerArea = {};
        designerArea.parkCode = parkCode;
        designerAreaListInfo(designerArea).then(response => {
          if (response.code == 200) {
            if (response.data.length > 0) {
              this.areaOptions = [];
              const data = {id: 0, name: '区域名称', children: []};
              data.children = this.handleTree(response.data, "id", "parentId", "remark");
              that.areaOptions.push(data);
              that.$nextTick(() => {
                that.areaId = that.areaOptions[0].children[0].id.toString();
                that.$refs.areaTree.setCurrentKey(that.areaId)

                this.treeNodeData = that.$refs.areaTree.getCurrentNode();
                let form = {};
                form.areaId = that.areaId;
                //获取当前点击节点的所有的设计器页面
                this.getDesignerAreaPage(form);
              })
            } else {
              this.areaOptions = [];
              const data = {id: 0, name: '区域名称', children: []};
              that.areaOptions.push(data);
              this.pageList = []
            }
          }
        })


      },

      //获取设备树
      getDeviceTree() {
        allListTree().then(response => {
          const data = {deviceTreeId: 0, sysName: '设备树', children: [], disabled: true};

          response.data.forEach(val => {
            if (val.deviceNodeId != deviceTreeSettings.DI &&
              val.deviceNodeId != deviceTreeSettings.DO &&
              val.deviceNodeId != deviceTreeSettings.AI &&
              val.deviceNodeId != deviceTreeSettings.AO) {
              val.disabled = true
            }
          })
          data.children = this.handleTree(response.data, "deviceTreeId", "deviceTreeFatherId");
          store.commit('setDeviceTree', data)
        })
      },

      getOtherDeviceTree() {
        allEquipmentFunctionTree().then(response => {
          const data = {id: 0, name: '设备树', children: [], disabled: true};
          if (response.code == 200) {
            response.data.forEach(val => {
              if (val.functionType == null) {
                val.disabled = true
              }
            })
            data.children = this.handleTree(response.data, "id", "pId");
            store.commit('setOtherDeviceTree', data)
          }
        })
      },

      //区域新增/修改
      submitAreaForm() {
        this.areaForm.parentId = this.treeNodeData.id;
        this.areaForm.parkCode = this.parkCode;
        this.$refs["areaForm"].validate(valid => {
          if (valid) {
            if (this.areaForm.id != null) {
              updateDesignerArea(this.areaForm).then(response => {
                if (response.code == 200) {
                  this.$refs.areaTree.getNode(this.treeNodeData.id).data.name = this.areaForm.name
                  this.$refs.areaTree.getNode(this.treeNodeData.id).data.remark = this.areaForm.remark
                  this.$refs.areaTree.getNode(this.treeNodeData.id).data.parkCode = this.areaForm.parkCode

                  this.$modal.msgSuccess(response.msg);
                  this.areaOpen = false;
                  this.areaReset();
                } else {
                  this.$modal.msgWarning(response.msg);
                }
              });
            } else {
              addDesignerArea(this.areaForm).then(response => {
                if (response.code == 200) {
                  let nodeData = {};
                  nodeData.id = response.data.id;
                  nodeData.name = response.data.name;
                  this.$refs.areaTree.append(
                    nodeData,
                    this.treeNodeData
                  )
                  this.$modal.msgSuccess(response.msg);
                  this.areaOpen = false;
                  this.areaReset();
                } else {
                  this.$modal.msgWarning(response.msg);
                }
              });
            }
          }
        });
      },
      // 页面复制提交
      submitCopyPageForm() {
        this.$refs["pageForm"].validate(valid => {


          if (this.copyTreeid == 0) {
            this.$modal.msgWarning("不能选择根节点");
            return;

          }

          let formdata = {
            areaId: this.copyTreeid,
            id: this.copyId,
            name: this.pageForm.name
          }
          copyDesignerScreenshot(formdata).then(response => {

            if (response.code == 200) {

              this.$modal.msgSuccess(response.msg);
              if (this.areaId == this.copyTreeid) {
                let form = {};
                form.areaId = this.areaId;

                //获取当前点击节点的所有的设计器页面
                this.getDesignerAreaPage(form);
              }
            } else {
              this.$modal.msgError(response.msg);

            }
            this.copyPageOpen = false;

          })

        })

      },
      //页面新增/修改
      submitPageForm() {
        const that = this;
        this.$refs["pageForm"].validate(valid => {
          if (valid) {
            if (this.pageForm.id != null) {
              updateDesignerAreaPage(this.pageForm).then(response => {
                if (response.code == 200) {
                  that.pageList.forEach(value => {
                    if (value.id == that.pageForm.id) {
                      value.name = that.pageForm.name;
                    }
                  })
                  that.pageOpen = false;
                  that.pageReset();
                  that.$modal.msgSuccess(response.msg);
                } else {
                  that.$modal.msgWarning(response.msg);
                }
              })
            } else {
              addDesignerAreaPage(this.pageForm).then(response => {
                if (response.code == 200) {
                  that.pageList.push(response.data);

                  that.pageOpen = false;
                  that.pageReset();
                  that.$modal.msgSuccess(response.msg);
                } else {
                  that.$modal.msgWarning(response.msg);
                }
              })
            }
          }
        })
      },

      // 取消按钮
      areaCancel() {
        this.areaOpen = false;
        this.areaReset();
      },
      pageCancel() {
        this.pageOpen = false;
        this.pageReset();
      },
      copyPageCancel() {
        this.copyPageOpen = false;
      },
      // 表单重置
      areaReset() {
        this.areaForm = {
          id: null,
          name: null,
          parkCode: null,
          remark: null,
        };
        this.resetForm("areaForm");
      },
      pageReset() {
        this.pageForm = {
          id: null,
          areaId: null,
          name: null,
        };
        this.resetForm("pageForm");
      },

      //区域按钮操作
      areaOperation(command) {
        const that = this;
        if (command == 'addArea') {//新增区域
          this.areaOpen = true
        } else if (command == 'updateArea') {//修改页面
          if (this.treeNodeData.id == 0) {
            this.$modal.msgWarning("当前节点不可修改");
            return
          }
          this.areaForm.id = this.treeNodeData.id;
          this.areaForm.name = this.treeNodeData.name;
          this.areaForm.parkCode = this.treeNodeData.parkCode;
          this.areaForm.remark = this.treeNodeData.remark;
          this.areaOpen = true

        } else if (command == 'deleteArea') {//删除区域
          if (this.treeNodeData.id == 0) {
            this.$modal.msgWarning("当前节点不可删除");
            return
          }
          this.deleteIdList.push(this.treeNodeData.id);
          this.getChildrenIdList(this.treeNodeData);

          deleteDesignerArea(this.deleteIdList).then(response => {
            if (response.code == 200) {
              that.deleteIdList.length = 0;
              that.$modal.msgSuccess(response.msg);
              that.$refs.areaTree.remove(
                that.treeNodeData
              )
            } else {
              that.$modal.msgWarning(response.msg);
            }
          });
        } else if (command == 'addPage') {//新增页面
          if (this.treeNodeData.id == 0) {
            that.$modal.msgWarning("当前节点禁止添加页面");
            return;
          }
          this.pageForm.areaId = this.treeNodeData.id;
          this.pageOpen = true;
        }
      },
      //页面按钮操作
      pageOperation(command, item) {


        if (command == 'updatePage') {//修改页面
          this.pageOpen = true
          this.pageForm.areaId = item.areaId;
          this.pageForm.id = item.id;
          this.pageForm.name = item.name;
        } else if (command == 'addPage') {//配置页面
          this.intoDesignerPage(item)
        } else if (command == 'deletePage') {//删除页面
          this.$confirm('此操作将永久删除该页面, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.deleteDesignerPage(item)
          }).catch(() => {

          });

        } else if (command == 'copyPage') {
          this.copyPageOpen = true;
          this.copyId = item.id;
          this.resetForm("pageForm");

          this.$refs.areaCopyTree.setCheckedKeys([]);
          this.pageForm.name = null;
        }
      },

      //删除页面
      deleteDesignerPage(item) {
        const that = this;
        this.areaId = item.areaId;
        this.pageId = item.id;
        let form = {};
        form.id = item.id;
        form.areaId = item.areaId;
        deleteDesignerAreaPage(form).then(response => {
          if (response.code == 200) {
            that.pageList.some((data, i) => {
              if (data.id === item.id) {
                that.pageList.splice(i, 1)
              }
            })
            that.$modal.msgSuccess(response.msg);
          } else {
            that.$modal.msgWarning(response.msg);
          }
        })

      },

      //获取当前节点下子节点的id数组
      getChildrenIdList(data) {

        if (typeof data.children != 'undefined') {
          data.children.forEach(val => {
            this.deleteIdList.push(val.id);
            this.getChildrenIdList(val);
          })

        }
      },

      //获取当前点击节点的所有的设计器页面
      getDesignerAreaPage(form) {
        seleteDesignerAreaPage(form).then(response => {
          if (response.code == 200) {
            if (response.data.length == 0) {
              this.pageList = [];
            } else {

              for (let i = 0; i < response.data.length; i++) {
                if (response.data[i].uploadDesignerScreenshot == null) {
                  continue;
                }
                response.data[i].uploadDesignerScreenshot = this.findStrSubtring(response.data[i].uploadDesignerScreenshot, '/', 1);
              }
              this.pageList = response.data
            }
          }

        })
      },
      findStrSubtring(str, cha, num) {
        var x = str.indexOf(cha)
        for (var i = 0; i < num; i++) {
          x = str.indexOf(cha, x + 1)
        }
        return process.env.VUE_APP_BASE_API + "/" + str.substring(x + 1, str.length)
      },
      // 筛选节点
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },
      // 节点单击事件
      handleNodeClick(data) {
        this.treeNodeData = data
        this.areaId = data.id;
        let form = {};
        form.areaId = this.areaId;

        //获取当前点击节点的所有的设计器页面
        this.getDesignerAreaPage(form);
      },

      // 节点单击事件
      copyHandleNodeClick(node, tree) {
        this.$nextTick(() => {
          this.copyTreeid = node.id;
          this.$refs.areaCopyTree.setCheckedKeys([node.id]);
        })
      },

      //查看页面
      intoExhibitionPage(item) {

        this.$store.commit('setEditMode', 'preview')
        this.areaId = item.areaId.toString();
        this.pageId = item.id.toString();
        let form = {};
        form.id = this.pageId;
        form.areaId = this.areaId;
        this.getAreaDesignerPage(form, "see");

      },
      //进入配置页面
      intoDesignerPage(item) {
        this.areaId = item.areaId.toString();
        this.pageId = item.id.toString();
        this.name = item.name.toString();

        this.$store.commit('setEditMode', 'edit')
        let form = {};
        form.id = parseInt(item.id);
        form.name = item.name;
        form.areaId = parseInt(item.areaId);
        this.getAreaDesignerPage(form, "update");
      },

      //获取当前区域的设计器页面
      getAreaDesignerPage(form, param) {

        let that = this;

        seleteDesignerAreaPage(form).then(response => {
          if (response.code == 200) {
            if (response.data[0].canvasStyle == null) {
              this.$store.commit('setCurComponent', {component: null, index: null})
              this.$store.commit('setComponentData', [])
              let bg = {
                "width": "1920",
                "height": "1080",
                "scale": 100,
                "color": "#000",
                "opacity": 1,
                "background": "#fff",
                "fontSize": 14,
                "backgroundColor": null
              }
              that.$store.commit('setCanvasStyle', bg);

              that.homePageShow = 2;
            } else {
              let canvasStyle = JSON.parse(response.data[0].canvasStyle);
              let canvasData = JSON.parse(response.data[0].canvasData);
              that.$store.commit('setCanvasStyle', canvasStyle);
              that.$store.commit('setComponentData', canvasData);
              setDefaultcomponentData(canvasData);

              if (param == "update") {
                that.homePageShow = 2;
              } else {
                that.homePageShow = 3;
                setTimeout(function () {
                  let form = {};
                  form.id = parseInt(that.pageId);
                  form.areaId = parseInt(that.areaId);
                  that.$refs.designerViewPage.getAreaDesignerPage(form);
                }, 300);
              }

            }
          }
        })
      },

      goBackHomePage() {
        if (this.homePageShow == 2) {
          this.$confirm('此操作不会保存修改的配置, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.homePageShow = 1;

            this.$nextTick(() => {
              this.$refs.areaTree.setCurrentKey(this.areaId)

              this.treeNodeData = this.$refs.areaTree.getCurrentNode();
              let form = {};
              form.areaId = this.areaId;
              //获取当前点击节点的所有的设计器页面
              this.getDesignerAreaPage(form);
            })
            //销毁所有的组件页面订阅
            pubsub.destructionAllDesignerSub();
          }).catch(() => {

          });
        } else {
          this.homePageShow = 1;

          this.$nextTick(() => {
            this.$refs.areaTree.setCurrentKey(this.areaId)

            this.treeNodeData = this.$refs.areaTree.getCurrentNode();
            let form = {};
            form.areaId = this.areaId;
            //获取当前点击节点的所有的设计器页面
            this.getDesignerAreaPage(form);
          })
          //销毁所有的组件页面订阅
          // pubsub.destructionAllDesignerSub();
        }


      },

      handleDrop(e) {
        e.preventDefault()
        e.stopPropagation()
        const designerIndex = e.dataTransfer.getData('index')
        const rectInfo = this.editor.getBoundingClientRect()
        if (designerIndex) {
          const component = deepCopy(componentList[designerIndex])
          component.style.top = e.clientY - rectInfo.y
          component.style.left = e.clientX - rectInfo.x
          component.id = generateID()

          // 根据画面比例修改组件样式比例 https://github.com/woai3c/visual-drag-demo/issues/91
          changeComponentSizeWithScale(component)

          this.$store.commit('addComponent', {component})
          this.$store.commit('recordSnapshot')
        }
      },

      handleDragOver(e) {
        e.preventDefault()
        e.dataTransfer.dropEffect = 'copy'
      },

      handleMouseDown(e) {
        e.stopPropagation()
        this.$store.commit('setClickComponentStatus', false)
        this.$store.commit('setInEditorStatus', true)
      },

      deselectCurComponent(e) {
        if (!this.isClickComponent) {
          this.$store.commit('setCurComponent', {component: null, index: null})
        }

        // 0 左击 1 滚轮 2 右击
        if (e.button != 2) {
          this.$store.commit('hideContextMenu')
        }
      },
    },
  }
</script>

<style lang="scss" scoped>
  .home {
    height: 86vh;
    background: #fff;

    main {
      height: calc(100% - 64px);
      position: relative;

      .left {
        position: absolute;
        height: 100%;
        width: 200px;
        left: 0;
        top: 0;

        & > div {
          overflow: auto;

          &:first-child {
            border-bottom: 1px solid #ddd;
          }
        }
      }

      .right {
        position: absolute;
        height: 100%;
        width: 14.5vw;
        right: 0;
        top: 0;
        overflow-y: auto;
        .el-select {
          width: 100%;
        }
      }

      .center {
        margin-left: 200px;
        margin-right: 288px;
        background: #f5f5f5;
        height: 100%;
        overflow: auto;
        padding: 20px;

        .content {
          width: 100%;
          height: 91%;
          overflow: auto;
        }
      }
    }

    .placeholder {
      text-align: center;
      color: #333;
    }

    .global-attr {
      padding: 10px;
    }
  }

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
    height: calc(94vh - 60px - 10px);
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
