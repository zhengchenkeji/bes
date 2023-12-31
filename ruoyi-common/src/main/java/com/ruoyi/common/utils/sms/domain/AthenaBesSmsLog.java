package com.ruoyi.common.utils.sms.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消息发送日志对象 athena_bes_sms_log
 *
 * @author sunshangeng
 * @date 2022-11-21
 */

@TableName("athena_bes_sms_log")
public class  AthenaBesSmsLog
{
    private static final long serialVersionUID = 1L;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 1：短信  2：邮箱 */
    @TableField("type")

    private Integer type;

    /** 发送时间 */
    @TableField("send_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")

    private Date sendTime;

    /** 是否发送成功 1：成功  0：未成功 */
    @TableField("is_success")

    private Integer isSuccess;

    /** 发送内容 */
    @TableField("send_text")

    private String sendText;

    /** 消息体 */
    @TableField("send_json")

    private String sendJson;

    /** 所属业务id */
    @TableField("yw_id")
    private String ywId;

    @TableField("yw_table")
    private String ywTable;
    /**所属配置*/
    @TableField("notice_config")
    private Integer noticeConfig;
    /**所属模板*/
    @TableField("notice_template")
    private Integer noticeTemplate;
    /** 添加时间 */
    @TableField("add_time")
    private Date addTime;

    /** 接收者 */
    @TableField("recipient")
    private String recipient;


    /** 响应的消息id */
    @TableField("response_id")
    private String responseId;

    /** 响应的消息体 */
    @TableField("response")
    private String response;


    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setType(Integer type)
    {
        this.type = type;
    }

    public Integer getType()
    {
        return type;
    }
    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime()
    {
        return sendTime;
    }
    public void setIsSuccess(Integer isSuccess)
    {
        this.isSuccess = isSuccess;
    }

    public Integer getIsSuccess()
    {
        return isSuccess;
    }
    public void setSendText(String sendText)
    {
        this.sendText = sendText;
    }

    public String getSendText()
    {
        return sendText;
    }
    public void setSendJson(String sendJson)
    {
        this.sendJson = sendJson;
    }

    public String getSendJson()
    {
        return sendJson;
    }
    public void setYwId(String ywId)
    {
        this.ywId = ywId;
    }

    public String getYwId()
    {
        return ywId;
    }
    public void setAddTime(Date addTime)
    {
        this.addTime = addTime;
    }

    public Date getAddTime()
    {
        return addTime;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    public String getYwTable() {
        return ywTable;
    }
    public void setYwTable(String ywTable) {
        this.ywTable = ywTable;
    }


    public Integer getNoticeConfig() {
        return noticeConfig;
    }

    public void setNoticeConfig(Integer noticeConfig) {
        this.noticeConfig = noticeConfig;
    }

    public Integer getNoticeTemplate() {
        return noticeTemplate;
    }

    public void setNoticeTemplate(Integer noticeTemplate) {
        this.noticeTemplate = noticeTemplate;
    }
}
