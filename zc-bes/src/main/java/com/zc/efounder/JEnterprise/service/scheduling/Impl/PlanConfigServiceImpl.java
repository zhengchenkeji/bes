package com.zc.efounder.JEnterprise.service.scheduling.Impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.Module;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.scheduling.*;
import com.zc.efounder.JEnterprise.service.deviceTree.impl.ModuleServiceImpl;
import com.zc.efounder.JEnterprise.Cache.ControllerCache;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.mapper.scheduling.PlanConfigMapper;
import com.zc.efounder.JEnterprise.mapper.scheduling.SceneModelMapper;
import com.zc.efounder.JEnterprise.service.scheduling.IPlanConfigService;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.constant.LDCCmd;
import com.zc.connect.business.dto.ddc.PlanParamDDC;
import com.zc.connect.business.dto.ldc.PlanParamLDC;
import com.zc.connect.business.handler.SendMsgHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.ruoyi.common.utils.SecurityUtils.getUsername;

/**
 * 计划编排Service业务层处理
 *
 * @author gaojikun
 * @date 2022-11-10
 */
@Service
public class PlanConfigServiceImpl implements IPlanConfigService {
    @Resource
    private PlanConfigMapper planConfigMapper;

    @Resource
    private SceneModelMapper sceneModelMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ModuleServiceImpl moduleService;

    @Resource
    private DeviceTreeCache deviceTreeCache;

    @Resource
    private ControllerCache controllerCache;

    @Resource
    private ModuleAndPointCache moduleAndPointCache;

