package com.zc.common.constant;

/**
 * websocket 消息事件名称定义
 * @author Athena-xiepufeng
 */
public class WebSocketEvent
{

    /**
     * 支付事件
     */
    public final static String PAYMENT = "payment_webSocket_send";

    /**
     * 设备实时数据
     */
    public final static String IOT_DEVICE_REALTIME_DATA = "iotDeviceRealtimeData";



    public static String DDC = "DDC";

    public static String EDC = "EDC"; // 能耗

    public static String LDC = "LDC"; // 照明

    public static String DEVICE_STATE  = "DEVICE_STATE";
    /***报警条数**/
    public static String ALARM  = "ALARM";
    /***刷新实时列表指令**/
    public static String ALARMLIST  = "ALARMLIST";
    /***报警播报**/
    public static String ALARMMSG  = "ALARMMSG";


    public static String MODBUS_SERVER_STATE = "MODBUS_SERVER_STATE"; // MODBUS服务端在线离线状态

    public static String MODBUS_DEVICE = "MODBUS_DEVICE"; //MODBUS设备

}
