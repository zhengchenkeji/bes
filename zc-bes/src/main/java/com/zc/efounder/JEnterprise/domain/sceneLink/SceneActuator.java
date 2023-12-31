package com.zc.efounder.JEnterprise.domain.sceneLink;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 场景联动-场景执行器对象 athena_bes_scene_actuator
 *
 * @author ruoyi
 * @date 2023-02-28
 */
public class SceneActuator extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    private Long id;

    /** 场景id */
    @Excel(name = "场景id")
    private Long sceneId;

    /** 动作模式 */
    @Excel(name = "动作模式")
    private Long movementMode;

    /** 执行类型 */
    @Excel(name = "执行类型")
    private String executeType;

    /** 通知模板 */
    @Excel(name = "通知模板")
    private String noticeTemplate;

    /** 通知配置 */
    @Excel(name = "通知配置")
    private String noticeConfig;

    /** 点位id */
    @Excel(name = "点位id")
    private String treeId;
    /**设备类型 0bes 1第三方*/
    private String deviceType;

    /** 执行功能、属性 */
    @Excel(name = "执行功能、属性")
    private String executeAttribute;

    /** 执行属性值 */
    @Excel(name = "执行属性值")
    private String executeValue;

    /**用户ids*/

    private String userIds;


    private String content;
    /**执行器用户，设备名称*/

    private  String userOrDevice;


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
    public void setMovementMode(Long movementMode)
    {
        this.movementMode = movementMode;
    }

    public Long getMovementMode()
    {
        return movementMode;
    }
    public void setExecuteType(String executeType)
    {
        this.executeType = executeType;
    }

    public String getExecuteType()
    {
        return executeType;
    }
    public void setNoticeTemplate(String noticeTemplate)
    {
        this.noticeTemplate = noticeTemplate;
    }

    public String getNoticeTemplate()
    {
        return noticeTemplate;
    }
    public void setNoticeConfig(String noticeConfig)
    {
        this.noticeConfig = noticeConfig;
    }

    public String getNoticeConfig()
    {
        return noticeConfig;
    }
    public void setTreeId(String treeId)
    {
        this.treeId = treeId;
    }

    public String getTreeId()
    {
        return treeId;
    }
    public void setExecuteAttribute(String executeAttribute)
    {
        this.executeAttribute = executeAttribute;
    }

    public String getExecuteAttribute()
    {
        return executeAttribute;
    }
    public void setExecuteValue(String executeValue)
    {
        this.executeValue = executeValue;
    }

    public String getExecuteValue()
    {
        return executeValue;
    }


    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public String getUserOrDevice() {
        return userOrDevice;
    }

    public void setUserOrDevice(String userOrDevice) {
        this.userOrDevice = userOrDevice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sceneId", getSceneId())
                .append("movementMode", getMovementMode())
                .append("executeType", getExecuteType())
                .append("noticeTemplate", getNoticeTemplate())
                .append("noticeConfig", getNoticeConfig())
                .append("treeId", getTreeId())
                .append("userOrDevice", getUserOrDevice())
                .append("executeAttribute", getExecuteAttribute())
                .append("executeValue", getExecuteValue())
                .append("content", getContent())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }



}
