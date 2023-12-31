package com.zc.efounder.JEnterprise.controller.deviceTreeNode;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.deviceTreeNode.AthenaDeviceNode;
import com.zc.efounder.JEnterprise.service.deviceTreeNode.AthenaDeviceNodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 树节点定义Controller
 *
 * @author qindehua
 * @date 2022-09-06
 */
@RestController
@RequestMapping("/deviceTree/node")
@Api(value = "树节点定义", tags = {"树节点定义"})
@ApiSupport(author = "QinDeHua", order = 6)
public class AthenaDeviceNodeController extends BaseController {
    @Autowired
    private AthenaDeviceNodeService athenaDeviceNodeService;

    /**
     * 查询树节点定义列表
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceNodeCode", value = "节点编号", paramType = "query"),
            @ApiImplicitParam(name = "deviceNodeId", value = "节点ID", paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "deviceNodeIsNode", value = "是否节点", paramType = "query"),
            @ApiImplicitParam(name = "deviceNodeName", value = "节点名称", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    @ApiOperation("查询树节点定义列表")
    @PreAuthorize("@ss.hasPermi('deviceTree:node:list')")
    @GetMapping("/list")
    public TableDataInfo list(AthenaDeviceNode athenaDeviceNode) {
        startPage();
        List<AthenaDeviceNode> list = athenaDeviceNodeService.selectAthenaDeviceNodeList(athenaDeviceNode);
        return getDataTable(list);
    }

    /**
     * 查询树节点定义数据
     */
    @ApiOperation("查询树节点定义数据")
    @PreAuthorize("@ss.hasPermi('deviceTree:node:list')")
    @GetMapping("/listDate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceNodeCode", value = "节点编号", paramType = "query"),
            @ApiImplicitParam(name = "deviceNodeId", value = "节点ID", paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "deviceNodeIsNode", value = "是否节点", paramType = "query"),
            @ApiImplicitParam(name = "deviceNodeName", value = "节点名称", paramType = "query"),
    })
    public AjaxResult listDate(AthenaDeviceNode athenaDeviceNode) {
        List<AthenaDeviceNode> list = athenaDeviceNodeService.selectAthenaDeviceNodeList(athenaDeviceNode);
        return AjaxResult.success(list);
    }

    /**
     * 导出树节点定义列表
     */
    @ApiOperation("导出树节点定义列表")
    @PreAuthorize("@ss.hasPermi('deviceTree:node:export')")
    @Log(title = "树节点定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceNodeCode", value = "节点编号", paramType = "query"),
            @ApiImplicitParam(name = "deviceNodeId", value = "节点ID", paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "deviceNodeIsNode", value = "是否节点", paramType = "query"),
            @ApiImplicitParam(name = "deviceNodeName", value = "节点名称", paramType = "query"),
    })
    public void export(HttpServletResponse response, AthenaDeviceNode athenaDeviceNode) {
        List<AthenaDeviceNode> list = athenaDeviceNodeService.selectAthenaDeviceNodeList(athenaDeviceNode);
        ExcelUtil<AthenaDeviceNode> util = new ExcelUtil<>(AthenaDeviceNode.class);
        util.exportExcel(response, list, "树节点定义数据");
    }

    /**
     * 获取树节点定义详细信息
     */
    @ApiOperation("获取树节点定义详细信息")
    @PreAuthorize("@ss.hasPermi('deviceTree:node:query')")
    @GetMapping(value = "/{deviceNodeId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceNodeId", value = "树节点ID", dataType = "Long", paramType = "path", required = true)
    })
    public AjaxResult getInfo(@PathVariable("deviceNodeId") Long deviceNodeId) {
        return AjaxResult.success(athenaDeviceNodeService.selectAthenaDeviceNodeByDeviceNodeId(deviceNodeId));
    }

    /**
     * 新增树节点定义
     */
    @ApiOperation("新增树节点定义")
    @PreAuthorize("@ss.hasPermi('deviceTree:node:add')")
    @Log(title = "树节点定义", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = {"deviceNodeId", "children"})
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AthenaDeviceNode athenaDeviceNode) {
        return athenaDeviceNodeService.insertAthenaDeviceNode(athenaDeviceNode);
    }

    /**
     * 修改树节点定义
     */
    @ApiOperation("修改树节点定义")
    @PreAuthorize("@ss.hasPermi('deviceTree:node:edit')")
    @Log(title = "树节点定义", businessType = BusinessType.UPDATE)
    @ApiOperationSupport(ignoreParameters = {"children"})
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AthenaDeviceNode athenaDeviceNode) {
        return athenaDeviceNodeService.updateAthenaDeviceNode(athenaDeviceNode);
    }

    /**
     * 删除树节点定义
     */
    @ApiOperation("删除树节点定义")
    @PreAuthorize("@ss.hasPermi('deviceTree:node:remove')")
    @Log(title = "树节点定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deviceNodeIds}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceNodeIds", value = "树节点ID集合,请以逗号隔开 例如(1,2)", dataType = "Long", allowMultiple = true, paramType = "path", required = true)
    })
    public AjaxResult remove(@PathVariable Long[] deviceNodeIds) {
        return athenaDeviceNodeService.deleteAthenaDeviceNodeByDeviceNodeIds(deviceNodeIds);
    }
}
