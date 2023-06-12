package com.zc.efounder.JEnterprise.commhandler;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.result.ResultMap;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.config.Manager.MqttClientServer;
import com.zc.efounder.JEnterprise.domain.baseData.*;
import com.zc.efounder.JEnterprise.mapper.baseData.ProductMapper;
import com.zc.efounder.JEnterprise.mapper.commhandler.JobManagerMapper;
import com.zc.efounder.JEnterprise.service.baseData.impl.EquipmentServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * description:mqtt消息处理 初始化订阅公共方法
 * author: sunshangeng
 * date:2023/3/16 9:55
 */
@Slf4j
public class MqttHandler {


    private static RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);
    private static ProductMapper productMapper = ApplicationContextProvider.getBean(ProductMapper.class);
    private static EquipmentServiceImpl equipmentService = ApplicationContextProvider.getBean(EquipmentServiceImpl.class);
    private static MqttClientServer mqttClientServer = ApplicationContextProvider.getBean(MqttClientServer.class);
    private static JobManagerMapper jobManagerMapper=ApplicationContextProvider.getBean(JobManagerMapper.class);
    private static final String defaultTopic = "mqtt";

    /**
     * @description:处理接收到的mqtt消息体
     * @author: sunshangeng
     * @date: 2023/3/16 11:54
     * @param: [主题, 消息体]
     * @return: void
     **/
    public static void executeMqttMessage(String topic, String message) {
        try {
        Map<String, ProductItemData> ProductItemDataCaChe = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);
        ProductItemDataCaChe.values().stream()
                .filter(item -> {
                    if (StringUtils.isNotBlank(item.getSubscribeAddress())) {
                        if (item.getSubscribeAddress().equals(topic)) {
                            Product prodcut = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, item.getProductId());
                            /**只保留产品启用的且协议类型为mqtt的*/
                            if (prodcut.getState().equals("1")&&prodcut.getCommunicationProtocol().equals("1")) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .forEach(item -> {
                    JSONObject dataJson = JSONObject.parseObject(message);
                    BigDecimal data = dataJson.getBigDecimal(item.getName());
                    /**处理数据存储*/
                    item.setDataValue(data);
                    item.setDataTime(new Date());
                    /**获取到对应设备*/
                    Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, dataJson.getString("deviceId"));
                    /**判断是否存储数据*/
                    if (item.getPreserveType().equals("1")) {
                        /**判断是否是电表*/
                        if (equipment.getMeterState().equals("1")) {
                            Map<String, Object> dataMap = new HashMap<>();
                            dataMap.put("id",DateUtils.dateTimeNow()+"_"+item.getId());
                            dataMap.put("meteruuid",equipment.getName());
                            dataMap.put("data",data);
                            dataMap.put("l_time", DateUtils.getTime());
                            dataMap.put("parkid",equipment.getParkCode());
                            dataMap.put("electricId",item.getId());
                            List<Map<String,Object>> mapList=new ArrayList<>();
                            mapList.add(dataMap);
                            /**插入电表数据表*/
                            jobManagerMapper.insertCalculateData(mapList);
                            /**插入电表原始数据表*/
                            jobManagerMapper.insertEnectricData(mapList);
                        } else {
                            /**非电表*/
                            productMapper.updateAthenaBesProductItemData(item);
                            equipmentService.insertHistoryData(new HistoryItemData(item.getId(), item.getDataValue()));
                        }
                    }
                    /**处理缓存item*/
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, item.getId(), item);
                });
        }catch (Exception e){
            log.error("处理mqtt消息时出错",e);
        }
    }

    /**
     * @description:mqtt调用功能
     * @author: sunshangeng
     * @date: 2023/3/16 16:45
     * @param: [功能id, qps]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap sendMessage(Long funtionId, Integer qos) throws Exception {
        String message = null;
        ProductFunction productFuntion = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, funtionId);
        if (productFuntion == null) {
            productFuntion = productMapper.selectAthenaBesProductFunctionById(funtionId);
            if (productFuntion != null) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, productFuntion.getId(), productFuntion);
            } else {
                return ResultMap.error("通过id未查询到功能定义！");
            }
        }

        if (productFuntion.getIssuedType().equals("0")) {
            /**指令下发*/
            message = productFuntion.getInstruct();
        } else {
            /**数据项下发*/
            ProductItemData itemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productFuntion.getDataItem());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(itemData.getName(), productFuntion.getItemValue());
            message = jsonObject.toJSONString();
        }
        if (qos == null) {
            mqttClientServer.pub("1",defaultTopic, message);
        } else {
            mqttClientServer.pub("1",defaultTopic, message, qos);
        }
        return ResultMap.ok();
    }


    /**
     * @description:变更设备协议订阅地址
     * @author: sunshangeng
     * @date: 2023/3/20 9:36
     * @param: [数据项id, 订阅id]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap ChangeSub(Long itemDataId, String topic) throws Exception {
        if (itemDataId == null || StringUtils.isBlank(topic)) {
            return ResultMap.error("传入的参数不完整");
        }

        ProductItemData itemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, itemDataId);
        if(itemData==null){
            /**新增时直接订阅主题*/
            mqttClientServer.sub("1",topic);
        }
        if (topic.equals(itemData.getSubscribeAddress())) {
            return ResultMap.ok();
        }
        /**调用取消订阅方法*/
        cancelSub(itemDataId, itemData.getSubscribeAddress());

        /**重新订阅新的主题*/
        mqttClientServer.sub("1",topic);

        return ResultMap.ok();
    }


    /**
     * @description:取消订阅
     * @author: sunshangeng
     * @date: 2023/3/20 14:29
     * @param: [数据项id, 订阅主题]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap cancelSub(Long itemDataId, String topic) throws Exception {

        if (itemDataId == null || StringUtils.isBlank(topic)) {
            return ResultMap.error("传入的参数不完整");
        }

        Map<String, ProductItemData> productItemDataCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);
        List<ProductItemData> productItemDataList = productItemDataCache.values().stream()
                .filter(item -> {
                    if (StringUtils.isNotBlank(item.getSubscribeAddress()) && item.getId() != itemDataId.longValue()) {
                        /**判断除当前外*/
                        if (item.getSubscribeAddress().equals(topic)) {
                            Product prodcut = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, item.getProductId());
                            /**只保留产品启用的*/
                            if (prodcut.getState().equals("1")) {
                                return true;
                            }
                        }
                    }
                    return false;
                }).collect(Collectors.toList());

        /**进行取消订阅*/
        if (productItemDataList.size() == 0) {
            mqttClientServer.cleanTopic("1",topic);
        }
        return ResultMap.ok();
    }
}
