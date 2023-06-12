package com.zc.datareported.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 节能办能耗上传记录对象 athena_bes_data_upload_record
 *
 * @author qindehua
 * @date 2022-11-11
 */
public class DataUploadRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 下载次数 */
    @Excel(name = "下载次数")
    private Long downloadCount;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String fileName;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setDownloadCount(Long downloadCount)
    {
        this.downloadCount = downloadCount;
    }

    public Long getDownloadCount()
    {
        return downloadCount;
    }
    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("downloadCount", getDownloadCount())
            .append("filePath", getFilePath())
            .append("fileName", getFileName())
            .toString();
    }
}
