package com.zc.efounder.JEnterprise.service.energyInfo.impl;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.AthenaBesHouseholdConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.vo.BranchVo;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBesHouseholdBranchLinkMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBesHouseholdConfigMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsAlarmNotifierLinkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsMapper;
import com.zc.efounder.JEnterprise.service.energyInfo.AthenaBesHouseholdConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 分户计量拓扑配置Service业务层处理
 *
 * @author qindehua
 * @date 2022-09-19
 */
@Service
public class AthenaBesHouseholdConfigServiceImpl implements AthenaBesHouseholdConfigService {
    @Resource
    private AthenaBesHouseholdConfigMapper athenaBesHouseholdConfigMapper;
    @Resource
    private AthenaBesHouseholdBranchLinkMapper athenaBesHouseholdBranchLinkMapper;
    @Resource
    private AlarmTacticsMapper alarmTacticsMapper;
    @Resource
    private AlarmTacticsAlarmNotifierLinkMapper tacticsAlarmNotifierLinkMapper;
    @Resource
    private RedisCache redisCache;
    //记录要删除的缓存
    List<String> delCache;


    @PostConstruct
    public void init() {
        /**
         * 添加数据到 redis 缓存
         */
        // 获取全部设备列表数据
        List<AthenaBesHouseholdConfig> list = athenaBesHouseholdConfigMapper.selectAthenaBesHouseholdConfigList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdConfig);

