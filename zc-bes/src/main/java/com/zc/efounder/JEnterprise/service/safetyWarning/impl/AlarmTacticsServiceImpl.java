package com.zc.efounder.JEnterprise.service.safetyWarning.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.AthenaBesHouseholdConfig;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.domain.entity.SubitemConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNoticeLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBesHouseholdConfigMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.SubitemConfigMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmNoticeLinkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsAlarmNotifierLinkMapper;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.service.safetyWarning.AlarmTacticsService;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsMapper;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTactics;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 告警策略Service业务层处理
 *
 * @author sunshangeng
 * @date 2022-09-16
 */
@Service
public class AlarmTacticsServiceImpl implements AlarmTacticsService {
    @Resource
    private AlarmTacticsMapper alarmTacticsMapper;


    @Resource
    private RedisCache redisCache;


    @Resource
    private DeviceTreeCache deviceTreeCache;

    @Resource
    private AthenaBesHouseholdConfigMapper householdConfigMapper;

    @Resource
    private SubitemConfigMapper subitemConfigMapper;


    @Resource
    private AlarmTacticsAlarmNotifierLinkMapper tacticsAlarmNotifierLinkMapper;


    @Resource
    private AlarmNoticeLinkMapper noticeLinkMapper;

    /**
     * sunshangeng 初始化缓存
     */
    @PostConstruct
    public void init() {

        List<AlarmTactics> alarmTactics = alarmTacticsMapper.selectAlarmTacticsList(new AlarmTactics());
//        List<DicDataEntity> dicDataEntities = new ArrayList<DicDataEntity>();
//        dicDataEntities = alarmTacticsMapper.selectAlarmTacticsList(new AlarmTactics());
        /**初始化时先清空*/
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics);
        if (alarmTactics != null && alarmTactics.size() > 0) {
            alarmTactics.forEach(item -> {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, item.getId(), item);
            });
        }

    }

    /**
     * @description:打开报警策略详情
     * @author: sunshangeng
     * @date: 2022/11/24 15:29
     * @param: [id]
     * @return: com.ruoyi.safetyWarning.alarmTactics.domain.AlarmTactics
     **/
    @Override
    public AlarmTactics selectAlarmTacticsById(Long id) {


        /**测试处理定时任务*/


        AlarmTactics alarmTactics = alarmTacticsMapper.selectAlarmTacticsById(id);
        /**根据当前的id获取父id*/
        if (alarmTactics.getDeviceType() == 1) {//电表
            DeviceTree metertree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, alarmTactics.getDeviceId());
            alarmTactics.setFatherId(metertree.getDeviceTreeFatherId() + "");

        } else if (alarmTactics.getDeviceType() == 2) {//支路
            AthenaBranchConfig branch = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, alarmTactics.getDeviceId());
            alarmTactics.setFatherId(branch.getParentId() + "");
            alarmTactics.setParkcode(branch.getParkCode());
            alarmTactics.setEnergycode(branch.getEnergyCode());
        } else if (alarmTactics.getDeviceType() == 3) {//分户
            AthenaBesHouseholdConfig householdConfig = householdConfigMapper.selectAthenaBesHouseholdConfigById(Long.parseLong(alarmTactics.getDeviceId()));
            alarmTactics.setFatherId(householdConfig.getParentId() + "");
            alarmTactics.setParkcode(householdConfig.getParkCode());
            alarmTactics.setEnergycode(householdConfig.getEnergyCode());
        } else if (alarmTactics.getDeviceType() == 4) {//分项
            SubitemConfig subitemConfig = subitemConfigMapper.selectSubitemConfigBySubitemId(alarmTactics.getDeviceId());
            alarmTactics.setFatherId(subitemConfig.getParentId() + "");
            alarmTactics.setParkcode(subitemConfig.getParkCode());
            alarmTactics.setEnergycode(subitemConfig.getEnergyCode());
            alarmTactics.setBuildingid(subitemConfig.getBuildingId() + "");
        }   else if (alarmTactics.getDeviceType() == 5) {//第三方设备
            Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, alarmTactics.getDeviceId());
            if(equipment.getpId() == null){
                alarmTactics.setFatherId("0");
            }else{
                alarmTactics.setFatherId(equipment.getpId() + "");
            }

        }
        return alarmTactics;
    }

    /**
     * 查询告警策略列表
     * sunshangeng
     *
     * @param alarmTactics 告警策略
     * @return 告警策略
     */
    @Override
    public List<AlarmTactics> selectAlarmTacticsList(AlarmTactics alarmTactics) {

        return alarmTacticsMapper.selectAlarmTacticsList(alarmTactics);
    }

    /**
     * @description:新增告警策略
     * @author: sunshangeng
     * @date: 2022/9/22 15:28
     * @param: [alarmTactics]
     * @return: int
     **/
    @Override
    @Transactional
    public AjaxResult insertAlarmTactics(AlarmTactics alarmTactics) {
        Map<String, AlarmTactics> alarmTacticsMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics);

        for (AlarmTactics alarm : alarmTacticsMap.values()) {
            if (alarm.getName().equals(alarmTactics.getName())) {
                return AjaxResult.error("新增失败，告警策略名称重复！");

            }
        }

        /**判断是否开启消息通知*/
        if (alarmTactics.getIsSendInform() == 1) {
            /**已开启*/
            for (AlarmNoticeLink noticeLink : alarmTactics.getNoticeLinkList()) {
                noticeLink.setCreateTime(new Date());

                Boolean savelink = noticeLinkMapper.insertAlarmNoticeLink(noticeLink);
                if (!savelink) {
                    throw new ServiceException("绑定通知未成功！");
                }
            }
        }
        alarmTactics.setCreateTime(DateUtils.getNowDate());
        int i = alarmTacticsMapper.insertAlarmTactics(alarmTactics);
        if (i > 0) {
            redisCache.deleteObject(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics);
            init();
            return AjaxResult.success("新增成功");
        } else {
            return AjaxResult.error("新增失败");
        }
    }

    /**
     * 修改告警策略
     *
     * @description:
     * @author: sunshangeng
     * @date: 2022/9/22 15:28
     * @param: [alarmTactics]
     * @return: int
     **/
    @Override
    @Transactional
    public AjaxResult updateAlarmTactics(AlarmTactics alarmTactics) {
        if (alarmTactics.getId() == null) {
            return AjaxResult.error("告警策略ID不能为空！");
        }
        Map<String, AlarmTactics> alarmTacticsMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics);

        for (AlarmTactics alarm : alarmTacticsMap.values()) {
            if (alarm.getName().equals(alarmTactics.getName()) && alarm.getId() != alarmTactics.getId()) {
                return AjaxResult.error("新增失败，告警策略名称重复！");
            }
        }


        /**判断是否开启消息通知*/
        if (alarmTactics.getIsSendInform() == 1) {
            /**已开启*/
            for (AlarmNoticeLink noticeLink : alarmTactics.getNoticeLinkList()) {
                noticeLinkMapper.delNoticeLinkBytype(alarmTactics.getId(), noticeLink.getNoticeType().intValue());

                noticeLink.setCreateTime(new Date());
                Boolean savelink = noticeLinkMapper.insertAlarmNoticeLink(noticeLink);
                if (!savelink) {
                    throw new ServiceException("绑定通知未成功！");
                }
            }
        }
        alarmTactics.setUpdateTime(DateUtils.getNowDate());
        int i = alarmTacticsMapper.updateAlarmTactics(alarmTactics);
        if (i > 0) {
            /**修改缓存*/
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, alarmTactics.getId(), alarmTactics);
            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.success("修改失败");
        }

    }

    /**
     * @description:批量删除报警策略
     * @author: sunshangeng
     * @date: 2022/9/22 15:28
     * @param: [ids]
     * @return: int
     **/
    @Override
    @Transactional
    public AjaxResult deleteAlarmTacticsByIds(Long[] ids) {

        if (ids == null || ids.length == 0) {
            return AjaxResult.success("删除失败，传入的信息为空！");
        }
        /**先删除关联关系*/
        tacticsAlarmNotifierLinkMapper.deleteByAlarmtacticsIdsBoolean(ids);
        int i = alarmTacticsMapper.deleteAlarmTacticsByIds(ids);

        if (i > 0) {
            for (int j = 0; j < ids.length; j++) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, ids[j]);
            }

            return AjaxResult.success("删除成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.success("删除失败");
        }

    }

    /**
     * @description:删除告警策略信息
     * @author: sunshangeng
     * @date: 2022/9/22 15:26
     * @param: [id]
     * @return: int
     **/
    @Override
    @Transactional
    public int deleteAlarmTacticsById(Long id) {

        Long[] ids = {id};
        /**先删除关联关系*/
        tacticsAlarmNotifierLinkMapper.deleteByAlarmtacticsIdsBoolean(ids);
        int i = alarmTacticsMapper.deleteAlarmTacticsById(id);
        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, id);
        return i;
    }

    /**
     * @description:获取告警策略字典
     * @author: sunshangeng
     * @date: 2022/9/22 15:19
     * @param: []
     * @return: java.util.List<com.ruoyi.common.domain.DicDataEntity>
     **/
    @Override
    public List<DicDataEntity> selectAlarmTacticsDicData() {
        List<DicDataEntity> dicDataEntities = new ArrayList<DicDataEntity>();
        Map<String, AlarmTactics> alarms = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics);
        if (alarms.values().size() == 0) {
            init();
            alarms = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics);
        }
        for (AlarmTactics alarm : alarms.values()) {
            dicDataEntities.add(new DicDataEntity(alarm.getId() + "", alarm.getName()));
        }
        return dicDataEntities;
    }

    /**
     * 查询报警配置根据告警类型
     *
     * @param alarmTypeId 报警类型id
     * @return {@code List<Map<Long, String>> }
     * @Author qindehua
     * @Date 2022/11/22
     **/
    @Override
    public List<Map<Long, String>> selectAlarmTacticsByAlarmTypeId(Long alarmTypeId) {
        return alarmTacticsMapper.selectAlarmTacticsByAlarmTypeId(alarmTypeId);
    }

    /**
     * @description:根据策略id和通知类型查询通知配置
     * @author: sunshangeng
     * @date: 2023/3/8 18:09
     * @param: [alarmTacticsid, noticeType]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult getNoticeLinkBytype(Long alarmTacticsid, Integer noticeType) {
        if (alarmTacticsid==null||
                noticeType==null) {

            return AjaxResult.error("传入的参数不完整");
        }

        AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmTacticsid, noticeType);
        return AjaxResult.success(alarmNoticeLink);
    }

    /**
     * @description:获取电表树结构
     * @author: sunshangeng
     * @date: 2022/11/11 16:23
     * @param: []
     * @return: java.util.List<com.ruoyi.deviceManagement.deviceTree.domain.deviceTree>
     **/
    @Override
    public AjaxResult selectTree(DeviceTree deviceTree) {
        List<DeviceTree> deviceTreeList = new ArrayList<>();
        Map<String, DeviceTree> stringdeviceTreeMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);
        stringdeviceTreeMap.forEach((key, value) -> {

            if (value.getSysName() == null || "".equals(value.getSysName())) {
                //如果点位信息没有则加上显示名称
                if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AI) {
                    value.setSysName("AI节点");

                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AO) {
                    value.setSysName("AO节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DI) {
                    value.setSysName("DI节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DO) {
                    value.setSysName("DO节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UI) {
                    value.setSysName("UI节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UX) {
                    value.setSysName("UX节点");
                }
            }

            deviceTreeList.add(value);
        });

        if (deviceTreeList.size() > 0) {

            deviceTreeList.sort((o1, o2) -> String.valueOf(o1.getDeviceTreeId()).compareTo(String.valueOf(o2.getDeviceTreeId())));

            return AjaxResult.success("获取成功", deviceTreeList);
        }
        return AjaxResult.error("获取失败");
    }
}
