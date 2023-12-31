package com.zc.efounder.JEnterprise.controller.electricPowerTranscription;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.vo.ParamVO;
import com.zc.efounder.JEnterprise.service.electricPowerTranscription.ParamService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 电参数报表
 *
 * @author qindehua
 * @date 2023/02/21
 */
@RestController
@RequestMapping("/electricPowerTranscription/param")
@Api(value = "ParamController", tags = {"电参数报表"})
@ApiSupport(order = 41,author = "qindehua")
public class ParamController extends BaseController {

    @Autowired
    private ParamService service;

    /**
     * 查询电表下面的电能参数
     *
     * @param id 电表树id
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/11/29
     **/
    @GetMapping("/getMeterParams")
    @ApiOperation(value = "查询电表下面的电能参数")
    public AjaxResult getMeterParams(@RequestParam("meterId") @ApiParam(value = "电表ID",required = true) Long id) {
        return service.getMeterParams(id);
    }

    /**
     * 查询根据电能参数  查询参数数据
     *
     * @param paramVO
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/11/30
     **/
    @GetMapping("/getDataByParamsCode")
    @ApiOperation(value = "查询根据电能参数  查询参数数据")
    @ApiOperationSupport(ignoreParameters = "meterIds")
    public AjaxResult getDataByParamsCode(@Validated ParamVO paramVO) {
        return service.getDataByParamsCode(paramVO);
    }

    /**
     * 导出
     *
     * @param response 响应
     * @param paramVO
     * @Author qindehua
     * @Date 2022/11/30
     **/
    @PostMapping("/export")
    @ApiOperation(value = "导出")
    @ApiOperationSupport(ignoreParameters = "meterIds")
    public void export(HttpServletResponse response, @Validated ParamVO paramVO) throws IOException
    {
        service.exportExcel(response,paramVO);
    }

    /**
     * 批量导出
     *
     * @param response 响应
     * @param paramVO
     * @Author qindehua
     * @Date 2022/12/01
     **/
    @PostMapping("/exportBatch")
    @ApiOperation(value = "批量导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "meterIds",value = "电表树id集合(以逗号隔开例如 1,2)",required = true,allowMultiple = true,paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "meter",value = "电表树id",paramType = "query",dataType = "long")
    })
    public void exportBatch(HttpServletResponse response, @Validated ParamVO paramVO) throws IOException
    {
        service.exportExcelBatch(response,paramVO);
    }


}
