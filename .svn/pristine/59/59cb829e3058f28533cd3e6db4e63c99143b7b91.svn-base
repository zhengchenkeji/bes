package com.ruoyi.common.utils.sms.server;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nls.client.AccessToken;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sms.domain.AthenaBesSmsLog;
import com.ruoyi.common.utils.sms.mapper.SmsLogMapper;
import com.ruoyi.common.utils.sms.model.ALSmSParam;
import com.ruoyi.common.utils.sms.model.EmailParam;
import com.ruoyi.common.utils.sms.model.SmsResult;
import com.zc.common.constant.RedisKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * description:通知相关操作类
 * author: sunshangeng
 * date:2023/2/21 10:24
 */
@Component
public class NoticeServer {

    private static final Logger log = LoggerFactory.getLogger(NoticeServer.class);

    @Resource
    private RedisCache redisCache;

    @Resource
    private SmsLogMapper smsLogMapper;




    /**
     * @description:获取阿里巴巴token
     * @author: sunshangeng
     * @date: 2023/2/21 10:38
     * @param: [param]
     * @return: com.ruoyi.common.utils.sms.model.SmsResult
     **/
    public SmsResult getAlibabaToken(ALSmSParam param) {
        /**token：ccd8b574469f4a078280f481583bc62c
         expireTime：1669126382*/

        String token = redisCache.getCacheObject(RedisKeyConstants.BES_BasicData_Albaba_Token+"_"+param.getYwid());
        if (StringUtils.isNotEmpty(token)) {

            return SmsResult.success(token);
        }

        AccessToken accessToken = new AccessToken(param.getAccesskeyid(), param.getSecret());
//         AccessToken accessToken = new AccessToken("LTAI5tBJ1mUWXqXXpn1NQGTt", "0urwsahlCfs9PO3ZU6SLyoT9NP0H4I");


        try {
            accessToken.apply();
        } catch (IOException e) {
            log.error("获取阿里巴巴token时报错：", e);
            e.printStackTrace();
            return SmsResult.fail("获取阿里巴巴token时报错："+e.getMessage());
        }
        token = accessToken.getToken();
        if(StringUtils.isBlank(token)){
            return SmsResult.fail("获取阿里巴巴语音播报token，请检查配置的Accesskeyid，Secret是否正确");

        }
//       System.out.println("token："+token);
        long expireTime = accessToken.getExpireTime();
//       System.out.println("expireTime："+expireTime);
        /*将获取的时间戳转换为毫秒时间戳*/
        Long time = expireTime * 1000;
        long nowtime = System.currentTimeMillis();
        /*计算出当前需要存储的时间戳减去两个小时 获得需要存储的时间*/
        time = ((time - nowtime) / 1000) - (60 * 60 * 2);
        redisCache.setCacheObject(RedisKeyConstants.BES_BasicData_Albaba_Token+"_"+param.getYwid(), token, time.intValue(), TimeUnit.SECONDS);
        return SmsResult.success(token);
    }


