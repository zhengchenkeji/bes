<script>
  import eventBus from '@/utils/designer/eventBus'

  export default {
    props: {
      linkage: {
        type: Object,
        default: () => {
        },
      },
      element: {
        type: Object,
        default: () => {
        },
      },
    },
    created() {
      if (this.linkage?.data?.length) {
        eventBus.$on('v-click', this.onClick)
        eventBus.$on('v-hover', this.onHover)

        //遍历所有的组件

      }


    },
    mounted() {
      const {data, duration} = this.linkage || {}
      if (data?.length) {
        this.$el.style.transition = `all ${duration}s`
      }
    },
    beforeDestroy() {
      if (this.linkage?.data?.length) {
        eventBus.$off('v-click', this.onClick)
        eventBus.$off('v-hover', this.onHover)
      }
    },
    methods: {
      changeStyle(data = []) {
        data.forEach(item => {
          item.style.forEach(e => {
            if (e.key) {
              this.element.style[e.key] = e.value
            }
          })
        })
      },

      openInout(componentId,data) {

        if (data.id == componentId && data.component == "Light") {
          this.$emit('lightOpenInout',data)
        }
      },

      onClick(componentId) {
        const data = this.linkage.data.filter(item => item.id === componentId && item.event === 'v-click')
        this.changeStyle(data)
        //传递父组件,打开相应弹窗
        // this.openInout(componentId,this.element)
      },

      onHover(componentId) {
        const data = this.linkage.data.filter(item => item.id === componentId && item.event === 'v-hover')
        this.changeStyle(data)
      },
    },
  }
</script>
