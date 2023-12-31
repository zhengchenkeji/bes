package com.zc.efounder.JEnterprise.domain.electricPowerTranscription;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 电力集抄接收类
 *
 * @author liuwenge
 * @date 2022/11/28
 */
@ApiModel(value = "ElectricityStatement",description = "电力集抄接收类")
public class ElectricityStatement {


    /** 支路ids */
    @NotEmpty(message = "支路不能为空")
    @ApiModelProperty(value = "支路id集合(以逗号隔开 例如 1,2)",required = true)
    private List<Long> branchIds;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:ii")
    @NotBlank(message = "时间不能为空")
    @ApiModelProperty(value = "开始时间",required = true)
    private String dateTime;


    public List<Long> getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(List<Long> branchIds) {
        this.branchIds = branchIds;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
