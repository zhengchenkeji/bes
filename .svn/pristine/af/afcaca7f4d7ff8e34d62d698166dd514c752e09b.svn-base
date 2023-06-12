<template>
  <div class="paydemo">
    <div class="content" style="padding-top: 30px;">
      <div style="width: 100%;">

        <!--标题-->
        <el-row :gutter="24" style="background-color: rgb(255, 242, 224);border-radius: 4px;">
          <div style="color: rgb(68, 181, 73);">
            <p style="padding:1vw 3vw;">统一支付模块集成微信和支付宝SDK，支持微信、支付宝的服务商和普通商户模式下单，该页面提供支付体验流程。</p>
          </div>
        </el-row>
        <!--微信支付-->
        <el-row :gutter="16" style="margin-top: 2vh;">
          <el-card class="box-card" :body-style="{ height: '12vh'}">
            <div slot="header" class="clearfix">
              <span>微信支付</span>
            </div>
            <el-col :span="4">
              <div class="paydemo-type color-change" @click="changeCurrentWayCode('WX_NATIVE', 'codeImgUrl', '0')"
                   :class="{this:(pay.currentWayCode === 'WX_NATIVE')}">
                <img src="@/assets/payTestImg/wx_native.svg" class="paydemo-type-img">
                <span class="color-change">微信二维码</span>
              </div>
            </el-col>

            <el-col :span="4">
              <div class="paydemo-type color-change" @click="changeCurrentWayCode('WX_BAR', '', '0')"
                   :class="{this:(pay.currentWayCode === 'WX_BAR')}">
                <img src="@/assets/payTestImg/wx_bar.svg" class="paydemo-type-img"><span
                class="color-change">微信条码</span>
              </div>
            </el-col>

            <el-col :span="4">
              <div class="paydemo-type color-change" @click="changeCurrentWayCode('WX_JSAPI', 'codeImgUrl', '0')"
                   :class="{this:(pay.currentWayCode === 'WX_JSAPI')}">
                <img src="@/assets/payTestImg/wx_jsapi.svg" class="paydemo-type-img"><span
                class="color-change">公众号/小程序</span>
              </div>
            </el-col>

            <el-col :span="4">
              <div class="paydemo-type paydemo-type-h5" @click="changeCurrentWayCode('WX_H5', 'payurl', '0')"
                   :class="{this:(pay.currentWayCode === 'WX_H5')}">
                <img src="@/assets/payTestImg/wx_h5.svg" class="paydemo-type-img"><span class="color-change">微信H5</span>
              </div>
            </el-col>

          </el-card>
        </el-row>
        <!--支付宝支付-->
        <el-row :gutter="16" style="margin-top: 2vh;">
          <el-card class="box-card" :body-style="{ height: '12vh'}">
            <div slot="header" class="clearfix">
              <span>支付宝支付</span>
            </div>
            <el-col :span="4">
              <div class="paydemo-type color-change" @click="changeCurrentWayCode('ALI_PC', 'payurl', '1')"
                   :class="{this:(pay.currentWayCode === 'ALI_PC')}">
                <img src="@/assets/payTestImg/ali_pc.svg" class="paydemo-type-img">
                <span class="color-change">支付宝PC网站</span>
              </div>
            </el-col>

            <el-col :span="4">
              <div class="paydemo-type color-change" @click="changeCurrentWayCode('ALI_QR', 'codeImgUrl', '1')"
                   :class="{this:(pay.currentWayCode === 'ALI_QR')}">
                <img src="@/assets/payTestImg/ali_qr.svg" class="paydemo-type-img">
                <span class="color-change">支付宝二维码</span>
              </div>
            </el-col>

            <el-col :span="4">
              <div class="paydemo-type color-change" @click="changeCurrentWayCode('ALI_JSAPI', 'codeImgUrl', '1')"
                   :class="{this:(pay.currentWayCode === 'ALI_JSAPI')}">
                <img src="@/assets/payTestImg/ali_jsapi.svg" class="paydemo-type-img">
                <span class="color-change">支付宝生活号</span>
              </div>
            </el-col>

            <el-col :span="4">
              <div class="paydemo-type paydemo-type-h5" @click="changeCurrentWayCode('ALI_WAP', 'payurl', '1')"
                   :class="{this:(pay.currentWayCode === 'ALI_WAP')}">
                <img src="@/assets/payTestImg/ali_wap.svg" class="paydemo-type-img">
                <span class="color-change">支付宝WAP</span>
              </div>
            </el-col>

          </el-card>
        </el-row>
        <!--支付信息-->
        <el-row :gutter="24" style="margin-top: 2vh;">
          <el-card class="box-card" :body-style="{ height: '20vh'}">
            <el-descriptions title="支付信息" :column="1">
              <el-descriptions-item label="订单编号：">
                <span>{{ pay.mchOrderNo }}</span>
                <el-button @click="randomOrderNo" style="padding: 2px 14px;margin-left: 10px;" size="mini">刷新订单号
                </el-button>
              </el-descriptions-item>
              <el-descriptions-item label="商品名称：">
                <span>{{ pay.orderTitle }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="支付金额(元)：">
                <el-radio-group size="mini" v-model="pay.radio" @change="payAmountChange($event)">
                  <el-radio v-model="pay.paytestAmount" :label="0.01">￥0.01</el-radio>
                  <el-radio v-model="pay.paytestAmount" :label="0.15">￥0.15</el-radio>
                  <el-radio v-model="pay.paytestAmount" :label="0.5">￥0.5</el-radio>
                </el-radio-group>
              </el-descriptions-item>
            </el-descriptions>

            <el-button @click="immediatelyPay"
                       style="float: right;margin: 0 1vw 2vh 0;padding:10px 20px;background-color: #1D58A9;border-radius: 5px;color:#fff"
                       type="primary">立即支付
            </el-button>
          </el-card>

        </el-row>
        <!--订单详情-->
        <el-row :gutter="24" style="margin-top: 2vh;margin-bottom: 5vh;">
          <el-card class="box-card" :body-style="{ height: '65vh'}" style="overflow: auto">

            <el-table border v-loading="order.loading" :data="order.list">
              <el-table-column label="订单编号" width="170" align="center" prop="orderNo"/>
              <el-table-column label="商品名称" align="center" prop="body"/>
              <el-table-column label="支付方式" align="center" prop="type"/>
              <el-table-column label="支付金额" width="80" align="center" prop="money"/>
              <el-table-column label="支付状态" align="center">
                <template slot-scope="scope">
                  <el-tag size="small" v-if="scope.row.status==0">待支付</el-tag>
                  <el-tag size="small" v-if="scope.row.status==1" style="color: #00ffc8">已支付</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="支付单号" align="center" prop="payNo"/>
              <el-table-column label="支付时间" align="center" prop="payTime"/>
              <el-table-column label="插入时间" align="center" prop="addTime" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.addTime)}}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button disabled size="mini" type="text" icon="el-icon-remove-outline"
                             @click="handleRefund(scope.row)"
                             v-hasPermi="['payment:example:refund']">退款
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination
              v-show="order.total>0"
              :total="order.total"
              :page.sync="queryParams.pageNum"
              :limit.sync="queryParams.pageSize"
              @pagination="getList"
            />

          </el-card>

        </el-row>

      </div>

      <!-- 二维码弹窗 -->
      <!-- 用户导入对话框 -->
      <el-dialog :title="pay.code_title" :visible.sync="pay.open" width="300px" append-to-body>
        <div style="display: flex;flex-direction: column;align-items: center;">
          <img :src="pay.code_url" class="paydemo-type-img">
          <div style="padding-top: 20px;display: flex;flex-direction: row;align-items: center;justify-content: center;">
            <img style="width: 25px;padding-right: 5px;" src="@/assets/payTestImg/wx_app.svg">请使用微信"扫一扫"扫码支付
          </div>
        </div>

      </el-dialog>

    </div>
  </div>
