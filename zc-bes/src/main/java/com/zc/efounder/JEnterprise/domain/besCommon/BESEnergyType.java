package com.zc.efounder.JEnterprise.domain.besCommon;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 能效类型
 * @author LvSihan
 *
 */
public class BESEnergyType extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private String guid;

	private String code;

    private String name;

    private String rrice;

    private String coalAmount;

    private String co2;

    private String unit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRrice() {
        return rrice;
    }

    public void setRrice(String rrice) {
        this.rrice = rrice;
    }

    public String getCoalAmount() {
        return coalAmount;
    }

    public void setCoalAmount(String coalAmount) {
        this.coalAmount = coalAmount;
    }

    public String getCo2() {
        return co2;
    }

    public void setCo2(String co2) {
        this.co2 = co2;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}