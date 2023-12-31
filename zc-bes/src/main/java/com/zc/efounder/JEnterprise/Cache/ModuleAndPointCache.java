package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.deviceTree.Module;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ModuleMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.PointMapper;
import com.zc.common.constant.RedisKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 模块，模块点的缓存
 *
 * @author qindehua
 * @date 2022/12/23
 */
@Component
public class ModuleAndPointCache {

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private PointMapper pointMapper;

    private static final Logger log = LoggerFactory.getLogger(ModuleAndPointCache.class);


    /**
     * 从缓存获取模块 根据树id
     *
     * @param deviceTreeId 设备树id
     * @return {@code AthenaElectricMeter }
     * @Author qindehua
     * @Date 2022/12/23
     **/
    public Module getModuleByDeviceId(Long deviceTreeId){

        if (deviceTreeId==null){
            log.warn("deviceTreeId为空！");
            return null;
        }

        Module module = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, deviceTreeId);
        if (module == null) {
            Module dataMapper=moduleMapper.selectModuleByDeviceTreeId(deviceTreeId.intValue());
            if (dataMapper==null){
                log.warn("树ID为："+deviceTreeId+"的模块 未找到！");
                return null;
            }else {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module,  dataMapper.getDeviceTreeId(), dataMapper);
                return dataMapper;
            }
        }
        return module;
    }

    /**
     * 从缓存获取点位 根据树id
     *
     * @param deviceTreeId 设备树id
     * @return {@code AthenaElectricMeter }
     * @Author qindehua
     * @Date 2022/12/23
     **/
    public Point getPointByDeviceId(Long deviceTreeId){

        if (deviceTreeId==null){
            log.warn("deviceTreeId为空！");
            return null;
        }

        Point point = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, deviceTreeId);
        if (point == null) {
            Point dataMapper=pointMapper.selectPointByTreeId(deviceTreeId);
            if (dataMapper==null){
                log.warn("树ID为："+deviceTreeId+"的点位 未找到！");
                return null;
            }else {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point,  dataMapper.getTreeId(), dataMapper);
                return dataMapper;
            }
        }
        return point;
    }
}
