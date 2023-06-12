package com.zc.efounder.JEnterprise.domain.baseData;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 第三方能源数据对象 athena_other_calculate_data
 *
 * @author ruoyi
 * @date 2023-04-20
 */
public class OtherCalculateData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 数据项主键 */
    @Excel(name = "数据项主键")
    private Long itemDataId;

    /** 采集时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采集时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date cjsj;

    /** 数据值 */
    @Excel(name = "数据值")
    private BigDecimal dataValue;

    /** 设备主键 */
    @Excel(name = "设备主键")
    private Long equipmentId;

    /** 能源类型 */
    @Excel(name = "能源类型")
    private String energyCode;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setItemDataId(Long itemDataId)
    {
        this.itemDataId = itemDataId;
    }

    public Long getItemDataId()
    {
        return itemDataId;
    }
    public void setCjsj(Date cjsj)
    {
        this.cjsj = cjsj;
    }

    public Date getCjsj()
    {
        return cjsj;
    }
    public void setDataValue(BigDecimal dataValue)
    {
        this.dataValue = dataValue;
    }

    public BigDecimal getDataValue()
    {
        return dataValue;
    }
    public void setEquipmentId(Long equipmentId)
    {
        this.equipmentId = equipmentId;
    }

    public Long getEquipmentId()
    {
        return equipmentId;
    }
    public void setEnergyCode(String energyCode)
    {
        this.energyCode = energyCode;
    }

    public String getEnergyCode()
    {
        return energyCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("itemDataId", getItemDataId())
            .append("cjsj", getCjsj())
            .append("dataValue", getDataValue())
            .append("equipmentId", getEquipmentId())
            .append("energyCode", getEnergyCode())
            .toString();
    }
}