        if (list == null || list.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        list.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdConfig, val.getHouseholdId(), val);
        });
    }

    /**
     * 添加缓存
     *
     * @Author qindehua
     * @Date 2022/10/24
     **/
    private void addCache(AthenaBesHouseholdConfig athenaBesHouseholdConfig) {
        redisCache.setCacheMapValue(
                RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdConfig, athenaBesHouseholdConfig.getHouseholdId(), athenaBesHouseholdConfig);
    }

    /**
     * 查询分户计量拓扑配置
     *
     * @param id 分户计量拓扑配置主键
     * @return 分户计量拓扑配置
     */
    @Override
    public AthenaBesHouseholdConfig selectAthenaBesHouseholdConfigById(Long id) {
        return athenaBesHouseholdConfigMapper.selectAthenaBesHouseholdConfigById(id);
    }

    /**
     * 查询分户计量拓扑配置列表
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @return 分户计量拓扑配置
     */
    @Override
    public List<AthenaBesHouseholdConfig> selectAthenaBesHouseholdConfigList(AthenaBesHouseholdConfig athenaBesHouseholdConfig) {
        List<AthenaBesHouseholdConfig> householdConfigs = new ArrayList<>();
        Map<String, AthenaBesHouseholdConfig> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdConfig);
        map.values().forEach(item -> {
            if (item.getParkCode().equals(athenaBesHouseholdConfig.getParkCode())
                    && item.getEnergyCode().equals(athenaBesHouseholdConfig.getEnergyCode())) {
                householdConfigs.add(item);
            }
        });
        //根据创建时间进行排序
        if (CollectionUtils.isNotEmpty(householdConfigs)) {
            householdConfigs.sort((a, b) -> a.getCreateTime().compareTo(b.getCreateTime()));
        }
        return householdConfigs;
    }

    /**
     * 查询分户计量拓扑配置列表及下面子节点
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @param code                     查询标识
     * @return 分户计量拓扑配置集合
     */
    @Override
    public List<AthenaBesHouseholdConfig> selectAthenaBesHouseholdConfigListSun(AthenaBesHouseholdConfig athenaBesHouseholdConfig, String code) {
        return athenaBesHouseholdConfigMapper.selectAthenaBesHouseholdConfigListSun(athenaBesHouseholdConfig, code);
    }

    /**
     * 新增时查看 是否有重复分户名称
     *
     * @param athenaBesHouseholdConfig 分户拓扑配置
     * @return 分户拓扑配置
     */
    @Override
    public AthenaBesHouseholdConfig selectAthenaBesHouseholdConfigRepeat(AthenaBesHouseholdConfig athenaBesHouseholdConfig) {
        return athenaBesHouseholdConfigMapper.selectAthenaBesHouseholdConfigRepeat(athenaBesHouseholdConfig);
    }

    /**
     * 新增分户计量拓扑配置
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertAthenaBesHouseholdConfig(AthenaBesHouseholdConfig athenaBesHouseholdConfig) {
        if (athenaBesHouseholdConfigMapper.selectAthenaBesHouseholdConfigRepeat(athenaBesHouseholdConfig) != null) {
            return AjaxResult.error("分户名称不允许重复！");
        }
        athenaBesHouseholdConfig.setCreateTime(DateUtils.getNowDate());
        String householdCode = null;
        Integer code = athenaBesHouseholdConfigMapper.selectHouseholdConfigCodeNum(athenaBesHouseholdConfig.getParkCode(), athenaBesHouseholdConfig.getEnergyCode()
                , (athenaBesHouseholdConfig.getParkCode() + athenaBesHouseholdConfig.getEnergyCode()).length());
        //当前区域、能源类型下  没有数据时直接添加
        if (code == null) {
            householdCode = athenaBesHouseholdConfig.getParkCode() + athenaBesHouseholdConfig.getEnergyCode() + 1;
        } else {
            //当前区域、能源类型下  有数据时 选出最大值  进行num+1
            householdCode = athenaBesHouseholdConfig.getParkCode() + athenaBesHouseholdConfig.getEnergyCode() + (code + 1);
        }
        athenaBesHouseholdConfig.setHouseholdCode(householdCode);
        if (athenaBesHouseholdConfigMapper.insertAthenaBesHouseholdConfig(athenaBesHouseholdConfig)) {
            addCache(athenaBesHouseholdConfig);
            return AjaxResult.success("新增成功！", athenaBesHouseholdConfig);
        }
        return AjaxResult.error("新增失败！");
    }

    /**
     * 修改分户计量拓扑配置
     *
     * @param athenaBesHouseholdConfig 分户计量拓扑配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateAthenaBesHouseholdConfig(AthenaBesHouseholdConfig athenaBesHouseholdConfig) {
        if (athenaBesHouseholdConfig.getHouseholdId()==null){
            return AjaxResult.error("分户ID不允许为空！");
        }
        if (athenaBesHouseholdConfigMapper.selectAthenaBesHouseholdConfigRepeat(athenaBesHouseholdConfig) != null) {
            return AjaxResult.error("分户名称不允许重复！");
        }
        athenaBesHouseholdConfig.setUpdateTime(DateUtils.getNowDate());
        if (athenaBesHouseholdConfigMapper.updateAthenaBesHouseholdConfig(athenaBesHouseholdConfig)) {
            addCache(athenaBesHouseholdConfig);
            return AjaxResult.success("修改成功！", athenaBesHouseholdConfig);
        } else {
            return AjaxResult.error("修改失败！");
        }

    }


    /**
     * 批量删除分户计量拓扑配置
     *
     * @param ids 需要删除的分户计量拓扑配置主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteAthenaBesHouseholdConfigByIds(Long[] ids) {
        if (StringUtils.isEmpty(ids)){
            return AjaxResult.error("分户ID不能为空！");
        }
        //判断分户下面是否有子组
        Map<String, AthenaBesHouseholdConfig> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdConfig);
        List<Long> list = Arrays.asList(ids);
        for (AthenaBesHouseholdConfig item : map.values()) {
            if (list.contains(item.getParentId()) && !list.contains(item.getHouseholdId())) {
                return AjaxResult.error("请先删除当前组下面的子组!");
            }
        }

        //先删除分户下面关联的支路
        athenaBesHouseholdBranchLinkMapper.deleteAthenaBesHouseholdBranchLinkByIds(ids);
        //再删除分户
        athenaBesHouseholdConfigMapper.deleteAthenaBesHouseholdConfigByIds(ids);
        //判断删除时  是否存在报警策略
        Long[] array = alarmTacticsMapper.selectAlarmTacticsByDeviceIds(ids);
        //删除报警策略
        if (array.length > 0) {
            tacticsAlarmNotifierLinkMapper.deleteByAlarmtacticsIdsBoolean(array);
            alarmTacticsMapper.deleteAlarmTacticsByIds(array);
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, array);
        }

        //先删除关联表缓存  再删除支路缓存
        Map<String, AthenaBesHouseholdBranchLink> householdBranchLinkMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink);
        //要删除的关联表
        householdBranchLinkMap.values().forEach(item -> {
            if (list.contains(item.getHouseholdId())) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink, item.getId());
            }
        });

        //删除缓存
        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdConfig, ids);

        return AjaxResult.success("删除成功!");
    }

    /**
     * 删除分户计量拓扑配置信息
     *
     * @param id 分户计量拓扑配置主键
     * @return 结果
     */
    @Override
    public int deleteAthenaBesHouseholdConfigById(Long id) {
        return athenaBesHouseholdConfigMapper.deleteAthenaBesHouseholdConfigById(id);
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param list 分户拓扑配置列表
     * @return 下拉树结构列表
     */

    @Override
    public List<TreeSelect> buildTreeSelect(List<AthenaBesHouseholdConfig> list) {
        List<AthenaBesHouseholdConfig> branchTrees = buildTree(list);
        return branchTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param list 分户拓扑配置列表
     * @return 树结构列表
     */
    @Override
    public List<AthenaBesHouseholdConfig> buildTree(List<AthenaBesHouseholdConfig> list) {
        List<AthenaBesHouseholdConfig> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<Long>();
        for (AthenaBesHouseholdConfig item : list) {
            tempList.add(item.getHouseholdId());
        }
        for (AthenaBesHouseholdConfig item : list) {
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
    private void recursionFn(List<AthenaBesHouseholdConfig> list, AthenaBesHouseholdConfig t) {
        // 得到子节点列表
        List<AthenaBesHouseholdConfig> childList = getChildList(list, t);
        t.setChildren(childList);
        for (AthenaBesHouseholdConfig tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }


    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<AthenaBesHouseholdConfig> list, AthenaBesHouseholdConfig t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 得到子节点列表
     */
    private List<AthenaBesHouseholdConfig> getChildList(List<AthenaBesHouseholdConfig> list, AthenaBesHouseholdConfig t) {
        List<AthenaBesHouseholdConfig> tlist = new ArrayList<>();
        Iterator<AthenaBesHouseholdConfig> it = list.iterator();
        while (it.hasNext()) {
            AthenaBesHouseholdConfig n = it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getHouseholdId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 包含支路
     *
     * @param branchVo 支路数据
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAthenaBranchConfig(BranchVo branchVo) {
        delCache = new ArrayList<>();
        //分户支路关联记录
        Map<String, AthenaBesHouseholdBranchLink> athenaBesHouseholdBranchLinks = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink);
        /**级联分户节点ID集合**/
        List<Long> cascadedIds = new ArrayList<>();
        /**之前支路ID集合**/
        List<Long> beginBranchIds = new ArrayList<>();
        /**新增支路ID集合**/
        List<Long> branchIds = branchVo.getBranchList();
        /**先递归查出所有 是否级联为 是 的父节点**/
        recursionFn(athenaBesHouseholdConfigMapper.selectAthenaBesHouseholdConfigListFather(branchVo.getParkCode(), branchVo.getEnergyCode()), branchVo.getFatherId(), cascadedIds);
        //如果数据为空  说明还未添加关联信息  直接添加即可
        if (athenaBesHouseholdBranchLinks != null && !athenaBesHouseholdBranchLinks.values().isEmpty()) {

            /**先查出来之前该分支下的旧数据   为删除父节点之前的重复数据做备用 及判断操作类型**/
            athenaBesHouseholdBranchLinks.values().forEach(item -> {
                if (branchVo.getHouseholdId().equals(item.getHouseholdId())) {
                    beginBranchIds.add(item.getBranchId());
                }
            });

            /**之前有数据 现在没数据 等式成立 只做删除操作**/
            if (beginBranchIds.size() > 0 && branchIds.size() == 0) {
                /**先通过当前分支ID 删除关联表**/
                athenaBesHouseholdBranchLinkMapper.deleteAthenaBesHouseholdBranchLinkByHouseholdId(branchVo.getHouseholdId());
                //记录删除的缓存
                athenaBesHouseholdBranchLinks.values().forEach(item -> {
                    if (item.getHouseholdId().equals(branchVo.getHouseholdId())) {
                        delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink + "," + item.getId());
                    }
                });

                /**删除父级数据**/
                if (cascadedIds.size() > 0) {
                    athenaBesHouseholdBranchLinkMapper.deleteAthenaBesHouseholdBranchLinkBranchByHouseholdId(cascadedIds, beginBranchIds);
                    for (Long cascadedId : cascadedIds) {
                        for (Long beginBranchId : beginBranchIds) {
                            //记录删除的缓存
                            athenaBesHouseholdBranchLinks.values().forEach(item -> {
                                if (item.getHouseholdId().equals(cascadedId) && item.getBranchId().equals(beginBranchId)) {
                                    delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink + "," + item.getId());
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


            /**之前支路ID集合为空 说明该节点之前没有关联支路 可以直接添加**/
            else if (beginBranchIds.size() == 0 && branchIds.size() > 0) {
                /**通过父节点ID及支路ID 删除父节点关联表重复数据  防止新增子节点关联支路时  导致父节点添加重复支路**/
                if (cascadedIds.size() > 0) {
                    athenaBesHouseholdBranchLinkMapper.deleteAthenaBesHouseholdBranchLinkBranchByHouseholdId(cascadedIds, branchIds);
                    for (Long cascadedId : cascadedIds) {
                        for (Long branchId : branchIds) {
                            //记录删除的缓存
                            athenaBesHouseholdBranchLinks.values().forEach(item -> {
                                if (item.getHouseholdId().equals(cascadedId) && item.getBranchId().equals(branchId)) {
                                    delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink + "," + item.getId());
                                }
                            });
                        }
                    }
                }
            }


            /**之前支路集合和支路集合都不为空时**/
            else if (beginBranchIds.size() > 0 && branchIds.size() > 0) {
                /**先删除旧父级数据**/
                if (cascadedIds.size() > 0) {
                    /**将两个集合 合并 及去重**/
                    List<Long> collect = Stream.of(beginBranchIds, branchIds)
                            .flatMap(Collection::stream)
                            .distinct()
                            .collect(Collectors.toList());
                    /**先删除旧数据**/
                    athenaBesHouseholdBranchLinkMapper.deleteAthenaBesHouseholdBranchLinkBranchByHouseholdId(cascadedIds, collect);
                    for (Long cascadedId : cascadedIds) {
                        for (Long branchId : collect) {
                            //记录删除的缓存
                            athenaBesHouseholdBranchLinks.values().forEach(item -> {
                                if (item.getHouseholdId().equals(cascadedId) && item.getBranchId().equals(branchId)) {
                                    delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink + "," + item.getId());
                                }
                            });
                        }
                    }
                }
                /**再通过当前分支ID 删除关联表**/
                athenaBesHouseholdBranchLinkMapper.deleteAthenaBesHouseholdBranchLinkByHouseholdId(branchVo.getHouseholdId());
                //记录删除的缓存
                athenaBesHouseholdBranchLinks.values().forEach(item -> {
                    if (item.getHouseholdId().equals(branchVo.getHouseholdId())) {
                        delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink + "," + item.getId());
                    }
                });

            }

            /**两个集合都为空 直接return**/
            else {
                return true;
            }

        }
        else if (branchIds.size() <= 0) {
            return true;
        }

        /**将当前节点插入级联节点集合中**/
        cascadedIds.add(branchVo.getHouseholdId());
        /**根据ids 通双重循环 进行批量新增**/
        if (!batchData(cascadedIds, branchVo)) {
            return false;
        }
        return true;
    }

    /**
     * 批处理数据
     *
     * @param cascadedIds 级联ids
     * @param branchVo    支路数据
     */
    private Boolean batchData(List<Long> cascadedIds, BranchVo branchVo) {
        //批量新增关联表
        List<AthenaBesHouseholdBranchLink> athenaBesHouseholdBranchLinks = new ArrayList<>();
        //双重循环 添加所需要新增的支路支路关联列表
        cascadedIds.forEach(id -> {
            branchVo.getBranchList().forEach(item -> {
                AthenaBesHouseholdBranchLink athenaBesHouseholdBranchLink = new AthenaBesHouseholdBranchLink(item, id);
                athenaBesHouseholdBranchLinks.add(athenaBesHouseholdBranchLink);
            });
        });
        if (athenaBesHouseholdBranchLinkMapper.batchInsertAthenaHouseholdBranchLink(athenaBesHouseholdBranchLinks)) {
            if (!delCache.isEmpty()) {
                //先删除之前旧数据
                delCache.forEach(item -> {
                    String[] array = item.split(",");
                    redisCache.delCacheMapValue(array[0], array[1]);
                });
            }
            //添加缓存
            addHouseholdBranchCache(athenaBesHouseholdBranchLinks);
            return true;
        }
        return false;
    }

    /**
     * 添加分户支路关系缓存
     *
     * @Author qindehua
     * @Date 2022/10/25
     **/
    private void addHouseholdBranchCache(List<AthenaBesHouseholdBranchLink> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        list.forEach(item -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink, item.getId(), item);
        });
    }

    /**
     * 递归列表
     *
     * @param list      列表
     * @param fatherId  当前节点的  父ID
     * @param fatherIds 父id集合
     */
    private void recursionFn(List<AthenaBesHouseholdConfig> list, Long fatherId, List<Long> fatherIds) {
        // 得到父节点ID
        for (AthenaBesHouseholdConfig item : list) {
            if (item.getHouseholdId().equals(fatherId)) {
                //判断父节点是否为级联  是 则添加
                if (item.getCascaded().equals(Constants.CASCADED)) {
                    fatherIds.add(item.getHouseholdId());
                }
                recursionFn(list, item.getParentId(), fatherIds);
            }
        }
    }

}
