package com.zc.efounder.JEnterprise.controller.energyDataReport;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.efounder.JEnterprise.service.energyDataReport.IDataCenterBaseInfoService;
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
 * 数据中心基本信息Controller
 *
 * @author ruoyi
 * @date 2022-09-13
 */
@RestController
@RequestMapping("/basicData/dataCenterBaseInfo")
@Api(value = "DataCenterBaseInfoController", tags = {"数据中心基本信息"})
@ApiSupport(order = 22)
public class DataCenterBaseInfoController extends BaseController
{
    @Autowired
    private IDataCenterBaseInfoService dataCenterBaseInfoService;

    /**
     * 查询数据中心基本信息列表
     */
    @PreAuthorize("@ss.hasPermi('basicData:dataCenterBaseInfo:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询数据中心基本信息列表")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataCenterId",value = "数据中心代码由部里统一分配，用于唯一标识数据中心和数据中转站"),
            @ApiImplicitParam(name = "dataCenterName",value = "数据中心名称"),
            @ApiImplicitParam(name = "dataCenterIp",value = "数据中心ip地址"),
            @ApiImplicitParam(name = "dataCenterPort",value = "数据中心端口号",dataType = "long"),
            @ApiImplicitParam(name = "dataCenterType",value = "类型 1=数据中心,2=数据中转站",dataType = "long"),
            @ApiImplicitParam(name = "dataCenterManager",value = "数据中心主管单位名称，最多24个汉字"),
            @ApiImplicitParam(name = "dataCenterDesc",value = "描述"),
            @ApiImplicitParam(name = "dataCenterContact",value = "联系人"),
            @ApiImplicitParam(name = "dataCenterTel",value = "联系电话"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(DataCenterBaseInfo dataCenterBaseInfo)
    {
        startPage();
        List<DataCenterBaseInfo> list = dataCenterBaseInfoService.selectDataCenterBaseInfoList(dataCenterBaseInfo);
        return getDataTable(list);
    }

    /**
     * 导出数据中心基本信息列表
     */
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataCenterId",value = "数据中心代码由部里统一分配，用于唯一标识数据中心和数据中转站"),
            @ApiImplicitParam(name = "dataCenterName",value = "数据中心名称"),
            @ApiImplicitParam(name = "dataCenterIp",value = "数据中心ip地址"),
            @ApiImplicitParam(name = "dataCenterPort",value = "数据中心端口号",dataType = "long"),
            @ApiImplicitParam(name = "dataCenterType",value = "类型 1=数据中心,2=数据中转站",dataType = "long"),
            @ApiImplicitParam(name = "dataCenterManager",value = "数据中心主管单位名称，最多24个汉字"),
            @ApiImplicitParam(name = "dataCenterDesc",value = "描述"),
            @ApiImplicitParam(name = "dataCenterContact",value = "联系人"),
            @ApiImplicitParam(name = "dataCenterTel",value = "联系电话"),
    })
    @PreAuthorize("@ss.hasPermi('basicData:dataCenterBaseInfo:export')")
    @Log(title = "数据中心基本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出数据中心基本信息列表")
    public void export(HttpServletResponse response, DataCenterBaseInfo dataCenterBaseInfo)
    {
        List<DataCenterBaseInfo> list = dataCenterBaseInfoService.selectDataCenterBaseInfoList(dataCenterBaseInfo);
        ExcelUtil<DataCenterBaseInfo> util = new ExcelUtil<>(DataCenterBaseInfo.class);
        util.exportExcel(response, list, "数据中心基本信息数据");
    }

    /**
     * 获取数据中心基本信息详细信息
     */
    @ApiOperation(value = "获取数据中心基本信息详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:dataCenterBaseInfo:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "数据中心ID",required = true,dataType = "long")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(dataCenterBaseInfoService.selectDataCenterBaseInfoById(id));
    }

    /**
     * 新增数据中心基本信息
     */
    @ApiOperation(value = "新增数据中心基本信息")
    @PreAuthorize("@ss.hasPermi('basicData:dataCenterBaseInfo:add')")
    @Log(title = "数据中心基本信息", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = "id")
    @PostMapping
    public AjaxResult add(@RequestBody DataCenterBaseInfo dataCenterBaseInfo)
    {
        return dataCenterBaseInfoService.insertDataCenterBaseInfo(dataCenterBaseInfo);
    }

    /**
     * 修改数据中心基本信息
     */
    @ApiOperation(value = "修改数据中心基本信息")
    @PreAuthorize("@ss.hasPermi('basicData:dataCenterBaseInfo:edit')")
    @Log(title = "数据中心基本信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataCenterBaseInfo dataCenterBaseInfo)
    {
        return dataCenterBaseInfoService.updateDataCenterBaseInfo(dataCenterBaseInfo);
    }

    /**
     * 删除数据中心基本信息
     */
    @ApiOperation(value = "删除数据中心基本信息")
    @PreAuthorize("@ss.hasPermi('basicData:dataCenterBaseInfo:remove')")
    @Log(title = "数据中心基本信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "数据中心ID集合(以逗号隔开例如：1,2)",required = true,dataType = "long",allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return dataCenterBaseInfoService.deleteDataCenterBaseInfoByIds(ids);
    }
}
