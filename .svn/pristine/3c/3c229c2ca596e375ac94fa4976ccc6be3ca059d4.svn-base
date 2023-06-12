package com.zc.efounder.JEnterprise.service.safetyWarning.impl;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.ApplicationContextProvider;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmHistoricalData;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmRealtimeData;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmWorkOrder;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmRealtimeDataMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmWorkOrderMapper;
import com.zc.efounder.JEnterprise.service.safetyWarning.IAlarmWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * 告警工单Service业务层处理
 *
 * @author ruoyi
 * @date 2023-03-06
 */
@Service
public class AlarmWorkOrderServiceImpl implements IAlarmWorkOrderService {
    @Resource
    private AlarmWorkOrderMapper alarmWorkOrderMapper;

    //实时报警service
    private AlarmRealtimeDataServiceImpl alarmRealtimeDataServiceImpl = ApplicationContextProvider.getBean(AlarmRealtimeDataServiceImpl.class);

    private AlarmRealtimeDataMapper alarmRealtimeDataMapper = ApplicationContextProvider.getBean(AlarmRealtimeDataMapper.class);

    /**
     * 查询告警工单
     *
     * @param id 告警工单主键
     * @return 告警工单
     */
    @Override
    public AlarmWorkOrder selectAlarmWorkOrderById(Long id) {
        return alarmWorkOrderMapper.selectAlarmWorkOrderById(id);
    }

    /**
     * 查询告警工单列表
     *
     * @param alarmWorkOrder 告警工单
     * @return 告警工单
     */
    @Override
    public List<AlarmWorkOrder> selectAlarmWorkOrderList(AlarmWorkOrder alarmWorkOrder) {
        List<AlarmWorkOrder> returnList = new ArrayList<>();

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)) {
            SysUser currentUser = loginUser.getUser();
            alarmWorkOrder.setUserId(String.valueOf(currentUser.getUserId()));
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin()) {
                //用户查询自己的信息
                alarmWorkOrder.setUserId(String.valueOf(currentUser.getUserId()));
                //有策略ID
                return alarmWorkOrderMapper.selectAlarmWorkOrderList(alarmWorkOrder);
            } else {
                //超级管理员查询全部的信息
                return alarmWorkOrderMapper.selectAlarmWorkOrderList(alarmWorkOrder);
            }
        }

        return returnList;
    }

    /**
     * 新增告警工单
     *
     * @param alarmWorkOrder 告警工单
     * @return 结果
     */
    @Override
    public int insertAlarmWorkOrder(AlarmWorkOrder alarmWorkOrder) {
        alarmWorkOrder.setCreateTime(DateUtils.getNowDate());
        return alarmWorkOrderMapper.insertAlarmWorkOrder(alarmWorkOrder);
    }


    /**
     * @description:处理告警工单
     * @author: sunshangeng
     * @date: 2023/3/10 11:32
     * @param: [alarmWorkOrder]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateAlarmWorkOrder(AlarmWorkOrder alarmWorkOrder) {

        AlarmWorkOrder oldWorkOrder = alarmWorkOrderMapper.selectAlarmWorkOrderById(alarmWorkOrder.getId());
        if (oldWorkOrder == null) {
            return AjaxResult.error("传入的数据有误！");
        }
        oldWorkOrder.setRemark(alarmWorkOrder.getRemark());
        if (StringUtils.isBlank(oldWorkOrder.getRemark())) {
            return AjaxResult.error("未添加备注信息！");
        }

        /**获取当前登录的用户信息 比对处理*/
        LoginUser loginUser = SecurityUtils.getLoginUser();

        boolean isupdate = false;
        for (String userid : alarmWorkOrder.getUserId().split(",")) {
            if (userid.equals(loginUser.getUserId().toString())) {
                isupdate = true;
            }
        }
        if (!isupdate) {
            return AjaxResult.error("您无权处理当前告警工单！");
        }

        oldWorkOrder.setStatus("1");
        oldWorkOrder.setUpdateTime(DateUtils.getNowDate());
        oldWorkOrder.setUpdateName(loginUser.getUsername());
        oldWorkOrder.setUpdateCode(loginUser.getUserId() + "");

        //实时告警处理逻辑
        //参数 1获取实时数据对应的ID用于删除实时数据 2添加的历史数据信息（azwz planVal promptMsg alarmTacticsId alarmTypeId equipmentId）
        AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData(oldWorkOrder.getAzwz(), oldWorkOrder.getPlanVal(), oldWorkOrder.getAlarmTacticsId(),
                oldWorkOrder.getPromptMsg(), oldWorkOrder.getAlarmTypeId(), oldWorkOrder.getEquipmentId());
        List<AlarmRealtimeData> alarmRealtimeDatas = alarmRealtimeDataMapper.selectAlarmRealtimeDataStateList(alarmRealtimeData);
        if (alarmRealtimeDatas != null && alarmRealtimeDatas.size() > 0) {
            alarmRealtimeDataServiceImpl.updateAlarmRealtimeData(alarmRealtimeDatas, true);
        }

        Boolean updateOrder = alarmWorkOrderMapper.updateAlarmWorkOrder(oldWorkOrder);
        if (!updateOrder) {
            return AjaxResult.error("处理工单未成功！");
        }
        return AjaxResult.success("处理成功！");
    }

    /**
     * 批量删除告警工单
     *
     * @param ids 需要删除的告警工单主键
     * @return 结果
     */
    @Override
    public int deleteAlarmWorkOrderByIds(Long[] ids) {
        return alarmWorkOrderMapper.deleteAlarmWorkOrderByIds(ids);
    }

    /**
     * 删除告警工单信息
     *
     * @param id 告警工单主键
     * @return 结果
     */
    @Override
    public int deleteAlarmWorkOrderById(Long id) {
        return alarmWorkOrderMapper.deleteAlarmWorkOrderById(id);
    }
}
