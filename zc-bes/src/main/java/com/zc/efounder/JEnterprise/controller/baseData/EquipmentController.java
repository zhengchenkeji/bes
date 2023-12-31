package com.zc.efounder.JEnterprise.controller.baseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.PageInfoStream;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.baseData.WarnItemData;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.service.baseData.EquipmentService;
import io.swagger.annotations.ApiImplicitParam;
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
 * 物联设备Controller
 *
 * @author gaojikun
 * @date 2023-03-08
 */
@RestController
@RequestMapping("/baseData/equipment")
public class EquipmentController extends BaseController
{
    @Autowired
    private EquipmentService athenaBesEquipmentService;

    /**
     * 查询物联设备列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:list')")
    @GetMapping("/list")
    public PageInfoStream list(Equipment athenaBesEquipment)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        List<Equipment> list = athenaBesEquipmentService.selectAthenaBesEquipmentList(athenaBesEquipment);
        //java8流分页
        List<Equipment> subList = list.stream().skip((pageNum-1)*pageSize).limit(pageSize).
                collect(Collectors.toList());
        PageInfoStream pageInfoStream = new PageInfoStream();
        pageInfoStream.setTotal(list.size());
        pageInfoStream.setData(subList);
        return pageInfoStream;
    }

    /**
     * 查询物联子设备列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:list')")
    @GetMapping("/sonList")
    public PageInfoStream sonList(Equipment athenaBesEquipment)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        List<Equipment> list = athenaBesEquipmentService.selectAthenaBesEquipmentList(athenaBesEquipment);
        //java8流分页
        List<Equipment> subList = list.stream().skip((pageNum-1)*pageSize).limit(pageSize).
                collect(Collectors.toList());
        PageInfoStream pageInfoStream = new PageInfoStream();
        pageInfoStream.setTotal(list.size());
        pageInfoStream.setData(subList);
        return pageInfoStream;
    }

    /**
     * 导出物联设备列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:export')")
    @Log(title = "物联设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Equipment athenaBesEquipment)
    {
        List<Equipment> list = athenaBesEquipmentService.selectAthenaBesEquipmentList(athenaBesEquipment);
        ExcelUtil<Equipment> util = new ExcelUtil<>(Equipment.class);
        util.exportExcel(response, list, "物联设备数据");
    }

    /**
     * 导出物联设备模板
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:export')")
    @Log(title = "物联设备", businessType = BusinessType.EXPORT)
    @PostMapping("/exportModel")
    public void exportModel(HttpServletResponse response, Equipment athenaBesEquipment)
    {
        athenaBesEquipment.setParentCode("父设备编号");
        athenaBesEquipment.setProductCode("产品编号");
        athenaBesEquipment.setCode("设备编号");
        athenaBesEquipment.setName("设备名称");
        athenaBesEquipment.setState("0");
        athenaBesEquipment.setNetworkType("0");
        athenaBesEquipment.setIpAddress("ip地址");
        athenaBesEquipment.setPortNum("端口");
        List<Equipment> list = new ArrayList<>();
        list.add(athenaBesEquipment);
        ExcelUtil<Equipment> util = new ExcelUtil<>(Equipment.class);
        util.exportExcel(response, list, "物联设备数据");
    }

    /**
     * 电表类型定义数据导入
     * */
    @Log(title = "物联设备数据导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('baseData:equipment:export')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<Equipment> util = new ExcelUtil<Equipment>(Equipment.class);
        List<Equipment> equipmentList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = athenaBesEquipmentService.importEquipment(equipmentList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 获取物联设备详细信息
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return athenaBesEquipmentService.selectAthenaBesEquipmentById(id);
    }

    /**
     * 获取物联设备详情
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:query')")
    @GetMapping(value = "/info/{id}")
    public AjaxResult getInfoById(@PathVariable("id") Long id)
    {
        return athenaBesEquipmentService.selectAthenaBesEquipmentInfoById(id);
    }

    /**
     * 获取物联设备实时数据
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:query')")
    @GetMapping(value = "/actualTime/{id}")
    public AjaxResult getActualTimeById(@PathVariable("id") Long id)
    {
        return athenaBesEquipmentService.selectAthenaBesEquipmentActualTimeById(id);
    }

    /**
     * 获取物联设备是保存的数据项列表
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:query')")
    @GetMapping(value = "/actualTimePreserve/{id}")
    public AjaxResult getActualTimePreserve(@PathVariable("id") Long id)
    {
        return athenaBesEquipmentService.getActualTimePreserve(id);
    }

    /**
     * 查询物联设备历史数据
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:list')")
    @GetMapping("/getHistoryList")
    public TableDataInfo getHistoryList(Equipment e) {
//        startPage();
        List<Map<String,Object>> list = athenaBesEquipmentService.selectItemDataHistoryListById(e);
        return getDataTable(list);
    }


    /**
     * 查询物联设备报警历史数据
     */
    @PreAuthorize("@ss.hasPermi('baseData:product:list')")
    @GetMapping("/getWarnDataList")
    public TableDataInfo getWarnDataList(Equipment e) {
        List<WarnItemData> list = athenaBesEquipmentService.selectItemDataWarnDataListById(e);
        return getDataTable(list);
    }



    /**
     * 新增物联设备
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:add')")
    @Log(title = "物联设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Equipment athenaBesEquipment)
    {
        return athenaBesEquipmentService.insertAthenaBesEquipment(athenaBesEquipment);
    }

    /**
     * 修改物联设备
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:edit')")
    @Log(title = "物联设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Equipment athenaBesEquipment)
    {
        return athenaBesEquipmentService.updateAthenaBesEquipment(athenaBesEquipment);
    }

    /**
     * 修改物联设备
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:edit')")
    @GetMapping("/updateAthenaBesEquipmentOfflineAlarm")
    public AjaxResult updateAthenaBesEquipmentOfflineAlarm(Equipment athenaBesEquipment)
    {
        return athenaBesEquipmentService.updateAthenaBesEquipmentOfflineAlarm(athenaBesEquipment);
    }

    /**
     * 删除物联设备
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:remove')")
    @Log(title = "物联设备", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return athenaBesEquipmentService.deleteAthenaBesEquipmentByIds(ids);
    }

    /**
     *
     * @Description: 物联设备功能结构树
     *
     * @auther: wanghongjie
     * @date: 15:50 2023/3/30
     * @param:
     * @return:
     *
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:functionTree')")
    @Log(title = "物联设备功能结构树", businessType = BusinessType.DELETE)
    @GetMapping("/equipmentFunctionTree")
    public AjaxResult equipmentFunctionTree()
    {
        return athenaBesEquipmentService.equipmentFunctionTree();
    }

    /**
     *
     * @Description: 物联设备功能结构树
     *
     * @auther: sunshangeng
     * @date: 15:50 2023/3/30
     * @param:
     * @return:
     *
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:functionTree')")
    @GetMapping("/equipmentFunctionTreeByJob")
    public AjaxResult equipmentFunctionTreeByJob()
    {
        return   athenaBesEquipmentService.equipmentFunctionTreeByJob();
    }


    /**
     *
     * @Description: 支路第三方设备树
     *
     * @auther: sunshangeng
     * @date: 15:50 2023/3/30
     * @param:
     * @return:
     *
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:functionTree')")
    @GetMapping("/getEquipmentListByBranch")
    public AjaxResult getEquipmentListByBranch(String energyCode,String parkCode)
    {
        return   athenaBesEquipmentService.getEquipmentListByBranch(parkCode,energyCode);
    }

    /**
     *
     * @Description: 物联设备结构树
     *
     * @auther: gaojikun
     * @return: AjaxResult
     *
     */
    @Log(title = "物联设备结构树", businessType = BusinessType.DELETE)
    @GetMapping("/equipmentTree")
    public AjaxResult equipmentTree()
    {
        return athenaBesEquipmentService.equipmentTree();
    }


    /**
     * @description:获取数据项
     * @author: gaojikun
     * @param: [设备id]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    @GetMapping("/getElectricParamsDatalistOther")
    public TableDataInfo getElectricParamsDatalistOther(Long id) {
        List<ProductItemData> list = athenaBesEquipmentService.getElectricParamsDatalistOther(id);
        return getDataTable(list);
    }


    /**
     *
     * @Description:场景第三方设备树
     *
     * @auther: sunshangeng
     * @date: 15:50 2023/3/30
     * @param:
     * @return:
     *
     */
    @PreAuthorize("@ss.hasPermi('baseData:equipment:functionTree')")
    @GetMapping("/getEquipmentListByScene")
    public AjaxResult getEquipmentListByScene()
    {
        return   athenaBesEquipmentService.getEquipmentListByScene();
    }

}
