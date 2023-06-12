package com.zc.efounder.JEnterprise.domain.energyAnalysis;

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
@ApiModel(value = "EnergyConsumptionTrend",description = "能耗趋势实体类")
public class EnergyConsumptionTrend extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 采集时间 */
    @ApiModelProperty(value = "采集时间")
    private String beforeCJSJ;
    @ApiModelProperty(value = "采集时间",required = true)
    private String CJSJ;

    /** 动能编号 */
    @ApiModelProperty(value = "动能编号",required = true)
    private String DNBH;

    /** 支路ID */
    @ApiModelProperty(value = "支路ID",required = true)
    private String ZLID;

    /** 统计类型 */
    @ApiModelProperty(value = "统计类型")
    private String type;

    /** 分组条件 */
    @ApiModelProperty(value = "分组条件")
    private String groupBy;

    public String getBeforeCJSJ() {
        return beforeCJSJ;
    }

    public void setBeforeCJSJ(String beforeCJSJ) {
        this.beforeCJSJ = beforeCJSJ;
    }

    public String getCJSJ() {
        return CJSJ;
    }

    public void setCJSJ(String CJSJ) {
        this.CJSJ = CJSJ;
    }

    public String getDNBH() {
        return DNBH;
    }

    public void setDNBH(String DNBH) {
        this.DNBH = DNBH;
    }

    public String getZLID() {
        return ZLID;
    }

    public void setZLID(String ZLID) {
        this.ZLID = ZLID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("CJSJ", getCJSJ())
                .append("DNBH", getDNBH())
                .append("ZLID", getZLID())
                .append("type", getType())
                .toString();
    }
}
