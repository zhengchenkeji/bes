package com.zc.efounder.JEnterprise.domain.scheduling;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: wanghongjie
 * @Description:计划编排--场景信息
 * @Date: Created in 14:56 2022/11/5
 * @Modified By:
 */
@ApiModel(value = "SceneControl",description = "计划编排--场景信息")
public class SceneControl extends BaseEntity {

    /**
     *主键id
     */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /**
     *区域id
     */
    @ApiModelProperty(value = "区域id",required = true)
    private Long schedulingAreaId;
    /**
     *场景名称
     */
    @ApiModelProperty(value = "场景名称",required = true)
    @Excel(name = "场景名称")
    private String name;
    /**
     *场景别名
     */
    @ApiModelProperty(value = "场景别名",required = true)
    @Excel(name = "场景别名")
    private String alias;
    /**
     *场景描述
     */
    @ApiModelProperty(value = "场景描述")
    @Excel(name = "场景描述")
    private String description;

    @ApiModelProperty(value = "使能状态 0=关闭,1=开启")
    @Excel(name = "使能状态" ,readConverterExp = "0=关闭,1=开启")
    private Integer active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSchedulingAreaId() {
        return schedulingAreaId;
    }

    public void setSchedulingAreaId(Long schedulingAreaId) {
        this.schedulingAreaId = schedulingAreaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}
