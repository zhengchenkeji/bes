package com.zc.efounder.JEnterprise.domain.sceneLink;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 场景联动-执行日志对象 athena_bes_scene_log
 *
 * @author sunshangeng
 * @date 2023-05-06
 */
public class SceneLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 场景ID */
    @Excel(name = "场景ID")
    private Long sceneId;

    /** 场景名称 */
    @Excel(name = "场景名称")
    private String sceneName;

    /** 触发方式 */
    @Excel(name = "触发方式")
    private String triggerModeCode;

    /** 触发内容*/
    @Excel(name = "触发内容 ")
    private String triggerContent;

    /** 触发器ID */
    @Excel(name = "触发器ID")
    private Long triggerId;

    /** 触发时的执行器ids ，多个按照逗号分割 */
    @Excel(name = "触发时的执行器ids ，多个按照逗号分割")
    private String actuatorIds;

    /** 执行内容 */
    @Excel(name = "执行内容")
    private String actuatorContent;

    /** 执行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "执行时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date executeTime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setSceneId(Long sceneId)
    {
        this.sceneId = sceneId;
    }

    public Long getSceneId()
    {
        return sceneId;
    }
    public void setSceneName(String sceneName)
    {
        this.sceneName = sceneName;
    }

    public String getSceneName()
    {
        return sceneName;
    }
    public void setTriggerModeCode(String triggerModeCode)
    {
        this.triggerModeCode = triggerModeCode;
    }

    public String getTriggerModeCode()
    {
        return triggerModeCode;
    }
    public void setTriggerContent(String triggerContent)
    {
        this.triggerContent = triggerContent;
    }

    public String getTriggerContent()
    {
        return triggerContent;
    }
    public void setTriggerId(Long triggerId)
    {
        this.triggerId = triggerId;
    }

    public Long getTriggerId()
    {
        return triggerId;
    }
    public void setActuatorIds(String actuatorIds)
    {
        this.actuatorIds = actuatorIds;
    }

    public String getActuatorIds()
    {
        return actuatorIds;
    }
    public void setActuatorContent(String actuatorContent)
    {
        this.actuatorContent = actuatorContent;
    }

    public String getActuatorContent()
    {
        return actuatorContent;
    }
    public void setExecuteTime(Date executeTime)
    {
        this.executeTime = executeTime;
    }

    public Date getExecuteTime()
    {
        return executeTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sceneId", getSceneId())
            .append("sceneName", getSceneName())
            .append("triggerModeCode", getTriggerModeCode())
            .append("triggerContent", getTriggerContent())
            .append("triggerId", getTriggerId())
            .append("actuatorIds", getActuatorIds())
            .append("actuatorContent", getActuatorContent())
            .append("executeTime", getExecuteTime())
            .toString();
    }
}
