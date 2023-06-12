package com.ruoyi.common.utils.sms.model;

/**
 * description: 发送消息参数工具类
 * author: sunshangeng
 * date:2022/11/21 14:04
 */
public class SmsParam {

    /**接收人*/
    private String recipient;

    /**内容*/
    private String context;

    /**业务id*/
    private String ywid;

    /**邮箱标题*/
    private  String title;


    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getYwid() {
        return ywid;
    }

    public void setYwid(String ywid) {
        this.ywid = ywid;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SmsParam(String recipient, String context, String ywid, String title) {
        this.recipient = recipient;
        this.context = context;
        this.ywid = ywid;
        this.title = title;
    }
    public SmsParam() {

    }

}
