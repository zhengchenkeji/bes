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
 * 告警实时数据对象 athena_bes_alarm_realtime_data
 *
 * @author qindehua
 * @date 2022-11-04
 */
@ApiModel(value = "AlarmRealtimeData", description = "告警实时数据对象")
public class AlarmRealtimeData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "唯一ID", required = true)
    private Long id;

    /**
     * 告警策略ID
     */
    @Excel(name = "告警策略ID")
    @ApiModelProperty(value = "告警策略ID")
    private Long alarmTacticsId;

    /**
     * 告警策略名称
     */
    @Excel(name = "告警策略名称")
    @ApiModelProperty(value = "告警策略名称")
    private String alarmTacticsName;

    /**
     * 告警值
     */
    @Excel(name = "告警值")
    @ApiModelProperty(value = "告警值", required = true)
    private String alarmValue;


    /**
     * 第一次产生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "第一次产生时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "第一次产生时间")
    private Date firstTime;

    /**
     * 最后一次产生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后一次产生时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后一次产生时间", required = true)
    private Date lastTime;

    /**
     * 告警次数
     */
    @Excel(name = "告警次数")
    @ApiModelProperty(value = "告警次数")
    private Integer amount;

    /**
     * 确认状态 0：未确认、1：已确认
     */
    @Excel(name = "确认状态 0：未确认、1：已确认")
    @ApiModelProperty(value = "确认状态 0：未确认、1：已确认")
    private Long confirmState;

    /**
     * 告警位置
     */
    @Excel(name = "告警位置")
    @ApiModelProperty(value = "告警位置")
    private String azwz;

    /**
     * 报警名称
     */
    @Excel(name = "报警名称")
    @ApiModelProperty(value = "报警名称")
    private String alarmName;

    /**
     * 计划值
     */
    @Excel(name = "计划值")
    @ApiModelProperty(value = "计划值")
    private String planVal;

    /**
     * 提示信息 （告警描述）
     */
    @Excel(name = "提示信息 （告警描述）")
    @ApiModelProperty(value = "提示信息 （告警描述）", required = true)
    private String promptMsg;


    /**
     * 告警等级 1：一般、2：较大、3：严重
     */
    @Excel(name = "告警等级 1：一般、2：较大、3：严重")
    @ApiModelProperty(value = "告警等级 1：一般、2：较大、3：严重")
    private String level;

    /**
     * 所属告警类型 1:电表  2:支路  3:分户 4:分项 5:设备报警
     */
    @Excel(name = "所属告警类型 1:电表  2:支路  3:分户 4:分项 5:设备报警")
    @ApiModelProperty(value = "所属告警类型 1:电表  2:支路  3:分户 4:分项 5:设备报警", required = true)
    private String alarmTypeId;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    /**
     * 设备ID
     * gaojikun
     */
    private Long equipmentId;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    /**
     * 告警策略ID集合
     * gaojikun
     */
    private List<Long> ids;

    /**
     * 设备ID集合
     * gaojikun
     */
    private List<Long> equipmentIds;

    public List<Long> getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public String getAlarmTacticsName() {
        return alarmTacticsName;
    }

    public void setAlarmTacticsName(String alarmTacticsName) {
        this.alarmTacticsName = alarmTacticsName;
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setAlarmTacticsId(Long alarmTacticsId) {
        this.alarmTacticsId = alarmTacticsId;
    }

    public Long getAlarmTacticsId() {
        return alarmTacticsId;
    }

    public void setAlarmValue(String alarmValue) {
        this.alarmValue = alarmValue;
    }

    public String getAlarmValue() {
        return alarmValue;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    public Date getFirstTime() {
        return firstTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setConfirmState(Long confirmState) {
        this.confirmState = confirmState;
    }

    public Long getConfirmState() {
        return confirmState;
    }

    public void setAzwz(String azwz) {
        this.azwz = azwz;
    }

    public String getAzwz() {
        return azwz;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setPlanVal(String planVal) {
        this.planVal = planVal;
    }

    public String getPlanVal() {
        return planVal;
    }

    public void setPromptMsg(String promptMsg) {
        this.promptMsg = promptMsg;
    }

    public String getPromptMsg() {
        return promptMsg;
    }


    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public void setAlarmTypeId(String alarmTypeId) {
        this.alarmTypeId = alarmTypeId;
    }

    public String getAlarmTypeId() {
        return alarmTypeId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public AlarmRealtimeData() {

    }

    public AlarmRealtimeData(Long alarmTacticsId, String azwz, String planVal, String alarmTypeId) {
        this.alarmTacticsId = alarmTacticsId;
        this.azwz = azwz;
        this.planVal = planVal;
        this.alarmTypeId = alarmTypeId;
    }

    public AlarmRealtimeData(String azwz, String planVal, Long alarmTacticsId, String promptMsg, String alarmTypeId, Long equipmentId) {
        this.azwz = azwz;
        this.planVal = planVal;
        this.alarmTacticsId = alarmTacticsId;
        this.promptMsg = promptMsg;
        this.alarmTypeId = alarmTypeId;
        this.equipmentId = equipmentId;
    }

    public AlarmRealtimeData(Long alarmTacticsId, String alarmTacticsName, String alarmValue, Date firstTime, Date lastTime, Integer amount,
                             Long confirmState, String azwz, String alarmName, String planVal, String promptMsg, String level, String alarmTypeId) {
        this.alarmTacticsId = alarmTacticsId;
        this.alarmTacticsName = alarmTacticsName;
        this.alarmValue = alarmValue;
        this.firstTime = firstTime;
        this.lastTime = lastTime;
        this.amount = amount;
        this.confirmState = confirmState;
        this.azwz = azwz;
        this.alarmName = alarmName;
        this.planVal = planVal;
        this.promptMsg = promptMsg;
        this.level = level;
        this.alarmTypeId = alarmTypeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("alarmTacticsId", getAlarmTacticsId())
                .append("alarmValue", getAlarmValue())
                .append("firstTime", getFirstTime())
                .append("lastTime", getLastTime())
                .append("amount", getAmount())
                .append("confirmState", getConfirmState())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("azwz", getAzwz())
                .append("alarmName", getAlarmName())
                .append("planVal", getPlanVal())
                .append("promptMsg", getPromptMsg())
                .append("level", getLevel())
                .append("alarmTypeId", getAlarmTypeId())
                .toString();
    }
}
