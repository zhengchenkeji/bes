package com.zc.efounder.JEnterprise.controller.safetyWarning;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmWorkOrder;
import com.zc.efounder.JEnterprise.service.safetyWarning.IAlarmWorkOrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 告警工单Controller
 *
 * @author ruoyi
 * @date 2023-03-06
 */
@RestController
@RequestMapping("/safetyWarning/alarmWorkOrder")
public class AlarmWorkOrderController extends BaseController
{
    @Autowired
    private IAlarmWorkOrderService alarmWorkOrderService;

    /**
     * 查询告警工单列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AlarmWorkOrder alarmWorkOrder)
    {
        startPage();
        List<AlarmWorkOrder> list = alarmWorkOrderService.selectAlarmWorkOrderList(alarmWorkOrder);
        return getDataTable(list);
    }

    /**
     * 导出告警工单列表
     */
    @Log(title = "告警工单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlarmWorkOrder alarmWorkOrder)
    {
        List<AlarmWorkOrder> list = alarmWorkOrderService.selectAlarmWorkOrderList(alarmWorkOrder);
        ExcelUtil<AlarmWorkOrder> util = new ExcelUtil<>(AlarmWorkOrder.class);
        util.exportExcel(response, list, "告警工单数据");
    }

    /**
     * 获取告警工单详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(alarmWorkOrderService.selectAlarmWorkOrderById(id));
    }

    /**
     * 新增告警工单
     */
    @Log(title = "告警工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlarmWorkOrder alarmWorkOrder)
    {
        return toAjax(alarmWorkOrderService.insertAlarmWorkOrder(alarmWorkOrder));
    }

    /**
     * 修改告警工单
     */
    @Log(title = "告警工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlarmWorkOrder alarmWorkOrder)
    {
        return alarmWorkOrderService.updateAlarmWorkOrder(alarmWorkOrder);
    }

    /**
     * 删除告警工单
     */
    @Log(title = "告警工单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(alarmWorkOrderService.deleteAlarmWorkOrderByIds(ids));
    }
}
