package com.ruoyi.wxpayment.service.impl;

import com.ruoyi.wxpayment.domain.PayOrder;
import com.ruoyi.wxpayment.mapper.PayOrderMapper;
import com.ruoyi.wxpayment.service.IPayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支付_订单Service业务层处理
 * 
 * @author Athena-YangChao
 * @date 2021-10-14
 */
@Service
public class PayOrderServiceImpl implements IPayOrderService
{
    @Autowired
    private PayOrderMapper payOrderMapper;

    /**
     * 查询支付_订单
     * 
     * @param id 支付_订单主键
     * @return 支付_订单
     */
    @Override
    public PayOrder selectPayOrderById(Integer id)
    {
        return payOrderMapper.selectPayOrderById(id);
    }

    /**
     * 查询支付_订单列表
     * 
     * @param payOrder 支付_订单
     * @return 支付_订单
     */
    @Override
    public List<PayOrder> selectPayOrderList(PayOrder payOrder)
    {
        return payOrderMapper.selectPayOrderList(payOrder);
    }

    /**
     * 新增支付_订单
     * 
     * @param payOrder 支付_订单
     * @return 结果
     */
    @Override
    public int insertPayOrder(PayOrder payOrder)
    {
        return payOrderMapper.insertPayOrder(payOrder);
    }

    /**
     * 修改支付_订单
     * 
     * @param payOrder 支付_订单
     * @return 结果
     */
    @Override
    public int updatePayOrder(PayOrder payOrder)
    {
        return payOrderMapper.updatePayOrder(payOrder);
    }

    /**
     * 批量删除支付_订单
     * 
     * @param ids 需要删除的支付_订单主键
     * @return 结果
     */
    @Override
    public int deletePayOrderByIds(Integer[] ids)
    {
        return payOrderMapper.deletePayOrderByIds(ids);
    }

    /**
     * 删除支付_订单信息
     * 
     * @param id 支付_订单主键
     * @return 结果
     */
    @Override
    public int deletePayOrderById(Integer id)
    {
        return payOrderMapper.deletePayOrderById(id);
    }

    @Override
    public int updatePayOrderWithOrderNo(PayOrder payOrder) {
        return payOrderMapper.updatePayOrderWithOrderNo(payOrder);
    }
}
