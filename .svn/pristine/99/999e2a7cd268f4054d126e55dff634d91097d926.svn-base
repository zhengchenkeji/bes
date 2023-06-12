package com.zc.efounder.JEnterprise.domain.sceneLink;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 场景联动-场景触发器对象 athena_bes_scene_trigger
 *
 * @author ruoyi
 * @date 2023-02-28
 */
public class SceneTrigger extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 场景id
     */
    @Excel(name = "场景id")
    private Long sceneId;

    /**
     * 触发类型code
     */
    @Excel(name = "触发类型code")
    private String triggerModeCode;

    /** 设备ID*/
    private String deviceId;


    /**设备类型 0bes 1第三方*/
    private String deviceType;

    private String deviceName;
    /**设备触发指令*/
    private String deviceInstruct;
    /**运算指令*/
    private String operator;
    /**运算比较值*/
    private String operatorValue;

    /**
     *任务id
     **/
    private Long jobId;

    /**
     * cron表达式
     */
    @Excel(name = "cron表达式")
    private String cronExpression;

    /**
     * 场景触发id
     */
    @Excel(name = "场景触发id")
    private String triggerSceneId;

    /**场景状态*/
    private Integer sceneStatus;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setTriggerModeCode(String triggerModeCode) {
        this.triggerModeCode = triggerModeCode;
    }

    public String getTriggerModeCode() {
        return triggerModeCode;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setTriggerSceneId(String triggerSceneId) {
        this.triggerSceneId = triggerSceneId;
    }

    public String getTriggerSceneId() {
        return triggerSceneId;
    }

    public Integer getSceneStatus() {
        return sceneStatus;
    }

    public void setSceneStatus(Integer sceneStatus) {
        this.sceneStatus = sceneStatus;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceInstruct() {
        return deviceInstruct;
    }

    public void setDeviceInstruct(String deviceInstruct) {
        this.deviceInstruct = deviceInstruct;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorValue() {
        return operatorValue;
    }

    public void setOperatorValue(String operatorValue) {
        this.operatorValue = operatorValue;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sceneId", getSceneId())
                .append("triggerModeCode", getTriggerModeCode())
                .append("cronExpression", getCronExpression())
                .append("triggerSceneId", getTriggerSceneId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("sceneStatus", getSceneStatus())
                .toString();
    }
}
