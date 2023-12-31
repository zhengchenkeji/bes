package com.zc.efounder.JEnterprise.service.deviceTree.impl;

import com.zc.ApplicationContextProvider;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.zc.efounder.JEnterprise.Cache.ControllerCache;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ControllerMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.DeviceTreeMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ModuleMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.PointMapper;
import com.zc.efounder.JEnterprise.service.deviceTree.ModuleService;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.constant.LDCCmd;
import com.zc.connect.business.constant.PointType;
import com.zc.connect.business.dto.ddc.ModuleParamDDC;
import com.zc.connect.business.dto.ddc.PointParamDDC;
import com.zc.connect.business.dto.ldc.ModuleParamLDC;
import com.zc.connect.business.dto.ldc.PointParamLDC;
import com.zc.connect.business.handler.SendMsgHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 模块Service业务层处理
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
@Service
public class ModuleServiceImpl implements ModuleService {

    // 设备树缓存定义
    private DeviceTreeCache deviceTreeCache = ApplicationContextProvider.getBean(DeviceTreeCache.class);
    //模块缓存定义
    private ModuleAndPointCache moduleAndPointCache = ApplicationContextProvider.getBean(ModuleAndPointCache.class);
    // 采集器缓存定义
    private ControllerCache controllerCache = ApplicationContextProvider.getBean(ControllerCache.class);
    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PointMapper pointMapper;

    @Resource
    private ControllerMapper controllerMapper;

    @Resource
    private DeviceTreeMapper deviceTreeMapper;

    @Resource
    private RedisCache redisCache;


    @PostConstruct
    public void init() {
        /**
         * 添加数据到 redis 缓存
         */
        addModuleCache();
    }

    /**
     * 添加数据到 redis 缓存
     *
     * @auther:gaojikun
     */
    @Override
    public void addModuleCache() {
        // 获取全部模块列表数据
        List<Module> modules = moduleMapper.selectModuleList(null);

        // 清楚 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_Module);

        if (modules == null || modules.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        modules.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, val.getDeviceTreeId(), val);
        });
    }

    /**
     * 查询模块
     *
     * @param id 模块主键
     * @return 模块
     * @auther:gaojikun
     */
    @Override
    public Module selectModuleById(Long id) {
        return moduleMapper.selectModuleById(id);
    }

    /**
     * 查询模块列表
     *
     * @param module 模块
     * @return 模块
     * @auther:gaojikun
     */
    @Override
    public List<Module> selectModuleList(Module module) {
        return moduleMapper.selectModuleList(module);
    }

    /**
     * 新增模块
     *
     * @param module 模块
     * @return 结果
     * @auther:gaojikun
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult insertModule(Module module) {

        module.setCreateTime(DateUtils.getNowDate());           //创建时间
        module.setSynchState((long) 0);
        module.setOnlineState((long) 0);

        String alias = module.getAlias();                       //别名
        String description = module.getDescription();           //描述
        String installAddress = module.getInstallAddress();     //安装地址
        String nodeType = module.getNodeType();                 //模块树节点类型
        String sysName = module.getSysName();                   //系统名称
        String flnId = module.getFatherId();                    //线路树id
        Long moduleTypeId = module.getModuleTypeId();           //所属模块型号ID
        Long controllerId = module.getControllerId();           //控制器ID
        Long type = module.getType();                           //模块类型
        Long slaveAddress = module.getSlaveAddress();           //通讯地址
        Long active = module.getActive();                       //使能
        Long synchState = module.getSynchState();               //同步状态
        Long onlineState = module.getOnlineState();             //在线状态

        if (!StringUtils.hasText(alias)
                || !StringUtils.hasText(description)
                || !StringUtils.hasText(installAddress)
                || !StringUtils.hasText(nodeType)
                || !StringUtils.hasText(sysName)
                || !StringUtils.hasText(flnId)
                || moduleTypeId == null
                || controllerId == null
                || type == null
                || slaveAddress == null
                || active == null
                || synchState == null
                || onlineState == null
        ) {
            return AjaxResult.error("请完善输入信息");
        }

        //名称查重
        Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree).values();
        for (Object obj : collection) {
            DeviceTree deviceTree = (DeviceTree) obj;
            if (deviceTree.getRedisSysName() == null) {
                continue;
            } else {
                if (deviceTree.getRedisSysName().equals(module.getSysName())) {
                    return AjaxResult.error("系统名称不允许重复!");
                }
            }
        }
        Controller con = null;
        List<Controller> controllers = new ArrayList<>();

        /***
         * @description: 判断是否是照明相关节点并获取控制器id
         * @author: sunshangeng
         **/
        if (module.getType() == 0) {//ddc模块
            recursive(Long.parseLong(module.getFatherId()), controllers);
            con = controllers.get(0);
            //添加线路ID
            module.setFlnId(Long.parseLong(flnId));
        } else {//照明模块
            /**查找控制器相关如果查询到就退出 查询不到一直查询*/
            String fatherid = module.getFatherId();
            while (true) {
                DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(fatherid));

                /**增加线路id*/
                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_TRUNK) {
                    module.setTrunkCode((long) (deviceTree.getDeviceTreeId()));
                }
                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_BRANCH) {
                    module.setBranchCode((long) deviceTree.getDeviceTreeId());
                }
                /**查找到控制器*/
                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_ILLUMINE) {
                    con = controllerCache.getControllerByDeviceTreeId((long) deviceTree.getDeviceTreeId());
                    break;
                } else {
                    fatherid = deviceTree.getDeviceTreeFatherId() + "";
                }
                /**判断父节点是根节点直接退出*/
                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_SMARTLIGHTING) {
                    return AjaxResult.error("未查询到有相关控制器节点！");
                }
            }
        }
        /**添加控制器id*/
        module.setControllerId((long) con.getId());

        if (con == null) {
            return AjaxResult.error("未查询到所属控制器信息！");
        }
        //如果模块的通信地址在当前ddc下有相同的地址,则在页面提示通信地址重复,添加失败
