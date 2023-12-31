package com.zc.efounder.JEnterprise.domain.commhandler;

import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * @author liuwenge
 * @Description 分户数据
 * @date 2022/10/21 8:44
 */
public class HouseholdData extends BaseEntity {

    //id
    private String id;

    //所属分项
    private String householdId;

    //数据值
    private Double dataValue;

    //采集时间
    private String collectTime;

    //日期类型 0：时 1：天 2：月 3：年
    private String dateType;

    //园区编号
    private String parkId;

    //花费电价总金额
    private BigDecimal electricPriceSum;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public Double getDataValue() {
        return dataValue;
    }

    public void setDataValue(Double dataValue) {
        this.dataValue = dataValue;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }

    public BigDecimal getElectricPriceSum() {
        return electricPriceSum;
    }

    public void setElectricPriceSum(BigDecimal electricPriceSum) {
        this.electricPriceSum = electricPriceSum;
    }
}