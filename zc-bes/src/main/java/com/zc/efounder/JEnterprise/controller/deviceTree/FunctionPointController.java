package com.zc.efounder.JEnterprise.controller.deviceTree;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.deviceTree.FunctionPoint;
import com.zc.efounder.JEnterprise.service.deviceTree.FunctionPointService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 功能点Controller
 *
 * @author sunshangeng
 * @date 2022-09-14
 */
@RestController
@RequestMapping("/deviceTree/deviceTree")
public class FunctionPointController extends BaseController
{
    @Autowired
    private FunctionPointService functionPointService;

    /**
     * 查询功能点列表
     */
    @PreAuthorize("@ss.hasPermi('system:deviceTree:list')")
    @GetMapping("/list")
    public TableDataInfo list(FunctionPoint functionPoint)
    {
        startPage();
        List<FunctionPoint> list = functionPointService.selectFunctionPointList(functionPoint);
        return getDataTable(list);
    }

    /**
     * 导出功能点列表
     */
    @PreAuthorize("@ss.hasPermi('system:deviceTree:export')")
    @Log(title = "功能点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FunctionPoint functionPoint)
    {
        List<FunctionPoint> list = functionPointService.selectFunctionPointList(functionPoint);
        ExcelUtil<FunctionPoint> util = new ExcelUtil<>(FunctionPoint.class);
        util.exportExcel(response, list, "功能点数据");
    }

    /**
     * 获取功能点详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:deviceTree:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(functionPointService.selectFunctionPointById(id));
    }

    /**
     * 新增功能点
     */
    @PreAuthorize("@ss.hasPermi('system:deviceTree:add')")
    @Log(title = "功能点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FunctionPoint functionPoint)
    {
        return toAjax(functionPointService.insertFunctionPoint(functionPoint));
    }

    /**
     * 修改功能点
     */
    @PreAuthorize("@ss.hasPermi('system:deviceTree:edit')")
    @Log(title = "功能点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FunctionPoint functionPoint)
    {
        return toAjax(functionPointService.updateFunctionPoint(functionPoint));
    }

    /**
     * 删除功能点
     */
    @PreAuthorize("@ss.hasPermi('system:deviceTree:remove')")
    @Log(title = "功能点", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(functionPointService.deleteFunctionPointByIds(ids));
    }

}
