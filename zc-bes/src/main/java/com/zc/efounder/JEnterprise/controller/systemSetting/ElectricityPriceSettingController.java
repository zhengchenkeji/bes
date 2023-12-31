package com.zc.efounder.JEnterprise.controller.systemSetting;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceRule;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSetting;
import com.zc.efounder.JEnterprise.service.systemSetting.ElectricityPriceSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * 电价设置Controller
 *
 * @author gaojikun
 * @date 2022-11-29
 */
@RestController
@RequestMapping("/systemSetting/electricityPriceSetting")
@Api(value = "ElectricityPriceSettingController", tags = {"电价设置"})
@ApiSupport(order = 30,author = "gaojikun")
public class ElectricityPriceSettingController extends BaseController
{
    @Resource
    private ElectricityPriceSettingService electricityPriceSettingService;

    /**
     * @param electricityPriceSetting
     * @return List<ElectricityPriceSetting>
     * @Description: 查询电价设置列表
     * @auther: gaojikun
     */
    @ApiOperation(value = "查询电价设置列表")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSetting:list')")
    @GetMapping("/list")
    @ApiOperationSupport(includeParameters = "monthDate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "monthDate",value = "月份",dataType = "Date",paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(ElectricityPriceSetting electricityPriceSetting)
    {
        startPage();
        List<ElectricityPriceSetting> list = electricityPriceSettingService.selectElectricityPriceSettingList(electricityPriceSetting);
        return getDataTable(list);
    }

    /**
     * @param electricityPriceSetting
     * @return List<ElectricityPriceSetting>
     * @Description: 导出电价设置列表
     * @auther: gaojikun
     */
    @ApiOperation(value = "导出电价设置列表")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSetting:export')")
    @Log(title = "电价设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(includeParameters = "monthDate")
    @ApiImplicitParam(name = "monthDate",value = "月份",dataType = "Date",paramType = "query")
    public void export(HttpServletResponse response, ElectricityPriceSetting electricityPriceSetting)
    {
        List<ElectricityPriceSetting> list = electricityPriceSettingService.selectElectricityPriceSettingList(electricityPriceSetting);
        ExcelUtil<ElectricityPriceSetting> util = new ExcelUtil<>(ElectricityPriceSetting.class);
        util.exportExcel(response, list, "电价设置数据");
    }

    /**
     * @param id
     * @return AjaxResult
     * @Description: 查询电价设置
     * @auther: gaojikun
     */
    @ApiOperation(value = "查询电价设置")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSetting:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "电价设置ID",required = true,paramType = "path",dataType = "long")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return electricityPriceSettingService.selectElectricityPriceSettingById(id);
    }

    /**
     * @param electricityPriceSetting
     * @return AjaxResult
     * @Description: 新增电价设置
     * @auther: gaojikun
     */
    @ApiOperation(value = "新增电价设置")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSetting:add')")
    @Log(title = "电价设置", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = "id")
    @PostMapping
    public AjaxResult add(@RequestBody ElectricityPriceSetting electricityPriceSetting)
    {
        return electricityPriceSettingService.insertElectricityPriceSetting(electricityPriceSetting);
    }

    /**
     * @param electricityPriceSetting
     * @return AjaxResult
     * @Description: 修改电价设置
     * @auther: gaojikun
     */
    @ApiOperation(value = "修改电价设置")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSetting:edit')")
    @Log(title = "电价设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ElectricityPriceSetting electricityPriceSetting)
    {
        return electricityPriceSettingService.updateElectricityPriceSetting(electricityPriceSetting);
    }

    /**
     * @param ids
     * @return AjaxResult
     * @Description: 批量删除电价设置
     * @auther: gaojikun
     */
    @ApiOperation(value = "批量删除电价设置")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSetting:remove')")
    @Log(title = "电价设置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "电价设置ID集合(以逗号隔开例如 1,2)",required = true,allowMultiple = true,paramType = "path",dataType = "long")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return electricityPriceSettingService.deleteElectricityPriceSettingByIds(ids);
    }
}
