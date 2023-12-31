package com.zc.efounder.JEnterprise.mapper.energyDataReport;

import java.util.List;

import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildGroupInfo;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import org.apache.ibatis.annotations.Param;

/**
 * 建筑基本项数据Mapper接口
 *
 * @author ruoyi
 * @date 2022-09-14
 */
public interface BuildBaseInfoMapper
{
    /**
     * 查询建筑基本项数据
     *
     * @param id 建筑基本项数据主键
     * @return 建筑基本项数据
     */
    public BuildBaseInfo selectBuildBaseInfoById(Long id);

    /**
     * 查询建筑基本项数据列表
     *
     * @param buildBaseInfo 建筑基本项数据
     * @return 建筑基本项数据集合
     */
    List<BuildBaseInfo> selectBuildBaseInfoList(BuildBaseInfo buildBaseInfo);

    /**
     * 新增建筑基本项数据
     *
     * @param buildBaseInfo 建筑基本项数据
     * @return 结果
     */
    int insertBuildBaseInfo(BuildBaseInfo buildBaseInfo);

    /**
     * 修改建筑基本项数据
     *
     * @param buildBaseInfo 建筑基本项数据
     * @return 结果
     */
    int updateBuildBaseInfo(BuildBaseInfo buildBaseInfo);

    /**
     * 删除建筑基本项数据
     *
     * @param id 建筑基本项数据主键
     * @return 结果
     */
    int deleteBuildBaseInfoById(Long id);

    /**
     * 批量删除建筑基本项数据
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteBuildBaseInfoByIds(Long[] ids);


    /**
     * 通过数据中心ID查询建筑基本信息列表
     *
     * @param id 数据中心ID
     * @return {@code List<BuildBaseInfo> }
     * @Author qindehua
     * @Date 2022/11/10
     **/
    List<BuildBaseInfo> selectBuildBaseInfoListByCenterId(@Param("id") Long id);


    /**
     * 查询所有数据中心
     */
    List<DataCenterBaseInfo> getAllDataCenterBaseInfo();

    /**
     * 查询所有建筑群
     */
    List<BuildGroupInfo> getAllBuildGroup();

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();

    List<BuildBaseInfo> selectAllInfo(BuildBaseInfo buildBaseInfo);
}
