package com.zc.efounder.JEnterprise.controller.scheduling;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneModelControl;
import com.zc.efounder.JEnterprise.domain.scheduling.SceneModelPointControl;
import com.zc.efounder.JEnterprise.service.scheduling.SceneModelService;
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
 * @Author:gaojikun
 * @Date:2022-11-08 11:11
 * @Description: 场景模式
 */
@RestController
@RequestMapping("/deviceManagement/scheduling/scenarioConfig/model")
@Api(value = "SceneModelController", tags = {"场景模式"})
@ApiSupport(order = 48,author = "gaojikun")
public class SceneModelController {

    @Resource
    private SceneModelService sceneModelService;

    /**
     * @Description: 获取场景模式列表
     * @auther: gaojikun
     * @param: sceneModelControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "获取场景模式列表")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:list')")
    @PostMapping("/getSceneModelList")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId",value = "场景id",dataType = "long"),
            @ApiImplicitParam(name = "name",value = "模式名称"),
    })
    public AjaxResult getSceneModelList(SceneModelControl sceneModelControl) {
        return sceneModelService.getSceneModelList(sceneModelControl);
    }

    /**
     * @Description: 根据id获取场景模式
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "根据id获取场景模式")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:getSceneModel')")
    @PostMapping("/getSceneModel")
    @ApiOperationSupport(includeParameters = "id")
    public AjaxResult getSceneModel(SceneModelControl sceneModelControl) {
        return sceneModelService.getSceneModel(sceneModelControl);
    }

    /**
     * @Description: 添加场景模式
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "添加场景模式")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:add')")
    @PostMapping("/addSceneModel")
    @ApiOperationSupport(ignoreParameters = "id")
    public AjaxResult addSceneModel(SceneModelControl sceneModelControl) {
        return sceneModelService.addSceneModel(sceneModelControl);
    }

    /**
     * @Description: 修改场景模式
     * @auther: gaojikun
     * @param: sceneControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "修改场景模式")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:edit')")
    @PostMapping("/updateSceneModel")
    public AjaxResult updateSceneModel(SceneModelControl sceneModelControl) {
        return sceneModelService.updateSceneModel(sceneModelControl);
    }

    /**
     * @Description: c
     * @auther: gaojikun
     * @param: ids
     * @return: AjaxResult
     */
    @ApiOperation(value = "删除场景模式")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:remove')")
    @DeleteMapping("/deleteSceneModel/{ids}")
    @ApiImplicitParam(name = "ids",value = "场景信息ID集合(以逗号隔开例如 1,2)",dataType = "Long",paramType = "path",required = true,allowMultiple = true)
    public AjaxResult deleteSceneModel(@PathVariable("ids") Long[] ids) {
        return sceneModelService.deleteSceneModel(ids);
    }

    /**
     * 导出场景模式列表
     */
    @ApiOperation(value = "导出场景模式列表")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:export')")
    @Log(title = "计划场景模式", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sceneId",value = "场景id",dataType = "long"),
            @ApiImplicitParam(name = "name",value = "模式名称"),
    })
    public void export(HttpServletResponse response, SceneModelControl sceneModelControl)
    {
        List<SceneModelControl> list = sceneModelService.getSceneModelListReturnList(sceneModelControl);
        ExcelUtil<SceneModelControl> util = new ExcelUtil<>(SceneModelControl.class);
        util.exportExcel(response, list, "计划场景模式列表");
    }


    /**
     * @Description: 获取场景模式点位列表
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "获取场景模式点位列表")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:getSceneModelPoint')")
    @PostMapping("/getSceneModelPoint")
    @ApiOperationSupport(includeParameters = "sceneModelId")
    public AjaxResult getSceneModelPoint(SceneModelPointControl sceneModelPointControl) {
        return sceneModelService.getSceneModelPoint(sceneModelPointControl);
    }

    /**
     * @Description: 添加场景模式点位配置
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "添加场景模式点位配置")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:addSceneModelPoint')")
    @PostMapping("/addSceneModelPoint")
    @ApiImplicitParam(name = "sceneModelId",value = "模式id",dataType = "long")
    @ApiOperationSupport(ignoreParameters = {"id"})
    public AjaxResult addSceneModelPoint(SceneModelPointControl sceneModelPointControl) {
        return sceneModelService.addSceneModelPoint(sceneModelPointControl);
    }


    /**
     * @Description: 场景模式同步
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "场景模式同步")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:sync')")
    @PostMapping("/modelPointSync")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "模式名称"),
    })
    public AjaxResult modelPointSync(SceneModelControl sceneModelControl) {
        return sceneModelService.modelPointSync(sceneModelControl);
    }

    /**
     * @Description: 模式数据对比
     * @auther: gaojikun
     * @param: sceneModelPointControl
     * @return: AjaxResult
     */
    @ApiOperation(value = "模式数据对比")
    @PreAuthorize("@ss.hasPermi('scheduling:sceneConfig:model:contrast')")
    @PostMapping("/modelPointContrast")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "模式名称"),
    })
    public AjaxResult modelPointContrast(SceneModelControl sceneModelControl) {
        return sceneModelService.modelPointContrast(sceneModelControl);
    }
}
