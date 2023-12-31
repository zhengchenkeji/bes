package com.zc.efounder.JEnterprise.domain.commhandler;

import java.io.Serializable;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 14:35 2022/7/8
 * @Modified By:
 */
public class EnergyRealTimeData implements Serializable {
    private static final long serialVersionUID = 1L;

    //电表系统名称
    private String fDbsysName;

    //电能编号
    private String fDnbh;

    //数据
    private Double fData;

    //采集时间
    private String fCjsj;

    //数据类型(1:电表   2:非电表)
    private String fType;

    //园区编号
    private String fYqbh;

    public String getfDbsysName() {
        return fDbsysName;
    }

    public void setfDbsysName(String fDbsysName) {
        this.fDbsysName = fDbsysName;
    }

    public String getfDnbh() {
        return fDnbh;
    }

    public void setfDnbh(String fDnbh) {
        this.fDnbh = fDnbh;
    }

    public Double getfData() {
        return fData;
    }

    public void setfData(Double fData) {
        this.fData = fData;
    }

    public String getfCjsj() {
        return fCjsj;
    }

    public void setfCjsj(String fCjsj) {
        this.fCjsj = fCjsj;
    }

    public String getfType() {
        return fType;
    }

    public void setfType(String fType) {
        this.fType = fType;
    }

    public String getfYqbh() {
        return fYqbh;
    }

    public void setfYqbh(String fYqbh) {
        this.fYqbh = fYqbh;
    }

    @Override
    public String toString() {
        return "EnergyRealTimeData{" +
                "fDbsysName='" + fDbsysName + '\'' +
                ", fDnbh='" + fDnbh + '\'' +
                ", fData='" + fData + '\'' +
                ", fCjsj='" + fCjsj + '\'' +
                ", fType='" + fType + '\'' +
                ", fYqbh='" + fYqbh + '\'' +
                '}';
    }
}
