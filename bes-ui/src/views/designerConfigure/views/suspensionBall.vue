<template>
  <div ref="dragArea" class="drag-area">
    <el-tooltip effect="dark" content="返回主界面" placement="top">
      <el-button size="mini" icon="el-icon-back" circle @click="back"></el-button>
    </el-tooltip>

<!--    <el-tooltip effect="dark" content="刷新数据" placement="top">-->
<!--      &lt;!&ndash;<el-button size="mini" icon="el-icon-refresh-right" circle @click="screenfullDiv"></el-button>&ndash;&gt;-->
<!--      <el-button size="mini" icon="el-icon-refresh-right" circle @click="refreshPage"></el-button>-->
<!--    </el-tooltip>-->
  </div>
</template>

<script>

  import screenfull from "screenfull";

  export default {
    methods: {
      /**
       * 返回主界面
       */
      callback() {
        this.$router.push("/index");
        console.log("返回主界面");
      },

      /**
       * 分享页面
       */
      shareLink() {
        console.log("分享页面");
      },

      back() {
        this.$emit('back');
      },
      screenfullDiv() {
        this.$emit('screenfullDiv');
        // if (screenfull.isEnabled) {
        //   screenfull.toggle(this.$refs.page);
        // }
      },
      //刷新数据
      refreshPage() {
        this.$emit('refreshPage');
      }
    },
    mounted() {
      /**
       * 监听悬浮拖拽区域
       */
      this.$nextTick(() => {
        // 获取DOM元素
        let dragArea = this.$refs.dragArea;
        // 缓存 clientX clientY 的对象: 用于判断是点击事件还是移动事件
        let clientOffset = {};
        // 绑定鼠标按下事件
        dragArea.addEventListener("mousedown", (event) => {
          let offsetX = dragArea.getBoundingClientRect().left; // 获取当前的x轴距离
          let offsetY = dragArea.getBoundingClientRect().top; // 获取当前的y轴距离
          let innerX = event.clientX - offsetX; // 获取鼠标在方块内的x轴距
          let innerY = event.clientY - offsetY; // 获取鼠标在方块内的y轴距
          console.log(offsetX, offsetY, innerX, innerY);
          // 缓存 clientX clientY
          clientOffset.clientX = event.clientX;
          clientOffset.clientY = event.clientY;
          // 鼠标移动的时候不停的修改div的left和top值
          document.onmousemove = function(event) {
            dragArea.style.left = event.clientX - innerX + "px";
            dragArea.style.top = event.clientY - innerY + "px";
            // dragArea 距离顶部的距离
            let dragAreaTop = window.innerHeight - dragArea.getBoundingClientRect().height;
            // dragArea 距离左部的距离
            let dragAreaLeft = window.innerWidth - dragArea.getBoundingClientRect().width;
            // 边界判断处理
            // 1、设置左右不能动
            dragArea.style.left = dragAreaLeft - 30 + "px";
            // 2、超出顶部处理
            if (dragArea.getBoundingClientRect().top <= 0) {
              dragArea.style.top = "0px";
            }
            // 3、超出底部处理
            if (dragArea.getBoundingClientRect().top >= dragAreaTop) {
              dragArea.style.top = dragAreaTop + "px";
            }
          };
          // 鼠标抬起时，清除绑定在文档上的mousemove和mouseup事件；否则鼠标抬起后还可以继续拖拽方块
          document.onmouseup = function() {
            document.onmousemove = null;
            document.onmouseup = null;
          };
        }, false);
        // 绑定鼠标松开事件
        dragArea.addEventListener('mouseup', (event) => {
          let clientX = event.clientX;
          let clientY = event.clientY;
          if (clientX === clientOffset.clientX && clientY === clientOffset.clientY) {
            console.log('click 事件');
          } else {
            console.log('drag 事件');
          }
        })
      });
    },
  };
</script>
<style lang="scss">
  .drag-area {
    position: fixed;
    right: 30px;
    top: 80%;
    z-index: 6666;
    padding: 13px;
    width: 3vw;
    opacity: 1;
    background-color: rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    box-shadow: 0px 2px 15px 0px rgba(9,41,77,0.15);
    cursor: move;
    user-select: none;
    text-align: center;
  }
</style>


