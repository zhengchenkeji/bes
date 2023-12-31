package com.zc.efounder.JEnterprise.commhandler;

import com.google.auto.service.AutoService;
import com.zc.ApplicationContextProvider;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.zc.efounder.JEnterprise.Cache.*;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.mapper.deviceTree.AthenaElectricMeterMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ControllerMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ModuleMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.PointMapper;
import com.zc.efounder.JEnterprise.mapper.scheduling.PlanConfigMapper;
import com.zc.efounder.JEnterprise.service.deviceTree.impl.AthenaElectricMeterServiceImpl;
import com.zc.efounder.JEnterprise.service.deviceTree.impl.ControllerServiceImpl;
import com.zc.efounder.JEnterprise.service.deviceTree.impl.ModuleServiceImpl;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.constant.WebSocketEvent;
import com.zc.common.core.websocket.WebSocketService;
import com.zc.connect.business.bo.ChannelTypeState;
import com.zc.connect.business.constant.*;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.business.dto.TimeData;
import com.zc.connect.business.dto.ddc.*;
import com.zc.connect.business.dto.edc.*;
import com.zc.connect.business.dto.ldc.*;
import com.zc.connect.business.handler.ClientMsgReceive;
import com.zc.connect.business.handler.SendMsgHandler;
import com.zc.connect.client.util.DataUtil;
import com.zc.connect.util.MsgUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

//import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 14:55 2022/7/22
 * @Modified By:
 */
@AutoService(ClientMsgReceive.class)
public class ClientEventCallback implements ClientMsgReceive {

    // 客户端状态存储
    public static final Map<String, ChannelTypeState> clientStateStore = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(ClientEventCallback.class);

    // 设备树缓存定义
    private DeviceTreeCache deviceTreeCache = ApplicationContextProvider.getBean(DeviceTreeCache.class);

    // 采集器缓存定义
    private ControllerCache controllerCache = ApplicationContextProvider.getBean(ControllerCache.class);

    //redis
    private RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);
    /**
     * qindehua 控制器
     */
    private ControllerMapper controllerMapper = ApplicationContextProvider.getBean(ControllerMapper.class);

    /**
     * qindehua 模块 模块点缓存
     */
    private ModuleAndPointCache moduleAndPointCache = ApplicationContextProvider.getBean(ModuleAndPointCache.class);

    /**
     * qindehua 模块
     */
    private ModuleServiceImpl moduleServiceImpl = ApplicationContextProvider.getBean(ModuleServiceImpl.class);
    /**
     * qindehua 电表
     */
    private AthenaElectricMeterMapper meterMapper = ApplicationContextProvider.getBean(AthenaElectricMeterMapper.class);
    /**
     * qindehua 模块
     */
    private ModuleMapper moduleMapper = ApplicationContextProvider.getBean(ModuleMapper.class);
    /**
     * qindehua 计划
     */
    private PlanConfigMapper planConfigMapper = ApplicationContextProvider.getBean(PlanConfigMapper.class);
    /**
     * qindehua 点位mapper
     */
    private PointMapper pointMapper = ApplicationContextProvider.getBean(PointMapper.class);
    /**
     * qindehua 电表 service
     */
    private AthenaElectricMeterServiceImpl meterServiceImpl = ApplicationContextProvider.
            getBean(AthenaElectricMeterServiceImpl.class);

    //控制器实现类
    private ControllerServiceImpl controllerServiceImpl = ApplicationContextProvider.getBean(ControllerServiceImpl.class);

    //电表缓存
    private MeterCache meterCache = ApplicationContextProvider.getBean(MeterCache.class);

    //电能参数采集方案关系缓存
    private ElectricCollRlglCache electricCollRlglCache = ApplicationContextProvider.getBean(ElectricCollRlglCache.class);

    //采集参数缓存
    private ElectricParamsCache electricParamsCache = ApplicationContextProvider.getBean(ElectricParamsCache.class);

    //电表 mapper
    private AthenaElectricMeterMapper athenaElectricMeterMapper = ApplicationContextProvider.getBean(AthenaElectricMeterMapper.class);

    private AlarmHandler alarmHandler = new AlarmHandler();

//    private PlanningHandler planningHandler = new PlanningHandler();
//
//    private FunctionPointStateServiceImpl functionPointStateService =  ApplicationContextProvider.getBean(FunctionPointStateServiceImpl.class);


    @Override
    public void controllerState(String ip, Boolean state) {
        if (!StringUtils.hasText(ip)) {
            log.warn("ip 地址不存在");
            return;
        }

        ChannelTypeState channelTypeState = clientStateStore.get(ip);

        if (null == channelTypeState) {
            channelTypeState = new ChannelTypeState();
        }
        try {

            Controller controller = controllerCache.getDdcByChannelId(ip);

            if (controller == null) {
                return;
            }

            ReceiveMsg<List<DeviceTree>> msg = new ReceiveMsg();
            msg.setIp(ip);
            msg.setCode(0);

            // 0：离线；1：在线
            Integer onlineStatus = 0;

            channelTypeState.setState(state);

            if (state) {
                onlineStatus = 1;
                msg.setCode(1);
            }
            if (controller != null) {

                if (controller.getType() == DeviceTreeConstants.BES_DDCNODE) {//DDC节点

                    // 设置控制器的时间（DDC）
                    if (state) {
                        SendMsgHandler.setControllerTimeDDC(ip, DataUtil.getTimeDataObject());
                    }

                    channelTypeState.setType(ChannelType.DDC);

                } else if (controller.getType() == DeviceTreeConstants.BES_ILLUMINE) {//照明节点

                    // 设置IP路由器的时间（LDC）
                    if (state) {
                        SendMsgHandler.setControllerTimeLDC(ip, DataUtil.getTimeDataObject());
                    }

                    channelTypeState.setType(ChannelType.LDC);

                } else if (controller.getType() == DeviceTreeConstants.BES_COLLECTORNODE) {//能耗节点

                    // 设置控制器的时间（能耗）
                    if (state) {
                        SendMsgHandler.setControllerTimeEDC(ip, DataUtil.getTimeDataObject());
                    }


                    channelTypeState.setType(ChannelType.EDC);
                }

                // 把在线状态保存到 map
                clientStateStore.put(ip, channelTypeState);
                // 更新能耗采集器在线状态
                controller.setOnlineState(onlineStatus);

                //获取当前设备树节点下的所有子节点
                List<DeviceTree> deviceTreeList = deviceTreeCache.getCascadeSubordinate(controller.getDeviceTreeId());

                // 更新缓存设备树在线状态
                Integer finalOnlineStatus = onlineStatus;
                deviceTreeList.forEach(item -> {
                    item.setDeviceTreeStatus(finalOnlineStatus);

                    //更新相应缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) item.getDeviceTreeId(), item);

                });
                // 更新采集器在线状态
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);

                msg.setData(deviceTreeList);

            }
            // 推送消息到web客户端
            WebSocketService.broadcast(WebSocketEvent.DEVICE_STATE, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @Override
    public void heartbeatCallback(String ip) {
        if (!StringUtils.hasText(ip)) {
            log.warn("心跳回调函数，ip 地址不存在");
            return;
        }

        ChannelTypeState channelTypeState = clientStateStore.get(ip);

        if (null == channelTypeState || !channelTypeState.getState()) {
            controllerState(ip, true);
        }
    }

    /***************************************** EDC (能耗) *******************************************/

    // 能耗采集器远程升级响应回调
    @Override
    public void remoteUpgradeEDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (Code.SUCCEED.equals(msg.getCode())) {
            Channel channel = MsgUtil.getChannelByTokenSN(ip);
            channel.close();
        }

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("远程升级响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("远程升级响应回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.EDC, msg);
    }

    /**
     * 添加控制器响应回调
     * 1、更新数据库能耗采集器同步状态
     * 2、推送消息到web客户端
     *
     * @param msg
     */
    @Override
    public void controllerAddEDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("添加控制器响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("添加控制器响应回调，sessionId 不存在");
            return;
        }

        // 同步状态 0 未同步 1 已同步
        Integer syncState = 0;

        // 把添加控制器下位机返回的消息推送到前端页面
        WebSocketService.postEvent(sessionId, WebSocketEvent.EDC, msg);

        // 消息返回成功则同步成功否则同步失败
        if (Code.SUCCEED.equals(msg.getCode())) {
            syncState = 1;
            controllerState(ip, true);
        }

        // 根据channelId地址查询缓存数据是否存在
        Controller controller = controllerCache.getDdcByChannelId(msg.getIp());

        if (null == controller) {
            return;
        }

        controller.setSynchState(syncState);
        // 能耗采集器为在线状态
        controller.setOnlineState(1);

        // 更新采集器在线以及同步状态
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);


        // 更新控制器的同步状态
        controllerServiceImpl.updateController(controller);
    }

    /**
     * 设置控制器的所有参数响应回调
     * 1、更新数据库能耗采集器同步状态
     * 2、推送消息到web客户端
     *
     * @param msg
     */
    @Override
    public void controllerSetEDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置控制器的所有参数回调，index 不存在，或者 ip 不存在");
            return;
        }


        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            // 把添加控制器下位机返回的消息推送到前端页面
            WebSocketService.postEvent(sessionId, WebSocketEvent.EDC, msg);
        }

        // 同步状态 0 未同步 1 已同步
        int syncState = 0;
        // 消息返回成功则同步成功否则同步失败
        if (Code.SUCCEED.equals(msg.getCode())) {
            syncState = 1;
        }
        // 更新在线状态（重设 ip 需要更新在线状态）
        ChannelTypeState channelTypeState = clientStateStore.get(ip);

        if (null == channelTypeState || !channelTypeState.getState()) {
            controllerState(ip, true);
        }

        /**根据channelId地址查询缓存数据是否存在*/
        Controller controller = controllerCache.getDdcByChannelId(ip);
        if (controller == null) {
            return;
        }

        /**同步状态*/
        controller.setSynchState(syncState);

        /**在线状态*/
        controller.setOnlineState(1);

        /**更新控制器的同步状态 没有同步成功直接返回*/
        if (!(controllerMapper.updateSynchState(controller))) {
            return;
        }

        // 所有操作成功后  最后更新控制器的  同步状态
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);

        // 所有操作成功后  最后更新树状态
        controllerState(ip, true);

        /**只有进行批量同步时    才进行以下操作*/
        if (controller.getSynchronize()) {
            /**获取设备树  下级级联信息*/
            List<DeviceTree> deviceTrees = deviceTreeCache.getCascadeSubordinate(controller.getDeviceTreeId());
            /**判断是否为空*/
            if (null == deviceTrees || deviceTrees.isEmpty()) {
                return;
            }
            for (DeviceTree deviceTree : deviceTrees) {
                int type = deviceTree.getDeviceNodeId();
                /**判断是否为电表类型*/
                if (type == DeviceTreeConstants.BES_AMMETER) {
                    /**过滤电表同步电表*/
                    AthenaElectricMeter meter = meterCache.getMeterByDeviceId((long) deviceTree.getDeviceTreeId());
                    if (meter == null) {
                        continue;
                    }
                    meterServiceImpl.syncMeter(meter);
                }

            }
        }


    }

    // 获取控制器的所有配置参数响应回调
    @Override
    public void controllerGetEDC(ReceiveMsg<ControllerDataEDC> controllerData) {
        /**qindehua */
        Integer index = controllerData.getIndex();
        String ip = controllerData.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取控制器的所有配置参数响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取控制器的所有配置参数响应回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.EDC, controllerData);
    }

    // 设置控制器的时间响应回调
    @Override
    public void controllerTimeSetEDC(ReceiveMsg msg) {
        /**qindehua**/
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置控制器的时间回调，index 不存在，或者 ip 不存在");
            return;
        }

        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("设置控制器的时间回调，sessionId 不存在");
            return;
        }
        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.EDC, msg);
    }

    // 获取控制器的时间响应回调
    @Override
    public void controllerTimeGetEDC(ReceiveMsg<TimeData> msg) {
        /**qindehua**/
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取控制器的时间回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取控制器的时间回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.EDC, msg);
    }

    // 重启控制器响应回调，相当于重启复位
    @Override
    public void controllerRestartEDC(ReceiveMsg msg) {
        /***qindehua */
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("重启控制器，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            log.warn("重启控制器，sessionId 不存在");
            // 推送消息到web客户端
            WebSocketService.postEvent(sessionId, WebSocketEvent.EDC, msg);
        }
        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            return;
        }

        // 根据channelId地址查询缓存数据是否存在
        Controller controller = controllerCache.getDdcByChannelId(ip);

        if (null == controller) {
            return;
        }

        String fIp = controller.getIp();
//        String fChannelId = controller.getCurrentIp();
//
//        /** 更新通信IP*/
//        if (StringUtils.hasText(fChannelId)
//                && StringUtils.hasText(fIp)
//                && !fIp.equals(fChannelId)) {
//            controller.setCurrentIp(fIp);
//            /** 更新数据库中的通信IP*/
//            if (!controllerMapper.updateController(controller)) {
//                log.warn("数据更新失败！");
//                return;
//            }
//            /******更新缓存数据*****/
//            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);
//            // 手动更新在线状态
//        }
        controllerState(fIp, false);

        Channel channel = MsgUtil.getChannelByTokenSN(ip);
        channel.close();


    }

    // 重置控制器响应回调，恢复出厂设置，并重启
    @Override
    public void controllerResetEDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("重置控制器，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(token)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(token, WebSocketEvent.EDC, msg);
        }

        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            return;
        }

        // 根据channelId地址查询缓存数据是否存在
        Controller controller = controllerCache.getDdcByChannelId(msg.getIp());

        if (null == controller) {
            return;
        }

        String fIp = controller.getIp();
