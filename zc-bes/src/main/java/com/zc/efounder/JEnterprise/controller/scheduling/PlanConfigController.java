package com.zc.efounder.JEnterprise.controller.scheduling;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.scheduling.PlanCollection;
import com.zc.efounder.JEnterprise.domain.scheduling.PlanConfig;
import com.zc.efounder.JEnterprise.domain.scheduling.PlanController;
import com.zc.efounder.JEnterprise.service.scheduling.IPlanConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: gaojikun
 * @Description:计划编排
 * @Date: Created in 14:51 2022/11/5
 * @Modified By:
 */
@RestController
@RequestMapping("/deviceManagement/scheduling/PlanConfig")
@Api(value = "PlanConfigController", tags = {"计划编排"})
@ApiSupport(order = 46, author = "gaojikun")
public class PlanConfigController extends BaseController {
    @Autowired
    private IPlanConfigService planConfigService;


    /*********************************************************** 左侧树 **************************************************/

    /**
     * @Description: 左侧计划树
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @ApiOperation(value = "左侧计划树")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:list')")
    @PostMapping("/getAllPlanConfigListInfo")
    public AjaxResult getAllPlanConfigListInfo(@ApiIgnore PlanConfig planConfig) {
        return planConfigService.getAllPlanConfigListInfo(planConfig);
    }

    /**
     * @Description: 查询计划信息
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @ApiOperation(value = "查询计划信息")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:query')")
    @PostMapping("/getPlanConfigInfo")
    @ApiOperationSupport(includeParameters = "id")
    public AjaxResult getPlanConfigInfo(PlanConfig planConfig) {
        return planConfigService.getPlanConfigInfo(planConfig);
    }

    /**
     * @Description: 新增计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @ApiOperation(value = "新增计划")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:add')")
    @Log(title = "计划编排", businessType = BusinessType.INSERT)
    @PostMapping("/addPlanConfig")
    @ApiOperationSupport(ignoreParameters = "id")
    public AjaxResult addPlanConfig(PlanConfig planConfig) {
        return planConfigService.addPlanConfig(planConfig);
    }

    /**
     * @Description: 修改计划
     * @auther: gaojikun
     * @param: PlanConfig
     * @return: AjaxResult
     */
    @ApiOperation(value = "修改计划")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:edit')")
    @Log(title = "计划编排", businessType = BusinessType.UPDATE)
    @PostMapping("/editPlanConfig")
    public AjaxResult editPlanConfig(PlanConfig planConfig) {
        return planConfigService.editPlanConfig(planConfig);
    }

    /**
     * @Description: 删除计划
     * @auther: gaojikun
     * @param: Long[]
     * @return: AjaxResult
     */
    @ApiOperation(value = "删除计划")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:remove')")
    @Log(title = "计划编排", businessType = BusinessType.DELETE)
    @DeleteMapping("/delPlanConfig/{ids}")
    @ApiImplicitParam(name = "ids", value = "计划ID集合(以逗号隔开例如 1,2)", dataType = "long", paramType = "path", required = true, allowMultiple = true)
    public AjaxResult delPlanConfig(@PathVariable Long[] ids) {
        return planConfigService.delPlanConfig(ids);
    }


    /*********************************************************** 右侧列表 **************************************************/

    /**
     * @Description: 查询控制计划列表
     * @auther: gaojikun
     * @param: planController
     * @return: TableDataInfo
     */
    @ApiOperation(value = "查询控制计划列表")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:Controller:list')")
    @GetMapping("/planControl/list")
    @ApiOperationSupport(includeParameters = {"id", "planId", "name", "startDate", "endDate", "pageNum", "pageSize"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate", value = "计划开始日期", dataType = "Date"),
            @ApiImplicitParam(name = "endDate", value = "计划结束日期", dataType = "Date"),
            @ApiImplicitParam(name = "name", value = "计划名称"),
            @ApiImplicitParam(name = "planId", value = "所属计划id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "id", value = "唯一ID", dataType = "Long"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)", paramType = "query", dataType = "int"),
    })
    public TableDataInfo planControlList(PlanController planController) {
        startPage();
        List<PlanController> list = planConfigService.selectPlanControlList(planController);
        return getDataTable(list);
    }

