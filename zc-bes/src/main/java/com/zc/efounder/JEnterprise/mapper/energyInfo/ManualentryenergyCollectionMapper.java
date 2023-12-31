package com.zc.efounder.JEnterprise.mapper.energyInfo;

import com.zc.efounder.JEnterprise.domain.energyInfo.ManualentryenergyCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 手动录入能耗详情Mapper接口
 *
 * @author ruoyi
 * @date 2022-12-02
 */
public interface ManualentryenergyCollectionMapper
{

    /**
     * 新增手动录入能耗详情
     *
     * @param ManualentryenergyCollection 手动录入能耗详情
     * @return 结果
     */
    boolean insertManualentryenergyCollection(ManualentryenergyCollection ManualentryenergyCollection);



    List<ManualentryenergyCollection>  selectEnergyDetailData(@Param("entryEnergyId") String entryEnergyId);

    Boolean deleteByManualentryenergyId(@Param("manualentryenergyId") Long manualentryenergyId);


}
