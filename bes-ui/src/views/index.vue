<template>
  <div class="app-container">


    <!-- 中间画布 -->
    <div style="margin-left: 2vw;margin-top: 1vh">
      <!--查询条件-->
      <el-form style="position: relative" :model="queryParams" ref="queryForm" size="small" :inline="true"
               label-width="68px">
        <el-form-item label="园区名称" prop="name">
          <el-select v-model="queryParams.parkCode"
                     @change="parkChange(queryParams.parkCode)"
                     placeholder="请选择" style="margin-bottom: 2px;width: 17vw">
            <el-option
              v-for="item in parkList"
              :key="item.code"
              :label="item.name"
              :value="item.code">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="区域" prop="code">
          <el-cascader v-model="queryParams.areaCode"
                       @change="areaChange(queryParams.areaCode)"
                       :options="areaList"
                       :props="modelTypeProps"
                       :show-all-levels="false"></el-cascader>
        </el-form-item>
        <el-form-item label="页面" prop="pageName">
          <el-select v-model="queryParams.pageCode"
                     @change="pageChange(queryParams.pageCode)"
                     placeholder="请选择" style="margin-bottom: 2px;width: 17vw">
            <el-option
              v-for="item in pageList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

    </div>
    <section style="background-color: #ebebeb">
      <designer-view ref="designerViewPageIndex" :area-id="areaId" :page-id="pageId" :keyValue="key"
                     :page-name="viewsIndex" :ShowBack="false"/>
    </section>

  </div>
</template>

<script>
  import designerView from "@/views/designerConfigure/views/designerView"
  import {mapState} from "vuex";
  import {designerAreaListInfo, seleteDesignerAreaPage} from "@/api/designerConfigure/designer";
  import {getAllPark} from "@/api/electricPowerTranscription/powerData/powerData";
  import {allEquipmentFunctionTree, allListTree} from "@/api/basicData/deviceManagement/deviceTree/deviceTree";
  import {deviceTreeSettings} from "@/api/basicData/deviceManagement/deviceTree/deviceTreeSettings";
  import store from "@/store";
  import pubsub from "@/store/modules/PubSub";

  export default {
    components: {designerView},
    name: 'Dashboard',
    computed: {
      ...mapState([
        'componentData',
        "curComponent",
        'isClickComponent',
        'canvasStyleData',
        'editor',
      ]),
      key() {
        return this.$route.path
      }
    },

    data() {

      return {
        // 查询参数
        queryParams: {
          parkCode: null,
          areaCode: null,
          pageCode: null
        },

        viewsIndex: 'viewsIndex',
        parkList: [],//园区
        areaList: [],//区域
        pageList: [],//页面
        areaId: "1",//区域id
        pageId: "7",//页面id
        form: {},
        modelTypeProps: {
          checkStrictly: true,
          emitPath: false,
        }

      }
    },

    created() {
      //获取所有的园区
      this.getAllPark();

      //获取设备树
      this.getDeviceTree();
      //获取第三方设备树
      this.getOtherDeviceTree();
      // this.$store.commit('setToViewPage', true)

      // this.areaId = this.$route.query.areaId;
      // this.pageId = this.$route.query.pageId;
      // this.form.id = parseInt(this.pageId);
      // this.form.areaId = parseInt(this.areaId);

    },
    mounted() {
      // this.restore()
    },
    methods: {
      getAllPark() {
        getAllPark().then(response => {
          this.parkList = response
          if (this.parkList.length > 0) {
            this.queryParams.parkCode = this.parkList[0].code
            //获取区域树节点信息
            this.getDesignerAreaList(this.parkList[0].code);
          } else {
            this.parkCode = ''
          }
        });
      },
      //获取区域树节点信息
      getDesignerAreaList(parkCode) {
        const that = this;
        let designerArea = {};
        designerArea.parkCode = parkCode;
        designerAreaListInfo(designerArea).then(response => {
          if (response.code == 200) {
            if (response.data.length > 0) {
              for (let i = 0; i < response.data.length; i++) {
                response.data[i].value = response.data[i].id;
                response.data[i].label = response.data[i].name;
              }

              const data = this.handleTree(response.data, "id", "parentId", "remark");
              this.queryParams.areaCode = response.data[0].value
              that.areaList = data;
              that.$nextTick(() => {
                that.areaId = that.areaList[0].id.toString();
                let form = {};
                form.areaId = that.areaId;
                //获取当前点击节点的所有的设计器页面
                this.getDesignerAreaPage(form);
              })
            } else {
              this.areaList = []
            }
          }
        })
      },

      //获取当前点击节点的所有的设计器页面
      getDesignerAreaPage(form) {
        seleteDesignerAreaPage(form).then(response => {

          if (response.code == 200) {
            if (response.data.length == 0) {
              this.pageList = [];
              this.queryParams.pageCode = null
            } else {
              this.pageList = response.data;
              this.queryParams.pageCode = response.data[0].id;

              this.pageChange(response.data[0].id);
            }
          }

        })
      },
      parkChange(data) {
        //获取区域树节点信息
        this.getDesignerAreaList(data);
      },
      areaChange(data) {

        let form = {};
        form.areaId = data;
        //获取当前点击节点的所有的设计器页面
        this.getDesignerAreaPage(form);
      },
      pageChange(data) {
        this.pageId = data;
        this.restore()
        //获取区域树节点信息
        // this.getDesignerAreaList(data);
      },
      restore() {

        //销毁所有的组件页面订阅
        pubsub.destructionAllDesignerSub();

        this.$store.commit('setEditMode', 'preview')
        let form = {};
        form.id = this.pageId;
        form.areaId = this.areaId;
        this.getAreaDesignerPage(form, "see");
      },
      //获取当前区域的设计器页面
      getAreaDesignerPage(form, param) {

        let that = this;

        let form1 = {};
        form1.id = parseInt(that.pageId);
        form1.areaId = parseInt(that.areaId);
        that.$refs.designerViewPageIndex.getAreaDesignerPage(form);
      },

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

    }
  }
</script>

<style lang="scss" scoped>
  ::v-deep .el-form-item--small.el-form-item {
    margin-bottom: 8px;
  }

  .app-container {
    padding: 0px !important;
  }

  .meter-chart {
    height: 200px;
  }

  .meter-title {
    font-size: 20px
  }

  ::v-deep .el-card__body {
    padding: 15px 20px 20px 20px;
  }
</style>
