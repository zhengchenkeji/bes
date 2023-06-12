package com.zc.efounder.JEnterprise.domain.energyInfo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 分户支路关联对象 athena_bes_household_branch_link
 *
 * @author qindehua
 * @date 2022-09-19
 */
public class AthenaBesHouseholdBranchLink extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 所属支路 */
    @Excel(name = "所属支路")
    private Long branchId;

    /** 所属分户 */
    @Excel(name = "所属分户")
    private Long householdId;

    public AthenaBesHouseholdBranchLink() {
    }

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
    public void setHouseholdId(Long householdId)
    {
        this.householdId = householdId;
    }

    public Long getHouseholdId()
    {
        return householdId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("branchId", getBranchId())
            .append("householdId", getHouseholdId())
            .toString();
    }

    public AthenaBesHouseholdBranchLink(Long branchId, Long householdId) {
        this.branchId = branchId;
        this.householdId = householdId;
    }

}
