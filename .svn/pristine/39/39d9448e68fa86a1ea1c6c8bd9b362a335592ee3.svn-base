package com.zc.efounder.JEnterprise.controller.scheduling;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneControl;
import com.zc.efounder.JEnterprise.domain.scheduling.SchedulingArea;
import com.zc.efounder.JEnterprise.mapper.scheduling.SceneConfigMapper;
import com.zc.efounder.JEnterprise.service.scheduling.SceneConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: wanghongjie
 * @Description:计划编排--场景配置
 * @Date: Created in 14:50 2022/11/5
 * @Modified By:
 */
@RestController
@RequestMapping("/deviceManagement/scheduling/scenarioConfig")
@Api(value = "SceneConfigController", tags = {"场景配置"})
@ApiSupport(order = 47,author = "gaojikun")
public class SceneConfigController {

    @Resource
    private SceneConfigService sceneConfigService;

    @Resource
    private SceneConfigMapper sceneConfigMapper;

    /**
     * @Description: 获取场景区域信息
     * @auther: wanghongjie
     * @date: 15:44 2022/11/5
     * @param:
     * @return:
     */
    @ApiOperation(value = "获取场景区域信息")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:sceneConfigAreaListInfo')")
    @PostMapping("/sceneConfigAreaListInfo")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId",value = "父节点id",dataType = "Long"),
            @ApiImplicitParam(name = "name",value = "区域名称"),
    })
    public AjaxResult sceneConfigAreaListInfo(SchedulingArea schedulingArea) {
        return sceneConfigService.sceneConfigAreaListInfo(schedulingArea);
    }

    /**
     * @Description: 添加区域
     * @auther: wanghongjie
     * @date: 17:36 2022/11/5
     * @param:
     * @return:
     */
    @ApiOperation(value = "添加区域")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:addSceneConfigArea')")
    @PostMapping("/addSceneConfigArea")
    @ApiOperationSupport(ignoreParameters = "id")
    public AjaxResult addSceneConfigArea(SchedulingArea schedulingArea) {
        return sceneConfigService.addSceneConfigArea(schedulingArea);
    }

    /**
     * @Description: 修改区域
     * @auther: wanghongjie
     * @date: 17:36 2022/11/5
     * @param:
     * @return:
     */
    @ApiOperation(value = "修改区域")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:updateSceneConfigArea')")
    @ApiOperationSupport(ignoreParameters = "parentId")
    @PostMapping("/updateSceneConfigArea")
    public AjaxResult updateSceneConfigArea(SchedulingArea schedulingArea) {
        return sceneConfigService.updateSceneConfigArea(schedulingArea);
    }

    /**
     * @Description: 删除区域
     * @auther: wanghongjie
     * @date: 17:36 2022/11/5
     * @param:
     * @return:
     */
    @ApiOperation(value = "删除区域")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:deleteSceneConfigArea')")
    @DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "区域ID集合(以逗号隔开例如 1,2)",dataType = "Long",paramType = "path",required = true,allowMultiple = true)
    public AjaxResult deleteSceneConfigArea(@PathVariable("ids") Long[] ids) {
        return sceneConfigService.deleteSceneConfigArea(ids);
    }

    /**
     * @Description: 获取场景列表
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "获取场景列表")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:list')")
    @GetMapping("/getSceneControlList")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "场景名称"),
            @ApiImplicitParam(name = "alias",value = "场景别名"),
    })
    public AjaxResult getSceneControlList(SceneControl sceneControl) {
        return sceneConfigService.getSceneControlList(sceneControl);
    }

    /**
     * @Description: 获取所有场景列表
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @ApiOperationSupport(ignoreParameters = {"id","alias"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "场景名称"),
            @ApiImplicitParam(name = "schedulingAreaId",value = "区域id"),
            @ApiImplicitParam(name = "active",value = "使能状态 0=关闭,1=开启",dataType = "int"),
    })
    @ApiOperation(value = "获取所有场景列表")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:list')")
    @PostMapping("/getAllSceneControlList")
    public AjaxResult getAllSceneControlList(SceneControl sceneControl) {
        return  AjaxResult.success(sceneConfigMapper.getSceneControlList(sceneControl));
    }



    /**
     * @Description: 根据id获取场景列表
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "根据id获取场景列表")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:getsceneConfig')")
    @PostMapping("/getsceneConfig")
    @ApiOperationSupport(includeParameters = "id")
    public AjaxResult getsceneConfig(SceneControl sceneControl) {
        return sceneConfigService.getsceneConfig(sceneControl);
    }

    /**
     * @Description: 添加场景信息
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "添加场景信息")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:add')")
    @PostMapping("/addSceneConfig")
    @ApiOperationSupport(ignoreParameters = "id")
    public AjaxResult addSceneConfig(SceneControl sceneControl) {
        return sceneConfigService.addSceneConfig(sceneControl);
    }

    /**
     * @Description: 修改场景信息
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "修改场景信息")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:edit')")
    @PostMapping("/updateSceneConfig")
    @ApiOperationSupport(ignoreParameters = {"schedulingAreaId","alias"})
    public AjaxResult updateSceneConfig(SceneControl sceneControl) {
        return sceneConfigService.updateSceneConfig(sceneControl);
    }

    /**
     * @Description: 删除场景信息
     * @auther: gaojikun
     * @param: ids
     * @return: AjaxResult
     */
    @ApiOperation(value = "删除场景信息")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:remove')")
    @DeleteMapping("/deleteSceneConfig/{ids}")
    @ApiImplicitParam(name = "ids",value = "场景信息ID集合(以逗号隔开例如 1,2)",dataType = "Long",paramType = "path",required = true,allowMultiple = true)
    public AjaxResult deleteSceneConfig(@PathVariable("ids") Long[] ids) {
        return sceneConfigService.deleteSceneConfig(ids);
    }

    /**
     * 导出场景列表
     */
    @ApiOperation(value = "导出场景列表")
    @PreAuthorize("@ss.hasPermi('scheduling:scenarioConfig:export')")
    @Log(title = "计划场景", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "场景名称"),
            @ApiImplicitParam(name = "alias",value = "场景别名"),
    })
    public void export(HttpServletResponse response, SceneControl sceneControl)
    {
        List<SceneControl> list = sceneConfigService.getSceneControlListReturnList(sceneControl);
        ExcelUtil<SceneControl> util = new ExcelUtil<>(SceneControl.class);
        util.exportExcel(response, list, "计划场景列表");
    }
}
