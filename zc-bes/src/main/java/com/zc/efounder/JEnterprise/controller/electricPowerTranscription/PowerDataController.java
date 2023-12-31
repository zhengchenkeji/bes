package com.zc.efounder.JEnterprise.controller.electricPowerTranscription;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.PowerData;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyConfig;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.efounder.JEnterprise.service.electricPowerTranscription.PowerDataService;
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
 * @Description:电力数据
 */
@RestController
@RequestMapping("/electricPowerTranscription/powerData")
@Api(value = "PowerDataController", tags = {"电力数据"})
@ApiSupport(order = 40,author = "gaojikun")
public class PowerDataController extends BaseController {
    @Autowired
    private PowerDataService powerDataService;

    /**
     * 查询所有园区
     */
    @GetMapping("/getAllPark")
    @ApiOperation(value = "查询所有园区")
    public List<Park> getAllPark() {
        return powerDataService.getAllPark();
    }

    /**
     * 查询所有能源类型
     */
    @GetMapping("/getEnergyType")
    @ApiOperation(value = "查询所有能源类型")
    @ApiOperationSupport(includeParameters = "parkCode")
    public List<EnergyType> getEnergyType(EnergyConfig energyConfig) {
        return powerDataService.getEnergyType(energyConfig);
    }

    /**
     * 查询支路下电表/设备
     */
    @GetMapping("/getCheckMeterList")
    @ApiOperation(value = "查询电表/设备")
    @ApiOperationSupport(includeParameters = {"branchId"})
    public AjaxResult getCheckMeterList(PowerData powerData) {
        return powerDataService.getCheckMeterList(powerData);
    }

    /**
     * 查询支路下电表/设备下绑定采集参数/数据项
     */
    @GetMapping("/getCheckMeterParamsList")
    @ApiOperation(value = "查询电表/设备下绑定采集参数/数据项")
    @ApiOperationSupport(includeParameters = {"meterType"})
    public AjaxResult getCheckMeterParamsList(PowerData powerData) {
        return powerDataService.getMeterParamsList(powerData);
    }

    /**
     * 查询所有采集参数
     */
    @GetMapping("/getAllCheckMeterParamsList")
    @ApiOperation(value = "查询所有采集参数")
    public AjaxResult getAllCheckMeterParamsList(@ApiIgnore PowerData powerData) {
        return powerDataService.getAllCheckMeterParamsList(powerData);
    }

    /**
     * 查询采集参数下采集参数分项
     */
    @GetMapping("/getMeterParamsList")
    @ApiOperation(value = "查询采集参数下采集参数分项")
    @ApiOperationSupport(includeParameters = {"meterType","paramsId"})
    public AjaxResult getMeterParamsList(PowerData powerData) {
        return powerDataService.getMeterParamsList(powerData);
    }

    /**
     * 查询采集参数下采集参数分项
     */
    @GetMapping("/getMeterParamsConfigList")
    @ApiOperation(value = "查询采集参数下采集参数分项")
    @ApiOperationSupport(includeParameters = {"meterType","paramsId"})
    public AjaxResult getMeterParamsConfigList(PowerData powerData) {
        return powerDataService.getMeterParamsConfigList(powerData);
    }

    /**
     * 查询支路下电表采集参数
     */
    @GetMapping("/getMeterParams")
    @ApiOperation(value = "查询支路下电表采集参数")
    @ApiOperationSupport(includeParameters = "meterId")
    public AjaxResult getMeterParams(PowerData powerData) {
        return powerDataService.getMeterParams(powerData);
    }

    /**
     * 查询日原始数据
     */
    @GetMapping("/queryDayChartsData")
    @ApiOperation(value = "查询日原始数据Charts")
    @ApiOperationSupport(ignoreParameters = {"paramsItemId","meterType"})
    public AjaxResult queryDayChartsData(PowerData powerData) {
        return powerDataService.queryDayChartsData(powerData);
    }

    @GetMapping("/queryDayTableData")
    @ApiOperation(value = "查询日原始数据Table")
    @ApiOperationSupport(ignoreParameters = {"paramsItemId","meterType"})
    public AjaxResult queryDayTableData(PowerData powerData) {
        return powerDataService.queryDayTableData(powerData);
    }
    /**
     * 查询逐日极值数据
     */
    @GetMapping("/queryMaxChartsData")
    @ApiOperation(value = "查询逐日极值数据Charts")
    @ApiOperationSupport(ignoreParameters = {"meterType","paramsIdStr"})
    public AjaxResult queryMaxChartsData(PowerData powerData) {
        return powerDataService.queryMaxChartsData(powerData);
    }

    @GetMapping("/queryMaxTableData")
    @ApiOperation(value = "查询逐日极值数据Table")
    @ApiOperationSupport(ignoreParameters = {"meterType","paramsIdStr"})
    public AjaxResult queryMaxTableData(PowerData powerData) {
        return powerDataService.queryMaxTableData(powerData);
    }

