package com.zc.connect.HTTP;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.uuid.UUIDUtil;
import com.ruoyi.quartz.domain.SysJob;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.HttpCommunicationConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.ThreadPool.ThreadPool;
import com.zc.efounder.JEnterprise.domain.baseData.*;
import com.zc.efounder.JEnterprise.mapper.baseData.EquipmentMapper;
import com.zc.efounder.JEnterprise.mapper.baseData.ProductMapper;
import com.zc.efounder.JEnterprise.mapper.commhandler.JobManagerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Author:gaojikun
 * @Date:2023-03-17 12:00
 * @Description:HTTP链接通讯
 */
@RestController
@RequestMapping("/HTTPCommunication")
public class HTTPCommunication extends BaseController {

    /**
     * log
     */
    private static final Logger log = LoggerFactory.getLogger(HTTPCommunication.class);

    /**
     * redis
     */
    private final RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);

    /**
     * 设备Mapper
     */
    private final EquipmentMapper equipmentMapper = ApplicationContextProvider.getBean(EquipmentMapper.class);

    /**
     * 产品Mapper
     */
    private final ProductMapper productMapper = ApplicationContextProvider.getBean(ProductMapper.class);

    /**
     * 电表数据存储Mapper
     */
    private final JobManagerMapper besJobManagerMapper = ApplicationContextProvider.getBean(JobManagerMapper.class);


    @PostConstruct
    public void timerHttpPolling() {
        //定时器,等待设备缓存更新完毕
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                SysJob sysJob = new SysJob();
                httpPolling(sysJob);
            }
        }, 1000 * HttpCommunicationConstants.TIMER_INTEGER);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-03-17 15:15
     * @Description:项目启动后执行设备轮询
     */
    @GetMapping("/HttpPolling")
    public void httpPolling(SysJob job) {
        //获取当前时间
        Date nowDate = DateUtils.getNowDate();

        //获取所有通讯为http的设备
        List<Equipment> equipmentList = new ArrayList<>();
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
        for (Object j : values) {
            Equipment e = (Equipment) j;
            if ("http".equals(e.getCommunication())) {
                equipmentList.add(e);
            }
        }

        if (equipmentList.size() > 0) {
            //用线程池来提高for循环的效率
            ThreadPool.executor.execute(() -> {
                for (Equipment equipment : equipmentList) {
                    try {
                        //编码
                        String encoded = "UTF-8";
                        //常量定义方法名
                        String methodsNameGet = HttpCommunicationConstants.GET_GET_EQUIPMENT_INFO;
                        String methodsNamePost = HttpCommunicationConstants.POST_GET_EQUIPMENT_INFO;

                        //ip
                        String ip;
                        //端口
                        String portNum;
                        if (!StringUtils.isEmpty(equipment.getIpAddress()) || equipment.getpId() != null) {
                            Equipment equipmentParent;
                            if (StringUtils.isEmpty(equipment.getIpAddress())) {
                                equipmentParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
                            } else {
                                equipmentParent = equipment;
                            }

                            ip = equipmentParent.getIpAddress();
                            portNum = equipmentParent.getPortNum();

                            if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(portNum)) {
//                                log.error("HTTP轮询设备，IP/端口为空：" + equipment.getCode() + "/" + equipment.getName());
                                throw new Exception();
                            }
                            //地址
                            String url = "http://" + ip + ":" + portNum;


                            //get请求
                            String param = "id=" + equipment.getId();
                            String getUrl = url + methodsNameGet;
                            //返回信息
                            String resultString;
                            //发送HTTP请求
                            resultString = HttpUtils.sendGet(getUrl, param, encoded);
                            //逻辑处理
                            boolean getBoolean = updateEquipmentInfo(resultString, equipment, ip, portNum, encoded, nowDate);
                            if (!getBoolean) {
//                                log.error("HTTP轮询设备，轮询设备GET请求时出现问题：" + equipment.getCode() + "/" + equipment.getName());
                            }

//                            //post请求
//                            String postUrl = url + methodsNamePost;
//                            //发送HTTP请求
//                            resultString = HttpUtils.sendPost(postUrl, param);
//                            //逻辑处理
//                            boolean postBoolean = updateEquipmentInfo(resultString, equipment, ip, portNum, encoded);
//                            if (!postBoolean) {
//                                log.error("HTTP轮询设备，轮询设备POST请求时出现问题：" + equipment.getCode() + "/" + equipment.getName());
//                            }
                        } else {
//                            log.error("HTTP轮询设备，IP/端口为空：" + equipment.getCode() + "/" + equipment.getName());
                            throw new Exception();
                        }
                    } catch (Exception e) {
//                        log.error("HTTP轮询设备，发生异常：" + equipment.getCode() + "/" + equipment.getName(), e);
                        //查询所有设备信息
                        List<Equipment> equipmentsRedis = equipmentMapper.selectAthenaBesEquipmentList(equipment);
                        equipment = equipmentsRedis.get(0);
                        equipment.setState("0");
                        //添加缓存
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getId(), equipment);
                    }
                }
            });
            //全局线程池