//        String fChannelId = controller.getCurrentIp();
//
//        if (StringUtils.hasText(fChannelId)
//                && StringUtils.hasText(fIp)
//                && !fIp.equals(fChannelId)) {
//
//            controller.setCurrentIp(fIp);
//            // 手动更新在线状态
//        }
        controllerState(fIp, false);

        // 同步状态 0 未同步 1 已同步
        controller.setSynchState(0);
        /** qindehua 更新数据库中的通信IP*/
        if (!controllerMapper.updateController(controller)) {
            log.warn("数据更新失败！");
            return;
        }
        Channel channel = MsgUtil.getChannelByTokenSN(ip);
        channel.close();


        // 所有操作成功后  最后更新控制器的  同步状态
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);

        List<DeviceTree> deviceTree = deviceTreeCache.getCascadeSubordinate(controller.getDeviceTreeId());

        for (DeviceTree tree : deviceTree) {
            // 过滤电表
            if (DeviceTreeConstants.BES_AMMETER.equals(tree.getDeviceNodeId())) {
                /**缓存取值*/
                AthenaElectricMeter meter = meterCache.getMeterByDeviceId((long) tree.getDeviceTreeId());
                /**值为空不进行同步*/
                if (meter == null) {
                    continue;
                }
                meter.setSynchState(0L);
                // 更新电表同步状态 并更新缓存
                if (meterMapper.updateAthenaElectricMeter(meter)) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, (long) tree.getDeviceTreeId(), meter);
                }
            }
        }
    }

    // 删除一个控制器响应回调，并删除和它相关的电表
    @Override
    public void controllerDeleteEDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除一个控制器响应回调，并删除和它相关的电表回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("删除一个控制器响应回调，并删除和它相关的电表回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.EDC, msg);

    }

    // 新增加一个电表信息响应回调
    @Override
    public void ammeterAddEDC(ReceiveMsg<AmmeterCollectParamData> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("新增加一个电表信息响应回调，index 不存在，或者 ip 不存在");
            return;
        }
        Integer meterID = msg.getData().getMeterID();

        AthenaElectricMeter besAmmeter = meterCache.getMeterByMeterId(meterID);
        DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(besAmmeter.getDeviceTreeId());

        // 1 已同步； 0 未同步
        if (Code.SUCCEED.equals(msg.getCode())) {
            besAmmeter.setSynchState(Long.valueOf("1"));
            besAmmeter.setOnlineState(Long.valueOf("1"));
            deviceTree.setDeviceTreeStatus(1);

            // 新增电表采集方案

            if (null == besAmmeter) {
                return;
            }

            Integer collectMethodID = besAmmeter.getCollectionMethodCode().intValue(); // 采集方案编号

            List<ElectricCollRlgl> electricDataInfo = electricCollRlglCache.getElectricCollRlglByCollId(collectMethodID);

            for (int i = 0; i < electricDataInfo.size(); i++) {

                // 采集参数
                CollectParamData collectParamData = new CollectParamData();

                collectParamData.setMeterID(meterID);

                // 采集方案
                CollectMethod collectMethod = new CollectMethod();

                collectMethod.setCollectMethodID(collectMethodID);
                collectMethod.setCollectCount(i + 1);

                collectParamData.setElectricDataCollectMethod(collectMethod);

                // 电能参数
                ElectricParam electricParam = new ElectricParam();

                ElectricCollRlgl electricData = electricDataInfo.get(i);

                ElectricParams electricParams = electricParamsCache.getCacheByEnergyCode(electricData.getElectricCode());

                try {
                    electricParam.setUnitType(electricParams.getUnit()); // 单位类型
                    electricParam.setPointLocation(electricParams.getPointLocation().intValue()); // 小数点位置 解析规则
                    electricParam.setDataEncodeType(electricParams.getDataEncodeType().intValue()); // 编码规则 BCD编码，或者是10进制编码
                    electricParam.setElectricName(electricParams.getName()); // 能耗名称
                    electricParam.setOffsetAddr(Long.parseLong(electricParams.getOffsetAddress())); // 寄存器偏移地址
                    electricParam.setDataLength(electricParams.getDataLength().intValue()); // 数据长度 字节数
                    electricParam.setElectricID(Integer.parseInt(electricParams.getCode())); // 能耗参数 id
                    electricParam.setDataType(electricParams.getDataType().intValue()); // 数据类型
                    electricParam.setCodeSeq(electricParams.getCodeSeq().intValue()); // 解码顺序
                    electricParam.setMeterID(meterID);

                } catch (Exception e) {
                    log.warn("类型转换异常");
                    e.printStackTrace();
                }

                collectParamData.setElectricDataInfo(electricParam);

                // 添加电能参数到下位机
                SendMsgHandler.addAmmeterCollectionSchemeEDC(ip, collectParamData);
            }

        } else {
            besAmmeter.setSynchState(Long.valueOf("0"));
            besAmmeter.setOnlineState(Long.valueOf("0"));
            deviceTree.setDeviceTreeStatus(0);
        }


        athenaElectricMeterMapper.updateAthenaElectricMeter(besAmmeter);
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, besAmmeter.getDeviceTreeId(), besAmmeter);
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, besAmmeter.getDeviceTreeId(), deviceTree);
        // 获取当前 token
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(token)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(token, WebSocketEvent.EDC, msg);
        }
    }

    // 删除一个电表响应回调
    @Override
    public void ammeterDeleteEDC(ReceiveMsg<AmmeterCollectParamData> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除一个电表响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 token
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(token)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(token, WebSocketEvent.EDC, msg);
        }

    }

    // 设置一个电表的所有参数响应回调
    @Override
    public void ammeterSetEDC(ReceiveMsg<AmmeterCollectParamData> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置一个电表的所有参数响应回调，index 不存在，或者 ip 不存在");
            return;
        }
        Integer meterID = msg.getData().getMeterID();

        AthenaElectricMeter besAmmeter = meterCache.getMeterByMeterId(meterID);

        // 1 已同步； 0 未同步
        if (Code.SUCCEED.equals(msg.getCode())) {
            besAmmeter.setSynchState(Long.valueOf("1"));

        } else {
            besAmmeter.setSynchState(Long.valueOf("0"));
        }

        athenaElectricMeterMapper.updateAthenaElectricMeter(besAmmeter);

        // 获取当前 token
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(token)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(token, WebSocketEvent.EDC, msg);
        }
    }

    // 获取一个电表的所有配置信息响应回调
    @Override
    public void ammeterGetEDC(ReceiveMsg<AmmeterCollectParamData> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取一个电表的所有配置信息响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 token
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(token)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(token, WebSocketEvent.EDC, msg);
        }
    }

    // 新增一个电表的采集方案
    @Override
    public void ammeterCollectionSchemeAddEDC(ReceiveMsg<ElectricParam> msg) {

    }

    // 设置一个电表的采集方案
    @Override
    public void ammeterCollectionSchemeSetEDC(ReceiveMsg<ElectricParam> msg) {

    }

    // 删除一个电表的采集方案
    @Override
    public void ammeterCollectionSchemeDeleteEDC(ReceiveMsg<ElectricParam> msg) {

    }

    // 获取电表实时数据响应回调
    @Override
    public void ammeterRealtimeDataGetEDC(ReceiveMsg<AmmeterData> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取电表实时数据响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 token
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(token)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(token, WebSocketEvent.EDC, msg);
        }

    }

    // 获取历史数据响应回调
    @Override
    public void ammeterHistoryDataGetEDC(ReceiveMsg<AmmeterHistoryData> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取历史数据响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 token
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(token)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(token, WebSocketEvent.EDC, msg);
        }
    }

    // 接收告警数据
    @Override
    public void alarmDataReceiveEDC(ReceiveMsg<List<AlarmData>> msg) {

    }

    // 接收电表实时数据 todo
    @Override
    public void ammeterRealtimeDataReceiveEDC(ReceiveMsg<List<AmmeterData>> msg) {
        if (null == msg) {
            log.warn("接收电表实时数据, 下位机上传的数据能耗数据不存在");
            return;
        }

        List<AmmeterData> data = msg.getData();

        if (data == null || data.isEmpty()) {
            log.warn("接收电表实时数据, 参数 data 不存在");
            return;
        }

        EnergyCollectHandler.ammeterDataHandle(msg);
        // todo 待优化（添加缓存配置减少查表）
        alarmHandler.alarmHandle(msg);//报警
    }

    // 接收电表断点续传数据 todo
    @Override
    public void ammeterResumeDataReceiveEDC(ReceiveMsg<List<AmmeterData>> msg) {
        if (null == msg) {
            return;
        }

        List<AmmeterData> data = msg.getData();

        if (data == null || data.isEmpty()) {
            log.warn("接收电表断点续传数据, 参数 data 不存在; 错误码：" + msg.getCode());
            return;
        }

        Collections.sort(data); // 根据时间对电表数据排序
        EnergyCollectHandler.ammeterDataHandle(msg);
        // todo 待优化（添加缓存配置减少查表）
        //alarmHandler.alarmHandle(msg);//报警
    }

    /***************************************** DDC （楼控） **********************************************/

    // 新增一个控制器返回回调（DDC）
    @Override
    public void controllerAddDDC(ReceiveMsg msg) {
        /**qindehua */
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("新增一个控制器返回回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(token)) {
            log.warn("新增一个控制器返回回调，sessionId 不存在");
            return;
        }

        // 同步状态 0 未同步 1 已同步
        int syncState = 0;

        // 把添加控制器下位机返回的消息推送到前端页面
        WebSocketService.postEvent(token, WebSocketEvent.DDC, msg);

        // 消息返回成功则同步成功否则同步失败
        if (Code.SUCCEED.equals(msg.getCode())) {
            syncState = 1;
            // 手动更新在线状态
            controllerState(ip, true);
        }

        // 根据channelId地址查询缓存数据是否存在
        Controller controller = controllerCache.getDdcByChannelId(msg.getIp());

        if (null == controller) {
            return;
        }

        controller.setSynchState(syncState);
        controller.setOnlineState(1);

        // 更新ddc在线以及同步状态
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);

        // 更新ddc控制器的同步状态
        controllerMapper.updateController(controller);
    }


    /**
     * 设置控制器的所有参数回调
     *
     * @param msg 消息
     * @Author qindehua
     * @Date 2022/09/24
     **/
    // 设置控制器的所有参数回调（DDC）
    @Override
    public void controllerSetDDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置控制器的所有参数回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            // 把添加控制器下位机返回的消息推送到前端页面
            WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
        }

        // 同步状态 0 未同步 1 已同步
        int syncState = 0;
        // 消息返回成功则同步成功否则同步失败
        if (Code.SUCCEED.equals(msg.getCode())) {
            syncState = 1;
        }
        /**根据channelId地址查询缓存数据是否存在*/
        Controller controller = controllerCache.getDdcByChannelId(ip);
        if (controller == null) {
            return;
        }

        /**同步状态*/
        controller.setSynchState(syncState);

        /**在线状态*/
        controller.setOnlineState(1);

        /**更新ddc控制器的同步状态 没有同步成功直接返回*/
        if (!(controllerMapper.updateSynchState(controller))) {
            return;
        }
        // 所有操作成功后  最后更新控制器的  同步状态
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);

        // 所有操作成功后  最后更新树状态
        controllerState(ip, true);
        /**只有进行批量同步时    才进行以下操作*/
        if (controller.getSynchronize()) {
            /**获取设备树  下级级联信息*/
            List<DeviceTree> deviceTrees = deviceTreeCache.getCascadeSubordinate(controller.getDeviceTreeId());
            /**判断是否为空*/
            if (null == deviceTrees || deviceTrees.isEmpty()) {
                return;
            }
            for (DeviceTree deviceTree : deviceTrees) {
                int type = deviceTree.getDeviceNodeId();
                /**判断是否为模块类型*/
                if (type == DeviceTreeConstants.BES_MODEL) {
                    /**从缓存获取模块信息*/
                    Module module = moduleAndPointCache.getModuleByDeviceId((long) deviceTree.getDeviceTreeId());
                    if (module==null){
                        continue;
                    }
                    moduleServiceImpl.synchronizeModule(module);
                    continue;
                }
                /**判断是否为虚点类型*/
                if (type == DeviceTreeConstants.BES_VPOINT) {
                    /**从缓存获取点位信息*/
                    Point point = moduleAndPointCache.getPointByDeviceId((long)deviceTree.getDeviceTreeId());
                    if (point==null){
                        continue;
                    }
                    moduleServiceImpl.synVirtualPoint(point);
                }
            }
        }


    }

    // 删除一个控制器，并删除和它相关的模块和点回调（DDC）
    @Override
    public void controllerDeleteDDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除一个控制器，并删除和它相关的模块和点回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(token)) {
            log.warn("删除一个控制器，并删除和它相关的模块和点回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(token, WebSocketEvent.DDC, msg);
    }

    // 获取控制器的所有配置参数回调（DDC）
    @Override
    public void controllerGetDDC(ReceiveMsg<ControllerDataDDC> controllerData) {
        /**qindehua */
        Integer index = controllerData.getIndex();
        String ip = controllerData.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取控制器的所有配置参数响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取控制器的所有配置参数响应回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, controllerData);
    }

    // 远程升级回调（DDC）
    @Override
    public void remoteUpgradeDDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (Code.SUCCEED.equals(msg.getCode())) {
            Channel channel = MsgUtil.getChannelByTokenSN(ip);
            channel.close();
        }

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("远程升级响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("远程升级响应回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 设置控制器的时间回调（DDC）
    @Override
    public void controllerTimeSetDDC(ReceiveMsg msg) {
        /**qindehua**/
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置控制器的时间回调，index 不存在，或者 ip 不存在");
            return;
        }

        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("设置控制器的时间回调，sessionId 不存在");
            return;
        }
        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 获取控制器的时间回调（DDC）
    @Override
    public void controllerTimeGetDDC(ReceiveMsg<TimeData> msg) {
        /**qindehua**/
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取控制器的时间回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取控制器的时间回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 重启控制器，相当于重启复位回调（DDC）
    @Override
    public void controllerRestartDDC(ReceiveMsg msg) {
        /***qindehua */
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("重启控制器，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
        }
        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            return;
        }

        // 根据channelId地址查询缓存数据是否存在
        Controller controller = controllerCache.getDdcByChannelId(ip);

        if (null == controller) {
            return;
        }

        String fIp = controller.getIp();
//        String fChannelId = controller.getCurrentIp();

//        /** 更新通信IP*/
//        if (StringUtils.hasText(fChannelId)
//                && StringUtils.hasText(fIp)
//                && !fIp.equals(fChannelId)) {
//            controller.setCurrentIp(fIp);
//            /** 更新数据库中的通信IP*/
//            if (!controllerMapper.updateController(controller)) {
//                log.warn("数据更新失败！");
//                return;
//            }
//            /******更新缓存数据*****/
//            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);
//
//        }
        // 手动更新在线状态
        controllerState(fIp, false);

        Channel channel = MsgUtil.getChannelByTokenSN(ip);
        channel.close();
    }

    // 重置控制器，恢复出厂设置，并重启回调（DDC）
    @Override
    public void controllerResetDDC(ReceiveMsg msg) {
        /***qindehua  */
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("重置DDC控制器，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(token)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(token, WebSocketEvent.DDC, msg);
        }

        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            return;
        }

        // 根据channelId地址查询缓存数据是否存在
        Controller controller = controllerCache.getDdcByChannelId(msg.getIp());

        if (null == controller) {
            return;
        }

        String fIp = controller.getIp();
