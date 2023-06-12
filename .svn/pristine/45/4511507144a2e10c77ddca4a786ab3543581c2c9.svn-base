package com.zc.efounder.JEnterprise.controller.deviceTree;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTree.RealTimeDataParam;
import com.zc.efounder.JEnterprise.domain.deviceTreeNode.AthenaDeviceNode;
import com.zc.efounder.JEnterprise.service.deviceTree.DeviceTreeService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 15:05 2022/9/8
 * @Modified By:
 */
@RestController
@Api(value = "设备树", tags = {"设备树"})
@ApiSupport( order = 7)
@RequestMapping("/basicData/deviceTree")
public class DeviceTreeController extends BaseController {

    @Resource
    private DeviceTreeService deviceTreeService;

    /**
     * 查询设备树列表
     */
    @ApiOperation("查询设备树列表")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:list')")
    @GetMapping("/listTree")
    @ApiOperationSupport(includeParameters = {
            "deviceTreeFatherId"
    })
    @ApiImplicitParam(name = "deviceTreeFatherId", value = "父设备id", paramType = "query",required = true,dataType = "int")
    public AjaxResult listTree(DeviceTree deviceTree) {
        List<DeviceTree> list = deviceTreeService.selectDeviceTreeList(deviceTree);
        return AjaxResult.success(list);
    }

    /**
     *
     * @Description: 获取所有的设备树节点
     *
     * @auther: wanghongjie
     * @date: 16:24 2022/10/24
     * @param:
     * @return:
     *
     */
    @ApiOperation("获取所有的设备树节点")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:allList')")
    @GetMapping("/allListTree")
    public AjaxResult allListTree() {
        AjaxResult ajaxResult = deviceTreeService.selectDeviceTreeAllList();
        return ajaxResult;
    }

    /**
     * @Description:删除树节点操作
     * @auther: wanghongjie
     * @date: 16:48 2022/9/16
     * @param:
     * @return:
     */
    @ApiOperation("删除树节点操作")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:remove')")
    @PostMapping("/deleteTreeNode")
    public AjaxResult deleteTreeNode(DeviceTree deviceTree) {
        AjaxResult ajaxResult = deviceTreeService.deleteTreeNode(deviceTree);

        return ajaxResult;
    }


    /**
     * @Description: 根据节点类型查询相应的按钮
     * @auther: wanghongjie
     * @date: 11:40 2022/9/9
     * @param:
     * @return:
     */
    @ApiOperation("根据节点类型查询相应的按钮")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:getButtonByTreeType')")
    @PostMapping("/getButtonByTreeType")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceNodeCode", value = "节点编号", paramType = "query"),
            @ApiImplicitParam(name = "deviceNodeId", value = "节点ID", paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "deviceNodeIsNode", value = "是否节点", paramType = "query"),
            @ApiImplicitParam(name = "deviceNodeName", value = "节点名称", paramType = "query"),
    })
    public AjaxResult getButtonByTreeType(AthenaDeviceNode deviceNode) {
        List<AthenaDeviceNode> list = deviceTreeService.getButtonByTreeType(deviceNode);
        if (list.size() == 0) {
            return AjaxResult.error("获取按钮失败");
        }
        return AjaxResult.success(list);
    }

    /**
     * @Description: 获取当前点击节点的详细信息
     * @auther: wanghongjie
     * @date: 10:54 2022/9/16
     * @param: [deviceNode]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @ApiOperation("获取当前点击节点的详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:getTreeNodeManage')")
    @GetMapping("/getTreeNodeManage")
    @ApiOperationSupport(includeParameters = {
            "deviceNodeId","deviceTreeId"
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceNodeId", value = "节点ID", paramType = "query",required = true,dataType = "int"),
            @ApiImplicitParam(name = "deviceTreeId", value = "树ID", paramType = "query",required = true,dataType = "int"),
    })
    public AjaxResult getTreeNodeManage(DeviceTree deviceTree) {
        AjaxResult ajaxResult = deviceTreeService.getTreeNodeManage(deviceTree);

        return ajaxResult;
    }

    /**
     *
     * @Description: 根据点位id获取点位实时值
     *
     * @auther: wanghongjie
     * @date: 15:22 2022/11/2
     * @param:
     * @return:
     *
     */
    @ApiOperation("根据点位id获取点位实时值")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:getRealTimeData')")
    @GetMapping("/getRealTimeData")
    public AjaxResult getRealTimeData(@RequestParam @ApiParam(value = "点位ID",required = true) Long id,
                                      @ApiParam(value = "0:bes  1:第三方",required = true) String pointType,
                                      @ApiParam(value = "设备id",required = true) Long equipmentId) {

        AjaxResult ajaxResult = deviceTreeService.getRealTimeData(id,pointType,equipmentId);

        return ajaxResult;
    }
    /**
     *
     * @Description: 设计器根据所有的点位id获取点位实时值
     *
     * @auther: wanghongjie
     * @date: 9:17 2023/5/25
     * @param:
     * @return:
     *
     */
    @ApiOperation("批量根据点位id获取点位实时值")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:getAllRealTimeData')")
    @PostMapping("/getAllRealTimeData")
    public AjaxResult getAllRealTimeData(@RequestBody List<RealTimeDataParam> realTimeDataParamList) {

        AjaxResult ajaxResult = deviceTreeService.getAllRealTimeData(realTimeDataParamList);

        return ajaxResult;
    }
}
