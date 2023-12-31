package com.zc.efounder.JEnterprise.controller.electricPowerTranscription;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ExtremeValueReport;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ExtremeValueReportParam;
import com.zc.efounder.JEnterprise.service.electricPowerTranscription.ExtremeValueReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Author:gaojikun
 * @Date:2022-11-28 8:59
 * @Description:极值报表
 */
@RestController
@RequestMapping("/electricPowerTranscription/extremeValueReport")
@Api(value = "ExtremeValueReportController", tags = {"极值报表"})
@ApiSupport(order = 42,author = "gaojikun")
public class ExtremeValueReportController extends BaseController {
    @Autowired
    private ExtremeValueReportService extremeValueReportService;

    /**
     * 查询极值报表
     */
    @PostMapping("/queryMaxChartsData")
    @ApiOperation(value = "查询极值报表")
    public AjaxResult queryDayChartsData(@RequestBody  ExtremeValueReport extremeValueReport) {
        return extremeValueReportService.queryDayChartsData(extremeValueReport);
    }

    /*导出极值报表*/
    @PostMapping("/exportPowerTable")
    @ResponseBody
    @ApiOperation(value = "导出极值报表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableColumnexport",value = "多条采集参数标题",required = true,paramType = "query"),
            @ApiImplicitParam(name = "paramsId",value = "采集参数编号",required = true,paramType = "query"),
            @ApiImplicitParam(name = "dataType",value = "日月 1:日 2:月",required = true,paramType = "query"),
            @ApiImplicitParam(name = "monDate",value = "月份",required = true,paramType = "query"),
            @ApiImplicitParam(name = "dayDate",value = "日期",required = true,paramType = "query"),
            @ApiImplicitParam(name = "branchId",value = "支路ID",required = true,paramType = "query"),
            @ApiImplicitParam(name = "paramsIdStr",value = "多条采集参数",required = true,paramType = "query"),
    })
    public void exportPowerTable( @ApiIgnore @RequestBody ExtremeValueReport extremeValueReport, HttpServletResponse response) {
        String fileName = "极值报表.xls"; // 设置要导出的文件的名字
        // 获取数据集合
        String[] titleList = extremeValueReport.getTableColumnexport().split("-");
        int i = 0;
        List<Map<String, Object>> dataList = extremeValueReportService.queryDayChartsDataList(extremeValueReport);
        List<Map<String, Object>> returnList = new ArrayList<>();
        //排序
        List<ExtremeValueReportParam> paramsList = extremeValueReport.getParamsIdStr();
        String[] paramsSortTwo = {"maxInfo", "maxTime", "minInfo", "minTime", "avgInfo"};
        for (Map<String, Object> map : dataList) {
            Map<String, Object> addMap = new LinkedHashMap<>();
            //先添加名称
            addMap.put("name", map.get("name").toString());
            //再顺序添加采集数据
            for (ExtremeValueReportParam paramsMap : paramsList) {
                String paramsId = paramsMap.getId();
                for (String paramsTwo : paramsSortTwo) {
                    for (String key : map.keySet()) {
                        if ("name".equals(key) || "code".equals(key)) {
                            continue;
                        }
                        //根据顺序添加
                        if (paramsId.equals(key.substring(7,key.length()-1)) && paramsTwo.equals(key.substring(0, 7))) {
                            addMap.put(key, map.get(key));
                            continue;
                        }
                    }
                }
            }
            returnList.add(addMap);
        }
        //通用导出
        exportCommon(returnList, titleList, fileName, response);
    }


    /*多级表头导出非通用*/
    //dataList:组装后的数据
    //titleList:标题行名称
    //fileName:excel名称
    public void exportCommon(List<Map<String, Object>> dataList, String[] oneHeahTitleList, String fileName, HttpServletResponse response) {
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 创建sheet
        XSSFSheet sheet = workbook.createSheet(fileName);

        /** 第三步，设置样式以及字体样式*/
        XSSFCellStyle style = defaultHeadStyle(workbook);
        XSSFCellStyle headerStyle = createHeadCellStyle(workbook);
        XSSFCellStyle contentStyle = createContentCellStyle(workbook);
        /** 第四步，创建标题 ,合并标题单元格 */
        // 行号
        int rowNum = 0;


        // 创建第一页的第一行，索引从0开始
        XSSFRow row0 = sheet.createRow(rowNum++);
        row0.setHeight((short) 600);// 设置行高

        List<String> addMergedRegionList = new ArrayList<>();
        for (String str : oneHeahTitleList) {
            if ("".equals(str) || "序号".equals(str) || "电表名称".equals(str)) {
                continue;
            } else {
                addMergedRegionList.add(str);
            }

        }

        List<String> oneHeahTitle = new ArrayList<>();
        oneHeahTitle.add("序号");
        oneHeahTitle.add("电表名称");
        for (String oneHeahStr : addMergedRegionList) {
            oneHeahTitle.add(oneHeahStr);
            oneHeahTitle.add(oneHeahStr);
            oneHeahTitle.add(oneHeahStr);
            oneHeahTitle.add(oneHeahStr);
            oneHeahTitle.add(oneHeahStr);
        }
        //存储最大列宽
        Map<Integer, Integer> maxWidth = new HashMap<Integer, Integer>();
        for (int i = 0; i < oneHeahTitle.size(); i++) {
            XSSFCell c00 = row0.createCell(i);
            c00.setCellValue(oneHeahTitle.get(i));
            maxWidth.put(i, c00.getStringCellValue().getBytes().length * 256 + 200);
            c00.setCellStyle(style);
        }

        // 合并单元格，参数依次为起始列，结束列，起始行，结束行 （索引0开始）
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 1, 1));
        for (int i = 0; i < addMergedRegionList.size(); i++) {
            int fristCol = 0;
            int fristColTwo = i * 5 + 2;
            int lastCol = (i + 1) * 5 + 1;
            if (i == 0) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, fristCol + 2, lastCol));
            } else {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, fristColTwo, lastCol));
            }
        }

        //第二行
        XSSFRow row2 = sheet.createRow(rowNum++);
        row2.setHeight((short) 600);
        List<String> twoHeahTitle = new ArrayList<>();
        twoHeahTitle.add("序号");
        twoHeahTitle.add("电表名称");
        for (String oneHeahStr : addMergedRegionList) {
            twoHeahTitle.add("最大值");
            twoHeahTitle.add("最大值");
            twoHeahTitle.add("最小值");
            twoHeahTitle.add("最小值");
            twoHeahTitle.add("平均值");

        }
        for (int i = 0; i < twoHeahTitle.size(); i++) {
            XSSFCell tempCell = row2.createCell(i);
            tempCell.setCellValue(twoHeahTitle.get(i));
            maxWidth.put(i, tempCell.getStringCellValue().getBytes().length * 256 + 200);
            tempCell.setCellStyle(style);
        }

        // 合并单元格，参数依次为起始列，结束列，起始行，结束行 （索引0开始）
        for (int i = 0; i < addMergedRegionList.size(); i++) {
            int maxFristCol = 2;
            int maxLastCol = 3;
            int minFristCol = 4;
            int minLastCol = 5;
            int avgFristCol = 6;
            int avgLastCol = 6;
            if (i == 0) {
                sheet.addMergedRegion(new CellRangeAddress(1, 1, maxFristCol, maxLastCol));
                sheet.addMergedRegion(new CellRangeAddress(1, 1, minFristCol, minLastCol));
                sheet.addMergedRegion(new CellRangeAddress(1, 2, avgFristCol, avgLastCol));
            } else {
                sheet.addMergedRegion(new CellRangeAddress(1, 1, maxFristCol + 5 * i, maxLastCol + 5 * i));
                sheet.addMergedRegion(new CellRangeAddress(1, 1, minFristCol + 5 * i, minLastCol + 5 * i));
                sheet.addMergedRegion(new CellRangeAddress(1, 2, avgFristCol + 5 * i, avgLastCol + 5 * i));
            }
        }

        //第三行
        XSSFRow row3 = sheet.createRow(rowNum++);
        row3.setHeight((short) 700);
        List<String> threeHeahTitle = new ArrayList<>();
        threeHeahTitle.add("序号");
        threeHeahTitle.add("电表名称");
        for (String oneHeahStr : addMergedRegionList) {
            threeHeahTitle.add("数值");
            threeHeahTitle.add("时间");
            threeHeahTitle.add("数值");
            threeHeahTitle.add("时间");
            threeHeahTitle.add("平均值");
        }
        for (int i = 0; i < threeHeahTitle.size(); i++) {
            XSSFCell tempCell = row3.createCell(i);
            tempCell.setCellValue(threeHeahTitle.get(i));
            maxWidth.put(i, tempCell.getStringCellValue().getBytes().length * 256 + 200);
            tempCell.setCellStyle(style);
        }


        //填充数据
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> valueMap = dataList.get(i);
            XSSFRow tempRow = sheet.createRow(rowNum++);
            Set set = valueMap.keySet();
            //序号
            tempRow.createCell(0).setCellValue(i + 1);
            int cellNum = 1;
            for (Object key : set) {
                String keyStr = valueMap.get(key).toString();
                //组装的字段填充
                tempRow.createCell(cellNum).setCellValue(keyStr);
                cellNum++;
            }
        }


        // 列宽自适应
        for (int i = 0; i < oneHeahTitleList.length; i++) {
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

    /**
     * 创建标题样式
     *
     * @param wb
     * @return
     */
    private static XSSFCellStyle defaultHeadStyle(XSSFWorkbook wb) {
        // 生成一个样式
        XSSFCellStyle style = wb.createCellStyle();
        // 设置这些样式
        style.setWrapText(true);// 设置自动换行
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());//设置背景色
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//设置背景色
        style.setAlignment(HorizontalAlignment.CENTER);//居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//居中
        style.setBorderBottom(BorderStyle.THIN);//边框
        style.setBorderLeft(BorderStyle.THIN);//边框
        style.setBorderRight(BorderStyle.THIN);//边框
        style.setBorderTop(BorderStyle.THIN);//边框
        //生成一个字体样式
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 11);//字体大小
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());//字体颜色
        font.setFontName("Arial");
        font.setBold(true);//加粗
        // 把字体 应用到当前样式
        style.setFont(font);
        return style;
    }

    /**
     * 创建表头样式
     *
     * @param wb
     * @return
     */
    private static XSSFCellStyle createHeadCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);// 设置自动换行
        cellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());//背景颜色
        cellStyle.setAlignment(HorizontalAlignment.CENTER); //水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); //垂直对齐
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框

        XSSFFont headerFont = (XSSFFont) wb.createFont(); // 创建字体样式
        headerFont.setBold(true); //字体加粗
        headerFont.setFontName("黑体"); // 设置字体类型
        headerFont.setFontHeightInPoints((short) 12); // 设置字体大小
        cellStyle.setFont(headerFont); // 为标题样式设置字体样式

        return cellStyle;
    }

    /**
     * 创建内容样式
     *
     * @param wb
     * @return
     */
    private static XSSFCellStyle createContentCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);// 水平居中
        cellStyle.setWrapText(true);// 设置自动换行
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN); //左边框
        cellStyle.setBorderRight(BorderStyle.THIN); //右边框
        cellStyle.setBorderTop(BorderStyle.THIN); //上边框

        // 生成12号字体
        XSSFFont font = wb.createFont();
        font.setColor((short) 8);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        return cellStyle;
    }


}
