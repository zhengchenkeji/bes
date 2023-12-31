package com.zc.efounder.JEnterprise.domain.modulePoint;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 模块点类型定义对象 module_point
 *
 * @author ruoyi
 * @date 2022-09-06
 */
@ApiModel(value = "ModulePoint",description = "模块点类型定义对象")
public class ModulePoint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /** 模块型号 */
    @Excel(name = "模块点类型")
    @ApiModelProperty(value = "模块型号",required = true)
    private String modulePoint;

    /** 模块描述 */
    @Excel(name = "模块描述")
    @ApiModelProperty(value = "模块描述",required = true)
    private String description;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /** 修改时间 */
    @ApiModelProperty(value = "修改时间",hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;


    public void setId(Long id)
    {
        this.id = id;
    }
    public Long getId()
    {
        return id;
    }
    public void setModulePoint(String modulePoint)
    {
        this.modulePoint = modulePoint;
    }
    public String getModulePoint()
    {
        return modulePoint;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public String getDescription()
    {
        return description;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("modulePoint", getModulePoint())
            .append("description", getDescription())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
