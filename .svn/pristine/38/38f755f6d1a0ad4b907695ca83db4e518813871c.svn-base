package com.zc.efounder.JEnterprise.domain.deviceTree;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 控制器对象 controller
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
@ApiModel(value = "Controller",description = "控制器")
public class Controller extends BaseEntity {
    private static final Long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "控制器ID",required = true)
    private Integer id;

    /**
     * 系统名称
     */
    @ApiModelProperty(value = "系统名称",required = true)
    @Excel(name = "系统名称")
    private String sysName;

    @Excel(name = "父节点树id")
    @ApiModelProperty(value = "父节点树id",required = true)
    private Integer deviceTreeFatherId;

    /** 所属节点类ID */
    @Excel(name = "所属节点类ID")
    @ApiModelProperty(value = "所属节点类ID",required = true)
    private Integer deviceNodeId;

    /** 节点功能名称（以，隔开） */
    @ApiModelProperty(value = "节点功能名称（以，隔开）")
    private String deviceNodeFunName;

    /** 新增节点类型（以，隔开） */
    @ApiModelProperty(value = "新增节点类型（以，隔开）")
    private String deviceNodeFunType;

    /**
     * 所属设备树
     */
    @Excel(name = "所属设备树")
    @ApiModelProperty(value = "所属设备树",required = true)
    private Integer deviceTreeId;

    /**
     * 控制器类型  1：DDC采集器、2：照明控制器 、3：能耗采集器
     */
    @ApiModelProperty(value = "控制器类型  1：DDC采集器、2：照明控制器 、3：能耗采集器",required = true)
    @Excel(name = "控制器类型")
    private Integer type;

    /**
     * 控制器别名
     */
    @ApiModelProperty(value = "控制器别名",required = true)
    @Excel(name = "控制器别名")
    private String alias;

    /**
     * 当前通讯ip
     */
    @Excel(name = "当前通讯ip")
    @ApiModelProperty(value = "当前通讯ip")
    private String currentIp;

    /**
     * ip地址
     */
    @Excel(name = "ip地址")
    @ApiModelProperty(value = "ip地址",required = true)
    private String ip;

    /**
     * 默认网关
     */
    @Excel(name = "默认网关")
    @ApiModelProperty(value = "默认网关",required = true)
    private String gateWay;

    /**
     * 子网掩码
     */
    @Excel(name = "子网掩码")
    @ApiModelProperty(value = "子网掩码",required = true)
    private String mask;

    /**
     * 服务ip地址
     */
    @Excel(name = "服务ip地址")
    @ApiModelProperty(value = "服务ip地址",required = true)
    private String serverIp;

    /**
     * 服务端口
     */
    @Excel(name = "服务端口")
    @ApiModelProperty(value = "服务端口",required = true)
    private Integer serverPort;

    /**
     * 安装位置
     */
    @Excel(name = "安装位置")
    @ApiModelProperty(value = "安装位置",required = true)
    private String location;

    /**
     * 归属区域
     */
    @Excel(name = "归属区域")
    @ApiModelProperty(value = "归属区域",required = true)
    private String zone;

    /**
     * 同步状态 0：未同步、 1：已同步
     */
    @Excel(name = "同步状态 ")
    @ApiModelProperty(value = "同步状态 0：未同步、 1：已同步",required = true)
    private Integer synchState;

    /**
     * 异常状态 0：正常、1：异常
     */
    @Excel(name = "异常状态")
    @ApiModelProperty(value = "异常状态 0：正常、1：异常",required = true)
    private Integer errorState;

    /**
     * 在线状态0：不在线、 1：在线
     */
    @Excel(name = "在线状态")
    @ApiModelProperty(value = "在线状态0：不在线、 1：在线",required = true)
    private Integer onlineState;

    /**
     * 使能状态  0：不使能、1：使能
     */
    @Excel(name = "使能状态")
    @ApiModelProperty(value = "使能状态  0：不使能、1：使能",required = true)
    private Integer active;

    /**
     * 描述
     */
    @Excel(name = "描述")
    @ApiModelProperty(value = "描述",required = true)
    private String description;

    /**
     * 采集周期： 分钟（只有能耗采集器有）
     */
    @Excel(name = "采集周期")
    @ApiModelProperty(value = "采集周期： 分钟（能耗采集器为必填项）")
    private Integer collectPeriod;

    /**
     * 上传周期：分钟（只有能耗采集器有）
     */
    @Excel(name = "上传周期")
    @ApiModelProperty(value = "上传周期：分钟（能耗采集器为必填项）")
    private Integer uploadPeriod;

    /**
     * 保存周期：小时（只有能耗采集器有）
     */
    @Excel(name = "保存周期")
    @ApiModelProperty(value = "保存周期：小时",required = true)
    private Integer savePeriod;

    /**
     * 是否批量同步  true：默认 批量同步  false： 只同步当前
     */
    @ApiModelProperty(value = "是否批量同步  true：默认 批量同步  false： 只同步当前")
    private boolean synchronize=true;

    public boolean getSynchronize() {
        return synchronize;
    }

    public void setSynchronize(boolean synchronize) {
        this.synchronize = synchronize;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setDeviceTreeId(Integer deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public Integer getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setCurrentIp(String currentIp) {
        this.currentIp = currentIp;
    }

    public String getCurrentIp() {
        return currentIp;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    public String getGateWay() {
        return gateWay;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getMask() {
        return mask;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getZone() {
        return zone;
    }

    public void setSynchState(Integer synchState) {
        this.synchState = synchState;
    }

    public Integer getSynchState() {
        return synchState;
    }

    public void setErrorState(Integer errorState) {
        this.errorState = errorState;
    }

    public Integer getErrorState() {
        return errorState;
    }

    public void setOnlineState(Integer onlineState) {
        this.onlineState = onlineState;
    }

    public Integer getOnlineState() {
        return onlineState;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getActive() {
        return active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCollectPeriod(Integer collectPeriod) {
        this.collectPeriod = collectPeriod;
    }

    public Integer getCollectPeriod() {
        return collectPeriod;
    }

    public void setUploadPeriod(Integer uploadPeriod) {
        this.uploadPeriod = uploadPeriod;
    }

    public Integer getUploadPeriod() {
        return uploadPeriod;
    }

    public void setSavePeriod(Integer savePeriod) {
        this.savePeriod = savePeriod;
    }

    public Integer getSavePeriod() {
        return savePeriod;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public Integer getDeviceTreeFatherId() {
        return deviceTreeFatherId;
    }

    public void setDeviceTreeFatherId(Integer deviceTreeFatherId) {
        this.deviceTreeFatherId = deviceTreeFatherId;
    }

    public Integer getDeviceNodeId() {
        return deviceNodeId;
    }

    public void setDeviceNodeId(Integer deviceNodeId) {
        this.deviceNodeId = deviceNodeId;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sysName", getSysName())
                .append("deviceTreeFatherId", getDeviceTreeFatherId())
                .append("deviceNodeId", getDeviceNodeId())
                .append("deviceNodeFunName", getDeviceNodeFunName())
                .append("deviceNodeFunType", getDeviceNodeFunType())
                .append("deviceTreeId", getDeviceTreeId())
                .append("type", getType())
                .append("alias", getAlias())
                .append("currentIp", getCurrentIp())
                .append("ip", getIp())
                .append("gateWay", getGateWay())
                .append("mask", getMask())
                .append("serverIp", getServerIp())
                .append("serverPort", getServerPort())
                .append("location", getLocation())
                .append("zone", getZone())
                .append("synchState", getSynchState())
                .append("errorState", getErrorState())
                .append("onlineState", getOnlineState())
                .append("active", getActive())
                .append("description", getDescription())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("collectPeriod", getCollectPeriod())
                .append("uploadPeriod", getUploadPeriod())
                .append("savePeriod", getSavePeriod())
                .append("synchronize", getSynchronize())
                .toString();
    }
}