</template>
<script>
  import {
    listOrder, idePayment, refundPayment , aliPayment
  } from '@/api/payment/order'
  import { getToken } from "@/utils/auth";
  import { mapState } from 'vuex'

  export default {
    name: 'payment',
    data() {
      return {
        // 支付
        pay: {
          payType: '0',// 0微信 1支付宝
          mchOrderNo: '', // 模拟商户订单号
          orderTitle: '接口调试', // 订单标题/商品名称
          paytestAmount: '0.01', // 支付金额，默认为0.01
          currentWayCode: 'WX_NATIVE', // 以何种方式进行支付，默认是微信二维码
          currentPayDataType: '', // 支付参数（二维码，条码）
          code_title: '扫码支付',
          code_url: null,// 支付二维码
          token: getToken(),
          open: false,// 扫码弹窗
          radio: 0.01
        },
        // 订单
        order: {
          list: [],// 订单list
          total: 0,// 总条数
          loading: true// 遮罩层
        },
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          orderNo: null,
          body: null,
          money: null,
          status: null,
          payNo: null,
          payTime: null,
          addTime: null
        },
        sideTheme:null,
      }
    },
    computed: {
      ...mapState({
        payment_websocket: state => state.websocket.payment
      })
    },
    watch: {
      payment_websocket( tag ){
        this.$message.success("支付成功，关闭弹窗！")
        this.pay.open = false;
      },
      deep: true,
    },
    created() {
      this.randomOrderNo()// 先刷新订单号
      this.getList()// 查询订单list
    },
    methods: {
      // 刷新订单号
      randomOrderNo() {
        this.pay.mchOrderNo = 'M' + new Date().getTime() + Math.floor(Math.random() * (9999 - 1000) + 1000)
      },
      // 查询支付_订单列表
      getList() {
        this.loading = true
        listOrder(this.queryParams).then(response => {
          this.order.list = response.rows
          this.order.total = response.total
          this.order.loading = false
        })
      },
      // 金额切换
      payAmountChange(data){
        this.pay.paytestAmount = data
      },
      // 切换支付方式
      changeCurrentWayCode(wayCode, currentPayDataType, payType) {
        this.pay.currentWayCode = wayCode
        this.pay.currentPayDataType = currentPayDataType
        this.pay.payType = payType
      },
      // 立即支付按钮
      immediatelyPay() {
        // 判断是微信支付还是支付宝支付
        const payType = this.pay.payType;
        if(payType === '0'){// 微信支付
          idePayment(this.pay).then(response => {
            // 打开弹窗
            this.pay.open = true
            // 付款二维码赋值
            this.pay.code_url = 'data:image/jpeg;base64,' + response.code_url
            // 刷新订单号
            this.randomOrderNo()
            // 刷新订单列表
            this.getList()
          })
        }else if(payType === '1'){// 支付宝支付
          aliPayment(this.pay).then(resp => {
            let divForm = document.getElementsByTagName("div");
            if (divForm.length) {
              document.body.removeChild(divForm[0]);
            }
            const div = document.createElement("div");
            div.innerHTML = resp; // data就是接口返回的form 表单字符串
            document.body.appendChild(div);
            // document.forms[0].setAttribute("target", "_blank"); // 新开窗口跳转
            document.forms[0].submit();
          })
        }else{
          this.$modal.msgWarning("请选择支付方式");
        }
      },
      // 退款按钮
      handleRefund(row) {
        let refund = {
          orderNo: row.orderNo,// 商户订单号
          amount: row.money,// 金额
          refundReason: '测试退款'
        }
        refundPayment(refund).then(response => {
          // 刷新订单号
          this.randomOrderNo()
          // 刷新订单列表
          this.getList()
        })
      }
    }
  }
</script>

<style scoped lang="css">
  @import 'payTest.css';

  ::v-deep .el-dialog:not(.is-fullscreen) {
    margin-top: 15vh !important;
  }
  .theme-blue .el-card{
      background-color: #004a77;
  }
  .theme-blue .paydemo ::v-deep .el-card{border-color:#4f8aaa;}
  .theme-blue .el-card__header .clearfix{color:white;}
  .theme-blue .paydemo .paydemo-type{color:white;border-color:#4f8aaa;}
  .theme-blue .paydemo .this{color: #1987c2;border-color:#4f8aaa;}
  .theme-blue .paydemo ::v-deep .el-card__header{border-bottom:1px solid #4f8aaa;}
  .theme-blue .paydemo ::v-deep .el-descriptions__title{color:white;}
  .theme-blue .paydemo ::v-deep .el-descriptions__body{background-color:#004a77;}
  .theme-blue .paydemo ::v-deep .el-table--border{border:1px solid #4f8aaa;}
</style>
