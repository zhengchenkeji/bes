package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.commhandler.EnergyRealTimeData;
import com.zc.common.constant.RedisKeyConstants;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description 电表实时数据缓存方法
 *
 * @author liuwenge
 * @date 2022/9/30 11:23
 */
@Component
public class EnergyRealTimeDataCache {
    @Resource
    private RedisCache redisCache;


    public List<EnergyRealTimeData> getCachedElementList(Set<String> key) throws CacheException
    {
        Map<String, EnergyRealTimeData> energyRealTimeDataCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_RealTimeData);
        if (energyRealTimeDataCache == null || energyRealTimeDataCache.size() == 0 || key == null) {
            return null;
        }

        List<EnergyRealTimeData> list = new ArrayList<>();

        Iterator<String> it = key.iterator();
        while (it.hasNext()) {
            list.add(energyRealTimeDataCache.get(it.next()));
        }

        return list;
    }
}