    /**
     * @Description: 左侧计划树
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    public AjaxResult getAllPlanConfigListInfo(PlanConfig planConfig) {
        List<PlanConfig> planConfigList = planConfigMapper.getAllPlanConfigListInfo(planConfig);
        if (planConfigList == null || planConfigList.size() == 0) {
            return AjaxResult.success("无数据");
        }
        return AjaxResult.success("获取成功", planConfigList);
    }

    /**
     * @Description: 查询计划信息
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    public AjaxResult getPlanConfigInfo(PlanConfig planConfig) {
        PlanConfig planConfigInfo = planConfigMapper.getPlanConfigInfo(planConfig);
        return AjaxResult.success("获取成功", planConfigInfo);
    }

    /**
     * @Description: 新增计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public AjaxResult addPlanConfig(PlanConfig planConfig) {
        if (planConfig.getName() == null || planConfig.getParentId() == null || StringUtils.isEmpty(planConfig.getName())) {
            return AjaxResult.error("参数错误");
        }
        //查重
        PlanConfig planConfigCheck = planConfigMapper.getPlanConfigCheck(planConfig);
        if (planConfigCheck != null) {
            return AjaxResult.error("计划名称重复");
        }
        //设置id
        List<PlanConfig> planConfigList = planConfigMapper.getAllPlanConfigListInfo(planConfig);
        int i = planConfigList.size() + 2;
        planConfig.setId(Long.parseLong(String.valueOf(i)));
        //添加
        planConfig.setCreateBy(getUsername());
        planConfig.setCreateTime(DateUtils.getNowDate());
        boolean isAdd = planConfigMapper.addPlanConfig(planConfig);
        if (!isAdd) {
            return AjaxResult.error("添加失败");
        }
        return AjaxResult.success("添加成功", planConfig);
    }

    /**
     * @Description: 修改计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    public AjaxResult editPlanConfig(PlanConfig planConfig) {
        if (planConfig.getId() == null || planConfig.getName() == null || planConfig.getParentId() == null || StringUtils.isEmpty(planConfig.getName())) {
            return AjaxResult.error("参数错误");
        }
        //查重
        PlanConfig planConfigCheck = planConfigMapper.getPlanConfigCheck(planConfig);
        if (planConfigCheck != null) {
            return AjaxResult.error("计划名称重复");
        }
        //修改
        planConfig.setUpdateBy(getUsername());
        planConfig.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdate = planConfigMapper.editPlanConfig(planConfig);
        if (!isUpdate) {
            return AjaxResult.error("修改失败");
        }
        return AjaxResult.success("修改成功");
    }

    /**
     * @Description: 删除计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    public AjaxResult delPlanConfig(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        for (Long id : ids) {
            int isDel = planConfigMapper.delPlanConfig(id);
            if (isDel == 0) {
                return AjaxResult.error("删除失败");
            }
        }
        return AjaxResult.success("删除成功");
    }

    /**
     * @Description: 查询控制计划列表
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    public List<PlanController> selectPlanControlList(PlanController planController) {
        if (0 == planController.getPlanId()) {
            //查询所有的控制计划
            planController.setPlanId(null);
        }
        //查询对应的子id集合
        List<PlanController> planControllers = planConfigMapper.selectPlanControllerList(planController);
        for (PlanController p : planControllers) {
            String modelPoints = "未关联点位";
            //根据模块id查询点位
            SceneModelPointControl sceneModelPointControl = new SceneModelPointControl();
            sceneModelPointControl.setSceneModelId(p.getModelControlId());
            List<SceneModelPointControl> pointlists = sceneModelMapper.getSceneModelPoint(sceneModelPointControl);
            if (pointlists.size() == 0) {
                p.setModelPoints(modelPoints);
            } else {
                Long controllerTreeId = null;
                Long moduleTreeId = null;
                //组装点位数据
                for (SceneModelPointControl s : pointlists) {
                    String onePointInfo = "";
                    Point point = moduleAndPointCache.getPointByDeviceId(s.getPointId());
                    if (null == controllerTreeId) {
                        controllerTreeId = queryControllerTreeId(Long.parseLong(String.valueOf(point.getControllerId())));
                    }
                    Controller controller = controllerCache.getControllerByDeviceTreeId(controllerTreeId);
                    moduleTreeId = queryModuleTreeId(point.getModuleId());
                    //虚点判断
                    if (point.getNodeType().equals(DeviceTreeConstants.BES_VPOINT.toString())) {
                        onePointInfo = "点位:" + point.getSysName() + " 所属控制器名称:" + controller.getSysName();
                    } else {
                        Module module = moduleAndPointCache.getModuleByDeviceId(moduleTreeId);
                        onePointInfo = "点位:" + point.getSysName() + " 所属控制器名称:" + controller.getSysName() + " 所属模块名称:" + module.getSysName();
                    }


                    if ("未关联点位".equals(modelPoints)) {
                        modelPoints = onePointInfo;
                    } else {
                        modelPoints = modelPoints + "," + onePointInfo;
                    }
                }
                p.setModelPoints(modelPoints);
            }
        }
        return planControllers;
    }

    /**
     * @Description: 查询模块
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    private Long queryModuleTreeId(Long id) {
        Collection<Object> allTree = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module).values();
        //数据为空时 结束递归
        if (allTree.size() == 0) {
            return 0L;
        }
        for (Object o : allTree) {
            Module module = (Module) o;
            if (module.getId().equals(id)) {
                return module.getDeviceTreeId();
            }
        }
        return 0L;
    }

    /**
     * @Description: 查询控制器
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    private Long queryControllerTreeId(Long id) {
        Collection<Object> allTree = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Controller).values();
        //数据为空时 结束递归
        if (allTree.size() == 0) {
            return 0L;
        }
        for (Object o : allTree) {
            Controller c = (Controller) o;
            if (c.getId() == id.intValue()) {
                return Long.parseLong(String.valueOf(c.getDeviceTreeId()));
            }
        }
        return 0L;
    }

    /**
     * @Description: 查询采集计划列表
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    public List<PlanCollection> selectPlanCollectList(PlanCollection planCollection) {
        if (1 == planCollection.getPlanId()) {
            //查询所有的采集计划
            planCollection.setPlanId(null);
        }
        return planConfigMapper.selectPlanCollectionList(planCollection);
    }

    /**
     * @Description: 查询控制计划信息
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    public AjaxResult selectPlanControllerById(PlanController planController) {
        return AjaxResult.success("查询成功", planConfigMapper.selectPlanControllerById(planController));
    }

    /**
     * @Description: 新增计划日期查重
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    private static Boolean dateCheck(PlanController Pone, PlanController Ptwo) {
        long oneStartTime = Pone.getCompareStartDataTime().getTime();
        long twoStartTime = Ptwo.getCompareStartDataTime().getTime();
        long oneEndTime = Pone.getCompareEndDataTime().getTime();
        long twoEndTime = Ptwo.getCompareEndDataTime().getTime();

        //都为替代日 或 都不为替代日
        if ((Pone.getPlanType() == 0 && Ptwo.getPlanType() == 0) || (Pone.getPlanType() == 1 && Ptwo.getPlanType() == 1)) {
            //5种情况 1.p1,p2相交叉  2.p1,p2相交叉 3.p2包含p1 4.p2,p1相等 5.p1包含p2
            if (oneStartTime < twoStartTime && oneEndTime < twoEndTime && oneEndTime > twoStartTime) {
                return true;
            }
            if (twoStartTime < oneStartTime && twoEndTime < oneEndTime && twoEndTime > oneStartTime) {
                return true;
            }
            if (oneStartTime <= twoStartTime && oneEndTime >= twoEndTime) {
                return true;
            }
            if (oneStartTime >= twoStartTime && oneEndTime <= twoEndTime) {
                return true;
            }
            return false;
        } else if (Pone.getPlanType() == 1 && Ptwo.getPlanType() == 0) {//p1为替代日
            if (oneStartTime < twoStartTime && oneEndTime < twoEndTime && oneEndTime > twoStartTime) {
                return true;
            }
            if (twoStartTime < oneStartTime && twoEndTime < oneEndTime && twoEndTime > oneStartTime) {
                return true;
            }
            if (oneStartTime == twoStartTime && oneEndTime == twoEndTime) {
                return true;
            }
            return false;
        } else {//p2为替代日
            if (oneStartTime < twoStartTime && oneEndTime < twoEndTime && oneEndTime > twoStartTime) {
                return true;
            }
            if (twoStartTime < oneStartTime && twoEndTime < oneEndTime && twoEndTime > oneStartTime) {
                return true;
            }
            if (oneStartTime == twoStartTime && oneEndTime == twoEndTime) {
                return true;
            }
            return false;
        }
    }

//    /**
//     * @Description: 新增计划时间查重
//     * @auther: gaojikun
//     * @param: PlanConfig
//     * @return: AjaxResult
//     */
//    private static Boolean timeCheck(PlanController Pone, PlanController Ptwo) {
//        //都为替代日 或 都不为替代日
//        if (Pone.getPlanType() == 0 && Ptwo.getPlanType() == 0 || Pone.getPlanType() == 1 && Ptwo.getPlanType() == 1) {
//            //5种情况
//            if ((Pone.getStartTime().getTime() < Ptwo.getStartTime().getTime() && Pone.getEndTime().getTime() > Ptwo.getStartTime().getTime())
//                    || (Ptwo.getStartTime().getTime() < Pone.getStartTime().getTime() && Ptwo.getEndTime().getTime() > Pone.getStartTime().getTime())
//                    || (Ptwo.getStartTime().getTime() <= Pone.getStartTime().getTime() && Ptwo.getEndTime().getTime() >= Pone.getEndTime().getTime())
//                    || (Pone.getStartTime().getTime() < Ptwo.getStartTime().getTime() && Pone.getEndTime().getTime() > Ptwo.getEndTime().getTime())
//            ) {
//                //有交集
//                return true;
//            } else {
//                //无交集
//                return false;
//            }
//        } else if (Pone.getPlanType() == 1 && Ptwo.getPlanType() == 0) {//Pone为替代日
//            if (Pone.getStartTime().getTime() > Ptwo.getStartTime().getTime() && Pone.getEndTime().getTime() < Ptwo.getEndTime().getTime()) {
//                //包含替代日
//                return false;
//            } else {
//                //不包含替代日
//                return true;
//            }
//        } else {//Ptwo为替代日
//            if (Pone.getStartTime().getTime() < Ptwo.getStartTime().getTime() && Pone.getEndTime().getTime() > Ptwo.getEndTime().getTime()) {
//                //包含替代日
//                return false;
//            } else {
//                //不包含替代日
//                return true;
//            }
//        }
//    }

