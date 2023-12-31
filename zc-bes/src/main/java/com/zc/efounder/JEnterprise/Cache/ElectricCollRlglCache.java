package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description 电能参数(采集参数)采集方案关系
 *
 * @author liuwenge
 * @date 2022/9/30 11:23
 */
@Component
public class ElectricCollRlglCache {
    @Resource
    private RedisCache redisCache;


    /**
     * @Description 根据采集方案编号查询
     *
     * @author liuwenge
     * @date 2022/9/30 14:00
     * @param collId
     * @return java.util.List<com.ruoyi.energyCollection.collMethod.domain.ElectricCollRlgl>
     */
    public List<ElectricCollRlgl> getElectricCollRlglByCollId(Integer collId) {

        if (collId == null)
        {
            return null;
        }

        Map<String, ElectricCollRlgl> electricCollRlglList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
        if (electricCollRlglList == null || electricCollRlglList.size() == 0) {
            return null;
        }


        List<ElectricCollRlgl> besElectricCollRlgls = new ArrayList<>();

        for (ElectricCollRlgl electricCollRlgl : electricCollRlglList.values()) {


            if (collId.equals(electricCollRlgl.getCollId()))
            {
                besElectricCollRlgls.add(electricCollRlgl);
            }
        }

        // 排序根据电能编号
        besElectricCollRlgls.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getElectricCode())));

        return besElectricCollRlgls;
    }

    public ElectricCollRlgl getElectricCollRlglByCollIdandParamsId(Integer collId,String paramId) {

        if (collId == null)
        {
            return null;
        }

        if (paramId == null)
        {
            return null;
        }

        Map<String, ElectricCollRlgl> electricCollRlglList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
        if (electricCollRlglList == null || electricCollRlglList.size() == 0) {
            return null;
        }


//        List<ElectricCollRlgl> besElectricCollRlgls = new ArrayList<>();

        for (ElectricCollRlgl electricCollRlgl : electricCollRlglList.values()) {


            if (collId.equals(electricCollRlgl.getCollId())&&paramId.equals(electricCollRlgl.getElectricCode()))
            {

                return electricCollRlgl;
//                besElectricCollRlgls.add(electricCollRlgl);
            }
        }

        // 排序根据电能编号
//        besElectricCollRlgls.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getElectricCode())));

        return null;
    }

}