//        String fChannelId = controller.getCurrentIp();
//
//        if (StringUtils.hasText(fChannelId)
//                && StringUtils.hasText(fIp)
//                && !fIp.equals(fChannelId)) {
//
//            controller.setCurrentIp(fIp);
//
//        }
        // 手动更新在线状态
        controllerState(fIp, false);
        // 同步状态 0 未同步 1 已同步
        controller.setSynchState(0);
        /** qindehua 更新数据库中的通信IP*/
        if (!controllerMapper.updateController(controller)) {
            log.warn("数据更新失败！");
            return;
        }
        Channel channel = MsgUtil.getChannelByTokenSN(ip);
        channel.close();


        // 所有操作成功后  最后更新控制器的  同步状态
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);

        List<DeviceTree> deviceTreeList = deviceTreeCache.getCascadeSubordinate(controller.getDeviceTreeId());

        if (null == deviceTreeList || deviceTreeList.isEmpty()) {
            return;
        }
        /**循环判断点位类型*/
        for (DeviceTree deviceTree : deviceTreeList) {

            int id = deviceTree.getDeviceTreeId();
            int type = deviceTree.getDeviceNodeId();
            Integer state = 0;
            if (DeviceTreeConstants.BES_MODEL.equals(type)) {
                Module module = moduleAndPointCache.getModuleByDeviceId((long) id);
                module.setDeviceTreeId((long) id);
                module.setSynchState((long) state);
                if (moduleMapper.updateModule(module)) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, (long) deviceTree.getDeviceTreeId(), module);
                }
                continue;
            }
            if (DeviceTreeConstants.BES_VPOINT.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                if (null != point.getNickName() && !"".equals(point.getNickName())) {
                    point.setSyncState(state);
                    if (pointMapper.updatePoint(point)) {
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);
                    }
                }

                continue;
            }
            if (DeviceTreeConstants.BES_AI.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                if (null != point.getNickName() && !"".equals(point.getNickName())) {
                    point.setSyncState(state);
                    if (pointMapper.updatePoint(point)) {
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);

                    }
                }
                continue;
            }
            if (DeviceTreeConstants.BES_AO.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                if (null != point.getNickName() && !"".equals(point.getNickName())) {
                    point.setSyncState(state);
                    if (pointMapper.updatePoint(point)) {
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);

                    }
                }
                continue;
            }
            if (DeviceTreeConstants.BES_DI.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                if (null != point.getNickName() && !"".equals(point.getNickName())) {
                    point.setSyncState(state);
                    if (pointMapper.updatePoint(point)) {
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);

                    }
                }
                continue;
            }
            if (DeviceTreeConstants.BES_DO.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                if (null != point.getNickName() && !"".equals(point.getNickName())) {
                    point.setSyncState(state);
                    if (pointMapper.updatePoint(point)) {
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);

                    }
                }
                continue;
            }
        }

    }

    // 新增加一个模块回调（DDC）
    @Override
    public void moduleAddDDC(ReceiveMsg<ModuleParamDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("新增加一个模块回调, index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            // 把添加控制器下位机返回的消息推送到前端页面
            WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
        }

        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            log.warn("新增加一个模块回调（DDC）, Code 错误码：" + msg.getCode());
            return;
        }

        Integer id = msg.getData().getId();

        //根据模块Id查询缓存
        Module module = new Module();
        Map<String, Module> cacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
        Collection<Module> values = cacheMap.values();
        for (Module m : values) {
            if (id.equals(Integer.parseInt(String.valueOf(m.getId())))) {
                module = m;
                break;
            }
        }

        if (null == module.getId()) {
            log.warn("新增加一个模块回调, 根据 id 没有查出设备树节点信息");
            return;
        }

        //DDC在线状态
        // 同步状态 0 未同步 1 已同步
        module.setOnlineState((long) 1);
        module.setSynchState((long) 1);
        module.setDeviceTreeStatus(1);
        // 更新模块的同步状态
        boolean isupdateModule = moduleMapper.updateModule(module);
        if (isupdateModule) {
            //更新缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
//            controllerState(ip, true);
            //回调成功后 将在线状态改为
            DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(module.getDeviceTreeId());
            deviceTree.setDeviceTreeStatus(1);
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTree.getDeviceTreeId(), deviceTree);
            log.warn("新增加一个模块回调, 修改数据库模块状态成功，添加缓存");
        } else {
            log.warn("新增加一个模块回调, 修改数据库模块状态时失败");
            return;
        }
    }

    // 设置一个模块的所有参数回调（DDC）
    @Override
    public void moduleSetDDC(ReceiveMsg<ModuleParamDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();
        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置一个模块的所有参数回调（DDC）, index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            log.warn("设置一个模块的所有参数回调（DDC）, sessionId 不存在");
            // 把添加控制器下位机返回的消息推送到前端页面
            WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
        }

        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            log.warn("设置一个模块的所有参数回调（DDC）, Code 错误码：" + msg.getCode());
            return;
        }

        Integer id = msg.getData().getId();

        //根据模块Id取出缓存
        Module module = new Module();
        Map<String, Module> cacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
        Collection<Module> values = cacheMap.values();
        for (Module m : values) {
            if (id.equals(Integer.parseInt(String.valueOf(m.getId())))) {
                module = m;
                break;
            }
        }

        if (null == module.getId()) {
            log.warn("设置一个模块的所有参数回调接收到的消息, 根据 id 没有查出设备树节点信息");
            return;
        }

        module.setSynchState((long) 1);
        //DDC在线状态
        module.setOnlineState((long) 1);
        module.setDeviceTreeStatus(1);

        // 更新模块的同步状态
        boolean isupdateModule = moduleMapper.updateModule(module);
        if (isupdateModule) {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
            //回调成功后 将在线状态改为
            DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(module.getDeviceTreeId());
            deviceTree.setDeviceTreeStatus(1);
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTree.getDeviceTreeId(), deviceTree);
            log.warn("设置一个模块的所有参数回调接收到的消息, 修改数据库模块状态成功，添加缓存");
        } else {
            log.warn("设置一个模块的所有参数回调接收到的消息, 修改数据库模块状态时失败");
            return;
        }

        //同步模块下的子节点,不管子节点是否同步成功
        //查询模块下的非空点位列表
        List<DeviceTree> deviceTreeList = deviceTreeCache.getCascadeSubordinate(Integer.parseInt(module.getDeviceTreeId().toString()));
        List<Point> pointList = new ArrayList<>();
        for (DeviceTree d : deviceTreeList) {
            Point point = moduleAndPointCache.getPointByDeviceId((long) d.getDeviceTreeId());
            if (null != point && null != point.getNickName() && !"".equals(point.getNickName())) {
                pointList.add(point);
            }
        }

        for (Point pointMapList : pointList) {
            //同步模块下的点位
            moduleServiceImpl.synchronizePoint(pointMapList);
        }
    }

    // 删除一个模块，并删除和模块相关的点回调（DDC）
    @Override
    public void moduleDeleteDDC(ReceiveMsg<ModuleParamDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除一个模块，并删除和模块相关的点回调, index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("删除一个模块，并删除和模块相关的点回调, sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 获取一个模块的所有配置信息回调（DDC）
    @Override
    public void moduleGetDDC(ReceiveMsg<ModuleParamDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取控制器的所有配置参数回调（DDC），index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取控制器的所有配置参数回调（DDC），sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 接收DDC实点更新的点信息回调（DDC）
    @Override
    public void realPointDataReceiveDDC(ReceiveMsg<List<PointDataDDC>> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收DDC实点更新的点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        List<PointDataDDC> pointList = msg.getData();

        if (null == pointList || pointList.isEmpty()) {
            log.warn("接收DDC实点更新的点信息回调, 参数 data 不存在");
            return;
        }

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

        if (controller.getIp() == null) {
            log.warn("接收DDC实点更新的点信息回调，DDC控制器不存在");
            return;
        }

        Integer id;//id
        Integer value;//实时值

        List<PointDataResponse> pointDataResponseList = new ArrayList<>();

        for (PointDataDDC pointData : pointList) {
            id = pointData.getId();
            value = pointData.getValue();

            if (null == id || null == value) {
                log.warn("接收DDC实点更新的点信息回调, 参数 data 不存在");
                continue;
            }

            PointDataResponse pointDataResponse1 = new PointDataResponse();


            pointDataResponse1.setId(id);
            pointDataResponse1.setValue(String.valueOf(value));

            //根据IP查询点位树缓存
            Point point = new Point();
            Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
            Collection<Point> pointValues = pointCacheMap.values();
            for (Point p : pointValues) {
                if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                    point = p;
                    break;
                }
            }

            if (null == point.getEquipmentId()) {
                log.warn("接收DDC实点更新的点信息回调，实点信息不存在");
                continue;
            }

            pointDataResponse1.setId(Integer.parseInt(String.valueOf(point.getTreeId())));
            pointDataResponse1.setAlias(point.getNickName());
            pointDataResponse1.setName(point.getSysName());
            pointDataResponse1.setSysNameOld("");
            pointDataResponse1.setUnit(point.getEngineerUnit());
            pointDataResponseList.add(pointDataResponse1);


            if (DeviceTreeConstants.BES_AO == Integer.parseInt(point.getNodeType())
                    || DeviceTreeConstants.BES_AI == Integer.parseInt(point.getNodeType())) {

                Integer accuracyNum = Integer.parseInt(String.valueOf(point.getAccuracy()));
                ;//获取精度
                Double valueDouble = value / Math.pow(10, accuracyNum);//获取精度转换后的实时值
                String values = subZeroAndDot(String.valueOf(valueDouble));
                pointDataResponse1.setValue(values);

                // 更新缓存
                point.setRunVal(values);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);

            } else {
                pointDataResponse1.setValue(String.valueOf(value));
                // 更新缓存
                point.setRunVal(String.valueOf(value));
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);
            }
            SceneLinkHandler.deviceTrigger(pointDataResponse1.getId()+"","0",SceneLinkHandler.SCENE_LINK_DEVICEACTION_ATTRIBUTE,pointDataResponse1.getValue());

        }


        alarmHandler.alarmHandle(msg, controller);//报警

        ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
        msg1.setData(pointDataResponseList);
        msg1.setCode(msg.getCode());
        msg1.setIndex(msg.getIndex());
        msg1.setIp(msg.getIp());
        // 推送消息到web客户端
        // WebSocketService.postEvent(WebSocketEvent.DDC, msg1);

//        // 获取当前 sessionId
//        String sessionId = MsgSubPubHandler.pubMsg(index, ip);
//
//        if (!StringUtils.hasText(sessionId)) {
//            log.warn("获取控制器的所有配置参数回调（DDC），sessionId 不存在");
//            return;
//        }
//
//        // 推送消息到web客户端
//        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
        distributePostEvent(WebSocketEvent.DDC, msg1);

////        functionPointStateService.stateReceiptApiHandle(pointDataResponseList);
//
//        //向第三个系统推送数据
//        SessionManager.sessionMap.forEach((sessionId, wbSession) -> {
//            String names = SessionManager.sessionIdNameDataMap.get(sessionId);
//            JSONArray jsonArray = (JSONArray) JSONArray.parse(names);
//            if (jsonArray == null) {
//                //没有请求数据不推送
//                return;
//            }
//            List<Map> list = new ArrayList<>();
//
//            pointDataResponseList.forEach(pointDataResponse -> {
//                String name = pointDataResponse.getName();
//                String val = pointDataResponse.getValue();
//                String unit = pointDataResponse.getUnit();
//                if (jsonArray.contains(name)) {
//                    Map<String, String> map = new HashMap<>();
//                    map.put("name", name);
//                    map.put("value", val);
//                    map.put("unit", unit);
//                    list.add(map);
//                }
//            });
//
//            if (list.isEmpty()) {
//                //没有数据不推送
//                return;
//            }
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("list", list);
//            TextMessage textMessage = new TextMessage(jsonObject.toString());
//            try {
//                wbSession.sendMessage(textMessage);
////                log.info("向前端推送实时数据："+jsonObject.toString());
//            } catch (IOException e) {
//                log.error("向前端推送实时数据报错:" + e.getMessage());
//            }
//        });

    }

    // 获取一个模块的错误代码回调（DDC）
    @Override
    public void moduleErrorCodeGetDDC(ReceiveMsg<ErrorCodeDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();
        Integer onlineStatus;

        //根据ip查询当前DDC控制器点是否存在
        Module module = new Module();
        Controller controller = new Controller();

        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        if (controller == null) {
            log.warn("获取一个模块的错误代码回调（DDC）, 当前控制器不存在 ip:" + ip);
            return;
        }

        ReceiveMsg<List<DeviceTree>> msgList = new ReceiveMsg();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取一个模块的错误代码回调（DDC）, index 不存在，或者 ip 不存在");
            return;
        }

        if (null == msg.getData() || null == msg.getData().getErrorCode()) {
            log.warn("获取一个模块的错误代码回调（DDC）, id 不存在");
            return;
        }
        Integer id = msg.getData().getId();


        Map<String, Module> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
        Collection<Module> pointValues = pointCacheMap.values();
        for (Module m : pointValues) {
            if (m.getId().intValue() == id && controller.getId() == m.getControllerId().intValue()) {
                module = m;
                break;
            }
        }

        DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(module.getDeviceTreeId());

        if (module.getId() == null || deviceTree == null) {
            log.warn("获取一个模块的错误代码回调（DDC）, 设备树节点不存在");
            return;
        }


        msgList.setIndex(index);
        msgList.setIp(ip);
        msgList.setCode(1);

        if (msg.getData().getErrorCode().equals(0)) {//在线

            onlineStatus = 1;
        } else {//离线
            onlineStatus = 0;
            //储存错误记录至记录表
            ModuleErrorLog moduleErrorLog = new ModuleErrorLog();
            moduleErrorLog.setDeviceTreeId(Integer.parseInt(String.valueOf(module.getDeviceTreeId())));
            moduleErrorLog.setModuleId(Integer.parseInt(String.valueOf(module.getId())));
            moduleErrorLog.setCreateTime(DateUtils.getNowDate());
            moduleErrorLog.setErrCode(msg.getData().getErrorCode().toString());
            boolean isAddErrorLog = pointMapper.addModuleErrorLog(moduleErrorLog);
            if (!isAddErrorLog) {
                log.warn("获取一个模块的错误代码回调（DDC）, 添加错误日志失败");
                return;
            }
        }
        // 更新设备树状态
        //获取模块及其下属所有设备树缓存
        List<DeviceTree> besSbPzStructs = deviceTreeCache.getCascadeSubordinate(deviceTree.getDeviceTreeId());

        // 更新缓存设备树在线状态
        for (DeviceTree d : besSbPzStructs) {
            d.setDeviceTreeStatus(onlineStatus);
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) d.getDeviceTreeId(), d);
            if (d.getDeviceNodeId() == DeviceTreeConstants.BES_MODEL) {
                module.setOnlineState(Long.parseLong(String.valueOf(onlineStatus)));
                //修改数据库
                moduleMapper.updateModule(module);
                //修改缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
            }
        }

        msgList.setData(besSbPzStructs);

        // 推送消息到web客户端
        WebSocketService.broadcast(WebSocketEvent.DEVICE_STATE, msgList);
