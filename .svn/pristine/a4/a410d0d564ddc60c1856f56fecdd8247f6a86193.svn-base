<template>
  <div :id="container"></div>
</template>

<script>
/**
 * 百度地图组件
 */
export default {
  name: 'bmap',
  data() {
    return {
      container: 'container',
      gisScript: 'gis-script',
      ak: 'VEHFLSQGMr6bC4784I5mixrFz6ecFNze',
      v: '1.0',
      map: undefined,
      zoom: 10 // 默认缩放级别
    }
  },
  created() {
  },
  mounted() {
    this.loadJScript()
  },
  methods: {
    loadJScript() {
      const gisScript = document.getElementById(this.gisScript)
      if (gisScript) {
        this.bmapInit()
        return
      }

      const script = document.createElement('script')
      script.type = 'text/javascript'
      script.id = this.gisScript
      script.src = `//api.map.baidu.com/api?type=webgl&v=${this.v}&ak=${this.ak}&callback=bmapInit`
      document.body.appendChild(script)
      window.bmapInit = ()=> this.bmapInit()

    },
    bmapInit() {

      const map = new BMapGL.Map(this.container); // 创建Map实例

      this.map = map

      // 获取地图容器大小
      const containerSize = map.getContainerSize()

      if (!containerSize.height || !containerSize.width) {
        console.warn('地图容器尺寸不能为 0！')
      }

      this.$emit('ready', map, this)

      map.enableScrollWheelZoom() // 启用滚轮放大缩小
      const zoom = map.getZoom()
      if (!zoom) {
        this.setCenterAndZoomByIp()
      }

    },
    /**
     * 设置中心点和级别根据ip
     */
    setCenterAndZoomByIp() {
      const localCity = new BMapGL.LocalCity()
      localCity.get((localCityResult) => {
        const point = new BMapGL.Point(localCityResult.center.lng, localCityResult.center.lat); // 创建点坐标
        this.map.centerAndZoom(point, localCityResult.level)
      })

    },
    /**
     * 浏览器定位
     */
    setLocationByBrowser() {
      const geolocation = new BMapGL.Geolocation()
      geolocation.getCurrentPosition((geolocationResult)=> {
        if (geolocationResult) {
          const point = new BMapGL.Point(geolocationResult.point.lng, geolocationResult.point.lat); // 创建点坐标
          this.map.panTo(point)
        }
      })
    }
  }
}
</script>

<style scoped>
/*隐藏百度地图商标logo*/
>>>.anchorBL{
  display: none;
}
</style>
