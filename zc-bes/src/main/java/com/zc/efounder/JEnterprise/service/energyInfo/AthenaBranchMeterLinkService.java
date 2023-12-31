package com.zc.efounder.JEnterprise.service.energyInfo;

import java.util.List;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;

/**
 * 支路电表关联Service接口
 * @author qindehua
 */
public interface AthenaBranchMeterLinkService
{
    /**
     * 查询支路电表关联
     *
     * @param id 支路电表关联主键
     * @return 支路电表关联
     */
    public AthenaBranchMeterLink selectAthenaBranchMeterLinkById(Long id);

    /**
     * 查询当前支路下电表列表
     *
     * @param branchId 支路ID
     * @param energyCode 能源编号
     * @return 支路电表关联集合
     */
    List<AthenaBranchMeterLink> selectAthenaBranchMeterLinkList(Long branchId,String energyCode);

    /**
     * 新增支路电表关联
     *
     * @param athenaBranchMeterLink 支路电表关联
     * @return 结果
     */
    int insertAthenaBranchMeterLink(AthenaBranchMeterLink athenaBranchMeterLink);

    /**
     * 修改支路电表关联
     *
     * @param athenaBranchMeterLink 支路电表关联
     * @return 结果
     */
    int updateAthenaBranchMeterLink(AthenaBranchMeterLink athenaBranchMeterLink);

    /**
     * 批量删除支路电表关联
     *
     * @param ids 需要删除的支路电表关联主键集合
     * @return 结果
     */
    boolean deleteAthenaBranchMeterLinkByIds(Long[] ids);

    /**
     * 删除支路电表关联信息
     *
     * @param id 支路电表关联主键
     * @return 结果
     */
    int deleteAthenaBranchMeterLinkById(Long id);
}
