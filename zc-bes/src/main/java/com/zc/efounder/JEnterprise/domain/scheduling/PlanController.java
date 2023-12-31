package com.zc.efounder.JEnterprise.domain.scheduling;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 计划编排对象 PlanConfig
 *
 * @author gaojikun
 * @date 2022-11-10
 */
@ApiModel(value = "PlanController",description = "计划编排对象")
public class PlanController extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /**
     * 所属控制模式
     */
    @ApiModelProperty(value = "所属控制模式",required = true)
    private Long modelControlId;

    /**
     * 所属场景
     */
    @ApiModelProperty(value = "所属场景",required = true)
    private Long sceneControlId;

    /**
     * 计划名称
     */
    @ApiModelProperty(value = "计划名称",required = true)
    @Excel(name = "计划名称")
    private String name;

    /**
     * 计划别名
     */
    @ApiModelProperty(value = "计划别名",required = true)
    @Excel(name = "计划别名")
    private String alias;

    /**
     * 是否启用 0：禁止、1：使能
     */
    @ApiModelProperty(value = "是否启用 0：禁止、1：使能",required = true)
    @Excel(name = "是否启用", readConverterExp = "0=否,1=是")
    private Long active;

    /**
     * 替代日 0：禁止、1：使能
     */
    @ApiModelProperty(value = "替代日 0：禁止、1：使能")
    @Excel(name = "替代日", readConverterExp = "0=否,1=是")
    private Long planType;

    /**
     * 计划类型 0：控制、1：采集
     */
    @ApiModelProperty(value = "计划类型 0：控制、1：采集")
