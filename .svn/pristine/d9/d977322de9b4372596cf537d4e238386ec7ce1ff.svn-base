<template>
  <div :id="container"></div>
</template>

<script>
/**
 * 高德地图组件
 */

import AMapLoader from '@amap/amap-jsapi-loader';

export default {
  name: 'amap',
  data() {
    return {
      container: 'container',
      gisScript: 'gis-script',
      key: '3b5479d9ad9f01d138fef5e70daed7bd',
      v: '2.0',
      map: undefined,
      zoom: 10 // 默认缩放级别

    }
  },
  created() {
  },
  mounted() {
    this.amapInit()
  },
  methods: {
    amapInit() {
      const vm = this
      AMapLoader.load({
        key: this.key,            // 申请好的Web端开发者Key，首次调用 load 时必填
        version: this.v,          // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
        plugins: [],              // 需要使用的的插件列表，如比例尺'AMap.Scale'等
        AMapUI: {                 // 是否加载 AMapUI，缺省不加载
          version: '1.1',         // AMapUI 缺省 1.1
          plugins:[],             // 需要加载的 AMapUI ui插件
        },
        Loca:{                    // 是否加载 Loca， 缺省不加载
          version: '2.0'          // Loca 版本，缺省 1.3.2
        },
      }).then((AMap)=>{
        const map = new AMap.Map(this.container, {
          zoom: this.zoom,
          mapStyle: "amap://styles/darkblue"
        });

        AMap.plugin("AMap.CitySearch", function () {
          var citySearch = new AMap.CitySearch()
          citySearch.getLocalCity(function (status, result) {
            // console.log(status, result)
            if (status === 'complete' && result.info === 'OK') {
              // 查询成功
              // console.log(result.city, '获取当前城市')
              //加载天气查询
              AMap.plugin('AMap.Weather', () => {
                //创建天气查询实例
                var weather = new AMap.Weather()
                //执行实时天气信息查询
                weather.getLive(result.city, (err, data) => {
                  // console.log(data)
                  // console.log(data.temperature, '温度')
                  // console.log(data.weather, '天气')
                })
              })
            }
          })
        });
        this.$emit('ready', map, this)
      }).catch(e => {
        console.log(e);
      })
    },
  }
}
</script>

<style scoped>

</style>
