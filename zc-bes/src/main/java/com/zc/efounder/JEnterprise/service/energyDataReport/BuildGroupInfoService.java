package com.zc.efounder.JEnterprise.service.energyDataReport;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildGroupInfo;

/**
 * 建筑群信息Service接口
 *
 * @author ruoyi
 * @date 2022-09-13
 */
public interface BuildGroupInfoService
{
    /**
     * 查询建筑群信息
     *
     * @param id 建筑群信息主键
     * @return 建筑群信息
     */
    BuildGroupInfo selectBuildGroupInfoById(Long id);

    /**
     * 查询建筑群信息列表
     *
     * @param buildGroupInfo 建筑群信息
     * @return 建筑群信息集合
     */
    List<BuildGroupInfo> selectBuildGroupInfoList(BuildGroupInfo buildGroupInfo);

    /**
     * 新增建筑群信息
     *
     * @param buildGroupInfo 建筑群信息
     * @return 结果
     */
    AjaxResult insertBuildGroupInfo(BuildGroupInfo buildGroupInfo);

    /**
     * 修改建筑群信息
     *
     * @param buildGroupInfo 建筑群信息
     * @return 结果
     */
    AjaxResult updateBuildGroupInfo(BuildGroupInfo buildGroupInfo);

    /**
     * 批量删除建筑群信息
     *
     * @param ids 需要删除的建筑群信息主键集合
     * @return 结果
     */
    AjaxResult deleteBuildGroupInfoByIds(Long[] ids);

    /**
     * 删除建筑群信息信息
     *
     * @param id 建筑群信息主键
     * @return 结果
     */
    int deleteBuildGroupInfoById(Long id);
}
