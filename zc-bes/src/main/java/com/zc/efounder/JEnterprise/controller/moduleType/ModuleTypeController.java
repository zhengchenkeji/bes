package com.zc.efounder.JEnterprise.controller.moduleType;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.mapper.modulePoint.ModulePointMapper;
import com.zc.efounder.JEnterprise.domain.moduleType.ModuleType;
import com.zc.efounder.JEnterprise.service.moduleType.ModuleTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 模块类型定义Controller
 *
 * @author gaojikun
 * @date 2022-09-06
 */
@RestController
@RequestMapping("/basicData/moduleType")
@Api(value = "ModuleTypeController", tags = {"模块类型定义"})
@ApiSupport(order = 17)
public class ModuleTypeController extends BaseController {
    @Autowired
    private ModuleTypeService moduleTypeService;
    @Autowired
    private ModulePointMapper modulePointMapper;


    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:查询模块类型定义列表
     * @Return:TableDataInfo
     */
    @ApiOperation(value = "查询模块类型定义列表")
    @PreAuthorize("@ss.hasPermi('basicData:moduleType:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleCode", value = "模块型号"),
            @ApiImplicitParam(name = "typeCode", value = "下位机区分温控器", dataType = "long"),
            @ApiImplicitParam(name = "pointSet", value = "模块类型；0 AI;1 AO;2 DI; 3  DO;4 UI;5 UX"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)", paramType = "query", dataType = "int"),
    })
    public TableDataInfo list(ModuleType moduleType) {
        startPage();
        List<ModuleType> list = moduleTypeService.selectModuleTypeList(moduleType);
        return getDataTable(list);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:导出模块类型定义列表
     * @Return:
     */
    @PreAuthorize("@ss.hasPermi('basicData:moduleType:export')")
    @Log(title = "模块类型定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ModuleType moduleType) {
        List<ModuleType> list = moduleTypeService.selectModuleTypeList(moduleType);
        ExcelUtil<ModuleType> util = new ExcelUtil<>(ModuleType.class);
        util.exportExcel(response, list, "模块类型定义数据");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:id
     * @Description:获取模块类型定义详细信息
     * @Return:AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('basicData:moduleType:query')")
    @ApiOperation(value = "获取模块类型定义详细信息")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", value = "模块类型定义ID", paramType = "path", dataType = "long", required = true)
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(moduleTypeService.selectModuleTypeById(id));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:新增模块类型定义
     * @Return:AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('basicData:moduleType:add')")
    @ApiOperation(value = "新增模块类型定义")
    @ApiOperationSupport(ignoreParameters = "id")
    @Log(title = "模块类型定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ModuleType moduleType) {
        return moduleTypeService.insertModuleType(moduleType);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:moduleType
     * @Description:修改模块类型定义
     * @Return:AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('basicData:moduleType:edit')")
    @Log(title = "模块类型定义", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改模块类型定义")
    @PutMapping
    public AjaxResult edit(@RequestBody ModuleType moduleType) {
        return moduleTypeService.updateModuleType(moduleType);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:ids
     * @Description:删除模块类型定义
     * @Return:AjaxResult
     */
    @ApiOperation(value = "删除模块类型定义")
    @PreAuthorize("@ss.hasPermi('basicData:moduleType:remove')")
    @Log(title = "模块类型定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids", value = "模块类型定义ID集合（以逗号隔开）", paramType = "path", dataType = "long", required = true, allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids) {
        return moduleTypeService.deleteModuleTypeByIds(ids);
    }


    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:
     * @Description:查询所有点类型
     * @Return:AjaxResult
     */
    @ApiOperation(value = "查询所有点类型")
    @GetMapping("/listPoint")
    public AjaxResult listPoint() {
        List<Map<String, Object>> tree = modulePointMapper.selectAllModulePointList();
        return AjaxResult.success(tree);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:23
     * @Param:
     * @Description:查询所有模块类型
     * @Return:List<ModuleType>
     */
    @ApiOperation(value = "查询所有模块类型")
    @GetMapping("/listModuleType")
    public List<ModuleType> listModuleType() {
        List<ModuleType> list = moduleTypeService.selectModuleTypeList(null);
        return list;
    }
}
