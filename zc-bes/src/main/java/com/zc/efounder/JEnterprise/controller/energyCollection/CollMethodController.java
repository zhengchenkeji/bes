package com.zc.efounder.JEnterprise.controller.energyCollection;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.mapper.energyCollection.CollMethodMapper;
import com.zc.efounder.JEnterprise.service.energyCollection.CollMethodService;
import com.zc.efounder.JEnterprise.service.energyCollection.ElectricCollRlglService;
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
 * 采集方案Controller
 *
 * @author gaojikun
 * @date 2022-09-08
 */
@RestController
@RequestMapping("/basicData/collMethod")
@Api(value = "CollMethodController", tags = {"采集方案"})
@ApiSupport(order = 18,author = "gaojikun")
public class CollMethodController extends BaseController
{
    @Autowired
    private CollMethodService collMethodService;

    @Autowired
    private ElectricCollRlglService electricCollRlglService;

    @Autowired
    private CollMethodMapper collMethodMapper;

    /**
    * @Author:gaojikun
    * @Date:2023-01-13 16:06
    * @Param:collMethod
    * @Description:查询采集方案列表
    * @Return:TableDataInfo
    */
    @ApiOperation(value = "查询采集方案列表")
    @PreAuthorize("@ss.hasPermi('basicData:collMethod:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "名称"),
            @ApiImplicitParam(name = "code",value = "名称"),
            @ApiImplicitParam(name = "energyCode",value = "能源编号"),
            @ApiImplicitParam(name = "parkCode",value = "园区编号"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(CollMethod collMethod)
    {
        startPage();
        List<CollMethod> list = collMethodService.selectCollMethodList(collMethod);
        return getDataTable(list);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:查询能耗类型
     * @Return:List<Map<String,String>>
     */
    @ApiOperation(value = "查询能耗类型")
    @GetMapping("/listEnergy")
    @ApiOperationSupport(includeParameters = "parkCode")
    public List<Map<String,String>> listEnergy(CollMethod collMethod)
    {
        List<Map<String,String>> list = collMethodMapper.findPark_EnergyType(collMethod.getParkCode(),"");
        return list;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:导出采集方案列表
     * @Return:
     */
    @PreAuthorize("@ss.hasPermi('basicData:collMethod:export')")
    @Log(title = "采集方案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CollMethod collMethod)
    {
        List<CollMethod> list = collMethodService.selectCollMethodList(collMethod);
        ExcelUtil<CollMethod> util = new ExcelUtil<>(CollMethod.class);
        util.exportExcel(response, list, "采集方案数据");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:code
     * @Description:获取采集方案详细信息
     * @Return:AjaxResult
     */
    @ApiOperation(value = "获取采集方案详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:collMethod:query')")
    @GetMapping(value = "/{code}")
    @ApiImplicitParam(name = "code",value = "采集方案编号",paramType = "path",required = true)
    public AjaxResult getInfo(@PathVariable("code") Long code)
    {
        return AjaxResult.success(collMethodService.selectCollMethodByCode(code));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:新增采集方案
     * @Return:AjaxResult
     */
    @ApiOperation(value = "新增采集方案")
    @PreAuthorize("@ss.hasPermi('basicData:collMethod:add')")
    @Log(title = "采集方案", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = "id")
    @PostMapping
    public AjaxResult add(@RequestBody CollMethod collMethod)
    {
        return collMethodService.insertCollMethod(collMethod);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:collMethod
     * @Description:修改采集方案
     * @Return:AjaxResult
     */
    @ApiOperation(value = "修改采集方案")
    @PreAuthorize("@ss.hasPermi('basicData:collMethod:edit')")
    @Log(title = "采集方案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CollMethod collMethod)
    {
        return collMethodService.updateCollMethod(collMethod);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:codes
     * @Description:删除采集方案
     * @Return:AjaxResult
     */
    @ApiOperation(value = "删除采集方案")
    @PreAuthorize("@ss.hasPermi('basicData:collMethod:remove')")
    @Log(title = "采集方案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{codes}")
    @ApiImplicitParam(name = "codes",value = "采集方案编号集合（以逗号隔开）",paramType = "path",required = true,allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] codes)
    {
        return collMethodService.deleteCollMethodByCodes(codes);
    }


    //------------------------能耗参数--------------------------------------------------
    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:查询电能参数采集方案关系列表
     * @Return:List<ElectricParams>
     */
    @ApiOperation(value = "查询电能参数采集方案关系列表")
    @GetMapping("/rlglCheckList")
    public List<ElectricParams> rlglCheckList(ElectricCollRlgl electricCollRlgl)
    {
        List<ElectricParams> list = electricCollRlglService.rlglCheckList(electricCollRlgl);
        return list;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:查询电能参数采集方案关系列表
     * @Return:List<ElectricParams>
     */
    @ApiOperation(value = "查询电能参数采集方案关系列表")
    @GetMapping("/rlglNoCheckList")
    public List<ElectricParams> rlglNoCheckList(ElectricCollRlgl electricCollRlgl)
    {
        List<ElectricParams> list = electricCollRlglService.rlglNoCheckList(electricCollRlgl);
        return list;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:全部选中
     * @Return:AjaxResult
     */
    @ApiOperation(value = "全部选中")
    @GetMapping("/allCheckList")
    public AjaxResult allCheckList(ElectricCollRlgl electricCollRlgl)
    {
        return toAjax(electricCollRlglService.allCheckList(electricCollRlgl));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:全部取消
     * @Return:AjaxResult
     */
    @ApiOperation(value = "全部取消")
    @GetMapping("/allNoCheckList")
    public AjaxResult allNoCheckList(ElectricCollRlgl electricCollRlgl)
    {
        return electricCollRlglService.allNoCheckList(electricCollRlgl);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:新增一条能耗
     * @Return:AjaxResult
     */
    @ApiOperation(value = "新增一条能耗")
    @GetMapping("/insertElectricCollRlgl")
    public AjaxResult insertElectricCollRlgl(ElectricCollRlgl electricCollRlgl)
    {
        return electricCollRlglService.insertElectricCollRlgl(electricCollRlgl);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:取消一条能耗
     * @Return:AjaxResult
     */
    @ApiOperation(value = "取消一条能耗")
    @GetMapping("/deleteElectricCollRlgl")
    public AjaxResult deleteElectricCollRlgl(ElectricCollRlgl electricCollRlgl)
    {
        return electricCollRlglService.deleteElectricCollRlgl(electricCollRlgl);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 16:06
     * @Param:electricCollRlgl
     * @Description:修改一条能耗
     * @Return:AjaxResult
     */
    @ApiOperation(value = "修改一条能耗")
    @GetMapping("/changeRlglVlaue")
    public AjaxResult changeRlglVlaue(ElectricCollRlgl electricCollRlgl)
    {
        return electricCollRlglService.changeRlglVlaue(electricCollRlgl);
    }
}
