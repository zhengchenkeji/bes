package com.zc.efounder.JEnterprise.controller.deviceTree;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.service.deviceTree.AthenaElectricMeterService;
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
 * 电表信息Controller
 *
 * @author 孙山耕
 * @date 2022-09-14
 */
@RestController
@Api(value = "AthenaElectricMeterController", tags = {"电表信息"})
@ApiSupport(order = 12)
@RequestMapping("/deviceTree/meter")
public class AthenaElectricMeterController extends BaseController
{
    @Autowired
    private AthenaElectricMeterService athenaElectricMeterService;

//    /**
//     * 查询电表信息列表
//     */
//    @PreAuthorize("@ss.hasPermi('deviceTree:meter:list')")
//    @GetMapping("/list")
//    public TableDataInfo list(AthenaElectricMeter athenaElectricMeter)
//    {
//        startPage();
//        List<AthenaElectricMeter> list = athenaElectricMeterService.selectAthenaElectricMeterList(athenaElectricMeter);
//        return getDataTable(list);
//    }


    /**
     * 查询电表信息列表 不分页
     *
     * @param energyCode 能源代码
     * @param parkCode   园区代码
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2023/02/24
     **/
    @ApiOperation(value = "查询电表信息列表 不分页")
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:list')")
    @GetMapping("/listInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "energyCode",value = "能源编号",required = true,paramType = "query"),
            @ApiImplicitParam(name = "parkCode",value = "园区编号",required = true,paramType = "query"),
    })
    public AjaxResult listInfo( String energyCode,String parkCode)
    {
        return AjaxResult.success(athenaElectricMeterService.selectAthenaElectricMeterListInfo(energyCode,parkCode));
    }

    /**
     * 导出电表信息列表
     */
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:export')")
    @Log(title = "电表信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AthenaElectricMeter athenaElectricMeter)
    {
        List<AthenaElectricMeter> list = athenaElectricMeterService.selectAthenaElectricMeterList(athenaElectricMeter);
        ExcelUtil<AthenaElectricMeter> util = new ExcelUtil<>(AthenaElectricMeter.class);
        util.exportExcel(response, list, "电表信息数据");
    }

    /**
     * 获取电表信息详细信息
     */
    @ApiOperation(value = "获取电表信息详细信息")
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:query')")
    @GetMapping(value = "/{meterId}")
    @ApiImplicitParam(name = "meterId",value = "电表ID",required = true,paramType = "path",dataType = "long")
    public AjaxResult getInfo(@PathVariable("meterId") Long meterId)
    {
        return AjaxResult.success(athenaElectricMeterService.selectAthenaElectricMeterByMeterId(meterId));
    }

    /**
     * 新增电表信息
     */
    @ApiOperation(value = "新增电表信息")
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:add')")
    @Log(title = "电表信息", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters ={
            "deviceTreeId", "meterId"
    })
    @PostMapping
    public AjaxResult add(@RequestBody AthenaElectricMeter athenaElectricMeter)
    {
        return athenaElectricMeterService.insertAthenaElectricMeter(athenaElectricMeter);
    }

    /**
     * 修改电表信息
     */
    @ApiOperation(value = "修改电表信息")
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:edit')")
    @Log(title = "电表信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AthenaElectricMeter athenaElectricMeter)
    {
        return athenaElectricMeterService.updateAthenaElectricMeter(athenaElectricMeter);
    }

    /**
     * 查询采集方案列表
     */
    @ApiOperation(value = "查询采集方案列表")
    @GetMapping("/getCollectionMethodList")
    @ApiImplicitParam(name = "parkId",value = "园区ID",required = true,paramType = "query")
    public List<CollMethod> getCollectionMethodList( String parkId)
    {
        return athenaElectricMeterService.getCollectionMethodList(parkId);
    }

   /**
    * @description:查询电表字典
    * @author: sunshangeng
    * @date: 2022/9/22 15:17
    * @param: []
    * @return: com.ruoyi.common.core.domain.AjaxResult
    **/
   @ApiOperation(value = "查询电表字典")
    @GetMapping("/getMeterDicData")
    public AjaxResult getMeterDicData()
    {
        return AjaxResult.success(athenaElectricMeterService.selectAthenaElectricMeterDicData());
    }

    /**
     * @Description 同步电表
     *
     * @author liuwenge
     * @date 2022/9/23 8:50
     * @param athenaElectricMeter 电表信息
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @ApiOperation(value = "同步电表")
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:sync')")
    @PostMapping("/syncMeter")
    @ApiOperationSupport(includeParameters = {
            "meterId","deviceTreeId","deviceTreeFatherId"
    })
    public AjaxResult syncMeter(AthenaElectricMeter athenaElectricMeter)
    {
        return athenaElectricMeterService.syncMeter(athenaElectricMeter);

    }

    /**
     * @Description 获取电表电能参数
     *
     * @author liuwenge
     * @date 2022/10/8 9:40
     * @param deviceTreeId 设备树id
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @ApiOperation(value = "获取电表电能参数")
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:getElectricParams')")
    @GetMapping("/getElectricParams")
    @ApiImplicitParam(name = "deviceTreeId", value = "设备树ID",required = true,paramType = "query")
    public AjaxResult getElectricParams(String deviceTreeId)
    {
        return athenaElectricMeterService.getElectricParams(deviceTreeId);
    }

    /**
     * @Description 电表数据对比
     *
     * @author liuwenge
     * @date 2022/9/30 16:16
     * @param deviceTreeId 电表设备树id
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @ApiOperation(value = "电表数据对比")
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:contrast')")
    @GetMapping("/getMeterInfoParam")
    @ApiImplicitParam(name = "deviceTreeId",value = "设备树ID",required = true,paramType = "query")
    public AjaxResult getMeterInfoParam(String deviceTreeId)
    {
        return athenaElectricMeterService.getMeterInfoParam(deviceTreeId);
    }

    /**
     * @Description 获取电表实时数据
     *
     * @author liuwenge
     * @date 2022/10/8 11:58
     * @param deviceTreeId 设备树id
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @ApiOperation(value = "获取电表实时数据")
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:getMeterRealTimeData')")
    @GetMapping("/getMeterRealTimeData")
    @ApiImplicitParam(name = "deviceTreeId",value = "设备树ID",required = true,paramType = "query")
    public AjaxResult getMeterRealTimeData( String deviceTreeId)
    {
        return athenaElectricMeterService.getMeterRealTimeData(deviceTreeId);
    }

    /**
     * @Description 获取历史能耗数据
     *
     * @author liuwenge
     * @date 2022/10/8 14:48
     * @param deviceTreeId 设备树id
     * @param selectDay 查询日期
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @ApiOperation(value = "获取历史能耗数据")
    @PreAuthorize("@ss.hasPermi('deviceTree:meter:getMeterHistoryData')")
    @GetMapping("/getMeterHistoryData")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceTreeId",value = "设备树ID",required = true,paramType = "query"),
            @ApiImplicitParam(name = "selectDay",value = "查询日期类型",required = true,paramType = "query",dataType = "int")
    })
    public AjaxResult getMeterHistoryData( String deviceTreeId, Integer selectDay)
    {
        return athenaElectricMeterService.getMeterHistoryData(deviceTreeId,selectDay);
    }

    /**
     * 测试电表数据上传
     */
    @GetMapping("/testData")
    public void testData(Integer meterId,Integer collectCount,String electricData)
    {
        athenaElectricMeterService.testData(meterId,collectCount,electricData);
    }
}
