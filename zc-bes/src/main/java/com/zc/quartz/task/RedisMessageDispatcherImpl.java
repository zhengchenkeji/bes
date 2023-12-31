package com.zc.quartz.task;


import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DictUtils;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisChannelConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.redis.pubsub.RedisMessageDispatcher;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.business.dto.ddc.PointParamDDC;
import com.zc.connect.business.dto.edc.AmmeterData;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.EnergyCollectHandler;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import org.springframework.data.redis.connection.Message;
import java.util.*;

/**
 * @Author:gaojikun
 * @Date:2023-02-22 10:12
 * @Description:
 */
@AutoService(RedisMessageDispatcher.class)
public class RedisMessageDispatcherImpl implements RedisMessageDispatcher {
    //redis
    private RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);
    //Module  Point
    private ModuleAndPointCache moduleAndPointCache = ApplicationContextProvider.getBean(ModuleAndPointCache.class);
    //DeviceTree
    private DeviceTreeCache deviceTreeCache = ApplicationContextProvider.getBean(DeviceTreeCache.class);

    //订阅通道对比 gaojikun
    @Override
    public boolean isChannelExist(String channel) {
        if (RedisChannelConstants.Meter_PUB_SUB_INFO.equals(channel)) {
            return true;
        } else {
            return false;
        }
    }

    //订阅业务层处理 gaojikun
    @Override
    public void onMessage(Message message, byte[] pattern) {
        Gson gson = new Gson();
        ReceiveMsg<Object> msgObject = gson.fromJson(message.toString(),new TypeToken<ReceiveMsg<Object>>() {}.getType());
        //判断电表类型
        if(msgObject.getIndex() == DDCCmd.POINT_PARAM_GET){
            //点位电表
            ReceiveMsg<PointParamDDC> msg = gson.fromJson(message.toString(),new TypeToken<ReceiveMsg<PointParamDDC>>() {}.getType());
            String ip = msg.getIp();
            PointParamDDC pointParamDDC = msg.getData();
            List<Map> dataList = new ArrayList<>();
            Integer id = pointParamDDC.getId();//id
            Integer value = 0;//实时值

            //根据ip查询当前DDC控制器点是否存在
            Controller controller = new Controller();
            Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
            Collection<Controller> controllerValues = controllerCacheMap.values();
            for (Controller c : controllerValues) {
                if (c.getIp().equals(ip)) {
                    controller = c;
                    break;
                }
            }

            if(controller == null){
                System.out.println("ERROR***************************************:定时任务订阅消息时,缓存未获取到Controller信息:"+ip);
                return;
            }

            //根据ip、id查询当前点位是否存在
            Point besSbPzStruct = new Point();
            Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
            Collection<Point> pointValues = pointCacheMap.values();
            for (Point p : pointValues) {
                if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                    besSbPzStruct = p;
                    break;
                }
            }

            if(controller == null){
                System.out.println("ERROR***************************************:定时任务订阅消息时,缓存未获取到Point信息:EID = "+id);
                return;
            }

            /** 虚点**/
            if (DeviceTreeConstants.BES_VPOINT == Integer.parseInt(besSbPzStruct.getNodeType())) {
                Point besVirtualPoint = moduleAndPointCache.getPointByDeviceId(besSbPzStruct.getTreeId());
                if (null == besVirtualPoint) {
                    return;
                }
                String pointType = besVirtualPoint.getVpointType();
                if (pointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))
                        || pointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {
                    String values = "";
                    if (besVirtualPoint.getAccuracy() != null && besVirtualPoint.getAccuracy() != 0) {
                        Integer accuracyNum = Integer.parseInt(String.valueOf(besVirtualPoint.getAccuracy()));//获取精度
                        Double valueDouble = value / Math.pow(10, accuracyNum);//获取精度转换后的实时值
                        values = subZeroAndDot(String.valueOf(valueDouble));
                    } else {
                        values = value.toString();
                    }
                    // 更新缓存
                    besSbPzStruct.setRunVal(values);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);
                    if (pointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))
                            && "1".equals(besVirtualPoint.getEnergyStatics().toString())) {

                        //获取电表数据
                        AthenaElectricMeter meter = new AthenaElectricMeter();
                        Collection<Object> meterValues = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter).values();
                        for (Object obj : meterValues) {
                            AthenaElectricMeter meterObj = (AthenaElectricMeter) obj;
                            if (besVirtualPoint.getTreeId().equals(meterObj.getDeviceTreeId())) {
                                meter = meterObj;
                                break;
                            }
                        }
                        if (meter.getMeterId() == null) {
                            System.out.println("ERROR***************************************:定时任务订阅消息时,缓存未获取到Meter信息:"+besVirtualPoint.getTreeId());
                            return;
                        }
                        Map<String, Object> energyData = new HashMap<>();
                        energyData.put("energyCode", besVirtualPoint.getEnergyCode());
                        energyData.put("sysName", besVirtualPoint.getSysName());
                        energyData.put("meterId", meter.getMeterId());
                        energyData.put("sysNameOld", "");
                        energyData.put("date", values);
                        DeviceTree deviceTreePark = deviceTreeCache.getDeviceTreeByDeviceTreeId(besVirtualPoint.getTreeId());
                        energyData.put("park", deviceTreePark.getPark());
                        dataList.add(energyData);
                    }
                }
            }
            /** 实点**/
            else {
                if (DeviceTreeConstants.BES_AI == Integer.parseInt(besSbPzStruct.getNodeType())
                        || DeviceTreeConstants.BES_AO == Integer.parseInt(besSbPzStruct.getNodeType())) {
                    String values = "";
                    if (besSbPzStruct.getAccuracy() != null && besSbPzStruct.getAccuracy() != 0) {
                        Integer accuracyNum = Integer.parseInt(String.valueOf(besSbPzStruct.getAccuracy()));//获取精度
                        Double valueDouble = value / Math.pow(10, accuracyNum);//获取精度转换后的实时值
                        values = subZeroAndDot(String.valueOf(valueDouble));
                    } else {
                        values = value.toString();
                    }
                    // 更新缓存
                    besSbPzStruct.setRunVal(values);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);
                    Point besAiPoint = null;
                    Point besAoPoint = null;
                    if (DeviceTreeConstants.BES_AI == Integer.parseInt(besSbPzStruct.getNodeType())) {
                        besAiPoint = moduleAndPointCache.getPointByDeviceId(besSbPzStruct.getTreeId());
                        String energystatics = besAiPoint.getEnergyStatics().toString();
                        if ("1".equals(energystatics)) {

                            //获取电表数据
                            AthenaElectricMeter meter = new AthenaElectricMeter();
                            Collection<Object> meterValues = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter).values();
                            for (Object obj : meterValues) {
                                AthenaElectricMeter meterObj = (AthenaElectricMeter) obj;
                                if (besAiPoint.getTreeId().equals(meterObj.getDeviceTreeId())) {
                                    meter = meterObj;
                                    break;
                                }
                            }
                            if (meter.getMeterId() == null) {
                                System.out.println("ERROR***************************************:定时任务订阅消息时,缓存未获取到Meter信息:"+besAiPoint.getTreeId());
                                return;
                            }
                            Map<String, Object> energyData = new HashMap<>();
                            energyData.put("energyCode", besAiPoint.getEnergyCode());
                            energyData.put("sysName", besAiPoint.getSysName());
                            energyData.put("meterId", meter.getMeterId());
                            energyData.put("sysNameOld", "");
                            energyData.put("date", values);
                            DeviceTree deviceTreePark = deviceTreeCache.getDeviceTreeByDeviceTreeId(besAiPoint.getTreeId());
                            energyData.put("park", deviceTreePark.getPark());
                            dataList.add(energyData);
                        }
                    } else {
                        besAoPoint = moduleAndPointCache.getPointByDeviceId(besSbPzStruct.getTreeId());
                        String energystatics = besAoPoint.getEnergyStatics().toString();
                        if ("1".equals(energystatics)) {

                            //获取电表数据
                            AthenaElectricMeter meter = new AthenaElectricMeter();
                            Collection<Object> meterValues = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter).values();
                            for (Object obj : meterValues) {
                                AthenaElectricMeter meterObj = (AthenaElectricMeter) obj;
                                if (besAoPoint.getTreeId().equals(meterObj.getDeviceTreeId())) {
                                    meter = meterObj;
                                    break;
                                }
                            }
                            if (meter.getMeterId() == null) {
                                System.out.println("ERROR***************************************:定时任务订阅消息时,缓存未获取到Meter信息:"+besAiPoint.getTreeId());
                                return;
                            }
                            Map<String, Object> energyData = new HashMap<>();
                            energyData.put("energyCode", besAoPoint.getEnergyCode());
                            energyData.put("sysName", besAoPoint.getSysName());
                            energyData.put("meterId", meter.getMeterId());
                            energyData.put("sysNameOld", "");
                            energyData.put("date", values);
                            DeviceTree deviceTreePark = deviceTreeCache.getDeviceTreeByDeviceTreeId(besAoPoint.getTreeId());
                            energyData.put("park", deviceTreePark.getPark());
                            dataList.add(energyData);
                        }
                    }
                }
            }
            // 存储虚点能耗数据
            if (!dataList.isEmpty()) {
                EnergyCollectHandler.ammeterDataHandle(dataList, ip);
            }
        }else{
            //组装数据
            ReceiveMsg<AmmeterData> msgNoList = gson.fromJson(message.toString(),new TypeToken<ReceiveMsg<AmmeterData>>() {}.getType());
            AmmeterData ammeterData  = msgNoList.getData();
            List<AmmeterData> data = new ArrayList<AmmeterData>(){{add(ammeterData);}};
            ReceiveMsg<List<AmmeterData>> msg = new ReceiveMsg<List<AmmeterData>>();
            msg.setIp(msgNoList.getIp());
            msg.setIndex(msgNoList.getIndex());
            msg.setData(data);
            msg.setCode(msgNoList.getCode());
            EnergyCollectHandler.ammeterDataHandle(msg);
        }

    }

    /**
     * 使用正则表达式去掉多余的.与0 gaojikun
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;

    }
}
