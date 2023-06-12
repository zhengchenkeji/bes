package com.zc.quartz.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 定时同步任务对象 athena_bes_time_task_sync
 *
 * @author ruoyi
 * @date 2022-11-01
 */
public class AthenaBesTimeTaskSync extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String id;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String jobName;
    /** 任务组名 */
    @Excel(name = "任务组名")
    private String jobGroup;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String equipmentName;

    /**调用字符串*/
    @Excel(name = "设备名称")
    private String invokeTarget;

    @Excel(name = "执行策略")
    private String misfirePolicy;


    @Excel(name = "是否允许并发执行")
    private String concurrent;

    /** cron表达式 */
    @Excel(name = "cron表达式")
    private String cron;

    /** 状态(0未启用,1启用) */
    @Excel(name = "状态(0未启用,1启用)")
    private String status;

    /** 定时任务id */
    @Excel(name = "定时任务id")
    private String jobId;

    /**选择的设备节点id数据组*/
    private String[] treeids;


    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
    public void setJobName(String jobName)
    {
        this.jobName = jobName;
    }

    public String getJobName()
    {
        return jobName;
    }
    public void setEquipmentName(String equipmentName)
    {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentName()
    {
        return equipmentName;
    }
    public void setCron(String cron)
    {
        this.cron = cron;
    }

    public String getCron()
    {
        return cron;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }

    public String getJobId()
    {
        return jobId;
    }


    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getInvokeTarget() {
        return invokeTarget;
    }

    public void setInvokeTarget(String invokeTarget) {
        this.invokeTarget = invokeTarget;
    }

    public String getMisfirePolicy() {
        return misfirePolicy;
    }

    public void setMisfirePolicy(String misfirePolicy) {
        this.misfirePolicy = misfirePolicy;
    }

    public String getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(String concurrent) {
        this.concurrent = concurrent;
    }

    public String[] getTreeids() {
        return treeids;
    }

    public void setTreeids(String[] treeids) {
        this.treeids = treeids;
    }


}
