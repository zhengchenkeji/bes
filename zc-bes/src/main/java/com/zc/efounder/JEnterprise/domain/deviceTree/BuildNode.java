package com.zc.efounder.JEnterprise.domain.deviceTree;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Author:gaojikun
 * @Date:2022-09-24 9:38
 * @Description:楼控虚点、总线、线路
 */
@ApiModel(value = "BuildNode",description = "点位")
public class BuildNode extends BaseEntity {
    /**id*/
    @ApiModelProperty(value = "点位ID")
    private Integer id;
    /**设备树Id*/
    @ApiModelProperty(value = "设备树Id",required = true)
    private Integer treeId;
    /**设备树点类型*/
    @ApiModelProperty(value = "设备树点类型",required = true)
    private Integer nodeType;
    /**端口号*/
    @ApiModelProperty(value = "端口号",required = true)
    private String portNum;
    /**干线通讯地址*/
    @ApiModelProperty(value = "干线通讯地址")
    private Integer branchNum;
    /**支线通讯地址*/
    @ApiModelProperty(value = "支线通讯地址")
    private Integer trunkNum;
    /**模块通讯地址*/
    @ApiModelProperty(value = "模块通讯地址")
    private Integer  modelNum;
    /**点位通讯地址*/
    @ApiModelProperty(value = "点位通讯地址")
    private Integer  pointNum;
    /**系统名称*/
    @ApiModelProperty(value = "系统名称",required = true)
    private String sysName;
    /**别名*/
    @ApiModelProperty(value = "别名",required = true)
    private String nickName;
    /**父ID*/
    @ApiModelProperty(value = "父ID",required = true)
    private String fatherId;

    /** 节点功能名称（以，隔开） */
    @ApiModelProperty(value = "节点功能名称（以，隔开）")
    private String deviceNodeFunName;

    /** 新增节点类型（以，隔开） */
    @ApiModelProperty(value = " 新增节点类型（以，隔开）")
    private String deviceNodeFunType;

    /****园区编号****/
    @ApiModelProperty(value = "园区编号(新增节点为园区根节点时 必填)")
    private String park;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTreeId() {
        return treeId;
    }

    public void setTreeId(Integer treeId) {
        this.treeId = treeId;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    public String getPortNum() {
        return portNum;
    }

    public void setPortNum(String portNum) {
        this.portNum = portNum;
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



    public Integer getBranchNum() {
        return branchNum;
    }

    public void setBranchNum(Integer branchNum) {
        this.branchNum = branchNum;
    }

    public Integer getTrunkNum() {
        return trunkNum;
    }

    public void setTrunkNum(Integer trunkNum) {
        this.trunkNum = trunkNum;
    }

    public Integer getModelNum() {
        return modelNum;
    }

    public void setModelNum(Integer modelNum) {
        this.modelNum = modelNum;
    }

    public Integer getPointNum() {
        return pointNum;
    }

    public void setPointNum(Integer pointNum) {
        this.pointNum = pointNum;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("treeId", getTreeId())
                .append("nodeType", getNodeType())
                .append("portNum", getPortNum())
                .append("nickName", getNickName())
                .append("sysName", getSysName())
                .append("deviceNodeFunType", getDeviceNodeFunType())
                .append("deviceNodeFunName", getDeviceNodeFunName())
                .toString();
    }
}
