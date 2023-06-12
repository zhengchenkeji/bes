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
          name:'',  //支路名称
          xData:[], //x坐标轴
          xUnit:'', //x坐标轴单位(当日:时,当月:日,当年:月)
          yData: [] //数据
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
          /*title:{
              text:'单位：kWh',
              top:'1%',
              right:'8%',
              textStyle:{
                  fontSize: '0.085rem',
                  fontFamily: '思源黑体',
                  color: '#5c7081',
                  fontWeight:400
              }
          },*/
          grid: {
              top: '14%',
              left: '4%',
              right: '4%',
              bottom: '2%',
              containLabel: true
          },
          tooltip: {
             trigger: 'axis',
          },
          xAxis: {
              data: that.data.xData,
              name:that.data.xUnit,
              /*nameTextStyle: {
                color: "black",
                fontSize: '12px',
                show:true
              },
              axisLine: {
                  lineStyle: {
                      color: "black",
                  },
              },
              axisLabel: {
                  color: "black",
                  fontSize: '12px',
              },*/
              splitLine: {
                  show: true,
                  lineStyle: {
                      type:'dashed',//类型 'solid'实线, 'dotted'点型虚线, 'dashed'线性虚线
                      color: "#e5e5e5",
                  },
              },
          },
          yAxis: {
              name:'kW·h',
              /*nameTextStyle: {
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
              },*/
              splitLine: {
                  show: true,
                  lineStyle: {
                    type:'dashed',//类型 'solid'实线, 'dotted'点型虚线, 'dashed'线性虚线
                      color: "#e5e5e5",
                  },
              },
              // interval: 500,
          },
          series: [
              {
                  name:that.data.name,
                  type: "bar",
                  barWidth: 24,
                  markPoint: {
                    data: [
                      { type: 'max', name: 'Max' },
                      { type: 'min', name: 'Min' }
                    ]
                  },
                  itemStyle: {
                      normal: {
                          label: {
                              show: false, //开启显示
                              position: 'top', //在上方显示
                              textStyle: {
                                  //数值样式
                                  color: '#33cc99',
                                  fontSize: 12,
                              },
                          },
                          color:'#33cc99',
                      },
                  },
                  data: that.data.yData,
              },
          ],
      })
    }
  }
}
</script>

<style scoped>

</style>
