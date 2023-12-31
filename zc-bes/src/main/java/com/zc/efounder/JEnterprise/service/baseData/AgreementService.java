package com.zc.efounder.JEnterprise.service.baseData;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.baseData.Agreement;

import java.util.List;

/**
 * 设备协议Service接口
 *
 * @author sunshangeng
 * @date 2023-03-14
 */
public interface AgreementService
{
    /**
     * 查询设备协议
     *
     * @param id 设备协议主键
     * @return 设备协议
     */
    public Agreement selectAgreementById(Long id);

    /**
     * 查询设备协议列表
     *
     * @param agreement 设备协议
     * @return 设备协议集合
     */
    List<Agreement> selectAgreementList(Agreement agreement);

    /**
     * 新增设备协议
     *
     * @param agreement 设备协议
     * @return 结果
     */
    AjaxResult insertAgreement(Agreement agreement);

    /**
     * 修改设备协议
     *
     * @param agreement 设备协议
     * @return 结果
     */
    AjaxResult updateAgreement(Agreement agreement);

    /**
     * 批量删除设备协议
     *
     * @param ids 需要删除的设备协议主键集合
     * @return 结果
     */
    AjaxResult deleteAgreementByIds(Long[] ids,Integer check);

    /**
     * 删除设备协议信息
     *
     * @param id 设备协议主键
     * @return 结果
     */
    AjaxResult deleteAgreementById(Long id);
}
