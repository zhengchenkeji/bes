package com.zc.efounder.JEnterprise.controller.energyAnalysis;


import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.service.energyAnalysis.BranchRingRatioService;
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
 * @Description 支路环比Controller
 *
 * @author liuwenge
 * @date 2022/11/9 10:38
 */
@RestController
@RequestMapping("/energyAnalysis/branchRingRatio")
@Api(value = "BranchRingRatioController", tags = {"支路环比"})
@ApiSupport(order = 37,author = "liuwenge")
public class BranchRingRatioController extends BaseController
{
    @Autowired
    private BranchRingRatioService branchRingRatioService;

    /**
     * 查询所有园区
     */
    @GetMapping("/getAllPark")
    @ApiOperation(value = "查询所有园区")
    public List<Park> getAllPark(){
        return branchRingRatioService.getAllPark();
    }

    /**
     * 查询所有能源类型
     */
    @GetMapping("/getEnergyType")
    @ApiOperation(value = "查询所有能源类型")
    public List<EnergyType> getEnergyType(){
        return branchRingRatioService.getEnergyType();
    }


    /**
     * 查询支路环比数据
     */
    @GetMapping("/queryData")
    @ApiOperation(value = "查询支路环比数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchId",value = "支路ID",required = true),
            @ApiImplicitParam(name = "dateType",value = "日期类型 1:日 2:月",required = true),
            @ApiImplicitParam(name = "dateTime",value = "当前日期",required = true),
    })
    public AjaxResult queryData(String branchId,String dateType,String dateTime){
        return branchRingRatioService.queryData(branchId,dateType,dateTime);
    }


}
