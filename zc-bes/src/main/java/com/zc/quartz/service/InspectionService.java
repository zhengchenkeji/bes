package com.zc.quartz.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.quartz.domain.SysJob;
import com.zc.efounder.JEnterprise.domain.deviceSynchronization.AthenaBesTimeTaskSyncSb;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * 定时巡检service
 *
 * @author qindehua
 * @date 2023/02/03
 */
public interface InspectionService {


    /**
     * 查询定时同步任务列表
     *
     * @param SysJob 定时同步任务
     * @return {@code List<SysJob> }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    List<SysJob> selectTaskSyncList(SysJob SysJob);


    /**
     * 新增定时同步任务
     *
     * @param SysJob 定时同步任务
     * @return 结果
     */
    AjaxResult insertTaskSync(SysJob SysJob) throws Exception;


    /**
     * 更新定时同步任务
     *
     * @param SysJob 定时同步任务
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    AjaxResult updateTaskSync(SysJob SysJob) throws Exception;

    /**
     * 批量删除定时同步任务
     *
     * @param ids 需要删除的定时同步任务主键集合
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    AjaxResult deleteTaskSyncByIds(Long[] ids) throws SchedulerException;

    /**
     * 查询定时同步任务
     *
     * @param id 定时同步任务主键
     * @return {@code SysJob }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    public SysJob selectTaskSyncById(Long id);

    /**
     * 查询设备树结构
     *
     * @param deviceTree 设备树
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/06
     **/
    AjaxResult getDeviceTree(DeviceTree deviceTree);


    /**
     * 获取同步任务点位
     *
     * @param id 定时任务Id
     * @return {@code List<AthenaBesTimeTaskSyncSb> }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    List<AthenaBesTimeTaskSyncSb> querySbList(String id);

    /**
     * 获取所有选中的节点
     *
     * @param id id
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    AjaxResult getCheckNodes(String id);


    /**
     * 改变状态
     *
     * @param job 定时任务
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    AjaxResult changeStatus(SysJob job) throws SchedulerException;

    /**
     * 立即执行一次
     *
     * @param job 定时任务
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    AjaxResult runJob(SysJob job) throws SchedulerException;


}
