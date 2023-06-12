package com.zc.efounder.JEnterprise.service.energyInfo;

import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;

import java.util.List;

/**
 * 分项支路关联Service接口
 *
 * @author qindehua
 * @date 2022-09-20
 */
public interface ISubitemBranchLinkService
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
     * 批量删除分项支路关联
     *
     * @param ids 需要删除的分项支路关联主键集合
     * @return 结果
     */
    int deleteSubitemBranchLinkByIds(Long[] ids);

    /**
     * 删除分项支路关联信息
     *
     * @param id 分项支路关联主键
     * @return 结果
     */
    int deleteSubitemBranchLinkById(Long id);


    /**
     * 查询分项下支路列表
     *
     * @param subitemId 分项ID
     * @return 支路列表
     */
    List<AthenaBranchConfig> selectAthenaBesBranchListById(String subitemId);

}
