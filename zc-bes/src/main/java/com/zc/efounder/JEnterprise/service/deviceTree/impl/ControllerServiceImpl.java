package com.zc.efounder.JEnterprise.service.deviceTree.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.constant.WebSocketEvent;
import com.zc.common.core.model.DataReception;
import com.zc.common.core.websocket.WebSocketService;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.business.constant.EDCCmd;
import com.zc.connect.business.constant.LDCCmd;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.business.dto.ddc.ControllerDataDDC;
import com.zc.connect.business.dto.edc.ControllerDataEDC;
import com.zc.connect.business.dto.ldc.ControllerDataLDC;
import com.zc.connect.business.handler.SendMsgHandler;
import com.zc.connect.util.DataUtil;
import com.zc.efounder.JEnterprise.Cache.ControllerCache;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.MeterCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ControllerMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.DeviceTreeMapper;
import com.zc.efounder.JEnterprise.service.deviceTree.ControllerService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * 控制器Service业务层处理
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
@Service
public class ControllerServiceImpl implements ControllerService {
    @Resource
    private ControllerMapper controllerMapper;

    @Resource
    private DeviceTreeMapper deviceTreeMapper;

    @Resource
    private MeterCache meterCache;

    @Resource
    private ModuleAndPointCache moduleAndPointCache;

    @Resource
    private ControllerCache controllerCache;

    @Resource
    private RedisCache redisCache;

    @Autowired
    public PointServiceImpl pointServiceImpl;

    // 设备树缓存定义
    @Autowired
    private DeviceTreeCache deviceTreeCache;

    @PostConstruct
    public void init() {
        //首次加载时将所有控制器在线状态设置为0离线
        String[] ids = controllerMapper.selectOnLine();
        if (ids.length > 0) {
            controllerMapper.updateOffLine(ids);
        }

        /**
         * 添加数据到 redis 缓存
         */
        initControllerCache();
    }

