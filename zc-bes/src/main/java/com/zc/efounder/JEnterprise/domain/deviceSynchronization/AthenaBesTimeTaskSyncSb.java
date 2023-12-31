package com.zc.efounder.JEnterprise.domain.deviceSynchronization;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 定时同步设备对象 athena_bes_time_task_sync_sb
 *
 * @author ruoyi
 * @date 2022-11-01
 */
public class AthenaBesTimeTaskSyncSb extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 同步任务id */
    @Excel(name = "同步任务id")
    private String syncId;

    /** 点位id */
    @Excel(name = "点位id")
    private String pointId;

    /** 点位名称 */
    @Excel(name = "点位名称")
    private String pointName;

    /** 点位类型 */
    @Excel(name = "点位类型")
    private String pointType;

    /** 点位名称 名称+别名 */
    @Excel(name = "点位名称 名称+别名")
    private String pointAllName;

    /** 点位父级名称 */
    @Excel(name = "点位父级名称")
    private String pointPsysName;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setSyncId(String syncId)
    {
        this.syncId = syncId;
    }

    public String getSyncId()
    {
        return syncId;
    }
    public void setPointId(String pointId)
    {
        this.pointId = pointId;
    }

    public String getPointId()
    {
        return pointId;
    }
    public void setPointName(String pointName)
    {
        this.pointName = pointName;
    }

    public String getPointName()
    {
        return pointName;
    }
    public void setPointType(String pointType)
    {
        this.pointType = pointType;
    }

    public String getPointType()
    {
        return pointType;
    }
    public void setPointAllName(String pointAllName)
    {
        this.pointAllName = pointAllName;
    }

    public String getPointAllName()
    {
        return pointAllName;
    }
    public void setPointPsysName(String pointPsysName)
    {
        this.pointPsysName = pointPsysName;
    }

    public String getPointPsysName()
    {
        return pointPsysName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("syncId", getSyncId())
            .append("pointId", getPointId())
            .append("pointName", getPointName())
            .append("pointType", getPointType())
            .append("pointAllName", getPointAllName())
            .append("pointPsysName", getPointPsysName())
            .toString();
    }
}
