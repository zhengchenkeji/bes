package com.zc.efounder.JEnterprise.service.energyInfo;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.AthenaBesHouseholdConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.vo.BranchVo;

import java.util.List;

/**
 * 分户计量拓扑配置Service接口
 *
 * @author qindehua
 * @date 2022-09-19
 */
public interface AthenaBesHouseholdConfigService
{
    /**
     * 查询分户计量拓扑配置
     *
     * @param id 分户计量拓扑配置主键
     * @return 分户计量拓扑配置
     */
    public AthenaBesHouseholdConfig selectAthenaBesHouseholdConfigById(Long id);

    /**
     * 查询分户计量拓扑配置列表
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @return 分户计量拓扑配置集合
     */
    List<AthenaBesHouseholdConfig> selectAthenaBesHouseholdConfigList(AthenaBesHouseholdConfig athenaBesHouseholdConfig);

    /**
     * 查询分户计量拓扑配置列表及下面子节点
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @param code               查询标识
     * @return 分户计量拓扑配置集合
     */
    List<AthenaBesHouseholdConfig> selectAthenaBesHouseholdConfigListSun(AthenaBesHouseholdConfig athenaBesHouseholdConfig,String code);

    /**
     * 新增时查看 是否有重复分户名称
     *
     * @param athenaBesHouseholdConfig 分户拓扑配置
     * @return 分户拓扑配置
     */
    public AthenaBesHouseholdConfig selectAthenaBesHouseholdConfigRepeat(AthenaBesHouseholdConfig athenaBesHouseholdConfig);


    /**
     * 新增分户计量拓扑配置
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @return 结果
     */
    AjaxResult insertAthenaBesHouseholdConfig(AthenaBesHouseholdConfig athenaBesHouseholdConfig);

    /**
     * 修改分户计量拓扑配置
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @return 结果
     */
    AjaxResult updateAthenaBesHouseholdConfig(AthenaBesHouseholdConfig athenaBesHouseholdConfig);



    /**
     * 批量删除分户计量拓扑配置
     *
     * @param ids 需要删除的分户计量拓扑配置主键集合
     * @return 结果
     */
    AjaxResult deleteAthenaBesHouseholdConfigByIds(Long[] ids);

    /**
     * 删除分户计量拓扑配置信息
     *
     * @param id 分户计量拓扑配置主键
     * @return 结果
     */
    int deleteAthenaBesHouseholdConfigById(Long id);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param list 分户拓扑配置列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildTreeSelect(List<AthenaBesHouseholdConfig> list);


    /**
     * 构建前端所需要树结构
     *
     * @param list 分户拓扑配置列表
     * @return 树结构列表
     */
    public List<AthenaBesHouseholdConfig> buildTree(List<AthenaBesHouseholdConfig> list);

    /**
     * 包含支路
     *
     * @param branchVo 支路数据
     * @return 是否成功
     */
    public boolean saveAthenaBranchConfig(BranchVo branchVo);
}
