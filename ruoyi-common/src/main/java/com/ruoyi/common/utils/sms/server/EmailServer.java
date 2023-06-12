package com.ruoyi.common.utils.sms.server;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.sms.domain.AthenaBesSmsLog;
import com.ruoyi.common.utils.sms.mapper.SmsLogMapper;
import com.ruoyi.common.utils.sms.model.EmailParam;
import com.ruoyi.common.utils.sms.model.SmsParam;
import com.ruoyi.common.utils.sms.model.SmsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.sql.ParameterMetaData;
import java.util.Date;

/**
 * description:电子邮件工具类
 * author: sunshangeng
 * date:2022/11/15 17:41
 */
@Component
public class EmailServer {
    private static final Logger log = LoggerFactory.getLogger(EmailServer.class);

    //    @Value("${spring.mail.from}") // 从application.yml配置文件中获取
//    private String from; // 发送发邮箱地址
//    @Autowired
//    private JavaMailSender mailSender;
    @Resource
    private SmsLogMapper smsLogMapper;


    /**
     * 发送纯文本邮件信息
     */
    public SmsResult sendMessage(SmsParam param) {
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        senderImpl.setHost("smtp.qq.com");
        senderImpl.setPort(587);
        senderImpl.setUsername("1615422990@qq.com");
        senderImpl.setPassword("aznsggrashgbchbh");
        senderImpl.setDefaultEncoding("UTF-8");
        AthenaBesSmsLog smsLog = new AthenaBesSmsLog();
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            // 创建一个邮件对象
            msg.setFrom("1615422990@qq.com"); // 设置发送发
            msg.setTo(param.getRecipient()); // 设置接收方
            msg.setSubject(param.getTitle()); // 设置邮件主题
            msg.setText(param.getContext()); // 设置邮件内容
            smsLog.setAddTime(new Date());
            smsLog.setSendTime(new Date());
            smsLog.setYwId(param.getYwid());
            smsLog.setSendText(param.getContext());
            smsLog.setRecipient(param.getRecipient());
            smsLog.setSendJson(JSONObject.toJSONString(msg));
            smsLog.setType(2);
            smsLog.setIsSuccess(1);
            // 发送邮件
//            mailSender.send(msg);
            senderImpl.send(msg);
        } catch (Exception e) {
            smsLog.setIsSuccess(0);
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
     * @description:发送纯文本邮件
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
            SimpleMailMessage msg = new SimpleMailMessage();
            // 创建一个邮件对象
            msg.setFrom(param.getFromEamil()); // 设置发送发
            msg.setTo(param.getRecipient()); // 设置接收方
            msg.setSubject(param.getTitle()); // 设置邮件主题
            msg.setText(param.getContext()); // 设置邮件内容
            smsLog.setAddTime(new Date());
            smsLog.setSendTime(new Date());
            smsLog.setYwId(param.getYwid());
            smsLog.setSendText(param.getContext());
            smsLog.setRecipient(param.getRecipient());
            smsLog.setSendJson(JSONObject.toJSONString(param));
            smsLog.setType(2);
            smsLog.setIsSuccess(1);
            // 发送邮件
//            mailSender.send(msg);
            senderImpl.send(msg);
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
            helper.setText(param.getContext()); // 设置邮件内容
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
            smsLog.setSendJson(JSONObject.toJSONString(mimeMessage));
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

//    /**
//     * 发送带附件的邮件信息
//     *
//     * @param to      接收方
//     * @param subject 邮件主题
//     * @param content 邮件内容（发送内容）
//     * @param files 文件数组 // 可发送多个附件
//     */
//    public void sendMessageCarryFiles(String to, String subject, String content, File[] files) {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
//            helper.setFrom(from); // 设置发送发
//            helper.setTo(to); // 设置接收方
//            helper.setSubject(subject); // 设置邮件主题
//            helper.setText(content); // 设置邮件内容
//            if (files != null && files.length > 0) { // 添加附件（多个）
//                for (File file : files) {
//                    helper.addAttachment(file.getName(), file);
//                }
//            }
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        // 发送邮件
//        mailSender.send(mimeMessage);
//    }
//    /**
//     * 发送带附件的邮件信息
//     *
//     * @param to      接收方
//     * @param subject 邮件主题
//     * @param content 邮件内容（发送内容）
//     * @param file 单个文件
//     */
//    public void sendMessageCarryFile(String to, String subject, String content, File file) {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
//            helper.setFrom(from); // 设置发送发
//            helper.setTo(to); // 设置接收方
//            helper.setSubject(subject); // 设置邮件主题
//            helper.setText(content); // 设置邮件内容
//            helper.addAttachment(file.getName(), file); // 单个附件
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//        // 发送邮件
//        mailSender.send(mimeMessage);
//    }
//
//    public String getFrom() {
//        return from;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }

}
