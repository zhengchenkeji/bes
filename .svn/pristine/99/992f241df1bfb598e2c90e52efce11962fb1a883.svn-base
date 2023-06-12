<template>
    <div :id="histogram"></div>
</template>
<script>
    import * as echarts from 'echarts';
    export default {
      name: 'histogram',
      data(){
        return {
          histogram: 'histogram',
        }
      },
      mounted() {
        this.echartsInit();
      },
      methods: {
        echartsInit(){
          var chartDom = document.getElementById('histogram');
          var myChart = echarts.init(chartDom);
          var option;
          option = {
            textStyle: {
              color: '#ffffff',
            },
            tooltip: {
                trigger: 'item',
            },
            legend: {
                bottom: '10',
                align:'auto',
                textStyle:{
                color:'#ffffff'
                }
            },
            xAxis: {
                type: 'category',
                data: ['16年', '17年', '18年', '19年', '20年']
            },
            yAxis: {
                type: 'value',
                axisLabel: {
                formatter: ''
              }
            },
            series: [
                {
                data: [120, 200, 150, 80, 70, 110, 130],
                type: 'bar'
                }
            ]
          };
          option && myChart.setOption(option);
        }
      },
    }
</script>
