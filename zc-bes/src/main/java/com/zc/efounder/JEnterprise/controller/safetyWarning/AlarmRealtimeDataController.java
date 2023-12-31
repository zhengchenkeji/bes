package com.zc.efounder.JEnterprise.controller.safetyWarning;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmRealtimeData;
import com.zc.efounder.JEnterprise.service.safetyWarning.IAlarmRealtimeDataService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 告警实时数据Controller
 *
 * @author qindehua
 * @date 2022-11-04
 */
@RestController
@RequestMapping("/alarmRealtime/data")
@Api(value = "AlarmRealtimeDataController", tags = {"告警实时数据"})
@ApiSupport(order = 26,author = "qindehua")
public class AlarmRealtimeDataController extends BaseController {
    @Autowired
    private IAlarmRealtimeDataService alarmRealtimeDataService;

    /**
     * 查询告警实时数据列表
     */
    @ApiOperation(value = "查询告警实时数据列表")
    @ApiOperationSupport(ignoreParameters = "id")
    @PreAuthorize("@ss.hasPermi('alarmRealtime:data:list')")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "alarmValue", value = "告警值",  paramType = "query"),
            @ApiImplicitParam(name = "lastTime", value = "最后一次产生时间",  paramType = "query",dataType = "date-time"),
            @ApiImplicitParam(name = "promptMsg", value = "提示信息 （告警描述）",  paramType = "query"),
            @ApiImplicitParam(name = "alarmTypeId", value = "所属告警类型 1:电表  2:支路  3:分户 4:分项 5:设备报警",  paramType = "query"),
    })
    public TableDataInfo list(AlarmRealtimeData alarmRealtimeData) {
        startPage();
        List<AlarmRealtimeData> list = alarmRealtimeDataService.selectAlarmRealtimeDataList(alarmRealtimeData);
        return getDataTable(list);
    }



    /**
     * 处理实时报警数据
     */
    @ApiOperation(value = "处理实时报警数据")
    @PreAuthorize("@ss.hasPermi('alarmRealtime:data:edit')")
    @PutMapping("/updateAlarm")
    public AjaxResult updateAlarm(@RequestBody List<AlarmRealtimeData> alarmRealtimeDatas) {
        return alarmRealtimeDataService.updateAlarmRealtimeData(alarmRealtimeDatas,false);
    }

    /**
     * 获取告警实时数据条数
     */
    @ApiOperation(value = "获取告警实时数据条数")
    @PreAuthorize("@ss.hasPermi('alarmRealtime:data:list')")
    @GetMapping("/alarmCount")
    public AjaxResult getAlarmCount() {
        return AjaxResult.success(alarmRealtimeDataService.selectAlarmCount());
    }

    /**
     * 获取告警实时数据通过报警级别
     */
    @ApiOperation(value = "获取告警实时数据通过报警级别")
    @PreAuthorize("@ss.hasPermi('alarmRealtime:data:list')")
    @GetMapping("/alarmInfo")
    public AjaxResult alarmInfo(@RequestParam("level") @ApiParam(value = "告警等级 1：一般、2：较大、3：严重",required = true) String level) {
        return alarmRealtimeDataService.selectAlarmInfo(level);
    }


    /**
     * 删除告警实时数据
     */
    @ApiOperation(value = "删除告警实时数据")
    @PreAuthorize("@ss.hasPermi('alarmRealtime:data:remove')")
    @Log(title = "告警实时数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "告警实时数据Id集合(以逗号隔开例如 1,2)",required = true,allowMultiple = true,paramType = "path",dataType = "long")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return alarmRealtimeDataService.deleteAlarmRealtimeDataByIds(ids);
    }
}
