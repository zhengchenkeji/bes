package com.zc.quartz.service.impl;

import com.ruoyi.common.constant.ScheduleConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.job.TaskException;
import com.ruoyi.common.utils.StringUtils;

import com.ruoyi.quartz.domain.SysJob;
import com.ruoyi.quartz.mapper.SysJobMapper;
import com.ruoyi.quartz.service.ISysJobService;
import com.ruoyi.quartz.util.ScheduleUtils;
import com.zc.common.constant.JobGroupConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.ProductFunction;
import com.zc.efounder.JEnterprise.domain.deviceSynchronization.AthenaBesTimeTaskSyncSb;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.mapper.deviceSynchronization.AthenaBesTimeTaskSyncSbMapper;
import com.zc.quartz.domain.AthenaBesTimeTaskSync;
import com.zc.quartz.mapper.AthenaBesTimeTaskSyncMapper;
import com.zc.quartz.service.AthenaBesTimeTaskSyncService;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


/**
 * 定时同步任务Service业务层处理
 *
 * @author ruoyi
 * @date 2022-11-01
 */
@Service
public class AthenaBesTimeTaskSyncServiceImpl implements AthenaBesTimeTaskSyncService {
    @Resource
    private AthenaBesTimeTaskSyncMapper athenaBesTimeTaskSyncMapper;

    @Resource
    private AthenaBesTimeTaskSyncSbMapper athenaBesTimeTaskSyncsbMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    ISysJobService sysJobService;

    @Resource
    SysJobMapper SysJobMapper;

    @Resource
    private DeviceTreeCache deviceTreeCache;

    @Autowired
    private Scheduler scheduler;


    /**
     * 查询定时同步任务
     * sunshangeng
     *
     * @param id 定时同步任务主键
     * @return 定时同步任务
     */
    @Override
    public SysJob selectAthenaBesTimeTaskSyncById(Long id) {
        SysJob sysJob = SysJobMapper.selectJobById(id);
        sysJob.setTreeids(athenaBesTimeTaskSyncsbMapper.selectNodeIdBySyncId(id.toString()).toArray(new String[]{}));
        return sysJob;
    }

    /**
     * 查询定时同步任务列表
     * sunshangeng
     *
     * @param SysJob 定时同步任务
     * @return 定时同步任务
     */
    @Override
    public List<SysJob> selectAthenaBesTimeTaskSyncList(SysJob SysJob) {

        /**设置查询组名*/
//        SysJob.setJobGroup("SYNCHRONIZEDEVICETASKS");
        return SysJobMapper.selectJobList(SysJob);
    }


    /**
     * 新增定时同步任务
     * sunshangeng
     *
     * @param SysJob 定时同步任务
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult insertAthenaBesTimeTaskSync(SysJob SysJob) throws Exception {

        /**验证传入的数据*/
        if (StringUtils.isBlank(SysJob.getJobName())//任务名称
                || StringUtils.isBlank(SysJob.getEquipmentName())//点位名称
                || SysJob.getTreeids().length == 0//点位id
                || StringUtils.isBlank(SysJob.getCronExpression())//cron表达式
                || StringUtils.isBlank(SysJob.getStatus())//禁用状态
                || StringUtils.isBlank(SysJob.getInvokeTarget())//调用目标
        ) {
            throw new ServiceException("参数传入不完整！");
        }
        Boolean taskId = SysJobMapper.insertJob(SysJob);

        if (!taskId) {
            throw new ServiceException("保存定时同步设备时出错！");
        }
//        String taskId = athenaBesTimeTaskSyncMapper.selectSyncMaxId();
//        athenaBesTimeTaskSync.setId(taskId);
        /**插入同步设备子表*/
        insertTaskSyncSb(SysJob, SysJob.getJobId().intValue());
//        /**绑定定时任务*/
//        Integer sysJobId = sysJobService.saveJobByDeviceTask(JSONObject.parseObject(JSONObject.toJSONString(athenaBesTimeTaskSync)));
//        athenaBesTimeTaskSync.setJobId(sysJobId+"");

//        int isupdate = athenaBesTimeTaskSyncMapper.updateAthenaBesTimeTaskSync(athenaBesTimeTaskSync);
//        if(isupdate==0){
//            throw  new ServiceException("绑定系统定时任务时异常！");
//        }
//        SysJob job=new SysJob();
//
//        job.setJobId(Long.parseLong(athenaBesTimeTaskSync.getId()));
//
//        job.setJobGroup(athenaBesTimeTaskSync.getJobGroup());
//        job.setJobName(athenaBesTimeTaskSync.getJobName());
//        job.setConcurrent(athenaBesTimeTaskSync.getConcurrent());
//        job.setInvokeTarget(athenaBesTimeTaskSync.getInvokeTarget());
//        job.setMisfirePolicy(athenaBesTimeTaskSync.getMisfirePolicy());

