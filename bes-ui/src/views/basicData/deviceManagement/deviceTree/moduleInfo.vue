<template>
  <div class="white-body-view">
    <div class="fromHeader">
      <!--  ************************************************************模块详情************************************** -->
      <el-form :inline="true" ref="queryFormModel" :model="queryFormModel" label-width="160px" :rules="modelRules"
               v-if="nodeTypeValue==deviceTreeSettings.model">
        <el-form-item label="系统名称" prop="sysName" style="width: 400px">
          <el-input v-model="queryFormModel.sysName" placeholder="请输入系统名称" disabled style="width: 202px"/>
        </el-form-item>

        <el-form-item label="模块别名" prop="alias" style="width: 400px">
          <el-input v-model="queryFormModel.alias"
                    maxlength="24" show-word-limit
                    placeholder="请输入别名" style="width: 202px"/>
        </el-form-item>

        <el-form-item label="安装地址"
                      prop="installAddress" style="width: 400px">
          <el-input v-model="queryFormModel.installAddress"
                    maxlength="12" show-word-limit
                    placeholder="请输入安装地址" style="width: 202px"/>
        </el-form-item>

        <el-form-item label="通信地址" prop="slaveAddress" style="width: 400px">
          {{ prefix }}
          <el-select v-model="queryFormModel.slaveAddress" style="width: 150px">
            <el-option
              v-for="item in slaveAddressList"
              :key="item.id"
              :label="item.value"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="模块描述"
                      prop="description" style="width: 400px">
          <el-input v-model="queryFormModel.description"
                    maxlength="12" show-word-limit
                    placeholder="请输入描述" style="width: 202px"/>
        </el-form-item>

        <el-form-item label="模块型号" prop="moduleTypeId" style="width: 400px">
          <el-select v-model="queryFormModel.moduleTypeId" style="width: 202px" disabled>
            <el-option
              v-for="item in moduleTypeList"
              :key="item.typeCode"
              :label="item.moduleCode"
              :value="item.typeCode">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="使能状态" prop="active" style="width: 400px">
          <el-switch v-model="queryFormModel.active" :active-value="1" :inactive-value="0" active-color="#13ce66"
                     inactive-color="#ff4949" style="width: 202px"/>
        </el-form-item>

        <el-form-item label="是否在线" prop="onlineState" style="width: 400px">
          <el-switch v-model="queryFormModel.onlineState" :active-value="1" :inactive-value="0" active-color="#13ce66"
                     inactive-color="#ff4949" disabled style="width: 202px"/>
        </el-form-item>

        <el-form-item label="是否同步" prop="synchState" style="width: 400px">
          <el-switch v-model="queryFormModel.synchState" :active-value="1" :inactive-value="0" active-color="#13ce66"
                     inactive-color="#ff4949" disabled style="width: 202px"/>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="80px" style="text-align: center"
               v-if="nodeTypeValue==deviceTreeSettings.model">
        <el-form-item>
          <el-button
            v-hasPermi="['basicData:deviceTree:module:sync']"
            type="primary" @click="synchronizeData(nodeType)">同步数据
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            v-hasPermi="['basicData:deviceTree:module:contrast']"
            type="primary" @click="dataComparison(nodeType)">数据对比
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            v-hasPermi="['basicData:deviceTree:module:edit']"
            type="primary" @click="updateFormModel" :disabled="saveBoolean">保存
          </el-button>
        </el-form-item>
      </el-form>
    </div>


    <!--  ************************************************************新增模块************************************** -->
    <el-drawer
      v-if="nodeTypeValue==deviceTreeSettings.model"
      size='35%'
      title="新增模块"
      :visible.sync="visible"
      :before-close="handleClose"
      direction="rtl"
    >
      <div style="margin-left: 15px; margin-right: 15px">
        <div style="margin-bottom: 15px;">
          <!-- 选择下拉框和搜索 -->
          <el-row :span="10" :xs="24">
            <el-col>
              <el-form :inline="true" ref="modelForm" :model="modelForm" label-width="90px" :rules="modelRules">
                <el-form-item label="系统名称" prop="sysName">
                  <el-input v-model="modelForm.sysName"
                            maxlength="24" show-word-limit
                            placeholder="请输入系统名称" style="width: 202px"/>
                </el-form-item>

                <el-form-item label="模块别名"
                              prop="alias">
                  <el-input v-model="modelForm.alias"
                            maxlength="24" show-word-limit
                            placeholder="请输入别名" style="width: 202px"/>
                </el-form-item>

                <el-form-item label="安装地址" prop="installAddress">
                  <el-input v-model="modelForm.installAddress"
                            maxlength="12" show-word-limit
                            placeholder="请输入安装位置" style="width: 202px"/>
                </el-form-item>

                <el-form-item label="通讯地址" prop="slaveAddress">
                  {{ prefix }}
                  <el-select v-model="modelForm.slaveAddress" style="width: 150px">
                    <el-option
                      v-for="item in slaveAddressList"
                      :key="item.id"
                      :label="item.value"
                      :value="item.id">
                    </el-option>
                  </el-select>
                </el-form-item>

                <el-form-item label="描述" prop="description">
                  <el-input v-model="modelForm.description"
                            maxlength="12" show-word-limit
                            placeholder="请输入描述" style="width: 202px"/>
                </el-form-item>

                <el-form-item label="模块型号" prop="moduleTypeId">
                  <el-select v-model="modelForm.moduleTypeId" style="width: 202px">
                    <el-option
                      v-for="item in moduleTypeList"
                      :key="item.typeCode"
                      :label="item.moduleCode"
                      :value="item.typeCode">
                    </el-option>
                  </el-select>
                </el-form-item>

                <el-form-item label="使能状态" prop="active">
                  <el-switch v-model="modelForm.active" :active-value="1" :inactive-value="0" active-color="#13ce66"
                             inactive-color="#ff4949" style="width: 202px"/>
                </el-form-item>

                <el-form-item label="是否同步" prop="synchState">
                  <el-switch v-model="modelForm.synchState" :active-value="1" :inactive-value="0" active-color="#13ce66"
                             inactive-color="#ff4949" disabled style="width: 202px"/>
                </el-form-item>
              </el-form>
              <el-form :inline="true" label-width="80px" style="text-align: center">
                <el-form-item>
                  <el-button
                    v-hasPermi="['basicData:deviceTree:module:edit']"
                    type="primary" @keyup.enter="submitFormModel" @click="submitFormModel">新增
                  </el-button>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-drawer>


    <!--  ************************************************************数据对比************************************** -->
    <el-dialog title="数据对比" :visible.sync="visibleData" width="800px" append-to-body @close="clearParmas()">
      <el-row>
        <el-col :span="11" style="background-color: #ebf6fb;text-align: center">
          <el-button style="margin-bottom: 15px;margin-top: 10px">上位机数据</el-button>
          <!-- *******************************上位机模块数据对比***************************************************** -->
          <el-form ref="queryFormModel" :inline="true" :model="upperFormData" label-width="90px"
                   v-if="nodeTypeValue==deviceTreeSettings.model">
            <el-form-item label="ID" prop="id">
              <el-input v-model="upperFormData.id" readonly/>
            </el-form-item>
            <el-form-item label="名称" prop="sysName">
              <el-input v-model="upperFormData.sysName" readonly/>
            </el-form-item>
            <el-form-item label="别名" prop="alias">
              <el-input v-model="upperFormData.alias" readonly/>
            </el-form-item>
            <el-form-item label="安装位置" prop="installAddress">
              <el-input v-model="upperFormData.installAddress" readonly/>
            </el-form-item>
            <el-form-item label="型号" prop="moduleTypeId">
              <el-input v-model="upperFormData.moduleTypeId" readonly/>
            </el-form-item>
            <el-form-item label="使能状态" prop="active">
              <el-input v-model="upperFormData.active == 0 ? '否' : '是'" readonly/>
            </el-form-item>
            <el-form-item label="描述信息" prop="description">
              <el-input v-model="upperFormData.description" readonly/>
            </el-form-item>
            <el-form-item label="通信地址" prop="slaveAddress"><!--style="width: 400px"-->
              <el-input v-model="upperFormData.slaveAddress" readonly/>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="2" style="height: 1px"></el-col>
        <el-col :span="11" style="background-color: #ebf6fb;text-align: center">
          <el-button style="margin-bottom: 15px;margin-top: 10px">下位机数据</el-button>
          <!-- *******************************下位机模块数据对比***************************************************** -->
          <el-form ref="underFormData" :inline="true" :model="underFormData" label-width="90px"
                   v-if="nodeTypeValue==deviceTreeSettings.model">
            <el-form-item label="ID" prop="id">
              <el-input v-model="underFormData.id" readonly class="error_input"
                        v-if="upperFormData.id != underFormData.id"/>
              <el-input v-model="underFormData.id" readonly v-else/>
            </el-form-item>
            <el-form-item label="名称" prop="name">
              <el-input v-model="underFormData.name" readonly class="error_input"
                        v-if="upperFormData.sysName != underFormData.name"/>
              <el-input v-model="underFormData.name" readonly v-else/>
            </el-form-item>
            <el-form-item label="别名" prop="alias">
              <el-input v-model="underFormData.alias" readonly class="error_input"
                        v-if="upperFormData.alias != underFormData.alias"/>
              <el-input v-model="underFormData.alias" readonly v-else/>
            </el-form-item>
            <el-form-item label="安装位置" prop="location">
              <el-input v-model="underFormData.location" readonly class="error_input"
                        v-if="upperFormData.installAddress != underFormData.location"/>
              <el-input v-model="underFormData.location" readonly v-else/>
            </el-form-item>
            <el-form-item label="型号" prop="modelID">
              <el-input v-model="underFormData.modelID" readonly class="error_input"
                        v-if="upperFormData.moduleTypeId != underFormData.modelID"/>
              <el-input v-model="underFormData.modelID" readonly v-else/>
            </el-form-item>
            <el-form-item label="使能状态" prop="active">
              <el-input v-model="underFormData.active == 0 ? '否' : '是'" readonly class="error_input"
                        v-if="upperFormData.active != underFormData.active && underFormData.active != null"/>
              <el-input v-model="underFormData.active == 0 ? '否' : '是'" readonly
                        v-if="upperFormData.active == underFormData.active && underFormData.active != null"/>
              <el-input v-model="underFormData.active" readonly v-if="underFormData.active == null"/>
            </el-form-item>
            <el-form-item label="描述信息" prop="description">
              <el-input v-model="underFormData.description" readonly class="error_input"
                        v-if="upperFormData.description != underFormData.description"/>
              <el-input v-model="underFormData.description" readonly v-else/>
            </el-form-item>
            <el-form-item label="通信地址" prop="slaveAddress">
              <el-input v-model="underFormData.slaveAddress" readonly class="error_input"
                        v-if="upperFormData.slaveAddress != underFormData.slaveAddress"/>
              <el-input v-model="underFormData.slaveAddress" readonly v-else/>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-dialog>


  </div>