//        WebSocketService.postEvent(WebSocketEvent.DDC, msgList);
    }

    // 批量获取模块的错误代码回调（DDC）
    @Override
    public void moduleErrorCodeGetDDC_ALL(ReceiveMsg<List<ErrorCodeDDC>> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();
        Integer onlineStatus = null;
        ReceiveMsg<List<DeviceTree>> msgList = new ReceiveMsg();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("批量接收一个模块的错误代码（DDC）, index 不存在，或者 ip 不存在");
            return;
        }

        List<ErrorCodeDDC> errorCodeDDC = msg.getData();
        if (null == errorCodeDDC || errorCodeDDC.isEmpty()) {
            log.warn("批量接收一个模块的错误代码, 参数 data 不存在");
            return;
        }

        //根据ip查询当前DDC控制器点是否存在
        Controller con = new Controller();
        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                con = c;
                break;
            }
        }

        if (con == null) {
            log.warn("批量接收一个模块的错误代码，DDC控制器不存在");
            return;
        }

        for (ErrorCodeDDC errorCode : errorCodeDDC) {
            Integer id = errorCode.getId();
            //查找当前id所对应的模块名称
            Module module = new Module();
            Map<String, Module> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
            Collection<Module> pointValues = pointCacheMap.values();
            for (Module m : pointValues) {
                if (m.getId().intValue() == id && con.getId() == m.getControllerId().intValue()) {
                    module = m;
                    break;
                }
            }

            if (module.getId() == null) {
                log.warn("批量接收一个模块的错误代码（DDC）, 模块缓存不存在该模块");
                continue;
            }

            DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(module.getDeviceTreeId());

            if (deviceTree == null) {
                continue;
            }

            msgList.setIp(ip);
            msgList.setIndex(index);

            if (errorCode.getErrorCode().equals(0)) {//在线

                onlineStatus = 1;
            } else {//离线
                onlineStatus = 0;
                //储存错误记录至记录表
                ModuleErrorLog moduleErrorLog = new ModuleErrorLog();
                moduleErrorLog.setDeviceTreeId(Integer.parseInt(String.valueOf(module.getDeviceTreeId())));
                moduleErrorLog.setModuleId(Integer.parseInt(String.valueOf(module.getId())));
                moduleErrorLog.setCreateTime(DateUtils.getNowDate());
                moduleErrorLog.setErrCode(errorCode.getErrorCode().toString());
                boolean isAddErrorLog = pointMapper.addModuleErrorLog(moduleErrorLog);
                if (!isAddErrorLog) {
                    log.warn("批量接收一个模块的错误代码（DDC）, 添加错误日志失败");
                    return;
                }
            }
            // 更新设备树状态
            List<DeviceTree> besSbPzStructs = deviceTreeCache.getCascadeSubordinate(deviceTree.getDeviceTreeId());
            for (DeviceTree d : besSbPzStructs) {
                d.setDeviceTreeStatus(onlineStatus);
                // 更新缓存设备树在线状态
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) d.getDeviceTreeId(), d);
                if (d.getDeviceNodeId() == DeviceTreeConstants.BES_MODEL) {
                    module.setOnlineState(Long.parseLong(String.valueOf(onlineStatus)));
                    //修改数据库
                    moduleMapper.updateModule(module);
                    //修改缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
                }
            }

            msgList.setData(besSbPzStructs);

            // 推送消息到web客户端
            WebSocketService.broadcast(WebSocketEvent.DEVICE_STATE, msgList);
        }
    }

    // 新增加一个逻辑点回调（DDC）
    @Override
    public void pointAddDDC(ReceiveMsg<PointParamDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("新增加一个逻辑点回调接收到的消息, index 不存在，或者 ip 不存在");
            return;
        }

        Integer id;

        if (null == msg.getData() || null == msg.getData().getId()) {
            log.warn("新增加一个逻辑点回调接收到的消息, id 不存在");
            return;
        }

        id = msg.getData().getId();

        //根据设备Id、控制器ip取出缓存
        Point point = new Point();
        Controller controller = new Controller();

        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
        Collection<Point> pointValues = pointCacheMap.values();
        for (Point p : pointValues) {
            if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                point = p;
                break;
            }
        }

        if (point.getEquipmentId() == null) {
            log.warn("新增加一个逻辑点回调接收到的消息, 根据 id 没有查出设备树节点信息");
            return;
        }

        Integer code = msg.getCode();

        if (Code.SUCCEED.equals(code)) {
            //修改同步状态
            point.setSyncState(1);
            point.setDeviceTreeStatus(1);
            boolean isUpdatePoint = pointMapper.updatePoint(point);
            if (isUpdatePoint) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);
                //回调成功后 将在线状态改为
                DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId( point.getTreeId());
                deviceTree.setDeviceTreeStatus(1);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTree.getDeviceTreeId(), deviceTree);
                log.warn("新增加一个逻辑点回调接收到的消息, 修改数据库点位状态成功，添加缓存");
//                controllerState(ip, true);
            } else {
                log.warn("新增加一个逻辑点回调接收到的消息, 修改数据库点位状态时失败");
                return;
            }

        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("新增加一个逻辑点回调接收到的消息, sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 设置一个逻辑点的所有参数回调（DDC）
    @Override
    public void pointParamSetDDC(ReceiveMsg<PointParamDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置一个逻辑点的所有参数回调接收到的消息, index 不存在，或者 ip 不存在");
            return;
        }

        Integer id;

        if (null == msg.getData() || null == msg.getData().getId()) {
            log.warn("设置一个逻辑点的所有参数回调接收到的消息, id 不存在");
            return;
        }

        id = msg.getData().getId();

        //根据设备Id、控制器ip取出缓存
        Point point = new Point();
        Controller controller = new Controller();

        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
        Collection<Point> pointValues = pointCacheMap.values();
        for (Point p : pointValues) {
            if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                point = p;
                break;
            }
        }

        if (point.getEquipmentId() == null) {
            log.warn("设置一个逻辑点的所有参数回调接收到的消息, 根据 id 没有查出设备树节点信息");
            return;
        }

        Integer code = msg.getCode();

        if (Code.SUCCEED.equals(code)) {
            //已同步修改同步状态
            point.setSyncState(1);//已同步
            point.setDeviceTreeStatus(1);
            boolean isUpdateState = pointMapper.updatePointSyncState(point);
            if (isUpdateState) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);
                //回调成功后 将在线状态改为
                DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(point.getTreeId());
                deviceTree.setDeviceTreeStatus(1);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTree.getDeviceTreeId(), deviceTree);
                log.warn("设置一个逻辑点的所有参数回调接收到的消息, 修改数据库点位状态成功，添加缓存");
            } else {
                log.warn("设置一个逻辑点的所有参数回调接收到的消息, 修改数据库点位状态时失败");
                return;
            }
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("设置一个逻辑点的所有参数回调接收到的消息, sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 设置一个逻辑点的值回调（DDC）
    @Override
    public void pointValueSetDDC(ReceiveMsg<PointDataDDC> msg) {
        Integer value;

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置一个逻辑点的值回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("设置一个逻辑点的值回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);

        Integer code = msg.getCode();

        if (!Code.SUCCEED.equals(code)) {
            log.warn("设置一个逻辑点的值失败，返回错误码：" + code);
            return;
        }

        Integer id = msg.getData().getId();

        //根据设备Id、控制器ip取出缓存
        Point point = new Point();
        Controller controller = new Controller();

        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
        Collection<Point> pointValues = pointCacheMap.values();
        for (Point p : pointValues) {
            if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                point = p;
                break;
            }
        }

        if (point.getEquipmentId() == null) {
            log.warn("设置一个逻辑点的值回调， 设备树点位不存在");
            return;
        }

        //gaojikun   屏蔽该处防止回调两次
        Integer nodeType = Integer.parseInt(point.getNodeType());
        if (DeviceTreeConstants.BES_VPOINT.equals(nodeType)) {
            value = msg.getData().getValue();

            //更改实点点位的实时值 实时值不在更新数据表
            // besSbdyMapper.updateVPointByid("BES_VPOINT",String.valueOf(id),value);

            ReceiveMsg<List<PointDataDDC>> msgs = new ReceiveMsg<>();

            msgs.setCode(0);
            msgs.setIndex(DDCCmd.VIRTUAL_POINT_DATA_RECEIVE);
            msgs.setIp(ip);

            List<PointDataDDC> pointDataList = new ArrayList<>();

            PointDataDDC pointDataDDC = new PointDataDDC();

            pointDataDDC.setId(id);
            pointDataDDC.setValue(value);

            pointDataList.add(pointDataDDC);

            msgs.setData(pointDataList);

            virtualPointDataReceiveDDC(msgs);

        } else {
            Integer workMode = msg.getData().getWorkMode();

            point.setWorkMode((long) workMode);

            boolean isupdatePoint = pointMapper.updatePoint(point);

            if (!isupdatePoint) {
                log.warn("设置一个逻辑点的值回调, 修改数据库点位失败");
                return;
            }

            value = msg.getData().getValue();

            //更改实点点位的实时值
            // 实时值不在更新表
            // besSbdyMapper.updatePointByid(besSbTreeNodeType.getF_node_table(), String.valueOf(id),value);

            ReceiveMsg<List<PointDataDDC>> msgs = new ReceiveMsg<>();

            msgs.setCode(0);
            msgs.setIndex(DDCCmd.REAL_POINT_DATA_RECEIVE);
            msgs.setIp(ip);

            List<PointDataDDC> pointDataList = new ArrayList<>();

            PointDataDDC pointDataDDC = new PointDataDDC();

            pointDataDDC.setId(id);
            pointDataDDC.setValue(value);

            pointDataList.add(pointDataDDC);

            msgs.setData(pointDataList);

            realPointDataReceiveDDC(msgs);
        }
    }

    // 设置逻辑点的值，根据点的名字（DDC）(未使用)
    @Override
    public void pointValueByNameSetDDC(ReceiveMsg<PointParamDDC> msg) {
        Map<String, Object> pointMapByid;
        Integer value;
        Double values;

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置一个逻辑点的值回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("设置一个逻辑点的值回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);

        Integer code = msg.getCode();

        if (!Code.SUCCEED.equals(code)) {
            log.warn("根据名称设置一个逻辑点的值失败，返回错误码：" + code);
            return;
        }

        Integer id = msg.getData().getId();

        //根据设备Id、控制器ip取出缓存
        Point point = new Point();
        Controller controller = new Controller();

        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
        Collection<Point> pointValues = pointCacheMap.values();
        for (Point p : pointValues) {
            if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                point = p;
                break;
            }
        }

        if (point.getEquipmentId() == null) {
            log.warn("设置一个逻辑点的值回调， 设备树点位不存在");
            return;
        }

        Integer nodeType = Integer.parseInt(point.getNodeType());


        if (DeviceTreeConstants.BES_VPOINT.equals(nodeType)) {//虚点

            if (Integer.parseInt(point.getVpointType()) == PointType.POINT_TYPE_VIRTUAL_AO) { //VAO点位

                value = Integer.parseInt(point.getInitVal());

                Double f_accuracy = Double.valueOf(String.valueOf(point.getAccuracy()));//获取精度

                values = value / Math.pow(10, f_accuracy);//获取精度转换后的实时值
                value = Integer.parseInt(new java.text.DecimalFormat("0").format(values));

            } else {

                value = Integer.parseInt(point.getInitVal());
            }

            ReceiveMsg<List<PointDataDDC>> msgs = new ReceiveMsg<>();

            msgs.setCode(0);
            msgs.setIndex(DDCCmd.VIRTUAL_POINT_DATA_RECEIVE);
            msgs.setIp(ip);

            List<PointDataDDC> pointDataList = new ArrayList<>();

            PointDataDDC pointDataDDC = new PointDataDDC();

            pointDataDDC.setId(id);
            pointDataDDC.setValue(value);

            pointDataList.add(pointDataDDC);

            msgs.setData(pointDataList);

            virtualPointDataReceiveDDC(msgs);

        } else {

            //查询模块点的点位信息
            if (Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_AO) {

                Double f_accuracy = Double.valueOf(String.valueOf(point.getAccuracy()));//获取精度
                value = Integer.parseInt(point.getInitVal());
                values = value / Math.pow(10, f_accuracy);//获取精度转换后的实时值
                value = Integer.parseInt(new java.text.DecimalFormat("0").format(values));
            } else {

                value = Integer.parseInt(point.getInitVal());
            }


            ReceiveMsg<List<PointDataDDC>> msgs = new ReceiveMsg<>();

            msgs.setCode(0);
            msgs.setIndex(DDCCmd.REAL_POINT_DATA_RECEIVE);
            msgs.setIp(ip);

            List<PointDataDDC> pointDataList = new ArrayList<>();

            PointDataDDC pointDataDDC = new PointDataDDC();

            pointDataDDC.setId(id);

            pointDataDDC.setValue(value);

            pointDataList.add(pointDataDDC);

            msgs.setData(pointDataList);

            realPointDataReceiveDDC(msgs);
        }
    }

    // 删除一个逻辑点回调（DDC）
    @Override
    public void pointDeleteDDC(ReceiveMsg<PointParamDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 获取一个逻辑点的所有配置参数回调（DDC）
    @Override
    public void pointParamGetDDC(ReceiveMsg<PointParamDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取一个逻辑点的所有配置参数回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取一个逻辑点的所有配置参数回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 获取一个逻辑点的报警信息回调（DDC）
    @Override
    public void pointAlarmDataGetDDC(ReceiveMsg<AlarmPointDataDDC> msg) {
        // 未使用，告警逻辑由上位机实现
    }

    // 接收DDC的全部点信息回调（DDC）
    @Override
    public void pointDataAllReceiveDDC(ReceiveMsg<List<PointDataDDC>> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收DDC的全部点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        List<PointDataDDC> pointList = msg.getData();

        if (null == pointList || pointList.isEmpty()) {
            log.warn("接收DDC的全部点信息回调, 参数 data 不存在");
            return;
        }

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

        if (controller.getIp() == null) {
            log.warn("接收DDC的全部点信息回调，DDC控制器不存在");
            return;
        }

        Integer id;//id
        Integer value;//实时值

        List<Map> dataList = new ArrayList<>();

        List<PointDataResponse> pointDataResponseList = new ArrayList<>();

        //计算保存周期
        Date nowDate = new Date();
        for (PointDataDDC pointData : pointList) {
            id = pointData.getId();
            value = pointData.getValue();

            if (null == id || null == value) {
                continue;
            }

            PointDataResponse pointDataResponse = new PointDataResponse();

            pointDataResponse.setId(id);

            Point besSbPzStruct = new Point();

            Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
            Collection<Point> pointValues = pointCacheMap.values();
            for (Point p : pointValues) {
                if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                    besSbPzStruct = p;
                    break;
                }
            }

            if (null == besSbPzStruct.getEquipmentId()) {
                continue;
            }

            pointDataResponse.setId(Integer.parseInt(String.valueOf(besSbPzStruct.getTreeId())));

            /** 虚点**/
            if (DeviceTreeConstants.BES_VPOINT == Integer.parseInt(besSbPzStruct.getNodeType())) {
                Point besVirtualPoint = moduleAndPointCache.getPointByDeviceId(besSbPzStruct.getTreeId());

                if (null == besVirtualPoint) {
                    continue;
                }

                String pointType = besVirtualPoint.getVpointType();

                pointDataResponse.setAlias(besVirtualPoint.getNickName());
                pointDataResponse.setName(besVirtualPoint.getSysName());
                pointDataResponse.setSysNameOld("");
                pointDataResponse.setUnit(besVirtualPoint.getEngineerUnit());

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

                    pointDataResponse.setValue(values);

                    // 更新缓存
                    besSbPzStruct.setRunVal(values);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);

                    if (pointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))
                            && "1".equals(besVirtualPoint.getEnergyStatics().toString())) {

                        Integer savePeriod = controller.getSavePeriod();
                        Date recordUploadPeriod = besVirtualPoint.getRecordUploadPeriod();

                        if (recordUploadPeriod == null) {
                            besVirtualPoint.setRecordUploadPeriod(new Date());
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besVirtualPoint.getTreeId(), besVirtualPoint);
                        }

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long diff = 0;
                        try {
                            long d1 = df.parse(df.format(besVirtualPoint.getRecordUploadPeriod())).getTime();
                            long d2 = df.parse(df.format(nowDate)).getTime();
                            diff = (d2 - d1) / 1000 / 60;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //如果保存周期为空或者保存周期等于能耗上传周期记录
                        if (savePeriod == null || Integer.parseInt(String.valueOf(diff)) >= /*0*/savePeriod) {
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
                                log.warn("接收DDC的全部点信息回调，缓存未取到电表数据");
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
                            besVirtualPoint.setRecordUploadPeriod(nowDate);
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besVirtualPoint);
                        }

                    }


                } else {

                    pointDataResponse.setValue(String.valueOf(value));
                    String values = String.valueOf(value);
//                    planningHandler.planingHandler(id,values);
                    // 更新缓存
                    besSbPzStruct.setRunVal(values);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);

                }
                pointDataResponseList.add(pointDataResponse);

            }
            /** 实点**/
            else {

                pointDataResponse.setAlias(besSbPzStruct.getNickName());
                pointDataResponse.setName(besSbPzStruct.getSysName());
                pointDataResponse.setSysNameOld("");
                pointDataResponse.setUnit(besSbPzStruct.getEngineerUnit());

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
                    pointDataResponse.setValue(values);
//                    planningHandler.planingHandler(id, values);
                    // 更新缓存
                    besSbPzStruct.setRunVal(values);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);

                    Point besAiPoint = null;
                    Point besAoPoint = null;
                    if (DeviceTreeConstants.BES_AI == Integer.parseInt(besSbPzStruct.getNodeType())) {

                        besAiPoint = moduleAndPointCache.getPointByDeviceId(besSbPzStruct.getTreeId());

                        String energystatics = besAiPoint.getEnergyStatics().toString();

                        if ("1".equals(energystatics)) {
                            Integer savePeriod = controller.getSavePeriod();
                            Date recordUploadPeriod = besAiPoint.getRecordUploadPeriod();

                            if (recordUploadPeriod == null) {
                                besAiPoint.setRecordUploadPeriod(new Date());
                                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besAiPoint.getTreeId(), besAiPoint);
                            }

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            long diff = 0;
                            try {
                                long d1 = df.parse(df.format(besAiPoint.getRecordUploadPeriod())).getTime();
                                long d2 = df.parse(df.format(nowDate)).getTime();
                                diff = (d2 - d1) / 1000 / 60;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //如果保存周期为空或者保存周期等于能耗上传周期记录
                            if (savePeriod == null || Integer.parseInt(String.valueOf(diff)) >= /*0*/savePeriod) {
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
                                    log.warn("接收DDC的全部点信息回调，缓存未取到电表数据");
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
                                besAiPoint.setRecordUploadPeriod(nowDate);
                                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besAiPoint);
                            }
                        }

                    } else {
                        besAoPoint = moduleAndPointCache.getPointByDeviceId(besSbPzStruct.getTreeId());

                        String energystatics = besAoPoint.getEnergyStatics().toString();

                        if ("1".equals(energystatics)) {
                            Integer savePeriod = controller.getSavePeriod();
                            Date recordUploadPeriod = besAoPoint.getRecordUploadPeriod();

                            if (recordUploadPeriod == null) {
                                besAoPoint.setRecordUploadPeriod(new Date());
                                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besAoPoint.getTreeId(), besAoPoint);
                            }

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            long diff = 0;
                            try {
                                long d1 = df.parse(df.format(besAoPoint.getRecordUploadPeriod())).getTime();
                                long d2 = df.parse(df.format(nowDate)).getTime();
                                diff = (d2 - d1) / 1000 / 60;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //如果保存周期为空或者保存周期等于能耗上传周期记录
                            if (savePeriod == null || Integer.parseInt(String.valueOf(diff)) >= /*0*/savePeriod) {
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
                                    log.warn("接收DDC的全部点信息回调，缓存未取到电表数据");
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
                                besAoPoint.setRecordUploadPeriod(nowDate);
                                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besAoPoint);
                            }
                        }
                    }


                } else {

                    pointDataResponse.setValue(String.valueOf(value));

                    String values = String.valueOf(value);
//                    planningHandler.planingHandler(id, values);
                    // 更新缓存
                    besSbPzStruct.setRunVal(values);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);


                }

                pointDataResponseList.add(pointDataResponse);
            }
            SceneLinkHandler.deviceTrigger(pointDataResponse.getId()+"","0",SceneLinkHandler.SCENE_LINK_DEVICEACTION_ATTRIBUTE,pointDataResponse.getValue());


        }

        // 存储虚点能耗数据
        if (!dataList.isEmpty()) {
            EnergyCollectHandler.ammeterDataHandle(dataList, ip);
        }

        alarmHandler.alarmHandle(msg, controller);//报警

        // 推送消息到web客户端
        ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
        msg1.setData(pointDataResponseList);
        msg1.setCode(msg.getCode());
        msg1.setIndex(msg.getIndex());
        msg1.setIp(msg.getIp());

        // WebSocketService.postEvent(WebSocketEvent.DDC, msg1);
        distributePostEvent(WebSocketEvent.DDC, msg1);

