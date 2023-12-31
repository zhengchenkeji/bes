package com.zc.efounder.JEnterprise.service.deviceTree;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTree.Module;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;

/**
 * 模块Service接口
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
public interface ModuleService {
    /**
     * 查询模块
     *
     * @param id 模块主键
     * @return 模块
     */
    public Module selectModuleById(Long id);

    /**
     * 查询模块列表
     *
     * @param module 模块
     * @return 模块集合
     */
    List<Module> selectModuleList(Module module);

    /**
     * 新增模块
     *
     * @param module 模块
     * @return 结果
     */
    AjaxResult insertModule(Module module);

    /**
     * 修改模块
     *
     * @param module 模块
     * @return 结果
     */
    AjaxResult updateModule(Module module);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块主键集合
     * @return 结果
     */
    AjaxResult deleteModuleByIds(Long[] ids);

    /**
     * 删除模块信息
     *
     * @param id 模块主键
     * @return 结果
     */
    AjaxResult deleteModuleById(Long id);

    /**
     * 同步模块
     *
     * @param module 模块
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/24
     **/
    AjaxResult synchronizeModule(Module module);

    /**
     * 同步模块点
     *
     * @param point 模块点
     * @return {@code AjaxResult }
     * @Author gaojikun
     * @Date 2022/09/29
     **/
    AjaxResult synchronizePoint(Point point);

    /**
     * 同步虚点
     *
     * @param point 虚点
     * @return {@code AjaxResult }
     * @Author gaojikun
     * @Date 2022/09/29
     **/
    AjaxResult synVirtualPoint(Point point);

    /**
     * 数据对比
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author gaojikun
     * @Date 2022/09/29
     **/
    AjaxResult getDataInfoParam(Long deviceTreeId, Integer type);

    /**
     * @Description: 刷新缓存
     * @auther: wanghongjie
     * @date: 16:34 2022/11/18
     * @param: []
     * @return: void
     */
    void addModuleCache();

}
