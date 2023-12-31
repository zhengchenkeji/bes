package com.zc.efounder.JEnterprise.service.energyDataReport.impl;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildGroupInfo;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.mapper.energyDataReport.BuildBaseInfoMapper;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildBaseInfo;
import com.zc.efounder.JEnterprise.service.energyDataReport.BuildBaseInfoService;
import org.springframework.util.StringUtils;

/**
 * 建筑基本项数据Service业务层处理
 *
 * @author liuwenge
 * @date 2022-09-14
 */
@Service
public class BuildBaseInfoServiceImpl implements BuildBaseInfoService
{
    @Autowired
    private BuildBaseInfoMapper buildBaseInfoMapper;

    /**
     * 查询建筑基本项数据
     * @author liuwenge
     * @param id 建筑基本项数据主键
     * @return 建筑基本项数据
     */
    @Override
    public AjaxResult selectBuildBaseInfoById(Long id)
    {
        if (!StringUtils.hasText(id.toString())){
            return AjaxResult.error("参数错误");
        }
        BuildBaseInfo buildBaseInfo = buildBaseInfoMapper.selectBuildBaseInfoById(id);
        return AjaxResult.success(buildBaseInfo);
    }

    /**
     * 查询建筑基本项数据列表
     * @author liuwenge
     * @param buildBaseInfo 建筑基本项数据
     * @return 建筑基本项数据
     */
    @Override
    public List<BuildBaseInfo> selectBuildBaseInfoList(BuildBaseInfo buildBaseInfo)
    {
        return buildBaseInfoMapper.selectBuildBaseInfoList(buildBaseInfo);
    }

    /**
     * 新增建筑基本项数据
     * @author liuwenge
     * @param buildBaseInfo 建筑基本项数据
     * @return 结果
     */
    @Override
    public AjaxResult insertBuildBaseInfo(BuildBaseInfo buildBaseInfo)
    {
        String dataCenterId = buildBaseInfo.getDataCenterId().toString();
        String buildGroupId = buildBaseInfo.getBuildGroupId().toString();
        String buildName = buildBaseInfo.getBuildName();
        String aliasName = buildBaseInfo.getAliasName();
        String buildOwner = buildBaseInfo.getBuildOwner();
        String state = buildBaseInfo.getState().toString();
        String districtCode = buildBaseInfo.getDistrictCode();
        String buildAddr = buildBaseInfo.getBuildAddr();
        String buildLong = buildBaseInfo.getBuildLong();
        String buildLat = buildBaseInfo.getBuildLat();
        String buildYear = buildBaseInfo.getBuildYear().toString();
        String upFloor = buildBaseInfo.getUpFloor().toString();
        String downFloor = buildBaseInfo.getDownFloor().toString();
        String buildFunc = buildBaseInfo.getBuildFunc();
        String totalArea = buildBaseInfo.getTotalArea();
        String airArea = buildBaseInfo.getAirArea();
        String heatArea = buildBaseInfo.getHeatArea();
        String airType = buildBaseInfo.getAirType();
        String heatType = buildBaseInfo.getHeatType();
        String struType = buildBaseInfo.getStruType();
        String wallMatType = buildBaseInfo.getWallMatType();
        String wallWarmType = buildBaseInfo.getWallWarmType();
        String wallWinType = buildBaseInfo.getWallWinType();
        String glassType = buildBaseInfo.getGlassType();
        String winFrameType = buildBaseInfo.getWinFrameType();
        String isStandard = buildBaseInfo.getIsStandard();
        String designDept = buildBaseInfo.getDesignDept();
        String workDept = buildBaseInfo.getWorkDept();
        String parkId = buildBaseInfo.getParkId();

        if (!StringUtils.hasText(dataCenterId)
                || !StringUtils.hasText(buildGroupId)
                || !StringUtils.hasText(buildName)
                || !StringUtils.hasText(aliasName)
                || !StringUtils.hasText(buildOwner)
                || !StringUtils.hasText(state)
                || !StringUtils.hasText(districtCode)
                || !StringUtils.hasText(buildAddr)
                || !StringUtils.hasText(buildLong)
                || !StringUtils.hasText(buildLat)
                || !StringUtils.hasText(buildYear)
                || !StringUtils.hasText(upFloor)
                || !StringUtils.hasText(downFloor)
                || !StringUtils.hasText(buildFunc)
                || !StringUtils.hasText(totalArea)
                || !StringUtils.hasText(airArea)
                || !StringUtils.hasText(heatArea)
                || !StringUtils.hasText(airType)
                || !StringUtils.hasText(heatType)
                || !StringUtils.hasText(struType)
                || !StringUtils.hasText(wallMatType)
                || !StringUtils.hasText(wallWarmType)
                || !StringUtils.hasText(wallWinType)
                || !StringUtils.hasText(glassType)
                || !StringUtils.hasText(winFrameType)
                || !StringUtils.hasText(isStandard)
                || !StringUtils.hasText(designDept)
                || !StringUtils.hasText(workDept)
                || !StringUtils.hasText(parkId)
        ){
          return AjaxResult.error("参数错误");
        }



        buildBaseInfo.setCreateTime(DateUtils.getNowDate());
        buildBaseInfoMapper.insertBuildBaseInfo(buildBaseInfo);
        return AjaxResult.success("添加成功");
    }

