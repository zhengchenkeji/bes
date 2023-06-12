package com.zc.efounder.JEnterprise.domain.electricPowerTranscription.vo;


import com.ruoyi.common.core.domain.BaseEntity;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ExtremeValueReportParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@ApiModel(value = "ParamVO",description = "电参数数据")
public class ParamVO extends BaseEntity {

    /**电能参数code***/
    @ApiModelProperty(value = "电能参数集合",required = true)
    private List<String> paramsId;

    @ApiModelProperty(value = "电能参数code集合(以逗号隔开例如 1,2)")
    private List<String> paramId;

    /**开始时间***/
    @ApiModelProperty(value = "开始时间",required = true)
    @NotBlank(message = "开始时间不能为空！")
    private String startTime;
    /**结束时间***/
    @ApiModelProperty(value = "结束时间",required = true)
    @NotBlank(message = "结束时间不能为空！")
    private String endTime;
    /**电表树id***/
    @ApiModelProperty(value = "电表树id",required = true)
    private Long meter;

    /**电表树id集合  批量导出时使用***/
    @ApiModelProperty(value = "电表树id集合  批量导出时使用(以逗号隔开例如 1,2)")
    private List<Long> meterIds;

    /**设备类型***/
    private String deviceType;

    public List<Long> getMeterIds() {
        return meterIds;
    }

    public void setMeterIds(List<Long> meterIds) {
        this.meterIds = meterIds;
    }

    public List<String> getParamsId() {
        return paramsId;
    }

    public void setParamsId(List<String> paramsId) {
        this.paramsId = paramsId;
    }

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

    public Long getMeter() {
        return meter;
    }

    public void setMeter(Long meter) {
        this.meter = meter;
    }

    public List<String> getParamId() {
        return paramId;
    }

    public void setParamId(List<String> paramId) {
        this.paramId = paramId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
