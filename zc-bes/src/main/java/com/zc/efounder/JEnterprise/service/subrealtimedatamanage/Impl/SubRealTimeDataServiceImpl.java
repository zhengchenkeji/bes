package com.zc.efounder.JEnterprise.service.subrealtimedatamanage.Impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import com.zc.efounder.JEnterprise.domain.baseData.ProductFunction;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.Cache.SubRealTimeDataCache;
import com.zc.efounder.JEnterprise.domain.deviceTree.PointControlCommand;
import com.zc.efounder.JEnterprise.service.subrealtimedatamanage.SubRealTimeDataService;
import com.zc.common.constant.RedisKeyConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: wanghongjie
 * @Description:客户端实时数据订阅
 * @Date: Created in 14:25 2022/9/24
 * @Modified By:
 */
@Service
@Slf4j
public class SubRealTimeDataServiceImpl implements SubRealTimeDataService {

    @Resource
    private RedisCache redisCache;

    @Resource
    private SubRealTimeDataCache subRealTimeDataCache;

    //初始化客户端实时数据订阅缓存
    @PostConstruct
    public void init() {

        initSubRealTimeDataCache();
    }

    public void initSubRealTimeDataCache() {
        // 清空 redis 客户端实时数据订阅缓存数据
        redisCache.deleteObject(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE);
    }

    /**
     * 客户端订阅消息
     *
     * @param event
     * @return
     */
    @Override
    public AjaxResult subscribe(String event) {

        if (StringUtils.isBlank(event)) {
            return AjaxResult.error("参数错误！");
        }
        /**判断是本地设备还是第三方设备*/
        if(event.indexOf("+")==-1){
            /**bes设备*/
            DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, Long.valueOf(event) );
            if (deviceTree == null) {
                return AjaxResult.success("传入的点位或者功能信息无效，无法订阅！");
            }
        }else{
            /**第三方设备*/
            /**截取数据项id*/
            String itemId=event.substring(0,event.indexOf("+"));
            /**截取设备id*/
            String equipmentId=event.substring(event.indexOf("+")+1,event.length());
            ProductItemData itemData= redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData,itemId);
            Equipment equipment= redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,equipmentId);
            if(itemData==null||equipment==null){
                return AjaxResult.success("传入的第三方设备信息无效！");
            }
        }

        /**处理设备树订阅*/
        if (!subRealTimeDataCache.subscribeCache(event)) {
            return AjaxResult.error("订阅失败！");
        }
        return AjaxResult.success("订阅成功！");



    }

    @Override
    public AjaxResult subscribeList(List<String> eventList) {

        for (String event : eventList) {
            if (StringUtils.isBlank(event)) {
                return AjaxResult.error("参数错误！");
            }
            /**判断是本地设备还是第三方设备*/
            if(event.indexOf("+")==-1){
                /**bes设备*/
                DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, Long.valueOf(event) );
                if (deviceTree == null) {
                    continue;
                }
            }else{
                /**第三方设备*/
                /**截取数据项id*/
                String itemId=event.substring(0,event.indexOf("+"));
                /**截取设备id*/
                String equipmentId=event.substring(event.indexOf("+")+1,event.length());
                ProductItemData itemData= redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData,itemId);
                Equipment equipment= redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,equipmentId);
                if(itemData==null||equipment==null){
                    continue;
                }
            }

            /**处理设备树订阅*/
            if (!subRealTimeDataCache.subscribeCache(event)) {
                continue;
            }
        }

        return AjaxResult.success("订阅成功！");
    }

    /**
     * 客户端取消订阅
     *
     * @param event
     * @return
     */
    @Override
    public AjaxResult unsubscribe(String event) {

        if (event == null) {
            return AjaxResult.error("参数错误！");
        }

//        DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, Long.valueOf(event));
//
//        if (deviceTree == null) {
////            ProductFunction productFunction = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, (long) event);
////            if (productFunction == null) {
////                return AjaxResult.success("传入的点位或者功能信息无效，无法订阅！");
////            }
//        }

        if (!subRealTimeDataCache.unsubscribeCache(event)) {
            return AjaxResult.error("取消订阅失败！");
        }

        return AjaxResult.success("取消订阅成功！");
    }

    @Override
    public AjaxResult unsubscribeList(List<String> eventList) {
        return null;
    }

    @Override
    public AjaxResult subscribeByEqId(String event) {
        Equipment equipment=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,event);
        if(equipment==null){
            return AjaxResult.success("传入的设备ID为空");
        }
        Product product=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product,equipment.getProductId());
        if(product==null){
            return AjaxResult.success("当前设备未绑定产品");

        }
        List<String> noSubList=new ArrayList<>();
        Map<String, ProductItemData> itemDataMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);
        List<ProductItemData> itemDataList = itemDataMap.values().stream()
                .filter(itemData -> itemData.getProductId().equals(product.getId()))
                .collect(Collectors.toList());
        if ("2".equals(product.getIotType())) {

            itemDataList.forEach(itemData -> {

                String sub=itemData.getId()+"+"+equipment.getId();
//                System.out.println("订阅："+sub);
                if (!subRealTimeDataCache.subscribeCache(sub)) {
                    noSubList.add(itemData.getName());
                    log.error("根据设备绑定点位订阅时订阅失败订阅信息:"+sub);
                }
            });
        }
        if(noSubList.size()>0){
            return AjaxResult.error(JSONObject.toJSONString(noSubList)+"，这些点位未订阅成功！");
        }
        return AjaxResult.success("订阅成功");
    }

    @Override
    public AjaxResult unsubscribeByEqid(String event) {

        Equipment equipment=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,event);
        if(equipment==null){
            return AjaxResult.success("传入的设备ID为空");
        }
        Product product=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product,equipment.getProductId());
        if(product==null){
            return AjaxResult.success("当前设备未绑定产品");

        }
        List<String> noUnSubList=new ArrayList<>();
        Map<String, ProductItemData> itemDataMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);
        List<ProductItemData> itemDataList = itemDataMap.values().stream()
                .filter(itemData -> itemData.getProductId().equals(product.getId()))
                .collect(Collectors.toList());
        if ("2".equals(product.getIotType())) {
            itemDataList.forEach(itemData -> {
                String sub=itemData.getId()+"+"+equipment.getId();
                if (!subRealTimeDataCache.unsubscribeCache(sub)) {
                    noUnSubList.add(itemData.getName());
                    log.error("根据设备绑定点位订阅时订阅失败订阅信息:"+sub);
                }
            });
        }
        if(noUnSubList.size()>0){
            return AjaxResult.error(JSONObject.toJSONString(noUnSubList)+"，这些点位未取消订阅成功！");
        }
        return AjaxResult.success("订阅成功");
    }
}
