package com.zc.efounder.JEnterprise.service.energyCollection.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.mapper.energyCollection.ElectricParamsMapper;
import com.zc.efounder.JEnterprise.service.energyCollection.ElectricParamsService;
import com.ruoyi.common.utils.DateUtils;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.mapper.energyCollection.ElectricCollRlglMapper;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTactics;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static com.ruoyi.common.core.domain.AjaxResult.error;
import static com.ruoyi.common.core.domain.AjaxResult.success;

/**
 * 采集参数定义Service业务层处理
 *
 * @author ruoyi
 * @date 2022-09-07
 */
@Service
public class ElectricParamsServiceImpl implements ElectricParamsService {
    @Resource
    private ElectricParamsMapper electricParamsMapper;
    @Resource
    private ElectricCollRlglMapper electricCollRlglMapper;

    /*@Autowired
    private ElectricParamsCache electricParamsCache;*/

    @Resource
    private RedisCache redisCache;

    @PostConstruct
    public void init() {
        /*
          添加数据到 redis 缓存
         */
        addElectricParamsCache();
    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addElectricParamsCache() {
        // 获取全部采集参数定义列表数据
        List<ElectricParams> electricParams = selectElectricParamsList(null);
        ElectricParams e = new ElectricParams();
        e.setId(0L);
        e.setCode(PointPowerParam.Point_Meter_Code);
        e.setName(PointPowerParam.Point_Meter_Name);
        electricParams.add(e);

        // 清楚 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams);

        if (electricParams == null || electricParams.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        electricParams.forEach(val -> redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, val.getId(), val));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:获取采集参数详细信息
     * @Return:ElectricParams
     */
    @Override
    public ElectricParams selectElectricParamsById(Long id) {
        return redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, id);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:查询采集参数列表
     * @Return:List<ElectricParams>
     */
    @Override
    public List<ElectricParams> selectElectricParamsList(ElectricParams electricParams) {
        return electricParamsMapper.selectElectricParamsList(electricParams);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:新增采集参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult insertElectricParams(ElectricParams electricParams) {
        if (electricParams.getName() == null || StringUtils.isEmpty(electricParams.getName())
                || electricParams.getOffsetAddress() == null || StringUtils.isEmpty(electricParams.getOffsetAddress())
                || electricParams.getUnit() == null || StringUtils.isEmpty(electricParams.getUnit())
                || electricParams.getEnergyCode() == null || electricParams.getDataType() == null
                || electricParams.getDataLength() == null || electricParams.getCodeSeq() == null
                || electricParams.getDataEncodeType() == null || electricParams.getParkCode() == null
                || electricParams.getPointLocation() == null || StringUtils.isEmpty(electricParams.getParkCode())
        ) {
            return AjaxResult.error("参数错误！");
        }
        //取所有参数定义缓存
        Map<String, ElectricParams> ElectricParamsCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams);
        Collection<ElectricParams> list = ElectricParamsCacheMap.values();
        List<ElectricParams> listNum = new ArrayList<>();
        for (ElectricParams e : list) {
            if (e.getName().equals(electricParams.getName())) {
                return AjaxResult.error("名称重复,请检查");
            }
            if(e.getEnergyCode() != null && e.getEnergyCode().equals(electricParams.getEnergyCode())){
                listNum.add(e);
            }
        }
        ElectricParams electricParamsQ = new ElectricParams();
        electricParams.setCreateTime(DateUtils.getNowDate());
        String fNybh = electricParams.getEnergyCode().toString();
        electricParamsQ.setEnergyCode(electricParams.getEnergyCode());

        String fDnbh = null;
        if (listNum.size() > 0) {
            //获取最大编号
            int maxBh = getMaxBh(listNum);
            //定义 编号长度
            int bhLength = 7;
            //生成新的编号
            fDnbh = String.format("%0" + bhLength + "d", maxBh + 1);
        } else {
            fDnbh = Integer.parseInt(fNybh) + "001";
        }
        electricParams.setCode(fDnbh);
        int isInsertElectricParams = electricParamsMapper.insertElectricParams(electricParams);
        electricParams = electricParamsMapper.selectElectricParamsByCode(electricParams.getCode());
        if (isInsertElectricParams == 0) {
            return AjaxResult.error("添加失败");
        }
        //添加到缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, electricParams.getId(), electricParams);
        return AjaxResult.success("添加成功");
    }

    // 获取最大编号
    private int getMaxBh(Collection<ElectricParams> list) {
        int maxnybh = 0;
        for (ElectricParams e : list) {
            String sBh = e.getCode();
            int iBh = Integer.parseInt(sBh);
            if (maxnybh < iBh) {
                maxnybh = iBh;
            }
        }
        return maxnybh;
    }


    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:修改采集参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult updateElectricParams(ElectricParams electricParams) {
        if (electricParams.getId() == null || electricParams.getCode() == null || StringUtils.isEmpty(electricParams.getCode())
                || electricParams.getName() == null || StringUtils.isEmpty(electricParams.getName())
                || electricParams.getOffsetAddress() == null || StringUtils.isEmpty(electricParams.getOffsetAddress())
                || electricParams.getUnit() == null || StringUtils.isEmpty(electricParams.getUnit())
                || electricParams.getEnergyCode() == null || electricParams.getDataType() == null
                || electricParams.getDataLength() == null || electricParams.getCodeSeq() == null
                || electricParams.getDataEncodeType() == null || electricParams.getParkCode() == null
                || electricParams.getPointLocation() == null || StringUtils.isEmpty(electricParams.getParkCode())
        ) {
            return AjaxResult.error("参数错误！");
        }
        //取所有参数定义缓存
        Map<String, ElectricParams> ElectricParamsCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams);
        Collection<ElectricParams> list = ElectricParamsCacheMap.values();
        for (ElectricParams e : list) {
            if (e.getName().equals(electricParams.getName()) && !e.getCode().equals(electricParams.getCode())) {
                return AjaxResult.error("名称重复,请检查");
            }
        }
        electricParams.setUpdateTime(DateUtils.getNowDate());
        int isUpdateElectricParams = electricParamsMapper.updateElectricParams(electricParams);
        if (isUpdateElectricParams == 0) {
            return AjaxResult.error("修改失败");
        }
        //添加到缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, electricParams.getId(), electricParams);
        return AjaxResult.success("修改成功");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:ids
     * @Description:删除采集参数
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult deleteElectricParamsByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误！");
        }
        //删除时看是否有关联
        for (Long str : ids) {
            //获取缓存采集参数定义数据
            ElectricParams electricParam = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, str);
            //获取采集方案采集参数关联缓存
            Map<String, ElectricCollRlgl> ElectricCollRlglCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
            Collection<ElectricCollRlgl> ElectricCollRlgls = ElectricCollRlglCacheMap.values();
            for (ElectricCollRlgl e : ElectricCollRlgls) {
                if (e.getElectricCode().equals(electricParam.getCode())) {
                    return error("采集参数定义已被采集方案关联，请先删除相关信息");
                }
            }
            //获取告警策略缓存
            Collection<Object> Alarms = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics).values();
            for (Object object : Alarms) {
                AlarmTactics ala = (AlarmTactics) object;
                if (ala.getElectricParamsId() != null) {
                    if (ala.getElectricParamsId().equals(str)) {
                        return error("采集参数定义已被告警策略，请先删除相关信息");
                    }
                }

            }
        }
        int isDeleteElectricParamsByIds = electricParamsMapper.deleteElectricParamsByIds(ids);
        if (isDeleteElectricParamsByIds > 0) {
            // 删除当前记录缓存
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, ids);
            return success("删除成功");
        }
        return error("删除失败");
    }


    /**
     * @description:获取采集参数信息
     * @author: sunshangeng
     * @date: 2022/9/22 14:40
     * @param: [电表id，是否统计]
     * @return: java.util.List<com.ruoyi.energyCollection.acquisitionParam.domain.ElectricParams>
     **/
    @Override
    public List<ElectricParams> selectElectricParamsListbymeterid(String meterid) {
        /**获取到电表信息*/
        AthenaElectricMeter athenaElectricMeter = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, meterid);
        List<ElectricParams> electricParams = new ArrayList<>();
        DeviceTree tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, meterid);

        /**AI能耗节点*/
        if (tree.getDeviceNodeId() == DeviceTreeConstants.BES_AI || tree.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT) {
            ElectricParams ElectricParam = new ElectricParams();
            ElectricParam.setEnergyCode(PointPowerParam.Point_Meter_Code);
            ElectricParam.setName(PointPowerParam.Point_Meter_Name);
            electricParams.add(ElectricParam);
            return electricParams;
//            return AjaxResult.success(electricParams);
        }

        if (athenaElectricMeter.getCollectionMethodCode() == null) {
            /**如果当前电表未绑定采集方案直接返回*/
            return electricParams;
        }


        /**获取到采集方案信息*/
        Map<String, CollMethod> collMethodMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod);
        CollMethod collMethod = new CollMethod();
        for (CollMethod value : collMethodMap.values()) {
            if (value.getId() == athenaElectricMeter.getCollectionMethodCode()) {
                collMethod = value;
                break;
            }
        }

        String methodCode = collMethod.getCode();
        /**获取电能采集*/
        List<ElectricCollRlgl> collrlglList = new ArrayList<>();
        Map<String, ElectricCollRlgl> collRlglMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
        collRlglMap.forEach((key, value) -> {
            if (value.getCollCode().equals(methodCode)) {
                collrlglList.add(value);
            }
        });
        /**获取采集参数*/
        collrlglList.forEach((item) -> {
            ElectricParams electricParam = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, (long) item.getElectricId());
            if (electricParam != null) {
                electricParams.add(electricParam);
            }
        });
//        if (cacheMap.size() == 0) {
//            addElectricParamsCache();
//        } else {
//            cacheMap.forEach((key, value) -> {
//                electricParams.add(value);
//            });
//        }
        return electricParams;
    }
}
