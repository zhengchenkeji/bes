package com.zc.efounder.JEnterprise.domain.energyInfo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 手动录入能耗详情对象 athena_bes_manualentryenergy_collection
 *
 * @author ruoyi
 * @date 2022-12-02
 */
@ApiModel(value = "ManualentryenergyCollection",description = "手动录入能耗详情对象")
public class ManualentryenergyCollection extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @ApiModelProperty(value = "唯一ID",hidden = true)
    private Long id;

    /** 采集参数id */
    @Excel(name = "采集参数code")
    @ApiModelProperty(value = "采集参数code",required = true)
    private String paramCode;

    @Excel(name = "采集参数名称")
    @ApiModelProperty(value = "采集参数名称",required = true)
    private String paramName;

    /** 手动录入采集参数id */
    @Excel(name = "手动录入采集参数id")
    @ApiModelProperty(value = "手动录入采集参数id",hidden = true)
    private Long manualentryenergyId;

    /** 录入能源值 */
    @Excel(name = "录入能源值")
    @ApiModelProperty(value = "录入能源值",required = true)
    private Double energyValue;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
//    public void setParamId(String paramId)
//    {
//        this.paramId = paramId;
//    }
//
//    public String getParamId()
//    {
//        return paramId;
//    }


    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public void setManualentryenergyId(Long manualentryenergyId)
    {
        this.manualentryenergyId = manualentryenergyId;
    }

    public Long getManualentryenergyId()
    {
        return manualentryenergyId;
    }
    public void setEnergyValue(Double energyValue)
    {
        this.energyValue = energyValue;
    }

    public Double getEnergyValue()
    {
        return energyValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("paramCode", getParamCode())
            .append("manualentryenergyId", getManualentryenergyId())
            .append("energyValue", getEnergyValue())
            .append("createTime", getCreateTime())
            .toString();
    }
}
