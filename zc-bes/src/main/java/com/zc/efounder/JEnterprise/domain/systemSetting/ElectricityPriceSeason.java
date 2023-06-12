package com.zc.efounder.JEnterprise.domain.systemSetting;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 电价-季节范围对象 athena_bes_electricity_price_season
 *
 * @author liuwenge
 * @date 2023-02-20
 */
@ApiModel(value ="ElectricityPriceSeason",description = "电价-季节范围对象")
public class ElectricityPriceSeason extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    @ApiModelProperty(value = "名称",required = true)
    private String name;

    /** 开始日期 */
    @Excel(name = "开始日期")
    @ApiModelProperty(value = "开始日期",required = true)
    private String startDate;

    /** 结束日期 */
    @Excel(name = "结束日期")
    @ApiModelProperty(value = "结束日期",required = true)
    private String endDate;

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
    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getStartDate()
    {
        return startDate;
    }
    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .toString();
    }
}
