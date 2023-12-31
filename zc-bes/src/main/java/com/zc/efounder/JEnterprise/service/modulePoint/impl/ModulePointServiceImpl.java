package com.zc.efounder.JEnterprise.service.modulePoint.impl;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.modulePoint.ModulePoint;
import com.zc.efounder.JEnterprise.mapper.modulePoint.ModulePointMapper;
import com.zc.efounder.JEnterprise.service.modulePoint.ModulePointService;
import com.zc.efounder.JEnterprise.domain.moduleType.ModuleType;
import com.zc.efounder.JEnterprise.mapper.moduleType.ModuleTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ruoyi.common.core.domain.AjaxResult.error;
import static com.ruoyi.common.core.domain.AjaxResult.success;

/**
 * 模块点类型定义Service业务层处理
 *
 * @author gaojikun
 * @date 2022-09-06
 */
@Service
public class ModulePointServiceImpl implements ModulePointService {
    @Autowired
    private ModulePointMapper moduleTypeMapper;

    @Autowired
    private ModuleTypeMapper moduleTypeMapperO;


    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:id
     * @Description:获取模块点类型定义详细信息
     * @Return:ModulePoint
     */
    @Override
    public ModulePoint selectModulePointById(Long id) {
        return moduleTypeMapper.selectModulePointById(id);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:查询模块点类型定义列表
     * @Return:List<ModulePoint>
     */
    @Override
    public List<ModulePoint> selectModulePointList(ModulePoint modulePoint) {
        return moduleTypeMapper.selectModulePointList(modulePoint);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:新增模块点类型定义
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult insertModulePoint(ModulePoint modulePoint) {
        if (modulePoint.getModulePoint() == null || StringUtils.isEmpty(modulePoint.getModulePoint())
                || modulePoint.getDescription() == null || StringUtils.isEmpty(modulePoint.getDescription())) {
            return AjaxResult.error("参数错误");
        }
        ModulePoint modulePointCheck = moduleTypeMapper.selectModulePointCheck(modulePoint);
        if (modulePointCheck != null) {
            return AjaxResult.error("模块点类型重复");
        }
        modulePoint.setCreateTime(DateUtils.getNowDate());
        boolean isAdd = moduleTypeMapper.insertModulePoint(modulePoint);
        if (!isAdd) {
            return AjaxResult.error("添加失败");
        }
        return AjaxResult.success("添加成功");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:修改模块点类型定义
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult updateModulePoint(ModulePoint modulePoint) {
        if (modulePoint.getModulePoint() == null || StringUtils.isEmpty(modulePoint.getModulePoint()) || modulePoint.getId() == null
                || modulePoint.getDescription() == null || StringUtils.isEmpty(modulePoint.getDescription()) ||
        modulePoint.getId()==null) {
            return AjaxResult.error("参数错误");
        }
        ModulePoint modulePointCheck = moduleTypeMapper.selectModulePointCheck(modulePoint);
        if (modulePointCheck != null) {
            return AjaxResult.error("模块点类型重复");
        }
        modulePoint.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdate = moduleTypeMapper.updateModulePoint(modulePoint);
        if (!isUpdate) {
            return AjaxResult.error("修改失败");
        }
        return AjaxResult.success("修改成功");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:ids
     * @Description:删除模块点类型定义
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult deleteModulePointByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        for (Long id : ids) {
            //具有外键所关联的信息，无法删除息
            ModuleType moduleType = new ModuleType();
            moduleType.setPointSet(id + "");
            List<ModuleType> moduleTypes = moduleTypeMapperO.selectModuleTypeListByPoint(moduleType);
            if (moduleTypes.size() > 0) {
                return error("模块点类型已被模块类型关联，请先删除相关信息");
            }
        }
        boolean isDeleteModulePointByIds = moduleTypeMapper.deleteModulePointByIds(ids);
        if (isDeleteModulePointByIds) {
            return success("删除成功");
        }
        return error("删除失败");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:id
     * @Description:删除模块点类型定义
     * @Return:int
     */
    @Override
    public int deleteModulePointById(Long id) {
        if (id == null) {
            return 0;
        }
        return moduleTypeMapper.deleteModulePointById(id);
    }
}
