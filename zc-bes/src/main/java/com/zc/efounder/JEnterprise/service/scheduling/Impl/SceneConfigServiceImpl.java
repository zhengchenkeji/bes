package com.zc.efounder.JEnterprise.service.scheduling.Impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.domain.scheduling.*;
import com.zc.efounder.JEnterprise.mapper.scheduling.PlanConfigMapper;
import com.zc.efounder.JEnterprise.mapper.scheduling.SceneConfigMapper;
import com.zc.efounder.JEnterprise.mapper.scheduling.SceneModelMapper;
import com.zc.efounder.JEnterprise.service.scheduling.SceneConfigService;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.constant.LDCCmd;
import com.zc.connect.business.handler.SendMsgHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.*;

import static com.ruoyi.common.utils.SecurityUtils.getUsername;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:39 2022/11/5
 * @Modified By:
 */
@Service
public class SceneConfigServiceImpl implements SceneConfigService {

    @Resource
    private SceneConfigMapper sceneConfigMapper;

    @Resource
    private SceneModelMapper sceneModelMapper;

    @Resource
    PlanConfigMapper planConfigMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private DeviceTreeCache deviceTreeCache;

    @Resource
    private ModuleAndPointCache moduleAndPointCache;

    /**
     * @param sceneControl
     * @Description: 获取场景区域信息
     * @auther: wanghongjie
     * @date: 15:47 2022/11/5
     * @param: [SchedulingArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult sceneConfigAreaListInfo(SchedulingArea sceneControl) {
        List<SceneControl> sceneControlList = sceneConfigMapper.sceneConfigAreaListInfo(sceneControl);
        if (sceneControlList == null || sceneControlList.size() == 0) {
            return AjaxResult.success("无数据");
        }
        return AjaxResult.success("获取成功", sceneControlList);
    }

    /**
     * @Description: 添加区域
     * @auther: wanghongjie
     * @date: 17:44 2022/11/5
     * @param: [SchedulingArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult addSceneConfigArea(SchedulingArea schedulingArea) {

        if (schedulingArea.getParentId() == null || schedulingArea.getName() == null || StringUtils.isEmpty(schedulingArea.getName())) {
            return AjaxResult.error("参数错误");
        }
        schedulingArea.setCreateBy(getUsername());
        schedulingArea.setCreateTime(DateUtils.getNowDate());
        boolean isAdd = sceneConfigMapper.addSceneConfigArea(schedulingArea);
        if (!isAdd) {
            return AjaxResult.error("添加失败");
        }
        return AjaxResult.success("添加成功", schedulingArea);
    }

    /**
     * @Description: 修改区域
     * @auther: wanghongjie
     * @date: 17:44 2022/11/5
     * @param: [SchedulingArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult updateSceneConfigArea(SchedulingArea schedulingArea) {
        if (schedulingArea.getId() == null || schedulingArea.getName() == null || StringUtils.isEmpty(schedulingArea.getName())) {
            return AjaxResult.error("参数错误");
        }
        schedulingArea.setUpdateBy(getUsername());
        schedulingArea.setUpdateTime(DateUtils.getNowDate());

        boolean isUpdate = sceneConfigMapper.updateSceneConfigArea(schedulingArea);

        if (isUpdate) {
            return AjaxResult.success("修改成功");
        }
        return AjaxResult.error("修改失败");
    }

    /**
     * @param
     * @Description: 删除区域
     * @auther: wanghongjie
     * @date: 17:44 2022/11/5
     * @param: [SchedulingArea]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult deleteSceneConfigArea(Long[] ids) {

        DataReception dataReception = new DataReception();

        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }

        try {
            for (int i = 0; i < ids.length; i++) {

                int addDesignerArea = sceneConfigMapper.deleteSceneConfigArea(ids[i]);
                if (addDesignerArea > 0) {
                    //删除区域关联的场景  之前根据区域id获取所有的场景
                    SceneControl s = new SceneControl();
                    s.setSchedulingAreaId(ids[i]);
                    //获取所有场景
                    List<SceneControl> sceneControlList = sceneConfigMapper.getSceneControlList(s);
                    if (sceneControlList == null && sceneControlList.size() == 0) {
                        continue;
                    }
                    for (int j = 0; j < sceneControlList.size(); j++) {
                        Long id = sceneControlList.get(j).getId();
//                        //获取所有模式
//                        List<SceneModelControl> sceneModelControlList = sceneConfigMapper.getSceneModelControlList(id);
//                        if (sceneModelControlList == null && sceneModelControlList.size() == 0) {
//                            continue;
//                        }
//                        for (SceneModelControl model : sceneModelControlList) {
//                            //模式是否在计划中
//                            PlanController planController = new PlanController();
//                            planController.setModelControlId(model.getId());
//                            List<PlanController> planLists = planConfigMapper.selectAllPlanControllerList(planController);
//                            if (planLists.size() > 0) {
//                                return AjaxResult.error("模式已在计划中配置,请删除相应计划后再操作");
//                            }
//                            //删除模式
//                            int delModel = sceneModelMapper.deleteSceneModel(model.getId());
//                            if (delModel == 0) {
//                                throw new Exception("删除失败,请联系管理员!");
//                            }
//                            //删除模式关联的点位
//                            int delPoint = sceneModelMapper.delSceneModelPoint(model.getId().intValue());
//                            if (delPoint == 0) {
//                                throw new Exception("删除失败,请联系管理员!");
//                            }
//                        }
                        DataReception dataReception1 = deleteSceneConfigReturnBoolean(id);
                        if ("0".equals(dataReception1.getCode())) {
                            throw new Exception(dataReception1.getMsg());
                        }
                    }
                } else {
                    throw new Exception("删除失败,请联系管理员!");
                }
            }
        } catch (NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("参数错误！");
            e.printStackTrace();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
        }

        if ("0".equals(dataReception.getCode())) {
            return AjaxResult.error(dataReception.getMsg());
        }
        return AjaxResult.success("删除成功");
    }

    /**
     * @Description: 获取场景列表
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @Override
    public AjaxResult getSceneControlList(SceneControl sceneControl) {
        if (sceneControl.getSchedulingAreaId() == null) {
            return AjaxResult.error("参数错误，获取场景列表失败");
        }

        //查询该区域下所有子区域
        List<Long> areaIdList = sceneConfigMapper.sceneConfigAreaIdList(sceneControl.getSchedulingAreaId());

        List<SceneControl> sceneControlList = new ArrayList<>();
        for (Long l : areaIdList) {
            sceneControl.setSchedulingAreaId(l);
            List<SceneControl> sceneControls = sceneConfigMapper.getSceneControlList(sceneControl);
            sceneControlList.addAll(sceneControls);
        }
        if (sceneControlList == null || sceneControlList.size() == 0) {
            return AjaxResult.success("无数据");
        }
        return AjaxResult.success("获取成功", sceneControlList);
    }

    /**
     * @Description: 获取场景列表
     * @auther: gaojikun
     * @param: sceneControl
     * @return: List<SceneControl>
     */
    @Override
    public List<SceneControl> getSceneControlListReturnList(SceneControl sceneControl) {
        List<SceneControl> sceneControlList = new ArrayList<>();
        if (sceneControl.getSchedulingAreaId() == null) {
            return sceneControlList;
        }
        //查询该区域下所有子区域
        List<Long> areaIdList = sceneConfigMapper.sceneConfigAreaIdList(sceneControl.getSchedulingAreaId());


        for (Long l : areaIdList) {
            sceneControl.setSchedulingAreaId(l);
            List<SceneControl> sceneControls = sceneConfigMapper.getSceneControlList(sceneControl);
            sceneControlList.addAll(sceneControls);
        }
        return sceneControlList;
    }

