<template>
  <div class="login">
    <el-row class="login-form">
      <el-col :span="13">
        <div style="width: 1px; height: 1px"></div>
      </el-col>
      <el-col :span="11">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
          <h3 class="title">{{title}}</h3>
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="账号"
            >
              <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="密码"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
            </el-input>
          </el-form-item>

          <el-form-item prop="code" v-if="captchaInput">
            <el-input
              v-model="loginForm.code"
              auto-complete="off"
              placeholder="验证码"
              style="width: 50%"
              @keyup.enter.native="handleLogin"
            >
              <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
            </el-input>
            <div class="login-code">
              <img :src="codeUrl" @click="getCode" class="login-code-img"/>
            </div>
          </el-form-item>

          <Verify
            @success="captchaCheckSuccess"
            :mode="'pop'"
            :captchaType="'blockPuzzle'"
            :imgSize="{ width: '330px', height: '155px' }"
            ref="verify"
          ></Verify>
          <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
          <el-form-item style="width:100%;">
            <el-button
              :loading="loading"
              size="medium"
              type="primary"
              style="width:100%;"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登 录 中...</span>
            </el-button>
            <div style="float: right;" v-if="register">
              <router-link class="link-type" :to="'/register'">立即注册</router-link>
            </div>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>

    <!-- license 授权验证 -->
    <license
      @success="licenseCheckSuccess"
      ref="license"
    />

    <!--  底部  -->
    <div class="el-login-footer">
    <!--<span>Copyright © 2018-2022 ruoyi.vip All Rights Reserved.</span>-->
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'
import Verify from "@/components/Verifition/Verify"
import License from "@/components/License"
import { getCaptchaConfig, getCodeImg } from '@/api/login'

export default {
  components: {
    Verify,
    License
  },
  name: "Login",
  data() {
    return {
      title: '',// 系统标题
      cookiePassword: "",
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关(后端获取开关信息)
      captchaOnOff: undefined,
      captchaInput: false,
      // 验证码类型
      captchaType: undefined,
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCookie();
    this.getCaptchaConfig();
    this.title = systemConfig.title(systemConfig.systemType);
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaOnOff = res.captchaOnOff === undefined ? true : res.captchaOnOff;
        if (this.captchaOnOff) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    // 获取验证码是否开启
    getCaptchaConfig(){
      getCaptchaConfig().then(response => {
        this.captchaOnOff = response.captchaOnOff;
        this.captchaType = response.captchaType;
        if ('behavioral' !== this.captchaType && this.captchaOnOff) {
          this.getCode();
          this.captchaInput = true;
        }
      })
    },
    // 验证码
    captchaCheckSuccess(params) {
      this.loginForm.code = params.captchaVerification;
      this.loading = true;

      if (this.loginForm.rememberMe) {
        Cookies.set("username", this.loginForm.username, { expires: 30 });
        Cookies.set("password", encrypt(this.loginForm.password), { expires: 30, });
        Cookies.set("rememberMe", this.loginForm.rememberMe, { expires: 30 });
      } else {
        Cookies.remove("username");
        Cookies.remove("password");
        Cookies.remove("rememberMe");
      }
      this.loginRequest();
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          if(this.captchaOnOff && 'behavioral' === this.captchaType){
            this.$refs.verify.show();
          }else{
            this.loginRequest();
          }
        }
      });
    },
    /** 登录请求 */
    loginRequest() {
      this.$store.dispatch("Login", this.loginForm).then(() => {
        this.$router.push({ path: this.redirect || "/" }).catch(() => {});
      }).catch((code) => {
        if (code === 4011 || code === 4031) {
          this.$refs.license.dialogVisible = true
        }
        this.loading = false;
      });
    },
    /** license检测成功 */
    licenseCheckSuccess() {
      this.loginRequest();
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  background-image: url("../assets/images/login-background-form.jpg");
  border-radius: 6px;
  //background: #ffffff;
  width: 540px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 38px;
    input {
      height: 38px;
      background-color:white!important;
      color:#000!important;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.login-code {
  //width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.login-code-img {
  height: 38px;
}
</style>