    /**
     * 修改建筑基本项数据
     * @author liuwenge
     * @param buildBaseInfo 建筑基本项数据
     * @return 结果
     */
    @Override
    public AjaxResult updateBuildBaseInfo(BuildBaseInfo buildBaseInfo)
    {
        String dataCenterId = buildBaseInfo.getDataCenterId().toString();
        String buildGroupId = buildBaseInfo.getBuildGroupId().toString();
        String buildName = buildBaseInfo.getBuildName();
        String aliasName = buildBaseInfo.getAliasName();
        String buildOwner = buildBaseInfo.getBuildOwner();
        String state = buildBaseInfo.getState().toString();
        String districtCode = buildBaseInfo.getDistrictCode();
        String buildAddr = buildBaseInfo.getBuildAddr();
        String buildLong = buildBaseInfo.getBuildLong();
        String buildLat = buildBaseInfo.getBuildLat();
        String buildYear = buildBaseInfo.getBuildYear().toString();
        String upFloor = buildBaseInfo.getUpFloor().toString();
        String downFloor = buildBaseInfo.getDownFloor().toString();
        String buildFunc = buildBaseInfo.getBuildFunc();
        String totalArea = buildBaseInfo.getTotalArea();
        String airArea = buildBaseInfo.getAirArea();
        String heatArea = buildBaseInfo.getHeatArea();
        String airType = buildBaseInfo.getAirType();
        String heatType = buildBaseInfo.getHeatType();
        String struType = buildBaseInfo.getStruType();
        String wallMatType = buildBaseInfo.getWallMatType();
        String wallWarmType = buildBaseInfo.getWallWarmType();
        String wallWinType = buildBaseInfo.getWallWinType();
        String glassType = buildBaseInfo.getGlassType();
        String winFrameType = buildBaseInfo.getWinFrameType();
        String isStandard = buildBaseInfo.getIsStandard();
        String designDept = buildBaseInfo.getDesignDept();
        String workDept = buildBaseInfo.getWorkDept();
        String parkId = buildBaseInfo.getParkId();

        if (!StringUtils.hasText(dataCenterId)
                || !StringUtils.hasText(buildGroupId)
                || !StringUtils.hasText(buildName)
                || !StringUtils.hasText(aliasName)
                || !StringUtils.hasText(buildOwner)
                || !StringUtils.hasText(state)
                || !StringUtils.hasText(districtCode)
                || !StringUtils.hasText(buildAddr)
                || !StringUtils.hasText(buildLong)
                || !StringUtils.hasText(buildLat)
                || !StringUtils.hasText(buildYear)
                || !StringUtils.hasText(upFloor)
                || !StringUtils.hasText(downFloor)
                || !StringUtils.hasText(buildFunc)
                || !StringUtils.hasText(totalArea)
                || !StringUtils.hasText(airArea)
                || !StringUtils.hasText(heatArea)
                || !StringUtils.hasText(airType)
                || !StringUtils.hasText(heatType)
                || !StringUtils.hasText(struType)
                || !StringUtils.hasText(wallMatType)
                || !StringUtils.hasText(wallWarmType)
                || !StringUtils.hasText(wallWinType)
                || !StringUtils.hasText(glassType)
                || !StringUtils.hasText(winFrameType)
                || !StringUtils.hasText(isStandard)
                || !StringUtils.hasText(designDept)
                || !StringUtils.hasText(workDept)
                || !StringUtils.hasText(parkId)
        ){
            return AjaxResult.error("参数错误");
        }
        buildBaseInfoMapper.updateBuildBaseInfo(buildBaseInfo);
        return AjaxResult.success("修改成功");
    }

    /**
     * 批量删除建筑基本项数据
     * @author liuwenge
     * @param ids 需要删除的建筑基本项数据主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteBuildBaseInfoByIds(Long[] ids)
    {
        if (ids.length < 1){
            return AjaxResult.error("请选择要删除的建筑");
        }
        buildBaseInfoMapper.deleteBuildBaseInfoByIds(ids);
        return AjaxResult.success("删除成功");
    }

    /**
     * 删除建筑基本项数据信息
     * @author liuwenge
     * @param id 建筑基本项数据主键
     * @return 结果
     */
    @Override
    public int deleteBuildBaseInfoById(Long id)
    {
        return buildBaseInfoMapper.deleteBuildBaseInfoById(id);
    }

    /**
     * 查询所有数据中心
     *  @author liuwenge
     */
    @Override
    public List<DataCenterBaseInfo> getAllDataCenterBaseInfo(){
        return buildBaseInfoMapper.getAllDataCenterBaseInfo();
    }

    /**
     * 查询所有建筑群
     *  @author liuwenge
     */
    @Override
    public List<BuildGroupInfo> getAllBuildGroup(){
        return buildBaseInfoMapper.getAllBuildGroup();
    }

    /**
     * 查询所有园区
     *  @author liuwenge
     */
    @Override
    public List<Park> getAllPark(){
        return buildBaseInfoMapper.getAllPark();
    }

    /**
     * @Description 查询需要导出的数据
     *
     * @author liuwenge
     * @date 2023/1/13 11:36
     * @param buildBaseInfo
     * @return java.util.List<com.ruoyi.energyDataReport.buildBaseInfo.domain.BuildBaseInfo>
     */
    @Override
    public List<BuildBaseInfo> selectAllInfo(BuildBaseInfo buildBaseInfo)
    {
        return buildBaseInfoMapper.selectAllInfo(buildBaseInfo);
    }
}
