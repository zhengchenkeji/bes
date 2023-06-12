package com.zc.efounder.JEnterprise.service.sceneLink.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.result.ResultMap;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.commhandler.SceneLinkHandler;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.domain.sceneLink.Scene;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneActuator;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneLog;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneTrigger;
import com.zc.efounder.JEnterprise.mapper.sceneLink.SceneActuatorMapper;
import com.zc.efounder.JEnterprise.mapper.sceneLink.SceneMapper;
import com.zc.efounder.JEnterprise.mapper.sceneLink.SceneTriggerMapper;
import com.zc.efounder.JEnterprise.service.sceneLink.SceneLinkService;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description:
 * author: sunshangeng
 * date:2023/2/28 11:30
 */
@Service
public class SceneLinkServiceImpl implements SceneLinkService {

    @Resource
    private SceneMapper sceneMapper;


    @Resource
    private SceneTriggerMapper sceneTriggerMapper;


    @Resource
    private SceneActuatorMapper sceneActuatorMapper;

    @Resource
    private RedisCache redisCache;


    /**
     * 添加缓存
     */
    @PostConstruct
    private void init() {
        /**清空所有场景 执行器 触发器缓存*/
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_SceneLink_Scene);
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_SceneLink_Actuator);
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_SceneLink_Trigger);
        /**获取到所有场景*/
        List<Scene> scenes = sceneMapper.selectSceneList(null);
        scenes.stream().forEach(item -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Scene, item.getId(), item);
        });

        /**获取到所有触发器*/
        List<SceneTrigger> triggerList = sceneTriggerMapper.selectSceneTriggerAll();
        triggerList.stream().forEach(item -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger, item.getId(), item);
        });

        /**获取到所有执行器*/
        List<SceneActuator> actuatorList = sceneActuatorMapper.selectSceneActuatorList(null);
        actuatorList.stream().forEach(item -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Actuator, item.getId(), item);
        });
    }

    /***
     * @description:新增场景联动
     * @author: sunshangeng
     * @date: 2023/3/2 10:31
     * @param: [scene]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    @Transactional
    public AjaxResult insertScene(Scene scene) throws TaskException, SchedulerException {
        if (StringUtils.isBlank(scene.getName())// 场景名称
                || scene.getTriggerList().size() == 0 ///触发器
                || scene.getActuatorList().size() == 0 //执行器
        ) {
            return AjaxResult.error("传入的信息不完整！");
        }
        /**新增场景联动*/
        scene.setCreateTime(new Date());
        /**默认停止*/
        scene.setSceneStatus(0L);
        Boolean insertScene = sceneMapper.insertScene(scene);
        if (!insertScene) {
            return AjaxResult.error("创建场景时失败！");
        }
        /**新增触发器*/
        for (SceneTrigger trigger : scene.getTriggerList()) {
            trigger.setSceneId(scene.getId());
            trigger.setSceneStatus(scene.getSceneStatus().intValue());
            Boolean saveTrigger = sceneTriggerMapper.insertSceneTrigger(trigger);
            if (!saveTrigger) {
                throw new ServiceException("创建触发器时出错！");
            }
        }
        /**新增执行器*/
        for (SceneActuator actuator : scene.getActuatorList()) {
            actuator.setSceneId(scene.getId());
            Boolean saveActuator = sceneActuatorMapper.insertSceneActuator(actuator);
            if (!saveActuator) {
                throw new ServiceException("创建执行器时出错！");
            }
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Actuator, actuator.getId(), actuator);

        }
        /**将触发器数据存入缓存*/

        for (SceneTrigger trigger : scene.getTriggerList()) {
            /**判断是否需要启动定时任务*/
            if (trigger.getTriggerModeCode().equals("2")) {
                ResultMap resultMap = SceneLinkHandler.createJob(trigger.getCronExpression(), scene.getName());
                if (!resultMap.get("code").toString().equals("0")) {
                    /*失败*/
                    throw new ServiceException(resultMap.get("msg").toString());
                }
                trigger.setJobId(Long.parseLong(resultMap.get("msg").toString()));
                trigger.setUpdateTime(new Date());
                sceneTriggerMapper.updateSceneTrigger(trigger);
            }
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger, trigger.getId(), trigger);
        }

        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Scene, scene.getId(), scene);
        return AjaxResult.success("成功");
    }

    @Override
    public AjaxResult getInfo(Long id) {
        /**获取场景*/
        Scene scene = sceneMapper.selectSceneById(id);
        if (scene == null) {
            return AjaxResult.error("未查询到场景数据！");
        }
        /**获取触发器*/
        SceneTrigger trigger = new SceneTrigger();
        trigger.setSceneId(scene.getId());
        List<SceneTrigger> triggerList = sceneTriggerMapper.selectSceneTriggerList(trigger);
        scene.setTriggerList(triggerList);
        /**获取执行器*/
        SceneActuator actuator = new SceneActuator();
        actuator.setSceneId(scene.getId());
        List<SceneActuator> actuatorList = sceneActuatorMapper.selectSceneActuatorList(actuator);
        scene.setActuatorList(actuatorList);
        return AjaxResult.success(scene);
    }

    /**
     * @description:修改场景
     * @author: sunshangeng
     * @date: 2023/3/2 16:57
     * @param: [scene]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    @Transactional
    public AjaxResult edit(Scene scene) throws SchedulerException, TaskException {

        /**修改场景*/
        scene.setUpdateTime(new Date());
        Boolean updateScene = sceneMapper.updateScene(scene);
        if (!updateScene) {
            return AjaxResult.error("修改场景时失败");
        }

        /**修改触发器 先删除在新增*/

        Boolean deltriggers = sceneTriggerMapper.deleteBySceneIdBoolean(scene.getId());
        if (!deltriggers) {
            throw new ServiceException("修改场景时处理触发器出错！");
        }
        /**删除定时任务*/
        Map<String, SceneTrigger> triggerMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SceneLink_Trigger);
        for (SceneTrigger item : triggerMap.values()) {
            if (scene.getId() == item.getSceneId()) {
                /**判断是否需要删除定时任务*/
                if (item.getTriggerModeCode().equals("2")) {
                    ResultMap resultMap = SceneLinkHandler.deleteJob(item.getJobId());
                    if (!resultMap.get("code").toString().equals("0")) {
                        /*失败*/
                        throw new ServiceException(resultMap.get("msg").toString());
                    }
                }
            }
        }
        /**新增*/
        for (SceneTrigger trigger : scene.getTriggerList()) {
            trigger.setSceneId(scene.getId());

            trigger.setSceneStatus(scene.getSceneStatus().intValue());
            /**判断是否需要创建定时任务*/
            if (trigger.getTriggerModeCode().equals("2")) {
                ResultMap resultMap = SceneLinkHandler.createJob(trigger.getCronExpression(), scene.getName());
                if (!resultMap.get("code").toString().equals("0")) {
                    /*失败*/
                    throw new ServiceException(resultMap.get("msg").toString());
                }
                trigger.setJobId(Long.parseLong(resultMap.get("msg").toString()));
                trigger.setUpdateTime(new Date());
                Boolean insertTrigger = sceneTriggerMapper.insertSceneTrigger(trigger);
                if (!insertTrigger) {
                    throw new ServiceException("修改场景时处理触发器出错！");

                }
            } else {
                trigger.setUpdateTime(new Date());
                Boolean insertTrigger = sceneTriggerMapper.insertSceneTrigger(trigger);
                if (!insertTrigger) {
                    throw new ServiceException("修改场景时处理触发器出错！");
                }
            }

        }

        /**删除执行器*/
        Map<String, SceneActuator> actuatorMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SceneLink_Actuator);
        Boolean delActuator = sceneActuatorMapper.deleteBySceneIdBoolean(scene.getId());
        if (!delActuator) {
            throw new ServiceException("修改场景时处理执行器出错！");
        }
        /**处理执行器*/
        for (SceneActuator actuator : scene.getActuatorList()) {
            actuator.setSceneId(scene.getId());
            Boolean saveActuator = sceneActuatorMapper.insertSceneActuator(actuator);
            if (!saveActuator) {
                throw new ServiceException("修改场景时处理执行器出错！");
            }
        }
        /**最后处理触发器缓存*/
        triggerMap.values().forEach(item -> {
            if (scene.getId() == item.getSceneId()) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger, item.getId());
            }
        });
        scene.getTriggerList().forEach(item -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger, item.getId(), item);
        });
        /**处理执行器缓存*/
        actuatorMap.values().forEach(item -> {
            if (scene.getId() == item.getSceneId()) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Actuator, item.getId());
            }
        });
        scene.getActuatorList().forEach(item -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Actuator, item.getId(), item);
        });

        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Scene, scene.getId(), scene);
        return AjaxResult.success();
    }

    /***
     * @description:删除场景列表
     * @author: sunshangeng
     * @date: 2023/3/6 14:08
     * @param: [sceneId]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    @Transactional
    public AjaxResult del(Long sceneId) throws SchedulerException {
        /**获取到场景信息*/
        Scene scene = sceneMapper.selectSceneById(sceneId);
        boolean isdelscene = sceneMapper.deleteSceneById(sceneId);
        if (!isdelscene) {
            throw new ServiceException("删除场景信息失败！");
        }
        /**获取触发器*/
        SceneTrigger trigger = new SceneTrigger();
        trigger.setSceneId(scene.getId());
        List<SceneTrigger> triggerList = sceneTriggerMapper.selectSceneTriggerList(trigger);
        scene.setTriggerList(triggerList);


        /**删除触发器*/
        Boolean deltriggers = sceneTriggerMapper.deleteBySceneIdBoolean(scene.getId());
        if (!deltriggers) {
            /*删除失败*/
            throw new ServiceException("删除触发器时出错！");
        }
        for (SceneTrigger sceneTrigger : triggerList) {
            /**删除定时任务*/
            if (sceneTrigger.getTriggerModeCode().equals("2")) {
                ResultMap resultMap = SceneLinkHandler.deleteJob(sceneTrigger.getJobId());
                if (!resultMap.get("code").toString().equals("0")) {
                    /*失败*/
                    throw new ServiceException(resultMap.get("msg").toString());
                }
            }
        }
        /**删除执行器*/
        Map<String, SceneActuator> actuatorMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SceneLink_Actuator);
        Boolean delActuator = sceneActuatorMapper.deleteBySceneIdBoolean(scene.getId());
        if (!delActuator) {
            /*删除失败*/
            throw new ServiceException("删除触发器时出错！");
        }
        /**最后清空缓存器*/
        triggerList.forEach(item -> {
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger, item.getId());
        });
        actuatorMap.values().stream().filter(item -> item.getSceneId().equals(sceneId)).forEach(item -> {
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Actuator, item.getId());
        });

        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Scene, sceneId);

        return AjaxResult.success();
    }

    /**
     * @description:查询所有场景列表
     * @author: sunshangeng
     * @date: 2023/3/6 14:09
     * @param: [scene]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.sceneLink.Scene>
     **/
    @Override
    public List<Scene> getSceneList(Scene scene) {

        return sceneMapper.selectSceneList(scene);
    }

    @Override
    public List<Scene> getSceneListDic(Scene scene) {
        Map<String, Scene> sceneMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Scene);
        List<Scene> dicSceneList = new ArrayList<>();
        if (scene.getId() != null) {
            dicSceneList = sceneMap.values().stream().filter(item -> item.getId() != scene.getId()).collect(Collectors.toList());
        } else {
            dicSceneList = sceneMap.values().stream().collect(Collectors.toList());

        }
        return dicSceneList;
    }


    /**
     * @description:执行手动触发
     * @author: sunshangeng
     * @date: 2023/3/6 14:11
     * @param: [id]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult execute(Long id) {
        /**获取所有执行器*/

        try {
            /**判断当前场景是否有手动触发*/
            Map<String, SceneTrigger> triggerMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger);
            List<SceneTrigger> triggerList = triggerMap.values().stream()
                    .filter(item -> item.getSceneId().equals(id))
                    .filter(item -> item.getTriggerModeCode().equals("1"))
                    .collect(Collectors.toList());
            if (triggerList==null||triggerList.size()==0) {

                return AjaxResult.error("当前场景未配置手动触发");
            }

            SceneLinkHandler.executeActuator(id,triggerList.get(0).getId());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }

        return AjaxResult.success();
    }

    /***
     * @description:修改场景联动状态
     * @author: sunshangeng
     * @date: 2023/4/23 14:26
     * @param: [scene]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    @Transactional
    public AjaxResult changeStatus(Scene scene) throws TaskException, SchedulerException {


        /**数据完整性验证*/
        if (scene == null ||
                scene.getId() == null ||
                scene.getSceneStatus() == null) {

            return AjaxResult.error("变更失败,传入的数据不完整！");
        }

        Scene oldScene = sceneMapper.selectSceneById(scene.getId());
        if (oldScene == null) {
            return AjaxResult.error("变更失败,传入的数据不正确！");

        }

        oldScene.setSceneStatus(scene.getSceneStatus());
        oldScene.setUpdateTime(new Date());
        Boolean updateScene = sceneMapper.updateScene(oldScene);
        if (updateScene) {

            Scene cache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Scene, scene.getId());
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Scene, cache.getId(), cache);

            cache.setSceneStatus(scene.getSceneStatus());
            Map<String, SceneTrigger> triggerMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger);
            List<SceneTrigger> triggerList = triggerMap.values().stream()
                    .filter(item -> item.getSceneId().equals(scene.getId()))
                    .collect(Collectors.toList());


            for (SceneTrigger trigger : triggerList) {
                /**如果是定时触发需要更改定时任务状态*/
                if( trigger.getTriggerModeCode().equals("2")){
                    if (scene.getSceneStatus() == 0) {
                        SceneLinkHandler.pauseJob(trigger.getJobId());

                    } else {
                        SceneLinkHandler.resumeJob(trigger.getJobId());

                    }
                }

                /**处理触发器中的场景联动状态*/
                trigger.setSceneStatus(scene.getSceneStatus().intValue());
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SceneLink_Trigger,trigger.getId(),trigger);
            }
        } else {
            return AjaxResult.error("变更失败");

        }

        return AjaxResult.success("变更成功");
    }

    /**
     * @description:获取Bes设备树
     * @author: sunshangeng
     * @date: 2023/4/23 16:49
     * @param: [deviceTree]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult getBesDeviceTree(DeviceTree deviceTree) {
        List<DeviceTree> deviceTreeList = new ArrayList<>();
        Map<String, DeviceTree> stringdeviceTreeMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);
        stringdeviceTreeMap.forEach((key, value) -> {


            if (value.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_AO
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_AI
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_DO
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_DI
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_UI
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_UX
            ) {
                Point point = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) value.getDeviceTreeId());
                value.setControllerId(point.getControllerId());
                value.setEngineerUnit(point.getEngineerUnit());
                /**判断能源AI节点*/
                if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AI) {
                    /**判断当前是否是能耗节点*/
                    /**判断AI节点是否能耗采集*/

                    if (point.getEnergyStatics() != null && 1 == point.getEnergyStatics()) {

                        value.setEnergyNode(Boolean.TRUE);
                    }
                }
                /**判断虚点节点*/
                if (value.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT) {
                    /**获取虚点数据*/
                    /**判断虚点是否是VAI节点是否能耗采集*/
                    if (point.getEnergyStatics() == 1 && point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))) {
                        value.setEnergyNode(Boolean.TRUE);
                    }
                }

                /**判断电表*/
                if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AMMETER) {
                    value.setEnergyNode(Boolean.TRUE);
                }

                value.setEnergyNode(Boolean.FALSE);

            } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_COLLECTORNODE ||
                    value.getDeviceNodeId() == DeviceTreeConstants.BES_DDCNODE ||
                    value.getDeviceNodeId() == DeviceTreeConstants.BES_ILLUMINE
            ) {
                value.setEnergyNode(Boolean.FALSE);
            } else {
                value.setEnergyNode(Boolean.TRUE);
            }

            if (value.getSysName() == null || "".equals(value.getSysName())) {
                //如果点位信息没有则加上显示名称
                if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AI) {
                    value.setSysName("AI节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AO) {
                    value.setSysName("AO节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DI) {
                    value.setSysName("DI节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DO) {
                    value.setSysName("DO节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UI) {
                    value.setSysName("UI节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UX) {
                    value.setSysName("UX节点");
                    value.setEnergyNode(Boolean.FALSE);
                }
            }
            deviceTreeList.add(value);
        });

        if (deviceTreeList.size() > 0) {

            deviceTreeList.sort((o1, o2) -> String.valueOf(o1.getDeviceTreeId()).compareTo(String.valueOf(o2.getDeviceTreeId())));

            return AjaxResult.success("获取成功!", deviceTreeList);
        }
        return AjaxResult.error("获取失败!");
    }

    /**
     * @description:获取联动日志列表
     * @author: sunshangeng
     * @date: 2023/5/8 15:00
     * @param: [sceneLog]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.sceneLink.SceneLog>
     **/
    @Override
    public List<SceneLog> getSceneLog(SceneLog sceneLog) {
        return sceneMapper.selectSceneLogList(sceneLog);
    }

}
