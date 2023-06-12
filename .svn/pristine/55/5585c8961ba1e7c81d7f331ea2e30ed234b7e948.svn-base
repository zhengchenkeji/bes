package com.zc.efounder.JEnterprise.controller.energyInfo;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.service.energyInfo.EnergyTypeService;
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
 * 能源类型Controller
 *
 * @author gaojikun
 * @date 2022-09-07
 */
@RestController
@RequestMapping("/basicData/energyType")
@Api(value = "EnergyTypeController", tags = {"能源类型"})
@ApiSupport(order = 13)
public class EnergyTypeController extends BaseController
{
    @Autowired
    private EnergyTypeService energyTypeService;

    /**
    * @Author:gaojikun
    * @Date:2023-01-13 15:44
    * @Param:energyType
    * @Description:查询能源类型列表
    * @Return:TableDataInfo
    */
    @ApiOperation(value = "查询能源类型列表")
    @PreAuthorize("@ss.hasPermi('basicData:energyType:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "guid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "co2",value = "二氧化碳",dataType = "double"),
            @ApiImplicitParam(name = "coalAmount",value = "耗煤量",dataType = "double"),
            @ApiImplicitParam(name = "code",value = "能源类型编号"),
            @ApiImplicitParam(name = "name",value = "能源类型名称"),
            @ApiImplicitParam(name = "price",value = "单价",dataType = "double"),
            @ApiImplicitParam(name = "unit",value = "单位"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(EnergyType energyType)
    {
        startPage();
        List<EnergyType> list = energyTypeService.selectEnergyTypeList(energyType);
        return getDataTable(list);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:导出能源类型列表
     * @Return:
     */
    @PreAuthorize("@ss.hasPermi('basicData:energyType:export')")
    @Log(title = "能源类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EnergyType energyType)
    {
        List<EnergyType> list = energyTypeService.selectEnergyTypeList(energyType);
        ExcelUtil<EnergyType> util = new ExcelUtil<>(EnergyType.class);
        util.exportExcel(response, list, "能源类型数据");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:guid
     * @Description:获取能源类型详细信息
     * @Return:AjaxResult
     */
    @ApiOperation(value = "获取能源类型详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:energyType:query')")
    @GetMapping(value = "/{guid}")
    @ApiImplicitParam(name = "guid",value = "能源类型ID",paramType = "path",required = true)
    public AjaxResult getInfo(@PathVariable("guid") String guid)
    {
        return AjaxResult.success(energyTypeService.selectEnergyTypeByGuid(guid));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:新增能源类型
     * @Return:AjaxResult
     */
    @ApiOperation(value = "新增能源类型")
    @PreAuthorize("@ss.hasPermi('basicData:energyType:add')")
    @Log(title = "能源类型", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperationSupport(ignoreParameters = "guid")
    public AjaxResult add(@RequestBody EnergyType energyType)
    {
        return energyTypeService.insertEnergyType(energyType);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:energyType
     * @Description:修改能源类型
     * @Return:AjaxResult
     */
    @ApiOperation(value = "修改能源类型")
    @PreAuthorize("@ss.hasPermi('basicData:energyType:edit')")
    @Log(title = "能源类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EnergyType energyType)
    {
        return energyTypeService.updateEnergyType(energyType);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 15:44
     * @Param:guids
     * @Description:删除能源类型
     * @Return:AjaxResult
     */
    @ApiOperation(value = "删除能源类型")
    @PreAuthorize("@ss.hasPermi('basicData:energyType:/remove')")
    @Log(title = "能源类型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{guids}")
    @ApiImplicitParam(name = "guids",value = "能源类型ID集合（以逗号隔开）",paramType = "path",required = true,allowMultiple = true)
    public AjaxResult remove(@PathVariable String[] guids)
    {
        return energyTypeService.deleteEnergyTypeByGuids(guids);
    }

    /**
     * 查询该园区下所有能耗类型
     *
     * @param parkCode 园区code
     * @return {@code List<EnergyType> }
     * @Author qindehua
     * @Date 2022/12/21
     **/
    @ApiOperation(value = "查询该园区下所有能耗类型")
    @GetMapping("/energyTypeList")
    @ApiImplicitParam(name = "parkCode",value = "园区编号",paramType = "query",required = true)
    public AjaxResult energyTypeList(String parkCode)
    {
        List<EnergyType> list = energyTypeService.findAllByParkCode(parkCode);
        return AjaxResult.success(list);
    }

    /**
     * 查询所有能耗类型
     *
     * @return {@code List<EnergyType> }
     * @Author qindehua
     * @Date 2022/12/21
     **/
    @ApiOperation(value = "查询所有能耗类型")
    @GetMapping("/allEnergyTypeList")
    @ApiOperationSupport(includeParameters = "treeId")
    @ApiImplicitParam(name = "treeId",value = "设备树ID",paramType = "query",required = true,dataType = "long")
    public AjaxResult allEnergyTypeList(Point point)
    {
        List<EnergyType> list = energyTypeService.allEnergyTypeList(point);
        return AjaxResult.success(list);
    }

}