    /**
     * @Description: 根据id获取场景列表
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @Override
    public AjaxResult getsceneConfig(SceneControl sceneControl) {
        if (sceneControl.getId() == null) {
            return AjaxResult.error("参数错误，获取场景信息失败");
        }
        sceneControl = sceneConfigMapper.getsceneConfig(sceneControl);
        return AjaxResult.success("获取成功", sceneControl);
    }

    /**
     * @Description: 添加场景信息
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @Override
    public AjaxResult addSceneConfig(SceneControl sceneControl) {
        if (sceneControl.getSchedulingAreaId() == null || sceneControl.getName() == null || StringUtils.isEmpty(sceneControl.getName())
                || sceneControl.getAlias() == null || StringUtils.isEmpty(sceneControl.getAlias())) {
            return AjaxResult.error("参数错误");
        }
        //场景名称查重
        SceneControl sceneControlCheck = sceneConfigMapper.getsceneConfigCheck(sceneControl);
        if (sceneControlCheck != null) {
            return AjaxResult.error("场景名称重复");
        }


        sceneControl.setCreateBy(getUsername());
        sceneControl.setCreateTime(DateUtils.getNowDate());

        boolean isAdd = sceneConfigMapper.addSceneConfig(sceneControl);
        if (!isAdd) {
            return AjaxResult.error("添加失败");
        }
        return AjaxResult.success("添加成功", sceneControl);
    }

    /**
     * @Description: 修改场景信息
     * @auther: gaojikunc
     * @param: sceneControl
     * @return: AjaxResult
     */
    @Override
    public AjaxResult updateSceneConfig(SceneControl sceneControl) {
        if (sceneControl.getId() == null || sceneControl.getName() == null || StringUtils.isEmpty(sceneControl.getName())) {
            return AjaxResult.error("参数错误");
        }
        //场景名称查重
        SceneControl sceneControlCheck = sceneConfigMapper.getsceneConfigCheck(sceneControl);
        if (sceneControlCheck != null) {
            return AjaxResult.error("场景名称重复");
        }

        sceneControl.setUpdateBy(getUsername());
        sceneControl.setUpdateTime(DateUtils.getNowDate());

        boolean isUpdate = sceneConfigMapper.updateSceneConfig(sceneControl);

        if (isUpdate) {
            return AjaxResult.success("修改成功");
        }
        return AjaxResult.error("修改失败");
    }

