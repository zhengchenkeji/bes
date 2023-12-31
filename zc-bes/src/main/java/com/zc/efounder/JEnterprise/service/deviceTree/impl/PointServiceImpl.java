package com.zc.efounder.JEnterprise.service.deviceTree.impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.ModbusFunctions;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.zc.common.core.ThreadPool.ThreadPool;
import com.zc.connect.nettyClient.ClientHandlers.SendSyncMsgHandler;
import com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler.ModbusSendSyncMsgHandler;
import com.zc.connect.nettyServer.ChildChannelHandler.heartbeat.HeartbeatDetector;
import com.zc.connect.nettyServer.NettyServer;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.ProductFunction;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.mapper.deviceTree.AthenaElectricMeterMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ControllerMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.PointMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.DeviceTreeMapper;
import com.zc.efounder.JEnterprise.service.deviceTree.PointService;
import com.zc.efounder.JEnterprise.Cache.ControllerCache;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.MeterCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.mapper.commhandler.JobManagerMapper;
import com.zc.efounder.JEnterprise.mapper.scheduling.SceneModelMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBranchMeterLinkMapper;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.constant.LDCCmd;
import com.zc.connect.business.constant.PointType;
import com.zc.connect.business.dto.ddc.PointDataDDC;
import com.zc.connect.business.dto.ddc.PointParamDDC;
import com.zc.connect.business.dto.ldc.PointDataLDC;
import com.zc.connect.business.dto.ldc.PointParamLDC;
import com.zc.connect.business.handler.SendMsgHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 虚点，总线
 *
 * @author gaojikun
 * @date 2022-09-14
 */
@Service
@Order(2)
public class PointServiceImpl implements PointService {
    @Resource
    private PointMapper pointMapper;

    @Resource
    private AthenaBranchMeterLinkMapper athenaBranchMeterLinkMapper;

    @Resource
    private AthenaElectricMeterMapper athenaElectricMeterMapper;

    @Resource
    private SceneModelMapper sceneModelMapper;

    @Resource
    private JobManagerMapper jobManagerMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ControllerMapper controllerMapper;

    @Resource
    private DeviceTreeMapper devicetreeMappper;
    // 设备树缓存定义
    @Autowired
    private DeviceTreeCache deviceTreeCache;

    @Resource
    private ModuleAndPointCache moduleAndPointCache;

    @Resource
    private ControllerCache controllerCache;

    @Resource
    private MeterCache meterCache;

    @Autowired
    private ModbusSendSyncMsgHandler modbusSendSyncMsgHandler;

    @Resource
    private SendSyncMsgHandler sendSyncMsgHandler;


