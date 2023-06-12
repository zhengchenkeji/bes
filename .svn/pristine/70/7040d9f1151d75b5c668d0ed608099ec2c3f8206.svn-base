package com.zc.efounder.JEnterprise.domain.noticeManage;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * description:通知配置类
 * author: sunshangeng
 * date:2023/2/8 11:35
 */
public class NoticeConfig  extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 通知配置名称 */
    @Excel(name = "通知配置名称")
    private String name;

    /** 通知类型 */
    @Excel(name = "通知类型")
    private String noticetype;

    /** 服务厂商 */
    @Excel(name = "服务厂商")
    private String servicefactory;

    /** 服务厂商的regionId */
    @Excel(name = "服务厂商的regionId")
    private String regionid;

    /** 服务厂商accessKeyId */
    @Excel(name = "服务厂商accessKeyId")
    private String accesskeyid;

    /** 服务厂商secret */
    @Excel(name = "服务厂商secret")
    private String secret;

    /** 发件人邮箱地址 */
    @Excel(name = "发件人邮箱地址")
    private String fromemail;

    /** 发件人邮箱密码 */
    @Excel(name = "发件人邮箱密码")
    private String fromemailpwd;

    /** 发件人邮箱地址 */
    @Excel(name = "发件人邮箱服务地址")
    private String emailServerHost;

    /** 发件人邮箱密码 */
    @Excel(name = "发件人邮箱服务端口")
    private String emailServerPort;


    /** 接收人 */
    @Excel(name = "接收人")
    private String recipient;

    @Excel(name = "百度云token")
    private String token;

    @Excel(name = "阿里云appkey")
    private String appkey;


    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setNoticetype(String noticetype)
    {
        this.noticetype = noticetype;
    }

    public String getNoticetype()
    {
        return noticetype;
    }
    public void setServicefactory(String servicefactory)
    {
        this.servicefactory = servicefactory;
    }

    public String getServicefactory()
    {
        return servicefactory;
    }
    public void setRegionid(String regionid)
    {
        this.regionid = regionid;
    }

    public String getRegionid()
    {
        return regionid;
    }
    public void setAccesskeyid(String accesskeyid)
    {
        this.accesskeyid = accesskeyid;
    }

    public String getAccesskeyid()
    {
        return accesskeyid;
    }
    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getSecret()
    {
        return secret;
    }
    public void setFromemail(String fromemail)
    {
        this.fromemail = fromemail;
    }

    public String getFromemail()
    {
        return fromemail;
    }
    public void setFromemailpwd(String fromemailpwd)
    {
        this.fromemailpwd = fromemailpwd;
    }

    public String getFromemailpwd()
    {
        return fromemailpwd;
    }
    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }

    public String getRecipient()
    {
        return recipient;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getEmailServerHost() {
        return emailServerHost;
    }

    public void setEmailServerHost(String emailServerHost) {
        this.emailServerHost = emailServerHost;
    }

    public String getEmailServerPort() {
        return emailServerPort;
    }

    public void setEmailServerPort(String emailServerPort) {
        this.emailServerPort = emailServerPort;
    }


    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("noticetype", getNoticetype())
                .append("servicefactory", getServicefactory())
                .append("regionid", getRegionid())
                .append("accesskeyid", getAccesskeyid())
                .append("secret", getSecret())
                .append("fromemail", getFromemail())
                .append("fromemailpwd", getFromemailpwd())
                .append("emailServerHost", getEmailServerHost())
                .append("emailServerPort", getEmailServerPort())
                .append("recipient", getRecipient())
                .append("token", getToken())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
