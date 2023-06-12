package com.zc.efounder.JEnterprise.controller.energyAnalysis;


import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.service.energyAnalysis.BranchContemporaneousService;
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
 * @Description 支路同比Controller
 *
 * @author liuwenge
 * @date 2022/11/9 10:38
 */
@RestController
@RequestMapping("/energyAnalysis/branchContemporaneous")
@Api(value = "BranchContemporaneousController", tags = {"支路同比"})
@ApiSupport(order = 36,author = "liuwenge")
public class BranchContemporaneousController extends BaseController
{
    @Autowired
    private BranchContemporaneousService branchContemporaneousService;

    /**
     * 查询所有园区
     */
    @GetMapping("/getAllPark")
    @ApiOperation(value = "查询所有园区")
    public List<Park> getAllPark(){
        return branchContemporaneousService.getAllPark();
    }

    /**
     * 查询所有能源类型
     */
    @GetMapping("/getEnergyType")
    @ApiOperation(value = "查询所有能源类型")
    public List<EnergyType> getEnergyType(){
        return branchContemporaneousService.getEnergyType();
    }


    @GetMapping("/queryData")
    @ApiOperation(value = "查询支路同比数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchId",value = "支路ID",required = true),
            @ApiImplicitParam(name = "dateTime",value = "当前年份",required = true),
    })
    public AjaxResult queryBranchContemporaneousData(String branchId,String dateTime){
        return branchContemporaneousService.queryBranchContemporaneousData(branchId,dateTime);
    }


}
