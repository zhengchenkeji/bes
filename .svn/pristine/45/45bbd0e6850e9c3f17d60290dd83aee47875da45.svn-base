package com.zc.efounder.JEnterprise.controller.energyAnalysis;


import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.service.energyAnalysis.HouseholdRingRatioService;
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
 * @Description 分户环比Controller
 *
 * @author liuwenge
 * @date 2022/11/9 10:38
 */
@RestController
@RequestMapping("/energyAnalysis/householdRingRatio")
@Api(value = "HouseholdRingRatioController", tags = {"分户环比"})
@ApiSupport(order = 39,author = "liuwenge")
public class HouseholdRingRatioController extends BaseController
{
    @Autowired
    private HouseholdRingRatioService householdRingRatioService;

    /**
     * 查询所有园区
     */
    @ApiOperation(value = "查询所有园区")
    @GetMapping("/getAllPark")
    public List<Park> getAllPark(){
        return householdRingRatioService.getAllPark();
    }

    /**
     * 查询所有能源类型
     */
    @ApiOperation(value = "查询所有能源类型")
    @GetMapping("/getEnergyType")
    public List<EnergyType> getEnergyType(){
        return householdRingRatioService.getEnergyType();
    }


    /**
     * 查询分户环比数据
     */
    @ApiOperation(value = "查询分户环比数据")
    @GetMapping("/queryData")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "householdId",value = "分户ID",required = true),
            @ApiImplicitParam(name = "dateType",value = "日期类型 1:日 2:月",required = true),
            @ApiImplicitParam(name = "dateTime",value = "当前日期",required = true),
    })
    public AjaxResult queryData(String householdId,String dateType,String dateTime){
        return householdRingRatioService.queryData(householdId,dateType,dateTime);
    }


}
