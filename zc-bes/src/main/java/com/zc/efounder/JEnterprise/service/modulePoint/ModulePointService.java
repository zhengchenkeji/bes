package com.zc.efounder.JEnterprise.service.modulePoint;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.modulePoint.ModulePoint;

import java.util.List;

/**
 * 模块点类型定义Service接口
 *
 * @author gaojikun
 * @date 2022-09-06
 */
public interface ModulePointService
{
    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:id
     * @Description:获取模块点类型定义详细信息
     * @Return:ModulePoint
     */
    public ModulePoint selectModulePointById(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:查询模块点类型定义列表
     * @Return:List<ModulePoint>
     */
    List<ModulePoint> selectModulePointList(ModulePoint modulePoint);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:新增模块点类型定义
     * @Return:AjaxResult
     */
    AjaxResult insertModulePoint(ModulePoint modulePoint);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:修改模块点类型定义
     * @Return:AjaxResult
     */
    AjaxResult updateModulePoint(ModulePoint modulePoint);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:ids
     * @Description:删除模块点类型定义
     * @Return:AjaxResult
     */
    AjaxResult deleteModulePointByIds(Long[] ids);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:id
     * @Description:删除模块点类型定义
     * @Return:int
     */
    int deleteModulePointById(Long id);
}
