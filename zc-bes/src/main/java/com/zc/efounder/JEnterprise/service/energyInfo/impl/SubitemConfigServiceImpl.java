package com.zc.efounder.JEnterprise.service.energyInfo.impl;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SubitemConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUIDUtil;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemConfTemplate;
import com.zc.efounder.JEnterprise.domain.energyInfo.vo.SubitemBranchVo;
import com.zc.efounder.JEnterprise.mapper.energyInfo.SubitemBranchLinkMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.SubitemConfTemplateMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.SubitemConfigMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsAlarmNotifierLinkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsMapper;
import com.zc.efounder.JEnterprise.service.energyInfo.ISubitemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 分项拓扑配置Service业务层处理
 *
 * @author qindehua
 * @date 2022-09-20
 */
@Service
public class SubitemConfigServiceImpl implements ISubitemConfigService {
    @Resource
    private SubitemConfigMapper subitemConfigMapper;
    @Resource
    private SubitemBranchLinkMapper subitemBranchLinkMapper;
    @Resource
    private SubitemConfTemplateMapper subitemConfTemplateMapper;
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
        List<SubitemConfig> list = selectSubitemConfigList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig);

        if (list == null || list.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        list.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig, val.getSubitemId(), val);
        });
    }

    /**
     * 添加缓存
     *
     * @Author qindehua
     * @Date 2022/10/24
     **/
    private void addCache(SubitemConfig subitemConfig) {
        redisCache.setCacheMapValue(
                RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig, subitemConfig.getSubitemId(), subitemConfig);
    }

    /**
     * 查询分项拓扑配置
     *
     * @param subitemId 分项拓扑配置主键
     * @return 分项拓扑配置
     */
    @Override
    public SubitemConfig selectSubitemConfigBySubitemId(String subitemId) {
        return subitemConfigMapper.selectSubitemConfigBySubitemId(subitemId);
    }

    /**
     * 查询分项拓扑配置列表
     *
     * @param subitemConfig 分项拓扑配置
     * @return 分项拓扑配置
     */
    @Override
    public List<SubitemConfig> selectSubitemConfigList(SubitemConfig subitemConfig) {
        return subitemConfigMapper.selectSubitemConfigList(subitemConfig);
    }

    /**
     * 新增分项拓扑配置
     *
     * @param subitemConfig 分项拓扑配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertSubitemConfig(SubitemConfig subitemConfig) {
        Map<String,SubitemConfig> map=redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig);
        for (SubitemConfig value : map.values()) {
            if (subitemConfig.getParkCode().equals(value.getParkCode()) &&
                    subitemConfig.getEnergyCode().equals(value.getEnergyCode()) &&
                    subitemConfig.getBuildingId().equals(value.getBuildingId())){
                if (subitemConfig.getSubitemName().equals(value.getSubitemName()) ||
                        subitemConfig.getBuildingEnergyCode().equals(value.getBuildingEnergyCode())){
                    return AjaxResult.error("分项名称及建筑能耗代码不允许重复！");
                }
            }
        }

        subitemConfig.setSubitemId(UUIDUtil.getRandom32BeginTimePK());
        subitemConfig.setCreateTime(DateUtils.getNowDate());
        String subitemCode = null;
        Integer code = subitemConfigMapper.selectSubitemConfigCodeNum(subitemConfig.getParkCode(), subitemConfig.getEnergyCode()
                , (subitemConfig.getParkCode() + subitemConfig.getEnergyCode()).length());
        //当前区域、能源类型下  没有数据时直接添加
        if (code == null) {
            subitemCode = subitemConfig.getParkCode() + subitemConfig.getEnergyCode() + 1;
        } else {
            //当前区域、能源类型下  有数据时 选出最大值  进行num+1
            subitemCode = subitemConfig.getParkCode() + subitemConfig.getEnergyCode() + (code + 1);
        }
        subitemConfig.setSubitemCode(subitemCode);
        if (subitemConfigMapper.insertSubitemConfig(subitemConfig)) {
            addCache(subitemConfig);
            return AjaxResult.success("新增成功！", subitemConfig);
        } else {
            return AjaxResult.error("新增失败！");
        }
    }

    /**
     * 批量插入
     *
     * @param subitemConfig 分项配置
     * @return {@code Boolean }
     * @Author qindehua
     * @Date 2022/11/10
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertSubitemBatch(SubitemConfig subitemConfig) {
        if (StringUtils.isEmpty(subitemConfig.getParkCode()) ||
                StringUtils.isEmpty(subitemConfig.getEnergyCode()) ||
                subitemConfig.getBuildingId()==null){
            return AjaxResult.success("参数不能为空！");
        }
        //加载模板数据
        List<SubitemConfTemplate> templates = subitemConfTemplateMapper.selectSubitemConfTemplateList(null);

        String subitemCode = null;
        Integer code = null;
        Integer num = 1;
        String energyCode = "";
        //新增数据
        List<SubitemConfig> data = new ArrayList<>();
        //查询分项支路关联数据
        Map<String, SubitemBranchLink> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink);
        //判断是否有分项已关联支路
        for (SubitemConfTemplate template : templates) {
            for (SubitemBranchLink value : map.values()) {
                if (template.getSubitemCode().equals(value.getSubitemId())) {
                    return AjaxResult.error("删除失败，已有分项关联支路");
                }
            }
        }
        //先查询当前园区下 当前能源类型 当前建筑下分项的数据
        List<SubitemConfig> subitemConfigs = subitemConfigMapper.selectSubitemConfigList(subitemConfig);
        //删除时的id数组
        String[] array = new String[subitemConfigs.size()];
        for (int i = 0; i < subitemConfigs.size(); i++) {
            array[i] = subitemConfigs.get(i).getSubitemId();
        }
        if (array.length > 0) {
            //先删除当前园区下 当前能源类型 当前建筑下分项
            subitemConfigMapper.deleteSubitemConfigBySubitemIds(array);
        }
        //替换表中的uuid
        Map<String, String> mapKey = new HashMap<>();
        for (SubitemConfTemplate template : templates) {
            String uuid = UUIDUtil.getRandom32BeginTimePK();
            mapKey.put(template.getSubitemCode(), uuid);
            template.setSubitemCode(uuid);
        }
        //将父节点的Key 替换成最新的key
        for (SubitemConfTemplate template : templates) {
            if (!"0".equals(template.getSubitemFatherCode())) {
                template.setSubitemFatherCode(mapKey.get(template.getSubitemFatherCode()));
            }
        }

        //再进行新增操作
        for (SubitemConfTemplate item : templates) {
            //只增加当前能源下信息
            if (item.getEnergyCode().equals(subitemConfig.getEnergyCode())) {
                if (!energyCode.equals(item.getEnergyCode())) {
                    num = 1;
                    code = subitemConfigMapper.selectSubitemConfigCodeNum(subitemConfig.getParkCode(), item.getEnergyCode()
                            , (subitemConfig.getParkCode() + item.getEnergyCode()).length());
                    if (code != null) {
                        num = num + code;
                    }
                    subitemCode = subitemConfig.getParkCode() + item.getEnergyCode();
                }
                SubitemConfig sub = new SubitemConfig();
                sub.setSubitemId(item.getSubitemCode());
                sub.setSubitemName(item.getSubitemName());
                sub.setSubitemCode(subitemCode + num);
                sub.setParkCode(subitemConfig.getParkCode());
                sub.setBuildingId(subitemConfig.getBuildingId());
                sub.setEnergyCode(item.getEnergyCode());
                sub.setParentId(item.getSubitemFatherCode());
                sub.setBuildingEnergyCode(item.getBuildingEnergyCode());
                sub.setCreateTime(DateUtils.getNowDate());
                sub.setCascaded(Constants.CASCADED);//级联默认为是

                data.add(sub);
                num++;
                energyCode = sub.getEnergyCode();
            }
        }
        if (subitemConfigMapper.insertSubitemBatch(data)) {

            if (array.length > 0) {
                // 清除 redis 缓存数据
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig, array);
            }
            // 添加 redis 缓存数据
            data.forEach(val -> {
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig, val.getSubitemId(), val);
            });
            //更新缓存
            return AjaxResult.success("新增成功！");
        } else {
            return AjaxResult.error("新增失败！");
        }

    }


    /**
     * 修改分项拓扑配置
     *
     * @param subitemConfig 分项拓扑配置
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateSubitemConfig(SubitemConfig subitemConfig) {
        if (StringUtils.isEmpty(subitemConfig.getSubitemId())){
            return AjaxResult.error("参数不能为空！");
        }
        Map<String,SubitemConfig> map=redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig);
        for (SubitemConfig value : map.values()) {
            if (subitemConfig.getParkCode().equals(value.getParkCode()) &&
                    subitemConfig.getEnergyCode().equals(value.getEnergyCode()) &&
                    subitemConfig.getBuildingId().equals(value.getBuildingId()) &&
                    !subitemConfig.getSubitemId().equals(value.getSubitemId())){
                if (subitemConfig.getSubitemName().equals(value.getSubitemName()) ||
                        subitemConfig.getBuildingEnergyCode().equals(value.getBuildingEnergyCode())){
                    return AjaxResult.error("分项名称及建筑能耗代码不允许重复！");
                }
            }
        }

        subitemConfig.setUpdateTime(DateUtils.getNowDate());

        if (subitemConfigMapper.updateSubitemConfig(subitemConfig)) {
            addCache(subitemConfig);
            return AjaxResult.success("修改成功！", subitemConfig);
        } else {
            return AjaxResult.error("修改失败！");

        }

    }

    /**
     * 批量删除分项拓扑配置
     *
     * @param subitemIds 需要删除的分项拓扑配置主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteSubitemConfigBySubitemIds(String[] subitemIds) {
        if (StringUtils.isEmpty(subitemIds)){
            return AjaxResult.error("分项ID不能为空！");
        }
        //判断分户下面是否有子组
        Map<String, SubitemConfig> map = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig);
        List<String> list = Arrays.asList(subitemIds);

        for (SubitemConfig item : map.values()) {
            if (list.contains(item.getParentId()) && !list.contains(item.getSubitemId())) {
                return AjaxResult.error("请先删除当前组下面的子组!");
            }
        }

        //先删除分项下面关联的支路
        subitemBranchLinkMapper.deleteSubitemBranchLinkBySubitemIds(subitemIds);
        //再删除分项
        subitemConfigMapper.deleteSubitemConfigBySubitemIds(subitemIds);
        //判断删除时  是否存在报警策略
        Long[] array = alarmTacticsMapper.selectAlarmTacticsByDeviceIds(subitemIds);
        //删除报警策略
        if (array.length > 0) {
            tacticsAlarmNotifierLinkMapper.deleteByAlarmtacticsIdsBoolean(array);
            alarmTacticsMapper.deleteAlarmTacticsByIds(array);
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, array);
        }
        //先删除关联表缓存  再删除支路缓存
        Map<String, SubitemBranchLink> branchLinkMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink);
        //要删除的关联表
        branchLinkMap.values().forEach(item -> {
            if (list.contains(item.getSubitemId())) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink, item.getId());
            }
        });
        //删除缓存
        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemConfig, subitemIds);
        return AjaxResult.success("删除成功!");

    }

    /**
     * 删除分项拓扑配置信息
     *
     * @param subitemId 分项拓扑配置主键
     * @return 结果
     */
    @Override
    public int deleteSubitemConfigBySubitemId(String subitemId) {
        return subitemConfigMapper.deleteSubitemConfigBySubitemId(subitemId);
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param list 分项拓扑配置列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildTreeSelect(List<SubitemConfig> list) {
        List<SubitemConfig> branchTrees = buildTree(list);
        return branchTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 构建前端所需要树结构
     *
     * @param list 分项拓扑配置列表
     * @return 树结构列表
     */
    @Override
    public List<SubitemConfig> buildTree(List<SubitemConfig> list) {
        List<SubitemConfig> returnList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        for (SubitemConfig item : list) {
            tempList.add(item.getSubitemId());
        }
        for (SubitemConfig item : list) {
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
    private void recursionFn(List<SubitemConfig> list, SubitemConfig t) {
        // 得到子节点列表
        List<SubitemConfig> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SubitemConfig tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }


    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SubitemConfig> list, SubitemConfig t) {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 得到子节点列表
     */
    private List<SubitemConfig> getChildList(List<SubitemConfig> list, SubitemConfig t) {
        List<SubitemConfig> tlist = new ArrayList<>();
        Iterator<SubitemConfig> it = list.iterator();
        while (it.hasNext()) {
            SubitemConfig n = it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().equals(t.getSubitemId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 删除时查看 当前节点下是否有子节点
     *
     * @param subitemIds 分项拓扑配置主键集合
     * @return 分项拓扑配置
     */
    @Override
    public SubitemConfig selectSubitemConfigSun(String[] subitemIds) {
        return subitemConfigMapper.selectSubitemConfigSun(subitemIds);
    }


    /**
     * 查询分项计量拓扑配置列表及下面子节点
     *
     * @param subitemConfig 分项计量拓扑配置
     * @param code          查询标识
     * @return 分项计量拓扑配置集合
     */
    @Override
    public List<SubitemConfig> selectSubitemConfigListSun(SubitemConfig subitemConfig, String code) {
        return subitemConfigMapper.selectSubitemConfigListSun(subitemConfig, code);
    }

    /**
     * 包含支路
     *
     * @param subitemBranchVo 支路数据
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAthenaBranchConfig(SubitemBranchVo subitemBranchVo) {
        delCache = new ArrayList<>();
        //支路分项关联缓存
        Map<String, SubitemBranchLink> subitemBranchLinks = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink);
        /**级联分项节点ID集合**/
        List<String> cascadedIds = new ArrayList<>();
        /**之前支路ID集合**/
        List<Long> beginBranchIds = new ArrayList<>();
        /**新增支路ID集合**/
        List<Long> branchIds = subitemBranchVo.getBranchList();
        /**先递归查出所有 是否级联为 是 的父节点**/
        recursionFn(subitemConfigMapper.selectSubitemConfigListFather(subitemBranchVo.getParkCode(), subitemBranchVo.getEnergyCode()), subitemBranchVo.getFatherId(), cascadedIds);

        //如果数据为空  说明还未添加关联信息  直接添加即可
        if (subitemBranchLinks != null && !subitemBranchLinks.values().isEmpty()) {

            /**先查出来之前该分支下的旧数据   为删除父节点之前的重复数据做备用 及判断操作类型**/
            subitemBranchLinks.values().forEach(item -> {
                if (subitemBranchVo.getSubitemId().equals(item.getSubitemId())) {
                    beginBranchIds.add(item.getBranchId());
                }
            });

            /**之前有数据 现在没数据 等式成立 只做删除操作**/
            if (beginBranchIds.size() > 0 && branchIds.size() == 0) {
                /**先通过当前分支ID 删除关联表**/
                subitemBranchLinkMapper.deleteSubitemBranchLinkBySubitemId(subitemBranchVo.getSubitemId());
                //记录删除的缓存
                subitemBranchLinks.values().forEach(item -> {
                    if (item.getSubitemId().equals(subitemBranchVo.getSubitemId())) {
                        delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink + "," + item.getId());
                    }
                });

                /**删除父级数据**/
                if (cascadedIds.size() > 0) {
                    subitemBranchLinkMapper.deleteSubitemBranchLinkByCascadedIds(cascadedIds, beginBranchIds);
                    for (String cascadedId : cascadedIds) {
                        for (Long beginBranchId : beginBranchIds) {
                            //记录删除的缓存
                            subitemBranchLinks.values().forEach(item -> {
                                if (item.getSubitemId().equals(cascadedId) && item.getBranchId().equals(beginBranchId)) {
                                    delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink + "," + item.getId());
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
                    subitemBranchLinkMapper.deleteSubitemBranchLinkByCascadedIds(cascadedIds, branchIds);
                    for (String cascadedId : cascadedIds) {
                        for (Long branchId : branchIds) {
                            //记录删除的缓存
                            subitemBranchLinks.values().forEach(item -> {
                                if (item.getSubitemId().equals(cascadedId) && item.getBranchId().equals(branchId)) {
                                    delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink + "," + item.getId());
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
                    subitemBranchLinkMapper.deleteSubitemBranchLinkByCascadedIds(cascadedIds, collect);
                    for (String cascadedId : cascadedIds) {
                        for (Long branchId : collect) {
                            //记录删除的缓存
                            subitemBranchLinks.values().forEach(item -> {
                                if (item.getSubitemId().equals(cascadedId) && item.getBranchId().equals(branchId)) {
                                    delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink + "," + item.getId());
                                }
                            });
                        }
                    }
                }
                /**再通过当前分支ID 删除关联表**/
                subitemBranchLinkMapper.deleteSubitemBranchLinkBySubitemId(subitemBranchVo.getSubitemId());
                //记录删除的缓存
                subitemBranchLinks.values().forEach(item -> {
                    if (item.getSubitemId().equals(subitemBranchVo.getSubitemId())) {
                        delCache.add(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink + "," + item.getId());
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
        cascadedIds.add(subitemBranchVo.getSubitemId());
        /**根据ids 通双重循环 进行批量新增**/
        if (!batchData(cascadedIds, subitemBranchVo)) {
            return false;
        }
        return true;
    }

    /**
     * 批处理数据
     *
     * @param cascadedIds     级联ids
     * @param subitemBranchVo 支路数据
     */
    private Boolean batchData(List<String> cascadedIds, SubitemBranchVo subitemBranchVo) {
        //批量新增关联表
        List<SubitemBranchLink> subitemBranchLinks = new ArrayList<>();
        //双重循环 添加所需要新增的分项支路关联列表
        cascadedIds.forEach(id -> {
            subitemBranchVo.getBranchList().forEach(item -> {
                SubitemBranchLink subitemBranchLink = new SubitemBranchLink(item, id);
                subitemBranchLinks.add(subitemBranchLink);
            });
        });
        if (subitemBranchLinkMapper.batchInsertSubitemBranchLink(subitemBranchLinks)) {
            if (!delCache.isEmpty()) {
                //先删除之前旧数据
                delCache.forEach(item -> {
                    String[] array = item.split(",");
                    redisCache.delCacheMapValue(array[0], array[1]);
                });
            }
            //添加缓存
            addSubitemBranchCache(subitemBranchLinks);
            return true;
        }
        return false;
    }

    /**
     * 添加分项支路关系缓存
     *
     * @Author qindehua
     * @Date 2022/10/25
     **/
    private void addSubitemBranchCache(List<SubitemBranchLink> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        list.forEach(item -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink, item.getId(), item);
        });
    }

    /**
     * 递归列表
     *
     * @param list      列表
     * @param fatherId  当前节点的  父ID
     * @param fatherIds 父id集合
     */
    private void recursionFn(List<SubitemConfig> list, String fatherId, List<String> fatherIds) {
        // 得到父节点ID
        for (SubitemConfig item : list) {
            if (item.getSubitemId().equals(fatherId)) {
                //判断父节点是否为级联  是 则添加
                if (item.getCascaded().equals(Constants.CASCADED)) {
                    fatherIds.add(item.getSubitemId());
                }
                recursionFn(list, item.getParentId(), fatherIds);
            }
        }
    }
}