    @PostConstruct
    public void init() {
        /**
         * 添加数据到 redis 缓存
         */
        addPointCache();
        /**
         * 添加数据到 redis 缓存
         */
        addBuildNodeCache();
        /**
         * 添加数据到 redis 缓存
         */
        addPointConfigCache();

    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addPointCache() {
        // 获取全部点位列表数据
        List<Point> points = pointMapper.selectPointList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_Point);

        if (points == null || points.isEmpty()) {

            return;
        }


        // 添加 redis 缓存数据
        points.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, val.getTreeId(), val);
        });
    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addPointConfigCache() {
        // 获取全部点位列表数据
        List<NodeConfigSet> nodeConfigSets = pointMapper.selectNodeConfigSetList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config);

        if (nodeConfigSets == null || nodeConfigSets.isEmpty()) {

            return;
        }

        // 添加 redis 缓存数据
        nodeConfigSets.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config, (long) val.getId(), val);
        });
    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addBuildNodeCache() {
        // 获取全部虚点、总线、线路列表数据
        List<BuildNode> buildNodes = pointMapper.selectBuildNodeList();

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Bus);
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Line);
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Vpoint);

        if (buildNodes == null || buildNodes.isEmpty()) {

            return;
        }


        // 添加 redis 缓存数据
        buildNodes.forEach(val -> {
            if (val.getNodeType().equals(DeviceTreeConstants.BES_VPOINTNOPROPERTY)) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Vpoint, (long) val.getTreeId(), val);
            }
            if (val.getNodeType().equals(DeviceTreeConstants.BES_BUS)) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Bus, (long) val.getTreeId(), val);
            }
            if (val.getNodeType().equals(DeviceTreeConstants.BES_LINE)) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Line, (long) val.getTreeId(), val);
            }

            if (val.getNodeType().equals(DeviceTreeConstants.BES_BUILDINGAUTO) ||
                    val.getNodeType().equals(DeviceTreeConstants.BES_SMARTLIGHTING) ||
                    val.getNodeType().equals(DeviceTreeConstants.BES_ENERGYCONCOL)
            ) {

                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ControllerType, (long) val.getTreeId(), val);
            }
            /**
             * @description:初始化园区根节点
             * @author: sunshangeng
             **/
            if (val.getNodeType().equals(DeviceTreeConstants.BES_PARKROOTNODE)) {
                /**初始化园区时增加园区 所属园区*/


                DeviceTree rootTree = devicetreeMappper.getdeviceTreeByid(val.getTreeId() + "");

                val.setPark(rootTree.getPark());
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ControllerType, (long) val.getTreeId(), val);
            }

            /***
             * @description:启动时将支线干线缓存添加
             * @author: sunshangeng
             **/
            if (val.getNodeType().equals(DeviceTreeConstants.BES_TRUNK)) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_TrunkLine, (long) val.getTreeId(), val);
            }
            if (val.getNodeType().equals(DeviceTreeConstants.BES_BRANCH)) {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_BranchLine, (long) val.getTreeId(), val);
            }

        });
    }

    /**
     * @Description: 刷新缓存
     * @auther: wanghongjie
     * @date: 16:36 2022/11/18
     * @param:
     * @return:
     */
    public void initPointCache() {
        /**
         * 添加数据到 redis 缓存
         */
        addPointCache();
        /**
         * 添加数据到 redis 缓存
         */
        addBuildNodeCache();
        /**
         * 添加数据到 redis 缓存
         */
        addPointConfigCache();
    }

    /**
     * @Description: 添加楼宇自动虚点、总线、线路
     * @auther: gaojikun
     * @param:deviceTree
     * @return:deviceTree
     */
    @Transactional
    @Override
    public AjaxResult insertDeviceTreee(BuildNode buildNode) {
        DataReception dataReception = insertDeviceTreeeReturn(buildNode);
        if (!dataReception.getState()) {
            String errorMsg = dataReception.getMsg();
            return AjaxResult.error(errorMsg);
        } else {
            return AjaxResult.success("添加成功", dataReception.getData());
        }
    }

    /**
     * @Description: 修改楼宇自动虚点、总线、线路
     * @auther: gaojikun
     * @param:deviceTree
     * @return:deviceTree
     */
    @Transactional
    @Override
    public AjaxResult updateDeviceTreee(BuildNode buildNode) {

        buildNode.setUpdateTime(DateUtils.getNowDate());

        String sysName = buildNode.getSysName();                //系统名称
        Integer nodeType = buildNode.getNodeType();             //设备树点类型
        String nickName = buildNode.getNickName();              //别名
        Integer treeId = buildNode.getTreeId();              //别名

        if (
                !StringUtils.hasText(sysName)
                        || !StringUtils.hasText(nickName)
                        || nodeType == null
                        || treeId == null
        ) {
            return AjaxResult.error("请输入完整信息");
        }

        if (nodeType.equals(DeviceTreeConstants.BES_LINE)) {
            String portNum = buildNode.getPortNum();           //端口号
            if (portNum == null) {
                return AjaxResult.error("请输入完整信息");
            }

            //端口查重
            Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Line).values();
            for (Object obj : values) {
                BuildNode b = (BuildNode) obj;
                if (!b.getTreeId().equals(buildNode.getTreeId()) && b.getPortNum().equals(buildNode.getPortNum())) {
                    DeviceTree nodeByLineTreeId = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(String.valueOf(b.getTreeId())));
                    DeviceTree nodeByLineTreeIdCheck = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(String.valueOf(buildNode.getTreeId())));
                    if (nodeByLineTreeId.getDeviceTreeFatherId() == nodeByLineTreeIdCheck.getDeviceTreeFatherId()) {
                        return AjaxResult.error("端口号重复");
                    }
                }
            }
            String sysNameold = buildNode.getSysName();
            String sysNameNew = sysNameold.substring(0, sysNameold.length() - 2);
            buildNode.setSysName(sysNameNew);
            BuildNode BuildNodeCheck = pointMapper.selectBuildNodeByPort(buildNode);
            if (BuildNodeCheck != null) {
                return AjaxResult.error("端口号重复");
            }
            buildNode.setSysName(sysNameold);
        }

        String key = "";
        if (nodeType.equals(DeviceTreeConstants.BES_BUS)) {
            key = RedisKeyConstants.BES_BasicData_DeviceTree_Build_Bus;
        } else if (nodeType.equals(DeviceTreeConstants.BES_VPOINTNOPROPERTY)) {
            key = RedisKeyConstants.BES_BasicData_DeviceTree_Build_Vpoint;
        } else if (nodeType.equals(DeviceTreeConstants.BES_PARKROOTNODE)) {
            /**
             * @description:如果是园区根节点
             * @author: sunshangeng
             **/
            key = RedisKeyConstants.BES_BasicData_DeviceTree_ControllerType;
            /**查重*/
            //判断添加的名称是否重复(将园区编号当成系统名称)
            Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree).values();
            for (Object obj : collection) {
                DeviceTree deviceTree = (DeviceTree) obj;
                if ( deviceTree.getRedisSysName() == null) {
                    continue;
                }
                if(deviceTree.getDeviceTreeId()==3281){
                    System.out.println("ceshi");
                }
                if (deviceTree.getDeviceTreeId()!=buildNode.getTreeId()&&deviceTree.getRedisSysName().equals(sysName)) {
                    /**重复*/
                    return AjaxResult.error("所属园区不能重复！");

                }
            }
            /**修改设备树结构*/
            DeviceTree deviceTree=new DeviceTree();
            deviceTree.setSysName(sysName);
            deviceTree.setPark(sysName);
            deviceTree.setUpdateTime(new Date());
            deviceTree.setDeviceTreeId(treeId);
            boolean isupdateDeviceTree = pointMapper.updateDeviceTreee(deviceTree);
            if(!isupdateDeviceTree){
                throw  new ServiceException("修改园区不成功！");
            }
            buildNode.setPark(sysName);
        } else {
            key = RedisKeyConstants.BES_BasicData_DeviceTree_Build_Line;
        }

        //取缓存信息
        DeviceTree deviceTreeP = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) buildNode.getTreeId());

        //修改表
        boolean isUpdateBuildNode = pointMapper.updateBuildNode(buildNode);
        if (isUpdateBuildNode) {
//            buildNode = pointMapper.selectBuildNodeByTreeId(buildNode);
            buildNode.setDeviceNodeFunName(deviceTreeP.getDeviceNodeFunName());
            buildNode.setDeviceNodeFunType(deviceTreeP.getDeviceNodeFunType());
            buildNode.setNickName(nickName);

            //存入节点缓存
            redisCache.setCacheMapValue(key, (long) buildNode.getTreeId(), buildNode);
            //存入树缓存
            deviceTreeP.setSysName(nickName);
            deviceTreeP.setRedisSysName(sysName);
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeP.getDeviceTreeId(), deviceTreeP);
            return AjaxResult.success("修改成功", buildNode);
        }
        return AjaxResult.error("修改失败");

    }

    @Transactional
    public DataReception insertDeviceTreeeReturn(BuildNode buildNode) {

        buildNode.setCreateTime(DateUtils.getNowDate());

        Integer nodeType = buildNode.getNodeType();             //设备树点类型
        String nickName = buildNode.getNickName();              //别名
        String fatherId = buildNode.getFatherId();              //设备树父ID

        if (
                !StringUtils.hasText(nickName)
                        || !StringUtils.hasText(fatherId)
                        || nodeType == null
        ) {
            return new DataReception(false, "请完善输入信息!");
        }

        if (nodeType.equals(DeviceTreeConstants.BES_LINE)) {
            String portNum = buildNode.getPortNum();           //端口号
            if (portNum == null) {
                return new DataReception(false, "请完善输入信息!");
            }

            //查询当前总线下线路数+系统名称查重
            List<DeviceTree> deviceTrees = new ArrayList<>();
            Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree).values();
            for (Object obj : collection) {
                DeviceTree deviceTree = (DeviceTree) obj;
                if (deviceTree.getRedisSysName() == null) {
                    continue;
                }
                if (deviceTree.getRedisSysName().equals(buildNode.getSysName())) {
                    return new DataReception(false, "系统名称不允许重复!");
                }
                if (deviceTree.getDeviceTreeFatherId() == Integer.parseInt(buildNode.getFatherId())) {
                    deviceTrees.add(deviceTree);
                }
            }
            if (deviceTrees.size() > 0) {
                //端口查重
                for (DeviceTree deviceTree : deviceTrees) {
                    BuildNode b = deviceTreeCache.getNodeByLineTreeId((long) deviceTree.getDeviceTreeId());
                    if (b.getPortNum().equals(buildNode.getPortNum())) {
                        return new DataReception(false, "端口号重复!");
                    }
                }
            }
        }

        if (nodeType.equals(DeviceTreeConstants.BES_PARKROOTNODE)) {//如果是园区根节点

            if(!StringUtils.hasText(buildNode.getPark())){
                return new DataReception(false, "园区编号不能为空!");
            }

            //判断添加的名称是否重复(将园区编号当成系统名称)
            String sysName = buildNode.getSysName();
            Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree).values();
            for (Object obj : collection) {
                DeviceTree deviceTree = (DeviceTree) obj;
                if (deviceTree.getRedisSysName() == null) {
                    continue;
                }
                if (deviceTree.getRedisSysName().equals(sysName)) {
                    return new DataReception(false, "所属园区不允许重复!");
                }
            }
            return addBuildNode(buildNode, nodeType, buildNode.getPark());

        }


        //查询是否存在总线，虚点节点
        Map<String, Object> map = pointMapper.selectDeviceTreeeByFatherId(Integer.parseInt(buildNode.getFatherId()), buildNode.getNodeType());

        if (map.isEmpty() || map.get("num") == null || map.get("sysName") == null || map.get("deviceType") == null) {

            return new DataReception(false, "添加失败，参数为空!");
        }


        if ("0".equals(map.get("num").toString()) && nodeType.equals(DeviceTreeConstants.BES_BUS)) {//总线生成sysName
            buildNode.setSysName(map.get("sysName") + "02");
            return addBuildNode(buildNode, Integer.parseInt(map.get("deviceType").toString()), map.get("park").toString());

        } else if ("0".equals(map.get("num").toString()) && nodeType.equals(DeviceTreeConstants.BES_VPOINTNOPROPERTY)) {//虚点生成sysName
            buildNode.setSysName(map.get("sysName") + "01");
            return addBuildNode(buildNode, Integer.parseInt(map.get("deviceType").toString()), map.get("park").toString());

        } else if (nodeType.equals(DeviceTreeConstants.BES_BUILDINGAUTO) || nodeType.equals(DeviceTreeConstants.BES_SMARTLIGHTING) || nodeType.equals(DeviceTreeConstants.BES_ENERGYCONCOL)) {
            /**
             * @description:如果是照明 楼控 能耗节点
             * @author: sunshangeng
             **/
            return addBuildNode(buildNode, Integer.parseInt(map.get("deviceType").toString()), map.get("park").toString());

        } else if (!nodeType.equals(DeviceTreeConstants.BES_BUS) && !nodeType.equals(DeviceTreeConstants.BES_VPOINTNOPROPERTY)) {//线路生成sysName

            int code = Integer.parseInt(map.get("num").toString()) + 1;
            if (code < 10) {
                buildNode.setSysName(map.get("sysName") + "0" + code);
            } else {
                buildNode.setSysName(map.get("sysName").toString() + code);
            }

            return addBuildNode(buildNode, Integer.parseInt(map.get("deviceType").toString()), map.get("park").toString());

        } else {
            return new DataReception(false, "该类型子节点已到达上限");
        }

    }

    @Transactional
    public DataReception addBuildNode(BuildNode buildNode, int deviceType, String park) {
        //添加树节点
        DeviceTree deviceTreeP = new DeviceTree();
        deviceTreeP.setSysName(buildNode.getSysName());
        deviceTreeP.setDeviceNodeId(buildNode.getNodeType());
        deviceTreeP.setDeviceTreeFatherId(Integer.parseInt(buildNode.getFatherId()));
        deviceTreeP.setPark(park);
        deviceTreeP.setCreateTime(DateUtils.getNowDate());

        if (deviceType != DeviceTreeConstants.BES_PARKROOTNODE) {
            deviceTreeP.setDeviceType(deviceType);
        }

        boolean isInsertDeviceTreee = pointMapper.insertDeviceTreee(deviceTreeP);
        if (isInsertDeviceTreee) {

            DeviceTree deviceTreeR = pointMapper.selectDeviceTreeeByDeviceTreee(deviceTreeP);
            buildNode.setDeviceNodeFunType(deviceTreeR.getDeviceNodeFunType());
            buildNode.setDeviceNodeFunName(deviceTreeR.getDeviceNodeFunName());

            //添加至表
            buildNode.setTreeId(deviceTreeP.getDeviceTreeId());
            boolean isInsertBuildNode = pointMapper.insertBuildNode(buildNode);
            if (isInsertBuildNode) {
                String key = "";
                if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_BUS)) {
                    key = RedisKeyConstants.BES_BasicData_DeviceTree_Build_Bus;
                } else if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_VPOINTNOPROPERTY)) {
                    key = RedisKeyConstants.BES_BasicData_DeviceTree_Build_Vpoint;
                } else if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_BUILDINGAUTO) || buildNode.getNodeType().equals(DeviceTreeConstants.BES_SMARTLIGHTING) || buildNode.getNodeType().equals(DeviceTreeConstants.BES_ENERGYCONCOL)) {
                    /**
                     * @description:照明 楼控 能耗节点
                     * @author: sunshangeng
                     **/
                    key = RedisKeyConstants.BES_BasicData_DeviceTree_ControllerType;
                } else if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_PARKROOTNODE)) {
                    /**如果是园区根节点*/
                    key = RedisKeyConstants.BES_BasicData_DeviceTree_ControllerType;
                } else {
                    key = RedisKeyConstants.BES_BasicData_DeviceTree_Build_Line;
                    DeviceTree deviceTreeFather = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(buildNode.getFatherId()));
                    deviceTreeR.setDeviceTreeStatus(deviceTreeFather.getDeviceTreeStatus());
                }

                if (!key.equals("")) {
                    //存入节点缓存
                    redisCache.setCacheMapValue(key, (long) buildNode.getTreeId(), buildNode);
                }


                //存入树缓存
                deviceTreeR.setSysName(buildNode.getNickName());
                deviceTreeR.setRedisSysName(buildNode.getSysName());
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeP.getDeviceTreeId(), deviceTreeR);

                return new DataReception(true, "添加成功", buildNode);
            }
        }
        return new DataReception(false, "添加失败!");
    }

    /**
     * @Description: 查询虚点类型
     * @auther: gaojikun
     */
    @Override
    public AjaxResult listVpoint() {
        return AjaxResult.success(pointMapper.listVpoint());
    }

    /**
     * @Description: 添加虚点
     * @auther: gaojikun
     * @param:point
     * @return:AjaxResult
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult insertPoint(Point point) {

        point.setCreateTime(DateUtils.getNowDate());            //创建时间
        point.setSyncState(0);
        point.setFaultState(0);

        String SysName = point.getSysName();                    //系统名称
        String nickName = point.getNickName();                  //别名
        String nodeType = point.getNodeType();                  //树点类型
        String initVal = point.getInitVal();                    //初始值
        Integer enabled = point.getEnabled();                   //使能
        String vpointType = point.getVpointType();              //虚点类型
        Long treeId = point.getTreeId();                        //树ID
        String engineerUnit = point.getEngineerUnit();          //单位
        Integer energyStatics = point.getEnergyStatics();       //能耗统计
        String energyCode = point.getEnergyCode();              //能耗类型
        Integer alarmEnable = point.getAlarmEnable();           //报警使能
        Integer alarmPriority = point.getAlarmPriority();       //报警优先级
        Integer alarmType = point.getAlarmType();               //报警类型
        String description = point.getDescription();            //描述
        Long accuracy = point.getAccuracy();                    //精度
        String fatherId = point.getFatherId();                  //所属虚点
        Integer syncState = point.getSyncState();                //同步状态
        Integer faultState = point.getFaultState();             //故障状态

        if (StringUtils.hasText(vpointType)){
            if (
                    (!StringUtils.hasText(SysName)                       //系统名称
                            || !StringUtils.hasText(nickName)           //别名
                            || !StringUtils.hasText(nodeType)           //树点类型
                            || !StringUtils.hasText(initVal)            //初始值
                            || !StringUtils.hasText(engineerUnit)       //单位
                            || !StringUtils.hasText(energyCode)         //能耗类型
                            || !StringUtils.hasText(description)        //描述
                            || !StringUtils.hasText(fatherId)           //所属虚点ID
                            || syncState == null                        //同步状态
                            || enabled == null                          //使能
                            || energyStatics == null                    //能耗采集
                            || alarmEnable == null                      //报警使能
                            || alarmPriority == null                    //报警优先级
                            || alarmType == null                        //报警类型
                            || accuracy == null                         //精度
                            || faultState == null                       //故障状态
                    ) && vpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))
            ) {//VAI
                return AjaxResult.error("请完善输入信息");
            }
            else if ((!StringUtils.hasText(SysName)                       //系统名称
                    || !StringUtils.hasText(nickName)           //别名
                    || !StringUtils.hasText(nodeType)           //树点类型
                    || !StringUtils.hasText(initVal)            //初始值
                    || !StringUtils.hasText(engineerUnit)       //单位
                    || !StringUtils.hasText(description)        //描述
                    || !StringUtils.hasText(fatherId)           //所属虚点ID
                    || syncState == null                        //同步状态
                    || enabled == null                          //使能
                    || alarmEnable == null                      //报警使能
                    || alarmPriority == null                    //报警优先级
                    || alarmType == null                        //报警类型
                    || accuracy == null                         //精度
                    || faultState == null                       //故障状态
            ) && vpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {//VAO
                return AjaxResult.error("请完善输入信息");
            }
            else if (
                    (!StringUtils.hasText(SysName)                       //系统名称
                            || !StringUtils.hasText(nickName)           //别名
                            || !StringUtils.hasText(nodeType)           //树点类型
                            || !StringUtils.hasText(initVal)            //初始值
                            || !StringUtils.hasText(description)        //描述
                            || !StringUtils.hasText(fatherId)           //所属虚点ID
                            || syncState == null                        //同步状态
                            || enabled == null                          //使能
                            || alarmEnable == null                      //报警使能
                            || alarmPriority == null                    //报警优先级
                            || alarmType == null                        //报警类型
                            || faultState == null                       //故障状态
                    ) && (vpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDO))
                            ||vpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI)))// VDO VDI
            ) {
                return AjaxResult.error("请完善输入信息");
            }
        }
        else {
            return AjaxResult.error("虚点类型不能为空！");
        }


        //名称查重
        Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree).values();
        for (Object obj : collection) {
            DeviceTree deviceTree = (DeviceTree) obj;
            if (deviceTree.getRedisSysName() != null) {
                if (deviceTree.getRedisSysName().equals(point.getSysName())) {
                    return AjaxResult.error("系统名称不允许重复!");
                }
            }
        }


        //先添加到树结构
        DeviceTree deviceTreeP = new DeviceTree();
        deviceTreeP.setDeviceNodeId(Integer.parseInt(point.getNodeType()));
        deviceTreeP.setDeviceTreeFatherId(Integer.parseInt(point.getFatherId()));
        deviceTreeP.setCreateTime(DateUtils.getNowDate());
        deviceTreeP.setSysName(point.getSysName());
//        Map<String, Object> map = pointMapper.selectDeviceTreeeByFatherId(deviceTreeP.getDeviceTreeFatherId(), deviceTreeP.getDeviceNodeId());
        DeviceTree treeFatherInfo = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(String.valueOf(deviceTreeP.getDeviceTreeFatherId())));
        deviceTreeP.setDeviceType(treeFatherInfo.getDeviceType());
        deviceTreeP.setPark(treeFatherInfo.getPark());
        boolean isInsertDeviceTreee = pointMapper.insertDeviceTreee(deviceTreeP);
        if (isInsertDeviceTreee) {
            //查询点子节点按钮
            DeviceTree deviceTreeR = pointMapper.selectDeviceTreeeByDeviceTreee(deviceTreeP);

            //查询控制器信息,添加设备ID
            Controller con = null;
            List<Controller> controllers = new ArrayList<>();
            recursive(Long.parseLong(point.getFatherId()), controllers);
            con = controllers.get(0);
            int controllerId = con.getId();
            List<Point> sortList = new ArrayList<>();
            Map<String, Point> PointCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Point);
            Collection<Point> pointValues = PointCacheMap.values();
            for (Point p : pointValues) {
                if (p.getControllerId() == controllerId) {
                    sortList.add(p);
                    continue;
                }
            }
            int equipmentId = 0;
            if (sortList.size() > 0) {
                sortList.sort((o1, o2) -> String.valueOf(o2.getTreeId()).compareTo(String.valueOf(o1.getTreeId())));
                equipmentId = sortList.get(0).getEquipmentId();
            }
            equipmentId = equipmentId + 1;

            point.setEquipmentId(equipmentId);
            point.setControllerId(controllerId);

            //添加到点位表
            point.setTreeId((long) deviceTreeP.getDeviceTreeId());
            point.setEnabled(1);

            boolean isInsertPoint = pointMapper.insertPoint(point);
            if (isInsertPoint) {
                //查询添加后的点位信息
                point = pointMapper.selectPointByGuid(point.getGuid());

                if (point.getEnergyStatics() != null && 1 == point.getEnergyStatics()) {
                    //能耗参数为1时 把点位当作电表存储
                    AthenaElectricMeter meter = new AthenaElectricMeter();
                    meter.setActive(Long.parseLong(point.getEnabled().toString()));
                    meter.setAlias(point.getNickName());
                    meter.setDeviceTreeId(point.getTreeId());
                    meter.setType("1");
                    meter.setCreateTime(DateUtils.getNowDate());
                    boolean addMeter = athenaElectricMeterMapper.insertAthenaElectricMeter(meter);
                    if (addMeter) {
                        //添加缓存
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, meter.getDeviceTreeId(), meter);
                    } else {
                        return AjaxResult.success("添加失败，未将点位添加到点表中", point);
                    }

                }

                point.setDeviceNodeFunName(deviceTreeR.getDeviceNodeFunName());
                point.setDeviceNodeFunType(deviceTreeR.getDeviceNodeFunType());
                point.setDeviceTreeStatus(deviceTreeR.getDeviceTreeStatus());

                //添加至设备树缓存
                deviceTreeR.setSysName(point.getNickName());
                deviceTreeR.setRedisSysName(point.getSysName());
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeP.getDeviceTreeId(), deviceTreeR);
                //点存入缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);
                /**楼控*****/
                if (con.getType() == DeviceTreeConstants.BES_DDCNODE) {

                    PointParamDDC pointParam = new PointParamDDC();

                    String initValue = point.getInitVal();
                    if (initValue == null || "".equals(initValue)) {
                        initValue = "0";
                    }

                    Integer paramInitVal;
                    if (null == accuracy || accuracy == 0) {
                        paramInitVal = Integer.parseInt(initValue);
                    } else {
                        Integer paramAccuracy = Integer.parseInt(String.valueOf(accuracy));
                        paramInitVal = (int) Math.round(Double.parseDouble(initValue) * (Math.pow(10, (paramAccuracy))));
                    }

                    Integer pointType = 0;
                    if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))) {
                        pointType = PointType.POINT_TYPE_VIRTUAL_AI;
                    } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {
                        pointType = PointType.POINT_TYPE_VIRTUAL_AO;
                    } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI))) {
                        pointType = PointType.POINT_TYPE_VIRTUAL_DI;
                    } else {
                        pointType = PointType.POINT_TYPE_VIRTUAL_DO;
                    }

                    pointParam.setPointType(pointType);                                                         //树点类型
                    pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                 //ID
                    pointParam.setActive(point.getEnabled());                                                   //使能
                    pointParam.setName(point.getSysName());                                                     //系统名称
                    pointParam.setAlias(point.getNickName());                                                   //别名
                    pointParam.setDescription(point.getDescription());                                          //描述
                    pointParam.setInitValue(paramInitVal);                                                      //初始值
                    pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                    pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                    pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                    pointParam.setAlarmTrigger(point.getCloseState());                                          //闭合状态

                    if (pointType == PointType.POINT_TYPE_VIRTUAL_AI || pointType == PointType.POINT_TYPE_VIRTUAL_AO) {
                        pointParam.setUnit(point.getEngineerUnit());                                                //单位
                        pointParam.setPrecision(Integer.parseInt(String.valueOf(point.getAccuracy())));             //精度
                        pointParam.setAlarmHighValue(0);
                        pointParam.setAlarmLowValue(0);
                    }

                    // 同步数据到下位机
                    boolean sendState = SendMsgHandler.addPointDDC(con.getIp(), pointParam);
                    if (!sendState) {
                        return AjaxResult.success("添加成功，点位下发信息失败", point);
                    }
                    // 添加订阅消息
                    MsgSubPubHandler.addSubMsg(DDCCmd.POINT_ADD, con.getIp());
                }
                /**照明*****/
                else {
                    PointParamLDC pointParam = new PointParamLDC();

                    String initValue = point.getInitVal();
                    if (initValue == null || "".equals(initValue)) {
                        initValue = "0";
                    }

                    Integer paramInitVal;

                    if (null == accuracy || accuracy == 0) {
                        paramInitVal = Integer.parseInt(initValue);
                    } else {
                        Integer paramAccuracy = Integer.parseInt(String.valueOf(accuracy));
                        paramInitVal = (int) Math.round(Double.parseDouble(initValue) * (Math.pow(10, (paramAccuracy))));
                    }

                    Integer pointType = 0;
                    if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))) {
                        pointType = PointType.POINT_TYPE_VIRTUAL_AI;
                    } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {
                        pointType = PointType.POINT_TYPE_VIRTUAL_AO;
                    } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI))) {
                        pointType = PointType.POINT_TYPE_VIRTUAL_DI;
                    } else {
                        pointType = PointType.POINT_TYPE_VIRTUAL_DO;
                    }

                    pointParam.setPointType(pointType);                                                         //树点类型
                    pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                 //ID
                    pointParam.setActive(point.getEnabled());                                                   //使能
                    pointParam.setName(point.getSysName());                                                     //系统名称
                    pointParam.setAlias(point.getNickName());                                                   //别名
                    pointParam.setDescription(point.getDescription());                                          //描述
                    pointParam.setInitValue(paramInitVal);                                                      //初始值
                    pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                    pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                    pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                    pointParam.setAlarmTrigger(point.getCloseState());                                          //闭合状态

                    if (pointType == PointType.POINT_TYPE_VIRTUAL_AI || pointType == PointType.POINT_TYPE_VIRTUAL_AO) {
                        pointParam.setUnit(point.getEngineerUnit());                                                //单位
                        pointParam.setPrecision(Integer.parseInt(String.valueOf(point.getAccuracy())));             //精度
                        pointParam.setAlarmHighValue(0);
                        pointParam.setAlarmLowValue(0);
                    }

                    // 同步数据到下位机
                    boolean sendState = SendMsgHandler.addPointLDC(con.getIp(), pointParam);
                    if (!sendState) {
                        return AjaxResult.success("添加成功，点位下发信息失败", point);
                    }
                    // 添加订阅消息
                    MsgSubPubHandler.addSubMsg(LDCCmd.POINT_ADD, con.getIp());

                }

                return AjaxResult.success("添加成功,下发成功！", point);
            }
        }
        return AjaxResult.error("添加失败");
    }

    /**
     * 递归 根据树ID 获取控制器缓存
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
     * @Description: 修改点
     * @auther: gaojikun修改成功
     * @param:point
     * @return:AjaxResult
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult updatePoint(Point point) {
        try {


            if (point.getAlarmType() == null) {
                point.setAlarmType(0);
            }
            point.setUpdateTime(DateUtils.getNowDate());         //修改时间

            //判断是否要新增模块点
            boolean isInsertPoint = false;
            if (DeviceTreeConstants.BES_VPOINT != Integer.parseInt(point.getNodeType())) {
                Point redisPoint = moduleAndPointCache.getPointByDeviceId(point.getTreeId());
                if (null == redisPoint.getNickName() || "".equals(redisPoint.getNickName())) {
                    isInsertPoint = true;
                }
            }

            if (DeviceTreeConstants.BES_VPOINT == Integer.parseInt(point.getNodeType())) {//修改虚点
                String SysName = point.getSysName();                    //系统名称
                String nickName = point.getNickName();                  //别名
                String nodeType = point.getNodeType();                  //树点类型
                String initVal = point.getInitVal();                    //初始值
                Integer enabled = point.getEnabled();                   //使能
                String vpointType = point.getVpointType();              //虚点类型
                Long treeId = point.getTreeId();                        //树ID
                String engineerUnit = point.getEngineerUnit();          //单位
                Integer energyStatics = point.getEnergyStatics();       //能耗统计
                String energyCode = point.getEnergyCode();              //能耗类型
                Integer alarmEnable = point.getAlarmEnable();           //报警使能
                Integer alarmPriority = point.getAlarmPriority();       //报警优先级
                Integer alarmType = point.getAlarmType();               //报警类型
                String description = point.getDescription();            //描述
                Long accuracy = point.getAccuracy();                    //精度
                Integer syncState = point.getSyncState();                //同步状态
                Integer faultState = point.getFaultState();             //故障状态
                if (StringUtils.hasText(vpointType)){
                    if (
                            (!StringUtils.hasText(SysName)                       //系统名称
                                    || !StringUtils.hasText(nickName)           //别名
                                    || !StringUtils.hasText(nodeType)           //树点类型
                                    || !StringUtils.hasText(initVal)            //初始值
                                    || !StringUtils.hasText(engineerUnit)       //单位
                                    || !StringUtils.hasText(energyCode)         //能耗类型
                                    || !StringUtils.hasText(description)        //描述
                                    || syncState == null                        //同步状态
                                    || enabled == null                          //使能
                                    || energyStatics == null                    //能耗采集
                                    || alarmEnable == null                      //报警使能
                                    || alarmPriority == null                    //报警优先级
                                    || alarmType == null                        //报警类型
                                    || accuracy == null                         //精度
                                    || faultState == null                       //故障状态
                            ) && vpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))//VAI
                    ) {//VAI
                        return AjaxResult.error("请完善输入信息");
                    } else if (
                            (!StringUtils.hasText(SysName)                       //系统名称
                                    || !StringUtils.hasText(nickName)           //别名
                                    || !StringUtils.hasText(nodeType)           //树点类型
                                    || !StringUtils.hasText(initVal)            //初始值
                                    || !StringUtils.hasText(engineerUnit)       //单位
                                    || !StringUtils.hasText(description)        //描述
                                    || syncState == null                        //同步状态
                                    || enabled == null                          //使能
                                    || alarmEnable == null                      //报警使能
                                    || alarmPriority == null                    //报警优先级
                                    || alarmType == null                        //报警类型
                                    || accuracy == null                         //精度
                                    || faultState == null                       //故障状态
                            ) && vpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {//VAO
                        return AjaxResult.error("请完善输入信息");
                    } else if (
                            (!StringUtils.hasText(SysName)                       //系统名称
                                    || !StringUtils.hasText(nickName)           //别名
                                    || !StringUtils.hasText(nodeType)           //树点类型
                                    || !StringUtils.hasText(initVal)            //初始值
                                    || !StringUtils.hasText(description)        //描述
                                    || syncState == null                        //同步状态
                                    || enabled == null                          //使能
                                    || alarmEnable == null                      //报警使能
                                    || alarmPriority == null                    //报警优先级
                                    || alarmType == null                        //报警类型
                                    || faultState == null                       //故障状态
                            ) && (vpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDO))
                                    || vpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI))) //VDO VDI
                    ) {//VDI
                        return AjaxResult.error("请完善输入信息");
                    }
                }
                else {
                    return AjaxResult.error("虚点类型不能为空！");
                }
            }
            else if (DeviceTreeConstants.BES_DO == Integer.parseInt(point.getNodeType())) {//DO点

                String sysName = point.getSysName();                        //系统名称
                String nickName = point.getNickName();                      //别名
                String nodeType = point.getNodeType();                      //树点类型
                Integer enabled = point.getEnabled();                       //使能
                Long reversed = point.getReversed();                        //是否反向
                String initVal = point.getInitVal();                        //初始值
                String description = point.getDescription();                //描述
                Long workMode = point.getWorkMode();                        //工作模式
                Integer alarmEnable = point.getAlarmEnable();               //报警使能
                Integer alarmType = point.getAlarmType();                   //报警类型
                Integer closeState = point.getCloseState();                 //闭合状态
                Integer faultState = point.getFaultState();                 //故障状态
                Integer alarmPriority = point.getAlarmPriority();           //报警优先级

                if (
                        !StringUtils.hasText(sysName)
                                || !StringUtils.hasText(nickName)
                                || !StringUtils.hasText(nodeType)
                                || enabled == null
                                || reversed == null
                                || !StringUtils.hasText(initVal)
                                || !StringUtils.hasText(description)
                                || workMode == null
                                || alarmEnable == null
                                || alarmType == null
                                || closeState == null
                                || faultState == null
                                || alarmPriority == null
                ) {
                    return AjaxResult.error("请完善输入信息");
                }
            }
            else if (DeviceTreeConstants.BES_DI == Integer.parseInt(point.getNodeType())) {//DI点

                String sysName = point.getSysName();                        //系统名称
                String nickName = point.getNickName();                      //别名
                String nodeType = point.getNodeType();                      //树点类型
                Integer enabled = point.getEnabled();                       //使能
                Long reversed = point.getReversed();                        //是否反向
                Long sourced = point.getSourced();                          //是否有源
                String description = point.getDescription();                //描述
                Long workMode = point.getWorkMode();                        //工作模式
                Integer alarmEnable = point.getAlarmEnable();               //报警使能
                Integer alarmType = point.getAlarmType();                   //报警类型
                Integer closeState = point.getCloseState();                 //闭合状态
                Integer faultState = point.getFaultState();                 //故障状态
                Integer alarmPriority = point.getAlarmPriority();           //报警优先级

                if (
                        !StringUtils.hasText(sysName)
                                || !StringUtils.hasText(nickName)
                                || !StringUtils.hasText(nodeType)
                                || enabled == null
                                || reversed == null
                                || sourced == null
                                || !StringUtils.hasText(description)
                                || workMode == null
                                || alarmEnable == null
                                || alarmType == null
                                || closeState == null
                                || faultState == null
                                || alarmPriority == null
                ) {
                    return AjaxResult.error("请完善输入信息");
                }
            }
            else if (DeviceTreeConstants.BES_AO == Integer.parseInt(point.getNodeType())) {//AO点

                String sysName = point.getSysName();                        //系统名称
                String nickName = point.getNickName();                      //别名
                String nodeType = point.getNodeType();                      //树点类型
                Integer enabled = point.getEnabled();                       //使能
                Long reversed = point.getReversed();                        //是否反向
                String description = point.getDescription();                //描述
                Long workMode = point.getWorkMode();                        //工作模式
                String engineerUnit = point.getEngineerUnit();              //单位
                String minVal = point.getMinVal();                          //最小值
                String maxVal = point.getMaxVal();                          //最大值
                Long accuracy = point.getAccuracy();                        //精度
                Long sinnalType = point.getSinnalType();                    //信号类型
                Integer alarmEnable = point.getAlarmEnable();               //报警使能
                Integer alarmType = point.getAlarmType();                   //报警类型
                Long highLimit = point.getHighLimit();                      //高限报警
                Long lowLimit = point.getLowLimit();                        //低限报警
                Integer energyStatics = point.getEnergyStatics();           //能耗统计
                String energyCode = point.getEnergyCode();                  //能耗类型
                Integer faultState = point.getFaultState();                 //故障状态
                Integer alarmPriority = point.getAlarmPriority();           //报警优先级
                String initVal = point.getInitVal();                        //初始值

                if (
                        !StringUtils.hasText(sysName)
                                || !StringUtils.hasText(nickName)
                                || !StringUtils.hasText(nodeType)
                                || enabled == null
                                || reversed == null
                                || !StringUtils.hasText(description)
                                || workMode == null
                                || !StringUtils.hasText(engineerUnit)
                                || !StringUtils.hasText(minVal)
                                || !StringUtils.hasText(maxVal)
                                || accuracy == null
                                || sinnalType == null
                                || alarmEnable == null
                                || alarmType == null
//                            || highLimit == null
//                            || lowLimit == null
//                                || energyStatics == null
//                                || !StringUtils.hasText(energyCode)
                                || faultState == null
                                || alarmPriority == null
                                || !StringUtils.hasText(initVal)
                ) {
                    return AjaxResult.error("请完善输入信息");
                }
            }
            else if (DeviceTreeConstants.BES_AI == Integer.parseInt(point.getNodeType())) {//AI点

                String sysName = point.getSysName();                        //系统名称
                String nickName = point.getNickName();                      //别名
                String nodeType = point.getNodeType();                      //树点类型
                Integer enabled = point.getEnabled();                       //使能
                Long reversed = point.getReversed();                        //是否反向
                String description = point.getDescription();                //描述
                Long workMode = point.getWorkMode();                        //工作模式
                String engineerUnit = point.getEngineerUnit();              //单位
                String minVal = point.getMinVal();                          //最小值
                String maxVal = point.getMaxVal();                          //最大值
                Long accuracy = point.getAccuracy();                        //精度
                Long sinnalType = point.getSinnalType();                    //信号类型
                Integer alarmEnable = point.getAlarmEnable();               //报警使能
                Integer alarmType = point.getAlarmType();                   //报警类型
                Long highLimit = point.getHighLimit();                      //高限报警
                Long lowLimit = point.getLowLimit();                        //低限报警
                Integer energyStatics = point.getEnergyStatics();           //能耗统计
                String energyCode = point.getEnergyCode();                  //能耗类型
                Integer faultState = point.getFaultState();                 //故障状态
                Integer alarmPriority = point.getAlarmPriority();           //报警优先级

                if (
                        !StringUtils.hasText(sysName)
                                || !StringUtils.hasText(nickName)
                                || !StringUtils.hasText(nodeType)
                                || enabled == null
                                || reversed == null
                                || !StringUtils.hasText(description)
                                || workMode == null
                                || !StringUtils.hasText(engineerUnit)
                                || !StringUtils.hasText(minVal)
                                || !StringUtils.hasText(maxVal)
                                || accuracy == null
                                || sinnalType == null
                                || alarmEnable == null
                                || alarmType == null
//                            || highLimit == null
//                            || lowLimit == null
                                || energyStatics == null
                                || !StringUtils.hasText(energyCode)
                                || faultState == null
                                || alarmPriority == null
                ) {
                    return AjaxResult.error("请完善输入信息");
                }
            }

            //名称查重
            Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree).values();
            for (Object obj : collection) {
                DeviceTree deviceTree = (DeviceTree) obj;
                if (deviceTree.getRedisSysName() != null) {
                    if (point.getTreeId() != deviceTree.getDeviceTreeId()) {
                        if (deviceTree.getRedisSysName().equals(point.getSysName())) {
                            return AjaxResult.error("系统名称不允许重复!");
                        }
                    }
                }
            }


            //先修改树
            /***qindehua  修改保存之后设备树  状态为在线***/
            DeviceTree deviceTreeP = deviceTreeCache.getDeviceTreeByDeviceTreeId(point.getTreeId());
            deviceTreeP.setDeviceNodeId(Integer.parseInt(point.getNodeType()));
            deviceTreeP.setDeviceTreeId(Integer.parseInt(String.valueOf(point.getTreeId())));
            deviceTreeP.setSysName(point.getSysName());
            deviceTreeP.setUpdateTime(DateUtils.getNowDate());
            boolean isUpdateDeviceTreee = pointMapper.updateDeviceTreee(deviceTreeP);
            if (isUpdateDeviceTreee) {

                //oldSysName
                Point oldPoint = moduleAndPointCache.getPointByDeviceId(point.getTreeId());
                String oldSysName = oldPoint.getSysName();
                Integer oldStatics = oldPoint.getEnergyStatics();
                String oldVpointType = "";
                if (DeviceTreeConstants.BES_VPOINT == Integer.parseInt(point.getNodeType())) {
                    oldVpointType = oldPoint.getVpointType();
                }

                List<AthenaBranchMeterLink> linkLists = new ArrayList();

                if (point.getEnergyStatics() != null && oldStatics != null && !oldStatics.equals(point.getEnergyStatics())) {//能耗参数发生改变
                    boolean isUpdateVpointAI = false;
                    String VpointType = point.getVpointType();
                    //虚点类型由AI转换为别的类型且是能耗统计的情况下
                    if (!"".equals(oldVpointType) && oldVpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))
                            && !"".equals(VpointType) && !VpointType.equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))
                            && oldStatics == 1) {
                        isUpdateVpointAI = true;
                    }
                    //修改能耗为0时
                    if (0 == point.getEnergyStatics() || isUpdateVpointAI) {
                        //根据树ID查询电表ID
                        AthenaElectricMeter querymeter = meterCache.getMeterByDeviceId(point.getTreeId());
                        if (querymeter.getMeterId() == null) {
                            return AjaxResult.error("未获取到电表缓存数据！修改失败！");
                        }
                        //删除电表
                        athenaElectricMeterMapper.deleteAthenaElectricMeterByMeterId(point.getTreeId().toString());
                        //删除缓存
                        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, point.getTreeId());

                        //查询支路配置
                        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink).values();
                        for (Object obj : values) {
                            AthenaBranchMeterLink link = (AthenaBranchMeterLink) obj;
                            if (querymeter.getMeterId().equals(link.getMeterId())) {
                                linkLists.add(link);
                            }
                        }
                        if (linkLists.size() > 0) {
                            for (AthenaBranchMeterLink delLink : linkLists) {
                                //删除关联支路配置
                                athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkById(delLink.getId());
                                //删除缓存
                                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, delLink.getId());
                            }
                        }
                    } else {
                        //修改能耗参数为1时 把点位当作电表存储
                        AthenaElectricMeter meter = new AthenaElectricMeter();
                        meter.setActive(Long.parseLong(point.getEnabled().toString()));
                        meter.setAlias(point.getNickName());
                        meter.setDeviceTreeId(point.getTreeId());
                        meter.setType("1");
                        meter.setCreateTime(DateUtils.getNowDate());
                        boolean addMeter = athenaElectricMeterMapper.insertAthenaElectricMeter(meter);
                        if (!addMeter) {
                            throw new Exception();
                        }
                        //添加缓存
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, meter.getDeviceTreeId(), meter);
                    }
                } else if (oldStatics == null && point.getEnergyStatics() != null && point.getEnergyStatics() == 1) {
                    //把点位当作电表存储
                    AthenaElectricMeter meter = new AthenaElectricMeter();
                    meter.setActive(Long.parseLong(point.getEnabled().toString()));
                    meter.setAlias(point.getNickName());
                    meter.setDeviceTreeId(point.getTreeId());
                    meter.setType("1");
                    meter.setCreateTime(DateUtils.getNowDate());
                    boolean addMeter = athenaElectricMeterMapper.insertAthenaElectricMeter(meter);
                    if (!addMeter) {
                        throw new Exception();
                    }
                    //添加缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, meter.getDeviceTreeId(), meter);
                }


                //当能源类型发生改变时 删除电表支路关联
                if (oldPoint.getEnergyStatics() != null && oldPoint.getEnergyCode() != null &&
                        point.getEnergyStatics() != null && point.getEnergyCode() != null &&
                        1 == oldPoint.getEnergyStatics() && 1 == point.getEnergyStatics() &&
                        !oldPoint.getEnergyCode().equals(point.getEnergyCode())) {
                    //获取电表ID
                    AthenaElectricMeter meterRedis = meterCache.getMeterByDeviceId(point.getTreeId());
                    //删除电表支路关联信息
                    athenaElectricMeterMapper.deleteBranchMeterLink(String.valueOf(meterRedis.getMeterId()));
                    //删除缓存
                    Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink).values();
                    for (Object obj : values) {
                        AthenaBranchMeterLink athenaBranchMeterLink = (AthenaBranchMeterLink) obj;
                        if (athenaBranchMeterLink.getMeterId().equals(meterRedis.getMeterId())) {
                            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, athenaBranchMeterLink.getId());
                        }
                    }
                }

                boolean isUpdatePoint = pointMapper.updatePoint(point);
                //修改电表数据表
                jobManagerMapper.updateCalculate(oldSysName, point.getSysName());
                //修改能耗监控差值数据表
                jobManagerMapper.updateMonitoring(oldSysName, point.getSysName());

                DeviceTree deviceTreeR = new DeviceTree();
                if ("".equals(deviceTreeP.getDeviceNodeFunType()) || "".equals(deviceTreeP.getDeviceNodeFunName()) ||
                        0 == deviceTreeP.getDeviceTreeStatus() || 0 == deviceTreeP.getDeviceTreeFatherId()) {
                    deviceTreeR = pointMapper.selectDeviceTreeeByDeviceTreee(deviceTreeP);
                } else {
                    deviceTreeR = deviceTreeP;
                }


                if (isUpdatePoint) {
                    //查询修改后的点位信息
                    point = pointMapper.selectPointByTreeId(point.getTreeId());
                    point.setDeviceNodeFunType(deviceTreeR.getDeviceNodeFunType());
                    point.setDeviceNodeFunName(deviceTreeR.getDeviceNodeFunName());
                    point.setDeviceTreeStatus(deviceTreeP.getDeviceTreeStatus());
                    point.setFatherId(String.valueOf(deviceTreeR.getDeviceTreeFatherId()));

                    //同步到模块点表
                    sceneModelMapper.updateSceneModelPoint(point);

                    //添加至设备树缓存
                    deviceTreeR.setSysName(point.getNickName());
                    deviceTreeR.setRedisSysName(point.getSysName());
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeP.getDeviceTreeId(), deviceTreeR);
                    //修改后的信息存入缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);

                    String initValue = point.getInitVal();
                    Integer precision = point.getAccuracy() == null ? null : Integer.parseInt(String.valueOf(point.getAccuracy())); //精度
                    String highRange = point.getMaxVal();                                                   //最高阀值
                    String lowRange = point.getMinVal();                                                    //最低阀值

                    //                    if (!nodeType.equals(DeviceTreeConstants.BES_DO)) {
                    if (initValue == null || "".equals(initValue)) {
                        initValue = "0";
                    }
