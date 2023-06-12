package com.zc.efounder.JEnterprise.domain.energyCollection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 电表类型定义对象 meter_type
 *
 * @author ruoyi
 * @date 2022-09-07
 */
@ApiModel(value = "MeterType",description = "电表类型定义对象")
public class MeterType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /** 类型名称 */
    @Excel(name = "类型名称")
    @ApiModelProperty(value = "类型名称",required = true)
    private String name;

    /** 类型编号 */
    @Excel(name = "类型编号")
    @ApiModelProperty(value = "类型编号",required = true)
    private String code;

    /** 能源编号 */
//    @Excel(name = "能源编号")
    @ApiModelProperty(value = "能源编号")
    private String energyNumber;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
    public void setEnergyNumber(String energyNumber)
    {
        this.energyNumber = energyNumber;
    }

    public String getEnergyNumber()
    {
        return energyNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("code", getCode())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("energyNumber", getEnergyNumber())
            .toString();
    }
}
