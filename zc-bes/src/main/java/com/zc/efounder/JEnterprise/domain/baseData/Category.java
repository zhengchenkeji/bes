package com.zc.efounder.JEnterprise.domain.baseData;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 品类对象 athena_bes_category
 *
 * @author sunshangeng
 * @date 2023-03-06
 */
public class Category extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键自增 */
    @ApiModelProperty(value = "品类标识",required = true)
    private Long id;

    /** 品类标识 */
    @Excel(name = "品类标识")
    @NotBlank(message = "品类标识不能为空")
    @ApiModelProperty(value = "品类标识",required = true)
    private String categoryMark;

    /** 品类名称 */
    @Excel(name = "品类名称")
    @NotBlank(message = "品类名称不能为空")
    @ApiModelProperty(value = "品类标识",required = true)
    private String categoryName;

    /** 是否物联设备 (0:否  1:是) */
    @Excel(name = "是否物联设备",readConverterExp = "0=否,1=是")
    @NotNull(message = "是否物联设备不能为空")
    @ApiModelProperty(value = "品类标识",required = true)
    private Long iotQuipment;

    /** 是否关键设备 (0:否  1:是) */
    @Excel(name = "是否关键设备",readConverterExp = "0=否,1=是")
    @ApiModelProperty(value = "是否关键设备")
    private Long cruxQuipment;
    public void setId(Long id)
    {
        this.id = id;
    }
    /** 备注 */
    @ApiModelProperty(value = "备注",hidden = true)
    @Excel(name = "备注")
    private String remark;


    public Long getId()
    {
        return id;
    }
    public void setCategoryMark(String categoryMark)
    {
        this.categoryMark = categoryMark;
    }

    public String getCategoryMark()
    {
        return categoryMark;
    }
    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName()
    {
        return categoryName;
    }
    public void setIotQuipment(Long iotQuipment)
    {
        this.iotQuipment = iotQuipment;
    }

    public Long getIotQuipment()
    {
        return iotQuipment;
    }
    public void setCruxQuipment(Long cruxQuipment)
    {
        this.cruxQuipment = cruxQuipment;
    }

    public Long getCruxQuipment()
    {
        return cruxQuipment;
    }


    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("categoryMark", getCategoryMark())
            .append("categoryName", getCategoryName())
            .append("iotQuipment", getIotQuipment())
            .append("cruxQuipment", getCruxQuipment())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
