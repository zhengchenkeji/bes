package com.zc.efounder.JEnterprise.service.scheduling.Impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.service.deviceTree.impl.ModuleServiceImpl;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.domain.scheduling.PlanController;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneControl;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneModelControl;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneModelPointControl;
import com.zc.efounder.JEnterprise.mapper.scheduling.PlanConfigMapper;
import com.zc.efounder.JEnterprise.mapper.scheduling.SceneConfigMapper;
import com.zc.efounder.JEnterprise.mapper.scheduling.SceneModelMapper;
import com.zc.efounder.JEnterprise.service.scheduling.SceneModelService;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.constant.LDCCmd;
import com.zc.connect.business.dto.ddc.ControlModeDDC;
import com.zc.connect.business.dto.ddc.ControlParamDDC;
import com.zc.connect.business.dto.ddc.ControlPointDDC;
import com.zc.connect.business.dto.ldc.ControlModeLDC;
import com.zc.connect.business.dto.ldc.ControlParamLDC;
import com.zc.connect.business.dto.ldc.ControlPointLDC;
import com.zc.connect.business.handler.SendMsgHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ruoyi.common.utils.SecurityUtils.getUsername;

/**
 * @Author:gaojikun
 * @Date:2022-11-08 11:14
 * @Description:
 */
@Service
public class SceneModelServiceIpml implements SceneModelService {
    @Resource
    private SceneModelMapper sceneModelMapper;

    @Resource
    private ModuleServiceImpl moduleService;

    @Resource
    SceneConfigMapper sceneConfigMapper;

    @Resource
    PlanConfigMapper planConfigMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private DeviceTreeCache deviceTreeCache;

    @Resource
    private ModuleAndPointCache moduleAndPointCache;

    private static Pattern pattern = Pattern.compile("-?[0-9]+(\\\\.[0-9]+)?");

    /**
     * @param sceneModelControl
     * @Description: 获取场景模式列表
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult getSceneModelList(SceneModelControl sceneModelControl) {
        List<SceneModelControl> list = sceneModelMapper.getSceneModelList(sceneModelControl);
        if (list.size() == 0) {
            return AjaxResult.success("无数据");
        }
        return AjaxResult.success("查询成功", list);
    }

    /**
     * @param sceneModelControl
     * @Description: 获取场景模式列表
     * @auther: gaojikun
     * @return: List<SceneModelControl>
     */
    @Override
    public List<SceneModelControl> getSceneModelListReturnList(SceneModelControl sceneModelControl) {
        List<SceneModelControl> list = new ArrayList<>();
        list = sceneModelMapper.getSceneModelList(sceneModelControl);
        return list;
    }

