package com.zc.efounder.JEnterprise.mapper.energyCollection;

import com.zc.efounder.JEnterprise.domain.energyCollection.MeterType;

import java.util.List;

/**
 * 电表类型定义Mapper接口
 * 
 * @author ruoyi
 * @date 2022-09-07
 */
public interface MeterTypeMapper 
{
    /**
     * 查询电表类型定义
     * 
     * @param id 电表类型定义主键
     * @return 电表类型定义
     */
    public MeterType selectMeterTypeById(Long id);

    /**
     * 查询电表类型定义列表
     * 
     * @param meterType 电表类型定义
     * @return 电表类型定义集合
     */
    List<MeterType> selectMeterTypeList(MeterType meterType);

    /**
     * 新增查重
     */
    List<MeterType> selectMeterTypeByMeterType(MeterType meterType);

    /**
     * 修改名称查重
     */
    List<MeterType> selectMeterTypeByMeterTypeEdit(MeterType meterType);

    /**
     * 新增电表类型定义
     * 
     * @param meterType 电表类型定义
     * @return 结果
     */
    int insertMeterType(MeterType meterType);

    /**
     * 修改电表类型定义
     * 
     * @param meterType 电表类型定义
     * @return 结果
     */
    int updateMeterType(MeterType meterType);

    /**
     * 删除电表类型定义
     * 
     * @param id 电表类型定义主键
     * @return 结果
     */
    int deleteMeterTypeById(Long id);

    /**
     * 批量删除电表类型定义
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteMeterTypeByIds(Long[] ids);

    /**
     * 根据code查询电表类型定义
     */
    MeterType getInfoByCode(String code);

}
