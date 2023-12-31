package com.zc.quartz.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.util.CronUtils;
import com.zc.efounder.JEnterprise.domain.deviceSynchronization.AthenaBesTimeTaskSyncSb;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.quartz.service.InspectionService;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 定时巡检控制器
 *
 * @author qindehua
 * @date 2023/02/03
 */
@RestController
@RequestMapping("/inspection/timeTaskSync")
public class InspectionController extends BaseController
{
    @Resource
    private InspectionService inspectionService;

    /**
     * 查询定时同步任务列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysJob sysJob)
    {
        startPage();
        List<SysJob> list = inspectionService.selectTaskSyncList(sysJob);
        return getDataTable(list);
    }
    /**
     * 新增定时同步任务
     */
    @PreAuthorize("@ss.hasPermi('inspection:inspection:add')")
    @Log(title = "定时同步任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysJob job)
    {
        try {
            if (!CronUtils.isValid(job.getCronExpression()))
            {
                return error("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
            }
            job.setCreateBy(getUsername());
            return inspectionService.insertTaskSync(job);
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
            job.setUpdateBy(getUsername());
            return inspectionService.updateTaskSync(job);
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
        return inspectionService.deleteTaskSyncByIds(ids);
    }

    /**
     * 获取定时同步任务详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(inspectionService.selectTaskSyncById(Long.parseLong(id)));
    }

    /**
     * 查询设备树控制器列表
     *
     * @param deviceTree 设备树
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @GetMapping("/listTreeController")
    public AjaxResult getDevicetreejob(DeviceTree deviceTree) {
        return inspectionService.getDeviceTree(deviceTree);
    }

    /**
     * 查询当前任务的点位
     *
     * @param id 定时任务Id
     * @return {@code TableDataInfo }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @GetMapping("/querySbList")
    public TableDataInfo querySbList(String id)
    {
        startPage();
        List<AthenaBesTimeTaskSyncSb> list = inspectionService.querySbList(id);
        return getDataTable(list);
    }

    /**
     * 获取所有已选中的节点
     *
     * @param id 任务id
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @GetMapping("/getChcekNodes/{id}")
    public AjaxResult getChcekNodes(@PathVariable("id") String id)
    {
        return  inspectionService.getCheckNodes(id);
    }

    /**
     * 改变状态
     *
     * @param sysJob 定时任务
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysJob sysJob)
    {
        try {
            return inspectionService.changeStatus(sysJob);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 执行一次
     *
     * @param job 定时任务
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @PutMapping("/run")
    public AjaxResult run(@RequestBody SysJob job) throws SchedulerException {
        return inspectionService.runJob(job);
    }
}
