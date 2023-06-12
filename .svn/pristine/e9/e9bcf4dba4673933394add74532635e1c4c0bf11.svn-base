<template>
  <div class="home">
    <Toolbar :area-id="areaId" :page-id="pageId" :name="name"/>
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
        <el-tabs v-if="curComponent" v-model="activeName">
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
</template>

<script>
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

  import Editor from '@/components/designer/Editor/designerIndex'
  import ComponentList from '@/components/designer/ComponentList' // 左侧列表组件
  import AnimationList from '@/components/designer/AnimationList' // 右侧动画列表
  import EventList from '@/components/designer/EventList' // 右侧事件列表
  import componentList from '@/custom-component/component-list' // 左侧列表数据
  import Toolbar from '@/components/designer/Toolbar'
  import {deepCopy} from '@/utils/designer/utils'
  import {mapState} from 'vuex'
  import generateID from '@/utils/designer/generateID'
  import {listenGlobalKeyDown} from '@/utils/designer/shortcutKey'
  import RealTimeComponentList from '@/components/designer/RealTimeComponentList'
  import CanvasAttr from '@/components/designer/CanvasAttr'
  import {changeComponentSizeWithScale} from '@/utils/designer/changeComponentsSizeWithScale'
  import {setDefaultcomponentData} from '@/store/designer/snapshot'

  import {seleteDesignerAreaPage} from '@/api/designerConfigure/designer'
  import pubsub from "@/store/modules/PubSub";

  export default {
    components: {
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
      LightAttr
    },
    name: "index",
    data() {
      return {
        activeName: 'attr',
        reSelectAnimateIndex: undefined,
        scale: 1,
        areaId: "1",//区域id
        pageId: "1",//页面id
        name:"页面",//页面名称
        form: {},
      }
    },
    computed: mapState([
      'componentData',
      "curComponent",
      'isClickComponent',
      'canvasStyleData',
      'editor',
      'editMode',
      'ToViewPage'
    ]),
    created() {
      this.areaId = this.$route.query.areaId;
      this.pageId = this.$route.query.pageId;
      // this.areaId = this.$route.params && this.$route.params.areaId;
      // this.pageId = this.$route.params && this.$route.params.pageId;
      this.name = this.$route.query.name;
      // this.form.id = parseInt(this.pageId);
      // this.form.areaId = parseInt(this.areaId);
      // this.getAreaDesignerPage(this.form);
      // this.getTypeList();
      // this.restore()

      // 全局监听按键事件
      listenGlobalKeyDown()

      //监听滚轮

    },
    beforeDestroy() {
      if (!this.ToViewPage) {
        pubsub.remove(null,'VtextWebsocket')
        pubsub.remove(null,'VLightWebsocket')
      }

    },

    /* mounted() {
       document.getElementById('body').onmousewheel = (event) => {
         let dir = event.deltaY > 0 ? 'Up' : 'Down'
         if (dir == 'Up') {
           //向上滑动
           // this.zoomin()
           this.zoomout()
         } else {
           //向下滑动
           // this.zoomout()
           this.zoomin()
         }
         return false;
       }
     },*/
    methods: {
      //获取当前区域的设计器页面
      /*getAreaDesignerPage(form) {
        let that = this;
        seleteDesignerAreaPage(form).then(response => {
          if (response.code == 200) {
            if (response.data[0].canvasStyle == null) {
              that.$store.commit('setCanvasStyle', []);
              this.$store.commit('setComponentData', [])
            } else {
              let canvasStyle = JSON.parse(response.data[0].canvasStyle);
              let canvasData = JSON.parse(response.data[0].canvasData);
              that.$store.commit('setCanvasStyle', canvasStyle);
              that.$store.commit('setComponentData', canvasData);
              setDefaultcomponentData(canvasData);
            }
          }
        })
      },*/

      // 向上滑动
      zoomin() {
        document.getElementById('content').style.transform = `scale(${this.scale})`
        this.scale += 0.05
      },
      // 向下滑动
      zoomout() {
        document.getElementById('content').style.transform = `scale(${this.scale})`
        this.scale -= 0.5
        if (this.scale < 0.5) {
          this.scale = 0.5
        }
      },
      restore() {
        // 用保存的数据恢复画布
        if (localStorage.getItem('canvasData')) {
          setDefaultcomponentData(JSON.parse(localStorage.getItem('canvasData')))
          this.$store.commit('setComponentData', JSON.parse(localStorage.getItem('canvasData')))
        }

        if (localStorage.getItem('canvasStyle')) {
          this.$store.commit('setCanvasStyle', JSON.parse(localStorage.getItem('canvasStyle')))
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


<style lang="scss">
  .home {
    height: 84vh;
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

        .el-select {
          width: 100%;
        }
      }

      .center {
        margin-left: 200px;
        margin-right: 288px;
        background: #f5f5f5;
        height: 87%;
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
</style>
