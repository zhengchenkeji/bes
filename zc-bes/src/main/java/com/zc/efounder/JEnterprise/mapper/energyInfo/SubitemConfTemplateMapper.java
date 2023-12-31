package com.zc.efounder.JEnterprise.mapper.energyInfo;

import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemConfTemplate;

import java.util.List;

/**
 * 批量新增分项模板Mapper接口
 *
 * @author qindehua
 * @date 2022-11-08
 */
public interface SubitemConfTemplateMapper
{
    /**
     * 查询批量新增分项模板
     *
     * @param subitemCode 批量新增分项模板主键
     * @return 批量新增分项模板
     */
    public SubitemConfTemplate selectSubitemConfTemplateBySubitemCode(String subitemCode);

    /**
     * 查询批量新增分项模板列表
     *
     * @param subitemConfTemplate 批量新增分项模板
     * @return 批量新增分项模板集合
     */
    List<SubitemConfTemplate> selectSubitemConfTemplateList(SubitemConfTemplate subitemConfTemplate);

    /**
     * 新增批量新增分项模板
     *
     * @param subitemConfTemplate 批量新增分项模板
     * @return 结果
     */
    int insertSubitemConfTemplate(SubitemConfTemplate subitemConfTemplate);

    /**
     * 修改批量新增分项模板
     *
     * @param subitemConfTemplate 批量新增分项模板
     * @return 结果
     */
    int updateSubitemConfTemplate(SubitemConfTemplate subitemConfTemplate);

    /**
     * 删除批量新增分项模板
     *
     * @param subitemCode 批量新增分项模板主键
     * @return 结果
     */
    int deleteSubitemConfTemplateBySubitemCode(String subitemCode);

    /**
     * 批量删除批量新增分项模板
     *
     * @param subitemCodes 需要删除的数据主键集合
     * @return 结果
     */
    int deleteSubitemConfTemplateBySubitemCodes(String[] subitemCodes);
}
