package com.zc.efounder.JEnterprise.service.electricPowerTranscription.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.connect.business.dto.edc.ElectricParam;
import com.zc.efounder.JEnterprise.Cache.MeterCache;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.ExtremeValueReportParam;
import com.zc.efounder.JEnterprise.domain.electricPowerTranscription.vo.ParamVO;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricCollRlgl;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.mapper.electricPowerTranscription.ParamMapper;
import com.zc.efounder.JEnterprise.service.electricPowerTranscription.ParamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 电参数impl
 *
 * @author qindehua
 * @date 2022/11/29
 */
@Service
public class ParamServiceImpl implements ParamService {

    private static final Logger log = LoggerFactory.getLogger(ParamServiceImpl.class);

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private MeterCache meterCache;

    @Autowired
    private ParamMapper mapper;

    /**
     * 查询电表下面的电能参数
     *
     * @param id id
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/11/29
     **/
    @Override
    public AjaxResult getMeterParams(Long id) {
        //采集参数
        List<Map<String, String>> electricParams = new LinkedList<>();
        //根据id取到电表ID gaojikun
        AthenaBranchMeterLink athenaBranchMeterLink = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, id);
        id = athenaBranchMeterLink.getMeterId();

        //查询电表
        AthenaElectricMeter athenaElectricMeter = meterCache.getMeterByMeterId(id.intValue());
        //获取采集参数Ids
        List<Integer> list = new ArrayList<>();

