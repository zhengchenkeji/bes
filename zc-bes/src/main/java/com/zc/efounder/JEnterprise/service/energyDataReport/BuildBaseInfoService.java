package com.zc.efounder.JEnterprise.service.energyDataReport;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildGroupInfo;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;

/**
 * 建筑基本项数据Service接口
 *
 * @author liuwenge
 * @date 2022-09-14
 */
public interface BuildBaseInfoService
{
    /**
     * 查询建筑基本项数据
     * @author liuwenge
     * @param id 建筑基本项数据主键
     * @return 建筑基本项数据
     */
    AjaxResult selectBuildBaseInfoById(Long id);

    /**
     * 查询建筑基本项数据列表
     * @author liuwenge
     * @param buildBaseInfo 建筑基本项数据
     * @return 建筑基本项数据集合
     */
    List<BuildBaseInfo> selectBuildBaseInfoList(BuildBaseInfo buildBaseInfo);

    /**
     * 新增建筑基本项数据
     * @author liuwenge
     * @param buildBaseInfo 建筑基本项数据
     * @return 结果
     */
    AjaxResult insertBuildBaseInfo(BuildBaseInfo buildBaseInfo);

    /**
     * 修改建筑基本项数据
     * @author liuwenge
     * @param buildBaseInfo 建筑基本项数据
     * @return 结果
     */
    AjaxResult updateBuildBaseInfo(BuildBaseInfo buildBaseInfo);

    /**
     * 批量删除建筑基本项数据
     * @author liuwenge
     * @param ids 需要删除的建筑基本项数据主键集合
     * @return 结果
     */
    AjaxResult deleteBuildBaseInfoByIds(Long[] ids);

    /**
     * 删除建筑基本项数据信息
     * @author liuwenge
     * @param id 建筑基本项数据主键
     * @return 结果
     */
    int deleteBuildBaseInfoById(Long id);

    /**
     * 查询所有数据中心
     * @author liuwenge
     */
    List<DataCenterBaseInfo> getAllDataCenterBaseInfo();

    /**
     * 查询所有建筑群
     * @author liuwenge
     */
    List<BuildGroupInfo> getAllBuildGroup();

    /**
     * 查询所有建筑群
     * @author liuwenge
     */
    List<Park> getAllPark();

    /**
     * @Description 导出查询
     *
     * @author liuwenge
     * @param buildBaseInfo
     * @return java.util.List<com.ruoyi.energyDataReport.buildBaseInfo.domain.BuildBaseInfo>
     */
    List<BuildBaseInfo> selectAllInfo(BuildBaseInfo buildBaseInfo);
}