    /**
     * @description:发送纯文本邮件-html
     * @author: sunshangeng
     * @date: 2023/2/20 16:36
     * @param: [param]
     * @return: com.ruoyi.common.utils.sms.model.SmsResult
     **/
    public SmsResult sendMessageByConfig(EmailParam param) {
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        senderImpl.setHost(param.getHost());
        senderImpl.setPort(param.getPort());
        senderImpl.setUsername(param.getFromEamil());
        senderImpl.setPassword(param.getFromEamilkey());
        senderImpl.setDefaultEncoding("UTF-8");
        AthenaBesSmsLog smsLog = new AthenaBesSmsLog();
        try {
            /**创建发送html邮件对象*/
            MimeMessage mimeMessage = senderImpl.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(param.getFromEamil()); // 设置发送发
            helper.setTo(param.getRecipient()); // 设置接收方
            helper.setSubject(param.getTitle()); // 设置邮件主题
            helper.setText(param.getContext(),true); // 设置邮件内容
//            创建纯文本邮件
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setFrom(param.getFromEamil()); // 设置发送发
//            msg.setTo(param.getRecipient()); // 设置接收方
//            msg.setSubject(param.getTitle()); // 设置邮件主题
//            msg.setText(param.getContext(),true); // 设置邮件内容
            smsLog.setAddTime(new Date());
            smsLog.setSendTime(new Date());
            smsLog.setYwId(param.getYwid());
            smsLog.setYwTable(param.getYwtable());
            smsLog.setSendText(param.getContext());
            smsLog.setRecipient(param.getRecipient());
            smsLog.setSendJson(JSONObject.toJSONString(param));
            smsLog.setType(2);
            smsLog.setNoticeConfig(param.getConfigId());
            smsLog.setNoticeTemplate(param.getTemplateId());
            smsLog.setIsSuccess(1);
            // 发送邮件
//            mailSender.send(msg);
            senderImpl.send(mimeMessage);
        } catch (Exception e) {
            smsLog.setIsSuccess(0);
            smsLogMapper.insert(smsLog);
            log.error("发送邮箱时出现问题", e);
            return SmsResult.fail(e.getMessage());
        }
        try {
            smsLogMapper.insert(smsLog);
        } catch (Exception e) {
            log.error("保存消息体时出错", e);
        }
        return SmsResult.success();
    }
    /**
     * @description:发送带附件的邮件
     * @author: sunshangeng
     * @date: 2023/2/20 16:36
     * @param: [param]
     * @return: com.ruoyi.common.utils.sms.model.SmsResult
     **/
    public SmsResult sendMessageByConfigFiles(EmailParam param) {
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        senderImpl.setHost(param.getHost());
        senderImpl.setPort(param.getPort());
        senderImpl.setUsername(param.getFromEamil());
        senderImpl.setPassword(param.getFromEamilkey());
        senderImpl.setDefaultEncoding("UTF-8");
        AthenaBesSmsLog smsLog = new AthenaBesSmsLog();
        MimeMessage mimeMessage = senderImpl.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(param.getFromEamil()); // 设置发送发
            helper.setTo(param.getRecipient()); // 设置接收方
            helper.setSubject(param.getTitle()); // 设置邮件主题
            helper.setText(param.getContext(),true); // 设置邮件内容
            if (param.getFiles() != null && param.getFiles().size() > 0) { // 添加附件（多个）
                for (File file : param.getFiles()) {
                    helper.addAttachment(file.getName(), file);
                }
            }

            smsLog.setAddTime(new Date());
            smsLog.setSendTime(new Date());
            smsLog.setYwId(param.getYwid());
            smsLog.setSendText(param.getContext());
            smsLog.setRecipient(param.getRecipient());
            smsLog.setYwTable(param.getYwtable());
            smsLog.setSendJson(JSONObject.toJSONString(param));
            smsLog.setNoticeConfig(param.getConfigId());
            smsLog.setNoticeTemplate(param.getTemplateId());
            smsLog.setType(2);
            smsLog.setIsSuccess(1);

            senderImpl.send(mimeMessage);
        } catch (Exception e) {
            smsLog.setIsSuccess(0);
            smsLogMapper.insert(smsLog);

            log.error("发送邮箱时出现问题", e);
            return SmsResult.fail(e.getMessage());
        }
        try {
            smsLogMapper.insert(smsLog);
        } catch (Exception e) {
            log.error("保存消息体时出错", e);
        }
        return SmsResult.success();
    }


    /**
     * @description:初始化短信阿里client
     * @author: sunshangeng
     * @date: 2023/2/21 10:37
     * @param: [accessKeyId, accessKeySecret, endpoint]
     * @return: com.aliyun.dysmsapi20170525.Client
     **/
    public com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret,String endpoint) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = endpoint;
        return new com.aliyun.dysmsapi20170525.Client(config);
    }


    /***
     * @description:根据配置发送短信
     * @author: sunshangeng
     * @date: 2023/2/21 10:37
     * @param: [param]
     * @return: com.ruoyi.common.utils.sms.model.SmsResult
     **/
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
                .setSignName(param.getTemplateSign())//签名
                .setTemplateCode(param.getTemplateCode())//模板
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
            smsLog.setNoticeConfig(param.getConfigId());
            smsLog.setNoticeTemplate(param.getTemplateId());
            smsLog.setYwTable(param.getYwtable());
            smsLog.setRecipient(param.getRecipient());
            smsLog.setResponseId(body.getRequestId());

            smsLog.setResponse(JSONObject.toJSONString(body));

            if (body.getCode().equals("OK")) {
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
    /**
     * @description:需要整理邮箱变更时处理，短信语音播报无需处理
     * @author: sunshangeng
     * @date: 2023/2/28 16:27
     * @param: [内容, 前台传入的json字符串]
     * @return: com.ruoyi.common.utils.sms.model.SmsResult
     **/
    public SmsResult handleContent(String content,JSONObject templateJson){

        /**获取到所在的模板是否有变量*/
        int number = content.indexOf("#{");
        /**传入的变量信息为空！*/
        if (templateJson == null && number != -1) {
            /**未进行变量赋值*/
            return SmsResult.fail("当前传入的JSON对象为空或模板中未定义变量");
        } else {
            /**循环替换模板内容*/
            for (String key : templateJson.keySet()) {
                content = content.replace("#{" + key + "}", templateJson.getString(key));
            }
        }

        number = content.indexOf("#{");
        /**说明变量未全部替换完成*/
        if (number != -1) {
            return SmsResult.fail("定义的模板变量和传入的变量信息无法对应");
        }
        return  SmsResult.success(content);
    }
}
