package com.zc.efounder.JEnterprise.domain.energyInfo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 电表数据VO
 * @author qindehua
 */
@ApiModel(value = "MeterDataVo", description = "电表数据VO")
public class MeterDataVo  {
    private static final long serialVersionUID = 1L;
    /**
     * 操作之后 该节点的电表列表
     */
    @ApiModelProperty(value = "操作之后 该节点的电表列表")
    private List<ElectricMeterVo> meterList;
    /**
     * 支路节点
     */
    @NotNull(message = "支路节点不允许为空！")
    @ApiModelProperty(value = "支路节点",required = true)
    private Long branchId;

    /**
     * 支路父节点
     */
    @ApiModelProperty(value = "支路父节点",required = true)
    @NotNull(message = "支路父节点不允许为空！")
    private Long fatherId;

    /**
     * 所属园区
     */
    @NotBlank(message = "所属园区不允许为空！")
    @ApiModelProperty(value = "所属园区",required = true)
    private String parkCode;

    /**
     * 所属能源
     */
    @NotBlank(message = "所属能源不允许为空！")
    @ApiModelProperty(value = "所属能源",required = true)
    private String energyCode;

    public List<ElectricMeterVo> getMeterList() {
        return meterList;
    }

    public void setMeterList(List<ElectricMeterVo> meterList) {
        this.meterList = meterList;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public void setFatherId(Long fatherId) {
        this.fatherId = fatherId;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getEnergyCode() {
        return energyCode;
    }

    public void setEnergyCode(String energyCode) {
        this.energyCode = energyCode;
    }

    @Override
    public String toString() {
        return "MeterDataVo{" +
                "meterList=" + meterList +
                ", branchId=" + branchId +
                ", fatherId=" + fatherId +
                ", parkCode='" + parkCode + '\'' +
                ", energyCode='" + energyCode + '\'' +
                '}';
    }

    public MeterDataVo(List<ElectricMeterVo> meterList, Long branchId) {
        this.meterList = meterList;
        this.branchId = branchId;
    }

    public MeterDataVo() {
    }
}
