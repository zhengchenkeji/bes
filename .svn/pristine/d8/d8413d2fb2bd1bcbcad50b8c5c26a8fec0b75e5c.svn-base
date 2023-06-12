package com.zc.efounder.JEnterprise.domain.safetyWarning;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 告警策略对象 athena_bes_alarm_tactics
 *
 * @author sunshangeng
 * @date 2022-09-16
 */
@ApiModel(value = "AlarmTactics",description = "告警策略对象")
public class AlarmTactics extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /** 告警名称 */
    @NotBlank(message = "告警策略名称不能为空")
    @ApiModelProperty(value = "告警名称",required = true)
    @Excel(name = "告警名称")
    private String name;

    /** 告警使能  */
    @Excel(name = "告警使能",readConverterExp = "0=不使能,1=使能")
    @NotNull(message = "告警使能不能为空")
    @ApiModelProperty(value = "告警使能",required = true)
    private Integer active;

    /** 告警等级 */
    @Excel(name = "告警等级",readConverterExp = "1=一般,2=较大,3=严重")
    @NotNull(message = "告警等级不能为空")
    @ApiModelProperty(value = "告警等级 1=一般,2=较大,3=严重",required = true)
    private Integer level;

    /** 是否为电表 */
//    @Excel(name = "是否为电表",readConverterExp = "0=否,1=是")
    @ApiModelProperty(value = "是否为电表")
    private Integer isMeter;

    /** 所属采集参数 */
    @ApiModelProperty(value = "所属采集参数")
    private Long electricParamsId;

    /** 所属电表 */
    @ApiModelProperty(value = "所属电表")
//    @Excel(name = "所属电表",readConverterExp = "0=否,1=是")
    private Long meterId;

    /** 所属告警类型 */
    @ApiModelProperty(value = "所属告警类型")
    private String alarmTypeId;

    /** 范围类型 */
    @Excel(name = "范围类型",readConverterExp = "1=确认值,2=阀值,3=上限,4=下限")
    @NotNull(message = "范围类型不能为空")
    @ApiModelProperty(value = "范围类型",required = true)
    private Integer rangeType;

    /** 上限值 */
    @Excel(name = "上限值")
    @ApiModelProperty(value = "上限值")
    private Double over;

    /** 下限制 */
    @Excel(name = "下限制")
    @ApiModelProperty(value = "下限制")
    private Double under;

    /** 准确值 */
    @Excel(name = "准确值")
    @ApiModelProperty(value = "准确值")
    private Double precise;

    /**  是否发送消息通知 0：否、1：是 */
    @Excel(name = " 是否发送消息通知",readConverterExp = "0=否,1=是")
    @NotNull(message = "是否发送消息通知不能为空")
    @ApiModelProperty(value = "是否发送消息通知 0：否、1：是 ",required = true)
    private Integer isSendInform;

    /** 告警播报0：否、1：是 */
    @Excel(name = "告警播报",readConverterExp = "0=否,1=是")
    @ApiModelProperty(value = "告警播报0：否、1：是")
    private Integer alarmSound;

    /** 发送邮件 0：否、1：是 */
    @Excel(name = "发送邮件",readConverterExp = "0=否,1=是")
    @ApiModelProperty(value = "发送邮件 0：否、1：是")
    private Integer sendEmail;

    /** 发送短信 0：否、1：是 */
    @Excel(name = "发送短信",readConverterExp = "0=否,1=是")
    @ApiModelProperty(value = "发送短信 0：否、1：是")
    private Integer textSb;

    /** 发送语音 0：否、1：是 */
