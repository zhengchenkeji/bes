package com.zc.efounder.JEnterprise.domain.energyInfo.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 电表VO
 * @author qindehua
 */
@ApiModel(value = "ElectricMeterVo", description = "电表VO")
public class ElectricMeterVo {
    private static final long serialVersionUID = 1L;

    /**
     * 电表ID
     */
    @ApiModelProperty(value = "电表ID",required = true)
    private Long meterId;
    /**
     * 运算符
     */
    @ApiModelProperty(value = "运算符",required = true)
    private String operator;

    /**
     * 电表别名
     */
    @ApiModelProperty(value = "电表别名",required = true)
    private String alias;


    /**
     * 电表系统名称
     */
    @ApiModelProperty(value = "电表系统名称",required = true)
    private String sysName;

    @ApiModelProperty(value = "电能参数",required = true)
    private String electricParam;
    /**
     * 电表类型
     */
    @ApiModelProperty(value = "电表类型 point-点位 meter-电表 ",required = true)
    private String type;

    /**
     * 采集方案
     */
    @ApiModelProperty(value = "采集方案",required = true)
    private Long collectionMethodCode;

    /** 设备树ID */
    @ApiModelProperty(value = "设备树ID",required = true)
    private Long deviceTreeId;


    /**
     * 设备类型
     */
    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Long getDeviceTreeId() {
        return deviceTreeId;
    }

    public void setDeviceTreeId(Long deviceTreeId) {
        this.deviceTreeId = deviceTreeId;
    }

    public Long getCollectionMethodCode() {
        return collectionMethodCode;
    }

    public void setCollectionMethodCode(Long collectionMethodCode) {
        this.collectionMethodCode = collectionMethodCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getElectricParam() {
        return electricParam;
    }

    public void setElectricParam(String electricParam) {
        this.electricParam = electricParam;
    }
}
