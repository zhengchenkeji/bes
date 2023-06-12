package com.zc.efounder.JEnterprise.domain.safetyWarning;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 告警工单对象 athena_bes_alarm_word_order
 *
 * @author ruoyi
 * @date 2023-03-06
 */
public class AlarmWorkOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 告警策略
     */
    private Long alarmTacticsId;

    @Excel(name = "告警策略")
    private String alarmTacticsName;

    /**
     * 告警值
     */
    @Excel(name = "告警值")
    private String alarmValue;

    /**
     * 第一次产生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "第一次产生时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date firstTime;

    /**
     * 最后一次产生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "最后一次产生时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastTime;

    /**
     * 告警次数
     */
    @Excel(name = "告警次数")
    private Integer amount;

    /**
     * 确认状态 0：未确认、1：已确认
     */
    @Excel(name = "确认状态", readConverterExp = "0=未确认,1=已确认")
    private Long confirmState;

    /**
     * 告警位置
     */
    @Excel(name = "告警位置")
    private String azwz;

    /**
     * 报警名称
     */
    @Excel(name = "报警名称")
    private String alarmName;

    /**
     * 计划值
     */
    @Excel(name = "计划值")
    private String planVal;

    /**
     * 提示信息 （告警描述）
     */
    @Excel(name = "告警描述")
    private String promptMsg;

    /**
     * 告警等级 1：一般、2：较大、3：严重
     */
    @Excel(name = "告警等级", readConverterExp = "1=一般,2=较大,3=严重")
    private String level;

    /**
     * 所属告警类型 1:电表  2:支路  3:分户 4:分项 5:设备报警
     */
    @Excel(name = "所属告警类型", readConverterExp = " 1=电表,2=支路,3=分户,4=分项,5=设备报警")
    private String alarmTypeId;

    /**
     * 所属用户id
     */
    private String userId;

    /**
     * 处理人编码
     */
    private String updateCode;

    /**
     * 处理人
     */
    @Excel(name = "处理人")
    private String updateName;

    /**
     * 状态(0未处理 1已处理)
     */
    @Excel(name = "处理状态", readConverterExp = "0=未处理,1=已处理")
    private String status;

    /**
     * 备注信息
     */
//    @Excel(name = "备注信息")
    private String remark;

    /**
     * 设备id
     * gaojikun
     */
    private Long equipmentId;

    /**
     * 设备id集合
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUpdateCode(String updateCode) {
        this.updateCode = updateCode;
    }

    public String getUpdateCode() {
        return updateCode;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getAlarmTacticsName() {
        return alarmTacticsName;
    }

    public void setAlarmTacticsName(String alarmTacticsName) {
        this.alarmTacticsName = alarmTacticsName;
    }

    public AlarmWorkOrder() {

    }

    public AlarmWorkOrder(Long alarmTacticsId, String azwz, String planVal, String alarmTypeId) {
        this.alarmTacticsId = alarmTacticsId;
        this.azwz = azwz;
        this.planVal = planVal;
        this.alarmTypeId = alarmTypeId;
    }

    public AlarmWorkOrder(Long alarmTacticsId, String alarmTacticsName, String alarmValue, Date firstTime, Date lastTime, Integer amount, Long confirmState,
                          String azwz, String alarmName, String planVal, String promptMsg, String level, String alarmTypeId, String userId, String status) {
        this.alarmTacticsId = alarmTacticsId;
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
        this.userId = userId;
        this.status = status;
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
                .append("azwz", getAzwz())
                .append("alarmName", getAlarmName())
                .append("planVal", getPlanVal())
                .append("promptMsg", getPromptMsg())
                .append("level", getLevel())
                .append("alarmTypeId", getAlarmTypeId())
                .append("userId", getUserId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("updateCode", getUpdateCode())
                .append("updateName", getUpdateName())
                .append("status", getStatus())
                .toString();
    }
}