    /**
     * @Description: 获取ip和系统类型
     * @auther: gaojikunc
     * @param: treeId
     * @return: DataReception
     */
    private DataReception queryIpAndType(Long treeId) {
        DataReception returnObject = new DataReception();
        //根据点位查询所属系统类型
        DeviceTree tree = deviceTreeCache.getDeviceTreeByDeviceTreeId(treeId);
        Map<String, String> map = new HashMap<>();
        String f_node_attribution = String.valueOf(tree.getDeviceType());
        //如果为0则没有类型，直接返回
        if ("0".equals(f_node_attribution)) {
            returnObject.setCode("0");
            returnObject.setMsg("点位未配置系统类型");
            return returnObject;
        }
        map.put("f_node_attribution", f_node_attribution);

        //查询DDCip
        Point point = moduleAndPointCache.getPointByDeviceId(treeId);
        if (point.getControllerId() == 0) {
            returnObject.setCode("0");
            returnObject.setMsg("点位缓存未取到控制器ID");
            return returnObject;
        }
        Collection<Object> values = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller).values();
        for (Object val : values) {
            Controller con = (Controller) val;
            if (point.getControllerId() == con.getId()) {
                map.put("channelID", con.getIp());
            }
        }
        if (map.get("channelID") == null || "".equals(map.get("channelID"))) {
            returnObject.setCode("0");
            returnObject.setMsg("点位未配置系统类型");
            return returnObject;
        }
        returnObject.setCode("200");
        returnObject.setData(map);
        return returnObject;
    }

    /**
     * @Description: 删除场景信息
     * @auther: gaojikun
     * @param: ids
     * @return: AjaxResult
     */
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public AjaxResult deleteSceneConfig(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        //返回信息
        String MSG = "";
        try {
            String f_node_attribution = null;//所属系统，cross1、lamp2、energy3
            String channelID = null;
            Integer id1 = null;
            for (Long id : ids) {
                //是否下发信息
                boolean isSend = false;
                //下发信息id
                id1 = id.intValue();

                //获取当前场景下所有的模式
                SceneModelControl selectMomdel = new SceneModelControl();
                selectMomdel.setSceneId(id);
                List<SceneModelControl> modelList = sceneModelMapper.getSceneModelList(selectMomdel);

                if (modelList.size() > 0) {
                    //删除模式
                    for (SceneModelControl modelInfo : modelList) {
                        //模式是否在计划中
                        PlanController planController = new PlanController();
                        planController.setModelControlId(modelInfo.getId());
                        List<PlanController> planLists = planConfigMapper.selectAllPlanControllerList(planController);
                        if (planLists.size() > 0) {
                            return AjaxResult.error("模式已在计划中配置,请删除相应计划后再操作");
                        }
                        //不在计划中
                        SceneModelPointControl sceneModelPointControl = new SceneModelPointControl();
                        sceneModelPointControl.setSceneModelId(modelInfo.getId());
                        List<SceneModelPointControl> pointList = sceneModelMapper.getSceneModelPoint(sceneModelPointControl);
                        if (pointList.size() == 0) {
                            continue;
                        } else {
                            if (!isSend) {
                                isSend = true;
                            }
                            //删除模式点位
                            if (f_node_attribution == null || channelID == null) {
                                //获取ip和系统类型
                                DataReception datereturn = queryIpAndType(pointList.get(0).getPointId());
                                if ("0".equals(datereturn.getCode())) {
                                    MSG = "查询点位ip与所属系统失败";
                                    throw new Exception();
                                }
                                Map<String, String> returnMap = (Map<String, String>) datereturn.getData();
                                f_node_attribution = returnMap.get("f_node_attribution");
                                channelID = returnMap.get("channelID");
                            }

                            int delPointInfo = sceneModelMapper.delSceneModelPoint(modelInfo.getId().intValue());
                            if (delPointInfo == 0) {
                                MSG = "删除模式点位失败";
                                throw new Exception();
                            }

                        }
                        int delModelInfo = sceneModelMapper.deleteSceneModel(modelInfo.getId());
                        if (delModelInfo == 0) {
                            MSG = "删除模式失败";
                            throw new Exception();
                        }
                    }

                    if (!isSend) {
                        return AjaxResult.success("删除成功");
                    }
                }

                //删除场景
                int isDelete = sceneConfigMapper.deleteSceneConfig(id);
                if (isDelete == 0) {
                    MSG = "删除场景失败";
                    throw new Exception();
                }

                //下发信息
                if (isSend) {
                    if ("1".equals(f_node_attribution)) {
                        Boolean flagDDC = SendMsgHandler.deleteSceneDDC(channelID, id1);
                        if (!flagDDC) {
                            return AjaxResult.success("删除成功，下发失败");
                        }
                        // 添加订阅消息
                        MsgSubPubHandler.addSubMsg(DDCCmd.SCENE_DELETE, channelID);
                        return AjaxResult.success("删除成功，下发成功");
                    } else {
                        Boolean flagLDC = SendMsgHandler.deleteSceneLDC(channelID, id1);
                        if (!flagLDC) {
                            return AjaxResult.success("删除成功，下发失败");
                        }
                        // 添加订阅消息
                        MsgSubPubHandler.addSubMsg(LDCCmd.SCENE_DELETE, channelID);
                        return AjaxResult.success("删除成功，下发成功");

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(MSG);
        }
        return AjaxResult.success("删除成功");
    }

    /**
     * @Description: 删除场景信息
     * @auther: gaojikun
     * @param: ids
     * @return: boolean
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteSceneConfigReturnBoolean(Long id) {
        DataReception returndate = new DataReception();
        if (id == null) {
            returndate.setCode("0");
            returndate.setMsg("参数错误");
            return returndate;
        }
        //返回信息
        String MSG = "";
        try {
            String f_node_attribution = null;//所属系统，cross1、lamp2、energy3
            String channelID = null;
            Integer id1 = null;
            //是否下发信息
            boolean isSend = false;
            //下发信息id
            id1 = id.intValue();

            //获取当前场景下所有的模式
            SceneModelControl selectMomdel = new SceneModelControl();
            selectMomdel.setSceneId(id);
            List<SceneModelControl> modelList = sceneModelMapper.getSceneModelList(selectMomdel);

            if (modelList.size() > 0) {
                //删除模式
                for (SceneModelControl modelInfo : modelList) {
                    //模式是否在计划中
                    PlanController planController = new PlanController();
                    planController.setModelControlId(modelInfo.getId());
                    List<PlanController> planLists = planConfigMapper.selectAllPlanControllerList(planController);
                    if (planLists.size() > 0) {
                        returndate.setCode("0");
                        returndate.setMsg("模式已在计划中配置,请删除相应计划后再操作");
                        return returndate;
                    }
                    //不在计划中
                    SceneModelPointControl sceneModelPointControl = new SceneModelPointControl();
                    sceneModelPointControl.setSceneModelId(modelInfo.getId());
                    List<SceneModelPointControl> pointList = sceneModelMapper.getSceneModelPoint(sceneModelPointControl);
                    if (pointList.size() == 0) {
                        continue;
                    } else {
                        if (!isSend) {
                            isSend = true;
                        }
                        //删除模式点位
                        if (f_node_attribution == null || channelID == null) {
                            //获取ip和系统类型
                            DataReception datereturn = queryIpAndType(pointList.get(0).getPointId());
                            if ("0".equals(datereturn.getCode())) {
                                MSG = "查询点位ip与所属系统失败";
                                throw new Exception();
                            }
                            Map<String, String> returnMap = (Map<String, String>) datereturn.getData();
                            f_node_attribution = returnMap.get("f_node_attribution");
                            channelID = returnMap.get("channelID");
                        }

                        int delPointInfo = sceneModelMapper.delSceneModelPoint(modelInfo.getId().intValue());
                        if (delPointInfo == 0) {
                            MSG = "删除模式点位失败";
                            throw new Exception();
                        }

                    }
                    int delModelInfo = sceneModelMapper.deleteSceneModel(modelInfo.getId());
                    if (delModelInfo == 0) {
                        MSG = "删除模式失败";
                        throw new Exception();
                    }
                }

                if (!isSend) {
                    returndate.setMsg("删除场景成功");
                    return returndate;
                }
            }

            //删除场景
            int isDelete = sceneConfigMapper.deleteSceneConfig(id);
            if (isDelete == 0) {
                MSG = "删除场景失败";
                throw new Exception();
            }

            //下发信息
            if (isSend) {
                if ("1".equals(f_node_attribution)) {
                    Boolean flagDDC = SendMsgHandler.deleteSceneDDC(channelID, id1);
                    if (!flagDDC) {
                        returndate.setMsg("删除成功，下发失败");
                        return returndate;
                    }
                    // 添加订阅消息
                    MsgSubPubHandler.addSubMsg(DDCCmd.SCENE_DELETE, channelID);
                    returndate.setMsg("删除成功，下发成功");
                    return returndate;
                } else {
                    Boolean flagLDC = SendMsgHandler.deleteSceneLDC(channelID, id1);
                    if (!flagLDC) {
                        returndate.setMsg("删除成功，下发失败");
                        return returndate;
                    }
                    // 添加订阅消息
                    MsgSubPubHandler.addSubMsg(LDCCmd.SCENE_DELETE, channelID);
                    returndate.setMsg("删除成功，下发成功");
                    return returndate;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            returndate.setCode("0");
            returndate.setMsg(MSG);
            return returndate;
        }
        returndate.setMsg("删除成功");
        return returndate;
    }
}
