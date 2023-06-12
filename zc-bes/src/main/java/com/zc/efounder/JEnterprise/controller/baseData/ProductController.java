package com.zc.efounder.JEnterprise.controller.baseData;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.zc.efounder.JEnterprise.domain.baseData.*;
import com.zc.efounder.JEnterprise.domain.deviceTree.PointControlCommand;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.service.baseData.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 产品定义Controller
 *
 * @author gaojikun
 * @date 2023-03-07
 */
@RestController
@RequestMapping("/baseData/product")
@Api(value = "ProductController", tags = {"产品定义"})
public class ProductController extends BaseController {
    @Autowired
    private ProductService productService;

    /**
     * 查询产品定义列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:list')")
    @ApiOperation(value = "查询品类列表")
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "产品名称"),
            @ApiImplicitParam(name = "code",value = "产品编号"),
            @ApiImplicitParam(name = "state",value = "启用状态"),
            @ApiImplicitParam(name = "categoryId",value = "品类"),
            @ApiImplicitParam(name = "communicationProtocol",value = "通讯协议"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public PageInfoStream list(Product product) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        List<Product> list = productService.selectAthenaBesProductList(product);
        //java8流分页
        List<Product> subList = list.stream().skip((pageNum - 1) * pageSize).limit(pageSize).
                collect(Collectors.toList());
        PageInfoStream pageInfoStream = new PageInfoStream();
        pageInfoStream.setTotal(list.size());
        pageInfoStream.setData(subList);
        return pageInfoStream;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-03-08 16:03
     * @Description:查询所有产品
     * @Return:AjaxResult
     */
    @GetMapping("/getAllProduct")
    @ApiOperation(value = "查询所有产品")
    public AjaxResult getAllProduct(Product product) {
        return productService.selectAllProductList(product);
    }