    /**
     * @param sceneModelControl
     * @Description: 获取场景模式信息
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult getSceneModel(SceneModelControl sceneModelControl) {
        sceneModelControl = sceneModelMapper.getSceneModel(sceneModelControl);
        return AjaxResult.success("查询成功", sceneModelControl);
    }

    /**
     * @param sceneModelControl
     * @Description: 新增场景模式
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult addSceneModel(SceneModelControl sceneModelControl) {
        if (sceneModelControl.getSceneId() == null || sceneModelControl.getName() == null || StringUtils.isEmpty(sceneModelControl.getName())) {
            return AjaxResult.error("参数错误");
        }
        //名称查重
        SceneModelControl sceneModelControlCheck = sceneModelMapper.getsceneModelCheck(sceneModelControl);
        if (sceneModelControlCheck != null) {
            return AjaxResult.error("模式名称重复");
        }

        //设置场景下模式id，从1开始
        List<SceneModelControl> modelList = sceneModelMapper.selectModelCountBySecne(sceneModelControl);
        if (modelList == null || modelList.size() == 0) {
            sceneModelControl.setModelId((long)1);
        }else{
            sceneModelControl.setModelId((long)(modelList.size()+1));
        }

        sceneModelControl.setCreateBy(getUsername());
        sceneModelControl.setCreateTime(DateUtils.getNowDate());
        boolean isAdd = sceneModelMapper.addSceneModel(sceneModelControl);
        if (!isAdd) {
            return AjaxResult.error("添加失败");
        }
        return AjaxResult.success("添加成功");
    }

    /**
     * @param sceneModelControl
     * @Description: 修改场景模式
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult updateSceneModel(SceneModelControl sceneModelControl) {
        if (sceneModelControl.getId() == null || sceneModelControl.getSceneId() == null || sceneModelControl.getName() == null || StringUtils.isEmpty(sceneModelControl.getName())) {
            return AjaxResult.error("参数错误");
        }
        //名称查重
        SceneModelControl sceneModelControlCheck = sceneModelMapper.getsceneModelCheck(sceneModelControl);
        if (sceneModelControlCheck != null) {
            return AjaxResult.error("模式名称重复");
        }

        sceneModelControl.setUpdateBy(getUsername());
        sceneModelControl.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdate = sceneModelMapper.updateSceneModel(sceneModelControl);
        if (!isUpdate) {
            return AjaxResult.error("修改失败");
        }
        return AjaxResult.success("修改成功");
    }

    /**
     * @param ids
     * @Description: 删除场景模式
     * @auther: gaojikun
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult deleteSceneModel(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        String MSG = "";
        try {
            String f_node_attribution = null;//所属系统，cross1、lamp2、energy3
            String channelID = null;
            Integer sceneId = null;
            Integer modelId = null;
            for (Long id : ids) {
                boolean isSend = false;//是否下发消息

                //查询模式id
                SceneModelControl sceneModelControl = new SceneModelControl();
                sceneModelControl.setId(id);
                SceneModelControl sceneModel1 = sceneModelMapper.getSceneModel(sceneModelControl);
                if(sceneModel1 == null || sceneModel1.getModelId() == null || sceneModel1.getSceneId() == null){
                    MSG = "未查询到模式信息";
                    throw new Exception();
                }
                modelId = sceneModel1.getModelId().intValue();

                //模式是否在计划中
                PlanController planController = new PlanController();
                planController.setModelControlId(id);
                List<PlanController> planLists = planConfigMapper.selectAllPlanControllerList(planController);
                if (planLists.size() > 0) {
                    return AjaxResult.error("模式已在计划中配置,请删除相应计划后再操作");
                }
                //不在计划中 删除模式下的点位及其场景配置
                SceneModelPointControl sceneModelPointControl = new SceneModelPointControl();
                sceneModelPointControl.setSceneModelId(id);
                List<SceneModelPointControl> pointList = sceneModelMapper.getSceneModelPoint(sceneModelPointControl);
                if (pointList.size() > 0) {
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
                    if (sceneId == null) {
                        sceneId = sceneModel1.getSceneId().intValue();
                    }
                    int delPointInfo = sceneModelMapper.delSceneModelPoint(id.intValue());
                    if (delPointInfo == 0) {
                        MSG = "删除模式点位失败";
                        throw new Exception();
                    }

                }
                int delModelInfo = sceneModelMapper.deleteSceneModel(id);
                if (delModelInfo == 0) {
                    MSG = "删除模式失败";
                    throw new Exception();
                }
                if (isSend) {
                    //下发信息
                    if ("1".equals(f_node_attribution)) {
                        Boolean flagDDC = SendMsgHandler.deleteSceneAndModeDDC(channelID, sceneId, modelId);
                        if (!flagDDC) {
                            return AjaxResult.success("删除成功，下发失败");
                        }
                        // 添加订阅消息
                        MsgSubPubHandler.addSubMsg(DDCCmd.SCENE_MODE_PARAM_DELETE, channelID);
                        return AjaxResult.success("删除成功，下发成功");
                    } else {
                        Boolean flagLDC = SendMsgHandler.deleteSceneAndModeLDC(channelID, sceneId, modelId);
                        if (!flagLDC) {
                            return AjaxResult.success("删除成功，下发失败");
                        }
                        // 添加订阅消息
                        MsgSubPubHandler.addSubMsg(LDCCmd.SCENE_MODE_PARAM_DELETE, channelID);
                        return AjaxResult.success("删除成功，下发成功");

                    }
                } else {
                    return AjaxResult.success("删除成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(MSG);
        }
        return AjaxResult.success("删除成功");
    }

    /**
     * @Description: 获取场景模式点位列表
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    @Override
    public AjaxResult getSceneModelPoint(SceneModelPointControl sceneModelPointControl) {
        List<SceneModelPointControl> list = sceneModelMapper.getSceneModelPoint(sceneModelPointControl);
        if (list.size() == 0) {
            return AjaxResult.success("无数据");
        }
        return AjaxResult.success("查询成功", list);
    }

    //获取ip和系统类型
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
     * @Description: 添加场景模式点位
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public AjaxResult addSceneModelPoint(SceneModelPointControl sceneModelPointControl) {
        if (sceneModelPointControl.getLists() == null || StringUtils.isEmpty(sceneModelPointControl.getLists())
                || sceneModelPointControl.getIsAllUpdate() == null || StringUtils.isEmpty(sceneModelPointControl.getIsAllUpdate())) {
            return AjaxResult.error("参数错误");
        }
        String isAllUpdate = sceneModelPointControl.getIsAllUpdate();
        String Msg = "";
        try {
            boolean sendState = false;
            boolean addTrue = false;//是否为新增下发
            String channelID = null;
            String f_node_attribution = null;//所属系统，cross1、lamp2、energy3

            Integer Type = null; //场景类型
            Type = 0;


            //类型转换
            String[] strlist = sceneModelPointControl.getLists().split("-#-");
            List<SceneModelPointControl> objList = new ArrayList<>();//点位数据信息
            for (String str : strlist) {
                SceneModelPointControl addInfo = JSONObject.parseObject(str, SceneModelPointControl.class);
                objList.add(addInfo);
            }
            if (objList.size() == 0) {
                return AjaxResult.error("请配置点位");
            }

            //根据模式id查询场景下所有模式
            List<Map<String, Object>> modelList = new ArrayList<>();
            if ("1".equals(isAllUpdate)) {
                Msg = "批量配置成功。请手动配置点位值,下发信息";
                modelList = sceneModelMapper.selectAllModelByModel(objList.get(0).getSceneModelId());
            } else {
                Map<String, Object> addMap = new HashMap<String, Object>() {{
                    put("id", objList.get(0).getSceneModelId());
                }};
                modelList.add(addMap);
            }

            if (modelList.size() == 0) {
                return AjaxResult.error("请配置模式");
            }
            SceneControl sceneInfo = new SceneControl();
            //循环配置模式点位
            for (Map modelMap : modelList) {
                //先删除所有配置点位
                int delNum = sceneModelMapper.delSceneModelPoint(Integer.parseInt(modelMap.get("id").toString()));
                if (delNum == 0) {
                    addTrue = true;
                }

                //遍历添加
                for (SceneModelPointControl s : objList) {
                    if (!"1".equals(isAllUpdate)) {
                        // 通过Matcher进行字符串匹配
                        Matcher m = pattern.matcher(s.getPointValue());
                        // 如果正则匹配通过 m.matches() 方法返回 true ，反之 false
                        if (!m.matches()) {
                            return AjaxResult.error("请确定点位值是整数");
                        }
                    }

                    //获取ip和所属系统
                    if (f_node_attribution == null || channelID == null) {
                        DataReception datereturn = queryIpAndType(s.getPointId());
                        if ("0".equals(datereturn.getCode())) {
                            return AjaxResult.error(datereturn.getMsg());
                        }
                        Map<String, String> returnMap = (Map<String, String>) datereturn.getData();
                        f_node_attribution = returnMap.get("f_node_attribution");
                        channelID = returnMap.get("channelID");
                    }
                    //修改点值
                    s.setCreateBy(getUsername());
                    s.setCreateTime(DateUtils.getNowDate());
                    s.setSceneModelId(Long.parseLong(modelMap.get("id").toString()));

                    boolean isAdd = sceneModelMapper.addSceneModelPoint(s);
                    if (!isAdd) {
                        throw new Exception();
                    }
                }

                //下发信息
                if (!"1".equals(isAllUpdate)) {
                    //模式信息
                    SceneModelControl modelInfo = new SceneModelControl();
                    modelInfo.setId(Long.parseLong(modelMap.get("id").toString()));
                    modelInfo = sceneModelMapper.getSceneModel(modelInfo);
                    if (modelInfo == null) {
                        return AjaxResult.error("未获取到模式信息");
                    }

                    //场景信息
                    if (sceneInfo == null || sceneInfo.getId() == null) {
                        sceneInfo.setId(modelInfo.getSceneId());
                        sceneInfo = sceneConfigMapper.getsceneConfig(sceneInfo);
                        if (sceneInfo == null) {
                            return AjaxResult.error("未获取到场景信息");
                        }
                    }

                    if ("1".equals(f_node_attribution)) {
                        //楼控
                        //控制场景
                        ControlParamDDC controlParamDDC = new ControlParamDDC(); //控制场景参数定义

                        ControlModeDDC controlModeDDC = new ControlModeDDC(); //控制场景模式定义

                        List<ControlPointDDC> controlPoint = new ArrayList<>(); //控制场景点位值/单位定义

                        //控制场景
                        //场景参数
                        Type = 0; //控制场景要求为0

                        controlParamDDC.setSceneType(Type);

                        controlParamDDC.setId(sceneInfo.getId().intValue());

                        controlParamDDC.setName(sceneInfo.getName());

                        controlParamDDC.setAlias(sceneInfo.getAlias());

                        controlParamDDC.setActive(sceneInfo.getActive());
                        //模式参数
                        controlModeDDC.setId(modelInfo.getModelId().intValue());

                        controlModeDDC.setActive(sceneInfo.getActive());

                        controlModeDDC.setName(modelInfo.getName());

                        for (SceneModelPointControl s : objList) {
                            ControlPointDDC controlPointDDC = new ControlPointDDC(); //模式里点信息 100个数据组
                            //取出点位设备Id
                            Point pointSb = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point,s.getPointId());
                            controlPointDDC.setPointID(pointSb.getEquipmentId());//设备ID
                            controlPointDDC.setRunValue(Integer.parseInt(s.getPointValue()));//数据无
                            controlPointDDC.setActive(sceneInfo.getActive());
                            controlPoint.add(controlPointDDC);
                        }

                        //组装
                        controlModeDDC.setControlPoint(controlPoint);
                        controlParamDDC.setControlMode(controlModeDDC);
                        if (addTrue) {
                            sendState = SendMsgHandler.addSceneDDC(channelID, controlParamDDC);
                        } else {
                            sendState = SendMsgHandler.setSceneDDC(channelID, controlParamDDC);
                        }
                        if (sendState) {
                            Msg = "下发成功!配置成功!";
                        } else {
                            Msg = "下发失败!配置成功!";
                        }
                        // 添加订阅消息
                        if (addTrue) {
                            MsgSubPubHandler.addSubMsg(DDCCmd.SCENE_ADD, channelID);
                        } else {
                            MsgSubPubHandler.addSubMsg(DDCCmd.SCENE_PARAM_SET, channelID);
                        }

                    } else {
                        //照明
                        //控制场景
                        ControlParamLDC controlParamLDC = new ControlParamLDC(); //控制场景参数定义

                        ControlModeLDC controlModeLDC = new ControlModeLDC(); //控制场景模式定义

                        List<ControlPointLDC> controlPointLDCs = new ArrayList<>(); //控制场景点位值/单位定义

                        //控制场景
                        //场景参数
                        Type = 0; //控制场景要求为0

                        controlParamLDC.setSceneType(Type);

                        controlParamLDC.setId(sceneInfo.getId().intValue());

                        controlParamLDC.setName(sceneInfo.getName());

                        controlParamLDC.setAlias(sceneInfo.getAlias());

                        controlParamLDC.setActive(sceneInfo.getActive());
                        //模式参数
                        controlModeLDC.setId(modelInfo.getModelId().intValue());

                        controlModeLDC.setActive(sceneInfo.getActive());

                        controlModeLDC.setName(modelInfo.getName());

                        for (SceneModelPointControl s : objList) {
                            ControlPointLDC controlPointLDC = new ControlPointLDC(); //模式里点信息 100个数据组
                            //取出点位设备Id
                            Point pointSb = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point,s.getPointId());
                            controlPointLDC.setPointID(pointSb.getEquipmentId());//设备ID
                            controlPointLDC.setRunValue(Integer.parseInt(s.getPointValue()));//别名
                            controlPointLDC.setActive(sceneInfo.getActive());
                            controlPointLDCs.add(controlPointLDC);
                        }

                        //组装
                        controlModeLDC.setControlPoint(controlPointLDCs);
                        controlParamLDC.setControlMode(controlModeLDC);
                        if (addTrue) {
                            sendState = SendMsgHandler.addControlSceneLDC(channelID, controlParamLDC);
                        } else {
                            sendState = SendMsgHandler.setControlSceneLDC(channelID, controlParamLDC);
                        }
                        if (sendState) {
                            Msg = "下发成功!配置成功!";
                        } else {
                            Msg = "下发失败!配置成功!";
                        }
                        // 添加订阅消息
                        if (addTrue) {
                            MsgSubPubHandler.addSubMsg(LDCCmd.SCENE_ADD, channelID);
                        } else {
                            MsgSubPubHandler.addSubMsg(LDCCmd.SCENE_PARAM_SET, channelID);
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("配置失败");
        }
        return AjaxResult.success(Msg);
    }

    /**
     * @Description: 场景模式同步
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    @Override
    public AjaxResult modelPointSync(SceneModelControl sceneModelControl) {
        String f_node_attribution = null;//所属系统，cross1、lamp2、energy3
//        String modelId = sceneModelControl.getId().toString();
//        String sceneId = sceneModelControl.getSceneId().toString();
        String channelID = null;

        //控制场景
        ControlParamLDC controlParamLDC = new ControlParamLDC(); //照明:控制场景参数定义
        ControlParamDDC controlParamDDC = new ControlParamDDC(); //楼控:控制场景参数定义

        ControlModeLDC controlModeLDC = new ControlModeLDC(); //照明:控制场景模式定义
        ControlModeDDC collectModeDDC = new ControlModeDDC(); //楼控:控制场景模式定义

        List<ControlPointLDC> controlPoint = new ArrayList<>(); //照明:控制场景点位值/单位定义
        List<ControlPointDDC> collectPoint = new ArrayList<>(); //楼控:控制场景点位值/单位定义

        //模式信息
        SceneModelControl modelInfo = sceneModelMapper.getSceneModel(sceneModelControl);
        if (modelInfo == null) {
            return AjaxResult.error("未获取到模式信息");
        }

        //场景信息
        SceneControl sceneInfo = new SceneControl();
        sceneInfo.setId(modelInfo.getSceneId());
        sceneInfo = sceneConfigMapper.getsceneConfig(sceneInfo);
        if (modelInfo == null) {
            return AjaxResult.error("未获取到场景信息");
        }

        //模式下所有点位
        SceneModelPointControl sceneModelPointControl = new SceneModelPointControl();
        sceneModelPointControl.setSceneModelId(sceneModelControl.getId());
        List<SceneModelPointControl> pointList = sceneModelMapper.getSceneModelPoint(sceneModelPointControl);//点位
        if (pointList.size() == 0) {
            return AjaxResult.error("请先配置点位");
        }

        for (SceneModelPointControl point : pointList) {
            if (point.getPointValue() == null) {
                return AjaxResult.error("请确定模式下点位是否配置值");
            }
        }

        //获取ip和所属系统
        if (f_node_attribution == null || channelID == null) {
            DataReception datereturn = queryIpAndType(pointList.get(0).getPointId());
            if ("0".equals(datereturn.getCode())) {
                return AjaxResult.error(datereturn.getMsg());
            }
            Map<String, String> returnMap = (Map<String, String>) datereturn.getData();
            f_node_attribution = returnMap.get("f_node_attribution");
            channelID = returnMap.get("channelID");
        }

        //控制场景
        Integer Type = 0;
        SceneModelControl updatemodelInfo = new SceneModelControl();
        if ("1".equals(f_node_attribution)) {
            String sceneid = sceneInfo.getId().toString();
            controlParamDDC.setId(Integer.parseInt(sceneid));
            controlParamDDC.setSceneType(Type);
            controlParamDDC.setName(sceneInfo.getName());
            controlParamDDC.setAlias(sceneInfo.getAlias());
            controlParamDDC.setActive(sceneInfo.getActive().intValue());
            collectModeDDC.setId(modelInfo.getModelId().intValue());
            collectModeDDC.setActive(sceneInfo.getActive());
            collectModeDDC.setName(modelInfo.getName());
            for (int i = 0; i < pointList.size(); i++) {
                ControlPointDDC controlPointDDC = new ControlPointDDC(); //模式里点信息 100个数据组
                SceneModelPointControl map = pointList.get(i);
                //取出点位设备Id
                Point pointSb = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point,map.getPointId());
                controlPointDDC.setPointID(pointSb.getEquipmentId());//设备ID
                controlPointDDC.setRunValue(Integer.parseInt(map.getPointValue()));//数据无
                controlPointDDC.setActive(sceneInfo.getActive().intValue());
                collectPoint.add(controlPointDDC);
            }
            //组装
            controlParamDDC.setControlMode(collectModeDDC);
            collectModeDDC.setControlPoint(collectPoint);
            boolean sendState = SendMsgHandler.setSceneDDC(channelID, controlParamDDC);
            if (sendState) {
                //修改同步信息
                updatemodelInfo.setSynchState(1);
                updatemodelInfo.setId(sceneModelControl.getId());
                Boolean flag = sceneModelMapper.updateSceneModel(updatemodelInfo);
                // 添加订阅消息
                MsgSubPubHandler.addSubMsg(DDCCmd.SCENE_PARAM_SET, channelID);
                return AjaxResult.success("同步成功");
            } else {
                //同步成功失败给模式表添加同步状态
                updatemodelInfo.setSynchState(0);
                updatemodelInfo.setId(sceneModelControl.getId());
                Boolean flag = sceneModelMapper.updateSceneModel(updatemodelInfo);
                return AjaxResult.error("同步失败");
            }
        } else {
            String sceneid = sceneInfo.getId().toString();
            controlParamLDC.setId(Integer.parseInt(sceneid));
            controlParamLDC.setSceneType(Type);
            controlParamLDC.setName(sceneInfo.getName());
            controlParamLDC.setAlias(sceneInfo.getAlias());
            controlParamLDC.setActive(sceneInfo.getActive().intValue());
            controlModeLDC.setId(modelInfo.getModelId().intValue());
            controlModeLDC.setActive(sceneInfo.getActive());
            controlModeLDC.setName(modelInfo.getName());
            for (int i = 0; i < pointList.size(); i++) {
                ControlPointLDC controlPointLDC = new ControlPointLDC(); //模式里点信息 100个数据组
                SceneModelPointControl map = pointList.get(i);
                //取出点位设备Id
                Point pointSb = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point,map.getPointId());
                controlPointLDC.setPointID(pointSb.getEquipmentId());//设备ID
                controlPointLDC.setRunValue(Integer.parseInt(map.getPointValue()));//数据无
                controlPointLDC.setActive(sceneInfo.getActive().intValue());
                controlPoint.add(controlPointLDC);
            }
            //组装
            controlParamLDC.setControlMode(controlModeLDC);
            controlModeLDC.setControlPoint(controlPoint);
            boolean sendState = SendMsgHandler.setControlSceneLDC(channelID, controlParamLDC);
            if (sendState) { //同步成功失败给模式表添加同步状态
                updatemodelInfo.setSynchState(1);
                updatemodelInfo.setId(sceneModelControl.getId());
                Boolean flag = sceneModelMapper.updateSceneModel(updatemodelInfo);
                // 添加订阅消息
                MsgSubPubHandler.addSubMsg(LDCCmd.SCENE_PARAM_SET, channelID);
                return AjaxResult.success("同步成功");
            } else {
                updatemodelInfo.setSynchState(0);
                updatemodelInfo.setId(sceneModelControl.getId());
                Boolean flag = sceneModelMapper.updateSceneModel(updatemodelInfo);

                return AjaxResult.error("同步失败");
            }
        }
    }

    /**
     * @Description: 模式数据对比
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    @Override
    public AjaxResult modelPointContrast(SceneModelControl sceneModelControl) {
        String f_node_attribution = null;//所属系统，cross1、lamp2、energy3
        String channelID = null;
        //返回上位机数据
        SceneModelPointControl sceneModelPointControl = new SceneModelPointControl();
        sceneModelPointControl.setSceneModelId(sceneModelControl.getId());
        List<SceneModelPointControl> list = sceneModelMapper.getSceneModelPoint(sceneModelPointControl);
        if (list.size() == 0) {
            return AjaxResult.error("请先配置点位");
        }
        Map<String, Object> returnMap = sceneModelMapper.getSceneModelContrast(sceneModelPointControl);
        String pointId = "";
        String pointValue = "";
        for (SceneModelPointControl s : list) {
            //获取ip和所属系统
            if (f_node_attribution == null || channelID == null) {
                DataReception datereturn = queryIpAndType(s.getPointId());
                if ("0".equals(datereturn.getCode())) {
                    return AjaxResult.error(datereturn.getMsg());
                }
                Map<String, String> pointMap = (Map<String, String>) datereturn.getData();
                f_node_attribution = pointMap.get("f_node_attribution");
                channelID = pointMap.get("channelID");
            }


            Point point = moduleAndPointCache.getPointByDeviceId(s.getPointId());

            if ("".equals(pointId)) {
                pointId = point.getEquipmentId().toString();
            } else {
                pointId = pointId + "," + point.getEquipmentId().toString();
            }
            if ("".equals(pointValue)) {
                pointValue = s.getPointValue();
            } else {
                pointValue = pointValue + "," + s.getPointValue();
            }
        }
        returnMap.put("pointId", pointId);
        returnMap.put("pointValue", pointValue);

        //下发消息
        Integer sceneId = sceneModelControl.getSceneId().intValue();
        Integer modelId = sceneModelControl.getModelId().intValue();

        if ("1".equals(f_node_attribution)) {
            Boolean getSceneUnderInfoDDC = SendMsgHandler.getSceneAndModeDDC(channelID, sceneId, modelId);
            if (!getSceneUnderInfoDDC) {
                return AjaxResult.error("下位机数据获取失败", returnMap);
            }
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.SCENE_MODE_PARAM_GET, channelID);
            return AjaxResult.success("下位机数据获取成功", returnMap);

        } else {
            Boolean getSceneUnderInfoLDC = SendMsgHandler.getSceneAndModeLDC(channelID, sceneId, modelId);
            if (!getSceneUnderInfoLDC) {
                return AjaxResult.error("下位机数据获取失败", returnMap);
            }
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.SCENE_MODE_PARAM_GET, channelID);
            return AjaxResult.success("下位机数据获取成功", returnMap);
        }
    }
}
