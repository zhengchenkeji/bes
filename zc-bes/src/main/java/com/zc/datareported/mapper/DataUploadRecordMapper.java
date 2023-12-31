package com.zc.datareported.mapper;


import com.zc.datareported.domain.DataUploadRecord;

import java.util.List;

/**
 * 节能办能耗上传记录Mapper接口
 *
 * @author qindehua
 * @date 2022-11-11
 */
public interface DataUploadRecordMapper
{
    /**
     * 查询节能办能耗上传记录
     *
     * @param id 节能办能耗上传记录主键
     * @return 节能办能耗上传记录
     */
    public DataUploadRecord selectDataUploadRecordById(Long id);

    /**
     * 查询节能办能耗上传记录列表
     *
     * @param dataUploadRecord 节能办能耗上传记录
     * @return 节能办能耗上传记录集合
     */
    List<DataUploadRecord> selectDataUploadRecordList(DataUploadRecord dataUploadRecord);

    /**
     * 新增节能办能耗上传记录
     *
     * @param dataUploadRecord 节能办能耗上传记录
     * @return 结果
     */
    int insertDataUploadRecord(DataUploadRecord dataUploadRecord);

    /**
     * 修改节能办能耗上传记录
     *
     * @param dataUploadRecord 节能办能耗上传记录
     * @return 结果
     */
    int updateDataUploadRecord(DataUploadRecord dataUploadRecord);

    /**
     * 删除节能办能耗上传记录
     *
     * @param id 节能办能耗上传记录主键
     * @return 结果
     */
    int deleteDataUploadRecordById(Long id);

    /**
     * 批量删除节能办能耗上传记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteDataUploadRecordByIds(Long[] ids);
}
