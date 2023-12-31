package com.zc.efounder.JEnterprise.domain.safetyWarning;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

/**
 * 告警历史数据对象 athena_bes_alarm_historical_data
 *
 * @author qindehua
 * @date 2022-11-17
 */
@ApiModel(value = "AlarmHistoricalData",description = "告警历史数据对象")
public class AlarmHistoricalData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @ApiModelProperty(value = "唯一ID")
    private Long id;

    /** 告警策略 */
    @Excel(name = "告警策略")
    @ApiModelProperty(value = "告警策略")
    private Long alarmTacticsId;

    /** 告警策略名称 */
    @Excel(name = "告警策略名称")
    @ApiModelProperty(value = "告警策略名称")
    private String alarmTacticsName;

    /** 告警值 */
    @Excel(name = "告警值")
    @ApiModelProperty(value = "告警值")
    private Double alarmValue;

    /** 告警描述 */
    @Excel(name = "告警描述")
    @ApiModelProperty(value = "告警描述")
    private String description;

    /** 告警时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "告警时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "告警时间")
    private Date alarmTime;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    /**
     * 所属告警类型 1:电表  2:支路  3:分户 4:分项 5:设备报警
     */
    @Excel(name = "所属告警类型 1:电表  2:支路  3:分户 4:分项 5:设备报警")
    @ApiModelProperty(value = "所属告警类型 1:电表  2:支路  3:分户 4:分项 5:设备报警")
    private String alarmTypeId;

    /**
     * 告警策略ID集合
     * gaojikun
     */
    private List<Long> ids;

    /**
     * 设备ID
     * gaojikun
     */
    private Long equipmentId;

    /**
     * 告警策略ID集合
     * gaojikun
     */
    private List<Long> equipmentIds;

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getAlarmTypeId() {
        return alarmTypeId;
    }

    public void setAlarmTypeId(String alarmTypeId) {
        this.alarmTypeId = alarmTypeId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAlarmTacticsName() {
        return alarmTacticsName;
    }

    public void setAlarmTacticsName(String alarmTacticsName) {
        this.alarmTacticsName = alarmTacticsName;
    }

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
    public void setAlarmValue(Double alarmValue)
    {
        this.alarmValue = alarmValue;
    }

    public Double getAlarmValue()
    {
        return alarmValue;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setAlarmTime(Date alarmTime)
    {
        this.alarmTime = alarmTime;
    }

    public Date getAlarmTime()
    {
        return alarmTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("alarmTacticsId", getAlarmTacticsId())
            .append("alarmValue", getAlarmValue())
            .append("description", getDescription())
            .append("alarmTime", getAlarmTime())
            .append("createTime", getCreateTime())
            .append("alarmTypeId", getAlarmTypeId())
            .toString();
    }
}
