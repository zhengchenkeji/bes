package com.zc.efounder.JEnterprise.controller.baseData;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.zc.efounder.JEnterprise.domain.baseData.Agreement;
import com.zc.efounder.JEnterprise.service.baseData.AgreementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设备协议Controller
 *
 * @author sunshangeng
 * @date 2023-03-14
 */
@RestController
@RequestMapping("/baseData/agreement")
@Api(value = "AgreementController", tags = {"协议管理"})
public class AgreementController extends BaseController
{
    @Autowired
    private AgreementService agreementService;

    /**
     * 查询设备协议列表
     */
    @ApiOperation(value = "查询协议列表")
    @GetMapping("/list")
    public TableDataInfo list(Agreement agreement)
    {
        startPage();
        List<Agreement> list = agreementService.selectAgreementList(agreement);
        return getDataTable(list);
    }

    /**
     * 导出设备协议列表
     */
    @ApiOperation(value = "导出协议列表")
    @Log(title = "设备协议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Agreement agreement)
    {
        List<Agreement> list = agreementService.selectAgreementList(agreement);
        ExcelUtil<Agreement> util = new ExcelUtil<>(Agreement.class);
        util.exportExcel(response, list, "设备协议数据");
    }

    /**
     * 获取设备协议详细信息
     */
    @ApiOperation(value = "获取协议详情")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(agreementService.selectAgreementById(id));
    }

    /**
     * 新增设备协议
     */
    @ApiOperation(value = "新增协议")
    @Log(title = "设备协议", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Agreement agreement)
    {
        return agreementService.insertAgreement(agreement);
    }

    /**
     * 修改设备协议
     */
    @ApiOperation(value = "修改协议")
    @Log(title = "设备协议", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Agreement agreement)
    {
        return agreementService.updateAgreement(agreement);
    }

    /**
     * 删除设备协议
     */
    @ApiOperation(value = "删除协议")
    @Log(title = "设备协议", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids,Integer check)
    {
        return agreementService.deleteAgreementByIds(ids,0);
    }
}
