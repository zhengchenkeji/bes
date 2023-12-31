package com.zc.efounder.JEnterprise.service.excelTableImport.Impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.poi.ExcelError;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.zc.efounder.JEnterprise.domain.moduleType.ModuleType;
import com.zc.efounder.JEnterprise.Cache.ControllerCache;
import com.zc.efounder.JEnterprise.Cache.DeviceTreeCache;
import com.zc.efounder.JEnterprise.mapper.excelTableImport.DeviceTreeExcelTableImportMapper;
import com.zc.efounder.JEnterprise.service.excelTableImport.DeviceTreeExcelTableImportService;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.common.core.model.excelres.Pzlj;
import com.zc.efounder.JEnterprise.domain.deviceTree.*;
import com.zc.efounder.JEnterprise.domain.excelTableImport.*;
import com.zc.efounder.JEnterprise.service.deviceTree.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static com.ruoyi.common.utils.SecurityUtils.getUsername;

/**
 * @Author: wanghongjie
 * @Description:设备树表格导入/导出
 * @Date: Created in 18:49 2022/11/9
 * @Modified By:
 */
@Service
public class DeviceTreeExcelTableImportServiceImpl implements DeviceTreeExcelTableImportService {

    @Resource
    private DeviceTreeExcelTableImportMapper excelTableImportMapper;

    @Resource
    private DeviceTreeCache deviceTreeCache;

    @Resource
    private ControllerCache controllerCache;

    @Resource
    private Pzlj pzlj;// 获取配置文件路径

    @Resource
    private RedisCache redisCache;

    @Resource
    private DeviceTreeService deviceTreeService;

    @Resource
    private ControllerService controllerService;

    @Resource
    private ModuleService moduleService;

    @Resource
    private PointService pointService;

    @Resource
    private BusService busService;

    @Resource
    private AthenaElectricMeterService athenaElectricMeterService;


    /**
     * @Description: 设备树导入管理
     * @auther: wanghongjie
     * @date: 18:52 2022/11/9
     * @param: [request, file, updateSupport]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public AjaxResult impExcel(HttpServletRequest request, MultipartFile multipartFile, boolean updateSupport) throws IOException {

        DataReception dataReception = new DataReception();

        if (multipartFile != null) {
            if (multipartFile.getSize() != 0L) {

                try {
                    ExcelUtil<DeviceTreeExcel> util = new ExcelUtil<DeviceTreeExcel>(DeviceTreeExcel.class);
                    List<DeviceTreeExcel> list = util.importExcel("节点表", multipartFile.getInputStream(), 0);// 导入excel,处理后生成list

                    if (list.size() > 0) {

                        if (list.get(0).getDeviceType().equals("3")) {//能耗

                            dataReception = energy(list, multipartFile);

                        } else if (list.get(0).getDeviceType().equals("2")) {//照明控制

                            dataReception = lightingControl(list, multipartFile);

                        } else if (list.get(0).getDeviceType().equals("1")) {//楼宇自控

                            dataReception = buildingAutomation(list, multipartFile);

                        }
                    }
                } catch (FileNotFoundException e) {
                    dataReception.setCode("0");
                    dataReception.setMsg("模板错误！");
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    dataReception.setCode("0");
                    dataReception.setMsg("模板错误！");
                    e.printStackTrace();
                } catch (Exception e) {
                    dataReception.setCode(dataReception.getCode());
                    dataReception.setMsg(dataReception.getMsg());
//					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    e.printStackTrace();
                }
            }
        }

        if (dataReception.getCode() == "1") {
            //重新加载设备树缓存
            Boolean boo = syncCache();
            if (boo) {
                return AjaxResult.success("导入成功");
            }

        }
        return AjaxResult.error(dataReception.getMsg());

    }

    /**
     * @Description: 能耗采集
     * @auther: wanghongjie
     * @date: 15:16 2022/11/19
     * @param: [list, multipartFile]
     * @return: com.zc.common.core.model.DataReception
     */
    private DataReception energy(List<DeviceTreeExcel> list, MultipartFile multipartFile) {
        DataReception dataReception = new DataReception();
// 获取要导出的数据
        List<ExcelError> excelErrors = new ArrayList<>();
        boolean inportflag = false;
        Map<String, Object> psysName = null;
        // 初始化导入工具类
        FileInputStream fis = null;

        String nodeTabName = "能耗节点表";


        try {

            Set<String> point = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {

                //判断总线表中系统名称数据是否重复
                Boolean success = point.add(list.get(i).getSysName());
                if (!success) {
                    throw new Exception(list.get(i).getSysName() + ":系统名称在能耗节点表中重复");
                }
                boolean flag = true; //插入数据成功的标志
                DeviceTreeExcel deviceTreeExcel = list.get(i);
                String errMsg = "";
                if (deviceTreeExcel.getDeviceNodeId() == null || deviceTreeExcel.getDeviceNodeId().equals("")) {
                    errMsg = "节点类型为空";
                    flag = false;
                } else if (deviceTreeExcel.getSysName() == null || deviceTreeExcel.getSysName().equals("")) {
                    if ("".equals(errMsg)) {
                        errMsg = "系统名称为空";
                        flag = false;
                    } else {
                        errMsg = errMsg + ",系统名称为空";
                        flag = false;
                    }
                } else if (deviceTreeExcel.getDescription() == null || deviceTreeExcel.getDescription().equals("")) {

                    deviceTreeExcel.setDescription(deviceTreeExcel.getSysName());

                } else if (deviceTreeExcel.getPsysName() == null || deviceTreeExcel.getPsysName().equals("")) {

                    if ("".equals(errMsg)) {
                        errMsg = "父节点系统名称为空";
                        flag = false;
                    } else {
                        errMsg = errMsg + ",父节点系统名称为空";
                        flag = false;
                    }
                } else if (deviceTreeExcel.getAlias() == null || deviceTreeExcel.getAlias().equals("")) {

                    deviceTreeExcel.setAlias(deviceTreeExcel.getSysName());

                } else if (deviceTreeExcel.getDeviceType() == null || deviceTreeExcel.getDeviceType().equals("")) {

                    if ("".equals(errMsg)) {
                        errMsg = "所属系统为空";
                        flag = false;
                    } else {
                        errMsg = errMsg + ",所属系统为空";
                        flag = false;
                    }
                } else if (deviceTreeExcel.getPark() == null || deviceTreeExcel.getPark().equals("")) {

                    if ("".equals(errMsg)) {
                        errMsg = "园区编号为空";
                        flag = false;
                    } else {
                        errMsg = errMsg + ",园区编号为空";
                        flag = false;
                    }
                }

                if (!flag) {
                    ExcelError excelError = new ExcelError();
                    excelError.setRow((i + 2) + "");
                    excelError.setErrorMsg(errMsg);
                    excelErrors.add(excelError);
                }
            }


            if (excelErrors.size() > 0) {
                throw new Exception(nodeTabName + "第" + excelErrors.get(0).getRow() + "行" + excelErrors.get(0).getErrorMsg());
            }

            for (DeviceTreeExcel treeExcel : list) {

                switch (treeExcel.getDeviceNodeId()) {

                    case "25": {//能耗采集节点


                        dataReception = insertBesStruct(treeExcel, nodeTabName);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }

                        BuildNode buildNode = new BuildNode();
                        buildNode.setTreeId(treeExcel.getDeviceTreeId());
                        buildNode.setNodeType(Integer.valueOf(treeExcel.getDeviceNodeId()));
                        buildNode.setSysName(treeExcel.getSysName());
                        buildNode.setNickName(treeExcel.getAlias());
                        buildNode.setCreateTime(DateUtils.getNowDate());
                        inportflag = excelTableImportMapper.add_buildNode(buildNode);
                        if (!inportflag) {
                            throw new Exception(treeExcel.getSysName() + "添加能耗采集节点失败");
                        }

                        break;
                    }
                    case "3": {//能耗控制器节点


                        ExcelUtil<ControllerExcel> util = new ExcelUtil<ControllerExcel>(ControllerExcel.class);
                        List<ControllerExcel> collectorlist = util.importExcel("能耗控制器节点", multipartFile.getInputStream(), 0);// 导入excel,处理后生成list

                        dataReception = insertDDCByExcel(collectorlist, treeExcel);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }


                        break;

                    }
                    case "4": {//能耗总线节点

                        ExcelUtil<BusExcel> util = new ExcelUtil<BusExcel>(BusExcel.class);
                        List<BusExcel> busExcelList = util.importExcel("能耗总线节点", multipartFile.getInputStream(), 0);// 导入excel,处理后生成list


                        List lists = new ArrayList();

                        Set<String> point_bus = new HashSet<>();
                        for (BusExcel busExcel : busExcelList) {
                            //判断总线表中系统名称数据是否重复
                            Boolean success = point_bus.add(busExcel.getSysName());
                            if (!success) {
                                throw new Exception(busExcel.getSysName() + ":系统名称在能耗总线节点表中重复");
                            }

                            lists.add(busExcel.getSysName());
                        }


                        Boolean aa = lists.contains(treeExcel.getSysName());

                        if (!aa) {
                            throw new Exception(treeExcel.getSysName() + ":系统名称在能耗总线节点表中不存在");
                        }

                        nodeTabName = "总线节点表";
                        dataReception = insertBesStruct(treeExcel, nodeTabName);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }

                        for (BusExcel busExcel : busExcelList) {

                            String f_sys_name = busExcel.getSysName();
                            String f_port = busExcel.getPort();

                            if (treeExcel.getSysName().equals(f_sys_name)) {

                                if (!StringUtils.hasText(f_sys_name)
                                        || !StringUtils.hasText(f_port)
                                ) {
                                    throw new Exception(f_sys_name + "能耗总线节点表格内容不完整");
                                }


                                //查询系统名称在设备配置表中是否存在
//                                if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name)) {
//                                    throw new Exception(f_sys_name + "系统名称在节点表中不存在");
//                                }
                                BuildNode buildNode = new BuildNode();
                                buildNode.setPortNum(f_port);
                                buildNode.setTreeId(treeExcel.getDeviceTreeId());
                                buildNode.setNodeType(Integer.valueOf(treeExcel.getDeviceNodeId()));
                                buildNode.setSysName(treeExcel.getSysName());
                                buildNode.setNickName(treeExcel.getAlias());
                                buildNode.setCreateTime(DateUtils.getNowDate());
                                inportflag = excelTableImportMapper.add_ammeterBus(buildNode);
                                if (!inportflag) {
                                    throw new Exception(treeExcel.getSysName() + "添加电表总线失败");
                                }
                            }
                        }
                        if (inportflag) {
                            dataReception.setMsg("导入成功！");
                            dataReception.setCode("1");
                        } else {
                            throw new Exception("导入失败");
                        }
                        break;
                    }
                    case "5": {//能耗电表节点
                        ExcelUtil<AmmeterExcel> util = new ExcelUtil<>(AmmeterExcel.class);
                        List<AmmeterExcel> ammeterlist = util.importExcel("能耗电表节点", multipartFile.getInputStream(), 0);// 导入excel,处理后生成list

                        List lists = new ArrayList();

                        Set<String> point_ammeter = new HashSet<>();
                        for (AmmeterExcel ammeterExcel : ammeterlist) {
                            //判断能耗节点表中系统名称数据是否重复
                            Boolean success = point_ammeter.add(ammeterExcel.getSysName());
                            if (!success) {
                                throw new Exception(ammeterExcel.getSysName() + ":系统名称在能耗节点表中重复");
                            }

                            lists.add(ammeterExcel.getSysName());
                        }
                        Boolean aa = lists.contains(treeExcel.getSysName());

                        if (!aa) {
                            throw new Exception(treeExcel.getSysName() + ":系统名称在能耗电表节点表中不存在");
                        }
                        nodeTabName = "能耗电表节点表";
                        dataReception = insertBesStruct(treeExcel, nodeTabName);
                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }


                        for (AmmeterExcel ammeterExcel : ammeterlist) {

                            String sys_name = ammeterExcel.getSysName();            //系统名称
                            String azwz = ammeterExcel.getLocation();                //安装位置
                            String wldz = ammeterExcel.getPhysicalAddress();                //物理地址
                            String blxbh = ammeterExcel.getMeterTypeCode();                //电表类型编号
                            String comm_rate = ammeterExcel.getCommRate();            //通信波特率编号
                            String protocol_type = ammeterExcel.getProtocolTypeId();        //通信协议类型
                            String cjfabh = ammeterExcel.getCollectionMethodCode();                //采集方案编号
                            String percentage = ammeterExcel.getRate();            //变比
                            String com_data_bit = ammeterExcel.getCommDataBit();            //数据位
                            String com_parity_bit = ammeterExcel.getCommParityBit();        //校验位
                            String com_stop_bit = ammeterExcel.getCommStopBit();            //停止位
                            String function_code = ammeterExcel.getFunctionCodeId();        //功能码

                            if (treeExcel.getSysName().equals(sys_name)) {

                                if (!StringUtils.hasText(sys_name)
                                        || !StringUtils.hasText(azwz)
                                        || !StringUtils.hasText(wldz)
                                        || !StringUtils.hasText(blxbh)
                                        || !StringUtils.hasText(comm_rate)
                                        || !StringUtils.hasText(protocol_type)
                                        || !StringUtils.hasText(cjfabh)
                                        || !StringUtils.hasText(percentage)
                                        || !StringUtils.hasText(com_data_bit)
                                        || !StringUtils.hasText(com_parity_bit)
                                        || !StringUtils.hasText(com_stop_bit)
                                        || !StringUtils.hasText(function_code)
                                ) {
                                    throw new Exception(sys_name + ":能耗电表节点表格内容不完整");
                                }

                                //查询系统名称在设备配置表中是否存在
//                                if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name)) {
//                                    throw new Exception(f_sys_name + ":系统名称在节点表中不存在");
//                                }

                                //根据通信波特率编号查询通信波特率缓存
                                List<SysDictData> communication_baud_rate = DictUtils.getDictCache("communication_baud_rate");

                                Boolean commBaudRate = false;
                                for (SysDictData sysDictData : communication_baud_rate) {
                                    if (sysDictData.getDictValue().equals(comm_rate)) {
                                        commBaudRate = true;
                                    }
                                }

                                if (!commBaudRate) {
                                    throw new Exception(treeExcel.getSysName() + ":通信波特率编号在能耗电表节点表中填写错误,请检查");
                                }

//                                //根据通信协议类型编号查询通信协议名称
                                List<SysDictData> communication_protocol = DictUtils.getDictCache("communication_protocol");
                                Boolean commProtocol = false;
                                for (SysDictData sysDictData : communication_protocol) {
                                    if (sysDictData.getDictValue().equals(protocol_type)) {
                                        commProtocol = true;
                                    }
                                }

                                if (!commProtocol) {
                                    throw new Exception(treeExcel.getSysName() + ":通信协议类型编号在能耗电表节点表中填写错误,请检查");
                                }

                                //根据电表类型编号查询电表类型名称
                                List<SysDictData> sys_meter_type = DictUtils.getDictCache("sys_meter_type");

                                Boolean sysMeterType = false;
                                for (SysDictData sysDictData : sys_meter_type) {
                                    if (sysDictData.getDictValue().equals(blxbh)) {
                                        sysMeterType = true;
                                    }
                                }
                                if (!sysMeterType) {
                                    throw new Exception(treeExcel.getSysName() + ":电表类型编号在能耗电表节点表中填写错误,请检查");
                                }

//                                //根据采集方案编号查询采集方案名称
                                final Boolean[] cjfabhBoo = {false};
                                redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_CollMethod).values().forEach(item -> {
                                    CollMethod collMethod = (CollMethod) item;
                                    if (collMethod.getCode().equals(cjfabh)) {
                                        cjfabhBoo[0] = true;
                                        ammeterExcel.setCollectionMethodCode(String.valueOf(collMethod.getId()));
                                    }
                                });

                                if (!cjfabhBoo[0]) {
                                    throw new Exception(treeExcel.getSysName() + ":采集方案编号在能耗电表节点表中填写错误,请检查");
                                }

                                //首先查询当前电表父节点的端口号
                                Bus buildNode = excelTableImportMapper.selectAmmeterBus(treeExcel.getDeviceTreeFatherId());

                                ammeterExcel.setCommPort(Integer.valueOf(buildNode.getPort()));
                                ammeterExcel.setDeviceTreeId(treeExcel.getDeviceTreeId());
                                ammeterExcel.setActive(1);
                                ammeterExcel.setAlias(treeExcel.getAlias());
                                ammeterExcel.setDescription(treeExcel.getDescription());
                                ammeterExcel.setType("0");
                                ammeterExcel.setPark(treeExcel.getPark());
                                ammeterExcel.setCreateTime(DateUtils.getNowDate());

                                inportflag = excelTableImportMapper.add_structAmmeter(ammeterExcel);
                            }
                        }
                        if (inportflag) {
                            dataReception.setMsg("导入成功！");
                            dataReception.setCode("1");
                        } else {
                            throw new Exception("导入失败");
                        }

