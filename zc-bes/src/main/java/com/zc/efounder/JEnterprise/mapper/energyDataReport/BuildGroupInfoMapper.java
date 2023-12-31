package com.zc.efounder.JEnterprise.mapper.energyDataReport;

import java.util.List;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildGroupInfo;

/**
 * 建筑群信息Mapper接口
 * 
 * @author ruoyi
 * @date 2022-09-13
 */
public interface BuildGroupInfoMapper 
{
    /**
     * 查询建筑群信息
     * 
     * @param id 建筑群信息主键
     * @return 建筑群信息
     */
    public BuildGroupInfo selectBuildGroupInfoById(Long id);

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
    int insertBuildGroupInfo(BuildGroupInfo buildGroupInfo);

    /**
     * 修改建筑群信息
     * 
     * @param buildGroupInfo 建筑群信息
     * @return 结果
     */
    int updateBuildGroupInfo(BuildGroupInfo buildGroupInfo);

    /**
     * 删除建筑群信息
     * 
     * @param id 建筑群信息主键
     * @return 结果
     */
    int deleteBuildGroupInfoById(Long id);

    /**
     * 批量删除建筑群信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteBuildGroupInfoByIds(Long[] ids);

    List<BuildGroupInfo> queryBuildGroupInfoByName(BuildGroupInfo buildGroupInfo);
}
