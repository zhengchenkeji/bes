package com.zc.efounder.JEnterprise.service.energyInfo.impl;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.PointPowerParam;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.vo.MeterDataVo;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBranchConfigMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBranchMeterLinkMapper;
import com.zc.efounder.JEnterprise.service.energyInfo.AthenaBranchConfigService;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBesHouseholdBranchLinkMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import com.zc.efounder.JEnterprise.mapper.energyInfo.SubitemBranchLinkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsAlarmNotifierLinkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsMapper;
import com.zc.common.constant.RedisKeyConstants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 支路拓扑配置Service业务层处理
 * @author qindehua
 */
@Service
public class AthenaBranchConfigServiceImpl implements AthenaBranchConfigService {
    @Resource
    private AthenaBranchConfigMapper athenaBranchConfigMapper;
    @Resource
    private AthenaBranchMeterLinkMapper athenaBranchMeterLinkMapper;
    @Resource
    private AthenaBesHouseholdBranchLinkMapper householdBranchLinkMapper;
    @Resource
    private SubitemBranchLinkMapper subitemBranchLinkMapper;
    @Resource
    private AlarmTacticsMapper alarmTacticsMapper;
    @Resource
    private AlarmTacticsAlarmNotifierLinkMapper tacticsAlarmNotifierLinkMapper;
    @Resource
    private RedisCache redisCache;

    //记录要删除的缓存
    List<String> delCache;

    /**
     * 初始化缓存
     *
     * @Author qindehua
     * @Date 2022/10/24
     **/
    @PostConstruct
    public void init() {
        // 获取全部设备列表数据
        List<AthenaBranchConfig> list = athenaBranchConfigMapper.selectAthenaBranchConfigList(null);

        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig);

        if (list == null || list.isEmpty()) {

            return;
        }

        list.forEach(item -> {
            redisCache.setCacheMapValue(
                    RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, item.getBranchId(), item);
        });
    }

    /**
     * 添加缓存
     *
     * @Author qindehua
     * @Date 2022/10/24
     **/
    private void addCache(AthenaBranchConfig athenaBranchConfig) {
        redisCache.setCacheMapValue(
                RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, athenaBranchConfig.getBranchId(), athenaBranchConfig);
    }

    /**
     * 查询建筑列表
     *
     * @param parkCode 园区code
     * @return map值
     */
    @Override
    public List<Map> selectBuildingList(String parkCode) {
        return athenaBranchConfigMapper.selectBuildingList(parkCode);
    }

    /**
     * 包含电表 保存操作
     *
     * @param meterDataVo 电表数据
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAthenaBranchConfigMeter(MeterDataVo meterDataVo) {
        delCache = new ArrayList<>();
        //支路电表关联缓存
        Map<String, AthenaBranchMeterLink> athenaBranchMeterLinks = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
        /**级联分支节点ID集合**/
        List<Long> cascadedIds = new ArrayList<>();
        /**之前电表ID集合**/
        List<Long> beginMeterIds = new ArrayList<>();
        /**新增电表ID集合**/
        List<Long> meterIds = new ArrayList<>();
        meterDataVo.getMeterList().forEach(item -> meterIds.add(item.getMeterId()));
        /**从缓存中 获取符合条件的支路数据**/
        List<AthenaBranchConfig> athenaBranchConfigs = new ArrayList<>();
        Map<String, AthenaBranchConfig> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig);
        map.values().forEach(item -> {
            if (meterDataVo.getParkCode().equals(item.getParkCode())
                    && meterDataVo.getEnergyCode().equals(item.getEnergyCode())) {
                athenaBranchConfigs.add(item);
            }
        });
        /**先递归查出所有 是否级联为 是 的父节点**/
        recursionFn(athenaBranchConfigs, meterDataVo.getFatherId(), cascadedIds);
