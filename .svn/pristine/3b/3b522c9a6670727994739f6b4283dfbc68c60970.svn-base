package com.zc.efounder.JEnterprise.controller.energyInfo;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfigList;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.service.energyInfo.EnergyTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 园区能源配置Controller
 *
 * @author ruoyi
 * @date 2022-09-07
 */
@RestController
@Api(value = "EnergyConfigController", tags = {"园区能源配置"})
@ApiSupport(order = 14)
@RequestMapping("/basicData/energyConfig")
public class EnergyConfigController extends BaseController
{
    @Autowired
    private EnergyTypeService energyTypeService;

    /**
     * 查询园区能源配置列表
     */
    @ApiOperation("查询园区能源配置列表")
    @PreAuthorize("@ss.hasPermi('basicData:energyConfig:list')")
    @GetMapping(value = "/list")
    @ApiOperationSupport(includeParameters = {"code","pageNum","pageSize"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(EnergyType energyType)
    {
        startPage();
        String code = energyType.getCode();
        List<Map<String,Object>> list = energyTypeService.selectEnergyConfigList(code);
        return getDataTable(list);
    }

    /**
     * 新增园区能源配置
     */
    @ApiOperation("新增园区能源配置")
    @PreAuthorize("@ss.hasPermi('basicData:energyConfig:add')")
    @Log(title = "园区能源配置", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    @ResponseBody
    public AjaxResult add(@RequestBody  EnergyConfigList energyConfigList)
    {
        return energyTypeService.insertEnergyConfig(energyConfigList);
    }

}
