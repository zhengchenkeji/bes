package com.zc.efounder.JEnterprise.service.energyAnalysis.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyAnalysis.dto.RankingDTO;
import com.zc.efounder.JEnterprise.domain.energyAnalysis.vo.RankingVO;
import com.zc.efounder.JEnterprise.mapper.energyAnalysis.RankingMapper;
import com.zc.efounder.JEnterprise.service.energyAnalysis.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 能耗排名Service
 *
 * @author qindehua
 */
@Service
public class RankingServiceImpl implements RankingService {

    @Autowired
    private RankingMapper rankingMapper;


    /**
     * 查询数据通过支路ids
     *
     * @param rankingVO 排名VO
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/11/28
     **/
    @Override
    public AjaxResult selectDataByBranchIds(RankingVO rankingVO) {
        //能耗数据值
        List<RankingDTO>  branchIds=rankingMapper.selectDataByBranchIds(rankingVO.getIds(),rankingVO.getStartTime(),rankingVO.getEndTime());
        Map<Long,Float> map=new HashMap<>();
        for (RankingDTO branchId : branchIds) {
            map.put(branchId.getId(),branchId.getValue());
        }
        //数据值
        List<RankingDTO> list=new ArrayList<>();
        for (Long branchId : rankingVO.getIds()) {
            float value=0;
            if (map!=null && map.get(branchId)!=null){
                value=map.get(branchId);
            }
            RankingDTO rankingDTO=new RankingDTO(branchId,value);
            list.add(rankingDTO);
        }
        list.sort(Comparator.comparing(RankingDTO::getValue).reversed());
        return AjaxResult.success(list);
    }

    /**
     * 查询数据通过分户ids
     *
     * @param rankingVO 排名VO
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/11/29
     **/
    @Override
    public AjaxResult selectDataByHouseholdIds(RankingVO rankingVO) {
        //能耗数据值
        List<RankingDTO>  branchIds=rankingMapper.selectDataByHouseholdIds(rankingVO.getIds(),rankingVO.getStartTime(),rankingVO.getEndTime());
        Map<Long,Float> map=new HashMap<>();
        for (RankingDTO branchId : branchIds) {
            map.put(branchId.getId(),branchId.getValue());
        }
        //数据值
        List<RankingDTO> list=new ArrayList<>();
        for (Long branchId : rankingVO.getIds()) {
            float value=0;
            if (map!=null && map.get(branchId)!=null){
                value=map.get(branchId);
            }
            RankingDTO rankingDTO=new RankingDTO(branchId,value);
            list.add(rankingDTO);
        }
        list.sort(Comparator.comparing(RankingDTO::getValue).reversed());
        return AjaxResult.success(list);
    }
}
