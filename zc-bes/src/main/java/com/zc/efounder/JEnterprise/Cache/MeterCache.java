package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.mapper.deviceTree.AthenaElectricMeterMapper;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description 电表缓存方法
 *
 * @author liuwenge
 * @date 2022/9/30 11:23
 */
@Component
public class MeterCache {
    @Resource
    private RedisCache redisCache;
    @Resource
    private AthenaElectricMeterMapper meterMapper;

    private static final Logger log = LoggerFactory.getLogger(MeterCache.class);


    /**
     * @Description 根据电表id查询电表信息
     *
     * @author liuwenge
     * @date 2022/9/30 11:26
     * @param meterId 电表id
     * @return com.ruoyi.deviceManagement.deviceTree.domain.AthenaElectricMeter
     */
    public AthenaElectricMeter getMeterByMeterId(Integer meterId) {
        Map<String, AthenaElectricMeter> meterList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter);
        if (meterList == null || meterList.size() == 0) {
            return null;
        }

        for (AthenaElectricMeter meter : meterList.values()) {


            if (meterId == meter.getMeterId().intValue())
            {
                return meter;
            }
        }

        return null;
    }

    public Controller getControllerByMeterTreeid(String treeid){

        Controller con = null;
        DeviceTree tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree,  treeid);
        for (int i = 0; i < 20; i++) {
            if (tree.getDeviceNodeId() == DeviceTreeConstants.BES_DDCNODE || //DDC
                    tree.getDeviceNodeId() == DeviceTreeConstants.BES_ILLUMINE|| //照明
                    tree.getDeviceNodeId()==DeviceTreeConstants.BES_COLLECTORNODE//能耗

            ) {
                con = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) tree.getDeviceTreeId());
                break;
            }
            tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) tree.getDeviceTreeFatherId());
        }


        return con;
    }

    /**
     * 从缓存获取电表 根据树id
     *
     * @param deviceTreeId 设备树id
     * @return {@code AthenaElectricMeter }
     * @Author qindehua
     * @Date 2022/12/23
     **/
    public AthenaElectricMeter getMeterByDeviceId(Long deviceTreeId){

        if (deviceTreeId==null){
            log.warn("deviceTreeId为空！");
            return null;
        }

        AthenaElectricMeter meter = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, deviceTreeId);

        if (meter == null) {
            AthenaElectricMeter dataMapper=meterMapper.selectAthenaElectricMeterByDeviceTreeId(deviceTreeId.toString());
            if (dataMapper==null){
                log.warn("树ID为："+deviceTreeId+"的电表 未找到！");
                return null;
            }else {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter,  dataMapper.getDeviceTreeId(), dataMapper);
                return dataMapper;
            }
        }
        return meter;
    }
}
