package com.zc.efounder.JEnterprise.service.moduleType.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.deviceTree.Module;
import com.zc.efounder.JEnterprise.mapper.deviceTree.ModuleMapper;
import com.zc.efounder.JEnterprise.domain.moduleType.ModuleType;
import com.zc.efounder.JEnterprise.mapper.moduleType.ModuleTypeMapper;
import com.zc.efounder.JEnterprise.service.moduleType.ModuleTypeService;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 模块类型定义Service业务层处理
 *
 * @author gaojikun
 * @date 2022-09-06
 */
@Service
public class ModuleTypeServiceImpl implements ModuleTypeService {
    @Resource
    private ModuleTypeMapper moduleTypeMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ModuleMapper moduleMapper;

    @PostConstruct
    public void init() {
        /**
         * 添加数据到 redis 缓存
         */
        addModuleTypeCache();
    }

    private void addModuleTypeCache() {
        // 获取全部模块类型列表数据
        List<ModuleType> modules = moduleTypeMapper.selectModuleTypeList(null);

        // 清楚 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType);

        if (modules == null || modules.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        modules.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType, val.getId(), val);
        });
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:id
     * @Description:获取模块类型定义详细信息
     * @Return:ModuleType
     */
    @Override
    public ModuleType selectModuleTypeById(Long id) {
        return moduleTypeMapper.selectModuleTypeById(id);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:查询模块类型定义列表
     * @Return:List<ModuleType>
     */
    @Override
    public List<ModuleType> selectModuleTypeList(ModuleType moduleType) {
        return moduleTypeMapper.selectModuleTypeList(moduleType);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:新增模块类型定义
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult insertModuleType(ModuleType moduleType) {
        if (moduleType.getModuleCode() == null || StringUtils.isEmpty(moduleType.getModuleCode())
                || moduleType.getPointSet() == null || StringUtils.isEmpty(moduleType.getPointSet())
                || moduleType.getTypeCode() == null) {
            return AjaxResult.error("参数错误");
        }
        moduleType.setCreateTime(DateUtils.getNowDate());
        ModuleType checkModuleType = moduleTypeMapper.selectModuleTypeByCode(moduleType);
        if (checkModuleType != null) {
            return AjaxResult.error("模块型号/类型代码重复");
        }
        int insertModuleTypeNum = moduleTypeMapper.insertModuleType(moduleType);
        if (insertModuleTypeNum > 0) {

            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType, moduleType.getId(), moduleType);
            return AjaxResult.success("添加成功");
        } else {
            return AjaxResult.error("添加失败");
        }
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:修改模块类型定义
     * @Return:AjaxResult
     */
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public AjaxResult updateModuleType(ModuleType moduleType) {
        if (moduleType.getModuleCode() == null || StringUtils.isEmpty(moduleType.getModuleCode())
                || moduleType.getPointSet() == null || StringUtils.isEmpty(moduleType.getPointSet())
                || moduleType.getTypeCode() == null || moduleType.getId() == null) {
            return AjaxResult.error("参数错误");
        }
        moduleType.setUpdateTime(DateUtils.getNowDate());
        ModuleType checkModuleType = moduleTypeMapper.selectModuleTypeByCode(moduleType);
        if (checkModuleType != null) {
            return AjaxResult.error("模块型号/类型代码重复");
        }
        int updateModuleTypeNum = moduleTypeMapper.updateModuleType(moduleType);
        //模块之前的类型代码
        Map<String, ModuleType> moduleTypeCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType);
        Collection<ModuleType> moduleTypeValues = moduleTypeCacheMap.values();
        Long oldCode = null;
        for (ModuleType m : moduleTypeValues) {
            if (m.getId().equals(moduleType.getId())) {
                oldCode = m.getTypeCode();
                break;
            }
        }

        if (updateModuleTypeNum > 0) {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType, moduleType.getId(), moduleType);

            //修改模块数据库
            boolean isUpdate = moduleMapper.updateModuleByTypeCode(moduleType.getTypeCode(), oldCode);
            if (!isUpdate) {
                try{
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("修改失败");
                }
            }

            //修改模块缓存的类型代码
            Map<String, Module> moduleCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree_Module);
            Collection<Module> moduleValues = moduleCacheMap.values();
            for (Module m : moduleValues) {
                if (m.getModuleTypeId().equals(oldCode)) {
                    m.setModuleTypeId(moduleType.getTypeCode());
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module, m.getDeviceTreeId(), m);
                }
            }

            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.error("修改失败");
        }
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:ids
     * @Description:删除模块类型定义
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult deleteModuleTypeByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        //查询模块类型是否关联设备树
        boolean isTreeInfo = false;
        a:
        for (Long id : ids) {
            ModuleType moduleType = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType, id);
            Collection moduleValues = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module).values();
            b:
            for (Object moduleObj : moduleValues) {
                Module module = (Module) moduleObj;
                if (module.getModuleTypeId().equals(moduleType.getTypeCode())) {
                    isTreeInfo = true;
                    break a;
                }
            }
        }
        if (isTreeInfo) {
            return AjaxResult.error("模块已被设备树关联，请先删除相关设备树信息");
        }
        int delete = moduleTypeMapper.deleteModuleTypeByIds(ids);
        if (delete > 0) {
            for (int i = 0; i < ids.length; i++) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType, ids[i]);
            }
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error("删除失败");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:id
     * @Description:删除模块类型定义
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult deleteModuleTypeById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误");
        }
        //查询模块类型是否关联设备树
        boolean isTreeInfo = false;
        ModuleType moduleType = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType, id);
        Collection moduleValues = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Module).values();
        for (Object moduleObj : moduleValues) {
            Module module = (Module) moduleObj;
            if (module.getModuleTypeId().equals(moduleType.getTypeCode())) {
                isTreeInfo = true;
                break;
            }
        }

        if (isTreeInfo) {
            return AjaxResult.error("模块已被设备树关联，请先删除相关设备树信息");
        }
        int delete = moduleTypeMapper.deleteModuleTypeById(id);
        if (delete > 0) {
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType, id);
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error("删除失败");
    }
}
