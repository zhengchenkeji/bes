package com.zc.efounder.JEnterprise.service.excelTableImport;

import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author: wanghongjie
 * @Description:设备树表格导入/导出
 * @Date: Created in 18:49 2022/11/9
 * @Modified By:
 */
public interface DeviceTreeExcelTableImportService {

    /**
     *
     * @Description: 设备树导入管理
     *
     * @auther: wanghongjie
     * @date: 18:52 2022/11/9
     * @param: [request, file, updateSupport]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     */
    AjaxResult impExcel(HttpServletRequest request, MultipartFile file, boolean updateSupport) throws IOException;
}
