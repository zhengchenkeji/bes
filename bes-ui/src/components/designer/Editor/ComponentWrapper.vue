<template>
  <div @click="onClick" @mouseenter="onMouseEnter">
    <component
      :is="config.component"
      v-if="config.component.startsWith('SVG')"
      ref="component"
      class="component"
      :style="getSVGStyle(config.style)"
      :prop-value="config.propValue"
      :element="config"
      :request="config.request"
      :linkage="config.linkage"
    />
    <component
      :key="(config.id + keyValue)"
      :keyValue="keyValue"
      :is="config.component"
      v-else
      ref="component"
      class="component"
      :style="getStyle(config.style)"
      :styles="getStyle(config.style)"
      :prop-value="config.propValue"
      :element="config"
      :request="config.request"
      :linkage="config.linkage"
    />
  </div>

</template>

<script>
  import {getStyle, getSVGStyle} from '@/utils/designer/style'
  import runAnimation from '@/utils/designer/runAnimation'
  import {mixins} from '@/utils/designer/events'
  import eventBus from '@/utils/designer/eventBus'

  export default {
    mixins: [mixins],
    props: {
      config: {
        type: Object,
        required: true,
        default: () => {
        },
      },
      keyValue: {
        type: Object,
        required: true,
        default: () => {
        },
      }
    },
    mounted() {
      runAnimation(this.$refs.component.$el, this.config.animations)
    },
    methods: {
      getStyle,
      getSVGStyle,

      onClick() {
        const events = this.config.events
        Object.keys(events).forEach(event => {
          this[event](events[event])
        })

        eventBus.$emit('v-click', this.config.id)
      },

      onMouseEnter() {
        eventBus.$emit('v-hover', this.config.id)
      },
    },
  }
</script>

<style lang="scss" scoped>
  .component {
    position: absolute;
  }
</style>
