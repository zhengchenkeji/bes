package com.zc.quartz.task;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.ModbusFunctions;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.quartz.domain.SysJob;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisChannelConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.redis.pubsub.RedisPubSub;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.constant.EDCCmd;
import com.zc.connect.business.constant.LDCCmd;
import com.zc.connect.business.constant.PointType;
import com.zc.connect.business.dto.ddc.PointParamDDC;
import com.zc.connect.business.dto.ldc.PointParamLDC;
import com.zc.connect.business.handler.SendMsgHandler;
import com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler.ModbusSendSyncMsgHandler;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import com.zc.efounder.JEnterprise.domain.baseData.ProductFunction;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.mapper.deviceSynchronization.AthenaBesTimeTaskSyncSbMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.BusMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ControllerMapper;
import com.zc.efounder.JEnterprise.service.deviceTree.ControllerService;
import com.zc.efounder.JEnterprise.service.deviceTree.impl.AthenaElectricMeterServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * description:定时任务
 * author: sunshangeng
 * date:2022/11/4 11:52
 */
@Component("besTask")
@Slf4j
public class BesExecute {
    @Resource
    private Scheduler scheduler;

    @Resource
    private AthenaBesTimeTaskSyncSbMapper taskSyncSbMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ControllerService controllerService;

    @Resource
    private AthenaElectricMeterServiceImpl athenaElectricMeterServiceImpl;
    @Resource
    private ModuleAndPointCache moduleAndPointCache;
    @Resource
    private BusMapper busMapper;
    @Resource
    private ControllerMapper controllerMapper;
    /**
     * 自定义第三方设备下发类
     */
    @Resource
    private ModbusSendSyncMsgHandler modbusSendSyncMsgHandler;

    private static final RedisPubSub redisPubSub = SpringUtils.getBean(RedisPubSub.class);


    //采集器
    private Controller controller = null;

    //    执行定时同步设备树
    public void executeTimeTaskSyncInfo(SysJob job) {

        List<String> list = taskSyncSbMapper.selectNodeIdBySyncId(job.getJobId()+"");
        for (String treeid : list) {
            DeviceTree tree=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree,treeid);
            controllerService.synchronizeDDC((long)tree.getDeviceTreeId(),tree.getDeviceType(),false);
        }
        System.out.println("定时同步设备树走了***********************************:sysJob" + JSONObject.toJSONString(job));
    }


    /**
     * @description:执行获取能耗
     * @author: sunshangeng
     * @date: 2023/4/13 17:21
     * @param: []
     * @return: void
     **/
    public void executeObtainEnergy(SysJob job) {
        List<String> list = taskSyncSbMapper.selectNodeIdBySyncId(job.getJobId() + "");
        for (String treeid : list) {
            /**设备ID*/
            String deviceId = treeid.substring(0, treeid.indexOf("_"));
            /**功能id*/
            String functionId = treeid.substring(treeid.indexOf("_") + 1, treeid.length());
            Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, deviceId);
            ProductFunction function = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, functionId);
            if (equipment == null || function == null) {
                /**无法正常查询 设备和方法*/
                log.error("执行定时任务ID为：" + job.getJobId() + ",任务名称为：" + job.getJobName() + "时,获取设备信息或者功能信息失败，无发正常处罚，设备功能ID为：" + treeid);
                break;
            }
            /**获得设备所属的IP和端口*/
            Long productId = equipment.getProductId();
            Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, productId);
            if (product == null) {
                log.error("执行定时任务ID为：" + job.getJobId() + ",任务名称为：" + job.getJobName() + "时,获取产品信息失败：" + treeid);

            }
            String ip = "";
            Integer port = null;
            switch (product.getIotType()) {
                case "0":
                case "1":
                    /**网关设备*/
                    /**直连设备*/
                    ip = equipment.getIpAddress();
                    port = Integer.parseInt(equipment.getPortNum());
                    break;

                case "2":
                    Equipment parentEquipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
                    if (parentEquipment == null) {
                        log.error("执行定时任务ID为：" + job.getJobId() + ",任务名称为：" + job.getJobName() + "时,获取网关设备信息失败：" + treeid);

                    }
                    ip = parentEquipment.getIpAddress();
                    port = Integer.parseInt(parentEquipment.getPortNum());
                    /**网关子设备*/
                    break;

            }

            try {
                modbusSendSyncMsgHandler.issued1(ip, port, equipment, ModbusFunctions.HOLDING_REGISTER, function.getId(), null);
            } catch (NoSuchAlgorithmException e) {
                log.error("采集时出错，", e);
            }

        }
        System.out.println("定时获取能耗***********************************:sysJob" + JSONObject.toJSONString(job));
    }


    /**
     * @description:定时获取第三方设备数据
     * @author: gaojikun
     * @param: []
     * @return: void
     **/
    public void abtainEquipmentDataTask(SysJob job) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String timeStr = sdf.format(new Date());
