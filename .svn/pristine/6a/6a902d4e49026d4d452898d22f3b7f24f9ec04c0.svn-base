<template>
  <div class="white-body-view">
    <div class="fromHeader">
      <el-form :inline="true" ref="queryform" :model="queryform" label-width="160px" :rules="rulesEdit">
        <el-form-item label="名称" style="width: 400px">
          <el-input v-model="queryform.sysName" disabled></el-input>
        </el-form-item>
        <el-form-item label="别名" style="width: 400px" v-if="queryform.nodeType != deviceTreeSettings.line">
          <el-input v-model="queryform.nickName" disabled></el-input>
        </el-form-item>
        <el-form-item label="别名" style="width: 400px" v-if="queryform.nodeType == deviceTreeSettings.line">
          <el-input v-model="queryform.nickName"></el-input>
        </el-form-item>
        <el-form-item label="端口" style="width: 400px" v-if="queryform.nodeType == deviceTreeSettings.line">
          <el-select v-model="queryform.portNum" style="width: 200px">
            <el-option
              v-for="item in portList"
              :key="item.id"
              :label="item.value"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="80px" style="text-align: center"
               v-if="queryform.nodeType == deviceTreeSettings.line">
        <el-form-item>
          <el-button
            v-hasPermi="['basicData:deviceTree:updateDeviceTreee']"
            type="primary" @click="updateForm" :disabled="saveBoolean">保存</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 新增虚点，总线 -->
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
              <el-form :inline="true" :rules="rules" ref="insertParams" :model="insertParams" label-width="90px">
                <el-form-item label="别名" prop="nickName">
                  <el-input v-model="insertParams.nickName" disabled
                            v-if="deviceNodeId!=deviceTreeSettings.line"></el-input>
                  <el-input v-model="insertParams.nickName" v-if="deviceNodeId==deviceTreeSettings.line"></el-input>
                </el-form-item>
                <el-form-item label="端口" prop="portNum" v-if="deviceNodeId==deviceTreeSettings.line">
                  <el-select v-model="insertParams.portNum" style="width: 200px">
                    <el-option
                      v-for="item in portList"
                      :key="item.id"
                      :label="item.value"
                      :value="item.id">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
              <el-form :inline="true" label-width="80px" style="text-align: center">
                <el-form-item>
                  <el-button
                    v-hasPermi="['basicData:deviceTree:insertDeviceTreee']"
                    type="primary" @click="submitForm">新增</el-button>
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
  import {
    insertDeviceTreee, updateDeviceTreee
  } from '@/api/basicData/deviceManagement/deviceTree/deviceTreePoint'
  import { deviceTreeSettings } from '../../../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings'
  export default {
    name: "parkRootNodeInfo",
    props: {
      treeNodeMsgs: {
        type: Object,
        default: {
          sysName: null,
          nodeType: null,
          nickName: null,
          portNum: null
        }
      }
    },
    data() {
      // 只能输入英文、数字、下划线!
      var checkinput = (rule, value, callback) => {
        if (!value) {
          return callback()
        }
        if (value) {
          var reg = /^[\u4E00-\u9FA5A-Za-z0-9_]+$/;
          if (!reg.test(value)) {
            callback(new Error('只能输入英文、数字、下划线!'))
          } else {
            callback()
          }
        }
      }
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
      //不允许输入汉字
      var checkChinese = (rule, value, callback) => {
        if (!value) {
          return callback()
        }
        if (value) {
          var reg = /[\u4e00-\u9fa5]/;
          if (reg.test(value)) {
            callback(new Error('不允许输入汉字'))
          } else {
            callback()
          }
        }
      }
      return {
        watchBoolean: false,//是否忽略监听
        saveBoolean: true,//按钮是否可以点击
        portList: [],//端口列表
        deviceTreeSettings: deviceTreeSettings,//点类型
        insertParams: {
          nodeType: null,//设备树node
          nickName: null,//系统名称
          sysName: null,//系统名称
          fatherId: null,//设备树父Id
          controllerId: null,//DDC id
          portNum: null//端口
        },
        visible: false,//右侧弹窗是否弹出
        deviceNodeId: null,//设备树node
        sysName: null,//系统名称
        deviceTreeFatherId: null,//设备树父Id
        controllerId: null,//DDC id
        title: '新增节点',
        queryform: this.treeNodeMsgs,
        form: {
          name: '',
          region: '',
          date1: '',
          date2: '',
          delivery: false,
          type: [],
          resource: '',
          desc: ''
        },
        rules: {
          nickName: [{ required: true, message: '别名不能为空', trigger: 'blur' },
            {validator: checkText}
          ],
          portNum: [{ required: true, message: '端口不能为空', trigger: 'change' }]
          // portNum: [{ required: true, message: '端口不能为空', trigger: 'blur' },{
          //   pattern: /^[0-9]*$/,
          //   message: '请输入正确的端口号'
          // }],
        },
        rulesEdit: {
          nickName: [{ required: true, message: '别名不能为空', trigger: 'blur' },
            {validator: checkText}
          ],
          portNum: [{ required: true, message: '端口不能为空', trigger: 'change' }],
          sysName: [{ required: true, message: '系统名称不能为空', trigger: 'blur' },
            {pattern: /^[a-zA-Z]/, message: "首位只能输入英文字母",},
            {validator: checkChinese},
            {validator: checkinput},
          ]
        }
      }
    },
    computed: {
      watchList: function() {
        const obj = {}
        Object.keys(this.queryform).forEach(key => {
          obj[key] = this.queryform[key]
        })
        return obj
      }
    },

    watch: {
      //2.判断是否为人为修改数据   还是点击节点更换的数据
      //4.数据确定之后  再次发生变化 即为人为修改数据 保存按钮生效
      watchList: {
        deep: true,
        handler: function(newVal, oldVal) {
          if (this.watchBoolean) {
            if (newVal !== oldVal) {
              this.saveBoolean = false
            }
          }
        }
      },
      //监听  1.先走该方法  刷新数据
      deep: true,
      treeNodeMsgs(value) {
        this.watchBoolean = false
        this.queryform = this.treeNodeMsgs
        this.saveBoolean = true
      },
      //3.数据确定
      queryform() {
        if (!(this.watchBoolean)) {
          this.watchBoolean = true
        }
      }
    },
    mounted() {
    },
    created() {
      this.queryPortList()
    },
    methods: {
      //查询端口列表
      queryPortList() {
        var arr = []
        for (let i = 0; i <= 5; i++) {
          var address = { id: i.toString(), value: i.toString() }
          arr.push(address)
        }
        this.portList = arr

      },
      //关闭弹窗
      handleClose(done) {
        this.$confirm('确认关闭？').then(_ => {
          done()
          this.$refs.insertParams.resetFields()
          //重新加载当前选中的树节点
          this.$emit('ReloadNode')
        }).catch(_ => {
        })
      },
      //添加树节点
      submitForm() {
        let that = this
        that.$refs['insertParams'].validate(valid => {
          if (valid) {
            //直接添加到树结构
            that.insertParams.nodeType = this.deviceNodeId
            that.insertParams.fatherId = this.deviceTreeFatherId
            that.insertParams.controllerId = this.controllerId
            that.insertParams.treeId = null
            insertDeviceTreee(that.insertParams).then(response => {
              if (response.code == 200) {
                response.data.deviceTreeId = response.data.treeId
                response.data.alias = response.data.nickName
                response.data.deviceNodeId = response.data.nodeType
                that.$emit('addTreeNode', response.data)
                that.$modal.msgSuccess('新增成功')
                this.$refs.insertParams.resetFields()
                this.visible = false
              } else {
                that.$modal.msgError('新增失败')
                this.visible = true
              }
            })
          }
        })
      },
      //修改树节点
      updateForm() {
        let that = this
        that.saveBoolean = true
        that.$refs['queryform'].validate(valid => {
          if (valid) {
            //直接添加到树结构
            that.insertParams.nodeType = that.queryform.nodeType
            that.insertParams.fatherId = that.deviceTreeFatherId
            that.insertParams.controllerId = that.controllerId
            that.insertParams.nickName = that.queryform.nickName
            that.insertParams.portNum = that.queryform.portNum
            that.insertParams.sysName = that.queryform.sysName
            that.insertParams.treeId = that.queryform.treeId
            updateDeviceTreee(that.insertParams).then(response => {
              if (response.code == 200) {
                response.data.deviceTreeId = response.data.treeId
                response.data.alias = response.data.nickName
                response.data.deviceNodeId = response.data.nodeType
                that.$modal.msgSuccess('保存成功')
                that.$emit('updateTreeNode', response.data)
                this.visible = false
              } else {
                that.$modal.msgError('保存失败')
              }
            })
          }
        })
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
