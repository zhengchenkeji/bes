<template>
  <div class="white-body-view">

    <!-- 调试弹窗 -->
    <el-drawer
      size='35%'
      :title="title"
      :visible.sync="visible"
      :before-close="handleClose"
      direction="rtl"
    >
      <div style="margin-left: 15px; margin-right: 15px">
        <div style="margin-bottom: 15px;">
          <!-- 选择下拉框和搜索 -->
          <el-row :span="10" :xs="24">
            <el-col>
              <el-form ref="form" :model="form" label-width="90px">
                <el-form-item label="系统名称" prop="sysName">
                  <el-input v-model="form.sysName" style="width: 300px" disabled></el-input>
                </el-form-item>
                <el-form-item label="设置">
                  <el-select v-model="form.initValue" style="width: 300px"
                             v-if="pointTypeValue == deviceTreeSettings.DODebugger">
                    <el-option
                      v-for="item in initValueList"
                      :key="item.id"
                      :label="item.value"
                      :value="item.id">
                    </el-option>
                  </el-select>
                  <div v-else>
                    <el-input v-model="form.initValue" style="width: 220px"/>
                    <el-input v-model="form.unit" style="width: 80px" disabled/>
                  </div>
                </el-form-item>
                <el-form-item label="工作模式" prop="workMode">
                  <el-radio-group v-model="form.workMode" style="width: 300px" @change="checkFreshFrom">
                    <el-radio :label="0">自动</el-radio>
                    <el-radio :label="1">手动</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-form>
              <el-form :inline="true" label-width="80px" style="text-align: center">
                <el-form-item>
                  <el-button
                    v-hasPermi="['basicData:deviceTree:point:debugPointInfo']"
                    type="primary" @click="submitForm">执行</el-button>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="backPage">返回</el-button>
                </el-form-item>
                <el-form-item>
                  <el-button
                    v-hasPermi="['basicData:deviceTree:point:selectEditPointValue']"
                    type="primary" @click="editDebuggerInfo(true,'')">点值配置</el-button>
                </el-form-item>

              </el-form>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-drawer>


    <!--点值配置-->
    <el-dialog title="点值配置" :visible.sync="visibleData" width="800px" append-to-body>
      <el-row v-for="(item, index) in ruleForms.items" :key="index" style="margin-left: 0.5vw">
        <el-col :span="22">
          <el-form :rules="rules" :inline="true" :model="item" class="demo-ruleForm" ref="formInline">
            <el-form-item label="提示">
              <el-input v-model="item.description" placeholder="请输入提示" style="width: 15vw"></el-input>
            </el-form-item>
            <el-form-item label="数值">
              <el-input v-model="item.initVal" placeholder="请输入数值" style="width: 15vw"></el-input>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="2">
          <i class="el-icon-circle-plus-outline" type="danger" v-if="index == 0"
             style="font-size: 30px; color: #67c23a; margin-right: 5px" @click="addItem"></i>
          <i class="el-icon-remove-outline" type="danger" v-if="index != 0"
             style="font-size: 30px; color: #f56c6c" @click="delItem(index)"></i>
        </el-col>
      </el-row>
      <el-form :inline="true" label-width="80px" style="text-align: center">
        <el-form-item>
          <el-button
            v-hasPermi="['basicData:deviceTree:point:debuggerEditPointValue']"
            type="primary" @click="submitItem">确定</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="disItem">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
  import {
    debuggerPoint, debuggerEditPointValue, selectEditPointValue
  } from '@/api/basicData/deviceManagement/deviceTree/deviceTreePoint'
  import { deviceTreeSettings } from '../../../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings'

  export default {
    props: {
      pointType: {
        type: String,
        default: ''
      },
      nodeType: {
        type: String,
        default: ''
      }
    },
    data() {
      return {
        debuggerId: null,
        pointTypeValue: null,
        initValueList: [],//初始值列表
        deviceTreeSettings: deviceTreeSettings,//点类型
        visible: false,//右侧弹窗是否弹出
        visibleData: false,//点值配置弹窗是否弹出
        deviceNodeId: null,//设备树node
        title: '调试点位',
        form: {
          treeId: null,
          sysName: null,
          initValue: null,
          workMode: null,
          pointType: null,
          nodeType: null,
          unit: null,
          accuracy: null
        },
        formInline: {
          description: '',
          initVal: ''
        },
        ruleForms: { items: [{ description: '', initVal: '' }] },
        rules: {
          description: [{ required: true, message: '提示不能为空', trigger: 'blur' }],
          initVal: [{ required: true, message: '数值不能为空', trigger: 'blur' }]
        }
      }
    },

    watch: {
      pointType(value) {
        this.pointTypeValue = value
        this.form.pointType = value
        if (value == deviceTreeSettings.DODebugger || value == deviceTreeSettings.DODebugger) {
          this.queryInitValValue(value)//点类型
        }

      }
    },
    mounted() {
    },
    created() {

    },
    methods: {
      //查询设置列表
      queryInitValValue(value) {
        this.editDebuggerInfo(false,value)
      },
      //关闭弹窗
      handleClose(done) {
        this.$confirm('确认关闭？').then(_ => {
          done()
          this.$refs.form.resetFields()
          //重新加载当前选中的树节点
          this.$emit('ReloadNode')
        }).catch(_ => {
        })
      },
      //发送调试
      submitForm() {
        let that = this
        this.$confirm('确认调试当前模块点 ' + this.form.sysName + ' 吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.debuggerId = this.form.treeId
          this.form.id = this.form.treeId
          this.form.value = this.form.initValue
          if (Number(this.form.initValue) > 0 && (this.form.initValue.toString()).indexOf('.') != -1) {//说明含有小数点
            if (this.form.initValue.toString().split('.')[1].length > this.form.accuracy || this.form.initValue.toString().split('.')[1].length < this.form.accuracy) {
              this.$modal.msgError('小数点位置不等于精度数值')
              return
            }
          }
          this.$refs['form'].validate(valid => {
            if (valid) {
              debuggerPoint(this.form).then(response => {
                if(response.code == 200){
                  this.$modal.msgSuccess('调试成功')
                  // this.$emit('updatePointWorkMode',that.form.workMode)
                  this.visible = true
                }
              })
              //   .catch(responseFail => {
              //   this.$modal.msgSuccess(responseFail.message)
              //   return
              // })
            }
          })
        })

      },
      //返回按钮
      backPage() {
        this.$refs.form.resetFields()
        this.visible = false
        //重新加载当前选中的树节点
        this.$emit('ReloadNode')
      },
      //设置配置
      editDebuggerInfo(value,value2) {
        //先清空
        this.ruleForms = { items: [{ description: '', initVal: '' }] }
        //再去查询
        let map = {}
        map['sysName'] = this.form.sysName
        //赋值
        selectEditPointValue(map).then(response => {
          response.data.forEach(item => {
            item.id = Number(item.initVal)
            item.value = item.description
          })
          if (response.data != null && response.data.length > 0) {
            this.ruleForms.items = response.data
            this.initValueList = response.data
          }
          if (this.ruleForms.items.length == 1 && this.ruleForms.items[0].initVal == '') {
            let list = []
            if (value2 == deviceTreeSettings.DODebugger || value2 == deviceTreeSettings.DODebugger) {
              list = [{ id: 255, value: '开机' }, { id: 0, value: '关机' }]
              this.initValueList = list
            }
          }
        })
        if (value) {
          this.visibleData = true
        }
      },
      //添加配置
      addItem() {
        // this.i += 1;
        this.ruleForms.items.push({
          description: '',
          initVal: ''
        })
        // console.log(this.i);
      },
      //删除配置
      delItem(index) {
        this.ruleForms.items.splice(index, 1)
      },
      //提交数据
      submitItem() {
        let that = this
        this.$confirm('确认配置当前点位 ' + this.form.sysName + ' 吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let list = []
          let changelist = []
          let isAjax = true
          that.ruleForms.items.forEach(item => {
            if (item.description == '' || item.initVal.toString() == '') {
              that.$modal.msgError('请输入提示和数值')
              isAjax = false
            } else {
              changelist.push({ id: Number(item.initVal), value: item.description })
              list.push('{' + '\'sysName\':' + '\'' + this.form.sysName + '\'' + ', \'description\':' + '\'' + item.description + '\'' + ', \'initVal\': ' + '\'' + item.initVal + '\'' + '}')
            }
          })
          if (isAjax) {
            let addList = {}
            list = list.join('-')
            addList['list'] = list
            debuggerEditPointValue(addList).then(response => {
              that.$modal.msgSuccess('配置成功')
              //配置当前下拉框
              that.initValueList = changelist
              that.visibleData = false
              return
            })
          }
        })
      },
      //强制刷新redio
      checkFreshFrom() {
        this.$forceUpdate()
      },
      //关闭弹窗
      disItem() {
        this.visibleData = false
        this.ruleForms = { items: [{ description: '', initVal: '' }] }
      }
    }
  }
</script>
<style lang="scss" scoped>
  .white-body-view {
    width: 100%;
    min-width: 320px;
  }

  .fromHeader .el-select .el-input {
    border-color: #409EFF;
    width: 400px;
  }
</style>
