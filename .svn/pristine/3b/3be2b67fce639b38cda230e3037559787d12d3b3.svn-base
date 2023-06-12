<template>
    <div :id="piev"></div>
</template>


<script>
    import * as echarts from 'echarts';
    export default {
        name: 'piev3',
        data(){
            return {
                piev: 'piev3',
            }
        },
        mounted() {
            this.echartsInit();
        },
        methods: {

            echartsInit(){
                var chartDom = document.getElementById('piev3');
                var myChart = echarts.init(chartDom);
                var option;
                var option = {
                    tooltip: {
                        trigger: 'item'
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
                        radius: '50%',
                        center: ['50%','40%'],
                        data: [
                            { value: 35, name: '类型一' },
                            { value: 65, name: '类型二' },
                        ],
                        emphasis: {
                            itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                        }
                    ]
                }
                option && myChart.setOption(option);
            }
        },
    }
</script>