    /**
     * 初始化到 redis 缓存
     */
    @Override
    public void initControllerCache() {

        // 获取全部设备列表数据
        List<Controller> Controllers = selectControllerList(null);
        // 清楚 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_Controller);

        if (Controllers == null || Controllers.isEmpty()) {
            return;
        }


        // 添加 redis 缓存数据
        Controllers.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) val.getDeviceTreeId(), val);
        });
    }

    /**
     * 查询控制器
     *
     * @param id 控制器主键
     * @return 控制器
     */
    @Override
    public Controller selectControllerById(Long id) {
        return controllerMapper.selectControllerById(id);
    }

    /**
     * 查询控制器列表
     *
     * @param controller 控制器
     * @return 控制器
     */
    @Override
    public List<Controller> selectControllerList(Controller controller) {
        return controllerMapper.selectControllerList(controller);
    }

    /**
     * 新增控制器
     *
     * @param controller 控制器
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/26
     **/
    @Transactional
    @Override
    public AjaxResult insertController(Controller controller) {


        controller.setCreateTime(DateUtils.getNowDate());
        controller.setCurrentIp(controller.getIp());
        controller.setSynchState(0);
        controller.setErrorState(0);
        controller.setOnlineState(0);

        //首先添加设备树节点
        DeviceTree deviceTreeMsg = new DeviceTree();

        Integer deviceTreeFatherId = controller.getDeviceTreeFatherId();  //父节点ID
        Integer deviceNodeId = controller.getDeviceNodeId(); //设备树节点ID
        Integer type = controller.getType();           //控制器类型
        String sysName = controller.getSysName();      //系统名称
        String alias = controller.getAlias();        //别名
        String ip = controller.getIp();           //ip地址
        String gateWay = controller.getGateWay();      //默认网关
        String mask = controller.getMask();         //子网掩码
        String serverIp = controller.getServerIp();     //服务器ip
        Integer serverPort = controller.getServerPort();   //服务器端口
        String location = controller.getLocation();     //安装位置
        String zone = controller.getZone();         //归属区域
        Integer active = controller.getActive();       //是否启用0：不使能、1：使能
        String description = controller.getDescription();  //描述
        Integer savePeriod = controller.getSavePeriod();   //保存周期
        Integer collectPeriod = controller.getCollectPeriod();   //采集周期
        Integer uploadPeriod = controller.getUploadPeriod();   //上传周期
        Integer synchState = controller.getSynchState();   //同步状态0：未同步、 1：已同步
        Integer errorState = controller.getErrorState();   //异常状态0：正常、1：异常
        Integer onlineState = controller.getOnlineState();  //在线状态0：不在线、 1：在线

        if (!StringUtils.hasText(sysName) ||
                !StringUtils.hasText(alias) ||
                !StringUtils.hasText(ip) ||
                "...".equals(ip)  ||
                !StringUtils.hasText(gateWay) ||
                "...".equals(gateWay) ||
                !StringUtils.hasText(mask) ||
                "...".equals(mask) ||
                !StringUtils.hasText(serverIp) ||
                "...".equals(serverIp) ||
                !StringUtils.hasText(location) ||
                !StringUtils.hasText(zone) ||
                !StringUtils.hasText(description) ||
                serverPort == null ||
                active == null ||
                savePeriod == null ||
                synchState == null ||
                errorState == null ||
                onlineState == null ||
                deviceTreeFatherId == null ||
                deviceNodeId == null ||
                type == null
        ) {
            return AjaxResult.error("请完善输入框信息");
        }
        if(type.equals(DeviceTreeConstants.BES_COLLECTORNODE)){
            if(collectPeriod==null || uploadPeriod==null){
                return AjaxResult.error("请完善输入框信息");
            }
        }
        deviceTreeMsg.setSysName(sysName);
        deviceTreeMsg.setDeviceTreeFatherId(deviceTreeFatherId);
        deviceTreeMsg.setDeviceNodeId(deviceNodeId);
        deviceTreeMsg.setDeviceType(type);
        deviceTreeMsg.setCreateTime(DateUtils.getNowDate());
        /**qindehua 系统名称不允许重复*/
        Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree).values();
        for (Object obj : collection) {
            DeviceTree deviceTree = (DeviceTree) obj;
            if (deviceTree.getRedisSysName() != null) {
                if (deviceTree.getRedisSysName().equals(sysName)) {
                    return AjaxResult.error("系统名称不允许重复!");
                }
            }
        }

        //添加park
        DeviceTree deviceTreePark = deviceTreeMapper.getdeviceTreeByid(deviceTreeFatherId.toString());

        /**qindehua IP地址不允许重复*/
        Collection collectionIP = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller).values();
        for (Object obj : collectionIP) {
            Controller con = (Controller) obj;
            if (!con.getDeviceTreeId().equals(controller.getDeviceTreeId())) {
                if (con.getIp().equals(ip)) {
                    DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long)con.getDeviceTreeId());
                    if (deviceTree.getPark().equals(deviceTreePark.getPark())){
                        return AjaxResult.error("该IP地址已存在!");
                    }
                }
            }
        }

        deviceTreeMsg.setPark(deviceTreePark.getPark());
        Boolean boo = deviceTreeMapper.insertDeviceTreeNode(deviceTreeMsg);
        if (!boo) {
            return AjaxResult.error("新增控制器失败");
        }
        //根据系统名称获取创建的设备树节点
        DeviceTree deviceTree = deviceTreeMapper.selectDeviceTreeBySYSName(deviceTreeMsg.getSysName());

        if (deviceTree == null) {
            return AjaxResult.error("新增控制器失败");
        }
        int deviceTreeId = deviceTree.getDeviceTreeId();
        controller.setDeviceTreeId(deviceTreeId);

        /***查询树所需要的数据*/
        DeviceTree treeData = deviceTreeMapper.selectDeviceTreeByData(deviceTree);
        if (treeData == null) {
            return AjaxResult.error("新增控制器失败");
        }

        //新增控制器时 默认在线状态为0
        treeData.setDeviceTreeStatus(0);
        treeData.setPark(deviceTreePark.getPark());
        //增加树按钮
        controller.setDeviceNodeFunName(treeData.getDeviceNodeFunName());
        controller.setDeviceNodeFunType(treeData.getDeviceNodeFunType());

        /***添加控制器节点信息  并且返回新增之后的控制器ID*/
        Boolean addController = controllerMapper.insertController(controller);

//        //添加park
//        DeviceTree deviceTreePark = deviceTreeMapper.getdeviceTreeByid(deviceTreeFatherId.toString());

        if (addController) {
            /***qindehua DDC节点 添加虚点、总线*/
            if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
                BuildNode buildNode = new BuildNode();
                buildNode.setFatherId(String.valueOf(controller.getDeviceTreeId()));//控制器树Id
                buildNode.setNickName("虚点");//别名
                buildNode.setNodeType(DeviceTreeConstants.BES_VPOINTNOPROPERTY);//树点类型
//                buildNode.setPark(deviceTreePark.getPark());
                DataReception dataReception = pointServiceImpl.insertDeviceTreeeReturn(buildNode);
                if (dataReception.getState()) {

                    buildNode.setNickName("总线");//别名
                    buildNode.setNodeType(DeviceTreeConstants.BES_BUS);//树点类型
                    dataReception = pointServiceImpl.insertDeviceTreeeReturn(buildNode);

                    if (!dataReception.getState()) {
                        return AjaxResult.error("新增控制器失败，生成总线节点失败", controller);
                    }

                } else {
                    return AjaxResult.error("新增控制器失败，生成虚点节点失败", controller);
                }

                /**将新增控制器数据  添加入缓存*/
                addControllerCache(controller, treeData);

                //组装下位机控制器
                ControllerDataDDC controllerDataDDC = new ControllerDataDDC();
                controllerDataDDC.setId(controller.getId());
                controllerDataDDC.setName(sysName);
                controllerDataDDC.setAlias(alias);
                controllerDataDDC.setDescription(description);
                controllerDataDDC.setLocation(location);
                controllerDataDDC.setZone(zone);
                controllerDataDDC.setActive(active);

                // 同步数据到下位机
                boolean sendState = SendMsgHandler.addControllerDDC(ip, controllerDataDDC);

                if (!sendState) {
                    return AjaxResult.success("保存成功，下发失败", controller);
                }
                MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_ADD, ip);
            }
            /***照明节点  添加虚点*/
            else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
                BuildNode buildNode = new BuildNode();
                buildNode.setFatherId(String.valueOf(controller.getDeviceTreeId()));//控制器树Id
                buildNode.setNickName("虚点");//别名
                buildNode.setNodeType(DeviceTreeConstants.BES_VPOINTNOPROPERTY);//树点类型
