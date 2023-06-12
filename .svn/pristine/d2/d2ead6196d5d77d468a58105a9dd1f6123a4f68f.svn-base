package com.zc.efounder.JEnterprise.controller.energyCollection;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.zc.efounder.JEnterprise.domain.energyCollection.MeterType;
import com.zc.efounder.JEnterprise.mapper.energyCollection.MeterTypeMapper;
import com.zc.efounder.JEnterprise.service.energyCollection.MeterTypeService;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * 电表类型定义Controller
 *
 * @author ruoyi
 * @date 2022-09-07
 */
@RestController
@RequestMapping("/basicData/meterType")
@Api(value = "MeterTypeController", tags = {"电表类型定义"})
@ApiSupport(order = 20)
public class MeterTypeController extends BaseController
{
    @Autowired
    private MeterTypeService meterTypeService;
    @Autowired
    private MeterTypeMapper meterTypeMapper;

    /**
     * 查询电表类型定义列表
     */
    @ApiOperation(value = "查询电表类型定义列表")
    @PreAuthorize("@ss.hasPermi('basicData:meterType:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "name", value = "类型名称",  paramType = "query"),
            @ApiImplicitParam(name = "code", value = "类型编号",  paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(MeterType meterType)
    {
        startPage();
        List<MeterType> list = meterTypeService.selectMeterTypeList(meterType);
        return getDataTable(list);
    }

    /**
     * 导出电表类型定义列表
     */
    @ApiOperation(value = "导出电表类型定义列表")
    @PreAuthorize("@ss.hasPermi('basicData:meterType:export')")
    @Log(title = "电表类型定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "类型名称",  paramType = "query"),
            @ApiImplicitParam(name = "code", value = "类型编号",  paramType = "query"),
    })
    public void export(HttpServletResponse response, MeterType meterType)
    {
        List<MeterType> list = meterTypeService.selectMeterTypeList(meterType);
        ExcelUtil<MeterType> util = new ExcelUtil<>(MeterType.class);
        util.exportExcel(response, list, "电表类型定义数据");
    }

    /**
     * 导出电表类型定义列表
     */
    @PreAuthorize("@ss.hasPermi('basicData:meterType:export')")
    @Log(title = "电表类型定义模板", businessType = BusinessType.EXPORT)
    @PostMapping("/exportModel")
    public void exportModel(HttpServletResponse response, MeterType meterType)
    {
        List<MeterType> list = new ArrayList<>();
        MeterType meterTypeModel = new MeterType();
        meterTypeModel.setCode("此处填写类型编号");
        meterTypeModel.setName("此处填写类型名称");
        list.add(meterTypeModel);
        ExcelUtil<MeterType> util = new ExcelUtil<>(MeterType.class);
        util.exportExcel(response, list, "电表类型定义模板");
    }

    /**
     * 电表类型定义数据导入
     * */
    @ApiOperation(value = "电表类型定义数据导入")
    @Log(title = "物联设备数据导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('basicData:meterType:exportt')")
    @PostMapping("/importData")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file",value = "文件",dataType = "file",paramType = "formData",required = true),
            @ApiImplicitParam(name = "updateSupport",value = "是否更新支持，如果已存在，则进行更新数据",dataType = "boolean",paramType = "query",required = true)
    })
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<MeterType> util = new ExcelUtil<MeterType>(MeterType.class);
        List<MeterType> meterTypeList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = meterTypeService.importMeterType(meterTypeList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 获取电表类型定义详细信息
     */
    @ApiOperation(value = "获取电表类型定义详细信息")
    @PreAuthorize("@ss.hasPermi('basicData:meterType:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "电表类型唯一ID",paramType = "path",dataType = "long",required = true)
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(meterTypeService.selectMeterTypeById(id));
    }

    /**
     * 新增电表类型定义
     */
    @ApiOperation(value = "新增电表类型定义")
    @PreAuthorize("@ss.hasPermi('basicData:meterType:add')")
    @ApiOperationSupport(ignoreParameters = "id")
    @Log(title = "电表类型定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MeterType meterType)
    {
        List<MeterType> meterTypes = meterTypeMapper.selectMeterTypeByMeterType(meterType);
        if(meterTypes.size()>0){
            return error("编号/名称重复，请检查");
        }
        return toAjax(meterTypeService.insertMeterType(meterType));
    }

    /**
     * 修改电表类型定义
     */
    @ApiOperation(value = "修改电表类型定义")
    @PreAuthorize("@ss.hasPermi('basicData:meterType:edit')")
    @Log(title = "电表类型定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MeterType meterType)
    {
        List<MeterType> meterTypes = meterTypeMapper.selectMeterTypeByMeterTypeEdit(meterType);
        if(meterTypes.size()>0){
            return error("名称重复，请检查");
        }
        return toAjax(meterTypeService.updateMeterType(meterType));
    }

    /**
     * 删除电表类型定义
     */
    @ApiOperation(value = "删除电表类型定义")
    @PreAuthorize("@ss.hasPermi('basicData:meterType:remove')")
    @Log(title = "电表类型定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "电表类型唯一ID集合(以逗号隔开)",paramType = "path",dataType = "long",required = true,allowMultiple = true)
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(meterTypeService.deleteMeterTypeByIds(ids));
    }
}
