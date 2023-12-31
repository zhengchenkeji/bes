package com.ruoyi.common.utils.sms.model;

import java.io.File;
import java.util.List;

/**
 * description:发送邮箱配置类
 * author: sunshangeng
 * date:2023/2/20 16:26
 */
public class EmailParam {

    /**接收人*/
    private String recipient;

    /**内容*/
    private String context;

    /**业务id*/
    private String ywid;


    /**业务table*/
    private String ywtable;
    /**邮箱标题*/
    private  String title;
    /**邮箱服务地址*/
    private  String host;
    /**邮箱端口*/
    private  Integer port;
    /**发送方*/
    private  String fromEamil;
    /**发送方key*/
    private  String fromEamilkey;
    /**通知配置id*/
    private Integer configId;
    /**通知模板id*/
    private Integer templateId;

    /**文件数组*/
    private List<File> files;

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getFromEamil() {
        return fromEamil;
    }

    public void setFromEamil(String fromEamil) {
        this.fromEamil = fromEamil;
    }

    public String getFromEamilkey() {
        return fromEamilkey;
    }

    public void setFromEamilkey(String fromEamilkey) {
        this.fromEamilkey = fromEamilkey;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
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

    public EmailParam(String recipient, String context, String ywid, String ywtable, String title, String host, Integer port, String fromEamil, String fromEamilkey,Integer configId,Integer templateId, List<File> files) {
        this.recipient = recipient;
        this.context = context;
        this.ywid = ywid;
        this.ywtable=ywtable;
        this.title = title;
        this.host = host;
        this.port = port;
        this.fromEamil = fromEamil;
        this.fromEamilkey = fromEamilkey;
        this.configId=configId;
        this.templateId=templateId;
        this.files = files;
    }

    public EmailParam(String recipient, String context, String ywid,String ywtable, String title, String host, Integer port, String fromEamil, String fromEamilkey,Integer configId,Integer templateId) {
        this.recipient = recipient;
        this.context = context;
        this.ywid = ywid;
        this.ywtable=ywtable;
        this.title = title;
        this.host = host;
        this.port = port;
        this.fromEamil = fromEamil;
        this.fromEamilkey = fromEamilkey;
        this.configId=configId;
        this.templateId=templateId;
    }

    public EmailParam() {
    }
}
