package com.zc.efounder.JEnterprise.controller.energyInfo;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SubitemConfig;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.energyInfo.vo.SubitemBranchVo;
import com.zc.efounder.JEnterprise.service.energyInfo.ISubitemBranchLinkService;
import com.zc.efounder.JEnterprise.service.energyInfo.ISubitemConfigService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分项拓扑配置Controller
 *
 * @author qindehua
 * @date 2022-09-20
 */
@RestController
@RequestMapping("/subitemConfig/config")
@ApiSupport(author = "QinDeHua",order = 5)
@Api(value = "分项拓扑配置", tags = {"分项拓扑配置"})
public class SubitemConfigController extends BaseController {
    @Autowired
    private ISubitemConfigService subitemConfigService;
    @Autowired
    private ISubitemBranchLinkService iSubitemBranchLinkService;

    /**
     * 查询分项拓扑配置列表
     */
    @ApiOperation("查询分项拓扑配置列表")
    @PreAuthorize("@ss.hasPermi('subitemConfig:config:list')")
    @GetMapping("/list")
    @ApiOperationSupport(includeParameters = {
            "subitemCode", "subitemName", "energyCode", "parkCode","code","subitemId","buildingId","pageNum","pageSize",
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "搜索按钮标识 0:节点查询 1:搜索按钮查询",  paramType = "query", required = true),
            @ApiImplicitParam(name = "subitemName", value = "分项名称",  paramType = "query"),
            @ApiImplicitParam(name = "subitemId", value = "分项ID",  paramType = "query"),
            @ApiImplicitParam(name = "energyCode", value = "所属能源",  paramType = "query"),
            @ApiImplicitParam(name = "parkCode", value = "所属园区",  paramType = "query"),
            @ApiImplicitParam(name = "buildingId", value = "所属建筑",  paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(SubitemConfig subitemConfig,
                              //搜索按钮标识 0:节点查询 1:搜索按钮查询
                              String code) {
        startPage();
        List<SubitemConfig> list = subitemConfigService.selectSubitemConfigListSun(subitemConfig, code);
        return getDataTable(list);
    }

    /**
     * 获取分项拓扑配置下拉树列表
     */
    @GetMapping("/treeSelect")
    @ApiOperation("获取分项拓扑配置下拉树列表")
    @PreAuthorize("@ss.hasPermi('householdConfig:config:list')")
    @ApiOperationSupport(includeParameters = {
            "subitemCode", "subitemName", "energyCode", "parkCode","subitemId","buildingId"
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subitemName", value = "分项名称",  paramType = "query"),
            @ApiImplicitParam(name = "subitemId", value = "分项ID",  paramType = "query"),
            @ApiImplicitParam(name = "energyCode", value = "所属能源",  paramType = "query"),
            @ApiImplicitParam(name = "parkCode", value = "所属园区",  paramType = "query"),
            @ApiImplicitParam(name = "buildingId", value = "所属建筑",  paramType = "query",dataType = "long"),
    })
    public AjaxResult treeSelect(SubitemConfig subitemConfig) {
        List<SubitemConfig> list = subitemConfigService.selectSubitemConfigList(subitemConfig);
        return AjaxResult.success(subitemConfigService.buildTreeSelect(list));
    }


//    /**
//     * 导出分项拓扑配置列表
//     */
//    @PreAuthorize("@ss.hasPermi('subitemConfig:config:export')")
//    @Log(title = "分项拓扑配置", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, SubitemConfig subitemConfig)
//    {
//        List<SubitemConfig> list = subitemConfigService.selectSubitemConfigList(subitemConfig);
//        ExcelUtil<SubitemConfig> util = new ExcelUtil<>(SubitemConfig.class);
//        util.exportExcel(response, list, "分项拓扑配置数据");
//    }

    /**
     * 获取分项拓扑配置详细信息
     */
    @ApiOperation("获取分项拓扑配置详细信息")
    @PreAuthorize("@ss.hasPermi('subitemConfig:config:query')")
    @GetMapping(value = "/{subitemId}")
    public AjaxResult getInfo(@PathVariable("subitemId") @ApiParam(value = "分项ID",required = true) String subitemId) {
        return AjaxResult.success(subitemConfigService.selectSubitemConfigBySubitemId(subitemId));
    }

    /**
     * 批量新增分项拓扑配置
     */
    @ApiOperationSupport(ignoreParameters = {
            "buildingEnergyCode","cascaded","parentId","subitemId","subitemName","children"
    })
    @PreAuthorize("@ss.hasPermi('subitemConfig:config:add')")
    @Log(title = "批量新增分项拓扑配置", businessType = BusinessType.INSERT)
    @ApiOperation("批量新增分项拓扑配置")
    @PostMapping("/addBatch")
    public AjaxResult addBatch(@RequestBody SubitemConfig subitemConfig) {
        return subitemConfigService.insertSubitemBatch(subitemConfig);
    }

    /**
     * 新增分项拓扑配置
     */
    @PreAuthorize("@ss.hasPermi('subitemConfig:config:add')")
    @Log(title = "新增分项拓扑配置", businessType = BusinessType.INSERT)
    @ApiOperation("新增分项拓扑配置")
    @PostMapping
    @ApiOperationSupport(ignoreParameters = {"subitemId","children"})
    public AjaxResult add(@Validated @RequestBody SubitemConfig subitemConfig) {
        return subitemConfigService.insertSubitemConfig(subitemConfig);
    }

    /**
     * 修改分项拓扑配置
     */
    @PreAuthorize("@ss.hasPermi('subitemConfig:config:edit')")
    @Log(title = "修改分项拓扑配置", businessType = BusinessType.UPDATE)
    @ApiOperation("修改分项拓扑配置")
    @ApiOperationSupport(ignoreParameters = {"children"})
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SubitemConfig subitemConfig) {
        return subitemConfigService.updateSubitemConfig(subitemConfig);
    }

    /**
     * 删除分项拓扑配置
     */
    @PreAuthorize("@ss.hasPermi('subitemConfig:config:remove')")
    @Log(title = "删除分项拓扑配置", businessType = BusinessType.DELETE)
    @ApiOperation("删除分项拓扑配置")
    @DeleteMapping("/{subitemIds}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subitemIds", value = "分项ID集合,请以逗号隔开 例如(\"1\",\"2\")", dataType = "String", allowMultiple = true, paramType = "path", required = true)
    })
    public AjaxResult remove(@PathVariable String[] subitemIds) {
        return subitemConfigService.deleteSubitemConfigBySubitemIds(subitemIds);
    }

    /**
     * 包含支路 保存操作
     */
    @PreAuthorize("@ss.hasPermi('subitemConfig:config:edit')")
    @ApiOperation("包含支路 保存操作")
    @Log(title = "包含支路 保存操作", businessType = BusinessType.UPDATE)
    @PutMapping("/branch")
    public AjaxResult branch(@RequestBody @Validated SubitemBranchVo subitemBranchVo) {
        if (!subitemConfigService.saveAthenaBranchConfig(subitemBranchVo)) {
            return AjaxResult.error("包含支路失败！");
        } else {
            return AjaxResult.success("包含支路成功！");
        }
    }

    /**
     * 查询分项下支路列表
     */
    @ApiOperation("查询分项下支路列表")
    @PreAuthorize("@ss.hasPermi('subitemConfig:config:list')")
    @GetMapping("/listBranchById")
    public AjaxResult listBranchById(@RequestParam("subitemId") @ApiParam(value = "分项ID",required = true) String subitemId) {
        return AjaxResult.success(iSubitemBranchLinkService.selectAthenaBesBranchListById(subitemId));
    }
}
