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
            yData:[], //y轴数据
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

          color: ["#3398DB"],

          tooltip: {
            trigger: "axis",
            axisPointer: {
              type: "shadow",
            },
          },
          legend: {
            data: that.data.yData,
          },
          grid: {
            left: "4%",
            right: "4%",
            bottom: "2%",
            top:"14%",
            containLabel: true,
          },
          xAxis: {
            type: "value",
          },
          yAxis: {
            type: "category",
            inverse: true,
            data: that.data.yData,
          },
          series: [
            {
              name:'能耗',
              type: "bar",
              data: that.data.xData,
              itemStyle: {
                normal: {
                  barBorderRadius: 30,
                  color: function(params) {
                    return new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
                      offset: 0,
                      color: 'rgb(57,89,255)' // 0% 处的颜色
                    },
                      {
                        offset: 1,
                        color: 'rgb(46,200,207)' // 100% 处的颜色
                      }
                    ])
                  }
                }
              },
              barMaxWidth:20,
            },
          ],
        })
      }
    }
  }
</script>

<style scoped>

</style>
