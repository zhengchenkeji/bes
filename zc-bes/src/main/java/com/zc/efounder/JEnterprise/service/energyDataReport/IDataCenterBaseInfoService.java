package com.zc.efounder.JEnterprise.service.energyDataReport;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;

/**
 * 数据中心基本信息Service接口
 * 
 * @author ruoyi
 * @date 2022-09-13
 */
public interface IDataCenterBaseInfoService 
{
    /**
     * 查询数据中心基本信息
     * 
     * @param id 数据中心基本信息主键
     * @return 数据中心基本信息
     */
    public DataCenterBaseInfo selectDataCenterBaseInfoById(Long id);

    /**
     * 查询数据中心基本信息列表
     * 
     * @param dataCenterBaseInfo 数据中心基本信息
     * @return 数据中心基本信息集合
     */
    List<DataCenterBaseInfo> selectDataCenterBaseInfoList(DataCenterBaseInfo dataCenterBaseInfo);

    /**
     * 新增数据中心基本信息
     * 
     * @param dataCenterBaseInfo 数据中心基本信息
     * @return 结果
     */
    AjaxResult insertDataCenterBaseInfo(DataCenterBaseInfo dataCenterBaseInfo);

    /**
     * 修改数据中心基本信息
     * 
     * @param dataCenterBaseInfo 数据中心基本信息
     * @return 结果
     */
    AjaxResult updateDataCenterBaseInfo(DataCenterBaseInfo dataCenterBaseInfo);

    /**
     * 批量删除数据中心基本信息
     * 
     * @param ids 需要删除的数据中心基本信息主键集合
     * @return 结果
     */
    AjaxResult deleteDataCenterBaseInfoByIds(Long[] ids);

    /**
     * 删除数据中心基本信息信息
     * 
     * @param id 数据中心基本信息主键
     * @return 结果
     */
    int deleteDataCenterBaseInfoById(Long id);
}
