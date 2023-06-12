package com.zc.efounder.JEnterprise.mapper.energyInfo;

import com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity;
import com.zc.efounder.JEnterprise.domain.energyInfo.ManualEntryEnergy;

import java.util.List;

/**
 * 手动录入能耗信息Mapper接口
 *
 * @author ruoyi
 * @date 2022-12-02
 */
public interface ManualentryenergyMapper
{


    /**
     * 查询手动录入能耗信息
     *
     * @param manualentryenergy 手动录入能耗信息对象
     * @return 能耗信息记录集合
     */
    List<ManualEntryEnergy> selectManualEntryEnergyList(ManualEntryEnergy manualentryenergy);


    /**
     * 新增手动录入能耗信息
     *
     * @param manualentryenergy 手动录入能耗信息
     * @return 结果
     */
    Boolean insertManualentryenergy(ManualEntryEnergy manualentryenergy);


    /**
     * 批量删除手动录入能耗信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteManualentryenergyByIds(Long[] ids);

    List<DicDataEntity> getElectricParamsbyCollCode(String collCode);

}