    /**
     * 查询产品数据项列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:list')")
    @GetMapping("/listItemData")
    @ApiOperation(value = "查询数据项")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productCode",value = "产品编号"),
    })
    public PageInfoStream listItemData(ProductItemData productItemData) {
//        //startPage();
//        List<ProductItemData> list = productService.selectAthenaBesProductItemDataList(productItemData);
//        return getDataTable(list);
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        List<ProductItemData> list = productService.selectAthenaBesProductItemDataList(productItemData);
        //java8流分页
        List<ProductItemData> subList = list.stream().skip((pageNum - 1) * pageSize).limit(pageSize).
                collect(Collectors.toList());
        PageInfoStream pageInfoStream = new PageInfoStream();
        pageInfoStream.setTotal(list.size());
        pageInfoStream.setData(subList);
        return pageInfoStream;
    }

    @PreAuthorize("@ss.hasPermi('baseData:product:list')")
    @GetMapping("/listItemDataNoPaging")
    public AjaxResult listItemDataNoPaging(ProductItemData productItemData) {
        List<ProductItemData> list = productService.selectAthenaBesProductItemDataList(productItemData);
        return AjaxResult.success("查询成功",list);
    }

    /**
     * 查询产品配置-功能定义列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:list')")
    @GetMapping("/listFunction")
    @ApiOperation(value = "查询产品功能列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productCode",value = "产品编号"),
    })
    public PageInfoStream listFunction(ProductFunction productFunction) {
////        startPage();
//        List<ProductFunction> list = productService.selectAthenaBesProductFunctionList(productFunction);
//        return getDataTable(list);
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        List<ProductFunction> list = productService.selectAthenaBesProductFunctionList(productFunction);
        //java8流分页
        List<ProductFunction> subList = list.stream().skip((pageNum - 1) * pageSize).limit(pageSize).
                collect(Collectors.toList());
        PageInfoStream pageInfoStream = new PageInfoStream();
        pageInfoStream.setTotal(list.size());
        pageInfoStream.setData(subList);
        return pageInfoStream;
    }

    /**
     * 导出产品定义列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:export')")
    @Log(title = "产品定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Product product) {
        List<Product> list = productService.selectAthenaBesProductList(product);
        ExcelUtil<Product> util = new ExcelUtil<>(Product.class);
        util.exportExcel(response, list, "产品定义");
    }

    /**
     * 导出产品数据项列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:export')")
    @Log(title = "产品数据项", businessType = BusinessType.EXPORT)
    @PostMapping("/exportItemData")
    public void exportItemData(HttpServletResponse response, ProductItemData productItemData) {
        List<ProductItemData> list = productService.selectAthenaBesProductItemDataList(productItemData);
        ExcelUtil<ProductItemData> util = new ExcelUtil<>(ProductItemData.class);
        util.exportExcel(response, list, "产品数据项");
    }

    /**
     * 导出产品配置-功能定义列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:export')")
    @Log(title = "产品功能", businessType = BusinessType.EXPORT)
    @PostMapping("/exportFunction")
    public void exportFunction(HttpServletResponse response, ProductFunction productFunction) {
        List<ProductFunction> list = productService.selectAthenaBesProductFunctionList(productFunction);
        ExcelUtil<ProductFunction> util = new ExcelUtil<>(ProductFunction.class);
        util.exportExcel(response, list, "产品功能");
    }

    /**
     * 获取产品定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询产品详情")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return productService.selectAthenaBesProductById(id);
    }

    /**
     * 获取产品数据项详细信息
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:query')")
    @GetMapping(value = "/ItemData/{id}")
    @ApiOperation(value = "查询数据项详情")
    public AjaxResult getItemDataInfo(@PathVariable("id") Long id) {
        return productService.selectAthenaBesProductItemDataById(id);
    }

    /**
     * 获取产品配置-功能定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:function:query')")
    @GetMapping(value = "/function/{id}")
    @ApiOperation(value = "查询产品功能")
    public AjaxResult getFunctionInfo(@PathVariable("id") Long id) {
        return productService.selectAthenaBesProductFunctionById(id);
    }

    /**
     * 新增产品定义
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:add')")
    @Log(title = "产品定义", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增产品定义")
    public AjaxResult add(@RequestBody Product product) {
        return productService.insertAthenaBesProduct(product);
    }

    /**
     * 新增产品数据项
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:add')")
    @Log(title = "产品定义", businessType = BusinessType.INSERT)
    @PostMapping(value = "/ItemData")
    @ApiOperation(value = "新增数据项")
    public AjaxResult addItemData(@RequestBody ProductItemData productItemData) {
        return productService.insertAthenaBesProductItemData(productItemData);
    }

    /**
     * 新增产品配置-功能定义
     */
    @PreAuthorize("@ss.hasPermi('system:function:add')")
    @Log(title = "产品配置-功能定义", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增产品功能")
    @PostMapping(value = "/function")
    public AjaxResult addFunction(@RequestBody ProductFunction productFunction) {
        return productService.insertAthenaBesProductFunction(productFunction);
    }

    /**
     * 修改产品定义
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:edit')")
    @Log(title = "产品定义", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改产品定义")
    public AjaxResult edit(@RequestBody Product product) {
        return productService.updateAthenaBesProduct(product);
    }

    /**
     * 修改产品数据项
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:edit')")
    @Log(title = "产品定义", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/ItemData")
    @ApiOperation(value = "修改数据项定义")
    public AjaxResult editItemData(@RequestBody ProductItemData productItemData) {
        return productService.updateAthenaBesProductItemData(productItemData);
    }

    /**
     * 修改产品配置-功能定义
     */
    @PreAuthorize("@ss.hasPermi('system:function:edit')")
    @Log(title = "产品配置-功能定义", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/function")
    @ApiOperation(value = "修改功能定义")
    public AjaxResult editFunction(@RequestBody ProductFunction productFunction) {
        return productService.updateAthenaBesProductFunction(productFunction);
    }

    /**
     * 删除产品定义
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:remove')")
    @Log(title = "产品定义", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除产品定义")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return productService.deleteAthenaBesProductByIds(ids);
    }

    /**
     * 删除产品数据项
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:remove')")
    @Log(title = "产品定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/ItemData/{ids}")
    @ApiOperation(value = "删除数据项定义")
    public AjaxResult removeItemData(@PathVariable Long[] ids) {
        return productService.deleteAthenaBesProductItemDataByIds(ids);
    }

    /**
     * 删除产品配置-功能定义
     */
    @PreAuthorize("@ss.hasPermi('system:function:remove')")
    @Log(title = "产品配置-功能定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/function/{ids}")
    @ApiOperation(value = "删除功能定义")

    public AjaxResult removeFunction(@PathVariable Long[] ids) {
        return productService.deleteAthenaBesProductFunctionByIds(ids);
    }

    /**
     * 查询数据类型list
     */
    @GetMapping("/getAllIotTypeList")
    @ApiOperation(value = "查询数据类型")
    public AjaxResult getAllIotTypeList(@RequestParam("dictType") String dictType) {
        String id = "";
        List<Map<String, Object>> list = productService.getAllIotTypeList(dictType, id);
        return AjaxResult.success(list);
    }

    /**
     * 查询数据类型list
     */
    @GetMapping("/getAllIotTypeIdList")
    public AjaxResult getAllIotTypeIdList(@RequestParam("dictType") String dictType, @RequestParam("id") String id) {
        List<Map<String, Object>> list = productService.getAllIotTypeList(dictType, id);
        return AjaxResult.success(list);
    }

    /**
     * 查询物联类型/消息协议list
     */
    @GetMapping("/getAllMessageList")
    public AjaxResult getAllMessageList(@RequestParam("dictType") String dictType) {
        String id = "";
        List<Map<String, Object>> list = productService.getAllMessageIdList(dictType, id);
        return AjaxResult.success(list);
    }

    /**
     * 查询物联类型/消息协议list
     */
    @GetMapping("/getAllMessageIdList")
    public AjaxResult getAllMessageIdList(@RequestParam("dictType") String dictType, @RequestParam("id") String id) {
        List<Map<String, Object>> list = productService.getAllMessageIdList(dictType, id);
        return AjaxResult.success(list);
    }

    /**
     * 查询对应的消息协议list
     */
    @GetMapping("/getMessageListById")
    public AjaxResult getMessageListById(@RequestParam("id") String id) {
        return productService.getMessageListById(id);
    }


    /**
     * @description:根据产品查询协议
     * @author: sunshangeng
     * @date: 2023/3/15 15:13
     * @param: [ProductId]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @GetMapping("/getAgreeMentByProductId")
    public AjaxResult getAgreeMentByProductId(@RequestParam("ProductId") Long ProductId ){
        return  productService.getAgreeMentByProductId(ProductId);
    }

    /**
    * @Author:gaojikun
    * @Date:2023-03-20 17:38
    * @Param: parkCode
    * @Description: 查询所有能源类型
    * @Return: AjaxResult
    */
    @GetMapping("/energyTypeList")
    public AjaxResult energyTypeList()
    {
        return productService.energyTypeList();
    }

    /**
     * @Description:查询数据项及相应参数配置
     * @Author:gaojikun
     * @Return: AjaxResult
     */
    @GetMapping("/selectItemDataList")
    public AjaxResult selectItemDataList(PointControlCommand pointControlCommand) {
        return productService.selectItemDataList(pointControlCommand);
    }

    /**
     * @Description:点位数据项是否含有读功能
     * @Author:gaojikun
     * @Return: AjaxResult
     */
    @GetMapping("/queryItemDataInfoByFunctionId")
    public AjaxResult queryItemDataInfoByFunctionId(PointControlCommand pointControlCommand) {
        return productService.queryItemDataInfoByFunctionId(pointControlCommand);
    }

}