//        System.out.println("*********执行定时获取第三方设备数据*********"+timeStr);
        //查询所有的子设备
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
        for (Object object : values) {
            Equipment equipment = (Equipment) object;
            if(equipment.getpId() != null){
                //是子设备 查询父设备HOST PORT
                Equipment equipmentParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
                String host = equipmentParent.getIpAddress();
                Integer port = Integer.parseInt(equipmentParent.getPortNum());
                //采集
                Integer functionCodeInt = 3;
                //起始地址 0-E7(231)
                Integer beginAddressInt = 0;
                Integer addressSizeInt = 232;
                try {
                    //根据子设备对应的信息下发采集指令
                    modbusSendSyncMsgHandler.issued2(host, port, equipment, functionCodeInt, beginAddressInt, addressSizeInt, false, null);
                } catch (Exception e) {
                    System.out.println("定时获取第三方设备数据,下发采集指令异常***********************************>  " + e);
                    continue;
                }
            }
        }
    }

    /**
     * @Author:gaojikun
     * @Date: 2023-02-20 9:18
     * @Param:
     * @Description: 每晚1.00进行定时任务，取消订阅
     * @Return: void
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void destroyElectricMeterDataInfoSubscribe() {
        //定时任务每天凌晨一点取消订阅信息
        redisPubSub.unsubscribe(RedisChannelConstants.Meter_PUB_SUB_INFO);
    }


    /**
     * @Author:gaojikun
     * @Date: 2023-02-20 9:18
     * @Param:
     * @Description: 每晚11.59进行定时任务，数据对比
     * @Return: void
     */
    @Scheduled(cron = "00 59 23 * * ?")
