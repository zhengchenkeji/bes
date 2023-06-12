package com.zc.efounder.JEnterprise.domain.scheduling;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 采集计划对象 athena_bes_plan_collection
 *
 * @author ruoyi
 * @date 2022-11-10
 */
@ApiModel(value = "PlanCollection",description = "采集计划对象")
public class PlanCollection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @ApiModelProperty(value = "唯一ID")
    private Long id;

    /** 所属采集模式 */
    @Excel(name = "所属采集模式")
    @ApiModelProperty(value = "所属采集模式")
    private Long modelCollectionId;

    /** 计划名称 */
    @Excel(name = "计划名称")
    @ApiModelProperty(value = "计划名称")
    private String name;

    /** 计划别名 */
    @Excel(name = "计划别名")
    @ApiModelProperty(value = "计划别名")
    private String alias;

    /** 采集类型1-时间、 2-变化量 */
    @Excel(name = "采集类型1-时间、 2-变化量")
    @ApiModelProperty(value = "采集类型1-时间、 2-变化量")
    private Long collectionType;

    /** 变化值 */
    @Excel(name = "变化值")
    @ApiModelProperty(value = "变化值")
    private String variationValue;

    /** cron表达式 */
    @Excel(name = "cron表达式")
    @ApiModelProperty(value = "cron表达式")
    private String cronExpr;


    /** 启停状态： 0-停止 1-运行 */
    @Excel(name = "启停状态： 0-停止 1-运行")
    @ApiModelProperty(value = "启停状态： 0-停止 1-运行")
    private Long startStatus;

    /** 所属计划id */
    @Excel(name = "所属计划id")
    @ApiModelProperty(value = "所属计划id")
    private Long planId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setModelCollectionId(Long modelCollectionId)
    {
        this.modelCollectionId = modelCollectionId;
    }

    public Long getModelCollectionId()
    {
        return modelCollectionId;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public String getAlias()
    {
        return alias;
    }
    public void setCollectionType(Long collectionType)
    {
        this.collectionType = collectionType;
    }

    public Long getCollectionType()
    {
        return collectionType;
    }
    public void setVariationValue(String variationValue)
    {
        this.variationValue = variationValue;
    }

    public String getVariationValue()
    {
        return variationValue;
    }
    public void setCronExpr(String cronExpr)
    {
        this.cronExpr = cronExpr;
    }

    public String getCronExpr()
    {
        return cronExpr;
    }
    public void setStartStatus(Long startStatus)
    {
        this.startStatus = startStatus;
    }

    public Long getStartStatus()
    {
        return startStatus;
    }
    public void setPlanId(Long planId)
    {
        this.planId = planId;
    }

    public Long getPlanId()
    {
        return planId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("modelCollectionId", getModelCollectionId())
            .append("name", getName())
            .append("alias", getAlias())
            .append("collectionType", getCollectionType())
            .append("variationValue", getVariationValue())
            .append("cronExpr", getCronExpr())
            .append("startStatus", getStartStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("planId", getPlanId())
            .toString();
    }
}
