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
 * 分项拓扑配置对象 athena_bes_subitem_config
 *
 * @author qindehua
 * @date 2022-09-20
 */
@ApiModel(value = "SubitemConfig", description = "分项拓扑配置")
public class SubitemConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分项ID */
    @ApiModelProperty(value = "分项ID",required = true)
    private String subitemId;

    /** 分项编号 */
    @Excel(name = "分项编号")
    @ApiModelProperty(value = "分项编号")
    private String subitemCode;

    /** 建筑能耗代码 */
    @Excel(name = "建筑能耗代码")
    @ApiModelProperty(value = "建筑能耗代码",required = true)
    @NotBlank(message = "建筑能耗代码不允许为空！")
    private String buildingEnergyCode;

    /** 分项名称 */
    @Excel(name = "分项名称")
    @ApiModelProperty(value = "分项名称",required = true)
    @NotBlank(message = "分项名称不允许为空！")
    private String subitemName;

    /** 所属能源 */
    @Excel(name = "所属能源")
    @ApiModelProperty(value = "所属能源",required = true)
    @NotBlank(message = "所属能源不允许为空！")
    private String energyCode;

    /** 所属园区 */
    @Excel(name = "所属园区")
    @ApiModelProperty(value = "所属园区",required = true)
    @NotBlank(message = "所属园区不允许为空！")
    private String parkCode;

    /** 所属父分项 */
    @Excel(name = "所属父分项")
    @ApiModelProperty(value = "所属父分项",required = true)
    @NotBlank(message = "所属父分项不允许为空！")
    private String parentId;

    /** 所属建筑 */
    @Excel(name = "所属建筑")
    @ApiModelProperty(value = "所属建筑",required = true)
    @NotNull(message = "所属建筑不允许为空！")
    private Long buildingId;

    /**级联配置  0：否 1：是 */
    @Excel(name = "0：否 1：是")
    @ApiModelProperty(value = "级联配置  0：否 1：是",required = true)
    @NotBlank(message = "级联配置不允许为空！")
    private String cascaded;

    /** 子节点 */
    private List<SubitemConfig> children=new ArrayList<>();

    public List<SubitemConfig> getChildren() {
        return children;
    }

    public void setChildren(List<SubitemConfig> children) {
        this.children = children;
    }

    public void setSubitemId(String subitemId)
    {
        this.subitemId = subitemId;
    }

    public String getSubitemId()
    {
        return subitemId;
    }
    public void setSubitemCode(String subitemCode)
    {
        this.subitemCode = subitemCode;
    }

    public String getSubitemCode()
    {
        return subitemCode;
    }
    public void setSubitemName(String subitemName)
    {
        this.subitemName = subitemName;
    }

    public String getSubitemName()
    {
        return subitemName;
    }
    public void setEnergyCode(String energyCode)
    {
        this.energyCode = energyCode;
    }

    public String getEnergyCode()
    {
        return energyCode;
    }
    public void setParkCode(String parkCode)
    {
        this.parkCode = parkCode;
    }

    public String getParkCode()
    {
        return parkCode;
    }
    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getParentId()
    {
        return parentId;
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

    public String getBuildingEnergyCode() {
        return buildingEnergyCode;
    }

    public void setBuildingEnergyCode(String buildingEnergyCode) {
        this.buildingEnergyCode = buildingEnergyCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("subitemId", getSubitemId())
            .append("subitemCode", getSubitemCode())
            .append("subitemName", getSubitemName())
            .append("energyCode", getEnergyCode())
            .append("parkCode", getParkCode())
            .append("parentId", getParentId())
            .append("buildingId", getBuildingId())
            .append("cascaded", getCascaded())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("buildingEnergyCode", getBuildingEnergyCode())
            .toString();
    }
}
