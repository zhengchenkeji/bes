package com.ruoyi.common.utils.sms.model;

/**
 * description:短信消息参数类
 * author: sunshangeng
 * date:2023/2/20 17:14
 */
public class ALSmSParam {

    /**接收人*/
    private String recipient;

    /**内容*/
    private String context;

    /**业务id*/
    private String ywid;
    /**业务table*/
    private String ywtable;

    /**阿里云区域id*/
    private String regionid;

    /**阿里云accesskeyid*/
    private  String accesskeyid;

    /**阿里云secret*/
    private String secret;

    /**阿里云模板code*/
    private String templateCode;
    /**阿里云模板签名*/
    private String templateSign;
    /**通知配置id*/
    private Integer configId;
    /**通知模板id*/
    private Integer templateId;



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

    public String getRegionid() {
        return regionid;
    }

    public void setRegionid(String regionid) {
        this.regionid = regionid;
    }

    public String getAccesskeyid() {
        return accesskeyid;
    }

    public void setAccesskeyid(String accesskeyid) {
        this.accesskeyid = accesskeyid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateSign() {
        return templateSign;
    }

    public void setTemplateSign(String templateSign) {
        this.templateSign = templateSign;
    }

    public String getYwtable() {
        return ywtable;
    }

    public void setYwtable(String ywtable) {
        this.ywtable = ywtable;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public ALSmSParam(String recipient, String context, String ywid, String ywtable, String regionid, String accesskeyid, String secret, String templateCode, String templateSign,Integer configId,Integer templateId) {
        this.recipient = recipient;
        this.context = context;
        this.ywid = ywid;
        this.ywtable=ywtable;
        this.regionid = regionid;
        this.accesskeyid = accesskeyid;
        this.secret = secret;
        this.templateCode = templateCode;
        this.templateSign = templateSign;
        this.configId=configId;
        this.templateId=templateId;
    }

    /**语音播报参数处理*/
    public ALSmSParam(String accesskeyid, String secret,String ywid) {
        this.accesskeyid = accesskeyid;
        this.secret = secret;
        this.ywid=ywid;
    }

    public ALSmSParam() {
    }
}
