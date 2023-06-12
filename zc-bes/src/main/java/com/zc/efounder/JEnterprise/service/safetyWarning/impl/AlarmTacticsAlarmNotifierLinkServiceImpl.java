package com.zc.efounder.JEnterprise.service.safetyWarning.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotifier;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmWorkOrder;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmWorkOrderMapper;
import com.zc.efounder.JEnterprise.service.safetyWarning.AlarmTacticsAlarmNotifierLinkService;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsAlarmNotifierLinkMapper;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTacticsAlarmNotifierLink;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 告警策略告警接收组关联Service业务层处理
 *
 * @author sunshangeng
 * @date 2022-09-20
 */
@Service
public class AlarmTacticsAlarmNotifierLinkServiceImpl implements AlarmTacticsAlarmNotifierLinkService {
    @Resource
    private AlarmTacticsAlarmNotifierLinkMapper alarmTacticsAlarmNotifierLinkMapper;

    @Resource
    private AlarmWorkOrderMapper alarmWorkOrderMapper;

    /**
     * 新增告警策略告警接收组关联
     *
     * @param alarmTacticsAlarmNotifierLink 告警策略告警接收组关联
     * @return 结果
     */
    @Override
    public AjaxResult insertAlarmTacticsAlarmNotifierLink(AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink) {
        //添加告警策略
        int i = alarmTacticsAlarmNotifierLinkMapper.insertAlarmTacticsAlarmNotifierLink(alarmTacticsAlarmNotifierLink);

        if(i != 1){
            return AjaxResult.error("添加失败");
        }
        //修改告警工单
        //查询已选择的策略组
        List<AlarmTacticsAlarmNotifierLink> checkList = alarmTacticsAlarmNotifierLinkMapper.selectCheckList(alarmTacticsAlarmNotifierLink);
        AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder();

        String userId = "";
        for (AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLinkFor : checkList) {
            //根据告警接受组取出userId
            AlarmNotifier alarmNotifier = alarmTacticsAlarmNotifierLinkMapper.selectUserIdByAlarmNotifierId(alarmTacticsAlarmNotifierLinkFor.getAlarmNotifierId());
            if ("".equals(userId)) {
                userId = alarmNotifier.getUserId();
            } else {
                userId = userId + "," + alarmNotifier.getUserId();
            }
        }
        //查重
        String[] userIdsArr = userId.split(",");
        Set<String> set = new HashSet<String>(Arrays.asList(userIdsArr));
        if (!set.contains("1")) {
            set.add("1");
        }
        String userIdStr = "";
        for (String string : set) {
            if ("".equals(userIdStr)) {
                userIdStr = string;
            } else {
                userIdStr = userIdStr + "," + string;
            }
        }
        alarmWorkOrder.setUserId(userIdStr);
        alarmWorkOrder.setAlarmTacticsId(alarmTacticsAlarmNotifierLink.getAlarmTacticsId());
        boolean b = alarmWorkOrderMapper.updateWorkOrderUserIdByAlarmTacticsId(alarmWorkOrder);

        if(!b){
            return AjaxResult.error("添加失败");
        }

        return AjaxResult.success("添加成功");

    }


    /**
     * 批量删除告警策略告警接收组关联
     *
     * @param alarmTacticsAlarmNotifierLink 需要删除的告警策略告警接收组关联主键
     * @return 结果
     */
    @Override
    public int deleteAlarmTacticsAlarmNotifierLinkByIds(AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLink) {
        //修改告警工单
        //查询除此id关联的告警接受组
        List<AlarmTacticsAlarmNotifierLink> otherList = alarmTacticsAlarmNotifierLinkMapper.selectOtherList(alarmTacticsAlarmNotifierLink);
        AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder();
        if (otherList == null || otherList.size() == 0) {
            //告警工单userId只剩下管理员
            alarmWorkOrder.setUserId("1");
            alarmWorkOrder.setAlarmTacticsId(alarmTacticsAlarmNotifierLink.getAlarmTacticsId());
            alarmWorkOrderMapper.updateWorkOrderUserIdByAlarmTacticsId(alarmWorkOrder);
        } else {
            String userId = "";
            for (AlarmTacticsAlarmNotifierLink alarmTacticsAlarmNotifierLinkFor : otherList) {
                //根据告警接受组取出userId
                AlarmNotifier alarmNotifier = alarmTacticsAlarmNotifierLinkMapper.selectUserIdByAlarmNotifierId(alarmTacticsAlarmNotifierLinkFor.getAlarmNotifierId());
                if ("".equals(userId)) {
                    userId = alarmNotifier.getUserId();
                } else {
                    userId = userId + "," + alarmNotifier.getUserId();
                }
            }
            //查重
            String[] userIdsArr = userId.split(",");
            Set<String> set = new HashSet<String>(Arrays.asList(userIdsArr));
            if (!set.contains("1")) {
                set.add("1");
            }
            String userIdStr = "";
            for (String string : set) {
                if ("".equals(userIdStr)) {
                    userIdStr = string;
                } else {
                    userIdStr = userIdStr + "," + string;
                }
            }
            alarmWorkOrder.setUserId(userIdStr);
            alarmWorkOrder.setAlarmTacticsId(alarmTacticsAlarmNotifierLink.getAlarmTacticsId());
            alarmWorkOrderMapper.updateWorkOrderUserIdByAlarmTacticsId(alarmWorkOrder);
        }

        //删除告警组
        return alarmTacticsAlarmNotifierLinkMapper.deleteAlarmTacticsAlarmNotifierLinkById(alarmTacticsAlarmNotifierLink.getId());
    }

    /**
     * 删除告警策略告警接收组关联信息
     *
     * @param id 告警策略告警接收组关联主键
     * @return 结果
     */
    @Override
    public int deleteAlarmTacticsAlarmNotifierLinkById(Long id) {
        return alarmTacticsAlarmNotifierLinkMapper.deleteAlarmTacticsAlarmNotifierLinkById(id);
    }


}
