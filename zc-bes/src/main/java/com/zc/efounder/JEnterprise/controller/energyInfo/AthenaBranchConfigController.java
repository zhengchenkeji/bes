package com.zc.efounder.JEnterprise.controller.energyInfo;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.energyInfo.vo.MeterDataVo;
import com.zc.efounder.JEnterprise.service.energyInfo.AthenaBranchConfigService;
import com.zc.efounder.JEnterprise.service.energyInfo.impl.AthenaBranchMeterLinkServiceImpl;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支路拓扑配置Controller
 *
 * @author qindehua
 */
@RestController
@Api(value = "支路拓扑配置", tags = {"支路拓扑配置"})
@ApiSupport(author = "QinDeHua", order = 3)
@RequestMapping("/branchConfig/config")
public class AthenaBranchConfigController extends BaseController {
    @Autowired
    private AthenaBranchConfigService athenaBranchConfigService;
    @Autowired
    private AthenaBranchMeterLinkServiceImpl athenaBranchMeterLinkService;

    /**
     * 查询支路拓扑配置列表
     */
    @PreAuthorize("@ss.hasPermi('branchConfig:config:list')")
    @ApiOperation("查询支路拓扑配置列表")
    @GetMapping("/list")
    @ApiOperationSupport(includeParameters = {
            "branchCode", "branchName", "energyCode", "parkCode","code","branchId","pageNum","pageSize"
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "搜索按钮标识 0:节点查询 1:搜索按钮查询",  paramType = "query", required = true),
            @ApiImplicitParam(name = "branchName", value = "支路名称",  paramType = "query"),
            @ApiImplicitParam(name = "branchId", value = "支路ID",  paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "energyCode", value = "所属能源",  paramType = "query"),
            @ApiImplicitParam(name = "parkCode", value = "所属园区",  paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(AthenaBranchConfig athenaBranchConfig,
                              //搜索按钮标识 0:节点查询 1:搜索按钮查询
                               @RequestParam String code) {
        startPage();
        List<AthenaBranchConfig> list = athenaBranchConfigService.selectAthenaBranchConfigListSun(athenaBranchConfig, code);
        return getDataTable(list);
    }


    /**
     * 获取支路拓扑配置下拉树列表
     */
    @ApiOperationSupport(includeParameters = {
            "branchCode", "branchName", "energyCode", "parkCode","branchId"
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchName", value = "支路名称",  paramType = "query"),
            @ApiImplicitParam(name = "branchId", value = "支路ID",  paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "energyCode", value = "所属能源",  paramType = "query"),
            @ApiImplicitParam(name = "parkCode", value = "所属园区",  paramType = "query"),
    })
    @GetMapping("/treeSelect")
    @ApiOperation("获取支路拓扑配置下拉树列表")
    @PreAuthorize("@ss.hasPermi('branchConfig:config:list')")
    public AjaxResult treeSelect(AthenaBranchConfig athenaBranchConfig) {
        List<AthenaBranchConfig> list = athenaBranchConfigService.selectAthenaBranchConfigList(athenaBranchConfig);
        return AjaxResult.success(athenaBranchConfigService.buildTreeSelect(list));
    }

//    /**
//     * 导出支路拓扑配置列表
//     */
//    @PreAuthorize("@ss.hasPermi('branchConfig:config:export')")
//    @ApiOperation("导出支路拓扑配置列表")
//    @Log(title = "支路拓扑配置", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, AthenaBranchConfig athenaBranchConfig)
//    {
//        List<AthenaBranchConfig> list = athenaBranchConfigService.selectAthenaBranchConfigList(athenaBranchConfig);
//        ExcelUtil<AthenaBranchConfig> util = new ExcelUtil<>(AthenaBranchConfig.class);
//        util.exportExcel(response, list, "支路拓扑配置数据");
//    }

    /**
     * 获取支路拓扑配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('branchConfig:config:query')")
    @ApiOperation("获取支路拓扑配置详细信息")
    @GetMapping(value = "/{branchId}")
    public AjaxResult getInfo(@PathVariable("branchId") @ApiParam(value = "支路ID",required = true) Long branchId) {
        return AjaxResult.success(athenaBranchConfigService.selectAthenaBranchConfigByBranchId(branchId));
    }


    /**
     * 新增支路拓扑配置
     */
    @PreAuthorize("@ss.hasPermi('branchConfig:config:add')")
    @ApiOperation("新增支路拓扑配置")
    @ApiOperationSupport(ignoreParameters = {"branchId", "children"})
    @Log(title = "支路拓扑配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AthenaBranchConfig athenaBranchConfig) {
        return athenaBranchConfigService.insertAthenaBranchConfig(athenaBranchConfig);
    }

    /**
     * 修改支路拓扑配置
     */
    @PreAuthorize("@ss.hasPermi('branchConfig:config:edit')")
    @ApiOperation("修改支路拓扑配置")
    @ApiOperationSupport(ignoreParameters = "children")
    @Log(title = "支路拓扑配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AthenaBranchConfig athenaBranchConfig) {
        return athenaBranchConfigService.updateAthenaBranchConfig(athenaBranchConfig);
    }

    /**
     * 删除时查看是否关联分户及分项
     */
    @GetMapping("/getMessage/{branchIds}")
    @ApiOperation("删除时查看是否关联分户及分项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchIds", value = "支路ID集合,请以逗号隔开 例如(1,2)", dataType = "Long", allowMultiple = true, paramType = "path", required = true)
    })
    @PreAuthorize("@ss.hasPermi('branchConfig:config:remove')")
    public AjaxResult treeSelect( @PathVariable Long[] branchIds) {
        return athenaBranchConfigService.getMessage(branchIds);
    }

