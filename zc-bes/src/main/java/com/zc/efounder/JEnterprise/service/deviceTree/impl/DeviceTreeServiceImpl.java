package com.zc.efounder.JEnterprise.service.deviceTree.impl;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.zc.efounder.JEnterprise.Cache.ControllerCache;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.MeterCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.domain.baseData.ProductFunction;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemRealTimeData;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.domain.deviceTreeNode.AthenaDeviceNode;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.domain.moduleType.ModuleType;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTactics;
import com.zc.efounder.JEnterprise.mapper.deviceSynchronization.AthenaBesTimeTaskSyncSbMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.*;
import com.zc.efounder.JEnterprise.mapper.deviceTreeNode.AthenaDeviceNodeMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBranchMeterLinkMapper;
import com.zc.efounder.JEnterprise.mapper.moduleType.ModuleTypeMapper;
import com.zc.efounder.JEnterprise.service.deviceTree.DeviceTreeService;
import com.zc.efounder.JEnterprise.service.safetyWarning.AlarmTacticsService;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.constant.EDCCmd;
import com.zc.connect.business.constant.LDCCmd;
import com.zc.connect.business.handler.SendMsgHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:26 2022/9/8
 * @Modified By:
 */
@Service
public class DeviceTreeServiceImpl implements DeviceTreeService {

    @Resource
    private DeviceTreeMapper deviceTreeMapper;

    @Resource
    private PointMapper pointMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private ModuleTypeMapper moduleTypeMapper;

    @Resource
    private AthenaDeviceNodeMapper athenaDeviceNodeMapper;

    @Resource
    private AthenaBranchMeterLinkMapper athenaBranchMeterLinkMapper;

    @Resource
    private AthenaElectricMeterMapper athenaElectricMeterMapper;


    @Resource
    private RedisCache redisCache;

    @Resource
    private ModuleAndPointCache moduleAndPointCache;

    //控制器
    @Resource
    private ControllerMapper controllerMapper;

    @Resource
    private DeviceTreeCache deviceTreeCache;

    @Resource
    private ControllerCache controllerCache;

    @Resource
    private MeterCache meterCache;

    @Resource
    private AthenaBesTimeTaskSyncSbMapper taskSyncSbMapper;

    @Resource
    private AlarmTacticsService tacticsService;
    //记录要删除的数据 统一删除redis缓存
    List<String> delcachelist;

    //是否删除控制器
    boolean isdeleteController = false;


    @PostConstruct
    public void init() {
        /**
         * 添加数据到 redis 缓存
         * *
         */
        addDeviceTreeCache();
    }

