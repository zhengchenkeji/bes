package com.zc.efounder.JEnterprise.service.energyInfo.impl;

import java.util.List;

import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.mapper.energyInfo.AthenaBesHouseholdBranchLinkMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.efounder.JEnterprise.service.energyInfo.AthenaBesHouseholdBranchLinkService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 分户支路关联Service业务层处理
 *
 * @author qindehua
 * @date 2022-09-19
 */
@Service
public class AthenaBesHouseholdBranchLinkServiceImpl implements AthenaBesHouseholdBranchLinkService
{
    @Autowired
    private AthenaBesHouseholdBranchLinkMapper athenaBesHouseholdBranchLinkMapper;
    @Resource
    private RedisCache redisCache;


    @PostConstruct
    public void init()
    {
        /**
         * 添加数据到 redis 缓存
         */
        addCache();
    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addCache()
    {
        // 获取全部设备列表数据
        List<AthenaBesHouseholdBranchLink> linkList = selectAthenaBesHouseholdBranchLinkList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink);

        if (linkList == null || linkList.isEmpty())
        {
            return;
        }

        // 添加 redis 缓存数据
        linkList.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink, val.getId(), val);
        });
    }

    /**
     * 查询分户支路关联
     *
     * @param id 分户支路关联主键
     * @return 分户支路关联
     */
    @Override
    public AthenaBesHouseholdBranchLink selectAthenaBesHouseholdBranchLinkById(Long id)
    {
        return athenaBesHouseholdBranchLinkMapper.selectAthenaBesHouseholdBranchLinkById(id);
    }
    /**
     * 查询分户下支路列表
     *
     * @param householdId 分户ID
     * @return 支路列表
     */
    @Override
    public List<AthenaBranchConfig> selectAthenaBesBranchListById(Long householdId) {
        return athenaBesHouseholdBranchLinkMapper.selectAthenaBesBranchListById(householdId);
    }

    /**
     * 查询分户支路关联列表
     *
     * @param athenaBesHouseholdBranchLink 分户支路关联
     * @return 分户支路关联
     */
    @Override
    public List<AthenaBesHouseholdBranchLink> selectAthenaBesHouseholdBranchLinkList(AthenaBesHouseholdBranchLink athenaBesHouseholdBranchLink)
    {
        return athenaBesHouseholdBranchLinkMapper.selectAthenaBesHouseholdBranchLinkList(athenaBesHouseholdBranchLink);
    }

    /**
     * 新增分户支路关联
     *
     * @param athenaBesHouseholdBranchLink 分户支路关联
     * @return 结果
     */
    @Override
    public int insertAthenaBesHouseholdBranchLink(AthenaBesHouseholdBranchLink athenaBesHouseholdBranchLink)
    {
        return athenaBesHouseholdBranchLinkMapper.insertAthenaBesHouseholdBranchLink(athenaBesHouseholdBranchLink);
    }

    /**
     * 修改分户支路关联
     *
     * @param athenaBesHouseholdBranchLink 分户支路关联
     * @return 结果
     */
    @Override
    public int updateAthenaBesHouseholdBranchLink(AthenaBesHouseholdBranchLink athenaBesHouseholdBranchLink)
    {
        return athenaBesHouseholdBranchLinkMapper.updateAthenaBesHouseholdBranchLink(athenaBesHouseholdBranchLink);
    }

    /**
     * 批量删除分户支路关联
     *
     * @param ids 需要删除的分户支路关联主键
     * @return 结果
     */
    @Override
    public int deleteAthenaBesHouseholdBranchLinkByIds(Long[] ids)
    {
        return athenaBesHouseholdBranchLinkMapper.deleteAthenaBesHouseholdBranchLinkByIds(ids);
    }

    /**
     * 删除分户支路关联信息
     *
     * @param id 分户支路关联主键
     * @return 结果
     */
    @Override
    public int deleteAthenaBesHouseholdBranchLinkById(Long id)
    {
        return athenaBesHouseholdBranchLinkMapper.deleteAthenaBesHouseholdBranchLinkById(id);
    }
}