    /**
     * @Description: 新增控制计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public AjaxResult insertPlanController(PlanController planController) {
        if (planController.getName() == null || planController.getAlias() == null || planController.getActive() == null
                || StringUtils.isEmpty(planController.getName()) || StringUtils.isEmpty(planController.getAlias())
                || planController.getExecutionWay() == null || planController.getModelControlId() == null
                || planController.getSceneControlId() == null || planController.getStartTime() == null
                || planController.getStartDate() == null || planController.getEndTime() == null
                || planController.getEndDate() == null) {
            return AjaxResult.error("参数错误");
        }
        //查重
        PlanController planControllerCheck = planConfigMapper.getPlanControllerCheck(planController);
        if (planControllerCheck != null) {
            return AjaxResult.error("控制计划名称重复");
        }

        DataReception datereturn = new DataReception();
        try {
            //查询所有的计划去遍历时间是否重复
            List<PlanController> planControllers = planConfigMapper.selectPlanControllerList(null);
            boolean dateTimeboo = false;
            for (PlanController p : planControllers) {
                if (p.getSceneControlId() == null || p.getSceneControlId() == 0 ||
                        p.getModelControlId() == null || p.getModelControlId() == 0
                ) {
                    continue;
                }
                //时间转换
                PlanController dataOne = p;
                PlanController dataTwo = planController;
                dataOne.setCompareStartDataTime(TwoDateChangeOne(dataOne.getStartDate(), dataOne.getStartTime()));
                dataOne.setCompareEndDataTime(TwoDateChangeOne(dataOne.getEndDate(), dataOne.getEndTime()));
                dataTwo.setCompareStartDataTime(TwoDateChangeOne(dataTwo.getStartDate(), dataTwo.getStartTime()));
                dataTwo.setCompareEndDataTime(TwoDateChangeOne(dataTwo.getEndDate(), dataTwo.getEndTime()));
//                long oneStartTime = dataOne.getCompareStartDataTime().getTime();
//                long oneEndTime = dataOne.getCompareStartDataTime().getTime();
//                long twoStartTime = dataTwo.getCompareEndDataTime().getTime();
//                long twoEndTime = dataTwo.getCompareEndDataTime().getTime();
//                System.out.println("dataOne.start:"+oneStartTime);
//                System.out.println("dataOne.end:"+oneEndTime);
//                System.out.println("dataTwo.start:"+twoStartTime);
//                System.out.println("dataTwo.end:"+twoEndTime);
                //场景。模式相同时判断时间
                if (p.getSceneControlId().equals(planController.getSceneControlId()) && p.getModelControlId().equals(p.getModelControlId())) {
                    dateTimeboo = dateCheck(dataOne, dataTwo);
                    if (dateTimeboo) {
                        return AjaxResult.error("日期重复！");
                    }
                } else {
                    continue;
                }
            }

            //查询模式点位
            List<SceneModelPointControl> list = querymodelPointList(planController);
            if (list.size() == 0) {
                return AjaxResult.error("请先配置模式点位！");
            }
            //获取ip和系统类型
            datereturn = queryIpAndType(list.get(0).getPointId());
            if ("0".equals(datereturn.getCode())) {
                return AjaxResult.error(datereturn.getMsg());
            }
            Map<String, String> returnMap = (Map<String, String>) datereturn.getData();
            String f_node_attribution = returnMap.get("f_node_attribution");
            String channelID = returnMap.get("channelID");

            //获取最大Id
            Long maxId = planConfigMapper.selectMaxId();
            if (maxId == null) {
                maxId = 0L;
            }
            maxId = maxId + 1;
            planController.setId(maxId);
            planController.setCreateBy(getUsername());
            planController.setCreateTime(DateUtils.getNowDate());
            boolean isAdd = false;
            //添加计划到数据库
            if (planController.getSceneType().intValue() == 0) {
                isAdd = planConfigMapper.insertPlanController(planController);
            } else {
                isAdd = planConfigMapper.insertPlanController(planController);
            }
            if (!isAdd) {
                return AjaxResult.error("添加失败，添加至数据库！");
            }

            if (planController.getActive() != null && planController.getActive() != 0) {
                //下发计划指令
                datereturn = insertInstructions(channelID, planController, f_node_attribution);
                if ("0".equals(datereturn.getCode())) {
                    throw new Exception();
                }
            }
            if ("".equals(datereturn.getMsg())) {
                datereturn.setMsg("添加成功，未选择使能，未下发指令");
            }
            return AjaxResult.success(datereturn.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(datereturn.getMsg());
        }
    }

    /**
     * 下发计划指令
     *
     * @param channelID
     * @param object
     * @param f_node_attribution
     * @return DataReception
     */
    public DataReception insertInstructions(String channelID, PlanController object, String f_node_attribution) {

        DataReception returnObject = new DataReception();

        PlanParamLDC planParamLDC = new PlanParamLDC();
        PlanParamDDC planParamDDC = new PlanParamDDC();


        planParamLDC.setId(object.getId().intValue());
        planParamDDC.setId(object.getId().intValue());

        Integer f_active = object.getActive().intValue();

        if (f_active == null || f_active == 0) {//如果使能状态为空 那么证明没有选择 选择了 就是1 使能 没选择就是不使能
            planParamLDC.setActive(0);
            planParamDDC.setActive(0);
            returnObject.setCode("0");
            returnObject.setMsg("状态为不使能! 指令不允许下发");
            return returnObject;
        } else {
            planParamLDC.setActive(f_active);//使能
            planParamDDC.setActive(f_active);//使能
        }
        planParamLDC.setName(object.getName());//计划名称2
        planParamDDC.setName(object.getName());//计划名称2

        planParamLDC.setAlias(object.getAlias());//计划别名3
        planParamDDC.setAlias(object.getAlias());//计划别名3

        //查询绑定的模式ID
        SceneModelControl sceneModelControl = new SceneModelControl();
        sceneModelControl.setId(object.getModelControlId());
        sceneModelControl.setSceneId(object.getSceneControlId());
        SceneModelControl sceneModel1 = sceneModelMapper.getSceneModel(sceneModelControl);
        if (sceneModel1.getModelId() == null) {
            returnObject.setCode("0");
            returnObject.setMsg("下发失败，没有查询到模式信息！");
            return returnObject;
        }
        Long modelControlId = sceneModel1.getModelId();

        //查询默认模式
        List<SceneModelControl> sceneModelList = sceneModelMapper.selectModelCountBySecne(sceneModelControl);
        if (sceneModelList == null || sceneModelList.size() == 0) {
            returnObject.setCode("0");
            returnObject.setMsg("下发失败，没有默认模式！");
            return returnObject;
        }

        planParamLDC.setDefaultModeID(sceneModelList.get(0).getModelId().intValue());//默认模式4
        planParamDDC.setDefaultModeID(sceneModelList.get(0).getModelId().intValue());//默认模式4

        planParamLDC.setExecutionWay(object.getExecutionWay().intValue());//执行方式5
        planParamDDC.setExecutionWay(object.getExecutionWay().intValue());//执行方式5

        planParamLDC.setModeID(modelControlId.intValue());//6模式ID
        planParamDDC.setModeID(modelControlId.intValue());//6模式ID

        planParamLDC.setPlanType(object.getPlanType().intValue());//7替代日
        planParamDDC.setPlanType(object.getPlanType().intValue());//7替代日

        planParamLDC.setWeekMask(Integer.parseInt(object.getWeekMask(), 2));//8周掩码
        planParamDDC.setWeekMask(Integer.parseInt(object.getWeekMask(), 2));//8周掩码

        Integer scenetype = object.getSceneType().intValue();

        planParamLDC.setSceneType(scenetype);//9计划类型
        planParamDDC.setSceneType(scenetype);//9计划类型

        planParamLDC.setSceneID(object.getSceneControlId().intValue());//10场景ID
        planParamDDC.setSceneID(object.getSceneControlId().intValue());//10场景ID

        String startDay = updateDateType(object.getStartDate());

        String enDay = updateDateType(object.getEndDate());

        planParamLDC.setStartDateYear(Integer.parseInt(startDay.substring(2, 4)));
        planParamDDC.setStartDateYear(Integer.parseInt(startDay.substring(2, 4)));

        planParamLDC.setStartDateMonth(Integer.parseInt(startDay.substring(5, 7)));
        planParamDDC.setStartDateMonth(Integer.parseInt(startDay.substring(5, 7)));

        planParamLDC.setStartDateDay(Integer.parseInt(startDay.substring(8, 10)));
        planParamDDC.setStartDateDay(Integer.parseInt(startDay.substring(8, 10)));

        planParamLDC.setEndDateYear(Integer.parseInt(enDay.substring(2, 4)));
        planParamDDC.setEndDateYear(Integer.parseInt(enDay.substring(2, 4)));

        planParamLDC.setEndDateMonth(Integer.parseInt(enDay.substring(5, 7)));
        planParamDDC.setEndDateMonth(Integer.parseInt(enDay.substring(5, 7)));

        planParamLDC.setEndDateDay(Integer.parseInt(enDay.substring(8, 10)));
        planParamDDC.setEndDateDay(Integer.parseInt(enDay.substring(8, 10)));

        String startTime = updateDateType(object.getStartTime());

        String endTime = updateDateType(object.getEndDate());

        planParamLDC.setStartTimeHour(Integer.parseInt(startTime.substring(11, 13)));
        planParamDDC.setStartTimeHour(Integer.parseInt(startTime.substring(11, 13)));

        planParamLDC.setStartTimeMinute(Integer.parseInt(startTime.substring(14, 16)));
        planParamDDC.setStartTimeMinute(Integer.parseInt(startTime.substring(14, 16)));

        planParamLDC.setStartTimeSecond(Integer.parseInt(startTime.substring(17, 19)));
        planParamDDC.setStartTimeSecond(Integer.parseInt(startTime.substring(17, 19)));

        planParamLDC.setEndTimeHour(Integer.parseInt(endTime.substring(11, 13)));
        planParamDDC.setEndTimeHour(Integer.parseInt(endTime.substring(11, 13)));

        planParamLDC.setEndTimeMinute(Integer.parseInt(endTime.substring(14, 16)));
        planParamDDC.setEndTimeMinute(Integer.parseInt(endTime.substring(14, 16)));

        planParamLDC.setEndTimeSecond(Integer.parseInt(endTime.substring(17, 19)));
        planParamDDC.setEndTimeSecond(Integer.parseInt(endTime.substring(17, 19)));

        if ("1".equals(f_node_attribution)) {//楼控
            Boolean state = SendMsgHandler.addPlanDDC(channelID, planParamDDC);
            if (!state) {
                returnObject.setCode("1");
                returnObject.setMsg("新增成功,下发失败");

                return returnObject;
            }
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.PLAN_ADD, channelID);
            returnObject.setCode("1");
            returnObject.setMsg("新增成功,下发成功");

        } else if ("2".equals(f_node_attribution)) {//照明
            Boolean state = SendMsgHandler.addPlanLDC(channelID, planParamLDC);
            if (!state) {
                returnObject.setCode("1");
                returnObject.setMsg("新增成功,下发失败");

                return returnObject;
            }
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.PLAN_ADD, channelID);
            returnObject.setCode("1");
            returnObject.setMsg("新增成功,下发成功");

        }
        return returnObject;
    }

    /**
     * @Description: 模式点位列表
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    private List<SceneModelPointControl> querymodelPointList(PlanController planController) {
        SceneModelPointControl sceneModelPointControl = new SceneModelPointControl();
        sceneModelPointControl.setSceneModelId(planController.getModelControlId());
        return sceneModelMapper.getSceneModelPoint(sceneModelPointControl);
    }

    /**
     * @Description: 获取ip和系统类型
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
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
     * @Description: 修改日期类型
     * @auther: gaojikun
     * @param: date
     * @return: String
     */
    private String updateDateType(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义新的日期格式
        String dateString = formatter.format(date);
        return dateString;
    }

    //组合两个日期
    private Date TwoDateChangeOne(Date dateOne, Date dateTwo) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义新的日期格式
        String dateStringOne = formatter.format(dateOne);
        String dateStringTwo = formatter.format(dateTwo);
        String dateString = dateStringOne.substring(0, 11) + dateStringTwo.substring(11);