    /**
     * 添加数据到 redis 缓存
     *
     * @auther:gaojikun
     */
    @Override
    public void addDeviceTreeCache() {
        // 获取全部设备列表数据
        List<DeviceTree> deviceTrees = deviceTreeMapper.selectDeviceTreeByRedis();
        // 清楚 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree);

        if (deviceTrees == null || deviceTrees.isEmpty()) {

            return;
        }

        // 添加 redis 缓存数据
        deviceTrees.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) val.getDeviceTreeId(), val);
        });
    }

    //查询设备树列表
    @Override
    public List<DeviceTree> selectDeviceTreeList(DeviceTree deviceTree) {
        /**
         * @description:设置查询父节点为-1
         * @author: sunshangeng
         **/
//        deviceTree.setDeviceTreeFatherId(-1);
        List<DeviceTree> returnList = new ArrayList<>();

        List<DeviceTree> list = new ArrayList<>();
        //遍历缓存取list
        Map<String, DeviceTree> deviceTreeCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree);
        Collection<DeviceTree> deviceTreeValues = deviceTreeCacheMap.values();
        for (DeviceTree tree : deviceTreeValues) {
            if (tree.getDeviceTreeFatherId() == deviceTree.getDeviceTreeFatherId()) {
                //判断节点是否存在子节点
                List<DeviceTree> childTreeList = deviceTreeCache.getSubordinate(tree.getDeviceTreeId());
                if (childTreeList.size() > 0) {
                    tree.setLeaf(false);
                } else {
                    tree.setLeaf(true);
                }
                list.add(tree);
                continue;
            }
        }

        if (list.size() > 0) {
//            boolean isSort = false;
            for (DeviceTree item : list) {

                if (item.getDeviceNodeId() == DeviceTreeConstants.BES_BUILDINGAUTO ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_SMARTLIGHTING ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_ENERGYCONCOL ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_PARKROOTNODE) {
                    item.setDeviceTreeStatus(1);
                }

                /*********qindehua 判断系统名称是否为空****/
                if(item.getDeviceNodeId()==DeviceTreeConstants.BES_DO
                || item.getDeviceNodeId()==DeviceTreeConstants.BES_DI
                || item.getDeviceNodeId()==DeviceTreeConstants.BES_AI
                || item.getDeviceNodeId()==DeviceTreeConstants.BES_AO
                || item.getDeviceNodeId()==DeviceTreeConstants.BES_UI
                || item.getDeviceNodeId()==DeviceTreeConstants.BES_UX
                || item.getDeviceNodeId()==DeviceTreeConstants.BES_VPOINT
                ){
                    Point point = moduleAndPointCache.getPointByDeviceId((long) item.getDeviceTreeId());
                    if (point != null) {
                        item.setControllerId(point.getControllerId());

                        if (point.getNickName() == null || "".equals(point.getNickName())) {

                            if (item.getDeviceNodeId() == DeviceTreeConstants.BES_AI) {
                                item.setSysName("AI节点");
                            } else if (item.getDeviceNodeId() == DeviceTreeConstants.BES_AO) {
                                item.setSysName("AO节点");
                            } else if (item.getDeviceNodeId() == DeviceTreeConstants.BES_DI) {
                                item.setSysName("DI节点");
                            } else if (item.getDeviceNodeId() == DeviceTreeConstants.BES_DO) {
                                item.setSysName("DO节点");
                            } else if (item.getDeviceNodeId() == DeviceTreeConstants.BES_UI) {
                                item.setSysName("UI节点");
                            } else if (item.getDeviceNodeId() == DeviceTreeConstants.BES_UX) {
                                item.setSysName("UX节点");
                            }
//                    if (!isSort) {//是否需要排序
//                        isSort = true;
//                    }
                            if (item.getDeviceNodeId() != DeviceTreeConstants.BES_UI
                                    && item.getDeviceNodeId() != DeviceTreeConstants.BES_UX
                                    && item.getDeviceNodeId() != DeviceTreeConstants.BES_PARKROOTNODE) {
                                //如果点位信息没有则不会显示按钮
                                item.setDeviceNodeFunName("");
                                item.setDeviceNodeFunType("");
                            }
                        } else {
                            //点位设置初始值和单位
                            if (item.getDeviceNodeId() == DeviceTreeConstants.BES_AI
                                    || item.getDeviceNodeId() == DeviceTreeConstants.BES_AO
                                    || item.getDeviceNodeId() == DeviceTreeConstants.BES_DI
                                    || item.getDeviceNodeId() == DeviceTreeConstants.BES_DO
                                    || item.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT
                            ) {
//                        if (!isSort) {//是否需要排序
//                            isSort = true;
//                        }
                                if (point != null && point.getInitVal() != null) {
                                    item.setInitVal(point.getInitVal());
                                }
                                if (point != null && point.getRunVal() != null) {
                                    item.setRunVal(point.getRunVal());
                                }
                                if (point != null && point.getEngineerUnit() != null) {
                                    item.setEngineerUnit(point.getEngineerUnit());
                                }
                            }
                        }
                    }
                }

                returnList.add(item);
            }

            //查询虚点+总线、线路、模块点时根据DeviceTreeId排序;gaojikun
            //排序
            returnList.sort((o1, o2) -> String.valueOf(o1.getDeviceTreeId()).compareTo(String.valueOf(o2.getDeviceTreeId())));
        }

        return returnList;
    }

    /**
     * @Description: 获取所有的设备树节点
     * @auther: wanghongjie
     * @date: 16:25 2022/10/24
     * @param: [DeviceTree]
     * @return: java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.DeviceTree>
     */
    @Override
    public AjaxResult selectDeviceTreeAllList() {

        List<DeviceTree> deviceTreeList = new ArrayList<>();
        Map<String, DeviceTree> stringdeviceTreeMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);


        stringdeviceTreeMap.forEach((key, value) -> {

            if (value.getSysName() == null || "".equals(value.getSysName())) {
                //如果点位信息没有则加上显示名称
                if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AI) {
                    value.setSysName("AI节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AO) {
                    value.setSysName("AO节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DI) {
                    value.setSysName("DI节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DO) {
                    value.setSysName("DO节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UI) {
                    value.setSysName("UI节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UX) {
                    value.setSysName("UX节点");
                }
            }

            deviceTreeList.add(value);
        });

        if (deviceTreeList.size() > 0) {

            deviceTreeList.sort((o1, o2) -> String.valueOf(o1.getDeviceTreeId()).compareTo(String.valueOf(o2.getDeviceTreeId())));

            return AjaxResult.success("获取成功", deviceTreeList);
        }
        return AjaxResult.error("获取失败");
    }

    //根据节点类型查询相应的按钮
    @Override
    public List<AthenaDeviceNode> getButtonByTreeType(AthenaDeviceNode deviceNode) {
        List<AthenaDeviceNode> list = new ArrayList<>();
        list = deviceTreeMapper.getButtonByTreeType(deviceNode);

        return list;
    }

    /**
     * @Description: 删除树节点操作
     * @auther: wanghongjie
     * @date: 16:53 2022/9/16
     * @param: [DeviceTree]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public AjaxResult deleteTreeNode(DeviceTree deviceTree) {

        delcachelist = new ArrayList<>();
        AjaxResult ajaxResult = new AjaxResult();
        if(Integer.valueOf(deviceTree.getDeviceTreeId())==null || Integer.valueOf(deviceTree.getDeviceNodeId())==null){
            return AjaxResult.error("树ID或节点ID不能为空！");
        }

        Integer deviceTreeId = deviceTree.getDeviceTreeId();
        Integer deviceNodeId = deviceTree.getDeviceNodeId();

        //判断节点类型
        Integer[] pointArr = {DeviceTreeConstants.BES_AI, DeviceTreeConstants.BES_AO, DeviceTreeConstants.BES_DI,
                DeviceTreeConstants.BES_DO, DeviceTreeConstants.BES_UI, DeviceTreeConstants.BES_UX,
                DeviceTreeConstants.BES_VPOINT};

        Set pointSet = new HashSet(Arrays.asList(pointArr));
        Boolean pointBoolean = pointSet.contains(deviceNodeId);
        DataReception dataReception = new DataReception();
        try {


            /**
             * @description:删除园区节点时调用
             * @author: sunshangeng
             **/
            if (DeviceTreeConstants.BES_PARKROOTNODE.equals(deviceNodeId)) {
                dataReception = deleteParkNode(deviceTree);
                //返回209前端二次确认
                if (dataReception.getState() && String.valueOf(HttpStatus.ASSOCIATED_DATA).equals(dataReception.getCode())) {
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, dataReception.getMsg());
                }

            }


            /**
             * @description:删除照明DDC采集器节点时调用
             * @author: sunshangeng
             **/
            if (DeviceTreeConstants.BES_BUILDINGAUTO.equals(deviceNodeId)//楼宇
                    || DeviceTreeConstants.BES_SMARTLIGHTING.equals(deviceNodeId)//照明
            ) {
                dataReception = deleteTypeTreeNode(deviceTree, true);

            }

            if (DeviceTreeConstants.BES_ENERGYCONCOL.equals(deviceNodeId)) {//采集
                dataReception = deleteTypeTreeNode(deviceTree, deviceTree.isDeleteAll());
                //返回209前端二次确认
                if (dataReception.getState() && String.valueOf(HttpStatus.ASSOCIATED_DATA).equals(dataReception.getCode())) {
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, dataReception.getMsg());
                }
            }

            /**
             * 直接删除虚点模块点时
             */
            if (pointBoolean) {//虚点、模块点

                //二次弹窗限制
                BuildNode b = new BuildNode();
                b.setFatherId(deviceTreeId.toString());
                List<DeviceTree> sonTrees = pointMapper.selectBuildNodeListByBusId(b);
                if (!deviceTree.isDeleteAll() && sonTrees.size() > 0) {
                    return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, "当前点位已被关联");
                }

                dataReception = deletePoint(deviceTreeId, deviceNodeId);
            }
            /**
             * 删除控制器时
             */
            if (DeviceTreeConstants.BES_DDCNODE.equals(deviceNodeId) ||//DDC
                    DeviceTreeConstants.BES_ILLUMINE.equals(deviceNodeId) //照明
            ) {

                dataReception = deleteController(deviceTreeId);

            }
            /**
             * 删除能耗控制器时
             */
            if (DeviceTreeConstants.BES_COLLECTORNODE.equals(deviceNodeId)) {//能耗

                //数据库操作
                dataReception = deleteControllerEDC(deviceTree);
                //返回209前端二次确认
                if (dataReception.getState() && String.valueOf(HttpStatus.ASSOCIATED_DATA).equals(dataReception.getCode())) {
                    return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, dataReception.getMsg());
                }
            }
            /**
             * 直接删除总线时
             */
            if (DeviceTreeConstants.BES_BUS.equals(deviceNodeId)) {//总线

                //二次弹窗限制
                BuildNode b = new BuildNode();
                b.setFatherId(deviceTreeId.toString());
                List<DeviceTree> sonTrees = pointMapper.selectBuildNodeListByBusId(b);
                if (!deviceTree.isDeleteAll() && sonTrees.size() > 0) {
                    return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, "当前总线已被关联");
                }
                dataReception = deleteBus(deviceTreeId);

            }
            /**
             * 直接删除线路时
             */
            if (DeviceTreeConstants.BES_LINE.equals(deviceNodeId)) {//线路

                //二次弹窗限制
                BuildNode b = new BuildNode();
                b.setFatherId(deviceTreeId.toString());
                List<DeviceTree> sonTrees = pointMapper.selectBuildNodeListByBusId(b);
                if (!deviceTree.isDeleteAll() && sonTrees.size() > 0) {
                    return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, "当前线路已被关联");
                }
                dataReception = deleteLine(deviceTreeId, true);

            }
            /**
             * 直接删除模块时
             */
            if (DeviceTreeConstants.BES_MODEL.equals(deviceNodeId)) {//模块

                //二次弹窗限制
                BuildNode b = new BuildNode();
                b.setFatherId(deviceTreeId.toString());
                List<DeviceTree> sonTrees = pointMapper.selectBuildNodeListByBusId(b);
                if (!deviceTree.isDeleteAll() && sonTrees.size() > 0) {
                    return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, "当前模块已被关联");
                }
                dataReception = deleteModule(deviceTreeId, true);

            }

            /**
             * 直接删除虚点根节点时
             */
            if (DeviceTreeConstants.BES_VPOINTNOPROPERTY.equals(deviceNodeId)) {//虚点根节点

                //二次弹窗限制
                BuildNode b = new BuildNode();
                b.setFatherId(deviceTreeId.toString());
                List<DeviceTree> sonTrees = pointMapper.selectBuildNodeListByBusId(b);
                if (!deviceTree.isDeleteAll() && sonTrees.size() > 0) {
                    return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, "当前虚点根节点已被关联");
                }
                dataReception = deleteVpoint(deviceTreeId);

            }
            /**
             * 删除能耗总线
             */
            if (DeviceTreeConstants.BES_ENERGYBUS.equals(deviceNodeId)) {//删除能耗总线

                //判断是否需要二次弹窗
                if (!deviceTree.isDeleteAll()) {

                    //数据库操作
                    dataReception = deleteEnergyBus(String.valueOf(deviceTree.getDeviceTreeId()));

                    //返回209前端二次确认
                    if (dataReception.getState() && String.valueOf(HttpStatus.ASSOCIATED_DATA).equals(dataReception.getCode())) {
                        return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, dataReception.getMsg());
                    }

                } else {

                    //二次确认后或不需要二次确认走这里
                    dataReception = deleteEnergyBusAll(String.valueOf(deviceTreeId));

                }
            }
            /**
             * 删除电表
             */
            if (DeviceTreeConstants.BES_AMMETER.equals(deviceNodeId)) {//删除电表

                //判断是否需要二次弹窗
                if (!deviceTree.isDeleteAll()) {

                    //数据库操作
                    dataReception = deleteMeter(String.valueOf(deviceTree.getDeviceTreeId()));

                    //返回209前端二次确认
                    if (dataReception.getState() && String.valueOf(HttpStatus.ASSOCIATED_DATA).equals(dataReception.getCode())) {
                        return AjaxResult.error(HttpStatus.ASSOCIATED_DATA, dataReception.getMsg());
                    }

                } else {
                    //二次确认后或不需要二次确认走这里
                    dataReception = this.deleteMeterAll(String.valueOf(deviceTreeId));

                }
            }
            /***
             * @description:删除干线耦合器时
             * @author: sunshangeng
             **/
            if (DeviceTreeConstants.BES_TRUNK.equals(deviceNodeId)) {

                dataReception = deleteTrunkLine(deviceTreeId, true);
            }

            /***
             * @description:删除支线耦合器时
             * @author: sunshangeng
             **/
            if (DeviceTreeConstants.BES_BRANCH.equals(deviceNodeId)) {
                dataReception = deleteBranchLine(deviceTreeId, true);
            }


        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("删除节点失败", e);
        }

        /**
         * 所有操作删除成功后  最后清除缓存
         * */
        if (dataReception.getState()) {
            for (String delItem : delcachelist) {
                String[] array = delItem.split(",");

                if (array[0] != null && RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionPointData.equals(array[0])) {
                    if (array[3] != null && "point".equals(array[3])) {
                        //删除能耗缓存
                        Map<String, Map<String, Object>> beforeControllerPointData =
                                redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionPointData, array[1]);

                        if (beforeControllerPointData != null) {
                            beforeControllerPointData.remove(array[2]);
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionPointData, array[1], beforeControllerPointData);
                        }
                        continue;
                    } else {
                        redisCache.delCacheMapValue(array[0], array[1]);
                        continue;
                    }

                } else if (array[0] != null && RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData.equals(array[0])) {
                    if ("meter".equals(array[3])) {
                        //删除能耗缓存
                        Map<String, Map<String, Map<String, Object>>> beforeControllerData =
                                redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData, array[1]);
                        if (beforeControllerData != null) {
                            beforeControllerData.remove(array[2]);
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData, array[1], beforeControllerData);
                        }
                        continue;
                    } else {
                        redisCache.delCacheMapValue(array[0], array[1]);
                        continue;
                    }

                }

                redisCache.delCacheMapValue(array[0], Long.parseLong(array[1]));
                //gaojikun 删除订阅缓存
                Set<String> token = redisCache.getCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, Long.parseLong(array[1]));
                if (token != null) {
                    redisCache.delCacheMapValue(RedisKeyConstants.SUB_REAL_TIME_DATA_CACHE, Long.parseLong(array[1]));
                }


            }

        } else {
            return AjaxResult.error(dataReception.getMsg());
        }

        if (dataReception.getData() != null) {
            return AjaxResult.success(dataReception.getMsg(), dataReception.getData());
        }
        return AjaxResult.success(dataReception.getMsg());
    }

    /**
     * @description:删除园区节点时方法
     * @author: sunshangeng
     **/
    public DataReception deleteParkNode(DeviceTree deviceTree) throws Exception {
        delcachelist=new ArrayList<>();

        DataReception dataReception = new DataReception();
        /**删除当前节点*/
        Boolean deleteLineTree = deviceTreeMapper.deleteLineTree(deviceTree.getDeviceTreeId());
        if (!deleteLineTree) {
            throw new Exception();
        }
        /**删除树关系绑定*/
        Boolean isdelbuildnode = pointMapper.deleteBuildNodeByTreeId(deviceTree.getDeviceTreeId());
        if (!isdelbuildnode) {
            throw new Exception();
        }
        //记录需要清空的树缓存
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + deviceTree.getDeviceTreeId());
        //记录需要清空的缓存
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_ControllerType + "," + deviceTree.getDeviceTreeId());

        /**查询所有控制器*/
        List<String> sublist = deviceTreeMapper.getSubNode(deviceTree.getDeviceTreeId() + "");
        /**如果无字节点直接返回*/
        if (sublist.size() == 0) {
            return new DataReception(true, "删除成功");
        }
        for (String treeid : sublist) {
            DeviceTree typeTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(treeid));
            dataReception = deleteTypeTreeNode(typeTree, deviceTree.isDeleteAll());

            if (DeviceTreeConstants.BES_ENERGYCONCOL.equals(typeTree.getDeviceType())) {
                //返回209前端二次确认
                if (dataReception.getState() && String.valueOf(HttpStatus.ASSOCIATED_DATA).equals(dataReception.getCode())) {
                    /**如果需要二次弹窗 直接返回*/
                    return dataReception;
                }
            }
        }
        return dataReception;
    }

    /**
     * @description:删除照明DDC采集器节点时调用
     * @author: sunshangeng
     * @date: 2022/10/18 15:37
     **/
    public DataReception deleteTypeTreeNode(DeviceTree deviceTree, boolean isDeleteAll) throws Exception {
        DataReception dataReception = new DataReception();
        /**删除当前节点*/
        Boolean deleteLineTree = deviceTreeMapper.deleteLineTree(deviceTree.getDeviceTreeId());
        if (!deleteLineTree) {
            throw new Exception();
        }
        /**删除树关系绑定*/
        Boolean isdelbuildnode = pointMapper.deleteBuildNodeByTreeId(deviceTree.getDeviceTreeId());
        if (!isdelbuildnode) {
            throw new Exception();
        }

        //记录需要清空的树缓存
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + deviceTree.getDeviceTreeId());
        //记录需要清空的缓存
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_ControllerType + "," + deviceTree.getDeviceTreeId());
        /**查询所有控制器*/
        List<String> sublist = deviceTreeMapper.getSubNode(deviceTree.getDeviceTreeId() + "");
        /**如果无字节点直接返回*/
        if (sublist.size() == 0) {
            return new DataReception(true, "删除成功");
        }

        for (String treeid : sublist) {
            DeviceTree conTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(treeid));
            if (DeviceTreeConstants.BES_DDCNODE.equals(conTree.getDeviceNodeId())//楼宇
                    || DeviceTreeConstants.BES_ILLUMINE.equals(conTree.getDeviceNodeId())//照明
            ) {
                dataReception = deleteController(Integer.parseInt(treeid));
            } else {//能耗
                //数据库操作
                conTree.setDeleteAll(isDeleteAll);
                dataReception = deleteControllerEDC(conTree);
            }
        }
        return dataReception;
    }

    /**
     * 删除控制器EDC
     *
     * @param deviceTree 设备树
     * @return {@code DataReception }
     * @Author qindehua
     * @Date 2022/10/8
     **/
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteControllerEDC(DeviceTree deviceTree) {
        DataReception dataReception = new DataReception(false, "删除失败！");
        try {
            /**从缓存中    查询出控制器下面所有总线**/
            List<Bus> busList = new ArrayList<>();

            /**从缓存中    查询出控制器下面所有总线**/
            Map<String, Bus> busMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus);
            busMap.values().forEach(item -> {
                if (item.getDeviceTreeFatherId().equals(String.valueOf(deviceTree.getDeviceTreeId()))) {
                    busList.add(item);
                }
            });

            /**控制器下面 总线不为空时**/
            if (busList != null || !busList.isEmpty()) {

                /**需要进行二次弹窗校验**/
                if (!deviceTree.isDeleteAll()) {
                    /**循环删除控制器下面的总线**/
                    for (Bus bus : busList) {
                        dataReception = deleteEnergyBus(String.valueOf(bus.getDeviceTreeId()));
                        if (!dataReception.getState()) {
                            throw new Exception();
                        } else if (dataReception.getState() && String.valueOf(HttpStatus.ASSOCIATED_DATA).equals(dataReception.getCode())) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            //返回209前端二次确认
                            return new DataReception(dataReception.getState(), dataReception.getMsg(), dataReception.getCode());
                        }
                    }
                }
                /**不需要进行二次弹窗校验**/
                else {
                    /**循环删除控制器下面的总线**/
                    for (Bus bus : busList) {
                        dataReception = deleteEnergyBusAll(String.valueOf(bus.getDeviceTreeId()));
                        if (!dataReception.getState()) {
                            throw new Exception();
                        }
                    }
                }
            }

            /**sunshangeng 删除定时任务中的设备*/
            deviceTreeMapper.deleteBySyncTreeIdBoolean(deviceTree.getDeviceTreeId()+"");

            /**
             * 控制器下面  没有总线  直接删除控制器
             * 如果所属总线的电表 都未关联所有支路 且删除总线和电表成功
             * 删除控制器
             **/
            boolean deleteControllerTree = deviceTreeMapper.deleteControllerTree(deviceTree.getDeviceTreeId());
            /***删除设备树表的控制器*/
            Boolean deleteLineTree = deviceTreeMapper.deleteLineTree(deviceTree.getDeviceTreeId());
            if (deleteControllerTree && deleteLineTree) {
                //记录需要清空的树缓存
                delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + deviceTree.getDeviceTreeId());
                //记录需要清空的控制器缓存
                delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Controller + "," + deviceTree.getDeviceTreeId());
            } else {
                throw new Exception();
            }

            /**删除时   进行下发*/
            Controller controller = controllerCache.getControllerByDeviceTreeId((long) deviceTree.getDeviceTreeId());

            String channelId = controller.getIp();
            boolean sendState = SendMsgHandler.deleteControllerEDC(channelId);
            if (!sendState) {
                return new DataReception(true, "删除成功,下发失败!");
            }
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_DELETE, channelId);

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new DataReception(dataReception.getState(), dataReception.getMsg(), dataReception.getCode());
        }

        return new DataReception(true, "删除成功！");
    }


    /**
     * @param deviceTree
     * @Description: 删除控制器节点
     * @auther: wanghongjie
     * @date: 17:06 2022/9/20
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Transactional
    public AjaxResult deleteCollector(DeviceTree deviceTree) {

        Controller controller = controllerCache.getControllerByDeviceTreeId((long) deviceTree.getDeviceTreeId());
        Boolean boo = controllerMapper.deleteControllerById(controller.getId());
        if (boo) {
            return AjaxResult.error();
        } else {
            return AjaxResult.error();
        }

    }

    /**
     * @Description: 获取当前点击节点的详细信息
     * @auther: wanghongjie
     * @date: 11:20 2022/9/16
     * @param: [DeviceTree]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult getTreeNodeManage(DeviceTree deviceTree) {
        Integer deviceTreeId = deviceTree.getDeviceTreeId();
        Integer deviceNodeId = deviceTree.getDeviceNodeId();

        Integer[] pointArr = {DeviceTreeConstants.BES_AI, DeviceTreeConstants.BES_AO, DeviceTreeConstants.BES_DI,
                DeviceTreeConstants.BES_DO, DeviceTreeConstants.BES_UI, DeviceTreeConstants.BES_UX,
                DeviceTreeConstants.BES_VPOINT};
        Set set = new HashSet(Arrays.asList(pointArr));
        Boolean pointBoolean = set.contains(deviceNodeId);


        if (DeviceTreeConstants.BES_DDCNODE.equals(deviceNodeId) ||
                DeviceTreeConstants.BES_COLLECTORNODE.equals(deviceNodeId)
        ) {

            return AjaxResult.success("获取控制器信息成功", controllerCache.getControllerByDeviceTreeId((long) deviceTreeId));

        } else if (DeviceTreeConstants.BES_BUILDINGAUTO.equals(deviceNodeId) ||
                DeviceTreeConstants.BES_SMARTLIGHTING.equals(deviceNodeId) ||
                DeviceTreeConstants.BES_ENERGYCONCOL.equals(deviceNodeId) ||
                DeviceTreeConstants.BES_PARKROOTNODE.equals(deviceNodeId)
        ) {

            return AjaxResult.success("获取控制器区域信息成功",
                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ControllerType, (long) deviceTreeId));

        } else if (DeviceTreeConstants.BES_ENERGYBUS.equals(deviceNodeId)) {

            return AjaxResult.success("获取能耗总线信息成功",
                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus, (long) deviceTreeId));

        } else if (DeviceTreeConstants.BES_AMMETER.equals(deviceNodeId)) {

            return AjaxResult.success("获取电表信息成功",
                    meterCache.getMeterByDeviceId((long) deviceTreeId));

        } else if (DeviceTreeConstants.BES_MODEL.equals(deviceNodeId)) {
            Module module = moduleAndPointCache.getModuleByDeviceId((long) deviceTreeId);
            /**
             * @description:如果是照明节点
             * @author: sunshangeng*/
            if (module.getType() == 1) {
                StringBuffer addressbuffer = new StringBuffer();
                if (module.getBranchCode() == null) {
                    addressbuffer.append("1.1.");
                } else {
                    BuildNode branchlinenode = deviceTreeCache.getNodeByBranchTreeId(module.getBranchCode());
                    addressbuffer.append(branchlinenode.getPortNum());
                }
                module.setLightingAddress(addressbuffer.toString());
                return AjaxResult.success("获取模块信息成功",
                        module);

            }
            return AjaxResult.success("获取模块信息成功",
                    module);

        } else if (DeviceTreeConstants.BES_LINE.equals(deviceNodeId)) {

            return AjaxResult.success("获取线路信息成功",
                    deviceTreeCache.getNodeByLineTreeId((long) deviceTreeId));

        } else if (DeviceTreeConstants.BES_BUS.equals(deviceNodeId)) {

            return AjaxResult.success("获取总线信息成功",
                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Bus, (long) deviceTreeId));

        } else if (pointBoolean) {
            Point pointObject = moduleAndPointCache.getPointByDeviceId((long) deviceTreeId);
            if (pointObject != null) {
                return AjaxResult.success("获取点位信息成功", pointObject);
            } else {
                return AjaxResult.success("获取点位信息成功",
                        deviceTreeCache.getDeviceTreeByDeviceTreeId((long) deviceTreeId));
            }

        } else if (DeviceTreeConstants.BES_VPOINTNOPROPERTY.equals(deviceNodeId)) {

            return AjaxResult.success("获取虚点信息成功",
                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Vpoint, (long) deviceTreeId));
        }
        /**
         * @description:增加照明相关处理
         * @author: sunshangeng
         **/
        else if (DeviceTreeConstants.BES_TRUNK.equals(deviceNodeId)) {

//            lightingSubNodelist.forEach(tree->{
//                if(tree.getDeviceType()==DeviceTreeConstants.BES_TRUNK){//总线
//                }else if(tree.getDeviceType()==DeviceTreeConstants.BES_BRANCH){//支线
//                }else if(tree.getDeviceType()==DeviceTreeConstants.BES_MODEL){//模块
//                }
//                updateController(tree.getDeviceType(), lighting);
//            });
            /**存储干线通讯地址*/
            BuildNode buildNode = deviceTreeCache.getNodeByTrunkTreeId((long) deviceTreeId);
            buildNode.setTrunkNum(Integer.parseInt(buildNode.getPortNum().substring(0, buildNode.getPortNum().length() - 1)));
//            buildNode.setPortNum();
            return AjaxResult.success("获取干线耦合器信息成功", buildNode);

        } else if (DeviceTreeConstants.BES_BRANCH.equals(deviceNodeId)) {
            /**存储支线通讯地址*/
            BuildNode buildNode = deviceTreeCache.getNodeByBranchTreeId((long) deviceTreeId);
            buildNode.setTrunkNum(Integer.parseInt(buildNode.getPortNum().substring(0, buildNode.getPortNum().indexOf("."))));
            buildNode.setBranchNum(Integer.parseInt(buildNode.getPortNum().substring(buildNode.getPortNum().indexOf(".") + 1, buildNode.getPortNum().length() - 1)));
            return AjaxResult.success("获取支线耦合器信息成功", buildNode);
        } else if (DeviceTreeConstants.BES_ILLUMINE.equals(deviceNodeId)) {
            /**
             * @description：判断是否是照明如果是照明处理子节点
             * @author: sunshangeng
             * @date: 2022/9/28 15:54
             **/
            Controller lighting = controllerCache.getControllerByDeviceTreeId((long) deviceTreeId);
            List<DeviceTree> lightingSubNodelist = deviceTreeMapper.getLightingSubNode(lighting.getDeviceTreeId() + "");
            /**判断是否有子节点*/
            if (lightingSubNodelist.size() > 0) {
                for (DeviceTree tree : lightingSubNodelist) {
                    lighting = updateController(tree.getDeviceNodeId(), lighting);
                    break;
                }
                return AjaxResult.success("获取照明控制器成功", lighting);
            } else {
                return AjaxResult.success("获取照明控制器成功", lighting);
            }
        } else {
            return AjaxResult.error("获取信息失败");
        }

    }


    /**
     * @description:处理树结构
     * @author: sunshangeng
     **/
    public Controller updateController(int devicetype, Controller controller) {
        int index1 = 0;
        int index2 = 0;
        if (devicetype == DeviceTreeConstants.BES_TRUNK) {
            /**删除模块和支线*/
            index2 = 1;
        } else if (devicetype == DeviceTreeConstants.BES_BRANCH) {
            /**删除模块和干线*/
            index1 = 0;
            index2 = 0;
        } else {
            /**删除干线和支线*/
            index1 = 1;
            index2 = 1;
        }
        /**处理缓存集合*/
        Controller redisController = controller;
        String[] methods = redisController.getDeviceNodeFunName().split(",");
        methods = ArrayUtils.remove(methods, index1);
        methods = ArrayUtils.remove(methods, index2);
        String NewDeviceNodeFun = StringUtil.join(methods, ",");
        redisController.setDeviceNodeFunName(NewDeviceNodeFun);
        methods = redisController.getDeviceNodeFunType().split(",");
        methods = ArrayUtils.remove(methods, index1);
        methods = ArrayUtils.remove(methods, index2);
        NewDeviceNodeFun = StringUtil.join(methods, ",");
        redisController.setDeviceNodeFunType(NewDeviceNodeFun);
        /**处理数据库数据*/
        return redisController;
    }


    /**
     * @Description: 删除虚点、模块点
     * @auther: gaojikun
     * @param:treeId
     * @param:deviceNodeId
     * @return:DataReception
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deletePoint(int treeId, int deviceNodeId) {

        try {
            Map<String, DeviceTree> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);
            /**
             * @description:递归获取当前控制器id
             * @author: sunshangeng
             **/
            Controller con = null;
            List<Integer> controllerId = new ArrayList<>();
            DeviceTree tree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) treeId);
            for (int i = 0; i < 20; i++) {
                if (tree.getDeviceNodeId() == DeviceTreeConstants.BES_DDCNODE || //DDC
                        tree.getDeviceNodeId() == DeviceTreeConstants.BES_ILLUMINE //照明
                ) {
                    con = controllerCache.getControllerByDeviceTreeId((long) tree.getDeviceTreeId());
                    break;
                }
                tree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) tree.getDeviceTreeFatherId());
            }

            if (con == null) {
                return new DataReception(false, "删除失败，缓存未取到控制器信息");
            }

            String channelId = con.getIp();

            //点位缓存去除点位信息
            Point restPoint = moduleAndPointCache.getPointByDeviceId((long) treeId);

            String delVpointType = restPoint.getVpointType();//虚点类型
            String delNodeType = restPoint.getNodeType();//实点类型
            Long delTreeId = restPoint.getTreeId();//树ID
            Integer delEenergyStatics = restPoint.getEnergyStatics();//能耗统计

            if (DeviceTreeConstants.BES_VPOINT != deviceNodeId) {
                //根据点位索引查询当前点位原始类型是否为UI、UX
                Integer channelIndex = restPoint.getChannelIndex().intValue();

                //查询模块类型
                Long moduleTypeCode = null;
                Map<String, Module> moduleCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
                Collection<Module> moduleValues = moduleCacheMap.values();
                for (Module m : moduleValues) {
                    if (m.getId().equals(restPoint.getModuleId())) {
                        moduleTypeCode = m.getModuleTypeId();
                        break;
                    }
                }

                if (moduleTypeCode == null) {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new DataReception(false, "删除失败，未获取到模块类型");
                    }
                }

                ModuleType moduleType = new ModuleType();
                moduleType.setTypeCode(moduleTypeCode);
                moduleType = moduleTypeMapper.selectModuleTypeByCode(moduleType);
                String pointSet = moduleType.getPointSet();
                String index = pointSet.substring(channelIndex, channelIndex + 1);

                if (DeviceTreeConstants.MODULE_TYPE_UI.equals(index)) {
                    restPoint.setNodeType(DeviceTreeConstants.BES_UI.toString());
                }
                if (DeviceTreeConstants.MODULE_TYPE_UX.equals(index)) {
                    restPoint.setNodeType(DeviceTreeConstants.BES_UX.toString());
                }

            }


            //先删除树
            Boolean isDeleteDeviceTreee = false;
            if (DeviceTreeConstants.BES_VPOINT == deviceNodeId) {//虚点
                isDeleteDeviceTreee = pointMapper.deleteDeviceTreee(treeId);
            } else {
                DeviceTree restDeviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) treeId);
                restDeviceTree.setSysName(null);
                restDeviceTree.setRedisSysName(null);
                restDeviceTree.setDeviceNodeId(Integer.parseInt(restPoint.getNodeType()));
                isDeleteDeviceTreee = pointMapper.updateDeviceTreeeRest(restDeviceTree);
                //查询按钮
                if (restPoint.getNodeType().equals(DeviceTreeConstants.BES_UI.toString())
                        || restPoint.getNodeType().equals(DeviceTreeConstants.BES_UX.toString())) {
                    AthenaDeviceNode athenaDeviceNode = athenaDeviceNodeMapper.selectAthenaDeviceNodeByDeviceNodeCode(restPoint.getNodeType());
                    restDeviceTree.setDeviceNodeFunType(athenaDeviceNode.getDeviceNodeFunType());
                    restDeviceTree.setDeviceNodeFunName(athenaDeviceNode.getDeviceNodeFunName());
                    restPoint.setDeviceNodeFunType(athenaDeviceNode.getDeviceNodeFunType());
                    restPoint.setDeviceNodeFunName(athenaDeviceNode.getDeviceNodeFunName());
                } else {
                    restDeviceTree.setDeviceNodeFunType("");
                    restDeviceTree.setDeviceNodeFunName("");
                    restPoint.setDeviceNodeFunType("");
                    restPoint.setDeviceNodeFunName("");
                }
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) treeId, restDeviceTree);
            }

            //再删除点
            if (isDeleteDeviceTreee) {
                NodeConfigSet nodeConfigSet = new NodeConfigSet();
                nodeConfigSet.setSysName(restPoint.getSysName());
                AthenaBranchMeterLink link = new AthenaBranchMeterLink();
                //根据树ID查询电表
                Collection<Object> meterValues = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter).values();
                for (Object obj : meterValues) {
                    AthenaElectricMeter meter = (AthenaElectricMeter) obj;
                    if (meter.getDeviceTreeId().equals(restPoint.getTreeId())) {
                        link.setMeterId(meter.getMeterId());
                    }
                }

                boolean isDeletePointByTreeId = false;
                if (DeviceTreeConstants.BES_VPOINT == deviceNodeId) {
                    isDeletePointByTreeId = pointMapper.deletePointByTreeId(treeId);
                } else {
                    restPoint.setUpdateTime(DateUtils.getNowDate());
//                    restPoint.setSysName(null);
                    restPoint.setNickName("");
                    restPoint.setDescription("");
                    restPoint.setEngineerUnit(null);
                    restPoint.setMaxVal(null);
                    restPoint.setMinVal(null);
                    restPoint.setAccuracy(null);
                    restPoint.setSinnalType(null);
                    restPoint.setHighLimit(null);
                    restPoint.setLowLimit(null);
                    restPoint.setEnergyCode(null);
                    restPoint.setEnergyStatics(null);
                    restPoint.setEnabled(1);
                    restPoint.setReversed(null);
                    restPoint.setWorkMode((long) 1);
                    restPoint.setAlarmEnable(null);
                    restPoint.setAlarmPriority(0);
                    restPoint.setAlarmType(0);
                    restPoint.setFaultState(null);
                    restPoint.setSyncState(0);
                    restPoint.setInitVal(null);
                    restPoint.setSourced(null);
                    restPoint.setCloseState(null);

                    isDeletePointByTreeId = pointMapper.updatePointRest(restPoint);
                }
                /*******qindehua  删除点位时 删除点位定时巡检关联数据************/
                int Sync=taskSyncSbMapper.deleteByTreeIdBoolean(String.valueOf(treeId));

                if (isDeletePointByTreeId && Sync>=0) {

                    //记录清空缓存
                    if (DeviceTreeConstants.BES_VPOINT == deviceNodeId) {//虚点
                        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + (long) treeId);
                        //记录清空缓存
                        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Point + "," + (long) treeId);
                    } else {
                        //修改缓存信息
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) treeId, restPoint);
                    }


                    //返回模块点树信息
