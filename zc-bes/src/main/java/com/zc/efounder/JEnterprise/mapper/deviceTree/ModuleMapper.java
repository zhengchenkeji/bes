package com.zc.efounder.JEnterprise.mapper.deviceTree;

import java.util.List;
import java.util.Map;

import com.zc.efounder.JEnterprise.domain.deviceTree.Module;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import org.apache.ibatis.annotations.Param;

/**
 * 模块Mapper接口
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
public interface ModuleMapper {
    /**
     * 查询模块
     *
     * @param id 模块主键
     * @return 模块
     */
    public Module selectModuleById(Long id);

    /**
     * 根据树ID 查询模块信息
     *
     * @param deviceTreeId 树ID
     * @return {@code Module }
     * @Author qindehua
     * @Date 2022/12/19
     **/
    public Module selectModuleByDeviceTreeId(@Param("deviceTreeId") Integer deviceTreeId);

    /**
     * 查询模块列表
     *
     * @param module 模块
     * @return 模块集合
     */
    List<Module> selectModuleList(Module module);

    /**
     * 根据系统名称查询模块
     */
    Module selectModuleListByName(Module module);

    /**
     * 根据DDCID查询模块列表
     */
    List<Module> selectModuleListByControllerId(Module module);

    /**
     * 根据ID查询模块点
     */
    Map<String, String> selectModulePoint(Module module);

    /**
     * 新增模块
     *
     * @param module 模块
     * @return 结果
     */
    boolean insertModule(Module module);

    /**
     * 修改模块
     *
     * @param module 模块
     * @return 结果
     */
    boolean updateModule(Module module);

    /**
     * 删除模块
     *
     * @param id 模块主键
     * @return 结果
     */
    int deleteModuleById(Long id);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteModuleByIds(Long[] ids);

    /*根据模块树Id查询所有模块点树Id*/
    List<DeviceTree> getModulePoint(int treeId);

    /*根据模块点树Id删除模块点*/
    boolean deleteModulePoint(Integer[] pointTreeIds);

    /*根据模块树Id删除模块*/
    boolean deleteModule(int treeId);

    /*删除树节点*/
    boolean deleteModuleTree(int treeId);

    Module selectModuleByPointTreeId(Integer treeId);

    /**
     * @Author:gaojikun
     * @Date:2023-01-17 10:30
     * @Param:
     * @Description:根据模块型号修改模块型号
     * @Return:
     */
    boolean updateModuleByTypeCode(@Param("newCode") Long newCode, @Param("oldCode") Long oldCode);


}
