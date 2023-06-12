package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: wanghongjie
 * @Description:物联设备缓存方法
 * @Date: Created in 8:37 2023/3/17
 * @Modified By:
 */
@Component
public class IotEquipmentCache {
    @Resource
    private RedisCache redisCache;

    //根据设备id获取所有的子设备
    public List<Equipment> getEquipmentListById(Long id) {

        if (id == null) {
            return null;
        }
        List<Equipment> equipmentList = new ArrayList<>();

        Map<String,Equipment> equipmentMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);
        if (equipmentMap == null || equipmentMap.size() == 0) {
            return null;
        }
        equipmentMap.values().forEach(val -> {
            if (id == val.getpId()) {
                equipmentList.add(val);
            }
        });

        if (equipmentList.size() == 0) {
            return null;
        }

        return equipmentList;
    }
}
