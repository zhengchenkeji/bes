package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @Description 分户与支路关系缓存方法
 *
 * @author liuwenge
 * @date 2022/10/20 16:16
 */
@Component
public class HouseholdBranchLinkCache {
    @Resource
    private RedisCache redisCache;

    /**
     * @Description 根据支路id查询所有的分户
     *
     * @author liuwenge
     * @date 2022/10/20 16:19
     * @param branchId
     */
    public List<AthenaBesHouseholdBranchLink> getCacheByBranchId(String branchId) {

        if (branchId == null)
        {
            return null;
        }

        Map<String, AthenaBesHouseholdBranchLink> linkList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_HouseholdBranchLink);
        if (linkList == null || linkList.size() == 0) {
            return null;
        }


        List<AthenaBesHouseholdBranchLink> householdBranchLinks = new ArrayList<>();

        for (AthenaBesHouseholdBranchLink householdBranchLink : linkList.values()) {


            if (branchId.equals(householdBranchLink.getBranchId().toString()))
            {
                householdBranchLinks.add(householdBranchLink);
            }
        }

        // 排序根据id
        householdBranchLinks.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getId().toString())));

        return householdBranchLinks;
    }
}
