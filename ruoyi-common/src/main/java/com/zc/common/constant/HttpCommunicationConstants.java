package com.zc.common.constant;

/**
 * @Author:gaojikun
 * @Date:2023-03-20 9:10
 * @Description:HTTP通讯协议常量
 */
public class HttpCommunicationConstants {

    /**
     * 定时器时间，单位S
     */
    public static final Integer TIMER_INTEGER = 60;

    /**
     * get获取设备信息
     */
    public static final String GET_GET_EQUIPMENT_INFO = "/equipment/queryEquipmentInfoGET";

    /**
     * post获取设备信息
     */
    public static final String POST_GET_EQUIPMENT_INFO = "/equipment/queryEquipmentInfoPOST";

    /**
     * get获取数据项实时数据
     */
    public static final String GET_GET_PRODUCTITEMDATA_INFO = "/equipment/queryProductItemDataInfoGET";

    /**
     * post获取数据项实时数据
     */
    public static final String POST_GET_PRODUCTITEMDATA_INFO = "/equipment/queryProductItemDataInfoPOST";
}
