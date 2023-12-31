package com.zc.efounder.JEnterprise.service.electricPowerTranscription;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ExtremeValueReport;

import java.util.List;
import java.util.Map;

/**
 * @Description 用能概况Service
 *
 * @author gaojikun
 * @date 2022/11/9 10:42
 */
public interface ExtremeValueReportService {

    /**
     * 查询极值报表
     */
    AjaxResult queryDayChartsData(ExtremeValueReport extremeValueReport);

    List<Map<String, Object>> queryDayChartsDataList(ExtremeValueReport extremeValueReport);

}