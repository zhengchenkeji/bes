<template>
  <div class="white-body-view">
    <el-form :inline="true" ref="updateForm" :model="updateForm" :rules="rules" label-width="80px">
      <el-form-item label="系统名称" prop="sysName" style="width: 48%;">
        <el-input v-model="updateForm.sysName" disabled style="width: 250px"></el-input>
      </el-form-item>
      <el-form-item label="别名" prop="alias" style="width: 48%;">
        <el-input v-model="updateForm.alias" :maxlength="24" show-word-limit type="text"
                  style="width: 250px"></el-input>
      </el-form-item>

      <el-form-item label="端口" prop="port" style="width: 48%;">
        <el-select v-model="updateForm.port" style="width: 250px">
          <el-option value="0"/>
          <el-option value="1"/>
          <el-option value="2"/>
          <el-option value="3"/>
          <el-option value="4"/>
          <el-option value="5"/>
        </el-select>
      </el-form-item>
    </el-form>
    <div style="text-align: center">
      <el-button
        v-hasPermi="['deviceTree:bus:edit']"
        type="primary" @click="submitForm('updateForm')" :disabled="saveBoolean">保存
      </el-button>
    </div>

    <!-- 新增总线 -->
    <el-drawer
      size='35%'
      title="新增总线"
      :visible.sync="visible"
      :before-close="handleClose"
      direction="rtl"
    >
      <div style="margin-left: 15px; margin-right: 15px">
        <div style="margin-bottom: 15px;">
          <!-- 选择下拉框和搜索 -->
          <el-row :span="10" :xs="24">
            <el-col>
              <el-form :inline="true" ref="addForm" :model="form" :rules="rules" label-width="90px">
                <el-form-item label="别名" prop="alias">
                  <el-input v-model="form.alias" :maxlength="24" show-word-limit type="text"></el-input>
                </el-form-item>

                <el-form-item label="端口" prop="port">
                  <el-select v-model="form.port">
                    <el-option value="0"/>
                    <el-option value="1"/>
                    <el-option value="2"/>
                    <el-option value="3"/>
                    <el-option value="4"/>
                    <el-option value="5"/>
                  </el-select>
                </el-form-item>
              </el-form>
              <el-form :inline="true" label-width="80px" style="text-align: center">

                <el-form-item>
                  <el-button
                    v-hasPermi="['deviceTree:bus:add']"
                    type="primary" @click="submitForm('addForm')">新增
                  </el-button>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
  import {addBus, updateBus} from '@/api/basicData/deviceManagement/deviceTree/deviceTree'

  export default {
    components: {},
    props: {
      treeNodeMsg: {
        type: Object,
        default: {
          alias: '',//别名
          port: '',//端口
          deviceNodeId: '4',//所属节点类
          deviceType: '3',//设备类型 1:楼控 2:照明  3:采集器
        }
      },
    },

    data() {
      //不允许输入,/!特殊符号
      var checkText = (rule, value, callback) => {
        if (!value) {
          return callback()
        }
        if (value) {
          var reg = /[,/!]/;
          if (reg.test(value)) {
            callback(new Error('不允许输入,/!特殊符号'))
          } else {
            callback()
          }
        }
      }
      return {
        //数值未修改 保存按钮禁用
        saveBoolean: true,
        //是否忽略监听
        watchBoolean: false,

        visible: false,
        //新增/修改表单
        updateForm: this.treeNodeMsg,
        form: {
          alias: '',//别名
          port: '',//端口
          deviceNodeId: '4',//所属节点类
          deviceType: '3',//设备类型 1:楼控 2:照明  3:采集器
        },
        //保存周期的数据
        savePeriodOptions: [],
        // 表单校验
        rules: {
          alias: [{required: true, message: '别名不能为空', trigger: 'blur'},
            {validator: checkText}],
          port: [{required: true, message: '端口不能为空', trigger: 'change'}],
        },
      }
    },
    computed: {
      watchList: function () {
        const obj = {}
        Object.keys(this.updateForm).forEach(key => {
          obj[key] = this.updateForm[key]
        })
        return obj
      },
    },
    watch: {
      watchList: {
        deep: true,
        handler: function (newVal, oldVal) {
          if (this.watchBoolean) {
            if (newVal !== oldVal) {
              this.saveBoolean = false
            }
          }
        }
      },
      deep: true,
      treeNodeMsg(value) {
        this.watchBoolean = false
        this.$refs["updateForm"].resetFields()
        this.updateForm = this.treeNodeMsg;
        this.saveBoolean = true
      },
      updateForm() {
        if (!(this.watchBoolean)) {
          this.watchBoolean = true
        }
      },
    },
    created() {
      //生成保存周期的数据
      this.generateSavePeriodOptions();
    },
    mounted() {
    },
    methods: {
      // 表单重置
      reset() {
        this.form = {};
        this.resetForm("form");
      },
      //生成保存周期的数据
      generateSavePeriodOptions() {
        let time = 0;
        for (let i = 0; i <= 200; i++) {

          if (time == 0) {
            let savePeriodOptionsMsg = {};
            savePeriodOptionsMsg.label = 0 + '分钟';
            savePeriodOptionsMsg.value = 0
            this.savePeriodOptions.push(savePeriodOptionsMsg);
            time = 1;
          } else if (time == 5) {
            let savePeriodOptionsMsg = {};
            savePeriodOptionsMsg.label = i + '分钟';
            savePeriodOptionsMsg.value = i
            this.savePeriodOptions.push(savePeriodOptionsMsg);
            time = 1;
          } else {
            time++
          }
        }
      },

      //提交按钮
      submitForm(formName) {
        this.$refs[formName].validate(valid => {
          if (valid) {
            if (formName == 'updateForm') {
              updateBus(this.updateForm).then(response => {
                this.saveBoolean = true
                this.$modal.msgSuccess("修改成功");
                this.$emit("updateTreeNode", this.updateForm)
                this.visible = false;
              });
            } else {
              this.form.deviceTreeFatherId = this.treeNodeMsg.deviceTreeId //父设备id
              addBus(this.form).then(response => {
                this.saveBoolean = true
                this.$modal.msgSuccess("新增成功");
                this.$emit('addTreeNode', response.data);
                  this.$refs["addForm"].resetFields()

                  this.visible = false;
              });
            }
          }
        });
      },


      handleClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            this.form = {
              alias: '',//别名
              port: '',//端口
              deviceNodeId: '4',//所属节点类
              deviceType: '3',//设备类型 1:楼控 2:照明  3:采集器
            }
            done();
            //重新加载当前选中的树节点
            this.$emit('ReloadNode');
          })
          .catch(_ => {
          });
      },

    }
  }
</script>
<style lang="scss" scoped>
  .white-body-view {
    width: 100%;
    min-width: 320px;
  }
</style>
