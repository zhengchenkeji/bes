package com.zc.efounder.JEnterprise.domain.energyDataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 建筑群信息对象 build_group_info
 *
 * @author ruoyi
 * @date 2022-09-13
 */
@ApiModel(value = "BuildGroupInfo",description = "建筑群信息对象")
public class BuildGroupInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @ApiModelProperty(value = "唯一id",required = true)
    private Long id;

    /** 建筑群名称 */
    @Excel(name = "建筑群名称")
    @ApiModelProperty(value = "建筑群名称",required = true)
    private String buildGroupName;

    /** 建筑群别名 */
    @Excel(name = "建筑群别名")
    @ApiModelProperty(value = "建筑群别名",required = true)
    private String groupAliasName;

    /** 建筑群描述 */
    @Excel(name = "建筑群描述")
    @ApiModelProperty(value = "建筑群描述",required = true)
    private String groupDesc;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setBuildGroupName(String buildGroupName)
    {
        this.buildGroupName = buildGroupName;
    }

    public String getBuildGroupName()
    {
        return buildGroupName;
    }
    public void setGroupAliasName(String groupAliasName)
    {
        this.groupAliasName = groupAliasName;
    }

    public String getGroupAliasName()
    {
        return groupAliasName;
    }
    public void setGroupDesc(String groupDesc)
    {
        this.groupDesc = groupDesc;
    }

    public String getGroupDesc()
    {
        return groupDesc;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("buildGroupName", getBuildGroupName())
            .append("groupAliasName", getGroupAliasName())
            .append("groupDesc", getGroupDesc())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
