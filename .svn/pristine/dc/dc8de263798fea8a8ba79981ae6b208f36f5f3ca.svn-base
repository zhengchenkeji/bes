package com.zc.efounder.JEnterprise.controller.besCommon;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.PageInfoStream;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.deviceTree.PointControlCommand;
import com.zc.efounder.JEnterprise.service.baseData.EquipmentService;
import com.zc.efounder.JEnterprise.service.baseData.ProductService;
import com.zc.efounder.JEnterprise.service.subrealtimedatamanage.SubRealTimeDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.FetchProfile;
import java.util.List;
/**
 * description:APP设备控制
 * author: sunshangeng
 * date:2023/5/19 15:13
 */
@RestController
@RequestMapping("/appDeviceController")
@Api(value = "AppDeviceController", tags = {"App设备定义"})
public class AppDeviceController  extends BaseController {
    @Resource
    private EquipmentService equipmentService;

    @Resource
    private ProductService productService;

    @Resource
    private SubRealTimeDataService subRealTimeDataService;

    /**
     * 查询所有第三方设备控制器
     */
    @GetMapping("/getControllerList")
    @ApiOperation(value = "查询所有产品")
    public PageInfoStream getControllerList()
    {
        List<Equipment> list = equipmentService.selectAthenaBesEquipmentList(new Equipment());
        PageInfoStream pageInfoStream = new PageInfoStream();
        pageInfoStream.setTotal(list.size());
        pageInfoStream.setData(list);
        return pageInfoStream;
    }

    /**
     * 根据第三方设备控制器，获取所有模块
     */
    @GetMapping("/getModuleList")
    @ApiOperation(value = "根据控制器查询模块")
    @ApiImplicitParam(name = "controllerId",value = "控制器id",required = true,dataType = "long")
    public PageInfoStream getModuleList(String controllerId)
    {
        Equipment equipment=new Equipment();
        equipment.setpId(Long.parseLong(controllerId));
        List<Equipment> list = equipmentService.selectAthenaBesEquipmentList(equipment);
        PageInfoStream pageInfoStream = new PageInfoStream();
        pageInfoStream.setTotal(list.size());
        pageInfoStream.setData(list);
        return pageInfoStream;
    }
    /**
     * 根据模块,获取点位
     */
    @GetMapping("/getItemDataList")
    @ApiOperation(value = "根据模块查询数据项")
    @ApiImplicitParam(name = "moduleId",value = "模块id",required = true,dataType = "long")
    public PageInfoStream getItemDataList(String moduleId)
    {
        List<ProductItemData> list = productService.getProductItemDataListByEqId(Long.parseLong(moduleId));
        PageInfoStream pageInfoStream = new PageInfoStream();
        pageInfoStream.setTotal(list.size());
        pageInfoStream.setData(list);
        return pageInfoStream;
    }
    /**
     * @description:
     * @author: sunshangeng
     * @date: 2023/5/25 18:03
     * @param: [设备ID, 功能ID]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @GetMapping("/debugModule")
    @ApiOperation(value = "下发功能")
    public AjaxResult debugDevice(Long deviceId,String funCode)
    {

        return  productService.debugDevice(deviceId,funCode);
    }

    @PostMapping("/debugPoint")
    @ApiOperation(value = "根据Id获取下数据项值")
    public AjaxResult getItemData(@RequestBody PointControlCommand pointControlCommand)
    {
        return productService.debugpoint(pointControlCommand);
    }

    /**
     * 客户端订阅消息格根据设备ID
     * @param event
     * @return
     */
    @RequestMapping(value = "/subscribe", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult subscribe(String event) {
        AjaxResult ajaxResult = new AjaxResult();

        ajaxResult = subRealTimeDataService.subscribeByEqId(event);

        return ajaxResult;
    }
    /**
     * 客户端取消订阅根据设备ID
     * @param event
     * @return
     */
    @RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult unsubscribe(String event) {
        AjaxResult ajaxResult = new AjaxResult();

        ajaxResult = subRealTimeDataService.unsubscribeByEqid(event);

        return ajaxResult;
    }


}
