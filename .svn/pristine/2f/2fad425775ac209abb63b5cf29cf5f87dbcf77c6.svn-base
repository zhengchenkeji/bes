package com.zc.efounder.JEnterprise.mapper.modulePoint;

import com.zc.efounder.JEnterprise.domain.modulePoint.ModulePoint;

import java.util.List;
import java.util.Map;

/**
 * 模块点类型定义Mapper接口
 * 
 * @author gaojikun
 * @date 2022-09-06
 */
public interface ModulePointMapper
{
    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:id
     * @Description:获取模块点类型定义详细信息
     * @Return:ModulePoint
     */
    public ModulePoint selectModulePointById(Long id);

    public ModulePoint selectModulePointCheck(ModulePoint modulePoint);

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
     * @Date:2023-01-13 15:23
     * @Param:
     * @Description:查询所有点类型
     * @Return:List<Map<String,Object>>
     */
    List<Map<String,Object>> selectAllModulePointList();

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:新增模块点类型定义
     * @Return:boolean
     */
    boolean insertModulePoint(ModulePoint modulePoint);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:修改模块点类型定义
     * @Return:boolean
     */
    boolean updateModulePoint(ModulePoint modulePoint);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:id
     * @Description:删除模块点类型定义
     * @Return:int
     */
    int deleteModulePointById(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:ids
     * @Description:删除模块点类型定义
     * @Return:boolean
     */
    boolean deleteModulePointByIds(Long[] ids);
}
