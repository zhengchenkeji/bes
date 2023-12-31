package com.zc.efounder.JEnterprise.mapper.energyCollection;

import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;

import java.util.List;

/**
 * 电能参数采集方案关系Mapper接口
 * 
 * @author gaojikun
 * @date 2022-09-09
 */
public interface ElectricCollRlglMapper 
{
    /**
     * 查询电能参数采集方案关系
     * 
     * @param electricCode 电能参数采集方案关系主键
     * @return 电能参数采集方案关系
     */
    public ElectricCollRlgl selectElectricCollRlglByElectricCode(String electricCode);

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:查询电能参数采集方案关系列表
     * @Return:List<ElectricCollRlgl>
     */
    List<ElectricCollRlgl> selectElectricCollRlglList(ElectricCollRlgl electricCollRlgl);

    /**
     * 新增电能参数采集方案关系
     * 
     * @param electricCollRlgl 电能参数采集方案关系
     * @return 结果
     */
    int insertElectricCollRlgl(ElectricCollRlgl electricCollRlgl);

    /**
     * 根据采集方案code查询能耗参数
     */
    ElectricCollRlgl selectElectricCollRlglByCode(ElectricCollRlgl electricCollRlgl);

    /**
     * 取消一条能耗参数
     */
    int deleteElectricCollRlgl(ElectricCollRlgl electricCollRlgl);

    /**
     * 全部取消
     */
    void removeAllByCollCode(ElectricCollRlgl electricCollRlgl);

    /**
     * 修改电能参数采集方案关系
     * 
     * @param electricCollRlgl 电能参数采集方案关系
     * @return 结果
     */
    boolean updateElectricCollRlgl(ElectricCollRlgl electricCollRlgl);

    /**
     * 删除电能参数采集方案关系
     * 
     * @param electricCode 电能参数采集方案关系主键
     * @return 结果
     */
    int deleteElectricCollRlglByElectricCode(String electricCode);

    /**
     * 批量删除电能参数采集方案关系
     * 
     * @param electricCodes 需要删除的数据主键集合
     * @return 结果
     */
    int deleteElectricCollRlglByElectricCodes(String[] electricCodes);

    int removeAllById(String[] ids);
}
