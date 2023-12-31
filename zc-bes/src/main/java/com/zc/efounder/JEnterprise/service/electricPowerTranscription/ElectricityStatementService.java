package com.zc.efounder.JEnterprise.service.electricPowerTranscription;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ElectricityStatement;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;

import java.util.List;
import java.util.Map;

/**
 * @Description 电力报表Service
 *
 * @author liuwenge
 * @date 2022/11/9 10:42
 */
public interface ElectricityStatementService {

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();

    /**
     * 查询数据
     */
    AjaxResult queryData(ElectricityStatement electricityStatement);

    /**
     * 导出
     */
    Map<String,List<Map<String,Object>>> exportTable(ElectricityStatement electricityStatement);
}