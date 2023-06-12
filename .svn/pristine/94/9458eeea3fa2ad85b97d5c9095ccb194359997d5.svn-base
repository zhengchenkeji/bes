<template>
    <div :id="ring"></div>
</template>
<script>
    import * as echarts from 'echarts';
    export default {
      name: 'ring',
      data(){
        return {
          ring: 'ring',
        }
      },
      mounted() {
        this.echartsInit();
      },
      methods: {
        echartsInit(){
          var chartDom = document.getElementById('ring');
          var myChart = echarts.init(chartDom);
          var option;
          option = {
            title:{
                text: '总数',
                x: 'center',
                y: '60',
                textStyle:{
                    color: '#ffffff'
                }
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
            series: [
                {
                    name: 'Access From',
                    type: 'pie',
                    radius: ['40%','70%'],
                    center: ['50%','40%'],
                    avoidLabelOverlap: false,
                    labelLine: {
                        show: true
                    },
                    data: [
                        { value: 35, name: '类型一' },
                        { value: 65, name: '类型二' },
                    ]
                }
            ]
            
          };
          option && myChart.setOption(option);
        }
      },
    }
</script>
