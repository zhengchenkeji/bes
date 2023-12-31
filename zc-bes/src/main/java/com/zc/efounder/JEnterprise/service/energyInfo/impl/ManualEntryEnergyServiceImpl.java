package com.zc.efounder.JEnterprise.service.energyInfo.impl;

import com.zc.ApplicationContextProvider;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.deviceTree.Point;
import com.zc.efounder.JEnterprise.Cache.ElectricParamsCache;
import com.zc.efounder.JEnterprise.Cache.MeterCache;

import com.zc.efounder.JEnterprise.Cache.ModuleAndPointCache;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.domain.energyInfo.ManualEntryEnergy;
import com.zc.efounder.JEnterprise.domain.energyInfo.ManualentryenergyCollection;
import com.zc.efounder.JEnterprise.commhandler.EntryEnergyHandler;
import com.zc.efounder.JEnterprise.mapper.energyInfo.ManualentryenergyCollectionMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.ManualentryenergyMapper;
import com.zc.efounder.JEnterprise.service.energyInfo.ManualEntryEnergyService;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.PointPowerParam;
import com.zc.common.constant.RedisKeyConstants;

import org.apache.poi.ss.usermodel.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;

/**
 * description:手动录入能耗信
 * author: sunshangeng
 */
@Service
public class ManualEntryEnergyServiceImpl implements ManualEntryEnergyService {

    @Resource
    private ModuleAndPointCache moduleAndPointCache;

    private static final Logger log = LoggerFactory.getLogger(ManualEntryEnergyServiceImpl.class);

