package com.zc.efounder.JEnterprise.domain.energyCollection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 采集方案对象 coll_method
 *
 * @author ruoyi
 * @date 2022-09-08
 */
@ApiModel(value = "CollMethod",description = "采集方案对象")
public class CollMethod extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /*id*/
    @ApiModelProperty(value = "唯一ID",required = true)
    private int id;

    /**
     * 编号
     */
    @ApiModelProperty(value = "采集方案编号",required = true)
    private String code;

    /**
     * 名称
     */
    @Excel(name = "名称")
    @ApiModelProperty(value = "名称",required = true)
    private String name;

    /**
     * 园区编号 FK(BES_PARK.F_YQBH)
     */
    @Excel(name = "园区编号")
    @ApiModelProperty(value = "园区编号",required = true)
    private String parkCode;

    /**
     * 能源类型FK(BES_ENERGY_TYPE.F_NYBH)
     */
    @Excel(name = "能源类型")
    @ApiModelProperty(value = "能源类型",required = true)
    private String energyCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setEnergyCode(String energyCode) {
        this.energyCode = energyCode;
    }

    public String getEnergyCode() {
        return energyCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("code", getCode())
                .append("name", getName())
                .append("parkCode", getParkCode())
                .append("energyCode", getEnergyCode())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
