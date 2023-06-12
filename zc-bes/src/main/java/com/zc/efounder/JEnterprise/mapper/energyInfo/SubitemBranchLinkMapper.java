package com.zc.efounder.JEnterprise.mapper.energyInfo;

import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分项支路关联Mapper接口
 *
 * @author qindehua
 * @date 2022-09-20
 */
public interface SubitemBranchLinkMapper
{
    /**
     * 查询分项支路关联
     *
     * @param id 分项支路关联主键
     * @return 分项支路关联
     */
    public SubitemBranchLink selectSubitemBranchLinkById(Long id);

    /**
     * 查询分项支路关联列表
     *
     * @param subitemBranchLink 分项支路关联
     * @return 分项支路关联集合
     */
    List<SubitemBranchLink> selectSubitemBranchLinkList(SubitemBranchLink subitemBranchLink);

    /**
     * 新增分项支路关联
     *
     * @param subitemBranchLink 分项支路关联
     * @return 结果
     */
    int insertSubitemBranchLink(SubitemBranchLink subitemBranchLink);

    /**
     * 修改分项支路关联
     *
     * @param subitemBranchLink 分项支路关联
     * @return 结果
     */
    int updateSubitemBranchLink(SubitemBranchLink subitemBranchLink);

    /**
     * 删除分项支路关联
     *
     * @param id 分项支路关联主键
     * @return 结果
     */
    int deleteSubitemBranchLinkById(Long id);

    /**
     * 批量删除分项支路关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSubitemBranchLinkByIds(Long[] ids);


    /**
     * 批量删除分项支路关联
     *
     * @param subitemIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSubitemBranchLinkBySubitemIds(String[] subitemIds);

    /**
     * 批量删除分项下支路
     *
     * @param cascadedIds  级联id
     * @param beginBranchIds 分项id
     * @return 结果
     */
    boolean deleteSubitemBranchLinkByCascadedIds(
            @Param("cascadedIds") List<String> cascadedIds,
            @Param("beginBranchIds")List<Long> beginBranchIds);


    /**
     * 删除分项下支路
     *
     * @param subitemId 分项主键
     * @return 结果
     */
    boolean deleteSubitemBranchLinkBySubitemId(@Param("subitemId") String subitemId);

    /**
     * 删除分项支路关联表
     *
     * @param ids 支路ids
     * @return 结果
     */
    boolean deleteSubitemBranchLinkByBranchIds(@Param("ids") Long[] ids);

    /**
     * 查询分项下支路列表
     *
     * @param subitemId 分项id
     * @return 支路列表
     */
     List<SubitemBranchLink> selectSubitemBranchLinkBySubitemId(@Param("subitemId") String subitemId);


    /**
     * 批量新增分项支路关联
     *
     * @param subitemBranchLinks 分项支路关联集合
     * @return 结果
     */
    Boolean batchInsertSubitemBranchLink(@Param("subitemBranchLinks") List<SubitemBranchLink> subitemBranchLinks);

    /**
     * 查询分项下支路列表
     *
     * @param subitemId 分项ID
     * @return 支路列表
     */
    List<AthenaBranchConfig> selectAthenaBesBranchListById(@Param("subitemId") String subitemId);


}