//        List<Module> addreeList = moduleMapper.selectModuleListByControllerId(module);
        List<Module> addreeList = new ArrayList<>();
        Map<String, Module> cacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
        Collection<Module> values = cacheMap.values();
        for (Module m : values) {
            if (con.getId() == m.getControllerId().intValue() && !m.getSysName().equals(module.getSysName())) {
                addreeList.add(m);
                continue;
            }
        }

        //判断是否为照明 通信地址不能相同
        if (con.getType() == DeviceTreeConstants.BES_DDCNODE) {
            if (addreeList.size() > 0) {
                String address = module.getSlaveAddress().toString();
                for (int i = 0; i < addreeList.size(); i++) {
                    if (address.equals(addreeList.get(i).getSlaveAddress().toString())) {
                        return AjaxResult.error("通信地址重复");
                    }
                }
            }
        } else {
            //获取父节点下     同级的数据
            //获取同级节点数据
            List<DeviceTree> list = deviceTreeCache.getCascadeSubordinate(Integer.parseInt(module.getFatherId()));
            for (DeviceTree deviceTree : list) {
                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_MODEL) {
                    Module moduleData = moduleAndPointCache.getModuleByDeviceId((long) deviceTree.getDeviceTreeId());
                    if (moduleData.getSlaveAddress().equals(module.getSlaveAddress())) {
                        return AjaxResult.error("通信地址不允许重复!");
                    }
                }
            }
        }

        //先添加到树结构
        DeviceTree deviceTreeP = new DeviceTree();
        deviceTreeP.setDeviceNodeId(Integer.parseInt(module.getNodeType()));
        deviceTreeP.setDeviceTreeFatherId(Integer.parseInt(module.getFatherId()));
        deviceTreeP.setCreateTime(DateUtils.getNowDate());
        deviceTreeP.setSysName(module.getSysName());