    /*导出日原始数据*/
    @GetMapping("/exportPowerTable")
    @ApiOperation(value = "导出日原始数据")
    @ResponseBody
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysName",value = "电表系统名称",required = true),
            @ApiImplicitParam(name = "paramsId",value = "参数编号",required = true),
            @ApiImplicitParam(name = "startTime",value = "开始日期",required = true),
            @ApiImplicitParam(name = "endTime",value = "结束日期",required = true),
            @ApiImplicitParam(name = "paramsIdStr",value = "参数分项编号",required = true),
    })
    public void exportPowerTable(@RequestParam @ApiIgnore Map<String, Object> params, HttpServletResponse response) {
        String fileName = "日原始数据.xls"; // 设置要导出的文件的名字
        // 获取数据集合
        String[] titleList = params.get("tableColumnexport").toString().split("-");
        PowerData powerData = new PowerData();
        powerData.setSysName(params.get("sysName").toString());
        powerData.setParamsId(params.get("paramsId").toString());
        powerData.setStartTime(params.get("startTime").toString());
        powerData.setEndTime(params.get("endTime").toString());
        powerData.setParamsIdStr(params.get("paramsIdStr").toString());
        powerData.setMeterType(params.get("meterType").toString());
        //查询数组
        List<Map<String, Object>> dataList = powerDataService.queryDayExport(powerData);
        //通用导出
        exportCommon(dataList, titleList, fileName, response);
    }

    /*导出逐日极值数据*/
    @GetMapping("/exportMaxPowerTable")
    @ResponseBody
    @ApiOperation(value = "导出逐日极值数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysName",value = "电表系统名称",required = true),
            @ApiImplicitParam(name = "paramsId",value = "参数编号",required = true),
            @ApiImplicitParam(name = "startTime",value = "开始日期",required = true),
            @ApiImplicitParam(name = "endTime",value = "结束日期",required = true),
            @ApiImplicitParam(name = "paramsItemId",value = "参数分项编号",required = true),
    })
    public void exportMaxPowerTable(@RequestParam @ApiIgnore Map<String, Object> params, HttpServletResponse response) {
        String fileName = "逐日极值数据.xls"; // 设置要导出的文件的名字
        // 获取数据集合
        String[] titleList = params.get("tableColumnexport").toString().split("-");
        PowerData powerData = new PowerData();
        powerData.setSysName(params.get("sysName").toString());
        powerData.setParamsId(params.get("paramsId").toString());
        powerData.setStartTime(params.get("startTime").toString());
        powerData.setEndTime(params.get("endTime").toString());
        powerData.setParamsItemId(params.get("paramsItemId").toString());
        List<Map<String, Object>> dataList = powerDataService.queryMaxExport(powerData);

        //通用导出
        exportMax(dataList, titleList, fileName, response);
    }

    /*导出通用*/
    //dataList:组装后的数据
    //titleList:标题行名称
    //fileName:excel名称
    public void exportCommon(List<Map<String, Object>> dataList, String[] titleList, String fileName, HttpServletResponse response) {
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
        for (int i = 0; i < titleList.length; i++) {
            XSSFCell cell0 = row.createCell(i);
            row.createCell(i).setCellValue(titleList[i]);
            maxWidth.put(i, cell0.getStringCellValue().getBytes().length * 256 + 200);
            cell0.setCellStyle(style);
        }
        //填充数据
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> valueMap = dataList.get(i);
            row = sheet.createRow(i + 1); // 从第2行开始填充数据
            Set set = valueMap.keySet();
            //序号
            row.createCell(0).setCellValue(i + 1);
            int cellNum = 1;
            for (Object key : set) {
                String keyStr = valueMap.get(key).toString();
                //组装的字段填充
                row.createCell(cellNum).setCellValue(keyStr);
                cellNum++;
            }
        }

        // 列宽自适应
        for (int i = 0; i < titleList.length; i++) {
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

    /*多级表头导出非通用*/
    //dataList:组装后的数据
    //titleList:标题行名称
    //fileName:excel名称
    public void exportMax(List<Map<String, Object>> dataList, String[] oneHeahTitleList, String fileName, HttpServletResponse response) {
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 创建sheet
        XSSFSheet sheet = workbook.createSheet(fileName);

        /** 第三步，设置样式以及字体样式*/
        XSSFCellStyle style = defaultHeadStyle(workbook);
        /** 第四步，创建标题 ,合并标题单元格 */
        // 行号
        int rowNum = 0;


        // 创建第一页的第一行，索引从0开始
        XSSFRow row0 = sheet.createRow(rowNum++);
        row0.setHeight((short) 600);// 设置行高

        List<String> addMergedRegionList = new ArrayList<>();
        for (String str : oneHeahTitleList) {
            if ("".equals(str) || "序号".equals(str) || "采集时间".equals(str)) {
                continue;
            } else {
                addMergedRegionList.add(str);
            }

        }

        List<String> oneHeahTitle = new ArrayList<>();
        oneHeahTitle.add("序号");
        oneHeahTitle.add("采集时间");
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
        twoHeahTitle.add("采集时间");
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
        threeHeahTitle.add("采集时间");
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

}
