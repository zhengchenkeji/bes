package com.zc.efounder.JEnterprise.mapper.energyInfo;

import java.util.List;
import java.util.Map;

import com.zc.efounder.JEnterprise.domain.baseData.OtherCalculateData;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import org.apache.ibatis.annotations.Param;

/**
 * 支路电表关联Mapper接口
 * @author qindehua
 */
public interface AthenaBranchMeterLinkMapper
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
    List<AthenaBranchMeterLink> selectAthenaBranchMeterLinkList(@Param("branchId") Long branchId,@Param("energyCode") String energyCode);

    /**
     * 查询支路电表关联列表 非联表
     *
     * @param branchId 支路ID
     * @return 支路电表关联集合
     */
    List<AthenaBranchMeterLink> selectAthenaBranchMeterLinkListInfo(Long branchId);

    /**
     * 新增支路电表关联
     *
     * @param athenaBranchMeterLink 支路电表关联
     * @return 结果
     */
    int insertAthenaBranchMeterLink(AthenaBranchMeterLink athenaBranchMeterLink);

    /**
     * 批量新增支路电表关联
     *
     * @param athenaBranchMeterLinks 支路电表关联集合
     * @return 结果
     */
    Boolean batchInsertAthenaBranchMeterLink(@Param("athenaBranchMeterLinks") List<AthenaBranchMeterLink> athenaBranchMeterLinks);

    /**
     * 修改支路电表关联
     *
     * @param athenaBranchMeterLink 支路电表关联
     * @return 结果
     */
    int updateAthenaBranchMeterLink(AthenaBranchMeterLink athenaBranchMeterLink);

    /**
     * 删除支路电表关联
     *
     * @param id 支路电表关联主键
     * @return 结果
     */
    int deleteAthenaBranchMeterLinkById(Long id);

    /**
     * 根据支路ID 删除支路电表关联
     *
     * @param branchId 支路ID
     * @return 结果
     */
    boolean deleteAthenaBranchMeterLinkByBranchId(Long branchId);

    /**
     * 根据支路Ids和电表Ids 删除支路电表关联
     *
     * @param cascadedIds 分支级联ids
     * @param meterIds    电表ids
     * @return 结果
     */
    boolean deleteAthenaBranchMeterLinkByBranchIdsAndMeterIds(@Param("cascadedIds") List<Long> cascadedIds,@Param("meterIds")List<Long> meterIds );


    /**
     * 批量删除支路电表关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    boolean deleteAthenaBranchMeterLinkByIds(Long[] ids);

    /**
     * 批量删除支路电表关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAthenaBranchMeterLinkByCascadedIds(@Param("ids") List<Long> ids);

    /**
     * 根据meterId删除支路电表关联
     *
     * @param athenaBranchMeterLink
     * @return 结果
     */
    boolean deleteAthenaBranchMeterLinkByMeterId(AthenaBranchMeterLink athenaBranchMeterLink);



    List<AthenaBranchMeterLink> selectAll();


    /**
     * @description:根据电表查询相关支路数据
     * @author: sunshangeng
     * @param:
     * @return:
     **/
    List<Map> selectBranchReportForm(@Param("sysname")String sysname,@Param("kssj")String kssj,@Param("jssj") String jssj);




    Map selectBranchEneryCache(@Param("equipmentId")String equipmentId, @Param("itemDataId")String itemDataId);
}
