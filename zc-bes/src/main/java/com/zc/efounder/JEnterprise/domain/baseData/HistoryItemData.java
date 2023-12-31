package com.zc.efounder.JEnterprise.domain.baseData;

import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author:gaojikun
 * @Date:2023-03-15 15:06
 * @Description:
 */
public class HistoryItemData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 数据项ID */
    private Long itemDataId;

    /** 时间 */
    private String time;

    /** 数值 */
    private BigDecimal dataValue;

    /** 编号 */
    private String code;

    /** 编号 */
    private Long equipmentId;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getDataValue() {
        return dataValue;
    }

    public void setDataValue(BigDecimal dataValue) {
        this.dataValue = dataValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getItemDataId() {
        return itemDataId;
    }

    public void setItemDataId(Long itemDataId) {
        this.itemDataId = itemDataId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public HistoryItemData(Long itemDataId, BigDecimal dataValue) {
        this.itemDataId = itemDataId;
        this.dataValue = dataValue;
    }

    public HistoryItemData(Long itemDataId, BigDecimal dataValue, Long equipmentId) {
        this.itemDataId = itemDataId;
        this.dataValue = dataValue;
        this.equipmentId = equipmentId;
    }

    public HistoryItemData() {
    }
}

