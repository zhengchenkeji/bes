package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ControllerMapper;
import com.zc.common.constant.RedisKeyConstants;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: wanghongjie
 * @Description:采集器存取方法定义
 * @Date: Created in 17:08 2022/9/21
 * @Modified By:
 */
@Component
public class ControllerCache {
    @Resource
    private RedisCache redisCache;
    @Resource
    private ControllerMapper controllerMapper;
    @Resource
    private ControllerMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(ControllerCache.class);


    /**
     * @Description: 根据采集器ip获取采集器信息
     * @auther: wanghongjie
     * @date: 17:24 2022/9/21
     * @param:
     * @return:
     */
    public Controller getDdcByChannelId(String ip) {
        /******************qindehua 增加缓存为空 查询数据库逻辑*******************************/
        //判断是否已查询过数据库
        boolean bool=true;

        Map<String, Controller> controllerList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);

        if (controllerList == null || controllerList.size() == 0) {
            List<Controller> controllers = mapper.selectControllerList(null);
            if (CollectionUtils.isNotEmpty(controllers)) {
                bool=false;
                controllers.forEach(item -> {
                    controllerList.put(String.valueOf(item.getDeviceTreeId()), item);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) item.getDeviceTreeId(), item);
                });
            } else {
                return null;
            }
        }

        for (Controller controller : controllerList.values()) {

            String channelId = controller.getIp();

            if (ip.equals(channelId)) {
                return controller;
            }
        }
        if (bool){
            Controller controller = mapper.selectControllerByIp(ip);
            if (controller == null) {
                return null;
            } else {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);
                return controller;
            }
        }

        return null;
    }

    /**
     * 从缓存中获取控制器   通过设备树id
     *
     * @param deviceTreeId 设备树id
     * @return {@code Controller }
     * @Author qindehua
     * @Date 2022/12/23
     **/
    public Controller getControllerByDeviceTreeId(Long deviceTreeId){
        if (deviceTreeId==null){
            log.warn("deviceTreeId为空！");
            return null;
        }

        //先在缓存数据中查出数据
        Controller controller = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, deviceTreeId);
        if (controller == null) {
            Controller dataMapper = controllerMapper.selectControllerInfoByDeviceTreeId(deviceTreeId);
            if(dataMapper==null){
                log.warn("树ID为："+deviceTreeId+"的控制器 未找到！");
                return null;
            }else {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller,(long)dataMapper.getDeviceTreeId(),dataMapper);
                return dataMapper;
            }
        }
        return controller;
    }
}
