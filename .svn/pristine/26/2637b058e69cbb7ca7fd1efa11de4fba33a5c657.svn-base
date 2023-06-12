package com.zc.efounder.JEnterprise.controller.energyDataReport;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildGroupInfo;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import com.zc.efounder.JEnterprise.domain.energyDataReport.BuildBaseInfo;
import com.zc.efounder.JEnterprise.service.energyDataReport.BuildBaseInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 建筑基本项数据Controller
 *
 * @author liuwenge
 * @date 2022-09-14
 */
@RestController
@RequestMapping("/basicData/buildBaseInfo")
@Api(value = "BuildBaseInfoController", tags = {"建筑基本项数据"})
@ApiSupport(order = 24)
public class BuildBaseInfoController extends BaseController {
    @Autowired
    private BuildBaseInfoService buildBaseInfoService;

    /**
     * 查询建筑基本项数据列表
     *
     * @author liuwenge
     */
    @ApiOperation(value = "查询建筑基本项数据列表")
    @PreAuthorize("@ss.hasPermi('basicData:buildBaseInfo:list')")
    @GetMapping("/list")
    @ApiOperationSupport(includeParameters = {
            "dataCenterId", "buildGroupId", "buildName", "parkId","pageNum","pageSize",
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataCenterId", dataType = "long", value = "所属数据中心ID"),
            @ApiImplicitParam(name = "buildGroupId", dataType = "long", value = "所属建筑群ID"),
            @ApiImplicitParam(name = "buildName", dataType = "long", value = "建筑名称"),
            @ApiImplicitParam(name = "parkId", value = "所属园区ID"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)", paramType = "query", dataType = "int"),
    })
    public TableDataInfo list(BuildBaseInfo buildBaseInfo) {
        startPage();
        List<BuildBaseInfo> list = buildBaseInfoService.selectBuildBaseInfoList(buildBaseInfo);
        return getDataTable(list);
    }

    /**
     * 导出建筑基本项数据列表
     *
     * @author liuwenge
     */
    @ApiOperation(value = "导出建筑基本项数据列表")
    @PreAuthorize("@ss.hasPermi('basicData:buildBaseInfo:export')")
    @Log(title = "建筑基本项数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(includeParameters = {
            "dataCenterId", "buildGroupId", "buildName", "parkId",
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataCenterId", dataType = "long", value = "所属数据中心ID"),
            @ApiImplicitParam(name = "buildGroupId", dataType = "long", value = "所属建筑群ID"),
            @ApiImplicitParam(name = "buildName", dataType = "long", value = "建筑名称"),
            @ApiImplicitParam(name = "parkId", value = "所属园区ID")
    })
    public void export(HttpServletResponse response, BuildBaseInfo buildBaseInfo) {
        List<BuildBaseInfo> list = buildBaseInfoService.selectAllInfo(buildBaseInfo);
        ExcelUtil<BuildBaseInfo> util = new ExcelUtil<>(BuildBaseInfo.class);
        util.exportExcel(response, list, "建筑基本项数据");
    }

    /**
     * 获取建筑基本项数据详细信息
     *
     * @author liuwenge
     */
    @ApiOperation(value = "获取建筑基本项数据详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:buildBaseInfo:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", value = "建筑ID", required = true, dataType = "long")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return buildBaseInfoService.selectBuildBaseInfoById(id);
    }

    /**
     * 新增建筑基本项数据
     *
     * @author liuwenge
     */
    @ApiOperation(value = "新增建筑基本项数据")
    @PreAuthorize("@ss.hasPermi('basicData:buildBaseInfo:add')")
    @Log(title = "建筑基本项数据", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperationSupport(ignoreParameters = "id")
    public AjaxResult add(@RequestBody BuildBaseInfo buildBaseInfo) {
        buildBaseInfo.setCreateUser(getUsername());
        return buildBaseInfoService.insertBuildBaseInfo(buildBaseInfo);
    }

    /**
     * 修改建筑基本项数据
     *
     * @author liuwenge
     */
    @ApiOperation(value = "修改建筑基本项数据")
    @PreAuthorize("@ss.hasPermi('basicData:buildBaseInfo:edit')")
    @Log(title = "建筑基本项数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BuildBaseInfo buildBaseInfo) {
        return buildBaseInfoService.updateBuildBaseInfo(buildBaseInfo);
    }

    /**
     * 删除建筑基本项数据
     *
     * @author liuwenge
     */
    @ApiOperation(value = "删除建筑基本项数据")
    @PreAuthorize("@ss.hasPermi('basicData:buildBaseInfo:remove')")
    @Log(title = "建筑基本项数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids", value = "建筑ID集合(以逗号隔开例如：1,2)", required = true, dataType = "long", allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids) {
        return buildBaseInfoService.deleteBuildBaseInfoByIds(ids);
    }

    /**
     * 查询所有数据中心
     *
     * @author liuwenge
     */
    @ApiOperation(value = "查询所有数据中心")
    @GetMapping("/getAllDataCenterBaseInfo")
    public List<DataCenterBaseInfo> getAllDataCenterBaseInfo() {
        return buildBaseInfoService.getAllDataCenterBaseInfo();
    }

    /**
     * 查询所有建筑群
     *
     * @author liuwenge
     */
    @ApiOperation(value = "查询所有建筑群")
    @GetMapping("/getAllBuildGroup")
    public List<BuildGroupInfo> getAllBuildGroup() {
        return buildBaseInfoService.getAllBuildGroup();
    }

    /**
     * 查询所有园区
     *
     * @author liuwenge
     */
    @ApiOperation(value = "查询所有园区")
    @GetMapping("/getAllPark")
    public List<Park> getAllPark() {
        return buildBaseInfoService.getAllPark();
    }
}
