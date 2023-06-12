package com.zc.efounder.JEnterprise.controller.energyAnalysis;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.energyAnalysis.vo.ReportFormResult;
import com.zc.efounder.JEnterprise.service.energyAnalysis.reportFormService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * description:集抄报表控制器
 * author: sunshangeng
 * date:2022/11/28 9:54
 */
@Controller
@RequestMapping("/energyAnalysis/reportForm")
@Api(value = "ReportFormController", tags = {"集抄报表"})
@ApiSupport(order = 35,author = "sunshangeng")
public class ReportFormController {

    @Autowired
    private reportFormService testservice;

    @GetMapping("/list")
    @ResponseBody
    @ApiOperation(value = "集抄报表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchIds",value = "支路ID集合(以逗号隔开例如 1,2)",dataType = "Long",allowMultiple = true,required = true),
            @ApiImplicitParam(name = "time",value = "时间集合(以逗号隔开)",dataType = "Date",allowMultiple = true,required = true)
    })
    public AjaxResult list(Long[] branchIds ,Date[] time)
    {
        return  testservice.service(branchIds,time);
    }
    @PostMapping("/export")
    @ResponseBody
    @ApiOperation(value = "导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchIds",value = "支路ID集合(以逗号隔开例如 1,2)",dataType = "Long",allowMultiple = true,required = true),
            @ApiImplicitParam(name = "time",value = "时间集合(以逗号隔开)",dataType = "Date",allowMultiple = true,required = true)
    })
    public void export(Long[] branchIds , Date[] time, HttpServletResponse response)
    {
        List<ReportFormResult> resultList;
        AjaxResult result = testservice.service(branchIds, time);
        resultList= (List<ReportFormResult>)result.get("data");
        ExcelUtil<ReportFormResult> util = new ExcelUtil<>(ReportFormResult.class);
        util.exportExcel(response, resultList, "集抄报表");


    }

}
