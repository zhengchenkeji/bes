<template>
  <div :class="className" :style="{height:height,width:width}"></div>
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
            xData: [], //x轴数据
            paramData: [], //采集数据
          }
        }
      }
    },
    watch: {
      data: {
        deep: true,
        handler: function (newV, oldV) {
          let series = []//y轴数据
          let seriesName = []
          let seriesColor = ['#c04a1b', '#f4e19a', '#1B2560D7', '#9A3D91AF', '#AFB13AA5', '#1B3DD9AD', '#1BDC1950', '#9A3D91AF', '#9A3D91AF', '#9A3D91AF']
          let color = []
          if (this.data.paramData.length > 0) {
            this.data.paramData.forEach((iteam, index) => {
              this.data.xData = iteam.CJSJ
              seriesName.push(iteam.name)
              color.push(seriesColor[index])
              let seriesMap = {
                name: iteam.name,
                type: 'line',
                symbol: "none", //坐标点
                smooth: true,
                data: iteam.data,
                markPoint: {
                  data: [
                    {type: 'max', name: 'Max'},
                    {type: 'min', name: 'Min'}
                  ]
                },
                markLine: {
                  data: [
                    {type: 'average', name: 'Avg'},
                  ],
                  label: {position: "insideEndTop"},
                }
              }
              series.push(seriesMap)
            })
          }
          this.setOption(series, seriesName, color);
        }
      }
    }
    ,
    data() {
      return {
        chart: null,
      }
    }
    ,
    mounted() {
      this.$nextTick(() => {
        this.initChart()
      })
    }
    ,
    beforeDestroy() {
      if (!this.chart) {
        return
      }
      this.chart.dispose()
      this.chart = null
    }
    ,
    methods: {
      initChart() {
        this.chart = echarts.init(this.$el)
        this.setOption();
      }
      ,
      setOption(array, seriesName, color) {
        var that = this
        this.chart.setOption({

          tooltip: {
            trigger: 'axis'
          },
          color: color,
          legend: {
            icon: "circle",
            right: '2%',
            data: seriesName,
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
            name: '时间',
            boundaryGap: true,
            splitLine: {
              show: true,
              lineStyle: {
                type: 'dashed',//类型 'solid'实线, 'dotted'点型虚线, 'dashed'线性虚线
                color: "#e5e5e5",
              },
            },
            data: that.data.xData
          },
          yAxis: {
            name: 'kW·h',
            nameTextStyle: {
              color: "black",
              fontSize: '12px',
              show: true
            },
            axisLine: {
              lineStyle: {
                type: 'dashed',//类型 'solid'实线, 'dotted'点型虚线, 'dashed'线性虚线
                color: "#e5e5e5",
              },
              show: true,
            },
            axisLabel: {
              color: "black",
              fontSize: '12px',
            },
            splitLine: {
              show: true,
              lineStyle: {
                type: 'dashed',//类型 'solid'实线, 'dotted'点型虚线, 'dashed'线性虚线
                color: "#e5e5e5",
              },
            },
          },
          series: array
        }, true)
      }
    }
  }
</script>

<style scoped>

</style>
