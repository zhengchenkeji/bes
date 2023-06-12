package com.zc.efounder.JEnterprise.domain.scheduling;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: wanghongjie
 * @Description:计划编排--场景信息--模块信息
 * @Date: Created in 15:04 2022/11/5
 * @Modified By:
 */
@ApiModel(value = "SceneModelControl",description = "场景信息--模块信息")
public class SceneModelControl extends BaseEntity {

    /**
     *主键id
     */
    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;
    /**
     *场景id
     */
    @ApiModelProperty(value = "场景id",required = true)
    private Long sceneId;
    /**
     *场景id
     */
    @ApiModelProperty(value = "模式id",required = true)
    private Long modelId;
    /**
     *模式名称
     */
    @ApiModelProperty(value = "模式名称",required = true)
    @Excel(name = "模式名称")
    private String name;
    /**
     *同步状态
     */
    @ApiModelProperty(value = "同步状态 0=未同步,1=已同步")
    @Excel(name = "同步状态",readConverterExp = "0=未同步,1=已同步")
    private int synchState;

    @Excel(name = "创建人")
    private int createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private int createTime;

    @Excel(name = "修改人")
    private int updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private int updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSceneId() {
        return sceneId;
    }

    public void setSceneId(Long sceneId) {
        this.sceneId = sceneId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSynchState() {
        return synchState;
    }

    public void setSynchState(int synchState) {
        this.synchState = synchState;
    }
}
