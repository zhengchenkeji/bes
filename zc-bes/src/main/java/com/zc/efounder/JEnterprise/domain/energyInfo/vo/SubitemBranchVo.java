package com.zc.efounder.JEnterprise.domain.energyInfo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * 分项支路VO
 *
 * @author qindehua
 * @date 2023/01/29
 */
@ApiModel(value = "SubitemBranchVo", description = "分项支路VO")
public class SubitemBranchVo {

    private static final long serialVersionUID = 1L;

    /**
     * 操作之后 该节点的支路ID集合
     */
    @ApiModelProperty(value = "操作之后 该节点的支路ID集合")
    private List<Long> branchList=new ArrayList<>();
    /**
     * 分项当前节点
     */
    @NotBlank(message = "分项当前节点不允许为空！")
    @ApiModelProperty(value = "分项当前节点",required = true)
    private String subitemId;

    /**
     * 分项当前父节点
     */
    @NotBlank(message = "分项当前父节点不允许为空！")
    @ApiModelProperty(value = "分项当前父节点",required = true)
    private String fatherId;

    /**
     * 所属园区
     */
    @NotBlank(message = "所属园区不允许为空！")
    @ApiModelProperty(value = "所属园区",required = true)
    private String parkCode;

    /**
     * 所属能源
     */
    @NotBlank(message = "所属能源不允许为空！")
    @ApiModelProperty(value = "所属能源",required = true)
    private String energyCode;

    public List<Long> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Long> branchList) {
        this.branchList = branchList;
    }

    public String getSubitemId() {
        return subitemId;
    }

    public void setSubitemId(String subitemId) {
        this.subitemId = subitemId;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public String getEnergyCode() {
        return energyCode;
    }

    public void setEnergyCode(String energyCode) {
        this.energyCode = energyCode;
    }
}
