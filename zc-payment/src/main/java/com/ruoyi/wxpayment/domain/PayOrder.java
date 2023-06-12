package com.ruoyi.wxpayment.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 支付_订单对象 pay_order
 * 
 * @author Athena-YangChao
 * @date 2021-10-14
 */
public class PayOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 商品名称 */
    @Excel(name = "商品名称")
    private String body;

    /** 支付方式 */
    @Excel(name = "支付方式")
    private String type;

    /** 金额 */
    @Excel(name = "金额")
    private String money;

    /** 0：未支付、1：已支付 */
    @Excel(name = "0：未支付、1：已支付")
    private Integer status;

    /** 支付单号 */
    @Excel(name = "支付单号")
    private String payNo;

    /** 支付时间 */
    @Excel(name = "支付时间")
    private String payTime;

    /** 插入时间 */
    @Excel(name = "插入时间", width = 30)
    private Date addTime;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setOrderNo(String orderNo) 
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo() 
    {
        return orderNo;
    }
    public void setBody(String body) 
    {
        this.body = body;
    }

    public String getBody() 
    {
        return body;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setMoney(String money) 
    {
        this.money = money;
    }

    public String getMoney() 
    {
        return money;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setPayNo(String payNo) 
    {
        this.payNo = payNo;
    }

    public String getPayNo() 
    {
        return payNo;
    }
    public void setPayTime(String payTime) 
    {
        this.payTime = payTime;
    }

    public String getPayTime() 
    {
        return payTime;
    }
    public void setAddTime(Date addTime) 
    {
        this.addTime = addTime;
    }

    public Date getAddTime() 
    {
        return addTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderNo", getOrderNo())
            .append("body", getBody())
            .append("type", getType())
            .append("money", getMoney())
            .append("status", getStatus())
            .append("payNo", getPayNo())
            .append("payTime", getPayTime())
            .append("addTime", getAddTime())
            .toString();
    }
}