//        functionPointStateService.stateReceiptApiHandle(pointDataResponseList);
    }

    // 接收虚点信息回调（DDC）
    @Override
    public void virtualPointDataReceiveDDC(ReceiveMsg<List<PointDataDDC>> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收虚点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        List<PointDataDDC> pointList = msg.getData();

        if (null == pointList || pointList.isEmpty()) {
            log.warn("接收虚点信息回调, 参数 data 不存在");
            return;
        }

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

        if (controller.getIp() == null) {
            log.warn("接收虚点信息回调，DDC控制器不存在");
            return;
        }

        Integer id;//id
        Integer value;//实时值

        List<PointDataResponse> pointDataResponseList = new ArrayList<>();

        for (PointDataDDC pointData : pointList) {
            id = pointData.getId();
            value = pointData.getValue();

            if (null == id || null == value) {
                continue;
            }

            PointDataResponse pointDataResponse1 = new PointDataResponse();

            pointDataResponse1.setId(id);

            //根据设备Id、控制器ip取出缓存
            Point besVirtualPoint = new Point();

            Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
            Collection<Point> pointValues = pointCacheMap.values();
            for (Point p : pointValues) {
                if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                    besVirtualPoint = p;
                    break;
                }
            }

            if (null == besVirtualPoint.getEquipmentId()) {
                continue;
            }

            pointDataResponse1.setId(Integer.parseInt(String.valueOf(besVirtualPoint.getTreeId())));
            pointDataResponse1.setAlias(besVirtualPoint.getNickName());
            pointDataResponse1.setName(besVirtualPoint.getSysName());
            pointDataResponse1.setSysNameOld(besVirtualPoint.getSysName());
            pointDataResponse1.setUnit(besVirtualPoint.getEngineerUnit());

            String pointType = besVirtualPoint.getVpointType();

            if (pointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))
                    || pointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {
                Integer accuracyNum = Integer.parseInt(String.valueOf(besVirtualPoint.getAccuracy()));//获取精度
                Double valueDouble = value / Math.pow(10, accuracyNum);//获取精度转换后的实时值
                String values = subZeroAndDot(String.valueOf(valueDouble));
                pointDataResponse1.setValue(values);

                // 更新缓存数据
                besVirtualPoint.setRunVal(values);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besVirtualPoint.getTreeId(), besVirtualPoint);


            } else {

                pointDataResponse1.setValue(String.valueOf(value));

                String values = String.valueOf(value);
                besVirtualPoint.setRunVal(values);
                //更改缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besVirtualPoint.getTreeId(), besVirtualPoint);

            }

            pointDataResponseList.add(pointDataResponse1);
            SceneLinkHandler.deviceTrigger(pointDataResponse1.getId()+"","0",SceneLinkHandler.SCENE_LINK_DEVICEACTION_ATTRIBUTE,pointDataResponse1.getValue());

        }


        alarmHandler.alarmHandle(msg, controller);//报警

        // 推送消息到web客户端
        ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
        msg1.setData(pointDataResponseList);
        msg1.setCode(msg.getCode());
        msg1.setIndex(msg.getIndex());
        msg1.setIp(msg.getIp());

        // WebSocketService.postEvent(WebSocketEvent.DDC, msg1);
        distributePostEvent(WebSocketEvent.DDC, msg1);

