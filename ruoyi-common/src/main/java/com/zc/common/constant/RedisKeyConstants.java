package com.zc.common.constant;

/**
 * Redis key 常量定义
 *
 * @author Athena-xiepufeng
 */
public class RedisKeyConstants {
    /**
     * 品类
     */
    public static final String IOT_CATEGORY = "iot:category";

    /**
     * 功能
     */
    public static final String IOT_FUNCTION = "iot:function";

    /**
     * 功能值
     */
    public static final String IOT_FUNCTION_VALUE = "iot:functionValue";

    /**
     * 产品
     */
    public static final String IOT_PRODUCT = "iot:product";

    /**
     * 设备
     */
    public static final String IOT_DEVICE = "iot:device";

    /**
     * 设备编号主键映射关系
     */
    public static final String IOT_DEVICE_CODE_ID = "iot:deviceCodeId";

    /**
     * 分组
     */
    public static final String IOT_GROUP = "iot:group";

    /**
     * 获取设备功能值 （实时数据）
     */
    public static String getDeviceFunctionValueKey(Long deviceId) {
        return "iot:deviceFunctionValue:" + deviceId;
    }

    /**
     * 获取功能标识符和功能id映射关系
     */
    public static String getCategoryFunctionIdentifierKey(Long categoryId) {
        return "iot:categoryFunctionIdentifier:" + categoryId;
    }

    /**
     * 设备父子关系
     */
    public static String getDeviceParentChildrenKey(Long deviceId) {
        return "iot:deviceParentChildren:" + deviceId;
    }

    /**
     * 客户端实时数据发布定义
     */
    public static final String SUB_REAL_TIME_DATA_CACHE = "bes:basedatamanage:subRealTimeDataCache:subRealtimeData";

    /**
     * 采集器定义
     */
    public static final String BES_BasicData_DeviceTree_ControllerType = "bes:basicData:devicetree:controllerType";

    /**
     * 采集器定义
     */
    public static final String BES_BasicData_DeviceTree_Controller = "bes:basicData:devicetree:controller";

    /**
     * 点位定义
     */
    public static final String BES_BasicData_DeviceTree_Point = "bes:basicData:devicetree:point";

    /**
     * 点位定义
     */
    public static final String BES_BasicData_DeviceTree_Point_Config = "bes:basicData:devicetree:pointConfig";

    /**
     * 查询模块类型定义
     */
    public static final String BES_BasicData_DeviceTree_Module = "bes:basicData:devicetree:module";
    /**
     * 模块定义
     */
    public static final String BES_BasicData_DeviceTree_ModuleType = "bes:basicData:devicetree:moduleType";

    /**
     * 虚点
     */
    public static final String BES_BasicData_DeviceTree_Build_Vpoint = "bes:basicData:devicetree:tree:build:vpoint";

    /**
     * 总线
     */
    public static final String BES_BasicData_DeviceTree_Build_Bus = "bes:basicData:devicetree:tree:build:bus";

    /**
     * 线路
     */
    public static final String BES_BasicData_DeviceTree_Build_Line = "bes:basicData:devicetree:tree:build:line";

    /**
     * 设备树
     */
    public static final String BES_BasicData_DeviceTree = "bes:basicData:devicetree:tree";

    /**
     * 采集方案定义
     */
    public static final String BES_BasicData_EnergyCollection_CollMethod = "bes:basicData:energyCollection:collMethod";

    /**
     * 采集参数定义
     */
    public static final String BES_BasicData_EnergyCollection_ElectricParams = "bes:basicData:energyCollection:electricParams";

    /**
     * 采集方案能源参数
     */
    public static final String BES_BasicData_EnergyCollection_ElectricCollRlgl = "bes:basicData:energyCollection:electricCollRlgl";

    /**
     * 能耗类型定义
     */
    public static final String BES_BasicData_EnergyInfo_EnergyType = "bes:basicData:energyInfo:energyType";

    /**
     * 园区能耗类型关联
     */
    public static final String BES_BasicData_EnergyInfo_EnergyConfig = "bes:basicData:energyInfo:energyConfig";

    /**
     * 能耗总线定义
     */
    public static final String BES_BasicData_DeviceTree_Bus = "bes:basicData:devicetree:bus";

    /**
     * 能耗电表定义
     */
    public static final String BES_BasicData_DeviceTree_Meter = "bes:basicData:devicetree:meter";


    /**
     * 干线耦合器定义
     */
    public static final String BES_BasicData_DeviceTree_TrunkLine = "bes:basicData:devicetree:TrunkLine";

    /**
     * 支线耦合器定义
     */
    public static final String BES_BasicData_DeviceTree_BranchLine = "bes:basicData:devicetree:BranchLine";


    /**
     * 告警策略
     */
    public static final String BES_BasicData_SafetyWarning_AlarmTactics = "bes:basicData:safetyWarning:alarmTactics";


