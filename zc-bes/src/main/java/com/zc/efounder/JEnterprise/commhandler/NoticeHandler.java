package com.zc.efounder.JEnterprise.commhandler;

import com.alibaba.fastjson.JSONObject;

import com.google.gson.JsonObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.result.ResultMap;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sms.domain.AthenaBesSmsLog;
import com.ruoyi.common.utils.sms.mapper.SmsLogMapper;
import com.ruoyi.common.utils.sms.model.ALSmSParam;
import com.ruoyi.common.utils.sms.model.EmailParam;
import com.ruoyi.common.utils.sms.model.SmsResult;
import com.ruoyi.common.utils.sms.server.NoticeServer;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.NoticeTableConstants;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeConfig;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeTemplate;
import com.zc.efounder.JEnterprise.domain.noticeManage.vo.DebugModel;
import com.zc.efounder.JEnterprise.mapper.commhandler.SubitemDataMapper;
import com.zc.efounder.JEnterprise.mapper.noticeManage.NoticeConfigMapper;
import com.zc.efounder.JEnterprise.mapper.noticeManage.NoticeTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * description:通知操作类
 * author: sunshangeng
 * date:2023/2/13 10:50
 */
public class NoticeHandler {
    private static final Logger log = LoggerFactory.getLogger(NoticeHandler.class);
    private static NoticeTemplateMapper TemplateMapper = ApplicationContextProvider.getBean(NoticeTemplateMapper.class);
    private static NoticeConfigMapper configMapper = ApplicationContextProvider.getBean(NoticeConfigMapper.class);
    private static NoticeServer noticeServer = ApplicationContextProvider.getBean(NoticeServer.class);

    private static SmsLogMapper smsLogMapper = ApplicationContextProvider.getBean(SmsLogMapper.class);

    public static final String SMS_NOFILe_EMAil = "1";
    public static final String SMS_FILe_EMAil = "2";
    public static final String SMS_MESSAGE_PHONE = "3";


