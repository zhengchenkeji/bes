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
 * 支路拓扑配置对象 athena_bes_branch_config
 *
 */

@ApiModel(value = "AthenaBranchConfig", description = "支路拓扑")
public class AthenaBranchConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 支路ID */
    @ApiModelProperty(value = "支路ID",required = true)
    private Long branchId;

    /** 支路编号 */
    @Excel(name = "支路编号")
    @ApiModelProperty(value = "支路编号")
    private String branchCode;

    /** 支路名称 */
    @Excel(name = "支路名称")
    @ApiModelProperty(value = "支路名称",required = true)
    @NotBlank(message = "支路名称不能为空！")
    private String branchName;

    /** 所属父支路 */
    @Excel(name = "所属父支路")
    @ApiModelProperty(value = "所属父支路 一级菜单请输入0",required = true)
    @NotNull(message = "所属父支路不允许为空！")
    private Long parentId;

    /** 所属能源 */
    @Excel(name = "所属能源")
    @ApiModelProperty(value = "所属能源",required = true)
    @NotBlank(message = "所属能源不能为空！")
    private String energyCode;

    /** 所属建筑 */
    @Excel(name = "所属建筑")
    @ApiModelProperty(value = "所属建筑",required = true)
    @NotNull(message = "所属建筑不能为空！")
    private Long buildingId;

    /** 所属园区 */
    @Excel(name = "所属园区")
    @ApiModelProperty(value = "所属园区",required = true)
    @NotBlank(message = "所属园区不能为空！")
    private String parkCode;

    /** 电表级联配置 0：否  1：是 */
    @ApiModelProperty(value = "电表级联配置 0：否  1：是",required = true)
    @Excel(name = "电表级联配置 0：否1：是",readConverterExp = "1=是,0=否")
    @NotBlank(message = "级联配置不能为空！")
    private String cascaded;

    /** 额定功率 */
    @Excel(name = "额定功率")
    @ApiModelProperty(value = "额定功率")
    private Double ratedPower;

    /** 安装位置 */
    @Excel(name = "安装位置")
    @ApiModelProperty(value = "安装位置")
    private String position;

    /** 采集参数配置ID集合，逗号隔开 */
    private String paramsId;

    /** 子节点 */
    private List<AthenaBranchConfig> children=new ArrayList<>();

    public List<AthenaBranchConfig> getChildren() {
        return children;
    }

    public void setChildren(List<AthenaBranchConfig> children) {
        this.children = children;
    }

    public void setBranchId(Long branchId)
    {
        this.branchId = branchId;
    }

    public Long getBranchId()
    {
        return branchId;
    }
    public void setBranchCode(String branchCode)
    {
        this.branchCode = branchCode;
    }

    public String getBranchCode()
    {
        return branchCode;
    }
    public void setBranchName(String branchName)
    {
        this.branchName = branchName;
    }

    public String getBranchName()
    {
        return branchName;
    }
    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public Long getParentId()
    {
        return parentId;
    }
    public void setEnergyCode(String energyCode)
    {
        this.energyCode = energyCode;
    }

    public String getEnergyCode()
    {
        return energyCode;
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
    public void setRatedPower(Double ratedPower)
    {
        this.ratedPower = ratedPower;
    }

    public Double getRatedPower()
    {
        return ratedPower;
    }
    public void setPosition(String position)
    {
        this.position = position;
    }

    public String getPosition()
    {
        return position;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getParamsId() {
        return paramsId;
    }

    public void setParamsId(String paramsId) {
        this.paramsId = paramsId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("branchId", getBranchId())
            .append("branchCode", getBranchCode())
            .append("branchName", getBranchName())
            .append("parentId", getParentId())
            .append("energyCode", getEnergyCode())
            .append("parkCode", getParkCode())
            .append("buildingId", getBuildingId())
            .append("cascaded", getCascaded())
            .append("ratedPower", getRatedPower())
            .append("position", getPosition())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