                        break;
                    }

                    default:
                        throw new Exception("节点类型不存在");
                }

            }
        } catch (FileNotFoundException | NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return dataReception;
    }

    /**
     * @Description: 照明
     * @auther: wanghongjie
     * @date: 10:36 2022/11/19
     * @param: [list, multipartFile]
     * @return: com.zc.common.core.model.DataReception
     */
    private DataReception lightingControl(List<DeviceTreeExcel> list, MultipartFile multipartFile) {

        DataReception dataReception = new DataReception();

        // 获取要导出的数据
        List<ExcelError> excelErrors = new ArrayList<>();
        boolean inportflag = false;
        // 初始化导入工具类
        //将ecxcel查出来的数据放到list里面,统一使用
        List<PointExcel> pointList = null;
        List<ModuleExcel> Modulelist = null;
        List<ControllerExcel> DDClist = null;
        List<CouplerExcel> couplerList = null;

        String nodeTabName = "智能照明节点表";

        try {

            excelErrors = nodeTable(list);


            if (excelErrors.size() > 0) {
                throw new Exception(nodeTabName + "第" + excelErrors.get(0).getRow() + "行" + excelErrors.get(0).getErrorMsg());
            }

            Set<String> point = new HashSet<>();

            for (DeviceTreeExcel treeExcel : list) {
                //判断楼宇自控节点表中系统名称数据是否重复
                Boolean success = point.add(treeExcel.getSysName());
                if (!success) {
                    throw new Exception(treeExcel.getSysName() + ":系统名称在楼宇自控节点表中重复");
                }
            }


            for (DeviceTreeExcel treeExcel : list) {

                String BES_DDCNODE = String.valueOf(DeviceTreeConstants.BES_DDCNODE);
                switch (treeExcel.getDeviceNodeId()) {


                    case "24":
                    case "22": {//智能照明节点,虚点(无属性页面)

                        dataReception = insertBesStruct(treeExcel, nodeTabName);

                        if (dataReception.getCode().equals("1")) {//新增成功

                            //添加进athena_bes_build_node楼控总线、虚点、线路表
                            DeviceTreeExcel deviceTreeExcel = new DeviceTreeExcel();
                            deviceTreeExcel = (DeviceTreeExcel) dataReception.getData();
                            BuildNode buildNode = new BuildNode();
                            buildNode.setTreeId(deviceTreeExcel.getDeviceTreeId());
                            buildNode.setNodeType(Integer.valueOf(treeExcel.getDeviceNodeId()));
                            buildNode.setSysName(treeExcel.getSysName());
                            buildNode.setNickName(treeExcel.getAlias());
                            buildNode.setCreateTime(DateUtils.getNowDate());


                            if (treeExcel.getDeviceNodeId().equals("22")) {//虚点节点(无属性页面)
                                inportflag = excelTableImportMapper.add_buildNode(buildNode);
                                if (!inportflag) {
                                    throw new Exception(treeExcel.getSysName() + "添加总线/虚点总线失败");
                                }
                            }
//                            else  if (treeExcel.getDeviceNodeId().equals("19")) {
//                                ExcelUtil<LineExcel> util = new ExcelUtil<>(LineExcel.class);
//                                lineList = util.importExcel("线路节点", multipartFile.getInputStream(), 0);// 导入excel,处理后生成list
//
//                                Set<String> lineName = new HashSet<>();
//                                List lists = new ArrayList();
//
//                                for (LineExcel lineExcel : lineList) {
//
//                                    //判断线路表中系统名称数据是否重复
//                                    Boolean success = lineName.add(lineExcel.getSysName());
//                                    if (!success) {
//                                        throw new Exception(lineExcel.getSysName() + ":系统名称在线路节点表中重复");
//                                    }
//
//                                    lists.add(lineExcel.getSysName());
//                                }
//                                Boolean aa = lists.contains(treeExcel.getSysName());
//
//                                if (!aa) {
//                                    throw new Exception(treeExcel.getSysName() + "系统名称在线路节点表中不存在");
//                                }
//
//                                for (LineExcel lineExcel : lineList) {
//                                    if (lineExcel.getSysName().equals(treeExcel.getSysName())) {
//                                        buildNode.setPortNum(lineExcel.getLineAddr());
//                                        continue;
//                                    }
//                                }
//
//                                inportflag = excelTableImportMapper.add_buildNode(buildNode);
//                                if (!inportflag) {
//                                    throw new Exception(treeExcel.getSysName() + "添加线路失败");
//                                }
//                            }

                        } else {
                            return dataReception;
                        }

                        break;
                    }
                    case "2": {//智能照明控制器节点

                        if (DDClist == null) {
                            ExcelUtil<ControllerExcel> util = new ExcelUtil<>(ControllerExcel.class);
                            DDClist = util.importExcel("照明控制器节点", multipartFile.getInputStream(), 0, false);// 导入excel,处理后生成list
                        }

                        dataReception = insertDDCByExcel(DDClist, treeExcel);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }

                        break;

                    }
                    case "28":
                    case "29": {

                        if (couplerList == null) {
                            ExcelUtil<CouplerExcel> util = new ExcelUtil<>(CouplerExcel.class);
                            couplerList = util.importExcel("耦合器节点", multipartFile.getInputStream(), 0, false);// 导入excel,处理后生成list
                        }
                        dataReception = insertCouplerByExcel(couplerList, treeExcel);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }
                        break;
                    }
                    case "18": {//模块

                        if (Modulelist == null) {
                            ExcelUtil<ModuleExcel> util = new ExcelUtil<>(ModuleExcel.class);
                            Modulelist = util.importExcel("模块节点", multipartFile.getInputStream(), 0);// 导入excel,处理后生成list
                        }

                        dataReception = insertModuleByExcel(Modulelist, treeExcel);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }

                        break;
                    }
                    case "DO":
                    case "DI":
                    case "AO":
                    case "AI": {

                        if (pointList == null) {
                            ExcelUtil<PointExcel> util = new ExcelUtil<>(PointExcel.class);
                            pointList = util.importExcel("点位节点", multipartFile.getInputStream(), 0);// 导入excel,处理后生成list
                        }

                        List lists = new ArrayList();

                        Set<String> point_DODIAOAI = new HashSet<>();
                        for (PointExcel pointExcel : pointList) {
                            //判断点位节点表中系统名称数据是否重复
                            Boolean success1 = point_DODIAOAI.add(pointExcel.getSysName());
                            if (!success1) {
                                throw new Exception(pointExcel.getSysName() + ":系统名称在点位节点表中重复");
                            }
                            lists.add(pointExcel.getSysName());
                        }
                        Boolean aa = lists.contains(treeExcel.getSysName());

                        if (!aa) {
                            throw new Exception(treeExcel.getSysName() + "系统名称在点位节点表中不存在");
                        }

                        dataReception = pointInsertByExcel(pointList, treeExcel);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }
                        break;
                    }
                    default:
                        throw new Exception(treeExcel.getDeviceNodeId() + ":节点类型不存在");
                }
            }

        } catch (FileNotFoundException | NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return dataReception;
    }

    /**
     * @Description: 添加耦合器节点
     * @auther: wanghongjie
     * @date: 11:17 2022/11/19
     * @param: [couplerList, treeExcel]
     * @return: com.zc.common.core.model.DataReception
     */
    private DataReception insertCouplerByExcel(List<CouplerExcel> couplerList, DeviceTreeExcel treeExcel) {
        DataReception dataReception = new DataReception();
        Boolean inportflag = false;

        String nodeTabName = "耦合器节点表";

        try {

            List lists = new ArrayList();

            Set<String> point = new HashSet<>();
            for (CouplerExcel couplerExcel : couplerList) {
                //判断总线表中系统名称数据是否重复
                Boolean success = point.add(couplerExcel.getSysName());
                if (!success) {
                    throw new Exception(couplerExcel.getSysName() + ":系统名称在能耗节点表中重复");
                }
                lists.add(couplerExcel.getSysName());
            }
            Boolean aa = lists.contains(treeExcel.getSysName());

            if (!aa) {
                throw new Exception(treeExcel.getSysName() + "系统名称在耦合器节点表中不存在");
            }

            dataReception = insertBesStruct(treeExcel, nodeTabName);
            if (!dataReception.getCode().equals("1")) {//新增成功
                return dataReception;
            }

            DeviceTreeExcel deviceTreeExcel = new DeviceTreeExcel();
            deviceTreeExcel = (DeviceTreeExcel) dataReception.getData();


            for (CouplerExcel couplerExcel : couplerList) {

                String sys_name = couplerExcel.getSysName();
                String addr = couplerExcel.getPort();

                if (!StringUtils.hasText(sys_name)
                        || !StringUtils.hasText(addr)
                ) {
                    throw new Exception(sys_name + ":耦合器节点表格内容不完整");
                }

                if (treeExcel.getSysName().equals(sys_name)) {

                    //查询系统名称在设备配置表中是否存在
//                    if (null == besSbdyExcelTableImportMapper.getSbTreeInfoBySysName(f_sys_name)) {
//                        throw new Exception(f_sys_name + "系统名称在节点表中不存在");
//                    }

                    //根据耦合器节点点位的父节点名称查询父节点下所有的点位
                    DeviceTree deviceTree = excelTableImportMapper.selectSbdyByPsysName(treeExcel.getPsysName());//根据父节点名称查询父节点的id
                    List<DeviceTree> module_pointList = excelTableImportMapper.selectModule_pointList(deviceTree.getDeviceTreeId());

                    DeviceTree pointMap = excelTableImportMapper.selectPointBySysName(treeExcel.getPsysName());
                    //如果当前节点是干线耦合器,则父节点只能是DDC控制器
                    if (treeExcel.getDeviceNodeId().equals("28")) {
                        if (!(pointMap.getDeviceNodeId() == 2)) {
                            throw new Exception(sys_name + ":干线耦合器不在DDC控制器下");
                        }

                        if (module_pointList.size() > 0) {//如果父节点下有点位
                            if (treeExcel.getDeviceNodeId() != String.valueOf(module_pointList.get(0).getDeviceNodeId())) {//如果当前点位的类型和父节点下点位的类型不同
                                if (module_pointList.get(0).getDeviceNodeId() == 28) {//支线耦合器
                                    throw new Exception(sys_name + ":节点所属的DDC控制器已添加支线耦合器,不能再添加干线耦合器");
                                } else if (module_pointList.get(0).getDeviceNodeId() == 18) {//模块
                                    throw new Exception(sys_name + ":节点所属的DDC控制器已添加模块节点,不能再添加干线耦合器");
                                }
                            }
                        }
                    }
                    //如果当前节点是支线耦合器,则父节点可以是干线耦合器或者DDC控制器节点
                    if (treeExcel.getDeviceNodeId().equals("29")) {
                        if (!(pointMap.getDeviceNodeId() == 2) && !(pointMap.getDeviceNodeId() == 28)) {
                            throw new Exception(sys_name + ":支线耦合器不在DDC控制器下或者干线耦合器下");
                        }

                        if (module_pointList.size() > 0) {//如果父节点下有点位
                            if (treeExcel.getDeviceNodeId() != String.valueOf(module_pointList.get(0).getDeviceNodeId())) {//如果当前点位的类型和父节点下点位的类型不同
                                if (module_pointList.get(0).getDeviceNodeId() == 28) {//干线耦合器
                                    throw new Exception(sys_name + ":节点所属的DDC控制器已添加干线耦合器,不能再添加支线耦合器");
                                } else if (module_pointList.get(0).getDeviceNodeId() == 18) {//模块
                                    throw new Exception(sys_name + ":节点所属的DDC控制器已添加模块节点,不能再添加支线耦合器");
                                }
                            }
                        }
                    }

                    couplerExcel.setAlias(treeExcel.getAlias());
                    couplerExcel.setPort(couplerExcel.getPort() + '.');

                    BuildNode buildNode = new BuildNode();
                    buildNode.setPortNum(couplerExcel.getPort());
                    buildNode.setTreeId(deviceTreeExcel.getDeviceTreeId());
                    buildNode.setNodeType(Integer.valueOf(treeExcel.getDeviceNodeId()));
                    buildNode.setSysName(treeExcel.getSysName());
                    buildNode.setNickName(treeExcel.getAlias());
                    buildNode.setCreateTime(DateUtils.getNowDate());

                    inportflag = excelTableImportMapper.add_buildNode(buildNode);

                    if (!inportflag) {
                        throw new Exception(treeExcel.getSysName() + "添加耦合器失败");
                    }
                }
            }

            if (inportflag) {
                dataReception.setMsg("导入成功！");
                dataReception.setCode("1");
            } else {
                throw new Exception("导入失败");
            }
        } catch (FileNotFoundException | NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return dataReception;
    }

    /**
     * @Description: 同步数据到缓存
     * @auther: wanghongjie
     * @date: 16:27 2022/11/18
     * @param: []
     * @return: void
     */
    private Boolean syncCache() {
        //设备树表缓存
        deviceTreeService.addDeviceTreeCache();
        //控制器缓存
        controllerService.initControllerCache();
        //模块缓存
        moduleService.addModuleCache();
        //点位缓存
        pointService.initPointCache();
        //总线缓存
        busService.addBusCache();
        //电表缓存
        athenaElectricMeterService.addMeterCache();

        return true;
    }

    /**
     * @Description: 楼宇自控
     * @auther: wanghongjie
     * @date: 10:48 2022/11/10
     * @param: [list, fileUrl]
     * @return: com.zc.common.core.model.DataReception
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception buildingAutomation(List<DeviceTreeExcel> list, MultipartFile fileUrl) {
        DataReception dataReception = new DataReception();

        // 获取要导出的数据
        List<ExcelError> excelErrors = new ArrayList<>();
        boolean inportflag = false;
        Map<String, Object> psysName = null;
        // 初始化导入工具类
        FileInputStream fis = null;
        //将ecxcel查出来的数据放到list里面,统一使用
        List<PointExcel> pointList = null;
        List<ModuleExcel> Modulelist = null;
        List<ControllerExcel> DDClist = null;
        List<LineExcel> lineList = null;

        String nodeTabName = "楼宇自控节点表";

        try {

            excelErrors = nodeTable(list);


            if (excelErrors.size() > 0) {
                throw new Exception(nodeTabName + "第" + excelErrors.get(0).getRow() + "行" + excelErrors.get(0).getErrorMsg());
            }

            Set<String> point = new HashSet<>();

            for (DeviceTreeExcel treeExcel : list) {
                //判断楼宇自控节点表中系统名称数据是否重复
                Boolean success = point.add(treeExcel.getSysName());
                if (!success) {
                    throw new Exception(treeExcel.getSysName() + ":系统名称在楼宇自控节点表中重复");
                }
            }


            for (DeviceTreeExcel treeExcel : list) {

                String BES_DDCNODE = String.valueOf(DeviceTreeConstants.BES_DDCNODE);
                switch (treeExcel.getDeviceNodeId()) {


                    case "23":
                    case "22":
                    case "20":
                    case "19": {//楼宇自控节点,虚点(无属性页面),总线,线路

                        dataReception = insertBesStruct(treeExcel, nodeTabName);

                        if (dataReception.getCode().equals("1")) {//新增成功

                            //添加进athena_bes_build_node楼控总线、虚点、线路表
                            DeviceTreeExcel deviceTreeExcel = new DeviceTreeExcel();
                            deviceTreeExcel = (DeviceTreeExcel) dataReception.getData();
                            BuildNode buildNode = new BuildNode();
                            buildNode.setTreeId(deviceTreeExcel.getDeviceTreeId());
                            buildNode.setNodeType(Integer.valueOf(treeExcel.getDeviceNodeId()));
                            buildNode.setSysName(treeExcel.getSysName());
                            buildNode.setNickName(treeExcel.getAlias());
                            buildNode.setCreateTime(DateUtils.getNowDate());


                            if (treeExcel.getDeviceNodeId().equals("22") || treeExcel.getDeviceNodeId().equals("20") || treeExcel.getDeviceNodeId().equals("23")) {
                                inportflag = excelTableImportMapper.add_buildNode(buildNode);
                                if (!inportflag) {
                                    throw new Exception(treeExcel.getSysName() + "添加总线/虚点总线失败");
                                }
                            } else if (treeExcel.getDeviceNodeId().equals("19")) {
                                ExcelUtil<LineExcel> util = new ExcelUtil<>(LineExcel.class);
                                lineList = util.importExcel("线路节点", fileUrl.getInputStream(), 0);// 导入excel,处理后生成list

                                Set<String> lineName = new HashSet<>();
                                List lists = new ArrayList();

                                for (LineExcel lineExcel : lineList) {

                                    //判断线路表中系统名称数据是否重复
                                    Boolean success = lineName.add(lineExcel.getSysName());
                                    if (!success) {
                                        throw new Exception(lineExcel.getSysName() + ":系统名称在线路节点表中重复");
                                    }

                                    lists.add(lineExcel.getSysName());
                                }
                                Boolean aa = lists.contains(treeExcel.getSysName());

                                if (!aa) {
                                    throw new Exception(treeExcel.getSysName() + "系统名称在线路节点表中不存在");
                                }

                                for (LineExcel lineExcel : lineList) {
                                    if (lineExcel.getSysName().equals(treeExcel.getSysName())) {
                                        buildNode.setPortNum(lineExcel.getLineAddr());
                                        continue;
                                    }
                                }

                                inportflag = excelTableImportMapper.add_buildNode(buildNode);
                                if (!inportflag) {
                                    throw new Exception(treeExcel.getSysName() + "添加线路失败");
                                }
                            }

                        } else {
                            return dataReception;
                        }
//                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
//
//                        }

                        break;
                    }
                    case "1": {//楼宇控制器节点

                        if (DDClist == null) {
                            ExcelUtil<ControllerExcel> util = new ExcelUtil<>(ControllerExcel.class);
                            DDClist = util.importExcel("楼宇控制器节点", fileUrl.getInputStream(), 0, false);// 导入excel,处理后生成list
                        }

                        dataReception = insertDDCByExcel(DDClist, treeExcel);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }

                        break;

                    }
                    case "18": {//模块

                        if (Modulelist == null) {
                            ExcelUtil<ModuleExcel> util = new ExcelUtil<>(ModuleExcel.class);
                            Modulelist = util.importExcel("模块节点", fileUrl.getInputStream(), 0);// 导入excel,处理后生成list
                        }

                        dataReception = insertModuleByExcel(Modulelist, treeExcel);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }

                        break;
                    }
                    case "DO":
                    case "DI":
                    case "AO":
                    case "AI": {

                        if (pointList == null) {
                            ExcelUtil<PointExcel> util = new ExcelUtil<>(PointExcel.class);
                            pointList = util.importExcel("点位节点", fileUrl.getInputStream(), 0);// 导入excel,处理后生成list
                        }

                        List lists = new ArrayList();

                        Set<String> point_DODIAOAI = new HashSet<>();
                        for (PointExcel pointExcel : pointList) {
                            //判断点位节点表中系统名称数据是否重复
                            Boolean success1 = point_DODIAOAI.add(pointExcel.getSysName());
                            if (!success1) {
                                throw new Exception(pointExcel.getSysName() + ":系统名称在点位节点表中重复");
                            }
                            lists.add(pointExcel.getSysName());
                        }
                        Boolean aa = lists.contains(treeExcel.getSysName());

                        if (!aa) {
                            throw new Exception(treeExcel.getSysName() + "系统名称在点位节点表中不存在");
                        }

                        dataReception = pointInsertByExcel(pointList, treeExcel);

                        if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                            return dataReception;
                        }
                        break;
                    }
                    default:
                        throw new Exception(treeExcel.getDeviceNodeId() + ":节点类型不存在");
                }
            }

        } catch (FileNotFoundException | NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return dataReception;
    }


    private List<ExcelError> nodeTable(List<DeviceTreeExcel> list) {
        List<ExcelError> excelErrors = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            boolean flag = true; //插入数据成功的标志
            DeviceTreeExcel treeExcel = list.get(i);
            String date = DateUtils.dateTimeNow();
            String errMsg = "";
            if (treeExcel.getDeviceNodeId() == null || treeExcel.getDeviceNodeId().equals("")) {
                errMsg = "节点类型为空";
                flag = false;
            } else if (treeExcel.getSysName() == null || treeExcel.getSysName().equals("")) {
                if ("".equals(errMsg)) {
                    errMsg = "系统名称为空";
                    flag = false;
                } else {
                    errMsg = errMsg + ",类型名称为空";
                    flag = false;
                }
            } else if (treeExcel.getPsysName() == null || treeExcel.getPsysName().equals("")) {

                if ("".equals(errMsg)) {
                    errMsg = "父节点系统名称为空";
                    flag = false;
                } else {
                    errMsg = errMsg + ",父节点系统名称为空";
                    flag = false;
                }
            } else if (treeExcel.getDeviceType() == null || treeExcel.getDeviceType().equals("")) {

                if ("".equals(errMsg)) {
                    errMsg = "所属系统为空";
                    flag = false;
                } else {
                    errMsg = errMsg + ",所属系统为空";
                    flag = false;
                }
            } else if (treeExcel.getPark() == null || treeExcel.getPark().equals("")) {

                if ("".equals(errMsg)) {
                    errMsg = "园区编号为空";
                    flag = false;
                } else {
                    errMsg = errMsg + ",园区编号为空";
                    flag = false;
                }
            } else if (treeExcel.getAlias() == null || treeExcel.getAlias().equals("")) {

                treeExcel.setAlias(treeExcel.getSysName());
            } else if (treeExcel.getDescription() == null || treeExcel.getDescription().equals("")) {

                treeExcel.setDescription(treeExcel.getAlias());

            }

            if (!flag) {
                ExcelError excelError = new ExcelError();
                excelError.setRow((i + 2) + "");
                excelError.setErrorMsg(errMsg);
                excelErrors.add(excelError);
            }
        }

        return excelErrors;
    }

    @Transactional(propagation = Propagation.NESTED)
    public DataReception insertBesStruct(DeviceTreeExcel treeExcel, String nodeTabName) {
        DataReception dataReception = new DataReception();
        boolean inportflag = false;

        try {

            //根据父节点名称查询数据库中是否有这个名称
//            DeviceTree psysName = deviceTreeCache.getSubordinateBySysName(treeExcel.getDeviceTreeFatherId());
            DeviceTree psysName = excelTableImportMapper.selectSbdyByPsysName(treeExcel.getPsysName());
            if (psysName == null) {
                throw new Exception(nodeTabName + "→" + treeExcel.getSysName() + ":父节点不存在");
            }

//            if (null != deviceTreeCache.getSubordinateBySysName(treeExcel.getSysName())) {
            if (null != excelTableImportMapper.selectSbdyByPsysName(treeExcel.getSysName())) {
                throw new Exception(nodeTabName + "→" + treeExcel.getSysName() + ":系统名称不能重复");
            }

//            String allpath = (String) psysName.get("F_ALLPATH");//父节点的全路径
//            allpath = allpath + ">" + sbdy.getfSysname();

            //获取父节点的id

            treeExcel.setDeviceTreeFatherId(String.valueOf(psysName.getDeviceTreeId()));
            treeExcel.setCreateTime(DateUtils.getNowDate());
            int add = excelTableImportMapper.add_deviceTree(treeExcel);


            if (add > 0) {
                dataReception.setMsg("导入成功！");
                dataReception.setCode("1");
                dataReception.setData(treeExcel);

            } else {
                throw new Exception("导入失败");
            }

        } catch (FileNotFoundException | NullPointerException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return dataReception;
    }

    /**
     * @Description: excel导入添加DDC控制器的信息
     * @auther: wanghongjie
     * @date: 17:13 2020/9/16
     * @param: [ddClist, sbdyStructs]
     * @return: com.core.common.ISSPReturnObject
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception insertDDCByExcel(List<ControllerExcel> ddClist, DeviceTreeExcel sbdyStructs) {
        DataReception returnObject = new DataReception();
        Boolean inportflag = null;

        String nodeTabName = "楼宇控制器节点表";
        try {

            List lists = new ArrayList();

            Set<String> point = new HashSet<>();
            Set<String> IPSet = new HashSet<>();
            for (ControllerExcel besDDCExcel : ddClist) {

                //判断控制器表中系统名称数据是否重复
                Boolean success = point.add(besDDCExcel.getSysName());
                if (!success) {
                    throw new Exception(besDDCExcel.getSysName() + ":系统名称在控制器节点表中重复");
                }
                //判断控制器表中ip数据是否重复
                Boolean successIp = IPSet.add(besDDCExcel.getIp());
                if (!successIp) {
                    throw new Exception(besDDCExcel.getIp() + ":ip在控制器节点表中重复");
                }

                lists.add(besDDCExcel.getSysName());
            }
            Boolean aa = lists.contains(sbdyStructs.getSysName());

            if (!aa) {
                throw new Exception(sbdyStructs.getSysName() + "系统名称在控制器节点表中不存在");
            }

            returnObject = insertBesStruct(sbdyStructs, nodeTabName);

            if (returnObject.getMsg() != null && returnObject.getMsg() != "导入成功！") {
                return returnObject;
            }

            DeviceTreeExcel deviceTreeExcel = new DeviceTreeExcel();
            deviceTreeExcel = (DeviceTreeExcel) returnObject.getData();


            for (ControllerExcel controllerExcel : ddClist) {

                String sysName = controllerExcel.getSysName();
                String zone = controllerExcel.getZone();
                String location = controllerExcel.getLocation();
                String ip = controllerExcel.getIp();
                String gateWay = controllerExcel.getGateWay();
                String mask = controllerExcel.getMask();
                String serverIp = controllerExcel.getServerIp();
                String serverPort = controllerExcel.getServerPort();
                String active = controllerExcel.getActive();

                if (!StringUtils.hasText(sysName)
                        || !StringUtils.hasText(zone)
                        || !StringUtils.hasText(location)
                        || !StringUtils.hasText(ip)
                        || !StringUtils.hasText(gateWay)
                        || !StringUtils.hasText(mask)
                        || !StringUtils.hasText(serverIp)
                        || !StringUtils.hasText(serverPort)
                        || !StringUtils.hasText(active)
                ) {
                    throw new Exception(sysName + "控制器节点表格内容不完整");
                }

                if (sbdyStructs.getSysName().equals(sysName)) {

                    //查询系统名称在设备配置表中是否存在
//                    if (null == deviceTreeCache.getSubordinateBySysName(sysName)) {
//                        throw new Exception(sysName + "系统名称在节点表中不存在");
//                    }
//                    if (null != excelTableImportMapper.selectPointBySysName(sysName)) {
//                        throw new Exception(sysName + "系统名称重复");
//                    }
//
//                    // 查询ip地址是否已经存在
                    if (controllerCache.getDdcByChannelId(ip) != null) {
                        throw new Exception(ip + "ip 地址重复");
                    }
                    ;
                    controllerExcel.setDescription(sbdyStructs.getDescription());
                    controllerExcel.setDeviceTreeId(deviceTreeExcel.getDeviceTreeId());
                    controllerExcel.setType(1);
                    controllerExcel.setAlias(sbdyStructs.getAlias());
                    controllerExcel.setSynchState(0);
                    controllerExcel.setErrorState(0);
                    controllerExcel.setOnlineState(0);
                    controllerExcel.setCreateBy(getUsername());
                    controllerExcel.setSavePeriod("5");
                    controllerExcel.setCurrentIp(ip);
                    controllerExcel.setCreateTime(DateUtils.getNowDate());
                    inportflag = excelTableImportMapper.add_controller(controllerExcel);


                }
            }

            if (inportflag) {
                returnObject.setMsg("导入成功！");
                returnObject.setCode("1");
            } else {
                throw new Exception("导入失败");
            }

        } catch (NullPointerException e) {
            returnObject.setCode("0");
            returnObject.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setCode("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return returnObject;
    }

    /**
     * @Description: 添加模块信息
     * @auther: wanghongjie
     * @date: 17:09 2020/9/16
     * @param: [modulelist, sbdyStructs]
     * @return: com.core.common.ISSPReturnObject
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception insertModuleByExcel(List<ModuleExcel> modulelist, DeviceTreeExcel deviceTreeExcel) {
        DataReception returnObject = new DataReception();
        Boolean inportflag = null;

        Integer addDefaultNodeCount = null;

        String nodeTabName = "楼宇模块节点表";
        try {
            JSONObject obj = new JSONObject();
            List lists = new ArrayList();

            Set<String> point = new HashSet<>();
            for (ModuleExcel besModuleExcel : modulelist) {

                //判断楼宇模块节点表中系统名称数据是否重复
                Boolean success = point.add(besModuleExcel.getSysName());
                if (!success) {
                    throw new Exception(besModuleExcel.getSysName() + ":系统名称在楼宇模块节点表中重复");
                }
                lists.add(besModuleExcel.getSysName());
            }
            Boolean aa = lists.contains(deviceTreeExcel.getSysName());

            if (!aa) {
                throw new Exception(deviceTreeExcel.getSysName() + "系统名称在模块节点表中不存在");
            }

            returnObject = insertBesStruct(deviceTreeExcel, nodeTabName);

            if (returnObject.getMsg() != null && returnObject.getMsg() != "导入成功！") {
                return returnObject;
            }

            DeviceTreeExcel treeExcel = new DeviceTreeExcel();
            treeExcel = (DeviceTreeExcel) returnObject.getData();


            for (ModuleExcel moduleExcel : modulelist) {

                moduleExcel.setDeviceTreeId(treeExcel.getDeviceTreeId());
                moduleExcel.setAlias(deviceTreeExcel.getAlias());

                String sysName = moduleExcel.getSysName();
                String moduleTypeId = moduleExcel.getModuleCode();
                String installAddress = moduleExcel.getInstallAddress();
                String slaveAddress = moduleExcel.getSlaveAddress();
                String active = moduleExcel.getActive();

                if (!StringUtils.hasText(sysName)
                        || !StringUtils.hasText(moduleTypeId)
                        || !StringUtils.hasText(installAddress)
                        || !StringUtils.hasText(slaveAddress)
                        || !StringUtils.hasText(active)
                ) {
                    throw new Exception(sysName + ":模块节点表格内容不完整");
                }

                if (deviceTreeExcel.getSysName().equals(sysName)) {

                    //如果模块的通信地址在当前ddc下有相同的地址,则在页面提示通信地址重复,添加失败
                    DeviceTree deviceTree = excelTableImportMapper.selectByPSysneme(treeExcel.getPsysName());

                    List<Module> addrList = excelTableImportMapper.addrListByPName(deviceTree.getDeviceTreeFatherId());

                    moduleExcel.setFlnId(deviceTree.getDeviceTreeId());

                    for (int i = 0; i < addrList.size(); i++) {
                        if (slaveAddress.equals(String.valueOf(addrList.get(i).getSlaveAddress()))) {
                            throw new Exception(slaveAddress + ":通信地址重复");
                        }
                    }

                    Map<String, ModuleType> moduleType = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_ModuleType);//查询当前模块的点集合

                    final boolean[] moduleTypeBoo = {false};
                    moduleType.forEach((key, value) -> {
                        if (value.getModuleCode().equals(moduleTypeId)) {
                            moduleExcel.setModelTypeCode(String.valueOf(value.getTypeCode()));
                            moduleTypeBoo[0] = true;
                        }
                    });

                    if (!moduleTypeBoo[0]) {
                        throw new Exception(moduleTypeId + ":请配置这个模块型号");
                    }

                    //查询当前模块的控制器id
                    Controller controller = excelTableImportMapper.queryControllerMapByModelPSysName(deviceTreeExcel.getPsysName());

                    if (deviceTreeExcel.getDeviceType().equals("1")) {//楼控
                        moduleExcel.setType(0);
                    } else {//照明
                        moduleExcel.setType(1);
                    }
                    moduleExcel.setControllerId(controller.getId());
                    moduleExcel.setDescription(deviceTreeExcel.getDescription());
                    moduleExcel.setSynchState(0);
                    moduleExcel.setOnlineState(0);
                    moduleExcel.setNodeType("18");
                    moduleExcel.setCreateTime(DateUtils.getNowDate());
                    //先添加模块信息
                    inportflag = excelTableImportMapper.add_Module(moduleExcel);
                    if (!inportflag) {
                        throw new Exception(sysName + "模块添加失败");
                    }

                    //根据模块型号获取当前模块的点集合
                    List<Map<String, Object>> modulePoint = excelTableImportMapper.selectModulePoint(moduleExcel);

                    if (modulePoint == null || modulePoint.size() == 0 || modulePoint.size() > 1) {
                        throw new Exception(moduleTypeId + ":请配置这个模块型号");
                    }
                    String pointSet = (String) modulePoint.get(0).get("pointSet");
                    moduleExcel.setPointSet(pointSet);

                    addDefaultNodeCount = addDefaultNodes(deviceTreeExcel, moduleExcel);//自动添加模块下的点位


//                    if (inportflag) {
//                        //20210802  当excel导入插座和低档温控器时,只需要添加值模块,下面的点位自动生成
//                        if ("WKQ_MOD".equals(module_type) || "计量插座".equals(module_type) || "新产业园人体光照度".equals(module_type)) {
//
//                            returnObject = insertPointAutomatic(besModuleExcel, module_type);
//
//                            if (returnObject.getMsg() != null && returnObject.getMsg() != "导入成功！") {
//                                return returnObject;
//                            }
//                        }
//                    }
                }
            }

            if (addDefaultNodeCount > 1) {
                returnObject.setMsg("导入成功！");
                returnObject.setCode("1");
            } else {
                throw new Exception("导入失败");
            }
        } catch (NullPointerException e) {
            returnObject.setCode("0");
            returnObject.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            returnObject.setCode("0");
            returnObject.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return returnObject;
    }

    /**
     * 添加默认节点(根据模块类型)
     *
     * @param moduleExcel
     * @return
     */
    private int addDefaultNodes(DeviceTreeExcel treeExcel, ModuleExcel moduleExcel) {

        //获取模块点类型信息

//        List<ModulePoint> modulePointTypeList = excelTableImportMapper.getModulePointTypeInfo();//获取模块点类型信息
//        Map<Long, String> mPointTypeMap = new HashMap<>();//(key:ID value:模块点类型)
//        for (ModulePoint modulePoint : modulePointTypeList) {
//            mPointTypeMap.put(modulePoint.getId(), modulePoint.getModulePoint());
//        }
//        List<BESEpModuleTypeRlgl> epNoduleTypeList = besSbdyMapper.getEpModuleTypeRlglInfo();//获取设备树节点和模块点类型信息
//        Map<String, String > epModuleTypeRlglMap = new HashMap<>();//(key:ID value:设备树节点类型)
//        for(BESEpModuleTypeRlgl epModuleTypeRlgl : epNoduleTypeList){
//            epModuleTypeRlglMap.put(epModuleTypeRlgl.getfModulepointId(), epModuleTypeRlgl.getfEpTreenodeType());
//        }

        String pFsysName = treeExcel.getSysName();
        char[] nodeTypes = moduleExcel.getPointSet().toCharArray();
        List<DeviceTreeExcel> nodeList = new ArrayList<>();
        List<PointExcel> nodePointList = new ArrayList<>();

        //取模块id，加1为第一个逻辑点的id
//        int sbid = obj.getInteger("sbid") + 1;
        for (int i = 0; i < nodeTypes.length; i++) {
            DeviceTreeExcel sbPzStruct = new DeviceTreeExcel();


            if (String.valueOf(nodeTypes[i]).equals("1")) {//AI
                sbPzStruct.setDeviceNodeId(String.valueOf(DeviceTreeConstants.BES_AI));
            } else if (String.valueOf(nodeTypes[i]).equals("2")) {//AO
                sbPzStruct.setDeviceNodeId(String.valueOf(DeviceTreeConstants.BES_AO));
            } else if (String.valueOf(nodeTypes[i]).equals("3")) {//DI
                sbPzStruct.setDeviceNodeId(String.valueOf(DeviceTreeConstants.BES_DI));
            } else if (String.valueOf(nodeTypes[i]).equals("4")) {//DO
                sbPzStruct.setDeviceNodeId(String.valueOf(DeviceTreeConstants.BES_DO));
            } else if (String.valueOf(nodeTypes[i]).equals("5")) {//UI
                sbPzStruct.setDeviceNodeId(String.valueOf(DeviceTreeConstants.BES_UI));
            } else if (String.valueOf(nodeTypes[i]).equals("6")) {//UX
                sbPzStruct.setDeviceNodeId(String.valueOf(DeviceTreeConstants.BES_UX));
            }


            sbPzStruct.setSysName(pFsysName + "0" + String.valueOf(i));
            sbPzStruct.setDeviceTreeFatherId(String.valueOf(moduleExcel.getDeviceTreeId()));
            sbPzStruct.setDeviceType(treeExcel.getDeviceType());
            sbPzStruct.setPark(treeExcel.getPark());
            sbPzStruct.setCreateTime(DateUtils.getNowDate());
            nodeList.add(sbPzStruct);
        }


        int insertCount = excelTableImportMapper.batchInsert(nodeList);

        if (insertCount > 0) {

            //获取当前模块下所有的点位
            List<DeviceTree> deviceTreeList = excelTableImportMapper.selectPointByPSysneme(moduleExcel.getDeviceTreeId());

            deviceTreeList.sort((o1, o2) -> String.valueOf(o1.getDeviceTreeId()).compareTo(String.valueOf(o2.getDeviceTreeId())));


            int equipmentId = 0;
            //添加到点位表

            //查询当前点位最大设备ID
            List<Point> pointList = excelTableImportMapper.getPointList(moduleExcel.getControllerId());
            if (pointList != null && pointList.size() > 0) {

                pointList.sort((o1, o2) -> String.valueOf(o2.getTreeId()).compareTo(String.valueOf(o1.getTreeId())));
                equipmentId = pointList.get(0).getEquipmentId();
            }

            for (int i = 0; i < nodeTypes.length; i++) {
                PointExcel pointExcel = new PointExcel();

                equipmentId = equipmentId + 1;

                pointExcel.setTreeId(deviceTreeList.get(i).getDeviceTreeId());
                pointExcel.setEquipmentId(equipmentId);
                pointExcel.setSysName(deviceTreeList.get(i).getSysName());
                pointExcel.setNodeType(String.valueOf(deviceTreeList.get(i).getDeviceNodeId()));
                pointExcel.setModuleId(moduleExcel.getDeviceTreeId());
                pointExcel.setChannelIndex(String.valueOf(i));
                pointExcel.setControllerId(moduleExcel.getControllerId());
                pointExcel.setCreateTime(DateUtils.getNowDate());
                nodePointList.add(pointExcel);

            }

        }
        int insertPointCount = excelTableImportMapper.batchInsertPoint(nodePointList);


        return insertPointCount;
    }

    /**
     * @Description: Excel导入点位节点
     * @auther: wanghongjie
     * @date: 9:17 2022/11/14
     * @param: [pointList, treeExcel]
     * @return: com.zc.common.core.model.DataReception
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception pointInsertByExcel(List<PointExcel> pointList, DeviceTreeExcel treeExcel) {
        DataReception dataReception = new DataReception();

        try {
            for (PointExcel point : pointList) {
                if (point.getSysName() == null) {
                    throw new Exception(point.getSysName() + "点位节点表的节点类型不存在");
                }
                if (point.getSysName().equals(treeExcel.getSysName())) {
                    //查询该节点的父节点的类型
                    String pSysNameType = excelTableImportMapper.selectPSysNameType(treeExcel.getPsysName());

                    if (pSysNameType == null) {
                        throw new Exception(treeExcel.getSysName() + "节点表的父节点输入错误");
                    }

                    if (pSysNameType.equals("18")) {//模块节点

                        if (!point.getNodeType().equals("DO") && !point.getNodeType().equals("DI")
                                && !point.getNodeType().equals("AO") && !point.getNodeType().equals("AI")) {

                            throw new Exception(point.getSysName() + "点位节点表的节点类型不存在");
                        }
                        if (point.getNodeType().equals("DO") || point.getNodeType().equals("DI")) {

                            dataReception = insertDO_DIPoint(point, treeExcel);

                            if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                                return dataReception;
                            }
                            if (dataReception.getMsg() == "导入成功！") {
                                return dataReception;
                            }


                        } else if (point.getNodeType().equals("AO") || point.getNodeType().equals("AI")) {

                            dataReception = insertAO_AIPoint(point, treeExcel);
                            if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                                return dataReception;
                            }
                            if (dataReception.getMsg() == "导入成功！") {
                                return dataReception;
                            }

                        }
                    } else if (pSysNameType.equals("22")) {//虚点无属性页面节点

                        if (!point.getNodeType().equals("DO") && !point.getNodeType().equals("DI")
                                && !point.getNodeType().equals("AO") && !point.getNodeType().equals("AI")) {

                            throw new Exception(point.getSysName() + "点位节点表的节点类型不存在");
                        }

                        if (point.getNodeType().equals("DO") || point.getNodeType().equals("DI")) {

                            dataReception = insertDO_DIVPoint(point, treeExcel);
                            if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                                return dataReception;
                            }
                            if (dataReception.getMsg() == "导入成功！") {
                                return dataReception;
                            }

                        } else if (point.getNodeType().equals("AO") || point.getNodeType().equals("AI")) {

                            dataReception = insertAO_AIVPoint(point, treeExcel);
                            if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                                return dataReception;
                            }
                            if (dataReception.getMsg() == "导入成功！") {
                                return dataReception;
                            }
                        }
                    }
                }

            }
        } catch (NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return dataReception;
    }


    /**
     * @Description:Excel DO, DI点位新增
     * @auther: wanghongjie
     * @date: 17:11 2022/11/15
     * @param: [point, treeExcel]
     * @return: com.zc.common.core.model.DataReception
     */
    @Transactional(propagation = Propagation.NESTED)
    public DataReception insertDO_DIPoint(PointExcel point, DeviceTreeExcel treeExcel) {
        DataReception dataReception = new DataReception();
        Integer idBySbdyStruct;
        String allpath;
        String sys_name;
        String type = null;
        String tabName = null;
        Boolean Has_it_been_added = true;//此点位是否已添加,默认已添加
        try {

            String alarm_type = null;
            String close_state = null;
            String alarm_priority = null;
            String fault_state = null;

            String sys_name_old = point.getSysName();
            String nick_name = treeExcel.getAlias();
            String enabled = point.getEnabled();
            String channel_index = point.getChannelIndex();
            String reversed = point.getReversed();

            String work_mode = point.getWorkMode();
            String init_val = point.getInitVal();
            String alarm_enable = point.getAlarmEnable();

            if (!alarm_enable.equals("0")) {
                alarm_type = point.getAlarmType();
                close_state = point.getCloseState();
                alarm_priority = point.getAlarmPriority();
                fault_state = point.getFaultState();

                if (!StringUtils.hasText(alarm_type)
                        || !StringUtils.hasText(close_state)
                        || !StringUtils.hasText(alarm_priority)
                        || !StringUtils.hasText(fault_state)) {
                    throw new Exception(sys_name_old + ":点位节点表格内容不完整");
                }
            }

            if (!StringUtils.hasText(sys_name_old)
                    || !StringUtils.hasText(enabled)
                    || !StringUtils.hasText(channel_index)
                    || !StringUtils.hasText(reversed)
                    || !StringUtils.hasText(work_mode)
                    || !StringUtils.hasText(init_val)
                    || !StringUtils.hasText(alarm_enable)

            ) {
                throw new Exception(sys_name_old + ":点位节点表格内容不完整");
            }

            //修改自动生成的点位的系统名称以及别名和点类型,首先查询当前模块下所有的点位
            DeviceTree deviceTree = excelTableImportMapper.selectSbdyByPsysName(treeExcel.getPsysName());//根据父节点名称查询父节点的id
            List<DeviceTree> module_pointList = excelTableImportMapper.selectModule_pointList(deviceTree.getDeviceTreeId());

            String name = module_pointList.get(Integer.parseInt(channel_index)).getSysName();

            if (name.contains(treeExcel.getPsysName())) {

                if (Integer.valueOf(name.replace(treeExcel.getPsysName(), "")).equals(Integer.valueOf(channel_index))) {

                    Has_it_been_added = false;//此点位未添加

                    //修改设备配置的点位信息,首先查询当前点位在设备配置表的信息,目的是查出自增id
                    DeviceTree pointMap = excelTableImportMapper.selectPointBySysName(name);
                    idBySbdyStruct = pointMap.getDeviceTreeId();

                    //判断当前点位的通道索引是否符合模块的模块型号,首先查出模块的点集合
                    String F_POINT_TYPE_CL = excelTableImportMapper.selectF_POINT_TYPE_CL(deviceTree.getDeviceTreeId());

                    final String substring = F_POINT_TYPE_CL.substring(Integer.parseInt(channel_index), Integer.parseInt(channel_index) + 1);
                    if (treeExcel.getDeviceNodeId().equals("DO")) {
                        if (!substring.equals("4")) {//DO点
                            throw new Exception(sys_name_old + "点位的通道索引不符合模块的模块型号");
                        }
                    }
                    if (treeExcel.getDeviceNodeId().equals("DI")) {
                        if (!substring.equals("3") && !substring.equals("5")) {//UI点,DI点
                            throw new Exception(sys_name_old + "点位的通道索引不符合模块的模块型号");
                        }
                    }

                    if (treeExcel.getDeviceNodeId().equals("DO")) {
                        type = "11";

                    } else if (treeExcel.getDeviceNodeId().equals("DI")) {

                        type = "12";

                        String sourced = point.getSourced();
                        if (!StringUtils.hasText(sourced)) {
                            throw new Exception(sys_name_old + "点位节点表格内容不完整");
                        }
                    }

                    Boolean updateStruct = excelTableImportMapper.updateStructPoint(idBySbdyStruct, sys_name_old, type);

                    if (updateStruct) {

                        point.setDescription(treeExcel.getDescription());
                        point.setNickName(treeExcel.getAlias());
                        point.setTreeId(idBySbdyStruct);

                        point.setNodeTypeId(type);
                        //修改点位信息到相应的点位表中
                        Boolean insertPointMapToNodeTable = excelTableImportMapper.updatePointMap(point);

                        if (insertPointMapToNodeTable) {
                            dataReception.setMsg("导入成功！");
                            dataReception.setCode("1");
                        } else {
                            throw new Exception("导入失败");
                        }
                    }
                }
            }

            if (Has_it_been_added) {
                throw new Exception(sys_name_old + ":此点位已添加");
            }
        } catch (NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return dataReception;

    }

    /**
     * @Description: Excel AO,AI点位新增
     * @auther: wanghongjie
     * @date: 10:06 2022/11/18
     * @param: [point, treeExcel]
     * @return: com.zc.common.core.model.DataReception
     */
    private DataReception insertAO_AIPoint(PointExcel point, DeviceTreeExcel treeExcel) {
        DataReception dataReception = new DataReception();
        Integer idBySbdyStruct;
        String allpath;
        String type = null;
        String tabName = null;
        String energystatics = null;
        Boolean Has_it_been_added = true;//此点位是否已添加,默认已添加

        try {
            String alarm_type;
            String close_state;
            String alarm_priority;
            String high_limit;
            String low_limit;


            String sys_name = point.getSysName();
            String enabled = point.getEnabled();
            String channel_index = point.getChannelIndex();
            String work_mode = point.getWorkMode();
            String reversed = point.getReversed();
            String init_val = point.getInitVal();
            String engineer_unit = point.getEngineerUnit();
            String validInputType = point.getValidInputType();
            String max_val = point.getMaxVal();
            String min_val = point.getMinVal();
            String accuracy = point.getAccuracy();
            String alarm_enable = point.getAlarmEnable();

            if (!alarm_enable.equals("0")) {
                alarm_type = point.getAlarmType();
                close_state = point.getCloseState();
                alarm_priority = point.getAlarmPriority();
                high_limit = point.getHighLimit();
                low_limit = point.getLowLimit();

                if (!StringUtils.hasText(alarm_type)
                        || !StringUtils.hasText(close_state)
                        || !StringUtils.hasText(alarm_priority)
                        || !StringUtils.hasText(high_limit)
                        || !StringUtils.hasText(low_limit)) {
                    throw new Exception(sys_name + ":点位节点表格内容不完整");
                }
            }

            if (!StringUtils.hasText(sys_name)
                    || !StringUtils.hasText(enabled)
                    || !StringUtils.hasText(channel_index)
                    || !StringUtils.hasText(reversed)
                    || !StringUtils.hasText(engineer_unit)
                    || !StringUtils.hasText(work_mode)
                    || !StringUtils.hasText(init_val)
                    || !StringUtils.hasText(alarm_enable)
                    || !StringUtils.hasText(validInputType)
                    || !StringUtils.hasText(max_val)
                    || !StringUtils.hasText(min_val)
                    || !StringUtils.hasText(accuracy)
            ) {
                throw new Exception(sys_name + ":点位节点表格内容不完整");
            }

            //修改自动生成的点位的系统名称以及别名和点类型,首先查询当前模块下所有的点位
            DeviceTree deviceTree = excelTableImportMapper.selectSbdyByPsysName(treeExcel.getPsysName());//根据父节点名称查询父节点的id
            List<DeviceTree> module_pointList = excelTableImportMapper.selectModule_pointList(deviceTree.getDeviceTreeId());

            String name = module_pointList.get(Integer.parseInt(channel_index)).getSysName();

            if (name.contains(treeExcel.getPsysName())) {

                if (Integer.valueOf(name.replace(treeExcel.getPsysName(), "")).equals(Integer.valueOf(channel_index))) {

                    Has_it_been_added = false;//此点位未添加


                    //修改设备配置的点位信息,首先查询当前点位在设备配置表的信息,目的是查出自增id
                    DeviceTree pointMap = excelTableImportMapper.selectPointBySysName(name);
                    idBySbdyStruct = pointMap.getDeviceTreeId();

                    //判断当前点位的通道索引是否符合模块的模块型号,首先查出模块的点集合
                    String F_POINT_TYPE_CL = excelTableImportMapper.selectF_POINT_TYPE_CL(deviceTree.getDeviceTreeId());

                    final String substring = F_POINT_TYPE_CL.substring(Integer.parseInt(channel_index), Integer.parseInt(channel_index) + 1);
                    if (treeExcel.getDeviceNodeId().equals("AO")) {
                        if (!substring.equals("2")) {//AO点
                            throw new Exception(sys_name + "点位的通道索引不符合模块的模块型号");
                        }
                    }
                    if (treeExcel.getDeviceNodeId().equals("AI")) {
                        if (!substring.equals("1") && !substring.equals("5")) {//UI点,AI点
                            throw new Exception(sys_name + "点位的通道索引不符合模块的模块型号");
                        }
                    }

                    if (treeExcel.getDeviceNodeId().equals("AO")) {
                        type = "14";

                    } else if (treeExcel.getDeviceNodeId().equals("AI")) {

                        type = "13";
                    }


                    Boolean updateStruct = excelTableImportMapper.updateStructPoint(idBySbdyStruct, sys_name, type);

                    if (updateStruct) {

                        point.setDescription(treeExcel.getDescription());
                        point.setNickName(treeExcel.getAlias());
                        point.setTreeId(idBySbdyStruct);

                        point.setNodeTypeId(type);
                        //修改点位信息到相应的点位表中
                        Boolean insertPointMapToNodeTable = excelTableImportMapper.updatePointMap(point);

                        if (insertPointMapToNodeTable) {
                            dataReception.setMsg("导入成功！");
                            dataReception.setCode("1");
                        } else {
                            throw new Exception("导入失败");
                        }
                    }
                }
            }

            if (Has_it_been_added) {
                throw new Exception(sys_name + ":此点位已添加");
            }

        } catch (FileNotFoundException | NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return dataReception;

    }

    /**
     * @Description: Excel 虚点DO,DI点位新增
     * @auther: wanghongjie
     * @date: 10:27 2022/11/18
     * @param: [point, treeExcel]
     * @return: com.zc.common.core.model.DataReception
     */
    private DataReception insertDO_DIVPoint(PointExcel point, DeviceTreeExcel deviceTreeExcel) {
        DataReception dataReception = new DataReception();
        String nodeTabName = "点位节点表";

        try {
            String alarm_type;
            String close_state;
            String alarm_priority;
            String fault_state;

            String sys_name = point.getSysName();
            String enabled = point.getEnabled();
            String init_val = point.getInitVal();
            String alarm_enable = point.getAlarmEnable();

            if (!alarm_enable.equals("0")) {
                alarm_type = point.getAlarmType();
                close_state = point.getCloseState();
                alarm_priority = point.getAlarmPriority();
                fault_state = point.getFaultState();

                if (!StringUtils.hasText(alarm_type)
                        || !StringUtils.hasText(close_state)
                        || !StringUtils.hasText(alarm_priority)
                        || !StringUtils.hasText(fault_state)) {
                    throw new Exception(sys_name + ":点位节点表格内容不完整");
                }
            }


            if (!StringUtils.hasText(sys_name)
                    || !StringUtils.hasText(enabled)
                    || !StringUtils.hasText(init_val)
                    || !StringUtils.hasText(alarm_enable)
            ) {
                throw new Exception(sys_name + ":点位节点表格内容不完整");
            }

            if (deviceTreeExcel.getDeviceNodeId().equals("DO") || deviceTreeExcel.getDeviceNodeId().equals("DI")) {
                deviceTreeExcel.setDeviceNodeId("21");
            }
            dataReception = insertBesStruct(deviceTreeExcel, nodeTabName);
            if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                return dataReception;
            }

            DeviceTreeExcel treeExcel = new DeviceTreeExcel();
            treeExcel = (DeviceTreeExcel) dataReception.getData();


            //查询当前点位在设备配置表的信息,目的是查出F_ID


            point.setTreeId(treeExcel.getDeviceTreeId());


            if (point.getNodeType().equals("DO")) {
                point.setVpointType("3");
            } else if (point.getNodeType().equals("DI")) {
                point.setVpointType("2");
            }


            //查询当前模块的控制器id
            Controller controller = excelTableImportMapper.queryControllerMapByModelPSysName(deviceTreeExcel.getPsysName());
            if (controller == null) {
                throw new Exception(sys_name + "添加点位时获取不到当前控制器的信息");
            }
            point.setDescription(deviceTreeExcel.getDescription());
            point.setControllerId(controller.getId());
            point.setCreateTime(DateUtils.getNowDate());


            //查询当前点位最大设备ID
            List<Point> pointList = excelTableImportMapper.getPointList(controller.getId());

            int equipmentId = 0;

            if (pointList != null && pointList.size() > 0) {

                pointList.sort((o1, o2) -> String.valueOf(o1.getTreeId()).compareTo(String.valueOf(o2.getTreeId())));
                equipmentId = pointList.get(0).getEquipmentId();
            }
            equipmentId = equipmentId + 1;
            point.setEquipmentId(equipmentId);

            //添加虚点点位信息到相应的点位表中
            Boolean insertPointMapToNodeTable = excelTableImportMapper.addPointMap(point);
            if (insertPointMapToNodeTable) {
                dataReception.setMsg("导入成功！");
                dataReception.setCode("1");
            } else {
                throw new Exception("导入失败");
            }


        } catch (FileNotFoundException | NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return dataReception;
    }

    /**
     * @Description:Excel 虚点AO, AI点位新增
     * @auther: wanghongjie
     * @date: 10:26 2022/11/18
     * @param: [point, treeExcel]
     * @return: com.zc.common.core.model.DataReception
     */
    private DataReception insertAO_AIVPoint(PointExcel point, DeviceTreeExcel deviceTreeExcel) {
        DataReception dataReception = new DataReception();
        String nodeTabName = "点位节点表";
        try {
            String alarm_type;
            String close_state;
            String alarm_priority;
            String high_limit;
            String low_limit;

            String sys_name = point.getSysName();
            String enabled = point.getEnabled();
            String init_val = point.getInitVal();
            String alarm_enable = point.getAlarmEnable();
            String engineer_unit = point.getEngineerUnit();
            String accuracy = point.getAccuracy();

            if (!alarm_enable.equals("0")) {
                alarm_type = point.getAlarmType();
                close_state = point.getCloseState();
                alarm_priority = point.getAlarmPriority();
                high_limit = point.getHighLimit();
                low_limit = point.getLowLimit();

                if (!StringUtils.hasText(alarm_type)
                        || !StringUtils.hasText(close_state)
                        || !StringUtils.hasText(alarm_priority)) {
                    throw new Exception(sys_name + ":点位节点表格内容不完整");
                }
            }

            if (!StringUtils.hasText(sys_name)
                    || !StringUtils.hasText(enabled)
                    || !StringUtils.hasText(init_val)
                    || !StringUtils.hasText(alarm_enable)
                    || !StringUtils.hasText(engineer_unit)
                    || !StringUtils.hasText(accuracy)
            ) {
                throw new Exception(sys_name + ":点位节点表格内容不完整");
            }

            if (deviceTreeExcel.getDeviceNodeId().equals("AO") || deviceTreeExcel.getDeviceNodeId().equals("AI")) {
                deviceTreeExcel.setDeviceNodeId("21");
            }
            dataReception = insertBesStruct(deviceTreeExcel, nodeTabName);

            if (dataReception.getMsg() != null && dataReception.getMsg() != "导入成功！") {
                return dataReception;
            }

            //查询当前点位在设备配置表的信息,目的是查出F_ID

            DeviceTreeExcel treeExcel = new DeviceTreeExcel();
            treeExcel = (DeviceTreeExcel) dataReception.getData();


            //查询当前点位在设备配置表的信息,目的是查出F_ID


            point.setTreeId(treeExcel.getDeviceTreeId());

            if (point.getNodeType().equals("AO")) {
                point.setVpointType("1");
            } else if (point.getNodeType().equals("AI")) {
                point.setVpointType("0");
            }


            //查询当前模块的控制器id
            Controller controller = excelTableImportMapper.queryControllerMapByModelPSysName(deviceTreeExcel.getPsysName());
            if (controller == null) {
                throw new Exception(sys_name + "添加点位时获取不到当前控制器的信息");
            }
            point.setDescription(deviceTreeExcel.getDescription());
            point.setControllerId(controller.getId());
            point.setCreateTime(DateUtils.getNowDate());

            //查询当前点位最大设备ID
            List<Point> pointList = excelTableImportMapper.getPointList(controller.getId());

            int equipmentId = 0;

            if (pointList != null && pointList.size() > 0) {

                pointList.sort((o1, o2) -> String.valueOf(o1.getTreeId()).compareTo(String.valueOf(o2.getTreeId())));
                equipmentId = pointList.get(0).getEquipmentId();
            }
            equipmentId = equipmentId + 1;
            point.setEquipmentId(equipmentId);
            //添加虚点点位信息到相应的点位表中
            Boolean insertPointMapToNodeTable = excelTableImportMapper.addPointMap(point);
            if (insertPointMapToNodeTable) {
                dataReception.setMsg("导入成功！");
                dataReception.setCode("1");
            } else {
                throw new Exception("导入失败");
            }


        } catch (FileNotFoundException | NullPointerException e) {
            dataReception.setCode("0");
            dataReception.setMsg("模板错误！");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            dataReception.setCode("0");
            dataReception.setMsg(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return dataReception;
    }


}
