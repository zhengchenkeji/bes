package com.zc.efounder.JEnterprise.domain.systemSetting;

import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * 分时电价对象
 *
 * @author liuwenge
 * @date 2023-02-20
 */
@ApiModel(value = "TimeOfUsePrice",description = "分时电价对象")
public class TimeOfUsePrice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 表头(季节) */
    @ApiModelProperty(value = "表头(季节)",required = true)
    private List<Map<String ,String>> column;
    /** 数据列表(分时电价配置)*/
    @ApiModelProperty(value = "数据列表(分时电价配置)",required = true)
    private List<Map<String ,String>> dataList;


    public List<Map<String, String>> getColumn() {
        return column;
    }

    public void setColumn(List<Map<String, String>> column) {
        this.column = column;
    }

    public List<Map<String, String>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, String>> dataList) {
        this.dataList = dataList;
    }
}
