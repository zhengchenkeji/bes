package com.zc.efounder.JEnterprise.service.energyInfo;

import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;

import java.util.List;

/**
 * 分户支路关联Service接口
 *
 * @author qindehua
 * @date 2022-09-19
 */
public interface AthenaBesHouseholdBranchLinkService
{
    /**
     * 查询分户支路关联
     *
     * @param id 分户支路关联主键
     * @return 分户支路关联
     */
    public AthenaBesHouseholdBranchLink selectAthenaBesHouseholdBranchLinkById(Long id);


    /**
     * 查询分户下支路列表
     *
     * @param householdId 分户ID
     * @return 支路列表
     */
    List<AthenaBranchConfig> selectAthenaBesBranchListById(Long householdId);

    /**
     * 查询分户支路关联列表
     *
     * @param athenaBesHouseholdBranchLink 分户支路关联
     * @return 分户支路关联集合
     */
    List<AthenaBesHouseholdBranchLink> selectAthenaBesHouseholdBranchLinkList(AthenaBesHouseholdBranchLink athenaBesHouseholdBranchLink);

    /**
     * 新增分户支路关联
     *
     * @param athenaBesHouseholdBranchLink 分户支路关联
     * @return 结果
     */
    int insertAthenaBesHouseholdBranchLink(AthenaBesHouseholdBranchLink athenaBesHouseholdBranchLink);

    /**
     * 修改分户支路关联
     *
     * @param athenaBesHouseholdBranchLink 分户支路关联
     * @return 结果
     */
    int updateAthenaBesHouseholdBranchLink(AthenaBesHouseholdBranchLink athenaBesHouseholdBranchLink);

    /**
     * 批量删除分户支路关联
     *
     * @param ids 需要删除的分户支路关联主键集合
     * @return 结果
     */
    int deleteAthenaBesHouseholdBranchLinkByIds(Long[] ids);

    /**
     * 删除分户支路关联信息
     *
     * @param id 分户支路关联主键
     * @return 结果
     */
    int deleteAthenaBesHouseholdBranchLinkById(Long id);
}