//        Map<String, Object> map = pointMapper.selectDeviceTreeeByFatherId(deviceTreeP.getDeviceTreeFatherId(), deviceTreeP.getDeviceNodeId());
        DeviceTree treeFatherInfo = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(String.valueOf(deviceTreeP.getDeviceTreeFatherId())));
        int deviceType = treeFatherInfo.getDeviceType();
        String park = null;
        Integer flnIdPort = 0;
        park = treeFatherInfo.getPark();
        deviceTreeP.setDeviceType(deviceType);
        deviceTreeP.setPark(park);
        //ddc 才进行总线查询
        if (con.getType() == DeviceTreeConstants.BES_DDCNODE) {
            //查询总线端口
            BuildNode flnBuildNode = deviceTreeCache.getNodeByLineTreeId(Long.parseLong(flnId));
            if (flnBuildNode.getPortNum() != null) {
                flnIdPort = Integer.parseInt(flnBuildNode.getPortNum());
            } else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("未获取到总线端口");
                }
            }
        }


        boolean isInsertDeviceTreee = pointMapper.insertDeviceTreee(deviceTreeP);
        if (isInsertDeviceTreee) {
            //查询树信息及按钮
            DeviceTree deviceTreeR = pointMapper.selectDeviceTreeeByDeviceTreee(deviceTreeP);

            //将子节点存入缓存
            module.setDeviceNodeFunName(deviceTreeR.getDeviceNodeFunName());
            module.setDeviceNodeFunType(deviceTreeR.getDeviceNodeFunType());
            module.setDeviceTreeStatus(deviceTreeR.getDeviceTreeStatus());
            module.setDeviceTreeId((long) deviceTreeR.getDeviceTreeId());
            boolean isInsertModule = moduleMapper.insertModule(module);
            if (isInsertModule) {

                //添加对应的模块点
                DataReception d = addModulePoint(module, deviceType, park, con);
                if (!d.getState()) {
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return AjaxResult.error("添加模块失败,点位下发失败");
                    }
                }

                //点存入缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
                //树节点存入缓存
                deviceTreeR.setSysName(module.getAlias());
                deviceTreeR.setRedisSysName(module.getSysName());
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeP.getDeviceTreeId(), deviceTreeR);
                /***
                 * qindehua
                 * 2022/10/13
                 * */
                /***楼控***/

                if (con.getType() == DeviceTreeConstants.BES_DDCNODE) {
                    ModuleParamDDC moduleParam = new ModuleParamDDC();
                    moduleParam.setName(sysName);                                                   //系统名称
                    moduleParam.setAlias(alias);                                                    //别名
                    moduleParam.setDescription(description);                                        //描述
                    moduleParam.setLocation(installAddress);                                        //安装地址
                    moduleParam.setModelID(Integer.parseInt(String.valueOf(moduleTypeId)));         //模块类别
                    moduleParam.setFlnID(flnIdPort);                                                //所属线路端口
                    moduleParam.setSlaveAddress(Integer.parseInt(String.valueOf(slaveAddress)));    //通讯地址
                    moduleParam.setActive(Integer.parseInt(String.valueOf(active)));                //使能
                    moduleParam.setId(Integer.parseInt(String.valueOf(module.getId())));            //ID

                    // 同步数据到下位机
                    boolean sendState = SendMsgHandler.addModuleDDC(con.getIp(), moduleParam);
                    if (!sendState) {
                        return AjaxResult.success("新增模块成功,模块下发失败", module);
                    }

                    // 添加订阅消息
                    MsgSubPubHandler.addSubMsg(DDCCmd.MODULE_ADD, con.getIp());
                    //新增成功，下发成功

                    return AjaxResult.success("添加成功", module);
                }
                /***照明***/
                else {
                    //获取干线和支线通信地址  如果父节点和控制器树ID一致 说明是直接添加模块 通信地址前缀 默认 1.1.
                    String[] array = new String[2];
                    if (module.getFatherId().equals(String.valueOf(con.getDeviceTreeId()))) {
                        array[0] = "1";
                        array[1] = "1";
                    } else {
                        BuildNode buildNode = deviceTreeCache.getNodeByBranchTreeId(module.getBranchCode());
                        array = buildNode.getPortNum().split("\\.");
                    }

                    ModuleParamLDC moduleParam = new ModuleParamLDC();
                    moduleParam.setName(sysName);                                                   //系统名称
                    moduleParam.setAlias(alias);                                                    //别名
                    moduleParam.setDescription(description);                                        //描述
                    moduleParam.setLocation(installAddress);                                        //安装地址
                    moduleParam.setModelID(Integer.parseInt(String.valueOf(moduleTypeId)));         //模块类别
                    moduleParam.setAreaNum(Integer.parseInt(array[0]));                             //所属干线
                    moduleParam.setBranchNum(Integer.parseInt(array[1]));                           //所属支线
                    moduleParam.setSlaveAddress(Integer.parseInt(String.valueOf(slaveAddress)));    //通讯地址
                    moduleParam.setActive(Integer.parseInt(String.valueOf(active)));                //使能
                    moduleParam.setId(Integer.parseInt(String.valueOf(module.getId())));            //ID

                    // 同步数据到下位机
                    boolean sendState = SendMsgHandler.addModuleLDC(con.getIp(), moduleParam);
                    if (!sendState) {
                        return AjaxResult.success("新增模块成功,模块下发失败", module);
                    }

                    // 添加订阅消息
                    MsgSubPubHandler.addSubMsg(LDCCmd.MODULE_ADD, con.getIp());

                    //新增成功，下发成功
                    return AjaxResult.success("添加成功", module);
                }

            }
        }
        return AjaxResult.error("添加失败");
    }


    /**
     * 添加对应的模块点(根据模块类型)
     *
     * @param module
     * @param deviceType
     * @return
     * @auther:gaojikun
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception addModulePoint(Module module, int deviceType, String park, Controller con) {
        DataReception dataReception = new DataReception();
        try {
            //获取模块类型点位list
            Map<String, String> setMap = moduleMapper.selectModulePoint(module);
            String pointSet = setMap.get("pointSet");
            List<String> pointList = new ArrayList<>();
            for (int i = 0; i < pointSet.length(); i++) {
                String point = pointSet.substring(i, i + 1);
                pointList.add(point);
            }

            //查询当前点位最大设备ID
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
//            Map<String, Object> modulePointMap = pointMapper.selectPointMaxId(controllerId);
                equipmentId = sortList.get(0).getEquipmentId();
            }
            equipmentId = equipmentId + 1;


            int code = 0;
            Point point = new Point();
//            List<PointParamDDC> paramList = new ArrayList();

            //循环添加模块点
            for (String str : pointList) {
                String pointSysName = "";
                if (code < 10) {
                    pointSysName = module.getSysName() + "0" + code;
                } else {
                    pointSysName = module.getSysName() + code;
                }
                DeviceTree deviceTreeNode = new DeviceTree();
                //添加至树结构
                if ("1".equals(str)) {
                    deviceTreeNode.setDeviceNodeId(DeviceTreeConstants.BES_AI);
                    deviceTreeNode.setSysName(pointSysName);//AI
                    point.setNodeType(String.valueOf(DeviceTreeConstants.BES_AI));
                } else if ("2".equals(str)) {
                    deviceTreeNode.setDeviceNodeId(DeviceTreeConstants.BES_AO);
                    deviceTreeNode.setSysName(pointSysName);//AO
                    point.setNodeType(String.valueOf(DeviceTreeConstants.BES_AO));
                } else if ("3".equals(str)) {
                    deviceTreeNode.setDeviceNodeId(DeviceTreeConstants.BES_DI);
                    deviceTreeNode.setSysName(pointSysName);//DI
                    point.setNodeType(String.valueOf(DeviceTreeConstants.BES_DI));
                } else if ("4".equals(str)) {
                    deviceTreeNode.setDeviceNodeId(DeviceTreeConstants.BES_DO);
                    deviceTreeNode.setSysName(pointSysName);//DO
                    point.setNodeType(String.valueOf(DeviceTreeConstants.BES_DO));
                } else if ("5".equals(str)) {
                    deviceTreeNode.setDeviceNodeId(DeviceTreeConstants.BES_UI);
                    deviceTreeNode.setSysName(pointSysName);//UI
                    point.setNodeType(String.valueOf(DeviceTreeConstants.BES_UI));
                } else {
                    deviceTreeNode.setDeviceNodeId(DeviceTreeConstants.BES_UX);
                    deviceTreeNode.setSysName(pointSysName);//UX
                    point.setNodeType(String.valueOf(DeviceTreeConstants.BES_UX));
                }
                deviceTreeNode.setDeviceTreeFatherId(Integer.parseInt(String.valueOf(module.getDeviceTreeId())));
                deviceTreeNode.setDeviceType(deviceType);
                deviceTreeNode.setCreateTime(DateUtils.getNowDate());
                deviceTreeNode.setPark(park);

                boolean isInsertDeviceTreee = pointMapper.insertDeviceTreee(deviceTreeNode);
                if (isInsertDeviceTreee) {
                    //查询树信息及按钮
                    DeviceTree deviceTreeR = pointMapper.selectDeviceTreeeByDeviceTreee(deviceTreeNode);

                    //添加到点位表
                    point.setEquipmentId(equipmentId);
                    point.setControllerId(controllerId);
                    point.setTreeId((long) deviceTreeNode.getDeviceTreeId());
                    point.setModuleId(module.getId());
                    point.setSysName(pointSysName);
                    point.setCreateTime(DateUtils.getNowDate());
                    point.setChannelIndex((long) code);
                    point.setEnabled(1);
                    point.setWorkMode((long) 1);

                    //添加到点位表
                    boolean isInsertPoint = pointMapper.insertPoint(point);
                    //查询添加后的点位信息
                    point = pointMapper.selectPointByGuid(point.getGuid());


                    if (isInsertPoint) {
                        //新增点位时  在线状态默认跟所在控制器一致
                        deviceTreeR.setDeviceTreeStatus(con.getOnlineState());
                        //存入树缓存  如果点位有别名则存入缓存树sysName字段  没有别名则说明是空点位  sysName字段应为空
                        if (point.getNickName() != null && !"".equals(point.getNickName())) {
                            deviceTreeR.setSysName(point.getNickName());
                        } else {
                            deviceTreeR.setSysName("");
                        }
                        deviceTreeR.setRedisSysName(point.getSysName());
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeNode.getDeviceTreeId(), deviceTreeR);
                        //存入点位缓存
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, point.getTreeId(), point);
                    } else {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return new DataReception(false, "添加模块下点位至点位表时失败");
                        }
                    }
                }
                code++;
                equipmentId++;
            }

            dataReception.setState(true);
            return dataReception;
        } catch (Exception e) {
            return new DataReception(false, "添加模块下点位时失败");
        }
    }


    /**
     * 修改模块
     *
     * @param module 模块
     * @return 结果
     * @auther:gaojikun
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult updateModule(Module module) {
        module.setUpdateTime(DateUtils.getNowDate());           //修改时间

        String alias = module.getAlias();                       //别名
        String description = module.getDescription();           //描述
        String installAddress = module.getInstallAddress();     //安装地址
        String nodeType = module.getNodeType();                 //模块树节点类型
        String sysName = module.getSysName();                   //系统名称
        String fatherId = module.getFatherId();                 //父级ID
        Long deviceTreeId = module.getDeviceTreeId();           //设备树ID
        Long moduleTypeId = module.getModuleTypeId();           //所属模块型号ID
        Long controllerId = module.getControllerId();           //控制器ID
        Long type = module.getType();                           //模块类型
        Long slaveAddress = module.getSlaveAddress();           //通讯地址
        Long active = module.getActive();                       //使能
        Long synchState = module.getSynchState();               //同步状态
        Long onlineState = module.getOnlineState();             //在线状态
        Long flnID = module.getFlnId();                         //所属总线ID
        Long trunkCode = module.getTrunkCode();                 //所在干线号
        Long branchCode = module.getBranchCode();               //所在支线号
        if (type==null){
            return AjaxResult.error("模块类型不能为空！");
        }
        else {
            if (type == 0) {
                if (!StringUtils.hasText(alias)
                                || !StringUtils.hasText(description)
                                || !StringUtils.hasText(installAddress)
                                || !StringUtils.hasText(nodeType)
                                || !StringUtils.hasText(sysName)
                                || !StringUtils.hasText(fatherId)
                                || moduleTypeId == null
                                || module.getId() == null
                                || controllerId == null
                                || slaveAddress == null
                                || active == null
                                || synchState == null
                                || onlineState == null
                                || flnID == null
                                || deviceTreeId == null
                ) {
                    return AjaxResult.error("请完善输入信息");
                }
            } else {
                if (!StringUtils.hasText(alias)
                                || !StringUtils.hasText(description)
                                || !StringUtils.hasText(installAddress)
                                || !StringUtils.hasText(nodeType)
                                || !StringUtils.hasText(sysName)
                                || !StringUtils.hasText(fatherId)
                                || moduleTypeId == null
                                || module.getId() == null
                                || controllerId == null
                                || slaveAddress == null
                                || active == null
                                || synchState == null
                                || onlineState == null
                                || deviceTreeId == null
                ) {
                    return AjaxResult.error("请完善输入信息");
                }
            }
        }

        DeviceTree moduleTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(deviceTreeId);


        //如果模块的通信地址在当前ddc下有相同的地址,则在页面提示通信地址重复,添加失败
        List<Controller> controllers = new ArrayList<>();
        recursive(module.getDeviceTreeId(), controllers);
        Controller con = controllers.get(0);
        if (con == null) {
            return AjaxResult.error("未查询到所属控制器信息！");
        }
        module.setControllerId((long) con.getId());
        List<Module> addreeList = moduleMapper.selectModuleListByControllerId(module);

        if (con.getType() == DeviceTreeConstants.BES_DDCNODE) {

            if (addreeList.size() > 0) {
                String address = module.getSlaveAddress().toString();
                for (int i = 0; i < addreeList.size(); i++) {
                    if (address.equals(addreeList.get(i).getSlaveAddress().toString())) {
                        return AjaxResult.error("通信地址重复");
                    }
                }
            }
        }
        //判断是否为照明 通信地址不能相同
        else {
            //获取父节点下     同级的数据
            DeviceTree deviceTreeData = deviceTreeCache.getDeviceTreeByDeviceTreeId(module.getDeviceTreeId());
            //获取同级节点数据
            List<DeviceTree> list = deviceTreeCache.getCascadeSubordinate(deviceTreeData.getDeviceTreeFatherId());

            for (DeviceTree deviceTree : list) {
                //过滤掉当前节点
                if (deviceTree.getDeviceTreeId() == module.getDeviceTreeId()) {
                    continue;
                }
                if (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_MODEL) {
                    Module moduleData = moduleAndPointCache.getModuleByDeviceId((long) deviceTree.getDeviceTreeId());
                    if (moduleData.getSlaveAddress().equals(module.getSlaveAddress())) {
                        return AjaxResult.error("通信地址不允许重复!");
                    }
                }
            }
        }

        //查询总线端口
        Integer flnIdPort = 0;
        //只有ddc 才进行总线查询
        if (con.getType() == DeviceTreeConstants.BES_DDCNODE) {
            BuildNode flnBuildNode = deviceTreeCache.getNodeByLineTreeId(flnID);
            if (flnBuildNode.getPortNum() != null) {
                flnIdPort = Integer.parseInt(flnBuildNode.getPortNum());
            } else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("未获取到总线端口");
                }
            }
        }


        boolean isUpdateModule = moduleMapper.updateModule(module);
        if (isUpdateModule) {

            module = moduleMapper.selectModuleById(module.getId());

            //修改缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, module.getDeviceTreeId(), module);
            /***
             * qindehua
             * 2022/10/13
             * */
            if (module.getType() == 0) {//楼控
                ModuleParamDDC moduleParam = new ModuleParamDDC();
                moduleParam.setName(sysName);                                                   //系统名称
                moduleParam.setAlias(alias);                                                    //别名
                moduleParam.setDescription(description);                                        //描述
                moduleParam.setLocation(installAddress);                                        //安装地址
                moduleParam.setModelID(Integer.parseInt(String.valueOf(moduleTypeId)));         //所属模块ID
                moduleParam.setFlnID(flnIdPort);                                                //所属线路端口
                moduleParam.setSlaveAddress(Integer.parseInt(String.valueOf(slaveAddress)));    //通讯地址
                moduleParam.setActive(Integer.parseInt(String.valueOf(active)));                //使能
                moduleParam.setId(Integer.parseInt(String.valueOf(module.getId())));            //ID

                // 同步数据到下位机
                boolean sendState = SendMsgHandler.setModuleDDC(con.getIp(), moduleParam);
                if (!sendState) {
                    return AjaxResult.success("修改模块成功,下发失败", module);
                }

                // 添加订阅消息
                MsgSubPubHandler.addSubMsg(DDCCmd.MODULE_PARAM_SET, con.getIp());

            } else {//照明
                //获取干线和支线通信地址  如果父节点和控制器树ID一致 说明是直接添加模块 通信地址前缀 默认 1.1.
                String[] array = new String[2];
                if (moduleTree.getDeviceTreeFatherId() == con.getDeviceTreeId()) {
                    array[0] = "1";
                    array[1] = "1";
                } else {
                    BuildNode buildNode = deviceTreeCache.getNodeByBranchTreeId(branchCode);
                    array = buildNode.getPortNum().split("\\.");
                }


                ModuleParamLDC moduleParam = new ModuleParamLDC();
                moduleParam.setName(sysName);                                                   //系统名称
                moduleParam.setAlias(alias);                                                    //别名
                moduleParam.setDescription(description);                                        //描述
                moduleParam.setLocation(installAddress);                                        //安装地址
                moduleParam.setModelID(Integer.parseInt(String.valueOf(moduleTypeId)));         //模块类别
                moduleParam.setAreaNum(Integer.parseInt(array[0]));                             //所属干线
                moduleParam.setBranchNum(Integer.parseInt(array[1]));                           //所属支线
                moduleParam.setSlaveAddress(Integer.parseInt(String.valueOf(slaveAddress)));    //通讯地址
                moduleParam.setActive(Integer.parseInt(String.valueOf(active)));                //使能
                moduleParam.setId(Integer.parseInt(String.valueOf(module.getId())));            //ID

                // 同步数据到下位机
                boolean sendState = SendMsgHandler.setModuleLDC(con.getIp(), moduleParam);
                if (!sendState) {
                    return AjaxResult.success("修改模块成功,下发失败", module);
                }

                // 添加订阅消息
                MsgSubPubHandler.addSubMsg(LDCCmd.MODULE_PARAM_SET, con.getIp());

            }
            return AjaxResult.success("修改模块成功", module);


        }
        return AjaxResult.error("修改模块失败", module);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块主键
     * @return AjaxResult
     * @author gaojikun
     */
    @Override
    public AjaxResult deleteModuleByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误！");
        }
        int delNum = moduleMapper.deleteModuleByIds(ids);
        if (delNum == 0) {
            return AjaxResult.error("删除失败！");
        } else {
            return AjaxResult.success("删除成功！");
        }
    }

    /**
     * 删除模块信息
     *
     * @param id 模块主键
     * @return AjaxResult
     * @author gaojikun
     */
    @Override
    public AjaxResult deleteModuleById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误！");
        }
        int delNum = moduleMapper.deleteModuleById(id);
        if (delNum == 0) {
            return AjaxResult.error("删除失败！");
        } else {
            return AjaxResult.success("删除成功！");
        }
    }



    /**
     * @Author:gaojikun
     * @Date:2022-09-30
     * @Param:module
     * @Description:同步模块
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult synchronizeModule(Module module) {

        String ip;                                                                      //所属ip的地址

        Integer treeId = Integer.parseInt(String.valueOf(module.getDeviceTreeId()));
        Module moduleMap = moduleAndPointCache.getModuleByDeviceId((long) treeId);
        DeviceTree moduleTree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long) treeId);

        Integer fatherTreeId = moduleTree.getDeviceTreeFatherId();                      //设备树父ID

        String name = moduleMap.getSysName();                                           //名称
        String alias = moduleMap.getAlias();                                            //别名
        String description = moduleMap.getDescription();                                //模块的描述信息
        String location = moduleMap.getInstallAddress();                                //模块的安装位置
        Integer slaveAddress = moduleMap.getSlaveAddress().intValue();                  //模块的通信地址
        Integer active = Integer.parseInt(String.valueOf(moduleMap.getActive()));       //模块的使能状态
        Integer id = Integer.parseInt(String.valueOf(moduleMap.getId()));               //模块的识别码
        Integer modelID = Integer.parseInt(String.valueOf(moduleMap.getModuleTypeId()));//模块型号编码
        Integer flnID = 0;                                                              //模块挂在哪条FLN总线上，可能取值PNP,FLN1、FLN2、FLN3、FLN4 (1,2,3,4,5)
        Integer areaNum = moduleMap.getTrunkCode() == null ? null : moduleMap.getTrunkCode().intValue();                           //模块所在干线号,默认为1
        Integer branchNum = moduleMap.getBranchCode() == null ? null : moduleMap.getBranchCode().intValue();                        //模块所在支线号,默认为1

        if (
                !StringUtils.hasText(name)
                        || !StringUtils.hasText(alias)
                        || !StringUtils.hasText(description)
                        || active == null
                        || !StringUtils.hasText(location)
                        || id == null
                        || modelID == null
                        || flnID == null
        ) {
            return AjaxResult.error("请配置模块信息");
        }


        if (DeviceTreeConstants.BES_DDCNODE == moduleTree.getDeviceType()) {//楼控的模块

            //查询总线端口
            BuildNode flnBuildNode = deviceTreeCache.getNodeByLineTreeId(moduleMap.getFlnId());
            flnID = Integer.parseInt(flnBuildNode.getPortNum());


            //查询模块所属DDC控制器
            DataReception dataReception = validation(moduleMap.getDeviceTreeId(), Integer.parseInt(moduleMap.getNodeType()));
            if (!dataReception.getState()) {
                return AjaxResult.error("获取控制器信息失败,同步失败!");
            }
            Controller con = (Controller) dataReception.getData();
            ip = con.getIp();


            ModuleParamDDC moduleParam = new ModuleParamDDC();
            moduleParam.setName(name);
            moduleParam.setAlias(alias);
            moduleParam.setDescription(description);
            moduleParam.setLocation(location);
            moduleParam.setModelID(modelID);
            moduleParam.setFlnID(flnID);
            moduleParam.setSlaveAddress(slaveAddress);
            moduleParam.setActive(active);
            moduleParam.setId(id);

            // 同步模块数据到下位机
            boolean sendState = SendMsgHandler.setModuleDDC(ip, moduleParam);

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.MODULE_PARAM_SET, ip);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }



//            //同步模块下的点位
//            //查询模块下的非空点位列表
//            List<DeviceTree> deviceTreeList = deviceTreeCache.getCascadeSubordinate(Integer.parseInt(module.getDeviceTreeId().toString()));
//            List<Point> pointList = new ArrayList<>();
//            for (DeviceTree d : deviceTreeList) {
//            Point point = moduleAndPointCache.getPointByDeviceId((long) d.getDeviceTreeId());
//                if (null != point && null != point.getNickName() && !"".equals(point.getNickName())) {
//                    pointList.add(point);
//                }
//            }
//
//            this.synchronizePointList(pointList, con);

            return AjaxResult.success("同步成功");

        } else {//照明的模块

            //查询模块所属LDC控制器
            List<Controller> controllers = new ArrayList<>();
            recursive(moduleMap.getDeviceTreeId(), controllers);
            if (controllers.isEmpty()) {
                return AjaxResult.error("LDC控制器缓存信息不存在!!");
            }
            ip = controllers.get(0).getIp();
            //获取干线和支线通信地址  如果父节点和控制器树ID一致 说明是直接添加模块 通信地址前缀 默认 1.1.
            String[] array = new String[2];
            if (moduleTree.getDeviceTreeFatherId() == controllers.get(0).getDeviceTreeId()) {
                array[0] = "1";
                array[1] = "1";
            } else {
                BuildNode buildNode = deviceTreeCache.getNodeByBranchTreeId((long) branchNum);
                array = buildNode.getPortNum().split("\\.");
            }
            ModuleParamLDC moduleParamLDC = new ModuleParamLDC();
            moduleParamLDC.setName(name);
            moduleParamLDC.setAlias(alias);
            moduleParamLDC.setDescription(description);
            moduleParamLDC.setLocation(location);
            moduleParamLDC.setModelID(modelID);
            moduleParamLDC.setAreaNum(Integer.parseInt(array[0]));
            moduleParamLDC.setBranchNum(Integer.parseInt(array[1]));
            moduleParamLDC.setSlaveAddress(slaveAddress);
            moduleParamLDC.setActive(active);
            moduleParamLDC.setId(id);

            // 同步数据到下位机
            boolean sendState = SendMsgHandler.setModuleLDC(ip, moduleParamLDC);

            if (!sendState) {
                return AjaxResult.error("同步失败!");
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.MODULE_PARAM_SET, ip);
            return AjaxResult.success("同步成功");
        }
    }


    /**
     * @Description: 同步模块点
     * @auther: gaojikun
     * @date: 2022/09/29
     * @param: point
     * @return: AjaxResult
     */
    @Override
    public AjaxResult synchronizePoint(Point point) {

        Integer pointType = null;

        Integer nodeType = Integer.parseInt(point.getNodeType());                               //设备树点类型
        Long treeId = point.getTreeId();                                                        //设备树点ID

        if (nodeType == null || treeId == null) {
            return AjaxResult.error("未获取到点位树Id和类型，同步失败!");
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
            return AjaxResult.error("同步失败，请配置点位信息!");
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
                return AjaxResult.error("请配置DI点信息");
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
                return AjaxResult.error("请配置DO点信息");
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
//                            ||
//                            alarmHighValueLong == null ||
//                            alarmLowValueLong == null
            ) {
                return AjaxResult.error("请配置AI点信息");
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
//                            ||
//                            alarmHighValueLong == null ||
//                            alarmLowValueLong == null
            ) {
                return AjaxResult.error("请配置AO点信息");
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
        //查询当前点位所属的控制器的IP
        DataReception dataReception = validation(point.getTreeId(), Integer.parseInt(point.getNodeType()));
        if (!dataReception.getState()) {
            return AjaxResult.error("获取控制器信息失败,同步失败!");
        }
        Controller con = (Controller) dataReception.getData();


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
                return AjaxResult.error("下发失败!");
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.POINT_PARAM_SET, ip);

            return AjaxResult.success("同步成功");

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
                return AjaxResult.error("同步失败");
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.POINT_PARAM_SET, ip);
            return AjaxResult.success("同步成功");
        } else {
            return AjaxResult.error("同步失败，未找到点位所属设备类型");
        }

    }


    /**
     * 同步虚点信息
     *
     * @auther: gaojikun
     * @date: 2022/09/29
     * @param: point
     * @return: AjaxResult
     */
    @Override
    public AjaxResult synVirtualPoint(Point point) {

        Long treeId = point.getTreeId();                                                        //设备树点ID

        if (treeId == null) {
            return AjaxResult.error("未获取到点位树Id，同步失败!");
        }

        Point virtualPoint = moduleAndPointCache.getPointByDeviceId(treeId);

        if (virtualPoint == null) {
            return AjaxResult.error("同步失败，参数错误!");
        }

        DataReception dataReception = validation(treeId, Integer.parseInt(point.getNodeType()));
        if (!dataReception.getState()) {
            return AjaxResult.error("获取控制器信息失败,同步失败!");
        }
        Controller con = (Controller) dataReception.getData();
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
                return AjaxResult.error("下发失败!");
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.POINT_PARAM_SET, channelId);

            return AjaxResult.success("同步成功");

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
                return AjaxResult.error("下发失败");
            }

            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.POINT_PARAM_SET, channelId);

            return AjaxResult.success("同步成功");
        } else {
            return AjaxResult.error("同步失败，未找到点位所属设备类型");
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
     * 验证
     *
     * @return {@code DataReception }
     * @Author gaojikun
     * @Date 2022/09/29
     **/
    private DataReception validation(Long deviceTreeId, Integer type) {
        //判断数据是否为空
        if (deviceTreeId == null || type == null) {
            return new DataReception(true, "参数错误!");
        }
        //获取当前节点所属控制器
        List<Controller> controllerId = new ArrayList<>();
        recursive(deviceTreeId, controllerId);
        if (controllerId.isEmpty()) {
            return new DataReception(false, "控制器缓存信息不存在!");
        }
        String channelId = controllerId.get(0).getIp();
        //判断channelID 地址
        if (!StringUtils.hasText(channelId)) {
            return new DataReception(true, "无效的 channelID 地址!");
        }
        return new DataReception(true, controllerId.get(0));
    }

    /**
     * 数据对比
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author gaojikun
     * @Date 2022/09/29
     **/
    @Override
    public AjaxResult getDataInfoParam(Long deviceTreeId, Integer type) {
        //获取控制器信息
        DataReception dataReception = validation(deviceTreeId, type);
        if (!dataReception.getState()) {
            return AjaxResult.error(dataReception.getMsg());
        }
        Controller controller = (Controller) dataReception.getData();
        String channelID = controller.getIp();

        if (type.equals(DeviceTreeConstants.BES_MODEL)) {//模块点

            Module module = moduleAndPointCache.getModuleByDeviceId(deviceTreeId);
            //楼控
            if (module.getType() == 0) {
                boolean sendState = SendMsgHandler.getModuleDDC(channelID, Integer.parseInt(String.valueOf(module.getId())));
                if (!sendState) {
                    return AjaxResult.error("获取模块下位机数据失败!");
                }
                MsgSubPubHandler.addSubMsg(DDCCmd.MODULE_PARAM_GET, channelID);
            }
            //照明
            else {
                boolean sendState = SendMsgHandler.getModuleLDC(channelID, Integer.parseInt(String.valueOf(module.getId())));
                if (!sendState) {
                    return AjaxResult.error("获取模块下位机数据失败!");
                }
                MsgSubPubHandler.addSubMsg(LDCCmd.MODULE_PARAM_GET, channelID);
            }

        } else if (type.equals(DeviceTreeConstants.BES_VPOINT)) {//虚点
            Point pointMap = moduleAndPointCache.getPointByDeviceId(deviceTreeId);

            if (controller.getType() == DeviceTreeConstants.BES_DDCNODE) {
                boolean sendState = SendMsgHandler.getPointParamDDC(channelID, pointMap.getEquipmentId());
                if (!sendState) {
                    return AjaxResult.error("获取虚点下位机数据失败!");
                }
                MsgSubPubHandler.addSubMsg(DDCCmd.POINT_PARAM_GET, channelID);
            } else {

                boolean sendState = SendMsgHandler.getPointParamLDC(channelID, pointMap.getEquipmentId());
                if (!sendState) {
                    return AjaxResult.error("获取虚点下位机数据失败!");
                }
                MsgSubPubHandler.addSubMsg(LDCCmd.POINT_PARAM_GET, channelID);
            }


        } else if (type.equals(DeviceTreeConstants.BES_AI)
                || type.equals(DeviceTreeConstants.BES_AO)
                || type.equals(DeviceTreeConstants.BES_DI)
                || type.equals(DeviceTreeConstants.BES_DO)) {//逻辑点

            Point pointMap = moduleAndPointCache.getPointByDeviceId(deviceTreeId);

            Integer nodeType = Integer.parseInt(pointMap.getNodeType());                           //设备点类型

            Integer active = pointMap.getEnabled();                                                 //是否启用
            String name = pointMap.getSysName();                                                    //点的名字,发送到下位机名字
            String alias = pointMap.getNickName();                                                  //别名
            String description = pointMap.getDescription();                                         //描述
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

//        if (!nodeType.equals(DeviceTreeConstants.BES_DI)) {
            if (initValue == null) {
                initValue = "0.0";
            }
//        }


            if (nodeType.equals(DeviceTreeConstants.BES_DI)) {
                if (
                        !StringUtils.hasText(name) ||
                                !StringUtils.hasText(alias) ||
                                !StringUtils.hasText(description) ||
                                active == null ||
                                workModeLong == null ||
                                polarityLong == null ||
                                initValue == null ||
                                alarmActive == null ||
                                alarmType == null ||
                                alarmPriority == null ||
                                activePassiveLong == null
                ) {
                    return AjaxResult.error("请配置DI点信息");
                }
            } else if (nodeType.equals(DeviceTreeConstants.BES_DO)) {
                if (
                        !StringUtils.hasText(name) ||
                                !StringUtils.hasText(alias) ||
                                !StringUtils.hasText(description) ||
                                active == null ||
                                workModeLong == null ||
                                polarityLong == null ||
                                initValue == null ||
                                alarmActive == null ||
                                alarmType == null ||
                                alarmPriority == null

                ) {
                    return AjaxResult.error("请配置DO点信息");
                }
            } else if (nodeType.equals(DeviceTreeConstants.BES_AI)) {
                if (
                        !StringUtils.hasText(name) ||
                                !StringUtils.hasText(unit) ||
                                !StringUtils.hasText(alias) ||
                                !StringUtils.hasText(description) ||
                                active == null ||
                                workModeLong == null ||
                                polarityLong == null ||
                                initValue == null ||
                                alarmActive == null ||
                                alarmType == null ||
                                alarmPriority == null ||
                                lineTypeLong == null ||
                                highRange == null ||
                                lowRange == null ||
                                precisionLong == null
//                                ||
//                                alarmHighValueLong == null ||
//                                alarmLowValueLong == null
                ) {
                    return AjaxResult.error("请配置AI点信息");
                }
            } else {
                if (
                        !StringUtils.hasText(name) ||
                                !StringUtils.hasText(unit) ||
                                !StringUtils.hasText(alias) ||
                                !StringUtils.hasText(description) ||
                                active == null ||
                                workModeLong == null ||
                                polarityLong == null ||
                                initValue == null ||
                                alarmActive == null ||
                                alarmType == null ||
                                alarmPriority == null ||
                                lineTypeLong == null ||
                                highRange == null ||
                                lowRange == null ||
                                precisionLong == null
//                                ||
//                                alarmHighValueLong == null ||
//                                alarmLowValueLong == null
                ) {
                    return AjaxResult.error("请配置AO点信息");
                }
            }
            if (controller.getType() == DeviceTreeConstants.BES_DDCNODE) {
                boolean sendState = SendMsgHandler.getPointParamDDC(channelID, pointMap.getEquipmentId());
                if (!sendState) {
                    return AjaxResult.error("获取模块点下位机数据失败!");
                }
                MsgSubPubHandler.addSubMsg(DDCCmd.POINT_PARAM_GET, channelID);
            } else {
                boolean sendState = SendMsgHandler.getPointParamLDC(channelID, pointMap.getEquipmentId());
                if (!sendState) {
                    return AjaxResult.error("获取模块点下位机数据失败!");
                }
                MsgSubPubHandler.addSubMsg(LDCCmd.POINT_PARAM_GET, channelID);
            }

        } else {
            return AjaxResult.error("非法参数，下发失败!");
        }
        return AjaxResult.success("获取下位机数据成功!");
    }
}