//        System.out.println("dateStringOne:" + dateStringOne);
//        System.out.println("dateStringTwo:" + dateStringTwo);
//        System.out.println("dateString:" + dateString);
//        System.out.println("================");
        Date date = formatter.parse(dateString);
        return date;
    }

    /**
     * @Description: 修改计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public AjaxResult updatePlanController(PlanController planController) {
        if (planController.getActive() == null || planController.getId() == null) {
            return AjaxResult.error("参数错误");
        }

        DataReception datereturn = new DataReception();
        //是否只是修改使能
        PlanController planControllerquery = planConfigMapper.selectPlanControllerById(planController);
        if (!planControllerquery.getActive().equals(planController.getActive())) {
            planControllerquery.setActive(planController.getActive());
            planController = planControllerquery;
        } else {
            if (planController.getName() == null || planController.getAlias() == null || planController.getActive() == null
                    || StringUtils.isEmpty(planController.getName()) || StringUtils.isEmpty(planController.getAlias())
                    || planController.getExecutionWay() == null || planController.getModelControlId() == null
                    || planController.getSceneControlId() == null || planController.getStartTime() == null
                    || planController.getStartDate() == null || planController.getEndTime() == null
                    || planController.getEndDate() == null || planController.getId() == null) {
                return AjaxResult.error("参数错误");
            }
        }


        //查重
        PlanController planControllerCheck = planConfigMapper.getPlanControllerCheck(planController);
        if (planControllerCheck != null) {
            return AjaxResult.error("控制计划名称重复");
        }


        try {
            //查询所有的计划去遍历时间是否重复
            PlanController selectPlanController = new PlanController();
            selectPlanController.setId(planController.getId());
            List<PlanController> planControllers = planConfigMapper.selectPlanControllerList(selectPlanController);
            boolean dateTimeboo = false;
            for (PlanController p : planControllers) {
                if (p.getSceneControlId() == null || p.getSceneControlId() == 0 ||
                        p.getModelControlId() == null || p.getModelControlId() == 0
                ) {
                    continue;
                }
                //时间转换
                PlanController dataOne = p;
                PlanController dataTwo = planController;
                dataOne.setCompareStartDataTime(TwoDateChangeOne(dataOne.getStartDate(), dataOne.getStartTime()));
                dataOne.setCompareEndDataTime(TwoDateChangeOne(dataOne.getEndDate(), dataOne.getEndTime()));
                dataTwo.setCompareStartDataTime(TwoDateChangeOne(dataTwo.getStartDate(), dataTwo.getStartTime()));
                dataTwo.setCompareEndDataTime(TwoDateChangeOne(dataTwo.getEndDate(), dataTwo.getEndTime()));
                //场景。模式相同时判断时间
                if (p.getSceneControlId().equals(planController.getSceneControlId()) && p.getModelControlId().equals(p.getModelControlId())) {
                    dateTimeboo = dateCheck(dataOne, dataTwo);
                    if (dateTimeboo) {
                        return AjaxResult.error("日期重复或超出，请检查！");
                    }
                } else {
                    continue;
                }
            }
            //查询模式点位
            List<SceneModelPointControl> list = querymodelPointList(planController);
            if (list.size() == 0) {
                return AjaxResult.error("请先配置模式点位！");
            }
            //获取ip和系统类型
            datereturn = queryIpAndType(list.get(0).getPointId());
            if ("0".equals(datereturn.getCode())) {
                throw new Exception();
            }
            Map<String, String> returnMap = (Map<String, String>) datereturn.getData();
            String f_node_attribution = returnMap.get("f_node_attribution");
            String channelID = returnMap.get("channelID");

            planController.setUpdateTime(DateUtils.getNowDate());

            boolean isUpdate = planConfigMapper.updatePlanController(planController);
            if (!isUpdate) {
                return AjaxResult.error("修改失败,修改数据库信息失败！");
            }

            datereturn = updateInstructions(channelID, planController, f_node_attribution, "修改");
            if ("0".equals(datereturn.getCode())) {
                throw new Exception();
            }

            return AjaxResult.success(datereturn.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(datereturn.getMsg());
        }
    }

    /**
     * @Description: 删除计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public AjaxResult deletePlanControllerByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        DataReception datereturn = new DataReception();
        try {
            String f_node_attribution = null;//所属系统，cross1、lamp2、energy3
            Boolean deleteState = false;

            for (Long id : ids) {
                PlanController planController = new PlanController();
                planController.setId(id);
                planController = planConfigMapper.selectPlanControllerById(planController);
//                //删除计划 去回调里边删除
//                int delNum = planConfigMapper.deletePlanConfigById(id);
//                if (delNum == 0) {
//                    return AjaxResult.error("删除失败，删除数据库数据！");
//                }

                String channelID = "";

                if (planController.getModelControlId() != null || planController.getModelControlId() != 0) {
                    List<SceneModelPointControl> list = querymodelPointList(planController);
                    if (list.size() > 0) {
                        //获取ip和系统类型
                        datereturn = queryIpAndType(list.get(0).getPointId());
                        if ("0".equals(datereturn.getCode())) {
                            throw new Exception();
                        }
                        Map<String, String> returnMap = (Map<String, String>) datereturn.getData();
                        f_node_attribution = returnMap.get("f_node_attribution");
                        channelID = returnMap.get("channelID");
//                    //删除计划公式
//                    returnObject = deletePlanInfoInstructions(Integer.valueOf(fId));
//
//                    if ("0".equals(returnObject.getStatus())) {//删除失败
//                        returnObject.setStatus("0");
//                        returnObject.setMsg("计划删除失败!");
//                        return returnObject;
//                    }
//                        //如果使能则放到回调中
//                        if(planController.getUpdateTime() == null && planController.getActive() == 0){
//                        }
                        if ("1".equals(f_node_attribution)) {//楼控
                            deleteState = SendMsgHandler.deletePlanDDC(channelID, id.intValue());
                        } else if ("2".equals(f_node_attribution)) {//照明
                            deleteState = SendMsgHandler.deletePlanLDC(channelID, id.intValue());
                        }
                    }
                }

                if (!deleteState) {
                    return AjaxResult.error("下发失败！");
                } else {
                    if ("1".equals(f_node_attribution)) {//楼控
                        // 添加订阅消息
                        MsgSubPubHandler.addSubMsg(DDCCmd.PLAN_DELETE, channelID);

                    } else if ("2".equals(f_node_attribution)) {//照明
                        // 添加订阅消息
                        MsgSubPubHandler.addSubMsg(LDCCmd.PLAN_DELETE, channelID);
                    }
                    return AjaxResult.success("删除成功！下发成功！");
                }
            }

            return AjaxResult.error("删除失败！");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(datereturn.getMsg());
        }
    }

    /**
     * @Description: 删除计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @Override
    //未使用
    public AjaxResult deletePlanConfigById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误");
        }
        int delNum = planConfigMapper.deletePlanConfigById(id);
        if (delNum == 0) {
            return AjaxResult.error("删除失败");
        } else {
            return AjaxResult.success("删除成功");
        }
    }

    /**
     * @Description: 控制计划-同步
     * @auther: gaojikun
     * @param: planController
     * @return: AjaxResult
     */
    @Override
    public AjaxResult modelPointSync(PlanController planController) {
        planController = planConfigMapper.selectPlanControllerById(planController);
        //查询模式点位
        List<SceneModelPointControl> list = querymodelPointList(planController);
        if (list.size() == 0) {
            return AjaxResult.error("请先配置点位");
        }
        //获取ip和系统类型
        DataReception datereturn = queryIpAndType(list.get(0).getPointId());
        if ("0".equals(datereturn.getCode())) {
            return AjaxResult.error(datereturn.getMsg());
        }
        Map<String, String> returnMap = (Map<String, String>) datereturn.getData();
        String f_node_attribution = returnMap.get("f_node_attribution");
        String channelID = returnMap.get("channelID");
        DataReception dataReception = updateInstructions(channelID, planController, f_node_attribution, "同步");
        if ("0".equals(dataReception.getCode())) {
            return AjaxResult.error(dataReception.getMsg());
        }
        return AjaxResult.success(dataReception.getMsg());
    }

    /**
     * 修改指令下发
     *
     * @param channelID
     * @param map
     * @param f_node_attribution
     * @param type
     * @return DataReception
     */
    public DataReception updateInstructions(String channelID, PlanController map, String f_node_attribution, String type) {

        DataReception dataReception = new DataReception();

        PlanParamLDC planParamLDC = new PlanParamLDC();
        PlanParamDDC planParamDDC = new PlanParamDDC();

        planParamLDC.setId(map.getId().intValue());//id
        planParamDDC.setId(map.getId().intValue());//id

        planParamLDC.setActive(map.getActive().intValue());//是否使能
        planParamDDC.setActive(map.getActive().intValue());//是否使能

        planParamLDC.setName(map.getName());//名字
        planParamDDC.setName(map.getName());//名字

        planParamLDC.setAlias(map.getAlias());//别名
        planParamDDC.setAlias(map.getAlias());//别名

        planParamLDC.setPlanType(map.getPlanType().intValue());//计划类型
        planParamDDC.setPlanType(map.getPlanType().intValue());//计划类型

        //查询绑定的模式ID
        SceneModelControl sceneModelControl = new SceneModelControl();
        sceneModelControl.setId(map.getModelControlId());
        sceneModelControl.setSceneId(map.getSceneControlId());
        SceneModelControl sceneModel1 = sceneModelMapper.getSceneModel(sceneModelControl);
        if (sceneModel1.getModelId() == null) {
            dataReception.setCode("0");
            dataReception.setMsg("下发失败，没有查询到模式信息！");
            return dataReception;
        }
        Long modelControlId = sceneModel1.getModelId();

        //查询默认模式
        List<SceneModelControl> sceneModelList = sceneModelMapper.selectModelCountBySecne(sceneModelControl);
        if (sceneModelList == null || sceneModelList.size() == 0) {
            dataReception.setCode("0");
            dataReception.setMsg("下发失败，没有默认模式！");
            return dataReception;
        }

        planParamLDC.setDefaultModeID(sceneModelList.get(0).getModelId().intValue());//默认模式4
        planParamDDC.setDefaultModeID(sceneModelList.get(0).getModelId().intValue());//默认模式4

        planParamLDC.setModeID(modelControlId.intValue());//6模式ID
        planParamDDC.setModeID(modelControlId.intValue());//6模式ID

        String startDay = updateDateType(map.getStartDate());

        String enDay = updateDateType(map.getEndDate());

        planParamLDC.setStartDateYear(Integer.parseInt(startDay.substring(2, 4)));
        planParamDDC.setStartDateYear(Integer.parseInt(startDay.substring(2, 4)));

        planParamLDC.setStartDateMonth(Integer.parseInt(startDay.substring(5, 7)));
        planParamDDC.setStartDateMonth(Integer.parseInt(startDay.substring(5, 7)));

        planParamLDC.setStartDateDay(Integer.parseInt(startDay.substring(8, 10)));
        planParamDDC.setStartDateDay(Integer.parseInt(startDay.substring(8, 10)));

        planParamLDC.setEndDateYear(Integer.parseInt(enDay.substring(2, 4)));
        planParamDDC.setEndDateYear(Integer.parseInt(enDay.substring(2, 4)));

        planParamLDC.setEndDateMonth(Integer.parseInt(enDay.substring(5, 7)));
        planParamDDC.setEndDateMonth(Integer.parseInt(enDay.substring(5, 7)));

        planParamLDC.setEndDateDay(Integer.parseInt(enDay.substring(8, 10)));
        planParamDDC.setEndDateDay(Integer.parseInt(enDay.substring(8, 10)));

        String startTime = updateDateType(map.getStartTime());

        String endTime = updateDateType(map.getEndTime());

        planParamLDC.setStartTimeHour(Integer.parseInt(startTime.substring(11, 13)));
        planParamDDC.setStartTimeHour(Integer.parseInt(startTime.substring(11, 13)));

        planParamLDC.setStartTimeMinute(Integer.parseInt(startTime.substring(14, 16)));
        planParamDDC.setStartTimeMinute(Integer.parseInt(startTime.substring(14, 16)));

        planParamLDC.setStartTimeSecond(Integer.parseInt(startTime.substring(17, 19)));
        planParamDDC.setStartTimeSecond(Integer.parseInt(startTime.substring(17, 19)));

        planParamLDC.setEndTimeHour(Integer.parseInt(endTime.substring(11, 13)));
        planParamDDC.setEndTimeHour(Integer.parseInt(endTime.substring(11, 13)));

        planParamLDC.setEndTimeMinute(Integer.parseInt(endTime.substring(14, 16)));
        planParamDDC.setEndTimeMinute(Integer.parseInt(endTime.substring(14, 16)));

        planParamLDC.setEndTimeSecond(Integer.parseInt(endTime.substring(17, 19)));
        planParamDDC.setEndTimeSecond(Integer.parseInt(endTime.substring(17, 19)));

        planParamLDC.setExecutionWay(map.getExecutionWay().intValue());//执行方式5
        planParamDDC.setExecutionWay(map.getExecutionWay().intValue());//执行方式5

        planParamLDC.setWeekMask(Integer.parseInt(map.getWeekMask(), 2));//8
        planParamDDC.setWeekMask(Integer.parseInt(map.getWeekMask(), 2));//8

        if (map.getSceneType() != null) {
            planParamLDC.setSceneType(map.getSceneType().intValue());
            planParamDDC.setSceneType(map.getSceneType().intValue());
        } else {
            planParamLDC.setSceneType(0);
            planParamDDC.setSceneType(0);
        }

        planParamLDC.setSceneID(map.getSceneControlId().intValue());//10场景
        planParamDDC.setSceneID(map.getSceneControlId().intValue());//10场景

        if ("1".equals(f_node_attribution)) {//楼控

            Boolean state = SendMsgHandler.setPlanParamDDC(channelID, planParamDDC);
            if (!state) {
                dataReception.setCode("1");
                if ("修改".equals(type)) {
                    dataReception.setMsg("修改成功,下发失败");
                } else if ("同步".equals(type)) {
                    dataReception.setCode("0");
                    dataReception.setMsg("同步失败");
                }
                return dataReception;
            }
            if ("修改".equals(type)) {
                dataReception.setMsg("修改成功,下发成功");
            } else if ("同步".equals(type)) {
                dataReception.setMsg("同步成功");
            }
            dataReception.setCode("1");
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.PLAN_PARAM_SET, channelID);

        } else if ("2".equals(f_node_attribution)) {//照明

            Boolean state = SendMsgHandler.setPlanParamLDC(channelID, planParamLDC);
            if (!state) {

                if ("修改".equals(type)) {
                    dataReception.setCode("1");
                    dataReception.setMsg("修改成功,下发失败");
                } else if ("同步".equals(type)) {
                    dataReception.setCode("0");
                    dataReception.setMsg("同步失败");
                }
                return dataReception;
            }
            if ("修改".equals(type)) {
                dataReception.setMsg("修改成功,下发成功");
            } else if ("同步".equals(type)) {
                dataReception.setMsg("同步成功");
            }
            dataReception.setCode("1");
//            dataReception.setMsg("修改成功,下发成功");
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.PLAN_PARAM_SET, channelID);
        }
        if ("1".equals(dataReception.getCode())) {
            map.setSynchState(1L);
            planConfigMapper.updatePlanController(map);
        }
        return dataReception;
    }


    /**
     * @Description: 控制计划-对比
     * @auther: gaojikun
     * @param: planController
     * @return: AjaxResult
     */
    @Override
    public AjaxResult planPointContrast(PlanController planController) {
        Map<String, Object> planMap = planConfigMapper.planPointContrast(planController);
        List<SceneModelPointControl> list = querymodelPointList(planController);
        if (list.size() == 0) {
            return AjaxResult.error("请先配置点位");
        }

        String f_node_attribution = null;//所属系统，cross1、lamp2、energy3
        Boolean state = false;

        String channelID = "";

        if (planMap.get("sceneId") == null || "".equals(planMap.get("sceneId").toString())) {
            return AjaxResult.error("请先配置场景");
        }

        if (planMap.get("modelId") != null && !"".equals(planMap.get("modelId").toString())) {

            DataReception datereturn = queryIpAndType(list.get(0).getPointId());
            if ("0".equals(datereturn.getCode())) {
                return AjaxResult.error(datereturn.getMsg());
            }
            Map<String, String> returnMap = (Map<String, String>) datereturn.getData();
            f_node_attribution = returnMap.get("f_node_attribution");
            channelID = returnMap.get("channelID");

            if ("1".equals(f_node_attribution)) {//楼控
                if ("".equals(channelID)) {
                    return AjaxResult.error("缓存未取到控制器IP");
                }
                Integer id = planController.getId().intValue();

                state = SendMsgHandler.getPlanParamDDC(channelID, id);

            } else if ("2".equals(f_node_attribution)) {//照明

                Integer id = planController.getId().intValue();

                state = SendMsgHandler.getPlanParamLDC(channelID, id);
            }

            if (!state) {
                return AjaxResult.error("下位机数据获取失败");
            } else {
                if ("1".equals(f_node_attribution)) {//楼控
                    // 添加订阅消息
                    MsgSubPubHandler.addSubMsg(DDCCmd.PLAN_PARAM_GET, channelID);

                } else if ("2".equals(f_node_attribution)) {//照明
                    // 添加订阅消息
                    MsgSubPubHandler.addSubMsg(LDCCmd.PLAN_PARAM_GET, channelID);
                }
                return AjaxResult.success("下位机数据获取成功", planMap);
            }
        } else {
            return AjaxResult.error("请先配置模式");
        }
    }

}
