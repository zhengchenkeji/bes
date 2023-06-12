package com.zc.efounder.JEnterprise.domain.sceneLink;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 场景联动-场景对象 athena_bes_scene
 *
 * @author ruoyi
 * @date 2023-02-28
 */
public class Scene extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 场景名称 */
    @Excel(name = "场景名称")
    private String name;

    /** 触发方式 */
    @Excel(name = "触发方式")
    private String triggerMode;

    /** 状态 */
    @Excel(name = "状态")
    private Long sceneStatus;
    /**执行器列表*/
    private List<SceneActuator> actuatorList;
    /**触发器列表*/
    private List<SceneTrigger>  triggerList;

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
    public void setTriggerMode(String triggerMode)
    {
        this.triggerMode = triggerMode;
    }

    public String getTriggerMode()
    {
        return triggerMode;
    }
    public void setSceneStatus(Long sceneStatus)
    {
        this.sceneStatus = sceneStatus;
    }

    public Long getSceneStatus()
    {
        return sceneStatus;
    }

    public List<SceneActuator> getActuatorList() {
        return actuatorList;
    }

    public void setActuatorList(List<SceneActuator> actuatorList) {
        this.actuatorList = actuatorList;
    }

    public List<SceneTrigger> getTriggerList() {
        return triggerList;
    }

    public void setTriggerList(List<SceneTrigger> triggerList) {
        this.triggerList = triggerList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("triggerMode", getTriggerMode())
            .append("sceneStatus", getSceneStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
