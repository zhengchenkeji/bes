package com.zc.efounder.JEnterprise.mapper.energyInfo;

import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分户支路关联Mapper接口
 *
 * @author qindehua
 * @date 2022-09-19
 */
public interface AthenaBesHouseholdBranchLinkMapper
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
     * @param householdId 分户id
     * @return 支路列表
     */
    public List<AthenaBesHouseholdBranchLink> selectAthenaBesBranchLinkByHouseholdId(@Param("householdId") Long householdId);

    /**
     * 查询分户下支路列表
     *
     * @param householdId 分户ID
     * @return 支路列表
     */
    List<AthenaBranchConfig> selectAthenaBesBranchListById(@Param("householdId") Long householdId);


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
     * 删除分户支路关联
     *
     * @param id 分户支路关联主键
     * @return 结果
     */
    int deleteAthenaBesHouseholdBranchLinkById(Long id);

    /**
     * 删除分户下支路
     *
     * @param householdId 分户主键
     * @return 结果
     */
    boolean deleteAthenaBesHouseholdBranchLinkByHouseholdId(@Param("householdId") Long householdId);

    /**
     * 批量删除分户下支路
     *
     * @param cascadedIds  级联id
     * @param beginBranchIds 分户id
     * @return 结果
     */
    boolean deleteAthenaBesHouseholdBranchLinkBranchByHouseholdId(
            @Param("cascadedIds") List<Long> cascadedIds,
            @Param("beginBranchIds")List<Long> beginBranchIds);

    /**
     * 批量删除分户支路关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAthenaBesHouseholdBranchLinkByIds(Long[] ids);

    /**
     * 批量删除分户支路关联
     *
     * @param ids 需要删除的支路ids
     * @return 结果
     */
    Boolean deleteHouseholdBranchLinkByBranchIds(@Param("ids") Long[] ids);

    /**
     * 批量新增分户支路关联
     *
     * @param athenaBesHouseholdBranchLinks 分户支路关联集合
     * @return 结果
     */
    Boolean batchInsertAthenaHouseholdBranchLink(@Param("athenaBesHouseholdBranchLinks") List<AthenaBesHouseholdBranchLink> athenaBesHouseholdBranchLinks);

}
