package com.zc.efounder.JEnterprise.domain.systemSetting;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 电价设置对象 athena_bes_electricity_price_setting
 *
 * @author gaojikun
 * @date 2022-11-29
 */
@ApiModel(value = "ElectricityPriceSetting",description = "电价设置对象")
public class ElectricityPriceSetting extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一ID",required = true)
    private Long id;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @Excel(name = "开始时间", width = 30, dateFormat = "HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    @ApiModelProperty(value = "开始时间字符串")
    private String startTimeStr;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    @Excel(name = "结束时间", width = 30, dateFormat = "HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "结束时间字符串")
    private String endTimeStr;

    /**
     * 月份
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "月份", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "月份")
    private Date monthDate;
    @ApiModelProperty(value = "月份字符串")
    private String monthDateStr;

    /**
     * 代理购电价格
     */
    @Excel(name = "代理购电价格")
    @ApiModelProperty(value = "代理购电价格",required = true)
    private BigDecimal appPrice;

    /**
     * 容量补偿电价
     */
    @Excel(name = "容量补偿电价")
    @ApiModelProperty(value = "容量补偿电价",required = true)
    private BigDecimal cctPrice;

    /**
     * 代理购电综合损益分摊标准
     */
    @Excel(name = "代理购电综合损益分摊标准")
    @ApiModelProperty(value = "代理购电综合损益分摊标准",required = true)
    private BigDecimal ascpappPrice;

    /**
     * 电度输配电价
     */
    @Excel(name = "电度输配电价")
    @ApiModelProperty(value = "电度输配电价",required = true)
    private BigDecimal etdpPrice;

    /**
     * 政府基金及附加
     */
    @Excel(name = "政府基金及附加")
    @ApiModelProperty(value = "政府基金及附加",required = true)
    private BigDecimal gfsPrice;

    /**
     * 尖峰时段
     */
    @Excel(name = "尖峰时段")
    @ApiModelProperty(value = "尖峰时段",required = true)
    private BigDecimal spikePrice;

    /**
     * 高峰时段
     */
    @Excel(name = "高峰时段")
    @ApiModelProperty(value = "高峰时段",required = true)
    private BigDecimal peakPrice;

    /**
     * 平时段
     */
    @Excel(name = "平时段")
    @ApiModelProperty(value = "平时段",required = true)
    private BigDecimal flatPrice;

    /**
     * 低谷时段
     */
    @Excel(name = "低谷时段")
    @ApiModelProperty(value = "低谷时段",required = true)
    private BigDecimal troughPrice;

    /**
     * 深谷时段
     */
    @Excel(name = "深谷时段")
    @ApiModelProperty(value = "深谷时段",required = true)
    private BigDecimal valleyPrice;

    /**
     * 创建人
     */
    @Excel(name = "创建人")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**
     * 修改人
     */
    @Excel(name = "修改人")
    @ApiModelProperty(value = "修改人")
    private String upadteBy;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getMonthDate() {
        return monthDate;
    }

    public void setMonthDate(Date monthDate) {
        this.monthDate = monthDate;
    }

    public String getMonthDateStr() {
        return monthDateStr;
    }

    public void setMonthDateStr(String monthDateStr) {
        this.monthDateStr = monthDateStr;
    }

    public BigDecimal getAppPrice() {
        return appPrice;
    }

    public void setAppPrice(BigDecimal appPrice) {
        this.appPrice = appPrice;
    }

    public BigDecimal getCctPrice() {
        return cctPrice;
    }

    public void setCctPrice(BigDecimal cctPrice) {
        this.cctPrice = cctPrice;
    }

    public BigDecimal getAscpappPrice() {
        return ascpappPrice;
    }

    public void setAscpappPrice(BigDecimal ascpappPrice) {
        this.ascpappPrice = ascpappPrice;
    }

    public BigDecimal getEtdpPrice() {
        return etdpPrice;
    }

    public void setEtdpPrice(BigDecimal etdpPrice) {
        this.etdpPrice = etdpPrice;
    }

    public BigDecimal getGfsPrice() {
        return gfsPrice;
    }

    public void setGfsPrice(BigDecimal gfsPrice) {
        this.gfsPrice = gfsPrice;
    }

    public BigDecimal getSpikePrice() {
        return spikePrice;
    }

    public void setSpikePrice(BigDecimal spikePrice) {
        this.spikePrice = spikePrice;
    }

    public BigDecimal getPeakPrice() {
        return peakPrice;
    }

    public void setPeakPrice(BigDecimal peakPrice) {
        this.peakPrice = peakPrice;
    }

    public BigDecimal getFlatPrice() {
        return flatPrice;
    }

    public void setFlatPrice(BigDecimal flatPrice) {
        this.flatPrice = flatPrice;
    }

    public BigDecimal getTroughPrice() {
        return troughPrice;
    }

    public void setTroughPrice(BigDecimal troughPrice) {
        this.troughPrice = troughPrice;
    }

    public BigDecimal getValleyPrice() {
        return valleyPrice;
    }

    public void setValleyPrice(BigDecimal valleyPrice) {
        this.valleyPrice = valleyPrice;
    }

    public void setUpadteBy(String upadteBy) {
        this.upadteBy = upadteBy;
    }

    public String getUpadteBy() {
        return upadteBy;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public void setEndTimeStr(String endTimeStr) {
        this.endTimeStr = endTimeStr;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("monthDate", getMonthDate())
                .append("appPrice", getAppPrice())
                .append("cctPrice", getCctPrice())
                .append("ascpappPrice", getAscpappPrice())
                .append("etdpPrice", getEtdpPrice())
                .append("gfsPrice", getGfsPrice())
                .append("spikePrice", getSpikePrice())
                .append("peakPrice", getPeakPrice())
                .append("flatPrice", getFlatPrice())
                .append("troughPrice", getTroughPrice())
                .append("valleyPrice", getValleyPrice())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("upadteBy", getUpadteBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
