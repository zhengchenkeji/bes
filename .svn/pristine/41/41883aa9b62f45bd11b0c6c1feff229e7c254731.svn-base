package com.zc.efounder.JEnterprise.domain.deviceTree;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 电表信息对象 athena_bes_electric_meter
 *
 * @author 孙山耕
 * @date 2022-09-14
 */
@ApiModel(value = "AthenaElectricMeter",description = "电表信息对象")
public class AthenaElectricMeter extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 电表ID */
    @ApiModelProperty(value = "电表ID",required = true)
    private Long meterId;

    /** 所属设备树 */
    @Excel(name = "所属设备树")
    @ApiModelProperty(value = "所属设备树",required = true)
    private Long deviceTreeId;

    /** 电表别名 */
    @Excel(name = "电表别名")
    @ApiModelProperty(value = "电表别名",required = true)
    private String alias;

    /** 使能状态0：不使能
            1：使能 */
    @Excel(name = "使能状态")
    @ApiModelProperty(value = "使能状态 0：不使能 1：使能 ",required = true)
    private Long active;

    /** 安装位置 */
    @Excel(name = "安装位置")
    @ApiModelProperty(value = "安装位置",required = true)
    private String location;

    /** 物理地址 */
    @Excel(name = "物理地址")
    @ApiModelProperty(value = "物理地址",required = true)
    private String physicalAddress;

    /** 所属电表类 */
    @Excel(name = "所属电表类")
    @ApiModelProperty(value = "所属电表类",required = true)
    private String meterTypeCode;

    /** 所属通讯协议 */
    @Excel(name = "所属通讯协议")
    @ApiModelProperty(value = "所属通讯协议",required = true)
    private Long protocolTypeId;

    /** 所属采集方案 */
    @Excel(name = "所属采集方案")
    @ApiModelProperty(value = "所属采集方案",required = true)
    private Long collectionMethodCode;

    /** 所属功能码 */
    @Excel(name = "所属功能码")
    @ApiModelProperty(value = "所属功能码",required = true)
    private Long functionCodeId;

    /** 通信波特率 0：1200 1：2400 2：4800 3：9600 4：19200 5：38400 6：57600 7：115200 */
    @Excel(name = "通信波特率")
    @ApiModelProperty(value = "通信波特率 0：1200 1：2400 2：4800 3：9600 4：19200 5：38400 6：57600 7：115200",required = true)
    private Long commRate;

    /** 通讯数据位 0：Data5 1：Data6  2：Data7  */
    @Excel(name = "通讯数据位 ")
    @ApiModelProperty(value = "通讯数据位 0：Data5 1：Data6  2：Data7",required = true)
    private Long commDataBit;

    /** 通讯校验位 0：无校验1：偶校验2：奇校验 3：空格校验4：mark校验 */
    @Excel(name = "通讯校验位")
    @ApiModelProperty(value = "通讯校验位 0：无校验1：偶校验2：奇校验 3：空格校验4：mark校验",required = true)
    private Long commParityBit;

    /** 通讯停止位 0：1 bit  1：1.5 bit  2：2 bit */
    @Excel(name = "通讯停止位")
    @ApiModelProperty(value = "通讯停止位 0：1 bit  1：1.5 bit  2：2 bit",required = true)
    private Long commStopBit;

    /** 是否静态电表 0：否1：是 */
    @Excel(name = "是否静态电表")
    @ApiModelProperty(value = "是否静态电表 0：否1：是",required = true)
    private Long isStatic;

    /** 通讯端口 */
    @Excel(name = "通讯端口")
    @ApiModelProperty(value = "通讯端口",required = true)
    private Long commPort;

    /** 比率 */
    @Excel(name = "比率")
    @ApiModelProperty(value = "比率",required = true)
    private Long rate;

    /** 同步状态 0：未同步1：已同步 */
    @Excel(name = "同步状态")
    @ApiModelProperty(value = " 同步状态 0：未同步1：已同步",required = true)
    private Long synchState;

    /** 异常状态 0：正常 1：异常 */
    @Excel(name = "异常状态")
    @ApiModelProperty(value = "异常状态 0：正常 1：异常",required = true)
    private Long errorState;

    /** 在线状态 0：不在线1：在线 */
    @Excel(name = "在线状态")
    @ApiModelProperty(value = "在线状态 0：不在线1：在线",required = true)
    private Long onlineState;

    /** 描述 */
    @Excel(name = "描述")
    @ApiModelProperty(value = "描述")
    private String description;

    /** 所属节点类 */
    @Excel(name = "所属节点类")
    @ApiModelProperty(value = "所属节点类",required = true)
    private String deviceNodeId;

    /** 系统名称 */
    @Excel(name = "系统名称")
    @ApiModelProperty(value = "系统名称",required = true)
    private String sysName;

    /** 设备类型 1:楼控 2:照明  3:采集器 */
    @Excel(name = "设备类型")
    @ApiModelProperty(value = "设备类型 1:楼控 2:照明  3:采集器",required = true)
    private String deviceType;

    /** 父设备id */
    @Excel(name = "父设备id")
    @ApiModelProperty(value = "父设备id",required = true)
    private String deviceTreeFatherId;

    /** 节点功能名称（以，隔开） */
    @ApiModelProperty(value = "节点功能名称（以，隔开）")
    private String deviceNodeFunName;

    /** 新增节点类型（以，隔开） */
    @ApiModelProperty(value = "新增节点类型（以，隔开）")
    private String deviceNodeFunType;

    /** 所属园区 */
    @ApiModelProperty(value = "所属园区")
    private String park;

    /** 点位类型 1为点位，0为电表 */
    @ApiModelProperty(value = "点位类型 1为点位，0为电表")
    private String type;


    public void setMeterId(Long meterId)
    {
        this.meterId = meterId;
    }

    public Long getMeterId()
    {
        return meterId;
    }
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
    public void setActive(Long active)
    {
        this.active = active;
    }

    public Long getActive()
    {
        return active;
    }
    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLocation()
    {
        return location;
    }
    public void setPhysicalAddress(String physicalAddress)
    {
        this.physicalAddress = physicalAddress;
    }

    public String getPhysicalAddress()
    {
        return physicalAddress;
    }
    public void setMeterTypeCode(String meterTypeCode)
    {
        this.meterTypeCode = meterTypeCode;
    }

    public String getMeterTypeCode()
    {
        return meterTypeCode;
    }
    public void setProtocolTypeId(Long protocolTypeId)
    {
        this.protocolTypeId = protocolTypeId;
    }

    public Long getProtocolTypeId()
    {
        return protocolTypeId;
    }
    public void setCollectionMethodCode(Long collectionMethodCode)
    {
        this.collectionMethodCode = collectionMethodCode;
    }

    public Long getCollectionMethodCode()
    {
        return collectionMethodCode;
    }
    public void setFunctionCodeId(Long functionCodeId)
    {
        this.functionCodeId = functionCodeId;
    }

    public Long getFunctionCodeId()
    {
        return functionCodeId;
    }
    public void setCommRate(Long commRate)
    {
        this.commRate = commRate;
    }

    public Long getCommRate()
    {
        return commRate;
    }
    public void setCommDataBit(Long commDataBit)
    {
        this.commDataBit = commDataBit;
    }

    public Long getCommDataBit()
    {
        return commDataBit;
    }
    public void setCommParityBit(Long commParityBit)
    {
        this.commParityBit = commParityBit;
    }

    public Long getCommParityBit()
    {
        return commParityBit;
    }
    public void setCommStopBit(Long commStopBit)
    {
        this.commStopBit = commStopBit;
    }

    public Long getCommStopBit()
    {
        return commStopBit;
    }
    public void setIsStatic(Long isStatic)
    {
        this.isStatic = isStatic;
    }

    public Long getIsStatic()
    {
        return isStatic;
    }
    public void setCommPort(Long commPort)
    {
        this.commPort = commPort;
    }

    public Long getCommPort()
    {
        return commPort;
    }
    public void setRate(Long rate)
    {
        this.rate = rate;
    }

    public Long getRate()
    {
        return rate;
    }
    public void setSynchState(Long synchState)
    {
        this.synchState = synchState;
    }

    public Long getSynchState()
    {
        return synchState;
    }
    public void setErrorState(Long errorState)
    {
        this.errorState = errorState;
    }

    public Long getErrorState()
    {
        return errorState;
    }
    public void setOnlineState(Long onlineState)
    {
        this.onlineState = onlineState;
    }

    public Long getOnlineState()
    {
        return onlineState;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("meterId", getMeterId())
            .append("deviceTreeId", getDeviceTreeId())
            .append("alias", getAlias())
            .append("active", getActive())
            .append("location", getLocation())
            .append("physicalAddress", getPhysicalAddress())
            .append("meterTypeCode", getMeterTypeCode())
            .append("protocolTypeId", getProtocolTypeId())
            .append("collectionMethodCode", getCollectionMethodCode())
            .append("functionCodeId", getFunctionCodeId())
            .append("commRate", getCommRate())
            .append("commDataBit", getCommDataBit())
            .append("commParityBit", getCommParityBit())
            .append("commStopBit", getCommStopBit())
            .append("isStatic", getIsStatic())
            .append("commPort", getCommPort())
            .append("rate", getRate())
            .append("synchState", getSynchState())
            .append("errorState", getErrorState())
            .append("onlineState", getOnlineState())
            .append("description", getDescription())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("deviceNodeId", getDeviceNodeId())
            .append("sysName", getSysName())
            .append("deviceType", getDeviceType())
            .append("deviceTreeFatherId", getDeviceTreeFatherId())
            .append("deviceNodeFunName", getDeviceNodeFunName())
            .append("deviceNodeFunType", getDeviceNodeFunType())
            .toString();
    }
}
