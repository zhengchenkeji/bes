package com.zc.efounder.JEnterprise.controller.systemSetting;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.systemSetting.ParameterConfiguration;
import com.zc.efounder.JEnterprise.domain.systemSetting.ParamsConfiguration;
import com.zc.efounder.JEnterprise.service.systemSetting.ParameterConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 主采集参数Controller
 *
 * @author gaojikun
 * @date 2022-11-30
 */
@RestController
@RequestMapping("/systemSetting/parameterConfiguration")
@Api(value = "ParameterConfigurationController", tags = {"参数配置"})
@ApiSupport(order = 31,author = "gaojikun")
public class ParameterConfigurationController extends BaseController
{
    @Autowired
    private ParameterConfigurationService parameterConfigurationService;

    /**
    * @Author:gaojikun
    * @Date:2023-01-13 13:45
    * @Param:parameterConfiguration
    * @Description:查询主采集参数列表
    * @Return:TableDataInfo
    */
    @ApiOperation(value = "查询主采集参数列表")
    @PreAuthorize("@ss.hasPermi('system:params:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "名称"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(ParameterConfiguration parameterConfiguration)
    {
        startPage();
        List<ParameterConfiguration> list = parameterConfigurationService.selectAthenaBesParamsList(parameterConfiguration);
        return getDataTable(list);
    }


    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:导出主采集参数列表
     * @Return:
     */
    @ApiOperation(value = "导出主采集参数列表")
    @PreAuthorize("@ss.hasPermi('system:params:export')")
    @Log(title = "主采集参数", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "名称"),
    })
    public void export(HttpServletResponse response, ParameterConfiguration parameterConfiguration)
    {
        List<ParameterConfiguration> list = parameterConfigurationService.selectAthenaBesParamsList(parameterConfiguration);
        ExcelUtil<ParameterConfiguration> util = new ExcelUtil<>(ParameterConfiguration.class);
        util.exportExcel(response, list, "主采集参数数据");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:获取主采集参数详细信息
     * @Return:AjaxResult
     */
    @ApiOperation(value = "获取主采集参数详细信息")
    @PreAuthorize("@ss.hasPermi('system:params:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "主采集参数ID",required = true,paramType = "path",dataType = "long")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(parameterConfigurationService.selectAthenaBesParamsById(id));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:新增主采集参数
     * @Return:AjaxResult
     */
    @ApiOperation(value = "新增主采集参数")
    @PreAuthorize("@ss.hasPermi('system:params:add')")
    @Log(title = "主采集参数", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperationSupport(ignoreParameters = "id")
    public AjaxResult add(@RequestBody @Valid ParameterConfiguration parameterConfiguration)
    {
        return parameterConfigurationService.insertAthenaBesParams(parameterConfiguration);
    }


    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:parameterConfiguration
     * @Description:修改主采集参数
     * @Return:AjaxResult
     */
    @ApiOperation(value = "修改主采集参数")
    @PreAuthorize("@ss.hasPermi('system:params:edit')")
    @Log(title = "主采集参数", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Valid ParameterConfiguration parameterConfiguration)
    {
        return parameterConfigurationService.updateAthenaBesParams(parameterConfiguration);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:ids
     * @Description:删除主采集参数
     * @Return:AjaxResult
     */
    @ApiOperation(value = "删除主采集参数")
    @PreAuthorize("@ss.hasPermi('system:params:remove')")
    @Log(title = "主采集参数", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "主采集参数ID集合(以逗号隔开例如 1,2)",required = true,allowMultiple = true,paramType = "path",dataType = "long")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return parameterConfigurationService.deleteAthenaBesParamsByIds(ids);
    }



    //----------------------------------------------采集参数------------------------------------------------------

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:查询选择采集参数列表
     * @Return:List<ElectricParams>
     */
    @GetMapping("/listCheckList")
    @ApiOperation(value = "查询选择采集参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramId",value = "采集参数ID",dataType = "long"),
            @ApiImplicitParam(name = "paramsId",value = "采集参数分项ID",dataType = "long"),
    })
    public List<ElectricParams> listCheckList(ParamsConfiguration paramsConfiguration)
    {
        List<ElectricParams> list = parameterConfigurationService.listCheckList(paramsConfiguration);
        return list;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:查询未选择采集参数列表
     * @Return:List<ElectricParams>
     */
    @GetMapping("/listNoCheckList")
    @ApiOperation(value = "查询未选择采集参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramId",value = "采集参数ID",dataType = "long"),
            @ApiImplicitParam(name = "paramsId",value = "采集参数分项ID",dataType = "long"),
    })
    public List<ElectricParams> listNoCheckList(ParamsConfiguration paramsConfiguration)
    {
        List<ElectricParams> list = parameterConfigurationService.listNoCheckList(paramsConfiguration);
        return list;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:全部选中采集参数
     * @Return:AjaxResult
     */
    @GetMapping("/allCheckList")
    @ApiOperation(value = "全部选中采集参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramId",value = "采集参数ID",dataType = "long"),
            @ApiImplicitParam(name = "paramsId",value = "采集参数分项ID",dataType = "long"),
    })
    public AjaxResult allCheckList(ParamsConfiguration paramsConfiguration)
    {
        return parameterConfigurationService.allCheckList(paramsConfiguration);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:全部取消
     * @Return:AjaxResult
     */
    @GetMapping("/allNoCheckList")
    @ApiOperation(value = "全部取消")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramId",value = "采集参数ID",dataType = "long"),
            @ApiImplicitParam(name = "paramsId",value = "采集参数分项ID",dataType = "long"),
    })
    public AjaxResult allNoCheckList(ParamsConfiguration paramsConfiguration)
    {
        return parameterConfigurationService.allNoCheckList(paramsConfiguration);
    }

    /**
    * @Author:gaojikun
    * @Date:2023-01-13 13:47
    * @Param:paramsConfiguration
    * @Description:添加一条采集参数
    * @Return:AjaxResult
    */
    @GetMapping("/insertParamConfigRlgl")
    @ApiOperation(value = "添加一条采集参数")
    public AjaxResult insertParamConfigRlgl(@Valid ParamsConfiguration paramsConfiguration)
    {
        return parameterConfigurationService.insertParamConfigRlgl(paramsConfiguration);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:paramsConfiguration
     * @Description:取消一条采集参数
     * @Return:AjaxResult
     */
    @GetMapping("/delParamConfigRlgl")
    @ApiOperation(value = "取消一条采集参数")
    public AjaxResult delParamConfigRlgl(ParamsConfiguration paramsConfiguration)
    {
        return parameterConfigurationService.delParamConfigRlgl(paramsConfiguration);
    }
}
