package com.zc.efounder.JEnterprise.domain.safetyWarning;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 告警接收组对象 alarm_notifier
 *
 * @author sunshangeng
 * @date 2022-09-15
 */
@ApiModel(value = "AlarmNotifier",description = "告警接收组对象")
public class AlarmNotifier extends BaseEntity
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
    private String deptId;


    /** 所属部门名称 */
    @Excel(name = "部门")
    @ApiModelProperty(value = "部门",required = true)
    private String deptName;

    /** 用户id */
    @ApiModelProperty(value = "用户id",required = true)
    private String userId;

    /**增加告警策略id 用于查询时传参*/
    private String tacticsId;

    /** 增加是否 吃否绑定关联关系参数 用于查询 1 :关联 0：未关联*/
    private Integer islink;

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

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getIslink() {
        return islink;
    }
    public void setIslink(Integer islink) {
        this.islink = islink;
    }

    public String getTacticsId() {
        return tacticsId;
    }

    public void setTacticsId(String tacticsId) {
        this.tacticsId = tacticsId;
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
