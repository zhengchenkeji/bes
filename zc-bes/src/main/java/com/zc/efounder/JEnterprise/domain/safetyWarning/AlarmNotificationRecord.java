package com.zc.efounder.JEnterprise.domain.safetyWarning;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 告警通知记录对象 athena_bes_alarm_notification_record
 *
 * @author ruoyi
 * @date 2022-09-19
 */
@ApiModel(value = "告警通知记录对象")
public class AlarmNotificationRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @ApiModelProperty(value = "唯一ID")
    private Long id;

    /** 所属告警策略 */
    @ApiModelProperty(value = "所属告警策略")
    private Long alarmTacticsId;

    /** 告知方式 1：邮件、2：短信、3：电话 */
    @Excel(name = "告知方式 ",readConverterExp = "1=邮箱,2=短信,3=电话")
    @ApiModelProperty(value = "告知方式 1：邮件、2：短信、3：电话")
    private Long informManner;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "发送时间")
    private Date sendTime;

    /** 是否发送成功 0：否、1：是 */
    @Excel(name = "是否发送成功 ",readConverterExp = "0=否,1=是")
    @ApiModelProperty(value = "是否发送成功 0：否、1：是")
    private Long isSendSucceed;

    /** 告警信息 */
    @Excel(name = "告警信息")
    @ApiModelProperty(value = "告警信息")
    private String alarmMessage;



    /**所属告知方式名称*/
    @Excel(name = "所属告警策略")
    @ApiModelProperty(value = "所属告警策略名称")
    private String tacticsName;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setAlarmTacticsId(Long alarmTacticsId)
    {
        this.alarmTacticsId = alarmTacticsId;
    }

    public Long getAlarmTacticsId()
    {
        return alarmTacticsId;
    }
    public void setInformManner(Long informManner)
    {
        this.informManner = informManner;
    }

    public Long getInformManner()
    {
        return informManner;
    }
    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime()
    {
        return sendTime;
    }
    public void setIsSendSucceed(Long isSendSucceed)
    {
        this.isSendSucceed = isSendSucceed;
    }

    public Long getIsSendSucceed()
    {
        return isSendSucceed;
    }
    public void setAlarmMessage(String alarmMessage)
    {
        this.alarmMessage = alarmMessage;
    }

    public String getAlarmMessage()
    {
        return alarmMessage;
    }

    public String getTacticsName() {
        return tacticsName;
    }

    public void setTacticsName(String tacticsName) {
        this.tacticsName = tacticsName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("alarmTacticsId", getAlarmTacticsId())
            .append("informManner", getInformManner())
            .append("sendTime", getSendTime())
            .append("isSendSucceed", getIsSendSucceed())
            .append("alarmMessage", getAlarmMessage())
            .append("createTime", getCreateTime())
            .toString();
    }

    public AlarmNotificationRecord(Long alarmTacticsId, Long informManner, Date sendTime, Long isSendSucceed, String alarmMessage, String tacticsName) {
        this.alarmTacticsId = alarmTacticsId;
        this.informManner = informManner;
        this.sendTime = sendTime;
        this.isSendSucceed = isSendSucceed;
        this.alarmMessage = alarmMessage;
        this.tacticsName = tacticsName;
    }
    public AlarmNotificationRecord() {

    }
}
