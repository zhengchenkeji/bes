package com.zc.common.constant;

/**
 *  Redis 消息订阅事件名称
 * @author Athena-xiepufeng
 */
public class RedisChannelConstants {


    /**
     * 设置属性
     */
    public static final String IOT_SET_ATTRIBUTE = "iot:setAttribute";

    /**
     * 获取属性
     */
    public static final String IOT_GET_ATTRIBUTE = "iot:getAttribute";

    /**
     * 服务调用
     */
    public static final String IOT_SERVE_INVOKE = "iot:serveInvoke";

    /**
     * 设置子设备属性
     */
    public static final String IOT_SET_ATTRIBUTE_CHILD_DEVICE = "iot:setAttributeChildDevice";

    /**
     * 获取子设备属性
     */
    public static final String IOT_GET_ATTRIBUTE_CHILD_DEVICE = "iot:getAttributeChildDevice";

    /**
     * 子设备服务调用
     */
    public static final String IOT_SERVE_INVOKE_CHILD_DEVICE = "iot:serveInvokeChildDevice";

    /**
     *
     * @Description: 测试消息订阅
     *
     * @auther: wanghongjie
     * @date: 14:02 2023/2/3
     * @param:
     * @return:
     *
     */
    public static  final String TEST_SUB_DEMO = "bes:deviceManage";

    /**
     * @Description: 定时任务电表信息
     * @auther: gaojikun
     */
    public static  final String Meter_PUB_SUB_INFO = "bes:meterDataInfo";

}