//                    }

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
                    //查询控制器信息
                    Controller con = null;
                    List<Controller> controllers = new ArrayList<>();
                    if (DeviceTreeConstants.BES_VPOINT == Integer.parseInt(point.getNodeType())) {
                        recursive(point.getTreeId(), controllers);
                        con = controllers.get(0);
                    } else {
                        recursive(point.getTreeId(), controllers);
                        con = controllers.get(0);
                    }
                    if (con == null) {
                        return AjaxResult.error("获取父级控制器失败！修改失败！");
                    }
                    if (con.getType() == DeviceTreeConstants.BES_DDCNODE) {//楼控

                        PointParamDDC pointParam = new PointParamDDC();
                        if (DeviceTreeConstants.BES_VPOINT == Integer.parseInt(point.getNodeType())) {//修改虚点

                            Integer pointType = 0;
                            if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))) {
                                pointType = PointType.POINT_TYPE_VIRTUAL_AI;
                            } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {
                                pointType = PointType.POINT_TYPE_VIRTUAL_AO;
                            } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI))) {
                                pointType = PointType.POINT_TYPE_VIRTUAL_DI;
                            } else {
                                pointType = PointType.POINT_TYPE_VIRTUAL_DO;
                            }

                            pointParam.setPointType(pointType);                                                         //树点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                 //ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setInitValue(initVal);                                                           //初始值
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setAlarmTrigger(point.getCloseState());                                          //闭合状态
                            if (pointType == PointType.POINT_TYPE_VIRTUAL_AI || pointType == PointType.POINT_TYPE_VIRTUAL_AO) {
                                pointParam.setUnit(point.getEngineerUnit());                                                //单位
                                pointParam.setPrecision(Integer.parseInt(String.valueOf(point.getAccuracy())));             //精度
                            }


                            if (pointType == PointType.POINT_TYPE_VIRTUAL_AI || pointType == PointType.POINT_TYPE_VIRTUAL_AO) {//VAI VAO 类型
                                pointParam.setAlarmHighValue(0);
                                pointParam.setAlarmLowValue(0);
                            }
                        } else if (DeviceTreeConstants.BES_AI == Integer.parseInt(point.getNodeType())) {//修改AI

                            Integer ParampointType = PointType.POINT_TYPE_LAI;//点类型

                            pointParam.setPointType(ParampointType);                                                    //下发点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                 //点位ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setModuleID(Integer.parseInt(String.valueOf(point.getModuleId())));              //模块ID
                            pointParam.setChannelIndex(Integer.parseInt(String.valueOf(point.getChannelIndex())));      //通道索引
                            pointParam.setWorkMode(Integer.parseInt(String.valueOf(point.getWorkMode())));              //工作模式
                            pointParam.setPolarity(Integer.parseInt(String.valueOf(point.getReversed())));              //是否反向
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setLineType(Integer.parseInt(String.valueOf(point.getSinnalType())));            //信号类型
                            pointParam.setUnit(point.getEngineerUnit());                                                //单位
                            pointParam.setHighRange(highRangeVal);                                                      //最大值
                            pointParam.setLowRange(lowRangeVal);                                                        //最小值
                            pointParam.setPrecision(Integer.parseInt(String.valueOf(point.getAccuracy())));             //精度
                            if (point.getHighLimit() == null || point.getLowLimit() == null) {
                                pointParam.setAlarmHighValue(0);                                                     //高限报警
                                pointParam.setAlarmLowValue(0);                                                      //低限报警
                            } else {
                                pointParam.setAlarmHighValue(Integer.parseInt(String.valueOf(point.getHighLimit())));       //高限报警
                                pointParam.setAlarmLowValue(Integer.parseInt(String.valueOf(point.getLowLimit())));         //低限报警
                            }

                            pointParam.setInitValue(initVal);

                        } else if (DeviceTreeConstants.BES_DI == Integer.parseInt(point.getNodeType())) {//修改DI

                            Integer ParampointType = PointType.POINT_TYPE_LDI;//点类型

                            pointParam.setPointType(ParampointType);                                                    //下发点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                 //点位ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setModuleID(Integer.parseInt(String.valueOf(point.getModuleId())));              //模块ID
                            pointParam.setChannelIndex(Integer.parseInt(String.valueOf(point.getChannelIndex())));      //通道索引
                            pointParam.setWorkMode(Integer.parseInt(String.valueOf(point.getWorkMode())));              //工作模式
                            pointParam.setPolarity(Integer.parseInt(String.valueOf(point.getReversed())));              //是否反向
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setAlarmTrigger(point.getCloseState());                                          //闭合状态
                            pointParam.setActivePassive(Integer.parseInt(String.valueOf(point.getSourced())));          //是否有源
                            pointParam.setInitValue(initVal);

                        } else if (DeviceTreeConstants.BES_DO == Integer.parseInt(point.getNodeType())) {//修改DO

                            Integer ParampointType = PointType.POINT_TYPE_LDO;//点类型

                            pointParam.setPointType(ParampointType);                                                    //下发点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                        //点位ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setModuleID(Integer.parseInt(String.valueOf(point.getModuleId())));              //模块ID
                            pointParam.setChannelIndex(Integer.parseInt(String.valueOf(point.getChannelIndex())));      //通道索引
                            pointParam.setWorkMode(Integer.parseInt(String.valueOf(point.getWorkMode())));              //工作模式
                            pointParam.setPolarity(Integer.parseInt(String.valueOf(point.getReversed())));              //是否反向
                            pointParam.setInitValue(initVal);                                                           //初始值
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setAlarmTrigger(point.getCloseState());                                          //闭合状态

                        } else if (DeviceTreeConstants.BES_AO == Integer.parseInt(point.getNodeType())) {//修改AO

                            Integer ParampointType = PointType.POINT_TYPE_LAO;//点类型

                            pointParam.setPointType(ParampointType);                                                    //下发点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                        //点位ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setModuleID(Integer.parseInt(String.valueOf(point.getModuleId())));              //模块ID
                            pointParam.setChannelIndex(Integer.parseInt(String.valueOf(point.getChannelIndex())));      //通道索引
                            pointParam.setWorkMode(Integer.parseInt(String.valueOf(point.getWorkMode())));              //工作模式
                            pointParam.setPolarity(Integer.parseInt(String.valueOf(point.getReversed())));              //是否反向
                            pointParam.setInitValue(initVal);                                                           //初始值
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setLineType(Integer.parseInt(String.valueOf(point.getSinnalType())));            //信号类型
                            pointParam.setUnit(point.getEngineerUnit());                                                //单位
                            pointParam.setHighRange(highRangeVal);                                                      //最大值
                            pointParam.setLowRange(lowRangeVal);                                                        //最小值
                            pointParam.setPrecision(Integer.parseInt(String.valueOf(point.getAccuracy())));             //精度
                            if (point.getHighLimit() == null || point.getLowLimit() == null) {
                                pointParam.setAlarmHighValue(0);                                                     //高限报警
                                pointParam.setAlarmLowValue(0);                                                      //低限报警
                            } else {
                                pointParam.setAlarmHighValue(Integer.parseInt(String.valueOf(point.getHighLimit())));       //高限报警
                                pointParam.setAlarmLowValue(Integer.parseInt(String.valueOf(point.getLowLimit())));         //低限报警
                            }

                        }

                        // 同步数据到下位机
                        if (isInsertPoint) {
                            boolean sendState = SendMsgHandler.addPointDDC(con.getIp(), pointParam);
                            if (!sendState) {
                                return AjaxResult.success("新增点位成功，点位下发信息失败", point);
                            }
                            // 添加订阅消息
                            MsgSubPubHandler.addSubMsg(DDCCmd.POINT_ADD, con.getIp());
                        } else {
                            boolean sendState = SendMsgHandler.setPointDDC(con.getIp(), pointParam);
                            if (!sendState) {
                                return AjaxResult.success("修改点位成功，点位下发信息失败", point);
                            }
                            // 添加订阅消息
                            MsgSubPubHandler.addSubMsg(DDCCmd.POINT_PARAM_SET, con.getIp());
                        }
                    } else {//照明
                        PointParamLDC pointParam = new PointParamLDC();

                        if (DeviceTreeConstants.BES_VPOINT == Integer.parseInt(point.getNodeType())) {//修改虚点

                            Integer pointType = 0;
                            if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))) {
                                pointType = PointType.POINT_TYPE_VIRTUAL_AI;
                            } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAO))) {
                                pointType = PointType.POINT_TYPE_VIRTUAL_AO;
                            } else if (point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VDI))) {
                                pointType = PointType.POINT_TYPE_VIRTUAL_DI;
                            } else {
                                pointType = PointType.POINT_TYPE_VIRTUAL_DO;
                            }

                            pointParam.setPointType(pointType);                                                         //树点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                 //ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setInitValue(initVal);                                                           //初始值
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setAlarmTrigger(point.getCloseState());                                          //闭合状态
                            if (pointType == PointType.POINT_TYPE_VIRTUAL_AI || pointType == PointType.POINT_TYPE_VIRTUAL_AO) {
                                pointParam.setUnit(point.getEngineerUnit());                                                //单位
                                pointParam.setPrecision(Integer.parseInt(String.valueOf(point.getAccuracy())));             //精度
                            }


                            if (pointType == PointType.POINT_TYPE_VIRTUAL_AI || pointType == PointType.POINT_TYPE_VIRTUAL_AO) {//VAI VAO 类型
                                pointParam.setAlarmHighValue(0);
                                pointParam.setAlarmLowValue(0);
                            }
                        } else if (DeviceTreeConstants.BES_AI == Integer.parseInt(point.getNodeType())) {//修改AI

                            Integer ParampointType = PointType.POINT_TYPE_LAI;//点类型

                            pointParam.setPointType(ParampointType);                                                    //下发点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                 //点位ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setModuleID(Integer.parseInt(String.valueOf(point.getModuleId())));              //模块ID
                            pointParam.setChannelIndex(Integer.parseInt(String.valueOf(point.getChannelIndex())));      //通道索引
                            pointParam.setWorkMode(Integer.parseInt(String.valueOf(point.getWorkMode())));              //工作模式
                            pointParam.setPolarity(Integer.parseInt(String.valueOf(point.getReversed())));              //是否反向
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setLineType(Integer.parseInt(String.valueOf(point.getSinnalType())));            //信号类型
                            pointParam.setUnit(point.getEngineerUnit());                                                //单位
                            pointParam.setHighRange(highRangeVal);                                                      //最大值
                            pointParam.setLowRange(lowRangeVal);                                                        //最小值
                            pointParam.setPrecision(Integer.parseInt(String.valueOf(point.getAccuracy())));             //精度
                            if (point.getHighLimit() == null || point.getLowLimit() == null) {
                                pointParam.setAlarmHighValue(null);                                                     //高限报警
                                pointParam.setAlarmLowValue(null);                                                      //低限报警
                            } else {
                                pointParam.setAlarmHighValue(Integer.parseInt(String.valueOf(point.getHighLimit())));       //高限报警
                                pointParam.setAlarmLowValue(Integer.parseInt(String.valueOf(point.getLowLimit())));         //低限报警
                            }
                            pointParam.setInitValue(initVal);

                        } else if (DeviceTreeConstants.BES_DI == Integer.parseInt(point.getNodeType())) {//修改DI

                            Integer ParampointType = PointType.POINT_TYPE_LDI;//点类型

                            pointParam.setPointType(ParampointType);                                                    //下发点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                 //点位ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setModuleID(Integer.parseInt(String.valueOf(point.getModuleId())));              //模块ID
                            pointParam.setChannelIndex(Integer.parseInt(String.valueOf(point.getChannelIndex())));      //通道索引
                            pointParam.setWorkMode(Integer.parseInt(String.valueOf(point.getWorkMode())));              //工作模式
                            pointParam.setPolarity(Integer.parseInt(String.valueOf(point.getReversed())));              //是否反向
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setAlarmTrigger(point.getCloseState());                                          //闭合状态
                            pointParam.setActivePassive(Integer.parseInt(String.valueOf(point.getSourced())));          //是否有源
                            pointParam.setInitValue(initVal);

                        } else if (DeviceTreeConstants.BES_DO == Integer.parseInt(point.getNodeType())) {//修改DO

                            Integer ParampointType = PointType.POINT_TYPE_LDO;//点类型

                            pointParam.setPointType(ParampointType);                                                    //下发点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                        //点位ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setModuleID(Integer.parseInt(String.valueOf(point.getModuleId())));              //模块ID
                            pointParam.setChannelIndex(Integer.parseInt(String.valueOf(point.getChannelIndex())));      //通道索引
                            pointParam.setWorkMode(Integer.parseInt(String.valueOf(point.getWorkMode())));              //工作模式
                            pointParam.setPolarity(Integer.parseInt(String.valueOf(point.getReversed())));              //是否反向
                            pointParam.setInitValue(initVal);                                                           //初始值
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setAlarmTrigger(point.getCloseState());                                          //闭合状态

                        } else if (DeviceTreeConstants.BES_AO == Integer.parseInt(point.getNodeType())) {//修改AO

                            Integer ParampointType = PointType.POINT_TYPE_LAO;//点类型

                            pointParam.setPointType(ParampointType);                                                    //下发点类型
                            pointParam.setId(Integer.parseInt(String.valueOf(point.getEquipmentId())));                        //点位ID
                            pointParam.setActive(point.getEnabled());                                                   //使能
                            pointParam.setName(point.getSysName());                                                     //系统名称
                            pointParam.setAlias(point.getNickName());                                                   //别名
                            pointParam.setDescription(point.getDescription());                                          //描述
                            pointParam.setModuleID(Integer.parseInt(String.valueOf(point.getModuleId())));              //模块ID
                            pointParam.setChannelIndex(Integer.parseInt(String.valueOf(point.getChannelIndex())));      //通道索引
                            pointParam.setWorkMode(Integer.parseInt(String.valueOf(point.getWorkMode())));              //工作模式
                            pointParam.setPolarity(Integer.parseInt(String.valueOf(point.getReversed())));              //是否反向
                            pointParam.setInitValue(initVal);                                                           //初始值
                            pointParam.setAlarmActive(point.getAlarmEnable());                                          //报警使能
                            pointParam.setAlarmType(point.getAlarmType());                                              //报警类型
                            pointParam.setAlarmPriority(point.getAlarmPriority());                                      //报警优先级
                            pointParam.setLineType(Integer.parseInt(String.valueOf(point.getSinnalType())));            //信号类型
                            pointParam.setUnit(point.getEngineerUnit());                                                //单位
                            pointParam.setHighRange(highRangeVal);                                                      //最大值
                            pointParam.setLowRange(lowRangeVal);                                                        //最小值
                            pointParam.setPrecision(Integer.parseInt(String.valueOf(point.getAccuracy())));             //精度
                            if (point.getHighLimit() == null || point.getLowLimit() == null) {
                                pointParam.setAlarmHighValue(null);                                                     //高限报警
                                pointParam.setAlarmLowValue(null);                                                      //低限报警
                            } else {
                                pointParam.setAlarmHighValue(Integer.parseInt(String.valueOf(point.getHighLimit())));       //高限报警
                                pointParam.setAlarmLowValue(Integer.parseInt(String.valueOf(point.getLowLimit())));         //低限报警
                            }

                        }

                        // 同步数据到下位机
                        if (isInsertPoint) {
                            boolean sendState = SendMsgHandler.addPointLDC(con.getIp(), pointParam);
                            if (!sendState) {
                                return AjaxResult.success("新增点位成功，点位下发信息失败", point);
                            }
                            // 添加订阅消息
                            MsgSubPubHandler.addSubMsg(LDCCmd.POINT_ADD, con.getIp());
                        } else {
                            boolean sendState = SendMsgHandler.setPointLDC(con.getIp(), pointParam);
                            if (!sendState) {
                                return AjaxResult.success("修改点位成功，点位下发信息失败", point);
                            }
                            // 添加订阅消息
                            MsgSubPubHandler.addSubMsg(LDCCmd.POINT_PARAM_SET, con.getIp());
                        }
                    }
                    return AjaxResult.success("下发成功！", point);
                }
            } else {
                throw new Exception();
            }
            return AjaxResult.error("修改失败");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("修改失败");
        }
    }


    /**
     * @description:给照明添加子节点
     * @author: sunshangeng
     * @date: 2022/9/26 17:48
     * @param: [buildNode]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    @Transactional
    public AjaxResult insertLightingTree(BuildNode buildNode) {
        if (buildNode.getNodeType() == null//树节点类型
                || buildNode.getNickName() == null//别名
                || buildNode.getSysName() == null//系统名
                || buildNode.getPortNum() == null//通信地址
                || org.apache.commons.lang3.StringUtils.isBlank(buildNode.getFatherId())//设备树id不能为空
        ) {
            return AjaxResult.error("请完善输入信息");
        }
        /**添加树节点*/
        DeviceTree deviceTreeP = new DeviceTree();
        //获取父节点的树数据
        DeviceTree deviceTreeFatehr = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(buildNode.getFatherId()));
        deviceTreeP.setSysName(buildNode.getSysName());
        deviceTreeP.setDeviceNodeId(buildNode.getNodeType());
        deviceTreeP.setDeviceTreeFatherId(Integer.parseInt(buildNode.getFatherId()));
        deviceTreeP.setDeviceType(DeviceTreeConstants.BES_ILLUMINE);
        deviceTreeP.setCreateTime(DateUtils.getNowDate());
        deviceTreeP.setPark(deviceTreeFatehr.getPark());

        /**qindehua 系统名称不允许重复*/
        Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree).values();
        for (Object obj : collection) {
            DeviceTree deviceTree = (DeviceTree) obj;
            if (deviceTree.getRedisSysName() != null) {
                if (deviceTree.getRedisSysName().equals(buildNode.getSysName())) {
                    return AjaxResult.error("系统名称不允许重复!");
                }
            }
        }
        //获取同级节点数据
        List<DeviceTree> list = deviceTreeCache.getCascadeSubordinate(deviceTreeFatehr.getDeviceTreeId());
        if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_TRUNK)) {
            //新增干线  判断当前控制器下通信地址是否重复
            for (DeviceTree deviceTree : list) {

                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_TRUNK) {
                    BuildNode trunkNode = deviceTreeCache.getNodeByTrunkTreeId((long) deviceTree.getDeviceTreeId());
                    if (trunkNode.getPortNum().equals(buildNode.getPortNum())) {
                        return AjaxResult.error("通信地址不允许重复!");
                    }
                }

            }
        } else if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_BRANCH)) {
            //新增支线 判断当前控制器下通信地址是否重复
            for (DeviceTree deviceTree : list) {
                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_BRANCH) {
                    BuildNode branchNode = deviceTreeCache.getNodeByBranchTreeId((long) deviceTree.getDeviceTreeId());
                    if (branchNode.getPortNum().equals(buildNode.getPortNum())) {
                        return AjaxResult.error("通信地址不允许重复!");
                    }
                }
            }
        } else {
            throw new ServiceException("数据参数不正确！");
        }
        boolean isInsertLightingTree = pointMapper.insertDeviceTreee(deviceTreeP);
        if (!isInsertLightingTree) {
            throw new ServiceException("未成功添加树节点");
        }
        DeviceTree deviceTreeR = pointMapper.selectDeviceTreeeByDeviceTreee(deviceTreeP);
        //添加至表
        buildNode.setTreeId(deviceTreeP.getDeviceTreeId());
        boolean isInsertBuildNode = pointMapper.insertBuildNode(buildNode);

        if (isInsertBuildNode) {
            if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_TRUNK)) {
                //存入干线节点缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_TrunkLine, (long) buildNode.getTreeId(), buildNode);
            } else if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_BRANCH)) {
                //存入支线节点缓
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_BranchLine, (long) buildNode.getTreeId(), buildNode);
            } else {

                throw new ServiceException("数据参数不正确！");
            }
            deviceTreeR.setDeviceTreeStatus(deviceTreeFatehr.getDeviceTreeStatus());
            /**存入树缓存*/
            deviceTreeR.setSysName(buildNode.getNickName());
            deviceTreeR.setRedisSysName(buildNode.getSysName());
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeP.getDeviceTreeId(), deviceTreeR);
//            deviceTreeR.setSysName(buildNode.getNickName());

            /**处理缓存集合*/
