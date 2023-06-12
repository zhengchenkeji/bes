package com.zc.efounder.JEnterprise.mapper.moduleType;

import com.zc.efounder.JEnterprise.domain.moduleType.ModuleType;

import java.util.List;
import java.util.Map;

/**
 * 模块类型定义Mapper接口
 *
 * @author gaojikun
 * @date 2022-09-06
 */
public interface ModuleTypeMapper {
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
     * @Description:查询模块类型定义
     * @Return:ModuleType
     */
    ModuleType selectModuleTypeByCode(ModuleType moduleType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:查询所有类型定义
     * @Return:List<ModuleType>
     */
    List<ModuleType> selectModuleTypeListByPoint(ModuleType moduleType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:新增模块类型定义
     * @Return:int
     */
    int insertModuleType(ModuleType moduleType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:修改模块类型定义
     * @Return:int
     */
    int updateModuleType(ModuleType moduleType);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:id
     * @Description:删除模块类型定义
     * @Return:int
     */
    int deleteModuleTypeById(Long id);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:ids
     * @Description:删除模块类型定义
     * @Return:int
     */
    int deleteModuleTypeByIds(Long[] ids);

    Map<String, Object> queryModuleTreeInfo(Long[] ids);

    Map<String, Object> queryModuleTreeInfoById(Long[] ids);
}