//    @Scheduled(cron = "0 */2 * * * ?")
    public void electricMeterDataInfoTask() {
        Date date = new Date();
        System.out.println("RUNNING:***********************************:每晚23点59分执行电表数据同步定时任务");
        //从电表缓存中获取所有电表（包括点位能耗电表）
        Map<String, AthenaElectricMeter> meterList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter);
        if (meterList == null || meterList.size() == 0) {
            System.out.println("ERROR:***********************************:每晚23点59分执行电表数据同步定时任务，未获取到电表数据");
        }
        //订阅信息
        redisPubSub.subscribe(RedisChannelConstants.Meter_PUB_SUB_INFO);

        //遍历电表列表去进行数据对比
        for (AthenaElectricMeter meter : meterList.values()) {
            //先对比
            boolean b = electricMeterDataInfoContrast(meter, date);
            if (!b) {
                //对比失败再同步,同步成功再去对比
                b = electricMeterDataInfoSYNC(meter, controller);
                if (b) {
                    electricMeterDataInfoContrast(meter, date);
                } else {
                    System.out.println("ERROR:***********************************:每晚23点59分执行电表数据同步定时任务3次失败" + meter.getDeviceTreeId());
                    //三次同步失败跳过该电表
                    continue;
                }
            }
        }
        System.out.println("END:***********************************:每晚23点59分执行电表数据同步定时任务");
    }


    /**
     * @Author:gaojikun
     * @Date:2023-02-20 9:36
     * @Param:
     * @Description:电表数据对比
     * @Return:boolean
     */
    public boolean electricMeterDataInfoContrast(AthenaElectricMeter meter, Date date) {
        //            athenaElectricMeterServiceImpl.getMeterInfoParam(meter.getDeviceTreeId().toString());
        if (meter.getDeviceTreeId() == null || meter.getDeviceTreeId() == 0) {
            System.out.println("ERROR:***********************************:每晚23点59分执行电表数据同步定时任务，获取的电表缓存中没有设备ID***********" + meter.getDeviceTreeId());
            return false;
        }
        //总线
        Bus bus = null;

        if (!"1".equals(meter.getType())) {
            bus = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus, meter.getDeviceTreeFatherId());
            if (bus == null) {
                Bus bus1 = busMapper.selectBusInfoByDeviceTreeId(meter.getDeviceTreeFatherId());
                if (bus1 != null) {
                    bus = bus1;
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus, bus1.getDeviceTreeId(), bus1);
                } else {
                    System.out.println("ERROR:***********************************:每晚23点59分执行电表数据同步定时任务，缓存和数据库均未取到总线信息***********" + meter.getDeviceTreeId());
                    return false;
                }
            }
            controller = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, bus.getDeviceTreeFatherId());
            if (controller == null) {
                Controller controller1 = controllerMapper.selectControllerInfoByDeviceTreeId(bus.getDeviceTreeFatherId());
                if (controller1 != null) {
                    controller = controller1;
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller1.getDeviceTreeId(), controller1);
                } else {
                    System.out.println("ERROR:***********************************:每晚23点59分执行电表数据同步定时任务，缓存和数据库均未取到采集器信息***********" + meter.getDeviceTreeId());
                    return false;
                }
            }
            //下发
            boolean sendState = SendMsgHandler.getAmmeterHistoryDataEDC(controller.getIp(), meter.getMeterId().intValue(), date.getDay());
            if (!sendState) {
                //下发失败，进行数据同步
                System.out.println("ERROR:***********************************:每晚23点59分执行电表数据同步定时任务，获取电表数据下发失败，进行数据同步***********" + meter.getDeviceTreeId());
                return false;
            }

            MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_HISTORY_DATA_GET, controller.getIp());
            return true;
        } else {
            Point point = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, meter.getDeviceTreeId());
            //查询该逻辑点所在的控制器的ip
            Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller).values();
            for (Object obj : values) {
                Controller con = (Controller) obj;
                if (con.getId() == point.getControllerId()) {
                    controller = con;
                }
            }
            if (controller == null) {
                System.out.println("ERROR:***********************************:每晚23点59分执行电表数据同步定时任务，缓存和数据库均未取到采集器信息***********" + meter.getDeviceTreeId());
                return false;
            }
            //下发
            boolean sendState = SendMsgHandler.getPointParamDDC(controller.getIp(), point.getEquipmentId());
            if (!sendState) {
                //下发失败，进行数据同步
                System.out.println("ERROR:***********************************:每晚23点59分执行电表数据同步定时任务，获取电表数据下发失败，进行数据同步***********" + meter.getDeviceTreeId());
                return false;
            }
            MsgSubPubHandler.addSubMsg(DDCCmd.POINT_PARAM_GET, controller.getIp());
            return true;
        }
    }

    /**
     * @Author:gaojikun
     * @Date:2023-02-20 9:36
     * @Param:
     * @Description:电表数据同步
     * @Return:
     */
    public boolean electricMeterDataInfoSYNC(AthenaElectricMeter meter, Controller controller) {
        boolean b = false;
        for (int i = 0; i < 3; i++) {
            System.out.println("RUNNING:***********************************:每晚23点59分执行电表数据同步定时任务,第" + (i + 1) + "次");
            //下发失败，进行数据同步
            if (!"1".equals(meter.getType())) {
                athenaElectricMeterServiceImpl.syncMeter(meter);
            } else {
                Point point = moduleAndPointCache.getPointByDeviceId(meter.getDeviceTreeId());
                if (DeviceTreeConstants.BES_VPOINT == Integer.parseInt(point.getNodeType())) {
                    //虚点
                    b = synVirtualPoint(point, controller);
                } else {
                    //实点
                    b = synchronizePoint(point, controller);
                }
                if (b) {
                    System.out.println("SUCCESS:***********************************:每晚23点59分执行电表数据同步定时任务,第" + (i + 1) + "次成功");
                    return b;
                }
            }
        }
        return b;
    }

    /**
     * 同步模块点 gaojikun
     */
    public Boolean synchronizePoint(Point point, Controller con) {

        Integer pointType = null;

        Integer nodeType = Integer.parseInt(point.getNodeType());                               //设备树点类型
        Long treeId = point.getTreeId();                                                        //设备树点ID

        if (nodeType == null || treeId == null) {
            return false;
        }

        if (nodeType.equals(DeviceTreeConstants.BES_DO)) {
            pointType = PointType.POINT_TYPE_LDO;//点类型
        } else if (nodeType.equals(DeviceTreeConstants.BES_DI)) {
            pointType = PointType.POINT_TYPE_LDI;//点类型
        } else if (nodeType.equals(DeviceTreeConstants.BES_AO)) {
            pointType = PointType.POINT_TYPE_LAO;//点类型
        } else if (nodeType.equals(DeviceTreeConstants.BES_AI)) {
            pointType = PointType.POINT_TYPE_LAI;//点类型
        }

        Point pointMap = moduleAndPointCache.getPointByDeviceId(treeId);

        if (pointMap == null) {
            return false;
        }


        Integer id = pointMap.getEquipmentId();                                                    //设备ID
        Integer active = pointMap.getEnabled();                                                 //是否启用
        String name = pointMap.getSysName();                                                    //点的名字,发送到下位机名字
        String alias = pointMap.getNickName();                                                  //别名
        String description = pointMap.getDescription();                                         //描述
        Long moduleIDLong = pointMap.getModuleId();                                             //点所在模块的ID号
        Long channelIndexLong = pointMap.getChannelIndex();                                     //点所在通道索引
        Long workModeLong = pointMap.getWorkMode();                                             //工作模式，手动模式或自动模式
        Long polarityLong = pointMap.getReversed();                                             //极性，正向或反向
        Integer alarmActive = pointMap.getAlarmEnable();                                        //报警是否启用
        Integer alarmType = pointMap.getAlarmType();                                            //报警类型，不报警、标准报警、加强报警
        Integer alarmPriority = pointMap.getAlarmPriority();                                    //报警优先级
        Long lineTypeLong = pointMap.getSinnalType();                                           //有效输入类型，包括0-10V、0-20mA、4-20mA
        String unit = pointMap.getEngineerUnit();                                               //[12]工程单位，如℉、℃、KWh
        Long precisionLong = pointMap.getAccuracy();                                            //精度
        String highRange = pointMap.getMaxVal();                                                //最高阀值
        String lowRange = pointMap.getMinVal();                                                 //最低阀值
        Long alarmHighValueLong = pointMap.getHighLimit();                                      //高限报警值
        Long alarmLowValueLong = pointMap.getLowLimit();                                        //底限报警值
        Long activePassiveLong = pointMap.getSourced();                                         //有源无源

        String initValue = pointMap.getInitVal();                                               //初始值

        if (!nodeType.equals(DeviceTreeConstants.BES_DO) && !nodeType.equals(DeviceTreeConstants.BES_DI)) {
            if (initValue == null) {
                initValue = "0.0";
            }
        } else {
            if (initValue == null) {
                initValue = "0";
            }
        }


        if (nodeType.equals(DeviceTreeConstants.BES_DI)) {
            if (
                    !StringUtils.hasText(name) ||
                            !StringUtils.hasText(alias) ||
                            !StringUtils.hasText(description) ||
                            active == null ||
                            workModeLong == null ||
                            moduleIDLong == null ||
                            polarityLong == null ||
                            initValue == null ||
                            alarmActive == null ||
                            alarmType == null ||
                            alarmPriority == null ||
                            activePassiveLong == null
            ) {
                return false;
            }
        } else if (nodeType.equals(DeviceTreeConstants.BES_DO)) {
            if (
                    !StringUtils.hasText(name) ||
                            !StringUtils.hasText(alias) ||
                            !StringUtils.hasText(description) ||
                            active == null ||
                            workModeLong == null ||
                            moduleIDLong == null ||
                            polarityLong == null ||
                            initValue == null ||
                            alarmActive == null ||
                            alarmType == null ||
                            alarmPriority == null

            ) {
                return false;
            }
        } else if (nodeType.equals(DeviceTreeConstants.BES_AI)) {
            if (
                    !StringUtils.hasText(name) ||
                            !StringUtils.hasText(unit) ||
                            !StringUtils.hasText(alias) ||
                            !StringUtils.hasText(description) ||
                            active == null ||
                            workModeLong == null ||
                            moduleIDLong == null ||
                            polarityLong == null ||
                            initValue == null ||
                            alarmActive == null ||
                            alarmType == null ||
                            alarmPriority == null ||
                            lineTypeLong == null ||
                            highRange == null ||
                            lowRange == null ||
                            precisionLong == null
            ) {
                return false;
            }
        } else {
            if (
                    !StringUtils.hasText(name) ||
                            !StringUtils.hasText(unit) ||
                            !StringUtils.hasText(alias) ||
                            !StringUtils.hasText(description) ||
                            active == null ||
                            workModeLong == null ||
                            moduleIDLong == null ||
                            polarityLong == null ||
                            initValue == null ||
                            alarmActive == null ||
                            alarmType == null ||
                            alarmPriority == null ||
                            lineTypeLong == null ||
                            highRange == null ||
                            lowRange == null ||
                            precisionLong == null
            ) {
                return false;
            }
        }

        Integer moduleID = moduleIDLong == null ? null : Integer.parseInt(String.valueOf(moduleIDLong));                           //点所在模块的ID号
        Integer channelIndex = channelIndexLong == null ? null : Integer.parseInt(String.valueOf(channelIndexLong));               //点所在通道索引
        Integer workMode = workModeLong == null ? null : Integer.parseInt(String.valueOf(workModeLong));                           //工作模式，手动模式或自动模式
        Integer polarity = polarityLong == null ? null : Integer.parseInt(String.valueOf(polarityLong));                           //极性，正向或反向
        Integer precision = precisionLong == null ? null : Integer.parseInt(String.valueOf(precisionLong));                        //精度
        Integer alarmHighValue = alarmHighValueLong == null ? null : Integer.parseInt(String.valueOf(alarmHighValueLong));         //高限报警值
        Integer alarmLowValue = alarmLowValueLong == null ? null : Integer.parseInt(String.valueOf(alarmLowValueLong));            //底限报警值
        Integer activePassive = activePassiveLong == null ? null : Integer.parseInt(String.valueOf(activePassiveLong));            //有源无源

        if (nodeType.equals(DeviceTreeConstants.BES_AI) || nodeType.equals(DeviceTreeConstants.BES_AO)) {
            if (alarmHighValue == null || alarmLowValue == null) {
                alarmHighValue = 0;
                alarmLowValue = 0;
            }
        }

        Integer alarmTrigger = pointMap.getCloseState();//报警触发  开或关

        Integer initVal;

        if (null == precision) {
            initVal = Integer.parseInt(initValue);
        } else {
            initVal = (int) Math.round(Double.parseDouble(initValue) * (Math.pow(10, (precision))));
        }

        Integer highRangeVal = null;

        if (highRange != null) {
            highRangeVal = (int) Math.round(Double.parseDouble(highRange) * (Math.pow(10, (precision))));
        }

        Integer lowRangeVal = null;

        if (lowRange != null) {
            lowRangeVal = (int) Math.round(Double.parseDouble(lowRange) * (Math.pow(10, (precision))));
        }

        String ip = con.getIp();

        if (DeviceTreeConstants.BES_DDCNODE == con.getType()) {//楼控

            PointParamDDC pointParam = new PointParamDDC();

            if (nodeType.equals(DeviceTreeConstants.BES_DI)) {

                pointParam.setPointType(pointType);
                pointParam.setId(id);
                pointParam.setActive(active);
                pointParam.setName(name);
                pointParam.setAlias(alias);
                pointParam.setDescription(description);
                pointParam.setModuleID(moduleID);
                pointParam.setChannelIndex(channelIndex);
                pointParam.setWorkMode(workMode);
                pointParam.setPolarity(polarity);
                pointParam.setInitValue(Integer.parseInt(initValue));
                pointParam.setAlarmActive(alarmActive);
                pointParam.setAlarmType(alarmType);
                pointParam.setAlarmPriority(alarmPriority);
                pointParam.setAlarmTrigger(alarmTrigger);
                pointParam.setActivePassive(activePassive);
            } else if (nodeType.equals(DeviceTreeConstants.BES_DO)) {

                pointParam.setPointType(pointType);
                pointParam.setId(id);
                pointParam.setActive(active);
                pointParam.setName(name);
                pointParam.setAlias(alias);
                pointParam.setDescription(description);
                pointParam.setModuleID(moduleID);
                pointParam.setChannelIndex(channelIndex);
                pointParam.setWorkMode(workMode);
                pointParam.setPolarity(polarity);
                pointParam.setInitValue(Integer.parseInt(initValue));
                pointParam.setAlarmActive(alarmActive);
                pointParam.setAlarmType(alarmType);
                pointParam.setAlarmPriority(alarmPriority);
                pointParam.setAlarmTrigger(alarmTrigger);

            } else if (nodeType.equals(DeviceTreeConstants.BES_AI)) {
                Integer lineType = Integer.parseInt(String.valueOf(lineTypeLong));                      //有效输入类型，包括0-10V、0-20mA、4-20mA

                pointParam.setPointType(pointType);
                pointParam.setId(id);
                pointParam.setActive(active);
                pointParam.setName(name);
                pointParam.setAlias(alias);
                pointParam.setDescription(description);
                pointParam.setModuleID(moduleID);
                pointParam.setChannelIndex(channelIndex);
                pointParam.setWorkMode(workMode);
                pointParam.setPolarity(polarity);
                pointParam.setInitValue(initVal);
                pointParam.setAlarmActive(alarmActive);
                pointParam.setAlarmType(alarmType);
                pointParam.setAlarmPriority(alarmPriority);
                pointParam.setLineType(lineType);
                pointParam.setUnit(unit);
                pointParam.setHighRange(highRangeVal);
                pointParam.setLowRange(lowRangeVal);
                pointParam.setPrecision(precision);
                pointParam.setAlarmHighValue(alarmHighValue);
                pointParam.setAlarmLowValue(alarmLowValue);
            } else {
                Integer lineType = Integer.parseInt(String.valueOf(lineTypeLong));                      //有效输入类型，包括0-10V、0-20mA、4-20mA

                pointParam.setPointType(pointType);
                pointParam.setId(id);
                pointParam.setActive(active);
                pointParam.setName(name);
                pointParam.setAlias(alias);
                pointParam.setDescription(description);
                pointParam.setModuleID(moduleID);
                pointParam.setChannelIndex(channelIndex);
                pointParam.setWorkMode(workMode);
                pointParam.setPolarity(polarity);
                pointParam.setInitValue(initVal);
                pointParam.setAlarmActive(alarmActive);
                pointParam.setAlarmType(alarmType);
                pointParam.setAlarmPriority(alarmPriority);
                pointParam.setLineType(lineType);
                pointParam.setUnit(unit);
                pointParam.setHighRange(highRangeVal);
                pointParam.setLowRange(lowRangeVal);
                pointParam.setPrecision(precision);
                pointParam.setAlarmHighValue(alarmHighValue);
                pointParam.setAlarmLowValue(alarmLowValue);
            }


            // 同步数据到下位机
            boolean sendState = SendMsgHandler.setPointDDC(ip, pointParam);

            if (!sendState) {
                return false;
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.POINT_PARAM_SET, ip);

            return true;

        } else if (DeviceTreeConstants.BES_ILLUMINE == con.getType()) {//照明


            PointParamLDC pointParam = new PointParamLDC();

            if (nodeType.equals(DeviceTreeConstants.BES_DI)) {

                pointParam.setPointType(pointType);
                pointParam.setId(id);
                pointParam.setActive(active);
                pointParam.setName(name);
                pointParam.setAlias(alias);
                pointParam.setDescription(description);
                pointParam.setModuleID(moduleID);
                pointParam.setChannelIndex(channelIndex);
                pointParam.setWorkMode(workMode);
                pointParam.setPolarity(polarity);
                pointParam.setInitValue(Integer.parseInt(initValue));
                pointParam.setAlarmActive(alarmActive);
                pointParam.setAlarmType(alarmType);
                pointParam.setAlarmPriority(alarmPriority);
                pointParam.setAlarmTrigger(alarmTrigger);
                pointParam.setActivePassive(activePassive);
            } else if (nodeType.equals(DeviceTreeConstants.BES_DO)) {

                pointParam.setPointType(pointType);
                pointParam.setId(id);
                pointParam.setActive(active);
                pointParam.setName(name);
                pointParam.setAlias(alias);
                pointParam.setDescription(description);
                pointParam.setModuleID(moduleID);
                pointParam.setChannelIndex(channelIndex);
                pointParam.setWorkMode(workMode);
                pointParam.setPolarity(polarity);
                pointParam.setInitValue(Integer.parseInt(initValue));
                pointParam.setAlarmActive(alarmActive);
                pointParam.setAlarmType(alarmType);
                pointParam.setAlarmPriority(alarmPriority);
                pointParam.setAlarmTrigger(alarmTrigger);

            } else if (nodeType.equals(DeviceTreeConstants.BES_AI)) {
                Integer lineType = Integer.parseInt(String.valueOf(lineTypeLong));                      //有效输入类型，包括0-10V、0-20mA、4-20mA

                pointParam.setPointType(pointType);
                pointParam.setId(id);
                pointParam.setActive(active);
                pointParam.setName(name);
                pointParam.setAlias(alias);
                pointParam.setDescription(description);
                pointParam.setModuleID(moduleID);
                pointParam.setChannelIndex(channelIndex);
                pointParam.setWorkMode(workMode);
                pointParam.setPolarity(polarity);
                pointParam.setInitValue(initVal);
                pointParam.setAlarmActive(alarmActive);
                pointParam.setAlarmType(alarmType);
                pointParam.setAlarmPriority(alarmPriority);
                pointParam.setLineType(lineType);
                pointParam.setUnit(unit);
                pointParam.setHighRange(highRangeVal);
                pointParam.setLowRange(lowRangeVal);
                pointParam.setPrecision(precision);
                pointParam.setAlarmHighValue(alarmHighValue);
                pointParam.setAlarmLowValue(alarmLowValue);
            } else {
                Integer lineType = Integer.parseInt(String.valueOf(lineTypeLong));                      //有效输入类型，包括0-10V、0-20mA、4-20mA

                pointParam.setPointType(pointType);
                pointParam.setId(id);
                pointParam.setActive(active);
                pointParam.setName(name);
                pointParam.setAlias(alias);
                pointParam.setDescription(description);
                pointParam.setModuleID(moduleID);
                pointParam.setChannelIndex(channelIndex);
                pointParam.setWorkMode(workMode);
                pointParam.setPolarity(polarity);
                pointParam.setInitValue(initVal);
                pointParam.setAlarmActive(alarmActive);
                pointParam.setAlarmType(alarmType);
                pointParam.setAlarmPriority(alarmPriority);
                pointParam.setLineType(lineType);
                pointParam.setUnit(unit);
                pointParam.setHighRange(highRangeVal);
                pointParam.setLowRange(lowRangeVal);
                pointParam.setPrecision(precision);
                pointParam.setAlarmHighValue(alarmHighValue);
                pointParam.setAlarmLowValue(alarmLowValue);
            }

            // 同步数据到下位机
            boolean sendState = SendMsgHandler.setPointLDC(ip, pointParam);

            if (!sendState) {
                return false;
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.POINT_PARAM_SET, ip);
            return true;
        } else {
            return false;
        }

    }


    /**
     * 同步虚点信息 gaojikun
     */
    public Boolean synVirtualPoint(Point point, Controller con) {

        Long treeId = point.getTreeId();                                                        //设备树点ID

        if (treeId == null) {
            return false;
        }

        Point virtualPoint = moduleAndPointCache.getPointByDeviceId(treeId);

        if (virtualPoint == null) {
            return false;
        }

        String channelId = con.getIp();

        String id = String.valueOf(virtualPoint.getTreeId());

        Integer pointType = Integer.parseInt(virtualPoint.getVpointType());                     //虚点类型
        Integer active = virtualPoint.getEnabled();                                             //使能
        String name = virtualPoint.getSysName();                                                //系统名称
        String alias = virtualPoint.getNickName();                                              //别名
        String description = virtualPoint.getDescription();                                     //描述
        String initValue = virtualPoint.getInitVal();                                           //初始值
        Integer alarmActive = virtualPoint.getAlarmEnable();                                    //报警使能
        Integer alarmType = virtualPoint.getAlarmType();                                        //报警类型
        Integer alarmPriority = virtualPoint.getAlarmPriority();                                //报警优先级
        Long precision = virtualPoint.getAccuracy();                                            //精度

        Integer initVal;
        Integer precisionInt = 0;
        if (null == precision) {
            initVal = Integer.parseInt(initValue);
        } else {
            precisionInt = Integer.parseInt(String.valueOf(precision));
            initVal = (int) Math.round(Double.parseDouble(initValue) * (Math.pow(10, (precisionInt))));
        }

        if (con.getType() == DeviceTreeConstants.BES_DDCNODE) { //楼控

            if (pointType.toString().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))) {
                pointType = PointType.POINT_TYPE_VIRTUAL_AI;
            } else if (pointType.toString().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {
                pointType = PointType.POINT_TYPE_VIRTUAL_AO;
            } else if (pointType.toString().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI))) {
                pointType = PointType.POINT_TYPE_VIRTUAL_DI;
            } else if (pointType.toString().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDO))) {
                pointType = PointType.POINT_TYPE_VIRTUAL_DO;
            }

            PointParamDDC pointParam = new PointParamDDC();

            pointParam.setPointType(pointType);
            pointParam.setId(virtualPoint.getEquipmentId());
            pointParam.setActive(active);
            pointParam.setName(name);
            pointParam.setAlias(alias);
            pointParam.setDescription(description);
            pointParam.setInitValue(initVal);
            pointParam.setAlarmActive(alarmActive);
            pointParam.setAlarmType(alarmType);
            pointParam.setAlarmPriority(alarmPriority);

            if (PointType.POINT_TYPE_VIRTUAL_AI == pointType || PointType.POINT_TYPE_VIRTUAL_AO == pointType) {

                pointParam.setUnit(virtualPoint.getEngineerUnit());
                pointParam.setPrecision(precisionInt);
                if ("1".equals(String.valueOf(virtualPoint.getAlarmType()))) {
                    pointParam.setAlarmHighValue(Integer.parseInt(String.valueOf(virtualPoint.getHighLimit())));
                    pointParam.setAlarmLowValue(Integer.parseInt(String.valueOf(virtualPoint.getLowLimit())));
                } else {
                    pointParam.setAlarmHighValue(0);
                    pointParam.setAlarmLowValue(0);
                }


            } else {
                pointParam.setAlarmTrigger(virtualPoint.getCloseState());
            }

            boolean sendState = SendMsgHandler.setPointDDC(channelId, pointParam);

            if (!sendState) {
                return false;
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.POINT_PARAM_SET, channelId);

            return true;

        } else if (con.getType() == DeviceTreeConstants.BES_ILLUMINE) {//照明
            if (pointType.toString().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))) {
                pointType = PointType.POINT_TYPE_VIRTUAL_AI;
            } else if (pointType.toString().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {
                pointType = PointType.POINT_TYPE_VIRTUAL_AO;
            } else if (pointType.toString().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI))) {
                pointType = PointType.POINT_TYPE_VIRTUAL_DI;
            } else if (pointType.toString().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDO))) {
                pointType = PointType.POINT_TYPE_VIRTUAL_DO;
            }

            PointParamLDC pointParamLDC = new PointParamLDC();

            pointParamLDC.setPointType(pointType);
            pointParamLDC.setId(virtualPoint.getEquipmentId());
            pointParamLDC.setActive(active);
            pointParamLDC.setName(name);
            pointParamLDC.setAlias(alias);
            pointParamLDC.setDescription(description);
            pointParamLDC.setInitValue(initVal);
            pointParamLDC.setAlarmActive(alarmActive);
            pointParamLDC.setAlarmType(alarmType);
            pointParamLDC.setAlarmPriority(alarmPriority);

            if (PointType.POINT_TYPE_VIRTUAL_AI == pointType || PointType.POINT_TYPE_VIRTUAL_AO == pointType) {

                pointParamLDC.setUnit(virtualPoint.getEngineerUnit());
                pointParamLDC.setPrecision(precisionInt);
                if ("1".equals(String.valueOf(virtualPoint.getAlarmType()))) {
                    pointParamLDC.setAlarmHighValue(Integer.parseInt(String.valueOf(virtualPoint.getHighLimit())));
                    pointParamLDC.setAlarmLowValue(Integer.parseInt(String.valueOf(virtualPoint.getLowLimit())));
                } else {
                    pointParamLDC.setAlarmHighValue(0);
                    pointParamLDC.setAlarmLowValue(0);
                }
            } else {
                pointParamLDC.setAlarmTrigger(virtualPoint.getCloseState());
            }

            boolean sendState = SendMsgHandler.setPointLDC(channelId, pointParamLDC);

            if (!sendState) {
                return false;
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.POINT_PARAM_SET, channelId);

            return true;
        } else {
            return false;
        }
    }

}