        if ("1".equals(athenaBranchMeterLink.getDeviceType())) {
            //第三方设备
            String electricParam = athenaBranchMeterLink.getElectricParam();
            String[] strArr = electricParam.split(",");
            for (String str : strArr) {
                //获取数据项
                ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, str);
                //code name
                electricParams.add(new HashMap<String, String>() {{
                    put("code", String.valueOf(productItemData.getId()));
                    put("name", productItemData.getName());
                    put("type", "1");
                }});
            }
        } else {
            //
            //先判断当前电表是否为AI点
            if (athenaElectricMeter == null || athenaElectricMeter.getCollectionMethodCode() == null) {
                electricParams.add(new HashMap<String, String>() {{
                    put("code", PointPowerParam.Point_Meter_Code);
                    put("name", PointPowerParam.Point_Meter_Name);
                    put("type", "0");
                }});
            } else {
                //获取采集方案下的所有采集参数Id
                for (Object value : redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl).values()) {
                    ElectricCollRlgl electricCollRlgl = (ElectricCollRlgl) value;
                    if (electricCollRlgl.getCollId() == athenaElectricMeter.getCollectionMethodCode().intValue()) {
                        list.add(electricCollRlgl.getElectricId());
                    }
                }
                //循环获取采集参数
                for (Integer integer : list) {
                    ElectricParams electricParam = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, (long) integer);

                    electricParams.add(new HashMap<String, String>() {{
                        put("code", electricParam.getCode());
                        put("name", electricParam.getName());
                        put("type", "0");
                    }});
                }
            }
        }

        return AjaxResult.success(electricParams);
    }

    /**
     * 查询根据电能参数  查询参数数据
     *
     * @param paramVO
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/11/30
     **/
    @Override
    public AjaxResult getDataByParamsCode(ParamVO paramVO) {
        if (paramVO.getMeter() == null) {
            return AjaxResult.error("电表树ID不能为空！");
        }
        //根据MeterId查询电表ID
        AthenaBranchMeterLink athenaBranchMeterLink = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, paramVO.getMeter());
        paramVO.setMeter(athenaBranchMeterLink.getMeterId());
        //返回数据
        Map<String, Object> map = new HashMap<>();
        //参数List
        List<String> paramsId = paramVO.getParamsId();
        if ("0".equals(paramVO.getDeviceType())) {
            AthenaElectricMeter athenaElectricMeter = meterCache.getMeterByMeterId(paramVO.getMeter().intValue());
            map = mapper.selectDataByParamsCodeOther(paramsId, paramVO.getStartTime(), paramVO.getEndTime(), athenaElectricMeter.getSysName());
        } else {
            Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, paramVO.getMeter());
            map = mapper.selectDataByParamsCodeOtherOther(paramsId, paramVO.getStartTime(), paramVO.getEndTime(), String.valueOf(equipment.getId()));
        }

        List<Map<String, Object>> data = new LinkedList<>();

        if (map.size() > 0) {
            map.values().forEach(item -> {
                data.add((HashMap<String, Object>) item);
            });
        }

        return AjaxResult.success(sortByFeild(data, "time", "desc"));
    }

    /**
     * List<Map>根据map字段排序
     *
     * @param list
     * @param feild   排序字段
     * @param sortTyp 排序方式 desc-倒序 asc-正序
     * @return
     */
    private static List<Map<String, Object>> sortByFeild(List<Map<String, Object>> list, String feild, String sortTyp) {
        if (CollectionUtils.isNotEmpty(list)) {
            list.sort((m1, m2) -> {
                if (StringUtils.equals(sortTyp, "desc")) {
                    return DateUtils.parseDate(String.valueOf(m2.get(feild))).compareTo(DateUtils.parseDate(String.valueOf(m1.get(feild))));
                } else {
                    return DateUtils.parseDate(String.valueOf(m1.get(feild))).compareTo(DateUtils.parseDate(String.valueOf(m2.get(feild))));
                }
            });
            // 或者 Collections类里面的sort方法也是list.sort()与上面一样
            // Collections.sort(list, (m1, m2)-> String.valueOf(m1.get(feild)).compareTo(String.valueOf(m2.get(feild)))); // lamuda排序
        }

        return list;
    }

    /**
     * 导出excel
     *
     * @param response 响应
     * @param paramVO
     * @Author qindehua
     * @Date 2022/11/30
     **/
    @Override
    public void exportExcel(HttpServletResponse response, ParamVO paramVO) {
        if (paramVO.getMeter() == null) {
            log.warn("电表树ID不能为空！");
            return;
        }
        //根据支路电表ID查询电表ID
        AthenaBranchMeterLink athenaBranchMeterLink = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, paramVO.getMeter());
        paramVO.setMeter(athenaBranchMeterLink.getMeterId());

        //采集参数code 数据项ID
        List<String> lists = paramVO.getParamId();
        //系统名称 设备ID
        String sysName = "";
        //别名 设备名称
        String alias = "";

        /**获取电参数名称字典***/
        Map<String, String> dictMap = new HashMap<>();
        if ("0".equals(paramVO.getDeviceType())) {
            AthenaElectricMeter athenaElectricMeter = meterCache.getMeterByMeterId(paramVO.getMeter().intValue());
            Map<String, ElectricParams> mappParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams);
            sysName = athenaElectricMeter.getSysName();
            alias = athenaElectricMeter.getAlias();

            //将AI 点 默认参数加载进来
            dictMap.put(PointPowerParam.Point_Meter_Code, PointPowerParam.Point_Meter_Name);
            for (ElectricParams value : mappParams.values()) {
                dictMap.put(value.getCode(), value.getName());
            }
        } else {
            Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, paramVO.getMeter());
            sysName = String.valueOf(equipment.getId());
            alias = equipment.getName();
            a:
            for (String itemdataId : lists) {
                if ("time".equals(itemdataId)) {
                    continue a;
                }
                ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, Long.parseLong(itemdataId));
                dictMap.put(String.valueOf(productItemData.getId()), productItemData.getName());
            }
        }

        /**Excel标题名称***/
        Map<String, String> titleList = new LinkedHashMap();
        titleList.put("index", "序号");
        titleList.put("time", "采集时间");
        //返回数据
        Map<String, Object> map = new HashMap<>();
        /**根据code 设置Excel标题名称***/
        for (String o : lists) {
            if (StringUtils.isNotEmpty(dictMap.get(o))) {
                titleList.put(o, dictMap.get(o));
            }
        }
        //查询返回参数
        if ("0".equals(paramVO.getDeviceType())) {
            map = mapper.selectDataByParamsCodeOther(lists, paramVO.getStartTime(), paramVO.getEndTime(), sysName);
        } else {
            map = mapper.selectDataByParamsCodeOtherOther(lists, paramVO.getStartTime(), paramVO.getEndTime(), sysName);
        }

        //将数据按顺序排列
        List<Map<String, Object>> dataList = new LinkedList<>();
        map.values().forEach(item -> {
            Map<String, Object> mapLink = new LinkedHashMap<>();
            Map<String, Object> mapData = (HashMap<String, Object>) item;
            for (String s : titleList.keySet()) {
                if ("index".equals(s)) {
                    continue;
                }
                mapLink.put(s, mapData.get(s));
            }
            dataList.add(mapLink);
        });
        ArrayList<String> title = new ArrayList<>(titleList.values());
        exportCommon(sortByFeild(dataList, "time", "desc"), title, alias, "电参数数据表", response);
    }

    /**
     * 批量导出
     *
     * @param response 响应
     * @param paramVO
     * @Author qindehua
     * @Date 2022/12/01
     **/
    @Override
    public void exportExcelBatch(HttpServletResponse response, ParamVO paramVO) {
        if (CollectionUtils.isEmpty(paramVO.getMeterIds())) {
            log.warn("电表树id集合不能为空！");
            return;
        }

        List<List<Map<String, Object>>> listData = new ArrayList<>();
        List<List<String>> listTitle = new ArrayList<>();
        List<String> listSheet = new ArrayList<>();

        for (Long id : paramVO.getMeterIds()) {

            //根据支路电表ID查询电表ID
            AthenaBranchMeterLink athenaBranchMeterLink = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink, id);
            id = athenaBranchMeterLink.getMeterId();
            String deviceType = athenaBranchMeterLink.getDeviceType();
            if ("0".equals(deviceType)) {
                //bes
                /**********先获取电表对应的采集参数*************/
                //采集参数
                List<String> paramsId = new ArrayList<>();
                //查询电表
                AthenaElectricMeter athenaElectricMeter = meterCache.getMeterByMeterId(id.intValue());
                listSheet.add(athenaElectricMeter.getAlias());
                //获取采集参数Ids
                List<Integer> list = new ArrayList<>();
                //先判断当前电表是否为AI点
                if (athenaElectricMeter.getCollectionMethodCode() == null) {
                    paramsId.add(PointPowerParam.Point_Meter_Code);
                } else {
                    //获取采集方案下的所有采集参数Id
                    for (Object value : redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricCollRlgl).values()) {
                        ElectricCollRlgl electricCollRlgl = (ElectricCollRlgl) value;
                        if (electricCollRlgl.getCollId() == athenaElectricMeter.getCollectionMethodCode().intValue()) {
                            list.add(electricCollRlgl.getElectricId());
                        }
                    }
                    //循环获取采集参数
                    for (Integer integer : list) {
                        ElectricParams electricParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams, (long) integer);
                        paramsId.add(electricParams.getCode());
                    }
                }
                /**********根据电表系统名称及采集参数查询   电能参数数据************/
                Map<String, ElectricParams> mappParams = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams);
                /**获取电参数名称字典***/
                Map<String, String> dictMap = new HashMap<>();
                for (ElectricParams value : mappParams.values()) {
                    dictMap.put(value.getCode(), value.getName());
                }
                //将AI 点 默认参数加载进来
                dictMap.put(PointPowerParam.Point_Meter_Code, PointPowerParam.Point_Meter_Name);
                /**Excel标题名称***/
                Map<String, String> titleList = new LinkedHashMap();
                titleList.put("index", "序号");
                titleList.put("time", "采集时间");

                /**根据code 设置Excel标题名称***/
                for (String o : paramsId) {
                    if (StringUtils.isNotEmpty(dictMap.get(o))) {
                        titleList.put(o, dictMap.get(o));
                    }
                }
                Map<String, Object> map = mapper.selectDataByParamsCodeOther(paramsId, paramVO.getStartTime(), paramVO.getEndTime(), athenaElectricMeter.getSysName());
                //将数据按顺序排列
                List<Map<String, Object>> dataList = new LinkedList<>();
                map.values().forEach(item -> {
                    Map<String, Object> mapLink = new LinkedHashMap<>();
                    Map<String, Object> mapData = (HashMap<String, Object>) item;
                    for (String s : titleList.keySet()) {
                        if ("index".equals(s)) {
                            continue;
                        }
                        mapLink.put(s, mapData.get(s));
                    }
                    dataList.add(mapLink);
                });
                ArrayList<String> title = new ArrayList<>(titleList.values());
                listTitle.add(title);
                listData.add(sortByFeild(dataList, "time", "desc"));
            } else {
                //第三方
                /**********先获取电表对应的采集参数*************/
                //数据项ID
                List<String> paramsId = new ArrayList<>();
                //获取设备信息
                Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,id);
                //获取绑定的数据项
                String electricParam = athenaBranchMeterLink.getElectricParam();
                String[] paramsArr = electricParam.split(",");
                //添加sheet
                listSheet.add(equipment.getName());
                //循环获取数据项
                /**获取电参数名称字典***/
                Map<String, String> dictMap = new HashMap<>();
                for (String itemDataId : paramsArr) {
                    ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, Long.parseLong(itemDataId));
                    dictMap.put(itemDataId, productItemData.getName());
                    paramsId.add(itemDataId);
                }

                /**********根据电表系统名称及采集参数查询   电能参数数据************/
                /**Excel标题名称***/
                Map<String, String> titleList = new LinkedHashMap();
                titleList.put("index", "序号");
                titleList.put("time", "采集时间");

                /**根据code 设置Excel标题名称***/
                for (String o : paramsId) {
                    if (StringUtils.isNotEmpty(dictMap.get(o))) {
                        titleList.put(o, dictMap.get(o));
                    }
                }
                Map<String, Object> map = mapper.selectDataByParamsCodeOtherOther(paramsId, paramVO.getStartTime(), paramVO.getEndTime(), String.valueOf(equipment.getId()));
                //将数据按顺序排列
                List<Map<String, Object>> dataList = new LinkedList<>();
                map.values().forEach(item -> {
                    Map<String, Object> mapLink = new LinkedHashMap<>();
                    Map<String, Object> mapData = (HashMap<String, Object>) item;
                    for (String s : titleList.keySet()) {
                        if ("index".equals(s)) {
                            continue;
                        }
                        mapLink.put(s, mapData.get(s));
                    }
                    dataList.add(mapLink);
                });
                ArrayList<String> title = new ArrayList<>(titleList.values());
                listTitle.add(title);
                listData.add(sortByFeild(dataList, "time", "desc"));
            }
        }
        exportCommonBatch(listData, listTitle, listSheet, "支路下所有电表数据表", response);

    }

    /**
     * 导出
     *
     * @param dataList  组装后的数据
     * @param titleList 标题行名称
     * @param sheetName sheet名称
     * @param fileName  文件名称
     * @param response  响应
     * @Author qindehua
     * @Date 2022/12/01
     **/
    public void exportCommon(List<Map<String, Object>> dataList, List<String> titleList, String sheetName, String fileName, HttpServletResponse response) {
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 创建sheet
        XSSFSheet sheet = workbook.createSheet(sheetName);
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
            maxWidth.put(i, cell0.getStringCellValue().getBytes().length * 256 + 400);
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

    /**
     * 批量导出通用
     *
     * @param dataList  组装后的数据
     * @param titleList 标题行名称
     * @param listSheet sheet名单
     * @param fileName  excel名称
     * @param response  响应
     * @Author qindehua
     * @Date 2022/12/01
     **/

    public void exportCommonBatch(List<List<Map<String, Object>>> dataList, List<List<String>> titleList, List<String> listSheet, String fileName, HttpServletResponse response) {
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();

        for (int x = 0; x < dataList.size(); x++) {
            // 创建sheet
            XSSFSheet sheet = workbook.createSheet(listSheet.get(x));
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
            for (int i = 0; i < titleList.get(x).size(); i++) {
                XSSFCell cell0 = row.createCell(i);
                row.createCell(i).setCellValue(titleList.get(x).get(i));
                maxWidth.put(i, cell0.getStringCellValue().getBytes().length * 256 + 400);
                cell0.setCellStyle(style);
            }
            //填充数据
            for (int i = 0; i < dataList.get(x).size(); i++) {
                Map<String, Object> valueMap = dataList.get(x).get(i);
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
            for (int i = 0; i < titleList.get(x).size(); i++) {
                sheet.setColumnWidth(i, maxWidth.get(i));
            }
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
