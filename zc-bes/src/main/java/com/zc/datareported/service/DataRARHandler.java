package com.zc.datareported.service;

import com.zc.datareported.mapper.DataUploadRecordMapper;
import com.zc.datareported.domain.DataUploadRecord;
import com.zc.efounder.JEnterprise.domain.energyDataReport.DataCenterBaseInfo;
import com.zc.common.framework.xmlprocessor.utils.CompressUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiepufeng
 * @date 2020/11/12 9:31
 */
@Service
public class DataRARHandler
{
    /**
     * 文件存放位置
     */
    @Value("${data-centre.file-storage-location}")
    private String fileStorageLocation;

    @Resource
    private DataUploadRecordMapper dataUploadRecordMapper;

    /**
     * 获取文件名称
     * @param date
     * @param nodeCode
     * @return
     */
    public static String getRarName(Date date, String nodeCode)
    {
        return  nodeCode + new SimpleDateFormat("YYYYMMddHH").format(date);
    }

    /**
     * 文件打包压缩
     * @return
     */
    public boolean compression(DataCenterBaseInfo besDatecenterType, Date date)
    {

        if (besDatecenterType == null || date == null)
        {
            return false;
        }

        String nodeCode = besDatecenterType.getDataCenterId();

        String rarName = DataRARHandler.getRarName(date, nodeCode);

        String buildfileName = fileStorageLocation + "/" + rarName + "Build.xml";
        String energyfileName = fileStorageLocation+ "/" + rarName + "Energy.xml";

        List<String> sourcePathList = new ArrayList<>();

        sourcePathList.add(buildfileName);
        sourcePathList.add(energyfileName);

        try
        {
            CompressUtil.zip(sourcePathList, fileStorageLocation, rarName);
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        DataUploadRecord dataUploadRecord = new DataUploadRecord();

        dataUploadRecord.setCreateTime(date);
        dataUploadRecord.setFileName(rarName);
        dataUploadRecord.setFilePath(fileStorageLocation);

        dataUploadRecordMapper.insertDataUploadRecord(dataUploadRecord);

        return true;
    }

}
