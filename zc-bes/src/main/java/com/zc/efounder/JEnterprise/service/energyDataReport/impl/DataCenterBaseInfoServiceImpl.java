package com.zc.efounder.JEnterprise.service.energyDataReport.impl;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.zc.efounder.JEnterprise.mapper.energyDataReport.DataCenterBaseInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.efounder.JEnterprise.service.energyDataReport.IDataCenterBaseInfoService;
import org.springframework.util.StringUtils;

/**
 * 数据中心基本信息Service业务层处理
 *
 * @author ruoyi
 * @date 2022-09-13
 */
@Service
public class DataCenterBaseInfoServiceImpl implements IDataCenterBaseInfoService
{
    @Autowired
    private DataCenterBaseInfoMapper dataCenterBaseInfoMapper;

    /**
     * 查询数据中心基本信息
     *
     * @param id 数据中心基本信息主键
     * @return 数据中心基本信息
     */
    @Override
    public DataCenterBaseInfo selectDataCenterBaseInfoById(Long id)
    {
        return dataCenterBaseInfoMapper.selectDataCenterBaseInfoById(id);
    }

    /**
     * 查询数据中心基本信息列表
     *
     * @param dataCenterBaseInfo 数据中心基本信息
     * @return 数据中心基本信息
     */
    @Override
    public List<DataCenterBaseInfo> selectDataCenterBaseInfoList(DataCenterBaseInfo dataCenterBaseInfo)
    {
        return dataCenterBaseInfoMapper.selectDataCenterBaseInfoList(dataCenterBaseInfo);
    }

    /**
     * 新增数据中心基本信息
     *
     * @param dataCenterBaseInfo 数据中心基本信息
     * @return 结果
     */
    @Override
    public AjaxResult insertDataCenterBaseInfo(DataCenterBaseInfo dataCenterBaseInfo)
    {
        String dataCenterId = dataCenterBaseInfo.getDataCenterId();
        String dataCenterName = dataCenterBaseInfo.getDataCenterName();
        String dataCenterIp = dataCenterBaseInfo.getDataCenterIp();
        String dataCenterPort = dataCenterBaseInfo.getDataCenterPort().toString();
        String dataCenterType = dataCenterBaseInfo.getDataCenterType().toString();
        String dataCenterManager = dataCenterBaseInfo.getDataCenterManager();
        String dataCenterDesc = dataCenterBaseInfo.getDataCenterDesc();
        String dataCenterContact = dataCenterBaseInfo.getDataCenterContact();
        String dataCenterTel = dataCenterBaseInfo.getDataCenterTel();

        if (!StringUtils.hasText(dataCenterId)
                ||!StringUtils.hasText(dataCenterName)
                ||!StringUtils.hasText(dataCenterIp)
                ||!StringUtils.hasText(dataCenterPort)
                ||!StringUtils.hasText(dataCenterType)
                ||!StringUtils.hasText(dataCenterManager)
                ||!StringUtils.hasText(dataCenterDesc)
                ||!StringUtils.hasText(dataCenterContact)
                ||!StringUtils.hasText(dataCenterTel)
        ){
            return AjaxResult.error("添加失败,参数错误");
        }

        dataCenterBaseInfo.setCreateTime(DateUtils.getNowDate());

        //验证系统名称是否重复
        List<DataCenterBaseInfo> dataCenterBaseInfoList = dataCenterBaseInfoMapper.queryDataCenterId(dataCenterBaseInfo);
        if (dataCenterBaseInfoList.size() > 0) {
            return AjaxResult.error("添加失败,数据中心代码重复!");
        }
        dataCenterBaseInfoMapper.insertDataCenterBaseInfo(dataCenterBaseInfo);
        return AjaxResult.success("添加成功");
    }

    /**
     * 修改数据中心基本信息
     *
     * @param dataCenterBaseInfo 数据中心基本信息
     * @return 结果
     */
    @Override
    public AjaxResult updateDataCenterBaseInfo(DataCenterBaseInfo dataCenterBaseInfo)
    {
        String dataCenterId = dataCenterBaseInfo.getDataCenterId();
        String dataCenterName = dataCenterBaseInfo.getDataCenterName();
        String dataCenterIp = dataCenterBaseInfo.getDataCenterIp();
        String dataCenterPort = dataCenterBaseInfo.getDataCenterPort().toString();
        String dataCenterType = dataCenterBaseInfo.getDataCenterType().toString();
        String dataCenterManager = dataCenterBaseInfo.getDataCenterManager();
        String dataCenterDesc = dataCenterBaseInfo.getDataCenterDesc();
        String dataCenterContact = dataCenterBaseInfo.getDataCenterContact();
        String dataCenterTel = dataCenterBaseInfo.getDataCenterTel();

        if (!StringUtils.hasText(dataCenterId)
                ||!StringUtils.hasText(dataCenterName)
                ||dataCenterBaseInfo.getId()==null
                ||!StringUtils.hasText(dataCenterIp)
                ||!StringUtils.hasText(dataCenterPort)
                ||!StringUtils.hasText(dataCenterType)
                ||!StringUtils.hasText(dataCenterManager)
                ||!StringUtils.hasText(dataCenterDesc)
                ||!StringUtils.hasText(dataCenterContact)
                ||!StringUtils.hasText(dataCenterTel)
        ){
            return AjaxResult.error("修改失败,参数错误");
        }

        dataCenterBaseInfo.setUpdateTime(DateUtils.getNowDate());
        dataCenterBaseInfoMapper.updateDataCenterBaseInfo(dataCenterBaseInfo);
        return AjaxResult.success("修改成功");
    }

    /**
     * 批量删除数据中心基本信息
     *
     * @param ids 需要删除的数据中心基本信息主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteDataCenterBaseInfoByIds(Long[] ids)
    {
        if (ids.length < 1){
            return AjaxResult.error("参数错误");
        }
        dataCenterBaseInfoMapper.deleteDataCenterBaseInfoByIds(ids);
        return AjaxResult.success("删除成功");
    }

    /**
     * 删除数据中心基本信息信息
     *
     * @param id 数据中心基本信息主键
     * @return 结果
     */
    @Override
    public int deleteDataCenterBaseInfoById(Long id)
    {
        return dataCenterBaseInfoMapper.deleteDataCenterBaseInfoById(id);
    }
}
