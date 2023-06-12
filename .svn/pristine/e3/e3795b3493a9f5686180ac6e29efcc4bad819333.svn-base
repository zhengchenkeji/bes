package com.ruoyi.alipayment.controller;

import com.alipay.api.AlipayApiException;
import com.ruoyi.alipayment.pojo.Order;
import com.ruoyi.alipayment.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description 支付宝沙箱测试 controller
 * @author Athena-YangChao
 */
@RestController
@RequestMapping("/payment/aliPay")
public class PayController {

    @Autowired
    private AliPayService aliPayService;

    /**
     * 支付宝支付 api
     *
     * @param mchOrderNo 订单号
     * @param orderTitle 商品名称
     * @param paytestAmount 商品价格
     * @param description 商品描述（内容）
     * @return
     * @throws AlipayApiException
     */
    @PostMapping(value = "/order/alipay")
    public String alipay(String mchOrderNo, String orderTitle,
                         String paytestAmount, String description) throws AlipayApiException {
        Order order = new Order();
        order.setOut_trade_no(mchOrderNo);
        order.setSubject(orderTitle);
        order.setTotal_amount(paytestAmount);
        order.setDescription(description);
        return aliPayService.aliPay(order);
    }

    /**
     * 微信支付异步通知
     */
    @RequestMapping(value = "order/notify")
    public String payNotify(HttpServletRequest request) {
        String a = "";
        
        return "";
    }
}
