package com.zc.efounder.JEnterprise.commhandler;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.ModbusFunctions;
import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.result.ResultMap;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.mapper.SysJobMapper;
import com.ruoyi.quartz.util.CronUtils;
import com.ruoyi.quartz.util.ScheduleUtils;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.NoticeTableConstants;
import com.zc.common.constant.WebSocketEvent;
import com.zc.common.core.websocket.WebSocketService;
import com.zc.connect.business.constant.EDCCmd;
import com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler.ModbusSendSyncMsgHandler;
import com.zc.connect.util.DataUtil;
import com.zc.connect.util.StringUtil;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.FunctionItemData;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import com.zc.efounder.JEnterprise.domain.baseData.ProductFunction;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.sceneLink.Scene;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneActuator;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneLog;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneTrigger;
import com.zc.efounder.JEnterprise.mapper.sceneLink.SceneActuatorMapper;
import com.zc.efounder.JEnterprise.mapper.sceneLink.SceneMapper;
import com.zc.efounder.JEnterprise.mapper.sceneLink.SceneTriggerMapper;
import com.zc.efounder.JEnterprise.service.deviceTree.ControllerService;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.constant.LDCCmd;
import com.zc.connect.business.dto.ddc.PointDataDDC;
import com.zc.connect.business.dto.ldc.PointDataLDC;
import com.zc.connect.business.handler.SendMsgHandler;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.domain.deviceTree.PointDebugLog;
import com.zc.efounder.JEnterprise.mapper.deviceTree.PointMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.FetchProfile;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * description:场景联动执行工具类
 * author: sunshangeng
 * date:2023/3/1 9:53
 */
@Component("SceneLinkHandler")
public class SceneLinkHandler {


    /**
     * 会将当前的执行任务的job信息传送过来
     */
    private SysJob job;


    private static final Logger log = LoggerFactory.getLogger(SceneLinkHandler.class);


    private static SysJobMapper jobMapper = ApplicationContextProvider.getBean(SysJobMapper.class);

    private static SceneTriggerMapper triggerMapper = ApplicationContextProvider.getBean(SceneTriggerMapper.class);
    private static SceneActuatorMapper actuatorMapper = ApplicationContextProvider.getBean(SceneActuatorMapper.class);

    /**
     * 定时任务处理类
     */
    private static Scheduler scheduler = ApplicationContextProvider.getBean(Scheduler.class);


    private static PointMapper pointMapper = ApplicationContextProvider.getBean(PointMapper.class);


    private static SceneMapper sceneMapper = ApplicationContextProvider.getBean(SceneMapper.class);

    private static ModuleAndPointCache moduleAndPointCache = ApplicationContextProvider.getBean(ModuleAndPointCache.class);

