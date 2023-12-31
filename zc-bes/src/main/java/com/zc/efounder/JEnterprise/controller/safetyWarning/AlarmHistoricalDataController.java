package com.zc.efounder.JEnterprise.controller.safetyWarning;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmHistoricalData;
import com.zc.efounder.JEnterprise.service.safetyWarning.IAlarmHistoricalDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 告警历史数据Controller
 *
 * @author qindehua
 * @date 2022-11-17
 */
@RestController
@RequestMapping("/alarmHistoricalData/data")
@Api(value = "AlarmHistoricalDataController", tags = {"告警历史数据"})
@ApiSupport(order = 27,author = "qindehua")
public class AlarmHistoricalDataController extends BaseController
{
    @Autowired
    private IAlarmHistoricalDataService alarmHistoricalDataService;

    /**
     * 查询告警历史数据列表
     */
    @ApiOperation(value = "查询告警历史数据列表")
    @PreAuthorize("@ss.hasPermi('alarmHistoricalData:data:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(AlarmHistoricalData alarmHistoricalData)
    {
        startPage();
        List<AlarmHistoricalData> list = alarmHistoricalDataService.selectAlarmHistoricalDataList(alarmHistoricalData);
        return getDataTable(list);
    }



    /**
     * 删除告警历史数据
     */
    @ApiOperation(value = "删除告警历史数据")
    @PreAuthorize("@ss.hasPermi('alarmHistoricalData:data:remove')")
    @Log(title = "告警历史数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "告警历史数据ID集合(以逗号隔开例如 1,2)",required = true,allowMultiple = true,paramType = "path",dataType = "long")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return alarmHistoricalDataService.deleteAlarmHistoricalDataByIds(ids);
    }
}
