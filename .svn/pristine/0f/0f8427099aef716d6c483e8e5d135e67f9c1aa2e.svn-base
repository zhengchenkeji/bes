package com.zc.efounder.JEnterprise.Cache;


import com.ruoyi.common.core.redis.RedisCache;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static com.ruoyi.common.utils.SecurityUtils.getLoginUser;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 14:51 2022/9/24
 * @Modified By:
 */
@Component
public class SubRealTimeDataCache {

    @Resource
    private RedisCache redisCache;

    // 添加一个订阅到缓存
    public Boolean subscribeCache(String event)
    {
        Set<String> subRealTimeDataSet = redisCache.getCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE,event);

        // 获取当前用户token
        String token = getLoginUser().getToken();

        if (subRealTimeDataSet == null) {

            subRealTimeDataSet = new HashSet<>();

            subRealTimeDataSet.add(token);

            redisCache.setCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event,subRealTimeDataSet);

            return true;
        }

        if (subRealTimeDataSet.contains(event))
        {
            return true;
        }

        subRealTimeDataSet.add(token);

        redisCache.setCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event,subRealTimeDataSet);

        return true;
    }

    // 取消一个订阅到缓存
    public Boolean unsubscribeCache(String event)
    {
        if (event == null)
        {
            return false;
        }

        // 获取当前用户token
        String token = getLoginUser().getToken();

        Set<String> subRealTimeDataSet = redisCache.getCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event);

        if (subRealTimeDataSet == null || subRealTimeDataSet.isEmpty()) {
            /**未查询到当前设备的订阅的信息，代表已删除订阅或未成功订阅 返回成功取消！*/
//            redisCache.delCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event);
            return true;
        }

        if (!subRealTimeDataSet.contains(token))
        {
            /**未查询到当前用户的订阅的信息，代表已删除订阅或未成功订阅 返回成功取消！*/
            return true;
        }

        if (subRealTimeDataSet.remove(token))
        {
            if (subRealTimeDataSet.isEmpty())
            {
                redisCache.delCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event);
            } else {
                redisCache.setCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event,subRealTimeDataSet);
            }

            return true;
        }

        return true;
    }

    // 根据 token 取消订阅到缓存
    public void unsubscribeCacheBySessionId(String token)
    {
        if (token == null)
        {
            return;
        }
        LinkedHashMap map = new LinkedHashMap();
        map = (LinkedHashMap) redisCache.getCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE);

        Collection<String> events = map.keySet();

        if (events.size() == 0) {
            return;
        }

        for (String event : events) {

            Set<String> subRealTimeDataSet = redisCache.getCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event);

            if (subRealTimeDataSet == null || subRealTimeDataSet.isEmpty())
            {
                redisCache.delCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event);
                continue;
            }

            if (!subRealTimeDataSet.contains(token))
            {
                continue;
            }

            if (subRealTimeDataSet.remove(token))
            {

                if (subRealTimeDataSet.isEmpty())
                {
                    redisCache.delCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event);
                } else {
                    redisCache.setCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, event, subRealTimeDataSet);
                }
            }

        }
    }
}
