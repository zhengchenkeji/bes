package com.zc.websocket.bo;


import com.zc.websocket.enums.ChannelStatus;

/**
 * Channel对应的属性数据定义
 *
 * @author Athena-xiepufeng
 *
 */
public class ChannelProperty
{
    /*
     * Channel 绑定的用户tokenSN
     */
    private String tokenSN;

    /*
     * channel状态
     */
    private ChannelStatus status;

    /*
     * channel当前状态保持周期计数器
     */
    private int statusCount;

    /*
     * 心跳超时计数器（注意：该变量只能在一个线程中访问）
     */
    private int count = 0;

    /*
     * 超时计数器归零标志
     */
    private volatile boolean resetCount = false;

    /**
     * 通知定时任务线程重置count标志(支持高并发)。
     */
    public void heartbeatRefresh()
    {
        if (!resetCount)
        {
            resetCount = true;
        }
    }

    public String getTokenSN()
    {
        return tokenSN;
    }

    public void setTokenSN(String tokenSN)
    {
        this.tokenSN = tokenSN;
    }

    public final ChannelStatus getStatus()
    {
        return status;
    }

    public final void setStatus(ChannelStatus status)
    {
        this.status = status;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public boolean isResetCount()
    {
        return resetCount;
    }

    public void setResetCount(boolean resetCount)
    {
        this.resetCount = resetCount;
    }

    public final int getStatusCount()
    {
        return statusCount;
    }

    public final void setStatusCount(int statusCount)
    {
        this.statusCount = statusCount;
    }
}
