package com.zc.efounder.JEnterprise.domain.deviceTree;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 总线对象 bus
 *
 * @author ruoyi
 * @date 2022-09-15
 */
@ApiModel(value = "Bus",description = "总线")
public class Bus extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 设备树id */
    @Excel(name = "设备树id")
    @ApiModelProperty(value = "设备树id",required = true)
    private Long deviceTreeId;

    /** 别名 */
    @Excel(name = "别名")
    @ApiModelProperty(value = "别名",required = true)
    private String alias;

    /** 端口号 */
    @Excel(name = "端口号")
    @ApiModelProperty(value = "端口号",required = true)
    private String port;

    /** 父设备id */
    @Excel(name = "父设备id")
    @ApiModelProperty(value = "父设备id",required = true)
    private String deviceTreeFatherId;

    /** 所属节点类 */
    @Excel(name = "所属节点类")
    @ApiModelProperty(value = "所属节点类",required = true)
    private String deviceNodeId;

    /** 设备类型 1:楼控 2:照明  3:采集器 */
    @Excel(name = "设备类型")
    @ApiModelProperty(value = "设备类型 1:楼控 2:照明  3:采集器",required = true)
    private String deviceType;

    /** 系统名称 */
    @Excel(name = "系统名称")
    @ApiModelProperty(value = "系统名称")
    private String sysName;

    /** 节点功能名称（以，隔开） */
    @ApiModelProperty(value = "节点功能名称（以，隔开）")
    private String deviceNodeFunName;

    /** 新增节点类型（以，隔开） */
    @ApiModelProperty(value = "新增节点类型（以，隔开）")
    private String deviceNodeFunType;

    /** 所属园区 */
    @ApiModelProperty(value = "所属园区")
    private String park;



    public void setDeviceTreeId(Long deviceTreeId)
    {
        this.deviceTreeId = deviceTreeId;
    }

    public Long getDeviceTreeId()
    {
        return deviceTreeId;
    }
    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public String getAlias()
    {
        return alias;
    }
    public void setPort(String port)
    {
        this.port = port;
    }

    public String getPort()
    {
        return port;
    }

    public String getDeviceTreeFatherId() {
        return deviceTreeFatherId;
    }

    public void setDeviceTreeFatherId(String deviceTreeFatherId) {
        this.deviceTreeFatherId = deviceTreeFatherId;
    }

    public String getDeviceNodeId() {
        return deviceNodeId;
    }

    public void setDeviceNodeId(String deviceNodeId) {
        this.deviceNodeId = deviceNodeId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getDeviceNodeFunName() {
        return deviceNodeFunName;
    }

    public void setDeviceNodeFunName(String deviceNodeFunName) {
        this.deviceNodeFunName = deviceNodeFunName;
    }

    public String getDeviceNodeFunType() {
        return deviceNodeFunType;
    }

    public void setDeviceNodeFunType(String deviceNodeFunType) {
        this.deviceNodeFunType = deviceNodeFunType;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("deviceTreeId", getDeviceTreeId())
            .append("alias", getAlias())
            .append("port", getPort())
            .append("deviceTreeFatherId", getDeviceTreeFatherId())
            .append("deviceNodeId", getDeviceNodeId())
            .append("deviceType", getDeviceType())
            .append("sysName", getSysName())
            .append("deviceNodeFunName", getDeviceNodeFunName())
            .append("deviceNodeFunType", getDeviceNodeFunType())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
