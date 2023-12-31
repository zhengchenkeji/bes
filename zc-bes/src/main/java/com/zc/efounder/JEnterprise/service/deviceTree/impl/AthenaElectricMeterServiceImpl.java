package com.zc.efounder.JEnterprise.service.deviceTree.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.zc.common.constant.RedisChannelConstants;
import com.zc.common.core.redis.pubsub.RedisPubSub;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.domain.deviceTree.dto.AthenaElectricMeterDto;
import com.zc.efounder.JEnterprise.mapper.deviceTree.AthenaElectricMeterMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.BusMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ControllerMapper;
import com.zc.efounder.JEnterprise.service.deviceTree.AthenaElectricMeterService;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.commhandler.EnergyCollectHandler;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.connect.business.constant.EDCCmd;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.business.dto.edc.AmmeterCollectParamData;
import com.zc.connect.business.dto.edc.AmmeterData;
import com.zc.connect.business.dto.edc.AmmeterParam;
import com.zc.connect.business.handler.SendMsgHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 电表信息Service业务层处理
 *
 * @author 孙山耕
 * @date 2022-09-14
 */
@Service
public class AthenaElectricMeterServiceImpl implements AthenaElectricMeterService {
    @Resource
    private AthenaElectricMeterMapper athenaElectricMeterMapper;
    @Resource
    private BusMapper busMapper;
    @Resource
    private ControllerMapper controllerMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ModuleAndPointCache moduleAndPointCache;

    private static final RedisPubSub redisPubSub = SpringUtils.getBean(RedisPubSub.class);


    @PostConstruct
    public void init() {
        /**
         * 添加数据到 redis 缓存
         */
        addMeterCache();
    }

    /**
     * 添加数据到 redis 缓存
     */
    @Override
    public void addMeterCache() {
        // 获取全部设备列表数据
        List<AthenaElectricMeter> meterList = selectAthenaElectricMeterList(null);
        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_Meter);

        if (meterList == null || meterList.isEmpty()) {

            return;
        }

