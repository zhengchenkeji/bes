package com.zc.efounder.JEnterprise.domain.inspection;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 定时巡检
 *
 * @author qindehua
 * @date 2023/02/03
 */
public class Inspection extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**点位树ID***/
    private Long pointTreeId;

    /**任务ID***/
    private Long jobId;

    /**控制器树ID***/
    private Long controllerTreeId;

    /**控制器IP地址***/
    private String ip;
    /**
    *状态值
     * 0:已下发 等待下位机回应
     * 1:下位机返回异常数据
     * 2:成功 下位机返回数据
    * */
    private Integer state;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getPointTreeId() {
        return pointTreeId;
    }

    public void setPointTreeId(Long pointTreeId) {
        this.pointTreeId = pointTreeId;
    }

    public Long getControllerTreeId() {
        return controllerTreeId;
    }

    public void setControllerTreeId(Long controllerTreeId) {
        this.controllerTreeId = controllerTreeId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Inspection() {

    }

    public Inspection(Long pointTreeId, Long jobId, Long controllerTreeId, String ip, Integer state) {
        this.pointTreeId = pointTreeId;
        this.jobId = jobId;
        this.controllerTreeId = controllerTreeId;
        this.ip = ip;
        this.state = state;
    }
}