//        functionPointStateService.stateReceiptApiHandle(pointDataResponseList);
    }

    // 增加一个场景回调（DDC）
    @Override
    public void sceneAddDDC(ReceiveMsg<SceneDataDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收新增场景信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("接收新增场景信息回调（DDC），sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 设置一个场景的所有参数回调（DDC）
    @Override
    public void sceneParamSetDDC(ReceiveMsg<SceneDataDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取一个场景的所有配置参数回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取一个场景的所有配置参数回调（DDC），sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 删除一个场景回调（DDC）
    @Override
    public void sceneDeleteDDC(ReceiveMsg<SceneDataDDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除场景信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("删除一个场景的所有配置参数回调（DDC），sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    //获取一个场景下的模式信息的回调
    @Override
    public void controlParamDDC(ReceiveMsg<SceneDataDDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收DDC场景模式点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        SceneDataDDC sceneDataDDC = msg.getData();

        if (null == sceneDataDDC) {
            log.warn("接收DDC场景模式点信息回调, 参数 data 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);
        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    //删除一个场景模式(DDC)
    @Override
    public void sceneModeParamDeleteDDC(ReceiveMsg<SceneParamDDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除DDC场景模式点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        SceneParamDDC sceneParamDDC = msg.getData();

        if (null == sceneParamDDC) {
            log.warn("删除DDC场景模式点信息回调, 参数 data 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 获取一个场景的配置信息回调（DDC）
    @Override
    public void sceneParamGetDDC(ReceiveMsg<SceneParamDDC> msg) {


    }

    // 增加一条计划回调（DDC）
    @Override
    public void planAddDDC(ReceiveMsg<PlanParamDDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("新增DDC计划信息回调，index 不存在，或者 ip 不存在");
            return;
        }

//        PlanParamDDC planParamDDC = msg.getData();
//
//        if (null == planParamDDC)
//        {
//            log.warn("新增DDC计划信息回调, 参数 data 不存在");
//            return;
//        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 修改一条计划的所有参数回调（DDC）
    @Override
    public void planParamSetDDC(ReceiveMsg<PlanParamDDC> msg) {

        Integer index = msg.getIndex();

        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("同步DDC计划信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        PlanParamDDC planParamDDC = msg.getData();

        if (null == planParamDDC) {
            log.warn("同步DDC计划信息回调, 参数 data 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 删除一条计划回调（DDC）
    @Override
    public void planDeleteDDC(ReceiveMsg<PlanParamDDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除DDC计划信息回调，index 不存在，或者 ip 不存在");
            return;
        }

//        PlanParamDDC planParamDDC = msg.getData();
//
//        if (null == planParamDDC)
//        {
//            log.warn("删除DDC计划信息回调, 参数 data 不存在");
//            return;
//        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    // 获取一条计划的所有参数回调（DDC）
    @Override
    public void planParamGetDDC(ReceiveMsg<PlanParamDDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();
        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收DDC计划信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        PlanParamDDC planParamDDC = msg.getData();

        if (null == planParamDDC) {
            log.warn("接收DDC计划信息回调, 参数 data 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.DDC, msg);
    }

    /*************************************** LDC（照明）*****************************************/

    // 新增一个IP路由器返回回调（LDC）
    @Override
    public void controllerAddLDC(ReceiveMsg msg) {
        /**qindehua */
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("新增一个控制器返回回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(token)) {
            log.warn("新增一个控制器返回回调，sessionId 不存在");
            return;
        }

        // 同步状态 0 未同步 1 已同步
        int syncState = 0;

        // 把添加控制器下位机返回的消息推送到前端页面
        WebSocketService.postEvent(token, WebSocketEvent.LDC, msg);

        // 消息返回成功则同步成功否则同步失败
        if (Code.SUCCEED.equals(msg.getCode())) {
            syncState = 1;
            // 手动更新在线状态
            controllerState(ip, true);
        }

        // 根据channelId地址查询缓存数据是否存在
        Controller controller = controllerCache.getDdcByChannelId(msg.getIp());

        if (null == controller) {
            return;
        }

        controller.setSynchState(syncState);
        controller.setOnlineState(syncState);

        // 更新LDC在线以及同步状态
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);

        // 更新LDC控制器的同步状态
        controllerMapper.updateController(controller);
    }

    // 设置IP路由器的所有参数回调（LDC）
    @Override
    public void controllerSetLDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置控制器的所有参数回调，index 不存在，或者 ip 不存在");
            return;
        }


        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            // 把添加控制器下位机返回的消息推送到前端页面
            WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
        } else {

        }

        // 同步状态 0 未同步 1 已同步
        int syncState = 0;
        // 消息返回成功则同步成功否则同步失败
        if (Code.SUCCEED.equals(msg.getCode())) {
            syncState = 1;
        }
        /**根据channelId地址查询缓存数据是否存在*/
        Controller controller = controllerCache.getDdcByChannelId(ip);
        if (controller == null) {
            return;
        }

        /**同步状态*/
        controller.setSynchState(syncState);

        /**在线状态*/
        controller.setOnlineState(1);


        /***/

        /**更新LDC控制器的同步状态  没有同步成功直接返回*/
        if (!(controllerMapper.updateSynchState(controller))) {
            return;
        }

        // 所有操作成功后  最后更新控制器的  同步状态
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);
        // 所有操作成功后  最后更新树状态
        controllerState(ip, true);
        /**只有进行批量同步时    才进行以下操作*/
        if (controller.getSynchronize()) {
            /**获取设备树  下级级联信息*/
            List<DeviceTree> deviceTrees = deviceTreeCache.getCascadeSubordinate(controller.getDeviceTreeId());
            /**判断是否为空*/
            if (null == deviceTrees || deviceTrees.isEmpty()) {
                return;
            }
            for (DeviceTree deviceTree : deviceTrees) {
                int type = deviceTree.getDeviceNodeId();
                /**判断是否为模块类型*/
                if (type == DeviceTreeConstants.BES_MODEL) {
                    /**从缓存获取模块信息*/
                    Module module = moduleAndPointCache.getModuleByDeviceId((long) deviceTree.getDeviceTreeId());
                    moduleServiceImpl.synchronizeModule(module);
                    continue;
                }
                /**判断是否为虚点类型*/
                if (type == DeviceTreeConstants.BES_VPOINT) {
                    /**从缓存获取点位信息*/
                    Point point = moduleAndPointCache.getPointByDeviceId((long) deviceTree.getDeviceTreeId());
                    moduleServiceImpl.synVirtualPoint(point);
                }
            }
        }


    }

    // 删除一个IP路由器，并删除和它相关的模块和点回调（LDC）
    @Override
    public void controllerDeleteLDC(ReceiveMsg msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除一个IP路由器，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("删除一个IP路由器，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 获取IP路由器的所有配置参数回调（LDC）
    @Override
    public void controllerGetLDC(ReceiveMsg<ControllerDataLDC> controllerData) {
        /**qindehua */
        Integer index = controllerData.getIndex();
        String ip = controllerData.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取控制器的所有配置参数响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取控制器的所有配置参数响应回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, controllerData);
    }

    // 远程升级回调（LDC）
    @Override
    public void remoteUpgradeLDC(ReceiveMsg msg) {
        /**qindehua **/
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (Code.SUCCEED.equals(msg.getCode())) {
            Channel channel = MsgUtil.getChannelByTokenSN(ip);
            channel.close();
        }

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("远程升级响应回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("远程升级响应回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 设置IP路由器的时间回调（LDC）
    @Override
    public void controllerTimeSetLDC(ReceiveMsg msg) {
        /**qindehua**/
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置控制器的时间回调，index 不存在，或者 ip 不存在");
            return;
        }

        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("设置控制器的时间回调，sessionId 不存在");
            return;
        }
        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 获取IP路由器的时间回调（LDC）
    @Override
    public void controllerTimeGetLDC(ReceiveMsg<TimeData> msg) {
        /**qindehua**/
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取控制器的时间回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取控制器的时间回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 重启IP路由器，相当于重启复位回调（LDC）
    @Override
    public void controllerRestartLDC(ReceiveMsg msg) {
        /***qindehua */
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("重启控制器，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
        }
        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            return;
        }

        // 根据channelId地址查询缓存数据是否存在
        Controller controller = controllerCache.getDdcByChannelId(ip);

        if (null == controller) {
            return;
        }

        String fIp = controller.getIp();
//        String fChannelId = controller.getCurrentIp();
//
//        /** 更新通信IP*/
//        if (StringUtils.hasText(fChannelId)
//                && StringUtils.hasText(fIp)
//                && !fIp.equals(fChannelId)) {
//            controller.setCurrentIp(fIp);
//            /** 更新数据库中的通信IP*/
//            if (!controllerMapper.updateController(controller)) {
//                log.warn("数据更新失败！");
//                return;
//            }
//            /******更新缓存数据*****/
//            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);
//            // 手动更新在线状态
//
//        }
        controllerState(fIp, false);
        Channel channel = MsgUtil.getChannelByTokenSN(ip);
        channel.close();

    }

    // 重置IP路由器，恢复出厂设置，并重启回调（LDC）
    @Override
    public void controllerResetLDC(ReceiveMsg msg) {
/***qindehua  */
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("重置LDC控制器，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String token = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(token)) {
            // 推送消息到web客户端
            WebSocketService.postEvent(token, WebSocketEvent.LDC, msg);
        }

        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            return;
        }

        // 根据channelId地址查询缓存数据是否存在
        Controller controller = controllerCache.getDdcByChannelId(msg.getIp());

        if (null == controller) {
            return;
        }

        String fIp = controller.getIp();
//        String fChannelId = controller.getCurrentIp();
//
//        if (StringUtils.hasText(fChannelId)
//                && StringUtils.hasText(fIp)
//
//                && !fIp.equals(fChannelId)) {
//
//
//            controller.setCurrentIp(fIp);
//
//        }
        // 手动更新在线状态
        controllerState(fIp, false);
        // 同步状态 0 未同步 1 已同步
        controller.setSynchState(0);
        /** qindehua 更新数据库中的通信IP*/
        if (!controllerMapper.updateController(controller)) {
            log.warn("数据更新失败！");
            return;
        }
        Channel channel = MsgUtil.getChannelByTokenSN(ip);
        channel.close();


        // 所有操作成功后  最后更新控制器的  同步状态
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);

        List<DeviceTree> deviceTreeList = deviceTreeCache.getCascadeSubordinate(controller.getDeviceTreeId());

        if (null == deviceTreeList || deviceTreeList.isEmpty()) {
            return;
        }
        /**循环判断点位类型*/
        for (DeviceTree deviceTree : deviceTreeList) {

            int id = deviceTree.getDeviceTreeId();
            int type = deviceTree.getDeviceNodeId();
            Integer state = 0;
            if (DeviceTreeConstants.BES_MODEL.equals(type)) {
                Module module = moduleAndPointCache.getModuleByDeviceId((long) id);
                module.setDeviceTreeId((long) id);
                module.setSynchState((long) state);
                if (moduleMapper.updateModule(module)) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, (long) deviceTree.getDeviceTreeId(), module);
                }
                continue;
            }
            if (DeviceTreeConstants.BES_VPOINT.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                point.setTreeId((long) id);
                point.setSyncState(state);
                if (pointMapper.updatePoint(point)) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);
                }
                continue;
            }
            if (DeviceTreeConstants.BES_AI.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                point.setTreeId((long) id);
                point.setSyncState(state);
                if (pointMapper.updatePoint(point)) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);

                }
                continue;
            }
            if (DeviceTreeConstants.BES_AO.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                point.setTreeId((long) id);
                point.setSyncState(state);
                if (pointMapper.updatePoint(point)) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);

                }
                continue;
            }
            if (DeviceTreeConstants.BES_DI.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                point.setTreeId((long) id);
                point.setSyncState(state);
                if (pointMapper.updatePoint(point)) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);

                }
                continue;
            }
            if (DeviceTreeConstants.BES_DO.equals(type)) {
                Point point = moduleAndPointCache.getPointByDeviceId((long) id);
                point.setTreeId((long) id);
                point.setSyncState(state);
                if (pointMapper.updatePoint(point)) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) deviceTree.getDeviceTreeId(), point);

                }
                continue;
            }
        }

    }

    // 新增加一个模块回调（LDC）
    @Override
    public void moduleAddLDC(ReceiveMsg<ModuleParamLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("新增加一个模块回调, index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            log.warn("新增加一个模块回调, sessionId 不存在");
            // 把添加控制器下位机返回的消息推送到前端页面
            WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
        }

        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            log.warn("新增加一个模块回调（LDC）, Code 错误码：" + msg.getCode());
            return;
        }

        // 同步状态 0 未同步 1 已同步
        String syncState = "1";

        Integer id = msg.getData().getId();

        //根据模块Id查询缓存
        Module module = new Module();
        Map<String, Module> cacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
        Collection<Module> values = cacheMap.values();
        for (Module m : values) {
            if (id.equals(Integer.parseInt(String.valueOf(m.getId())))) {
                module = m;
                break;
            }
        }

        if (null == module.getId()) {
            log.warn("新增加一个模块回调, 根据 id 没有查出设备树节点信息");
            return;
        }

        //LDC在线状态
        module.setOnlineState((long) 1);
        module.setSynchState(Long.parseLong(syncState));

        // 更新模块的同步状态
        boolean isupdateModule = moduleMapper.updateModule(module);
        if (isupdateModule) {
            //更新缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
            DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(module.getDeviceTreeId());
            //回调成功后 将在线状态改为
            deviceTree.setDeviceTreeStatus(1);
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTree.getDeviceTreeId(), deviceTree);
            //手动更新设备树状态
//            controllerState(ip, true);
            log.warn("新增加一个模块回调, 修改数据库模块状态成功，添加缓存");
        } else {
            log.warn("新增加一个模块回调, 修改数据库模块状态时失败");
            return;
        }
    }

    // 设置一个模块的所有参数回调（LDC）
    @Override
    public void moduleSetLDC(ReceiveMsg<ModuleParamLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();
        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置一个模块的所有参数回调（LDC）, index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (StringUtils.hasText(sessionId)) {
            log.warn("设置一个模块的所有参数回调（LDC）, sessionId 不存在");
            // 把添加控制器下位机返回的消息推送到前端页面
            WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
        }

        // 消息返回成功则同步成功否则同步失败
        if (!Code.SUCCEED.equals(msg.getCode())) {
            log.warn("设置一个模块的所有参数回调（LDC）, Code 错误码：" + msg.getCode());
            return;
        }

        Integer id = msg.getData().getId();

        //根据模块Id取出缓存
        Module module = new Module();
        Map<String, Module> cacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
        Collection<Module> values = cacheMap.values();
        for (Module m : values) {
            if (id.equals(Integer.parseInt(String.valueOf(m.getId())))) {
                module = m;
                break;
            }
        }

        if (null == module.getId()) {
            log.warn("设置一个模块的所有参数回调接收到的消息, 根据 id 没有查出设备树节点信息");
            return;
        }
        //设备id，与下位机通讯

        module.setSynchState((long) 1);
        //LDC在线状态
        module.setOnlineState((long) 1);

        // 更新模块的同步状态
        boolean isupdateModule = moduleMapper.updateModule(module);
        if (isupdateModule) {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
            log.warn("设置一个模块的所有参数回调接收到的消息, 修改数据库模块状态成功，添加缓存");

        } else {
            log.warn("设置一个模块的所有参数回调接收到的消息, 修改数据库模块状态时失败");
            return;
        }

        //同步模块下的子节点,不管子节点是否同步成功
        //获取缓存节点树信息
        List<Point> pointMapLists = new ArrayList<>();
        Map<String, DeviceTree> deviceTreeCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree);
        Collection<DeviceTree> cacheList = deviceTreeCacheMap.values();
        for (DeviceTree d : cacheList) {
            if (d.getDeviceTreeFatherId() == Integer.parseInt(String.valueOf(module.getDeviceTreeId()))) {
                Point p = moduleAndPointCache.getPointByDeviceId((long) d.getDeviceTreeId());
                if (null != p && null != p.getNickName() && !"".equals(p.getNickName())) {
                    pointMapLists.add(p);
                }
            }
        }

        for (Point pointMapList : pointMapLists) {
            //同步模块下的点位
            moduleServiceImpl.synchronizePoint(pointMapList);
        }

    }

    // 删除一个模块，并删除和模块相关的点回调（LDC）
    @Override
    public void moduleDeleteLDC(ReceiveMsg<ModuleParamLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除一个模块，并删除和模块相关的点回调, index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("删除一个模块，并删除和模块相关的点回调, sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 获取一个模块的所有配置信息回调（LDC）
    @Override
    public void moduleGetLDC(ReceiveMsg<ModuleParamLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取控制器的所有配置参数回调（LDC），index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取控制器的所有配置参数回调（LDC），sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 接收LDC实点更新的点信息回调（LDC）
    @Override
    public void realPointDataReceiveLDC(ReceiveMsg<List<PointDataLDC>> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收LDC实点更新的点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        List<PointDataLDC> pointList = msg.getData();

        if (null == pointList || pointList.isEmpty()) {
            log.warn("接收LDC实点更新的点信息回调, 参数 data 不存在");
            return;
        }
        //根据ip查询当前LDC控制器点是否存在
        Controller controller = new Controller();
        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        if (controller.getIp() == null) {
            log.warn("接收LDC实点更新的点信息回调，LDC控制器不存在");
            return;
        }

        Integer id;//id
        Integer value;//实时值

        List<PointDataResponse> pointDataResponseList = new ArrayList<>();

        for (PointDataLDC pointData : pointList) {
            id = pointData.getId();
            value = pointData.getValue();

            if (null == id || null == value) {
                log.warn("接收LDC实点更新的点信息回调, 参数 data 不存在");
                continue;
            }

            PointDataResponse pointDataResponse1 = new PointDataResponse();


            pointDataResponse1.setId(id);
            pointDataResponse1.setValue(String.valueOf(value));

            //根据IP查询点位树缓存
            Point point = new Point();
            Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
            Collection<Point> pointValues = pointCacheMap.values();
            for (Point p : pointValues) {
                if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                    point = p;
                    break;
                }
            }

            if (null == point.getEquipmentId()) {
                log.warn("接收LDC实点更新的点信息回调，实点信息不存在");
                continue;
            }

            pointDataResponse1.setId(Integer.parseInt(String.valueOf(point.getTreeId())));
            pointDataResponse1.setAlias(point.getNickName());
            pointDataResponse1.setName(point.getSysName());
            pointDataResponse1.setSysNameOld("");
            pointDataResponse1.setUnit(point.getEngineerUnit());
            pointDataResponseList.add(pointDataResponse1);


            if (DeviceTreeConstants.BES_AO == Integer.parseInt(point.getNodeType())
                    || DeviceTreeConstants.BES_AI == Integer.parseInt(point.getNodeType())) {

                Integer accuracyNum = Integer.parseInt(String.valueOf(point.getAccuracy()));
                ;//获取精度
                Double valueDouble = value / Math.pow(10, accuracyNum);//获取精度转换后的实时值
                String values = subZeroAndDot(String.valueOf(valueDouble));
                pointDataResponse1.setValue(values);

//

//                boolean isUpdatePoint = pointMapper.updatePoint(point);
//                if (isUpdatePoint) {
//                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);
//                }
                // 更新缓存
                point.setRunVal(values);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);


            } else {

                pointDataResponse1.setValue(String.valueOf(value));
                point.setRunVal(String.valueOf(value));
                //更改缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);

            }
            SceneLinkHandler.deviceTrigger(pointDataResponse1.getId()+"","0",SceneLinkHandler.SCENE_LINK_DEVICEACTION_ATTRIBUTE,pointDataResponse1.getValue());

        }

        alarmHandler.alarmHandle(msg, controller);//报警

        ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
        msg1.setData(pointDataResponseList);
        msg1.setCode(msg.getCode());
        msg1.setIndex(msg.getIndex());
        msg1.setIp(msg.getIp());
        // 推送消息到web客户端

        distributePostEvent(WebSocketEvent.LDC, msg1);
    }

    // 获取一个模块的错误代码回调（LDC）
    @Override
    public void moduleErrorCodeGetLDC(ReceiveMsg<ErrorCodeLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();
        Integer onlineStatus;

        //根据ip查询当前LDC控制器点是否存在

        Integer id = msg.getData().getId();

        Module module = new Module();
        Controller controller = new Controller();

        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        if (controller == null) {
            log.warn("获取一个模块的错误代码回调（LDC）, 当前控制器不存在 ip:" + ip);
            return;
        }

        ReceiveMsg<List<DeviceTree>> msgList = new ReceiveMsg();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取一个模块的错误代码回调（LDC）, index 不存在，或者 ip 不存在");
            return;
        }

        if (null == msg.getData() || null == msg.getData().getErrorCode()) {
            log.warn("获取一个模块的错误代码回调（LDC）, id 不存在");
            return;
        }


        Map<String, Module> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
        Collection<Module> pointValues = pointCacheMap.values();
        for (Module m : pointValues) {
            if (m.getId().intValue() == id && controller.getId() == m.getControllerId().intValue()) {
                module = m;
                break;
            }
        }

        DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(module.getDeviceTreeId());

        if (module.getId() == null || deviceTree == null) {
            log.warn("获取一个模块的错误代码回调（LDC）, 设备树节点不存在");
            return;
        }


        msgList.setIndex(index);
        msgList.setIp(ip);
        msgList.setCode(1);

        if (msg.getData().getErrorCode().equals(0)) {//在线
            onlineStatus = 1;
        } else {//离线
            onlineStatus = 0;
            //储存错误记录至记录表
            ModuleErrorLog moduleErrorLog = new ModuleErrorLog();
            moduleErrorLog.setDeviceTreeId(Integer.parseInt(String.valueOf(module.getDeviceTreeId())));
            moduleErrorLog.setModuleId(Integer.parseInt(String.valueOf(module.getId())));
            moduleErrorLog.setCreateTime(DateUtils.getNowDate());
            moduleErrorLog.setErrCode(msg.getData().getErrorCode().toString());
            boolean isAddErrorLog = pointMapper.addModuleErrorLog(moduleErrorLog);
            if (!isAddErrorLog) {
                log.warn("获取一个模块的错误代码回调（LDC）, 添加错误日志失败");
                return;
            }
        }
        // 更新设备树状态
        //获取模块及其下属所有设备树缓存
        List<DeviceTree> besSbPzStructs = deviceTreeCache.getCascadeSubordinate(deviceTree.getDeviceTreeId());

        // 更新缓存设备树在线状态
        for (DeviceTree d : besSbPzStructs) {
            d.setDeviceTreeStatus(onlineStatus);
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) d.getDeviceTreeId(), d);
            if (d.getDeviceNodeId() == DeviceTreeConstants.BES_MODEL) {
                module.setOnlineState(Long.parseLong(String.valueOf(onlineStatus)));
                //修改数据库
                moduleMapper.updateModule(module);
                //修改缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
            }
        }

        msgList.setData(besSbPzStructs);

        // 推送消息到web客户端
        WebSocketService.broadcast(WebSocketEvent.DEVICE_STATE, msgList);
    }

    //批量接收一个模块的错误代码
    @Override
    public void moduleErrorCodeGetLDCALL(ReceiveMsg<List<ErrorCodeLDC>> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();
        Integer onlineStatus = null;
        ReceiveMsg<List<DeviceTree>> msgList = new ReceiveMsg();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("批量接收一个模块的错误代码（LDC）, index 不存在，或者 ip 不存在");
            return;
        }

        List<ErrorCodeLDC> errorCodeLDC = msg.getData();
        if (null == errorCodeLDC || errorCodeLDC.isEmpty()) {
            log.warn("批量接收一个模块的错误代码, 参数 data 不存在");
            return;
        }

        //根据ip查询当前LDC控制器点是否存在
        Controller con = new Controller();
        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                con = c;
                break;
            }
        }

        if (con == null) {
            log.warn("批量接收一个模块的错误代码，LDC控制器不存在");
            return;
        }

        for (ErrorCodeLDC errorCode : errorCodeLDC) {
            Integer id = errorCode.getId();
            //查找当前id所对应的模块名称
            Module module = new Module();
            Map<String, Module> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
            Collection<Module> pointValues = pointCacheMap.values();
            for (Module m : pointValues) {
                if (m.getId().intValue() == id && con.getId() == m.getControllerId().intValue()) {
                    module = m;
                    break;
                }
            }

            if (module.getId() == null) {
                log.warn("批量接收一个模块的错误代码（LDC）, 模块缓存不存在该模块");
                continue;
            }

            DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(module.getDeviceTreeId());

            if (deviceTree == null) {
                continue;
            }

            msgList.setIp(ip);
            msgList.setIndex(index);

            if (errorCode.getErrorCode().equals(0)) {//在线

                onlineStatus = 1;
            } else {//离线
                onlineStatus = 0;
                //储存错误记录至记录表
                ModuleErrorLog moduleErrorLog = new ModuleErrorLog();
                moduleErrorLog.setDeviceTreeId(Integer.parseInt(String.valueOf(module.getDeviceTreeId())));
                moduleErrorLog.setModuleId(Integer.parseInt(String.valueOf(module.getId())));
                moduleErrorLog.setCreateTime(DateUtils.getNowDate());
                moduleErrorLog.setErrCode(errorCode.getErrorCode().toString());
                boolean isAddErrorLog = pointMapper.addModuleErrorLog(moduleErrorLog);
                if (!isAddErrorLog) {
                    log.warn("批量接收一个模块的错误代码（LDC）, 添加错误日志失败");
                    return;
                }
            }
            // 更新设备树状态
            List<DeviceTree> besSbPzStructs = deviceTreeCache.getCascadeSubordinate(deviceTree.getDeviceTreeId());
            for (DeviceTree d : besSbPzStructs) {
                d.setDeviceTreeStatus(onlineStatus);
                // 更新缓存设备树在线状态
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) d.getDeviceTreeId(), d);
                if (d.getDeviceNodeId() == DeviceTreeConstants.BES_MODEL) {
                    module.setOnlineState(Long.parseLong(String.valueOf(onlineStatus)));
                    //修改数据库
                    moduleMapper.updateModule(module);
                    //修改缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
                }
            }

            msgList.setData(besSbPzStructs);

            // 推送消息到web客户端
            WebSocketService.broadcast(WebSocketEvent.DEVICE_STATE, msgList);
        }
    }

    // 新增加一个逻辑点回调（LDC）
    @Override
    public void pointAddLDC(ReceiveMsg<PointParamLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("新增加一个逻辑点回调接收到的消息, index 不存在，或者 ip 不存在");
            return;
        }

        Integer id;

        if (null == msg.getData() || null == msg.getData().getId()) {
            log.warn("新增加一个逻辑点回调接收到的消息, id 不存在");
            return;
        }

        id = msg.getData().getId();

        //根据设备Id、控制器ip取出缓存
        Point point = new Point();
        Controller controller = new Controller();

        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
        Collection<Point> pointValues = pointCacheMap.values();
        for (Point p : pointValues) {
            if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                point = p;
                break;
            }
        }

        if (point.getEquipmentId() == null) {
            log.warn("新增加一个逻辑点回调接收到的消息, 根据 id 没有查出设备树节点信息");
            return;
        }

        Integer code = msg.getCode();

        if (Code.SUCCEED.equals(code)) {
            //修改同步状态
            point.setSyncState(1);
            point.setDeviceTreeStatus(1);
            boolean isUpdatePoint = pointMapper.updatePoint(point);
            if (isUpdatePoint) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);
                DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(point.getTreeId());
                //回调成功后 将在线状态改为
                deviceTree.setDeviceTreeStatus(1);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTree.getDeviceTreeId(), deviceTree);
                log.warn("新增加一个逻辑点回调接收到的消息, 修改数据库点位状态成功，添加缓存");