    /**
     * 电表上次采集时间
     */
    public static final String BES_BasicData_EnergyCollection_MeterAcquisitionTime = "bes:basicData:energyCollection:meterAcquisitionTime";

    /**
     * 电表上次采集数据
     */
    public static final String BES_BasicData_EnergyCollection_MeterAcquisitionData = "bes:basicData:energyCollection:meterAcquisitionData";

    /**
     * 电表上次采集数据
     */
    public static final String BES_BasicData_EnergyCollection_MeterAcquisitionPointData = "bes:basicData:energyCollection:meterAcquisitionPointData";

    /**
     * 电表原始数据
     */
    public static final String BES_BasicData_EnergyCollection_OriginalData = "bes:basicData:energyCollection:originalData";

    /**
     * 电表实时数据
     */
    public static final String BES_BasicData_EnergyCollection_RealTimeData = "bes:basicData:energyCollection:realTimeData";

    /**
     * 支路与电表关系
     */
    public static final String BES_BasicData_EnergyInfo_BranchMeterLink = "bes:basicData:energyInfo:branchMeterLink";

    /**
     * 支路拓扑配置缓存
     */
    public static final String BES_BasicData_EnergyInfo_BranchConfig = "bes:basicData:energyInfo:branchConfig";

    /**
     * 分户与支路关系缓存
     */
    public static final String BES_BasicData_EnergyInfo_HouseholdBranchLink = "bes:basicData:energyInfo:householdBranchLink";

    /**
     * 分项与支路关系缓存
     */
    public static final String BES_BasicData_EnergyInfo_SubitemBranchLink = "bes:basicData:energyInfo:subitemBranchLink";

    /**
     * 分项缓存
     */
    public static final String BES_BasicData_EnergyInfo_SubitemConfig = "bes:basicData:energyInfo:subitemConfig";

/**
     * 分项缓存
     */
    public static final String BES_BasicData_EnergyInfo_HouseholdConfig = "bes:basicData:energyInfo:householdConfig";


    /**
     * 阿里巴巴token
     **/
    public static final String BES_BasicData_Albaba_Token = "bes:basicData:albaba:token";


    /**
     * 定时任务巡检
     **/
    public static final String BES_Scheduled_Task_Inspection = "bes:scheduled:task:inspection";



    /**
     * 电价季节缓存
     **/
    public static final String BES_BasicData_SystemSetting_ElectricityPriceSeason = "bes:basicData:systemSetting:electricityPriceSeason";

    /**
     * 电价时间缓存
     **/
    public static final String BES_BasicData_SystemSetting_ElectricityPriceTime = "bes:basicData:systemSetting:electricityPriceTime";

    /**
     * 分时电价缓存
     **/
    public static final String BES_BasicData_SystemSetting_ElectricityPriceLink = "bes:basicData:systemSetting:electricityPriceLink";

    /**
     * 电价设置缓存
     **/
    public static final String BES_BasicData_SystemSetting_ElectricityPrice = "bes:basicData:systemSetting:electricityPrice";



    /**
     * 产品定义缓存
     **/
    public static final String BES_BasicData_Product = "bes:basicData:product:product";


    /**
     * 产品数据项缓存
     **/
    public static final String BES_BasicData_Product_ItemData = "bes:basicData:product:itemData";

    /**
     * 产品数据项参数缓存
     **/
    public static final String BES_BasicData_Product_ItemData_Params = "bes:basicData:product:itemData:params";


    /**
     * 产品功能缓存
     **/
    public static final String BES_BasicData_Product_Function = "bes:basicData:product:function";

    /**
     * 产品功能参数缓存
     **/
    public static final String BES_BasicData_Product_Function_ItemData = "bes:basicData:product:function:itemData";

    /**
     * 物联设备缓存
     **/
    public static final String BES_BasicData_Equipment = "bes:basicData:equipment:equipment";

    /**
     * 设备协议缓存
     **/
    public static final String BES_BasicData_Agreement = "bes:basicData:product:agreement";
    /**
     * 第三方设备实时数据缓存名
     **/
    public static final String BES_BasicData_Product_ItemData_RealTime = "bes:basicData:product:itemData:realTime";
    /**
     * 触发器缓存
     **/
//    public static final String BES_SceneLink_Trigger = "bes:scenelink:trigger";


    /**场景联动-场景信息*/
    public static final String BES_BasicData_SceneLink_Scene="bes:basicData:scenelink:scene";
    /**场景联动-触发器信息*/
    public static final String BES_BasicData_SceneLink_Trigger="bes:basicData:scenelink:trigger";
    /**场景联动-执行器信息*/
    public static final String BES_BasicData_SceneLink_Actuator="bes:basicData:scenelink:actuator";


}
