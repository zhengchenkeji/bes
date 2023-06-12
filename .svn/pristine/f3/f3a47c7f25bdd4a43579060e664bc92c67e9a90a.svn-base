package com.ruoyi.wxpayment.service;

import com.ruoyi.common.core.result.ResultMap;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Athena-YangChao
 * 微信支付服务接口
 */
public interface WxPayService {

    /**
     * @Description 微信支付统一下单
     * @param orderNo: 订单编号
     * @param amount: 实际支付金额
     * @param body: 订单描述
     * @return
     */
    ResultMap unifiedOrder(String orderNo, String currentWayCode, double amount, String body, HttpServletRequest request) ;

    /**
     * @Description 订单支付异步通知
     * @param notifyStr: 微信异步通知消息字符串
     * @return
     */
    String notify(String notifyStr) throws Exception;

    /**
     * @Description: 退款
     * @param orderNo: 订单编号
     * @param amount: 实际支付金额
     * @param refundReason: 退款原因
     * @return
     */
    ResultMap refund(String orderNo, double amount, String refundReason);

}