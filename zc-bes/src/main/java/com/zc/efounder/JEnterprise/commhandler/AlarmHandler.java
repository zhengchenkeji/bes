package com.zc.efounder.JEnterprise.commhandler;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.result.ResultMap;
import com.zc.ApplicationContextProvider;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.AthenaBesHouseholdConfig;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.domain.entity.SubitemConfig;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.sms.model.SmsParam;
import com.ruoyi.common.utils.sms.server.EmailServer;
import com.ruoyi.common.utils.sms.server.SmsServer;
import com.zc.common.constant.NoticeTableConstants;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.ElectricCollRlglCache;
import com.zc.efounder.JEnterprise.Cache.MeterCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.safetyWarning.*;
import com.zc.efounder.JEnterprise.mapper.energyCollection.ElectricParamsMapper;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBranchConfigMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBesHouseholdConfigMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.SubitemConfigMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.*;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.constant.WebSocketEvent;
import com.zc.common.core.model.DataReception;
import com.zc.common.core.websocket.WebSocketService;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.business.dto.ddc.PointDataDDC;
import com.zc.connect.business.dto.edc.AmmeterData;
import com.zc.connect.business.dto.ldc.PointDataLDC;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

/**
 * 告警处理类
 *
 * @author wanghongjie
 * @date 2020/6/12 8:08
 */
public class AlarmHandler {

    private static final Logger log = LoggerFactory.getLogger(AlarmHandler.class);

    static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

