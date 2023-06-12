package com.zc.efounder.JEnterprise.domain.systemSetting;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 电价设置对象 athena_bes_electricity_price_setting
 * 
 * @author ruoyi
 * @date 2022-11-29
 */
public class ElectricityPriceRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 父Id */
    private Long parentId;

    /** 名称 */
    private String name;

    /** 备注 */
    private String remark;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
            .append("parentId", getParentId())
            .append("name", getName())
            .append("remark", getRemark())
            .toString();
    }
}