        // 添加 redis 缓存数据
        meterList.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, val.getDeviceTreeId(), val);
        });
    }

    /**
     * 查询电表信息
     *
     * @param meterId 电表信息主键
     * @return 电表信息
     */
    @Override
    public AthenaElectricMeter selectAthenaElectricMeterByMeterId(Long meterId) {
        return athenaElectricMeterMapper.selectAthenaElectricMeterByMeterId(meterId);
    }

    /**
     * 查询电表信息列表
     *
     * @param athenaElectricMeter 电表信息
     * @return 电表信息
     */
    @Override
    public List<AthenaElectricMeter> selectAthenaElectricMeterList(AthenaElectricMeter athenaElectricMeter) {
        return athenaElectricMeterMapper.selectAthenaElectricMeterList(athenaElectricMeter);
    }

    /**
     * 查询电表信息列表 不分页
     *
     * @param energyCode 能源代码
     * @param parkCode   园区代码
     * @return {@code List<AthenaElectricMeterDto> }
     * @Author qindehua
     * @Date 2023/02/24
     **/
    @Override
    public List<AthenaElectricMeterDto> selectAthenaElectricMeterListInfo(String energyCode,String parkCode) {
        //采集方案下的采集参数
        Map<Long, List<ElectricParams>> map = new HashMap<>();
        //采集方案数组
        List<Long> collMethodCollection = new ArrayList<>();
        //结果数据
        List<AthenaElectricMeterDto> meterList = new ArrayList<>();


        /**********获取符合条件的   采集方案数据Ids*********/
        redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod).values().forEach(item -> {
            CollMethod collMethod = (CollMethod) item;
            if (collMethod.getEnergyCode().equals(energyCode) && collMethod.getParkCode().equals(parkCode)) {
                collMethodCollection.add((long) collMethod.getId());
            }
        });

        /**********获取符合条件的   采集方案及采集参数*********/
        collMethodCollection.forEach(data -> {
            // 采集方案下的采集参数
            List<ElectricParams> list = new ArrayList<>();

            redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl).values().forEach(item -> {
                ElectricCollRlgl electricCollRlgl = (ElectricCollRlgl) item;
                if (electricCollRlgl.getCollId() == data && "1".equals(electricCollRlgl.getStatisticalParam())) {
                    list.add(redisCache.getCacheMapValue(
                            RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, (long) electricCollRlgl.getElectricId()));
                }
            });
            //将采集方案 及放下采集参数 存入map
            map.put(data, list);
        });

        /**********根据采集方案数据  获取相应电表*********/
        redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter).values().forEach(item -> {
            AthenaElectricMeter athenaElectricMeter = (AthenaElectricMeter) item;
            //点位
            List<ElectricParams> list=new ArrayList<>();
            ElectricParams e = new ElectricParams();
            e.setCode(PointPowerParam.Point_Meter_Code);
            e.setName(PointPowerParam.Point_Meter_Name);
            list.add(e);
            if ("1".equals(athenaElectricMeter.getType())) {
                Point point = moduleAndPointCache.getPointByDeviceId(athenaElectricMeter.getDeviceTreeId());
                if (point.getEnergyCode().equals(energyCode)) {
                    AthenaElectricMeterDto athenaElectricMeterDto = new AthenaElectricMeterDto
                            (athenaElectricMeter.getMeterId().toString(), athenaElectricMeter.getAlias(),
                                    point.getSysName(), null, list,
                                    athenaElectricMeter.getType(),athenaElectricMeter.getDeviceTreeId());
                    meterList.add(athenaElectricMeterDto);
                }
            }
            //电表
            else {
                for (Long data : map.keySet()) {
                    if (athenaElectricMeter.getCollectionMethodCode().equals(data)) {
                        AthenaElectricMeterDto athenaElectricMeterDto = new AthenaElectricMeterDto
                                (athenaElectricMeter.getMeterId().toString(), athenaElectricMeter.getAlias(),
                                        athenaElectricMeter.getSysName(), athenaElectricMeter.getCollectionMethodCode(),
                                        map.get(data), athenaElectricMeter.getType(),athenaElectricMeter.getDeviceTreeId());
                        meterList.add(athenaElectricMeterDto);
                        continue;
                    }
                }
            }

        });

        return meterList;
    }

    /**
     * 新增电表信息
     *
     * @param athenaElectricMeter 电表信息
     * @return 结果
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult insertAthenaElectricMeter(AthenaElectricMeter athenaElectricMeter) {
        athenaElectricMeter.setCreateTime(DateUtils.getNowDate());
        /*//设备类型 1:楼控 2:照明  3:采集器
        athenaElectricMeter.setDeviceType("3");
        //所属节点类
        athenaElectricMeter.setDeviceNodeId("5");*/
        String alias = athenaElectricMeter.getAlias(); //电表别名
        String active = athenaElectricMeter.getActive().toString(); //使能状态0：不使能 1：使能
        String location = athenaElectricMeter.getLocation(); //安装位置
        String physicalAddress = athenaElectricMeter.getPhysicalAddress(); //物理地址
        String meterTypeCode = athenaElectricMeter.getMeterTypeCode(); //所属电表类
        String protocolTypeId = athenaElectricMeter.getProtocolTypeId().toString(); //所属通讯协议
        String collectionMethodCode = athenaElectricMeter.getCollectionMethodCode().toString(); //所属采集方案
        String functionCodeId = athenaElectricMeter.getFunctionCodeId().toString(); //所属功能码
        String commRate = athenaElectricMeter.getCommRate().toString(); //通信波特率
        String commDataBit = athenaElectricMeter.getCommDataBit().toString(); //通讯数据位
        String commParityBit = athenaElectricMeter.getCommParityBit().toString(); //通讯校验位
        String commStopBit = athenaElectricMeter.getCommStopBit().toString(); //通讯停止位
        String isStatic = athenaElectricMeter.getIsStatic().toString(); //是否静态电表
        String commPort = athenaElectricMeter.getCommPort().toString(); //通讯端口
        String rate = athenaElectricMeter.getRate().toString(); //比率
        String synchState = athenaElectricMeter.getSynchState().toString(); //同步状态0：未同步1：已同步
        String errorState = athenaElectricMeter.getErrorState().toString(); //异常状态 0：正常 1：异常
        String onlineState = athenaElectricMeter.getOnlineState().toString(); //在线状态 0：不在线1：在线
        String description = athenaElectricMeter.getDescription();//描述
        String deviceNodeId = athenaElectricMeter.getDeviceNodeId(); //所属节点类
        String sysName = athenaElectricMeter.getSysName(); //系统名称
        String deviceType = athenaElectricMeter.getDeviceType(); //设备类型 1:楼控 2:照明  3:采集器
        String deviceTreeFatherId = athenaElectricMeter.getDeviceTreeFatherId(); //父设备id

        if (!StringUtils.hasText(alias) ||
                !StringUtils.hasText(active) ||
                !StringUtils.hasText(location) ||
                !StringUtils.hasText(physicalAddress) ||
                !StringUtils.hasText(meterTypeCode) ||
                !StringUtils.hasText(protocolTypeId) ||
                !StringUtils.hasText(collectionMethodCode) ||
                !StringUtils.hasText(functionCodeId) ||
                !StringUtils.hasText(commRate) ||
                !StringUtils.hasText(commDataBit) ||
                !StringUtils.hasText(commParityBit) ||
                !StringUtils.hasText(commStopBit) ||
                !StringUtils.hasText(isStatic) ||
                !StringUtils.hasText(commPort) ||
                !StringUtils.hasText(rate) ||
                !StringUtils.hasText(synchState) ||
                !StringUtils.hasText(errorState) ||
                !StringUtils.hasText(onlineState) ||
                !StringUtils.hasText(deviceNodeId) ||
                !StringUtils.hasText(sysName) ||
                !StringUtils.hasText(deviceType) ||
                !StringUtils.hasText(deviceTreeFatherId)
        ) {
            return AjaxResult.error("请完善输入框信息");
        }

        //验证系统名称是否重复
        List<AthenaElectricMeter> athenaElectricMeters = athenaElectricMeterMapper.queryAthenaElectricMeterSysName(athenaElectricMeter);
        if (athenaElectricMeters.size() > 0) {
            return AjaxResult.error("添加失败,系统名称重复!");
        }

        //同步状态为 0未同步 ,下位机回调成功时再改为1已同步
        athenaElectricMeter.setSynchState(Long.valueOf("0"));

        //查询父级所属园区
        String park = athenaElectricMeterMapper.selectPark(athenaElectricMeter.getDeviceTreeFatherId());
        athenaElectricMeter.setPark(park);
        try {
            //添加设备树
            boolean addDeviceTree = athenaElectricMeterMapper.insertDeviceTree(athenaElectricMeter);
            if (!addDeviceTree) {
                throw new Exception();
            }

            athenaElectricMeter.setType("0");
            //插入电表表
            boolean addMeter = athenaElectricMeterMapper.insertAthenaElectricMeter(athenaElectricMeter);
            if (!addMeter) {
                throw new Exception();
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("添加失败");
        }

        //电表缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, athenaElectricMeter.getDeviceTreeId(), athenaElectricMeter);

        //从表中查出树信息
        DeviceTree deviceTreeNode = athenaElectricMeterMapper.selectDeviceTreeByDeviceTreeId(athenaElectricMeter.getDeviceTreeId().toString());
        //设备树缓存
        int deviceTreeId = deviceTreeNode.getDeviceTreeId(); //设备树id
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeId, deviceTreeNode);

        //查询采集器ip
        String controllerIP = athenaElectricMeterMapper.selectControllerIP(deviceTreeFatherId);
        if (!StringUtils.hasText(controllerIP)) {
            return AjaxResult.success("添加成功,下发失败", deviceTreeNode);
        } else {
            //下发
            Integer meterID = athenaElectricMeter.getMeterId().intValue();
            AmmeterCollectParamData ammeterCollectParam = new AmmeterCollectParamData();
            ammeterCollectParam.setMeterID(meterID); // 电表id

            AmmeterParam ammeterParam = new AmmeterParam();

            ammeterParam.setPhyAddr(physicalAddress); // 物理地址
            ammeterParam.setComAgreementType(Integer.parseInt(protocolTypeId)); // 通信协议
            ammeterParam.setActive(Integer.parseInt(active)); // 电表的使能状态
            ammeterParam.setDescription(description); // 描述
            ammeterParam.setComRate(Integer.parseInt(commRate)); // 通信波特率
            ammeterParam.setComPort(Integer.parseInt(commPort)); // 通信端口号
            ammeterParam.setComDataBit(Integer.parseInt(commDataBit)); // 通信数据位
            ammeterParam.setComParityBit(Integer.parseInt(commParityBit)); // 通信校验位
            ammeterParam.setComStopBit(Integer.parseInt(commStopBit)); // 通信停止位
            ammeterParam.setMeterType(Integer.parseInt(meterTypeCode)); // 表类型
            ammeterParam.setAlias(alias); // 表的别名
            ammeterParam.setMeterID(meterID); // 表序列号
            ammeterParam.setLocation(location); // 电表的安装位置
            ammeterParam.setFunctionCode(Integer.parseInt(functionCodeId)); // 功能码

            ammeterCollectParam.setMeterParameter(ammeterParam);


            // 同步数据到下位机
            boolean sendState = SendMsgHandler.addAmmeterEDC(controllerIP, ammeterCollectParam);

            if (!sendState) {
                return AjaxResult.success("添加成功，下发失败", deviceTreeNode);
            }
            // 添加订阅消息
            MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_ADD, controllerIP);
        }

        return AjaxResult.success("添加成功，下发成功", deviceTreeNode);

    }

    /**
     * 修改电表信息
     *
     * @param athenaElectricMeter 电表信息
     * @return 结果
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult updateAthenaElectricMeter(AthenaElectricMeter athenaElectricMeter) {
        String meterId = athenaElectricMeter.getMeterId().toString(); //电表id
        String deviceTreeId = athenaElectricMeter.getDeviceTreeId().toString(); //所属设备树
        String alias = athenaElectricMeter.getAlias(); //电表别名
        String active = athenaElectricMeter.getActive().toString(); //使能状态0：不使能 1：使能
        String location = athenaElectricMeter.getLocation(); //安装位置
        String physicalAddress = athenaElectricMeter.getPhysicalAddress(); //物理地址
        String meterTypeCode = athenaElectricMeter.getMeterTypeCode(); //所属电表类
        String protocolTypeId = athenaElectricMeter.getProtocolTypeId().toString(); //所属通讯协议
        String collectionMethodCode = athenaElectricMeter.getCollectionMethodCode().toString(); //所属采集方案
        String functionCodeId = athenaElectricMeter.getFunctionCodeId().toString(); //所属功能码
        String commRate = athenaElectricMeter.getCommRate().toString(); //通信波特率
        String commDataBit = athenaElectricMeter.getCommDataBit().toString(); //通讯数据位
        String commParityBit = athenaElectricMeter.getCommParityBit().toString(); //通讯校验位
        String commStopBit = athenaElectricMeter.getCommStopBit().toString(); //通讯停止位
        String isStatic = athenaElectricMeter.getIsStatic().toString(); //是否静态电表
        String commPort = athenaElectricMeter.getCommPort().toString(); //通讯端口
        String rate = athenaElectricMeter.getRate().toString(); //比率
        String synchState = athenaElectricMeter.getSynchState().toString(); //同步状态0：未同步1：已同步
        String errorState = athenaElectricMeter.getErrorState().toString(); //异常状态 0：正常 1：异常
        String onlineState = athenaElectricMeter.getOnlineState().toString(); //在线状态 0：不在线1：在线
        String deviceNodeId = athenaElectricMeter.getDeviceNodeId(); //所属节点类
        String sysName = athenaElectricMeter.getSysName(); //系统名称
        String deviceType = athenaElectricMeter.getDeviceType(); //设备类型 1:楼控 2:照明  3:采集器
        String deviceTreeFatherId = athenaElectricMeter.getDeviceTreeFatherId(); //父设备id
        String description = athenaElectricMeter.getDescription();//描述

        if (!StringUtils.hasText(meterId) ||
                !StringUtils.hasText(deviceTreeId) ||
                !StringUtils.hasText(alias) ||
                !StringUtils.hasText(active) ||
                !StringUtils.hasText(location) ||
                !StringUtils.hasText(physicalAddress) ||
                !StringUtils.hasText(meterTypeCode) ||
                !StringUtils.hasText(protocolTypeId) ||
                !StringUtils.hasText(collectionMethodCode) ||
                !StringUtils.hasText(functionCodeId) ||
                !StringUtils.hasText(commRate) ||
                !StringUtils.hasText(commDataBit) ||
                !StringUtils.hasText(commParityBit) ||
                !StringUtils.hasText(commStopBit) ||
                !StringUtils.hasText(isStatic) ||
                !StringUtils.hasText(commPort) ||
                !StringUtils.hasText(rate) ||
                !StringUtils.hasText(synchState) ||
                !StringUtils.hasText(errorState) ||
                !StringUtils.hasText(onlineState) ||
                !StringUtils.hasText(deviceNodeId) ||
                !StringUtils.hasText(sysName) ||
                !StringUtils.hasText(deviceType) ||
                !StringUtils.hasText(deviceTreeFatherId)
        ) {
            return AjaxResult.error("请完善输入框信息");
        }
        athenaElectricMeter.setUpdateTime(DateUtils.getNowDate());
        //同步状态为 0未同步 ,下位机回调成功时再改为1已同步
        athenaElectricMeter.setSynchState(Long.valueOf("0"));

        try {
            //修改电表信息
            boolean updateMeterFlag = athenaElectricMeterMapper.updateAthenaElectricMeter(athenaElectricMeter);
            if (!updateMeterFlag) {
                throw new Exception();
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("修改失败");
        }

        //重新加载缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, deviceTreeId, athenaElectricMeter);

        DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, deviceTreeId);
        deviceTree.setSysName(alias);
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, deviceTreeId, deviceTree);

        //查询采集器ip  用于下发
        String controllerIP = athenaElectricMeterMapper.selectControllerIP(deviceTreeFatherId);
        if (!StringUtils.hasText(controllerIP)) {
            return AjaxResult.error("修改成功,下发失败", athenaElectricMeter);
        }
        Integer meterID = athenaElectricMeter.getMeterId().intValue();
        AmmeterCollectParamData ammeterCollectParam = new AmmeterCollectParamData();
        ammeterCollectParam.setMeterID(meterID); // 电表id

        AmmeterParam ammeterParam = new AmmeterParam();
        ammeterParam.setPhyAddr(physicalAddress); // 物理地址
        ammeterParam.setComAgreementType(Integer.parseInt(protocolTypeId)); // 通信协议
        ammeterParam.setActive(Integer.parseInt(active)); // 电表的使能状态
        ammeterParam.setDescription(description); // 描述
        ammeterParam.setComRate(Integer.parseInt(commRate)); // 通信波特率
        ammeterParam.setComPort(Integer.parseInt(commPort)); // 通信端口号
        ammeterParam.setComDataBit(Integer.parseInt(commDataBit)); // 通信数据位
        ammeterParam.setComParityBit(Integer.parseInt(commParityBit)); // 通信校验位
        ammeterParam.setComStopBit(Integer.parseInt(commStopBit)); // 通信停止位
        ammeterParam.setMeterType(Integer.parseInt(meterTypeCode)); // 表类型
        ammeterParam.setAlias(alias); // 表的别名
        ammeterParam.setMeterID(meterID); // 表序列号
        ammeterParam.setLocation(location); // 电表的安装位置
        ammeterParam.setFunctionCode(Integer.parseInt(functionCodeId)); // 功能码

        ammeterCollectParam.setMeterParameter(ammeterParam);

        // 删除电表
        if (!SendMsgHandler.deleteAmmeterEDC(controllerIP, meterID)) {
            return AjaxResult.success("保存成功，下发失败", athenaElectricMeter);
        }

        // 添加订阅消息
        MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_DELETE, controllerIP);

        // 同步数据到下位机
        if (!SendMsgHandler.addAmmeterEDC(controllerIP, ammeterCollectParam)) {
            return AjaxResult.success("保存成功，下发失败", athenaElectricMeter);
        }

        // 添加订阅消息
        MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_ADD, controllerIP);

        return AjaxResult.success("保存成功，下发成功", athenaElectricMeter);
    }

    /**
     * 批量删除电表信息
     *
     * @param meterIds 需要删除的电表信息主键
     * @return 结果
     */
    @Override
    public int deleteAthenaElectricMeterByMeterIds(Long[] meterIds) {
        return athenaElectricMeterMapper.deleteAthenaElectricMeterByMeterIds(meterIds);
    }


    /**
     * 获取采集方案列表
     *
     * @return 结果
     */
    @Override
    public List<CollMethod> getCollectionMethodList(String parkId) {
        return athenaElectricMeterMapper.getCollectionMethodList(parkId);
    }

    /**
     * 查询电表字典
     *
     * @return 电表信息字典集合
     */
    @Override
    public List<AthenaElectricMeter> selectAthenaElectricMeterDicData() {
        return athenaElectricMeterMapper.selectAthenaElectricMeterDicData();
    }

    /**
     * @param athenaElectricMeter 电表信息
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 同步电表
     * @author liuwenge
     * @date 2022/9/23 8:52
     */
    @Override
    public AjaxResult syncMeter(AthenaElectricMeter athenaElectricMeter) {

        String fatherId = athenaElectricMeter.getDeviceTreeFatherId();
        String deviceTreeId = athenaElectricMeter.getDeviceTreeId().toString();

        if (!StringUtils.hasText(deviceTreeId) || !StringUtils.hasText(fatherId)) {
            return AjaxResult.error("数据异常");
        }

        //取出电表信息
        AthenaElectricMeter meterInfo = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, deviceTreeId);
        if (meterInfo == null){
            AthenaElectricMeter athenaElectricMeter1 = athenaElectricMeterMapper.selectAthenaElectricMeterByMeterId(athenaElectricMeter.getMeterId());
            if (athenaElectricMeter1 != null){
                meterInfo = athenaElectricMeter1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter,deviceTreeId,athenaElectricMeter1);
            } else {
                return AjaxResult.error("数据异常");
            }
        }
        String meterId = meterInfo.getMeterId().toString();
        String alias = meterInfo.getAlias(); //电表别名
        String active = meterInfo.getActive().toString(); //使能状态0：不使能 1：使能
        String location = meterInfo.getLocation(); //安装位置
        String physicalAddress = meterInfo.getPhysicalAddress(); //物理地址
        String meterTypeCode = meterInfo.getMeterTypeCode(); //所属电表类
        String protocolTypeId = meterInfo.getProtocolTypeId().toString(); //所属通讯协议
        String functionCodeId = meterInfo.getFunctionCodeId().toString(); //所属功能码
        String commRate = meterInfo.getCommRate().toString(); //通信波特率
        String commDataBit = meterInfo.getCommDataBit().toString(); //通讯数据位
        String commParityBit = meterInfo.getCommParityBit().toString(); //通讯校验位
        String commStopBit = meterInfo.getCommStopBit().toString(); //通讯停止位
        String commPort = meterInfo.getCommPort().toString(); //通讯端口
        String description = meterInfo.getDescription();//描述
        //获取采集器ip  用于下发
        String controllerIP = athenaElectricMeterMapper.selectControllerIP(fatherId);
        if (!StringUtils.hasText(controllerIP)) {
            return AjaxResult.error("数据异常");
        }

        AmmeterCollectParamData ammeterCollectParam = new AmmeterCollectParamData();
        ammeterCollectParam.setMeterID(Integer.parseInt(meterId)); // 电表id

        AmmeterParam ammeterParam = new AmmeterParam();

        ammeterParam.setPhyAddr(physicalAddress); // 物理地址
        ammeterParam.setComAgreementType(Integer.parseInt(protocolTypeId)); // 通信协议
        ammeterParam.setActive(Integer.parseInt(active)); // 电表的使能状态
        ammeterParam.setDescription(description); // 描述
        ammeterParam.setComRate(Integer.parseInt(commRate)); // 通信波特率
        ammeterParam.setComPort(Integer.parseInt(commPort)); // 通信端口号
        ammeterParam.setComDataBit(Integer.parseInt(commDataBit)); // 通信数据位
        ammeterParam.setComParityBit(Integer.parseInt(commParityBit)); // 通信校验位
        ammeterParam.setComStopBit(Integer.parseInt(commStopBit)); // 通信停止位
        ammeterParam.setMeterType(Integer.parseInt(meterTypeCode)); // 表类型
        ammeterParam.setAlias(alias); // 表的别名
        ammeterParam.setMeterID(Integer.parseInt(meterId)); // 表序列号
        ammeterParam.setLocation(location); // 电表的安装位置
        ammeterParam.setFunctionCode(Integer.parseInt(functionCodeId)); // 功能码

        ammeterCollectParam.setMeterParameter(ammeterParam);

        // 删除电表
        if (!SendMsgHandler.deleteAmmeterEDC(controllerIP, Integer.parseInt(meterId))) {
            return AjaxResult.error("下发失败");
        }

        // 添加订阅消息
        MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_DELETE, controllerIP);

        // 同步数据到下位机
        if (!SendMsgHandler.addAmmeterEDC(controllerIP, ammeterCollectParam)) {
            return AjaxResult.error("下发失败");
        }

        // 添加订阅消息
        MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_ADD, controllerIP);

        return AjaxResult.success("下发成功");
    }

    /**
     * @param deviceTreeId
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 获取电表参数
     * @author liuwenge
     * @date 2022/10/8 9:03
     */
    @Override
    public AjaxResult getMeterInfoParam(String deviceTreeId) {

        if (!StringUtils.hasText(deviceTreeId)) {
            return AjaxResult.error("数据异常");
        }

        //从缓存中获取电表信息
        AthenaElectricMeter athenaElectricMeter = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, deviceTreeId);
        if (athenaElectricMeter == null) {
            AthenaElectricMeter athenaElectricMeter1 = athenaElectricMeterMapper.selectAthenaElectricMeterByDeviceTreeId(deviceTreeId);
            if (athenaElectricMeter1 != null){
                athenaElectricMeter = athenaElectricMeter1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, deviceTreeId,athenaElectricMeter1);
            } else {
                return AjaxResult.error("电表数据异常");
            }
        }
        //电表id
        Integer meterId = athenaElectricMeter.getMeterId().intValue();

        //总线
        Bus bus = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus, athenaElectricMeter.getDeviceTreeFatherId());
        if (bus == null) {
            Bus bus1 = busMapper.selectBusInfoByDeviceTreeId(athenaElectricMeter.getDeviceTreeFatherId());
            if (bus1 != null){
                bus = bus1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus,bus1.getDeviceTreeId(),bus1);
            } else {
                return AjaxResult.error("总线数据异常");
            }
        }

        //采集器
        Controller controller = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, bus.getDeviceTreeFatherId());
        if (controller == null) {
            Controller controller1 = controllerMapper.selectControllerInfoByDeviceTreeId(bus.getDeviceTreeFatherId());
            if (controller1 != null){
                controller = controller1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller,(long) controller1.getDeviceTreeId(),controller1);
            } else {
                return AjaxResult.error("采集器数据异常");
            }
        }
        //采集器ip
        String controllerIP = controller.getIp();

        //下发
        boolean sendState = SendMsgHandler.getAmmeterEDC(controllerIP, meterId);

        if (!sendState) {
            return AjaxResult.error("下发失败");
        }

        MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_GET, controllerIP);

        return AjaxResult.success("下发成功");

    }

    /**
     * @param deviceTreeId
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 获取电表电能参数
     * @author liuwenge
     * @date 2022/10/8 10:24
     */
    @Override
    public AjaxResult getElectricParams(String deviceTreeId) {

        if (!StringUtils.hasText(deviceTreeId)) {
            return AjaxResult.error("数据异常");
        }
        List<Map<String, Object>> electricParams = athenaElectricMeterMapper.getElectricParams(deviceTreeId);

        return AjaxResult.success(electricParams);
    }

    /**
     * @param deviceTreeId
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 获取电表实时数据
     * @author liuwenge
     * @date 2022/10/8 11:58
     */
    @Override
    public AjaxResult getMeterRealTimeData(String deviceTreeId) {
        if (!StringUtils.hasText(deviceTreeId)) {
            return AjaxResult.error("数据异常");
        }

        //从缓存中获取电表信息
        AthenaElectricMeter athenaElectricMeter = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, deviceTreeId);
        if (athenaElectricMeter == null) {
            AthenaElectricMeter athenaElectricMeter1 = athenaElectricMeterMapper.selectAthenaElectricMeterByDeviceTreeId(deviceTreeId);
            if (athenaElectricMeter1 != null){
                athenaElectricMeter = athenaElectricMeter1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, deviceTreeId,athenaElectricMeter1);
            } else {
                return AjaxResult.error("电表数据异常");
            }
        }
        //电表id
        Integer meterId = athenaElectricMeter.getMeterId().intValue();

        //总线
        Bus bus = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus, athenaElectricMeter.getDeviceTreeFatherId());
        if (bus == null) {
            Bus bus1 = busMapper.selectBusInfoByDeviceTreeId(athenaElectricMeter.getDeviceTreeFatherId());
            if (bus1 != null){
                bus = bus1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus,bus1.getDeviceTreeId(),bus1);
            } else {
                return AjaxResult.error("总线数据异常");
            }
        }

        //采集器
        Controller controller = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, bus.getDeviceTreeFatherId());
        if (controller == null) {
            Controller controller1 = controllerMapper.selectControllerInfoByDeviceTreeId(bus.getDeviceTreeFatherId());
            if (controller1 != null){
                controller = controller1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller,(long) controller1.getDeviceTreeId(),controller1);
            } else {
                return AjaxResult.error("采集器数据异常");
            }
        }
        //采集器ip
        String controllerIP = controller.getIp();

        if (!StringUtils.hasText(controllerIP)) {
            return AjaxResult.error("无效的 采集器ip");
        }


        boolean sendState = SendMsgHandler.getAmmeterRealtimeDataEDC(controllerIP, meterId);

        if (!sendState) {
            return AjaxResult.error("下发失败");
        }

        MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_REALTIME_DATA_GET, controllerIP);

        return AjaxResult.success("下发成功");
    }

    /**
     * @param deviceTreeId 设备树id
     * @param selectDay    查询日期
     * @return com.ruoyi.common.core.domain.AjaxResult
     * @Description 获取历史能耗数据
     * @author liuwenge
     * @date 2022/10/8 14:51
     */
    @Override
    public AjaxResult getMeterHistoryData(String deviceTreeId, Integer selectDay) {
        if (!StringUtils.hasText(deviceTreeId) || null == selectDay) {
            return AjaxResult.error("参数错误");
        }

        //从缓存中获取电表信息
        AthenaElectricMeter athenaElectricMeter = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, deviceTreeId);
        if (athenaElectricMeter == null) {
            AthenaElectricMeter athenaElectricMeter1 = athenaElectricMeterMapper.selectAthenaElectricMeterByDeviceTreeId(deviceTreeId);
            if (athenaElectricMeter1 != null){
                athenaElectricMeter = athenaElectricMeter1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, deviceTreeId,athenaElectricMeter1);
            } else {
                return AjaxResult.error("电表数据异常");
            }
        }
        //电表id
        Integer meterId = athenaElectricMeter.getMeterId().intValue();

        //总线
        Bus bus = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus, athenaElectricMeter.getDeviceTreeFatherId());
        if (bus == null) {
            Bus bus1 = busMapper.selectBusInfoByDeviceTreeId(athenaElectricMeter.getDeviceTreeFatherId());
            if (bus1 != null){
                bus = bus1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Bus,bus1.getDeviceTreeId(),bus1);
            } else {
                return AjaxResult.error("总线数据异常");
            }
        }

        //采集器
        Controller controller = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller, bus.getDeviceTreeFatherId());
        if (controller == null) {
            Controller controller1 = controllerMapper.selectControllerInfoByDeviceTreeId(bus.getDeviceTreeFatherId());
            if (controller1 != null){
                controller = controller1;
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller,(long) controller1.getDeviceTreeId(),controller1);
            } else {
                return AjaxResult.error("采集器数据异常");
            }
        }
        //采集器ip
        String controllerIP = controller.getIp();
        if (!StringUtils.hasText(controllerIP)) {
            return AjaxResult.error("无效的 采集器ip");
        }

        boolean sendState = SendMsgHandler.getAmmeterHistoryDataEDC(controllerIP, meterId, selectDay);

        if (!sendState) {
            return AjaxResult.error("下发失败");
        }

        MsgSubPubHandler.addSubMsg(EDCCmd.AMMETER_HISTORY_DATA_GET, controllerIP);

        return AjaxResult.success("下发成功");
    }

    @Override
    public void testData(Integer meterId, Integer collectCount, String electricData) {

        ReceiveMsg receiveMsg = new ReceiveMsg();
        AmmeterData ammeterData = new AmmeterData();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = format.format(date);
        Integer year = Integer.parseInt(createTime.substring(2, 4));
        Integer month = Integer.parseInt(createTime.substring(5, 7));
        Integer day = Integer.parseInt(createTime.substring(8, 10));
        Integer hour = Integer.parseInt(createTime.substring(11, 13));
        Integer minute = Integer.parseInt(createTime.substring(14, 16));
        Integer second = Integer.parseInt(createTime.substring(17, 19));
        ammeterData.setMeterID(meterId);
        ammeterData.setCollectCount(collectCount);
        ammeterData.setElectricData(electricData);
        ammeterData.setDateYear(year);
        ammeterData.setDateMonth(month);
        ammeterData.setDateDay(day);
        ammeterData.setTimeHour(hour);
        ammeterData.setTimeMinute(minute);
        ammeterData.setTimeSecond(second);
        List<AmmeterData> data = new ArrayList<>();
        data.add(ammeterData);
        receiveMsg.setIndex(89);
        receiveMsg.setData(data);
        receiveMsg.setCode(0);
        receiveMsg.setIp("10.168.56.89");

        EnergyCollectHandler.ammeterDataHandle(receiveMsg);
    }

