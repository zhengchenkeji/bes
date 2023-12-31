import Vue from 'vue'

import Cookies from 'js-cookie'

import Element from 'element-ui'
import './assets/styles/element-variables.scss'
import './views/iot/css/iot.css'

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/ruoyi.scss' // ruoyi css
import App from './App'
import store from './store'
import router from './router'
// import '@/custom-component' // 注册自定义组件
import directive from './directive' //directive
import plugins from './plugins' // plugins
import { download } from '@/utils/request'

import '@/assets/iconfont/iconfont.css'
import '@/assets/styles/animate.scss'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/styles/reset.css'
import '@/assets/styles/global.scss'


import './assets/icons' // icon
import './permission' // permission control
import { Socket } from './utils/socket'
import { getDicts } from "@/api/system/dict/data";
import { getConfigKey } from "@/api/system/config";
import { parseTime, resetForm, addDateRange, selectDictLabel, selectDictLabels, handleTree } from "@/utils/ruoyi";
// 分页组件
import Pagination from "@/components/Pagination";
// 自定义表格工具组件
import RightToolbar from "@/components/RightToolbar"
// 富文本组件
import Editor from "@/components/Editor"
// 文件上传组件
import FileUpload from "@/components/FileUpload"
// 图片上传组件
import ImageUpload from "@/components/ImageUpload"
// 图片预览组件
import ImagePreview from "@/components/ImagePreview"
// 字典标签组件
import DictTag from '@/components/DictTag'
// 头部标签组件
import VueMeta from 'vue-meta'
// 字典数据组件
import DictData from '@/components/DictData'

import CircleShape from "@/custom-component/CircleShape/Component";
import Picture from "@/custom-component/Picture/Component";
import VText from "@/custom-component/VText/Component";
import VButton from "@/custom-component/VButton/Component";
import Group from "@/custom-component/Group/Component";
import RectShape from "@/custom-component/RectShape/Component";
import LineShape from "@/custom-component/LineShape/Component";
import LineShapeSVG from "@/custom-component/LineShapeSVG/Component";
import VTable from "@/custom-component/VTable/Component";

import SVGTriangle from "@/custom-component/svgs/SVGTriangle/Component";
import SVGStar from "@/custom-component/svgs/SVGStar/Component";
// 灯光组件
import Light from "@/custom-component/Light/Component";
// 筒灯
import DownLight from "@/custom-component/DownLight/Component";
// 通讯灯
import CommunicationLight from "@/custom-component/CommunicationLight/Component";
// 大厅灯
import HallLight from "@/custom-component/HallLight/Component";
// 风机
import DraughtFan from "@/custom-component/DraughtFan/Component";
//天气
import fetchJsonp from "fetch-jsonp"
Vue.prototype.$fetchJsonp = fetchJsonp;

import {VueJsonp} from 'vue-jsonp'
Vue.use(VueJsonp)

import html2canvas from 'html2canvas'

// 全局方法挂载
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.download = download
Vue.prototype.handleTree = handleTree
Vue.prototype.socket = Socket

// 全局组件挂载
Vue.component('DictTag', DictTag)
Vue.component('Pagination', Pagination)
Vue.component('RightToolbar', RightToolbar)
Vue.component('Editor', Editor)
Vue.component('FileUpload', FileUpload)
Vue.component('ImageUpload', ImageUpload)
Vue.component('ImagePreview', ImagePreview)

Vue.component('CircleShape', CircleShape)
Vue.component('Picture', Picture)
Vue.component('VText', VText)
Vue.component('VButton', VButton)
Vue.component('Group', Group)
Vue.component('RectShape', RectShape)
Vue.component('LineShape', LineShape)
Vue.component('LineShapeSVG', LineShapeSVG)
Vue.component('VTable', VTable)

Vue.component('SVGTriangle', SVGTriangle)
Vue.component('SVGStar', SVGStar)
Vue.component('Light', Light)
Vue.component('DownLight', DownLight)
Vue.component('CommunicationLight', CommunicationLight)
Vue.component('HallLight', HallLight)
Vue.component('DraughtFan', DraughtFan)

Vue.use(directive)
Vue.use(plugins)
Vue.use(VueMeta)
DictData.install()

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
