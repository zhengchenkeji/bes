package com.zc.efounder.JEnterprise.service.safetyWarning.impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.result.ResultMap;
import com.ruoyi.common.utils.SecurityUtils;
import com.zc.ApplicationContextProvider;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sms.model.SmsParam;
import com.ruoyi.common.utils.sms.server.EmailServer;
import com.ruoyi.common.utils.sms.server.SmsServer;
import com.zc.common.constant.NoticeTableConstants;
import com.zc.efounder.JEnterprise.commhandler.AlarmHandler;
import com.zc.efounder.JEnterprise.commhandler.NoticeHandler;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.efounder.JEnterprise.domain.safetyWarning.*;
import com.zc.efounder.JEnterprise.mapper.energyInfo.ParkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.*;
import com.zc.efounder.JEnterprise.service.safetyWarning.IAlarmRealtimeDataService;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.constant.WebSocketEvent;
import com.zc.common.core.model.DataReception;
import com.zc.common.core.websocket.WebSocketService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 告警实时数据Service业务层处理
 *
 * @author qindehua
 * @date 2022-11-04
 */
@Service
public class AlarmRealtimeDataServiceImpl implements IAlarmRealtimeDataService {
    //实时报警数据
    private AlarmRealtimeDataMapper alarmRealtimeDataMapper = ApplicationContextProvider.getBean(AlarmRealtimeDataMapper.class);
    //历史报警数据
    private AlarmHistoricalDataMapper alarmHistoricalDataMapper = ApplicationContextProvider.getBean(AlarmHistoricalDataMapper.class);
    //redis
    private RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);

    private static final Logger log = LoggerFactory.getLogger(AlarmHandler.class);
    //告警策略
    private AlarmNotificationRecordMapper notificationRecordMapper = ApplicationContextProvider.getBean(AlarmNotificationRecordMapper.class);
    //发送邮件
    private EmailServer emailServer = ApplicationContextProvider.getBean(EmailServer.class);
    //发送短信
    private SmsServer smsServer = ApplicationContextProvider.getBean(SmsServer.class);
    //告警策略mapper
    private AlarmTacticsMapper alarmTacticsMapper = ApplicationContextProvider.getBean(AlarmTacticsMapper.class);
    //园区mapper
    private ParkMapper parkMapper = ApplicationContextProvider.getBean(ParkMapper.class);
    //告警工单mapper
    private AlarmWorkOrderMapper alarmWorkOrderMapper = ApplicationContextProvider.getBean(AlarmWorkOrderMapper.class);

