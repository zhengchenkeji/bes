package com.zc.efounder.JEnterprise.domain.energyCollection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 采集参数定义对象 electric_params
 *
 * @author ruoyi
 * @date 2022-09-07
 */
@ApiModel(value = "ElectricParams",description = "采集参数定义对象")
public class ElectricParams extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    @ApiModelProperty(value = "唯一id",required = true)
    private Long id;

    /**
     * 能耗编号
     */
    @Excel(name = "能耗编号")
    @ApiModelProperty(value = "能耗编号",required = true)
    private String code;

    /**
     * 能源类型FK(energy_type.F_code)
     */
    @Excel(name = "能源类型FK(energy_type.F_code)")
    @ApiModelProperty(value = "能源类型",required = true)
    private String energyCode;

    @ApiModelProperty(value = "能源名称")
    private String energyName;

    /**
     * 能耗名称
     */
    @Excel(name = "能耗名称")
    @ApiModelProperty(value = "能耗名称",required = true)
    private String name;

    /**
     * 偏移地址
     */
    @Excel(name = "偏移地址")
    @ApiModelProperty(value = "偏移地址",required = true)
    private String offsetAddress;

    /**
     * 数据长度
     */
    @Excel(name = "数据长度")
    @ApiModelProperty(value = "数据长度",required = true)
    private Long dataLength;

    /**
     * 编码规则;0-bcd编码;1-dec编码;2-其他
     */
    @Excel(name = "编码规则;0-bcd编码;1-dec编码;2-其他")
    @ApiModelProperty(value = "编码规则;0-bcd编码;1-dec编码;2-其他",required = true)
    private Long dataEncodeType;

    /**
     * 单位
     */
    @Excel(name = "单位")
    @ApiModelProperty(value = "单位",required = true)
    private String unit;

    /**
     * 小数点位置
     */
    @Excel(name = "小数点位置")
    @ApiModelProperty(value = "小数点位置",required = true)
    private Long pointLocation;

    /**
     * 数据类型;0-int;1-float;2-double
     */
    @Excel(name = "数据类型;0-int;1-float;2-double")
    @ApiModelProperty(value = "数据类型;0-int;1-float;2-double",required = true)
    private Long dataType;

    /**
     * 解码顺序;0-12;1-21;2-1234;3-4321;4-2143;5-3412;6-123456;7-12345678;
     */
    @Excel(name = "解码顺序;0-12;1-21;2-1234;3-4321;4-2143;5-3412;6-123456;7-12345678;")
    @ApiModelProperty(value = "解码顺序;0-12;1-21;2-1234;3-4321;4-2143;5-3412;6-123456;7-12345678;",required = true)
    private Long codeSeq;

    /**
     * 园区FK(park.code)
     */
    @Excel(name = "园区FK(park.code)")
    @ApiModelProperty(value = "园区编号",required = true)
    private String parkCode;

    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remarks;
    @ApiModelProperty(value = "是否作为统计分析参数")
    private String statisticalParam;//是否作为统计分析参数
    @ApiModelProperty(value = "是否变比(是：1，否：0)")
    private String isRate; // 是否变比

    /** 参数类型；0-bes;1-第三方数据项 */
    private String deviceType;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setEnergyCode(String energyCode) {
        this.energyCode = energyCode;
    }

    public String getEnergyCode() {
        return energyCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOffsetAddress(String offsetAddress) {
        this.offsetAddress = offsetAddress;
    }

    public String getOffsetAddress() {
        return offsetAddress;
    }

    public void setDataLength(Long dataLength) {
        this.dataLength = dataLength;
    }

    public Long getDataLength() {
        return dataLength;
    }

    public void setDataEncodeType(Long dataEncodeType) {
        this.dataEncodeType = dataEncodeType;
    }

    public Long getDataEncodeType() {
        return dataEncodeType;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setPointLocation(Long pointLocation) {
        this.pointLocation = pointLocation;
    }

    public Long getPointLocation() {
        return pointLocation;
    }

    public void setDataType(Long dataType) {
        this.dataType = dataType;
    }

    public Long getDataType() {
        return dataType;
    }

    public void setCodeSeq(Long codeSeq) {
        this.codeSeq = codeSeq;
    }

    public Long getCodeSeq() {
        return codeSeq;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getParkCode() {
        return parkCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatisticalParam() {
        return statisticalParam;
    }

    public void setStatisticalParam(String statisticalParam) {
        this.statisticalParam = statisticalParam;
    }

    public String getIsRate() {
        return isRate;
    }

    public void setIsRate(String isRate) {
        this.isRate = isRate;
    }

    public String getEnergyName() {
        return energyName;
    }

    public void setEnergyName(String energyName) {
        this.energyName = energyName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("code", getCode())
                .append("energyCode", getEnergyCode())
                .append("name", getName())
                .append("offsetAddress", getOffsetAddress())
                .append("dataLength", getDataLength())
                .append("dataEncodeType", getDataEncodeType())
                .append("unit", getUnit())
                .append("pointLocation", getPointLocation())
                .append("dataType", getDataType())
                .append("codeSeq", getCodeSeq())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("parkCode", getParkCode())
                .append("remarks", getRemarks())
                .append("statisticalParam", getStatisticalParam())
                .append("isRate", getIsRate())
                .toString();
    }

    public ElectricParams(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public ElectricParams(Long id, String name, String energyName,String deviceType) {
        this.id = id;
        this.name = name;
        this.energyName = energyName;
        this.deviceType = deviceType;
    }
    public ElectricParams() {
    }
}