    private static RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);

    private static ControllerService controllerService = ApplicationContextProvider.getBean(ControllerService.class);

    private static ModbusSendSyncMsgHandler modbusSendSyncMsgHandler = ApplicationContextProvider.getBean(ModbusSendSyncMsgHandler.class);

    /**
     * 场景任务前缀名
     */
    public static final String SCENE_LINK_JOB_NAME_PREFIX = "场景联动_";

    /**
     * 场景任务组名
     */
    public static final String SCENE_LINK_JOB_GROUP = "SCENELINKEGROUP";

    /**
     * 场景任务的目标字符串
     */
    public static final String SCENE_LINK_JOB_INVOKETARGET = "SceneLinkHandler.SceneLinkJobExecute";

    /**
     * 场景任务的执行策略 默认中断后 执行一次
     */
    public static final String SCENE_LINK_JOB_MISFIREPOLICY = "2";

    /**
     * 场景任务的并发配置 默认允许
     */
    public static final String SCENE_LINK_JOB_CONCURRENT = "0";


    /**
     * 场景任务的启动状态 默认不运行运行
     */
    public static final String SCENE_LINK_JOB_STATUS = "1";

    /**
     * 场景任务的启动状态 上线
     */
    public static final String SCENE_LINK_DEVICEACTION_UP = "1";
    /**
     * 场景触发的设备动作 离线
     */
    public static final String SCENE_LINK_DEVICEACTION_DOWN = "2";
    /**
     * 场景任务的启动状态 点位属性
     */
    public static final String SCENE_LINK_DEVICEACTION_ATTRIBUTE = "3";

    /**
     * @description:创建定时触发的触发器
     * @author: sunshangeng
     * @date: 2023/3/1 9:55
     * @param: [cron表达式, 场景名称]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap createJob(String cron, String SceneName) throws TaskException, SchedulerException {

        /**验证cron表达式 是否正确*/
        if (!CronUtils.isValid(cron)) {
            return ResultMap.error("失败，Cron表达式不正确");
        }
        /**组装job数据*/
        SysJob sysJob = new SysJob();
        sysJob.setJobName(SCENE_LINK_JOB_NAME_PREFIX + SceneName);
        sysJob.setJobGroup(SCENE_LINK_JOB_GROUP);
        sysJob.setInvokeTarget(SCENE_LINK_JOB_INVOKETARGET);
        sysJob.setCronExpression(cron);
        /**执行策略详解：
         1立即执行（所有misfire的任务会马上执行）打个比方，如果9点misfire了，在10：15系统恢复之后，9点，10点的misfire会马上执行
         2执行一次（会合并部分的misfire，正常执行下一个周期的任务）假设9，10的任务都misfire了，系统在10：15分起来了。只会执行一次misfire，下次正点执行。
         3放弃执行(所有的misfire不管，执行下一个周期的任务)*/
        sysJob.setMisfirePolicy(SCENE_LINK_JOB_MISFIREPOLICY);
        sysJob.setConcurrent(SCENE_LINK_JOB_CONCURRENT);
        sysJob.setStatus(SCENE_LINK_JOB_STATUS);
        sysJob.setCreateBy("admin");
        sysJob.setUpdateBy("admin");
        sysJob.setRemark("创建场景时定义的任务");
        Boolean isSave = jobMapper.insertJob(sysJob);
        if (isSave) {
            ScheduleUtils.createScheduleJob(scheduler, sysJob);
        }
        return ResultMap.ok(sysJob.getJobId().toString());
    }

    /**
     * @description:暂停任务
     * @author: sunshangeng
     * @date: 2023/3/1 13:56
     * @param: [id]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap pauseJob(Long id) throws TaskException, SchedulerException {
        SysJob sysJob = jobMapper.selectJobById(id);
        String jobGroup = sysJob.getJobGroup();
        sysJob.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = jobMapper.updateJob(sysJob);
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(id, jobGroup));
        } else {
            return ResultMap.error("未查找到该任务！");

        }
        return ResultMap.ok();
    }

    /***
     * @description:*恢复任务
     * @author: sunshangeng
     * @date: 2023/3/1 13:56
     * @param: [id]
     * @return: com.ruoyi.common.core.result.ResultMap
     */
    public static ResultMap resumeJob(Long id) throws TaskException, SchedulerException {
        SysJob sysJob = jobMapper.selectJobById(id);
        String jobGroup = sysJob.getJobGroup();
        sysJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = jobMapper.updateJob(sysJob);
        if (rows > 0) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(id, jobGroup));
        } else {
            return ResultMap.error("未查找到该任务！");
        }
        return ResultMap.ok();
    }

    /**
     * @description:删除定时任务
     * @author: sunshangeng
     * @date: 2023/3/1 13:56
     * @param: [id]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap deleteJob(Long id) throws SchedulerException {
        SysJob sysJob = jobMapper.selectJobById(id);

        String jobGroup = sysJob.getJobGroup();
        int rows = jobMapper.deleteJobById(id);
        if (rows > 0) {
            scheduler.deleteJob(ScheduleUtils.getJobKey(id, jobGroup));
        } else {
            return ResultMap.error("未查找到该任务");
        }
        return ResultMap.ok();
    }


    /**
     * @description:修改任务
     * @author: sunshangeng
     * @date: 2023/3/1 13:50
     * @param: [job, jobGroup]
     * @return: void
     **/
    public static ResultMap updateSchedulerJob(Long id, String cron) throws SchedulerException, TaskException {
        /**验证cron表达式 是否正确*/
        if (!CronUtils.isValid(cron)) {
            return ResultMap.error("失败，Cron表达式不正确");
        }
        SysJob sysJob = jobMapper.selectJobById(id);
        sysJob.setCronExpression(cron);
        int row = jobMapper.updateJob(sysJob);
        if (row > 0) {
            // 判断是否存在
            JobKey jobKey = ScheduleUtils.getJobKey(id, sysJob.getJobGroup());
            if (scheduler.checkExists(jobKey)) {
                // 防止创建时存在数据问题 先移除，然后在执行创建操作
                scheduler.deleteJob(jobKey);
            }
            ScheduleUtils.createScheduleJob(scheduler, sysJob);
            return ResultMap.ok();
        } else {
            return ResultMap.error("修改未成功！");
        }
    }


    /***
     * @description:定时的场景触发方法
     * @author: sunshangeng
     * @date: 2023/3/1 10:20
     * @param: []
     * @return: void
     **/
    public void SceneLinkJobExecute() {
        Long jobId = job.getJobId();
        Map<String, SceneTrigger> triggerMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger);
        List<SceneTrigger> triggerList = triggerMap.values().stream().filter(item -> item.getSceneStatus() == 1)
                .filter(item -> item.getTriggerModeCode().equals("2"))
                .filter(item -> item.getJobId().equals(jobId)).collect(Collectors.toList());
        /**未查询到定时任务信息*/
        if (triggerList == null || triggerList.size() == 0) {
            return;
        }
        triggerList.forEach(item -> {
            /**执行器操作*/
            executeActuator(item.getSceneId(), item.getId());

        });
    }

    /**
     * @description:设备触发
     * @author: sunshangeng
     * @date: 2023/4/28 17:26
     * @param: [设备ID, 设备类型0：bes 1：other]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap deviceTrigger(String deviceId, String type, String actionType, String actionValue) {
        if (!StringUtils.hasText(type) || !StringUtils.hasText(deviceId)) {
            return ResultMap.error("传入的参数不完整");
        }
//        if (type.equals("0")) {
//            /**处理bes设备*/
//            DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, deviceId);
//            if (deviceId == null) {
//                return ResultMap.error("无法成功查询设备");
//            }
//
//        } else {
//            /**处理第三方设备*/
//            String eqid = deviceId.substring(0, deviceId.indexOf("_"));
//            String functionId = deviceId.substring(deviceId.indexOf("_") + 1, deviceId.length());
//            Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, eqid);
//            ProductFunction productFunction = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, functionId);
//            if (equipment == null || productFunction == null) {
//                return ResultMap.error("无法成功查询设备");
//            }
//        }


        /**根据触发指令查找触发器 获取到所有触发器*/
        Map<String, SceneTrigger> triggerMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger);
        List<SceneTrigger> triggerList = triggerMap.values().stream()
                .filter(item -> item.getTriggerModeCode().equals("3"))      /**将设备触发的过滤出来*/
                .filter(item -> item.getDeviceId().equals(deviceId))   /**将当前设备绑定的所有触发器查询出来*/
                .filter(item -> item.getDeviceInstruct().equals(actionType))/**将当前设备的执行动作过滤出来*/
                .filter(item -> item.getDeviceInstruct().equals(actionType))/**将当前设备的执行动作过滤出来*/
                .filter(item-> item.getSceneStatus()==0)/**过滤掉其他处理的数据*/
                .collect(Collectors.toList());
        if (triggerList.size() == 0) {
            /**代表当前设备未绑定触发器 直接返回*/
            return ResultMap.ok();
        }
        for (SceneTrigger trigger : triggerList) {
            switch (actionType) {
                case SCENE_LINK_DEVICEACTION_UP:
                case SCENE_LINK_DEVICEACTION_DOWN:
                    executeActuator(trigger.getSceneId(), trigger.getId());
                    break;
                case SCENE_LINK_DEVICEACTION_ATTRIBUTE:
                    Boolean execute = false;

                    if (!StringUtils.hasText(actionValue)) {
                        return ResultMap.error("设备触发，点位属性触发时属性值传入为空！");
                    }

                    switch (trigger.getOperator()) {
                        case "1"://等于
                            if (trigger.getOperatorValue().equals(actionValue)) {
                                execute = true;
                            }
                            break;
                        case "2"://大于
                            if (Double.parseDouble(actionValue) > Double.parseDouble(trigger.getOperatorValue())) {
                                execute = true;
                            }
                            break;
                        case "3"://小于
                            if (Double.parseDouble(actionValue) < Double.parseDouble(trigger.getOperatorValue())) {
                                execute = true;
                            }
                            break;
                        case "4"://大于等于
                            if (Double.parseDouble(actionValue) >= Double.parseDouble(trigger.getOperatorValue())) {
                                execute = true;
                            }
                            break;
                        case "5"://小于等于
                            if (Double.parseDouble(actionValue) <= Double.parseDouble(trigger.getOperatorValue())) {
                                execute = true;
                            }
                            break;
                        case "6"://不等于
                            if (!trigger.getOperatorValue().equals(actionValue)) {
                                execute = true;
                            }
                            break;
                    }

                    if (execute) {
                        executeActuator(trigger.getSceneId(), trigger.getId());

                    }

                    break;
            }
        }

