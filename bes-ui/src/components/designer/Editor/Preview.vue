<template>
  <div ref="container" class="bg">
    <el-button  class="close" @click="close">关闭</el-button>
    <el-button v-if="bottons.isSaveScreen" class="submit" @click="save">保存</el-button>
    <el-button v-if ="bottons.isScreenshot" class="submit" @click="htmlToImage(true)">确定</el-button>
    <div class="canvas-container">
      <div
        class="canvas"
        :style="{
                    ...getCanvasStyle(canvasStyleData),

                    width: changeStyleWithScale(canvasStyleData.width) + 'px',
                    height: changeStyleWithScale(canvasStyleData.height) + 'px',
                }"
      >
        <ComponentWrapper
          v-for="(item, index) in copyData"
          :key="index"
          :config="item"
        />
      </div>
    </div>
  </div>
</template>

<script>
  import {getStyle, getCanvasStyle} from '@/utils/designer/style'
  import {mapState} from 'vuex'
  import ComponentWrapper from './ComponentWrapper'
  import {changeStyleWithScale} from '@/utils/designer/translate'
  import {toPng} from 'html-to-image'
  import {deepCopy} from '@/utils/designer/utils'
  import html2canvas from 'html2canvas';
  import {addDesignerAreaPage, updateDesignerAreaPage, uploadDesignerScreenshot} from "@/api/designerConfigure/designer";

  export default {
    components: {ComponentWrapper},
    props: {
      bottons: {
        type: Object,
        default() {
          return {
            isSaveScreen: false,
            isScreenshot: false,
          };
        },
      },
      areaId: {
        type: String,
        default: "",
      },
      pageId: {
        type: String,
        default: "",
      },
      name: {
        type: String,
        default: "",
      }
    },
    data() {
      return {
        copyData: [],
        form: {}
      }
    },
    computed: mapState([
      'componentData',
      'canvasStyleData',
    ]),
    watch: {
      isSaveScreen(val) {
        this.isSaveScreen = val
      },
      deep: true
    },
    created() {
      this.$set(this, 'copyData', deepCopy(this.componentData))
    },
    methods: {
      getStyle,
      getCanvasStyle,

      changeStyleWithScale,

      close() {
        this.$emit('close')
      },
      save() {
        this.form.id = parseFloat(this.pageId);
        this.form.areaId = parseFloat(this.areaId);
        this.form.name = this.name;
        this.form.canvasData = JSON.stringify(this.componentData);
        this.form.canvasStyle = JSON.stringify(this.canvasStyleData);

        if (this.pageId == '') {
          addDesignerAreaPage(this.form).then(response => {
            // addDesignerAreaPage(JSON.stringify(this.form)).then(response => {
            if (response.code == 200) {
              this.$modal.msgSuccess(response.msg);
              this.htmlToImage(false);
            } else {
              this.$modal.msgWarning(response.msg);
            }
          })
        } else {
          updateDesignerAreaPage(this.form).then(response => {
            // addDesignerAreaPage(JSON.stringify(this.form)).then(response => {
            if (response.code == 200) {
              this.$modal.msgSuccess(response.msg);
              this.htmlToImage(false);
            } else {
              this.$modal.msgWarning(response.msg);
            }
          })
        }
      },

      htmlToImage(boolean) {
        const that = this;
        const rect = document.querySelector('.canvas')
        html2canvas(document.querySelector('.canvas'), {
          width: rect.width,
          height: rect.height
        }).then(function (canvas) {
          const pageData = canvas.toDataURL('image/jpeg', 1.0)
          const imgData = pageData.replace('image/jpeg', 'image/octet-stream')

          if (!boolean) {
            // 第一步：将dataUrl转换成Blob
            var fd = new FormData()
            var blob = that.dataURItoBlob(imgData)
            fd.append('picturefile', blob, Date.now() + '.jpg');
            fd.append("vue_app_base_api",process.env.VUE_APP_BASE_API)
            fd.append('areaId',parseFloat(that.areaId));
            fd.append('pageId',parseFloat(that.pageId));
            // 第二步：上传分享图
            uploadDesignerScreenshot(fd).then(response => {

            })

            return
          }
          const imgName = '生成图片.jpg'
          const save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a')
          save_link.href = imgData
          save_link.download = imgName
          const event = document.createEvent('MouseEvents')
          event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)
          save_link.dispatchEvent(event)



        }).catch(error => {
          console.error('oops, something went wrong!', error)
        })
          .finally(this.close)
        /*toPng(this.$refs.container.querySelector('.canvas'))
        .then(dataUrl => {
          download(dataUrl, 'my-node.png');
            // const a = document.createElement('a')
            // a.setAttribute('download', 'screenshot')
            // a.href = dataUrl
            // a.click()
        })
        .catch(error => {
            console.error('oops, something went wrong!', error)
        })
        .finally(this.close)*/
      },

      dataURItoBlob (dataURI) { // base64转buffer
        var byteString = atob(dataURI.split(',')[1])
        var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0]
        var ab = new ArrayBuffer(byteString.length)
        var ia = new Uint8Array(ab)
        for (var i = 0; i < byteString.length; i++) {
          ia[i] = byteString.charCodeAt(i)
        }
        return new Blob([ab], { type: mimeString })
      },
    },
  }
</script>

<style lang="scss" scoped>
  .bg {
    width: 90%;
    height: 100%;
    top: 0;
    left: 10vw;
    position: fixed;
    background: rgb(0, 0, 0, .5);
    z-index: 10;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: auto;
    padding: 20px;

    .canvas-container {
      width: calc(100% - 40px);
      height: calc(100% - 120px);
      overflow: auto;

      .canvas {
        background: #fff;
        position: relative;
        margin: auto;
      }
    }

    .close {
      position: absolute;
      right: 100px;
      top: 20px;
    }
    .submit {
      position: absolute;
      right: 20px;
      top: 20px;
    }
  }
</style>
