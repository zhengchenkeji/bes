package com.zc.efounder.JEnterprise.domain.excelTableImport;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 18:23 2020/9/11
 * @Modified By:
 */
public class ControllerExcel extends BaseEntity {

    private static final long serialVersionUID = -1735819340848095677L;

    /**
     * 主键
     */
    private int id;
    /**
     * 设备树id
     */
    private int deviceTreeId;

    /**
     * 节点类型(1：DDC采集器、2：照明控制器、3：能耗采集器)
     */
    private int type;

    /**
     * 同步状态(0：未同步、 1：已同步)
     */
    private int synchState;

    /**
     * 异常状态(0：正常、1：异常)
     */
    private int errorState;

    /**
     * 在线状态(0：不在线、 1：在线)
     */
    private int onlineState;

    private String currentIp;


    @Excel(name = "系统名称")
    private String sysName;

    /**
     * 别名
     */
    private String alias;

    @Excel(name = "所属区域")
    private String zone;
    @Excel(name = "安装位置")
    private String location;

    /**
     * 描述
     */
    private String description;

    @Excel(name = "IP地址")
    private String ip;

    /** 所属节点类ID */
    private String deviceNodeId;

    @Excel(name = "默认网关")
    private String gateWay;

    @Excel(name = "子网掩码")
    private String mask;

    @Excel(name = "主机ip")
    private String serverIp;

    @Excel(name = "主机端口")
    private String serverPort;

    @Excel(name = "使能状态")
    private String active;

    @Excel(name = "采集周期")
    private String collectPeriod;

    /**
     * 上传周期：分钟（只有能耗采集器有）
     */
    @Excel(name = "上传周期")
    private String uploadPeriod;

    /**
     * 保存周期：小时（只有能耗采集器有）
     */
    @Excel(name = "保存周期")
    private String savePeriod;

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

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDeviceNodeId() {
        return deviceNodeId;
    }

    public void setDeviceNodeId(String deviceNodeId) {
        this.deviceNodeId = deviceNodeId;
    }


    public String getGateWay() {
        return gateWay;
    }

    public void setGateWay(String gateWay) {
        this.gateWay = gateWay;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCollectPeriod() {
        return collectPeriod;
    }

    public void setCollectPeriod(String collectPeriod) {
        this.collectPeriod = collectPeriod;
    }

    public String getUploadPeriod() {
        return uploadPeriod;
    }

    public void setUploadPeriod(String uploadPeriod) {
        this.uploadPeriod = uploadPeriod;
    }

    public String getSavePeriod() {
        return savePeriod;
    }

    public void setSavePeriod(String savePeriod) {
        this.savePeriod = savePeriod;
    }

    public int getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(int deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSynchState() {
        return synchState;
    }

    public void setSynchState(int synchState) {
        this.synchState = synchState;
    }

    public int getErrorState() {
        return errorState;
    }

    public void setErrorState(int errorState) {
        this.errorState = errorState;
    }

    public int getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(int onlineState) {
        this.onlineState = onlineState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrentIp() {
        return currentIp;
    }

    public void setCurrentIp(String currentIp) {
        this.currentIp = currentIp;
    }
}
