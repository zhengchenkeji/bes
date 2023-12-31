package com.zc.efounder.JEnterprise.service.energyCollection;

import com.zc.efounder.JEnterprise.domain.energyCollection.MeterType;

import java.util.List;

/**
 * 电表类型定义Service接口
 *
 * @author ruoyi
 * @date 2022-09-07
 */
public interface MeterTypeService
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
     * 导入电表类型定义
     *
     * @param meterTypeList 电表类型定义数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importMeterType(List<MeterType> meterTypeList, Boolean isUpdateSupport, String operName);

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
     * 批量删除电表类型定义
     *
     * @param ids 需要删除的电表类型定义主键集合
     * @return 结果
     */
    int deleteMeterTypeByIds(Long[] ids);

    /**
     * 删除电表类型定义信息
     *
     * @param id 电表类型定义主键
     * @return 结果
     */
    int deleteMeterTypeById(Long id);
}
