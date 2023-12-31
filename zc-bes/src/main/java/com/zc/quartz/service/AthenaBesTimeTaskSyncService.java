package com.zc.quartz.service;

import com.ruoyi.common.core.domain.AjaxResult;

import com.ruoyi.quartz.domain.SysJob;
import com.zc.efounder.JEnterprise.domain.deviceSynchronization.AthenaBesTimeTaskSyncSb;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.quartz.domain.AthenaBesTimeTaskSync;
import org.quartz.SchedulerException;

import java.util.List;
import java.util.Map;

/**
 * 定时同步任务Service接口
 *
 * @author ruoyi
 * @date 2022-11-01
 */
public interface AthenaBesTimeTaskSyncService
{
    /**
     * 查询定时同步任务
     *
     * @param id 定时同步任务主键
     * @return 定时同步任务
     */
    public SysJob selectAthenaBesTimeTaskSyncById(Long id);

    /**
     * 查询定时同步任务列表
     *
     * @param SysJob 定时同步任务
     * @return 定时同步任务集合
     */
    List<SysJob> selectAthenaBesTimeTaskSyncList(SysJob SysJob);

    /**
     * 新增定时同步任务
     *
     * @param SysJob 定时同步任务
     * @return 结果
     */
    AjaxResult insertAthenaBesTimeTaskSync(SysJob SysJob) throws Exception;

    /**
     * 修改定时同步任务
     *
     * @param SysJob 定时同步任务
     * @return 结果
     */
    AjaxResult updateAthenaBesTimeTaskSync(SysJob SysJob) throws Exception;

    /**
     * 批量删除定时同步任务
     *
     * @param ids 需要删除的定时同步任务主键集合
     * @return 结果
     */
    AjaxResult deleteAthenaBesTimeTaskSyncByIds(Long[] ids) throws SchedulerException;



    /**
     * @description:查询设备树结构
     * @author: sunshangeng
     **/
     List<Map<String,Object>> getDeviceTree(DeviceTree deviceTree);


    /**
     * @description:获取同步任务点位
     * @author: sunshangeng
     * @date: 2022/11/2 17:28
     **/
    List<AthenaBesTimeTaskSyncSb> querySbList(AthenaBesTimeTaskSync athenaBesTimeTaskSync);

    /**
     * @description:获取所有选中的节点
     * @author: sunshangeng
     * @date: 2022/11/3 14:55
     * @param:
     * @return:
     **/
    AjaxResult getCheckNodes(String id);

    AjaxResult changeStatus(SysJob job) throws SchedulerException;

    AjaxResult runJob(SysJob job) throws SchedulerException;

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     * @return 结果
     */
     int deleteJob(SysJob job) throws SchedulerException;


}
