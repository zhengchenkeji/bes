package com.zc.efounder.JEnterprise.controller.energyInfo;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.efounder.JEnterprise.mapper.energyInfo.ParkMapper;
import com.zc.efounder.JEnterprise.service.energyInfo.IParkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 园区Controller
 *
 * @author ruoyi
 * @date 2022-09-08
 */
@RestController
@RequestMapping("/basicData/parkInfo")
@Api(value = "ParkController", tags = {"园区配置"})
@ApiSupport(order = 15)
public class ParkController extends BaseController
{
    @Autowired
    private IParkService parkService;

    @Autowired
    private ParkMapper parkMapper;

    /**
     * 查询园区列表
     */
    @ApiOperation(value = "查询园区列表")
    @PreAuthorize("@ss.hasPermi('basicData:parkInfo:list')")
    @GetMapping("/list")
    @ApiOperationSupport(includeParameters = {"name","code","pageNum","pageSize",})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "园区名称",  paramType = "query"),
            @ApiImplicitParam(name = "code", value = "园区编号",  paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(Park park)
    {
        startPage();
        List<Park> list = parkService.selectParkList(park);
        return getDataTable(list);

    }

    /**
     * 查询所有园区
     */
    @ApiOperation(value = "查询所有园区")
    @GetMapping("/parkList")
    public List<Park> parkList()
    {
        List<Park> list = parkMapper.findAllPark();
        return list;
    }

    /**
     * 用户列表
     */
    @ApiOperation(value = "用户列表")
    @GetMapping(value = "/listUser")
    public AjaxResult listUser()
    {
        List<Map<String,Object>> list = parkMapper.listUser();
        return AjaxResult.success(list);
    }

    /**
     * 组织机构列表
     */
    @ApiOperation(value = "组织机构列表")
    @GetMapping(value = "/listOrganization")
    public AjaxResult listOrganization()
    {
        List<Map<String,Object>> list = parkMapper.listOrganization();
        return AjaxResult.success(list);
    }

    /**
     * 导出园区列表
     */
    @ApiOperation(value = "导出园区列表")
    @PreAuthorize("@ss.hasPermi('basicData:parkInfo:export')")
    @Log(title = "园区", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(includeParameters = {"name","code","pageNum","pageSize",})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "园区名称",  paramType = "query"),
            @ApiImplicitParam(name = "code", value = "园区编号",  paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public void export(HttpServletResponse response, Park park)
    {
        List<Park> list = parkService.selectParkList(park);
        ExcelUtil<Park> util = new ExcelUtil<>(Park.class);
        util.exportExcel(response, list, "园区数据");
    }

    /**
     * 获取园区详细信息
     */
    @ApiOperation(value = "获取园区详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:parkInfo:query')")
    @GetMapping(value = "/{code}")
    @ApiImplicitParam(name = "code",value = "园区编号",paramType = "path",required = true)
    public AjaxResult getInfo(@PathVariable("code") String code)
    {
        return AjaxResult.success(parkService.selectParkByCode(code));
    }

    /**
     * 新增园区
     */
    @ApiOperation(value = "新增园区")
    @PreAuthorize("@ss.hasPermi('basicData:parkInfo:add')")
    @Log(title = "园区", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperationSupport(ignoreParameters = "code")
    public AjaxResult add(@RequestBody @Validated Park park)
    {
        return toAjax(parkService.insertPark(park));
    }

    /**
     * 修改园区
     */
    @ApiOperation(value = "修改园区")
    @PreAuthorize("@ss.hasPermi('basicData:parkInfo:edit')")
    @Log(title = "园区", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated Park park)
    {
        return parkService.updatePark(park);
    }

    /**
     * 删除园区
     */
    @ApiOperation(value = "删除园区")
    @PreAuthorize("@ss.hasPermi('basicData:parkInfo:remove')")
    @Log(title = "园区", businessType = BusinessType.DELETE)
	@DeleteMapping("/{codes}")
    @ApiImplicitParam(name = "codes",value = "园区编号集合（以逗号隔开）",paramType = "path",required = true,allowMultiple = true)
    public AjaxResult remove(@PathVariable String[] codes   )
    {
        return parkService.deleteParkByCodes(codes);
    }
}
