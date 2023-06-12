package com.zc.efounder.JEnterprise.service.energyAnalysis;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyAnalysis.EnergyConsumptionTrend;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;

import java.util.List;

/**
 * @Description 用能概况Service
 *
 * @author gaojikun
 * @date 2022/11/9 10:42
 */
public interface EnergyConsumptionTrendService {

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();

    /**
     * 查询所有能源类型
     */
    List<EnergyType> getEnergyType(EnergyConfig energyConfig);

    /**
     * 查询支路能耗趋势
     */
    AjaxResult queryAccessRdData(EnergyConsumptionTrend energyConsumptionTrend);

    /**
     * 查询分户能耗趋势
     */
    AjaxResult queryIndividualAccountData(EnergyConsumptionTrend energyConsumptionTrend);

}