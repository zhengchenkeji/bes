package com.zc.efounder.JEnterprise.domain.energyInfo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 批量新增分项模板对象 athena_bes_subitem_conf_template
 *
 * @author qindehua
 * @date 2022-11-08
 */
public class SubitemConfTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分项编号 */
    private String subitemCode;

    /** 分项名称 */
    @Excel(name = "分项名称")
    private String subitemName;

    /** 父分项编号 */
    @Excel(name = "父分项编号")
    private String subitemFatherCode;

    /** 级数 */
    @Excel(name = "级数")
    private String level;

    /** 能源编号 */
    @Excel(name = "能源编号")
    private String energyCode;

    /** 建筑能耗代码 */
    @Excel(name = "建筑能耗代码")
    private String buildingEnergyCode;

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
    public void setSubitemFatherCode(String subitemFatherCode)
    {
        this.subitemFatherCode = subitemFatherCode;
    }

    public String getSubitemFatherCode()
    {
        return subitemFatherCode;
    }
    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getLevel()
    {
        return level;
    }
    public void setEnergyCode(String energyCode)
    {
        this.energyCode = energyCode;
    }

    public String getEnergyCode()
    {
        return energyCode;
    }
    public void setBuildingEnergyCode(String buildingEnergyCode)
    {
        this.buildingEnergyCode = buildingEnergyCode;
    }

    public String getBuildingEnergyCode()
    {
        return buildingEnergyCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("subitemCode", getSubitemCode())
            .append("subitemName", getSubitemName())
            .append("subitemFatherCode", getSubitemFatherCode())
            .append("level", getLevel())
            .append("energyCode", getEnergyCode())
            .append("buildingEnergyCode", getBuildingEnergyCode())
            .toString();
    }
}
