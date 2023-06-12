package com.ruoyi.wxpayment.controller;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.result.ResultMap;
import com.ruoyi.common.utils.file.ZxingUtil;
import com.ruoyi.wxpayment.domain.PayOrder;
import com.ruoyi.wxpayment.service.IPayOrderService;
import com.ruoyi.wxpayment.service.WxPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Athena-YangChao
 * */
@RestController
@RequestMapping("/payment/wxPay")
public class WxPayController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(WxPayController.class);

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private IPayOrderService payOrderService;
    @Autowired
    private RedisCache redisCache;


    /**
     * 根据前台传输不同trade_type类型-跳转不同支付方式
     *
     * @param mchOrderNo     商户订单号
     * @param currentWayCode 支付方式
     * @param paytestAmount  支付金额
     * @param orderTitle     订单标题
     * @param request
     */
    @GetMapping("/order")
    public ResultMap order(String mchOrderNo, String currentWayCode, double paytestAmount, String orderTitle, HttpServletRequest request) {
        // 0. 先将订单号绑定token放到redis里面 支付异步通知回调使用webscoket推送到前台 --订单保存五分钟
        // 获取当前用户token
        String token = getLoginUser().getToken();
        redisCache.setCacheObject(Constants.PAYMENT_WEBSOCKET + mchOrderNo, token ,5, TimeUnit.SECONDS);

        ResultMap rm = new ResultMap();
        // 1、添加订单--具体流程看具体业务，在此只做简单流程
        PayOrder payOrder = new PayOrder();
        payOrder.setOrderNo(mchOrderNo);
        payOrder.setBody(orderTitle);
        payOrder.setType(currentWayCode);
        payOrder.setMoney(String.valueOf(paytestAmount));
        payOrder.setStatus(0);

        switch (currentWayCode) {
            // Native支付
            case "WX_NATIVE":
                try {
                    // 2、开始微信支付统一下单
                    ResultMap resultMap = wxPayService.unifiedOrder(mchOrderNo, "NATIVE", paytestAmount, orderTitle, request);
                    if ((Boolean) resultMap.get("state")) {
                        Map<String, String> returnMap = (Map<String, String>) resultMap.get("data");
                        String code_url = returnMap.get("code_url");
                        String prepayId = returnMap.get("prepayid");
                        payOrder.setPayNo(prepayId);

                        StringBuffer imgPath = new StringBuffer("/payment/temp/");
                        File dir = new File(imgPath.toString());
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        imgPath.append(prepayId).append(".png");

                        //产生付款二维码
                        ZxingUtil.qrCode(250, 250, code_url, "png", imgPath.toString());

                        //付款二维码返回到浏览器
                        File img = new File(imgPath.toString());
                        FileInputStream inputStream = new FileInputStream(img);
                        byte[] bytes = new byte[inputStream.available()];
                        inputStream.read(bytes, 0, inputStream.available());

//                    System.err.println(imgPath.toString());

                        rm.put("code_url", bytes);
                    } else {
                        ResultMap.error((Integer) resultMap.get("code"), String.valueOf(resultMap.get("msg")));
                    }
                } catch (Exception e) {
                    logger.error("扫码支付 qrcode 失败", e);
                    return null;
                }
                break;

            // JSAPI支付--小程序支付
            case "WX_JSAPI":
                break;

            // 条码支付
            case "WX_BAR":
                break;

            // H5支付
            case "WX_H5":
                break;

            default:
                break;
        }
        payOrderService.insertPayOrder(payOrder);
        return rm;
    }


    /**
     * 统一下单接口
     */
    @GetMapping("/unifiedOrder")
    public ResultMap unifiedOrder(String orderNo, String currentWayCode, double amount, String body, HttpServletRequest request) {
        try {
            // 1、验证订单是否存在

            // 2、开始微信支付统一下单
            ResultMap resultMap = wxPayService.unifiedOrder(orderNo, currentWayCode, amount, body, request);
            return resultMap;//系统通用的返回结果集
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultMap.error("运行异常，请联系管理员");
        }
    }

    /**
     * 微信支付异步通知
     */
    @RequestMapping(value = "/notify")
    public String payNotify(HttpServletRequest request) {
        InputStream is = null;
        String xmlBack = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[报文为空]]></return_msg></xml> ";
        try {
            is = request.getInputStream();
            // 将InputStream转换成String
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            xmlBack = wxPayService.notify(sb.toString());
        } catch (Exception e) {
            logger.error("微信支付回调通知失败：", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return xmlBack;
    }

    @PreAuthorize("@ss.hasPermi('payment:example:refund')")
    @PostMapping("/refund")
    public ResultMap refund(String orderNo, double amount, String refundReason) {

        return wxPayService.refund(orderNo, amount, refundReason);
    }

}
