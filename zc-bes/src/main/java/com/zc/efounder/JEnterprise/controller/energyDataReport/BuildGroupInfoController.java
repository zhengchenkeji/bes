package com.zc.efounder.JEnterprise.controller.energyDataReport;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildGroupInfo;
import com.zc.efounder.JEnterprise.service.energyDataReport.BuildGroupInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 建筑群信息Controller
 *
 * @author ruoyi
 * @date 2022-09-13
 */
@RestController
@RequestMapping("/basicData/buildGroupInfo")
@Api(value = "BuildGroupInfoController", tags = {"建筑群信息"})
@ApiSupport(order = 23)
public class BuildGroupInfoController extends BaseController
{
    @Autowired
    private BuildGroupInfoService buildGroupInfoService;

    /**
     * 查询建筑群信息列表
     */
    @ApiOperation(value = "查询建筑群信息列表")
    @PreAuthorize("@ss.hasPermi('basicData:buildGroupInfo:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "buildGroupName",value = "建筑群名称"),
            @ApiImplicitParam(name = "groupAliasName",value = "建筑群别名"),
            @ApiImplicitParam(name = "groupDesc",value = "建筑群描述"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(BuildGroupInfo buildGroupInfo)
    {
        startPage();
        List<BuildGroupInfo> list = buildGroupInfoService.selectBuildGroupInfoList(buildGroupInfo);
        return getDataTable(list);
    }

    /**
     * 导出建筑群信息列表
     */
    @PreAuthorize("@ss.hasPermi('basicData:buildGroupInfo:export')")
    @ApiOperation(value = "导出建筑群信息列表")
    @Log(title = "建筑群信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "buildGroupName",value = "建筑群名称"),
            @ApiImplicitParam(name = "groupAliasName",value = "建筑群别名"),
            @ApiImplicitParam(name = "groupDesc",value = "建筑群描述"),
    })
    public void export(HttpServletResponse response, BuildGroupInfo buildGroupInfo)
    {
        List<BuildGroupInfo> list = buildGroupInfoService.selectBuildGroupInfoList(buildGroupInfo);
        ExcelUtil<BuildGroupInfo> util = new ExcelUtil<>(BuildGroupInfo.class);
        util.exportExcel(response, list, "建筑群信息数据");
    }

    /**
     * 获取建筑群信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('basicData:buildGroupInfo:query')")
    @ApiOperation(value = "获取建筑群信息详细信息")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "建筑群ID",dataType = "long",required = true)
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(buildGroupInfoService.selectBuildGroupInfoById(id));
    }

    /**
     * 新增建筑群信息
     */
    @PreAuthorize("@ss.hasPermi('basicData:buildGroupInfo:add')")
    @ApiOperation(value = "新增建筑群信息")
    @Log(title = "建筑群信息", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = "id")
    @PostMapping
    public AjaxResult add(@RequestBody BuildGroupInfo buildGroupInfo)
    {
        return buildGroupInfoService.insertBuildGroupInfo(buildGroupInfo);
    }

    /**
     * 修改建筑群信息
     */
    @PreAuthorize("@ss.hasPermi('basicData:buildGroupInfo:edit')")
    @ApiOperation(value = "修改建筑群信息")
    @Log(title = "建筑群信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BuildGroupInfo buildGroupInfo)
    {
        return buildGroupInfoService.updateBuildGroupInfo(buildGroupInfo);
    }

    /**
     * 删除建筑群信息
     */
    @PreAuthorize("@ss.hasPermi('basicData:buildGroupInfo:remove')")
    @ApiOperation(value = "删除建筑群信息")
    @Log(title = "建筑群信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "建筑群ID集合(以逗号隔开例如 1,2)",dataType = "long",required = true,allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return buildGroupInfoService.deleteBuildGroupInfoByIds(ids);
    }
}
