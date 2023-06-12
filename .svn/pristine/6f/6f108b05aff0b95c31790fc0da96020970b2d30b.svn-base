package com.zc.efounder.JEnterprise.domain.scheduling;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:27 2022/11/5
 * @Modified By:
 */
@ApiModel(value = "SceneModelPointControl",description = "场景模型点控制")
public class SceneModelPointControl extends BaseEntity {
    /**
     *主键id
     */
    @ApiModelProperty(value = "唯一ID")
    private Long id;
    /**
     *模式id
     */
    @ApiModelProperty(value = "模式id",required = true)
    private Long sceneModelId;
    /**
     *点位id
     */
    @ApiModelProperty(value = "点位id")
    private Long pointId;
    /**
     *系统名称
     */
    @ApiModelProperty(value = "系统名称")
    private String sysName;
    /**
     *别名
     */
    @ApiModelProperty(value = "别名")
    private String alias;
    /**
     *点位控制值
     */
    @ApiModelProperty(value = "点位控制值")
    private String pointValue;
    /**
     *点位控制单位
     */
    @ApiModelProperty(value = "点位控制单位")
    private String pointUnit;
    /*所属控制器*/
    @ApiModelProperty(value = "所属控制器")
    private Integer pointControllerId;
    /*修改列表*/
    @ApiModelProperty(value = "修改列表",required = true)
    private String lists;

    /*是否批量配置*/
    @ApiModelProperty(value = "是否批量配置 1是批量配置，2是单独配置",required = true)
    private String isAllUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSceneModelId() {
        return sceneModelId;
    }

    public void setSceneModelId(Long sceneModelId) {
        this.sceneModelId = sceneModelId;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public String getPointValue() {
        return pointValue;
    }

    public void setPointValue(String pointValue) {
        this.pointValue = pointValue;
    }

    public String getPointUnit() {
        return pointUnit;
    }

    public void setPointUnit(String pointUnit) {
        this.pointUnit = pointUnit;
    }

    public Integer getPointControllerId() {
        return pointControllerId;
    }

    public void setPointControllerId(Integer pointControllerId) {
        this.pointControllerId = pointControllerId;
    }

    public String getLists() {
        return lists;
    }

    public void setLists(String lists) {
        this.lists = lists;
    }

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

    public String getIsAllUpdate() {
        return isAllUpdate;
    }

    public void setIsAllUpdate(String isAllUpdate) {
        this.isAllUpdate = isAllUpdate;
    }
}
