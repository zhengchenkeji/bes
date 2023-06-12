<template>
  <!-- license 用户授权对话框 -->
  <el-dialog
    title="用户授权"
    :visible.sync="dialogVisible"
    width="500px"
    @close="handleClose"
    @open="handleOpen"
    append-to-body>
    <el-form ref="licenseForm" :model="licenseForm" :rules="licenseRules" label-width="80px">
      <el-form-item label="申请码" prop="identityCode">
        <el-input v-loading="licenseLoading" v-model="licenseForm.identityCode" type="textarea" readonly/>
      </el-form-item>
      <el-form-item label="授权码" prop="authCode">
        <el-input v-model="licenseForm.authCode" placeholder="请输入授权码" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="handleAuthorization">授 权</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { authentication, getIdentityCode } from '@/api/license'

export default {
  name: 'License',
  data() {
    return {
      licenseForm: {
        identityCode: undefined, // 申请码
        authCode: undefined // 授权码
      },
      licenseRules: {
        authCode: { required: true, trigger: "blur", message: "请输入授权码" }
      },
      loading: false,
      licenseLoading: false,
      dialogVisible: false
    };
  },
  methods: {

    /**
     * license 授权
     */
    handleAuthorization() {
      this.$refs["licenseForm"].validate(valid => {
        if (valid) {
          // 授权处理
          authentication(this.licenseForm).then(({ data }) => {
            if (data) {
              this.$modal.msgSuccess("授权成功")
              this.dialogVisible = false
              this.$emit('success')
            }else {
              this.$modal.msgWarning("授权失败")
              this.licenseForm.authCode = undefined
            }
          });
        }
      });
    },

    /**
     * 模态框关闭事件处理
     */
    handleClose() {
      this.$refs["licenseForm"].resetFields()
    },
    /**
     * 模态框打开事件处理
     * 获取申请码
     */
    handleOpen() {
      this.licenseLoading = true
      getIdentityCode().then(({ msg }) => {
        if (msg) {
          this.licenseLoading = false
          this.licenseForm.identityCode = msg
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
