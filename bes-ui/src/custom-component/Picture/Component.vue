<template>
  <div style="overflow: hidden;">
    <canvas v-if="canvasShow" ref="canvas"></canvas>
    <img ref="gif"
         v-else
      :src="gif"
         :style="[gifWidth,gifHeight]"
      />
  </div>
</template>

<script>
  import OnEvent from '../common/OnEvent'

  export default {
    extends: OnEvent,
    props: {
      propValue: {
        type: Object,
        required: true,
        default: () => {
        },
      },
      element: {
        type: Object,
        default: () => {
        },
      },
    },
    data() {
      return {
        width: 0,
        height: 0,
        img: null,
        canvas: null,
        ctx: null,
        isFirst: true,
        canvasShow: true,
        gif: null,
        gifWidth: {},
        gifHeight: {},
      }
    },
    watch: {
      'element.style.width': function () {
        this.drawImage()
      },
      'element.style.height': function () {
        this.drawImage()
      },
      'propValue.flip.vertical': function () {
        this.mirrorFlip()
      },
      'propValue.flip.horizontal': function () {
        this.mirrorFlip()
      },
    },
    mounted() {
      this.canvas = this.$refs.canvas
      this.ctx = this.canvas.getContext('2d')
      this.drawImage()
    },
    methods: {
      drawImage() {
        const {width, height} = this.element.style
        this.canvas.width = width
        this.canvas.height = height
        if (this.isFirst) {
          this.isFirst = false
          this.img = document.createElement('img')
          if (this.propValue.url.substr(this.propValue.url.lastIndexOf(".") + 1) == "gif") {
            this.canvasShow = false
            this.gif = this.propValue.url

            this.gifWidth = {'width':width + 'px'};
            this.gifHeight = {'height':width + 'px'};
          } else {
            this.canvasShow = true
          }
          this.img.src = this.findStrSubtring(this.propValue.url,'/',1);
          // this.img.src  = process.env.VUE_APP_BASE_API + "/profile/designerPicture/2022/10/28/blob_20221028143738A002.jpg"
          // this.img.src  = "/profile/designerPicture/2022/10/28/blob_20221028143738A002.jpg"
          // this.img.src = require('@/assets/designer/404.png')
          this.img.onload = () => {
            this.ctx.drawImage(this.img, 0, 0, width, height)
            this.mirrorFlip()
          }
        } else {
          this.mirrorFlip()
        }
      },
      findStrSubtring (str, cha, num) {
        var x = str.indexOf(cha)
        for (var i = 0; i < num; i++) {
          x = str.indexOf(cha, x + 1)
        }
        return process.env.VUE_APP_BASE_API + '/' + str.substring(x + 1, str.length)
      },
      mirrorFlip() {
        const {vertical, horizontal} = this.propValue.flip
        const {width, height} = this.element.style
        const hvalue = horizontal ? -1 : 1
        const vValue = vertical ? -1 : 1

        // 清除图片
        this.ctx.clearRect(0, 0, width, height)
        // 平移图片
        this.ctx.translate(width / 2 - width * hvalue / 2, height / 2 - height * vValue / 2)
        // 对称镜像
        this.ctx.scale(hvalue, vValue)
        this.ctx.drawImage(this.img, 0, 0, width, height)
        // 还原坐标点
        this.ctx.setTransform(1, 0, 0, 1, 0, 0);

        this.gif = this.propValue.url

        this.gifWidth = {'width':width + 'px'};
        this.gifHeight = {'height':width + 'px'};

      },
    },
  }
</script>