//            ThreadPool.executor.shutdown();
        } else {
            log.error("HTTP轮询设备，无http通讯设备");
        }
    }


    /**
     * 获取到的信息去修改缓存及数据库
     */
    public boolean updateEquipmentInfo(String info, Equipment equipment, String ip, String portNum, String encoded, Date nowDate) {
        try {


//        Equipment returnEquipment = JSON.parseObject(info, Equipment.class);
//        System.out.println("返回信息————————————》"+returnEquipment);
            //可以链接设备，修改状态为已上线
//            equipment.setState("1");

            //修改数据库
            boolean isUpdate = false;
//            boolean isUpdate = equipmentMapper.updateAthenaBesEquipment(equipment);
//            if (isUpdate) {
            //查询所有设备信息
            List<Equipment> equipmentsRedis = equipmentMapper.selectAthenaBesEquipmentList(equipment);
            equipment = equipmentsRedis.get(0);
            equipment.setState("1");
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getId(), equipment);
//            }

            //设备对应的产品
            Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, equipment.getProductId());
            //产品对应的数据项
            List<ProductItemData> productItemDataList = new ArrayList<>();
            Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
            for (Object j : values) {
                ProductItemData productItemData = (ProductItemData) j;
                if (productItemData.getProductId().equals(product.getId())) {
                    productItemDataList.add(productItemData);
                }
            }

            //产品有对应的数据项
            if (productItemDataList.size() > 0) {
                //电表数据项
                List<Map<String, Object>> data = new ArrayList<>();
                //非电表数据项
                List<HistoryItemData> dataOther = new ArrayList<>();
                for (ProductItemData productItemDataF : productItemDataList) {

                    //是否有参数,组装数据
                    List<ProductItemData> allProductItemDataList = new ArrayList<ProductItemData>(){{add(productItemDataF);}};
                    if(productItemDataF.getHighDetail() != null && productItemDataF.getHighDetail().size() > 0){
                        List<ProductItemData> highDetail = productItemDataF.getHighDetail();
                        allProductItemDataList.addAll(highDetail);
                    }
                    if(productItemDataF.getLowDetail() != null && productItemDataF.getLowDetail().size() > 0){
                        List<ProductItemData> lowDetail = productItemDataF.getLowDetail();
                        allProductItemDataList.addAll(lowDetail);
                    }
                    if(productItemDataF.getStructDetail() != null && productItemDataF.getStructDetail().size() > 0){
                        List<ProductItemData> structDetail = productItemDataF.getStructDetail();
                        allProductItemDataList.addAll(structDetail);
                    }

                    for(ProductItemData productItemData : allProductItemDataList){
                        //HTTP查询数据项信息
                        String url = "http://" + ip + ":" + portNum;
                        //get请求
                        String param = "id=" + equipment.getId() + "&productId=" + equipment.getProductId() + "&productItemDataId=" + productItemData.getId();
                        String getUrl = url + HttpCommunicationConstants.GET_GET_PRODUCTITEMDATA_INFO;
                        //返回信息
                        String resultString;
                        //发送HTTP请求
                        resultString = HttpUtils.sendGet(getUrl, param, encoded);

                        ProductItemData returnProductItemData = JSON.parseObject(resultString, ProductItemData.class);

                        //数据项是保存,则保存实时值
                        if (productItemData.getPreserveType() != null && "1".equals(productItemData.getPreserveType())) {
                            productItemData.setDataValue(returnProductItemData.getDataValue());
                            productItemData.setDataTime(new Date());
                            //修改实时值
                            isUpdate = productMapper.updateAthenaBesProductItemData(productItemData);

//                        //-------------------------------测试添加报警记录START-------------------------------
//                        WarnItemData warnItemData = new WarnItemData();
//                        warnItemData.setEarlyWarnType(1L);
//                        warnItemData.setEquipmentId(equipment.getId());
//                        warnItemData.setHappenTime(nowDate);
//                        warnItemData.setItemDataId(productItemData.getId());
//                        warnItemData.setWarnInfo("测试报警内容");
//                        warnItemData.setWarnType(1L);
//                        warnItemData.setWarnName("测试报警");
//                        isUpdate = equipmentMapper.insertWarnItemData(warnItemData);
//                        if(!isUpdate){
//                            log.error("添加测试报警数据失败：" + equipment.getCode() + "/" + equipment.getName());
//                        }
//                        //-------------------------------测试添加报警记录END-------------------------------

                            if (isUpdate) {
                                //添加缓存
                                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productItemData.getId(), productItemData);

                                //设备为电表
                                if ("1".equals(equipment.getMeterState())) {
                                    Map<String, Object> datum = new HashMap<>();
                                    //数据表ID-uuid
                                    datum.put("id", UUIDUtil.getRandom32BeginTimePK());
                                    //电表系统名称-设备编号
                                    datum.put("meteruuid", equipment.getCode());
                                    //实时数据
                                    datum.put("data", returnProductItemData.getDataValue());
                                    //上传时间
                                    String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate);
                                    datum.put("l_time", nowTime);
                                    //设备园区
                                    datum.put("parkid", equipment.getParkCode());
                                    //电能参数-数据项ID
                                    datum.put("electricId", productItemData.getId());
                                    data.add(datum);
                                } else {
                                    HistoryItemData h = new HistoryItemData();
                                    h.setItemDataId(productItemData.getId());
                                    h.setCreateTime(nowDate);
                                    h.setDataValue(returnProductItemData.getDataValue());
                                    h.setEquipmentId(equipment.getId());

                                    dataOther.add(h);
                                }
                            }
                        }
                    }


                }
                if (data.size() > 0) {
                    // 插入电表数据表 TODO 暂时插入到电表数据表和电表原始数据表中
                    besJobManagerMapper.insertCalculateData(data);
                    // 插入电表原始数据表
                    besJobManagerMapper.insertEnectricData(data);
                }
                if (dataOther.size() > 0) {
                    // 插入历史数据表 TODO 暂时插入到历史数据表中
                    isUpdate = equipmentMapper.insertHistoryDataList(dataOther);
                }

            }
            return isUpdate;
        } catch (Exception e) {
//            log.error("HTTP轮询设备，逻辑处理发生异常：" + equipment.getCode() + "/" + equipment.getName(), e);
            //查询所有设备信息
            List<Equipment> equipmentsRedis = equipmentMapper.selectAthenaBesEquipmentList(equipment);
            equipment = equipmentsRedis.get(0);
            equipment.setState("0");
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getId(), equipment);
            return false;
        }
    }

    /**
     * @Author:gaojikun
     * @Date:2023-03-20 11:22
     * @Param:id
     * @Description:测试HTTP协议GET请求
     * @Return:String
     */
    @GetMapping("/queryEquipmentInfoGET")
    public String queryEquipmentInfoGet(@RequestParam("id") String id) {
        Equipment returnEquipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, Long.parseLong(id));

        return JSONObject.toJSONString(returnEquipment);
    }

    /**
     * @Author:gaojikun
     * @Date:2023-03-20 11:22
     * @Param:Equipment
     * @Description:测试HTTP协议POST请求
     * @Return:String
     */
    @RequestMapping("/queryEquipmentInfoPOST")
    @ResponseBody
    public String queryEquipmentInfoPost(Equipment equipment) {
        Equipment returnEquipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getId());

        return JSONObject.toJSONString(returnEquipment);
    }
}