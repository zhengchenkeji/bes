<template>
  <div>
    <div class="toolbar">
      <el-button @click="undo">撤消</el-button>
      <el-button @click="redo">重做</el-button>
      <label for="input" class="insert">
        插入图片
        <input
          id="input"
          type="file"
          hidden
          @change="handleFileChange"
        />
      </label>

      <el-button style="margin-left: 10px;" @click="preview(false)">预览</el-button>
      <el-button @click="save(true)">保存</el-button>
      <el-button @click="clearCanvas">清空画布</el-button>
      <el-button :disabled="!areaData.components.length" @click="compose">组合</el-button>
      <el-button
        :disabled="!curComponent || curComponent.isLock || curComponent.component != 'Group'"
        @click="decompose"
      >
        拆分
      </el-button>

      <el-button :disabled="!curComponent || curComponent.isLock" @click="lock">锁定</el-button>
      <el-button :disabled="!curComponent || !curComponent.isLock" @click="unlock">解锁</el-button>
      <el-button @click="preview(true)">截图</el-button>

      <div class="canvas-config">
        <span>画布大小</span>
        <input v-model="canvasStyleData.width">
        <span>*</span>
        <input v-model="canvasStyleData.height">
      </div>
      <div class="canvas-config">
        <span>画布比例</span>
        <input v-model="scale" @input="handleScaleChange"> %
      </div>
      <el-button @click="back">返回</el-button>
    </div>

    <!-- 预览 -->
    <Preview v-if="isShowPreview"
             :area-id="areaId"
             :page-id="pageId"
             :name="name"
             :bottons="bottons"
             @close="handlePreviewChange"/>
  </div>
</template>

