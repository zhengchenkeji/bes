<template>
  <div class="content">
    <svg
      version="1.1"
      baseProfile="full"
      xmlns="http://www.w3.org/2000/svg"
      class="topo"
      id="svg"
      ondragover="return false"
      oncontextmenu="return true"
    >
      <!-- 已连接的线 -->
      <line
        v-for="(item) in lines"
        :key="item.x"
        :x1="item.x1" :y1="item.y1"
        :x2="item.x2" :y2="item.y2"
        style="stroke:rgb(214,214,218);stroke-width:2"/>

      <g
        v-for="(item, index) in topoNodes"
        :key="item.id"
        @mousedown.left.stop.prevent="moveAndLink(index, $event)"
      >
        <image :xlink:href="item.symbol" width="50px" height="50px" :x="item.x" :y="item.y"></image>
        <text :x="item.x + 25" :y="item.y + 66" style="text-anchor: middle; user-select: none;">
          {{item.name}}
        </text>
      </g>
    </svg>
  </div>

</template>

<script>
  import OnEvent from '../common/OnEvent'

  export default {
    extends: OnEvent,
    data() {
      return {
        res: {
          code: 200,
          data: [{
            name: "default",
            devices: [
              {
                id: "3",
                name: "Router",
                ip: "169.254.200.2",
                type: "router",
                x: 400,
                y: 50
              },
              {
                id: "1",
                name: "Linux",
                ip: "192.168.67.101",
                type: "server",
                x: 52,
                y: 500
              },
              {
                id: "5",
                name: "Winserver",
                ip: "192.168.67.200",
                type: "server",
                x: 500,
                y: 500
              },
              {
                id: "4",
                name: "SW",
                ip: "192.168.67.201",
                type: "switch",
                x: 200,
                y: 200
              }
            ],
            relation: [
              {source: "3", target: "4", network: "Net-CSRiface_1"},//连线——————source:起点，target（目标）：终点
              {source: "1", target: "4", network: "Net-SWiface_16"},
              {source: "5", target: "4", network: "Net-R4iface_0"}
            ]
          }]
        },
        topoNodes: [], // topo图中的节点
        topoLinks: [], // topo图中的连线
        isMove: true,// 操作模式，默认为移动。可切换为连接模式
        positions: [],//更改的位置
        token: null
      }
    },
    computed: {
      // 动态计算节点间的连线
      lines() {
        let hash = {}
        const OFFSET = 20
        this.topoNodes.forEach((item, index) => {
          hash[item.id] = index
        })
        /*
            hash:{
                1: 1
                3: 0
                4: 3
                5: 2
             },

             source:3 1 5,
             target:4 4 4
         */
        return this.topoLinks.map(item => {
          const startNode = this.topoNodes[hash[Number(item.source)]]
          const endNode = this.topoNodes[hash[Number(item.target)]]
          return {
            x1: startNode.x + OFFSET,
            y1: startNode.y + OFFSET,
            x2: endNode.x + OFFSET,
            y2: endNode.y + OFFSET,
          }
        })
      }
    },
    created() {
      this.token = sessionStorage.getItem("token");
      this.getData();
    },
    methods: {
      getData() {
//使用模拟数据
        this.topoNodes = this.res.data[0].devices;
        this.topoLinks=this.res.data[0].relation;
        for (let item of this.topoNodes) {
            item.symbol = require(`@/assets/icons/svg/bug.svg`);
        }
        for (let item of this.topoLinks) {
            item.source = Number(item.source);
            item.target=Number(item.target);
        }
      },

      //移动事件
      moveAndLink(index, e) {
        // 判断当前模式
        if (this.isMove) {
          // 移动模式
          const layerX = e.layerX - this.topoNodes[index].x;
          const layerY = e.layerY - this.topoNodes[index].y;

          //实时获取更新后的坐标
          document.onmousemove = (e) => {
            this.topoNodes[index].x = e.layerX - layerX;
            this.topoNodes[index].y = e.layerY - layerY;
          }
          //将新坐标存进数据库
          //如果使用模拟数据，删除该方法
          document.onmouseup = () => {
            this.positions = [];
            for (let j = 0; j < this.topoNodes.length; j++) {
              this.positions.push({
                'ip': this.topoNodes[j].ip,
                'x': this.topoNodes[j].x,
                'y': this.topoNodes[j].y
              })
            }
            document.onmousemove = null
            document.onmouseup = null
          }

        } else {
          document.onmousemove = null // 重置鼠标移动事件
          this.isMove = true // 重置为移动模式
        }
      },

    }
  }
</script>

<style lang="scss" scoped>
  .content {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .topo {
    width: 1070px;
    height: 600px;
  }
  .line-shape {
    width: 100%;
    height: 100%;
    overflow: auto;
  }
</style>
