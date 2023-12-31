package com.zc.efounder.JEnterprise.controller.deviceTree;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.deviceTree.Module;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.service.deviceTree.ModuleService;
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
import java.util.Map;

/**
 * 模块Controller
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
@RestController
@RequestMapping("/deviceTree/module")
@Api(value = "模块", tags = {"模块"})
@ApiSupport(order = 9)
public class ModuleController extends BaseController {
    @Autowired
    private ModuleService moduleService;

    /**
     * 查询模块列表
     */
    @ApiOperation(value = "查询模块列表",hidden = true)
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:module:list')")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "active", value = "使能状态0：不使能、1：使能"),
            @ApiImplicitParam(name = "alias", value = "模块别名"),
            @ApiImplicitParam(name = "controllerId", value = "所属控制器"),
            @ApiImplicitParam(name = "description", value = "描述"),
            @ApiImplicitParam(name = "deviceTreeId", value = "所属设备树"),
            @ApiImplicitParam(name = "fatherId", value = "父ID" ),
            @ApiImplicitParam(name = "id", value = "模块ID" ),
            @ApiImplicitParam(name = "installAddress", value = "安装位置" ),
            @ApiImplicitParam(name = "moduleTypeId", value = "所属模块型号" ),
            @ApiImplicitParam(name = "nodeType", value = "节点类型" ),
            @ApiImplicitParam(name = "onlineState", value = "在线状态 0：不在线、1：在线" ),
            @ApiImplicitParam(name = "slaveAddress", value = "通讯地址" ),
            @ApiImplicitParam(name = "synchState", value = "同步状态 0：未同步、1：已同步" ),
            @ApiImplicitParam(name = "sysName", value = "模块名称" ),
            @ApiImplicitParam(name = "type", value = "模块类型0：ddc 模块、1：照明模块、2：干线耦合器、3：支线耦合器" ),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(Module module) {
        startPage();
        List<Module> list = moduleService.selectModuleList(module);
        return getDataTable(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:module:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Module module) {
        List<Module> list = moduleService.selectModuleList(module);
        ExcelUtil<Module> util = new ExcelUtil<>(Module.class);
        util.exportExcel(response, list, "模块数据");
    }

    /**
     * 获取模块详细信息
     */
    @ApiOperation("获取模块详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:module:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", value = "模块ID", paramType = "path", required = true,dataType = "long")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(moduleService.selectModuleById(id));
    }

    /**
     * 新增模块
     */
    @ApiOperation("新增模块")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:module:add')")
    @PostMapping("/insertModule")
    @ApiOperationSupport(ignoreParameters = {"id","deviceTreeId"})
    public AjaxResult insertModule(Module module) {
        return moduleService.insertModule(module);
    }

    /**
     * 修改模块
     */
    @ApiOperation("修改模块")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:module:edit')")
    @GetMapping("/updateModule")
    public AjaxResult updateModule(Module module) {
        return moduleService.updateModule(module);
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:module:remove')")
    @Log(title = "删除模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return moduleService.deleteModuleByIds(ids);
    }


    /**
     * 同步模块
     *
     * @param params
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/24
     **/
    @ApiOperation("同步模块")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:module:sync')")
    @GetMapping("/synchronizeModule")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id", paramType = "query", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "nodeType", value = "节点类型", paramType = "query", required = true, dataType = "int")
    })
    public AjaxResult synchronizeModule(@RequestParam @ApiIgnore Map<String, Object> params) {
        Module module = new Module();
        module.setDeviceTreeId(Long.parseLong(String.valueOf(params.get("deviceTreeId"))));
        module.setNodeType(params.get("nodeType").toString());
        return moduleService.synchronizeModule(module);
    }

    /**
     * 同步模块点
     *
     * @param params
     * @return {@code AjaxResult }
     * @Author gaojikun
     * @Date 2022/09/29
     **/
    @ApiOperation("同步模块点")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:point:sync')")
    @GetMapping("/synchronizePoint")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "treeId", value = "设备树id", paramType = "query", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "nodeType", value = "节点类型", paramType = "query", required = true, dataType = "int")
    })
    @ResponseBody
    public AjaxResult synchronizePoint(@RequestParam @ApiIgnore Map<String, Object> params) {
        Point point = new Point();
        point.setTreeId(Long.parseLong(String.valueOf(params.get("treeId"))));
        point.setNodeType(params.get("nodeType").toString());
        return moduleService.synchronizePoint(point);
    }

    /**
     * 同步虚点
     *
     * @param params
     * @return {@code AjaxResult }
     * @Author gaojikun
     * @Date 2022/09/29
     **/
    @ApiOperation("同步虚点")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:vpoint:sync')")
    @GetMapping("/synVirtualPoint")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "treeId", value = "设备树id", paramType = "query", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "nodeType", value = "节点类型", paramType = "query", required = true, dataType = "int")
    })
    @ResponseBody
    public AjaxResult synVirtualPoint(@RequestParam @ApiIgnore Map<String, Object> params) {
        Point point = new Point();
        point.setTreeId(Long.parseLong(String.valueOf(params.get("treeId"))));
        point.setNodeType(params.get("nodeType").toString());
        return moduleService.synVirtualPoint(point);
    }


    /**
     * 数据对比
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author gaojikun
     * @Date 2022/09/29
     **/
    @ApiOperation("数据对比")
    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:module:contrast')")
    @GetMapping("/getDataInfoParam")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id", paramType = "query", required = true,dataType = "Long"),
            @ApiImplicitParam(name = "type", value = "设备类型", paramType = "query", required = true,dataType = "int")
    })
    public AjaxResult getDataInfoParam(Long deviceTreeId, Integer type) {
        return moduleService.getDataInfoParam(deviceTreeId, type);
    }
}
