package com.zc.efounder.JEnterprise.domain.baseData;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author:gaojikun
 * @Date:2023-03-23 8:48
 * @Description:数据项报警
 */
public class WarnItemData {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 预警类型 */
    private Long earlyWarnType;

    /** 发生时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date happenTime;

    /** 恢复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date resumeTime;

    /** 告警状态 */
    private Long warnType;

    /** 告警名称 */
    private String warnName;

    /** 告警内容 */
    private String warnInfo;

    /** 数据项主键 */
    private Long itemDataId;

    /** 数据项名称 */
    private String itemDataName;

    /** 设备主键 */
    private Long equipmentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEarlyWarnType() {
        return earlyWarnType;
    }

    public void setEarlyWarnType(Long earlyWarnType) {
        this.earlyWarnType = earlyWarnType;
    }

    public Date getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(Date happenTime) {
        this.happenTime = happenTime;
    }

    public Date getResumeTime() {
        return resumeTime;
    }

    public void setResumeTime(Date resumeTime) {
        this.resumeTime = resumeTime;
    }

    public Long getWarnType() {
        return warnType;
    }

    public void setWarnType(Long warnType) {
        this.warnType = warnType;
    }

    public String getWarnName() {
        return warnName;
    }

    public void setWarnName(String warnName) {
        this.warnName = warnName;
    }

    public String getWarnInfo() {
        return warnInfo;
    }

    public void setWarnInfo(String warnInfo) {
        this.warnInfo = warnInfo;
    }

    public Long getItemDataId() {
        return itemDataId;
    }

    public void setItemDataId(Long itemDataId) {
        this.itemDataId = itemDataId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getItemDataName() {
        return itemDataName;
    }

    public void setItemDataName(String itemDataName) {
        this.itemDataName = itemDataName;
    }


    public WarnItemData(Date happenTime,Long equipmentId,String warnName,String warnInfo){
        this.happenTime = happenTime;
        this.equipmentId = equipmentId;
        this.warnName = warnName;
        this.warnInfo = warnInfo;
    }

    public WarnItemData(){

    }

}