    private static RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);
    @Resource
    private ManualentryenergyMapper manualentryenergyMapper;

    @Resource
    private ManualentryenergyCollectionMapper manualentryenergyCollectionMapper;

    private static MeterCache meterCache = ApplicationContextProvider.getBean(MeterCache.class);

    private static ElectricParamsCache electricParamsCache = ApplicationContextProvider.getBean(ElectricParamsCache.class);


    /***
     * @description:获取所有手动录入能耗信息
     * @author: sunshangeng
     * @param: [manualentryenergy]
     * @return: java.util.List<com.ruoyi.energyInfo.manualEntryEnergy.domain.ManualEntryEnergy>
     **/
    @Override
    public List<ManualEntryEnergy> getmanualEntryEnergyData(ManualEntryEnergy manualentryenergy) {
        List<ManualEntryEnergy> entryEnergyList = manualentryenergyMapper.selectManualEntryEnergyList(manualentryenergy);

        entryEnergyList.forEach(item -> {
            DeviceTree tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, item.getControllerTreeid());
            item.setControllerName(tree.getSysName());
            tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, item.getPointTreeid());
            item.setPointName(tree.getSysName());
        });
        return entryEnergyList;

    }

    /**
     * @description:添加能耗信息
     * @author: sunshangeng
     * @param: [manualentryenergy]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    @Transactional
    public AjaxResult insertManualentryenergy(ManualEntryEnergy manualentryenergy) {
        if (manualentryenergy == null ||
                manualentryenergy.getEnergyCjsj() == null
                || !StringUtils.hasText(manualentryenergy.getEnergyType())
                || manualentryenergy.getElectricparamsNameList() == null
                || manualentryenergy.getElectricparamsNameList().size() == 0
        ) {
            return AjaxResult.error("传入的信息不完整，请核对后提交！");
        }

        /**判断采集器id是否为空 ，避免导入时重复操作*/
        if (manualentryenergy.getControllerTreeid() == null) {
            Controller controller = meterCache.getControllerByMeterTreeid(manualentryenergy.getPointTreeid() + "");
            manualentryenergy.setControllerTreeid((long) controller.getDeviceTreeId());
        }
        /**插入能耗信息*/
        manualentryenergyMapper.insertManualentryenergy(manualentryenergy);

        /**循环插入能耗详情*/
        for (ManualentryenergyCollection manualentryenergyCollection : manualentryenergy.getElectricparamsNameList()) {

            manualentryenergyCollection.setManualentryenergyId(manualentryenergy.getId());

            boolean save = manualentryenergyCollectionMapper.insertManualentryenergyCollection(manualentryenergyCollection);
            /**插入错误自动回滚*/
            if (!save) {
                throw new ServiceException("采集参数为：" + manualentryenergyCollection.getParamCode() + "的数据，插入能耗详情出错");
            }
        }
        Boolean save = null;
        DeviceTree point = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, manualentryenergy.getPointTreeid());
        if (point.getDeviceNodeId() == DeviceTreeConstants.BES_AMMETER) {
            /**电表*/
            save = EntryEnergyHandler.saveMeterEnergy(manualentryenergy);

        } else {
            /**AI节点*/
            save = EntryEnergyHandler.savePointEnery(manualentryenergy);
        }
        /**插入成功后保存能耗信息*/
        if (!save) {
            throw new ServiceException("编码为：" + manualentryenergy.getPointTreeid() + "数据，插入能耗数据出错");
        }
        return AjaxResult.success("插入成功");
    }

    /**
     * @description:获取数结构
     * @author: sunshangeng
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult treeSelect() {
        List<DeviceTree> deviceTreeList = new ArrayList<>();
        Map<String, DeviceTree> stringdeviceTreeMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree);
        stringdeviceTreeMap.forEach((key, value) -> {
            /**
             * @description:完善点位信息
             * @author: gaojikun
             **/
            if(value.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_AO
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_AI
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_DO
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_DI
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_UI
                    || value.getDeviceNodeId() == DeviceTreeConstants.BES_UX){
                Point point = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Point, (long) value.getDeviceTreeId());
                if(value.getDeviceTreeId() == 4518){
                    value.setControllerId(point.getControllerId());
                    value.setEngineerUnit(point.getEngineerUnit());
                }else{
                    value.setControllerId(point.getControllerId());
                    value.setEngineerUnit(point.getEngineerUnit());
                }
            }
            if (value.getSysName() == null || "".equals(value.getSysName())) {
                //如果点位信息没有则加上显示名称
                if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AI) {
                    value.setSysName("AI节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AO) {
                    value.setSysName("AO节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DI) {
                    value.setSysName("DI节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_DO) {
                    value.setSysName("DO节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UI) {
                    value.setSysName("UI节点");
                } else if (value.getDeviceNodeId() == DeviceTreeConstants.BES_UX) {
                    value.setSysName("UX节点");
                }
            }
            /**判断能源AI节点*/
            if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AI) {
                /**判断当前是否是能耗节点*/
                Point point = moduleAndPointCache.getPointByDeviceId((long) value.getDeviceTreeId());
                /**判断AI节点是否能耗采集*/

                if (point.getEnergyStatics()!=null && 1 == point.getEnergyStatics()) {

                    value.setEnergyNode(Boolean.TRUE);
                }
            }
            /**判断虚点节点*/
            if (value.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT) {
                /**获取虚点数据*/
                Point point = moduleAndPointCache.getPointByDeviceId((long) value.getDeviceTreeId());
                /**判断虚点是否是VAI节点是否能耗采集*/
                if (point.getEnergyStatics() == 1 && point.getVpointType().equals(DictUtils.getDictValue(DeviceTreeConstants.BES_VPOINT_TYPE, DeviceTreeConstants.BES_VPOINT_LABEL_VAI))) {
                    value.setEnergyNode(Boolean.TRUE);
                }
            }

            /**判断电表*/
            if (value.getDeviceNodeId() == DeviceTreeConstants.BES_AMMETER) {
                value.setEnergyNode(Boolean.TRUE);
            }
            deviceTreeList.add(value);
        });

        if (deviceTreeList.size() > 0) {

            deviceTreeList.sort((o1, o2) -> String.valueOf(o1.getDeviceTreeId()).compareTo(String.valueOf(o2.getDeviceTreeId())));

            return AjaxResult.success("获取成功", deviceTreeList);
        }
        return AjaxResult.error("获取失败");
    }

    /**
     * @description:根据电表id获取采集参数
     * @author: sunshangeng
     * @date: 2022/12/2 17:25
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult getElectricParams(String meterid) {
        if (!StringUtils.hasText(meterid)) {
            return AjaxResult.error("获取失败,未传入电表信息!");
        }
        List<DicDataEntity> paramslist = new ArrayList<>();
        /**获取到电表信息*/
        AthenaElectricMeter athenaElectricMeter = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter, meterid);
        DeviceTree tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, meterid);

        /**AI能耗节点*/
        if (tree.getDeviceNodeId() == DeviceTreeConstants.BES_AI || tree.getDeviceNodeId() == DeviceTreeConstants.BES_VPOINT) {

            DicDataEntity dataEntity = new DicDataEntity(PointPowerParam.Point_Meter_Code, PointPowerParam.Point_Meter_Name);

            paramslist.add(dataEntity);
            return AjaxResult.success(paramslist);
        }
