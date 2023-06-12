package com.zc.efounder.JEnterprise.service.energyAnalysis;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;

import java.util.List;

/**
 * @Description 用能概况Service
 *
 * @author liuwenge
 * @date 2022/11/9 10:42
 */
public interface SurveyService{

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();

    /**
     * 查询所有能源类型
     */
    List<EnergyType> getEnergyType();

    /**
     * 查询支路环比
     */
    AjaxResult queryRingRatioData(String branchId);

    /**
     * 查询支路能耗趋势
     */
    AjaxResult queryTrendData(String branchId,String trendType);

    /**
     * 查询支路能耗趋势
     */
    AjaxResult queryRankData(String branchId,String rankType);
}