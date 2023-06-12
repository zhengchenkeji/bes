package com.zc.efounder.JEnterprise.controller.energyAnalysis;


import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.service.energyAnalysis.SurveyService;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 用能概况Controller
 *
 * @author liuwenge
 * @date 2022/11/9 10:38
 */
@RestController
@RequestMapping("/energyAnalysis/survey")
@Api(value = "SurveyController", tags = {"用能概况"})
@ApiSupport(order = 33,author = "liuwenge")
public class SurveyController extends BaseController
{
    @Autowired
    private SurveyService surveyService;

    /**
     * 查询所有园区
     */
    @ApiOperation(value = "查询所有园区")
    @GetMapping("/getAllPark")
    public List<Park> getAllPark(){
        return surveyService.getAllPark();
    }

    /**
     * 查询所有能源类型
     */
    @GetMapping("/getEnergyType")
    @ApiOperation(value = "查询所有能源类型")
    public List<EnergyType> getEnergyType(){
        return surveyService.getEnergyType();
    }

    /**
     * 查询支路环比
     */
    @GetMapping("/queryRingRatioData")
    @ApiOperation(value = "查询支路环比")
    @ApiImplicitParam(name = "branchId",value = "支路ID",paramType = "query",required = true)
    public AjaxResult queryRingRatioData(String branchId){
        return surveyService.queryRingRatioData(branchId);
    }

    /**
     * 查询支路能耗趋势
     */
    @GetMapping("/queryTrendData")
    @ApiOperation(value = "查询支路能耗趋势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchId",value = "支路ID",paramType = "query",required = true),
            @ApiImplicitParam(name = "trendType",value = "类型: 0天 1月 2年",paramType = "query",required = true),
    })
    public AjaxResult queryTrendData(String branchId,String trendType){
        return surveyService.queryTrendData(branchId,trendType);
    }

    /**
     * 查询支路能耗排行
     */
    @GetMapping("/queryRankData")
    @ApiOperation(value = "查询支路能耗排行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchId",value = "支路ID",paramType = "query",required = true),
            @ApiImplicitParam(name = "rankType",value = "类型 0日 1月 2年",paramType = "query",required = true),
    })
    public AjaxResult queryRankData(String branchId,String rankType){
        return surveyService.queryRankData(branchId,rankType);
    }


}
