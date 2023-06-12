package com.ruoyi.wxpayment.service.impl;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.result.ResultMap;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.wxpayment.config.WxPayAppConfig;
import com.ruoyi.wxpayment.domain.PayOrder;
import com.ruoyi.wxpayment.service.IPayOrderService;
import com.ruoyi.wxpayment.service.WxPayService;
import com.zc.common.core.websocket.WebSocketService;
import com.zc.common.constant.WebSocketEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxPayServiceImpl implements WxPayService {
    private final Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);

    @Autowired
    private WxPayAppConfig wxPayAppConfig;
    @Autowired
    private IPayOrderService payOrderService;
    @Autowired
    private RedisCache redisCache;

    @Override
    public ResultMap unifiedOrder(String orderNo, String currentWayCode, double amount, String body, HttpServletRequest request) {
        Map<String, String> returnMap = new HashMap<>();
        Map<String, String> responseMap = new HashMap<>();
        Map<String, String> requestMap = new HashMap<>();
        try {
            WXPay wxpay = new WXPay(wxPayAppConfig);
            requestMap.put("body", body);                                     // 商品描述
            requestMap.put("out_trade_no", orderNo);                          // 商户订单号
            requestMap.put("total_fee", String.valueOf((int) (amount * 100)));   // 总金额
            requestMap.put("spbill_create_ip", IpUtils.getIpAddr(request)); // 终端IP
            requestMap.put("trade_type", currentWayCode);                     // 支付类型
            requestMap.put("notify_url", wxPayAppConfig.getPayNotifyUrl());   // 接收微信支付异步通知回调地址
            Map<String, String> resultMap = wxpay.unifiedOrder(requestMap);
            //获取返回码
            String returnCode = resultMap.get("return_code");
            String returnMsg = resultMap.get("return_msg");
            // 二维码链接--trade_type=NATIVE时有返回，此url用于生成支付二维码，然后提供给用户进行扫码支付。
            String imageContent = resultMap.get("code_url");
            // 预支付交易会话标识
            String prepayId = resultMap.get("prepay_id");


            //若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
            if ("SUCCESS".equals(returnCode)) {
                String resultCode = resultMap.get("result_code");
                String errCodeDes = resultMap.get("err_code_des");
                if ("SUCCESS".equals(resultCode)) {
                    responseMap = resultMap;
                }
            }
            if (responseMap == null || responseMap.isEmpty()) {
                return ResultMap.error("获取预支付交易会话标识失败");
            }
            // 3、签名生成算法
            Long time = System.currentTimeMillis() / 1000;
            String timestamp = time.toString();
            returnMap.put("appid", wxPayAppConfig.getAppID());
            // 商户号
            returnMap.put("partnerid", wxPayAppConfig.getMchID());
            // 预支付交易会话标识
            returnMap.put("prepayid", responseMap.get("prepay_id"));
            // 随机字符串
            returnMap.put("noncestr", responseMap.get("nonce_str"));
            returnMap.put("code_url", responseMap.get("code_url"));
            returnMap.put("timestamp", timestamp);
            returnMap.put("package", "Sign=WXPay");
            returnMap.put("sign", WXPayUtil.generateSignature(returnMap, wxPayAppConfig.getKey()));//微信支付签名
            return ResultMap.ok().put("data", returnMap);
        } catch (Exception e) {
            logger.error("订单号：{}，错误信息：{}", orderNo, e.getMessage());
            return ResultMap.error("微信支付统一下单失败");
        }
    }

    @Override
    public String notify(String notifyStr) {
        String xmlBack = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[报文为空]]></return_msg></xml> ";
        try {
            // 转换成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(notifyStr);
            WXPay wxpayApp = new WXPay(wxPayAppConfig);
            if (wxpayApp.isPayResultNotifySignatureValid(resultMap)) {
                String returnCode = resultMap.get("return_code");  //状态
                String outTradeNo = resultMap.get("out_trade_no");  //商户订单号
                String transactionId = resultMap.get("transaction_id");
                if ("SUCCESS".equals(returnCode)) {
                    if (StringUtils.isNotBlank(outTradeNo)) {
                        /**
                         * 注意！！！
                         * 请根据业务流程，修改数据库订单支付状态，和其他数
                         * 据的相应状态
                         *
                         * 1. 修改该订单号得支付状态
                         * 2， 添加支付时间
                         */
                        PayOrder payOrder = new PayOrder();
                        payOrder.setOrderNo(outTradeNo);
                        payOrder.setStatus(1);
                        payOrder.setPayTime(DateUtils.getTime());
                        payOrderService.updatePayOrderWithOrderNo(payOrder);

                        // WebScoket 推送数据给前台
                        String token = redisCache.getCacheObject(Constants.PAYMENT_WEBSOCKET + outTradeNo);
                        WebSocketService.postEvent(token, WebSocketEvent.PAYMENT, "success"+DateUtils.getTime());
                        logger.info("微信手机支付回调成功,订单号:{}", outTradeNo);
                        xmlBack = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlBack;
    }

    @Override
    public ResultMap refund(String orderNo, double amount, String refundReason) {

        if (StringUtils.isBlank(orderNo)) {
            return ResultMap.error("订单编号不能为空");
        }
        if (amount <= 0) {
            return ResultMap.error("退款金额必须大于0");
        }

        Map<String, String> responseMap = new HashMap<>();
        Map<String, String> requestMap = new HashMap<>();
        WXPay wxpay = new WXPay(wxPayAppConfig);
        requestMap.put("out_trade_no", orderNo);
//        requestMap.put("out_refund_no", UUIDGenerator.getOrderNo());
//        requestMap.put("total_fee", "订单支付时的总金额，需要从数据库查");
        requestMap.put("total_fee", String.valueOf((int) (amount * 100)));
        requestMap.put("refund_fee", String.valueOf((int) (amount * 100)));//所需退款金额
        requestMap.put("refund_desc", refundReason);
        try {
            responseMap = wxpay.refund(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String return_code = responseMap.get("return_code");   //返回状态码
        String return_msg = responseMap.get("return_msg");     //返回信息
        if ("SUCCESS".equals(return_code)) {
            String result_code = responseMap.get("result_code");       //业务结果
            String err_code_des = responseMap.get("err_code_des");     //错误代码描述
            if ("SUCCESS".equals(result_code)) {
                //表示退款申请接受成功，结果通过退款查询接口查询
                //修改用户订单状态为退款申请中或已退款。退款异步通知根据需求，可选
                //
                return ResultMap.ok("退款申请成功");
            } else {
                logger.info("订单号:{}错误信息:{}", orderNo, err_code_des);
                return ResultMap.error(err_code_des);
            }
        } else {
            logger.info("订单号:{}错误信息:{}", orderNo, return_msg);
            return ResultMap.error(return_msg);
        }
    }

}
