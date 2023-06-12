package com.zc.efounder.JEnterprise.controller.energyAnalysis;


import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.service.energyAnalysis.HouseholdContemporaneousService;
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
 * @Description 分户同比Controller
 *
 * @author liuwenge
 * @date 2022/11/9 10:38
 */
@RestController
@RequestMapping("/energyAnalysis/householdContemporaneous")
@Api(value = "HouseholdContemporaneousController", tags = {"分户同比"})
@ApiSupport(order = 38,author = "liuwenge")
public class HouseholdContemporaneousController extends BaseController
{
    @Autowired
    private HouseholdContemporaneousService householdContemporaneousService;

    /**
     * 查询所有园区
     */
    @GetMapping("/getAllPark")
    @ApiOperation(value = "查询所有园区")
    public List<Park> getAllPark(){
        return householdContemporaneousService.getAllPark();
    }

    /**
     * 查询所有能源类型
     */
    @GetMapping("/getEnergyType")
    @ApiOperation(value = "查询所有能源类型")
    public List<EnergyType> getEnergyType(){
        return householdContemporaneousService.getEnergyType();
    }


    @GetMapping("/queryData")
    @ApiOperation(value = "查询分户同比数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "householdId",value = "分户ID",required = true),
            @ApiImplicitParam(name = "dateTime",value = "当前年份",required = true),
    })
    public AjaxResult queryData(String householdId,String dateTime){
        return householdContemporaneousService.queryData(householdId,dateTime);
    }


}
