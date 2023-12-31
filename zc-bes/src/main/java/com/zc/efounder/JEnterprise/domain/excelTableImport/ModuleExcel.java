package com.zc.efounder.JEnterprise.domain.excelTableImport;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:34 2020/9/12
 * @Modified By:
 */
public class ModuleExcel extends BaseEntity {

    private static final long serialVersionUID = -786865760499035795L;

    /**
     * 主键
     */
    private int id;

    /**
     * 所属设备树id
     */
    private int deviceTreeId;

    /**
     * 所属控制器id
     */
    private int controllerId;

    /**
     * 模块类型0：ddc 模块、1：照明模块、2：干线耦合器、3：支线耦合器
     */
    private int type;

    /**
     * 同步状态 0：未同步、1：已同步
     */
    private int synchState;

    /**
     * 在线状态 0：不在线、1：在线
     */
    private int onlineState;

    /**
     * 描述
     */
    private String description;

    /**
     * 所在干线号
     */
    private String trunkCode;

    /**
     * 所在支线号
     */
    private String branchCode;


    @Excel(name = "模块名称")
    private String sysName;
    /**
     * 别名
     */
    private String alias;

    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 所在FLN总线 ddc、FLN1、FLN2、FLN3、FLN4 (1,2,3,4)
     */
    private int flnId;
    /**
     * 模块点集合
     */
    private String pointSet;

    private String modelTypeCode;//type_code


    @Excel(name = "模块型号")
    private String moduleCode;

    @Excel(name = "安装位置")
    private String installAddress;

    @Excel(name = "通信地址")
    private String slaveAddress;

    @Excel(name = "使能状态")
    private String active;

    @Excel(name = "线路地址")
    private String portNum;

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

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlaveAddress() {
        return slaveAddress;
    }

    public void setSlaveAddress(String slaveAddress) {
        this.slaveAddress = slaveAddress;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(int deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public int getControllerId() {
        return controllerId;
    }

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getInstallAddress() {
        return installAddress;
    }

    public void setInstallAddress(String installAddress) {
        this.installAddress = installAddress;
    }

    public String getPortNum() {
        return portNum;
    }

    public void setPortNum(String portNum) {
        this.portNum = portNum;
    }

    public int getFlnId() {
        return flnId;
    }

    public void setFlnId(int flnId) {
        this.flnId = flnId;
    }

    public String getPointSet() {
        return pointSet;
    }

    public void setPointSet(String pointSet) {
        this.pointSet = pointSet;
    }

    public String getTrunkCode() {
        return trunkCode;
    }

    public void setTrunkCode(String trunkCode) {
        this.trunkCode = trunkCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public int getSynchState() {
        return synchState;
    }

    public void setSynchState(int synchState) {
        this.synchState = synchState;
    }

    public int getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(int onlineState) {
        this.onlineState = onlineState;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModelTypeCode() {
        return modelTypeCode;
    }

    public void setModelTypeCode(String modelTypeCode) {
        this.modelTypeCode = modelTypeCode;
    }
}