//    @Excel(name = "发送语音",readConverterExp = "0=否,1=是")
    @ApiModelProperty(value = "发送语音 0：否、1：是")
    private Integer sendVoice;

    /** 通知模板 */
    @ApiModelProperty(value = "通知模板")
    private String template;

    /** 通知类型 */
    @ApiModelProperty(value = "通知类型")
    @Excel(name = "通知类型",readConverterExp = "1=告警触发,2=告警解除")
    private Integer informType;

    /** 设备id */
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**策略类型*/
    @Excel(name = "策略类型",readConverterExp = "1=电表,2=支路,3=分户,4=分项")
    @NotNull(message = "策略类型不能为空！")
    @ApiModelProperty(value = "策略类型",required = true)
    private Integer deviceType;

    /**设备名称*/
    @Excel(name = "所属设备")
    @NotBlank(message = "所属设备不能为空！")
    @ApiModelProperty(value = "所属设备",required = true)
    private String deviceName;

	 /** 运算公式 */
    @Excel(name = "运算公式")
    @ApiModelProperty(value = "运算公式")
    private String operator;

    /**当前节点的父节点id*/
    @ApiModelProperty(value = "当前节点的父节点id")
    private String fatherId;

    /**所属园区*/
    @ApiModelProperty(value = "所属园区")
    private  String parkcode;
    /**所属能源*/
    @ApiModelProperty(value = "所属能源")
    private  String energycode;
    /**所属建筑*/
    @ApiModelProperty(value = "所属建筑")
    private String buildingid;

    /**存储绑定的模板通知*/
    private List<AlarmNoticeLink> noticeLinkList;

    /**所属数据项;逗号分隔*/
    private String itemDataId;


    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }


    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setActive(Integer active)
    {
        this.active = active;
    }

    public Integer getActive()
    {
        return active;
    }
    public void setLevel(Integer level)
    {
        this.level = level;
    }

    public Integer getLevel()
    {
        return level;
    }
    public void setIsMeter(Integer isMeter)
    {
        this.isMeter = isMeter;
    }

    public Integer getIsMeter()
    {
        return isMeter;
    }
    public void setElectricParamsId(Long electricParamsId)
    {
        this.electricParamsId = electricParamsId;
    }

    public Long getElectricParamsId()
    {
        return electricParamsId;
    }
    public void setMeterId(Long meterId)
    {
        this.meterId = meterId;
    }

    public Long getMeterId()
    {
        return meterId;
    }
    public void setAlarmTypeId(String alarmTypeId)
    {
        this.alarmTypeId = alarmTypeId;
    }

    public String getAlarmTypeId()
    {
        return alarmTypeId;
    }
    public void setRangeType(Integer rangeType)
    {
        this.rangeType = rangeType;
    }

    public Integer getRangeType()
    {
        return rangeType;
    }
    public void setOver(Double over)
    {
        this.over = over;
    }
    public Double getOver()
    {
        return over;
    }
    public void setUnder(Double under)
    {
        this.under = under;
    }
    public Double getUnder()
    {
        return under;
    }
    public void setPrecise(Double precise)
    {
        this.precise = precise;
    }
    public Double getPrecise()
    {
        return precise;
    }
    public void setIsSendInform(Integer isSendInform)
    {
        this.isSendInform = isSendInform;
    }

    public Integer getIsSendInform()
    {
        return isSendInform;
    }
    public void setAlarmSound(Integer alarmSound)
    {
        this.alarmSound = alarmSound;
    }

    public Integer getAlarmSound()
    {
        return alarmSound;
    }
    public void setSendEmail(Integer sendEmail)
    {
        this.sendEmail = sendEmail;
    }

    public Integer getSendEmail()
    {
        return sendEmail;
    }
    public void setTextSb(Integer textSb)
    {
        this.textSb = textSb;
    }

    public Integer getTextSb()
    {
        return textSb;
    }
    public void setSendVoice(Integer sendVoice)
    {
        this.sendVoice = sendVoice;
    }

    public Integer getSendVoice()
    {
        return sendVoice;
    }
    public void setTemplate(String template)
    {
        this.template = template;
    }

    public String getTemplate()
    {
        return template;
    }
    public void setInformType(Integer informType)
    {
        this.informType = informType;
    }

    public Integer getInformType()
    {
        return informType;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getParkcode() {
        return parkcode;
    }

    public void setParkcode(String parkcode) {
        this.parkcode = parkcode;
    }

    public String getEnergycode() {
        return energycode;
    }

    public void setEnergycode(String energycode) {
        this.energycode = energycode;
    }

    public String getBuildingid() {
        return buildingid;
    }

    public void setBuildingid(String buildingid) {
        this.buildingid = buildingid;
    }

    public List<AlarmNoticeLink> getNoticeLinkList() {
        return noticeLinkList;
    }

    public void setNoticeLinkList(List<AlarmNoticeLink> noticeLinkList) {
        this.noticeLinkList = noticeLinkList;
    }

    public String getItemDataId() {
        return itemDataId;
    }

    public void setItemDataId(String itemDataId) {
        this.itemDataId = itemDataId;
    }

    @Override
    public String toString() {
        return "AlarmTactics{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", level=" + level +
                ", isMeter=" + isMeter +
                ", electricParamsId=" + electricParamsId +
                ", meterId=" + meterId +
                ", alarmTypeId='" + alarmTypeId + '\'' +
                ", rangeType=" + rangeType +
                ", over=" + over +
                ", under=" + under +
                ", precise=" + precise +
                ", isSendInform=" + isSendInform +
                ", alarmSound=" + alarmSound +
                ", sendEmail=" + sendEmail +
                ", textSb=" + textSb +
                ", sendVoice=" + sendVoice +
                ", template='" + template + '\'' +
                ", informType=" + informType +
                ", deviceId='" + deviceId + '\'' +
                ", deviceType=" + deviceType +
                ", deviceName='" + deviceName + '\'' +
                ", operator='" + operator + '\'' +
                ", fatherId='" + fatherId + '\'' +
                ", parkcode='" + parkcode + '\'' +
                ", energycode='" + energycode + '\'' +
                ", buildingid='" + buildingid + '\'' +
                '}';
    }
}
