package com.ruoyi.wxpayment.service;

import com.ruoyi.wxpayment.domain.PayOrder;

import java.util.List;

/**
 * 支付_订单Service接口
 * 
 * @author Athena-YangChao
 * @date 2021-10-14
 */
public interface IPayOrderService 
{
    /**
     * 查询支付_订单
     * 
     * @param id 支付_订单主键
     * @return 支付_订单
     */
    public PayOrder selectPayOrderById(Integer id);

    /**
     * 查询支付_订单列表
     * 
     * @param payOrder 支付_订单
     * @return 支付_订单集合
     */
    public List<PayOrder> selectPayOrderList(PayOrder payOrder);

    /**
     * 新增支付_订单
     * 
     * @param payOrder 支付_订单
     * @return 结果
     */
    public int insertPayOrder(PayOrder payOrder);

    /**
     * 修改支付_订单
     * 
     * @param payOrder 支付_订单
     * @return 结果
     */
    public int updatePayOrder(PayOrder payOrder);

    /**
     * 批量删除支付_订单
     * 
     * @param ids 需要删除的支付_订单主键集合
     * @return 结果
     */
    public int deletePayOrderByIds(Integer[] ids);

    /**
     * 删除支付_订单信息
     * 
     * @param id 支付_订单主键
     * @return 结果
     */
    public int deletePayOrderById(Integer id);

    /**
     * 根据订单号修改 订单支付状态 以及 支付时间
     * */
    int updatePayOrderWithOrderNo(PayOrder payOrder);
}
