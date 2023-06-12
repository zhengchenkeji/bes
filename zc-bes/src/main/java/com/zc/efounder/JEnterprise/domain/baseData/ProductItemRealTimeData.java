package com.zc.efounder.JEnterprise.domain.baseData;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * description:设备数据项实时数据表
 * author: sunshangeng
 * date:2023/3/31 10:31
 */
public class ProductItemRealTimeData {
    /**默认为数据项id+设备id*/
    private String id;
    /**数据项id*/
    private Long itemDataId;
    /**设备id*/
    private Long equipmentId;
    /**数据值*/
    private String dataValue;
    /**创建时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    private ProductItemRealTimeData(Long itemDataId, Long equipmentId, String dataValue, Date createTime) {
        this.itemDataId = itemDataId;
        this.equipmentId = equipmentId;
        this.dataValue = dataValue;
        this.createTime = createTime;
        this.id=itemDataId+""+equipmentId;
    }

    /**静态创建对象方法*/
    public static  ProductItemRealTimeData CreateRealTimeData(Long itemDataId, Long equipmentId, String dataValue, Date createTime){
     return    new ProductItemRealTimeData(itemDataId,equipmentId,dataValue,createTime);
    }
    /**静态创建对象方法*/

    public static  ProductItemRealTimeData CreateRealTimeData(Long itemDataId, Long equipmentId, String dataValue){
        return    new ProductItemRealTimeData(itemDataId,equipmentId,dataValue,new Date());
    }

    public ProductItemRealTimeData() {
    }
}
