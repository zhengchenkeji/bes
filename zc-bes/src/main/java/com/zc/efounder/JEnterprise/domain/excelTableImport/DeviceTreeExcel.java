package com.zc.efounder.JEnterprise.domain.excelTableImport;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Author: wanghongjie
 * @Description:设备树节点导入实体类
 * @Date: Created in 8:48 2022/11/10
 * @Modified By:
 */
public class DeviceTreeExcel extends BaseEntity {
    private static final long serialVersionUID = 7432224418739800164L;

    /**
     * 设备树id
     */
    private int deviceTreeId;

    /**
     * 所属节点类ID
     */
    @Excel(name = "节点类型")
    private String deviceNodeId;

    /**
     * 系统名称
     */
    @Excel(name = "系统名称")
    private String sysName;

    /**
     * 别名
     * @author
     */
    @Excel(name = "别名")
    private String alias;

    /**
     * 别名
     * @author
     */
    @Excel(name = "描述")
    private String description;

    /**
     * 设备类型 1:楼控 2:照明  3:采集器
     */
    @Excel(name = "所属系统")
    private String deviceType;

    /**
     * 父设备id
     */
    @Excel(name = "父节点名称")
    private String psysName;

    private String deviceTreeFatherId;

    @Excel(name = "园区编号")
    private String park;


    public String getDeviceNodeId() {
        return deviceNodeId;
    }

    public void setDeviceNodeId(String deviceNodeId) {
        this.deviceNodeId = deviceNodeId;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceTreeFatherId() {
        return deviceTreeFatherId;
    }

    public void setDeviceTreeFatherId(String deviceTreeFatherId) {
        this.deviceTreeFatherId = deviceTreeFatherId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    public int getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(int deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public String getPsysName() {
        return psysName;
    }

    public void setPsysName(String psysName) {
        this.psysName = psysName;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("deviceTreeId", getDeviceTreeId())
                .append("deviceNodeId", getDeviceNodeId())
                .append("sysName", getSysName())
                .append("alias", getAlias())
                .append("description", getDescription())
                .append("deviceType", getDeviceType())
                .append("psysName", getPsysName())
                .append("deviceTreeFatherId", getDeviceTreeFatherId())
                .append("park", getPark())
                .toString();
    }
}