    public static ResultMap sendNotice(Long Configid, Long templateId, String[] recipients, String ContentJson,String ywTable,String ywId) {
        if (Configid == null
                || recipients == null
                || recipients.length == 0) {
            return ResultMap.error("传入的参数不完整！");
        }
        List<String> recipientList=new ArrayList<>();

        ResultMap resultMap=null;

        /**获取配置信息*/
        NoticeConfig config = configMapper.selectNoticeConfigById(Configid);
        /**判断发送什么类型的消息通知*/
        if(config.getNoticetype().equals("1")){
            /**循环获取电话*/
            for (int i = 0; i < recipients.length; i++) {
                recipientList.add(configMapper.selectUserById(Long.parseLong(recipients[i])).getPhonenumber());
            }
            /*短信*/
            sendPhoneMessage(config,templateId,recipientList.toArray(new String[recipientList.size()]),ContentJson/*JSONObject.toJSONString(new HashMap<String,String>(){{put("code","123456");}})*/,ywTable,ywId);
             resultMap = sendPhoneMessage(config, templateId, recipientList.toArray(new String[recipientList.size()]), ContentJson, ywTable, ywId);

             return  resultMap;
        }else if(config.getNoticetype().equals("2")){
            /*邮箱*/
            /**循环获取邮箱*/
            for (int i = 0; i < recipients.length; i++) {
                recipientList.add(configMapper.selectUserById(Long.parseLong(recipients[i])).getEmail());
            }
            resultMap = sendEamil(config,templateId,recipientList.toArray(new String[recipientList.size()]),ContentJson,ywTable,ywId);
            return  resultMap;

        }else if(config.getNoticetype().equals("3")){

            /*处理语音播报语音播报*/
            JSONObject voice=new JSONObject();
            NoticeTemplate template=TemplateMapper.selectNoticeTemplateById(templateId);
            /**判断厂商*/
            if (config.getServicefactory().equals("31")) {
                /**阿里*/
                /**语音播报*/
                ALSmSParam Param = new ALSmSParam(config.getAccesskeyid(),config.getSecret(),config.getId().toString());
                SmsResult result = noticeServer.getAlibabaToken(Param);
                /***/
                if (result.isSuccess()) {
                    voice.put("token",result.getMessage());
                    voice.put("appkey",config.getAppkey());
                } else {
                    log.error("语音播报获取阿里token失败");
                }
            }else{
                /**百度*/
                voice.put("token",config.getToken());
            }

//            String username = SecurityUtils.getUsername();
            /**自动替换当前登录信息*/
            String Content=template.getContent();
//            Content = Content.replace("#{username}", username);
            JSONObject ContentObject=JSONObject.parseObject(ContentJson);
            if(ContentObject!=null){
                for (String key : ContentObject.keySet()) {
                    Content = Content.replace("#{" + key + "}", ContentObject.getString(key));
                }
            }

            voice.put("time", System.currentTimeMillis());
            voice.put("message",Content);
            voice.put("type",config.getServicefactory());


            /**循环插入日志*/
            for (String recipient : recipients) {
                AthenaBesSmsLog smsLog=new AthenaBesSmsLog();
                /**发送完成后存入数据库*/
                smsLog.setAddTime(new Date());
                smsLog.setSendTime(new Date());
                smsLog.setYwId(ywId);
                smsLog.setSendText(Content);
                smsLog.setType(3);
                smsLog.setNoticeConfig(Configid.intValue());
                smsLog.setNoticeTemplate(templateId.intValue());
                smsLog.setYwTable(ywTable);
                smsLog.setRecipient(configMapper.selectUserById(Long.parseLong(recipient)).getNickName()+"("+recipient+")");
                smsLog.setIsSuccess(1);
                smsLogMapper.insert(smsLog);
            }
            return ResultMap.ok(voice.toJSONString());
        }else{
            return ResultMap.error("传入的通知配置无法识别！");
        }
    }
    /**
     * @description:发送邮件工具方法
     * @author: sunshangeng
     * @date: 2023/3/3 17:21
     * @param: [configId, templateId, emails, ContentJson]
     * @return: com.ruoyi.common.core.result.ResultMap
     */
    public static ResultMap sendEamil(NoticeConfig config, Long templateId, String[] emails, String ContentJson,String ywTable,String ywId) {

        NoticeTemplate noticeTemplate = TemplateMapper.selectNoticeTemplateById(templateId);
        JSONObject ContentObject = JSONObject.parseObject(ContentJson);
        /**邮箱*/

        /**提取模板用于填充数据*/
        String Content = noticeTemplate.getContent();
        /**获取到所在的模板是否有变量*/
        int number = noticeTemplate.getContent().indexOf("#{");
        /**传入的变量信息为空！*/
        if (ContentObject == null && number != -1) {
            /**未进行变量赋值*/
            return ResultMap.error("当前模板存在变量但未赋值！");
        } else {
            /**循环替换模板内容*/
            for (String key : ContentObject.keySet()) {
                Content = Content.replace("#{" + key + "}", ContentObject.getString(key));
            }
        }
        number = Content.indexOf("#{");
        /**说明变量未全部替换完成*/
        if (number != -1) {
            return ResultMap.error("定义的模板变量和传入的变量信息无法对应！");
        }
        EmailParam emailParam = null;
        SmsResult smsResult = null;
        /**判断是否需要发送附件*/
        if (org.apache.commons.lang3.StringUtils.isNotBlank(noticeTemplate.getFilePath())) {
            /**需要发送附件的*/
            String[] filesPath = noticeTemplate.getFilePath().split(";");
            List<File> fileList = new ArrayList<>();
            for (int i = 0; i < filesPath.length; i++) {
                /**拼接实际存储路径*/
                String filepath = filesPath[i].substring(filesPath[i].indexOf("/upload"), filesPath[i].length());
                filepath = RuoYiConfig.getProfile() + filepath;
                /**读取文件*/
                File file = new File(filepath);
                /**判断是否有文件*/
                if (file.isFile() && file.exists()) {
                    fileList.add(file);
                }
            }
            /**循环发送*/
            for (int i = 0; i < emails.length; i++) {
                emailParam = new EmailParam(emails[i]
                        , Content
                        , ywId
                        , ywTable
                        , noticeTemplate.getTitle()
                        , config.getEmailServerHost()
                        , Integer.parseInt(config.getEmailServerPort())
                        , config.getFromemail()
                        , config.getFromemailpwd()
                        ,config.getId().intValue()
                        ,templateId.intValue()
                        , fileList
                );
                smsResult = noticeServer.sendMessageByConfigFiles(emailParam);
                if (!smsResult.isSuccess()) {
                    log.error("邮件发送失败：" + smsResult.getMessage());
                }
            }
        } else {
            /**无需发送附件的*/
            /**循环发送*/
            for (int i = 0; i < emails.length; i++) {
                emailParam = new EmailParam(emails[i]
                        , Content
                        , ywId
                        , ywTable
                        , noticeTemplate.getTitle()
                        , config.getEmailServerHost()
                        , Integer.parseInt(config.getEmailServerPort())
                        , config.getFromemail()
                        , config.getFromemailpwd()
                        ,config.getId().intValue()
                        ,templateId.intValue());
                smsResult = noticeServer.sendMessageByConfig(emailParam);
                if (!smsResult.isSuccess()) {
                    log.error("邮件发送失败：" + smsResult.getMessage());
                }
            }
        }
        return ResultMap.ok();
    }

    /**
     * @description:组装短信
     * @author: sunshangeng
     * @date: 2023/2/28 16:40
     * @return: com.ruoyi.common.utils.sms.model.EmailParam
     **/
    private static ResultMap sendPhoneMessage(NoticeConfig config, Long templateId, String[] phones, String ContentJson,String ywTable,String ywId) {
        if (templateId==null) {
            return ResultMap.error("传入的参数不完整！");
        }
        NoticeTemplate noticeTemplate = TemplateMapper.selectNoticeTemplateById(templateId);
        /**循环发送短信*/
        for (int i = 0; i < phones.length; i++) {
            ALSmSParam ALSmSParam = new ALSmSParam(phones[i],
                    ContentJson,
                    ywId,
                    ywTable,
                    config.getRegionid(),
                    config.getAccesskeyid(),
                    config.getSecret(),
                    noticeTemplate.getTemplatecode(),
                    noticeTemplate.getTemplatesign(),
                    config.getId().intValue(),
                    templateId.intValue());
            SmsResult smsResult = noticeServer.sendbyConfig(ALSmSParam);
            if (!smsResult.isSuccess()) {
                log.error("短信发送失败：" + smsResult.getMessage());
            }
        }
        return ResultMap.ok();
    }
}
