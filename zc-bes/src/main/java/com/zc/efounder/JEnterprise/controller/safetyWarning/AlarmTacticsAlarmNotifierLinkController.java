package com.zc.efounder.JEnterprise.controller.safetyWarning;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTacticsAlarmNotifierLink;
import com.zc.efounder.JEnterprise.service.safetyWarning.AlarmTacticsAlarmNotifierLinkService;

/**
 * 告警策略告警接收组关联Controller
 *
 * @author sunshangeng
 * @date 2022-09-20
 */
@RestController
@RequestMapping("/safetyWarning/TacticsAlarmNotifierLink")
public class AlarmTacticsAlarmNotifierLinkController extends BaseController
{
    @Autowired
    private AlarmTacticsAlarmNotifierLinkService AlarmTacticsAlarmNotifierLinkService;

    /**
     * 新增告警策略告警接收组关联
     */
    @PreAuthorize("@ss.hasPermi('alarmTactics:alarmTactics:add')")
    @Log(title = "告警策略告警接收组关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink)
    {
        return AlarmTacticsAlarmNotifierLinkService.insertAlarmTacticsAlarmNotifierLink(alarmTacticsAlarmNotifierLink);
    }


    /**
     * 删除告警策略告警接收组关联
     */
    @PreAuthorize("@ss.hasPermi('alarmTactics:alarmTactics:remove')")
    @Log(title = "告警策略告警接收组关联", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink)
    {
        return toAjax(AlarmTacticsAlarmNotifierLinkService.deleteAlarmTacticsAlarmNotifierLinkByIds(alarmTacticsAlarmNotifierLink));
    }


}
