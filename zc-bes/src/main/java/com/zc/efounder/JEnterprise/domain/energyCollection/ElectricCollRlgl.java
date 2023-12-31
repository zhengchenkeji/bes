package com.zc.efounder.JEnterprise.domain.energyCollection;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 电能参数采集方案关系对象 electric_coll_rlgl
 *
 * @author gaojikun
 * @date 2022-09-09
 */
@ApiModel(value = "ElectricCollRlgl",description = "电能参数采集方案关系对象")
public class ElectricCollRlgl extends BaseEntity {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "唯一ID",hidden = true)
    private String id;

    /**
     * 电能参数id FK(electric_params.id)
     */
    @ApiModelProperty(value = "电能参数id")
    private int electricId;

    /**
     * 电能参数编号 FK(electric_params.code)
     */
    @ApiModelProperty(value = "电能参数编号")
    private String electricCode;

    /**
     * 采集方案编号 FK(coll_method.code)
     */
    @ApiModelProperty(value = "采集方案编号")
    private String collCode;

    /**
     * 采集方案id
     */
    @ApiModelProperty(value = "采集方案id")
    private int collId;

    /**
     * 是否作为统计分析参数(是：1，否：0)
     */
    @Excel(name = "是否作为统计分析参数(是：1，否：0)")
    @ApiModelProperty(value = "是否作为统计分析参数")
    private String statisticalParam;

    /**
     * 是否变比(是：1，否：0)
     */
    @Excel(name = "是否变比(是：1，否：0)")
    @ApiModelProperty(value = "是否变比(是：1，否：0)")
    private String isRate;
    @ApiModelProperty(value = "关键字")
    private String keywords;
    @ApiModelProperty(value = "园区编号")
    private String parkCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setElectricCode(String electricCode) {
        this.electricCode = electricCode;
    }

    public String getElectricCode() {
        return electricCode;
    }

    public int getElectricId() {
        return electricId;
    }

    public void setElectricId(int electricId) {
        this.electricId = electricId;
    }

    public void setCollCode(String collCode) {
        this.collCode = collCode;
    }

    public String getCollCode() {
        return collCode;
    }

    public int getCollId() {
        return collId;
    }

    public void setCollId(int collId) {
        this.collId = collId;
    }

    public void setStatisticalParam(String statisticalParam) {
        this.statisticalParam = statisticalParam;
    }

    public String getStatisticalParam() {
        return statisticalParam;
    }

    public void setIsRate(String isRate) {
        this.isRate = isRate;
    }

    public String getIsRate() {
        return isRate;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("electricId", getElectricId())
                .append("electricCode", getElectricCode())
                .append("collCode", getCollCode())
                .append("collId", getCollId())
                .append("statisticalParam", getStatisticalParam())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("isRate", getIsRate())
                .append("keywords", getKeywords())
                .toString();
    }
}
