package com.zc.efounder.JEnterprise.service.energyCollection.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.mapper.energyCollection.CollMethodMapper;
import com.zc.efounder.JEnterprise.mapper.energyCollection.ElectricCollRlglMapper;
import com.zc.efounder.JEnterprise.service.energyCollection.CollMethodService;
import com.zc.common.constant.RedisKeyConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import static com.ruoyi.common.core.domain.AjaxResult.error;
import static com.ruoyi.common.core.domain.AjaxResult.success;


/**
 * 采集方案Service业务层处理
 *
 * @author gaojikun
 * @date 2022-09-08
 */
@Service
public class CollMethodServiceImpl implements CollMethodService {
    @Autowired
    private CollMethodMapper collMethodMapper;
    @Autowired
    private ElectricCollRlglMapper electricCollRlglMapper;

    @Resource
    private RedisCache redisCache;

    @PostConstruct
    public void init() {
        /*
          添加数据到 redis 缓存
         */
        addCollMethodCache();
    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addCollMethodCache() {
        // 获取全部采集参数定义列表数据
        List<CollMethod> collMethods = selectCollMethodList(null);

        // 清楚 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod);

        if (collMethods == null || collMethods.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        collMethods.forEach(val -> redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, (long)val.getId(), val));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:code
     * @Description:获取采集方案详细信息
     * @Return:CollMethod
     */
    @Override
    public CollMethod selectCollMethodByCode(Long id) {
        return redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, id);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:查询采集方案列表
     * @Return:List<CollMethod>
     */
    @Override
    public List<CollMethod> selectCollMethodList(CollMethod collMethod) {
        return collMethodMapper.selectCollMethodList(collMethod);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:新增采集方案
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult insertCollMethod(CollMethod collMethod) {
        if (/*collMethod.getCode() == null || StringUtils.isEmpty(collMethod.getCode()) ||*/
                collMethod.getName() == null || StringUtils.isEmpty(collMethod.getName()) ||
                collMethod.getParkCode() == null || StringUtils.isEmpty(collMethod.getParkCode()) ||
                collMethod.getEnergyCode() == null || StringUtils.isEmpty(collMethod.getEnergyCode())) {
            return AjaxResult.error("参数错误！");
        }
        //获取缓存数据
        Map<String, CollMethod> CollMethodCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod);
        Collection<CollMethod> list = CollMethodCacheMap.values();
        for (CollMethod c : list) {
            if (c.getName().equals(collMethod.getName())) {
                return AjaxResult.error("名称重复,请检查");
            }
        }
//        //名称查重
//        List<CollMethod> collMethods = collMethodMapper.selectCollMethodCheck(collMethod);
//        if (collMethods.size() > 0) {
//            return AjaxResult.error("名称重复");
//        }
        String fCjfabh = null;
        if (list.size() > 0) {
            //获取最大编号
            int maxBh = getMaxBh(list);
            //定义 编号长度
            int bhLength = 4;
            //生成新的编号
            fCjfabh = String.format("%0" + bhLength + "d", maxBh + 1);
        } else {
            fCjfabh = "0001";
        }
        collMethod.setCode(fCjfabh);
        collMethod.setCreateTime(DateUtils.getNowDate());
        int isInsertCollMethod = collMethodMapper.insertCollMethod(collMethod);
        if (isInsertCollMethod == 0) {
            return AjaxResult.error("添加失败");
        }
        //添加到缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, (long)collMethod.getId(), collMethod);
        return AjaxResult.success("添加成功");
    }

    // 获取最大编号
    private int getMaxBh(Collection<CollMethod> list) {
        int maxnybh = 0;
        for (CollMethod c : list) {
            String sBh = c.getCode();
            int iBh = Integer.parseInt(sBh);
            if (maxnybh < iBh) {
                maxnybh = iBh;
            }
        }
        return maxnybh;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:修改采集方案
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult updateCollMethod(CollMethod collMethod) {
        if (collMethod.getId() == 0 || collMethod.getCode() == null || StringUtils.isEmpty(collMethod.getCode()) ||
                collMethod.getName() == null || StringUtils.isEmpty(collMethod.getName()) ||
                collMethod.getParkCode() == null || StringUtils.isEmpty(collMethod.getParkCode()) ||
                collMethod.getEnergyCode() == null || StringUtils.isEmpty(collMethod.getEnergyCode())) {
            return AjaxResult.error("参数错误！");
        }
        //获取缓存数据
        Map<String, CollMethod> CollMethodCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod);
        Collection<CollMethod> list = CollMethodCacheMap.values();
        for (CollMethod c : list) {
            if (c.getName().equals(collMethod.getName()) && !c.getCode().equals(collMethod.getCode())) {
                return AjaxResult.error("名称重复,请检查");
            }
        }
//        //名称查重
//        List<CollMethod> collMethods = collMethodMapper.selectCollMethodCheck(collMethod);
//        if (collMethods.size() > 0) {
//            return AjaxResult.error("名称重复");
//        }
        collMethod.setUpdateTime(DateUtils.getNowDate());
        int isUpdateCollMethod = collMethodMapper.updateCollMethod(collMethod);
        if (isUpdateCollMethod == 0) {
            return AjaxResult.error("修改失败");
        }
        // 添加到缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, (long)collMethod.getId(), collMethod);
        return AjaxResult.success("修改成功");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:codes
     * @Description:删除采集方案
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult deleteCollMethodByCodes(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误！");
        }
        for (Long id : ids) {
            //获取缓存采集参数定义数据
            CollMethod collMethod = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, id);
            //获取采集方案采集参数关联缓存
            Map<String, ElectricCollRlgl> ElectricCollRlglCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl);
            Collection<ElectricCollRlgl> ElectricCollRlgls = ElectricCollRlglCacheMap.values();
            for (ElectricCollRlgl e : ElectricCollRlgls) {
                if (e.getCollCode().equals(collMethod.getCode())) {
                    return error("采集方案定义已被电表关联，请先删除相关信息");
                }
            }
        }
        boolean isDeleteCollMethodByCodes = collMethodMapper.deleteCollMethodByCodes(ids);
        if (isDeleteCollMethodByCodes) {
            // 删除当前记录缓存
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, ids);
            return success("删除成功");
        }

        return error("删除失败");

    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:code
     * @Description:删除采集方案
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult deleteCollMethodByCode(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误！");
        }
        int i = collMethodMapper.deleteCollMethodByCode(id);
        if (i > 0) {
            // 删除当前记录缓存
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod, id);
            return AjaxResult.success("删除成功！");
        }
        return AjaxResult.error("删除失败！");
    }
}
