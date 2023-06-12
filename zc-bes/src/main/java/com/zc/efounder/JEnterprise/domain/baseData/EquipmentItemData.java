package com.zc.efounder.JEnterprise.domain.baseData;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.List;

/**
 *
 * @Description: 设备给数据项重命名表
 * 
 * @auther: wanghongjie
 * @date: 15:01 2023/5/30
 * @param: 
 * @return: 
 *
 */
public class EquipmentItemData extends BaseEntity {

    private static final long serialVersionUID = -3716053464926594290L;
    /**
     * 主键
     */
    private Long id;

    /**
     * @Description: 设备id
     *
     */
    private Long equipmentId;

    /**
     * 数据项id
     */
    private Long itemDataId;

    /**
     * 数据项重命名
     */
    private String itemDataCustomName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getItemDataId() {
        return itemDataId;
    }

    public void setItemDataId(Long itemDataId) {
        this.itemDataId = itemDataId;
    }

    public String getItemDataCustomName() {
        return itemDataCustomName;
    }

    public void setItemDataCustomName(String itemDataCustomName) {
        this.itemDataCustomName = itemDataCustomName;
    }
}
