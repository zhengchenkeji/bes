package com.zc.efounder.JEnterprise.commhandler;

import com.google.auto.service.AutoService;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.constant.WebSocketEvent;
import com.zc.common.core.rabbitMQ.MessagingService;
import com.zc.common.core.websocket.WebSocketService;
import com.zc.connect.business.bo.ChannelTypeState;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.business.handler.ModbusMsgReceive;
import com.zc.efounder.JEnterprise.Cache.IotEquipmentCache;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.baseData.WarnItemData;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmRealtimeData;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmWorkOrder;
import com.zc.efounder.JEnterprise.mapper.baseData.EquipmentMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.ParkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmRealtimeDataMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmWorkOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 11:19 2023/2/22
 * @Modified By:
 */
@AutoService(ModbusMsgReceive.class)
public class ModbusClientEventCallback implements ModbusMsgReceive {

    // 客户端状态存储
    public static final Map<String, ChannelTypeState> stateStore = new HashMap<>();

    private MessagingService messagingService = ApplicationContextProvider.getBean(MessagingService.class);

    //redis
    private RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);

    //IotEquipmentCache
    private IotEquipmentCache iotEquipmentCache = ApplicationContextProvider.getBean(IotEquipmentCache.class);

    private static final Logger log = LoggerFactory.getLogger(ModbusClientEventCallback.class);
    //设备mapper
    private EquipmentMapper equipmentMapper = ApplicationContextProvider.getBean(EquipmentMapper.class);
    //实时报警mapper
    private AlarmRealtimeDataMapper alarmRealtimeDataMapper = ApplicationContextProvider.getBean(AlarmRealtimeDataMapper.class);
    //报警工单mapper
    private AlarmWorkOrderMapper alarmWorkOrderMapper = ApplicationContextProvider.getBean(AlarmWorkOrderMapper.class);
    //园区mapper
    private ParkMapper parkMapper = ApplicationContextProvider.getBean(ParkMapper.class);

    //上线离线处理
    @Override
    public void controllerState(String ip, int post, Boolean state) {

        if (!StringUtils.hasText(ip)) {
            log.warn("ip 地址不存在");
            return;
        }

        ChannelTypeState channelTypeState = stateStore.get(ip);

        if (null == channelTypeState) {
            channelTypeState = new ChannelTypeState();
        }

        try {

            Map<String, Equipment> equipmentMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

            if (equipmentMap == null || equipmentMap.size() == 0) {
                return;
            }

            equipmentMap.values().forEach(val -> {
                //获取ip
                String deviceIp = val.getIpAddress();
                //获取端口
                String devicePost = val.getPortNum();

                Long id = null;

                if (ip.equals(deviceIp) && String.valueOf(post).equals(devicePost)) {

                    id = val.getId();

                    String deviceState = val.getState();
                    //获取当前ip状态
                    if (!state && deviceState.equals("1")) {//"0"离线  "1" 在线
                        val.setState("0");
                    }


                    if (state && deviceState.equals("0")) {//
                        val.setState("1");

                    }

                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, val.getId(), val);

                }
                if (id == null) {
                    return;
                }

                if (!state) {//如果网关离线,则子设备全部离线

                    List<Equipment> equipmentList = iotEquipmentCache.getEquipmentListById(id);
                    if (equipmentList == null) {
                        return;
                    }

                    equipmentList.forEach(val1 -> {
                        val1.setState("0");
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, val1.getId(), val1);
                        ReceiveMsg<Equipment> msg = new ReceiveMsg();
                        msg.setIndex(1);//获取模块实时在线离线状态
                        msg.setIp(ip);
                        msg.setPost(post);
                        msg.setCode(0);
                        msg.setData(val1);

                        // 推送消息到web客户端
                        WebSocketService.broadcast(WebSocketEvent.MODBUS_DEVICE, msg);
                    });
                }
            });

            ReceiveMsg<List<Equipment>> msg = new ReceiveMsg();
            msg.setIp(ip);
            msg.setPost(post);
            msg.setCode(0);

            // 0：离线；1：在线
            Integer onlineStatus = 0;

            channelTypeState.setState(state);

            if (state) {
                onlineStatus = 1;
                msg.setCode(1);
            }

            // 推送消息到web客户端
            WebSocketService.broadcast(WebSocketEvent.MODBUS_SERVER_STATE, msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设备在线离线
    @Override
    public void deviceState(String ip, int post, Long id, Boolean state) {
        if (!StringUtils.hasText(ip)) {
            log.warn("ip 地址不存在");
            return;
        }

        try {

            Map<String, Equipment> controllerList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

            if (controllerList == null || controllerList.size() == 0) {
                return;
            }
            controllerList.values().forEach(val -> {

                if (val.getId().equals(id)) {

                    String deviceState = val.getState();

                    if (!state && deviceState.equals("1")) {//"0"离线  "1" 在线
                        val.setState("0");
                        if ("1".equals(val.getOfflineAlarm())) {
//                            //是设备离线报警 则添加报警记录
                            Date nowDate = new Date();
                            Integer countNumber = 1;
                            String controllName = "";
                            if(val.getpId() != null){
                                Equipment cacheMapValue = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, val.getpId());
                                controllName = cacheMapValue.getName();
                            }
                            //是否由设备离线报警记录
                            AlarmRealtimeData alarmRealtimeDataSelect = new AlarmRealtimeData(null,  controllName + ":" + val.getName() , "1", "5");
                            alarmRealtimeDataSelect.setEquipmentId(val.getId());
                            List<AlarmRealtimeData> alarmRealtimeDataCount = alarmRealtimeDataMapper.selectAlarmRealtimeDataStateList(alarmRealtimeDataSelect);
                            if (alarmRealtimeDataCount == null || alarmRealtimeDataCount.size() == 0) {
                                //告警策略ID 告警策略Name 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型
                                AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData(null, "设备离线报警", "0",
                                        nowDate, nowDate, countNumber, 0L, controllName + ":" + val.getName(), "设备离线报警",
                                        "1", controllName + ":" + val.getName()+",设备离线报警", null, "5");
                                //添加实时报警
                                alarmRealtimeData.setCreateTime(nowDate);
                                alarmRealtimeData.setEquipmentId(val.getId());
                                alarmRealtimeDataMapper.insertAlarmRealtimeData(alarmRealtimeData);
                            }else{
                                countNumber = countNumber + alarmRealtimeDataCount.get(0).getAmount();
                                //告警策略ID 告警策略Name 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型
                                AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData();
                                alarmRealtimeData.setId(alarmRealtimeDataCount.get(0).getId());
                                alarmRealtimeData.setAmount(countNumber);
                                alarmRealtimeData.setLastTime(nowDate);
                                //修改实时报警
                                alarmRealtimeDataMapper.updateAlarmRealtimeDataById(alarmRealtimeData);
                            }

                            //是否是第一次告警工单
                            AlarmWorkOrder alarmWorkOrderSelect = new AlarmWorkOrder(null, controllName + ":" + val.getName() , "1", "5");
                            List<AlarmWorkOrder> alarmWorkOrderCount = alarmWorkOrderMapper.selectAlarmWorkOrderStateList(alarmWorkOrderSelect);

                            if (alarmWorkOrderCount == null || alarmWorkOrderCount.size() == 0) {
                                //根据设备-园区-查出用户
                                String userId = "1";
                                Park park = parkMapper.selectParkByCode(val.getParkCode());
                                if(!"1".equals(park.getUserName())){
                                    userId = userId + "," + park.getUserName();
                                }

                                //告警策略ID 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型 所属用户id 状态
                                AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder(null,"设备离线报警", "0",
                                        nowDate, nowDate, 1, 0L, controllName + ":" + val.getName(), "设备离线报警",
                                        "1", controllName + ":" + val.getName()+",设备离线报警", null, "5", userId, "0");

                                //添加告警工单
                                alarmWorkOrder.setCreateTime(nowDate);
                                alarmWorkOrder.setEquipmentId(val.getId());
                                alarmWorkOrderMapper.insertAlarmWorkOrder(alarmWorkOrder);
                            } else {
                                //告警策略ID 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型 所属用户id 状态
                                AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder();
                                alarmWorkOrder.setId(alarmWorkOrderCount.get(0).getId());
                                alarmWorkOrder.setAmount(1+alarmWorkOrderCount.get(0).getAmount());
                                alarmWorkOrder.setStatus("0");
                                alarmWorkOrder.setLastTime(nowDate);
                                alarmWorkOrder.setRemark("");
                                alarmWorkOrder.setUpdateName(null);
                                alarmWorkOrder.setUpdateCode(null);
                                alarmWorkOrder.setUpdateTime(null);
                                //修改告警工单
                                alarmWorkOrderMapper.updateAlarmWorkOrder(alarmWorkOrder);
                            }


                        }
                        /**场景联动设备触发*/
                        SceneLinkHandler.deviceTrigger(val.getId()+"","1",SceneLinkHandler.SCENE_LINK_DEVICEACTION_DOWN,null);
                    }

                    if (state && deviceState.equals("0")) {//
                        /**场景联动设备触发*/
                        SceneLinkHandler.deviceTrigger(val.getId()+"","1",SceneLinkHandler.SCENE_LINK_DEVICEACTION_UP,null);
                        val.setState("1");

                    }
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, val.getId(), val);

                    ReceiveMsg<Equipment> msg = new ReceiveMsg();
                    msg.setIndex(1);//获取模块实时在线离线状态
                    msg.setIp(ip);
                    msg.setPost(post);
                    msg.setCode(0);
                    msg.setData(val);

                    // 0：离线；1：在线
                    Integer onlineStatus = 0;


                    if (state) {
                        onlineStatus = 1;
                        msg.setCode(1);
                    }

                    // 推送消息到web客户端
                    WebSocketService.broadcast(WebSocketEvent.MODBUS_DEVICE, msg);
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @Description: rabbitMQ消息订阅
     * @auther: wanghongjie
     * @date: 17:52 2023/3/9
     * @param: [s]
     * @return: void
     */
    @Override
    public void sendRegistrationMessage(String s) throws Exception {
//        messagingService.sendRegistrationMessage(s);
        messagingService.sendMsg(s);
    }

    /**
     * @Description: 设备实时数据
     * @auther: wanghongjie
     * @date: 16:01 2023/3/17
     * @param: [productItemData]
     * @return: void
     */
    @Override
    public void deviceRealTimeData(List<ProductItemData> productItemData) {

        if (productItemData == null || productItemData.size() == 0) {
            return;
        }

        ReceiveMsg<List<ProductItemData>> msg = new ReceiveMsg();

        msg.setIndex(2);//设备实时数据
        msg.setData(productItemData);

        // 推送消息到web客户端
        WebSocketService.broadcast(WebSocketEvent.MODBUS_DEVICE, msg);
    }

    /**
     * @param b
     * @Description: 指令下发成功后推到前端
     * @auther: wanghongjie
     * @date: 14:12 2023/3/20
     * @param:
     * @return:
     */
    @Override
    public void sendMessageBoolen(boolean b) {
        // 推送消息到web客户端
        ReceiveMsg msg = new ReceiveMsg();
        msg.setIndex(3);
        msg.setCode(0);
        WebSocketService.broadcast(WebSocketEvent.MODBUS_DEVICE, msg);
    }

    /**
     * @Description: 获取数据
     * @auther: wanghongjie
     * @date: 16:45 2023/3/25
     * @param: [host, post, msg]
     * @return: void
     */
    @Override
    public void getMassgaeState(String event, ReceiveMsg<List<PointDataResponse>> msg) {

        if (event == null || msg == null) {
            return;
        }

        List<PointDataResponse> pointDataResponseList = msg.getData();

        Map<String, List<PointDataResponse>> resultMap = new HashMap<>();

        for (int i = 0; i < pointDataResponseList.size(); i++) {
            PointDataResponse pointDataResponse = pointDataResponseList.get(i);
            String id = pointDataResponse.getModbusId();
            Set<String> token = redisCache.getCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, id);

            if (token == null) {
//                System.out.println("token为空，没有走websoket推送消息——————————————————>"+id);
                continue;
            }

            token.forEach(tokenMsg -> {
                List<PointDataResponse> pointDataResponseList1 = resultMap.computeIfAbsent(tokenMsg, k -> new ArrayList<>());
                pointDataResponseList1.add(pointDataResponse);
            });
        }

        resultMap.forEach((token, pointDataResponseList1) -> {
            msg.setData(pointDataResponseList1);
//            System.out.println("token不为空，走websoket推送消息——————————————————>"+msg);
            WebSocketService.postEvent(token, event, msg);
        });

        // 推送消息到web客户端
//        ReceiveMsg receiveMsg = new ReceiveMsg();
//        receiveMsg.setIndex(4);
//        receiveMsg.setData(msg);
//        receiveMsg.setCode(0);
//        WebSocketService.broadcast(WebSocketEvent.MODBUS_DEVICE, receiveMsg);
    }


    /**
     * 实时数据分发到客户端
     *
     * @param msg
     */
    public void distributePostEvent(String event, ReceiveMsg<List<PointDataResponse>> msg) {
        if (event == null || msg == null) {
            return;
        }

        List<PointDataResponse> pointDataResponseList = msg.getData();

        Map<String, List<PointDataResponse>> resultMap = new HashMap<>();

        for (int i = 0; i < pointDataResponseList.size(); i++) {
            PointDataResponse pointDataResponse = pointDataResponseList.get(i);
            Integer id = pointDataResponse.getId();
            Set<String> token = redisCache.getCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, String.valueOf(id));

            if (token == null) {
                continue;
            }

            token.forEach(tokenMsg -> {
                List<PointDataResponse> pointDataResponseList1 = resultMap.computeIfAbsent(tokenMsg, k -> new ArrayList<>());
                pointDataResponseList1.add(pointDataResponse);
            });
        }

        resultMap.forEach((token, pointDataResponseList1) -> {
            msg.setData(pointDataResponseList1);
            WebSocketService.postEvent(token, event, msg);
        });

    }

}
