package com.zc.efounder.JEnterprise;

import com.ruoyi.common.core.redis.RedisCache;

import com.zc.common.constant.RedisKeyConstants;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.business.dto.edc.AmmeterData;
import com.zc.efounder.JEnterprise.Cache.ControllerCache;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.commhandler.AlarmHandler;
import com.zc.efounder.JEnterprise.mapper.deviceTree.DeviceTreeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableScheduling
@Configuration
public class SchedulingTest {
    private static final Logger log = LoggerFactory.getLogger(AlarmHandler.class);

    @Autowired
    private RedisCache redisCache;
    @Resource
    private ControllerCache controllerCache;
    @Resource
    private DeviceTreeCache deviceTreeCache;
    @Resource
    private DeviceTreeMapper deviceTreeMapper;


    /**
     * 初始化缓存
     *
     * @Author qindehua
     * @Date 2022/10/24
     **/
    @PostConstruct
    public void init() {
        Map<String, Map<String,Object>> originalCache1=new HashMap<>();
        Map<String, Object> dataMap1 = new HashMap<>();
        dataMap1.put("electricId","1000005");
        dataMap1.put("meterId",185);
        dataMap1.put("data",7.0);
        originalCache1.put("1000005",dataMap1);
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData,"185",originalCache1);

        Map<String, Map<String,Object>> originalCache2=new HashMap<>();
        Map<String, Object> dataMap2 = new HashMap<>();
        dataMap2.put("electricId","1000005");
        dataMap2.put("meterId",189);
        dataMap2.put("data",80.0);
        originalCache2.put("1000005",dataMap2);
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData,"189",originalCache2);
    }

    public void test(){
        System.out.println("===================================");
    }


    /**
     * 每分钟执行一次。
     */
//    @Scheduled(cron = "0 */1 * * * ?")
    public void runScheduleCron() {
//        System.out.println("==========================================");
//        DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, 4572L);
//        if (deviceTree==null){
//            DeviceTree dataMapper=deviceTreeMapper.selectDeviceTreeRedisById(4572);
//            if (dataMapper==null){
//                return;
//            }else {
//                deviceTree=dataMapper;
//                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) dataMapper.getDeviceTreeId(), dataMapper);
//            }
//        }
//        System.out.println(deviceTree);
//        controllerCache.getDdcByChannelId("10.168.136.28");
//        deviceTreeCache.getCascadeSubordinate(3281);
//        System.out.println(deviceTreeCache.getCascadeSubordinate(3281).toString());
        AlarmHandler alarmHandler=new AlarmHandler();
        ReceiveMsg<List<AmmeterData>> msg=new ReceiveMsg<>();
        AmmeterData ammeterData1=new AmmeterData();
        ammeterData1.setMeterID(185);
        ammeterData1.setCollectCount(1);
        ammeterData1.setElectricData("1000005");
        ammeterData1.setElectricData("18");
        AmmeterData ammeterData2=new AmmeterData();
//        ammeterData2.setMeterID(185);
//        ammeterData2.setCollectCount(1);
//        ammeterData2.setElectricData("1000003,1000002,1000005");
        List<AmmeterData> list=new ArrayList<>();
        list.add(ammeterData1);
//        list.add(ammeterData2);
        msg.setData(list);




        alarmHandler.alarmHandle(msg);
//        Point point=new Point();
//        point.setTreeId(4601L);
//        point.setEnergyCode("01000");
//        alarmHandler.PointAlarmHandleAI(point,false,60);
    }

}

