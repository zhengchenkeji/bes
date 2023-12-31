package com.zc.efounder.JEnterprise.controller.systemSetting;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.systemSetting.TimeOfUsePrice;
import com.zc.efounder.JEnterprise.service.systemSetting.TimeOfUsePriceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *分时电价Controller
 *
 * @author liuwenge
 * @date 2023-02-20
 */
@RestController
@RequestMapping("/systemSetting/timeOfUsePrice")
@Api(value = "TimeOfUsePriceController", tags = {"分时电价"})
@ApiSupport(order = 45,author = "liuwenge")
public class TimeOfUsePriceController extends BaseController
{
    @Autowired
    private TimeOfUsePriceService timeOfUsePriceService;

    /**
     * 查询分时电价列表
     * @author liuwenge
     */
    @PreAuthorize("@ss.hasPermi('systemSetting:timeOfUsePrice:list')")
    @ApiOperation(value = "查询分时电价列表")
    @GetMapping("/list")
    public AjaxResult list()
    {
        return timeOfUsePriceService.selectList();
    }

    /**
     * 修改分时电价列表
     * @author liuwenge
     */
    @ApiOperation(value = "修改分时电价列表")
    @PreAuthorize("@ss.hasPermi('systemSetting:timeOfUsePrice:update')")
    @PostMapping("/update")
    public AjaxResult update(@RequestBody TimeOfUsePrice timeOfUsePrice) {
        return timeOfUsePriceService.update(timeOfUsePrice);
    }

    /**
     * @Description 导出分时电价
     *
     * @author liuwenge
     * @date 2023/2/21 15:49
     * @param response
     * @return void
     */
    @ApiOperation(value = "导出分时电价")
    @PreAuthorize("@ss.hasPermi('systemSetting:timeOfUsePrice:exportTable')")
    @GetMapping("/exportTable")
    public void exportTable(HttpServletResponse response) {

        Map<String,List<Map<String,Object>>> result = timeOfUsePriceService.exportTable();

        List<Map<String,Object>> column = result.get("columnList");
        List<Map<String,Object>> dataList = result.get("dataList");

        List<String> titleList = new ArrayList<>();
        List<String> titleCodeList = new ArrayList<>();

        titleCodeList.add("time");
        titleList.add("时间");

        if (column.size() > 0){
            for (Map<String, Object> map : column) {
                titleCodeList.add(map.get("code").toString());
                titleList.add(map.get("name").toString());
            }
        }

        // 临时文件名
        String fileName = "timeOfUsePrice" + System.currentTimeMillis();
        // sheet页名称
        String FileName = "sheet";

        exportCommon(dataList,titleList,titleCodeList,fileName,response);


    }

    /*导出通用*/
    //dataList:组装后的数据
    //titleList:标题行名称
    //titleCodeList: 标题行code
    //fileName:excel名称
    public void exportCommon(List<Map<String, Object>> dataList, List<String> titleList,List<String> titleCodeList, String fileName, HttpServletResponse response) {
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 创建sheet
        XSSFSheet sheet = workbook.createSheet(fileName);
        // 生成一个样式
        CellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());//设置背景色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//设置背景色
        style.setAlignment(HorizontalAlignment.CENTER);//居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//居中
        style.setBorderBottom(BorderStyle.THIN);//边框
        style.setBorderLeft(BorderStyle.THIN);//边框
        style.setBorderRight(BorderStyle.THIN);//边框
        style.setBorderTop(BorderStyle.THIN);//边框
        //生成一个字体样式
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);//字体大小
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());//字体颜色
        font.setFontName("Arial");
        font.setBold(true);//加粗
        // 把字体 应用到当前样式
        style.setFont(font);
        //存储最大列宽
        Map<Integer, Integer> maxWidth = new HashMap<Integer, Integer>();


        // 生成标题行
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < titleList.size(); i++) {
            XSSFCell cell0 = row.createCell(i);
            row.createCell(i).setCellValue(titleList.get(i));
            maxWidth.put(i, cell0.getStringCellValue().getBytes().length * 256 + 200);
            cell0.setCellStyle(style);
        }
        //填充数据
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> valueMap = dataList.get(i);
            row = sheet.createRow(i + 1); // 从第2行开始填充数据
            int cellNum = 0;
            for (String titleCode : titleCodeList) {
                String value = valueMap.get(titleCode).toString();
                row.createCell(cellNum).setCellValue(value);
                cellNum++;
            }

        }

        // 列宽自适应
        for (int i = 0; i < titleList.size(); i++) {
            sheet.setColumnWidth(i, maxWidth.get(i));
        }

        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            response.flushBuffer();

            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            System.out.println(fileName + "导出excel出现错误————————————————————————————————————————》" + e);
        }
    }


}
