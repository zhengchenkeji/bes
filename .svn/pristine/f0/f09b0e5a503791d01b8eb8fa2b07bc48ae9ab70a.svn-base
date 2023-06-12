<template>
  <div :class="className" :style="{height:height,width:width}" ></div>
</template>

<script>
import * as echarts from 'echarts';
export default {
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '100%'
    },
    data: {
      type: Object,
      default: function () {
        return {
          xData:[], //x轴数据
          yesterday: [], //昨日数据
          today:[], //今日数据
        }
      }
    }
  },
  watch:{
    data:{
      deep: true,
      handler:function(newV,oldV){
        this.setOption();
      }
    }
  },
  data() {
    return {
      chart: null,
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el)
      this.setOption();
    },
    setOption(){
        var that = this
      this.chart.setOption({

        tooltip: {
          trigger: 'axis'
        },
        color:['#1bc0bf','#c59af4'],
        legend: {
          icon: "circle",
          right: '2%',
          data: ['昨日','当日'],
        },
        grid: {
          top: '14%',
          left: '4%',
          right: '4%',
          bottom: '2%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          name:'时',
          boundaryGap: true,
          splitLine: {
            show: true,
            lineStyle: {
              type:'dashed',//类型 'solid'实线, 'dotted'点型虚线, 'dashed'线性虚线
              color: "#e5e5e5",
            },
          },
          data: that.data.xData
        },
        yAxis: {
          name:'kW·h',
          nameTextStyle: {
            color: "black",
            fontSize: '12px',
            show:true
          },
          axisLine: {
            lineStyle: {
              type:'dashed',//类型 'solid'实线, 'dotted'点型虚线, 'dashed'线性虚线
              color: "#e5e5e5",
            },
            show:true,
          },
          axisLabel: {
            color: "black",
            fontSize: '12px',
          },
          splitLine: {
            show: true,
            lineStyle: {
              type:'dashed',//类型 'solid'实线, 'dotted'点型虚线, 'dashed'线性虚线
              color: "#e5e5e5",
            },
          },
        },
        series: [
          {
            name: '昨日',
            type: 'line',
            symbol: "none", //坐标点
            smooth:true,
            data: that.data.yesterday,
            markPoint: {
              data: [
                { type: 'max', name: 'Max' },
                { type: 'min', name: 'Min' }
              ]
            },
            markLine: {
              data: [{ type: 'average', name: 'Avg' }],
              label: {position: "insideEndTop"},
            }
          },
          {
            name: '当日',
            type: 'line',
            symbol: "none", //坐标点
            smooth:true,
            data: that.data.today,
            markPoint: {
              data: [
                { type: 'max', name: 'Max' },
                { type: 'min', name: 'Min' }
              ]
            },
            markLine: {
              data: [
                { type: 'average', name: 'Avg' },
              ],
              label: {position: "insideEndTop"},
            }
          }
        ]
      })
    }
  }
}
</script>

<style scoped>

</style>
