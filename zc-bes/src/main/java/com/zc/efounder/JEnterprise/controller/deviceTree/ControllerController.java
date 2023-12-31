package com.zc.efounder.JEnterprise.controller.deviceTree;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.service.deviceTree.ControllerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 控制器Controller
 *
 * @author qindehua
 * @date 2022-09-14
 */
@RestController
@Api(value = "控制器", tags = {"控制器"})
@ApiSupport( order = 8)
@RequestMapping("/basicData/deviceTree/controller")
public class ControllerController extends BaseController
{
    @Autowired
    private ControllerService controllerService;

    /**
     * 查询控制器列表
     */
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询控制器列表",hidden = true)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id"),
            @ApiImplicitParam(name = "alias", value = "控制器别名"),
            @ApiImplicitParam(name = "type", value = "控制器类型 1：DDC采集器、2：照明控制器 、3：能耗采集器"),
            @ApiImplicitParam(name = "active", value = "使能状态 0：不使能、1：使能"),
            @ApiImplicitParam(name = "description", value = "描述"),
            @ApiImplicitParam(name = "deviceNodeId", value = "所属节点类ID"),
            @ApiImplicitParam(name = "deviceTreeFatherId", value = "父节点树id"),
            @ApiImplicitParam(name = "errorState", value = "异常状态 0：正常、1：异常"),
            @ApiImplicitParam(name = "gateWay", value = "默认网关"),
            @ApiImplicitParam(name = "id", value = "控制器ID"),
            @ApiImplicitParam(name = "ip", value = "ip地址"),
            @ApiImplicitParam(name = "location", value = "安装位置"),
            @ApiImplicitParam(name = "mask", value = "子网掩码"),
            @ApiImplicitParam(name = "onlineState", value = "在线状态0：不在线、 1：在线"),
            @ApiImplicitParam(name = "savePeriod", value = "保存周期：小时（只有能耗采集器有）"),
            @ApiImplicitParam(name = "serverIp", value = "服务ip地址"),
            @ApiImplicitParam(name = "serverPort", value = "服务端口"),
            @ApiImplicitParam(name = "synchState", value = "同步状态 0：未同步、 1：已同步"),
            @ApiImplicitParam(name = "sysName", value = "系统名称"),
            @ApiImplicitParam(name = "zone", value = "归属区域"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(Controller controller)
    {
        startPage();
        List<Controller> list = controllerService.selectControllerList(controller);
        return getDataTable(list);
    }

    /**
     * 导出控制器列表
     */
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:export')")
    @Log(title = "控制器", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Controller controller)
    {
        List<Controller> list = controllerService.selectControllerList(controller);
        ExcelUtil<Controller> util = new ExcelUtil<>(Controller.class);
        util.exportExcel(response, list, "控制器数据");
    }

    /**
     * 获取控制器详细信息
     */
    @ApiOperation("获取控制器详细信息")
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id", value = "控制器ID", paramType = "path",required = true,dataType = "long")
    public AjaxResult getInfo(@PathVariable("id")Long id)
    {
        return AjaxResult.success(controllerService.selectControllerById(id));
    }

    /**
     * 新增控制器
     */
    @ApiOperation("新增控制器")
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:add')")
    @Log(title = "控制器", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = {
            "id","deviceTreeId"
    })
    @PostMapping("/addController")
    public AjaxResult addController(@RequestBody Controller controller)
    {
        AjaxResult ajaxResult = controllerService.insertController(controller);
        return ajaxResult;
    }

    /**
     * 修改控制器
     */
    @ApiOperation("修改控制器")
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:edit')")
    @Log(title = "控制器", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Controller controller)
    {
        return controllerService.updateController(controller);
    }


    /**
     * 设置时间
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @ApiOperation("设置时间")
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:setTimeDDC')")
    @GetMapping("/setTimeDDC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id", paramType = "query",required = true,dataType = "long"),
            @ApiImplicitParam(name = "type", value = "控制器类型", paramType = "query",required = true,dataType = "int")
    })
    public AjaxResult setTimeDDC( Long deviceTreeId, Integer type)
    {
        return controllerService.setTimeDDC(deviceTreeId,type);
    }

    /**
     * 获取时间
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @ApiOperation("获取时间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id", paramType = "query",required = true,dataType = "long"),
            @ApiImplicitParam(name = "type", value = "控制器类型", paramType = "query",required = true,dataType = "int")
    })
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:getTimeDDC')")
    @GetMapping("/getTimeDDC")
    public AjaxResult getTimeDDC(Long deviceTreeId,Integer type)
    {
        return controllerService.getTimeDDC(deviceTreeId,type);
    }

    /**
     * 重新启动
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @ApiOperation("重新启动")
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:restartDDC')")
    @GetMapping("/restartDDC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id", paramType = "query",required = true,dataType = "long"),
            @ApiImplicitParam(name = "type", value = "控制器类型", paramType = "query",required = true,dataType = "int")
    })
    public AjaxResult restartDDC(Long deviceTreeId,Integer type)
    {
        return controllerService.restartDDC(deviceTreeId,type);
    }

    /**
     * 重置控制器
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @ApiOperation("重置控制器")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id", paramType = "query",required = true,dataType = "long"),
            @ApiImplicitParam(name = "type", value = "控制器类型", paramType = "query",required = true,dataType = "int")
    })
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:resetDDC')")
    @GetMapping("/resetDDC")
    public AjaxResult resetDDC(Long deviceTreeId,Integer type)
    {
        return controllerService.resetDDC(deviceTreeId,type);
    }

    /**
     * 同步控制器
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @param synchronize  是否批量同步
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/10/10
     **/
    @ApiOperation("同步控制器")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id", paramType = "query",required = true,dataType = "long"),
            @ApiImplicitParam(name = "type", value = "控制器类型", paramType = "query",required = true,dataType = "int"),
            @ApiImplicitParam(name = "synchronize", value = "是否批量同步 true： 批量同步  false： 只同步当前",dataType = "boolean", paramType = "query",required = true)
    })
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:sync')")
    @GetMapping("/synchronizeDDC")
    public AjaxResult synchronizeDDC(Long deviceTreeId,Integer type,boolean synchronize)
    {
        return controllerService.synchronizeDDC(deviceTreeId,type,synchronize);
    }

    /**
     * 远程升级
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/22
     **/
    @ApiOperation("远程升级")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id", paramType = "query",required = true,dataType = "long"),
            @ApiImplicitParam(name = "type", value = "控制器类型", paramType = "query",required = true,dataType = "int")
    })
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:remoteUpgradeDdc')")
    @GetMapping("/remoteUpgradeDdc")
    public AjaxResult remoteUpgradeDdc(Long deviceTreeId,Integer type)
    {
        return controllerService.remoteUpgradeDdc(deviceTreeId,type);
    }


    /**
     * 参数对比
     *
     * @param deviceTreeId 设备树id
     * @param type         类型
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/09/23
     **/
    @ApiOperation("参数对比")
    @PreAuthorize("@ss.hasPermi('deviceTree:controller:contrast')")
    @GetMapping("/getDDCInfoParam")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId", value = "设备树id", paramType = "query",required = true,dataType = "long"),
            @ApiImplicitParam(name = "type", value = "控制器类型", paramType = "query",required = true,dataType = "int")
    })
    public AjaxResult getDDCInfoParam(Long deviceTreeId,Integer type)
    {
        return controllerService.getDDCInfoParam(deviceTreeId,type);
    }

}
