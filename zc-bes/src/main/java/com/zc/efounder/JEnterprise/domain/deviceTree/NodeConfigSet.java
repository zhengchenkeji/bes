package com.zc.efounder.JEnterprise.domain.deviceTree;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 点值配置
 *
 * @author gaojikun
 * @date 2022-09-15
 */
@ApiModel(value = "NodeConfigSet",description = "点值配置")
public class NodeConfigSet extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @ApiModelProperty(value = "点值配置ID")
    private int id;

    /**
     * 系统名称
     */
    @Excel(name = "系统名称")
    @ApiModelProperty(value = "系统名称")
    private String sysName;

    /**
     * 初始值
     */
    @Excel(name = "初始值")
    @ApiModelProperty(value = "初始值")
    private int initVal;

    /**
     * 描述
     */
    @Excel(name = "描述")
    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "list 以 - 符号隔开",required = true)
    private String list;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getSysName() {
        return sysName;
    }

    public void setInitVal(int initVal) {
        this.initVal = initVal;
    }

    public int getInitVal() {
        return initVal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sysName", getSysName())
                .append("initVal", getInitVal())
                .append("description", getDescription())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
