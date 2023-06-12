package com.ruoyi.wxpayment.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.wxpayment.domain.PayOrder;
import com.ruoyi.wxpayment.service.IPayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 支付_订单Controller
 *
 * @author Athena-YangChao
 * @date 2021-10-14
 */
@RestController
@RequestMapping("/payment/order")
public class PayOrderController extends BaseController {
    @Autowired
    private IPayOrderService payOrderService;

    /**
     * 查询支付_订单列表
     */
    @PreAuthorize("@ss.hasPermi('payment:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(PayOrder payOrder) {
        startPage();
        List<PayOrder> list = payOrderService.selectPayOrderList(payOrder);
        return getDataTable(list);
    }

    /**
     * 导出支付_订单列表
     */
    @PreAuthorize("@ss.hasPermi('payment:order:export')")
    @Log(title = "支付_订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PayOrder payOrder) {
        List<PayOrder> list = payOrderService.selectPayOrderList(payOrder);
        ExcelUtil<PayOrder> util = new ExcelUtil<PayOrder>(PayOrder.class);
        util.exportExcel(response, list, "支付_订单数据");
    }

    /**
     * 获取支付_订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('payment:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id) {
        return AjaxResult.success(payOrderService.selectPayOrderById(id));
    }

    /**
     * 新增支付_订单
     */
    @PreAuthorize("@ss.hasPermi('payment:order:add')")
    @Log(title = "支付_订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PayOrder payOrder) {
        return toAjax(payOrderService.insertPayOrder(payOrder));
    }

    /**
     * 修改支付_订单
     */
    @PreAuthorize("@ss.hasPermi('payment:order:edit')")
    @Log(title = "支付_订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PayOrder payOrder) {
        return toAjax(payOrderService.updatePayOrder(payOrder));
    }

    /**
     * 删除支付_订单
     */
    @PreAuthorize("@ss.hasPermi('payment:order:remove')")
    @Log(title = "支付_订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids) {
        return toAjax(payOrderService.deletePayOrderByIds(ids));
    }

}