//    @Excel(name = "计划类型 0：控制、1：采集")
    private Long sceneType;

    /**
     * 计划开始日期
     */
    @ApiModelProperty(value = "计划开始日期",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 计划开始时间
     */
    @ApiModelProperty(value = "计划开始时间",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "计划开始时间", width = 30, dateFormat = "HH:mm:ss")
    private Date startTime;

    /**
     * 计划结束日期
     */
    @ApiModelProperty(value = "计划结束日期",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 计划结束时间
     */
    @ApiModelProperty(value = "计划结束时间",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "计划结束时间", width = 30, dateFormat = "HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(hidden = true)
    private Date compareStartDataTime;

    @ApiModelProperty(hidden = true)
    private Date compareEndDataTime;

    /**
     * 执行方式 0-按天执行 在设定日期内，每天从计划开始时间执行到计划结束时间结束执行、1-持续执行从计划开始日期的计划开始时间执行，到计划结束日期的结束时间结束执行
     */
    @ApiModelProperty(value = "执行方式 0-按天执行 在设定日期内，每天从计划开始时间执行到计划结束时间结束执行、1-持续执行从计划开始日期的计划开始时间执行，到计划结束日期的结束时间结束执行",required = true)
    @Excel(name = "执行方式", readConverterExp = "0=按天执行,1=持续执行")
    private Long executionWay;

    /**
     * 周掩码 周执行频率7位二进制，从低位到高位依次代表周一到周日 例 1111100  周六周日不执行
     */
    @ApiModelProperty(value = "周掩码 周执行频率7位二进制，从低位到高位依次代表周一到周日 例 1111100  周六周日不执行")
    @Excel(name = "周掩码")
    private String weekMask;

    /**
     * 采集方式 1-变化量 2-时间间隔
     */
//    @Excel(name = "采集方式 1-变化量 2-时间间隔")
    @ApiModelProperty(value = "采集方式 1-变化量 2-时间间隔")
    private Long collectType;

    /**
     * 时间间隔(单位为分钟)
     */
//    @Excel(name = "时间间隔(单位为分钟)")
    @ApiModelProperty(value = "时间间隔(单位为分钟)")
    private Long interval;

    /**
     * 任务调度
     */
//    @Excel(name = "任务调度")
    @ApiModelProperty(value = "任务调度")
    private Long schedulingId;

    /**
     * 同步状态:0：未同步, 1：已同步
     */
    @ApiModelProperty(value = "同步状态:0：未同步, 1：已同步")
    @Excel(name = "同步状态", readConverterExp = "0=未同步,1=已同步")
    private Long synchState;

    /**
     * 所属计划id
     */
    @ApiModelProperty(value = "所属计划id")
    private Long planId;

    /**
     * 场景名称
     */
    @Excel(name = "所属场景")
    @ApiModelProperty(value = "场景名称")
    private String sceneControlName;

    /**
     * 场景描述
     */
    @ApiModelProperty(value = "场景描述")
    private String sceneControlDescribe;

    /**
     * 模式名称
     */
    @Excel(name = "所属控制模式")
    @ApiModelProperty(value = "模式名称")
    private String modelControlName;

    /**
     * 关联点位
     */
    @ApiModelProperty(value = "关联点位")
    private String modelPoints;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setModelControlId(Long modelControlId) {
        this.modelControlId = modelControlId;
    }

    public Long getModelControlId() {
        return modelControlId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSceneControlId() {
        return sceneControlId;
    }

    public void setSceneControlId(Long sceneControlId) {
        this.sceneControlId = sceneControlId;
    }

    public String getName() {
        return name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setActive(Long active) {
        this.active = active;
    }

    public Long getActive() {
        return active;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Long getPlanType() {
        return planType;
    }

    public void setPlanType(Long planType) {
        this.planType = planType;
    }

    public Long getSceneType() {
        return sceneType;
    }

    public void setSceneType(Long sceneType) {
        this.sceneType = sceneType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setExecutionWay(Long executionWay) {
        this.executionWay = executionWay;
    }

    public Long getExecutionWay() {
        return executionWay;
    }

    public void setWeekMask(String weekMask) {
        this.weekMask = weekMask;
    }

    public String getWeekMask() {
        return weekMask;
    }

    public void setCollectType(Long collectType) {
        this.collectType = collectType;
    }

    public Long getCollectType() {
        return collectType;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public Long getInterval() {
        return interval;
    }

    public void setSchedulingId(Long schedulingId) {
        this.schedulingId = schedulingId;
    }

    public Long getSchedulingId() {
        return schedulingId;
    }

    public void setSynchState(Long synchState) {
        this.synchState = synchState;
    }

    public Long getSynchState() {
        return synchState;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getSceneControlName() {
        return sceneControlName;
    }

    public void setSceneControlName(String sceneControlName) {
        this.sceneControlName = sceneControlName;
    }

    public String getSceneControlDescribe() {
        return sceneControlDescribe;
    }

    public void setSceneControlDescribe(String sceneControlDescribe) {
        this.sceneControlDescribe = sceneControlDescribe;
    }

    public String getModelControlName() {
        return modelControlName;
    }

    public void setModelControlName(String modelControlName) {
        this.modelControlName = modelControlName;
    }

    public String getModelPoints() {
        return modelPoints;
    }

    public void setModelPoints(String modelPoints) {
        this.modelPoints = modelPoints;
    }

    public Date getCompareStartDataTime() {
        return compareStartDataTime;
    }

    public void setCompareStartDataTime(Date compareStartDataTime) {
        this.compareStartDataTime = compareStartDataTime;
    }

    public Date getCompareEndDataTime() {
        return compareEndDataTime;
    }

    public void setCompareEndDataTime(Date compareEndDataTime) {
        this.compareEndDataTime = compareEndDataTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("modelControlId", getModelControlId())
                .append("name", getName())
                .append("alias", getAlias())
                .append("active", getActive())
                .append("planType", getPlanType())
                .append("startDate", getStartDate())
                .append("startTime", getStartTime())
                .append("endDate", getEndDate())
                .append("endTime", getEndTime())
                .append("executionWay", getExecutionWay())
                .append("weekMask", getWeekMask())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("collectType", getCollectType())
                .append("interval", getInterval())
                .append("schedulingId", getSchedulingId())
                .append("synchState", getSynchState())
                .toString();
    }
}
