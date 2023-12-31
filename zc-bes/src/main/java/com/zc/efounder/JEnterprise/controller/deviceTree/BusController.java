package com.zc.efounder.JEnterprise.controller.deviceTree;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.deviceTree.Bus;
import com.zc.efounder.JEnterprise.service.deviceTree.BusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 总线Controller
 *
 * @author ruoyi
 * @date 2022-09-15
 */
@RestController
@Api(value = "BusController", tags = {"总线"})
@ApiSupport(order = 11)
@RequestMapping("/deviceTree/bus")
public class BusController extends BaseController
{
    @Autowired
    private BusService busService;

//    /**
//     * 查询总线列表
//     */
//    @PreAuthorize("@ss.hasPermi('deviceTree:bus:list')")
//    @GetMapping("/list")
//    public TableDataInfo list(Bus bus)
//    {
//        startPage();
//        List<Bus> list = busService.selectBusList(bus);
//        return getDataTable(list);
//    }
//
//    /**
//     * 导出总线列表
//     */
//    @PreAuthorize("@ss.hasPermi('deviceTree:bus:export')")
//    @Log(title = "总线", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, Bus bus)
//    {
//        List<Bus> list = busService.selectBusList(bus);
//        ExcelUtil<Bus> util = new ExcelUtil<>(Bus.class);
//        util.exportExcel(response, list, "总线数据");
//    }

//    /**
//     * 获取总线详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('deviceTree:bus:query')")
//    @GetMapping(value = "/{deviceTreeId}")
//    public AjaxResult getInfo(@PathVariable("deviceTreeId") String deviceTreeId)
//    {
//        return AjaxResult.success(busService.selectBusByDeviceTreeId(deviceTreeId));
//    }

    /**
     * 新增总线
     */
    @ApiOperation(value = "新增总线")
    @PreAuthorize("@ss.hasPermi('deviceTree:bus:add')")
    @Log(title = "总线", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = "deviceTreeId")
    @PostMapping
    public AjaxResult add(@RequestBody Bus bus)
    {
        return busService.insertBus(bus);
    }

    /**
     * 修改总线
     */
    @ApiOperation(value = "修改总线")
    @PreAuthorize("@ss.hasPermi('deviceTree:bus:edit')")
    @Log(title = "总线", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Bus bus)
    {
        return busService.updateBus(bus);
    }

    /**
     * 删除总线
     */
    /*@PreAuthorize("@ss.hasPermi('deviceTree:bus:remove')")
    @Log(title = "总线", businessType = BusinessType.DELETE)
	@DeleteMapping("/{deviceTreeId}")
    public AjaxResult remove(@PathVariable String deviceTreeId)
    {
        return busService.deleteBusByDeviceTreeId(deviceTreeId);
    }*/
}