    //redis
    private RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);
    //获取树缓存
    private DeviceTreeCache deviceTreeCache = ApplicationContextProvider.getBean(DeviceTreeCache.class);
    //获取模块及点位缓存
    private ModuleAndPointCache moduleAndPointCache = ApplicationContextProvider.getBean(ModuleAndPointCache.class);

    //获取支路mapper
    private AthenaBranchConfigMapper branchConfigMapper = ApplicationContextProvider.getBean(AthenaBranchConfigMapper.class);
    //获取分户mapper
    private AthenaBesHouseholdConfigMapper householdConfigMapper = ApplicationContextProvider.getBean(AthenaBesHouseholdConfigMapper.class);
    //获取分项mapper
    private SubitemConfigMapper subitemConfigMapper = ApplicationContextProvider.getBean(SubitemConfigMapper.class);
    //获取采集参数mapper
    private ElectricParamsMapper electricParamsMapper = ApplicationContextProvider.getBean(ElectricParamsMapper.class);
    //告警策略
    private AlarmTacticsMapper alarmTacticsMapper = ApplicationContextProvider.getBean(AlarmTacticsMapper.class);
    //告警策略
    private AlarmNotificationRecordMapper notificationRecordMapper = ApplicationContextProvider.getBean(AlarmNotificationRecordMapper.class);

    //告警实时数据
    private AlarmRealtimeDataMapper alarmRealtimeDataMapper = ApplicationContextProvider.getBean(AlarmRealtimeDataMapper.class);

    //电表缓存
    private MeterCache meterCache = ApplicationContextProvider.getBean(MeterCache.class);
    //发送邮件
    private EmailServer emailServer = ApplicationContextProvider.getBean(EmailServer.class);
    //发送短信
    private SmsServer smsServer = ApplicationContextProvider.getBean(SmsServer.class);

    //采集方案 采集参数关系缓存
    private ElectricCollRlglCache collRlglCache = ApplicationContextProvider.getBean(ElectricCollRlglCache.class);
    //告警工单
    private AlarmWorkOrderMapper workOrderMapper = ApplicationContextProvider.getBean(AlarmWorkOrderMapper.class);
    //通知配置和告警策略关系
    private AlarmNoticeLinkMapper noticeLinkMapper = ApplicationContextProvider.getBean(AlarmNoticeLinkMapper.class);

    /**
     * 照明和楼控控制器告警处理
     *
     * @param msg 消息体
     * @Author qindehua
     * @Date 2022/11/03
     **/
    public <T> void alarmHandle(ReceiveMsg<List<T>> msg, Controller controller) {

        List<T> pointList = msg.getData();

        Integer alarmValue = null; //闭合或者断开
        Integer alarmFaultValue = null; //故障
        Boolean alarmYesOrNo = false; //是否有报警消息插入到实时报警表,初始值为false

        for (T pointData : pointList) {
            //获取点位的id
            Integer pointId;
            //获取点位的value
            Integer pointValue;

            if (pointData instanceof PointDataDDC) {
                pointId = ((PointDataDDC) pointData).getId();
                pointValue = ((PointDataDDC) pointData).getValue();
            } else if (pointData instanceof PointDataLDC) {
                pointId = ((PointDataLDC) pointData).getId();
                pointValue = ((PointDataLDC) pointData).getValue();
            } else {
                continue;
            }

            if (pointId == 0) {
                continue;
            }
            //根据设备Id、控制器id取出缓存
            Point point = new Point();

            Map<String, Point> pointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
            Collection<Point> pointValues = pointCacheMap.values();
            for (Point p : pointValues) {
                if (p.getEquipmentId().equals(pointId) && controller.getId() == p.getControllerId()) {
                    point = p;
                    break;
                }
            }

            if (null == point || null == point.getEquipmentId()) {
                continue;
            }


            if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDO)) ||
                    point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI)) ||
                    Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_DO || Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_DI
            ) {//VDO,VDI,DI,DO点
                //闭合报警  默认闭合
                if (point.getCloseState() == 0) {
                    alarmValue = 255;
                }
                //断开报警
                else if (point.getCloseState() == 1) {
                    alarmValue = 0;
                }
                //使能状态是使能,报警使能状态是使能   才进行以下操作
                if (point.getAlarmEnable() == 1 && point.getEnabled() == 1) {
                    if (pointValue.equals(alarmValue)) {
                        alarmHandling(point, pointValue, controller);
                    }
                    /*************故障状态为  是 ***************/
                    if (point.getFaultState() == 1) {
                        alarmFaultValue = 100;
                        if (pointValue.equals(alarmFaultValue)) {
                            //报警 统一处理
                            alarmHandling(point, pointValue, controller);
                        }
                    }
                } else {
                    continue;
                }
            } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO)) ||
                    point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI)) ||
                    Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_AI || Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_AO) {//VAO,VAI,AI,AO点


                if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI)) ||
                        Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_AI) {//如果点位的能耗统计开启,则走能耗报警逻辑
                    if (point.getEnergyStatics() == 1) {

                        PointAlarmHandleAI(point, alarmYesOrNo, pointValue);
                    }
                }
                //AO,AI点的值需要进行精度换算 使能状态是使能,报警使能状态是使能   才进行以下操作
                if (Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_AI || Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_AO) {
                    if (point.getAlarmEnable() == 1 && point.getEnabled() == 1) {
                        Integer accuracyNum = Integer.parseInt(String.valueOf(point.getAccuracy()));//获取精度
                        Double valueDouble = pointValue / Math.pow(10, accuracyNum);//获取精度转换后的实时值
                        //当前值大于高限 或者小于底限的时候  报警
                        if (valueDouble > Double.valueOf(point.getMaxVal()) || valueDouble < Double.valueOf(point.getMinVal())) {
                            //报警 统一处理
                            alarmHandling(point, pointValue, controller);
                        }
                    }
                }
            }
        }

    }

    /**
     * 报警统一处理
     *
     * @param point      点
     * @param pointValue 点值
     * @param controller 控制器
     * @Author qindehua
     * @Date 2022/11/07
     **/
    public void alarmHandling(Point point, Integer pointValue, Controller controller) {
        String alarmName = "";//报警名称
        String moduleName = "";//模块名称
        String ip = controller.getIp();//ip地址
        String controllerName = controller.getSysName();//DDC名称
        Boolean alarmYesOrNo = false;//是否有报警消息插入到实时报警表,初始值为false
        String pointName = point.getSysName();//点位名称
        String leave = null;  //报警等级
        //要添加的报警信息
        AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData();

        //查询点位所属的模块名称
        if (Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_VPOINT) {
            moduleName = "虚点";
        } else {
            DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(point.getTreeId());
            if (deviceTree == null) {
                log.warn("树节点为空！");
                return;
            }
            Module module = moduleAndPointCache.getModuleByDeviceId((long) deviceTree.getDeviceTreeFatherId());
            if (module == null) {
                log.warn("模块为空！");
                return;
            }
            moduleName = module.getSysName();
        }

        alarmRealtimeData.setAzwz("控制器" + ":" + controllerName + "," + moduleName + "(" + ip + ")" + "(" + pointName + ")");//报警位置(控制器:   ,模块:    (ip))
        alarmRealtimeData.setAlarmTypeId("5");//信息类型  1:电表  2:支路  3:分户 4:分项 5:设备报警

        if (point.getAlarmPriority() == 0) {
            leave = "1";
        } else if (point.getAlarmPriority() == 1) {
            leave = "2";
        } else if (point.getAlarmPriority() == 2) {
            leave = "3";
        }

        alarmRealtimeData.setLevel(leave);

        if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDO)) ||
                point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI)) ||
                Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_DO || Integer.parseInt(point.getNodeType()) == (DeviceTreeConstants.BES_DI)
        ) {//VDO,VDI,DI,DO点
            if (pointValue == 255) {
                alarmName = "闭合报警";
            } else if (pointValue == 0) {
                alarmName = "断开报警";
            } else {
                //如果故障报警选择是的情况下,那么下位机上来的数据返回的是故障状态做报警提示,故障报警选择否的情况下,则不对数据做报警判断
                if (point.getFaultState() == 1) {
                    alarmName = "故障报警";
                } else {
                    return;
                }
            }
            alarmYesOrNo = true;
            alarmRealtimeData.setAlarmValue(String.valueOf(pointValue));//实际值
            alarmRealtimeData.setPlanVal(String.valueOf(pointValue));//计划值
            alarmRealtimeData.setAlarmName(alarmName);//报警名称
            alarmRealtimeData.setPromptMsg(pointName + "(" + alarmName + ")");//提示信息

            //判断是否存在相同的未处理报警及插入或修改数据
            insertAlarmRealtime(alarmRealtimeData);
        } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO)) ||
                point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI)) ||
                Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_AI || Integer.parseInt(point.getNodeType()) == DeviceTreeConstants.BES_AO) {//VAO,VAI,AI,AO点
            Integer accuracyNum = Integer.parseInt(String.valueOf(point.getAccuracy()));//获取精度
            Double valueDouble = pointValue / Math.pow(10, accuracyNum);//获取精度转换后的实时值

            float maxVal = Float.valueOf(point.getMaxVal());//最大值
            float minVal = Float.valueOf(point.getMinVal());//最小值
            alarmRealtimeData.setAlarmValue(String.valueOf(valueDouble));//实际值
            //当前值大于高限
            if (valueDouble > maxVal) {
                alarmYesOrNo = true;
                alarmRealtimeData.setPlanVal(minVal + "--" + maxVal);//计划值
                alarmRealtimeData.setAlarmName("高限报警");
                alarmRealtimeData.setPromptMsg(pointName + "(实际参数大于计划值)");//提示信息

                //判断是否存在相同的未处理报警及插入或修改数据
                insertAlarmRealtime(alarmRealtimeData);
            }
            //小于底限的时候
            else if (valueDouble < minVal) {
                alarmYesOrNo = true;
                alarmRealtimeData.setPlanVal(minVal + "--" + maxVal);//计划值
                alarmRealtimeData.setAlarmName("低限报警");
                alarmRealtimeData.setPromptMsg(pointName + "(实际参数小于计划值)");//提示信息

                //判断是否存在相同的未处理报警及插入或修改数据
                insertAlarmRealtime(alarmRealtimeData);
            }
        }
        Map<String, Object> msgMap = new HashedMap();

        if (alarmYesOrNo == true) {
            //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
            DataReception dataReception = getNoRecoverCount();//查询报警的条数

            msgMap.put("alarmRealtimeCount", dataReception.getData());

            // 推送消息到web客户端
            WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
            WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

        }
    }

    /**
     * 插入报警实时数据  设备报警
     *
     * @param alarmRealtimeData
     * @Author qindehua
     * @Date 2022/11/04
     **/
    private void insertAlarmRealtime(AlarmRealtimeData alarmRealtimeData) {
        //根据告警位置和提示信息 查询报警实时数据
        AlarmRealtimeData data = alarmRealtimeDataMapper.selectAlarmRealtimeData(alarmRealtimeData);

        //插入数据
        if (data == null) {
            alarmRealtimeData.setCreateTime(DateUtils.getNowDate());//创建时间
            alarmRealtimeData.setFirstTime(DateUtils.getNowDate());//第一次产生的时间
            alarmRealtimeData.setLastTime(alarmRealtimeData.getFirstTime());//最后一次产生的时间
            alarmRealtimeData.setAmount(1);//告警次数
            alarmRealtimeDataMapper.insertAlarmRealtimeData(alarmRealtimeData);
        }
        //更新数据
        else {
            alarmRealtimeData.setUpdateTime(DateUtils.getNowDate());//修改时间
            alarmRealtimeData.setLastTime(DateUtils.getNowDate());//最后一次产生的时间
            alarmRealtimeData.setAmount(data.getAmount() + 1);//告警次数
            alarmRealtimeDataMapper.updateAlarmRealtimeData(alarmRealtimeData);
        }
    }

    /**
     * 插入报警实时数据  能耗报警
     *
     * @param alarmRealtimeData
     * @Author qindehua
     * @Date 2022/11/04
     **/
    private void insertAlarmRealtimeByAlarmTacticsId(AlarmRealtimeData alarmRealtimeData) {
        //根据告警位置和提示信息 查询报警实时数据
        AlarmRealtimeData data = alarmRealtimeDataMapper.selectAlarmRealtimeDataByAlarmTacticsId(alarmRealtimeData.getAlarmTacticsId());
        //新增数据
        if (data == null) {
            alarmRealtimeData.setCreateTime(DateUtils.getNowDate());//修改时间
            alarmRealtimeData.setFirstTime(DateUtils.getNowDate());//第一次产生的时间
            alarmRealtimeData.setLastTime(alarmRealtimeData.getFirstTime());//最后一次产生的时间
            alarmRealtimeData.setAmount(1);//告警次数
            alarmRealtimeDataMapper.insertAlarmRealtimeData(alarmRealtimeData);
        }
        //修改数据
        else {
            alarmRealtimeData.setUpdateTime(DateUtils.getNowDate());//修改时间
            alarmRealtimeData.setLastTime(DateUtils.getNowDate());//最后一次产生的时间
            alarmRealtimeData.setAmount(data.getAmount() + 1);//告警次数
            alarmRealtimeDataMapper.updateAlarmRealtimeDataByAlarmTacticsId(alarmRealtimeData);
        }
    }

    /***
     * @description:插入报警工单
     * @author: sunshangeng
     * @date: 2023/3/9 11:32
     * @return: void
     **/
    private void saveAlarmWorkOrder(AlarmRealtimeData alarmRealtimeData, List<AlarmNotifier> notifierList) {

        notifierList.forEach(item -> {
            AlarmWorkOrder workOrder = workOrderMapper.getWorkOrderByTactics(alarmRealtimeData.getAlarmTacticsId(), item.getUserId(), alarmRealtimeData.getAzwz());
            if (workOrder == null) {
                workOrder = new AlarmWorkOrder();
                /**新增*/
                workOrder.setAlarmName(alarmRealtimeData.getAlarmName());
                workOrder.setAlarmTacticsId(alarmRealtimeData.getAlarmTacticsId());
                workOrder.setAlarmTypeId(alarmRealtimeData.getAlarmTypeId());
                workOrder.setAlarmValue(alarmRealtimeData.getAlarmValue());
                workOrder.setAmount(1);
                workOrder.setAzwz(alarmRealtimeData.getAzwz());
                workOrder.setConfirmState((long) 0);
                workOrder.setFirstTime(DateUtils.getNowDate());
                workOrder.setPlanVal(alarmRealtimeData.getPlanVal());
                workOrder.setPromptMsg(alarmRealtimeData.getPromptMsg());
                workOrder.setLevel(alarmRealtimeData.getLevel());
                workOrder.setLastTime(DateUtils.getNowDate());
                workOrder.setCreateTime(new Date());
                workOrder.setUserId(item.getUserId());
                workOrderMapper.insertAlarmWorkOrder(workOrder);
            } else {
                /**修改*/
                workOrder.setLastTime(new Date());
                workOrder.setAmount(workOrder.getAmount() + 1);
                workOrderMapper.updateAlarmWorkOrder(workOrder);
            }
        });
    }

    /**
     * 点报警处理 AI
     *
     * @param point        点
     * @param alarmYesOrNo 报警是或否
     * @param pointValue   点值
     * @Author qindehua
     * @Date 2022/11/04
     **/
    public void PointAlarmHandleAI(Point point, Boolean alarmYesOrNo, Integer pointValue) {
        //返回前端的msg
        Map<String, Object> msgMap = new HashedMap();

        //组织机构ids
//        List<String> strList = new ArrayList<>();

        //获取电表
        AthenaElectricMeter electricMeter = meterCache.getMeterByDeviceId(point.getTreeId());
        if (electricMeter == null) {
            log.warn("电表为空！");
            return;
        }
        //获取告警策略
//        List<AlarmTactics> alarmTactics = alarmTacticsMapper.selectAlarmTacticsListByDeviceId(electricMeter.getDeviceTreeId().toString());
        List<AlarmTactics> alarmTactics = getAlarmTacticsListByDeviceId(electricMeter.getDeviceTreeId().toString());

        /*********************电表报警**************************/
        if (CollectionUtils.isNotEmpty(alarmTactics)) {
            for (AlarmTactics alarmTactic : alarmTactics) {
                alarmYesOrNo = false;
                //电能参数的值List
                List ammeterList = new ArrayList();
                ammeterList.add(pointValue);
                //能源名称
                String name = "";
                //获取能源名称
                Map<String, EnergyType> energyTypeMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
                for (EnergyType energyType : energyTypeMap.values()) {
                    if (energyType.getCode().equals(point.getEnergyCode())) {
                        name = energyType.getName();
                        continue;
                    }
                }
                float nData = pointValue.floatValue();
//                float nData = 0.0f;
//                String operator = alarmTactic.getOperator();//查询配置的报警公式
//                for (int k = ammeterList.size(); k > 0; k--) {
//                    String strData = String.valueOf(ammeterList.get(k - 1));
//                    String strOperator = "$" + k;
//                    operator = operator.replace(strOperator, strData);
//                }
//                try {
//                    nData = Float.parseFloat(jse.eval(operator).toString());
//
//                } catch (ScriptException e) {
//                    e.printStackTrace();
//                }
                //要添加的报警信息
                AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData();
//                    besWaringInfo.setFYqbh(alarmList.getfYqbh());
                /**************************************待优化*****************************************/
                alarmRealtimeData.setAzwz(electricMeter.getAlias() + "/" + "(" + name + ")" + "(" + alarmTactic.getName() + ")");//报警位置
                alarmRealtimeData.setAlarmName(alarmTactic.getName());//报警名称
                alarmRealtimeData.setAlarmTacticsId(alarmTactic.getId());//告警策略id
                alarmRealtimeData.setAlarmValue(String.valueOf(nData));//实际值
//                    besWaringInfo.setFOpearation("1");//是否处理
                alarmRealtimeData.setAlarmTypeId(String.valueOf(alarmTactic.getDeviceType()));//信息类型  1:电表  2:支路  3:分户 4:分项 5:设备报警
                alarmRealtimeData.setLevel(String.valueOf(alarmTactic.getLevel()));//报警等级

                /**
                 * 1=确认值,2=阀值,3=上限,4=下限
                 * 确认值
                 * */
                if (1 == alarmTactic.getRangeType()) {
                    float equal = alarmTactic.getPrecise().floatValue();//准确值
                    if (equal != nData) {
                        alarmYesOrNo = true;
                        alarmRealtimeData.setPlanVal(alarmTactic.getPrecise().toString());//计划值
                        alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不等于计划值");//提示信息
                        //判断是否存在相同的未处理报警
                        insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                        //判断是否发送信息
                        isSendMessage(alarmTactic, alarmRealtimeData);
                        if (alarmYesOrNo == true) {
                            //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                            DataReception dataReception = getNoRecoverCount();//查询报警的条数

                            msgMap.put("alarmRealtimeCount", dataReception.getData());
                            //用户
                            // msgMap.put("userNameList",userList);
                            // 推送消息到web客户端
                            WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                            WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                        }
                    }
                }
                /************ 阀值********/
                else if (2 == alarmTactic.getRangeType()) {
                    float nomore = alarmTactic.getOver().floatValue();//最大值
                    float noless = alarmTactic.getUnder().floatValue();//最小
                    if (nData > nomore || nData < noless) {
                        alarmYesOrNo = true;
                        alarmRealtimeData.setPlanVal(noless + "--" + nomore);//计划值
                        alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不在计划值范围");//提示信息
                        //判断是否存在相同的未处理报警
                        insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                        //判断是否发送信息
                        isSendMessage(alarmTactic, alarmRealtimeData);
                        if (alarmYesOrNo == true) {
                            //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                            DataReception dataReception = getNoRecoverCount();//查询报警的条数

                            msgMap.put("alarmRealtimeCount", dataReception.getData());
                            //用户
                            // msgMap.put("userNameList",userList);
                            // 推送消息到web客户端
                            WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                            WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                        }
                    }
                }
                /************ 上限********/
                else if (3 == alarmTactic.getRangeType()) {
                    float nomore = alarmTactic.getOver().floatValue();//最大值
                    if (nData > nomore) {
                        alarmYesOrNo = true;
                        alarmRealtimeData.setPlanVal(String.valueOf(nomore));//计划值
                        alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数大于计划值");//提示信息
                        //判断是否存在相同的未处理报警
                        insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                        //判断是否发送信息
                        isSendMessage(alarmTactic, alarmRealtimeData);
                        if (alarmYesOrNo == true) {
                            //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                            DataReception dataReception = getNoRecoverCount();//查询报警的条数

                            msgMap.put("alarmRealtimeCount", dataReception.getData());
                            //用户
                            // msgMap.put("userNameList",userList);
                            // 推送消息到web客户端
                            WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                            WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                        }
                    }
                }
                /************ 下限********/
                else if (4 == alarmTactic.getRangeType()) {
                    float noless = alarmTactic.getUnder().floatValue();//最小值
                    if (nData < noless) {
                        alarmYesOrNo = true;
                        alarmRealtimeData.setPlanVal(String.valueOf(noless));//计划值
                        alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数小于计划值");//提示信息
                        //判断是否存在相同的未处理报警
                        insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                        //判断是否发送信息
                        isSendMessage(alarmTactic, alarmRealtimeData);
                        if (alarmYesOrNo == true) {
                            //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                            DataReception dataReception = getNoRecoverCount();//查询报警的条数

                            msgMap.put("alarmRealtimeCount", dataReception.getData());
                            //用户
                            // msgMap.put("userNameList",userList);
                            // 推送消息到web客户端
                            WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                            WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                        }
                    }
                }
                //如果没有走报警处理 则进行  之前是否有发生报警判断
                if (!alarmYesOrNo) {
                    AlarmRealtimeData data = alarmRealtimeDataMapper.selectAlarmRealtimeDataByAlarmTacticsId(alarmTactic.getId());
                    //之前有过报警  但是现在未发生报警  说明 告警已解除
                    //判断是否发送告警解除通知
                    if (data != null) {
                        isSendMessageRescind(alarmTactic, data);
                    }
                }
            }
        }

        /*************************************判断电表是否关联支路  关联则进行支路报警判断 *************************************/
        //电表关联的支路
        List<AthenaBranchMeterLink> branchMeterLinks = new ArrayList<>();
        Map<String, AthenaBranchMeterLink> athenaBranchMeterLinkMap =
                redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
        for (AthenaBranchMeterLink value : athenaBranchMeterLinkMap.values()) {
            if (electricMeter.getMeterId().equals(value.getMeterId())) {
                branchMeterLinks.add(value);
            }
        }
        if (branchMeterLinks.size() > 0) {
            /*************************************支路报警 *************************************/
            alarmBranch(branchMeterLinks, athenaBranchMeterLinkMap, false, msgMap, electricMeter.getMeterId(), pointValue);
        }
    }

    /**
     * 查询报警条数
     *
     * @return {@code DataReception }
     * @Author qindehua
     * @Date 2022/11/04
     **/
    public DataReception getNoRecoverCount() {
        try {
            Integer count = alarmRealtimeDataMapper.selectAlarmRealtimeDataCount();
            return new DataReception(true, count);
        } catch (Exception e) {
            return new DataReception(false);
        }
    }


    /**
     * 能耗报警处理
     *
     * @param msg 数据
     * @Author qindehua
     * @Date 2022/11/14
     **/
    public void alarmHandle(ReceiveMsg<List<AmmeterData>> msg) {

        List<AmmeterData> ammeterDataList = msg.getData();
        /****是否有报警消息插入到实时报警表,初始值为false**/
        Boolean alarmYesOrNo = false;
        /****变比**/
        Integer rate;

        //返回前端的msg
        Map<String, Object> msgMap = new HashedMap();

        //组织机构ids
//        List<String> strList = new ArrayList<>();


        for (AmmeterData ammeterData : ammeterDataList) {
            // 电能参数
            String electricData = ammeterData.getElectricData();//获取上传的电能参数的值
            //分割
            String[] electricDatas = electricData.split(",");
            //获取的电表ID
            Integer meterID = ammeterData.getMeterID();
            // 实际采集的电能参数个数
            Integer collectCount = ammeterData.getCollectCount();
            //条件判断
            if (electricData.isEmpty()
                    || null == collectCount
                    || collectCount <= 0) {
                log.warn("下位机数据错误");
                continue;
            }

            if (!collectCount.equals(electricDatas.length)) {
                log.warn("下位机数据错误");
                continue;
            }

            /******************获取当前电表 **************/
            AthenaElectricMeter meter = meterCache.getMeterByMeterId(meterID);
            if (meter == null) {
                log.warn("上位机与下位机电表一致（上位机没有对应的电能参数）");
                continue;
            }

            if (meter.getRate() != null) {//如果变比不为null,将变比转换成int
                rate = meter.getRate().intValue();
            } else {//否则跳出当前循环
                continue;
            }
            //将传入的值转成List
            List<String> electricDatasList = Arrays.asList(electricDatas);

            //根据采集方案ID 获取采集方案、采集参数关联表 list
            List<ElectricCollRlgl> electricCollRlgls =
                    collRlglCache.getElectricCollRlglByCollId(meter.getCollectionMethodCode().intValue());
            if (electricCollRlgls == null || electricCollRlgls.size() == 0) {
                continue;
            }

            /***********************根据电表ID 判断是否包含电表报警*************************************/
//            List<AlarmTactics> alarmTactics = alarmTacticsMapper.selectAlarmTacticsListByDeviceId(meter.getDeviceTreeId().toString());
            List<AlarmTactics> alarmTactics = getAlarmTacticsListByDeviceId(meter.getDeviceTreeId().toString());
            //不为空 则进行电表报警判断
            if (CollectionUtils.isNotEmpty(alarmTactics)) {
                /*************************************获取告警策略  电表报警  *************************************/
                alarmMeter(alarmTactics, meter, alarmYesOrNo, msgMap);
            }

            /***********************查看该电表 是否关联支路*************************************/
            List<AthenaBranchMeterLink> branchMeterLinks = new ArrayList<>();
            Map<String, AthenaBranchMeterLink> athenaBranchMeterLinkMap =
                    redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
            athenaBranchMeterLinkMap.values().forEach(item -> {
                if (meter.getMeterId().equals(item.getMeterId())) {
                    branchMeterLinks.add(item);
                }
            });
            /*************************************判断电表是否关联支路  关联则进行支路报警判断 *************************************/
            if (branchMeterLinks.size() > 0) {
                /*************************************支路报警 *************************************/
                alarmBranch(branchMeterLinks, athenaBranchMeterLinkMap, false, msgMap, null, null);
            }

        }
    }

    /**
     * 获取告警策略  电表报警
     *
     * @param list         报警配置列表
     * @param meter        电表
     * @param alarmYesOrNo 报警是或否
     * @param msgMap       数据map
     * @Author qindehua
     * @Date 2022/11/17
     **/
    private void alarmMeter(List<AlarmTactics> list, AthenaElectricMeter meter, Boolean alarmYesOrNo, Map<String, Object> msgMap) {
        for (AlarmTactics alarmTactic : list) {
            alarmYesOrNo = false;
            //电能参数的值List
            List ammeterList = new ArrayList();
            //能源名称
            String name = "";
            //获取能源名称 根据采集方案id
            Map<String, CollMethod> collMethodMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod);
            for (CollMethod value : collMethodMap.values()) {
                if (value.getId() == meter.getCollectionMethodCode()) {
                    Map<String, EnergyType> energyTypeMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
                    for (EnergyType energyType : energyTypeMap.values()) {
                        if (energyType.getCode().equals(value.getEnergyCode())) {
                            name = energyType.getName();
                            continue;
                        }
                    }
                    continue;
                }
            }

            //将对应的采集参数值存入ammeterList
            Map<String, Map<String, Object>> originalDataMap = redisCache.getCacheMapValue(
                    RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData, meter.getMeterId());
            //根据采集参数id 获取采集参数
            ElectricParams electricParams = redisCache.getCacheMapValue(
                    RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, alarmTactic.getElectricParamsId());
            if (electricParams == null) {
                ElectricParams dataMapper = electricParamsMapper.selectElectricParamsById(alarmTactic.getElectricParamsId());
                if (dataMapper != null) {
                    electricParams = dataMapper;
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, dataMapper.getId(), dataMapper);
                }
            }

            if (MapUtils.isEmpty(originalDataMap) || electricParams == null || originalDataMap.get(electricParams.getCode()) == null) {
                continue;
            } else {
                ammeterList.add(originalDataMap.get(electricParams.getCode()).get("data").toString());
            }

            float nData = Float.valueOf(originalDataMap.get(electricParams.getCode()).get("data").toString());
