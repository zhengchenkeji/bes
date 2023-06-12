package com.zc.efounder.JEnterprise.domain.deviceTree;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @Author: wanghongjie
 * @Description:点位控制指令
 * @Date: Created in 18:34 2022/11/3
 * @Modified By:
 */
@ApiModel(value = "PointControlCommand",description = "点位控制指令")
public class PointControlCommand {

    @ApiModelProperty(value = "点位或功能ID",required = true)
    private Integer id;
    @ApiModelProperty(value = "设备id",required = true)
    private Integer equipmentId;
    @ApiModelProperty(value = "值",required = true)
    private Double value;
    @ApiModelProperty(value = "多个寄存器值",required = true)
    private List<PointControlCommand> valueList;
    @ApiModelProperty(value = "工作模式 0:自动 1:手动",required = true)
    private Integer workMode;
    @ApiModelProperty(value = "点位类型 0:bes协议 1:第三方协议",required = true)
    private Integer pointType;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }


    public List<PointControlCommand> getValueList() {
        return valueList;
    }

    public void setValueList(List<PointControlCommand> valueList) {
        this.valueList = valueList;
    }

    public Integer getWorkMode() {
        return workMode;
    }

    public void setWorkMode(Integer workMode) {
        this.workMode = workMode;
    }

    public Integer getPointType() {
        return pointType;
    }

    public void setPointType(Integer pointType) {
        this.pointType = pointType;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }
}
