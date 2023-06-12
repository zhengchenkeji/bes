package com.zc.efounder.JEnterprise.controller.energyAnalysis;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyAnalysis.EnergyConsumptionTrend;
import com.zc.efounder.JEnterprise.service.energyAnalysis.EnergyConsumptionTrendService;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description 能耗趋势Controller
 *
 * @author gaojikun
 * @date 2022/11/9 10:38
 */
@RestController
@RequestMapping("/energyAnalysis/EnergyConsumptionTrend")
@Api(value = "EnergyConsumptionTrendController", tags = {"能耗趋势"})
@ApiSupport(order = 32,author = "gaojikun")
public class EnergyConsumptionTrendController extends BaseController
{
    @Autowired
    private EnergyConsumptionTrendService energyConsumptionTrendService;

    /**
     * 查询所有园区
     */
    @ApiOperation(value = "查询所有园区")
    @GetMapping("/getAllPark")
    public List<Park> getAllPark(){
        return energyConsumptionTrendService.getAllPark();
    }

    /**
     * 查询所有能源类型
     */
    @GetMapping("/getEnergyType")
    @ApiOperation(value = "查询所有能源类型")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "energyCode",value = "能源编号"),
            @ApiImplicitParam(name = "check",value = "操作标识  1:添加 0:删除"),
    })
    public List<EnergyType> getEnergyType(EnergyConfig energyConfig){
        return energyConsumptionTrendService.getEnergyType(energyConfig);
    }

    /**
     * 查询支路能耗趋势
     */
    @GetMapping("/queryAccessRdData")
    @ApiOperation(value = "查询支路能耗趋势")
    public AjaxResult queryAccessRdData(EnergyConsumptionTrend energyConsumptionTrend){
        return energyConsumptionTrendService.queryAccessRdData(energyConsumptionTrend);
    }

    /**
     * 查询分户能耗趋势
     */
    @GetMapping("/queryIndividualAccountData")
    @ApiOperation(value = "查询分户能耗趋势")
    public AjaxResult queryIndividualAccountData(EnergyConsumptionTrend energyConsumptionTrend){
        return energyConsumptionTrendService.queryIndividualAccountData(energyConsumptionTrend);
    }
}