//            moduleService.updateController(buildNode.getFatherId(),buildNode.getNodeType());
            return AjaxResult.success("创建成功！", deviceTreeR);


        } else {
            throw new ServiceException("未成功添加树节点");
        }
    }


    /**
     * @description:照明修改节点
     * @author: sunshangeng
     * @date: 2022/9/26 17:48
     * @param: [buildNode]
     **/
    @Override
    @Transactional
    public AjaxResult updateLightingTree(BuildNode buildNode) {
        if (buildNode.getTreeId()==null
                || buildNode.getNodeType() == null//树节点类型
                || buildNode.getNickName() == null//别名
                || buildNode.getSysName() == null//系统名
                || buildNode.getPortNum() == null//通信地址
                || org.apache.commons.lang3.StringUtils.isBlank(buildNode.getFatherId())//设备树id不能为空
        ) {
            return AjaxResult.error("请完善输入信息");
        }
        //获取父节点的树数据
        DeviceTree deviceTreeData = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) buildNode.getTreeId());
        if (deviceTreeData == null) {
            return AjaxResult.error("修改对象不存在!");
        }
        //获取同级节点数据
        List<DeviceTree> list = deviceTreeCache.getCascadeSubordinate(deviceTreeData.getDeviceTreeFatherId());
        //获取子节点数据
        List<DeviceTree> listSun = deviceTreeCache.getCascadeSubordinate(buildNode.getTreeId());

        if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_TRUNK)) {
            BuildNode trunkData = deviceTreeCache.getNodeByTrunkTreeId((long) buildNode.getTreeId());
            if (listSun.size() > 1 && !trunkData.getPortNum().equals(buildNode.getPortNum())) {
                return AjaxResult.error("该节点下面已有子节点，不允许修改通信地址!");
            }
            //新增干线  判断当前控制器下通信地址是否重复
            for (DeviceTree deviceTree : list) {
                //过滤掉当前节点
                if (deviceTree.getDeviceTreeId() == buildNode.getTreeId()) {
                    continue;
                }
                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_TRUNK) {
                    BuildNode trunkNode = deviceTreeCache.getNodeByTrunkTreeId((long) deviceTree.getDeviceTreeId());
                    if (trunkNode.getPortNum().equals(buildNode.getPortNum())) {
                        return AjaxResult.error("通信地址不允许重复!");
                    }
                }
            }
        } else if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_BRANCH)) {
            BuildNode branchData = deviceTreeCache.getNodeByBranchTreeId((long) buildNode.getTreeId());
            if (listSun.size() > 1 && !branchData.getPortNum().equals(buildNode.getPortNum())) {
                return AjaxResult.error("该节点下面已有子节点，不允许修改通信地址!");
            }
            //新增支线 判断当前控制器下通信地址是否重复
            for (DeviceTree deviceTree : list) {
                //过滤掉当前节点
                if (deviceTree.getDeviceTreeId() == buildNode.getTreeId()) {
                    continue;
                }
                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_BRANCH) {
                    BuildNode branchNode = deviceTreeCache.getNodeByBranchTreeId((long) deviceTree.getDeviceTreeId());
                    if (branchNode.getPortNum().equals(buildNode.getPortNum())) {
                        return AjaxResult.error("通信地址不允许重复!");
                    }
                }
            }
        } else {
            throw new ServiceException("数据参数不正确！");
        }

        boolean isUpdateBuildNode = pointMapper.updateBuildNodeByLighting(buildNode);
        if (isUpdateBuildNode) {
            if (buildNode.getNodeType().equals(DeviceTreeConstants.BES_TRUNK)) {
                BuildNode trunkline = deviceTreeCache.getNodeByTrunkTreeId((long) buildNode.getTreeId());
                trunkline.setNickName(buildNode.getNickName());
                trunkline.setPortNum(buildNode.getPortNum());
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_TrunkLine, (long) buildNode.getTreeId(), trunkline);
                /**修改支线通讯地址*/
                List<String> subNodelist = devicetreeMappper.getSubNode(buildNode.getTreeId().toString());
                for (int i = 0; i < subNodelist.size(); i++) {
                    BuildNode subnode = deviceTreeCache.getNodeByBranchTreeId(Long.parseLong(subNodelist.get(i)));
                    if (subnode != null) {
                        /**修改子节点并修改缓存*/
                        subnode.setPortNum(buildNode.getPortNum() + subnode.getPortNum().substring(subnode.getPortNum().indexOf(".") + 1));
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_BranchLine, subnode.getTreeId().longValue(), subnode);
                        boolean isUpdatesubBuildNode = pointMapper.updateBuildNodeByLighting(subnode);
                        if (!isUpdatesubBuildNode) {
                            throw new ServiceException("修改失败！");
                        }
                    }
                }
            } else {
                BuildNode branchLine = deviceTreeCache.getNodeByBranchTreeId((long) buildNode.getTreeId());
                branchLine.setNickName(buildNode.getNickName());
                branchLine.setPortNum(buildNode.getPortNum());
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_BranchLine, (long) buildNode.getTreeId(), branchLine);
            }

            /**存入树缓存*/
            DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) buildNode.getTreeId());
            deviceTree.setSysName(buildNode.getNickName());
            deviceTree.setRedisSysName(buildNode.getSysName());
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) buildNode.getTreeId(), deviceTree);

            return AjaxResult.success("修改成功！", deviceTree);
        } else {
            throw new ServiceException("修改失败！");
//            return AjaxResult.success("修改失败！");
        }
    }


    /**
     * @param pointControlCommand
     * @return AjaxResult
     * @Description: 调试一个逻辑点的初始值值
     * @auther: gaojikun
     */
    @Override
    public AjaxResult debugPointInfo(PointControlCommand pointControlCommand) {

        if (pointControlCommand.getId() == null || pointControlCommand.getValue() == null || pointControlCommand.getWorkMode() == null) {
            return AjaxResult.error("参数错误");
        }

        Controller controllerPoint = null;

        Integer treeId = pointControlCommand.getId();                   //树ID
        String id = null;                                               //设备id
        Double value = pointControlCommand.getValue();                  //初始值
        Integer workMode = pointControlCommand.getWorkMode();           //工作模式
        Double accuracy;


        Point pointMap = moduleAndPointCache.getPointByDeviceId((long) treeId);
        if (pointMap == null || pointMap.getEquipmentId() == null ||
                pointMap.getControllerId() == 0 || pointMap.getNodeType() == null ||
                pointMap.getSysName() == null || pointMap.getWorkMode() == null) {
            return AjaxResult.error("参数错误,点位缓存未获取到点位数据");
        }

        PointDebugLog pointDebugLog = new PointDebugLog();
        pointDebugLog.setCreateTime(DateUtils.getNowDate());
        pointDebugLog.setDeviceTreeId(pointControlCommand.getId());
        pointDebugLog.setSysName(pointMap.getSysName());
        pointDebugLog.setOperatValue(value.toString());
        if (SecurityUtils.getUsername() == null) {
            return AjaxResult.error("未获取到操作人信息");
        }
        pointDebugLog.setCreateName(SecurityUtils.getUsername());

        //查询该逻辑点所在的控制器的ip
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller).values();
        for (Object obj : values) {
            Controller con = (Controller) obj;
            if (con.getId() == pointMap.getControllerId()) {
                controllerPoint = con;
            }
        }
