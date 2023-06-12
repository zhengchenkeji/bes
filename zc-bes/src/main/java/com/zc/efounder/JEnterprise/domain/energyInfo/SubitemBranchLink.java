package com.zc.efounder.JEnterprise.domain.energyInfo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 分项支路关联对象 athena_bes_subitem_branch_link
 *
 * @author qindehua
 * @date 2022-09-20
 */
public class SubitemBranchLink extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 支路ID */
    @Excel(name = "支路ID")
    private Long branchId;

    /** 分项ID */
    @Excel(name = "分项ID")
    private String subitemId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setBranchId(Long branchId)
    {
        this.branchId = branchId;
    }

    public Long getBranchId()
    {
        return branchId;
    }
    public void setSubitemId(String subitemId)
    {
        this.subitemId = subitemId;
    }

    public String getSubitemId()
    {
        return subitemId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("branchId", getBranchId())
            .append("subitemId", getSubitemId())
            .toString();
    }

    public SubitemBranchLink() {
    }

    public SubitemBranchLink(Long branchId, String subitemId) {
        this.branchId = branchId;
        this.subitemId = subitemId;
    }
}