</template>

<script>
  import {
    insertModule, updateModule, listModuleType,
    getDataInfoParam, synchronizeModel
  } from '@/api/basicData/deviceManagement/deviceTree/deviceTreePoint'
  import {deviceTreeSettings} from '../../../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings'
  import {mapState} from 'vuex'

  export default {
    props: {
      nodeType: {
        type: String,
        default: ''
      },
      treeNodeMsgs: {
        type: Object,
        default: {
          sysName: null,
          nickName: null,
          description: null,
          installAddress: null,
          slaveAddress: null,
          moduleTypeId: null,
          type: null,
          active: null,
          onlineState: null
        }
      }
    },

    data() {
      // 别名校验方法
      var checkinput = (rule, value, callback) => {
        if (!value) {
          return callback()
        }
        if (value) {
          var reg = /^[\u4E00-\u9FA5A-Za-z0-9_]+$/
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
        nodeTypeValue: null,
        upperFormData: {},//上位机数据
        underFormData: {},//下位机数据
        prefix: null,//通讯地址前缀
        watchBoolean: false,//是否忽略监听
        saveBoolean: true,//按钮是否可以点击
        deviceTreeSettings: deviceTreeSettings,//设备树node设置js
        controllerId: null,//DDC id
        fatherId: null,//设备树父ID
        visible: false,//右侧弹窗是否弹出
        visibleData: false,//对比框弹出
        formVisible: false,
        queryFormModel: this.treeNodeMsgs,//模块展示，数据对比
        modelForm: {//模块添加
          sysName: null,
          alias: null,
          installAddress: null,
          description: null,
          moduleTypeId: null,
          nodeType: 0,
          fatherId: 0,
          active: 1,
          synchState: 0,
          onlineState: 0
        },
        moduleTypeList: [],//模块类型
        slaveAddressList: [],//通讯地址
        // 表单校验
        modelRules: {
          sysName: [{required: true, message: '系统名称不能为空', trigger: 'blur'},
            {pattern: /^[a-zA-Z]/, message: "首位只能输入英文字母",},
            {validator: checkChinese},
            {validator: checkinput},
          ],
          alias: [{required: true, message: '别名不能为空', trigger: 'blur'},
            {validator: checkText}
          ],
          installAddress: [{required: true, message: '安装地址不能为空', trigger: 'blur'}],
          slaveAddress: [{required: true, message: '请选择通讯地址', trigger: 'change'}],
          description: [{required: true, message: '描述不能为空', trigger: 'blur'}],
          moduleTypeId: [{required: true, message: '请选择模块型号', trigger: 'change'}],
          type: [{required: true, message: '请选择模块类型', trigger: 'change'}],
          active: [{required: true, message: '请选择使能状态', trigger: 'change'}]
        }
      }
    },
    computed: {
      ...mapState({
        //模块同步状态
        besModuleState_websocket: state => state.websocket.besModuleState,
        //模块信息
        besModule_websocket: state => state.websocket.besModule,
        /************************************qindehua LDC*****************************************/
        //照明模块新增
        moduleAddLDC: state => state.websocket.moduleAddLDC,
        //同步数据
        moduleParamSetLDC: state => state.websocket.moduleParamSetLDC,
        //数据对比
        moduleParamGetLDC: state => state.websocket.moduleParamGetLDC
      }),
      watchListModel: function () {
        const obj = {}
        Object.keys(this.queryFormModel).forEach(key => {
          obj[key] = this.queryFormModel[key]
        })
        return obj
      }
    },

    watch: {
      nodeType(value) {
        if (value != null) {
          this.nodeTypeValue = value//设备node
        }
        // this.queryModuleType()
      },

      //2.判断是否为人为修改数据   还是点击节点更换的数据
      //4.数据确定之后  再次发生变化 即为人为修改数据 保存按钮生效
      watchListModel: {
        deep: true,
        handler: function (newVal, oldVal) {
          if (this.watchBoolean) {
            if (newVal.alias !== oldVal.alias || newVal.installAddress !== oldVal.installAddress
              || newVal.slaveAddress !== oldVal.slaveAddress || newVal.description !== oldVal.description
              || newVal.moduleTypeId !== oldVal.moduleTypeId || newVal.type !== oldVal.type
              || newVal.active !== oldVal.active) {
              this.saveBoolean = false
            }
          }
        }
      },
      //监听  1.先走该方法  刷新数据
      deep: true,
      treeNodeMsgs(value) {//右侧设备数据展示
        //查询能源类型
        // if(value.nodeType == deviceTreeSettings.model || value.nodeType == deviceTreeSettings.line){
        // this.queryModuleType()
        // }
        this.watchBoolean = false
        this.saveBoolean = true
        let str = value.nodeType
        str = str + ''
        if (str == deviceTreeSettings.model) {
          this.queryFormModel = this.treeNodeMsgs
        }
        this.$nextTick(() => {
          if (this.treeNodeMsgs.nodeType != null && this.treeNodeMsgs.nodeType == deviceTreeSettings.model) {
            //去除校验
            this.$refs.queryFormModel.clearValidate()
          }
        })
      },
      //3.数据确定
      queryFormModel() {
        if (!(this.watchBoolean)) {
          this.watchBoolean = true
        }
      },

      //模块同步状态实时显示
      besModuleState_websocket(data) {
        if (typeof data == null) {
          return
        }
        let str = ''
        str = data
        str = str.split('-')
        if (str[0] == 'true') {
          this.queryFormModel.synchState = 1
        } else {
          this.queryFormModel.synchState = 0
        }
      },

      //模块信息
      besModule_websocket(data) {
        if (data == null) {
          return
        }
        data.modelID = this.setmoduleType(data.modelID)//模块类型
        this.underFormData = data
      },

      /************************************qindehua LDC*****************************************/
      //照明模块新增
      moduleAddLDC(state) {
        if (typeof state !== 'boolean') {
          return
        }
        if (state) {
          this.queryFormModel.synchState = 1
        } else {
          this.queryFormModel.synchState = 0
        }
        this.$store.commit('MODULE_ADD_LDC', 0)
      },
      //同步数据
      moduleParamSetLDC(state) {
        if (typeof state !== 'boolean') {
          return
        }
        if (state) {
          this.queryFormModel.synchState = 1
        } else {
          this.queryFormModel.synchState = 0
        }
        this.$store.commit('MODULE_PARAM_SET_LDC', 0)
      },
      //数据对比
      moduleParamGetLDC(data) {
        if (data == null) {
          return
        }
        data.modelID = this.setmoduleType(data.modelID)//模块类型
        this.underFormData = data
      }

    },

    created() {
      //查询模块类型
      this.queryModuleType()
      //通讯地址类型
      this.queryslaveAddressList()
    },
    methods: {
      checkprefix(prefix) {
        if (prefix == null || prefix == '') {
          this.prefix = '1.1.'
        } else if (prefix == 'DDC') {
          this.prefix = ''
        } else {
          this.prefix = prefix
        }
      },
      queryslaveAddressList() {//查询通讯地址列表
        var arr = []
        for (let i = 1; i <= 255; i++) {
          var address = {id: i, value: i}
          arr.push(address)
        }
        this.slaveAddressList = arr

      },
      queryModuleType() {//查询模块类型列表
        
        listModuleType().then(response => {
          this.moduleTypeList = response
        })
      },
      // 表单重置
      reset() {
        this.form = {}
        this.resetForm('form')
      },

      //提交模块
      submitFormModel() {

        // if(this.treeNodeMsgs.trunkNum!=null){
        //   // 照明子模块
        //   this.modelForm.slaveAddress=this.treeNodeMsgs.trunkNum+'.'+this.treeNodeMsgs.branchNum+'.'+this.modelForm.slaveAddress;

        // }

        // 非照明子模块
        this.$refs['modelForm'].validate(valid => {
          if (valid) {
            this.modelForm.controllerId = Number(this.controllerId)
            this.modelForm.fatherId = Number(this.fatherId)
            this.modelForm.nodeType = Number(this.nodeTypeValue)
            //添加虚点
            insertModule(this.modelForm).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess(response.msg)
                response.data.deviceNodeId = response.data.nodeType
                this.$emit('addTreeNode', response.data)
                this.visible = false
                this.rest()
              }
            })
          }
        })
      },
      //保存模块
      updateFormModel() {
        this.$refs['queryFormModel'].validate(valid => {
          if (valid) {
            this.$confirm('确认修改当前模块 ' + this.queryFormModel.alias + ' 吗?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              // this.$refs['queryFormModel'].validate(valid => {
              //   if (valid) {
              //修改模块点
              updateModule(this.queryFormModel).then(response => {
                if (response.code == 200) {
                  this.saveBoolean = true
                  this.$modal.msgSuccess(response.msg)
                  this.$emit('updateTreeNode', response.data)
                  //   }
                  // })
                }
              })
            })
          }
        })
      },
      //同步数据
      synchronizeData(nodeType) {

        if (!this.saveBoolean) {
          this.$modal.confirm('数据已修改，请先保存！')
        } else {
          if (nodeType == deviceTreeSettings.model) {

            synchronizeModel({
              deviceTreeId: this.queryFormModel.deviceTreeId,
              nodeType: this.queryFormModel.nodeType
            }).then(response => {
              this.$modal.msgSuccess(response.msg)
            })
          }
        }

      },
      //数据对比
      dataComparison(nodeType) {
        let data = {
          id: this.queryFormModel.id,//ID
          sysName: this.queryFormModel.sysName,//系统名称
          alias: this.queryFormModel.alias,//别名
          installAddress: this.queryFormModel.installAddress,//安装地址
          moduleTypeId: this.setmoduleType(this.queryFormModel.moduleTypeId),//模块类型
          description: this.queryFormModel.description,//描述
          active: this.queryFormModel.active,//使能状态
          slaveAddress: this.queryFormModel.slaveAddress//通信地址
        }

        if (nodeType == deviceTreeSettings.model) {//模块

          this.upperFormData = data

          getDataInfoParam({
            deviceTreeId: this.queryFormModel.deviceTreeId,
            type: this.nodeTypeValue
          }).then(response => {
            this.$modal.msgSuccess(response.msg)
          })
          this.$nextTick(() => {
            this.visibleData = true
          })
        }
      },
      //取模块型号
      setmoduleType(value) {
        for (let i = 0; i < this.moduleTypeList.length; i++) {
          if (value == this.moduleTypeList[i].typeCode) {
            return this.moduleTypeList[i].moduleCode
          }
        }
      },
      closedDialog() {//关闭数据对比
        this.visibleData = false
      },
      handleClose(done) {//关闭新增页面
        this.$confirm('确认关闭？')
          .then(_ => {
            done()
            this.rest()
            //重新加载当前选中的树节点
            this.$emit('ReloadNode')
          })
          .catch(_ => {
          })
      },
      rest() {
        this.modelForm = {
          sysName: null,
          alias: null,
          installAddress: null,
          description: null,
          moduleTypeId: null,
          nodeType: 0,
          fatherId: 0,
          active: 1,
          synchState: 0,
          onlineState: 0
        }
      },
      //清除数据对比
      clearParmas() {
        this.underFormData = {}
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

  .error_input ::v-deep .el-input__inner {
    color: red;
  }
</style>
