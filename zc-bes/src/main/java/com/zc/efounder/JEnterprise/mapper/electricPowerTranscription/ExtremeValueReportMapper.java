package com.zc.efounder.JEnterprise.mapper.electricPowerTranscription;

import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ExtremeValueReport;

import java.util.List;
import java.util.Map;

/**
 * @author gaojikun
 * @Description 用能概况Mapper接口
 * @date 2022/11/9 10:49
 */
public interface ExtremeValueReportMapper {

    /**
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Description 查询极值报表BES
     */
    List<Map<String, Object>> queryDayChartsData(ExtremeValueReport extremeValueReport);

    /**
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Description 查询极值报表第三方
     */
    List<Map<String, Object>> queryDayChartsDataOther(ExtremeValueReport extremeValueReport);

    /**
     * @Description 查询支路下电表
     */
    List<Map<String, Object>> queryMeterList(ExtremeValueReport extremeValueReport);
    /**
     * @Description 查询支路下设备
     */
    List<Map<String, Object>> queryEquipmentList(ExtremeValueReport extremeValueReport);
}