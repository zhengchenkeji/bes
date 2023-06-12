package com.zc.efounder.JEnterprise.mapper.baseData;

import com.zc.efounder.JEnterprise.domain.baseData.Agreement;

import java.util.List;

/**
 * 设备协议Mapper接口
 *
 * @author sunshangeng
 * @date 2023-03-14
 */
public interface AgreementMapper
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
    Boolean insertAgreement(Agreement agreement);

    /**
     * 修改设备协议
     *
     * @param agreement 设备协议
     * @return 结果
     */
    Boolean updateAgreement(Agreement agreement);

    /**
     * 删除设备协议
     *
     * @param id 设备协议主键
     * @return 结果
     */
    Boolean deleteAgreementById(Long id);

    /**
     * 批量删除设备协议
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAgreementByIds(Long[] ids);
}
