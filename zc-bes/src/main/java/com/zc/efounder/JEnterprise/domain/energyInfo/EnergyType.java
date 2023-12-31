package com.zc.efounder.JEnterprise.domain.energyInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 能源类型对象 energy_type
 *
 * @author ruoyi
 * @date 2022-09-07
 */
@ApiModel(value = "EnergyType",description = "能源类型对象")
public class EnergyType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** guid */
    @ApiModelProperty(value ="能源类型ID",required = true)
    private String guid;

    @Excel(name = "能源类型编号")
    @ApiModelProperty(value ="能源类型编号",required = true)
    /** 能源类型编号 */
    private String code;

    /** 能源类型名称 */
    @Excel(name = "能源类型名称")
    @ApiModelProperty(value ="能源类型名称",required = true)
    private String name;

    /** 单价 */
    @Excel(name = "单价")
    @ApiModelProperty(value ="单价",required = true)
    private Double price;

    /** 耗煤量 */
    @Excel(name = "耗煤量")
    @ApiModelProperty(value ="耗煤量",required = true)
    private Double coalAmount;

    /** 二氧化碳 */
    @Excel(name = "二氧化碳")
    @ApiModelProperty(value ="二氧化碳",required = true)
    private Double co2;

    /** 单位 */
    @Excel(name = "单位")
    @ApiModelProperty(value ="单位",required = true)
    private String unit;

    public void setGuid(String guid)
    {
        this.guid = guid;
    }

    public String getGuid()
    {
        return guid;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setPrice(Double price)
    {
        this.price = price;
    }

    public Double getPrice()
    {
        return price;
    }
    public void setCoalAmount(Double coalAmount)
    {
        this.coalAmount = coalAmount;
    }

    public Double getCoalAmount()
    {
        return coalAmount;
    }
    public void setCo2(Double co2)
    {
        this.co2 = co2;
    }

    public Double getCo2()
    {
        return co2;
    }
    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getUnit()
    {
        return unit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("guid", getGuid())
            .append("code", getCode())
            .append("name", getName())
            .append("price", getPrice())
            .append("coalAmount", getCoalAmount())
            .append("co2", getCo2())
            .append("unit", getUnit())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
