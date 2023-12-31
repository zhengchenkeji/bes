package com.zc.efounder.JEnterprise.domain.deviceTree;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 模块错误日志 ModuleErrorLog
 *
 * @author gaojikun
 * @date 2022-09-15
 */
public class ModuleErrorLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private int id;

    /**
     * 模块ID
     */
    private int moduleId;

    /**
     * 树ID
     */
    private int deviceTreeId;

    /**
     * 返回状态
     */
    private String errCode;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public int getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(int deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("moduleId", getModuleId())
                .append("deviceTreeId", getDeviceTreeId())
                .toString();
    }
}