<script>
  import store from "@/store";
  import generateID from '@/utils/designer/generateID'
  import toast from '@/utils/designer/toast'
  import {mapState} from 'vuex'
  import Preview from '@/components/designer/Editor/Preview'
  import {commonStyle, commonAttr} from '@/custom-component/component-list'
  import eventBus from '@/utils/designer/eventBus'
  import {$} from '@/utils/designer/utils'
  import changeComponentsSizeWithScale, {changeComponentSizeWithScale} from '@/utils/designer/changeComponentsSizeWithScale'

  import { addDesignerAreaPage, updateDesignerAreaPage, uploadDesignerAvatar } from '@/api/designerConfigure/designer'

  export default {
    components: {Preview},
    props: {
      areaId:{
        type: String,
        default: "",
      },
      pageId:{
        type: String,
        default: "",
      },
      name:{
        type: String,
        default: "",
      }
    },
    data() {
      return {
        isShowPreview: false,
        timer: null,
        scale: 100,
        form: {},
        bottons: {}
      }
    },
    computed: mapState([
      'componentData',
      'canvasStyleData',
      'areaData',
      'curComponent',
      'curComponentIndex',
    ]),
    created() {
      eventBus.$on('preview', this.preview)
      eventBus.$on('save', this.save)
      eventBus.$on('clearCanvas', this.clearCanvas)

      this.scale = this.canvasStyleData.scale
    },
    methods: {
      back() {
        this.$emit('back');
        // this.$router.push({
        //   path: '/designerConfigure/configures/index',
        // })
      },
      handleScaleChange() {
        clearTimeout(this.timer)
        this.timer = setTimeout(() => {
          // 画布比例设一个最小值，不能为 0
          // eslint-disable-next-line no-bitwise
          this.scale = (~~this.scale) || 1
          changeComponentsSizeWithScale(this.scale)
        }, 1000)
      },

      lock() {
        this.$store.commit('lock')
      },

      unlock() {
        this.$store.commit('unlock')
      },

      compose() {
        this.$store.commit('compose')
        this.$store.commit('recordSnapshot')
      },

      decompose() {
        this.$store.commit('decompose')
        this.$store.commit('recordSnapshot')
      },

      undo() {
        this.$store.commit('undo')
      },

      redo() {
        this.$store.commit('redo')
      },

      handleFileChange(e) {
        const file = e.target.files[0]
        if (!file.type.includes('image')) {
          toast('只能插入图片')
          return
        }
        let formData = new FormData();
        formData.append("picturefile", file);
        uploadDesignerAvatar(formData).then(response => {
          const fileResultPicture = process.env.VUE_APP_BASE_API + response.imgUrl;

          const reader = new FileReader()
          reader.onload = (res) => {
            const fileResult = res.target.result
            const img = new Image()


            img.onload = () => {
              //限制图片的宽高不能超过画布
              let width = img.width;
              if (width > this.canvasStyleData.width) {
                width = this.canvasStyleData.width;
              }

              let height = img.height;
              if (height > this.canvasStyleData.height) {
                height = this.canvasStyleData.height;
              }

              const component = {
                ...commonAttr,
                id: generateID(),
                component: 'Picture',
                label: '图片',
                icon: '',
                propValue: {
                  url: fileResultPicture,
                  flip: {
                    horizontal: false,
                    vertical: false,
                  },
                },
                style: {
                  ...commonStyle,
                  top: 0,
                  left: 0,
                  // width: img.width,
                  // height: img.height,
                  width: width,
                  height: height,
                },
              }

              // 根据画面比例修改组件样式比例 https://github.com/woai3c/visual-drag-demo/issues/91
              changeComponentSizeWithScale(component)

              this.$store.commit('addComponent', {component})
              this.$store.commit('recordSnapshot')

              // 修复重复上传同一文件，@change 不触发的问题
              $('#input').setAttribute('type', 'text')
              $('#input').setAttribute('type', 'file')
            }

            img.src = fileResult
          }
          reader.readAsDataURL(file)
        })




      },

      preview(isScreenshot) {
        this.bottons.isSaveScreen = isScreenshot;
        this.bottons.isScreenshot = isScreenshot;
        this.isShowPreview = true
        this.$store.commit('setEditMode', 'preview')
      },

      save(isSaveScreen) {
        this.bottons.isSaveScreen = isSaveScreen;
        this.bottons.isScreenshot = !isSaveScreen;
        this.isShowPreview = true
        this.$store.commit('setEditMode', 'preview')


        // console.log(this.areaId);

        // localStorage.setItem('canvasData', JSON.stringify(this.componentData))
        // localStorage.setItem('canvasStyle', JSON.stringify(this.canvasStyleData))
        // this.$message.success('保存成功')
      },

      clearCanvas() {
        this.$store.commit('setCurComponent', {component: null, index: null})
        this.$store.commit('setComponentData', [])
        this.$store.commit('recordSnapshot')
      },

      handlePreviewChange() {
        this.bottons.isSaveScreen = false;
        this.bottons.isScreenshot = false;
        this.isShowPreview = false
        this.isSaveScreen = false
        this.$store.commit('setEditMode', 'edit')
      },
    },
  }
</script>

<style lang="scss" scoped>
  .toolbar {
    padding: 15px 10px;
    white-space: nowrap;
    overflow-x: auto;
    background: #fff;
    border-bottom: 1px solid #ddd;

    .canvas-config {
      display: inline-block;
      margin-left: 10px;
      font-size: 14px;
      color: #606266;

      input {
        width: 50px;
        margin-left: 4px;
        outline: none;
        padding: 0 5px;
        border: 1px solid #ddd;
        color: #606266;
      }

      span {
        margin-left: 10px;
      }
    }

    .insert {
      display: inline-block;
      line-height: 1;
      white-space: nowrap;
      cursor: pointer;
      background: #fff;
      border: 1px solid #dcdfe6;
      color: #606266;
      appearance: none;
      text-align: center;
      box-sizing: border-box;
      outline: 0;
      margin: 0;
      transition: .1s;
      font-weight: 500;
      padding: 9px 15px;
      font-size: 12px;
      border-radius: 3px;
      margin-left: 10px;

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
  }
</style>