//        if(athenaElectricMeter==null){
//            /**如果当前电表未绑定采集方案直接返回*/
//
//            return AjaxResult.success(electricParams);
//        }
        if (athenaElectricMeter.getCollectionMethodCode() == null) {
            /**如果当前电表未绑定采集方案直接返回*/
            return AjaxResult.success(paramslist);
        }
        /**获取到采集方案信息*/
        Map<String, CollMethod> collMethodMap = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod);
        CollMethod collMethod = new CollMethod();
        for (CollMethod value : collMethodMap.values()) {
            if (value.getId() == athenaElectricMeter.getCollectionMethodCode()) {
                collMethod = value;
                break;
            }
        }
        String methodCode = collMethod.getCode();
        /**根据采集方案获取采集参数*/
        paramslist = manualentryenergyMapper.getElectricParamsbyCollCode(methodCode);
        return AjaxResult.success(paramslist);
    }

    /***
     * @description:导入功能
     * @author: sunshangeng
     * @param: [request, file]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult impExcel(HttpServletRequest request, MultipartFile file) throws Exception {
        if (file == null) {
            return AjaxResult.error("未上传文件！");

        }
        List<ManualEntryEnergy> entryEnergyList = importExcel(file.getInputStream());
//            /**获取到导入的数据后根据采集时间排序数据*/
        if (entryEnergyList.size() > 0) {
            entryEnergyList.sort((o1, o2) -> o1.getEnergyCjsj().compareTo(o2.getEnergyCjsj()));
        }
        /**插入数据*/
        for (ManualEntryEnergy manualEntryEnergy : entryEnergyList) {
            insertManualentryenergy(manualEntryEnergy);
        }

        return AjaxResult.success();
    }

    /**
     * @description:获取手动录入能耗详情
     * @author: sunshangeng
     * @param: [entryEnergyId]
     * @return: java.util.List<com.ruoyi.energyInfo.manualEntryEnergy.domain.ManualentryenergyCollection>
     **/
    @Override
    public List<ManualentryenergyCollection> getEnergyDetailData(String entryEnergyId) {

        List<ManualentryenergyCollection> manualentryenergyCollectionList = manualentryenergyCollectionMapper.selectEnergyDetailData(entryEnergyId);

        manualentryenergyCollectionList.forEach(item -> {
            if (item.getParamCode().equals(PointPowerParam.Point_Meter_Code)) {
                item.setParamName(PointPowerParam.Point_Meter_Name);

            } else {
                ElectricParams param = electricParamsCache.getCacheByEnergyCode(item.getParamCode());
                item.setParamName(param.getName());
            }

        });

        return manualentryenergyCollectionList;
    }

    /***
     * @description:组装导出手动能耗信息
     * @author: sunshangeng
     * @param: [manualentryenergy]
     * @return: java.util.List<com.ruoyi.energyInfo.manualEntryEnergy.domain.ManualEntryEnergy>
     **/
    @Override
    public List<ManualEntryEnergy> exportManualEntryEnergyData(ManualEntryEnergy manualentryenergy) {
        List<ManualEntryEnergy> entryEnergyList = manualentryenergyMapper.selectManualEntryEnergyList(manualentryenergy);
        entryEnergyList.forEach(item -> {

            DeviceTree tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, item.getControllerTreeid());
            /**组装控制器名称*/
            item.setControllerName(tree.getSysName());
            tree = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree, item.getPointTreeid());
            /**组装能耗节点名称*/
            item.setPointName(tree.getSysName());
            /**组装能耗类型名称*/
            item.setEnergyTypeName(getEnergyTypeNameByCode(item.getEnergyType()));
            StringBuffer paramKeyValues = new StringBuffer();
            /**组装采集参数键值*/

            List<ManualentryenergyCollection> manualentryenergyCollectionList = manualentryenergyCollectionMapper.selectEnergyDetailData(item.getId() + "");
            manualentryenergyCollectionList.forEach(params -> {
                ElectricParams param = electricParamsCache.getCacheByEnergyCode(params.getParamCode());
                if (params.getParamCode().equals(PointPowerParam.Point_Meter_Code)) {
                    paramKeyValues.append(PointPowerParam.Point_Meter_Name);

                } else {
                    paramKeyValues.append(param.getName());

                }
                paramKeyValues.append("：");
                paramKeyValues.append(String.format("%.6f", params.getEnergyValue()));
                paramKeyValues.append(",");
            });
            if (StringUtils.hasText(paramKeyValues)) {
                paramKeyValues.toString().substring(0, paramKeyValues.length() - 1);
                item.setExcelParamValues(paramKeyValues.toString());
            }
        });
        return entryEnergyList;
    }

    /**
     * @description:根据code获取能耗类型名称
     * @author: sunshangeng
     * @param: [code]
     * @return: java.lang.String
     **/
    public String getEnergyTypeNameByCode(String code) {
        if (!StringUtils.hasText(code)) {

            return null;
        }

        String energyTypeName = "";
        Map<String, EnergyType> EnergyTypeMaps = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
        for (EnergyType energyType : EnergyTypeMaps.values()) {
            if (energyType.getCode().equals(code)) {
                energyTypeName = energyType.getName();
                break;
            }
        }
        return energyTypeName;
    }


    /**
     * @description: 删除手动能耗信息
     * @author: sunshangeng
     * @param: [ids]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    @Transactional
    public AjaxResult deleteManualentryenergy(Long[] ids) throws Exception {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("删除失败传入的参数为空！");
        }
        /**删除能耗信息*/
        int isdelete = manualentryenergyMapper.deleteManualentryenergyByIds(ids);
        if (isdelete > 0) {
            /**删除能耗详情*/
            for (int i = 0; i < ids.length; i++) {
                Boolean isdeleteColltion = manualentryenergyCollectionMapper.deleteByManualentryenergyId(ids[i]);
                if (!isdeleteColltion) {
                    throw new Exception("删除能耗详情时失败！");
                }
            }
        } else {
            throw new Exception("删除能耗信息时失败！");

        }
        return AjaxResult.success(ids);
    }

    /**
     * @description:获取所有能耗类型
     * @author: sunshangeng
     * @return: java.util.List<com.ruoyi.energyInfo.energy.domain.EnergyType>
     **/
    @Override
    public Collection<EnergyType> allEnergyTypeList() {

        Map<String, EnergyType> energyTypeCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
        Collection<EnergyType> values = energyTypeCache.values();
        return values;
    }


    /**
     * 处理上传的内容
     */
    public List<ManualEntryEnergy> importExcel(InputStream is) throws Exception {
        if (is == null) {
            throw new ServiceException("传入的文件未控！请核对！");

        }

        /*生成poi对象*/
        Workbook wb = WorkbookFactory.create(is);
        if (wb.getNumberOfSheets() == 1) {
            throw new ServiceException("当前模板不正确！请核对！");
        }

        System.out.println("sheet数量：" + wb.getNumberOfSheets());
        /**默认获取sheet2的内容*/
        Sheet sheet = wb.getSheetAt(1);
        if (sheet == null) {
            throw new IOException("当前表格未录入数据项！请按照模板填写！");
        }
        /*判断是否有数据项*/
        int rows = sheet.getLastRowNum();
        if (rows < 1) {
            throw new IOException("当前表格未录入数据项！请按照模板填写！");
        }
        Row titleRow = sheet.getRow(0);
        if (!titleRow.getCell(0).getStringCellValue().equals("能耗节点") ||
                !titleRow.getCell(1).getStringCellValue().equals("能源类型") ||
                !titleRow.getCell(2).getStringCellValue().equals("采集时间") ||
                !titleRow.getCell(3).getStringCellValue().equals("采集参数键值")) {
            throw new ServiceException("当前模板不正确！请核对！");

        }


        /**获取标题行*/
//        Row headerow=sheet.getRow(0);
        // 从第2行开始取数据,默认第一行是表头.
        List<ManualEntryEnergy> entryEnergyList = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            /**获取行数据*/
            Row row = sheet.getRow(i);
            /**如果当前数据为空 跳过*/
            if (row == null || row.getCell(0).getCellType().equals(CellType.BLANK)) {
                break;
            }
            ManualEntryEnergy manualEntryEnergy = new ManualEntryEnergy();
            List<ManualentryenergyCollection> entryenergyCollectionList = new ArrayList<>();
            /**因不确定列长度,死循环遍历*/
            for (int cellindex = 0; cellindex < 100; cellindex++) {
                Cell cell = row.getCell(cellindex);
                /**如果获取不到列数值代表当前行数据已经获取完成 退出循环*/
                if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
                    break;
                }
                if (cellindex == 0) {
                    manualEntryEnergy.setPointTreeid(Long.parseLong(cell.getStringCellValue()));
                    Controller controller = meterCache.getControllerByMeterTreeid(manualEntryEnergy.getPointTreeid() + "");
                    /**如果识别不到采集器 跳出本次循环*/
                    if (controller == null) {
                        throw new ServiceException("能耗节点为：" + cell.getStringCellValue() + "，数据导入不成功，当前数据条目，识别不到采集器，能耗节点识别错误！");
                    }
                    manualEntryEnergy.setControllerTreeid((long) controller.getDeviceTreeId());
                } else if (cellindex == 1) {
                    if (!cell.getCellType().equals(CellType.STRING)) {
                        throw new ServiceException("能耗节点为：" + manualEntryEnergy.getPointTreeid() + "，数据导入不成功，当前数据条目，能源类型数据格式不正确，请核对！");
                    }
                    /**判断能源类型是否合法**/
                    Map<String, EnergyType> energyTypeList = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_EnergyInfo_EnergyType);
                    for (EnergyType value : energyTypeList.values()) {
                        if (value.getCode().equals(cell.getStringCellValue())) {
                            manualEntryEnergy.setEnergyType(cell.getStringCellValue());
                            continue;
                        }
                    }
                    if (!StringUtils.hasText(manualEntryEnergy.getEnergyType())) {
                        throw new ServiceException("能耗节点为：" + manualEntryEnergy.getPointTreeid() + "，能源类型为：" + manualEntryEnergy.getEnergyType() + "，节点类型数据导入不成功，当前数据条目，能源类型不合法，请核对！");
                    }

                } else if (cellindex == 2) {
                    if (cell.getCellType().equals(CellType.NUMERIC)) {
                        manualEntryEnergy.setEnergyCjsj(cell.getDateCellValue());
                    } else {
                        /**非正常数据格式*/
                        throw new ServiceException("能耗节点为：" + manualEntryEnergy.getPointTreeid() + "，能源类型为：" + manualEntryEnergy.getEnergyType() + "，数据导入不成功，当前数据条目，采集时间数据格式不正确，请核对！");
                    }
                } else {
                    String paramValue = cell.getStringCellValue();
                    /**解析对应的采集参数和能耗值*/
                    /**说明当前行数据未成功写入电表值跳出循环*/

//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                    int index = paramValue.indexOf("=");
                    if (index == -1) {
                        throw new ServiceException("能耗节点为：" + manualEntryEnergy.getPointTreeid() + "，能源类型为：" + manualEntryEnergy.getEnergyType() + "，能耗采集采集时间为：" + DateUtils.parseDateToStr("yyyy/MM/dd HH:mm:ss", manualEntryEnergy.getEnergyCjsj()) + "，数据导入不成功，当前数据条目，采集参数键值数据格式不正确，请核对！");
                    }
                    /**正常*/
                    ManualentryenergyCollection collection = new ManualentryenergyCollection();
                    collection.setParamCode(paramValue.substring(0, index));
                    collection.setEnergyValue(Double.parseDouble(paramValue.substring(index + 1, paramValue.length())));
                    entryenergyCollectionList.add(collection);
                }
            }
            manualEntryEnergy.setElectricparamsNameList(entryenergyCollectionList);
            entryEnergyList.add(manualEntryEnergy);
        }
        return entryEnergyList;

    }


}
