<template>
  <el-form ref="licenseForm" :model="licenseInfo" :rules="licenseRules" label-width="80px">
    <el-form-item label="申请码" prop="identityCode">
      <el-input v-loading="licenseLoading" v-model="licenseInfo.identityCode" type="textarea" readonly/>
    </el-form-item>
    <el-form-item label="授权码" prop="authCode">
      <el-input v-model="licenseInfo.authCode" placeholder="请输入授权码" />
    </el-form-item>
    <el-form-item label="授权日期" prop="authStartDate">
      <el-input v-model="licenseInfo.authStartDate" readonly/>
    </el-form-item>
    <el-form-item label="到期日期" prop="authEndDate">
      <el-input v-model="licenseInfo.authEndDate" readonly/>
    </el-form-item>
    <el-form-item label="有效天数" prop="effectiveDay">
      <el-input v-model="licenseInfo.effectiveDay" readonly/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <el-button type="danger" size="mini" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>

import { authorize } from '@/api/license'

export default {
  props: {
    licenseInfo: {
      type: Object
    }
  },
  data() {
    return {
      // 表单校验
      licenseRules: {
        authCode: { required: true, trigger: "blur", message: "请输入授权码" }
      },
      licenseLoading: false
    };
  },
  methods: {
    submit() {
      this.$refs["licenseForm"].validate(valid => {
        if (valid) {
          authorize(this.licenseInfo).then(({ data }) => {
            if (data) {
              this.$modal.msgSuccess("授权成功")
              this.$emit('refresh-data', true)
            }else {
              this.$modal.msgWarning("授权失败")
              this.licenseInfo.authCode = undefined
            }
          });
        }
      });
    },
    close() {
      this.$store.dispatch("tagsView/delView", this.$route);
      this.$router.push({ path: "/index" });
    }
  }
};
</script>
