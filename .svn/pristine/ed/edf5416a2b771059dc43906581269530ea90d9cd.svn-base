package com.zc.efounder.JEnterprise.controller.systemSetting;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason;
import com.zc.efounder.JEnterprise.service.systemSetting.ElectricityPriceSeasonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 电价-季节范围Controller
 *
 * @author liuwenge
 * @date 2023-02-20
 */
@RestController
@RequestMapping("/systemSetting/electricityPriceSeason")
@Api(value = "ElectricityPriceSeasonController", tags = {"电价-季节范围"})
@ApiSupport(order = 44,author = "liuwenge")
public class ElectricityPriceSeasonController extends BaseController
{
    @Autowired
    private ElectricityPriceSeasonService electricityPriceSeasonService;

    /**
     * 查询电价-季节范围列表
     * @author liuwenge
     */
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSeason:list')")
    @GetMapping("/list")
    @ApiOperation("查询电价-季节范围列表")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "名称"),
            @ApiImplicitParam(name = "startDate",value = "开始日期"),
            @ApiImplicitParam(name = "endDate",value = "结束日期"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(ElectricityPriceSeason electricityPriceSeason)
    {
        startPage();
        List<ElectricityPriceSeason> list = electricityPriceSeasonService.selectElectricityPriceSeasonList(electricityPriceSeason);
        return getDataTable(list);
    }

    /**
     * 导出电价-季节范围列表
     * @author liuwenge
     */
    @ApiOperation("导出电价-季节范围列表")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSeason:export')")
    @Log(title = "电价-季节范围", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "名称"),
            @ApiImplicitParam(name = "startDate",value = "开始日期"),
            @ApiImplicitParam(name = "endDate",value = "结束日期"),
    })
    public void export(HttpServletResponse response, ElectricityPriceSeason electricityPriceSeason)
    {
        List<ElectricityPriceSeason> list = electricityPriceSeasonService.selectElectricityPriceSeasonList(electricityPriceSeason);
        ExcelUtil<ElectricityPriceSeason> util = new ExcelUtil<>(ElectricityPriceSeason.class);
        util.exportExcel(response, list, "电价-季节范围数据");
    }

    /**
     * 获取电价-季节范围详细信息
     * @author liuwenge
     */
    @ApiOperation("获取电价-季节范围详细信息")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSeason:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "唯一ID",dataType = "long",paramType = "path",required = true)
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(electricityPriceSeasonService.selectElectricityPriceSeasonById(id));
    }

    /**
     * 新增电价-季节范围
     * @author liuwenge
     */
    @ApiOperation("新增电价-季节范围")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSeason:add')")
    @Log(title = "电价-季节范围", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperationSupport(ignoreParameters = "id")
    public AjaxResult add(@RequestBody ElectricityPriceSeason electricityPriceSeason)
    {
        return electricityPriceSeasonService.insertElectricityPriceSeason(electricityPriceSeason);
    }

    /**
     * 修改电价-季节范围
     * @author liuwenge
     */
    @ApiOperation("修改电价-季节范围")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSeason:edit')")
    @Log(title = "电价-季节范围", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ElectricityPriceSeason electricityPriceSeason)
    {
        return electricityPriceSeasonService.updateElectricityPriceSeason(electricityPriceSeason);
    }

    /**
     * 删除电价-季节范围
     * @author liuwenge
     */
    @ApiOperation("删除电价-季节范围")
    @PreAuthorize("@ss.hasPermi('systemSetting:electricityPriceSeason:remove')")
    @Log(title = "电价-季节范围", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "唯一ID集合(以逗号隔开例如 1,2)",dataType = "long",paramType = "path",required = true,allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return electricityPriceSeasonService.deleteElectricityPriceSeasonByIds(ids);
    }
}
