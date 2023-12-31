package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.deviceTree.BuildNode;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.mapper.deviceTree.DeviceTreeMapper;
import com.zc.efounder.JEnterprise.mapper.deviceTree.PointMapper;
import com.zc.common.constant.RedisKeyConstants;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wanghongjie
 * @Description:设备树存取方法定义
 * @Date: Created in 17:03 2022/9/21
 * @Modified By:
 */
@Component
public class DeviceTreeCache {

    @Resource
    private RedisCache redisCache;
    @Resource
    private DeviceTreeMapper mapper;
    @Resource
    private DeviceTreeMapper deviceTreeMapper;
    @Resource
    private PointMapper pointMapper;

    private static final Logger log = LoggerFactory.getLogger(DeviceTreeCache.class);


    /**
     * 获取下级级联信息
     *
     * @param deviceTreeId 设备树id
     * @return
     */
    public List<DeviceTree> getCascadeSubordinate(Integer deviceTreeId) {
        /******************qindehua 增加缓存为空 查询数据库逻辑*******************************/
        if (null == deviceTreeId) {
            return null;
        }
        //判断是否已查询过数据库
        boolean bool = true;

        Map<String, DeviceTree> deviceTreeList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);


        if (null == deviceTreeList || deviceTreeList.isEmpty()) {
            List<DeviceTree> deviceTrees = mapper.selectDeviceTreeByRedis();
            if (CollectionUtils.isNotEmpty(deviceTrees)) {
                bool = false;
                deviceTrees.forEach(item -> {
                    deviceTreeList.put(String.valueOf(item.getDeviceTreeId()), item);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) item.getDeviceTreeId(), item);
                });
            } else {
                return null;
            }
        }

        Map<Integer, List<DeviceTree>> deviceTreeMapList = new HashMap<>();


        for (DeviceTree deviceTree : deviceTreeList.values()) {

            deviceTreeMapList.computeIfAbsent(deviceTree.getDeviceTreeFatherId(), k -> new ArrayList<>()).add(deviceTree);
        }

        List<DeviceTree> sbPzStructModelList = new ArrayList<>();

        DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeId);
        if (deviceTree == null) {
            if (bool) {
                DeviceTree deviceTreeMapper = mapper.selectDeviceTreeRedisById(deviceTreeId);
                if (deviceTreeMapper == null) {
                    return null;
                } else {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) deviceTreeMapper.getDeviceTreeId(), deviceTreeMapper);
                    sbPzStructModelList.add(deviceTreeMapper);
                }
            }
        } else {
            sbPzStructModelList.add(deviceTree);
        }

        getUnderModel(sbPzStructModelList, deviceTreeMapList, deviceTreeId);

        return sbPzStructModelList;
    }

    private void getUnderModel(List<DeviceTree> sbPzStructModelList, Map<Integer, List<DeviceTree>> deviceTreeMapList, Integer deviceTreeId) {
        if (deviceTreeMapList == null
                || deviceTreeMapList.isEmpty()
                || sbPzStructModelList == null
                || deviceTreeId == null) {
            return;
        }

        List<DeviceTree> childModelList = deviceTreeMapList.get(deviceTreeId);

        if (childModelList != null && !childModelList.isEmpty()) {
            childModelList.forEach(sbPzStructModel ->
            {
                getUnderModel(sbPzStructModelList, deviceTreeMapList, sbPzStructModel.getDeviceTreeId());

            });

            sbPzStructModelList.addAll(childModelList);
        }

    }

    //判断当前节点是否存在子节点
    public List<DeviceTree> getSubordinate(Integer deviceTreeId) {
        List<DeviceTree> childModelList = new ArrayList<>();
        if (null == deviceTreeId) {
            return null;
        }

        Map<String, DeviceTree> deviceTreeList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);


        if (null == deviceTreeList || deviceTreeList.isEmpty()) {
            return null;
        }

        Map<Integer, List<DeviceTree>> deviceTreeMapList = new HashMap<>();


        for (DeviceTree deviceTree : deviceTreeList.values()) {

            deviceTreeMapList.computeIfAbsent(deviceTree.getDeviceTreeFatherId(), k -> new ArrayList<>()).add(deviceTree);
        }
        if (deviceTreeMapList.containsKey(deviceTreeId)) {

            childModelList = deviceTreeMapList.get(deviceTreeId);

        }
        return childModelList;
    }

    /**
     * @Description: 根据系统名称查询当前节点信息
     * @auther: wanghongjie
     * @date: 11:42 2022/11/10
     * @param:
     * @return:
     */
    public DeviceTree getSubordinateBySysName(String sysName) {
        if (null == sysName || StringUtils.isEmpty(sysName)) {
            return null;
        }

        Map<String, DeviceTree> deviceTreeList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);


        if (null == deviceTreeList || deviceTreeList.isEmpty()) {
            return null;
        }


        for (DeviceTree deviceTreeMsg : deviceTreeList.values()) {

            if (deviceTreeMsg.getSysName().equals(sysName)) {
                return deviceTreeMsg;
            }
        }
        return null;
    }

    /**
     * @Description: 根据父节点id查询信息
     * @auther: wanghongjie
     * @date: 8:53 2022/11/11
     * @param:
     * @return:
     */
    public List<DeviceTree> getSubordinateByPSysName(Integer pSysNameId) {
        List<DeviceTree> childModelList = new ArrayList<>();

        if (null == pSysNameId) {
            return null;
        }

        Map<String, DeviceTree> deviceTreeList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);


        if (null == deviceTreeList || deviceTreeList.isEmpty()) {
            return null;
        }


        for (DeviceTree deviceTreeMsg : deviceTreeList.values()) {

            if (deviceTreeMsg.getDeviceTreeFatherId() == pSysNameId) {
                childModelList.add(deviceTreeMsg);
            }
        }

        if (childModelList.size() == 0) {
            return null;
        }

        return childModelList;
    }

    /**
     * 获取树节点根据设备树id
     *
     * @return {@code DeviceTree }
     * @Author qindehua
     * @Date 2022/12/23
     **/
    public DeviceTree getDeviceTreeByDeviceTreeId(Long deviceTreeId) {
        if (deviceTreeId == null) {
            log.warn("deviceTreeId为空！");
            return null;
        }
        DeviceTree deviceTree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, deviceTreeId);
        if (deviceTree == null) {
            DeviceTree dataMapper = deviceTreeMapper.selectDeviceTreeRedisById(deviceTreeId.intValue());
            if (dataMapper == null) {
                log.warn("树节点为空！");
                return null;
            } else {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, (long) dataMapper.getDeviceTreeId(), dataMapper);
                return dataMapper;
            }
        }
        return deviceTree;
    }

    /**
     * 从缓存获取 线路 根据树id
     *
     * @param deviceTreeId 线路设备树id
     * @return {@code BuildNode }
     * @Author qindehua
     * @Date 2022/12/29
     **/
    public BuildNode getNodeByLineTreeId(Long deviceTreeId){

        if (deviceTreeId==null){
            log.warn("deviceTreeId为空！");
            return null;
        }

        BuildNode buildNode = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Line, deviceTreeId);
        if (buildNode == null) {
            BuildNode dataMapper=pointMapper.selectBuildNodeByDeviceTreeId(deviceTreeId);
            if (dataMapper==null){
                log.warn("树ID为："+deviceTreeId+"的线路 未找到！");
                return null;
            }else {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Build_Line,  (long)dataMapper.getTreeId(), dataMapper);
                return dataMapper;
            }
        }
        return buildNode;
    }

    /**
     * 从缓存获取干线 根据树id
     *
     * @param deviceTreeId 干线设备树id
     * @return {@code BuildNode }
     * @Author qindehua
     * @Date 2022/12/29
     **/
    public BuildNode getNodeByTrunkTreeId(Long deviceTreeId){

        if (deviceTreeId==null){
            log.warn("deviceTreeId为空！");
            return null;
        }

        BuildNode buildNode = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_TrunkLine, deviceTreeId);
        if (buildNode == null) {
            BuildNode dataMapper=pointMapper.selectBuildNodeByDeviceTreeId(deviceTreeId);
            if (dataMapper==null){
                log.warn("树ID为："+deviceTreeId+"的干线 未找到！");
                return null;
            }else {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_TrunkLine,  (long)dataMapper.getTreeId(), dataMapper);
                return dataMapper;
            }
        }
        return buildNode;
    }

    /**
     * 从缓存获取支线 根据树id
     *
     * @param deviceTreeId 支线设备树id
     * @return {@code BuildNode }
     * @Author qindehua
     * @Date 2022/12/29
     **/
    public BuildNode getNodeByBranchTreeId(Long deviceTreeId){

        if (deviceTreeId==null){
            log.warn("deviceTreeId为空！");
            return null;
        }

        BuildNode buildNode = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_BranchLine, deviceTreeId);
        if (buildNode == null) {
            BuildNode dataMapper=pointMapper.selectBuildNodeByDeviceTreeId(deviceTreeId);
            if (dataMapper==null){
                log.warn("树ID为："+deviceTreeId+"的支线 未找到！");
                return null;
            }else {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_BranchLine,  (long)dataMapper.getTreeId(), dataMapper);
                return dataMapper;
            }
        }
        return buildNode;
    }
}