    /**
     * 删除支路拓扑配置
     */
    @PreAuthorize("@ss.hasPermi('branchConfig:config:remove')")
    @ApiOperation("删除支路拓扑配置")
    @Log(title = "支路拓扑配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{branchIds}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "branchIds", value = "支路ID集合,请以逗号隔开 例如(1,2)", dataType = "Long", allowMultiple = true, paramType = "path", required = true)
    })
    public AjaxResult remove(@PathVariable Long[] branchIds) {
        return athenaBranchConfigService.deleteAthenaBranchConfigByBranchIds(branchIds);
    }


    /**
     * 包含电表保存操作
     */
    @PreAuthorize("@ss.hasPermi('branchConfig:config:edit')")
    @ApiOperation("包含电表保存操作")
    @Log(title = "包含电表操作", businessType = BusinessType.UPDATE)
    @PutMapping("/meter")
    public AjaxResult meter(@RequestBody @Validated MeterDataVo meterDataVo) {
        if (athenaBranchConfigService.saveAthenaBranchConfigMeter(meterDataVo)) {
            return AjaxResult.success("包含电表成功!");
        } else {
            return AjaxResult.error("包含电表失败！");
        }
    }


    /**
     * 查询当前支路下电表列表
     */
    @PreAuthorize("@ss.hasPermi('branchConfig:config:list')")
    @ApiOperation("查询当前支路下电表列表")
    @GetMapping("/meterList")
    public AjaxResult meterList(@RequestParam("branchId")
                                @ApiParam(name = "branchId", value = "支路ID", required = true) Long branchId,
                                @RequestParam("energyCode")
                                @ApiParam(name = "energyCode", value = "能源编号", required = true) String energyCode) {
        return AjaxResult.success(athenaBranchMeterLinkService.selectAthenaBranchMeterLinkList(branchId, energyCode));
    }

    /**
     * 查询所有建筑列表
     */
    @ApiOperation("查询所有建筑列表")
    @GetMapping("/buildingList")
    public AjaxResult buildingList(@RequestParam("parkCode")
                                   @ApiParam(name = "parkCode", value = "园区编号", required = true) String parkCode) {
        return AjaxResult.success(athenaBranchConfigService.selectBuildingList(parkCode));
    }

    /**
     * 根据能源，园区查询支路列表
     */
    @PreAuthorize("@ss.hasPermi('branchConfig:config:list')")
    @ApiOperation("根据能源,园区查询支路列表")
    @GetMapping("/branchList")
    @ApiOperationSupport(includeParameters = {
            "parkCode", "energyCode"
    })
    public AjaxResult branchList(AthenaBranchConfig athenaBranchConfig) {
        return AjaxResult.success(athenaBranchConfigService.selectAthenaBranchConfigList(athenaBranchConfig));
    }

    /**
     * @description:获取所有支路数据
     * @author: sunshangeng
     * @date: 2022/11/14 15:55
     * @param: [athenaBranchConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @ApiOperation("获取所有支路数据")
    @GetMapping("/selectBranchTreeList")
    public AjaxResult selectBranchTreeList() {
        return athenaBranchConfigService.SelectBranchTreeList();
    }

}
