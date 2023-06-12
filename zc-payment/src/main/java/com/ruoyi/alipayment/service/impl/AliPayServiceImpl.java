package com.ruoyi.alipayment.service.impl;

import com.alipay.api.AlipayApiException;
import com.ruoyi.alipayment.config.Alipay;
import com.ruoyi.alipayment.pojo.Order;
import com.ruoyi.alipayment.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description 支付service 实现类
 * @author Athena-YangChao
 */
@Service
public class AliPayServiceImpl implements AliPayService {

    @Autowired
    private Alipay alipay;

    @Override
    public String aliPay(Order order) throws AlipayApiException {
        return alipay.pay(order);
    }
}
