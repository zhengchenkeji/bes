package com.zc.efounder.JEnterprise.controller.energyInfo;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.AthenaBesHouseholdConfig;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.energyInfo.vo.BranchVo;
import com.zc.efounder.JEnterprise.service.energyInfo.AthenaBesHouseholdBranchLinkService;
import com.zc.efounder.JEnterprise.service.energyInfo.AthenaBesHouseholdConfigService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分户计量拓扑配置Controller
 *
 * @author qindehua
 * @date 2022-09-19
 */
@RestController
@Api(value = "分户拓扑配置", tags = {"分户拓扑配置"})
@ApiSupport(author = "QinDeHua",order = 4)
@RequestMapping("/householdConfig/config")
public class AthenaBesHouseholdConfigController extends BaseController {
    @Autowired
    private AthenaBesHouseholdConfigService athenaBesHouseholdConfigService;
    @Autowired
    private AthenaBesHouseholdBranchLinkService athenaBesHouseholdBranchLinkService;

    /**
     * 查询分户计量拓扑配置列表
     */
    @ApiOperation("查询分户拓扑配置列表")
    @PreAuthorize("@ss.hasPermi('householdConfig:config:list')")
    @GetMapping("/list")
    @ApiOperationSupport(includeParameters = {
            "householdCode", "householdName", "energyCode", "parkCode","code","householdId","pageNum","pageSize"
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "搜索按钮标识 0:节点查询 1:搜索按钮查询",  paramType = "query", required = true),
            @ApiImplicitParam(name = "householdName", value = "分户名称",  paramType = "query"),
            @ApiImplicitParam(name = "householdId", value = "分户ID",  paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "energyCode", value = "所属能源",  paramType = "query"),
            @ApiImplicitParam(name = "parkCode", value = "所属园区",  paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(AthenaBesHouseholdConfig athenaBesHouseholdConfig,
                              //搜索按钮标识 0:节点查询 1:搜索按钮查询
                               String code) {
        startPage();
        List<AthenaBesHouseholdConfig> list = athenaBesHouseholdConfigService.selectAthenaBesHouseholdConfigListSun(athenaBesHouseholdConfig, code);
        return getDataTable(list);
    }

    /**
     * 查询分户下支路列表
     */
    @ApiOperation("查询分户下支路列表")
    @PreAuthorize("@ss.hasPermi('householdConfig:config:list')")
    @GetMapping("/listBranchById")
    public AjaxResult listBranchById(@RequestParam @ApiParam(value = "分户ID",required = true) Long householdId) {
        return AjaxResult.success(athenaBesHouseholdBranchLinkService.selectAthenaBesBranchListById(householdId));
    }

    /**
     * 获取分户拓扑配置下拉树列表
     */
    @ApiOperationSupport(includeParameters = {
            "householdCode", "householdName", "energyCode", "parkCode","householdId"
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "householdName", value = "分户名称",  paramType = "query"),
            @ApiImplicitParam(name = "householdId", value = "分户ID",  paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "energyCode", value = "所属能源",  paramType = "query"),
            @ApiImplicitParam(name = "parkCode", value = "所属园区",  paramType = "query"),
    })
    @GetMapping("/treeSelect")
    @ApiOperation("获取分户拓扑配置下拉树列表")
    @PreAuthorize("@ss.hasPermi('householdConfig:config:list')")
    public AjaxResult treeSelect(AthenaBesHouseholdConfig athenaBesHouseholdConfig) {
        List<AthenaBesHouseholdConfig> list = athenaBesHouseholdConfigService.selectAthenaBesHouseholdConfigList(athenaBesHouseholdConfig);
        return AjaxResult.success(athenaBesHouseholdConfigService.buildTreeSelect(list));
    }

//    /**
//     * 导出分户计量拓扑配置列表
//     */
//    @PreAuthorize("@ss.hasPermi('householdConfig:config:export')")
//    @Log(title = "分户计量拓扑配置", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, AthenaBesHouseholdConfig athenaBesHouseholdConfig)
//    {
//        List<AthenaBesHouseholdConfig> list = athenaBesHouseholdConfigService.selectAthenaBesHouseholdConfigList(athenaBesHouseholdConfig);
//        ExcelUtil<AthenaBesHouseholdConfig> util = new ExcelUtil<>(AthenaBesHouseholdConfig.class);
//        util.exportExcel(response, list, "分户计量拓扑配置数据");
//    }

    /**
     * 获取分户计量拓扑配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('householdConfig:config:query')")
    @ApiOperation("获取分户拓扑配置详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id")@ApiParam(value = "分户ID",required = true) Long id) {
        return AjaxResult.success(athenaBesHouseholdConfigService.selectAthenaBesHouseholdConfigById(id));
    }

    /**
     * 新增分户计量拓扑配置
     */
    @PreAuthorize("@ss.hasPermi('householdConfig:config:add')")
    @ApiOperation("新增分户计量拓扑配置")
    @ApiOperationSupport(ignoreParameters = {"householdId","children"})
    @Log(title = "分户计量拓扑配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AthenaBesHouseholdConfig athenaBesHouseholdConfig) {
        return athenaBesHouseholdConfigService.insertAthenaBesHouseholdConfig(athenaBesHouseholdConfig);
    }

    /**
     * 修改分户计量拓扑配置
     */
    @PreAuthorize("@ss.hasPermi('householdConfig:config:edit')")
    @ApiOperation("修改分户计量拓扑配置")
    @ApiOperationSupport(ignoreParameters = "children")
    @Log(title = "修改分户计量拓扑配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AthenaBesHouseholdConfig athenaBesHouseholdConfig) {
        return athenaBesHouseholdConfigService.updateAthenaBesHouseholdConfig(athenaBesHouseholdConfig);
    }

    /**
     * 删除分户计量拓扑配置
     */
    @PreAuthorize("@ss.hasPermi('householdConfig:config:remove')")
    @ApiOperation("删除分户计量拓扑配置")
    @Log(title = "删除分户计量拓扑配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "分户ID集合,请以逗号隔开 例如(1,2)",dataType = "Long", allowMultiple = true, paramType = "path", required = true)
    })
    public AjaxResult remove(@PathVariable Long[] ids) {
        return athenaBesHouseholdConfigService.deleteAthenaBesHouseholdConfigByIds(ids);
    }

    /**
     * 包含支路 保存操作
     */
    @PreAuthorize("@ss.hasPermi('householdConfig:config:edit')")
    @ApiOperation("包含支路 保存操作")
    @Log(title = "包含支路 保存操作", businessType = BusinessType.UPDATE)
    @PutMapping("/branch")
    public AjaxResult branch(@RequestBody @Validated BranchVo branchVo) {
        if (!athenaBesHouseholdConfigService.saveAthenaBranchConfig(branchVo)) {
            return AjaxResult.error("包含支路失败！");
        } else {
            return AjaxResult.success("包含支路成功！");
        }

    }
}
