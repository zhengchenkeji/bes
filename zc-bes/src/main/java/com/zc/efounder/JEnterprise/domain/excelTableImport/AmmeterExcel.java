package com.zc.efounder.JEnterprise.domain.excelTableImport;


import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 14:46 2020/9/10
 * @Modified By:
 */
public class AmmeterExcel extends BaseEntity {

    private static final long serialVersionUID = 8355178720427337581L;

    /**
     * 设备树id
     */
    private int deviceTreeId;
    /**
     * 使能状态
     */
    private int active;
    /**
     * 是否静态电表 0：否1：是
     */
    private int isStatic;
    /**
     * 通讯端口
     */
    private int commPort;
    /**
     * 类型;0-电表;1-点位
     */
    private String type;


    @Excel(name = "系统名称")
    private String sysName;
    /**
     * 别名
     */
    private String alias;

    @Excel(name = "安装位置")
    private String location;

    @Excel(name = "物理地址")
    private String physicalAddress;
    /**
     * 描述
     */
    private String description;

    @Excel(name = "电表类型编号")
    private String meterTypeCode;

    @Excel(name = "通信波特率编号")
    private String commRate;

    @Excel(name = "通信协议编号")
    private String protocolTypeId;

    @Excel(name = "采集方案编号")
    private String collectionMethodCode;

    @Excel(name = "园区编号")
    private String park;

    @Excel(name = "变比")
    private String rate;

    @Excel(name = "数据位")
    private String commDataBit;

    @Excel(name = "校验位")
    private String commParityBit;

    @Excel(name = "停止位")
    private String commStopBit;

    @Excel(name = "功能码")
    private String functionCodeId;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeterTypeCode() {
        return meterTypeCode;
    }

    public void setMeterTypeCode(String meterTypeCode) {
        this.meterTypeCode = meterTypeCode;
    }

    public String getCommRate() {
        return commRate;
    }

    public void setCommRate(String commRate) {
        this.commRate = commRate;
    }

    public String getProtocolTypeId() {
        return protocolTypeId;
    }

    public void setProtocolTypeId(String protocolTypeId) {
        this.protocolTypeId = protocolTypeId;
    }

    public String getCollectionMethodCode() {
        return collectionMethodCode;
    }

    public void setCollectionMethodCode(String collectionMethodCode) {
        this.collectionMethodCode = collectionMethodCode;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCommDataBit() {
        return commDataBit;
    }

    public void setCommDataBit(String commDataBit) {
        this.commDataBit = commDataBit;
    }

    public String getCommParityBit() {
        return commParityBit;
    }

    public void setCommParityBit(String commParityBit) {
        this.commParityBit = commParityBit;
    }

    public String getCommStopBit() {
        return commStopBit;
    }

    public void setCommStopBit(String commStopBit) {
        this.commStopBit = commStopBit;
    }

    public String getFunctionCodeId() {
        return functionCodeId;
    }

    public void setFunctionCodeId(String functionCodeId) {
        this.functionCodeId = functionCodeId;
    }

    public int getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(int deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getIsStatic() {
        return isStatic;
    }

    public void setIsStatic(int isStatic) {
        this.isStatic = isStatic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCommPort() {
        return commPort;
    }

    public void setCommPort(int commPort) {
        this.commPort = commPort;
    }
}
