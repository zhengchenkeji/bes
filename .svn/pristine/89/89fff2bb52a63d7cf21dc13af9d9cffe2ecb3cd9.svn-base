package com.zc.efounder.JEnterprise.controller.excelTableImport;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.service.excelTableImport.DeviceTreeExcelTableImportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: wanghongjie
 * @Description:设备树表格导入/导出
 * @Date: Created in 18:46 2022/11/9
 * @Modified By:
 */
@RestController
@RequestMapping("/basicData/ExcelImport")
public class DeviceTreeExcelTableImportController {

    @Resource
    private DeviceTreeExcelTableImportService tableImportService;

    @PreAuthorize("@ss.hasPermi('basicData:deviceTree:import')")
    @PostMapping("/importData")
    public AjaxResult importData(HttpServletRequest request, MultipartFile file, boolean updateSupport) throws Exception
    {
        AjaxResult ajaxResult = tableImportService.impExcel(request,file,updateSupport);

        return ajaxResult;
    }
}
