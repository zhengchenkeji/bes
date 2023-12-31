package com.zc.quartz.service.impl;

import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;

import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.mapper.SysJobMapper;
import com.ruoyi.quartz.util.ScheduleUtils;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.deviceSynchronization.AthenaBesTimeTaskSyncSb;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.mapper.deviceSynchronization.AthenaBesTimeTaskSyncSbMapper;
import com.zc.quartz.service.InspectionService;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定时巡检impl
 *
 * @author qindehua
 * @date 2023/02/03
 */
@Service
public class InspectionServiceImpl implements InspectionService {

    @Resource
    private SysJobMapper sysJobMapper;
    @Resource
    private Scheduler scheduler;
    @Resource
    private RedisCache redisCache;
    @Resource
    private AthenaBesTimeTaskSyncSbMapper athenaBesTimeTaskSyncsbMapper;


    /*****调用方法路径*******/
    private final static String INVOKETARGET = "inspectionTask.inspection()";

    /*****初始化时清空定时巡检缓存*******/
    @PostConstruct
    public void init() {
        redisCache.deleteObject(RedisKeyConstants.BES_Scheduled_Task_Inspection);
    }

    /**
     * 查询定时同步任务列表
     *
     * @param SysJob 定时同步任务
     * @return {@code List<SysJob> }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @Override
    public List<SysJob> selectTaskSyncList(SysJob SysJob) {
        /**设置查询组名*/
        SysJob.setJobGroup("INSPECTION");
        return sysJobMapper.selectJobList(SysJob);
    }

    /**
     * 新增定时任务
     *
     * @param sysJob 定时任务
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertTaskSync(SysJob sysJob) throws Exception {
        sysJob.setInvokeTarget(INVOKETARGET);
        sysJob.setCreateTime(DateUtils.getNowDate());

        /**验证传入的数据*/
        if (StringUtils.isBlank(sysJob.getJobName())//任务名称
                || StringUtils.isBlank(sysJob.getEquipmentName())//点位名称
                || sysJob.getTreeids().length == 0//点位id
                || StringUtils.isBlank(sysJob.getCronExpression())//cron表达式
                || StringUtils.isBlank(sysJob.getStatus())//禁用状态
        ) {
            throw new ServiceException("参数传入不完整！");
        }
        Boolean taskId = sysJobMapper.insertJob(sysJob);

        if (!taskId) {
            throw new ServiceException("保存定时同步设备时出错！");
        }

        /**插入同步设备子表*/
        insertTaskSyncSb(sysJob, sysJob.getJobId().intValue());

        /**创建定时任务*/
        ScheduleUtils.createScheduleJob(scheduler, sysJob);

        return AjaxResult.success("添加成功");
    }

    /**
     * 更新定时任务
     *
     * @param sysJob 定时任务
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateTaskSync(SysJob sysJob) throws Exception {
        sysJob.setInvokeTarget(INVOKETARGET);
        sysJob.setUpdateTime(DateUtils.getNowDate());
        /**验证传入的数据*/
        if (StringUtils.isBlank(sysJob.getJobName())//任务名称
                || StringUtils.isBlank(sysJob.getEquipmentName())//点位名称
                || sysJob.getJobId() == null//点位ID
                || sysJob.getTreeids().length == 0//点位id
                || StringUtils.isBlank(sysJob.getCronExpression())//cron表达式
                || StringUtils.isBlank(sysJob.getStatus())//禁用状态
        ) {
            throw new ServiceException("参数传入不完整！");
        }
        int isUpdateJob = sysJobMapper.updateJob(sysJob);
        updateSchedulerJob(sysJob, sysJob.getJobGroup());

        if (isUpdateJob == 0) {
            throw new ServiceException("修改定时同步时出错！");
        }
        if (sysJob.getTreeids().length == 0) {
            throw new ServiceException("未选择设备！");
        }
        /**删除已有设备*/
        athenaBesTimeTaskSyncsbMapper.deleteBySyncIdBoolean(sysJob.getJobId().toString());
        /**插入同步设备子表*/
        insertTaskSyncSb(sysJob, sysJob.getJobId().intValue());

        return AjaxResult.success("添加成功");
    }

    /**
     * 批量删除定时同步任务
     *
     * @param ids 需要删除的定时同步任务主键集合
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteTaskSyncByIds(Long[] ids) throws SchedulerException {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("传入的参数不完整。");
        }
        for (Long jobId : ids) {
            SysJob job = sysJobMapper.selectJobById(jobId);
            Integer isdeltask = deleteJob(job);
            /**删除失败抛出异常回滚*/
            if (isdeltask == 0) {
                throw new ServiceException("删除定时同步任务时失败！");
            }
        }
        /**删除关联信息*/
        for (int i = 0; i < ids.length; i++) {
            Boolean isdelsb = athenaBesTimeTaskSyncsbMapper.deleteBySyncIdBoolean(ids[i] + "");
            if (!isdelsb) {
                throw new ServiceException("删除关联信息时失败！");
            }
        }
        return AjaxResult.success("删除成功!");
    }

    /**
     * 查询定时同步任务
     *
     * @param id id
     * @return {@code SysJob }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @Override
    public SysJob selectTaskSyncById(Long id) {
        if (id==null){
            return null;
        }
        SysJob sysJob = sysJobMapper.selectJobById(id);
        List<AthenaBesTimeTaskSyncSb> syncSb = athenaBesTimeTaskSyncsbMapper.selectNodeBySyncId(id.toString());
        List<String> treeids = new ArrayList<>();
        List<String> name = new ArrayList<>();
        syncSb.forEach(item -> {
            treeids.add(item.getPointId());
            name.add(item.getPointName());
        });
        sysJob.setTreeids(treeids.toArray(new String[]{}));
        sysJob.setEquipmentName(name.toString().replace('[',' ').replace(']',' ').trim());
        return sysJob;
    }

    /**
     * 查询设备树结构
     *
     * @param deviceTree 设备树
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/06
     **/
    @Override
    public AjaxResult getDeviceTree(DeviceTree deviceTree) {

        List<DeviceTree> deviceTreeList = new ArrayList<>();
        Map<String, DeviceTree> stringdeviceTreeMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);
        stringdeviceTreeMap.forEach((key, value) -> {
            //定时巡检 目前只巡检DDC和照明
            if (value.getDeviceType() == DeviceTreeConstants.BES_DDCNODE || value.getDeviceType() == DeviceTreeConstants.BES_ILLUMINE) {
                value.setEnergyNode(Boolean.TRUE);
            }
            if (value.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_AO
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_AI
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_DO
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_DI
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_UI
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_UX) {
                Point point = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) value.getDeviceTreeId());
                value.setControllerId(point.getControllerId());
                value.setEngineerUnit(point.getEngineerUnit());
            } else {
                value.setEnergyNode(Boolean.FALSE);
            }

            if (value.getSysName() == null || "".equals(value.getSysName())) {
                //如果点位信息没有则加上显示名称
                if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AI) {
                    value.setSysName("AI节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AO) {
                    value.setSysName("AO节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DI) {
                    value.setSysName("DI节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DO) {
                    value.setSysName("DO节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UI) {
                    value.setSysName("UI节点");
                    value.setEnergyNode(Boolean.FALSE);
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UX) {
                    value.setSysName("UX节点");
                    value.setEnergyNode(Boolean.FALSE);
                }
            }
            deviceTreeList.add(value);
        });

        if (deviceTreeList.size() > 0) {

            deviceTreeList.sort((o1, o2) -> String.valueOf(o1.getDeviceTreeId()).compareTo(String.valueOf(o2.getDeviceTreeId())));

            return AjaxResult.success("获取成功!", deviceTreeList);
        }
        return AjaxResult.error("获取失败!");
    }

    /**
     * 获取同步任务点位
     *
     * @param id 定时任务Id
     * @return {@code List<AthenaBesTimeTaskSyncSb> }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @Override
    public List<AthenaBesTimeTaskSyncSb> querySbList(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return athenaBesTimeTaskSyncsbMapper.selectAllBySyncId(id);
    }

    /**
     * 获取所有选中的节点
     *
     * @param id id
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @Override
    public AjaxResult getCheckNodes(String id) {
        if (StringUtils.isBlank(id)) {
            AjaxResult.error("传入的参数未空");
        }
        return AjaxResult.success(athenaBesTimeTaskSyncsbMapper.selectNodeIdBySyncId(id));
    }

    /**
     * 改变状态
     *
     * @param job 定时任务
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @Override
    public AjaxResult changeStatus(SysJob job) throws SchedulerException {
        int rows = 0;
        String status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            rows = resumeJob(job);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            rows = pauseJob(job);
        }
        if (rows == 0) {
            throw new ServiceException("变更任务状态失败");
        }
        return AjaxResult.success("变更任务状态成功！");
    }


    /**
     * 立即执行一次
     *
     * @param job 定时任务
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/03
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult runJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        SysJob properties = sysJobMapper.selectJobById(job.getJobId());
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
        scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
        return AjaxResult.success("执行成功");
    }

    /**
     * 删除定时任务
     *
     * @param job 定时任务
     * @return int
     * @Author qindehua
     * @Date 2023/02/03
     **/
    public int deleteJob(SysJob job) throws SchedulerException {

        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        int rows = sysJobMapper.deleteJobById(jobId);
        if (rows > 0) {
            scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 插入同步设备子表…
     *
     * @param sysJob 定时任务
     * @param taskId 任务id
     * @Author qindehua
     * @Date 2023/02/03
     **/
    private void insertTaskSyncSb(SysJob sysJob, Integer taskId) throws Exception {
        for (int i = 0; i < sysJob.getTreeids().length; i++) {
            DeviceTree contree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, sysJob.getTreeids()[i]);
            if (contree != null) {
                AthenaBesTimeTaskSyncSb timeTaskSyncSb = new AthenaBesTimeTaskSyncSb();
                timeTaskSyncSb.setSyncId(taskId + "");
                timeTaskSyncSb.setPointId(contree.getDeviceTreeId() + "");
                timeTaskSyncSb.setPointName(contree.getSysName());
                timeTaskSyncSb.setPointType(contree.getDeviceType() + "");
                timeTaskSyncSb.setPointAllName(contree.getRedisSysName() + "(" + contree.getSysName() + ")");
                DeviceTree fathertree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) contree.getDeviceTreeFatherId());
                timeTaskSyncSb.setPointPsysName(fathertree.getSysName());
                int isInsertTaskSb = athenaBesTimeTaskSyncsbMapper.insertAthenaBesTimeTaskSyncSb(timeTaskSyncSb);
                if (isInsertTaskSb == 0) {
                    throw new Exception();
                }
            }
        }
    }


    /**
     * 更新定时任务
     *
     * @param job      定时任务
     * @param jobGroup 定时任务组
     * @Author qindehua
     * @Date 2023/02/03
     **/
    public void updateSchedulerJob(SysJob job, String jobGroup) throws SchedulerException, TaskException {
        Long jobId = job.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
    }


    /**
     * 暂停
     *
     * @param job 定时任务
     * @return int
     * @Author qindehua
     * @Date 2023/02/03
     **/
    public int pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = sysJobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 恢复
     *
     * @param job 定时任务
     * @return int
     * @Author qindehua
     * @Date 2023/02/03
     **/
    public int resumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = sysJobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }
}
