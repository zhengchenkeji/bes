package com.zc.efounder.JEnterprise.domain.deviceTree;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 调试操作日志 PointDebugLog
 *
 * @author gaojikun
 * @date 2022-09-15
 */
public class PointDebugLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private int id;

    /**
     * 点位树ID
     */
    private int deviceTreeId;

    /**
     * 点位系统名称
     */
    private String sysName;

    /**
     * 调试值
     */
    private String operatValue;

    /**
     * 操作人
     */
    private String createName;

    /**
     * 操作时间
     */
    private Date createTime;

    /**
     * 成功状态
     */
    private String debugState;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(int deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getOperatValue() {
        return operatValue;
    }

    public void setOperatValue(String operat_value) {
        this.operatValue = operat_value;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDebugState() {
        return debugState;
    }

    public void setDebugState(String debugState) {
        this.debugState = debugState;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sysName", getSysName())
                .append("deviceTreeId", getDeviceTreeId())
                .append("createName", getCreateName())
                .append("createTime", getCreateTime())
                .append("debugState", getDebugState())
                .append("operatValue", getOperatValue())
                .toString();
    }
}
