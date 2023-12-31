package com.zc.efounder.JEnterprise.mapper.energyAnalysis;

import com.zc.efounder.JEnterprise.domain.energyAnalysis.EnergyConsumptionTrend;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;

import java.util.List;
import java.util.Map;

/**
 * @Description 用能概况Mapper接口
 *
 * @author gaojikun
 * @date 2022/11/9 10:49
 */
public interface EnergyConsumptionTrendMapper {

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();

    /**
     * @Description 支路能耗趋势
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String,Object>> queryTrendData(EnergyConsumptionTrend energyConsumptionTrend);

    /**
     * @Description 支路能耗趋势
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    List<Map<String,Object>> queryRankData(EnergyConsumptionTrend energyConsumptionTrend);
}