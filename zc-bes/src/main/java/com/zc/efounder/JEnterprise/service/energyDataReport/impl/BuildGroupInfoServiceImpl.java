package com.zc.efounder.JEnterprise.service.energyDataReport.impl;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildGroupInfo;
import com.zc.efounder.JEnterprise.mapper.energyDataReport.BuildGroupInfoMapper;
import com.zc.efounder.JEnterprise.service.energyDataReport.BuildGroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 建筑群信息Service业务层处理
 *
 * @author ruoyi
 * @date 2022-09-13
 */
@Service
public class BuildGroupInfoServiceImpl implements BuildGroupInfoService
{
    @Autowired
    private BuildGroupInfoMapper buildGroupInfoMapper;

    /**
     * 查询建筑群信息
     *
     * @param id 建筑群信息主键
     * @return 建筑群信息
     */
    @Override
    public BuildGroupInfo selectBuildGroupInfoById(Long id)
    {
        return buildGroupInfoMapper.selectBuildGroupInfoById(id);
    }

    /**
     * 查询建筑群信息列表
     *
     * @param buildGroupInfo 建筑群信息
     * @return 建筑群信息
     */
    @Override
    public List<BuildGroupInfo> selectBuildGroupInfoList(BuildGroupInfo buildGroupInfo)
    {
        return buildGroupInfoMapper.selectBuildGroupInfoList(buildGroupInfo);
    }

    /**
     * 新增建筑群信息
     *
     * @param buildGroupInfo 建筑群信息
     * @return 结果
     */
    @Override
    public AjaxResult insertBuildGroupInfo(BuildGroupInfo buildGroupInfo)
    {
        String buildGroupName = buildGroupInfo.getBuildGroupName();
        String groupAliasName = buildGroupInfo.getGroupAliasName();
        String groupDesc = buildGroupInfo.getGroupDesc();

        if (!StringUtils.hasText(buildGroupName)
                || !StringUtils.hasText(groupAliasName)
                ||!StringUtils.hasText(groupDesc)){
            return AjaxResult.error("参数错误");
        }

        List<BuildGroupInfo> buildGroupInfoList = buildGroupInfoMapper.queryBuildGroupInfoByName(buildGroupInfo);
        if (buildGroupInfoList.size() > 0){
            return AjaxResult.error("添加失败,建筑群名称重复!");
        }
        buildGroupInfo.setCreateTime(DateUtils.getNowDate());
        buildGroupInfoMapper.insertBuildGroupInfo(buildGroupInfo);
        return AjaxResult.success("添加成功");
    }

    /**
     * 修改建筑群信息
     *
     * @param buildGroupInfo 建筑群信息
     * @return 结果
     */
    @Override
    public AjaxResult updateBuildGroupInfo(BuildGroupInfo buildGroupInfo)
    {
        String buildGroupName = buildGroupInfo.getBuildGroupName();
        String groupAliasName = buildGroupInfo.getGroupAliasName();
        String groupDesc = buildGroupInfo.getGroupDesc();

        if (!StringUtils.hasText(buildGroupName)
                || !StringUtils.hasText(groupAliasName)
                || buildGroupInfo.getId()==null
                ||!StringUtils.hasText(groupDesc)){
            return AjaxResult.error("参数错误");
        }

        buildGroupInfo.setUpdateTime(DateUtils.getNowDate());
        buildGroupInfoMapper.updateBuildGroupInfo(buildGroupInfo);
        return AjaxResult.success("修改成功");
    }

    /**
     * 批量删除建筑群信息
     *
     * @param ids 需要删除的建筑群信息主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteBuildGroupInfoByIds(Long[] ids)
    {
        if (ids.length < 1){
            return AjaxResult.error("参数错误");
        }
        buildGroupInfoMapper.deleteBuildGroupInfoByIds(ids);
        return AjaxResult.success("删除成功");
    }

    /**
     * 删除建筑群信息信息
     *
     * @param id 建筑群信息主键
     * @return 结果
     */
    @Override
    public int deleteBuildGroupInfoById(Long id)
    {
        return buildGroupInfoMapper.deleteBuildGroupInfoById(id);
    }
}
