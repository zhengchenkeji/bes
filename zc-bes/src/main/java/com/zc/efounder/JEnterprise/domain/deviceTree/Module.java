package com.zc.efounder.JEnterprise.domain.deviceTree;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 模块对象 module
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
@ApiModel(value = "Module",description = "模块")
public class Module extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "模块ID")
    private Long id;

    /**
     * 所属设备树
     */
    @Excel(name = "所属设备树")
    @ApiModelProperty(value = "所属设备树",required = true)
    private Long deviceTreeId;

    /**
     * 所属模块型号
     */
    @Excel(name = "所属模块型号")
    @ApiModelProperty(value = "所属模块型号",required = true)
    private Long moduleTypeId;

    /**
     * 所属控制器
     */
    @Excel(name = "所属控制器")
    @ApiModelProperty(value = "所属控制器",required = true)
    private Long controllerId;

    /**
     * 模块类型0：ddc 模块、1：照明模块、2：干线耦合器、3：支线耦合器
     */
    @Excel(name = "模块类型")
    @ApiModelProperty(value = "模块类型0：ddc 模块、1：照明模块、2：干线耦合器、3：支线耦合器",required = true)
    private Long type;

    /**
     * 模块别名
     */
    @Excel(name = "模块别名")
    @ApiModelProperty(value = "模块别名",required = true)
    private String alias;

    /**
     * 所在干线号
     */
    @Excel(name = "所在干线号")
    @ApiModelProperty(value = "所在干线号")
    private Long trunkCode;

    /**
     * 所在支线号
     */
    @Excel(name = "所在支线号")
    @ApiModelProperty(value = "所在支线号")
    private Long branchCode;

    /**
     * 所在FLN总线 ddc、FLN1、FLN2、FLN3、FLN4 (1,2,3,4)
     */
    @Excel(name = "所在FLN总线")
    @ApiModelProperty(value = "所在FLN总线 ddc、FLN1、FLN2、FLN3、FLN4 (1,2,3,4) (修改照明模块时该字段为必填项！！！)")
    private Long flnId;

    /**
     * 通讯地址
     */
    @Excel(name = "通讯地址")
    @ApiModelProperty(value = "通讯地址",required = true)
    private Long slaveAddress;

    /**
     * 使能状态0：不使能、1：使能
     */
    @Excel(name = "使能状态")
    @ApiModelProperty(value = "使能状态0：不使能、1：使能",required = true)
    private Long active;

    /**
     * 同步状态 0：未同步、1：已同步
     */
    @Excel(name = "同步状态")
    @ApiModelProperty(value = "同步状态 0：未同步、1：已同步",required = true)
    private Long synchState;

    /**
     * 在线状态 0：不在线、1：在线
     */
    @Excel(name = "在线状态")
    @ApiModelProperty(value = "在线状态 0：不在线、1：在线",required = true)
    private Long onlineState;

    /**
     * 描述
     */
    @Excel(name = "描述")
    @ApiModelProperty(value = "描述",required = true)
    private String description;

    @ApiModelProperty(value = "安装位置",required = true)
    private String installAddress;

    @ApiModelProperty(value = "模块名称",required = true)
    private String sysName;

    @ApiModelProperty(value = "节点类型",required = true)
    private String nodeType;

    @ApiModelProperty(value = "父ID",required = true)
    private String fatherId;

    /** 节点功能名称（以，隔开） */
    @ApiModelProperty(value = "节点功能名称（以，隔开）")
    private String deviceNodeFunName;

    /** 新增节点类型（以，隔开） */
    @ApiModelProperty(value = "新增节点类型（以，隔开）")
    private String deviceNodeFunType;

    @ApiModelProperty(value = "照明模块通信地址前缀")
    /*照明模块通信地址前缀*/
    private String lightingAddress;

    /**
     * 新增节点在线状态
     */
    @ApiModelProperty(value = "新增节点在线状态")
    private int deviceTreeStatus;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setDeviceTreeId(Long deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public Long getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setModuleTypeId(Long moduleTypeId) {
        this.moduleTypeId = moduleTypeId;
    }

    public Long getModuleTypeId() {
        return moduleTypeId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    public Long getControllerId() {
        return controllerId;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getType() {
        return type;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setTrunkCode(Long trunkCode) {
        this.trunkCode = trunkCode;
    }

    public Long getTrunkCode() {
        return trunkCode;
    }

    public void setBranchCode(Long branchCode) {
        this.branchCode = branchCode;
    }

    public Long getBranchCode() {
        return branchCode;
    }

    public void setFlnId(Long flnId) {
        this.flnId = flnId;
    }

    public Long getFlnId() {
        return flnId;
    }

    public void setSlaveAddress(Long slaveAddress) {
        this.slaveAddress = slaveAddress;
    }

    public Long getSlaveAddress() {
        return slaveAddress;
    }

    public void setActive(Long active) {
        this.active = active;
    }

    public Long getActive() {
        return active;
    }

    public void setSynchState(Long synchState) {
        this.synchState = synchState;
    }

    public Long getSynchState() {
        return synchState;
    }

    public void setOnlineState(Long onlineState) {
        this.onlineState = onlineState;
    }

    public Long getOnlineState() {
        return onlineState;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getInstallAddress() {
        return installAddress;
    }

    public void setInstallAddress(String installAddress) {
        this.installAddress = installAddress;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
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

    public int getDeviceTreeStatus() {
        return deviceTreeStatus;
    }

    public void setDeviceTreeStatus(int deviceTreeStatus) {
        this.deviceTreeStatus = deviceTreeStatus;
    }

    public String getLightingAddress() {
        return lightingAddress;
    }

    public void setLightingAddress(String lightingAddress) {
        this.lightingAddress = lightingAddress;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deviceTreeId", getDeviceTreeId())
                .append("moduleTypeId", getModuleTypeId())
                .append("controllerId", getControllerId())
                .append("type", getType())
                .append("alias", getAlias())
                .append("trunkCode", getTrunkCode())
                .append("branchCode", getBranchCode())
                .append("flnId", getFlnId())
                .append("slaveAddress", getSlaveAddress())
                .append("active", getActive())
                .append("synchState", getSynchState())
                .append("onlineState", getOnlineState())
                .append("description", getDescription())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("installAddress", getInstallAddress())
                .append("sysName", getSysName())
                .append("nodeType", getNodeType())
                .append("fatherId", getFatherId())
                .append("deviceNodeFunName", getDeviceNodeFunName())
                .append("deviceNodeFunType", getDeviceNodeFunType())
                .append("deviceTreeStatus", getDeviceTreeStatus())
                .toString();
    }
}
