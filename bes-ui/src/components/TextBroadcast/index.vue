<template>
  <div>
    <audio id="audio" controls="true" autoplay="true" style="display: none;" :src="audioUrl" ref="audio"
      @ended="overAudio" @error="errorAudio"></audio>
    <!--    <el-alert style="width: max-content" :closable="false" v-show="alertShow" -->
    <!--              v-bind:title="alertTitle" show-icon -->
    <!--              type="warning" -->
    <!--              @close="hello"> -->
    <!--    </el-alert>-->
  </div>
</template>
<script>
import {
  getToken, getAudioEnvironment
} from "@/api/basicData/safetyWarning/alarmTactics/alarmTactics";

/**文字播报组件*/
export default {
  name: "TextBroadcast",

  props: {
    message: {
      type: String,
      default: ''
    },
    alertShow: {
      type: Boolean,
      default: false
    },
    token: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: ''
    },
    appkey: {
      type: String,
      default: ''
    }

  },
  watch: {
    message(newVal, oldVal) {
      
      // console.log("发生一次改变", "新值：" + newVal, "旧值：" + oldVal)
      // newVal是新值，oldVal是旧值
      if (!newVal) {
        return;
      }
      var data = newVal.split("$")[0];
      this.messagetext = data

      /**判断当前是否有正在播放,如果有直接插入，没有进入播放方法*/
      if (this.messageList.length == 0) {
        this.messageList.push(data)
        this.playVideo(data);
      } else {
        var audio = document.getElementById("audio");

        if (audio.paused) {
          this.playVideo();

        }
        this.messageList.push(data)
      }
    }
  },
  data() {
    return {
      //  接收父组件传入的消息
      messagetext: "",
      messageList: [],
      isplay: true,
      baidutoken: "25.ac8017e7634d8d27704ddf1572862136.315360000.1981596844.282335-28000130",
      albbtoken: "",
      audiotype: 0,
      alertTitle: "123",
      audioUrl: null,
    };
  },
  created() {
    // this.getAudioEnvironment();


  },
  methods: {
    errorAudio(){
      console.log("播放失败");
      this.overAudio()
    },
    overAudio() {
      if (this.messageList != null && this.messageList.length != 0) {
        this.messageList.shift();
        this.playVideo();
      }
    },
    getAudioEnvironment() {
      getAudioEnvironment().then(response => {
        this.audiotype = response.data.audioType;
        if (this.audiotype == 1) {
          this.getalbbToken();
        } else {
          this.baidutoken = response.data.baidutoken;
        }
      })
    },
    /**定时获取阿里巴巴token*/
    getalbbToken() {
      getToken().then(response => {
        setInterval(() => {
          getToken().then(response => {
            this.albbtoken = response.data
          })
        }, 60000 * 60)
        this.albbtoken = response.data
      })
    },
    /**播放语音*/
    playVideo() {
      
      if (this.messageList == null || this.messageList.length == 0) {
        return;
      }
      const text = this.messageList[0];

      let url = "";
      if (this.type == 32) {
        /**百度*/
        url = "http://tsn.baidu.com/text2audio?lan=zh&ctp=1&cuid=E4-54-E8-8D-5B-AE" +
          "&tok=" + this.token +
          "&per=0&vol=15&pit=6&tex=" +
          encodeURI(text);
      } else {
        /**阿里*/
        url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/tts?appkey=" + this.appkey +
          "&token=" + this.token +
          "&text=" + encodeURI(text) +
          "&format=wav&sample_rate=16000";
      }
      this.audioUrl = url;
      /**开始播放*/

      // this.$nextTicker(()=>{
      // this.$refs.audio.play()
      // this.$refs.audio.pause();
      // this.$refs.audio.load();
      // setTimeout(function() {
      try {

        this.$refs.audio.play();
      } catch (err) {
        console.log("语音播报时出现错误,错误描述："+err.message)
        console.log(this.audioUrl);
        this.overAudio()
      }
      // }, 200);

      // })

      if (this.alertShow) {
        this.$notify({
          title: '警告',
          message: text,
          type: 'warning'
        });
      }
    },

    playoneAudio(token, text, type, appkey) {
      let url = "";
      if (type == 32) {
        /**百度*/
        url = "http://tsn.baidu.com/text2audio?lan=zh&ctp=1&cuid=E4-54-E8-8D-5B-AE" +
          "&tok=" + token +
          "&per=0&vol=15&pit=6&tex=" +
          encodeURI(text);
      } else {
        /**阿里*/
        url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/tts?appkey=" + appkey +
          "&token=" + token +
          "&text=" + encodeURI(text) +
          "&format=wav&sample_rate=16000";
      }

      this.audioUrl = url;
      console.log(this.audioUrl);
      this.$refs.audio.play();
      if (this.alertShow) {
        this.$notify({
          title: '警告',
          message: text,
          type: 'warning'
        });
      }
    }
  }
};

</script>
<style></style>
