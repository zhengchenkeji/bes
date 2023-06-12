package com.zc.efounder.JEnterprise.domain.safetyWarning;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 告警配置和通知关系对象 athena_bes_alarm_notice_link
 *
 * @author ruoyi
 * @date 2023-03-08
 */
public class AlarmNoticeLink extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 告警策略配置 */
    @Excel(name = "告警策略配置")
    private Long alarmTacticsid;

    /** 通知配置id */
    @Excel(name = "通知配置id")
    private Long noticeConfigid;

    /** 通知模板Id */
    @Excel(name = "通知模板Id")
    private Long noticeTemplateid;

    /** 通知类型 */
    @Excel(name = "通知类型")
    private Long noticeType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setAlarmTacticsid(Long alarmTacticsid)
    {
        this.alarmTacticsid = alarmTacticsid;
    }

    public Long getAlarmTacticsid()
    {
        return alarmTacticsid;
    }
    public void setNoticeConfigid(Long noticeConfigid)
    {
        this.noticeConfigid = noticeConfigid;
    }

    public Long getNoticeConfigid()
    {
        return noticeConfigid;
    }
    public void setNoticeTemplateid(Long noticeTemplateid)
    {
        this.noticeTemplateid = noticeTemplateid;
    }

    public Long getNoticeTemplateid()
    {
        return noticeTemplateid;
    }
    public void setNoticeType(Long noticeType)
    {
        this.noticeType = noticeType;
    }

    public Long getNoticeType()
    {
        return noticeType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("alarmTacticsid", getAlarmTacticsid())
            .append("noticeConfigid", getNoticeConfigid())
            .append("noticeTemplateid", getNoticeTemplateid())
            .append("noticeType", getNoticeType())
            .append("createTime", getCreateTime())
            .toString();
    }
}
