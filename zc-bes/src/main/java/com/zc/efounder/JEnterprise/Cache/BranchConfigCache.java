package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.common.constant.RedisKeyConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @Description 支路缓存方法
 *
 * @author liuwenge
 * @date 2022/11/16 8:49
 */
@Component
public class BranchConfigCache {
    @Resource
    private RedisCache redisCache;


    /**
     * @Description 根据支路id查询子级支路
     *
     * @author liuwenge
     * @date 2022/11/16 8:54
     * @param branchId 父级支路id
     * @return java.util.List<com.ruoyi.common.core.domain.entity.AthenaBranchConfig>
     */
    public List<AthenaBranchConfig> getBranchByParentId(Integer branchId) {

        if (branchId == null)
        {
            return null;
        }

        Map<String, AthenaBranchConfig> branchConfigList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig);
        if (branchConfigList == null || branchConfigList.size() == 0) {
            return null;
        }


        List<AthenaBranchConfig> branchConfigs = new ArrayList<>();

        for (AthenaBranchConfig athenaBranchConfig : branchConfigList.values()) {


            if (branchId.equals(athenaBranchConfig.getParentId().intValue()))
            {
                branchConfigs.add(athenaBranchConfig);
            }
        }

        // 排序根据电能编号
        branchConfigs.sort(Comparator.comparingInt(o -> o.getBranchId().intValue()));

        return branchConfigs;
    }
}