//                controllerState(ip, true);
            } else {
                log.warn("新增加一个逻辑点回调接收到的消息, 修改数据库点位状态时失败");
                return;
            }

        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("新增加一个逻辑点回调接收到的消息, sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 设置一个逻辑点的所有参数回调（LDC）
    @Override
    public void pointParamSetLDC(ReceiveMsg<PointParamLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置一个逻辑点的所有参数回调接收到的消息, index 不存在，或者 ip 不存在");
            return;
        }

        Integer id;

        if (null == msg.getData() || null == msg.getData().getId()) {
            log.warn("设置一个逻辑点的所有参数回调接收到的消息, id 不存在");
            return;
        }

        id = msg.getData().getId();

        //根据设备Id、控制器ip取出缓存
        Point point = new Point();
        Controller controller = new Controller();

        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
        Collection<Point> pointValues = pointCacheMap.values();
        for (Point p : pointValues) {
            if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                point = p;
                break;
            }
        }

        if (point.getEquipmentId() == null) {
            log.warn("设置一个逻辑点的所有参数回调接收到的消息, 根据 id 没有查出设备树节点信息");
            return;
        }

        Integer code = msg.getCode();

        if (Code.SUCCEED.equals(code)) {
            //已同步修改同步状态
            point.setSyncState(1);//已同步
            boolean isUpdateState = pointMapper.updatePointSyncState(point);
            if (isUpdateState) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);
                DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(point.getTreeId());
                //回调成功后 将在线状态改为
                deviceTree.setDeviceTreeStatus(1);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTree.getDeviceTreeId(), deviceTree);
                log.warn("设置一个逻辑点的所有参数回调接收到的消息, 修改数据库点位状态成功，添加缓存");
            } else {
                log.warn("设置一个逻辑点的所有参数回调接收到的消息, 修改数据库点位状态时失败");
                return;
            }
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("设置一个逻辑点的所有参数回调接收到的消息, sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 设置一个逻辑点的值回调（LDC）
    @Override
    public void pointValueSetLDC(ReceiveMsg<PointDataLDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("设置一个逻辑点的值回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("设置一个逻辑点的值回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);

        Integer code = msg.getCode();

        if (!Code.SUCCEED.equals(code)) {
            log.warn("设置一个逻辑点的值失败，返回错误码：" + code);
            return;
        }

        Integer id = msg.getData().getId();

        //根据设备Id、控制器ip取出缓存
        Point point = new Point();
        Controller controller = new Controller();

        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
        Collection<Point> pointValues = pointCacheMap.values();
        for (Point p : pointValues) {
            if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                point = p;
                break;
            }
        }

        if (point.getEquipmentId() == null) {
            log.warn("设置一个逻辑点的值回调， 设备树点位不存在");
            return;
        }
        Integer nodeType = Integer.parseInt(point.getNodeType());
        Integer value = 0;
        if (DeviceTreeConstants.BES_VPOINT.equals(nodeType)) {
            Integer workMode = msg.getData().getWorkMode();

            point.setWorkMode((long) workMode);

            boolean isupdatePoint = pointMapper.updatePoint(point);

            if (!isupdatePoint) {
                log.warn("设置一个逻辑点的值回调, 修改数据库点位失败");
                return;
            }

            value = msg.getData().getValue();

            ReceiveMsg<List<PointDataLDC>> msgs = new ReceiveMsg<>();

            msgs.setCode(0);
            msgs.setIndex(LDCCmd.VIRTUAL_POINT_DATA_RECEIVE);
            msgs.setIp(ip);

            List<PointDataLDC> pointDataList = new ArrayList<>();

            PointDataLDC pointData = new PointDataLDC();

            pointData.setId(id);
            pointData.setValue(value);
            pointData.setWorkMode(workMode);


            pointDataList.add(pointData);

            msgs.setData(pointDataList);

            virtualPointDataReceiveLDC(msgs);

        } else {
            Integer workMode = msg.getData().getWorkMode();

            point.setWorkMode((long) workMode);

            boolean isupdatePoint = pointMapper.updatePoint(point);

            if (!isupdatePoint) {
                log.warn("设置一个逻辑点的值回调, 修改数据库点位失败");
                return;
            }

            value = msg.getData().getValue();

            ReceiveMsg<List<PointDataLDC>> msgs = new ReceiveMsg<>();

            msgs.setCode(0);
            msgs.setIndex(LDCCmd.REAL_POINT_DATA_RECEIVE);
            msgs.setIp(ip);

            List<PointDataLDC> pointDataList = new ArrayList<>();

            PointDataLDC pointData = new PointDataLDC();

            pointData.setId(id);
            pointData.setValue(value);
            pointData.setWorkMode(workMode);

            pointDataList.add(pointData);

            msgs.setData(pointDataList);

            realPointDataReceiveLDC(msgs);
        }
    }

    // 设置一个逻辑点的值回调根据名称（LDC）(未使用)
    @Override
    public void pointValueByNameSetLDC(ReceiveMsg<PointParamLDC> msg) {

    }

    // 删除一个逻辑点回调（LDC）
    @Override
    public void pointDeleteLDC(ReceiveMsg<PointParamLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 获取一个逻辑点的所有配置参数回调（LDC）
    @Override
    public void pointParamGetLDC(ReceiveMsg<PointParamLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("获取一个逻辑点的所有配置参数回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取一个逻辑点的所有配置参数回调，sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 获取一个逻辑点的报警信息回调（LDC）
    @Override
    public void pointAlarmDataGetLDC(ReceiveMsg<AlarmPointDataLDC> msg) {
        // 未使用，告警逻辑由上位机实现
    }

    // 接收LDC的全部点信息回调（LDC）
    @Override
    public void pointDataAllReceiveLDC(ReceiveMsg<List<PointDataLDC>> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收LDC的全部点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        List<PointDataLDC> pointList = msg.getData();

        if (null == pointList || pointList.isEmpty()) {
            log.warn("接收LDC的全部点信息回调, 参数 data 不存在");
            return;
        }

        //根据ip查询当前LDC控制器点是否存在
        Controller controller = new Controller();
        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        if (controller.getIp() == null) {
            log.warn("接收LDC的全部点信息回调，LDC控制器不存在");
            return;
        }

        Integer id;//id
        Integer value;//实时值

        List<Map> dataList = new ArrayList<>();

        List<PointDataResponse> pointDataResponseList = new ArrayList<>();

        for (PointDataLDC pointData : pointList) {
            id = pointData.getId();
            value = pointData.getValue();

            if (null == id || null == value) {
                continue;
            }

            PointDataResponse pointDataResponse = new PointDataResponse();

            pointDataResponse.setId(id);

            Point besSbPzStruct = new Point();

            //获取点位信息
            Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
            Collection<Point> pointValues = pointCacheMap.values();

            //计算保存周期
            Date nowDate = new Date();
            for (Point p : pointValues) {
                if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                    besSbPzStruct = p;
                    break;
                }
            }

            if (null == besSbPzStruct.getEquipmentId()) {
                continue;
            }

            pointDataResponse.setId(Integer.parseInt(String.valueOf(besSbPzStruct.getTreeId())));

            /*****************虚点****************/
            if (DeviceTreeConstants.BES_VPOINT == Integer.parseInt(besSbPzStruct.getNodeType())) {
                Point besVirtualPoint = moduleAndPointCache.getPointByDeviceId(besSbPzStruct.getTreeId());

                if (null == besVirtualPoint) {
                    continue;
                }

                String pointType = besVirtualPoint.getVpointType();

                pointDataResponse.setAlias(besVirtualPoint.getNickName());
                pointDataResponse.setName(besVirtualPoint.getSysName());
                pointDataResponse.setSysNameOld("");
                pointDataResponse.setUnit(besVirtualPoint.getEngineerUnit());
                /***VAI VAO****/
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

                    pointDataResponse.setValue(values);

                    // 更新缓存
                    besSbPzStruct.setRunVal(values);
                    //更新实时值缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);

                    if (pointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))
                            && "1".equals(besVirtualPoint.getEnergyStatics().toString())) {

                        Integer savePeriod = controller.getSavePeriod();
                        Date recordUploadPeriod = besVirtualPoint.getRecordUploadPeriod();

                        if (recordUploadPeriod == null) {
                            besVirtualPoint.setRecordUploadPeriod(new Date());
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besVirtualPoint.getTreeId(), besVirtualPoint);
                        }

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long diff = 0;
                        try {
                            long d1 = df.parse(df.format(besVirtualPoint.getRecordUploadPeriod())).getTime();
                            long d2 = df.parse(df.format(nowDate)).getTime();
                            diff = (d2 - d1) / 1000 / 60;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //如果保存周期为空或者保存周期等于能耗上传周期记录
                        if (savePeriod == null || Integer.parseInt(String.valueOf(diff)) >= savePeriod/*0*/) {
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
                                log.warn("接收LDC的全部点信息回调，缓存未取到电表数据");
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
                            besVirtualPoint.setRecordUploadPeriod(nowDate);
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besVirtualPoint.getTreeId(), besVirtualPoint);
                        }
                    }


                }
                /***VDO VDI****/
                else {
                    pointDataResponse.setValue(String.valueOf(value));

                    // 更新缓存
                    besSbPzStruct.setRunVal(String.valueOf(value));
                    //更新实时值缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);
                }
                pointDataResponseList.add(pointDataResponse);

            }

            /*****************实点****************/
            else {

                pointDataResponse.setAlias(besSbPzStruct.getNickName());
                pointDataResponse.setName(besSbPzStruct.getSysName());
                pointDataResponse.setSysNameOld("");
                pointDataResponse.setUnit(besSbPzStruct.getEngineerUnit());

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
                    pointDataResponse.setValue(values);

                    // 更新缓存
                    besSbPzStruct.setRunVal(values);
                    //更新实时值缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);

                    Point besAiPoint = null;
                    Point besAoPoint = null;
                    if (DeviceTreeConstants.BES_AI == Integer.parseInt(besSbPzStruct.getNodeType())) {

                        besAiPoint = moduleAndPointCache.getPointByDeviceId(besSbPzStruct.getTreeId());

                        String energystatics = besAiPoint.getEnergyStatics().toString();

                        if ("1".equals(energystatics)) {
                            Integer savePeriod = controller.getSavePeriod();
                            Date recordUploadPeriod = besAiPoint.getRecordUploadPeriod();

                            if (recordUploadPeriod == null) {
                                besAiPoint.setRecordUploadPeriod(new Date());
                                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besAiPoint.getTreeId(), besAiPoint);
                            }

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            long diff = 0;
                            try {
                                long d1 = df.parse(df.format(besAiPoint.getRecordUploadPeriod())).getTime();
                                long d2 = df.parse(df.format(nowDate)).getTime();
                                diff = (d2 - d1) / 1000 / 60;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //如果保存周期为空或者保存周期等于能耗上传周期记录
                            if (savePeriod == null || Integer.parseInt(String.valueOf(diff)) >= savePeriod/*0*/) {
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
                                    log.warn("接收LDC的全部点信息回调，缓存未取到电表数据");
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
                                besAiPoint.setRecordUploadPeriod(nowDate);
                                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besAiPoint.getTreeId(), besAiPoint);
                            }
                        }

                    } else {
                        besAoPoint = moduleAndPointCache.getPointByDeviceId(besSbPzStruct.getTreeId());

                        String energystatics = besAoPoint.getEnergyStatics().toString();

                        if ("1".equals(energystatics)) {
                            Integer savePeriod = controller.getSavePeriod();
                            Date recordUploadPeriod = besAoPoint.getRecordUploadPeriod();

                            if (recordUploadPeriod == null) {
                                besAoPoint.setRecordUploadPeriod(new Date());
                                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besAoPoint.getTreeId(), besAoPoint);
                            }

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            long diff = 0;
                            try {
                                long d1 = df.parse(df.format(besAoPoint.getRecordUploadPeriod())).getTime();
                                long d2 = df.parse(df.format(nowDate)).getTime();
                                diff = (d2 - d1) / 1000 / 60;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //如果保存周期为空或者保存周期等于能耗上传周期记录
                            if (savePeriod == null || Integer.parseInt(String.valueOf(diff)) >= savePeriod/*0*/) {
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
                                    log.warn("接收LDC的全部点信息回调，缓存未取到电表数据");
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
                                besAoPoint.setRecordUploadPeriod(nowDate);
                                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besAoPoint.getTreeId(), besAoPoint);
                            }
                        }
                    }


                } else {

                    pointDataResponse.setValue(String.valueOf(value));

                    // 更新缓存
                    besSbPzStruct.setRunVal(String.valueOf(value));
                    //更新实时值缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besSbPzStruct.getTreeId(), besSbPzStruct);
                }

                pointDataResponseList.add(pointDataResponse);
            }
            /********场景联动--设备触发--根据点位值触发********/
            SceneLinkHandler.deviceTrigger(pointDataResponse.getId()+"","0",SceneLinkHandler.SCENE_LINK_DEVICEACTION_ATTRIBUTE,pointDataResponse.getValue());


        }
        // 存储虚点能耗数据
        if (!dataList.isEmpty()) {
            EnergyCollectHandler.ammeterDataHandle(dataList, ip);
        }

        alarmHandler.alarmHandle(msg, controller);//报警

        // 推送消息到web客户端
        ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
        msg1.setData(pointDataResponseList);
        msg1.setCode(msg.getCode());
        msg1.setIndex(msg.getIndex());
        msg1.setIp(msg.getIp());

        distributePostEvent(WebSocketEvent.LDC, msg1);
    }

    // 接收虚点信息回调（LDC）
    @Override
    public void virtualPointDataReceiveLDC(ReceiveMsg<List<PointDataLDC>> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收虚点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        List<PointDataLDC> pointList = msg.getData();

        if (null == pointList || pointList.isEmpty()) {
            log.warn("接收虚点信息回调, 参数 data 不存在");
            return;
        }

        //根据ip查询当前LDC控制器点是否存在
        Controller controller = new Controller();
        Map<String, Controller> controllerCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);
        Collection<Controller> controllerValues = controllerCacheMap.values();
        for (Controller c : controllerValues) {
            if (c.getIp().equals(ip)) {
                controller = c;
                break;
            }
        }

        if (controller.getIp() == null) {
            log.warn("接收虚点信息回调，LDC控制器不存在");
            return;
        }
        Integer id;//id
        Integer value;//实时值

        List<PointDataResponse> pointDataResponseList = new ArrayList<>();

        for (PointDataLDC pointData : pointList) {
            id = pointData.getId();
            value = pointData.getValue();

            if (null == id || null == value) {
                continue;
            }

            PointDataResponse pointDataResponse1 = new PointDataResponse();

            pointDataResponse1.setId(id);

            //根据设备Id、控制器ip取出缓存
            Point besVirtualPoint = new Point();

            Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
            Collection<Point> pointValues = pointCacheMap.values();
            for (Point p : pointValues) {
                if (p.getEquipmentId().equals(id) && controller.getId() == p.getControllerId()) {
                    besVirtualPoint = p;
                    break;
                }
            }

            if (null == besVirtualPoint.getEquipmentId()) {
                continue;
            }
            pointDataResponse1.setId(Integer.parseInt(String.valueOf(besVirtualPoint.getTreeId())));
            pointDataResponse1.setAlias(besVirtualPoint.getNickName());
            pointDataResponse1.setName(besVirtualPoint.getSysName());
            pointDataResponse1.setSysNameOld(besVirtualPoint.getSysName());
            pointDataResponse1.setUnit(besVirtualPoint.getEngineerUnit());
            pointDataResponse1.setValue(String.valueOf(value));

            String pointType = besVirtualPoint.getVpointType();


            if (pointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))
                    || pointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {
                Integer accuracyNum = Integer.parseInt(String.valueOf(besVirtualPoint.getAccuracy()));//获取精度
                Double valueDouble = value / Math.pow(10, accuracyNum);//获取精度转换后的实时值
                String values = subZeroAndDot(String.valueOf(valueDouble));

                pointDataResponse1.setValue(values);
                besVirtualPoint.setRunVal(String.valueOf(value));
//                 更新缓存数据
//                besVirtualPoint.setDeviceTreeStatus(1);
//                boolean isUpdate = pointMapper.updatePoint(besVirtualPoint);
//                if (isUpdate) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besVirtualPoint.getTreeId(), besVirtualPoint);
//                }
            } else {

//                besVirtualPoint.setDeviceTreeStatus(1);
//                boolean isUpdate = pointMapper.updatePoint(besVirtualPoint);
//                if (isUpdate) {
//                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point,  besVirtualPoint.getTreeId(), besVirtualPoint);
//                }
                pointDataResponse1.setValue(String.valueOf(value));
                besVirtualPoint.setRunVal(String.valueOf(value));
                //更新缓存数据
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, besVirtualPoint.getTreeId(), besVirtualPoint);

            }

            /********场景联动--设备触发--根据点位值触发********/
            SceneLinkHandler.deviceTrigger(pointDataResponse1.getId()+"","0",SceneLinkHandler.SCENE_LINK_DEVICEACTION_ATTRIBUTE,pointDataResponse1.getValue());


            pointDataResponseList.add(pointDataResponse1);
        }


        alarmHandler.alarmHandle(msg, controller);//报警

        // 推送消息到web客户端
        ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
        msg1.setData(pointDataResponseList);
        msg1.setCode(msg.getCode());
        msg1.setIndex(msg.getIndex());
        msg1.setIp(msg.getIp());

        distributePostEvent(WebSocketEvent.LDC, msg1);

    }

    // 增加一个场景回调（LDC）
    @Override
    public void sceneAddLDC(ReceiveMsg<SceneDataLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收新增场景信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("接收一个场景的所有配置参数回调（LDC），sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 设置一个场景的所有参数（LDC）
    @Override
    public void sceneParamSetLDC(ReceiveMsg<SceneDataLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收场景信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("获取一个场景的所有配置参数回调（LDC），sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 删除一个场景（LDC）
    @Override
    public void sceneDeleteLDC(ReceiveMsg<SceneDataLDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除场景信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        if (!StringUtils.hasText(sessionId)) {
            log.warn("删除一个场景的所有配置参数回调（LDC），sessionId 不存在");
            return;
        }

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    // 获取一个场景的配置信息回调（LDC）
    @Override
    public void sceneParamGetLDC(ReceiveMsg<SceneParamLDC> msg) {

    }

    // 增加一条计划回调（LDC）
    @Override
    public void planAddLDC(ReceiveMsg<PlanParamLDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("新增LDC计划信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        PlanParamLDC planParamLDC = msg.getData();

        if (null == planParamLDC) {
            log.warn("新增LDC计划信息回调, 参数 data 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);

    }

    // 修改一条计划的所有参数回调（LDC）
    @Override
    public void planParamSetLDC(ReceiveMsg<PlanParamLDC> msg) {

        Integer index = msg.getIndex();

        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("同步LDC计划信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        PlanParamLDC planParamLDC = msg.getData();

        if (null == planParamLDC) {
            log.warn("同步LDC计划信息回调, 参数 data 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);

    }

    // 删除一条计划回调（LDC）
    @Override
    public void planDeleteLDC(ReceiveMsg<PlanParamLDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();
        Integer code = msg.getCode();

        if(code == 0){
            PlanParamLDC data = msg.getData();
            //删除操作
            planConfigMapper.deletePlanConfigById(Long.parseLong(String.valueOf(data.getId())));
        }


        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除LDC计划信息回调，index 不存在，或者 ip 不存在");
            return;
        }

//        PlanParamLDC planParamLDC = msg.getData();
//
//        if (null == planParamLDC)
//        {
//            log.warn("删除LDC计划信息回调, 参数 data 不存在");
//            return;
//        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);

    }

    // 获取一条计划的所有参数回调（LDC）
    @Override
    public void planParamGetLDC(ReceiveMsg<PlanParamLDC> msg) {
        Integer index = msg.getIndex();
        String ip = msg.getIp();
        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收LDC计划信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        PlanParamLDC planParamLDC = msg.getData();

        if (null == planParamLDC) {
            log.warn("接收LDC计划信息回调, 参数 data 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    //获取一个场景下的模式信息的回调
    @Override
    public void controlParamLDC(ReceiveMsg<ControlParamLDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("接收LDC场景模式点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        ControlParamLDC controlParamLDC = msg.getData();

        if (null == controlParamLDC) {
            log.warn("接收LDC场景模式点信息回调, 参数 data 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);
        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }

    //新增一个场景下的模式信息的回调
//    @Override
//    public void sceneModeParamAddLDC(ReceiveMsg<SceneModeParamLDC> msg){
//        Integer index = msg.getIndex();
//        String ip = msg.getIp();
//
//        if (null == index || !StringUtils.hasText(ip))
//        {
//            log.warn("新增LDC场景模式点信息回调，index 不存在，或者 ip 不存在");
//            return;
//        }
//
//        SceneModeParamLDC sceneModeParamLDC = msg.getData();
//
//        if (null == sceneModeParamLDC)
//        {
//            log.warn("新增LDC场景模式点信息回调, 参数 data 不存在");
//            return;
//        }
//
//        // 获取当前 sessionId
//        String sessionId = MsgSubPubHandler.pubMsg(index, ip);
//
//        // 推送消息到web客户端
//        WebSocketService.postEvent(sessionId,WebSocketEvent.LDC, msg);
//    }

    //删除一个场景模式(LDC)
    @Override
    public void sceneModeParamDeleteLDC(ReceiveMsg<SceneModeParamLDC> msg) {

        Integer index = msg.getIndex();
        String ip = msg.getIp();

        if (null == index || !StringUtils.hasText(ip)) {
            log.warn("删除LDC场景模式点信息回调，index 不存在，或者 ip 不存在");
            return;
        }

        SceneModeParamLDC sceneModeParamLDC = msg.getData();

        if (null == sceneModeParamLDC) {
            log.warn("删除LDC场景模式点信息回调, 参数 data 不存在");
            return;
        }

        // 获取当前 sessionId
        String sessionId = MsgSubPubHandler.pubMsg(index, ip);

        // 推送消息到web客户端
        WebSocketService.postEvent(sessionId, WebSocketEvent.LDC, msg);
    }


    /**
     * 使用正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;

    }

    //    @Scheduled(cron = "* 0/1 * * * ?")
    public void saveCalculateDataDaily() {


        String event = WebSocketEvent.DDC;
        List<PointDataResponse> pointDataResponseList = new ArrayList<>();

        PointDataResponse pointDataResponse1 = new PointDataResponse();

        pointDataResponse1.setId(111);
        pointDataResponse1.setValue(String.valueOf(2221));

        Point point = new Point();

        pointDataResponse1.setAlias(point.getNickName());
        pointDataResponse1.setName(point.getSysName());
        pointDataResponse1.setSysNameOld("");
        pointDataResponse1.setUnit(point.getEngineerUnit());
        pointDataResponseList.add(pointDataResponse1);

        ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
        msg1.setData(pointDataResponseList);
        msg1.setCode(0);
        msg1.setIndex(24);
        msg1.setIp("10.168.56.222");

        distributePostEvent(event, msg1);
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