//    @Scheduled(cron = "0 */1 * * * ?")
    public void testDataTask() {
        //能耗电表测试数据上传 gaojikun
        ReceiveMsg receiveMsg = new ReceiveMsg();
        AmmeterData ammeterData = new AmmeterData();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = format.format(date);
        Integer year = Integer.parseInt(createTime.substring(2, 4));
        Integer month = Integer.parseInt(createTime.substring(5, 7));
        Integer day = Integer.parseInt(createTime.substring(8, 10));
        Integer hour = Integer.parseInt(createTime.substring(11, 13));
        Integer minute = Integer.parseInt(createTime.substring(14, 16));
        Integer second = Integer.parseInt(createTime.substring(17, 19));
        ammeterData.setMeterID(182);
        ammeterData.setCollectCount(5);
        ammeterData.setElectricData("79,79,79,79,79");
        ammeterData.setDateYear(year);
        ammeterData.setDateMonth(month);
        ammeterData.setDateDay(day);
        ammeterData.setTimeHour(hour);
        ammeterData.setTimeMinute(minute);
        ammeterData.setTimeSecond(second);
//        List<AmmeterData> data = new ArrayList<>();
//        data.add(ammeterData);
        receiveMsg.setIndex(89);
//        receiveMsg.setData(data);
        receiveMsg.setData(ammeterData);
        receiveMsg.setCode(0);
        receiveMsg.setIp("10.168.56.89");

//        EnergyCollectHandler.ammeterDataHandle(receiveMsg);

        Gson gson = new Gson();
        String dataJson = gson.toJson(receiveMsg, new TypeToken<ReceiveMsg<AmmeterData>>() {}.getType());

        redisPubSub.publish(RedisChannelConstants.Meter_PUB_SUB_INFO,dataJson);
    }


}

