package com.zc.efounder.JEnterprise.domain.scheduling;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 计划编排对象 PlanConfig
 *
 * @author gaojikun
 * @date 2022-11-10
 */
@ApiModel(value = "PlanConfig",description = "计划编排对象")
public class PlanConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    @ApiModelProperty(value = "名称",required = true)
    private String name;

    @ApiModelProperty(value = "父ID",required = true)
    private Long parentId;

    @ApiModelProperty(value = "类型",required = true)
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("parentId", getParentId())
                .append("type", getType())
                .toString();
    }
}