//                    DeviceTree returnDeviceTree = null;
//                    if (DeviceTreeConstants.BES_VPOINT != deviceNodeId) {
//                        returnDeviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) treeId);
//                    }
                    Point subPoint = moduleAndPointCache.getPointByDeviceId((long) treeId);

                    //删除点值配置
                    pointMapper.deletePointSettingByName(nodeConfigSet);
                    //删除缓存
                    List<NodeConfigSet> delList = new ArrayList();
                    Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config).values();
                    for (Object obj : collection) {
                        NodeConfigSet nodeConfig = (NodeConfigSet) obj;
                        if (nodeConfig.getSysName().equals(nodeConfigSet.getSysName())) {
                            delList.add(nodeConfig);
                            continue;
                        }
                    }
                    delList.forEach(val -> {
                        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config + "," + (long) val.getId());
                    });

                    if (link.getMeterId() != null) {
                        //删除支路配置
                        athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkByMeterId(link);
                        //删除缓存
                        List<AthenaBranchMeterLink> delLinkList = new ArrayList();
                        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink).values();
                        for (Object obj : values) {
                            AthenaBranchMeterLink athenaBranchMeterLink = (AthenaBranchMeterLink) obj;
                            if (athenaBranchMeterLink.getMeterId().equals(link.getMeterId())) {
                                delLinkList.add(athenaBranchMeterLink);
                                continue;
                            }
                        }
                        delLinkList.forEach(val -> {
                            delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink + "," + val.getId());
                        });
                    }
                    //VAI AI AO 类型且勾选能耗统计
                    if (delEenergyStatics != null && 1 == delEenergyStatics &&
                            (delNodeType.equals(DeviceTreeConstants.BES_AI.toString())
                                    || delVpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI)))
                    ) {
                        //删除电表信息
                        athenaElectricMeterMapper.deleteAthenaElectricMeterByMeterId(delTreeId.toString());

                        //获取电表ID
                        AthenaElectricMeter meterRedis = meterCache.getMeterByDeviceId(delTreeId);
                        if (!isdeleteController) {
                            //删除能耗缓存
                            delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionPointData + "," + con.getIp() + "," + meterRedis.getMeterId() + ",point");
                        }
                        //删除缓存
                        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Meter + "," + delTreeId);

                        //删除电表支路关联信息
                        athenaElectricMeterMapper.deleteBranchMeterLink(String.valueOf(meterRedis.getMeterId()));
                        //删除缓存
                        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink).values();
                        for (Object obj : values) {
                            AthenaBranchMeterLink athenaBranchMeterLink = (AthenaBranchMeterLink) obj;
                            if (athenaBranchMeterLink.getMeterId().equals(meterRedis.getMeterId())) {
                                delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink + "," + athenaBranchMeterLink.getId());
                            }
                        }
                    }


                    /**楼控**/
                    if (con.getType() == DeviceTreeConstants.BES_DDCNODE) {
                        //下发消息
                        boolean sendState = SendMsgHandler.deletePointDDC(channelId, subPoint.getEquipmentId());//channelId：DDCIP,ID:模块点Id

                        if (!sendState) {
                            if (subPoint != null) {
//                            subPoint.setDeviceNodeFunType(returnDeviceTree.getDeviceNodeFunType());
//                            subPoint.setDeviceNodeFunName(returnDeviceTree.getDeviceNodeFunName());
                                return new DataReception(true, "删除成功，下发失败", subPoint);
                            }
                            return new DataReception(true, "删除成功，下发失败");
                        }

                        // 添加订阅消息
                        MsgSubPubHandler.addSubMsg(DDCCmd.POINT_DELETE, channelId);
                    }
                    /**照明**/
                    else {
                        //下发消息
                        //channelId：控制器IP,
                        boolean sendState = SendMsgHandler.deletePointLDC(channelId, subPoint.getEquipmentId());

                        if (!sendState) {
                            if (subPoint != null) {
                                return new DataReception(true, "删除成功，下发失败", subPoint);
                            }
                            return new DataReception(true, "删除成功，下发失败");
                        }

                        // 添加订阅消息
                        MsgSubPubHandler.addSubMsg(LDCCmd.POINT_DELETE, channelId);
                    }


                    if (subPoint != null) {
                        return new DataReception(true, "删除成功，下发成功", subPoint);
                    }
                    return new DataReception(true, "删除成功，下发成功");
                }
                else {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                        return new DataReception(false, "删除表信息失败");
                    }
                }
            } else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                    return new DataReception(false, "删除设备树点信息失败");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return new DataReception(false, "删除失败");
        }
    }

    /**
     * @Description: 删除被关联的虚点、模块点
     * @auther: gaojikun
     * @param:treeId
     * @param:deviceNodeId
     * @return:DataReception
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deletePointAll(int treeId, int deviceNodeId) {

        try {
            Map<String, DeviceTree> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);
            /**
             * @description:递归获取当前控制器id
             * @author: sunshangeng
             **/
            Controller con = null;
            List<Integer> controllerId = new ArrayList<>();
            DeviceTree tree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) treeId);
            for (int i = 0; i < 20; i++) {
                if (tree.getDeviceNodeId() == DeviceTreeConstants.BES_DDCNODE || //DDC
                        tree.getDeviceNodeId() == DeviceTreeConstants.BES_ILLUMINE //照明
                ) {
                    con = controllerCache.getControllerByDeviceTreeId((long) tree.getDeviceTreeId());
                    break;
                }
                tree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) tree.getDeviceTreeFatherId());
            }

            if (con == null) {
                return new DataReception(false, "删除失败，缓存未取到控制器信息");
            }

            String channelId = con.getIp();
            //先删除树
            Boolean isDeleteDeviceTreee = pointMapper.deleteDeviceTreee(treeId);

            if (isDeleteDeviceTreee) {

                //再删除点
                boolean isDeletePointByTreeId = pointMapper.deletePointByTreeId(treeId);
                if (isDeletePointByTreeId) {

                    //记录清空缓存
                    delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + (long) treeId);
                    //记录清空缓存
                    delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Point + "," + (long) treeId);

                    Point subPoint = moduleAndPointCache.getPointByDeviceId((long) treeId);

                    String delVpointType = subPoint.getVpointType();//虚点类型
                    String delNodeType = subPoint.getNodeType();//实点类型
                    Long delTreeId = subPoint.getTreeId();//树ID
                    Integer delEenergyStatics = subPoint.getEnergyStatics();//能耗统计

                    NodeConfigSet nodeConfigSet = new NodeConfigSet();
                    nodeConfigSet.setSysName(subPoint.getSysName());
                    AthenaBranchMeterLink link = new AthenaBranchMeterLink();
                    //根据树ID查询电表
                    Collection<Object> meterValues = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter).values();
                    for (Object obj : meterValues) {
                        AthenaElectricMeter meter = (AthenaElectricMeter) obj;
                        if (meter.getDeviceTreeId().equals(subPoint.getTreeId())) {
                            link.setMeterId(meter.getMeterId());
                        }
                    }

                    //删除点值配置
                    pointMapper.deletePointSettingByName(nodeConfigSet);
                    //删除缓存
                    List<NodeConfigSet> delList = new ArrayList();
                    Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config).values();
                    for (Object obj : collection) {
                        NodeConfigSet nodeConfig = (NodeConfigSet) obj;
                        if (nodeConfig.getSysName().equals(nodeConfigSet.getSysName())) {
                            delList.add(nodeConfig);
                            continue;
                        }
                    }
                    delList.forEach(val -> {
                        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config + "," + val.getId());
                    });

                    if (link.getMeterId() != null) {
                        //删除支路配置
                        athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkByMeterId(link);
                        //删除缓存
                        List<AthenaBranchMeterLink> delLinkList = new ArrayList();
                        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink).values();
                        for (Object obj : values) {
                            AthenaBranchMeterLink athenaBranchMeterLink = (AthenaBranchMeterLink) obj;
                            if (athenaBranchMeterLink.getMeterId().equals(link.getMeterId())) {
                                delLinkList.add(athenaBranchMeterLink);
                                continue;
                            }
                        }
                        delLinkList.forEach(val -> {
                            delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink + "," + val.getId());
                        });
                    }

                    //VAI AI 类型且勾选能耗统计
                    if (1 == delEenergyStatics &&
                            (delNodeType.equals(DeviceTreeConstants.BES_AI.toString())
                                    || delVpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI)))
                    ) {
                        //获取电表ID
                        AthenaElectricMeter meterRedis = meterCache.getMeterByDeviceId(delTreeId);
                        if (!isdeleteController) {
                            //删除能耗缓存
                            delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionPointData + "," + con.getIp() + "," + meterRedis.getMeterId() + ",point");
                        }
                        //删除电表信息
                        athenaElectricMeterMapper.deleteAthenaElectricMeterByMeterId(delTreeId.toString());
                        //删除缓存
                        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config + "," + delTreeId);

                        //删除电表支路关联信息
                        athenaElectricMeterMapper.deleteBranchMeterLink(String.valueOf(meterRedis.getMeterId()));
                        //删除缓存
                        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink).values();
                        for (Object obj : values) {
                            AthenaBranchMeterLink athenaBranchMeterLink = (AthenaBranchMeterLink) obj;
                            if (athenaBranchMeterLink.getMeterId().equals(meterRedis.getMeterId())) {
                                delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink + "," + athenaBranchMeterLink.getId());
                            }
                        }
                    }

                    if (deviceNodeId == DeviceTreeConstants.BES_VPOINT) {
                        /**楼控**/
                        if (con.getType() == DeviceTreeConstants.BES_DDCNODE) {
                            //下发消息
                            boolean sendState = SendMsgHandler.deletePointDDC(channelId, subPoint.getEquipmentId());//channelId：DDCIP,ID:模块点Id

                            if (!sendState) {
                                return new DataReception(true, "删除成功，下发失败");
                            }

                            // 添加订阅消息
                            MsgSubPubHandler.addSubMsg(DDCCmd.POINT_DELETE, channelId);
                        }
                        /**照明**/
                        else {
                            //下发消息
                            boolean sendState = SendMsgHandler.deletePointLDC(channelId, subPoint.getEquipmentId());

                            if (!sendState) {
                                return new DataReception(true, "删除成功，下发失败");
                            }
                            // 添加订阅消息
                            MsgSubPubHandler.addSubMsg(LDCCmd.POINT_DELETE, channelId);
                        }
                    }

                    return new DataReception(true, "删除成功，下发成功");
                } else {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                        return new DataReception(false, "删除表信息失败");
                    }
                }
            } else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                    return new DataReception(false, "删除设备树点信息失败");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return new DataReception(false, "删除失败");
        }
    }

    /**
     * 递归
     *
     * @Author qindehua
     * @Date 2022/09/27
     **/
    private void recursive(Long treeId, List<Controller> controller) {
        DeviceTree tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, treeId);
        /**数据为空时 结束递归*/
        if (tree == null) {
            return;
        }
        if (tree.getDeviceNodeId() == DeviceTreeConstants.BES_DDCNODE || //DDC
                tree.getDeviceNodeId() == DeviceTreeConstants.BES_ILLUMINE //照明
        ) {
            controller.add(
                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller,
                            (long) tree.getDeviceTreeId()));
            return;
        }
        recursive((long) tree.getDeviceTreeFatherId(), controller);
    }

    /**
     * 删除模块信息
     *
     * @param treeId 树id
     * @param isSend 是否进行下发
     * @return {@code DataReception }
     * @Author qindehua
     * @Date 2022/11/03
     **/
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteModule(int treeId, Boolean isSend) {
        DataReception dataReception = new DataReception();

        /**
         * @description:递归获取当前控制器id
         * @author: sunshangeng
         **/
        DeviceTree tree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) treeId);

        Controller controller = null;
        for (int i = 0; i < 20; i++) {
            if (tree.getDeviceNodeId() == DeviceTreeConstants.BES_DDCNODE || //DDC
                    tree.getDeviceNodeId() == DeviceTreeConstants.BES_ILLUMINE //照明
            ) {
                controller = controllerCache.getControllerByDeviceTreeId((long) tree.getDeviceTreeId());
                break;
            }
            tree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) tree.getDeviceTreeFatherId());
        }
        if (controller == null) {
            return new DataReception(false, "删除失败，缓存未取到控制器信息");
        }

        String channelId = controller.getIp();
        if (channelId == null || channelId.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new DataReception(false, "删除失败，缓存未取到控制器信息");
        }

        //根据模块树Id查询所有模块点树Id
