package com.zc.efounder.JEnterprise.domain.excelTableImport;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 10:06 2020/9/14
 * @Modified By:
 */
public class PointExcel extends BaseEntity {

    private static final long serialVersionUID = -8026344694648798604L;

    private Integer guid;

    private Integer treeId;

    private Integer equipmentId;

    private Integer moduleId;

    private Integer staticsTime;

    private Integer syncState;

    private Integer controllerId;

    private String energyCode;

    private String vpointType;

    @Excel(name = "点位类型")
    private String nodeType;

    //点位类型id
    private String nodeTypeId;

    @Excel(name = "系统名称")
    private String sysName;
    /**
     * 别名
     */
    private String nickName;

    @Excel(name = "使能状态")
    private String enabled;

    /**
     * 描述
     */
    private String description;

    /**
     * 园区编号
     */
    private String parkCode;

    @Excel(name = "通道索引")
    private String channelIndex;

    @Excel(name = "工作模式")
    private String workMode;

    @Excel(name = "正反向")
    private String reversed;

    @Excel(name = "初始值")
    private String initVal;

    @Excel(name = "有效输入类型")
    private String validInputType;

    @Excel(name = "工程单位")
    private String engineerUnit;

    @Excel(name = "最高阀值")
    private String maxVal;

    @Excel(name = "最低阀值")
    private String minVal;

    @Excel(name = "精度")
    private String accuracy;

    @Excel(name = "有源无源")
    private String sourced;

    @Excel(name = "报警是否启用")
    private String alarmEnable;

    @Excel(name = "报警类型")
    private String alarmType;

    @Excel(name = "报警优先级")
    private String alarmPriority;

    @Excel(name = "高限报警值")
    private String highLimit;

    @Excel(name = "底限报警值")
    private String lowLimit;

    @Excel(name = "报警触发")
    private String closeState;

    @Excel(name = "故障状态")
    private String faultState;

    @Excel(name = "能耗是否统计")
    private String energyStatics;


    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChannelIndex() {
        return channelIndex;
    }

    public void setChannelIndex(String channelIndex) {
        this.channelIndex = channelIndex;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public String getReversed() {
        return reversed;
    }

    public void setReversed(String reversed) {
        this.reversed = reversed;
    }

    public String getInitVal() {
        return initVal;
    }

    public void setInitVal(String initVal) {
        this.initVal = initVal;
    }

    public String getValidInputType() {
        return validInputType;
    }

    public void setValidInputType(String validInputType) {
        this.validInputType = validInputType;
    }

    public String getEngineerUnit() {
        return engineerUnit;
    }

    public void setEngineerUnit(String engineerUnit) {
        this.engineerUnit = engineerUnit;
    }

    public String getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(String maxVal) {
        this.maxVal = maxVal;
    }

    public String getMinVal() {
        return minVal;
    }

    public void setMinVal(String minVal) {
        this.minVal = minVal;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getSourced() {
        return sourced;
    }

    public void setSourced(String sourced) {
        this.sourced = sourced;
    }

    public String getAlarmEnable() {
        return alarmEnable;
    }

    public void setAlarmEnable(String alarmEnable) {
        this.alarmEnable = alarmEnable;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmPriority() {
        return alarmPriority;
    }

    public void setAlarmPriority(String alarmPriority) {
        this.alarmPriority = alarmPriority;
    }

    public String getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(String highLimit) {
        this.highLimit = highLimit;
    }

    public String getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(String lowLimit) {
        this.lowLimit = lowLimit;
    }

    public String getCloseState() {
        return closeState;
    }

    public void setCloseState(String closeState) {
        this.closeState = closeState;
    }

    public String getFaultState() {
        return faultState;
    }

    public void setFaultState(String faultState) {
        this.faultState = faultState;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getEnergyStatics() {
        return energyStatics;
    }

    public void setEnergyStatics(String energyStatics) {
        this.energyStatics = energyStatics;
    }

    public Integer getGuid() {
        return guid;
    }

    public void setGuid(Integer guid) {
        this.guid = guid;
    }

    public Integer getTreeId() {
        return treeId;
    }

    public void setTreeId(Integer treeId) {
        this.treeId = treeId;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getStaticsTime() {
        return staticsTime;
    }

    public void setStaticsTime(Integer staticsTime) {
        this.staticsTime = staticsTime;
    }

    public Integer getSyncState() {
        return syncState;
    }

    public void setSyncState(Integer syncState) {
        this.syncState = syncState;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }

    public String getNodeTypeId() {
        return nodeTypeId;
    }

    public void setNodeTypeId(String nodeTypeId) {
        this.nodeTypeId = nodeTypeId;
    }

    public String getEnergyCode() {
        return energyCode;
    }

    public void setEnergyCode(String energyCode) {
        this.energyCode = energyCode;
    }

    public String getVpointType() {
        return vpointType;
    }

    public void setVpointType(String vpointType) {
        this.vpointType = vpointType;
    }
}
