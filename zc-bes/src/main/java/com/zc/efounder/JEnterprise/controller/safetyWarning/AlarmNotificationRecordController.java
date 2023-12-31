package com.zc.efounder.JEnterprise.controller.safetyWarning;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotificationRecord;
import com.zc.efounder.JEnterprise.service.safetyWarning.AlarmNotificationRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 告警通知记录Controller
 *
 * @author ruoyi
 * @date 2022-09-19
 */
@RestController
@RequestMapping("/safetyWarning/NotificationRecord")
@Api(value = "AlarmNotificationRecordController", tags = {"告警通知记录"})
@ApiSupport(order = 29)
public class AlarmNotificationRecordController extends BaseController
{
    @Autowired
    private AlarmNotificationRecordService AlarmNotificationRecordService;

    /**
     * 查询告警通知记录列表
     */
    @ApiOperation(value = "查询告警通知记录列表")
    @PreAuthorize("@ss.hasPermi('safetyWarning:NotificationRecord:list')")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(AlarmNotificationRecord alarmNotificationRecord)
    {
        startPage();
        List<AlarmNotificationRecord> list = AlarmNotificationRecordService.selectAlarmNotificationRecordList(alarmNotificationRecord);
        return getDataTable(list);
    }
    /**
     * 导出告警通知记录列表
     */
    @PreAuthorize("@ss.hasPermi('safetyWarning:NotificationRecord:export')")
    @Log(title = "告警通知记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出告警通知记录列表")
    public void export(HttpServletResponse response, AlarmNotificationRecord alarmNotificationRecord)
    {
        List<AlarmNotificationRecord> list = AlarmNotificationRecordService.exportAlarmNotificationRecordList(alarmNotificationRecord);
        ExcelUtil<AlarmNotificationRecord> util = new ExcelUtil<>(AlarmNotificationRecord.class);
        util.exportExcel(response, list, "告警通知记录数据");
    }
}
