package com.zc.efounder.JEnterprise.service.safetyWarning.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmRealtimeData;
import com.zc.efounder.JEnterprise.mapper.energyInfo.ParkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmRealtimeDataMapper;
import com.zc.efounder.JEnterprise.service.safetyWarning.IAlarmHistoricalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmHistoricalDataMapper;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmHistoricalData;

/**
 * 告警历史数据Service业务层处理
 *
 * @author qindehua
 * @date 2022-11-17
 */
@Service
public class AlarmHistoricalDataServiceImpl implements IAlarmHistoricalDataService {

    //redis
    private RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);

    @Autowired
    private AlarmHistoricalDataMapper alarmHistoricalDataMapper;

    @Autowired
    private AlarmRealtimeDataMapper alarmRealtimeDataMapper;

    //告警策略
    @Autowired
    private ParkMapper parkMapper;

    /**
     * 查询告警历史数据
     *
     * @param id 告警历史数据主键
     * @return 告警历史数据
     */
    @Override
    public AlarmHistoricalData selectAlarmHistoricalDataById(Long id) {
        return alarmHistoricalDataMapper.selectAlarmHistoricalDataById(id);
    }

    /**
     * 查询告警历史数据列表
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 告警历史数据
     */
    @Override
    public List<AlarmHistoricalData> selectAlarmHistoricalDataList(AlarmHistoricalData alarmHistoricalData) {
        List<AlarmHistoricalData> returnList = new ArrayList<>();
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)) {
            SysUser currentUser = loginUser.getUser();
            //不是超级管理员则过滤
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin()) {
                //查询关联的告警策略
                List<Long> idsList = alarmRealtimeDataMapper.selectUserNotifier(String.valueOf(currentUser.getUserId()));

                if (idsList != null && idsList.size() > 0) {
                    alarmHistoricalData.setIds(idsList);
                    //根据告警策略查询对应的实时数据
                    //有策略ID
                    List<AlarmHistoricalData> haveIds = alarmHistoricalDataMapper.selectAlarmHistoricalDataList(alarmHistoricalData);

                    //无策略ID（设备离线报警）
                    //用户ID-园区-设备
                    List<Long> equipmentId = new ArrayList<>();
                    List<Park> parks = parkMapper.selectParkList(null);
                    Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
                    for (Park park : parks) {
                        if (park.getUserName().equals(String.valueOf(currentUser.getUserId()))) {
                            for (Object object : values) {
                                Equipment equipment = (Equipment) object;
                                if (equipment.getParkCode() != null && equipment.getParkCode().equals(park.getCode())) {
                                    equipmentId.add(equipment.getId());
                                }
                            }
                        }
                    }

                    List<AlarmHistoricalData> noHaveIds = new ArrayList<>();
                    if (equipmentId.size() > 0) {
                        alarmHistoricalData.setEquipmentIds(equipmentId);
                        noHaveIds = alarmHistoricalDataMapper.selectAlarmHistoricalDataNoIdList(alarmHistoricalData);
                    }

                    //返回数据
                    haveIds.addAll(noHaveIds);
                    return haveIds;
                }
            } else {
                // 如果是超级管理员(ID为1)，则不过滤数据
                return alarmHistoricalDataMapper.selectAlarmHistoricalDataList(alarmHistoricalData);
            }
        }
        //未登录、未关联则返回空
        return returnList;
    }

    /**
     * 新增告警历史数据
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 结果
     */
    @Override
    public int insertAlarmHistoricalData(AlarmHistoricalData alarmHistoricalData) {
        alarmHistoricalData.setCreateTime(DateUtils.getNowDate());
        return alarmHistoricalDataMapper.insertAlarmHistoricalData(alarmHistoricalData);
    }

    /**
     * 修改告警历史数据
     *
     * @param alarmHistoricalData 告警历史数据
     * @return 结果
     */
    @Override
    public int updateAlarmHistoricalData(AlarmHistoricalData alarmHistoricalData) {
        return alarmHistoricalDataMapper.updateAlarmHistoricalData(alarmHistoricalData);
    }

    /**
     * 批量删除告警历史数据
     *
     * @param ids 需要删除的告警历史数据主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteAlarmHistoricalDataByIds(Long[] ids) {
        if (StringUtils.isEmpty(ids)) {
            return AjaxResult.error("参数为空！");
        }
        return AjaxResult.success(alarmHistoricalDataMapper.deleteAlarmHistoricalDataByIds(ids));
    }

    /**
     * 删除告警历史数据信息
     *
     * @param id 告警历史数据主键
     * @return 结果
     */
    @Override
    public int deleteAlarmHistoricalDataById(Long id) {
        return alarmHistoricalDataMapper.deleteAlarmHistoricalDataById(id);
    }
}
