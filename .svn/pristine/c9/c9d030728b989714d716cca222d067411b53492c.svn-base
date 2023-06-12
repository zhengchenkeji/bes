<template>
  <div class="app-container">
    <!-- 中间画布 -->
    <section >
      <designer-view/>
    </section>
  </div>
</template>

<script>
  import designerView from './designerView'
  import {mapState} from "vuex";
  import {setDefaultcomponentData} from "@/store/designer/snapshot";
  import { seleteDesignerAreaPage } from "@/api/designerConfigure/designer";
  import pubsub from "@/store/modules/PubSub";

  export default {
    components: {designerView},
    name: "index",
    computed: mapState([
      'componentData',
      "curComponent",
      'isClickComponent',
      'canvasStyleData',
      'editor',
    ]),
    data() {
      return {
        areaId: "1",//区域id
        pageId:"1",//页面id
        form: {}
      }
    },
    created() {
      // this.$store.commit('setToViewPage', true)

      this.areaId = this.$route.query.areaId;
      this.pageId = this.$route.query.pageId;
      this.form.id = parseInt(this.pageId);
      this.form.areaId = parseInt(this.areaId);
      // this.restore()
    },
    beforeDestroy() {
      // this.$store.commit('setToViewPage', false)
      // pubsub.remove(null,'VtextWebsocket')
      // pubsub.remove(null,'VLightWebsocket')
      // pubsub.remove(null,'VtextWebsocket')
      // pubsub.remove(null,'VtextWebsocket')
      // pubsub.remove(null,'VtextWebsocket')
    },
    methods: {

      restore() {
        // 用保存的数据恢复画布
        if (localStorage.getItem('canvasData')) {
          setDefaultcomponentData(JSON.parse(localStorage.getItem('canvasData')))
          this.$store.commit('setComponentData', JSON.parse(localStorage.getItem('canvasData')))
        }

        if (localStorage.getItem('canvasStyle')) {
          this.$store.commit('setCanvasStyle', JSON.parse(localStorage.getItem('canvasStyle')))
        }
      },
    },
  }
</script>

<style scoped>

</style>
