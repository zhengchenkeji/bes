package com.zc.efounder.JEnterprise.controller.modulePoint;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zc.efounder.JEnterprise.domain.modulePoint.ModulePoint;
import com.zc.efounder.JEnterprise.service.modulePoint.ModulePointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
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
 * 模块点类型定义Controller
 *
 * @author gaojikun
 * @date 2022-09-06
 */
@RestController
@RequestMapping("/basicData/modulePoint")
@Api(value = "ModulePointController", tags = {"模块点类型定义"})
@ApiSupport(order = 16)
public class ModulePointController extends BaseController
{
    @Autowired
    private ModulePointService moduleTypeService;


    /**
    * @Author:gaojikun
    * @Date:2023-01-13 15:06
    * @Param:modulePoint
    * @Description:查询模块点类型定义列表
    * @Return:TableDataInfo
    */
    @ApiOperation(value = "查询模块点类型定义列表")
    @PreAuthorize("@ss.hasPermi('basicData:modulePoint:list')")
    @GetMapping("/list")
    @ApiOperationSupport(includeParameters = {
            "modulePoint","description","pageNum","pageSize"
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modulePoint",value = "模块点类型"),
            @ApiImplicitParam(name = "description",value = "模块描述"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(ModulePoint modulePoint)
    {
        startPage();
        List<ModulePoint> list = moduleTypeService.selectModulePointList(modulePoint);
        return getDataTable(list);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:导出模块点类型定义列表
     * @Return:
     */
    @PreAuthorize("@ss.hasPermi('basicData:modulePoint:export')")
    @Log(title = "模块点类型定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ModulePoint modulePoint)
    {
        List<ModulePoint> list = moduleTypeService.selectModulePointList(modulePoint);
        ExcelUtil<ModulePoint> util = new ExcelUtil<>(ModulePoint.class);
        util.exportExcel(response, list, "模块点类型定义数据");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:id
     * @Description:获取模块点类型定义详细信息
     * @Return:AjaxResult
     */
    @ApiOperation(value = "获取模块点类型定义详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:modulePoint:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "模块点类型定义ID",paramType = "path",dataType = "long",required = true)
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(moduleTypeService.selectModulePointById(id));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:新增模块点类型定义
     * @Return:AjaxResult
     */
    @ApiOperation(value = "新增模块点类型定义")
    @PreAuthorize("@ss.hasPermi('basicData:modulePoint:add')")
    @ApiOperationSupport(ignoreParameters = "id")
    @Log(title = "模块点类型定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ModulePoint modulePoint)
    {
        return moduleTypeService.insertModulePoint(modulePoint);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:modulePoint
     * @Description:修改模块点类型定义
     * @Return:AjaxResult
     */
    @ApiOperation(value = "修改模块点类型定义")
    @PreAuthorize("@ss.hasPermi('basicData:modulePoint:edit')")
    @Log(title = "模块点类型定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ModulePoint modulePoint)
    {
        return moduleTypeService.updateModulePoint(modulePoint);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:06
     * @Param:ids
     * @Description:删除模块点类型定义
     * @Return:AjaxResult
     */
    @ApiOperation(value = "删除模块点类型定义")
    @PreAuthorize("@ss.hasPermi('basicData:modulePoint:remove')")
    @Log(title = "模块点类型定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "模块点类型定义ID集合（以逗号隔开）",paramType = "path",dataType = "long",required = true,allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return moduleTypeService.deleteModulePointByIds(ids);
    }
}
