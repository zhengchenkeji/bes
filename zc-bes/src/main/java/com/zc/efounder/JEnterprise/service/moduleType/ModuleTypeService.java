package com.zc.efounder.JEnterprise.service.moduleType;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.moduleType.ModuleType;

import java.util.List;

/**
 * 模块类型定义Service接口
 *
 * @author gaojikun
 * @date 2022-09-06
 */
public interface ModuleTypeService
{
    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:id
     * @Description:获取模块类型定义详细信息
     * @Return:ModuleType
     */
    public ModuleType selectModuleTypeById(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:查询模块类型定义列表
     * @Return:List<ModuleType>
     */
    List<ModuleType> selectModuleTypeList(ModuleType moduleType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:新增模块类型定义
     * @Return:AjaxResult
     */
    AjaxResult insertModuleType(ModuleType moduleType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:修改模块类型定义
     * @Return:AjaxResult
     */
    AjaxResult updateModuleType(ModuleType moduleType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:ids
     * @Description:删除模块类型定义
     * @Return:AjaxResult
     */
    AjaxResult deleteModuleTypeByIds(Long[] ids);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:id
     * @Description:删除模块类型定义
     * @Return:AjaxResult
     */
    AjaxResult deleteModuleTypeById(Long id);
}
