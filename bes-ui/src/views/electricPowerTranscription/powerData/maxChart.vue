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
          maxInfo: [], //最大值
          minInfo:[], //最小值
          avgInfo:[], //平均值
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
        color:['#1bc0bf','#f4e19a','#FFF4E19A'],
        legend: {
          icon: "circle",
          right: '2%',
          data: ['最大值','最小值','平均值'],
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
          name:'日',
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
            name: '最大值',
            type: 'line',
            symbol: "none", //坐标点
            smooth:true,
            data: that.data.maxInfo,
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
            name: '最小值',
            type: 'line',
            symbol: "none", //坐标点
            smooth:true,
            data: that.data.minInfo,
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
            name: '平均值',
            type: 'line',
            symbol: "none", //坐标点
            smooth:true,
            data: that.data.avgInfo,
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
