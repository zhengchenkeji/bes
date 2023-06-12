package com.zc.efounder.JEnterprise.domain.deviceTree;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 点位对象 point
 *
 * @author ruoyi
 * @date 2022-09-15
 */
@ApiModel(value = "Point",description = "点位")
public class Point extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @ApiModelProperty(value = "点位ID")
    private Long guid;

    /**
     * 设备树id
     */
    @Excel(name = "设备树id")
    @ApiModelProperty(value = "设备树id",required = true)
    private Long treeId;

    /**
     * 设备id，用于下位机通讯
     */
    @Excel(name = "设备id，用于下位机通讯")
    @ApiModelProperty(value = "设备id，用于下位机通讯")
    private Integer equipmentId;

    /**
     * 系统名称
     */
    @Excel(name = "系统名称")
    @ApiModelProperty(value = "系统名称",required = true)
    private String sysName;

    /**
     * 别名
     */
    @Excel(name = "别名")
    @ApiModelProperty(value = "别名",required = true)
    private String nickName;

    /**
     * 点类型
     */
    @Excel(name = "点类型")
    @ApiModelProperty(value = "点类型",required = true)
    private String nodeType;

    /**
     * 虚点类型
     */
    @Excel(name = "虚点类型")
    @ApiModelProperty(value = "虚点类型(为虚点时必填)")
    private String vpointType;

    /**
     * 是否可用;0-否；1-是
     */
    @Excel(name = "是否可用;0-否；1-是")
    @ApiModelProperty(value = "是否可用;0-否；1-是",required = true)
    private Integer enabled;

    /**
     * 初始值
     */
    @Excel(name = "初始值")
    @ApiModelProperty(value = "初始值(虚点，DO AO点时为必填)")
    private String initVal;

    /**
     * 实时值
     */
    @Excel(name = "实时值")
    @ApiModelProperty(value = "实时值")
    private String runVal;

    /**
     * 有效输入类型
     */
    @Excel(name = "有效输入类型")
    @ApiModelProperty(value = "有效输入类型")
    private Long validInputType;

    /**
     * 精度
     */
    @Excel(name = "精度")
    @ApiModelProperty(value = "精度(点位为VAI VAO AO AI 点时必填)")
    private Long accuracy;

    /**
     * 所在模块
     */
    @Excel(name = "所在模块")
    @ApiModelProperty(value = "所在模块")
    private Long moduleId;

    /**
     * 通道索引
     */
    @Excel(name = "通道索引")
    @ApiModelProperty(value = "通道索引")
    private Long channelIndex;

    /**
     * 工程单位
     */
    @Excel(name = "工程单位")
    @ApiModelProperty(value = "工程单位(VAI VAO AI AO时必填)")
    private String engineerUnit;

    /**
     * 信号类型  0-10v 0  ;0-20mA 1;4-20mA 2
     */
    @Excel(name = "信号类型  0-10v 0  ;0-20mA 1;4-20mA 2")
    @ApiModelProperty(value = "信号类型  0-10v 0  ;0-20mA 1;4-20mA 2(AO AI点位必填)")
    private Long sinnalType;

    /**
     * 最小值   传感器量程最小值
     */
    @Excel(name = "最小值   传感器量程最小值")
    @ApiModelProperty(value = "最小值   传感器量程最小值(为AI AO时必填)")
    private String minVal;

    /**
     * 最大值
     */
    @Excel(name = "最大值")
    @ApiModelProperty(value = "最大值 (为AI AO时必填)")
    private String maxVal;

    /**
     * 是否反向   否0，是1
     */
    @Excel(name = "是否反向   否0，是1")
    @ApiModelProperty(value = "是否反向   否0，是1 (实点时为必填)")
    private Long reversed;

    /**
     * 是否有源   否0，是1
     */
    @Excel(name = "是否有源   否0，是1")
    @ApiModelProperty(value = "是否有源   否0，是1(DI点时必填)")
    private Long sourced;

    /**
     * 工作模式   自动0，手动1
     */
    @Excel(name = "工作模式   自动0，手动1")
    @ApiModelProperty(value = "工作模式   自动0，手动1(实点时必填)")
    private Long workMode;

    /**
     * 报警使能  0：不使能 1：使能
     */
    @Excel(name = "报警使能  0：不使能 1：使能")
    @ApiModelProperty(value = "报警使能  0：不使能 1：使能",required = true)
    private Integer alarmEnable;

    /**
     * 报警类型
     */
    @Excel(name = "报警类型")
    @ApiModelProperty(value = "报警类型",required = true)
    private Integer alarmType;

    /**
     * 高限报警
     */
    @Excel(name = "高限报警")
    @ApiModelProperty(value = "设备树id")
    private Long highLimit;

    /**
     * 低限报警
     */
    @Excel(name = "低限报警")
    @ApiModelProperty(value = "低限报警")
    private Long lowLimit;

    /**
     * 闭合状态 闭合正常，0闭合，1不闭合
     */
    @Excel(name = "闭合状态 闭合正常，0闭合，1不闭合")
    @ApiModelProperty(value = "闭合状态 闭合正常，0闭合，1不闭合(DO DI点位必填)")
    private Integer closeState;

    /**
     * 报警优先级 报警优先级，只有对于采用标准报警的点才有此配置项。危及人身安全2;严重 1;危机安全 0
     */
    @Excel(name = "报警优先级 报警优先级，只有对于采用标准报警的点才有此配置项。危及人身安全2;严重 1;危机安全 0")
    @ApiModelProperty(value = "报警优先级 报警优先级，只有对于采用标准报警的点才有此配置项。危及人身安全2;严重 1;危机安全 0",required = true)
    private Integer alarmPriority;

    /**
     * 描述
     */
    @Excel(name = "描述")
    @ApiModelProperty(value = "描述",required = true)
    private String description;

    /**
     * 能耗统计（0：否；1：是）
     */
    @Excel(name = "能耗统计", readConverterExp = "0=：否；1：是")
    @ApiModelProperty(value = "能耗统计（0：否；1：是）(VAI AI点时必填)")
    private Integer energyStatics;

    /**
     * 统计周期（0:15分钟；1:1小时；2:1天）
     */
    @Excel(name = "统计周期", readConverterExp = "0=:15分钟；1:1小时；2:1天")
    @ApiModelProperty(value = "统计周期（0:15分钟；1:1小时；2:1天）")
    private Integer staticsTime;

    /**
     * 能源类型
     */
    @Excel(name = "能源类型")
    @ApiModelProperty(value = "能源类型(VAI AI点时必填)")
    private String energyCode;

    /**
     * 同步状态(0:未同步,1:已同步)
     */
    @Excel(name = "同步状态(0:未同步,1:已同步)")
    @ApiModelProperty(value = "步状态(0:未同步,1:已同步)",required = true)
    private Integer syncState;

    /**
     * 故障状态（0：否；1：是）
     */
    @Excel(name = "故障状态", readConverterExp = "0=：否；1：是")
    @ApiModelProperty(value = "故障状态（0：否；1：是）",required = true)
    private Integer faultState;

    @ApiModelProperty(value = "父id",required = true)
    private String fatherId;

    /**
     * 节点功能名称（以，隔开）
     */
    @ApiModelProperty(value = "节点功能名称（以，隔开）")
    private String deviceNodeFunName;

    /**
     * 新增节点类型（以，隔开）
     */
    @ApiModelProperty(value = "新增节点类型（以，隔开）")
    private String deviceNodeFunType;

    /**
     * 新增节点在线状态
     */
    @ApiModelProperty(value = "新增节点在线状态")
    private int deviceTreeStatus;

    /**
     * 控制器Id
     */
    @ApiModelProperty(value = "控制器Id")
    private int controllerId;

    @ApiModelProperty(value = "能耗上传周期记录")
    private Date recordUploadPeriod; // 能耗上传周期记录

    public String getRunVal() {
        return runVal;
    }

    public void setRunVal(String runVal) {
        this.runVal = runVal;
    }

    public void setGuid(Long guid) {
        this.guid = guid;
    }

    public Long getGuid() {
        return guid;
    }

    public void setTreeId(Long treeId) {
        this.treeId = treeId;
    }

    public Long getTreeId() {
        return treeId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getSysName() {
        return sysName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setVpointType(String vpointType) {
        this.vpointType = vpointType;
    }

    public String getVpointType() {
        return vpointType;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setInitVal(String initVal) {
        this.initVal = initVal;
    }

    public String getInitVal() {
        return initVal;
    }

    public void setValidInputType(Long validInputType) {
        this.validInputType = validInputType;
    }

    public Long getValidInputType() {
        return validInputType;
    }

    public void setAccuracy(Long accuracy) {
        this.accuracy = accuracy;
    }

    public Long getAccuracy() {
        return accuracy;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setChannelIndex(Long channelIndex) {
        this.channelIndex = channelIndex;
    }

    public Long getChannelIndex() {
        return channelIndex;
    }

    public void setEngineerUnit(String engineerUnit) {
        this.engineerUnit = engineerUnit;
    }

    public String getEngineerUnit() {
        return engineerUnit;
    }

    public void setSinnalType(Long sinnalType) {
        this.sinnalType = sinnalType;
    }

    public Long getSinnalType() {
        return sinnalType;
    }

    public void setMinVal(String minVal) {
        this.minVal = minVal;
    }

    public String getMinVal() {
        return minVal;
    }

    public void setMaxVal(String maxVal) {
        this.maxVal = maxVal;
    }

    public String getMaxVal() {
        return maxVal;
    }

    public void setReversed(Long reversed) {
        this.reversed = reversed;
    }

    public Long getReversed() {
        return reversed;
    }

    public void setSourced(Long sourced) {
        this.sourced = sourced;
    }

    public Long getSourced() {
        return sourced;
    }

    public void setWorkMode(Long workMode) {
        this.workMode = workMode;
    }

    public Long getWorkMode() {
        return workMode;
    }

    public void setAlarmEnable(Integer alarmEnable) {
        this.alarmEnable = alarmEnable;
    }

    public Integer getAlarmEnable() {
        return alarmEnable;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setHighLimit(Long highLimit) {
        this.highLimit = highLimit;
    }

    public Long getHighLimit() {
        return highLimit;
    }

    public void setLowLimit(Long lowLimit) {
        this.lowLimit = lowLimit;
    }

    public Long getLowLimit() {
        return lowLimit;
    }

    public void setCloseState(Integer closeState) {
        this.closeState = closeState;
    }

    public Integer getCloseState() {
        return closeState;
    }

    public void setAlarmPriority(Integer alarmPriority) {
        this.alarmPriority = alarmPriority;
    }

    public Integer getAlarmPriority() {
        return alarmPriority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnergyStatics(Integer energyStatics) {
        this.energyStatics = energyStatics;
    }

    public Integer getEnergyStatics() {
        return energyStatics;
    }

    public void setStaticsTime(Integer staticsTime) {
        this.staticsTime = staticsTime;
    }

    public Integer getStaticsTime() {
        return staticsTime;
    }

    public void setEnergyCode(String energyCode) {
        this.energyCode = energyCode;
    }

    public String getEnergyCode() {
        return energyCode;
    }

    public void setSyncState(Integer syncState) {
        this.syncState = syncState;
    }

    public Integer getSyncState() {
        return syncState;
    }

    public void setFaultState(Integer faultState) {
        this.faultState = faultState;
    }

    public Integer getFaultState() {
        return faultState;
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

    public Integer getDeviceTreeStatus() {
        return deviceTreeStatus;
    }

    public void setDeviceTreeStatus(Integer deviceTreeStatus) {
        this.deviceTreeStatus = deviceTreeStatus;
    }

    public int getControllerId() {
        return controllerId;
    }

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public Date getRecordUploadPeriod() {
        return recordUploadPeriod;
    }

    public void setRecordUploadPeriod(Date recordUploadPeriod) {
        this.recordUploadPeriod = recordUploadPeriod;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("guid", getGuid())
                .append("treeId", getTreeId())
                .append("equipmentId", getEquipmentId())
                .append("sysName", getSysName())
                .append("nickName", getNickName())
                .append("nodeType", getNodeType())
                .append("vpointType", getVpointType())
                .append("enabled", getEnabled())
                .append("initVal", getInitVal())
                .append("runVal", getRunVal())
                .append("validInputType", getValidInputType())
                .append("accuracy", getAccuracy())
                .append("moduleId", getModuleId())
                .append("channelIndex", getChannelIndex())
                .append("engineerUnit", getEngineerUnit())
                .append("sinnalType", getSinnalType())
                .append("minVal", getMinVal())
                .append("maxVal", getMaxVal())
                .append("reversed", getReversed())
                .append("sourced", getSourced())
                .append("workMode", getWorkMode())
                .append("alarmEnable", getAlarmEnable())
                .append("alarmType", getAlarmType())
                .append("highLimit", getHighLimit())
                .append("lowLimit", getLowLimit())
                .append("closeState", getCloseState())
                .append("alarmPriority", getAlarmPriority())
                .append("description", getDescription())
                .append("energyStatics", getEnergyStatics())
                .append("staticsTime", getStaticsTime())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("energyCode", getEnergyCode())
                .append("syncState", getSyncState())
                .append("faultState", getFaultState())
                .append("fatherId", getFatherId())
                .append("deviceNodeFunType", getDeviceNodeFunType())
                .append("deviceNodeFunName", getDeviceNodeFunName())
                .append("deviceTreeStatus", getDeviceTreeStatus())
                .append("controllerId", getControllerId())
                .append("recordUploadPeriod", getRecordUploadPeriod())
                .toString();
    }
}
