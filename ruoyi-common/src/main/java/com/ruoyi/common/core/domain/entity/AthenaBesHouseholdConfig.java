package com.ruoyi.common.core.domain.entity;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 分户计量拓扑配置对象 athena_bes_household_config
 *
 * @author qindehua
 * @date 2022-09-19
 */
@ApiModel(value = "AthenaBesHouseholdConfig",description = "分户计量拓扑配置")
public class AthenaBesHouseholdConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分户ID */
    @ApiModelProperty(value = "分户ID",required = true)
    private Long householdId;

    /** 所属能源 */
    @Excel(name = "所属能源")
    @NotBlank(message = "所属能源不允许为空！")
    @ApiModelProperty(value = "所属能源",required = true)
    private String energyCode;

    /** 分户编号 */
    @Excel(name = "分户编号")
    @ApiModelProperty(value = "分户编号")
    private String householdCode;

    /** 分户名称 */
    @Excel(name = "分户名称")
    @NotBlank(message = "分户名称不允许为空！")
    @ApiModelProperty(value = "分户名称",required = true)
    private String householdName;

    /** 所属父分户 */
    @Excel(name = "所属父分户")
    @NotNull(message = "所属父分户不允许为空！")
    @ApiModelProperty(value = "所属父分户",required = true)
    private Long parentId;

    /** 所属园区 */
    @Excel(name = "所属园区")
    @NotBlank(message = "所属园区不允许为空！")
    @ApiModelProperty(value = "所属园区",required = true)
    private String parkCode;

    /** 所属建筑 */
    @Excel(name = "所属建筑")
    @ApiModelProperty(value = "所属建筑",required = true)
    @NotNull(message = "所属建筑不允许为空！")
    private Long buildingId;

    /** 支路级联配置 0：否 1：是 */
    @Excel(name = "支路级联配置 0：否 1：是")
    @ApiModelProperty(value = "支路级联配置 0：否 1：是",required = true)
    @NotBlank(message = "级联配置不允许为空！")
    private String cascaded;

    /** 人数 */
    @Excel(name = "人数")
    @NotNull(message = "人数不允许为空！")
    @ApiModelProperty(value = "人数",required = true)
    private Long population;

    /** 面积 */
    @Excel(name = "面积")
    @NotNull(message = "面积不允许为空！")
    @ApiModelProperty(value = "面积",required = true)
    private Double area;

    /** 户型 */
    @Excel(name = "户型")
    @NotBlank(message = "户型不允许为空！")
    @ApiModelProperty(value = "户型",required = true)
    private String houseType;

    /** 所属位置 */
    @Excel(name = "所属位置")
    @NotBlank(message = "所属位置不允许为空！")
    @ApiModelProperty(value = "所属位置",required = true)
    private String location;

    /** 子节点 */
    private List<AthenaBesHouseholdConfig> children=new ArrayList<>();

    public List<AthenaBesHouseholdConfig> getChildren() {
        return children;
    }

    public void setChildren(List<AthenaBesHouseholdConfig> children) {
        this.children = children;
    }

    public void setHouseholdId(Long householdId)
    {
        this.householdId = householdId;
    }

    public Long getHouseholdId()
    {
        return householdId;
    }

    public void setEnergyCode(String energyCode)
    {
        this.energyCode = energyCode;
    }

    public String getEnergyCode()
    {
        return energyCode;
    }

    public String getHouseholdCode() {
        return householdCode;
    }

    public void setHouseholdCode(String householdCode) {
        this.householdCode = householdCode;
    }

    public String getHouseholdName() {
        return householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public Long getParentId()
    {
        return parentId;
    }
    public void setParkCode(String parkCode)
    {
        this.parkCode = parkCode;
    }

    public String getParkCode()
    {
        return parkCode;
    }
    public void setBuildingId(Long buildingId)
    {
        this.buildingId = buildingId;
    }

    public Long getBuildingId()
    {
        return buildingId;
    }
    public void setCascaded(String cascaded)
    {
        this.cascaded = cascaded;
    }

    public String getCascaded()
    {
        return cascaded;
    }
    public void setPopulation(Long population)
    {
        this.population = population;
    }

    public Long getPopulation()
    {
        return population;
    }
    public void setArea(Double area)
    {
        this.area = area;
    }

    public Double getArea()
    {
        return area;
    }
    public void setHouseType(String houseType)
    {
        this.houseType = houseType;
    }

    public String getHouseType()
    {
        return houseType;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("householdId", getHouseholdId())
            .append("energyCode", getEnergyCode())
            .append("householdCode", getHouseholdCode())
            .append("householdName", getHouseholdName())
            .append("parentId", getParentId())
            .append("parkCode", getParkCode())
            .append("buildingId", getBuildingId())
            .append("cascaded", getCascaded())
            .append("population", getPopulation())
            .append("area", getArea())
            .append("houseType", getHouseType())
            .append("location", getLocation())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
