package com.zc.efounder.JEnterprise.service.energyInfo;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SubitemConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.vo.SubitemBranchVo;

import java.util.List;

/**
 * 分项拓扑配置Service接口
 *
 * @author qindehua
 * @date 2022-09-20
 */
public interface ISubitemConfigService
{
    /**
     * 查询分项拓扑配置
     *
     * @param subitemId 分项拓扑配置主键
     * @return 分项拓扑配置
     */
    public SubitemConfig selectSubitemConfigBySubitemId(String subitemId);

    /**
     * 查询分项拓扑配置列表
     *
     * @param subitemConfig 分项拓扑配置
     * @return 分项拓扑配置集合
     */
    List<SubitemConfig> selectSubitemConfigList(SubitemConfig subitemConfig);

    /**
     * 新增分项拓扑配置
     *
     * @param subitemConfig 分项拓扑配置
     * @return 结果
     */
    AjaxResult insertSubitemConfig(SubitemConfig subitemConfig);

    /**
     * 批量新增分项拓扑配置
     *
     * @param subitemConfig 分项配置
     * @return {@code Boolean }
     * @Author qindehua
     * @Date 2022/11/10
     **/
    AjaxResult insertSubitemBatch(SubitemConfig subitemConfig);

    /**
     * 修改分项拓扑配置
     *
     * @param subitemConfig 分项拓扑配置
     * @return 结果
     */
    AjaxResult updateSubitemConfig(SubitemConfig subitemConfig);

    /**
     * 批量删除分项拓扑配置
     *
     * @param subitemIds 需要删除的分项拓扑配置主键集合
     * @return 结果
     */
    AjaxResult deleteSubitemConfigBySubitemIds(String[] subitemIds);

    /**
     * 删除分项拓扑配置信息
     *
     * @param subitemId 分项拓扑配置主键
     * @return 结果
     */
    int deleteSubitemConfigBySubitemId(String subitemId);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param list 分项拓扑配置列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildTreeSelect(List<SubitemConfig> list);


    /**
     * 构建前端所需要树结构
     *
     * @param list 分项拓扑配置列表
     * @return 树结构列表
     */
    public List<SubitemConfig> buildTree(List<SubitemConfig> list);


    /**
     * 删除时查看 当前节点下是否有子节点
     *
     * @param subitemIds 分项拓扑配置主键集合
     * @return 分项拓扑配置
     */
    public SubitemConfig selectSubitemConfigSun(String[] subitemIds);


    /**
     * 查询分项计量拓扑配置列表及下面子节点
     *
     * @param subitemConfig 分项计量拓扑配置
     * @param code               查询标识
     * @return 分项计量拓扑配置集合
     */
    List<SubitemConfig> selectSubitemConfigListSun(SubitemConfig subitemConfig,String code);

    /**
     * 包含支路
     *
     * @param subitemBranchVo 支路数据
     * @return 是否成功
     */
     boolean saveAthenaBranchConfig(SubitemBranchVo subitemBranchVo);
}