//                buildNode.setPark(deviceTreePark.getPark());
                DataReception dataReception = pointServiceImpl.insertDeviceTreeeReturn(buildNode);
                if (!dataReception.getState()) {
                    return AjaxResult.error("新增控制器失败，生成虚点节点失败", controller);
                }

                /**将新增控制器数据  添加入缓存*/
                addControllerCache(controller, treeData);

                //组装下位机控制器
                ControllerDataLDC controllerDataLDC = new ControllerDataLDC();
                controllerDataLDC.setId(controller.getId());
                controllerDataLDC.setName(sysName);
                controllerDataLDC.setAlias(alias);
                controllerDataLDC.setDescription(description);
                controllerDataLDC.setLocation(location);
                controllerDataLDC.setZone(zone);
                controllerDataLDC.setActive(active);

                // 同步数据到下位机
                boolean sendState = SendMsgHandler.addControllerLDC(ip, controllerDataLDC);

                if (!sendState) {
                    return AjaxResult.success("保存成功，下发失败", controller);
                }
                MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_ADD, ip);
            }
            /***能耗 */
            else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
                /**将新增控制器数据  添加入缓存*/
                addControllerCache(controller, treeData);

                //组装下位机控制器
                ControllerDataEDC controllerDataEDC = new ControllerDataEDC();
                controllerDataEDC.setId(controller.getId());
                controllerDataEDC.setName(sysName);
                controllerDataEDC.setAlias(alias);
                controllerDataEDC.setDescription(description);
                controllerDataEDC.setLocation(location);
                controllerDataEDC.setZone(zone);
                controllerDataEDC.setActive(active);
                controllerDataEDC.setSamplePeriod(collectPeriod);
                controllerDataEDC.setUpDataSamplePeriod(uploadPeriod);
                controllerDataEDC.setHisDataSavePeriod(savePeriod);
                // 同步数据到下位机
                boolean sendState = SendMsgHandler.addControllerEDC(ip, controllerDataEDC);

                if (!sendState) {
                    return AjaxResult.success("保存成功，下发失败", controller);
                }
                MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_ADD, ip);
            }
        }

        return AjaxResult.success("新增成功，下发成功", controller);
    }

    /**
     * 添加控制器缓存
     *
     * @param controller 控制器
     * @param deviceTree 设备树
     * @Author qindehua
     * @Date 2022/10/15
     **/
    private void addControllerCache(Controller controller, DeviceTree deviceTree) {

        // 添加 redis controller缓存数据
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);

        // 添加 redis 设备tree缓存数据
        deviceTree.setSysName(controller.getAlias());
        deviceTree.setRedisSysName(controller.getSysName());
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTree.getDeviceTreeId(), deviceTree);
    }

    /**
     * 修改控制器
     *
     * @param controller 控制器
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public AjaxResult updateController(Controller controller) {
        controller.setUpdateTime(DateUtils.getNowDate());
        Integer id = controller.getId();  //控制器ID
        Integer deviceTreeFatherId = controller.getDeviceTreeFatherId();  //父节点ID
        Integer deviceNodeId = controller.getDeviceNodeId();  //设备树节点ID
        Integer deviceTreeId = controller.getDeviceTreeId();  //设备树ID
        Integer type = controller.getType();           //控制器类型
        String sysName = controller.getSysName();      //系统名称
        String alias = controller.getAlias();        //别名
        String ip = controller.getIp();           //主机ip
        String gateWay = controller.getGateWay();      //默认网关
        String mask = controller.getMask();         //子网掩码
        String serverIp = controller.getServerIp();     //服务器ip
        Integer serverPort = controller.getServerPort();   //服务器端口
        String location = controller.getLocation();     //安装位置
        String zone = controller.getZone();         //归属区域
        Integer active = controller.getActive();       //是否启用0：不使能、1：使能
        String description = controller.getDescription();  //描述
        Integer savePeriod = controller.getSavePeriod();   //保存周期
        Integer collectPeriod = controller.getCollectPeriod();   //采集周期
        Integer uploadPeriod = controller.getUploadPeriod();   //上传周期
        Integer synchState = controller.getSynchState();   //同步状态0：未同步、 1：已同步
        Integer errorState = controller.getErrorState();   //异常状态0：正常、1：异常
        Integer onlineState = controller.getOnlineState();  //在线状态0：不在线、 1：在线

        if (!StringUtils.hasText(sysName) ||
                !StringUtils.hasText(alias) ||
                !StringUtils.hasText(ip) ||
                "...".equals(ip)  ||
                !StringUtils.hasText(gateWay) ||
                "...".equals(gateWay) ||
                !StringUtils.hasText(mask) ||
                "...".equals(mask) ||
                !StringUtils.hasText(serverIp) ||
                "...".equals(serverIp) ||
                !StringUtils.hasText(location) ||
                !StringUtils.hasText(zone) ||
                !StringUtils.hasText(description) ||
                serverPort == null ||
                deviceTreeId == null ||
                id == null ||
                active == null ||
                savePeriod == null ||
                synchState == null ||
                errorState == null ||
                onlineState == null ||
                deviceTreeFatherId == null ||
                deviceNodeId == null ||
                type == null
        ) {
            return AjaxResult.error("请完善输入框信息");
        }
        if(type.equals(DeviceTreeConstants.BES_COLLECTORNODE)){
            if(collectPeriod==null || uploadPeriod==null){
                return AjaxResult.error("请完善输入框信息");
            }
        }
        /**qindehua IP地址不允许重复*/
        Collection collectionIP = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller).values();
        for (Object obj : collectionIP) {
            Controller con = (Controller) obj;
            if (!con.getDeviceTreeId().equals(deviceTreeId)) {
                if (con.getIp().equals(ip)) {
                    DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId((long)con.getDeviceTreeId());
                    //当前需要修改控制器  树缓存数据
                    DeviceTree deviceTreeUpdate = deviceTreeCache.getDeviceTreeByDeviceTreeId((long)deviceTreeId);
                    if (deviceTree.getPark().equals(deviceTreeUpdate.getPark())){
                        return AjaxResult.error("该IP地址已存在!");
                    }
                }
            }
        }
        //修改成功后 更新redis 缓存
        if (controllerMapper.updateController(controller)) {
            DataReception dataReception = null;
            //先在缓存数据中查出先前数据
            Controller controllerBegin = controllerCache.getControllerByDeviceTreeId((long) deviceTreeId);
            if (controllerBegin == null) {
                return AjaxResult.error("控制器为空！");
            }

            //获取当前设备树节点下的所有子节点
            List<DeviceTree> deviceTreeList = deviceTreeCache.getCascadeSubordinate(controller.getDeviceTreeId());

            if (deviceTreeList == null || deviceTreeList.isEmpty()) {
                return AjaxResult.error("参数错误！修改控制器失败！");
            }
            /**切换IP之后  将默认为批量同步*/
            if (controllerBegin.getIp() != controller.getIp()) {
                controller.setSynchronize(true);
            }
            /***qindehua DDC节点*/
            if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {

                //组装下位机控制器
                ControllerDataDDC controllerDataDDC = new ControllerDataDDC();
                controllerDataDDC.setName(sysName);
                controllerDataDDC.setAlias(alias);
                controllerDataDDC.setDescription(description);
                controllerDataDDC.setLocation(location);
                controllerDataDDC.setZone(zone);
                controllerDataDDC.setId(id);
                controllerDataDDC.setActive(active);
                controllerDataDDC.setIp(ip);
                controllerDataDDC.setGateWay(gateWay);
                controllerDataDDC.setMask(mask);
                controllerDataDDC.setServerIP(serverIp);
                controllerDataDDC.setServerPort(serverPort);

                // 同步数据到下位机
                boolean sendState = SendMsgHandler.setControllerDDC(ip, controllerDataDDC);

                if (!sendState) {
                    /**判断是否更新ip了**/
                    dataReception = isUpdateIP(controllerBegin, controller, deviceTreeList);
                    if (dataReception.getState()) {
                        /**更新缓存*/
                        updateControllerCache(controller, null);
                        return AjaxResult.success(dataReception.getMsg(), dataReception.getData());
                    } else {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return AjaxResult.success(dataReception.getMsg());
                        }
                    }
                }
                /**更新缓存*/
                updateControllerCache(controller, null);
                MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_PARAM_SET, ip);
            }
            /***照明节点*/
            else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {

                controller.setDeviceNodeFunType(controllerBegin.getDeviceNodeFunType());
                controller.setDeviceNodeFunName(controllerBegin.getDeviceNodeFunName());

                //组装下位机控制器
                ControllerDataLDC controllerDataLDC = new ControllerDataLDC();
                controllerDataLDC.setName(sysName);
                controllerDataLDC.setAlias(alias);
                controllerDataLDC.setDescription(description);
                controllerDataLDC.setLocation(location);
                controllerDataLDC.setZone(zone);
                controllerDataLDC.setId(id);
                controllerDataLDC.setActive(active);
                controllerDataLDC.setIp(ip);
                controllerDataLDC.setGateWay(gateWay);
                controllerDataLDC.setMask(mask);
                controllerDataLDC.setServerIP(serverIp);
                controllerDataLDC.setServerPort(serverPort);

                // 同步数据到下位机
                boolean sendState = SendMsgHandler.setControllerLDC(ip, controllerDataLDC);

                if (!sendState) {
                    /**判断是否更新ip了**/
                    dataReception = isUpdateIP(controllerBegin, controller, deviceTreeList);
                    if (dataReception.getState()) {
                        /**更新缓存*/
                        updateControllerCache(controller, null);
                        return AjaxResult.success(dataReception.getMsg(), dataReception.getData());
                    } else {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return AjaxResult.success(dataReception.getMsg());
                        }
                    }
                }
                /**更新缓存*/
                updateControllerCache(controller, null);
                MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_PARAM_SET, ip);
            }
            /***能耗*/
            else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {


                //组装下位机控制器
                ControllerDataEDC controllerDataEDC = new ControllerDataEDC();
                controllerDataEDC.setId(id);
                controllerDataEDC.setName(sysName);
                controllerDataEDC.setAlias(alias);
                controllerDataEDC.setDescription(description);
                controllerDataEDC.setLocation(location);
                controllerDataEDC.setZone(zone);
                controllerDataEDC.setActive(active);
                controllerDataEDC.setSamplePeriod(collectPeriod);
                controllerDataEDC.setUpDataSamplePeriod(uploadPeriod);
                controllerDataEDC.setHisDataSavePeriod(savePeriod);
                controllerDataEDC.setIp(ip);
                controllerDataEDC.setGateWay(gateWay);
                controllerDataEDC.setMask(mask);
                controllerDataEDC.setServerIP(serverIp);
                controllerDataEDC.setServerPort(serverPort);

                // 同步数据到下位机
                boolean sendState = SendMsgHandler.setControllerEDC(ip, controllerDataEDC);


                if (!sendState) {
                    /**判断是否更新ip了**/
                    dataReception = isUpdateIP(controllerBegin, controller, deviceTreeList);
                    if (dataReception.getState()) {
                        /**更新缓存*/
                        updateControllerCache(controller, null);
                        return AjaxResult.success(dataReception.getMsg(), dataReception.getData());
                    } else {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return AjaxResult.success(dataReception.getMsg());
                        }
                    }
                }
                /**更新缓存*/
                updateControllerCache(controller, null);
                MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_PARAM_SET, ip);
            }
        } else {
            return AjaxResult.error("修改控制器失败！");
        }
        return AjaxResult.success("修改成功！下发成功！");
    }


    /**
     * 判断是否更新ip
     *
     * @param controllerBegin 未修改之前控制器
     * @param controllerAfter 修改之后控制器
     * @return {@code Controller }
     * @Author qindehua
     * @Date 2022/10/14
     **/
    private DataReception isUpdateIP(Controller controllerBegin, Controller controllerAfter, List<DeviceTree> deviceTreeList) {
        /**
         * ip 相等 说明是下发时下位机出现问题
         * ip 不相等 说明是人为修改IP
         * 需要手动更改 在线状态和同步状态 及树的状态
         * **/
        if (!controllerBegin.getIp().equals(controllerAfter.getIp())) {
            controllerAfter.setSynchState(0);
            controllerAfter.setOnlineState(0);
            //修改成功后 更新redis 缓存
            if (!controllerMapper.updateController(controllerAfter)) {
                return new DataReception(false, "修改控制器失败！");
            }
            List<Integer> moduleList = new ArrayList<>();//模块树ID集合
            List<Integer> pointList = new ArrayList<>();//点位树ID集合
            List<Integer> meterList = new ArrayList<>();//电表树ID集合
            //遍历  取出相应子节点树ID集合
            for (DeviceTree item : deviceTreeList) {
                /**树节点类型  为模块**/
                if (item.getDeviceNodeId() == DeviceTreeConstants.BES_MODEL) {
                    moduleList.add(item.getDeviceTreeId());
                    continue;
                }
                /**树节点类型  为电表**/
                else if (item.getDeviceNodeId() == DeviceTreeConstants.BES_AMMETER) {
                    meterList.add(item.getDeviceTreeId());
                    continue;
                }
                /**树节点类型  为点位**/
                else if (item.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_AI ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_AO ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_DI ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_DO ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_UI ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_UX
                ) {
                    pointList.add(item.getDeviceTreeId());
                    continue;
                }
            }


            //更新相应数据库
            if (!moduleList.isEmpty()) {
                if (!controllerMapper.updateSynchStateModule(moduleList)) {
                    return new DataReception(false, "修改控制器失败！");
                }
            }
            if (!pointList.isEmpty()) {
                if (!controllerMapper.updateSynchStatePoint(pointList)) {
                    return new DataReception(false, "修改控制器失败！");
                }
            }
            if (!meterList.isEmpty()) {
                if (!controllerMapper.updateSynchStateMeter(meterList)) {
                    return new DataReception(false, "修改控制器失败！");
                }
            }


            /**更新缓存*/
            updateControllerCache(controllerAfter, deviceTreeList);
            //获取最新缓存
            List<DeviceTree> deviceTreeState = deviceTreeCache.getCascadeSubordinate(controllerAfter.getDeviceTreeId());
            /**前端推送数据***/
            ReceiveMsg<List<DeviceTree>> msg = new ReceiveMsg();
            msg.setData(deviceTreeState);
            msg.setIp(controllerAfter.getIp());
            msg.setCode(0);
            // 推送消息到web客户端
            WebSocketService.broadcast(WebSocketEvent.DEVICE_STATE, msg);
        }
        return new DataReception(true, "修改成功，下发失败!", controllerAfter);
    }


    /**
     * 更新缓存控制器
     *
     * @param controller     控制器
     * @param deviceTreeList 设备树列表
     * @Author qindehua
     * @Date 2022/10/15
     **/
    public void updateControllerCache(Controller controller, List<DeviceTree> deviceTreeList) {
        // 更新redis缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, (long) controller.getDeviceTreeId(), controller);
        if (CollectionUtils.isNotEmpty(deviceTreeList)) {
            for (DeviceTree item : deviceTreeList) {
                item.setDeviceTreeStatus(0);
                //更新设备树缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) item.getDeviceTreeId(), item);
                /**树节点类型  为模块**/
                if (item.getDeviceNodeId() == DeviceTreeConstants.BES_MODEL) {
                    Module module = moduleAndPointCache.getModuleByDeviceId((long) item.getDeviceTreeId());
                    if (module == null) {
                        continue;
                    }
                    module.setSynchState(0L);
                    module.setOnlineState(0L);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, (long) item.getDeviceTreeId(), module);
                }
                /**树节点类型  为电表**/
                else if (item.getDeviceNodeId() == DeviceTreeConstants.BES_AMMETER) {
                    AthenaElectricMeter meter = meterCache.getMeterByDeviceId((long) item.getDeviceTreeId());
                    if (meter == null) {
                        continue;
                    }
                    meter.setSynchState(0L);
                    meter.setOnlineState(0L);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, (long) item.getDeviceTreeId(), meter);
                }
                /**树节点类型  为点位**/
                else if (item.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_AI ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_AO ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_DI ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_DO ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_UI ||
                        item.getDeviceNodeId() == DeviceTreeConstants.BES_UX
                ) {
                    Point point = moduleAndPointCache.getPointByDeviceId((long) item.getDeviceTreeId());
                    if (point == null) {
                        continue;
                    }
                    point.setSyncState(0);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) item.getDeviceTreeId(), point);
                }
            }

        }


    }


    /**
     * 验证
     *
     * @return {@code DataReception }
     * @Author qindehua
     * @Date 2022/09/27
     **/
    private DataReception validation(Long deviceTreeId, Integer type) {
        //判断数据是否为空
        if (deviceTreeId == null || type == null) {
            return new DataReception(true, "参数错误!");
        }
        //获取当前节点的信息
        Controller controller = controllerCache.getControllerByDeviceTreeId(deviceTreeId);
        if (controller == null) {
            return new DataReception(true, "DDC控制器不存在!");
        }
        //判断channelID 地址
        String channelID = controller.getIp();
        if (!StringUtils.hasText(channelID)) {
            return new DataReception(true, "无效的 channelID 地址!");
        }
        return new DataReception(false, controller);
    }

    /**
     * 设置时间,ddc
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @Override
    public AjaxResult setTimeDDC(Long deviceTreeId, Integer type) {
        /**校验数据*/
        DataReception dataReception = validation(deviceTreeId, type);
        if (dataReception.getState()) {
            return AjaxResult.error(dataReception.getMsg());
        }
        Controller controller = (Controller) dataReception.getData();
        String channelID = controller.getIp();
        /**DDC节点*/
        if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
            boolean sendState = SendMsgHandler.setControllerTimeDDC(channelID, DataUtil.getTimeDataObject());
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_TIME_SET, channelID);
        }
        /**照明节点*/
        else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
            boolean sendState = SendMsgHandler.setControllerTimeLDC(channelID, DataUtil.getTimeDataObject());
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }

            MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_TIME_SET, channelID);
        }
        /**采集器节点*/
        else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
            boolean sendState = SendMsgHandler.setControllerTimeEDC(channelID, DataUtil.getTimeDataObject());
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }

            MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_TIME_SET, channelID);
        } else {
            return AjaxResult.error("非法参数，下发失败!");
        }
        return AjaxResult.success("下发成功!");
    }


    /**
     * 获取时间,ddc
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @Override
    public AjaxResult getTimeDDC(Long deviceTreeId, Integer type) {
        /**校验数据*/
        DataReception dataReception = validation(deviceTreeId, type);
        if (dataReception.getState()) {
            return AjaxResult.error(dataReception.getMsg());
        }
        Controller controller = (Controller) dataReception.getData();
        String channelID = controller.getIp();
        /**DDC节点*/
        if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
            boolean sendState = SendMsgHandler.getControllerTimeDDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_TIME_GET, channelID);
        }
        /**照明节点*/
        else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
            boolean sendState = SendMsgHandler.getControllerTimeLDC(channelID);

            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }

            MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_TIME_GET, channelID);
        }
        /**采集器节点*/
        else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
            boolean sendState = SendMsgHandler.getControllerTimeEDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }

            MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_TIME_GET, channelID);
        } else {
            return AjaxResult.error("非法参数，下发失败!");
        }
        return AjaxResult.success("下发成功!");
    }

    /**
     * 重启DDC
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @Override
    public AjaxResult restartDDC(Long deviceTreeId, Integer type) {
        /**校验数据*/
        DataReception dataReception = validation(deviceTreeId, type);
        if (dataReception.getState()) {
            return AjaxResult.error(dataReception.getMsg());
        }
        Controller controller = (Controller) dataReception.getData();
        String channelID = controller.getIp();
        /**DDC节点*/
        if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
            boolean sendState = SendMsgHandler.restartControllerDDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_RESTART, channelID);
        }
        /**照明节点*/
        else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
            boolean sendState = SendMsgHandler.restartControllerLDC(channelID);

            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }

            MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_RESTART, channelID);
        }
        /**采集器节点*/
        else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
            boolean sendState = SendMsgHandler.restartControllerEDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }

            MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_RESTART, channelID);
        } else {
            return AjaxResult.error("非法参数，下发失败!");
        }
        return AjaxResult.success("下发成功!");
    }


    /**
     * 重置DDC
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @Override
    public AjaxResult resetDDC(Long deviceTreeId, Integer type) {
        /**校验数据*/
        DataReception dataReception = validation(deviceTreeId, type);
        if (dataReception.getState()) {
            return AjaxResult.error(dataReception.getMsg());
        }
        Controller controller = (Controller) dataReception.getData();
        String channelID = controller.getIp();
        /**DDC节点*/
        if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
            boolean sendState = SendMsgHandler.resetControllerDDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_RESET, channelID);
        }
        /**照明节点*/
        else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
            boolean sendState = SendMsgHandler.resetControllerLDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_RESET, channelID);
        }
        /**采集器节点*/
        else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
            boolean sendState = SendMsgHandler.resetControllerEDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_RESET, channelID);
        } else {
            return AjaxResult.error("非法参数，下发失败!");
        }
        return AjaxResult.success("下发成功!");
    }

    /**
     * 同步DDC
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @param synchronize  是否批量同步
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/10/10
     **/
    @Override
    public AjaxResult synchronizeDDC(Long deviceTreeId, Integer type, boolean synchronize) {
        //获取当前DDC的属性信息
        Controller controller = controllerCache.getControllerByDeviceTreeId(deviceTreeId);
        if (controller == null) {
            return AjaxResult.error("控制器为空！");
        }
        String sysName = controller.getSysName();      //系统名称
        String alias = controller.getAlias();        //别名
        String description = controller.getDescription();  //描述
        String location = controller.getLocation();     //安装位置
        String zone = controller.getZone();         //归属区域
        Integer id = controller.getId();           //控制器的ID,必须唯一
        Integer active = controller.getActive();       //是否启用0：不使能、1：使能 控制器的使能状态，只有使能时才会正常工作，才会与下位机通信
        String serverIp = controller.getServerIp();     //服务器ip
        String ip = controller.getIp();           //主机ip
        Integer serverPort = controller.getServerPort();   //服务器端口
        String gateWay = controller.getGateWay();      //默认网关
        String mask = controller.getMask();         //子网掩码
        Integer savePeriod = controller.getSavePeriod();         //保存周期
        Integer collectPeriod = controller.getCollectPeriod();         //采集周期
        Integer uploadPeriod = controller.getUploadPeriod();         //上传周期

        if (!StringUtils.hasText(ip) ||
                "...".equals(ip)  ||
                !StringUtils.hasText(gateWay) ||
                "...".equals(gateWay) ||
                !StringUtils.hasText(mask) ||
                "...".equals(mask) ||
                !StringUtils.hasText(serverIp) ||
                "...".equals(serverIp)) {
            return AjaxResult.error("请完善输入框信息！");
        }

        /*******记录是否进行批量同步 并存入缓存中******/
        controller.setSynchronize(synchronize);
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, deviceTreeId, controller);

        /**DDC节点*/
        if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
            ControllerDataDDC controllerData = new ControllerDataDDC();
            controllerData.setName(sysName);
            controllerData.setAlias(alias);
            controllerData.setDescription(description);
            controllerData.setLocation(location);
            controllerData.setZone(zone);
            controllerData.setId(id);
            controllerData.setActive(active);
            controllerData.setIp(ip);
            controllerData.setGateWay(gateWay);
            controllerData.setMask(mask);
            controllerData.setServerIP(serverIp);
            controllerData.setServerPort(Integer.valueOf(serverPort));
            // 同步数据到下位机
            boolean sendState = SendMsgHandler.setControllerDDC(ip, controllerData);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_PARAM_SET, ip);
        }
        /**照明节点*/
        else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
            ControllerDataLDC controllerDataLDC = new ControllerDataLDC();
            controllerDataLDC.setName(sysName);
            controllerDataLDC.setAlias(alias);
            controllerDataLDC.setDescription(description);
            controllerDataLDC.setLocation(location);
            controllerDataLDC.setZone(zone);
            controllerDataLDC.setId(id);
            controllerDataLDC.setActive(active);
            controllerDataLDC.setIp(ip);
            controllerDataLDC.setGateWay(gateWay);
            controllerDataLDC.setMask(mask);
            controllerDataLDC.setServerIP(serverIp);
            controllerDataLDC.setServerPort(Integer.valueOf(serverPort));
            // 同步数据到下位机
            boolean sendState = SendMsgHandler.setControllerLDC(ip, controllerDataLDC);

            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_PARAM_SET, ip);
        }
        /**能耗节点*/
        else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
            ControllerDataEDC controllerDataEDC = new ControllerDataEDC();
            controllerDataEDC.setName(sysName);
            controllerDataEDC.setAlias(alias);
            controllerDataEDC.setDescription(description);
            controllerDataEDC.setLocation(location);
            controllerDataEDC.setZone(zone);
            controllerDataEDC.setId(id);
            controllerDataEDC.setActive(active);
            controllerDataEDC.setIp(ip);
            controllerDataEDC.setGateWay(gateWay);
            controllerDataEDC.setMask(mask);
            controllerDataEDC.setServerIP(serverIp);
            controllerDataEDC.setServerPort(Integer.valueOf(serverPort));
            controllerDataEDC.setSamplePeriod(collectPeriod);
            controllerDataEDC.setUpDataSamplePeriod(uploadPeriod);
            controllerDataEDC.setHisDataSavePeriod(savePeriod);
            // 同步数据到下位机
            boolean sendState = SendMsgHandler.setControllerEDC(ip, controllerDataEDC);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_PARAM_SET, ip);
        } else {
            return AjaxResult.error("非法参数，下发失败!");
        }

        return AjaxResult.success("下发成功!");
    }

    /**
     * DDC远程升级
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @Override
    public AjaxResult remoteUpgradeDdc(Long deviceTreeId, Integer type) {
        /**校验数据*/
        DataReception dataReception = validation(deviceTreeId, type);
        if (dataReception.getState()) {
            return AjaxResult.error(dataReception.getMsg());
        }
        Controller controller = (Controller) dataReception.getData();
        String channelID = controller.getIp();
        /**DDC节点*/
        if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
            boolean sendState = SendMsgHandler.upgradeRemoteDDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(DDCCmd.REMOTE_UPGRADE, channelID);
        }
        /**照明节点*/
        else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
            boolean sendState = SendMsgHandler.upgradeRemoteLDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(LDCCmd.REMOTE_UPGRADE, channelID);
        }
        /**采集器节点*/
        else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
            boolean sendState = SendMsgHandler.remoteUpgradeEDC(channelID);
            if (!sendState) {
                return AjaxResult.error("下发失败!");
            }
            MsgSubPubHandler.addSubMsg(EDCCmd.REMOTE_UPGRADE, channelID);
        } else {
            return AjaxResult.error("非法参数，下发失败!");
        }
        return AjaxResult.success("下发成功!");
    }

    /**
     * 参数对比
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/23
     **/
    @Override
    public AjaxResult getDDCInfoParam(Long deviceTreeId, Integer type) {
        /**校验数据*/
        DataReception dataReception = validation(deviceTreeId, type);
        if (dataReception.getState()) {
            return AjaxResult.error(dataReception.getMsg());
        }
        Controller controller = (Controller) dataReception.getData();
        String channelID = controller.getIp();
        /**DDC节点*/
        if (type.equals(DeviceTreeConstants.BES_DDCNODE)) {
            boolean sendState = SendMsgHandler.getControllerDDC(channelID);
            if (!sendState) {
                return AjaxResult.error("获取下位机数据失败!");
            }
            MsgSubPubHandler.addSubMsg(DDCCmd.CONTROLLER_PARAM_GET, channelID);
        }
        /**照明节点*/
        else if (type.equals(DeviceTreeConstants.BES_ILLUMINE)) {
            boolean sendState = SendMsgHandler.getControllerLDC(channelID);
            if (!sendState) {
                return AjaxResult.error("获取下位机数据失败!");
            }
            MsgSubPubHandler.addSubMsg(LDCCmd.CONTROLLER_PARAM_GET, channelID);
        }
        /**采集器节点*/
        else if (type.equals(DeviceTreeConstants.BES_COLLECTORNODE)) {
            boolean sendState = SendMsgHandler.getControllerEDC(channelID);
            if (!sendState) {
                return AjaxResult.error("获取下位机数据失败!");
            }
            MsgSubPubHandler.addSubMsg(EDCCmd.CONTROLLER_PARAM_GET, channelID);
        } else {
            return AjaxResult.error("非法参数，下发失败!");
        }
        return AjaxResult.success("获取下位机数据成功!");
    }

}
