package com.zc.efounder.JEnterprise.service.electricPowerTranscription;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.PowerData;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;

import java.util.List;
import java.util.Map;

/**
 * @Description 用能概况Service
 *
 * @author gaojikun
 * @date 2022/11/9 10:42
 */
public interface PowerDataService {

    /**
     * 查询所有园区
     */
    List<Park> getAllPark();

    /**
     * 查询所有能源类型
     */
    List<EnergyType> getEnergyType(EnergyConfig energyConfig);

    /**
     * 查询支路下电表/设备
     */
    AjaxResult getCheckMeterList(PowerData powerData);

    /**
     * 查询采集参数
     */
    AjaxResult getAllCheckMeterParamsList(PowerData powerData);

    /**
     * 查询支路下电表/设备下绑定采集参数/数据项
     */
    AjaxResult getMeterParamsList(PowerData powerData);


    /**
     * 查询采集参数下分项
     */
    AjaxResult getMeterParamsConfigList(PowerData powerData);

    /**
     * 查询电表下采集参数
     */
    AjaxResult getMeterParams(PowerData powerData);

    /**
     * 日原始数据
     */
    AjaxResult queryDayChartsData(PowerData powerData);

    AjaxResult queryDayTableData(PowerData powerData);

    List<Map<String, Object>> queryDayExport(PowerData powerData);

    /**
     * 逐日极值数据
     */
    AjaxResult queryMaxChartsData(PowerData powerData);

    AjaxResult queryMaxTableData(PowerData powerData);

    List<Map<String, Object>> queryMaxExport(PowerData powerData);

}