//    通知配置关系
    private AlarmNoticeLinkMapper noticeLinkMapper = ApplicationContextProvider.getBean(AlarmNoticeLinkMapper.class);

    /**
     * 查询告警实时数据
     *
     * @param id 告警实时数据主键
     * @return 告警实时数据
     */
    @Override
    public AlarmRealtimeData selectAlarmRealtimeDataById(Long id) {
        return alarmRealtimeDataMapper.selectAlarmRealtimeDataById(id);
    }

    /**
     * 查询告警实时数据列表
     *
     * @param alarmRealtimeData 告警实时数据
     * @return 告警实时数据
     */
    @Override
    public List<AlarmRealtimeData> selectAlarmRealtimeDataList(AlarmRealtimeData alarmRealtimeData) {
        List<AlarmRealtimeData> returnList = new ArrayList<>();
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)) {
            SysUser currentUser = loginUser.getUser();
            //不是超级管理员则过滤
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin()) {
                //查询关联的告警策略
                List<Long> idsList = alarmRealtimeDataMapper.selectUserNotifier(String.valueOf(currentUser.getUserId()));

                if (idsList != null && idsList.size() > 0) {
                    alarmRealtimeData.setIds(idsList);
                    //根据告警策略查询对应的实时数据
                    //有策略ID
                    List<AlarmRealtimeData> haveIds = alarmRealtimeDataMapper.selectAlarmRealtimeDataList(alarmRealtimeData);

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

                    List<AlarmRealtimeData> noHaveIds = new ArrayList<>();
                    if (equipmentId.size() > 0) {
                        alarmRealtimeData.setEquipmentIds(equipmentId);
                        noHaveIds = alarmRealtimeDataMapper.selectAlarmRealtimeDataNoIdList(alarmRealtimeData);
                    }

                    //返回数据
                    haveIds.addAll(noHaveIds);
                    return haveIds;
                }
            } else {
                // 如果是超级管理员(ID为1)，则不过滤数据
                return alarmRealtimeDataMapper.selectAlarmRealtimeDataList(alarmRealtimeData);
            }
        }
        //未登录、未关联则返回空
        return returnList;
    }

    @Override
    public Integer selectAlarmCount() {
        return alarmRealtimeDataMapper.selectAlarmRealtimeDataCount();
    }

    @Override
    public AjaxResult selectAlarmInfo(String level) {
        if (StringUtils.isEmpty(level)) {
            return AjaxResult.error("参数为空！");
        }
        return AjaxResult.success(alarmRealtimeDataMapper.selectAlarmRealtimeDataInfo(level));
    }


    /**
     * 批量删除告警实时数据
     *
     * @param ids 需要删除的告警实时数据主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteAlarmRealtimeDataByIds(Long[] ids) {
        if (StringUtils.isEmpty(ids)) {
            return AjaxResult.error("告警ID不能为空！");
        }
        //删除数据
        alarmRealtimeDataMapper.deleteAlarmRealtimeDataByIds(ids);
        //推送前端实时条数
        Map<String, Object> msgMap = new HashedMap();
        DataReception dataReception = getNoRecoverCount();//查询报警的条数
        msgMap.put("alarmRealtimeCount", dataReception.getData());
        // 推送消息到web客户端
        WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
        return AjaxResult.success("删除成功！");
    }

    /**
     * 处理报警实时数据
     *
     * @param alarmRealtimeDatas 报警实时数据
     * @return {@code AjaxResult }
     * @Author qindehua
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateAlarmRealtimeData(List<AlarmRealtimeData> alarmRealtimeDatas, boolean isWorkOrderCall) {
        List<AlarmHistoricalData> list = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        List<Long> workOrderIds = new ArrayList<>();
        /**
         * 告警处理操作
         * gaojikun
         * */
        //非空验证
        if (CollectionUtils.isEmpty(alarmRealtimeDatas)) {
            return AjaxResult.error("参数为空！");
        }
        for (AlarmRealtimeData alarmRealtimeData : alarmRealtimeDatas) {

            AlarmHistoricalData data = new AlarmHistoricalData();
            data.setCreateTime(DateUtils.getNowDate());
            if (alarmRealtimeData.getAlarmTacticsId() != null) {
                data.setAlarmTacticsId(alarmRealtimeData.getAlarmTacticsId());
            }
            data.setAlarmTypeId(alarmRealtimeData.getAlarmTypeId());
            if (alarmRealtimeData.getLastTime() == null) {
                data.setAlarmTime(alarmRealtimeData.getFirstTime());
            } else {
                data.setAlarmTime(alarmRealtimeData.getLastTime());
            }

            data.setDescription(alarmRealtimeData.getPromptMsg());
            data.setAlarmValue(Double.parseDouble(alarmRealtimeData.getAlarmValue()));
            data.setEquipmentId(alarmRealtimeData.getEquipmentId());
            list.add(data);
            if (alarmRealtimeData.getId() != null) {
                ids.add(alarmRealtimeData.getId());
            }

            if (!isWorkOrderCall) {
                //查询告警工单ID
                AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder();
                alarmWorkOrder.setAzwz(alarmRealtimeData.getAzwz());
                alarmWorkOrder.setEquipmentId(alarmRealtimeData.getEquipmentId());
                alarmWorkOrder.setAlarmTypeId(alarmRealtimeData.getAlarmTypeId());
                alarmWorkOrder.setPromptMsg(alarmRealtimeData.getPromptMsg());
                alarmWorkOrder.setPlanVal(alarmRealtimeData.getPlanVal());
                List<AlarmWorkOrder> alarmWorkOrders = alarmWorkOrderMapper.selectAlarmWorkOrderStateList(alarmWorkOrder);
                if (alarmWorkOrders != null && alarmWorkOrders.size() > 0) {
                    workOrderIds.add(alarmWorkOrders.get(0).getId());
                }
            }


        }
        //先删除报警实时数据
        alarmRealtimeDataMapper.deleteAlarmRealtimeDataByIds(ids.toArray(new Long[ids.size()]));
        //再新增历史数据表
        alarmHistoricalDataMapper.insertAlarmHistoricalDataBatch(list);

        if (!isWorkOrderCall && workOrderIds.size() > 0) {
            //修改告警工单处理状态
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser currentUser = loginUser.getUser();
            alarmWorkOrderMapper.updateAlarmWorkOrderDealBatch(workOrderIds, "1",
                    DateUtils.getNowDate(), String.valueOf(currentUser.getUserId()), currentUser.getUserName(),
                    "告警实时数据已处理");
        }

        //完成操作后 循环进行通知
        for (AlarmRealtimeData alarmRealtimeData : alarmRealtimeDatas) {
            //告警策略Id 不为空 进行以下操作
            if (null != alarmRealtimeData.getAlarmTacticsId()) {


                AlarmTactics alarmTactics = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, alarmRealtimeData.getAlarmTacticsId());

                if (alarmTactics == null) {
                    AlarmTactics dataMapper = alarmTacticsMapper.selectAlarmTacticsById(alarmRealtimeData.getAlarmTacticsId());
                    if (dataMapper == null) {
                        continue;
                    } else {
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, dataMapper.getAlarmTypeId(), dataMapper);
                        alarmTactics = dataMapper;
                    }
                }
                String contextJson = createJson(alarmRealtimeData.getAlarmName(),"告警解除",alarmRealtimeData.getPromptMsg()
                        , alarmRealtimeData.getAlarmValue(), alarmRealtimeData.getPlanVal());

                //判断是否发送消息通知  0：否  1：是  通知类型  为0：全部 或者  2：告警解除
                if (1 == alarmTactics.getIsSendInform() && (0 == alarmTactics.getInformType() || 2 == alarmTactics.getInformType())) {

                    //告警播报  0：否  1：是
                    if (1 == alarmTactics.getAlarmSound()) {
                        //查询出来该报警策略关联了那些告警接收人
                        List<AlarmNotifier> alarmNotifiers = alarmTacticsMapper.selectAlarmNotifierByAlarmTacticsId(alarmTactics.getId());
                        /**获取当前绑定的语音播报通知配置*/
                        AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 3);
                        for (AlarmNotifier alarmNotifier : alarmNotifiers) {

                            ResultMap resultMap = NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");
                            String resultJson = resultMap.get("msg").toString();

                            String[] userIdsArr = alarmNotifier.getUserId().split(",");
                            List<String> strings = Arrays.asList(userIdsArr);
                            if (!strings.contains("1")) {
                                strings.add("1");
                            }
                            List<String> tokens = getTokenList(strings);
                            for (String token : tokens) {
                                // 推送消息到web客户端
                                WebSocketService.postEvent(token, WebSocketEvent.ALARMMSG, resultJson);
                                //添加通知记录
                                AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 3L, DateUtils.getNowDate(), 1L, contextJson, alarmTactics.getName());
                                alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                                notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                            }
                        }
