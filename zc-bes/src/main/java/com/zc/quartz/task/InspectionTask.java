package com.zc.quartz.task;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.quartz.domain.SysJob;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.connect.business.handler.SendMsgHandler;
import com.zc.efounder.JEnterprise.Cache.ControllerCache;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.domain.inspection.Inspection;
import com.zc.efounder.JEnterprise.mapper.deviceSynchronization.AthenaBesTimeTaskSyncSbMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时巡检
 *
 * @author qindehua
 * @date 2023/02/06
 */
@Component("inspectionTask")
public class InspectionTask {

    private static final Logger log = LoggerFactory.getLogger(InspectionTask.class);

    /**
     * 计划关联的设备
     */
    @Resource
    private AthenaBesTimeTaskSyncSbMapper athenaBesTimeTaskSyncsbMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private DeviceTreeCache deviceTreeCache;
    @Resource
    private ModuleAndPointCache moduleAndPointCache;
    @Resource
    private ControllerCache controllerCache;

    /**
     * 定时巡检
     *
     * @Author qindehua
     * @Date 2023/02/06
     **/
    public void inspection(SysJob job) {

        //获取点位信息
        List<String> pointList = athenaBesTimeTaskSyncsbMapper.selectNodeIdBySyncId(job.getJobId().toString());
        for (String s : pointList) {
            //获取设备树节点
            DeviceTree deviceTree = deviceTreeCache.getDeviceTreeByDeviceTreeId(Long.parseLong(s));
            if (deviceTree.getDeviceTreeStatus() == 0) {
                log.error("名称为:" + deviceTree.getSysName() + "的点位已离线！");
                continue;
            }
            //进行下位机下发操作
            getDataInfoParam(Long.valueOf(deviceTree.getDeviceTreeId()), deviceTree.getDeviceNodeId(),job);
        }
    }

    /**
     * 获得点位数据信息参数
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @Author qindehua
     * @Date 2023/02/06
     **/
    private Boolean getDataInfoParam(Long deviceTreeId, Integer type,SysJob job) {
        //获取控制器信息 数据校验
        DataReception dataReception = validation(deviceTreeId, type);
        if (!dataReception.getState()) {
            log.error(dataReception.getMsg());
            return false;
        }
        Controller controller = (Controller) dataReception.getData();
        String channelId = controller.getIp();

        Point pointMap = moduleAndPointCache.getPointByDeviceId(deviceTreeId);
        boolean sendState = false;
        //楼控
        if (DeviceTreeConstants.BES_DDCNODE.equals(controller.getType())) {
            sendState = SendMsgHandler.getPointParamDDC(channelId, pointMap.getEquipmentId());
        }
        //照明
        else if (DeviceTreeConstants.BES_ILLUMINE.equals(controller.getType())) {
            sendState = SendMsgHandler.getPointParamLDC(channelId, pointMap.getEquipmentId());
        }
        if (!sendState) {
            log.error("获取逻辑点下位机数据失败!");
            return false;
        }
        //将数据存入缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_Scheduled_Task_Inspection, deviceTreeId,
                new Inspection(deviceTreeId, job.getJobId(), Long.valueOf(controller.getDeviceTreeId()), channelId, 0));
        return true;
    }

    /**
     * 验证
     *
     * @return {@code DataReception }
     * @Author gaojikun
     * @Date 2022/09/29
     **/
    private DataReception validation(Long deviceTreeId, Integer type) {
        //判断数据是否为空
        if (deviceTreeId == null || type == null) {
            return new DataReception(true, "参数错误!");
        }
        //获取当前节点所属控制器
        List<Controller> controllerId = new ArrayList<>();
        recursive(deviceTreeId, controllerId);
        if (controllerId.isEmpty()) {
            return new DataReception(false, "控制器缓存信息不存在!");
        }
        String channelId = controllerId.get(0).getIp();
        //判断channelID 地址
        if (!StringUtils.hasText(channelId)) {
            return new DataReception(true, "无效的 channelID 地址!");
        }
        return new DataReception(true, controllerId.get(0));
    }

    /**
     * 递归
     *
     * @Author qindehua
     * @Date 2022/09/27
     **/
    private void recursive(Long treeId, List<Controller> controller) {
        DeviceTree tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, treeId);
        /**数据为空时 结束递归*/
        if (tree == null) {
            return;
        }
        if (tree.getDeviceNodeId() == DeviceTreeConstants.BES_DDCNODE || //DDC
                tree.getDeviceNodeId() == DeviceTreeConstants.BES_ILLUMINE //照明
        ) {
            controller.add(
                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Controller,
                            (long) tree.getDeviceTreeId()));
            return;
        }
        recursive((long) tree.getDeviceTreeFatherId(), controller);
    }

}
