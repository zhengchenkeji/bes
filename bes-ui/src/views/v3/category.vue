<template>
    <div :id="category"></div>
</template>


<script>
    import * as echarts from 'echarts';
    export default {
      name: 'category',
      data(){
        return {
          category: 'category',
        }
      },
      mounted() {
        this.echartsInit();
      },
      methods: {
        echartsInit(){
          var chartDom = document.getElementById('category');
          var myChart = echarts.init(chartDom);
          var option;
          option = {
            textStyle: {
              color: '#ffffff',
            },
            tooltip: {
              trigger: 'axis'
            },
            legend: {
              align: "auto",
              bottom:'5%',
              textStyle:{
                color:'#ffffff'
              }
            },
            xAxis: {
              name:'2020',
              type: 'category',
              boundaryGap: false,
              data: ['一月', '二月', '三月', '四月', '五月', '六月', '七月']
            },
            yAxis: {
              type: 'value',
              axisLabel: {
                formatter: ''
              }
            },
            series: [
              {
                name:'2020',
                type: 'line',
                data: [10, 11, 19, 11, 12, 12, 9],
                
                color: '#f8b118'
              },
              {
                name:'2021',
                type: 'line',
                data: [1, -2, 6, 5, 3, 2, 0],
                color: '#3ccbf1'
              }
            ]
          };
          option && myChart.setOption(option);
        }
      },
    }
</script>
