package com.zc.efounder.JEnterprise.domain.commhandler;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 电表错误差值日志
 **
 */
public class MonitoringErrorLog extends BaseEntity {

    //id
    private Integer id;

    //电表id
    private String sysName;

    //电能参数id
    private String electricId;

    //之前的数值
    private Double beforeData;

    //本次数值
    private Double newData;

    //差值
    private Double diffData;

    //类型: 0电表 1点位
    private String meterType;

    //时间
    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getElectricId() {
        return electricId;
    }

    public void setElectricId(String electricId) {
        this.electricId = electricId;
    }

    public Double getBeforeData() {
        return beforeData;
    }

    public void setBeforeData(Double beforeData) {
        this.beforeData = beforeData;
    }

    public Double getNewData() {
        return newData;
    }

    public void setNewData(Double newData) {
        this.newData = newData;
    }

    public Double getDiffData() {
        return diffData;
    }

    public void setDiffData(Double diffData) {
        this.diffData = diffData;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

}