package com.zc.efounder.JEnterprise.domain.energyAnalysis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 能耗排名接收类
 *
 * @author qindehua
 * @date 2022/11/28
 */
@ApiModel(value = "RankingVO",description = "能耗排名接收类")
public class RankingVO {


    /** 能源节点ids */
   @NotEmpty(message = "能源节点不能为空")
   @ApiModelProperty(value = "能源节点ids",required = true)
    private List<Long> ids;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd ")
    @NotBlank(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间(yyyy-MM-dd)",required = true)
    private String startTime;

    /** 结束时间 */
    @NotBlank(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd ")
    @ApiModelProperty(value = "结束时间(yyyy-MM-dd)",required = true)
    private String endTime;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> branchIds) {
        this.ids = branchIds;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
