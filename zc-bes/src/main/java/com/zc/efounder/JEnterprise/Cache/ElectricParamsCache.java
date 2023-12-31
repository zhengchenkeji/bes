package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description 采集参数缓存方法
 *
 * @author liuwenge
 * @date 2022/9/30 14:23
 */
@Component
public class ElectricParamsCache {
    @Resource
    private RedisCache redisCache;


    /**
     * @Description 根据能耗编号获取缓存
     *
     * @author liuwenge
     * @date 2022/9/30 14:16
     * @param energyCode
     * @return com.ruoyi.energyCollection.acquisitionParam.domain.ElectricParams
     */
    public ElectricParams getCacheByEnergyCode(String energyCode) {
        Map<String, ElectricParams> electricParamsList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams);
        if (electricParamsList == null || electricParamsList.size() == 0) {
            return null;
        }

        for (ElectricParams electricParams : electricParamsList.values()) {


            if (energyCode.equals(electricParams.getCode()))
            {
                return electricParams;
            }
        }

        return null;
    }
}