    /**
     * @Description: 查询采集计划列表
     * @auther: gaojikun
     * @param: planCollection
     * @return: TableDataInfo
     */
    @ApiOperation(value = "查询采集计划列表")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:Collection:list')")
    @GetMapping("/planCollect/list")
    @ApiOperationSupport(includeParameters = {"planId", "pageNum", "pageSize"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "planId", value = "所属计划id", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)", paramType = "query", dataType = "int"),
    })
    public TableDataInfo planCollectList(PlanCollection planCollection) {
        startPage();
        List<PlanCollection> list = planConfigService.selectPlanCollectList(planCollection);
        return getDataTable(list);
    }

    /**
     * 导出控制-采集计划
     */
    @ApiOperation(value = "导出控制-采集计划")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:Controller:export')")
    @Log(title = "计划列表", businessType = BusinessType.EXPORT)
    @PostMapping("/PlanControl/export")
    @ApiOperationSupport(includeParameters = {"id", "planId", "name", "startDate", "endDate"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate", value = "计划开始日期", dataType = "Date"),
            @ApiImplicitParam(name = "endDate", value = "计划结束日期", dataType = "Date"),
            @ApiImplicitParam(name = "name", value = "计划名称"),
            @ApiImplicitParam(name = "planId", value = "所属计划id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "id", value = "唯一ID", dataType = "Long"),
    })
    public void export(HttpServletResponse response, PlanController planConfig) {
        List<PlanController> list = planConfigService.selectPlanControlList(planConfig);
        ExcelUtil<PlanController> util = new ExcelUtil<>(PlanController.class);
        util.exportExcel(response, list, "控制计划列表");

    }

    /**
     * @Description: 控制计划详情
     * @auther: gaojikun
     * @param: planController
     * @return: AjaxResult
     */
    @ApiOperation(value = "控制计划详情")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:Controller:getPlanControl')")
    @PostMapping("/getPlanControl")
    @ApiOperationSupport(includeParameters = "id")
    public AjaxResult getPlanControl(PlanController planController) {
        return planConfigService.selectPlanControllerById(planController);
    }

    /**
     * @Description: 新增控制计划
     * @auther: gaojikun
     * @param: planController
     * @return: AjaxResult
     */
    @ApiOperation(value = "新增控制计划")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:Controller:add')")
    @Log(title = "控制计划", businessType = BusinessType.INSERT)
    @PostMapping("/addPlanController")
    @ApiOperationSupport(ignoreParameters = "id")
    public AjaxResult addPlanController(PlanController planController) {
        return planConfigService.insertPlanController(planController);
    }

    /**
     * @Description: 修改控制计划
     * @auther: gaojikun
     * @param: planController
     * @return: AjaxResult
     */
    @ApiOperation(value = "修改控制计划")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:Controller:edit')")
    @Log(title = "控制计划", businessType = BusinessType.UPDATE)
    @PostMapping("/updatePlanControl")
    public AjaxResult updatePlanControl(PlanController planController) {
        return planConfigService.updatePlanController(planController);
    }

    /**
     * @Description: 删除控制计划
     * @auther: gaojikun
     * @param: ids
     * @return: AjaxResult
     */
    @ApiOperation(value = "删除控制计划")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:Controller:remove')")
    @Log(title = "控制计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/deletePlanControl/{ids}")
    @ApiImplicitParam(name = "ids", value = "控制计划ID集合(以逗号隔开例如 1,2)", dataType = "long", paramType = "path", required = true, allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids) {
        return planConfigService.deletePlanControllerByIds(ids);
    }

    /**
     * @Description: 控制计划-模式点位同步
     * @auther: gaojikun
     * @param: planController
     * @return: AjaxResult
     */
    @ApiOperation(value = "控制计划-模式点位同步")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:Controller:sync')")
    @PostMapping("/modelPointSync")
    @ApiOperationSupport(includeParameters = {"id", "modelControlId", "active", "name", "alias", "planType",
            "startDate", "startTime", "endDate", "endTime", "executionWay", "weekMask", "", "sceneControlId", "sceneType"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "weekMask", value = "周掩码 周执行频率7位二进制，从低位到高位依次代表周一到周日 例 1111100  周六周日不执行", required = true),
            @ApiImplicitParam(name = "sceneType", value = "计划类型 0：控制、1：采集", required = true),
            @ApiImplicitParam(name = "planType", value = "替代日 0：禁止、1：使能", required = true, dataType = "long"),
    })
    public AjaxResult modelPointSync(PlanController planController) {
        return planConfigService.modelPointSync(planController);
    }

    /**
     * @Description: 控制计划-计划数据对比
     * @auther: gaojikun
     * @param: planController
     * @return: AjaxResult
     */
    @ApiOperation(value = "控制计划-计划数据对比")
    @PreAuthorize("@ss.hasPermi('Scheduling:PlanConfig:Controller:contrast')")
    @PostMapping("/planPointContrast")
    @ApiOperationSupport(includeParameters = {"id", "modelControlId"})
    public AjaxResult planPointContrast(PlanController planController) {
        return planConfigService.planPointContrast(planController);
    }
}
