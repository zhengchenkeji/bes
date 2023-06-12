package com.zc.efounder.JEnterprise.controller.energyAnalysis;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.service.energyAnalysis.impl.RankingServiceImpl;
import com.zc.efounder.JEnterprise.domain.energyAnalysis.vo.RankingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 能耗排名
 *
 * @author qindehua
 */
@RestController
@RequestMapping("/energyAnalysis/ranking")
@Api(value = "SurveyController", tags = {"能耗排名"})
@ApiSupport(order = 34,author = "qindehua")
public class RankingController {

    @Autowired
    private RankingServiceImpl service;

    /**
     * 查询支路能耗
     */
    @PostMapping("/branch")
    @ApiOperation(value = "查询支路能耗")
    public AjaxResult selectRankingBranch(@RequestBody @Validated RankingVO rankingVO){
        return service.selectDataByBranchIds(rankingVO);
    }
    /**
     * 查询分户能耗
     */
    @PostMapping("/household")
    @ApiOperation(value = "查询分户能耗")
    public AjaxResult selectRankingHousehold(@RequestBody @Validated RankingVO rankingVO){
        return service.selectDataByHouseholdIds(rankingVO);
    }

}
