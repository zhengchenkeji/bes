package com.zc.efounder.JEnterprise.service.energyInfo.impl;

import java.util.List;

import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.service.energyInfo.ISubitemBranchLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.mapper.energyInfo.SubitemBranchLinkMapper;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 分项支路关联Service业务层处理
 *
 * @author qindehua
 * @date 2022-09-20
 */
@Service
public class SubitemBranchLinkServiceImpl implements ISubitemBranchLinkService
{
    @Autowired
    private SubitemBranchLinkMapper subitemBranchLinkMapper;

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
        List<SubitemBranchLink> linkList = selectSubitemBranchLinkList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink);

        if (linkList == null || linkList.isEmpty())
        {
            return;
        }

        // 添加 redis 缓存数据
        linkList.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink, val.getId(), val);
        });
    }

    /**
     * 查询分项支路关联
     *
     * @param id 分项支路关联主键
     * @return 分项支路关联
     */
    @Override
    public SubitemBranchLink selectSubitemBranchLinkById(Long id)
    {
        return subitemBranchLinkMapper.selectSubitemBranchLinkById(id);
    }

    /**
     * 查询分项支路关联列表
     *
     * @param subitemBranchLink 分项支路关联
     * @return 分项支路关联
     */
    @Override
    public List<SubitemBranchLink> selectSubitemBranchLinkList(SubitemBranchLink subitemBranchLink)
    {
        return subitemBranchLinkMapper.selectSubitemBranchLinkList(subitemBranchLink);
    }

    /**
     * 新增分项支路关联
     *
     * @param subitemBranchLink 分项支路关联
     * @return 结果
     */
    @Override
    public int insertSubitemBranchLink(SubitemBranchLink subitemBranchLink)
    {
        return subitemBranchLinkMapper.insertSubitemBranchLink(subitemBranchLink);
    }

    /**
     * 修改分项支路关联
     *
     * @param subitemBranchLink 分项支路关联
     * @return 结果
     */
    @Override
    public int updateSubitemBranchLink(SubitemBranchLink subitemBranchLink)
    {
        return subitemBranchLinkMapper.updateSubitemBranchLink(subitemBranchLink);
    }

    /**
     * 批量删除分项支路关联
     *
     * @param ids 需要删除的分项支路关联主键
     * @return 结果
     */
    @Override
    public int deleteSubitemBranchLinkByIds(Long[] ids)
    {
        return subitemBranchLinkMapper.deleteSubitemBranchLinkByIds(ids);
    }

    /**
     * 删除分项支路关联信息
     *
     * @param id 分项支路关联主键
     * @return 结果
     */
    @Override
    public int deleteSubitemBranchLinkById(Long id)
    {
        return subitemBranchLinkMapper.deleteSubitemBranchLinkById(id);
    }

    /**
     * 查询分项下支路列表
     *
     * @param subitemId 分项ID
     * @return 支路列表
     */
    @Override
    public List<AthenaBranchConfig> selectAthenaBesBranchListById(String subitemId) {
        return subitemBranchLinkMapper.selectAthenaBesBranchListById(subitemId);
    }
}
