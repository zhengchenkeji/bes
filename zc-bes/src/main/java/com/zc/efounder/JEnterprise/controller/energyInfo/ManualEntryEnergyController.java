package com.zc.efounder.JEnterprise.controller.energyInfo;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.ManualEntryEnergy;
import com.zc.efounder.JEnterprise.domain.energyInfo.ManualentryenergyCollection;
import com.zc.efounder.JEnterprise.service.energyInfo.ManualEntryEnergyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;


/**
 * description:手动录入能耗控制器
 * author: sunshangeng
 * date:2022/12/2 12:24
 */
@RestController
@RequestMapping("/basicData/manualEntryEnergy")
@Api(value = "ManualEntryEnergyController", tags = {"手动录入能耗"})
@ApiSupport(order = 21,author = "sunshangeng")
public class ManualEntryEnergyController extends BaseController {


    @Resource
    private ManualEntryEnergyService manualEntryEnergyService;


    /**
     * @description:添加能耗
     * @author: sunshangeng
     * @param: [manualentryenergy]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/

    @PostMapping
    @ApiOperation(value = "添加能耗")
    @ApiOperationSupport(ignoreParameters = {"id"})
    public AjaxResult insertManualentryenergy(@RequestBody ManualEntryEnergy manualentryenergy){
        return  manualEntryEnergyService.insertManualentryenergy(manualentryenergy);

    }

    /**
     * @description:获取设备树结构
     * @author: sunshangeng
     * @param: [manualentryenergy]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @ApiOperation(value = "获取设备树结构")
    @GetMapping("/treeSelect")
    public AjaxResult treeSelect(){

        return  manualEntryEnergyService.treeSelect();

    }

    /**
     * @description:获取采集参数
     * @author: sunshangeng
     * @param: [manualentryenergy]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @GetMapping("/getElectricParams")
    @ApiOperation(value = "获取采集参数")
    @ApiImplicitParam(name = "meterid",value = "电表ID",required = true,dataType = "query")
    public AjaxResult getElectricParams(String meterid){
        return  manualEntryEnergyService.getElectricParams(meterid);
    }

    /**
     * @description:导入能耗
     * @author: sunshangeng
     * @param: [request, file, updateSupport]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/

    @PostMapping("/importData")
    @ApiOperation(value = "导入能耗")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file",value = "需要导入的文件",dataType = "file",paramType = "formData",required = true)
    })
    public AjaxResult importData(HttpServletRequest request, MultipartFile file,@ApiIgnore boolean updateSupport) throws Exception
    {
        AjaxResult ajaxResult = manualEntryEnergyService.impExcel(request,file);

        return ajaxResult;
    }


    /**
     * @description:获取采集参数
     * @author: sunshangeng
     * @param: [manualentryenergy]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @GetMapping("/getmanualEntryEnergyData")
    @ApiOperation(value = "获取采集参数列表")
    @ApiOperationSupport(includeParameters = {"energyCjsj","energyType","pointTreeid",})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "energyCjsj",value = "采集时间",dataType = "Date"),
            @ApiImplicitParam(name = "energyType",value = "能源类型"),
            @ApiImplicitParam(name = "pointTreeid",value = "节点id",dataType = "Long"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo getmanualEntryEnergyData( ManualEntryEnergy manualEntryEnergy){
        startPage();
        List<ManualEntryEnergy> entryEnergyList = manualEntryEnergyService.getmanualEntryEnergyData(manualEntryEnergy);
        return getDataTable(entryEnergyList) ;
    }



    /**
     * @description:根据能耗id获取能耗详情
     * @author: sunshangeng
     * @param: [manualEntryEnergy]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    @ApiOperation(value = "根据能耗id获取能耗详情")
    @GetMapping("/getEnergyDetailData")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entryEnergyId",value = "手动录入采集参数id",required = true),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo getEnergyDetailData(String entryEnergyId){
        startPage();
        List<ManualentryenergyCollection> energyDetailData = manualEntryEnergyService.getEnergyDetailData(entryEnergyId);

        return getDataTable(energyDetailData) ;
    }

    /**删除能耗信息*/
    @Log(title = "手动录入能耗信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiOperation(value = "删除能耗信息")
    @ApiImplicitParam(name = "ids",value = "手动录入能耗ID集合(以逗号隔开)",dataType = "Long",required = true,allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids) throws Exception {
        return manualEntryEnergyService.deleteManualentryenergy(ids);
    }

    /**
     * 导出能耗数据
     */
    @PostMapping("/export")
    @ApiOperation(value = "导出能耗数据")
    @ApiOperationSupport(includeParameters = {"energyCjsj","energyType","pointTreeid",})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "energyCjsj",value = "采集时间",dataType = "Date"),
            @ApiImplicitParam(name = "energyType",value = "能源类型"),
            @ApiImplicitParam(name = "pointTreeid",value = "节点id",dataType = "Long"),
    })
    public void export(HttpServletResponse response, ManualEntryEnergy manualentryenergy)
    {
        List<ManualEntryEnergy> entryEnergyList = manualEntryEnergyService.exportManualEntryEnergyData(manualentryenergy);
        ExcelUtil<ManualEntryEnergy> util = new ExcelUtil<>(ManualEntryEnergy.class);
        util.exportExcel(response, entryEnergyList, "手动输入能耗数据");
    }

    /**
     * 查询所有能耗类型
     *
     * @return {@code List<EnergyType> }
     * @Author sunshangeng
     **/
    @GetMapping("/allEnergyTypeList")
    @ApiOperation(value = "查询所有能耗类型")
    public AjaxResult allEnergyTypeList()
    {
        Collection<EnergyType> list = manualEntryEnergyService.allEnergyTypeList();
        return AjaxResult.success(list);
    }
}
