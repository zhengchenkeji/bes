package com.zc.efounder.JEnterprise.domain.deviceTree;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 功能点对象 function_point
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
public class FunctionPoint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 所属设备树 */
    @Excel(name = "所属设备树")
    private Long deviceTreeId;

    /** 点类型0：ai、1：ao、2：do、3：di、4：ai（虚点）、5：ao（虚点）、6：di（虚点）、7：do（虚点） */
    @Excel(name = "点类型")
    private Long type;

    /** 功能点别名 */
    @Excel(name = "功能点别名")
    private String alias;

    /** 使能状态 0：禁止、1：使能 */
    @Excel(name = "使能状态 0：禁止、1：使能")
    private Long active;

    /** 初始值 */
    @Excel(name = "初始值")
    private String initValue;

    /** 所在模块 */
    @Excel(name = "所在模块")
    private Long moduleId;

    /** 点所在通道索引 */
    @Excel(name = "点所在通道索引")
    private Long channelIndex;

    /** 工作模式 0：自动、1：手动 */
    @Excel(name = "工作模式")
    private Long workMode;

    /** 极性，正向或反向0：正向、1：反向 */
    @Excel(name = "极性，正向或反向")
    private Long polarity;

    /** 有效输入类型 LV0010 = 0,  //线性电压0..10V 、  LC0420 = 1,  //线性电流4..20mA 、 LC0020 = 2,  //线性电流0..20mA */
    @Excel(name = "有效输入类型")
    private Long lineType;

    /** 工程单位 */
    @Excel(name = "工程单位")
    private Long unit;

    /** 精度 */
    @Excel(name = "精度")
    private Long precisionCode;

    /** 最高阀值 */
    @Excel(name = "最高阀值")
    private String highRange;

    /** 最低阀值 */
    @Excel(name = "最低阀值")
    private String lowRange;

    /** 有源无源 0：无源、1：有源 */
    @Excel(name = "有源无源 ")
    private Long activePassive;

    /** 能耗采集 */
    @Excel(name = "能耗采集")
    private Long meterId;

    /** 报警类型 0：不报警、1：标准报警、2：增强报警 */
    @Excel(name = "报警类型")
    private Long alarmType;

    /** 报警是否启用0：禁止、1：启用 */
    @Excel(name = "报警是否启用")
    private Long alarmActive;

    /** 报警优先级0：一般、1：较大、2：重大 */
    @Excel(name = "报警优先级")
    private Long alarmPriority;

    /** 高限报警值 */
    @Excel(name = "高限报警值")
    private String alarmHighValue;

    /** 底限报警值 */
    @Excel(name = "底限报警值")
    private String alarmLowValue;

    /** 报警触发0：闭合报警、1：断开报警 */
    @Excel(name = "报警触发")
    private Long alarmTrigger;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setDeviceTreeId(Long deviceTreeId)
    {
        this.deviceTreeId = deviceTreeId;
    }

    public Long getDeviceTreeId()
    {
        return deviceTreeId;
    }
    public void setType(Long type)
    {
        this.type = type;
    }

    public Long getType()
    {
        return type;
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
    public void setInitValue(String initValue)
    {
        this.initValue = initValue;
    }

    public String getInitValue()
    {
        return initValue;
    }
    public void setModuleId(Long moduleId)
    {
        this.moduleId = moduleId;
    }

    public Long getModuleId()
    {
        return moduleId;
    }
    public void setChannelIndex(Long channelIndex)
    {
        this.channelIndex = channelIndex;
    }

    public Long getChannelIndex()
    {
        return channelIndex;
    }
    public void setWorkMode(Long workMode)
    {
        this.workMode = workMode;
    }

    public Long getWorkMode()
    {
        return workMode;
    }
    public void setPolarity(Long polarity)
    {
        this.polarity = polarity;
    }

    public Long getPolarity()
    {
        return polarity;
    }
    public void setLineType(Long lineType)
    {
        this.lineType = lineType;
    }

    public Long getLineType()
    {
        return lineType;
    }
    public void setUnit(Long unit)
    {
        this.unit = unit;
    }

    public Long getUnit()
    {
        return unit;
    }
    public void setPrecisionCode(Long precisionCode)
    {
        this.precisionCode = precisionCode;
    }

    public Long getPrecisionCode()
    {
        return precisionCode;
    }
    public void setHighRange(String highRange)
    {
        this.highRange = highRange;
    }

    public String getHighRange()
    {
        return highRange;
    }
    public void setLowRange(String lowRange)
    {
        this.lowRange = lowRange;
    }

    public String getLowRange()
    {
        return lowRange;
    }
    public void setActivePassive(Long activePassive)
    {
        this.activePassive = activePassive;
    }

    public Long getActivePassive()
    {
        return activePassive;
    }
    public void setMeterId(Long meterId)
    {
        this.meterId = meterId;
    }

    public Long getMeterId()
    {
        return meterId;
    }
    public void setAlarmType(Long alarmType)
    {
        this.alarmType = alarmType;
    }

    public Long getAlarmType()
    {
        return alarmType;
    }
    public void setAlarmActive(Long alarmActive)
    {
        this.alarmActive = alarmActive;
    }

    public Long getAlarmActive()
    {
        return alarmActive;
    }
    public void setAlarmPriority(Long alarmPriority)
    {
        this.alarmPriority = alarmPriority;
    }

    public Long getAlarmPriority()
    {
        return alarmPriority;
    }
    public void setAlarmHighValue(String alarmHighValue)
    {
        this.alarmHighValue = alarmHighValue;
    }

    public String getAlarmHighValue()
    {
        return alarmHighValue;
    }
    public void setAlarmLowValue(String alarmLowValue)
    {
        this.alarmLowValue = alarmLowValue;
    }

    public String getAlarmLowValue()
    {
        return alarmLowValue;
    }
    public void setAlarmTrigger(Long alarmTrigger)
    {
        this.alarmTrigger = alarmTrigger;
    }

    public Long getAlarmTrigger()
    {
        return alarmTrigger;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceTreeId", getDeviceTreeId())
            .append("type", getType())
            .append("alias", getAlias())
            .append("active", getActive())
            .append("initValue", getInitValue())
            .append("moduleId", getModuleId())
            .append("channelIndex", getChannelIndex())
            .append("workMode", getWorkMode())
            .append("polarity", getPolarity())
            .append("lineType", getLineType())
            .append("unit", getUnit())
            .append("precisionCode", getPrecisionCode())
            .append("highRange", getHighRange())
            .append("lowRange", getLowRange())
            .append("activePassive", getActivePassive())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("meterId", getMeterId())
            .append("alarmType", getAlarmType())
            .append("alarmActive", getAlarmActive())
            .append("alarmPriority", getAlarmPriority())
            .append("alarmHighValue", getAlarmHighValue())
            .append("alarmLowValue", getAlarmLowValue())
            .append("alarmTrigger", getAlarmTrigger())
            .toString();
    }
}
