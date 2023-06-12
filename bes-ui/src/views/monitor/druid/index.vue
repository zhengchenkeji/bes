<template>
  <i-frame :src="url" />
</template>
<script>
import iFrame from "@/components/iFrame";
export default {
  name: "Druid",
  components: { iFrame },
  data() {
    return {
      url: process.env.VUE_APP_BASE_API + "/druid/login.html"
    };
  },
};
</script>
