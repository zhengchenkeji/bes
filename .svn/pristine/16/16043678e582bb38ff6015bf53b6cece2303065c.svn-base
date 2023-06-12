package com.zc.efounder.JEnterprise.domain.safetyWarning;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;

/**
 * @Description 告警接受组
 *
 * @author liuwenge
 * @date 2023/2/28 17:14
 */
@ApiModel(value = "AlarmGroup",description = "告警接收组")
public class AlarmGroup extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /** 组名 */
    @Excel(name = "组名")
    @NotBlank(message = "组名不能为空")
    @ApiModelProperty(value = "组名",required = true)
    private String name;

    /** 所属部门id */
    @NotBlank(message = "部门不能为空")
    @ApiModelProperty(value = "所属部门id",required = true)
    private Long deptId;


    /** 所属部门名称 */
    @Excel(name = "部门")
    @ApiModelProperty(value = "部门",required = true)
    private Long deptName;

    /** 用户id */
    @ApiModelProperty(value = "用户id",required = true)
    private Long userId;


    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptName() {
        return deptName;
    }

    public void setDeptName(Long deptName) {
        this.deptName = deptName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("deptId", getDeptId())
            .append("deptName", getDeptName())
            .append("userId", getUserId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
