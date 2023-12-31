package com.ruoyi.common.utils.sms.server;// This file is auto-generated, don't edit it. Thanks.

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.ruoyi.common.utils.sms.domain.AthenaBesSmsLog;
import com.ruoyi.common.utils.sms.mapper.SmsLogMapper;
import com.ruoyi.common.utils.sms.model.ALSmSParam;
import com.ruoyi.common.utils.sms.model.SmsParam;
import com.ruoyi.common.utils.sms.model.SmsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class SmsServer {

    private static final Logger log = LoggerFactory.getLogger(SmsServer.class);


    /**
     * 阿里云的  AccessKey ID
     */
    @Value("${alibaba.accessKeyId}")
    private String accessKeyId;
    /**
     * 阿里云的 AccessKey Secret
     */
    @Value("${alibaba.accessKeySecret}")
    private String accessKeySecret;
    /**
     * 阿里云的 实例地址
     */
    @Value("${alibaba.endpoint}")
    private String endpoint;

    /**
     * 签名
     */
    @Value("${alibaba.signName}")
    private String singName;
    /**
     * 模板
     */
    @Value("${alibaba.templateCode}")
    private String templateCode;

    @Resource
    private SmsLogMapper smsLogMapper;


    /**{
     "Message": "OK",
     "RequestId": "FF1BC126-08F6-5299-9197-B2CEB44BD73D",
     "Code": "OK",
     "BizId": "187310268914451714^0"
     }
     */
    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = endpoint;
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret,String endpoint) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = endpoint;
        return new com.aliyun.dysmsapi20170525.Client(config);
    }


    public SmsResult send(SmsParam smsParam) throws Exception {
        // 初始化 Client，采用 AK&SK 鉴权访问的方式
        com.aliyun.dysmsapi20170525.Client client = this.createClient(accessKeyId, accessKeySecret);
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName(singName)//签名
                .setTemplateCode(templateCode)//模板
                .setPhoneNumbers(smsParam.getRecipient())//接收方手机号
                .setTemplateParam("{\"code\":\"" + smsParam.getContext() + "\"}");//内容
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        AthenaBesSmsLog smsLog = new AthenaBesSmsLog();
        SmsResult reslut;
        try {
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
            SendSmsResponseBody body = sendSmsResponse.getBody();

            /**发送完成后存入数据库*/
            smsLog.setAddTime(new Date());
            smsLog.setSendTime(new Date());
            smsLog.setYwId(smsParam.getYwid());
            smsLog.setSendText(smsParam.getContext());
            smsLog.setSendJson(JSONObject.toJSONString(sendSmsRequest));
            smsLog.setType(1);
            smsLog.setRecipient(smsParam.getRecipient());
            smsLog.setResponseId(body.getRequestId());
            smsLog.setResponse(JSONObject.toJSONString(body));

            if (body.getCode().equals("ok")) {
                smsLog.setIsSuccess(1);

                /*发送成功*/
                reslut = SmsResult.success();
            } else {
                smsLog.setIsSuccess(0);
                /*发送失败*/
                reslut = SmsResult.fail(body.getMessage());
            }

        } catch (Exception _error) {
            smsLog.setIsSuccess(0);
            log.error("发送短信时出现错误", _error);
            reslut = SmsResult.fail(_error.getMessage());
        }
        smsLogMapper.insert(smsLog);
        return reslut;

    }


    public SmsResult sendbyConfig(ALSmSParam param)  {
        // 初始化 Client，采用 AK&SK 鉴权访问的方式
        com.aliyun.dysmsapi20170525.Client client = null;
        try {
            client = this.createClient(param.getAccesskeyid(), param.getSecret(),param.getRegionid());
        } catch (Exception e) {
            e.printStackTrace();
            return SmsResult.fail("初始化阿里云短信服务失败！");
        }
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName(singName)//签名
                .setTemplateCode(templateCode)//模板
                .setPhoneNumbers(param.getRecipient())//接收方手机号
                .setTemplateParam(param.getContext());/*模板填充*/
//                .setTemplateParam("{\"code\":\""+param.getContext()+"\"}");//内容
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        AthenaBesSmsLog smsLog = new AthenaBesSmsLog();
        SmsResult reslut;
        try {
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
            SendSmsResponseBody body = sendSmsResponse.getBody();

            /**发送完成后存入数据库*/
            smsLog.setAddTime(new Date());
            smsLog.setSendTime(new Date());
            smsLog.setYwId(param.getYwid());
            smsLog.setSendText(param.getContext());
            smsLog.setSendJson(JSONObject.toJSONString(sendSmsRequest));
            smsLog.setType(1);
            smsLog.setRecipient(param.getRecipient());
            smsLog.setResponseId(body.getRequestId());
            smsLog.setResponse(JSONObject.toJSONString(body));

            if (body.getCode().equals("ok")) {
                smsLog.setIsSuccess(1);

                /*发送成功*/
                reslut = SmsResult.success();
            } else {
                smsLog.setIsSuccess(0);
                /*发送失败*/
                reslut = SmsResult.fail(body.getMessage());
            }

        } catch (Exception _error) {
            smsLog.setIsSuccess(0);
            log.error("发送短信时出现错误", _error);
            reslut = SmsResult.fail(_error.getMessage());
        }
        smsLogMapper.insert(smsLog);
        return reslut;

    }
}