//        if(type.equals("0")){
//
//
//        }else if(type.equals("1")){
//
//        }else{
//            return ResultMap.error("动作指令错误");
//        }

//        /**进行执行器的处理*/
//        triggerList.forEach(item -> {
//            /**获取到场景信息*/
//            Long sceneId = item.getSceneId();
//            /**排除掉已停止的场景信息*/
//            if (item.getSceneStatus() != 0) {
//                executeActuator(sceneId);
//
//            }
//        });


        return ResultMap.ok();
    }

    /**
     * @description:场景触发
     * @author: sunshangeng
     * @date: 2023/3/1 15:50
     * @param: [当前执行的场景id]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap ExecuteSceneTrigger(String sceneId) {

        if (!StringUtils.hasText(sceneId)) {
            return ResultMap.error("传入的场景id为空！");
        }
//        redisCache.setCacheMapValue(RedisKeyConstants.BES_SceneLink_Trigger,item.getId(),item);

        Map<String, SceneTrigger> triggerMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SceneLink_Trigger);
        List<SceneTrigger> triggerList = triggerMap.values().stream()
                .filter(item -> item.getTriggerModeCode().equals("4"))
                .filter(item -> item.getSceneStatus() == 1)
                .filter(item -> {
                    if (item.getTriggerSceneId().indexOf(sceneId) != -1) {
                        return true;

                    } else {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        if (triggerList == null || triggerList.size() == 0) {
            /**代表未查询到有绑定的场景触发*/
            return ResultMap.ok();
        }


        /**处理执行器*/
        triggerList.forEach(item -> {

            executeActuator(item.getSceneId(), item.getId());
            /**执行器处理方法*/
        });

        return ResultMap.ok();
    }


    /**
     * 调试点位
     *
     * @param treeId 点位树id
     * @param value  值
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/03/01
     **/
    public static DataReception debugPointInfo(Long treeId, Double value) {

        Integer workMode = 1;
        if (treeId == null || value == null) {
            log.error("调试点位参数错误!");
            return new DataReception(false, "参数错误!");
        }

        Controller controllerPoint = null;

        Double accuracy;

        Point pointMap = moduleAndPointCache.getPointByDeviceId(treeId);
        if (pointMap == null || pointMap.getEquipmentId() == null ||
                pointMap.getControllerId() == 0 || pointMap.getNodeType() == null ||
                pointMap.getSysName() == null || pointMap.getWorkMode() == null) {
            log.error("参数错误,点位缓存未获取到点位数据!");
            return new DataReception(false, "参数错误,点位缓存未获取到点位数据!");
        }

        PointDebugLog pointDebugLog = new PointDebugLog();
        pointDebugLog.setCreateTime(DateUtils.getNowDate());
        pointDebugLog.setDeviceTreeId(treeId.intValue());
        pointDebugLog.setSysName(pointMap.getSysName());
        pointDebugLog.setOperatValue(value.toString());
//        if (SecurityUtils.getUsername() == null) {
//            log.error("未获取到操作人信息!");
//            return new DataReception(false,"未获取到操作人信息!");
//        }
//        pointDebugLog.setCreateName(SecurityUtils.getUsername());
        //场景配置 暂无操作人
        pointDebugLog.setCreateName("场景配置");

        //查询该逻辑点所在的控制器的ip
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller).values();
        for (Object obj : values) {
            Controller con = (Controller) obj;
            if (con.getId() == pointMap.getControllerId()) {
                controllerPoint = con;
            }
        }

        if (controllerPoint == null || controllerPoint.getIp() == null) {
            log.error("参数错误,控制器缓存未获取到控制器数据!");
            return new DataReception(false, "参数错误,控制器缓存未获取到控制器数据!");
        }

        String id = String.valueOf(pointMap.getEquipmentId());
        String nodeType = pointMap.getNodeType();

        //如果精度不为空或者不为0则进行换算
        Long accuracyLong = pointMap.getAccuracy();
        if (null != accuracyLong && accuracyLong > 0) {
            accuracy = Double.valueOf(String.valueOf(pointMap.getAccuracy()));
            value = value * Math.pow(10, accuracy);
        }


        BigDecimal b = new BigDecimal(value);
        double f1 = b.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        String aa = String.valueOf(f1);
        Integer v = Integer.parseInt(aa.substring(0, aa.indexOf(".")));

        String channelId = controllerPoint.getIp();

        if (v == null) {
            log.error("请输入值!");
            return new DataReception(false, "请输入值!");
        }
        if (!StringUtils.hasText(channelId) || controllerPoint.getType() == 0) {
            log.error("参数错误,控制器缓存未获取到控制器ip和类型!");
            return new DataReception(false, "参数错误,控制器缓存未获取到控制器ip和类型!");
        }

        if (Integer.parseInt(nodeType) == DeviceTreeConstants.BES_VPOINT) {
            workMode = 0;
        }

        if (controllerPoint.getType().equals(DeviceTreeConstants.BES_DDCNODE)) {//楼控

            PointDataDDC pointData = new PointDataDDC();
            pointData.setId(Integer.valueOf(id));
            pointData.setValue(v);
            pointData.setWorkMode(workMode);
            boolean sendState = SendMsgHandler.setPointValueDDC(channelId, pointData);

            if (!sendState) {
                // 存储操作记录
                pointDebugLog.setDebugState("0");
                pointMapper.addDebugLog(pointDebugLog);
                return new DataReception(false, "下发失败");
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.POINT_VALUE_SET, channelId);
            // 存储操作记录
            pointDebugLog.setDebugState("1");
            pointMapper.addDebugLog(pointDebugLog);
            //修改缓存信息工作模式
            if (pointMap.getWorkMode() != workMode.longValue()) {
                pointMap.setWorkMode(workMode.longValue());
                pointMapper.updatePoint(pointMap);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, treeId, pointMap);
            }
            return new DataReception(true, "下发成功!");

        } else if (controllerPoint.getType().equals(DeviceTreeConstants.BES_ILLUMINE)) {//照明

            PointDataLDC pointDataLDC = new PointDataLDC();
            pointDataLDC.setId(Integer.valueOf(id));
            pointDataLDC.setValue(v);
            pointDataLDC.setWorkMode(workMode);
            boolean sendState = SendMsgHandler.setPointValueLDC(channelId, pointDataLDC);

            if (!sendState) {
                // 存储操作记录
                pointDebugLog.setDebugState("0");
                pointMapper.addDebugLog(pointDebugLog);
                return new DataReception(false, "下发失败!");
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.POINT_VALUE_SET, channelId);
            // 存储操作记录
            pointDebugLog.setDebugState("1");
            pointMapper.addDebugLog(pointDebugLog);
            //修改缓存信息工作模式
            if (pointMap.getWorkMode() != workMode.longValue()) {
                pointMap.setWorkMode(workMode.longValue());
                pointMapper.updatePoint(pointMap);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, treeId, pointMap);
            }
            return new DataReception(true, "下发成功!");
        }

        // 存储操作记录
        pointDebugLog.setDebugState("0");
        pointMapper.addDebugLog(pointDebugLog);
        return new DataReception(false, "下发失败!");
    }


    /***
     * @description:执行同步控制器操作
     * @author: sunshangeng
     * @date: 2023/4/28 16:34
     * @param:
     * @return:
     **/
    public static ResultMap ExecuteSynchronizeController(Long treeId) {

        DeviceTree tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, treeId);
        AjaxResult ajaxResult = controllerService.synchronizeDDC(treeId, tree.getDeviceType(), false);
        if (ajaxResult.get("code").toString().equals("200")) {
            return ResultMap.ok();
        } else {
            return ResultMap.error(ajaxResult.get("msg") + "");

        }
    }

    /***
     * @description:执行第三方设备指令下发
     * @author: sunshangeng
     * @date: 2023/4/28 16:34
     * @param:
     * @return:
     **/
    public static ResultMap ExecuteOtherFunctionSend(String deviceOrfunctionId) {

        /**设备ID*/
        String deviceId = deviceOrfunctionId.substring(0, deviceOrfunctionId.indexOf("_"));
        /**功能id*/
        String functionId = deviceOrfunctionId.substring(deviceOrfunctionId.indexOf("_") + 1, deviceOrfunctionId.length());
        Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, deviceId);
        ProductFunction function = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, functionId);
        if (equipment == null || function == null) {
            /**无法正常查询 设备和方法*/
            return ResultMap.error("配置的设备有问题");
        }
        /**获得设备所属的IP和端口*/
        Long productId = equipment.getProductId();
        Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, productId);
        if (product == null) {
            return ResultMap.error("获取产品信息失败");
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
                    return ResultMap.error("获取设备信息失败");

                }
                ip = parentEquipment.getIpAddress();
                port = Integer.parseInt(parentEquipment.getPortNum());
                /**网关子设备*/
                break;

        }

        try {
            modbusSendSyncMsgHandler.issued1(ip, port, equipment, null, function.getId(), null);
        } catch (NoSuchAlgorithmException e) {
            log.error("采集时出错，", e);
        }

        return ResultMap.ok();
    }


    /***
     * @description:重启控制器
     * @author: sunshangeng
     * @date: 2023/5/4 17:27
     * @param: 树ID
     * @return: com.ruoyi.common.core.result.ResultMap
     **/

    public static ResultMap ExecuteResetBesController(Long treeid) {
        Controller controller = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, treeid);
        if (controller == null) {
            log.error("重启时查询不到数据控制器数据，树ID为：" + treeid);
            return ResultMap.error("未查询到控制器");
        }
        String channelID = controller.getIp();
        Integer type = controller.getDeviceNodeId();
        /**DDC节点*/
        if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
            boolean sendState = SendMsgHandler.restartControllerDDC(channelID);
            if (!sendState) {
                return ResultMap.error("下发失败!");
            }
        }
        /**照明节点*/
        else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
            boolean sendState = SendMsgHandler.restartControllerLDC(channelID);

            if (!sendState) {
                return ResultMap.error("下发失败!");
            }

        }
        /**采集器节点*/
        else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
            boolean sendState = SendMsgHandler.restartControllerEDC(channelID);
            if (!sendState) {
                return ResultMap.error("下发失败!");
            }

        } else {
            return ResultMap.error("非法参数，下发失败!");
        }
        return ResultMap.ok();
    }


    /**
     * @description:设置时间
     * @author: sunshangeng
     * @date: 2023/5/5 16:06
     * @param: [树ID]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap ExecuteSetTimeController(Long treeid) {
        Controller controller = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, treeid);
        if (controller == null) {
            log.error("同步时间时查询不到数据控制器数据，树ID为：" + treeid);
            return ResultMap.error("未查询到控制器");
        }
        String channelID = controller.getIp();
        Integer type = controller.getDeviceNodeId();
        /**DDC节点*/
        /**DDC节点*/
        if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
            boolean sendState = SendMsgHandler.setControllerTimeDDC(channelID, DataUtil.getTimeDataObject());
            if (!sendState) {
                return ResultMap.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_TIME_SET, channelID);
        }
        /**照明节点*/
        else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
            boolean sendState = SendMsgHandler.setControllerTimeLDC(channelID, DataUtil.getTimeDataObject());
            if (!sendState) {
                return ResultMap.error("下发失败!");
            }

            MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_TIME_SET, channelID);
        }
        /**采集器节点*/
        else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
            boolean sendState = SendMsgHandler.setControllerTimeEDC(channelID, DataUtil.getTimeDataObject());
            if (!sendState) {
                return ResultMap.error("下发失败!");
            }

            MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_TIME_SET, channelID);
        } else {
            return ResultMap.error("非法参数，下发失败!");
        }
        return ResultMap.ok();
    }

    /**
     * @description:执行执行器
     * @author: sunshangeng
     * @date: 2023/5/4 10:43
     * @param: 场景ID，触发器ID
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap executeActuator(Long sceneid, Long triggerId) {


        /**触发成功后 执行 场景触发*/
        ExecuteSceneTrigger(sceneid + "");
        /**获取所有执行器*/
        Map<String, SceneActuator> actuatorMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Actuator);
        /**进行执行器的过滤*/
        List<SceneActuator> actuatorList = actuatorMap.values().stream()
                /**过滤掉非当前场景的执行器*/
                .filter(sceneActuator -> sceneActuator.getSceneId().equals(sceneid))
                .collect(Collectors.toList());

        executeSaveSceneLog(sceneid, triggerId, actuatorList);
        /**执行*/
        for (SceneActuator actuator : actuatorList) {

            if (actuator.getMovementMode() == 1) {

                Long templateId = actuator.getNoticeTemplate() == null ? null : Long.parseLong(actuator.getNoticeTemplate());
                /**消息通知*/
                ResultMap resultMap = NoticeHandler.sendNotice(Long.parseLong(actuator.getNoticeConfig())
                        , templateId
                        , actuator.getUserIds().split(",")
                        , actuator.getContent()
                        , NoticeTableConstants.BES_SCENE_ACTUATOR
                        , actuator.getId() + "");

                if (actuator.getExecuteType().equals("3")) {
                    /**如果是语音播报 发送到前端*/
                    //获取符合角色的token列表
                    List<String> tokens = getTokenList(actuator.getUserIds());
                    String resultJson = resultMap.get("msg").toString();
                    /**组装json对象*/
                    tokens.forEach(token -> {
                        // 推送消息到web客户端
                        WebSocketService.postEvent(token, WebSocketEvent.ALARMMSG, resultJson);
                    });
                }


                if (!resultMap.get("code").toString().equals("0")) {
                    log.error("场景ID为：" + sceneid + ",执行消息推送时，出现错误响应消息为：" + resultMap.get("msg"));
                }
            } else {
                /**设备输出*/

                switch (actuator.getDeviceType()) {
                    case "0":
                        /**bes 设备*/
                        if (actuator.getExecuteType().equals("1")) {
                            /**设置属性*/
                            DataReception dataReception = debugPointInfo(Long.parseLong(actuator.getTreeId()), Double.parseDouble(actuator.getExecuteValue()));
                            if (!dataReception.getState()) {
                                log.error("场景ID为：" + sceneid + ",执行点位设置属性时，出现错误响应消息为：" + dataReception.getMsg());

                            }

                        } else {
                            /**调用功能*/
                            if (actuator.getExecuteAttribute().equals("2")) {
                                /**重启*/
                                ResultMap resultMap = ExecuteResetBesController(Long.parseLong(actuator.getTreeId()));
                                if (!resultMap.get("code").toString().equals("0")) {
                                    log.error("场景ID为：" + sceneid + ",执行控制器重启时，出现错误响应消息为：" + resultMap.get("msg"));
                                }


                            } else if (actuator.getExecuteAttribute().equals("3")) {
                                /**同步设备树*/
                                ResultMap resultMap = ExecuteSynchronizeController(Long.parseLong(actuator.getTreeId()));
                                if (!resultMap.get("code").toString().equals("0")) {
                                    log.error("场景ID为：" + sceneid + ",执行同步控制器同步时，出现错误响应消息为：" + resultMap.get("msg"));
                                }
                            } else if (actuator.getExecuteAttribute().equals("4")) {
                                ResultMap resultMap = ExecuteSynchronizeController(Long.parseLong(actuator.getTreeId()));
                                if (!resultMap.get("code").toString().equals("0")) {
                                    log.error("场景ID为：" + sceneid + ",执行同步控制器时间时，出现错误响应消息为：" + resultMap.get("msg"));
                                }
                            }
                        }
                        break;
                    case "1":
                        /**other 设备*/
                        ResultMap resultMap = ExecuteOtherFunctionSend(actuator.getTreeId());
                        if (!resultMap.get("code").toString().equals("0")) {
                            log.error("场景ID为：" + sceneid + ",执行第三方设备功能下发时，出现错误响应消息为：" + resultMap.get("msg"));
                        }
                        break;

                }

            }


        }
        return ResultMap.ok();
    }

    /**
     * @description:执行保存场景联动日志
     * @author: sunshangeng
     * @date: 2023/5/8 9:46
     * @param: [sceneid, triggerId, actuatorList]
     * @return: com.ruoyi.common.core.result.ResultMap
     **/
    public static ResultMap executeSaveSceneLog(Long sceneid, Long triggerId, List<SceneActuator> actuatorList) {

        /**获取到场景信息*/
        Scene scene = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Scene, sceneid);
        /**获取到触发器信息*/
        SceneTrigger trigger = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger, triggerId);

        /**组装场景日志信息*/
        SceneLog sceneLog = new SceneLog();
        sceneLog.setExecuteTime(new Date());
        sceneLog.setSceneName(scene.getName());
        sceneLog.setSceneId(scene.getId());
        sceneLog.setTriggerId(triggerId);
        sceneLog.setTriggerModeCode(trigger.getTriggerModeCode());
        String actuatorIds = "";
        for (int i = 0; i < actuatorList.size(); i++) {
            if (actuatorIds.length() == 0 || i == actuatorList.size() - 1) {
                actuatorIds = actuatorIds + actuatorList.get(i).getId();
            } else {
                actuatorIds = actuatorIds + actuatorList.get(i).getId() + ",";
            }
        }
        sceneLog.setActuatorIds(actuatorIds);


        String triggerLabel = DictUtils.getDictLabel("trigger_code", trigger.getTriggerModeCode());
        String triggerContent = "触发时间：" + DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, sceneLog.getExecuteTime());
        switch (trigger.getTriggerModeCode()) {
            case "1":
            case "2":
                triggerContent = triggerContent + "，触发方式：" + triggerLabel + "";
                break;
            case "3":
                triggerContent = triggerContent + "，触发方式：" + triggerLabel + "，";

                if (trigger.getDeviceInstruct().equals("3")) {
                    String label = DictUtils.getDictLabel("scene_operator_type", trigger.getOperator());

                    triggerContent = triggerContent + trigger.getDeviceName() + "接收到数据触发了" + label + trigger.getOperatorValue() + "的操作";

                    /**点位*/
//                    if(trigger.getDeviceType().equals("0")){
//
//                    }else{
//
//                    }
                } else {
                    String instruct = trigger.getDeviceInstruct().equals("1") ? "上线" : "离线";
                    /**控制器*/
                    triggerContent = triggerContent + trigger.getDeviceName() + "触发了" + instruct + "操作。";
                }
                break;
            case "4":
                triggerContent = triggerContent + "，触发方式：" + triggerLabel + "，";
                Scene triggerScene = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Scene, trigger.getTriggerSceneId());
                triggerContent = triggerContent + "因" + triggerScene.getName() + "场景执行，触发了" + scene.getName() + "的运行";
                break;
        }

        sceneLog.setTriggerContent(triggerContent);

        Boolean savelog = sceneMapper.insertSceneLog(sceneLog);
        if (savelog) {
            return ResultMap.ok();
        } else {
            return ResultMap.error("插入场景日志信息失败");
        }
    }

    public SysJob getJob() {
        return job;
    }

    public void setJob(SysJob job) {
        this.job = job;
    }


    /**
     * 获得token列表
     *
     * @return {@code List<String> }
     * @Author qindehua
     * @Date 2023/01/13
     **/
    public static List<String> getTokenList(String userids) {
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
