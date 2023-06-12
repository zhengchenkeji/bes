package com.zc.efounder.JEnterprise.domain.safetyWarning;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 告警策略告警接收组关联对象 athena_bes_alarm_tactics_alarm_notifier__link
 *
 * @author sunshangeng
 * @date 2022-09-20
 */
public class AlarmTacticsAlarmNotifierLink extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 所属告警策略 */
    private Long alarmTacticsId;

    /** 告警通知人 */
    private Long alarmNotifierId;

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
    public void setAlarmNotifierId(Long alarmNotifierId)
    {
        this.alarmNotifierId = alarmNotifierId;
    }

    public Long getAlarmNotifierId()
    {
        return alarmNotifierId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("alarmTacticsId", getAlarmTacticsId())
            .append("alarmNotifierId", getAlarmNotifierId())
            .toString();
    }
}
