package com.zc.efounder.JEnterprise.controller.baseData;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.zc.efounder.JEnterprise.domain.baseData.Category;
import com.zc.efounder.JEnterprise.service.baseData.CategoryService;
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
 * 品类Controller
 *
 * @author sunshangeng
 * @date 2023-03-06
 */
@RestController
@RequestMapping("/baseData/category")
@Api(value = "CategoryController", tags = {"品类定义"})

public class CategoryController extends BaseController
{
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询品类列表
     */
    @ApiOperation(value = "查询品类列表")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "品类主键"),
            @ApiImplicitParam(name = "categoryMark",value = "品类标识"),
            @ApiImplicitParam(name = "categoryName",value = "品类名称"),
            @ApiImplicitParam(name = "iotQuipment",value = "是否物联设备"),
            @ApiImplicitParam(name = "cruxQuipment",value = "是否关键设备"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(Category Category)
    {
        startPage();
        List<Category> list = categoryService.selectCategoryList(Category);
        return getDataTable(list);
    }

    /**
      * @description:查询全部品类列表
      * @author: sunshangeng
      * @date: 2023/3/7 9:50
      * @param: [Category]
      * @return:
      *
     */
    @ApiOperation(value = "查询全部品类列表")
    @GetMapping("/getAllCategorylist")
    public AjaxResult getAllCategorylist()
    {
        List<Category> list = categoryService.selectCategoryList(null);
        return AjaxResult.success(list);
    }
    /**
     * 导出品类列表
     */
    @ApiOperation(value = "导出品类列表")
    @Log(title = "品类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Category Category)
    {
        List<Category> list = categoryService.selectCategoryList(Category);
        ExcelUtil<Category> util = new ExcelUtil<>(Category.class);
        util.exportExcel(response, list, "品类数据");
    }

    /**
     * 获取品类详细信息
     */
    @ApiOperation(value = "获取品类详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(categoryService.selectCategoryById(id));
    }

    /**
     * 新增品类
     */
    @Log(title = "品类", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增品类")
    @PostMapping
    public AjaxResult add(@RequestBody @Valid Category Category)
    {
        return categoryService.insertCategory(Category);
    }

    /**
     * 修改品类
     */
    @ApiOperation(value = "修改品类")
    @Log(title = "品类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Valid  Category Category)
    {
        return categoryService.updateCategory(Category);
    }

    /**
     * 删除品类
     */
    @ApiOperation(value = "删除品类")
    @Log(title = "品类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return categoryService.deleteCategoryByIds(ids);
    }
}
