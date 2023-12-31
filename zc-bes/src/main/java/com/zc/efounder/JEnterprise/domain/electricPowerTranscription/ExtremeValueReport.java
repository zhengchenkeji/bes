package com.zc.efounder.JEnterprise.domain.electricPowerTranscription;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @Author:gaojikun
 * @Date:2022-11-25 14:57
 * @Description: 能耗趋势实体类
 */
@ApiModel(value = "ExtremeValueReport",description = "能耗趋势实体类")
public class ExtremeValueReport extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 日期 */
    @ApiModelProperty(value = "日期 ",required = true)
    private String dayDate;

    /** 月份 */
    @ApiModelProperty(value = "月份",required = true)
    private String monDate;

    /** 采集参数编号 */
    @ApiModelProperty(value = "采集参数编号",required = true)
    private String paramsId;

    /** 多条采集参数 */
    @ApiModelProperty(value = "多条采集参数",required = true)
    private List<ExtremeValueReportParam> paramsIdStr;

    /** 多条采集参数标题 */
    @ApiModelProperty(value = "多条采集参数标题",hidden = true)
    private String tableColumnexport;

    /** 支路下电表 */
    @ApiModelProperty(value = "支路下电表集合(以逗号隔开例如 1,2)",required = true)
    private List<String> meterList;

    /** 日月 */
    @ApiModelProperty(value = "日月 1:日 2:月",required = true)
    private String dataType;

    /** 支路ID */
    @ApiModelProperty(value = "支路ID",required = true)
    private String branchId;

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public String getMonDate() {
        return monDate;
    }

    public void setMonDate(String monDate) {
        this.monDate = monDate;
    }

    public String getParamsId() {
        return paramsId;
    }

    public void setParamsId(String paramsId) {
        this.paramsId = paramsId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<ExtremeValueReportParam> getParamsIdStr() {
        return paramsIdStr;
    }

    public void setParamsIdStr(List<ExtremeValueReportParam> paramsIdStr) {
        this.paramsIdStr = paramsIdStr;
    }

    public List<String> getMeterList() {
        return meterList;
    }

    public void setMeterList(List<String> meterList) {
        this.meterList = meterList;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getTableColumnexport() {
        return tableColumnexport;
    }

    public void setTableColumnexport(String tableColumnexport) {
        this.tableColumnexport = tableColumnexport;
    }
}