//                        // 推送消息到web客户端
//                        WebSocketService.broadcast(WebSocketEvent.ALARMMSG, context);
                    }

                    //发送邮件  0：否  1：是
                    if (1 == alarmTactics.getSendEmail()) {
                        //查询出来该报警策略关联了那些告警接收人
                        List<AlarmNotifier> alarmNotifiers = alarmTacticsMapper.selectAlarmNotifierByAlarmTacticsId(alarmTactics.getId());
                        AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 2);

                        //循环发送邮件
                        for (AlarmNotifier alarmNotifier : alarmNotifiers) {

                            String[] userIdsArr = alarmNotifier.getUserId().split(",");
                            List<String> strings = Arrays.asList(userIdsArr);
                            if (!strings.contains("1")) {
                                strings.add("1");
                            }

                            try {
                                for (String userId : strings) {
                                    //获取用户邮箱
                                    SysUser userInfo = alarmTacticsMapper.selectUserInfoById(userId);
                                    if (userInfo != null && !StringUtils.isEmpty(userInfo.getEmail())) {
                                        //业务id暂定
                                        NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");
                                        //添加通知记录
                                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 1L, DateUtils.getNowDate(), 1L, contextJson, alarmTactics.getName());
                                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);

                                    } else {
                                        log.error("用户信息未获取到邮箱信息！");
                                        throw new Exception();
                                    }
                                }
                            } catch (Exception e) {
                                log.error("发送邮件失败！");
                                //添加通知记录
                                AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 1L, DateUtils.getNowDate(), 0L, contextJson, alarmTactics.getName());
                                alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                                notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                            }
                        }
                    }

                    //发送短信  0：否  1：是
                    if (1 == alarmTactics.getTextSb()) {
                        AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 1);

                        //查询出来该报警策略关联了那些告警接收人
                        List<AlarmNotifier> alarmNotifiers = alarmTacticsMapper.selectAlarmNotifierByAlarmTacticsId(alarmTactics.getId());
                        //循环发送短信
                        for (AlarmNotifier alarmNotifier : alarmNotifiers) {

                            String[] userIdsArr = alarmNotifier.getUserId().split(",");
                            List<String> strings = Arrays.asList(userIdsArr);
                            if (!strings.contains("1")) {
                                strings.add("1");
                            }

                            try {
                                for (String userId : strings) {
                                    //获取用户邮箱
                                    SysUser userInfo = alarmTacticsMapper.selectUserInfoById(userId);
                                    if (userInfo != null && !StringUtils.isEmpty(userInfo.getPhonenumber())) {
                                        //业务id暂定
                                        NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");

                                        //添加通知记录
                                        AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 2L, DateUtils.getNowDate(), 1L, contextJson, alarmTactics.getName());
                                        alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                                        notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                                    } else {
                                        log.error("用户信息未获取到短信信息！");
                                        throw new Exception();
                                    }

                                }
                            } catch (Exception e) {
                                log.error("发送短信失败！");
                                AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTactics.getId(), 2L, DateUtils.getNowDate(), 0L, contextJson, alarmTactics.getName());
                                alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                                notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                            }
                        }

                    }
                }
            }
        }

        //推送前端实时条数
        Map<String, Object> msgMap = new HashedMap();
        DataReception dataReception = getNoRecoverCount();//查询报警的条数
        msgMap.put("alarmRealtimeCount", dataReception.getData());
        // 推送消息到web客户端
        WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
        return AjaxResult.success("处理成功！");
    }

    /**
     * 查询报警条数
     *
     * @return {@code DataReception }
     * @Author qindehua
     * @Date 2022/11/04
     **/
    public DataReception getNoRecoverCount() {
        try {
            Integer count = alarmRealtimeDataMapper.selectAlarmRealtimeDataCount();
            return new DataReception(true, count);
        } catch (Exception e) {
            return new DataReception(false);
        }
    }

    private String strMessage(String name, String msg, String val, String planVal) {
        String str = "报警名称:" + name + ",报警描述:" + msg + ",报警值:" + val + ",计划值:" + planVal + ",该报警已手动处理！";
        return str;
    }

    /**
     * 获得token列表
     *
     * @return {@code List<String> }
     * @Author qindehua
     * @Date 2023/01/13
     **/
    private List<String> getTokenList(List<String> strings) {
        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        List<String> list = new ArrayList<>();
        for (String key : keys) {
            LoginUser loginUser = redisCache.getCacheObject(key);
            for (String str : strings) {
                SysUser sysUser = loginUser.getUser();
                if (sysUser.getUserId().equals(Long.parseLong(str))) {
                    list.add(loginUser.getToken());
                }
            }
        }
        return list;
    }
    private String createJson(String name,String triggerMode, String msg, String val, String planVal) {
//        String str = "报警名称:" + name + ",报警描述:" + msg + ",报警值:" + val + ",计划值:" + planVal;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("triggerMode", triggerMode);
        jsonObject.put("msg", msg);
        jsonObject.put("val", val);
        jsonObject.put("planVal", planVal);
        return jsonObject.toJSONString();
    }
}