        /**创建定时任务*/
        ScheduleUtils.createScheduleJob(scheduler, SysJob);


        return AjaxResult.success("添加成功");
    }

    /**
     * 插入设备子表 sunshangeng
     */
    private void insertTaskSyncSb(SysJob SysJob, Integer taskId) throws Exception {
        for (int i = 0; i < SysJob.getTreeids().length; i++) {
            /**根据告警组处理数据*/
            String treeid = SysJob.getTreeids()[i];
            AthenaBesTimeTaskSyncSb TimeTaskSyncSb = new AthenaBesTimeTaskSyncSb();

            if (SysJob.getJobGroup().equals(JobGroupConstants.ENERGY_JOB)) {
                /**处理第三方设备树的*/
                /**设备ID*/
                String deviceId=treeid.substring(0,treeid.indexOf("_"));
                /**功能id*/
                String functionId=treeid.substring(treeid.indexOf("_")+1,treeid.length());
                ProductFunction productFunction=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function,functionId);
                Equipment equipment=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,deviceId);
                /**判断获取到的功能ID和设备ID是否为空*/
                if(productFunction!=null&&equipment!=null){
                    TimeTaskSyncSb.setSyncId(taskId + "");
                    TimeTaskSyncSb.setPointId(treeid + "");
                    TimeTaskSyncSb.setPointName(productFunction.getName());
                    TimeTaskSyncSb.setPointType(functionId);
                    TimeTaskSyncSb.setPointAllName(equipment.getName()+ "(" + productFunction.getName() + ")");
                    TimeTaskSyncSb.setPointPsysName(equipment.getName());
                    int isInsertTaskSb = athenaBesTimeTaskSyncsbMapper.insertAthenaBesTimeTaskSyncSb(TimeTaskSyncSb);
                    if (isInsertTaskSb == 0) {
                        throw new Exception();
                    }
                }
            } else {
                DeviceTree contree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, treeid);
                if (contree != null) {
                    TimeTaskSyncSb.setSyncId(taskId + "");
                    TimeTaskSyncSb.setPointId(contree.getDeviceTreeId() + "");
                    TimeTaskSyncSb.setPointName(contree.getSysName());
                    TimeTaskSyncSb.setPointType(contree.getDeviceType() + "");
                    TimeTaskSyncSb.setPointAllName(contree.getRedisSysName() + "(" + contree.getSysName() + ")");
                    DeviceTree fathertree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) contree.getDeviceTreeFatherId());
                    TimeTaskSyncSb.setPointPsysName(fathertree.getSysName());
                    int isInsertTaskSb = athenaBesTimeTaskSyncsbMapper.insertAthenaBesTimeTaskSyncSb(TimeTaskSyncSb);
                    if (isInsertTaskSb == 0) {
                        throw new Exception();
                    }
                }
            }


        }
    }

    /**
     * 修改定时同步任务
     * sunshangeng
     *
     * @param SysJob 定时同步任务
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult updateAthenaBesTimeTaskSync(SysJob SysJob) throws Exception {
        int isUpdateJob = SysJobMapper.updateJob(SysJob);
        updateSchedulerJob(SysJob, SysJob.getJobGroup());

        if (isUpdateJob == 0) {
            throw new ServiceException("修改定时同步时出错！");
        }
        if (SysJob.getTreeids().length == 0) {
            throw new ServiceException("未选择设备！");
        }
        /**删除已有点位*/
        athenaBesTimeTaskSyncsbMapper.deleteBySyncIdBoolean(SysJob.getJobId().toString());
        /**插入同步设备子表*/
        insertTaskSyncSb(SysJob, SysJob.getJobId().intValue());

        return AjaxResult.success("添加成功");

    }

    /**
     * 批量删除定时同步任务
     * sunshangeng
     *
     * @param ids 需要删除的定时同步任务主键
     * @return 结果
     */
    @Override
    @Transactional
    public AjaxResult deleteAthenaBesTimeTaskSyncByIds(Long[] ids) throws SchedulerException {
        if (ids == null
                || ids.length == 0) {
            return AjaxResult.error("传入的参数不完整。");
        }
        for (Long jobId : ids) {
            SysJob job = SysJobMapper.selectJobById(jobId);
            Integer isdeltask = deleteJob(job);
            /**删除失败抛出异常回滚*/
            if (isdeltask == 0) {
                throw new ServiceException("删除定时同步任务时失败！");
            }
        }


        /**删除关联信息*/
        for (int i = 0; i < ids.length; i++) {
            Boolean isdelsb = athenaBesTimeTaskSyncsbMapper.deleteBySyncIdBoolean(ids[i] + "");
//            if (!isdelsb) {
//                throw new ServiceException("删除关联信息时失败！");
//            }
        }
        return AjaxResult.success("删除成功!");
    }
