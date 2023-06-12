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
          xData:['01','02','03','04','05','06','07','08','09','10','11','12'], //x坐标轴
          xUnit:'时', //x轴单位
          thisData: [], //数据
          lastData: [], //数据
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
        legend: {
          icon: "roundRect",
          right: 'center',
          data: that.data.legend,
        },
          grid: {
              top: '14%',
              left: '4%',
              right: '4%',
              bottom: '5%',
              containLabel: true
          },
          tooltip: {
             trigger: 'axis',
          },
          xAxis: {
            name:that.data.xUnit,
            data: that.data.xData,
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
                  name:'本期',
                  type: "bar",
                  barMaxWidth: 24,
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
                                  color: '#1388ac',
                                  fontSize: 12,
                              },
                          },
                          color:'#1388ac',
                      },
                  },
                  data: that.data.thisData,
              },{
                  name:'同期',
                  type: "bar",
                  barMaxWidth: 24,
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
                                  color: '#4a74ff',
                                  fontSize: 12,
                              },
                          },
                          color:'#4a74ff',
                      },
                  },
                  data: that.data.lastData,
              },
          ],
      })
    }
  }
}
</script>

<style scoped>

</style>
