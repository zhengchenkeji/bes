package com.zc.efounder.JEnterprise.service.energyInfo;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;

/**
 * 园区Service接口
 *
 * @author ruoyi
 * @date 2022-09-08
 */
public interface IParkService
{
    /**
     * 查询园区
     *
     * @param code 园区主键
     * @return 园区
     */
    public Park selectParkByCode(String code);

    /**
     * 查询园区列表
     *
     * @param park 园区
     * @return 园区集合
     */
    List<Park> selectParkList(Park park);

    /**
     * 新增园区
     *
     * @param park 园区
     * @return 结果
     */
    int insertPark(Park park);

    /**
     * 修改园区
     *
     * @param park 园区
     * @return 结果
     */
    AjaxResult updatePark(Park park);

    /**
     * 批量删除园区
     *
     * @param codes 需要删除的园区主键集合
     * @return 结果
     */
    AjaxResult deleteParkByCodes(String[] codes);

}
