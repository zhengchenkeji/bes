package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @Description 分项与支路关系缓存方法
 *
 * @author liuwenge
 * @date 2022/10/20 16:16
 */
@Component
public class SubitemBranchLinkCache {
    @Resource
    private RedisCache redisCache;

    /**
     * @Description 根据支路id查询所有的分项
     *
     * @author liuwenge
     * @date 2022/10/20 16:19
     * @param branchId
     * @return java.util.List<com.ruoyi.energyInfo.subitemConfig.domain.SubitemBranchLink>
     */
    public List<SubitemBranchLink> getCacheByBranchId(String branchId) {

        if (branchId == null)
        {
            return null;
        }

        Map<String, SubitemBranchLink> linkList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_SubitemBranchLink);
        if (linkList == null || linkList.size() == 0) {
            return null;
        }


        List<SubitemBranchLink> subitemBranchLinks = new ArrayList<>();

        for (SubitemBranchLink subitemBranchLink : linkList.values()) {


            if (branchId.equals(subitemBranchLink.getBranchId().toString()))
            {
                subitemBranchLinks.add(subitemBranchLink);
            }
        }

        // 排序根据id
        subitemBranchLinks.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getId().toString())));

        return subitemBranchLinks;
    }
}
