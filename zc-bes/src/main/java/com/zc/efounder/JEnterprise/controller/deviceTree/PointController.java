package com.zc.efounder.JEnterprise.controller.deviceTree;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.service.deviceTree.PointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 虚点，总线
 *
 * @author gaojikun
 * @date 2022-09-14
 */
@RestController
@RequestMapping("/basicData/point")
@Api(value = "虚点，总线", tags = {"虚点，总线"})
@ApiSupport(order = 10,author = "gaojikun")
public class PointController extends BaseController {
    @Autowired
    private PointService pointService;


    /**
     * @Description: 添加树节点
     * @auther: gaojikun
     * @param:deviceTree
     * @return:AjaxResult
     */
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:insertDeviceTreee')")
    @ApiOperation("添加树节点")
    @PostMapping("/insertDeviceTreee")
    @ApiOperationSupport(ignoreParameters = {"id","treeId"})
    public AjaxResult insertDeviceTreee(BuildNode buildNode) {
        return pointService.insertDeviceTreee(buildNode);
    }

    /**
     * @Description: 修改树节点
     * @auther: gaojikun
     * @param:deviceTree
     * @return:AjaxResult
     */
    @ApiOperation("修改树节点")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:updateDeviceTreee')")
    @PostMapping("/updateDeviceTreee")
    public AjaxResult updateDeviceTreee(BuildNode buildNode) {
        return pointService.updateDeviceTreee(buildNode);
    }

    /**
     * @Description: 添加虚点
     * @auther: gaojikun
     * @param:point
     * @return:AjaxResult
     */
    @ApiOperation("添加虚点")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:insertPoint')")
    @PostMapping("/insertPoint")
    @ApiOperationSupport(ignoreParameters = {"treeId"})
    public AjaxResult insertPoint(Point point) {
        return pointService.insertPoint(point);
    }

    /**
     * @Description: 修改点位
     * @auther: gaojikun
     * @param:point
     * @return:AjaxResult
     */
    @ApiOperation("修改点位")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:updatePoint')")
    @PostMapping("/updatePoint")
    public AjaxResult updatePoint(Point point) {
        return pointService.updatePoint(point);
    }

    /**
     * @Description: 查询虚点类型
     * @auther: gaojikun
     */
    @ApiOperation("查询虚点类型")
//    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:listVpoint')")
    @GetMapping("/listVpoint")
    public AjaxResult listVpoint() {
        return pointService.listVpoint();
    }


    /**
     * @description: 给照明子节点添加到树节点
     * @author: sunshangeng
     * @date: 2022/9/26 17:44
     * @param:
     * @return:
     **/
    @ApiOperation("给照明子节点添加到树节点")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:insertLightingTree')")
    @PostMapping("/insertLightingTree")
    @ApiOperationSupport(ignoreParameters = {"id","treeId",""})
    public AjaxResult insertLightingTree(BuildNode buildNode)
    {
        return pointService.insertLightingTree(buildNode);
    }

    /**
     * @description: 修改照明子节点添加到树节点
     * @author: sunshangeng
     * @date: 2022/9/26 17:44
     * @param:
     * @return:
     **/
    @ApiOperation("修改照明子节点添加到树节点")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:updateLightingTree')")
    @PostMapping("/updateLightingTree")
    @ApiOperationSupport(ignoreParameters = {"park"})
    public AjaxResult updateLightingTree(BuildNode buildNode)
    {
        return pointService.updateLightingTree(buildNode);
    }

    /**
     * 调试一个逻辑点的初始值
     * @param pointControlCommand
     * @return AjaxResult
     * @author: gaojikun
     */
    @ApiOperation("调试一个逻辑点的初始值")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:debugPointInfo')")
    @GetMapping("/debugPointInfo")
    public AjaxResult debugPointInfo(PointControlCommand pointControlCommand){
        return pointService.debugPointInfo(pointControlCommand);
    }

    /**
     * 点值配置
     * @param NodeConfigSet
     * @return AjaxResult
     * @author: gaojikun
     */
    @ApiOperation("点值配置")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:debuggerEditPointValue')")
    @GetMapping("/debuggerEditPointValue")
    public AjaxResult debuggerEditPointValue(NodeConfigSet NodeConfigSet){
        return pointService.debuggerEditPointValue(NodeConfigSet);
    }

    /**
     * 查询点值配置
     * @param NodeConfigSet
     * @return AjaxResult
     * @author: gaojikun
     */
    @ApiOperation("查询点值配置")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:selectEditPointValue')")
    @GetMapping("/selectEditPointValue")
    @ApiOperationSupport(ignoreParameters = {"list"})
    public AjaxResult selectEditPointValue(NodeConfigSet NodeConfigSet){
        return pointService.selectEditPointValue(NodeConfigSet);
    }

    /**
     *
     * @Description:批量下发点位值
     *
     * @auther: wanghongjie
     * @date: 17:03 2022/11/3
     * @param:
     * @return:
     *
     */
    @ApiOperation("批量下发点位值")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:debugPointListInfo')")
    @PostMapping("/debugPointListInfo")
    public AjaxResult debugPointListInfo(@RequestBody List<PointControlCommand> list) throws NoSuchAlgorithmException {
        return pointService.debugPointListInfo(list);
    }

    /**
     *
     * @Description: 第三方协议下发获取实时值
     *
     * @auther: wanghongjie
     * @date: 17:33 2023/4/10
     * @param: [pointControlCommand]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    @GetMapping("/debugPointRealTimeInfo")
    public AjaxResult debugPointRealTimeInfo(PointControlCommand pointControlCommand) throws NoSuchAlgorithmException {
        return pointService.debugPointRealTimeInfo(pointControlCommand);
    }

    /**
     *
     * @Description: 设计器批量获取实时值
     *
     * @auther: wanghongjie
     * @date: 14:30 2023/5/29
     * @param: [list]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    @PostMapping("/debugPointListRealTimeInfo")
    public AjaxResult debugPointListRealTimeInfo(@RequestBody List<RealTimeDataParam> list) throws NoSuchAlgorithmException {
        return pointService.debugPointListRealTimeInfo(list);
    }

}
