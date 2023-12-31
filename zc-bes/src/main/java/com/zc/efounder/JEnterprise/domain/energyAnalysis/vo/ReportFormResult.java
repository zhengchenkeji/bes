package com.zc.efounder.JEnterprise.domain.energyAnalysis.vo;

import com.ruoyi.common.annotation.Excel;

/**
 * description:集抄报表数据响应类
 * author: sunshangeng
 * date:2022/11/28 9:13
 */
public class ReportFormResult {

    /**能源名称*/
    @Excel(name = "能源名称")
    private String energyname;
    /**开始时间*/
    @Excel(name = "起始时间")
    private String startTime;

    /**起始值*/
    @Excel(name = "起始数值")
    private Double startValue=0.000000;
    /**截止时间*/
    @Excel(name = "截止时间")
    private  String endTime;

    /**截止值*/
    @Excel(name = "截止数值")

    private Double endValue =0.000000;
    /**差值*/
    @Excel(name = "差值")
    private Double difference;





    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getStartValue() {
        return startValue;
    }

    public void setStartValue(Double startValue) {
        this.startValue = startValue;
    }

    public Double getEndValue() {
        return endValue;
    }

    public void setEndValue(Double endValue) {
        this.endValue = endValue;
    }

    public Double getDifference() {
        return difference;
    }

    public void setDifference(Double difference) {
        this.difference = difference;
    }

    public String getEnergyname() {
        return energyname;
    }

    public void setEnergyname(String energyname) {
        this.energyname = energyname;
    }

    @Override
    public String toString() {
        return "ReportFormResult{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", startValue=" + startValue +
                ", endValue=" + endValue +
                ", difference=" + difference +
                ", energyname='" + energyname + '\'' +
                '}';
    }
}
