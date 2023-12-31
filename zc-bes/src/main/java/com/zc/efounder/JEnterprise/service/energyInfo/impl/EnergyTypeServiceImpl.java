package com.zc.efounder.JEnterprise.service.energyInfo.impl;

import java.util.*;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.deviceTree.BuildNode;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.mapper.energyCollection.ElectricParamsMapper;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.mapper.energyCollection.CollMethodMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfigList;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.mapper.energyInfo.EnergyTypeMapper;
import com.zc.efounder.JEnterprise.service.energyInfo.EnergyTypeService;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static com.ruoyi.common.core.domain.AjaxResult.error;
import static com.ruoyi.common.core.domain.AjaxResult.success;

/**
 * 能源类型Service业务层处理
 *
 * @author gaojikun
 * @date 2022-09-07
 */
@Service
public class EnergyTypeServiceImpl /*extends AssociatedService*/ implements EnergyTypeService/*, ForeignKeyService*/ {
    @Autowired
    private EnergyTypeMapper energyTypeMapper;
    @Autowired
    private ElectricParamsMapper electricParamsMapper;
    @Autowired
    private CollMethodMapper collMethodMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ModuleAndPointCache moduleAndPointCache;

    @Resource
    private DeviceTreeCache deviceTreeCache;

    @PostConstruct
    public void init() {
        /*
          添加数据到 redis 缓存
         */
        addEnergyTypeCache();
        addEnergyConfigCache();
    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addEnergyTypeCache() {
        // 获取全部采集参数定义列表数据
        List<EnergyType> energyTypes = selectEnergyTypeList(null);


        // 清楚 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
        if (energyTypes == null || energyTypes.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        energyTypes.forEach(val -> redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType, val.getGuid(), val));
    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addEnergyConfigCache() {
        // 获取全部园区能耗类型关联列表数据
        List<EnergyConfig> energyConfigs = selectParkEnergyRlglList(null);

        // 清楚 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig);

        if (energyConfigs == null || energyConfigs.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        energyConfigs.forEach(val -> redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig, val.getId(), val));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:guid
     * @Description:获取能源类型详细信息
     * @Return:EnergyType
     */
    @Override
    public EnergyType selectEnergyTypeByGuid(String guid) {
        return energyTypeMapper.selectEnergyTypeByGuid(guid);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:查询能源类型列表
     * @Return:List<EnergyType>
     */
    @Override
    public List<EnergyType> selectEnergyTypeList(EnergyType energyType) {
        return energyTypeMapper.selectEnergyTypeList(energyType);
    }

    /**
     * 查询园区能耗类型关联列表
     *
     * @return
     */
    public List<EnergyConfig> selectParkEnergyRlglList(EnergyConfig energyConfig) {
        return energyTypeMapper.selectParkEnergyRlglList(energyConfig);
    }

    /**
     * 查询能耗列表
     *
     * @param code
     * @return 能源类型
     * @Author:gaojikun
     */
    @Override
    public List<Map<String, Object>> selectEnergyConfigList(String code) {
//        List<Map<String, Object>> eneryMap = new ArrayList<>();
//        //查询能耗类型缓存
//        Map<String, EnergyType> EnergyTypeCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
//        Collection<EnergyType> list = EnergyTypeCacheMap.values();
//        if (list.size() > 0) {
//            for (EnergyType e : list) {
//                //重新定义map
//                Map<String, Object> newMap = new HashMap<String, Object>();
//                Boolean rlglmark = false;//初始化布尔类型为false
//                //查询园区能耗类型关联缓存
//                Map<String, EnergyConfig> EnergyConfigCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig);
//                Collection<EnergyConfig> configList = EnergyConfigCacheMap.values();
//                //查询是否背该园区关联
//                if (configList.size() > 0) {
//                    for (EnergyConfig config : configList) {
//                        if (config.getParkCode().equals(code) && config.getEnergyCode().equals(e.getCode())) {
//                            rlglmark = true;
//                            break;
//                        }
//                    }
//                }
//
//                //map中重新set值
//                newMap.put("rlgl", rlglmark);
//                newMap.put("code", e.getCode());
//                newMap.put("name", e.getName());
//                newMap.put("coalAmount", e.getCoalAmount());
//                newMap.put("price", e.getPrice());
//                newMap.put("co2", e.getCo2());
//                eneryMap.add(newMap);
//            }
//        }
//        return eneryMap;
        List<Map<String, Object>> eneryMap = energyTypeMapper.selectEnergyConfigList(code);

        for (int i = 0; i < eneryMap.size(); i++) {
            //重新定义map
            Map<String, Object> newMap = new HashMap<String, Object>();

            String fnybh = (String) eneryMap.get(i).get("code");//能源编号
            String fnymc = (String) eneryMap.get(i).get("name");//能源名称
            String fPrice = String.valueOf(eneryMap.get(i).get("price"));//单价
            String fCoalAmount = String.valueOf(eneryMap.get(i).get("coal_amount"));//耗煤量
            String fCo2 = String.valueOf(eneryMap.get(i).get("co2"));//二氧化碳

            Object rl = eneryMap.get(i).get("F_PARK_ENENGGY_RLGL");//关系表中勾选标记

            String rlgl = (String) rl;//转换成string类型
            Boolean rlglmark = false;//初始化布尔类型为false
            //判断关系标记是否为空，当不为空的情况下，布尔标记为true,则对应表格中勾选状态
            if (rlgl != null) {//判断非空  isEmpty()  !=""  !=null
                rlglmark = true;
            }
            //map中重新set值
            newMap.put("rlgl", rlglmark);
            newMap.put("code", fnybh);
            newMap.put("name", fnymc);
            newMap.put("coalAmount", fCoalAmount);
            newMap.put("price", fPrice);
            newMap.put("co2", fCo2);
            eneryMap.set(i, newMap);
        }

        return eneryMap;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:新增能源类型
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult insertEnergyType(EnergyType energyType) {
        if (energyType.getCode() == null || StringUtils.isEmpty(energyType.getCode()) || energyType.getName() == null
                || StringUtils.isEmpty(energyType.getName()) || energyType.getUnit() == null || StringUtils.isEmpty(energyType.getUnit())
                || energyType.getCo2() == null || energyType.getPrice() == null || energyType.getCoalAmount() == null) {
            return AjaxResult.error("参数错误");
        }
        //获取缓存数据
        Map<String, EnergyType> EnergyTypeCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
        Collection<EnergyType> list = EnergyTypeCacheMap.values();
        for (EnergyType e : list) {
            if (e.getName().equals(energyType.getName()) || e.getCode().equals(energyType.getCode())) {
                return AjaxResult.error("编号/名称重复,请检查");
            }
        }
        energyType.setGuid(UUID.randomUUID().toString().replaceAll("-", ""));
        energyType.setCreateTime(DateUtils.getNowDate());
        boolean isInsertEnergyType = energyTypeMapper.insertEnergyType(energyType);
        if (isInsertEnergyType) {
            //加入缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType, energyType.getGuid(), energyType);
            return success("添加成功");
        }
        return error("添加失败");
    }

    /**
     * 新增园区能耗
     *
     * @return AjaxResult
     * @Author:gaojikun
     */
    @Override
    public AjaxResult insertEnergyConfig(EnergyConfigList energyConfigList) {
        if(energyConfigList.getEnergyConfigList() == null || energyConfigList.getEnergyConfigList().size() == 0){
            return AjaxResult.error("参数错误");
        }
        List<EnergyConfig> energyConfigs = energyConfigList.getEnergyConfigList();
        a:
        for (EnergyConfig energyConfig : energyConfigs) {
            //添加
            if ("1".equals(energyConfig.getCheck())) {
                Collection<Object> BranchConfigRedis = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig).values();
                if (BranchConfigRedis.size() > 0) {
                    b:
                    for (Object value : BranchConfigRedis) {
                        EnergyConfig e = (EnergyConfig) value;
                        //缓存中有则直接返回
                        if (e.getParkCode().equals(energyConfig.getParkCode()) && e.getEnergyCode().equals(energyConfig.getEnergyCode())) {
                            continue a;
                        }
                    }
                }
                energyConfig.setCreateTime(DateUtils.getNowDate());
                energyConfig.setUpdateTime(DateUtils.getNowDate());
                int addNum = energyTypeMapper.addpark_energytype(energyConfig);
                if (addNum > 0) {
                    //添加缓存
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig, energyConfig.getId(), energyConfig);
                    continue;
                } else {
                    return error("更新园区能源配置失败");
                }
            } else {
                Collection<Object> BranchConfigRedis = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig).values();
                if (BranchConfigRedis.size() > 0) {
                    //删除
                    for (Object value : BranchConfigRedis) {
                        EnergyConfig e = (EnergyConfig) value;
                        //缓存中有则删除
                        if (e.getParkCode().equals(energyConfig.getParkCode()) && e.getEnergyCode().equals(energyConfig.getEnergyCode())) {
                            //获取采集方案下的所有采集参数Id
                            for (Object values : redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree).values()) {
                                DeviceTree deviceTree = (DeviceTree) values;
                                //AI AO VAI
                                if (deviceTree.getPark() != null && !"".equals(deviceTree.getPark()) && deviceTree.getPark().equals(energyConfig.getParkCode()) &&
                                        (deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_AI || deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_AO ||
                                                deviceTree.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT)
                                ) {
                                    int treeId = deviceTree.getDeviceTreeId();
                                    Point point = moduleAndPointCache.getPointByDeviceId(Long.parseLong(String.valueOf(treeId)));
                                    if (point.getEnergyCode() != null && !"".equals(point.getEnergyCode()) && point.getEnergyCode().equals(energyConfig.getEnergyCode())) {
                                        return error("更新园区能源配置失败，点位已关联改能源配置");
                                    }
                                }
                            }
                            int delNum = energyTypeMapper.delpark_energytype(energyConfig);
                            if (delNum > 0) {
                                //删除缓存
                                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig, e.getId());
                                break;
                            } else {
                                return error("更新园区能源配置失败");
                            }
                        }
                    }
                } else {
                    continue;
                }
            }
        }
        return success("更新园区能源配置成功");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:修改能源类型
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult updateEnergyType(EnergyType energyType) {
        if (energyType.getCode() == null || StringUtils.isEmpty(energyType.getCode()) ||
                energyType.getName() == null || StringUtils.isEmpty(energyType.getName()) ||
                energyType.getUnit() == null || StringUtils.isEmpty(energyType.getUnit()) ||
                energyType.getCo2() == null ||
                StringUtils.isEmpty(energyType.getGuid()) ||
                energyType.getPrice() == null ||
                energyType.getCoalAmount() == null) {
            return AjaxResult.error("参数错误");
        }
        //获取缓存数据
        Map<String, EnergyType> EnergyTypeCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
        Collection<EnergyType> list = EnergyTypeCacheMap.values();
        for (EnergyType e : list) {
            if ((e.getName().equals(energyType.getName()) || e.getCode().equals(energyType.getCode())) && !e.getGuid().equals(energyType.getGuid())) {
                return AjaxResult.error("编号/名称重复,请检查");
            }
        }
        energyType.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdateEnergyType = energyTypeMapper.updateEnergyType(energyType);
        if (isUpdateEnergyType) {
            // 更新记录缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType, energyType.getGuid(), energyType);
            return success("修改成功");
        }
        return error("修改失败");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:guids
     * @Description:删除能源类型
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult deleteEnergyTypeByGuids(String[] guids) {
        if (guids == null || guids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        for (String str : guids) {
            EnergyType besEnergyType = energyTypeMapper.selectEnergyTypeByGuid(str);
            //具有外键所关联的信息，无法删除息
            List<Map<String, Object>> enerys = energyTypeMapper.selectEnergyConfigListByCode("", besEnergyType.getCode());
            if (enerys.size() > 0) {
//                throw new ServiceException("能耗类型已被园区能耗配置关联，请先删除相关信息", HttpStatus.BAD_REQUEST);
                return error("能耗类型已被园区能耗配置关联，请先删除相关信息");
            }
            ElectricParams electricParam = new ElectricParams();
            electricParam.setEnergyCode(besEnergyType.getCode());
            List<ElectricParams> electricParams = electricParamsMapper.selectElectricParamsList(electricParam);
            if (electricParams.size() > 0) {
                return error("能耗类型已被采集参数定义关联，请先删除相关信息");
            }
            CollMethod collMethod = new CollMethod();
            collMethod.setEnergyCode(besEnergyType.getCode());
            List<CollMethod> collMethods = collMethodMapper.selectCollMethodList(collMethod);
            if (collMethods.size() > 0) {
                return error("能耗类型已被采集方案定义关联，请先删除相关信息");
            }


        }
        boolean isDeleteEnergyTypeByGuids = energyTypeMapper.deleteEnergyTypeByGuids(guids);
        if (isDeleteEnergyTypeByGuids) {
            // 删除当前记录缓存
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType, guids);
            return success("删除成功");
        }
        return error("删除失败");

    }

    /**
     * 删除能源类型信息
     *
     * @param guid 能源类型主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteEnergyTypeByGuid(String guid) {
        if (guid == null) {
            return AjaxResult.error("参数错误");
        }
        EnergyType besEnergyType = energyTypeMapper.selectEnergyTypeByGuid(guid);
        boolean isDeleteEnergyTypeByGuid = energyTypeMapper.deleteEnergyTypeByGuid(guid);
        if (isDeleteEnergyTypeByGuid) {
            // 删除当前记录缓存
//            energyTypeCache.deleteOneEnergyTypeCache(besEnergyType.getCode());
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType, guid);
            return success("删除成功");
        }
        return error("删除失败");
    }

    /**
     * 查询该园区下所有能耗类型
     *
     * @param parkCode 园区code
     * @return {@code List<EnergyType> }
     * @Author qindehua
     * @Date 2022/12/21
     **/
    @Override
    public List<EnergyType> findAllByParkCode(String parkCode) {
        if (parkCode == null) {
            return null;
        }
        Map<String, EnergyConfig> energyConfigMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig);
        //园区下 能源编号list
        List<String> codeList = new ArrayList<>();
        for (EnergyConfig value : energyConfigMap.values()) {
            if (parkCode.equals(value.getParkCode())) {
                codeList.add(value.getEnergyCode());
            }
        }
        //能源列表list
        List<EnergyType> list = new ArrayList<>();
        if (codeList.size() > 0) {
            Map<String, EnergyType> energyTypeMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
            for (EnergyType value : energyTypeMap.values()) {
                if (codeList.contains(value.getCode())) {
                    list.add(value);
                }
            }
        }
        if (list.size() > 0) {
            list.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));
        }
        return list;
    }

    /**
     * 查询所有能耗类型
     *
     * @return {@code List<EnergyType> }
     * @Author gaojikun
     * @Date 2022/12/21
     **/
    @Override
    public List<EnergyType> allEnergyTypeList(Point point) {
        List<EnergyType> energyTypeList = new ArrayList<>();
        if (point.getTreeId() == null) {
            return energyTypeList;
        }
        Point pointByDeviceId = moduleAndPointCache.getPointByDeviceId(point.getTreeId());
        if (pointByDeviceId == null) {
            BuildNode b = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Vpoint, point.getTreeId());
            if (b == null || b.getNodeType() == null || !b.getNodeType().equals(DeviceTreeConstants.BES_VPOINTNOPROPERTY)) {
                return energyTypeList;
            }
        }
        DeviceTree deviceTreeByDeviceTreeId = deviceTreeCache.getDeviceTreeByDeviceTreeId(point.getTreeId());

        Map<String, EnergyType> energyTypeCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
        Map<String, EnergyConfig> energyConfigCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyConfig);
        if (energyConfigCache == null || energyConfigCache.size() == 0) {
            return energyTypeList;
        }
        energyConfigCache.values().forEach(item -> {
            if (deviceTreeByDeviceTreeId.getPark().equals(item.getParkCode())) {
                energyTypeCache.values().forEach(itemtype -> {
                    if (itemtype.getCode().equals(item.getEnergyCode())) {
                        energyTypeList.add(itemtype);
                    }
                });
            }
        });
        //按照code重新排序
        energyTypeList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));
        return energyTypeList;
    }

}
