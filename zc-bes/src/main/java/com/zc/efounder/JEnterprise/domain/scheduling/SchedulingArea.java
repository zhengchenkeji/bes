package com.zc.efounder.JEnterprise.domain.scheduling;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: wanghongjie
 * @Description:计划编排区域树信息
 * @Date: Created in 16:25 2022/11/5
 * @Modified By:
 */
@ApiModel(value = "SchedulingArea",description = "计划编排区域树信息")
public class SchedulingArea extends BaseEntity {

    /**
     *主键id
     */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /**
     *父节点id
     */
    @ApiModelProperty(value = "父节点id",required = true)
    private Long parentId;
    /**
     *区域名称
     */
    @ApiModelProperty(value = "区域名称",required = true)
    private String name;
    /**
     *区域备注
     */
    @ApiModelProperty(value = "区域备注")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
