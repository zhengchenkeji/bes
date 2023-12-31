package com.zc.efounder.JEnterprise.controller.sceneLink;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.job.TaskException;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.sceneLink.Scene;
import com.zc.efounder.JEnterprise.domain.sceneLink.SceneLog;
import com.zc.efounder.JEnterprise.service.sceneLink.SceneLinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * description:场景联动控制器
 * author: sunshangeng
 * date:2023/2/28 10:53
 */
@RestController
@RequestMapping("/sceneLink")
@Api(value = "SceneLinkController", tags = {"场景联动"})
@ApiSupport(author = "sunshangeng")
public class SceneLinkController  extends BaseController {





    @Resource
    private SceneLinkService sceneLinkService;
    /***
     * @description:新增场景
     * @author: sunshangeng
     * @date: 2023/3/3 14:43
     * @param: [scene]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    @ApiOperation(value = "新增场景联动")
    @Log(title = "新增场景联动", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add( @RequestBody Scene scene) throws TaskException, SchedulerException {
        return  sceneLinkService.insertScene(scene);
    }
    /**
     **
      * @description:场景信息
      * @author: sunshangeng
      * @date: 2023/3/3 14:43
      * @param: [id]
      * @return: com.ruoyi.common.core.domain.AjaxResult
      *
     */
    @ApiOperation(value = "获取场景联动详细信息")
    @GetMapping("/info/{id}")
    @ApiImplicitParam(name = "id",value = "场景ID",required = true,dataType = "long")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return sceneLinkService.getInfo(id);
    }


    /**
     * @description:修改场景信息
     * @author: sunshangeng
     * @date: 2023/3/3 14:42
     * @param: [scene]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    @ApiOperation(value = "修改场景信息")
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody Scene scene) throws TaskException, SchedulerException {
        return sceneLinkService.edit(scene);
    }

    /***
     * @description:删除场景
     * @author: sunshangeng
     * @date: 2023/3/3 14:42
     * @param: [id]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    @ApiOperation(value = "删除场景")
    @DeleteMapping("/del/{id}")
    @ApiImplicitParam(name = "id",value = "场景ID",required = true,dataType = "long")
    public AjaxResult del(@PathVariable("id") Long id) throws TaskException, SchedulerException {
        return sceneLinkService.del(id);
    }

    /**
     * @description:查询场景列表
     * @author: sunshangeng
     * @date: 2023/3/3 14:42
     * @param: [scene]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    @ApiOperation(value = "查询场景联动")
    @GetMapping("/list")
    public TableDataInfo list(Scene scene)
    {
        startPage();
        List<Scene> list = sceneLinkService.getSceneList(scene);
        return getDataTable(list);
    }


    @ApiOperation(value = "查询场景联动字典")
    @GetMapping("/listSceneDic")
    public TableDataInfo listSceneDic(Scene scene)
    {
        List<Scene> list = sceneLinkService.getSceneListDic(scene);
        return getDataTable(list);
    }


    /**
     * @description:获取所有场景
     * @author: sunshangeng
     * @date: 2023/3/3 14:42
     * @param: [scene]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    @ApiOperation(value = "查询场景联动")
    @GetMapping("/allSceneList")
    public TableDataInfo allSceneList(Scene scene)
    {
        startPage();
        List<Scene> list = sceneLinkService.getSceneList(scene);
        return getDataTable(list);
    }
    /**
     * @description:手动触发执行
     * @author: sunshangeng
     * @date: 2023/3/6 14:07
     * @param: [id]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @GetMapping("/execute/{id}")
    public  AjaxResult execute(@PathVariable("id") Long id){


        return  sceneLinkService.execute(id);
    }


    /**
     * 场景联状态修改
     */
    @Log(title = "场景联动", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody Scene scene) throws SchedulerException, TaskException {
        return sceneLinkService.changeStatus(scene);
    }

 /**
  * @description:
  * @author: sunshangeng
  * @date: 2023/4/23 16:48
  * @param: [deviceTree]
  * @return: com.ruoyi.common.core.domain.AjaxResult
  **/
 @GetMapping("/getbesDeviceList")
    public AjaxResult getbesDeviceList(DeviceTree deviceTree) {
        return sceneLinkService.getBesDeviceTree(deviceTree);
    }

    @ApiOperation(value = "查询场景联动")
    @GetMapping("/listlog")
    public TableDataInfo listlog(SceneLog sceneLog)
    {
        startPage();
        List<SceneLog> list = sceneLinkService.getSceneLog(sceneLog);
        return getDataTable(list);
    }


}
