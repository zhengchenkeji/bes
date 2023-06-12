<template>

  <div class="line-shape">
    <div v-if="booFlow" class="line-block gradient" :style="{'--color':linePointColor,'--height':height,'--second':second}"></div>
    <div v-else class="line-block_false" :style="{'--color':linePointColor,'--height':height,'--second':second}"></div>
  </div>

</template>

<script>
  import OnEvent from '../common/OnEvent'
  import {isNumberStr} from "@/utils";
  export default {
    extends: OnEvent,
    props: {
      propValue: {
        type: String,
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
    },
    data () {
      return {
        border: '1px solid black',
        color: 'red',
        height:"1px",
        booFlow: false,
        linePointColor: "#000",
        second:'40.1s',
      }
    },
    watch:{
      'element.style.width': function (val) {
        this.second = val/100 * 4 + "s";
        console.log(val)
      },
      'element.style.height': function (val) {
        this.height = val + 'px';
      },
      'element.style.booFlow': function (val) {
        this.booFlow = val;
      },
      'element.style.linePointColor': function (val) {
        this.linePointColor = val;
      }
    },
    created() {
      this.booFlow = this.element.style.booFlow;
      this.height = this.element.style.height + 'px';
      this.linePointColor = this.element.style.linePointColor;
      this.second = this.element.style.width/100 * 4 + "s";
    }
  }
</script>

<style lang="scss"  scoped>
  .line-shape {
    width: 100%;
    height: 100%;
    position: absolute;
  }

  /* 渐变流光效果线条，要将横向宽度设置为超过100%的值，否则无动画效果 */
  .line-block {
    /*position: relative;*/
    width: 100%;
    height: var(--height);
   /* background: linear-gradient(
        -90deg,
        var(--color) 0%, var(--color) 2%, transparent 4%, transparent 6%,
        var(--color) 6%, var(--color) 8%, transparent 10%, transparent 12%,
        var(--color) 12%, var(--color) 14%, transparent 16%, transparent 18%,
        var(--color) 18%, var(--color) 20%, transparent 22%, transparent 24%,
        var(--color) 24%, var(--color) 26%, transparent 28%, transparent 30%,
        var(--color) 30%, var(--color) 32%, transparent 34%, transparent 36%,
        var(--color) 36%, var(--color) 38%, transparent 40%, transparent 42%,
        var(--color) 42%, var(--color) 44%, transparent 46%, transparent 48%,
        var(--color) 48%, var(--color) 50%, transparent 52%, transparent 54%,
        var(--color) 54%, var(--color) 56%, transparent 58%, transparent 60%,
        var(--color) 60%, var(--color) 62%, transparent 64%, transparent 66%,
        var(--color) 66%, var(--color) 68%, transparent 70%, transparent 72%,
        var(--color) 72%, var(--color) 74%, transparent 76%, transparent 78%,
        var(--color) 78%, var(--color) 80%, transparent 82%, transparent 84%,
        var(--color) 84%, var(--color) 86%, transparent 88%, transparent 90%,
        var(--color) 90%, var(--color) 92%, transparent 94%, transparent 96%,
        var(--color) 96%, var(--color) 98%, transparent 98%, transparent 100%
    );*/
    background: repeating-linear-gradient(-90deg,var(--color) 0,var(--color) 10px,transparent 35px,transparent 40px);

    /*border-radius: 15px;*/
    overflow: hidden;
    position: absolute;
    background-size: 200% 100%;
    animation: Gradient var(--second) linear infinite; //使用fade-left动画
    -webkit-animation: Gradient var(--second) linear infinite;
    -moz-animation: Gradient var(--second) linear infinite;
    /*box-shadow: 0px 0px 17px 5px #72dffa; // 外发光*/
  }
  .line-block_false {
    width: 100%;
    height: var(--height);
    overflow: hidden;
    position: absolute;
    background-size: 200% 100%;
  }

  /* 指定使用Gradient动画，5s完成一次动画，匀速，无限循环 */
  /*.gradient {*/
  /*  -webkit-animation: Gradient 10s linear infinite;*/
  /*  -moz-animation: Gradient 1s linear infinite;*/
  /*}*/

  /* 定义Gradient动画效果：初始时显示最右端，结束时显示最左端（向右滚动） */
  @keyframes Gradient {
    0% {
      background-position: 100% 100%;
    }
    100% {
      background-position: 0% 100%;
    }
  }

  @-moz-keyframes Gradient {
    0% {
      background-position: 100% 100%;
    }
    100% {
      background-position: 0% 100%;
    }
  }
  @-webkit-keyframes Gradient {
    0% {
      background-position: 100% 100%;
    }
    100% {
      background-position: 0% 100%;
    }

    /*0% {*/
    /*  background-position: 100% 100%;*/
    /*  opacity: 0;*/
    /*}*/
    /*10% {*/
    /*  opacity: 1;*/
    /*}*/
    /*90% {*/
    /*  opacity: 1;*/
    /*}*/
    /*100% {*/
    /*  background-position: 0% 100%; // 到达终点时位置要减去自身的长度*/
    /*  opacity: 0;*/
    /*}*/
  }
</style>