//        controllerPoint = controllerCache.getDdcByChannelId(String.valueOf(pointMap.getControllerId()));

        if (controllerPoint == null || controllerPoint.getIp() == null) {
            return AjaxResult.error("参数错误,控制器缓存未获取到控制器数据");
        }

//        if (workMode == null) {
//            workMode = Integer.parseInt(String.valueOf(pointMap.getWorkMode()));
//        }

        id = String.valueOf(pointMap.getEquipmentId());
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
            return AjaxResult.error("请输入值");
        }
        if (!StringUtils.hasText(channelId) || controllerPoint.getType() == 0) {
            return AjaxResult.error("参数错误,控制器缓存未获取到控制器ip和类型");
        }
		/*if ("0".equals(f_work_mode)){//自动状态,更改数据库的当前模式为手动

			besSbdyMapper.updatePointByWorkMode(tabName,updateWorkMode,id);
		}*/
        if (Integer.parseInt(nodeType) == DeviceTreeConstants.BES_VPOINT) {
            workMode = 0;
        }

        if (controllerPoint.getType() == DeviceTreeConstants.BES_DDCNODE) {//楼控

            PointDataDDC pointData = new PointDataDDC();
            pointData.setId(Integer.valueOf(id));
            pointData.setValue(v);
            pointData.setWorkMode(workMode);
            boolean sendState = SendMsgHandler.setPointValueDDC(channelId, pointData);

            if (!sendState) {
                // 存储操作记录
                pointDebugLog.setDebugState("0");
                pointMapper.addDebugLog(pointDebugLog);
                return AjaxResult.error("下发失败");
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
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) treeId, pointMap);
            }
            return AjaxResult.success("下发成功");

        } else if (controllerPoint.getType() == DeviceTreeConstants.BES_ILLUMINE) {//照明

            PointDataLDC pointDataLDC = new PointDataLDC();
            pointDataLDC.setId(Integer.valueOf(id));
            pointDataLDC.setValue(v);
            pointDataLDC.setWorkMode(workMode);
            boolean sendState = SendMsgHandler.setPointValueLDC(channelId, pointDataLDC);

            if (!sendState) {
                // 存储操作记录
                pointDebugLog.setDebugState("0");
                pointMapper.addDebugLog(pointDebugLog);
                return AjaxResult.error("下发失败");
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
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) treeId, pointMap);
            }
            return AjaxResult.success("下发成功");
        }

        // 存储操作记录
        pointDebugLog.setDebugState("0");
        pointMapper.addDebugLog(pointDebugLog);
        return AjaxResult.error("下发失败");
    }


    /**
     * 点值配置
     *
     * @param nodeConfigSets
     * @return AjaxResult
     * @author: gaojikun
     */
    @Override
    public AjaxResult debuggerEditPointValue(NodeConfigSet nodeConfigSets) {
        if (!StringUtils.hasText(nodeConfigSets.getList())){
            return AjaxResult.error("参数错误！");
        }
        String[] strlist = nodeConfigSets.getList().split("-");

        List<NodeConfigSet> objList = new ArrayList<>();

        for (String str : strlist) {
            NodeConfigSet addNode = JSONObject.parseObject(str, NodeConfigSet.class);
            objList.add(addNode);
        }

        if (objList.size() > 0) {
            //先删除所有配置
            pointMapper.deletePointSettingByName(objList.get(0));
            //查缓存
            List<NodeConfigSet> delList = new ArrayList();
            Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config).values();
            for (Object obj : collection) {
                NodeConfigSet nodeConfig = (NodeConfigSet) obj;
                if (nodeConfig.getSysName().equals(objList.get(0).getSysName())) {
                    delList.add(nodeConfig);
                    continue;
                }
            }
            delList.forEach(val -> {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config, (long) val.getId());
            });

            for (NodeConfigSet nodeConfigSet : objList) {
                //添加
                boolean isInsertNodeConfigSet = pointMapper.insertNodeConfigSet(nodeConfigSet);
                if (isInsertNodeConfigSet) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config, (long) nodeConfigSet.getId(), nodeConfigSet);
                    continue;
                } else {
                    return AjaxResult.error("配置失败");
                }
            }
        }

        return AjaxResult.success("配置成功");

    }


    /**
     * 查询点值配置
     *
     * @param nodeConfigSets
     * @return AjaxResult
     * @author: gaojikun
     */
    @Override
    public AjaxResult selectEditPointValue(NodeConfigSet nodeConfigSets) {
        List<NodeConfigSet> nodeConfigSetList = new ArrayList<>();
        Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point_Config).values();
        for (Object obj : collection) {
            NodeConfigSet nodeConfig = (NodeConfigSet) obj;
            if (nodeConfig.getSysName().equals(nodeConfigSets.getSysName())) {
                nodeConfigSetList.add(nodeConfig);
                continue;
            }
        }
        nodeConfigSetList.sort((o1, o2) -> String.valueOf(o1.getId()).compareTo(String.valueOf(o2.getId())));
        return AjaxResult.success(nodeConfigSetList);
    }

    /**
     * @param list
     * @Description: 批量下发点位值
     * @auther: wanghongjie
     * @date: 17:28 2022/11/3
     * @param: [list]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult debugPointListInfo(List<PointControlCommand> list) throws NoSuchAlgorithmException {

        if (list == null ) {//|| list.size() == 0
            return AjaxResult.error("参数错误");
        }

        for (PointControlCommand pointControlCommand : list) {
            if (pointControlCommand.getPointType() == 0) {//bes

                debugPointInfo(pointControlCommand);

            } else if (pointControlCommand.getPointType() == 1) {//第三方协议

                Integer id = pointControlCommand.getId();//功能id
                Integer equipmentId = pointControlCommand.getEquipmentId();//设备id

                if (id == null || equipmentId == null) {

                    return AjaxResult.error("参数错误");
                }

                //获取设备所属的控制器的ip以及端口
                Map<String, Equipment> controllerList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

                if (controllerList == null || controllerList.size() == 0) {
                    return AjaxResult.error("参数错误");
                }

                Equipment equipments = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,(long) equipmentId);

                for (Equipment equipment : controllerList.values()) {
                    if (equipment.getId().equals(equipments.getpId())) {
                        String ip = equipment.getIpAddress();
                        Integer host = Integer.valueOf(equipment.getPortNum());

                        List<PointControlCommand> valueList = pointControlCommand.getValueList();

                        // 线程开启
                        ThreadPool.executor.execute(() -> {
                            try {

                                modbusSendSyncMsgHandler.issued1(ip, host, equipments,null, (long) id, valueList);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                    }
                }
            }
        }
        return AjaxResult.success("下发成功");
    }


    /**
     *
     * @Description: 第三方协议下发获取实时值
     *
     * @auther: wanghongjie
     * @date: 17:34 2023/4/10
     * @param: [pointControlCommand]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    @Override
    public AjaxResult debugPointRealTimeInfo(PointControlCommand pointControlCommand) throws NoSuchAlgorithmException {




        if (pointControlCommand.getPointType() == 0) {//bes

            debugPointInfo(pointControlCommand);

        } else if (pointControlCommand.getPointType() == 1) {//第三方协议

            Integer id = pointControlCommand.getId();//功能id
            Integer equipmentId = pointControlCommand.getEquipmentId();//设备id

            if (id == null || equipmentId == null) {

                return AjaxResult.error("参数错误");
            }

            //获取设备所属的控制器的ip以及端口
            Map<String, Equipment> controllerList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

            if (controllerList == null || controllerList.size() == 0) {
                return AjaxResult.error("参数错误");
            }

            Equipment equipments = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,(long) equipmentId);

            //获取设备所有的功能
            ProductFunction productFunction = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, (long) id);

            String issuedType = productFunction.getIssuedType();//下发方式;0-指令下发;1-数据项下发

            if (com.ruoyi.common.utils.StringUtils.isEmpty(issuedType)) {
                return AjaxResult.error("参数错误");
            }

            for (Equipment equipment : controllerList.values()) {
                if (equipment.getId().equals(equipments.getpId())) {
                    String ip = equipment.getIpAddress();
                    Integer host = Integer.valueOf(equipment.getPortNum());

                    // 线程开启
                    ThreadPool.executor.execute(() -> {
                        try {


                            modbusSendSyncMsgHandler.issued1(ip, host, equipments,3, (long) id,null);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                }
            }
        }
        return AjaxResult.success("下发成功");
    }

    /**
     *
     * @Description: 设计器批量获取实时值
     *
     * @auther: wanghongjie
     * @date: 14:30 2023/5/29
     * @param: [list]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    @Override
    public AjaxResult debugPointListRealTimeInfo(List<RealTimeDataParam> list) {
        if (list == null || list.size() == 0) {
            return AjaxResult.error("参数为空!");
        }

        Map<Long,List<RealTimeDataParam>> mapList = new HashMap<>();

        for (RealTimeDataParam realTimeDataParam : list) {

            Long functionId = realTimeDataParam.getId();//点位或者功能id

            Long equipmentId = realTimeDataParam.getEquipmentId();//设备id

            String pointType = realTimeDataParam.getPointType();//"0" bes设备树  "1"第三方协议

            if (pointType == null) {
                return null;
            }

            if (pointType.equals("1")) {//第三方功能


                if (mapList.size() == 0) {
                    List<RealTimeDataParam> realTimeDataParamList = new ArrayList<>();
                    realTimeDataParamList.add(realTimeDataParam);
                    mapList.put(equipmentId,realTimeDataParamList);
                    continue;
                }

                Boolean boo = false;
                for (int i = 0; i < mapList.size(); i++) {
                    if (mapList.containsKey(equipmentId)) {
                        boo = true;
                        mapList.get(equipmentId).add(realTimeDataParam);
                    }
                }

                if (!boo) {
                    List<RealTimeDataParam> realTimeDataParamList = new ArrayList<>();
                    realTimeDataParamList.add(realTimeDataParam);
                    mapList.put(equipmentId,realTimeDataParamList);
                }
            }
        }
        //获取设备所属的控制器的ip以及端口
        Map<String, Equipment> controllerList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

        Iterator<Long> iter = mapList.keySet().iterator();
        while (iter.hasNext()) {
            Long key = iter.next();

            if (controllerList == null || controllerList.size() == 0) {
                return AjaxResult.error("参数错误");
            }

            Equipment equipments = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, key);

            for (Equipment equipment : controllerList.values()) {
                if (equipment.getId().equals(equipments.getpId())) {
                    String ip = equipment.getIpAddress();
                    Integer host = Integer.valueOf(equipment.getPortNum());

                    // 线程开启
                    ThreadPool.executor.execute(() -> {
                        try {


                            modbusSendSyncMsgHandler.issued2(ip, host, equipments, 3, 0, 60, false, null);
                            modbusSendSyncMsgHandler.issued2(ip, host, equipments, 3, 4096, 10, false, null);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
       /* Iterator<Long> iter = mapList.keySet().iterator();
        while (iter.hasNext()) {
            Long key = iter.next();
            List<RealTimeDataParam> realTimeDataParamList = mapList.get(key);

            realTimeDataParamList.stream()
                    .sorted(Comparator.comparing(RealTimeDataParam::getId))
                    .collect(Collectors.toList());

            List<Long> longList = new ArrayList<>();

            realTimeDataParamList.forEach(val -> {
                Long functionId = val.getId();

                //查询功能绑定的数据项读写类型
                ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, functionId);
                if (productItemData == null) {
                    return;
                }
                Long sortNum = productItemData.getSortNum();
                longList.add(sortNum);
            });

            if (longList.size() == 0) {
                return AjaxResult.error();
            }
            List<Long> beginSortNumList = new ArrayList<>();
            beginSortNumList.add(longList.get(0));

            List<Long> endSortNumList = new ArrayList<>();

            for (int i = 0; i < longList.size(); i++) {
                if ((longList.get(i) - beginSortNumList.get(beginSortNumList.size() - 1)) > 60) {
                    beginSortNumList.add(longList.get(i));
                    endSortNumList.add(longList.get(i));
                }

                if (i == longList.size() - 1) {
                    endSortNumList.add(longList.get(i));
                }
            }

            beginSortNumList.forEach(v -> {

            });
        }*/

        return null;
    }
}
