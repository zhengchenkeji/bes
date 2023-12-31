package com.zc.efounder.JEnterprise.mapper.energyDataReport;

import java.util.List;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;

/**
 * 数据中心基本信息Mapper接口
 * 
 * @author ruoyi
 * @date 2022-09-13
 */
public interface DataCenterBaseInfoMapper 
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
    int insertDataCenterBaseInfo(DataCenterBaseInfo dataCenterBaseInfo);

    /**
     * 修改数据中心基本信息
     * 
     * @param dataCenterBaseInfo 数据中心基本信息
     * @return 结果
     */
    int updateDataCenterBaseInfo(DataCenterBaseInfo dataCenterBaseInfo);

    /**
     * 删除数据中心基本信息
     * 
     * @param id 数据中心基本信息主键
     * @return 结果
     */
    int deleteDataCenterBaseInfoById(Long id);

    /**
     * 批量删除数据中心基本信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteDataCenterBaseInfoByIds(Long[] ids);

    List<DataCenterBaseInfo> queryDataCenterId(DataCenterBaseInfo dataCenterBaseInfo);
}