//            float nData = 0.0f;
//            String operator = alarmTactic.getOperator();//查询配置的报警公式
//            for (int k = ammeterList.size(); k > 0; k--) {
//                String strData = String.valueOf(ammeterList.get(k - 1));
//                String strOperator = "$" + k;
//                operator = operator.replace(strOperator, strData);
//            }
//            try {
//
//                nData = Float.parseFloat(jse.eval(operator).toString());
//
//            } catch (ScriptException e) {
//                e.printStackTrace();
//            }
            //要添加的报警信息
            AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData();
            /**************************************待优化*****************************************/

            //                    besWaringInfo.setFYqbh(alarmList.getfYqbh());
            alarmRealtimeData.setAzwz(meter.getAlias() + "/" + "(" + name + ")" + "(" + alarmTactic.getName() + ")");//报警位置
            alarmRealtimeData.setAlarmName(alarmTactic.getName());//报警名称
            alarmRealtimeData.setAlarmTacticsId(alarmTactic.getId());//告警策略id
            alarmRealtimeData.setAlarmValue(String.valueOf(nData));//实际值
//                    besWaringInfo.setFOpearation("1");//是否处理
            alarmRealtimeData.setAlarmTypeId(String.valueOf(alarmTactic.getDeviceType()));//信息类型  1:电表  2:支路  3:分户 4:分项 5:设备报警
            alarmRealtimeData.setLevel(String.valueOf(alarmTactic.getLevel()));//报警等级

            /**
             * 1=确认值,2=阀值,3=上限,4=下限
             * 确认值
             * */
            if (1 == alarmTactic.getRangeType()) {
                float equal = alarmTactic.getPrecise().floatValue();//准确值
                if (equal != nData) {
                    alarmYesOrNo = true;
                    alarmRealtimeData.setPlanVal(alarmTactic.getPrecise().toString());//计划值
                    alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不等于计划值");//提示信息
                    //判断是否存在相同的未处理报警
                    insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                    //判断是否发送信息
                    isSendMessage(alarmTactic, alarmRealtimeData);
                    if (alarmYesOrNo == true) {
                        //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                        DataReception dataReception = getNoRecoverCount();//查询报警的条数

                        msgMap.put("alarmRealtimeCount", dataReception.getData());
                        //用户
                        // msgMap.put("userNameList",userList);
                        // 推送消息到web客户端
                        WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                        WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                    }
                }
            }
            /************ 阀值********/
            else if (2 == alarmTactic.getRangeType()) {
                float nomore = alarmTactic.getOver().floatValue();//最大值
                float noless = alarmTactic.getUnder().floatValue();//最小
                if (nData > nomore || nData < noless) {
                    alarmYesOrNo = true;
                    alarmRealtimeData.setPlanVal(noless + "--" + nomore);//计划值
                    alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不在计划值范围");//提示信息
                    //判断是否存在相同的未处理报警
                    insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                    //判断是否发送信息
                    isSendMessage(alarmTactic, alarmRealtimeData);
                    if (alarmYesOrNo == true) {
                        //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                        DataReception dataReception = getNoRecoverCount();//查询报警的条数

                        msgMap.put("alarmRealtimeCount", dataReception.getData());
                        //用户
                        // msgMap.put("userNameList",userList);
                        // 推送消息到web客户端
                        WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                        WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                    }
                }
            }
            /************ 上限********/
            else if (3 == alarmTactic.getRangeType()) {
                float nomore = alarmTactic.getOver().floatValue();//最大值
                if (nData > nomore) {
                    alarmYesOrNo = true;
                    alarmRealtimeData.setPlanVal(String.valueOf(nomore));//计划值
                    alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数大于计划值");//提示信息
                    //判断是否存在相同的未处理报警
                    insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                    //判断是否发送信息
                    isSendMessage(alarmTactic, alarmRealtimeData);
                    if (alarmYesOrNo == true) {
                        //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                        DataReception dataReception = getNoRecoverCount();//查询报警的条数

                        msgMap.put("alarmRealtimeCount", dataReception.getData());
                        //用户
                        // msgMap.put("userNameList",userList);
                        // 推送消息到web客户端
                        WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                        WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                    }
                }
            }
            /************ 下限********/
            else if (4 == alarmTactic.getRangeType()) {
                float noless = alarmTactic.getUnder().floatValue();//最小值
                if (nData < noless) {
                    alarmYesOrNo = true;
                    alarmRealtimeData.setPlanVal(String.valueOf(noless));//计划值
                    alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数小于计划值");//提示信息
                    //判断是否存在相同的未处理报警
                    insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                    //判断是否发送信息
                    isSendMessage(alarmTactic, alarmRealtimeData);
                    if (alarmYesOrNo == true) {
                        //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                        DataReception dataReception = getNoRecoverCount();//查询报警的条数

                        msgMap.put("alarmRealtimeCount", dataReception.getData());
                        //用户
                        // msgMap.put("userNameList",userList);
                        // 推送消息到web客户端
                        WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                        WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);
                    }
                }
            }
            //如果没有走报警处理 则进行  之前是否有发生报警判断
            if (!alarmYesOrNo) {
                AlarmRealtimeData data = alarmRealtimeDataMapper.selectAlarmRealtimeDataByAlarmTacticsId(alarmTactic.getId());
                //之前有过报警  但是现在未发生报警  说明 告警已解除
                //判断是否发送告警解除通知
                if (data != null) {
                    isSendMessageRescind(alarmTactic, data);
                }
            }
        }
    }


    /**
     * 支路报警
     *
     * @param branchMeterLinks         电表关联的支路列表
     * @param athenaBranchMeterLinkMap 支路电表关联map
     * @param alarmYesOrNo             报警是或否
     * @param msgMap                   返回数据
     * @param meterId                  电表id
     * @param pointValue               点值
     * @Author qindehua
     * @Date 2022/12/05
     **/
    private void alarmBranch(List<AthenaBranchMeterLink> branchMeterLinks, Map<String, AthenaBranchMeterLink> athenaBranchMeterLinkMap,
                             Boolean alarmYesOrNo, Map<String, Object> msgMap, Long meterId, Integer pointValue) {
        for (AthenaBranchMeterLink branchMeterLink : branchMeterLinks) {

//            List<AlarmTactics> tacticsList = alarmTacticsMapper.selectAlarmTacticsListByDeviceId(branchMeterLink.getBranchId().toString());
            List<AlarmTactics> tacticsList = getAlarmTacticsListByDeviceId(branchMeterLink.getBranchId().toString());
            /********************************************不为空则进行支路报警*******************************************************/
            if (CollectionUtils.isNotEmpty(tacticsList)) {
                for (AlarmTactics tactics : tacticsList) {
                    alarmYesOrNo = false;
                    List<AthenaBranchMeterLink> meters = new ArrayList<>();
                    //查询该支路下的所有电表
                    athenaBranchMeterLinkMap.values().forEach(item -> {
                        if (branchMeterLink.getBranchId().equals(item.getBranchId())) {
                            meters.add(item);
                        }
                    });
                    //总值
                    Float val = 0.0f;
                    //循环支路下面的电表 并将采集参数的值进行相加
                    for (AthenaBranchMeterLink item : meters) {
                        if (meterId != null && item.getMeterId().equals(meterId)) {
                            //判断运算符
                            if ("0".equals(item.getOperator())) {
                                val = val - Float.valueOf(pointValue);
                            } else {
                                val = val + Float.valueOf(pointValue);
                            }
                        } else {
                            Map<String, Map<String, Object>> originalDataMap = redisCache.getCacheMapValue(
                                    RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData, item.getMeterId());

                            if (MapUtils.isEmpty(originalDataMap) || originalDataMap.get(item.getElectricParam()) == null) {
                                continue;
                            }
                            //判断运算符
                            if ("0".equals(item.getOperator())) {
                                val = val - Float.valueOf(originalDataMap.get(item.getElectricParam()).get("data").toString());
                            } else {
                                val = val + Float.valueOf(originalDataMap.get(item.getElectricParam()).get("data").toString());
                            }
                        }
                    }
                    float nData = val.floatValue();
//                    //实际值
//                    float nData = 0.0f;
//                    //电能参数的值List
//                    List ammeterList = new ArrayList();
//                    ammeterList.add(val);
//                    String operator = tactics.getOperator();//查询配置的报警公式
//                    for (int k = ammeterList.size(); k > 0; k--) {
//                        String strData = String.valueOf(ammeterList.get(k - 1));
//                        String strOperator = "$" + k;
//                        operator = operator.replace(strOperator, strData);
//                    }
//                    try {
//                        nData = Float.parseFloat(jse.eval(operator).toString());
//
//                    } catch (ScriptException e) {
//                        e.printStackTrace();
//                    }

                    //要添加的报警信息
                    AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData();
                    /**************************************待优化*****************************************/
                    AthenaBranchConfig athenaBranchConfig = redisCache.getCacheMapValue(
                            RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, branchMeterLink.getBranchId());
                    if (athenaBranchConfig == null) {
                        AthenaBranchConfig dataMapper = branchConfigMapper.selectAthenaBranchConfigByBranchId(branchMeterLink.getBranchId());
                        if (dataMapper == null) {
                            log.warn("支路数据为空！");
                            return;
                        } else {
                            athenaBranchConfig = dataMapper;
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, dataMapper.getBranchId(), dataMapper);
                        }
                    }
                    //                    besWaringInfo.setFYqbh(alarmList.getfYqbh());
                    alarmRealtimeData.setAzwz(athenaBranchConfig.getBranchName() + "/" + "(" + tactics.getName() + ")");//报警位置
                    alarmRealtimeData.setAlarmName(tactics.getName());//报警名称
                    alarmRealtimeData.setAlarmTacticsId(tactics.getId());//告警策略id
                    alarmRealtimeData.setAlarmValue(String.valueOf(nData));//实际值
                    alarmRealtimeData.setAlarmTypeId(String.valueOf(tactics.getDeviceType()));//信息类型  1:电表  2:支路  3:分户 4:分项 5:设备报警
                    alarmRealtimeData.setLevel(String.valueOf(tactics.getLevel()));//报警等级
                    /**
                     * 1=确认值,2=阀值,3=上限,4=下限
                     * 确认值
                     * */
                    if (1 == tactics.getRangeType()) {
                        float equal = tactics.getPrecise().floatValue();//准确值
                        if (equal != nData) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(tactics.getPrecise().toString());//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不等于计划值");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(tactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    /************ 阀值********/
                    else if (2 == tactics.getRangeType()) {
                        float nomore = tactics.getOver().floatValue();//最大值
                        float noless = tactics.getUnder().floatValue();//最小
                        if (nData > nomore || nData < noless) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(noless + "--" + nomore);//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不在计划值范围");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(tactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    /************ 上限********/
                    else if (3 == tactics.getRangeType()) {
                        float nomore = tactics.getOver().floatValue();//最大值
                        if (nData > nomore) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(String.valueOf(nomore));//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数大于计划值");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(tactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    /************ 下限********/
                    else if (4 == tactics.getRangeType()) {
                        float noless = tactics.getUnder().floatValue();//最小值
                        if (nData < noless) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(String.valueOf(noless));//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数小于计划值");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(tactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    //如果没有走报警处理 则进行  之前是否有发生报警判断
                    if (!alarmYesOrNo) {
                        AlarmRealtimeData data = alarmRealtimeDataMapper.selectAlarmRealtimeDataByAlarmTacticsId(tactics.getId());
                        //之前有过报警  但是现在未发生报警  说明 告警已解除
                        //判断是否发送告警解除通知
                        if (data != null) {
                            isSendMessageRescind(tactics, data);
                        }
                    }
                }
            }

            /*************************************判断支路是否关联分户  关联则进行分户报警判断 *************************************/
            Map<String, AthenaBesHouseholdBranchLink> householdBranchLinkMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink);
            List<AthenaBesHouseholdBranchLink> householdBranchLinks = new ArrayList<>();
            for (AthenaBesHouseholdBranchLink value : householdBranchLinkMap.values()) {
                if (branchMeterLink.getBranchId().equals(value.getBranchId())) {
                    householdBranchLinks.add(value);
                }
            }
            if (householdBranchLinks.size() > 0) {
                /*************************************分户报警 *************************************/
                householdAlarm(householdBranchLinkMap, householdBranchLinks, false, msgMap, meterId, pointValue);
            }
            /*************************************判断支路是否关联分项  关联则进行分项报警判断 *************************************/
            Map<String, SubitemBranchLink> subitemBranchLinkMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink);
            List<SubitemBranchLink> subitemBranchLinks = new ArrayList<>();
            for (SubitemBranchLink subitemBranchLink : subitemBranchLinkMap.values()) {
                if (branchMeterLink.getBranchId().equals(subitemBranchLink.getBranchId())) {
                    subitemBranchLinks.add(subitemBranchLink);
                }
            }
            if (subitemBranchLinks.size() > 0) {
                /*************************************分项报警 *************************************/
                subitemAlarm(subitemBranchLinkMap, subitemBranchLinks, false, msgMap, meterId, pointValue);
            }
        }
    }

    /**
     * 分户报警
     *
     * @param householdBranchLinkMap 分户支路关联数据
     * @param householdBranchLinks   支路关联的分户数据
     * @param alarmYesOrNo           报警是或否
     * @param msgMap                 返回数据
     * @param meterId                电表id
     * @param pointValue             点值
     * @Author qindehua
     * @Date 2022/12/05
     **/
    private void householdAlarm(Map<String, AthenaBesHouseholdBranchLink> householdBranchLinkMap, List<AthenaBesHouseholdBranchLink> householdBranchLinks,
                                Boolean alarmYesOrNo, Map<String, Object> msgMap, Long meterId, Integer pointValue) {
        for (AthenaBesHouseholdBranchLink householdBranchLink : householdBranchLinks) {
//            List<AlarmTactics> householdTacticsList = alarmTacticsMapper.selectAlarmTacticsListByDeviceId(householdBranchLink.getHouseholdId().toString());
            List<AlarmTactics> householdTacticsList = getAlarmTacticsListByDeviceId(householdBranchLink.getHouseholdId().toString());
            if (CollectionUtils.isEmpty(householdTacticsList)) {
                continue;
            } else {
                for (AlarmTactics householdTactics : householdTacticsList) {
                    alarmYesOrNo = false;
                    //该分户下 所有支路
                    List<AthenaBesHouseholdBranchLink> householdBranchList = new ArrayList<>();
                    //查询该分户下所有支路
                    householdBranchLinkMap.values().forEach(item -> {
                        if (householdBranchLink.getHouseholdId().equals(item.getHouseholdId())) {
                            householdBranchList.add(item);
                        }
                    });
                    //总值
                    Float val = 0.0f;

                    //循环支路
                    for (AthenaBesHouseholdBranchLink householdBranch : householdBranchList) {
                        //查询该支路下所有电表
                        Map<String, AthenaBranchMeterLink> athenaBranchMeterLinkMap =
                                redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
                        //支路下所有电表
                        List<AthenaBranchMeterLink> meters = new ArrayList<>();
                        for (AthenaBranchMeterLink value : athenaBranchMeterLinkMap.values()) {
                            if (householdBranch.getBranchId().equals(value.getBranchId())) {
                                meters.add(value);
                            }
                        }

                        //再循环支路下面的电表 并将采集参数的值进行相加
                        for (AthenaBranchMeterLink item : meters) {
                            if (meterId != null && item.getMeterId().equals(meterId)) {
                                //判断运算符
                                if ("0".equals(item.getOperator())) {
                                    val = val - Float.valueOf(pointValue);
                                } else {
                                    val = val + Float.valueOf(pointValue);
                                }
                            } else {
                                Map<String, Map<String, Object>> originalDataMap = redisCache.getCacheMapValue(
                                        RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData, item.getMeterId());

                                if (MapUtils.isEmpty(originalDataMap) || originalDataMap.get(item.getElectricParam()) == null) {
                                    continue;
                                }
                                //判断运算符
                                if ("0".equals(item.getOperator())) {
                                    val = val - Float.valueOf(originalDataMap.get(item.getElectricParam()).get("data").toString());
                                } else {
                                    val = val + Float.valueOf(originalDataMap.get(item.getElectricParam()).get("data").toString());
                                }
                            }
                        }
                    }
                    float nData = val.floatValue();
//                    //实际值
//                    float nData = 0.0f;
//                    //电能参数的值List
//                    List ammeterList = new ArrayList();
//                    ammeterList.add(val);
//                    String operator = householdTactics.getOperator();//查询配置的报警公式
//                    for (int k = ammeterList.size(); k > 0; k--) {
//                        String strData = String.valueOf(ammeterList.get(k - 1));
//                        String strOperator = "$" + k;
//                        operator = operator.replace(strOperator, strData);
//                    }
//                    try {
//                        nData = Float.parseFloat(jse.eval(operator).toString());
//
//                    } catch (ScriptException e) {
//                        e.printStackTrace();
//                    }

                    //要添加的报警信息
                    AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData();
                    /**************************************待优化*****************************************/
                    AthenaBesHouseholdConfig athenaBesHouseholdConfig = redisCache.getCacheMapValue(
                            RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdConfig, householdBranchLink.getHouseholdId());
                    if (athenaBesHouseholdConfig == null) {
                        AthenaBesHouseholdConfig dataMapper = householdConfigMapper.selectAthenaBesHouseholdConfigById(householdBranchLink.getHouseholdId());
                        if (dataMapper == null) {
                            log.warn("分户数据为空！");
                            return;
                        } else {
                            athenaBesHouseholdConfig = dataMapper;
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdConfig, dataMapper.getHouseholdId(), dataMapper);
                        }
                    }
                    //                    besWaringInfo.setFYqbh(alarmList.getfYqbh());
                    alarmRealtimeData.setAzwz(athenaBesHouseholdConfig.getHouseholdName() + "/" + "(" + householdTactics.getName() + ")");//报警位置
                    alarmRealtimeData.setAlarmName(householdTactics.getName());//报警名称
                    alarmRealtimeData.setAlarmTacticsId(householdTactics.getId());//告警策略id
                    alarmRealtimeData.setAlarmValue(String.valueOf(nData));//实际值
                    alarmRealtimeData.setAlarmTypeId(String.valueOf(householdTactics.getDeviceType()));//信息类型  1:电表  2:支路  3:分户 4:分项 5:设备报警
                    alarmRealtimeData.setLevel(String.valueOf(householdTactics.getLevel()));//报警等级
                    /**
                     * 1=确认值,2=阀值,3=上限,4=下限
                     * 确认值
                     * */
                    if (1 == householdTactics.getRangeType()) {
                        float equal = householdTactics.getPrecise().floatValue();//准确值
                        if (equal != nData) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(householdTactics.getPrecise().toString());//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不等于计划值");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(householdTactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    /************ 阀值********/
                    else if (2 == householdTactics.getRangeType()) {
                        float nomore = householdTactics.getOver().floatValue();//最大值
                        float noless = householdTactics.getUnder().floatValue();//最小
                        if (nData > nomore || nData < noless) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(noless + "--" + nomore);//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不在计划值范围");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(householdTactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    /************ 上限********/
                    else if (3 == householdTactics.getRangeType()) {
                        float nomore = householdTactics.getOver().floatValue();//最大值
                        if (nData > nomore) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(String.valueOf(nomore));//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数大于计划值");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(householdTactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    /************ 下限********/
                    else if (4 == householdTactics.getRangeType()) {
                        float noless = householdTactics.getUnder().floatValue();//最小值
                        if (nData < noless) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(String.valueOf(noless));//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数小于计划值");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(householdTactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    //如果没有走报警处理 则进行  之前是否有发生报警判断
                    if (!alarmYesOrNo) {
                        AlarmRealtimeData data = alarmRealtimeDataMapper.selectAlarmRealtimeDataByAlarmTacticsId(householdTactics.getId());
                        //之前有过报警  但是现在未发生报警  说明 告警已解除
                        //判断是否发送告警解除通知
                        if (data != null) {
                            isSendMessageRescind(householdTactics, data);
                        }
                    }
                }

            }
        }

    }

    /**
     * 分项报警
     *
     * @param subitemBranchLinkMap 分项支路关联数据
     * @param subitemBranchLinks   支路关联的分项数据
     * @param alarmYesOrNo         报警是或否
     * @param msgMap               返回数据
     * @param meterId              电表id
     * @param pointValue           点值
     * @Author qindehua
     * @Date 2022/12/05
     **/
    private void subitemAlarm(Map<String, SubitemBranchLink> subitemBranchLinkMap, List<SubitemBranchLink> subitemBranchLinks,
                              Boolean alarmYesOrNo, Map<String, Object> msgMap, Long meterId, Integer pointValue) {
        //循环所关联的分项  判断是否添加分项报警配置
        for (SubitemBranchLink subitemBranchLink : subitemBranchLinks) {
            alarmYesOrNo = false;
//            List<AlarmTactics> subitemTacticsList = alarmTacticsMapper.selectAlarmTacticsListByDeviceId(subitemBranchLink.getSubitemId());
            List<AlarmTactics> subitemTacticsList = getAlarmTacticsListByDeviceId(subitemBranchLink.getSubitemId());
            if (CollectionUtils.isEmpty(subitemTacticsList)) {
                continue;
            } else {
                for (AlarmTactics subitemTactics : subitemTacticsList) {
                    //该分项下 所有支路
                    List<SubitemBranchLink> subitemBranchList = new ArrayList<>();
                    //查询该分户下所有支路
                    subitemBranchLinkMap.values().forEach(item -> {
                        if (subitemBranchLink.getSubitemId().equals(item.getSubitemId())) {
                            subitemBranchList.add(item);
                        }
                    });
                    //总值
                    Float val = 0.0f;

                    for (SubitemBranchLink branchLink : subitemBranchList) {
                        //查询该支路下所有电表
                        Map<String, AthenaBranchMeterLink> athenaBranchMeterLinkMap =
                                redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
                        //支路下所有电表
                        List<AthenaBranchMeterLink> meters = new ArrayList<>();
                        for (AthenaBranchMeterLink value : athenaBranchMeterLinkMap.values()) {
                            if (branchLink.getBranchId().equals(value.getBranchId())) {
                                meters.add(value);
                            }
                        }

                        //再循环支路下面的电表 并将采集参数的值进行相加
                        for (AthenaBranchMeterLink item : meters) {
                            if (meterId != null && item.getMeterId().equals(meterId)) {
                                //判断运算符
                                if ("0".equals(item.getOperator())) {
                                    val = val - Float.valueOf(pointValue);
                                } else {
                                    val = val + Float.valueOf(pointValue);
                                }
                            } else {
                                Map<String, Map<String, Object>> originalDataMap = redisCache.getCacheMapValue(
                                        RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData, item.getMeterId());

                                if (MapUtils.isEmpty(originalDataMap) || originalDataMap.get(item.getElectricParam()) == null) {
                                    continue;
                                }

                                //判断运算符
                                if ("0".equals(item.getOperator())) {
                                    val = val - Float.valueOf(originalDataMap.get(item.getElectricParam()).get("data").toString());
                                } else {
                                    val = val + Float.valueOf(originalDataMap.get(item.getElectricParam()).get("data").toString());
                                }
                            }
                        }
                    }
                    float nData = val.floatValue();
//                    //实际值
//                    float nData = 0.0f;
//                    //电能参数的值List
//                    List ammeterList = new ArrayList();
//                    ammeterList.add(val);
//                    String operator = subitemTactics.getOperator();//查询配置的报警公式
//                    for (int k = ammeterList.size(); k > 0; k--) {
//                        String strData = String.valueOf(ammeterList.get(k - 1));
//                        String strOperator = "$" + k;
//                        operator = operator.replace(strOperator, strData);
//                    }
//                    try {
//                        nData = Float.parseFloat(jse.eval(operator).toString());
//
//                    } catch (ScriptException e) {
//                        e.printStackTrace();
//                    }

                    //要添加的报警信息
                    AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData();
                    /**************************************待优化*****************************************/
                    SubitemConfig subitemConfig = redisCache.getCacheMapValue(
                            RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig, subitemBranchLink.getSubitemId());
                    if (subitemConfig == null) {
                        SubitemConfig dataMapper = subitemConfigMapper.selectSubitemConfigBySubitemId(subitemBranchLink.getSubitemId());
                        if (dataMapper == null) {
                            log.warn("分户数据为空！");
                            return;
                        } else {
                            subitemConfig = dataMapper;
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig, dataMapper.getSubitemId(), dataMapper);
                        }
                    }
                    //                    besWaringInfo.setFYqbh(alarmList.getfYqbh());
                    alarmRealtimeData.setAzwz(subitemConfig.getSubitemName() + "/" + "(" + subitemTactics.getName() + ")");//报警位置
                    alarmRealtimeData.setAlarmName(subitemTactics.getName());//报警名称
                    alarmRealtimeData.setAlarmTacticsId(subitemTactics.getId());//告警策略id
                    alarmRealtimeData.setAlarmValue(String.valueOf(nData));//实际值
                    alarmRealtimeData.setAlarmTypeId(String.valueOf(subitemTactics.getDeviceType()));//信息类型  1:电表  2:支路  3:分户 4:分项 5:设备报警
                    alarmRealtimeData.setLevel(String.valueOf(subitemTactics.getLevel()));//报警等级
                    /**
                     * 1=确认值,2=阀值,3=上限,4=下限
                     * 确认值
                     * */
                    if (1 == subitemTactics.getRangeType()) {
                        float equal = subitemTactics.getPrecise().floatValue();//准确值
                        if (equal != nData) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(subitemTactics.getPrecise().toString());//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不等于计划值");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(subitemTactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    /************ 阀值********/
                    else if (2 == subitemTactics.getRangeType()) {
                        float nomore = subitemTactics.getOver().floatValue();//最大值
                        float noless = subitemTactics.getUnder().floatValue();//最小
                        if (nData > nomore || nData < noless) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(noless + "--" + nomore);//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数不在计划值范围");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(subitemTactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    /************ 上限********/
                    else if (3 == subitemTactics.getRangeType()) {
                        float nomore = subitemTactics.getOver().floatValue();//最大值
                        if (nData > nomore) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(String.valueOf(nomore));//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数大于计划值");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(subitemTactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    /************ 下限********/
                    else if (4 == subitemTactics.getRangeType()) {
                        float noless = subitemTactics.getUnder().floatValue();//最小值
                        if (nData < noless) {
                            alarmYesOrNo = true;
                            alarmRealtimeData.setPlanVal(String.valueOf(noless));//计划值
                            alarmRealtimeData.setPromptMsg(alarmRealtimeData.getAlarmName() + "实际参数小于计划值");//提示信息
                            //判断是否存在相同的未处理报警
                            insertAlarmRealtimeByAlarmTacticsId(alarmRealtimeData);
                            //判断是否发送信息
                            isSendMessage(subitemTactics, alarmRealtimeData);
                            if (alarmYesOrNo == true) {
                                //如果有报警消息插入实时报警表,则将报警的条数传到前端页面上,并发出报警声音
                                DataReception dataReception = getNoRecoverCount();//查询报警的条数

                                msgMap.put("alarmRealtimeCount", dataReception.getData());
                                //用户
                                // msgMap.put("userNameList",userList);
                                // 推送消息到web客户端
                                WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
                                WebSocketService.broadcast(WebSocketEvent.ALARMLIST, true);

                            }
                        }
                    }
                    //如果没有走报警处理 则进行  之前是否有发生报警判断
                    if (!alarmYesOrNo) {
                        AlarmRealtimeData data = alarmRealtimeDataMapper.selectAlarmRealtimeDataByAlarmTacticsId(subitemTactics.getId());
                        //之前有过报警  但是现在未发生报警  说明 告警已解除
                        //判断是否发送告警解除通知
                        if (data != null) {
                            isSendMessageRescind(subitemTactics, data);
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断是否发送 信息
     *
     * @param alarmTactics      报警策略
     * @param alarmRealtimeData 报警实时数据
     * @Author qindehua
     * @Date 2022/11/23
     **/
    private void isSendMessage(AlarmTactics alarmTactics, AlarmRealtimeData alarmRealtimeData) {
        List<AlarmNotifier> alarmNotifiers = alarmTacticsMapper.selectAlarmNotifierByAlarmTacticsId(alarmTactics.getId());

        if (alarmNotifiers == null || alarmNotifiers.size() == 0) {
            /**当前告警策略未绑定告警接收组直接退出*/
            return;
        }


        //判断是否发送消息通知  0：否  1：是  通知类型  为0：全部 或者  1：报警触发
        if (1 == alarmTactics.getIsSendInform() && (0 == alarmTactics.getInformType() || 1 == alarmTactics.getInformType())) {
            String contextJson = createJson(alarmRealtimeData.getAlarmName(),"告警触发",alarmRealtimeData.getPromptMsg()
                    , alarmRealtimeData.getAlarmValue(), alarmRealtimeData.getPlanVal());

            /**sunshangeng 创建报警工单*/
            saveAlarmWorkOrder(alarmRealtimeData, alarmNotifiers);
            //告警播报  0：否  1：是
            if (1 == alarmTactics.getAlarmSound()) {
                /**获取当前绑定的语音播报通知配置*/
                AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 3);

                //查询出来该报警策略关联了那些告警接收人
//                List<AlarmNotifier> alarmNotifiers = alarmTacticsMapper.selectAlarmNotifierByAlarmTacticsId(alarmTactics.getId());
                for (AlarmNotifier alarmNotifier : alarmNotifiers) {

                    ResultMap resultMap = NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");
                    String resultJson = resultMap.get("msg").toString();
                    //获取符合角色的token列表
                    List<String> tokens = getTokenList(alarmNotifier.getUserId());
                    /**组装json对象*/
                    tokens.forEach(token -> {
                        // 推送消息到web客户端
                        WebSocketService.postEvent(token, WebSocketEvent.ALARMMSG, resultJson);
                    });
                }
            }
            //发送邮件  0：否  1：是
            if (1 == alarmTactics.getSendEmail()) {
                /**查询绑定的邮件通知*/
                AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 2);
                //循环发送邮件
                for (AlarmNotifier alarmNotifier : alarmNotifiers) {


                    //发送邮件
                    try {
                        NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");
//                        emailServer.sendMessage(smsParam);
//                        //添加通知记录
                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 1L, DateUtils.getNowDate(), 1L, contextJson, alarmTactics.getName());
                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                    } catch (Exception e) {
                        log.error("发送邮件失败！", e);
//                        //添加通知记录
                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 1L, DateUtils.getNowDate(), 0L, contextJson, alarmTactics.getName());
                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                    }
                }
            }
            //发送短信  0：否  1：是
            if (1 == alarmTactics.getTextSb()) {

                /**查询绑定的短信通知*/
                AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 1);
                //循环发送短信
                for (AlarmNotifier alarmNotifier : alarmNotifiers) {
                    //业务id暂定
                    //发送短信
                    try {
                        NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");
                        //添加通知记录
                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 2L, DateUtils.getNowDate(), 1L, contextJson, alarmTactics.getName());
                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                    } catch (Exception e) {
                        log.error("发送短信失败！");
                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 2L, DateUtils.getNowDate(), 0L, contextJson, alarmTactics.getName());
                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                    }
                }
            }
        }
    }

    /**
     * 判断告警解除时  是否发送 信息
     *
     * @param alarmTactics      报警策略
     * @param alarmRealtimeData 报警实时数据
     * @Author qindehua
     * @Date 2022/11/23
     **/
    private void isSendMessageRescind(AlarmTactics alarmTactics, AlarmRealtimeData alarmRealtimeData) {
        List<AlarmNotifier> alarmNotifiers = alarmTacticsMapper.selectAlarmNotifierByAlarmTacticsId(alarmTactics.getId());

        if (alarmNotifiers == null || alarmNotifiers.size() == 0) {
            /**当前告警策略未绑定告警接收组直接退出*/
            return;
        }

        //判断是否发送消息通知  0：否  1：是  通知类型  为0：全部 或者  2：告警解除
        if (1 == alarmTactics.getIsSendInform() && (0 == alarmTactics.getInformType() || 2 == alarmTactics.getInformType())) {
            String contextJson = createJson(alarmRealtimeData.getAlarmName(),"告警解除",alarmRealtimeData.getPromptMsg()
                    , alarmRealtimeData.getAlarmValue(), alarmRealtimeData.getPlanVal());
            //告警播报  0：否  1：是
            if (1 == alarmTactics.getAlarmSound()) {
                AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 3);


                //查询出来该报警策略关联了那些告警接收人
                for (AlarmNotifier alarmNotifier : alarmNotifiers) {
                    ResultMap resultMap = NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");
                    String resultJson = resultMap.get("msg").toString();
                    //获取符合角色的token列表
                    List<String> tokens = getTokenList(alarmNotifier.getUserId());
                    /**组装json对象*/
                    tokens.forEach(token -> {
                        // 推送消息到web客户端
                        WebSocketService.postEvent(token, WebSocketEvent.ALARMMSG, resultJson);
                    });
                }
            }
            //发送邮件  0：否  1：是
            if (1 == alarmTactics.getSendEmail()) {

                //查询出来该报警策略关联了那些告警接收人
                //循环发送邮件
                for (AlarmNotifier alarmNotifier : alarmNotifiers) {
                    AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 2);
                    //发送邮件
                    try {
                        NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");
//                        emailServer.sendMessage(smsParam);
//                        //添加通知记录
                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 1L, DateUtils.getNowDate(), 1L, contextJson, alarmTactics.getName());
                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                    } catch (Exception e) {
                        log.error("发送邮件失败！", e);
//                        //添加通知记录
                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 1L, DateUtils.getNowDate(), 0L, contextJson, alarmTactics.getName());
                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                    }
                }
            }
            //发送短信  0：否  1：是
            if (1 == alarmTactics.getTextSb()) {
                /**查询绑定的短信通知*/
                AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 1);
                //查询出来该报警策略关联了那些告警接收人
                //循环发送邮件
                for (AlarmNotifier alarmNotifier : alarmNotifiers) {
                    //循环发送短信
                    //业务id暂定
                    //发送短信
                    try {
                        NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");
                        //添加通知记录
                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 2L, DateUtils.getNowDate(), 1L, contextJson, alarmTactics.getName());
                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                    } catch (Exception e) {
                        log.error("发送短信失败！");
                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 2L, DateUtils.getNowDate(), 0L, contextJson, alarmTactics.getName());
                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                    }

                }
            }
        }
    }
    private String strMessage(String name, String msg, String val, String planVal) {
        String str = "报警名称:" + name + ",报警描述:" + msg + ",报警值:" + val + ",计划值:" + planVal;
        return str;
    }
    private String createJson(String name,String triggerMode, String msg, String val, String planVal) {
//        String str = "报警名称:" + name + ",报警描述:" + msg + ",报警值:" + val + ",计划值:" + planVal;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("triggerMode", triggerMode);
        jsonObject.put("msg", msg);
        jsonObject.put("val", val);
        jsonObject.put("planVal", planVal);
        return jsonObject.toJSONString();
    }
    private String strMessageRescind(String name,String triggerMode, String msg, String val, String planVal) {
        String str = "报警名称:" + name + ",触发方式："+triggerMode+",报警描述:" + msg + ",报警值:" + val + ",计划值:" + planVal + ",该报警已自动解除！";
        return str;
    }

    /**
     * 获取报警策略通过设备id
     *
     * @param deviceId 设备id
     * @return {@code List<AlarmTactics> }
     * @Author qindehua
     **/
    public List<AlarmTactics> getAlarmTacticsListByDeviceId(String deviceId) {
        List<AlarmTactics> list = new ArrayList<>();
        if (deviceId == null) {
            return list;
        }
        Map<String, AlarmTactics> map = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics);
        map.values().forEach(item -> {
            if (deviceId.equals(item.getDeviceId())) {
                list.add(item);
            }
        });
        return list;
    }


    /**
     * 获得token列表
     *
     * @return {@code List<String> }
     * @Author qindehua
     * @Date 2023/01/13
     **/
    private List<String> getTokenList(String userids) {
        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        List<String> list = new ArrayList<>();
        keys.forEach(key -> {
            LoginUser loginUser = redisCache.getCacheObject(key);
            /**sunshangeng 修改根据新的接收组获取用户token*/
            String[] array = userids.split(",");
            for (String userid : array) {
                SysUser sysUser = loginUser.getUser();
                if (sysUser.getUserId() == Long.parseLong(userid)) {
                    list.add(loginUser.getToken());
                }
            }
        });

        return list;
    }
}