//        /**先查出来之前该分支下的旧数据   为删除父节点之前的重复数据做备用 及判断操作类型**/
//        athenaBranchMeterLinkMapper.selectAthenaBranchMeterLinkListInfo(meterDataVo.getBranchId()).forEach(item -> beginMeterIds.add(item.getMeterId()));

        //如果数据为空  说明还未添加关联信息  直接添加即可
        if (athenaBranchMeterLinks != null && !athenaBranchMeterLinks.values().isEmpty()) {

            /**先查出来之前该分支下的旧数据   为删除父节点之前的重复数据做备用 及判断操作类型**/
            athenaBranchMeterLinks.values().forEach(item -> {
                if (meterDataVo.getBranchId().equals(item.getBranchId())) {
                    beginMeterIds.add(item.getMeterId());
                }
            });

            /**之前有数据 现在没数据 等式成立 只做删除操作**/
            if (beginMeterIds.size() > 0 && meterIds.size() == 0) {
                /**先通过当前分支ID 删除关联表**/
                athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkByBranchId(meterDataVo.getBranchId());
                //记录删除的缓存
                athenaBranchMeterLinks.values().forEach(item -> {
                    if (item.getBranchId().equals(meterDataVo.getBranchId())) {
                        delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink + "," + item.getId());
                    }
                });

                /**删除父级数据**/
                if (cascadedIds.size() > 0) {
                    athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkByBranchIdsAndMeterIds(cascadedIds, beginMeterIds);
                    for (Long cascadedId : cascadedIds) {
                        for (Long beginMeterId : beginMeterIds) {
                            //记录删除的缓存
                            athenaBranchMeterLinks.values().forEach(item -> {
                                if (item.getBranchId().equals(cascadedId) && item.getMeterId().equals(beginMeterId)) {
                                    delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink + "," + item.getId());
                                }
                            });
                        }
                    }

                }
                if (!delCache.isEmpty()) {
                    //先删除之前旧数据
                    delCache.forEach(item -> {
                        String[] array = item.split(",");
                        redisCache.delCacheMapValue(array[0], array[1]);
                    });

                }
                return true;
            }


            /**之前电表ID集合为空 说明该节点之前没有关联电表 可以直接添加**/
            else if (beginMeterIds.size() == 0 && meterIds.size() > 0) {
                /**通过父节点ID及电表ID 删除父节点关联表重复数据  防止新增子节点关联电表时  导致父节点添加重复电表**/
                if (cascadedIds.size() > 0) {
                    athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkByBranchIdsAndMeterIds(cascadedIds, meterIds);
                    for (Long cascadedId : cascadedIds) {
                        for (Long meterId : meterIds) {
                            //记录删除的缓存
                            athenaBranchMeterLinks.values().forEach(item -> {
                                if (item.getBranchId().equals(cascadedId) && item.getMeterId().equals(meterId)) {
                                    delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink + "," + item.getId());
                                }
                            });
                        }
                    }
                }
            }


            /**之前电表集合和电表集合都不为空时**/
            else if (beginMeterIds.size() > 0 && meterIds.size() > 0) {
                /**先删除旧父级数据**/
                if (cascadedIds.size() > 0) {
                    /**将两个集合 合并 及去重**/
                    List<Long> collect = Stream.of(beginMeterIds, meterIds)
                            .flatMap(Collection::stream)
                            .distinct()
                            .collect(Collectors.toList());
                    /**先删除旧数据**/
                    athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkByBranchIdsAndMeterIds(cascadedIds, collect);
                    for (Long cascadedId : cascadedIds) {
                        for (Long meterId : collect) {
                            //记录删除的缓存
                            athenaBranchMeterLinks.values().forEach(item -> {
                                if (item.getBranchId().equals(cascadedId) && item.getMeterId().equals(meterId)) {
                                    delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink + "," + item.getId());
                                }
                            });
                        }
                    }

                }
                /**再通过当前分支ID 删除关联表**/
                athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkByBranchId(meterDataVo.getBranchId());
                //记录删除的缓存
                athenaBranchMeterLinks.values().forEach(item -> {
                    if (item.getBranchId().equals(meterDataVo.getBranchId())) {
                        delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink + "," + item.getId());
                    }
                });
            }
            /**两个集合都为空 直接return**/
            else {
                return true;
            }
        } else if (meterIds.size() <= 0) {
            return true;
        }

        /**将当前节点插入级联节点集合中**/
        cascadedIds.add(meterDataVo.getBranchId());
        /**根据ids 通双重循环 进行批量新增**/
        if (!batchData(cascadedIds, meterDataVo)) {
            return false;
        }
        return true;
    }


    /**
     * 批处理数据
     *
     * @param cascadedIds 级联ids
     * @param meterDataVo 电表数据
     */
    private Boolean batchData(List<Long> cascadedIds, MeterDataVo meterDataVo) {
        //批量新增关联表
        List<AthenaBranchMeterLink> athenaBranchMeterLinks = new ArrayList<>();
        //双重循环 添加所需要新增的支路电表关联列表
        cascadedIds.forEach(id -> {
            meterDataVo.getMeterList().forEach(item -> {
                AthenaBranchMeterLink athenaBranchMeterLink = new AthenaBranchMeterLink(id, item.getMeterId(), item.getOperator(),
                        item.getElectricParam(), item.getSysName(), item.getAlias(), item.getType(),
                        item.getCollectionMethodCode(), item.getDeviceTreeId(),item.getDeviceType());
                athenaBranchMeterLinks.add(athenaBranchMeterLink);
            });
        });
        if (athenaBranchMeterLinkMapper.batchInsertAthenaBranchMeterLink(athenaBranchMeterLinks)) {
            if (!delCache.isEmpty()) {
                //先删除之前旧数据
                delCache.forEach(item -> {
                    String[] array = item.split(",");
                    redisCache.delCacheMapValue(array[0], array[1]);
                });
            }

            //再添加缓存
            addBranchMeterCache(athenaBranchMeterLinks);
            return true;
        }
        return false;
    }

    /**
     * 添加分支电表关系缓存
     *
     * @Author qindehua
     * @Date 2022/10/25
     **/
    private void addBranchMeterCache(List<AthenaBranchMeterLink> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        list.forEach(item -> {
            if(item.getDeviceType().equals("0")){
                /**当前设备为bes设备 添加采集方案id*/
                /**加入树节点*/
                Map<String, AthenaElectricMeter> meterMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter);

                if(item.getElectricParam().equals(PointPowerParam.Point_Meter_Code)){
                    item.setType("1");

                    /**加入树节点*/
                    meterMap.values()
                            .stream()
                            .filter(meter->meter.getMeterId().equals(item.getMeterId())).forEach(meter->{
                        item.setDeviceTreeId(meter.getDeviceTreeId());
                    });
                }else{
                    meterMap.values()
                            .stream()
                            .filter(meter->meter.getMeterId().equals(item.getMeterId())).forEach(meter->{
                        item.setCollectionMethodCode(meter.getCollectionMethodCode());
                    });
                }


            }


            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, item.getId(), item);
        });
    }

    /**
     * 递归列表
     *
     * @param list      列表
     * @param fatherId  当前节点的  父ID
     * @param fatherIds 父id集合
     */
    private void recursionFn(List<AthenaBranchConfig> list, Long fatherId, List<Long> fatherIds) {

        // 得到父节点ID
        for (AthenaBranchConfig item : list) {
            if (item.getBranchId().equals(fatherId)) {
                //判断父节点是否为级联  是 则添加
                if (item.getCascaded().equals(Constants.CASCADED)) {
                    fatherIds.add(item.getBranchId());
                }
                recursionFn(list, item.getParentId(), fatherIds);
            }
        }
    }

    /**
     * 查询支路拓扑配置
     *
     * @param branchId 支路拓扑配置主键
     * @return 支路拓扑配置
     */
    @Override
    public AthenaBranchConfig selectAthenaBranchConfigByBranchId(Long branchId) {
        Map<String, AthenaBranchConfig> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig);
        for (AthenaBranchConfig item : map.values()) {
            if (branchId.equals(item.getBranchId())) {
                return item;
            }
        }
        return null;
    }


    /**
     * 构建前端所需要下拉树结构
     *
     * @param list 支路拓扑配置列表
     * @return 下拉树结构列表
     */

    @Override
    public List<TreeSelect> buildTreeSelect(List<AthenaBranchConfig> list) {
        List<AthenaBranchConfig> branchTrees = buildTree(list);
        return branchTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param list 支路拓扑配置列表
     * @return 树结构列表
     */
    @Override
    public List<AthenaBranchConfig> buildTree(List<AthenaBranchConfig> list) {
        List<AthenaBranchConfig> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<Long>();
        for (AthenaBranchConfig item : list) {
            tempList.add(item.getBranchId());
        }
        for (AthenaBranchConfig item : list) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(item.getParentId())) {
                recursionFn(list, item);
                returnList.add(item);
            }
        }
        if (returnList.isEmpty()) {
            returnList = list;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<AthenaBranchConfig> list, AthenaBranchConfig t) {
        // 得到子节点列表
        List<AthenaBranchConfig> childList = getChildList(list, t);
        t.setChildren(childList);
        for (AthenaBranchConfig tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }


    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<AthenaBranchConfig> list, AthenaBranchConfig t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 得到子节点列表
     */
    private List<AthenaBranchConfig> getChildList(List<AthenaBranchConfig> list, AthenaBranchConfig t) {
        List<AthenaBranchConfig> tlist = new ArrayList<>();
        Iterator<AthenaBranchConfig> it = list.iterator();
        while (it.hasNext()) {
            AthenaBranchConfig n = it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getBranchId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }


    /**
     * 查询支路拓扑配置列表
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return 支路拓扑配置
     */
    @Override
    public List<AthenaBranchConfig> selectAthenaBranchConfigList(AthenaBranchConfig athenaBranchConfig) {

        List<AthenaBranchConfig> athenaBranchConfigs = new ArrayList<>();
        Map<String, AthenaBranchConfig> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig);
        map.values().forEach(item -> {
            if (item.getParkCode().equals(athenaBranchConfig.getParkCode())
                    && item.getEnergyCode().equals(athenaBranchConfig.getEnergyCode())) {
                athenaBranchConfigs.add(item);
            }
        });
        //根据创建时间进行排序
        if (CollectionUtils.isNotEmpty(athenaBranchConfigs)) {
            athenaBranchConfigs.sort((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));
        }
        return athenaBranchConfigs;
    }

    /**
     * 查询支路拓扑配置列表 查询 自身及子数据
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @param code               查询标识
     * @return 支路拓扑配置
     */
    @Override
    public List<AthenaBranchConfig> selectAthenaBranchConfigListSun(AthenaBranchConfig athenaBranchConfig, String code) {
        return athenaBranchConfigMapper.selectAthenaBranchConfigListSun(athenaBranchConfig, code);
    }

    /**
     * 新增支路拓扑配置
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/10/25
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertAthenaBranchConfig(AthenaBranchConfig athenaBranchConfig) {

        //名称查重
        Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig).values();
        for (Object obj : collection) {
            AthenaBranchConfig data = (AthenaBranchConfig) obj;
            if (data.getBranchName().equals(athenaBranchConfig.getBranchName()) &&
                    data.getParkCode().equals(athenaBranchConfig.getParkCode()) &&
                    data.getEnergyCode().equals(athenaBranchConfig.getEnergyCode())) {
                return AjaxResult.error("支路名称不允许重复!");
            }
        }
        athenaBranchConfig.setCreateTime(DateUtils.getNowDate());
        String branchCode = null;
        Integer code = athenaBranchConfigMapper.selectAthenaBranchConfigCodeNum(athenaBranchConfig.getParkCode(),
                athenaBranchConfig.getEnergyCode(), (athenaBranchConfig.getParkCode() + athenaBranchConfig.getEnergyCode()).length());
        //当前区域、能源类型下  没有数据时直接添加
        if (code == null) {
            branchCode = athenaBranchConfig.getParkCode() + athenaBranchConfig.getEnergyCode() + 1;
        } else {
            //当前区域、能源类型下  有数据时 选出最大值  进行num+1
            branchCode = athenaBranchConfig.getParkCode() + athenaBranchConfig.getEnergyCode() + (code + 1);
        }
        athenaBranchConfig.setBranchCode(branchCode);
        if (athenaBranchConfigMapper.insertAthenaBranchConfig(athenaBranchConfig)) {
            addCache(athenaBranchConfig);
            return AjaxResult.success("新增成功！", athenaBranchConfig);
        }
        return AjaxResult.error("新增失败");
    }

    /**
     * 修改支路拓扑配置
     *
     * @param athenaBranchConfig 支路拓扑配置
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/10/25
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateAthenaBranchConfig(AthenaBranchConfig athenaBranchConfig) {
        if (athenaBranchConfig.getBranchId()==null){
            return AjaxResult.error("支路ID不允许为空！");
        }
        //名称查重
        Collection collection = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig).values();
        for (Object obj : collection) {
            AthenaBranchConfig data = (AthenaBranchConfig) obj;
            if (data.getBranchId().equals(athenaBranchConfig.getBranchId())) {
                continue;
            }
            if (data.getBranchName().equals(athenaBranchConfig.getBranchName()) &&
                    data.getParkCode().equals(athenaBranchConfig.getParkCode()) &&
                    data.getEnergyCode().equals(athenaBranchConfig.getEnergyCode())) {
                return AjaxResult.error("支路名称不允许重复!");
            }
        }
        athenaBranchConfig.setUpdateTime(DateUtils.getNowDate());

        if (athenaBranchConfigMapper.updateAthenaBranchConfig(athenaBranchConfig)) {
            //更新缓存
            addCache(athenaBranchConfig);
            return AjaxResult.success("修改成功！", athenaBranchConfig);
        }

        return AjaxResult.error("修改失败！");
    }

    /**
     * 批量删除支路拓扑配置
     *
     * @param branchIds 需要删除的支路拓扑配置主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteAthenaBranchConfigByBranchIds(Long[] branchIds) {
        if (StringUtils.isEmpty(branchIds)){
            return AjaxResult.error("支路ID不能为空！");
        }
        //判断支路下面是否有子组
        Map<String, AthenaBranchConfig> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig);
        List<Long> list = Arrays.asList(branchIds);
        for (AthenaBranchConfig item : map.values()) {
            if (list.contains(item.getParentId()) && !list.contains(item.getBranchId())) {
                return AjaxResult.error("请先删除当前组下面的子组!");
            }
        }
        //先删除支路下面关联的电表
        athenaBranchMeterLinkMapper.deleteAthenaBranchMeterLinkByIds(branchIds);
        //再删除分户支路关联表
        householdBranchLinkMapper.deleteHouseholdBranchLinkByBranchIds(branchIds);
        //再删除分项支路关联表
        subitemBranchLinkMapper.deleteSubitemBranchLinkByBranchIds(branchIds);
        //再删除支路
        athenaBranchConfigMapper.deleteAthenaBranchConfigByBranchIds(branchIds);
        //判断删除时  是否存在报警策略
        Long[] array = alarmTacticsMapper.selectAlarmTacticsByDeviceIds(branchIds);
        //删除报警策略
        if (array.length > 0) {
            tacticsAlarmNotifierLinkMapper.deleteByAlarmtacticsIdsBoolean(array);
            alarmTacticsMapper.deleteAlarmTacticsByIds(array);
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, array);
        }

        //先删除关联表缓存  再删除支路缓存
        Map<String, AthenaBranchMeterLink> meterLinkMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
        //要删除的支路电表关联表
        meterLinkMap.values().forEach(item -> {
            if (list.contains(item.getBranchId())) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, item.getId());
            }
        });

        //要删除的分户支路关联表
        Map<String, AthenaBesHouseholdBranchLink> householdBranchLinkMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink);
        householdBranchLinkMap.values().forEach(item -> {
            if (list.contains(item.getBranchId())) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink, item.getId());
            }
        });
        //要删除的分项支路关联表
        Map<String, SubitemBranchLink> subitemBranchLinkMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink);
        subitemBranchLinkMap.values().forEach(item -> {
            if (list.contains(item.getBranchId())) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink, item.getId());
            }
        });

        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, branchIds);
        return AjaxResult.success("删除成功！");
    }

    /**
     * @description:获取所有支路数据
     * @author: sunshangeng
     * @date: 2022/11/14 15:48
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult SelectBranchTreeList() {
        Map<String, AthenaBranchConfig> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig);
        List<AthenaBranchConfig> branchConfigList = new ArrayList<>();
        map.values().forEach(item -> {
            branchConfigList.add(item);
        });
        List<TreeSelect> treeSelectList = buildTreeSelect(branchConfigList);
        return AjaxResult.success(treeSelectList);
    }

    /**
     * 删除时 查看是否关联分户及分项
     *
     * @param branchIds 支路id
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/12/16
     **/
    @Override
    public AjaxResult getMessage(Long[] branchIds) {
        if (StringUtils.isEmpty(branchIds)){
            return AjaxResult.error("参数为空！");
        }
        List<Long> list= Arrays.asList(branchIds);
        String msg="";
        //已和分户关联
        String household="";
        //已和分项关联
        String subitem="";
        Map<String, AthenaBesHouseholdBranchLink> householdBranchLinkMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink);
        for (AthenaBesHouseholdBranchLink value : householdBranchLinkMap.values()) {
            if (list.contains(value.getBranchId())){
                household="分户";
                 break;
            }
        }
        Map<String, SubitemBranchLink> subitemBranchLinkMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink);
        for (SubitemBranchLink value : subitemBranchLinkMap.values()) {
            if (list.contains(value.getBranchId())){
                subitem="分项";
                break;
            }
        }
        if ("".equals(household) || "".equals(subitem)){
            return AjaxResult.success(msg,true);

        }else {
            msg="支路已和"+household+subitem+"关联，是否进行删除？";
            return AjaxResult.success(msg,false);
        }
    }
}
