<template>
  <div class="white-body-view">
    <div class="fromHeader">
      <el-form :rules="rules" :inline="true" ref="queryform" :model="treeNodeMsg" label-width="80px">
        <el-form-item label="名称">
          <el-input maxlength="24" v-model=treeNodeMsg.sysName disabled></el-input>
        </el-form-item>
        <el-form-item label="别名" prop="nickName">
          <el-input v-model="treeNodeMsg.nickName" maxlength="24" show-word-limit></el-input>
        </el-form-item>
        <br/>
        <el-form-item label="通讯地址" prop="portNum">
          {{prefix}}<el-select v-model="treeNodeMsg.portNum" style="width: 180px">
            <el-option v-for="item in slaveAddressList" :key="item.id" :label="item.value" :value="item.id">
            </el-option>
          </el-select>

        </el-form-item>
      </el-form>
      <div style="text-align: center">
        <el-button
          v-hasPermi="['basicData:deviceTree:point:updateLightingTree']"
          type="primary" @click="submitForm('queryform')" :disabled="saveBoolean">保存</el-button>
      </div>
    </div>

    <!-- 新增干线、支线 -->
    <el-drawer size='35%'  :title="title" :visible.sync="visible" :before-close="handleClose" direction="rtl">
      <div style="margin-left: 15px; margin-right: 15px">
        <div style="margin-bottom: 15px;">
          <!-- 选择下拉框和搜索 -->
          <el-row :span="10" :xs="24">
            <el-col>
              <el-form :inline="true" :rules="rules" ref="insertParams" :model="insertParams" label-width="90px">
                <el-form-item label="系统名称" prop="sysName">
                  <el-input v-model.trim="insertParams.sysName" maxlength="24" show-word-limit placeholder="不能输入汉字"></el-input>
                </el-form-item>

                <el-form-item label="别名" prop="nickName">
                  <el-input v-model="insertParams.nickName" maxlength="24" show-word-limit></el-input>
                </el-form-item>
                <br/>

                <el-form-item label="通信地址" prop="showNum">
                  {{prefix}}<el-select v-model="insertParams.showNum" style="width: 180px">
                    <el-option v-for="item in slaveAddressList" :key="item.id" :label="item.value" :value="item.id">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
              <el-form :inline="true" label-width="80px" style="text-align: center">
                <el-form-item>
                  <el-button
                    v-hasPermi="['basicData:deviceTree:point:insertLightingTree']"
                    type="primary" @click="submitForm('insertParams')">新增</el-button>
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
  insertLightingTree, updateLightingTree
} from '@/api/basicData/deviceManagement/deviceTree/deviceTreePoint'
import { deviceTreeSettings } from '../../../../../api/basicData/deviceManagement/deviceTree/deviceTreeSettings'
export default {
  props: {
    treeNodeMsgs: {
      type: Object,
      default: {
      }
    },
  },
  created() {

    this.queryslaveAddressList()
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
      //数值未修改 保存按钮禁用
      saveBoolean: true,
      watchBoolean: false,//判断是否是初始赋值
      deviceTreeSettings: deviceTreeSettings,
      insertParams: {
        nodeType: null,//设备树node
        nickName: null,//系统名称
        sysName: null,//系统名称
        fatherId: null,//设备树父Id
        controllerId: null,//DDC id
        portNum: null,//端口
        deviceId: null, //当前树节点id
        showNum: null//当前显示的端口
      },
      treeNodeMsg: {},
      slaveAddressList: [],//通讯地址
      lineType: undefined,//当前新增节点类型
      visible: false,//右侧弹窗是否弹出
      deviceNodeId: null,//设备树node
      sysName: null,//系统名称
      deviceTreeFatherId: null,//设备树父Id
      controllerId: null,//DDC id
      title: '新增节点',
      prefix: null,
      queryform: {},
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
        sysName: [{ required: true, message: '别名不能为空', trigger: 'blur' },
          {pattern: /^[a-zA-Z]/, message: "首位只能输入英文字母",},
          {validator: checkChinese},
          {validator: checkinput},
        ],
        nickName: [{ required: true, message: '别名不能为空', trigger: 'blur' },
          {validator: checkText},
        ],
        portNum: [{ required: true, message: '通信地址不能为空', trigger: 'change' }],
        showNum: [{ required: true, message: '通信地址不能为空', trigger: 'change' }],
      }
    }
  },
  computed: {
    watchList: function() {
        const obj = {}
        Object.keys(this.treeNodeMsg).forEach(key => {
          obj[key] = this.treeNodeMsg[key]
        })
        return obj
      },
  },
  // 监听数据变化
  watch: {
    watchList: {
      handler(newVal, oldVal) {
        if (this.watchBoolean) {
          if (newVal != oldVal) {
            this.saveBoolean = false;
          }
        }
      },
      deep: true,
      immediate: false,
    },

    treeNodeMsgs() {

      //未赋值时  过滤掉 值的改变
      this.watchBoolean = false
      this.treeNodeMsg = this.treeNodeMsgs;
      this.$nextTick(function (){
        this.$refs.queryform.clearValidate();
        //赋值后 监听值的改变
        this.watchBoolean = true
        this.saveBoolean = true
      })
    },
    // immediate: true,
  },
  methods: {
    checkprefix(prefix) {
      console.log(prefix);
      if (prefix == null || prefix == "") {
        this.prefix = "1."
      } else {
        this.prefix = prefix;
      }
    },
    queryslaveAddressList() {//查询通讯地址列表
      var arr = []
      for (let i = 1; i <= 255; i++) {
        var address = { id: i, value: i }
        arr.push(address)
      }
      this.slaveAddressList = arr

    },
    // 表单重置
    reset() {
      this.insertParams = {
      };
      this.resetForm("form");
    },
    //关闭弹窗
    handleClose(done) {
      this.$confirm('确认关闭？').then(_ => {
        done();
        //重新加载当前选中的树节点
        this.$emit('ReloadNode');
      }).catch(_ => { });
    },
    //添加树节点
    submitForm(formName) {
      let that = this
      // console.log(this.prefix+that.insertParams.portNum+".")
      this.$refs[formName].validate(valid => {

        if (valid) {
          if (formName == 'queryform') {
            if (this.prefix == null) {
              that.insertParams.portNum = that.treeNodeMsg.portNum + ".";
            } else {
              that.insertParams.portNum = this.prefix + that.treeNodeMsg.portNum + ".";
            }
            that.insertParams.nickName = this.treeNodeMsg.nickName;
            that.insertParams.sysName = this.treeNodeMsg.sysName;
            that.insertParams.fatherId = this.treeNodeMsg.fatherId;
            // that.insertParams.park = this.treeNodeMsg.park;
            that.insertParams.treeId = this.treeNodeMsg.deviceTreeId;
            that.insertParams.nodeType = this.treeNodeMsg.nodeType;
            updateLightingTree(this.insertParams).then(response => {
              if (response.code == 200) {
                let treenode = response.data;
                this.$modal.msgSuccess("修改成功");
                this.saveBoolean = true;
                treenode.alias = that.insertParams.nickName
                that.$emit('updateTreeNode', response.data);
                this.visible = false;
              } else {

              }
            });
          } else {
            that.insertParams.nodeType = this.lineType
            that.insertParams.fatherId = this.treeNodeMsg.deviceTreeId
            that.insertParams.portNum = this.prefix + that.insertParams.showNum + ".";
            insertLightingTree(that.insertParams).then(response => {
              if (response.code == 200) {
                this.$modal.msgSuccess("新增成功");
                let treenode = response.data;
                that.reset();
                treenode.alias = that.insertParams.nickName
                that.$emit('addTreeNode', treenode);
                this.visible = false;
              }
            });
          }
        }
      });


    }
  }
}
</script>
<style lang="scss" scoped>
.white-body-view {
  width: 100%;
  min-width: 320px;
}
</style>
