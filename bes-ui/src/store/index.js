import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import user from './modules/user'
import websocket from './modules/websocket'
import tagsView from './modules/tagsView'
import permission from './modules/permission'
import settings from './modules/settings'
import getters from './getters'

import animation from './designer/animation'
import compose from './designer/compose'
import contextmenu from './designer/contextmenu'
import copy from './designer/copy'
import event from './designer/event'
import layer from './designer/layer'
import snapshot from './designer/snapshot'
import lock from './designer/lock'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    user,
    websocket,
    tagsView,
    permission,
    settings
  },
  getters,
  state: {
    ...animation.state,
    ...compose.state,
    ...contextmenu.state,
    ...copy.state,
    ...event.state,
    ...layer.state,
    ...snapshot.state,
    ...lock.state,

    editMode: 'edit', // 编辑器模式 edit preview
    canvasStyleData: { // 页面全局数据
      width: 1200,
      height: 740,
      scale: 100,
      color: '#000',
      opacity: 1,
      background: '#fff',
      fontSize: 14,
    },
    isInEdiotr: false, // 是否在编辑器中，用于判断复制、粘贴组件时是否生效，如果在编辑器外，则无视这些操作
    componentData: [], // 画布组件数据
    curComponent: null,
    curComponentIndex: null,
    // 点击画布时是否点中组件，主要用于取消选中组件用。
    // 如果没点中组件，并且在画布空白处弹起鼠标，则取消当前组件的选中状态
    isClickComponent: false,
    deviceTreeData: null,//设备树数据
    otherDeviceTreeData: null,//第三方协议设备树数据
    isUnsubscribe: true,//是否取消订阅
    ToConfigurePage: false,//设计器配置页面是否存在
    ToViewPage: false,//设计器查看页面是否存在
    IndexData: null,//首页缓存界面数据

    pointRealTimeData: null,//设计器点位实时数据
  },
  mutations: {
    ...animation.mutations,
    ...compose.mutations,
    ...contextmenu.mutations,
    ...copy.mutations,
    ...event.mutations,
    ...layer.mutations,
    ...snapshot.mutations,
    ...lock.mutations,

    setIndexData(state, status) {
      state.IndexData = status
    },
    setClickComponentStatus(state, status) {
      state.isClickComponent = status
    },

    setEditMode(state, mode) {
      state.editMode = mode
    },

    setIsUnsubscribe(state, status) {
      state.isUnsubscribe = status
    },

    setToConfigurePage(state, status) {
      state.ToConfigurePage = status
    },

    setToViewPage(state, status) {
      state.ToViewPage = status
    },

    setInEditorStatus(state, status) {
      state.isInEdiotr = status
    },

    setCanvasStyle(state, style) {
      state.canvasStyleData = style
    },

    setCurComponent(state, { component, index }) {
      state.curComponent = component
      state.curComponentIndex = index
    },

    setShapeStyle({ curComponent,canvasStyleData }, { top, left, width, height, rotate }) {
      let copyTop = top;
      if (copyTop < 0) {
        copyTop = 0
      } else if (copyTop > canvasStyleData.height - height) {
        copyTop = canvasStyleData.height - height;
      }

      let copyLeft = left;

      let left11 = null;
      var radian = ((2 * Math.PI) / 360) * rotate;
      if (rotate < 90) {
        left11 = width/2 - Math.cos(radian) * (width/2);//邻边
      } else {
        left11 = width/2 - (-Math.cos(radian) * (width/2));//邻边
      }

      // b:Math.cos(radian) * long//对边
      if (copyLeft < (0 - left11)) {
        copyLeft = 0 - left11
      } else if (copyLeft > canvasStyleData.width - left11) {
        copyLeft = canvasStyleData.width - left11
      }
      if (top) curComponent.style.top = Math.round(copyTop)
      if (left) curComponent.style.left = Math.round(copyLeft)
      if (width) curComponent.style.width = Math.round(width)
      if (height) curComponent.style.height = Math.round(height)
      if (rotate) curComponent.style.rotate = Math.round(rotate)
    },

    setShapeSingleStyle({ curComponent }, { key, value }) {
      curComponent.style[key] = value
    },

    setComponentData(state, componentData = []) {
      Vue.set(state, 'componentData', componentData)
    },

    addComponent(state, { component, index }) {
      if (index !== undefined) {
        state.componentData.splice(index, 0, component)
      } else {
        state.componentData.push(component)
      }
    },

    deleteComponent(state, index) {
      if (index === undefined) {
        index = state.curComponentIndex
      }

      if (index == state.curComponentIndex) {
        state.curComponentIndex = null
        state.curComponent = null
      }

      if (/\d/.test(index)) {
        state.componentData.splice(index, 1)
      }
    },


    /********************先放在这个位置,测试好之后将其抽出放到单独的组件中**************************/
    //放入设备树数据
    setDeviceTree(state, treeData = []) {
      Vue.set(state, 'deviceTreeData', treeData)
    },
    //放入设备树数据
    setOtherDeviceTree(state, treeData = []) {
      Vue.set(state, 'otherDeviceTreeData', treeData)
    },
    //设计器点位实时数据
    setPointRealTimeData(state, dataIndexArray =[]) {
      Vue.set(state, 'pointRealTimeData', dataIndexArray)
    }
  },
})

export default store