//    /**
//     * 删除定时同步任务信息
//     *
//     * @param id 定时同步任务主键
//     * @return 结果
//     */
//    @Override
//    public int deleteAthenaBesTimeTaskSyncById(String id)
//    {
//        return athenaBesTimeTaskSyncMapper.deleteAthenaBesTimeTaskSyncById(id);
//    }

    /**
     * @description:从设备树获取所有控制器
     * @author: sunshangeng
     * @date: 2022/10/31 10:03
     * @param: [deviceTree]
     * @return: java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.deviceTree>
     **/
    @Override
    public List<Map<String, Object>> getDeviceTree(DeviceTree deviceTree) {

        List<Map<String, Object>> list = new ArrayList<>();
        //遍历缓存取list 根节点
        Map<String, DeviceTree> deviceTreeCacheMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_DeviceTree);
        Collection<DeviceTree> deviceTreeValues = deviceTreeCacheMap.values();
        for (DeviceTree tree : deviceTreeValues) {
            if (tree.getDeviceTreeFatherId() == -1) {
                //判断节点是否存在子节点
                List<DeviceTree> childTreeList = deviceTreeCache.getSubordinate(tree.getDeviceTreeId());
                if (childTreeList.size() == 0) {
                    continue;
                } else {
                    Map<String, Object> treemap = new HashMap<>();
                    treemap.put("id", tree.getDeviceTreeId());
                    treemap.put("label", tree.getSysName());
                    List<Map<String, Object>> childrenlist = new ArrayList<>();
                    /*遍历类型节点*/
                    for (DeviceTree typetree : childTreeList) {
                        Map<String, Object> typetreemap = new HashMap<>();
                        typetreemap.put("id", typetree.getDeviceTreeId());
                        typetreemap.put("label", typetree.getSysName());
                        List<Map<String, Object>> controllerList = new ArrayList<>();
                        /*遍历控制器节点*/
                        List<DeviceTree> conchildTreeList = deviceTreeCache.getSubordinate(typetree.getDeviceTreeId());
                        for (DeviceTree contree : conchildTreeList) {
                            Map<String, Object> contreemap = new HashMap<>();
                            contreemap.put("id", contree.getDeviceTreeId());
                            contreemap.put("label", contree.getSysName());
                            controllerList.add(contreemap);
                        }
                        if (controllerList.size() == 0) {
                            /**说明当前节点控制器不存在*/
                            continue;
                        }
                        typetreemap.put("children", controllerList);
                        childrenlist.add(typetreemap);
                    }
                    if (childrenlist.size() == 0) {
                        /**说明当前节点无可用子节点*/
                        continue;
                    }
                    treemap.put("children", childrenlist);
                    list.add(treemap);
                }
            }
        }
        return list;
    }

    /**
     * @description:根据同步任务获取当前点位
     * @author: sunshangeng
     * @date: 2022/11/2 17:29
     * @param: [athenaBesTimeTaskSync]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    @Override
    public List<AthenaBesTimeTaskSyncSb> querySbList(AthenaBesTimeTaskSync athenaBesTimeTaskSync) {

        if (athenaBesTimeTaskSync == null || StringUtils.isBlank(athenaBesTimeTaskSync.getId())) {
            return null;
        }
        return athenaBesTimeTaskSyncsbMapper.selectAllBySyncId(athenaBesTimeTaskSync.getId());
    }

    /**
     * @description:获取所有选中的节点
     * @author: sunshangeng
     **/
    @Override
    public AjaxResult getCheckNodes(String id) {
        if (StringUtils.isBlank(id)) {
            AjaxResult.error("传入的参数未空");
        }
        return AjaxResult.success(athenaBesTimeTaskSyncsbMapper.selectNodeIdBySyncId(id));
    }


    /**
     * @description:变更选择状态
     * @author: sunshangeng
     * @date: 2022/11/7 14:06
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
     * @description:执行一次
     * @author: sunshangeng
     * @date: 2022/11/7 14:06
     **/
    @Override
    @Transactional
    public AjaxResult runJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        SysJob properties = SysJobMapper.selectJobById(job.getJobId());
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
        scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);


        return AjaxResult.success("执行成功");
    }

    /*删除定时任务 sunshangeng*/
    @Override
    public int deleteJob(SysJob job) throws SchedulerException {

        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        int rows = SysJobMapper.deleteJobById(jobId);
        if (rows > 0) {
            scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 更新任务
     * sunshangeng
     *
     * @param job      任务对象
     * @param jobGroup 任务组名
     */
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
     * @description:恢复开始
     * @author: sunshangeng
     **/
    public int resumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = SysJobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * @description:停止
     * @author: sunshangeng
     **/
    public int pauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = SysJobMapper.updateJob(job);
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }
}
