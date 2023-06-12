package com.zc.efounder.JEnterprise.service.energyAnalysis;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;

import java.util.List;

/**
 * @Description 支路环比Service
 *
 * @author liuwenge
 * @date 2022/11/9 10:42
 */
public interface BranchRingRatioService {

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();

    /**
     * 查询所有能源类型
     */
    List<EnergyType> getEnergyType();

    /**
     * 查询支路环比数据
     */
    AjaxResult queryData(String branchId,String dateType,String dateTime);
}