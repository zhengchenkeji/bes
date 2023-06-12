package com.zc.efounder.JEnterprise.domain.commhandler;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 电表数据保存时间
 **
 */
public class SyncTime extends BaseEntity {
    /**
     * 标识符
     */
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}