//        List<DeviceTree> treeList = moduleMapper.getModulePoint(treeId);
        List<DeviceTree> deviceTreeList = deviceTreeCache.getCascadeSubordinate(Integer.parseInt(String.valueOf(treeId)));
        List<Point> treeListInfo = new ArrayList<>();
        List<Point> treeListNoInfo = new ArrayList<>();
        for (DeviceTree deviceTree : deviceTreeList) {
            Point point = moduleAndPointCache.getPointByDeviceId((long) deviceTree.getDeviceTreeId());
            if (point == null) {
                continue;
            }
            if (null != point.getNickName() && !"".equals(point.getNickName())) {
                treeListInfo.add(point);
            } else {
                treeListNoInfo.add(point);
            }

        }

        //模块下有模块点
        //有点位信息点位
        if (treeListInfo.size() > 0) {
            for (Point pointDel : treeListInfo) {
                dataReception = deletePointAll(pointDel.getTreeId().intValue(), Integer.parseInt(pointDel.getNodeType()));
                if (!dataReception.getState()) {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new DataReception(false, "删除模块下模块点信息失败");
                    }
                }
            }
        }

        //无点位信息点位
        if (treeListNoInfo.size() > 0) {
            for (Point pointDel : treeListNoInfo) {
                //先删除树
                Boolean isDeleteDeviceTreee = pointMapper.deleteDeviceTreee(Integer.parseInt(String.valueOf(pointDel.getTreeId())));
                if (isDeleteDeviceTreee) {
                    //再删除点
                    boolean isDeletePointByTreeId = pointMapper.deletePointByTreeId(Integer.parseInt(String.valueOf(pointDel.getTreeId())));
                    if (isDeletePointByTreeId) {
                        //添加缓存至列表
                        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + pointDel.getTreeId());
                        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Point + "," + pointDel.getTreeId());
                    } else {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            e.printStackTrace();
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return new DataReception(false, "删除模块下模块点信息失败");
                        }
                    }
                } else {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new DataReception(false, "删除模块下模块点信息失败");
                    }
                }

            }
        }

        //删除树节点
        Boolean isDeleteModuleTree = moduleMapper.deleteModuleTree(treeId);
        if (!isDeleteModuleTree) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return new DataReception(false, "删除模块树节点信息失败");
            }
        }

        //根据模块树Id删除模块
        Boolean isDeleteModule = moduleMapper.deleteModule(treeId);
        if (!isDeleteModule) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return new DataReception(false, "删除模块信息失败");
            }
        }

        //添加至缓存删除List中
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Module + "," + (long) treeId);
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + (long) treeId);

        Module subModule = moduleAndPointCache.getModuleByDeviceId((long) treeId);

        /***
         * qindehua
         * 2022/10/13
         * */
        if (isSend) {
            if (subModule.getType() == 0) { //楼控
                //下发消息
                boolean sendState = SendMsgHandler.deleteModuleDDC(channelId, Integer.parseInt(String.valueOf(subModule.getId())));//channelId：DDCIP,ID:模块树Id

                if (!sendState) {
                    return new DataReception(true, "删除成功，下发失败");
                }

                // 添加订阅消息
                MsgSubPubHandler.addSubMsg(DDCCmd.MODULE_DELETE, channelId);

            } else {//照明
                //下发消息
                boolean sendState = SendMsgHandler.deleteModuleLDC(channelId, Integer.parseInt(String.valueOf(subModule.getId())));//channelId：LDCIP,ID:模块树Id

                if (!sendState) {
                    return new DataReception(true, "删除成功，下发失败");
                }

                // 添加订阅消息
                MsgSubPubHandler.addSubMsg(LDCCmd.MODULE_DELETE, channelId);
            }
        }
        return new DataReception(true, "删除成功，下发成功");
    }

    /**
     * 删除虚点根节点信息
     *
     * @return DataReception
     * @auther:gaojikun
     * @param:treeId
     */
    @Transactional
    public DataReception deleteVpoint(int treeId) {
        DataReception dataReception = new DataReception();

        //获取控制器Id
        /** qindehu 修改   删除控制器时  因为先删除了控制器 而抛出空指针异常*/
        Map<String, DeviceTree> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);
        //将值存入集合
        //  获取所在控制器
        List<Controller> controller = new ArrayList<>();
        recursive((long) treeId, controller);
        if (controller.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return new DataReception(false, "删除失败，缓存未取到控制器信息");
        }
        String channelId = controller.get(0).getIp();
        if (channelId == null || channelId.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return new DataReception(false, "删除失败，缓存未取到控制器信息");
        }

        //根据虚点根节点设备树Id查询所有虚点设备树Id
        List<DeviceTree> vPointList = deviceTreeCache.getCascadeSubordinate(Integer.parseInt(String.valueOf(treeId)));
        //虚点下有虚点节点
        if (vPointList.size() > 0) {
            //根据虚点设备树Id删除虚点
            for (DeviceTree deviceTreeVpoint : vPointList) {

                if (deviceTreeVpoint.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT) {
                    //删除虚点
                    dataReception = deletePointAll(deviceTreeVpoint.getDeviceTreeId(), deviceTreeVpoint.getDeviceNodeId());
                    if (!dataReception.getState()) {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            e.printStackTrace();
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                            return new DataReception(false, "删除虚点根节点下虚点信息失败");
                        }
                    }
                }
            }
        }

        //删除所有设备树虚点+根节点
        Boolean isDeleteModuleTree = moduleMapper.deleteModuleTree(treeId);
        if (!isDeleteModuleTree) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return new DataReception(false, "删除虚点根节点树节点信息失败");
            }
        }

        //删除虚点表
        Boolean isdelBuildNode = pointMapper.deleteBuildNodeByTreeId(treeId);
        if (!isdelBuildNode) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return new DataReception(false, "删除虚点根节点表信息失败");
            }
        }

        //添加至缓存
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + (long) treeId);
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Vpoint + "," + (long) treeId);

        return new DataReception(true, "删除虚点根节点成功");
    }

    /**
     * 删除线路信息
     *
     * @return DataReception
     * @auther:gaojikun
     * @param:treeId
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteLine(int treeId, boolean isSend) {
        DataReception dataReception = new DataReception();

        //获取控制器Id
        /** qindehu 修改   删除控制器时  因为先删除了控制器 而抛出空指针异常*/
        Map<String, DeviceTree> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);
        //将值存入集合
        //循环两次  获取所在控制器
        List<Controller> controller = new ArrayList<>();
        recursive((long) treeId, controller);
        if (controller.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return new DataReception(false, "删除失败，缓存未取到控制器信息");
        }
        String channelId = controller.get(0).getIp();
        if (channelId == null || channelId.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return new DataReception(false, "删除失败，缓存未取到控制器信息");
        }


        //查询所有模块点设备树Id 并删除
        List<DeviceTree> moduleList = deviceTreeMapper.selectModelueTree(treeId);
        //总线下有模块
        if (moduleList.size() > 0) {
            for (DeviceTree deviceTreeModule : moduleList) {
                dataReception = deleteModule(deviceTreeModule.getDeviceTreeId(), isSend);
                if (!dataReception.getState()) {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                        return new DataReception(false, "删除总线下模块信息失败");
                    }
                }
            }
        }

        //删除线路树节点
        Boolean isdelLine = deviceTreeMapper.deleteLineTree(treeId);
        if (!isdelLine) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return new DataReception(false, "删除总线树节点信息失败");
            }
        }

        //删除线路表
        Boolean isdelBuildNode = pointMapper.deleteBuildNodeByTreeId(treeId);
        if (!isdelBuildNode) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return new DataReception(false, "删除总线表信息失败");
            }
        }


        //添加缓存
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + (long) treeId);
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Line + "," + (long) treeId);

        return new DataReception(true, "删除总线成功");
    }

    ;

    /**
     * 删除总线
     *
     * @return DataReception
     * @auther:gaojikun
     * @param:treeId
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteBus(int treeId) throws Exception {

        DataReception dataReception = new DataReception();

        //获取控制器Id
        /** qindehu 修改   删除控制器时  因为先删除了控制器 而抛出空指针异常*/
        Map<String, DeviceTree> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);
        //将值存入集合
        //循环一次  获取所在控制器
        List<Controller> controller = new ArrayList<>();
        recursive((long) treeId, controller);
        if (controller.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return new DataReception(false, "删除失败，缓存未取到控制器信息");
        }
        String channelId = controller.get(0).getIp();
        if (channelId == null || channelId.isEmpty()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return new DataReception(false, "删除失败，缓存未取到控制器信息");
        }

        //根据虚点根节点设备树Id查询所有虚点设备树Id
        List<DeviceTree> lineList = moduleMapper.getModulePoint(treeId);
        //总线下有线路节点
        if (lineList.size() > 0) {
            //根据线路设备树Id删除线路
            for (DeviceTree deviceTreeLine : lineList) {

                //删除线路
                dataReception = deleteLine(deviceTreeLine.getDeviceTreeId(), false);
                if (!dataReception.getState()) {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                        return new DataReception(false, "删除总线节点下线路信息失败");
                    }
                }
            }
        }

        //删除总线树节点
        Boolean isDeleteModuleTree = moduleMapper.deleteModuleTree(treeId);
        if (!isDeleteModuleTree) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return new DataReception(false, "删除总线树节点信息失败");
            }
        }

        //删除总线表
        Boolean isdelBuildNode = pointMapper.deleteBuildNodeByTreeId(treeId);
        if (!isdelBuildNode) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                return new DataReception(false, "删除线路节点表信息失败");
            }
        }

        //添加至缓存
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + (long) treeId);
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Bus + "," + (long) treeId);

        return new DataReception(true, "删除总线节点成功");
    }


    /**
     * 删除干线
     *
     * @param deviceTreeId 设备树id
     * @param isSend       是否发送
     * @return {@code DataReception }
     * @Author qindehua
     * @Date 2022/11/03
     **/
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteTrunkLine(Integer deviceTreeId, Boolean isSend) throws Exception {
        //删除树节点
        Boolean deleteLineTree = deviceTreeMapper.deleteLineTree(deviceTreeId);
        if (!deleteLineTree) {
            throw new Exception();
        }

        /**删除树关系绑定*/
        boolean isdelbuildnode = pointMapper.deleteBuildNodeByTreeId(deviceTreeId);
        if (!isdelbuildnode) {
            throw new Exception();
        }

        /**记录要删除的缓存*/
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_TrunkLine + "," + deviceTreeId);
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + deviceTreeId);
        /**删除支线*/
        List<DeviceTree> branchList = moduleMapper.getModulePoint(deviceTreeId);
        for (int i = 0; i < branchList.size(); i++) {
            deleteBranchLine(branchList.get(i).getDeviceTreeId(), isSend);
        }
        return new DataReception(true, "删除成功！");
    }

    /**
     * 删除支线
     *
     * @param deviceTreeId 设备树id
     * @param isSend       是否下发
     * @return {@code DataReception }
     * @Author qindehua
     * @Date 2022/11/03
     **/
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteBranchLine(Integer deviceTreeId, Boolean isSend) throws Exception {
        //删除树节点
        Boolean deleteLineTree = deviceTreeMapper.deleteLineTree(deviceTreeId);
        if (!deleteLineTree) {
            throw new Exception();
        }
        /**删除树关系绑定*/

        boolean isdelbuildnode = pointMapper.deleteBuildNodeByTreeId(deviceTreeId);
        if (!isdelbuildnode) {
            throw new Exception();
        }
        /**记录要删除的缓存*/
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_BranchLine + "," + deviceTreeId);
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + deviceTreeId);
        /**删除 模块*/
        List<DeviceTree> moduleList = moduleMapper.getModulePoint(deviceTreeId);
        moduleList.forEach(module -> {
            deleteModule(module.getDeviceTreeId(), isSend);
        });
        return new DataReception(true, "删除成功！");
    }

    /**
     * 删除控制器
     *
     * @param treeId 树id
     * @return {@code DataReception }
     * @Author qindehua
     * @Date 2022/11/03
     **/
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteController(Integer treeId) throws Exception {
        isdeleteController = true;
        //删除树节点
        Boolean deleteLineTree = deviceTreeMapper.deleteLineTree(treeId);
        if (!deleteLineTree) {
            throw new Exception();

        }
        //删除控制器
        boolean deleteControllerTree = deviceTreeMapper.deleteControllerTree(treeId);
        if (deleteControllerTree) {
            //记录需要清空的树缓存
            delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + treeId.longValue());
            //记录需要清空的缓存
            delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Controller + "," + treeId.longValue());


            /**删除定时同步中的设备*/
            deviceTreeMapper.deleteBySyncTreeIdBoolean(treeId+"");

            //总线、虚点删除 gaojikun
            List<DeviceTree> busVpointList = moduleMapper.getModulePoint(treeId);
            if (busVpointList.size() > 0) {
                for (DeviceTree d : busVpointList) {
                    DataReception dataReception = new DataReception();
                    /**总线*/
                    if (d.getDeviceNodeId() == DeviceTreeConstants.BES_BUS) {
                        dataReception = deleteBus(d.getDeviceTreeId());
                        if (!dataReception.getState()) {
                            try {
                                throw new Exception();
                            } catch (Exception e) {
                                e.printStackTrace();
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                isdeleteController = false;
                                return new DataReception(false, "删除控制器下总线信息失败!");
                            }
                        }
                    }
                    /**虚点无属性*/
                    if (d.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINTNOPROPERTY) {
                        dataReception = deleteVpoint(d.getDeviceTreeId());
                        if (!dataReception.getState()) {
                            try {
                                throw new Exception();
                            } catch (Exception e) {
                                e.printStackTrace();
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                isdeleteController = false;
                                return new DataReception(false, "删除控制器下虚点信息失败!");
                            }
                        }
                    }

                    /**干线节点*/
                    if (d.getDeviceNodeId() == DeviceTreeConstants.BES_TRUNK) {
                        dataReception = deleteTrunkLine(d.getDeviceTreeId(), false);
                        if (!dataReception.getState()) {
                            try {
                                throw new Exception();
                            } catch (Exception e) {
                                e.printStackTrace();
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                isdeleteController = false;
                                return new DataReception(false, "删除干线耦合器失败!");
                            }
                        }
                    }

                    /**支线节点*/
                    if (d.getDeviceNodeId() == DeviceTreeConstants.BES_BRANCH) {
                        dataReception = deleteBranchLine(d.getDeviceTreeId(), false);
                        if (!dataReception.getState()) {
                            try {
                                throw new Exception();
                            } catch (Exception e) {
                                e.printStackTrace();
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                isdeleteController = false;
                                return new DataReception(false, "删除支线耦合器失败!");
                            }
                        }
                    }

                    /**模块节点*/
                    if (d.getDeviceNodeId() == DeviceTreeConstants.BES_MODEL) {
                        //是否进行下发
                        dataReception = deleteModule(d.getDeviceTreeId(), false);
                        if (!dataReception.getState()) {
                            try {
                                throw new Exception();
                            } catch (Exception e) {
                                e.printStackTrace();
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                isdeleteController = false;
                                return new DataReception(false, "删除模块失败!");
                            }
                        }
                    }

                }
            }


            Controller controller = controllerCache.getControllerByDeviceTreeId((long) treeId);
            String channelId = controller.getIp();

            //删除能耗缓存
            delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionPointData + "," + channelId + ",," + "controller");
            isdeleteController = false;

            /****楼控*****/
            if (controller.getType() == DeviceTreeConstants.BES_DDCNODE) {
                boolean sendState = SendMsgHandler.deleteControllerDDC(channelId);
                if (!sendState) {
                    return new DataReception(true, "删除成功,下发失败!");
                }
                // 添加订阅消息
                MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_DELETE, channelId);
            }
            /****照明*****/
            else {
                boolean sendState = SendMsgHandler.deleteControllerLDC(channelId);
                if (!sendState) {
                    return new DataReception(true, "删除成功,下发失败!");
                }
                // 添加订阅消息
                MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_DELETE, channelId);
            }
            return new DataReception(true, "删除成功!");
        }
        isdeleteController = false;
        return new DataReception(false, "删除控制器失败!");

    }

    /**
     * @param deviceTreeId 设备树id
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 删除电表
     * @author liuwenge
     * @date 2022/9/24 11:53
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteMeter(String deviceTreeId) {
        if (!StringUtils.hasText(deviceTreeId)) {
            return new DataReception(true, "请选择电表");
        }
        List<String> meterIdList = new ArrayList<>();
        meterIdList.add(deviceTreeId);
        //支路是否关联此电表
        int branchNum = deviceTreeMapper.selectBranchMeterLink(meterIdList);

        //关联了支路返回209   前端二次确认
        if (branchNum > 0) {
            return new DataReception(true, "该电表已关联支路", String.valueOf(HttpStatus.ASSOCIATED_DATA));
        }

        try {
            //从设备树表中删除
            boolean deleteDeviceTree = deviceTreeMapper.deleteDeviceTree(deviceTreeId);
            if (!deleteDeviceTree) {
                throw new Exception();
            }

            //从电表中删除
            boolean deleteMeter = deviceTreeMapper.deleteAthenaElectricMeterByMeterId(deviceTreeId);
            if (!deleteMeter) {
                throw new Exception();
            }

            //查询关联的报警策略
            String[] alarmTacticsIds = deviceTreeMapper.selectAlarmTactics(deviceTreeId);
            if (alarmTacticsIds.length > 0) {

                /**sunshangeng 删除告警策略*/
                Map<String, AlarmTactics> alarmTacticsMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics);
                for (AlarmTactics alarm : alarmTacticsMap.values()) {
                    if(alarm.getDeviceType()==1&&alarm.getDeviceId().equals(deviceTreeId)){
                        Long []ids={alarm.getId()};
                        tacticsService.deleteAlarmTacticsByIds(ids);
                    }
                }
                //删除告警抑制
                deviceTreeMapper.deleteAlarmRestrainControl(alarmTacticsIds);
                //删除告警实时数据
                deviceTreeMapper.deleteAlarmRealtimeData(alarmTacticsIds);
                //删除告警历史数据
                deviceTreeMapper.deleteAlarmHistoricalData(alarmTacticsIds);
                //删除告警接受组关联
                deviceTreeMapper.deleteAlarmNotifierLink(alarmTacticsIds);
                //删除告警通知记录
                deviceTreeMapper.deleteAlarmNotificationRecord(alarmTacticsIds);
            }

            //从缓存中取出电表信息
            AthenaElectricMeter athenaElectricMeter = meterCache.getMeterByDeviceId(Long.parseLong(deviceTreeId));
            //电表id  用于下发
            Integer meterId = athenaElectricMeter.getMeterId().intValue();

            //查询采集器ip  用于下发
            Bus bus = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus, athenaElectricMeter.getDeviceTreeFatherId());
            Controller controller = controllerCache.getControllerByDeviceTreeId(Long.parseLong(bus.getDeviceTreeFatherId()));
            String controllerIP = controller.getIp();

            //把电表缓存加入待删除列表
            delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Meter + "," + deviceTreeId);
            //把设备树缓存加入待删除列表
            delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + deviceTreeId);

            //把电表上次采集时间缓存加入待删除列表
            delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionTime + "," + meterId);
            //把电表原始数据缓存加入待删除列表
            delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData + "," + meterId);
            //把电表实时数据缓存加入待删除列表
            delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData + "," + controllerIP + "," + athenaElectricMeter.getSysName() + ",meter");


            if (!StringUtils.hasText(controllerIP)) {
                return new DataReception(true, "删除成功，下发失败");
            }
            // 删除电表下发
            if (!SendMsgHandler.deleteAmmeterEDC(controllerIP, meterId)) {
                return new DataReception(true, "删除成功，下发失败");
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_DELETE, controllerIP);

            return new DataReception(true, "删除成功，下发成功");

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new DataReception(false, "删除失败");
        }


    }

    /**
     * @param deviceTreeId 设备树id
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 删除有支路关联的电表
     * @author liuwenge
     * @date 2022/9/24 11:53
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteMeterAll(String deviceTreeId) {

        DataReception dataReception = new DataReception();
        if (!StringUtils.hasText(deviceTreeId)) {
            return new DataReception(false, "请选择电表");
        }

        try {
            //从设备树表中删除
            boolean deleteDeviceTree = deviceTreeMapper.deleteDeviceTree(deviceTreeId);
            if (!deleteDeviceTree) {
                throw new Exception();
            }

            //从电表中删除
            boolean deleteMeter = deviceTreeMapper.deleteAthenaElectricMeterByMeterId(deviceTreeId);
            if (!deleteMeter) {
                throw new Exception();
            }

            //从缓存中取出电表信息
            AthenaElectricMeter athenaElectricMeter = meterCache.getMeterByDeviceId(Long.parseLong(deviceTreeId));
            if (athenaElectricMeter == null) {
                return new DataReception(false, "参数错误");
            }
            //从支路电表关联表中删除
            deviceTreeMapper.deleteBranchMeterLink(String.valueOf(athenaElectricMeter.getMeterId()));
            //查询关联的报警策略
            String[] alarmTacticsIds = deviceTreeMapper.selectAlarmTactics(deviceTreeId);
            if (alarmTacticsIds.length > 0) {
                /**sunshangeng 删除告警策略*/
                Map<String, AlarmTactics> alarmTacticsMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics);
                for (AlarmTactics alarm : alarmTacticsMap.values()) {
                    if(alarm.getDeviceType()==1&&alarm.getDeviceId().equals(deviceTreeId)){
                        Long []ids={alarm.getId()};
                        tacticsService.deleteAlarmTacticsByIds(ids);
                    }
                }

                //删除告警抑制
                deviceTreeMapper.deleteAlarmRestrainControl(alarmTacticsIds);
                //删除告警实时数据
                deviceTreeMapper.deleteAlarmRealtimeData(alarmTacticsIds);
                //删除告警历史数据
                deviceTreeMapper.deleteAlarmHistoricalData(alarmTacticsIds);
                //删除告警接受组关联
                deviceTreeMapper.deleteAlarmNotifierLink(alarmTacticsIds);
                //删除告警通知记录
                deviceTreeMapper.deleteAlarmNotificationRecord(alarmTacticsIds);
            }

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new DataReception(false, "删除失败");
        }

        //从缓存中取出电表信息
        AthenaElectricMeter athenaElectricMeter = meterCache.getMeterByDeviceId(Long.parseLong(deviceTreeId));
        //电表id  用于下发
        Integer meterId = athenaElectricMeter.getMeterId().intValue();

        //查询采集器ip  用于下发
        Bus bus = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus, athenaElectricMeter.getDeviceTreeFatherId());
        Controller controller = controllerCache.getControllerByDeviceTreeId(Long.parseLong(bus.getDeviceTreeFatherId()));
        String controllerIP = controller.getIp();

        //电表缓存加入待删除列表
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Meter + "," + deviceTreeId);
        //设备树缓存加入待删除列表
        delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + deviceTreeId);

        //把电表上次采集时间缓存加入待删除列表
        delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionTime + "," + meterId);
        //把电表原始数据缓存加入待删除列表
        delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyCollection_OriginalData + "," + meterId);
        //把电表实时数据缓存加入待删除列表
        delcachelist.add(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData + "," + controllerIP + "," + athenaElectricMeter.getSysName() + ",meter");


        if (!StringUtils.hasText(controllerIP)) {
            return new DataReception(true, "删除成功,下发异常");
        }
        // 删除电表下发
        if (!SendMsgHandler.deleteAmmeterEDC(controllerIP, meterId)) {
            return new DataReception(true, "删除成功,下发异常");
        }

        // 添加订阅消息
        MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_DELETE, controllerIP);

        return new DataReception(true, "删除成功,下发成功");
    }

    /**
     * @param deviceTreeId
     * @return com.ruoyi.common.core.domain.BesReturnObject
     * @Description 删除能耗总线
     * @author liuwenge
     * @date 2022/9/28 10:32
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteEnergyBus(String deviceTreeId) {
        if (!StringUtils.hasText(deviceTreeId)) {
            return new DataReception(false, "请选择总线");
        }

        //查询总线下面所有电表
        List<String> meterIdList = deviceTreeMapper.selectMeterList(deviceTreeId);

        //查询电表关联支路
        if (meterIdList.size() > 0) {
            int branchNum = deviceTreeMapper.selectBranchMeterLink(meterIdList);
            //如果有关联支路返回209,前端二次确认才能删除
            if (branchNum > 0) {
                return new DataReception(true, "总线下的电表已关联支路", String.valueOf(HttpStatus.ASSOCIATED_DATA));
            }
        }

        //没有电表或者电表未关联支路则直接删除
        try {
            //从设备树表中删除总线
            boolean deleteDeviceTree = deviceTreeMapper.deleteDeviceTree(deviceTreeId);
            if (!deleteDeviceTree) {
                throw new Exception();
            } else {
                //加入缓存待删除列表
                delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + deviceTreeId);
            }

            //从总线表中删除
            boolean deleteBus = deviceTreeMapper.deleteBusById(deviceTreeId);
            if (!deleteBus) {
                throw new Exception();
            } else {
                //加入缓存待删除列表
                delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Bus + "," + deviceTreeId);
            }

            //电表删除逻辑
            if (meterIdList.size() > 0) {
                meterIdList.forEach(item -> {
                    this.deleteMeter(item);
                });
            }

            return new DataReception(true, "删除成功");

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new DataReception(false, "删除失败");
        }

    }

    /**
     * @param deviceTreeId
     * @return com.zc.common.core.model.DataReception
     * @Description 二次确认后删除能耗总线
     * @author liuwenge
     * @date 2022/9/29 11:34
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception deleteEnergyBusAll(String deviceTreeId) {
        if (!StringUtils.hasText(deviceTreeId)) {
            return new DataReception(false, "请选择总线");
        }

        //查询总线下面所有电表
        List<String> meterIdList = deviceTreeMapper.selectMeterList(deviceTreeId);

        //没有电表或者电表未关联支路则直接删除
        try {
            //从设备树表中删除总线
            boolean deleteDeviceTree = deviceTreeMapper.deleteDeviceTree(deviceTreeId);
            if (!deleteDeviceTree) {
                throw new Exception();
            } else {
                //加入缓存待删除列表
                delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree + "," + deviceTreeId);
            }

            //电表删除逻辑
            if (meterIdList.size() > 0) {
                for (String meterId : meterIdList) {
                    this.deleteMeterAll(meterId);
                }
            }

            //从总线表中删除
            boolean deleteBus = deviceTreeMapper.deleteBusById(deviceTreeId);
            if (!deleteBus) {
                throw new Exception();
            } else {
                //加入缓存待删除列表
                delcachelist.add(RedisKeyConstants.BES_BasicData_DeviceTree_Bus + "," + deviceTreeId);
            }

            return new DataReception(true, "删除成功");

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new DataReception(false, "删除失败");
        }

    }



    /**
     * @Description: 根据点位id获取点位实时值
     * @auther: wanghongjie
     * @date: 15:25 2022/11/2
     * @param: [id]
     * [id] 点位或者功能id
     * [pointType]  "0" bes设备树  "1"第三方协议
     * [equipmentId] 设备id
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult getRealTimeData(Long id, String pointType, Long equipmentId) {


        RealTimeDataParam realTimeDataParam = new RealTimeDataParam();
        realTimeDataParam.setId(id);
        realTimeDataParam.setPointType(pointType);
        realTimeDataParam.setEquipmentId(equipmentId);

        List<Map<String,Map<String,Object>>> initValList = selectRealTimeData(realTimeDataParam);

        if (initValList == null) {
            return AjaxResult.error("获取失败");
        }
        return AjaxResult.success("获取成功", initValList);

    }



    @Override
    public AjaxResult getAllRealTimeData(List<RealTimeDataParam> realTimeDataParamList) {

        List<Map<String,Map<String,Object>>> initValList = new ArrayList<>();

        for (RealTimeDataParam realTimeDataParam : realTimeDataParamList) {

            List<Map<String,Map<String,Object>>> list = selectRealTimeData(realTimeDataParam);

            if (list == null) {
                continue;
            }

            for (int i = 0; i < list.size(); i++) {
                initValList.add(list.get(i));
            }
        }

        if (initValList.size() == 0) {
            return AjaxResult.error("获取失败");
        }
        return AjaxResult.success("获取成功", initValList);
    }

    public List<Map<String,Map<String,Object>>> selectRealTimeData(RealTimeDataParam realTimeDataParam) {

        List<Map<String,Map<String,Object>>> initValList = new ArrayList<>();

        Long id = realTimeDataParam.getId();

        Long equipmentId = realTimeDataParam.getEquipmentId();

        String pointType = realTimeDataParam.getPointType();

        if (pointType == null) {
            return null;
        }
        if (pointType.equals("0")) {//bes
            Point point = moduleAndPointCache.getPointByDeviceId(id);

            Map<String,Map<String,Object>> map = new HashMap<>();

            Map<String,Object> map1 = new HashMap<>();
            map1.put("id",id);
            map1.put("value",point.getInitVal());
            map.put(id.toString(),map1);

            initValList.add(map);

        } else if (pointType.equals("1")) {//第三方功能

            if(equipmentId == null) {
                return null;
            }
            //根据功能id获取功能信息
            ProductFunction productFunction = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function,id);

            if (productFunction == null) {
                return null;
            }

            String issuedType = productFunction.getIssuedType();//下发方式;0-指令下发;1-数据项下发

            switch (issuedType)
            {
                case "0"://指令下发

                    Integer beginIndex = Integer.valueOf(com.zc.connect.util.StringUtil.hexToDecimal(productFunction.getInstruct().substring(3,7)));;//起始寄存器地址

                    Integer indexSize = Integer.valueOf(com.zc.connect.util.StringUtil.hexToDecimal(productFunction.getInstruct().substring(7,11)));;//寄存器数量

                    Integer endIndex = beginIndex + indexSize - 1;//截止寄存器地址



                    Map<String,ProductItemData> productItemDataMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);
                    if (productItemDataMap == null) {
                        return null;
                    }

                    List<ProductItemData> productItemDataLists = new ArrayList<>();

                    productItemDataLists= productItemDataMap.values().stream()
                            .filter(itemData -> itemData.getProductId()== productFunction.getProductId())
                            .filter(itemData -> itemData.getItemDataId()==null)
                            .sorted(Comparator.comparing(ProductItemData::getSortNum))
                            .collect(Collectors.toList());

                    int i = 0;


                    for (ProductItemData productItemData : productItemDataLists) {

                        Integer sortNum = productItemData.getSortNum().intValue();//寄存器地址

                        if (sortNum < beginIndex || sortNum  > endIndex) {
                            continue;
                        }

                        Boolean breaks = true;
                        while (breaks) {
                            if (sortNum != (beginIndex + i)) {
                                ++i;
                                if ((beginIndex + i) > endIndex) {
                                    breaks = false;
                                }
                            } else {
                                breaks = false;
                            }
                        }


                        if (sortNum == (beginIndex + i)) {

                            //获取数据项是否高低位

                            //获取高低位
                            String highLow = productItemData.getHighLow();
                            if (highLow.equals("1")) {//是高低位
                                //获取高低位数据项
                                for (ProductItemData productItemDatas : productItemDataMap.values()) {
                                    if (productItemDatas.getItemDataId() == null) {
                                        continue;
                                    }

                                    if (productItemDatas.getItemDataId().equals(productItemData.getId())) {

                                        if (productItemDatas.getParamsType().equals("1")) {//高位

                                            handleData(productItemDatas,equipmentId,pointType,id,initValList);
                                        }
                                        if (productItemDatas.getParamsType().equals("2")) {//低位

                                            handleData(productItemDatas,equipmentId,pointType,id,initValList);
                                        }
                                    }
                                }

//                                for (ProductItemData productItemDatas : productItemDataMap.values()) {
//                                    if (productItemDatas.getItemDataId() == null) {
//                                        continue;
//                                    }
//
//                                    if (productItemDatas.getItemDataId().equals(productItemData.getId())) {
//
//                                        if (productItemDatas.getParamsType().equals("2")) {//低位
//
//                                            handleData(productItemDatas,equipmentId,pointType,id,initValList);
//                                        }
//                                    }
//                                }
                            }

                            if (productItemData.getDataType().equals("7")) {//结构体
                                //TODO 实时数据不展示结构体数据
                            }

                            if (!highLow.equals("1") && !productItemData.getDataType().equals("7")) {//不是高低位也不是结构体,说明就是单独寄存器十进制的值

                                handleData(productItemData,equipmentId,pointType,id,initValList);
                            }
                        }
                        i++;
                    }
                    break;
                case "1"://数据项下发
                    ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData,productFunction.getDataItem());
                    if (productItemData == null) {
                        return null;
                    }

                    handleData(productItemData,equipmentId,pointType,id,initValList);

                    break;
            }
        }

        if (initValList.size() == 0 ) {
           return null;
        }
        return initValList;
    }

    private void handleData(ProductItemData productItemDatas, Long equipmentId, String pointType, Long id, List<Map<String, Map<String, Object>>> initValList) {
        Map<String,Map<String,Object>> map = new HashMap<>();



        ProductItemRealTimeData productItemRealTimeData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_RealTime,
                productItemDatas.getId().toString() + equipmentId);

        Map<String,Object> map1 = new HashMap<>();
        map1.put("equipmentId", equipmentId);
        map1.put("id", id);
        map1.put("pointType", pointType);
        map1.put("unit",productItemDatas.getDataUnit());
        map.put(productItemDatas.getId().toString() + "+" + equipmentId.toString(),map1);

        if (productItemRealTimeData == null) {

            map1.put("value", "0");
        } else {
            map1.put("value",productItemRealTimeData.getDataValue());
        }

        initValList.add(map);
    }

}


