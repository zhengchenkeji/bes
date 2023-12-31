package com.zc.quartz.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;

import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.util.CronUtils;
import com.ruoyi.quartz.util.ScheduleUtils;
import com.zc.efounder.JEnterprise.domain.deviceSynchronization.AthenaBesTimeTaskSyncSb;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.quartz.domain.AthenaBesTimeTaskSync;
import com.zc.quartz.service.AthenaBesTimeTaskSyncService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 定时同步任务Controller
 *
 * @author ruoyi
 * @date 2022-11-01
 */
//@RestController
//@RequestMapping("/deviceJob/timeTaskSync")
public class synchronizationDeviceController extends BaseController
{
    @Autowired
    private AthenaBesTimeTaskSyncService athenaBesTimeTaskSyncService;

    /**
     * 查询定时同步任务列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysJob SysJob)
    {
        startPage();
        List<SysJob> list = athenaBesTimeTaskSyncService.selectAthenaBesTimeTaskSyncList(SysJob);
        return getDataTable(list);
    }

    /**
     * 导出定时同步任务列表
     */
    @PreAuthorize("@ss.hasPermi('deviceJob:deviceJob:export')")
    @Log(title = "定时同步任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJob SysJob)
    {
        List<SysJob> list = athenaBesTimeTaskSyncService.selectAthenaBesTimeTaskSyncList(SysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<>(SysJob.class);
        util.exportExcel(response, list, "定时同步任务数据");
    }

    /**
     * 获取定时同步任务详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(athenaBesTimeTaskSyncService.selectAthenaBesTimeTaskSyncById(Long.parseLong(id)));
    }

    /**
     * 新增定时同步任务
     */
    @PreAuthorize("@ss.hasPermi('deviceJob:deviceJob:add')")
    @Log(title = "定时同步任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysJob job)
    {
        try {

            if (!CronUtils.isValid(job.getCronExpression()))
            {
                return error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
            }
            else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
            {
                return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
            }
            else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
            {
                return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
            }
            else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
            {
                return error("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
            }
            else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
            {
                return error("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
            }
            else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
            {
                return error("新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
            }
            job.setCreateBy(getUsername());
            return athenaBesTimeTaskSyncService.insertAthenaBesTimeTaskSync(job);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 修改定时同步任务
     */
    @Log(title = "定时同步任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysJob job)
    {
        try {
            if (!CronUtils.isValid(job.getCronExpression()))
            {
                return error("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
            }
            else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
            {
                return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
            }
            else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
            {
                return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
            }
            else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
            {
                return error("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
            }
            else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
            {
                return error("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
            }
            else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
            {
                return error("修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
            }
            job.setUpdateBy(getUsername());
            return athenaBesTimeTaskSyncService.updateAthenaBesTimeTaskSync(job);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除定时同步任务
     */
    @Log(title = "删除定时同步任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) throws SchedulerException {
        return athenaBesTimeTaskSyncService.deleteAthenaBesTimeTaskSyncByIds(ids);
    }
    /***
     * @description:查询设备树控制器列表
     * @author: sunshangeng

     */
    @GetMapping("/listTreeController")
    public AjaxResult getDevicetreejob(DeviceTree deviceTree) {
        List<Map<String,Object>> list = athenaBesTimeTaskSyncService.getDeviceTree(deviceTree);
        return AjaxResult.success(list);
    }

    /**
     * 查询当前任务的点位
     */
    @GetMapping("/querySbList")
    public TableDataInfo querySbList(AthenaBesTimeTaskSync athenaBesTimeTaskSync)
    {
        startPage();
        List<AthenaBesTimeTaskSyncSb> list = athenaBesTimeTaskSyncService.querySbList(athenaBesTimeTaskSync);
        return getDataTable(list);
    }

    /**
     * 获取所有已选中的节点
     */
    @GetMapping("/getChcekNodes/{id}")
    public AjaxResult getChcekNodes(@PathVariable("id") String id)
    {
        return  athenaBesTimeTaskSyncService.getCheckNodes(id);
    }

    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysJob SysJob)
    {
        try {
            return athenaBesTimeTaskSyncService.changeStatus(SysJob);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }

    @PutMapping("/run")
    public AjaxResult run(@RequestBody SysJob job) throws SchedulerException {
        return athenaBesTimeTaskSyncService.runJob(job);
    }

}
