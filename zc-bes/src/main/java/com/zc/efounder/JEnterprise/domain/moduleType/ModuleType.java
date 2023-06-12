package com.zc.efounder.JEnterprise.domain.moduleType;


import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 模块类型对象 module_type
 *
 * @author ruoyi
 * @date 2022-09-06
 */
@ApiModel(value = "ModuleType",description = "模块类型对象")
public class ModuleType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /** 模块型号 */
    @Excel(name = "模块型号")
    @ApiModelProperty(value = "模块型号",required = true)
    private String moduleCode;

    /** 下位机区分温控器 */
    @Excel(name = "下位机区分温控器")
    @ApiModelProperty(value = "下位机区分温控器",required = true)
    private Long typeCode;

    /** 模块类型；0 AI;1 AO;2 DI; 3  DO;4 UI;5 UX */
    @Excel(name = "模块类型；0 AI;1 AO;2 DI; 3  DO;4 UI;5 UX")
    @ApiModelProperty(value = "模块类型；0 AI;1 AO;2 DI; 3  DO;4 UI;5 UX ",required = true)
    private String pointSet;

    /** 模块描述 */
    @Excel(name = "模块描述")
    @ApiModelProperty(value = "模块描述")
    private String description;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setModuleCode(String moduleCode)
    {
        this.moduleCode = moduleCode;
    }

    public String getModuleCode()
    {
        return moduleCode;
    }
    public void setTypeCode(Long typeCode)
    {
        this.typeCode = typeCode;
    }

    public Long getTypeCode()
    {
        return typeCode;
    }
    public void setPointSet(String pointSet)
    {
        this.pointSet = pointSet;
    }

    public String getPointSet()
    {
        return pointSet;
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
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("moduleCode", getModuleCode())
            .append("typeCode", getTypeCode())
            .append("pointSet", getPointSet())
            .append("description", getDescription())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
