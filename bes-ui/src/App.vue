<template>
  <div id="app">
    <router-view />
    <websocket />
  </div>

</template>

<script>
import websocket from '@/views/websocket'
export default  {
  name:  'App',
  components: {
    websocket
  },
  metaInfo() {
      return {
          // ${process.env.VUE_APP_TITLE}
          title: this.$store.state.settings.dynamicTitle && this.$store.state.settings.title,
          titleTemplate: title => {
              return title ? `${title} - ${systemConfig.title(systemConfig.systemType)}` : process.env.VUE_APP_TITLE
          }
      }
  }
}
</script>
