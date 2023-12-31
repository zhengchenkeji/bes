package com.zc.efounder.JEnterprise.controller.energyCollection;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.service.energyCollection.ElectricParamsService;
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
 * 采集参数Controller
 *
 * @author gaojikun
 * @date 2022-09-07
 */
@RestController
@RequestMapping("/basicData/acquisitionParam")
@Api(value = "ElectricParamsController", tags = {"采集参数"})
@ApiSupport(order = 19,author = "gaojikun")
public class ElectricParamsController extends BaseController {
    @Autowired
    private ElectricParamsService electricParamsService;

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:查询采集参数列表
     * @Return:TableDataInfo
     */
    @ApiOperation(value = "查询采集参数列表")
    @PreAuthorize("@ss.hasPermi('basicData:acquisitionParam:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code",value = "能耗编号"),
            @ApiImplicitParam(name = "energyCode",value = "能源类型"),
            @ApiImplicitParam(name = "name",value = "能耗名称"),
            @ApiImplicitParam(name = "offsetAddress",value = "偏移地址"),
            @ApiImplicitParam(name = "dataLength",value = "数据长度",dataType = "long"),
            @ApiImplicitParam(name = "dataEncodeType",value = "编码规则;0-bcd编码;1-dec编码;2-其他",dataType = "long"),
            @ApiImplicitParam(name = "unit",value = "单位"),
            @ApiImplicitParam(name = "pointLocation",value = "小数点位置",dataType = "long"),
            @ApiImplicitParam(name = "dataType",value = "数据类型;0-int;1-float;2-double",dataType = "long"),
            @ApiImplicitParam(name = "codeSeq",value = "解码顺序;0-12;1-21;2-1234;3-4321;4-2143;5-3412;6-123456;7-12345678;",dataType = "long"),
            @ApiImplicitParam(name = "parkCode",value = "园区编号"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(ElectricParams electricParams) {
        startPage();
        List<ElectricParams> list = electricParamsService.selectElectricParamsList(electricParams);
        return getDataTable(list);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:导出采集参数列表
     * @Return:
     */
    @PreAuthorize("@ss.hasPermi('basicData:acquisitionParam:export')")
    @Log(title = "采集参数", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ElectricParams electricParams) {
        List<ElectricParams> list = electricParamsService.selectElectricParamsList(electricParams);
        ExcelUtil<ElectricParams> util = new ExcelUtil<>(ElectricParams.class);
        util.exportExcel(response, list, "采集参数数据");
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:id
     * @Description:获取采集参数详细信息
     * @Return:AjaxResult
     */
    @ApiOperation(value = "获取采集参数详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:acquisitionParam:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "采集参数Id",paramType = "path",required = true,dataType = "long")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(electricParamsService.selectElectricParamsById(id));
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:新增采集参数
     * @Return:AjaxResult
     */
    @ApiOperation(value = "新增采集参数")
    @PreAuthorize("@ss.hasPermi('basicData:acquisitionParam:add')")
    @Log(title = "采集参数", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = "id")
    @PostMapping
    public AjaxResult add(@RequestBody ElectricParams electricParams) {
        return electricParamsService.insertElectricParams(electricParams);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:electricParams
     * @Description:修改采集参数
     * @Return:AjaxResult
     */
    @ApiOperation(value = "修改采集参数")
    @PreAuthorize("@ss.hasPermi('basicData:acquisitionParam:edit')")
    @Log(title = "采集参数", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ElectricParams electricParams) {
        return electricParamsService.updateElectricParams(electricParams);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-01-13 13:45
     * @Param:ids
     * @Description:删除采集参数
     * @Return:AjaxResult
     */
    @ApiOperation(value = "删除采集参数")
    @PreAuthorize("@ss.hasPermi('basicData:acquisitionParam:remove')")
    @Log(title = "采集参数", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "采集参数Id集合(以逗号隔开)",paramType = "path",required = true,dataType = "long",allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids) {
        return electricParamsService.deleteElectricParamsByIds(ids);
    }

    /**
     * @description:获取采集参数字典
     * @author: sunshangeng
     * @date: 2022/9/22 14:57
     * @param: [电表id]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    @ApiOperation(value = "获取采集参数字典")
    @GetMapping("/getElectricParamsDatalist")
    @ApiImplicitParam(name = "meterid",value = "电表ID",required = true,paramType = "query")
    public TableDataInfo getElectricParamsDatalist(String meterid) {
        List<ElectricParams> list = electricParamsService.selectElectricParamsListbymeterid(meterid);
        return getDataTable(list);
    }

}
