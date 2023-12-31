package com.zc.efounder.JEnterprise.domain.deviceTree;

import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.common.utils.ExcelVOAttribute;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:08 2022/9/8
 * @Modified By:
 */
@ApiModel(value = "DeviceTree",description = "树节点定义对象")
public class DeviceTree extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 设备树Id
     */
    @ApiModelProperty(value = "设备树Id",required = true)
    private int deviceTreeId;

    /**
     * 所属节点类ID
     */
    @ExcelVOAttribute(name = "节点类型", column = "A")
    @ApiModelProperty(value = "所属节点类ID",required = true)
    private int deviceNodeId;

    /**
     * 系统名称
     */
    @ExcelVOAttribute(name = "系统名称", column = "B")
    @ApiModelProperty(value = "系统名称")
    private String sysName;

    /**
     * 缓存 系统名称
     */
    @ApiModelProperty(value = "缓存 系统名称")
    private String redisSysName;

    /**
     * 设备类型 1:楼控 2:照明  3:采集器
     */
    @ApiModelProperty(value = "设备类型 1:楼控 2:照明  3:采集器")
    @ExcelVOAttribute(name = "所属系统 ", column = "D")
    private int deviceType;

    /**
     * 父设备id
     */
    @ApiModelProperty(value = "父设备id")
    @ExcelVOAttribute(name = "父节点名称", column = "E")
    private int deviceTreeFatherId;

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

    @ApiModelProperty(value = "url")
    private String url;

    /*********设备树在线离线状态**************/
    @ApiModelProperty(value = "设备树在线离线状态")
    private int deviceTreeStatus;

    /*********qindehua 是否进行二次删除**************/
    @ApiModelProperty(value = "是否进行二次删除",required = true)
    private boolean deleteAll;

    /*********gaojikun 点位单位**************/
    @ApiModelProperty(value = "点位单位")
    private String engineerUnit;

    /*********gaojikun 点位值**************/
    @ApiModelProperty(value = "点位值")
    private String initVal;

    /*********gaojikun 点位实时缓存值**************/
    @ApiModelProperty(value = "点位实时缓存值")
    private String runVal;

    /*********gaojikun 点位所属控制器**************/
    @ApiModelProperty(value = "点位所属控制器")
    private Integer controllerId;

    /*********sunshangeng 是否是能耗节点 默认false**************/
    @ApiModelProperty(name = "energyNode",value = "是否是能耗节点 默认false")
    private Boolean energyNode=Boolean.FALSE  ;



    /**
     * 别名
     * @author liuwenge
     */
    @ExcelVOAttribute(name = "别名", column = "C")
    @ApiModelProperty(value = "别名")
    private String alias;

    /**
     *
     * @Description: 是否含有子节点
     *
     * @auther: wanghongjie
     * @date: 8:38 2022/10/9
     * @param: []
     * @return: int
     *
     */
    @ApiModelProperty(value = "是否含有子节点")
    private boolean leaf;

    /**
     * 所属园区
     */
    @ApiModelProperty(value = "所属园区")
    private String park;


    public String getRedisSysName() {
        return redisSysName;
    }

    public void setRedisSysName(String redisSysName) {
        this.redisSysName = redisSysName;
    }

    public int getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(int deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public int getDeviceNodeId() {
        return deviceNodeId;
    }

    public void setDeviceNodeId(int deviceNodeId) {
        this.deviceNodeId = deviceNodeId;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceTreeFatherId() {
        return deviceTreeFatherId;
    }

    public void setDeviceTreeFatherId(int deviceTreeFatherId) {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDeviceTreeStatus() {
        return deviceTreeStatus;
    }

    public void setDeviceTreeStatus(int deviceTreeStatus) {
        this.deviceTreeStatus = deviceTreeStatus;
    }

    public boolean isDeleteAll() {
        return deleteAll;
    }

    public void setDeleteAll(boolean deleteAll) {
        this.deleteAll = deleteAll;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    public String getEngineerUnit() {
        return engineerUnit;
    }

    public void setEngineerUnit(String engineerUnit) {
        this.engineerUnit = engineerUnit;
    }

    public String getInitVal() {
        return initVal;
    }

    public void setInitVal(String initVal) {
        this.initVal = initVal;
    }

    public String getRunVal() {
        return runVal;
    }

    public void setRunVal(String runVal) {
        this.runVal = runVal;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }

    public Boolean getEnergyNode() {
        return this.energyNode;
    }

    public void setEnergyNode(Boolean energyNode) {
        this.energyNode = energyNode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("deviceTreeId", getDeviceTreeId())
                .append("deviceNodeId", getDeviceNodeId())
                .append("sysName", getSysName())
                .append("deviceType", getDeviceType())
                .append("deviceTreeFatherId", getDeviceTreeFatherId())
                .append("deviceNodeFunName", getDeviceNodeFunName())
                .append("deviceNodeFunType", getDeviceNodeFunType())
                .append("url", getUrl())
                .append("deviceTreeStatus", getDeviceTreeStatus())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("deleteAll", isDeleteAll())
                .append("alias", getAlias())
                .append("leaf", isLeaf())
                .append("leaf", isLeaf())
                .append("initVal", getInitVal())
                .append("engineerUnit", getEngineerUnit())
                .append("redisSysName", getRedisSysName())
                .toString();
    }
}
