package com.zc.efounder.JEnterprise.domain.electricPowerTranscription;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Author:gaojikun
 * @Date:2022-11-25 14:57
 * @Description: 能耗趋势实体类
 */
@ApiModel(value = "PowerData",description = "能耗趋势实体类")
public class PowerData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 支路编号 */
    @ApiModelProperty(value = "支路编号",required = true)
    private String branchId;

    /** 电表编号 */
    @ApiModelProperty(value = "电表编号",required = true)
    private String meterId;

    /** 电表类型 */
    @ApiModelProperty(value = "电表类型",required = true)
    private String meterType;

    /** 电表系统名称 */
    @ApiModelProperty(value = "电表系统名称",required = true)
    private String sysName;

    /** 参数编号 */
    @ApiModelProperty(value = "参数编号",required = true)
    private String paramsId;

    /** 选中的逐日极值参数ID */
    @ApiModelProperty(value = "选中的逐日极值参数ID",required = true)
    private String paramsItemId;

    /** 采集参数ID */
    @ApiModelProperty(value = "采集参数ID",required = true)
    private String paramsIdStr;

    /** 开始日期 */
    @ApiModelProperty(value = "开始日期",required = true)
    private String startTime;

    /** 结束日期 */
    @ApiModelProperty(value = "结束日期",required = true)
    private String endTime;

    /** 第三方设备数据项 */
    private String electricParam;

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
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

    public String getParamsId() {
        return paramsId;
    }

    public void setParamsId(String paramsId) {
        this.paramsId = paramsId;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getParamsIdStr() {
        return paramsIdStr;
    }

    public void setParamsIdStr(String paramsIdStr) {
        this.paramsIdStr = paramsIdStr;
    }

    public String getParamsItemId() {
        return paramsItemId;
    }

    public void setParamsItemId(String paramsItemId) {
        this.paramsItemId = paramsItemId;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public String getElectricParam() {
        return electricParam;
    }

    public void setElectricParam(String electricParam) {
        this.electricParam = electricParam;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("branchId", getBranchId())
                .append("meterId", getMeterId())
                .append("paramsId", getParamsId())
                .toString();
    }
}
