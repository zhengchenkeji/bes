package com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.internal.cglib.core.$Constants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.ModbusFunctions;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.AthenaBranchConfig;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.result.ResultMap;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.sms.model.SmsParam;
import com.ruoyi.common.utils.sms.server.EmailServer;
import com.ruoyi.common.utils.sms.server.SmsServer;
import com.ruoyi.common.utils.uuid.UUIDUtil;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.NoticeTableConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.constant.WebSocketEvent;
import com.zc.common.core.model.DataReception;
import com.zc.common.core.websocket.WebSocketService;
import com.zc.common.utils.CloneUtils;
import com.zc.connect.business.dto.JsonMsg;
import com.zc.connect.business.dto.ReceiveMsg;
import com.zc.connect.business.handler.ModbusMsgReceive;
import com.zc.connect.config.CodeConfig;
import com.zc.connect.nettyClient.ClientHandler;
import com.zc.connect.nettyServer.enums.StatisticalTypeEnum;
import com.zc.connect.util.DataUtil;
import com.zc.connect.util.MsgUtil;
import com.zc.connect.util.StringUtil;
import com.zc.efounder.JEnterprise.Cache.HouseholdBranchLinkCache;
import com.zc.efounder.JEnterprise.Cache.SubitemBranchLinkCache;
import com.zc.efounder.JEnterprise.commhandler.*;
import com.zc.efounder.JEnterprise.domain.baseData.*;
import com.zc.efounder.JEnterprise.domain.commhandler.BesBranchData;
import com.zc.efounder.JEnterprise.domain.commhandler.MonitoringErrorLog;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBesHouseholdBranchLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.AthenaBranchMeterLink;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.efounder.JEnterprise.domain.energyInfo.SubitemBranchLink;
import com.zc.efounder.JEnterprise.domain.safetyWarning.*;
import com.zc.efounder.JEnterprise.mapper.baseData.ProductMapper;
import com.zc.efounder.JEnterprise.mapper.commhandler.EnergyDataMapper;
import com.zc.efounder.JEnterprise.mapper.commhandler.JobManagerMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.ParkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.*;
import com.zc.efounder.JEnterprise.service.baseData.impl.EquipmentServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 9:16 2023/3/9
 * @Modified By:
 */
@Component
public class ModbusServerHandlers {
    private static final Logger log = LoggerFactory.getLogger(ModbusServerHandlers.class);

    private static List<Product> productLists = new ArrayList<>();

    private static List<Equipment> equipmentTCPList = new ArrayList<>();

    /**
     * 需要存储在能源表的 能源类型
     */
    private static List<String> energyTypeList = new ArrayList<>();
    //redis
    private RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);
    //产品
    private ProductMapper productMapper = ApplicationContextProvider.getBean(ProductMapper.class);
    //设备
    private static EquipmentServiceImpl equipmentService = ApplicationContextProvider.getBean(EquipmentServiceImpl.class);

    private static JobManagerMapper jobManagerMapper = ApplicationContextProvider.getBean(JobManagerMapper.class);
    //实时报警mapper
    private static AlarmRealtimeDataMapper alarmRealtimeDataMapper = ApplicationContextProvider.getBean(AlarmRealtimeDataMapper.class);
    //告警工单mapper
    private static AlarmWorkOrderMapper alarmWorkOrderMapper = ApplicationContextProvider.getBean(AlarmWorkOrderMapper.class);
    //园区mapper
    private ParkMapper parkMapper = ApplicationContextProvider.getBean(ParkMapper.class);
    //告警策略mapper
    private AlarmTacticsMapper alarmTacticsMapper = ApplicationContextProvider.getBean(AlarmTacticsMapper.class);
    //告警通知
    private AlarmNotificationRecordMapper notificationRecordMapper = ApplicationContextProvider.getBean(AlarmNotificationRecordMapper.class);
    //发送邮件
    private EmailServer emailServer = ApplicationContextProvider.getBean(EmailServer.class);
    //发送短信
    private SmsServer smsServer = ApplicationContextProvider.getBean(SmsServer.class);
    //通知配置和告警策略关系
    private AlarmNoticeLinkMapper noticeLinkMapper = ApplicationContextProvider.getBean(AlarmNoticeLinkMapper.class);


    /**
     * 分项缓存
     */
    private static final SubitemBranchLinkCache subitemBranchRlglCache = ApplicationContextProvider.getBean(SubitemBranchLinkCache.class);
    /**
     * 分户缓存
     */
    private static final HouseholdBranchLinkCache householdBranchRlglCache = ApplicationContextProvider.getBean(HouseholdBranchLinkCache.class);
    /**
     * 能耗緩存
     */
    private static final EnergyDataMapper energyDataMapper = ApplicationContextProvider.getBean(EnergyDataMapper.class);

    private static JobManagerMapper besJobManagerMapper = ApplicationContextProvider.getBean(JobManagerMapper.class);


    @PostConstruct
    private void init() {
        /**处理需存储在能源表的能耗类型*/
        energyTypeList.add("01000");//电
        energyTypeList.add("02000");//水
        energyTypeList.add("03000");//燃气
        energyTypeList.add("04000");//集中供热
        energyTypeList.add("05000");//集中供冷
        energyTypeList.add("06000");//煤
        energyTypeList.add("07000");//汽油
        energyTypeList.add("08000");//煤油
        energyTypeList.add("09000");//柴油
        energyTypeList.add("10000");//可再生能源
    }

    /**
     * 回令业务处理
     * 解析
     */
    public void response_analysis(ChannelHandlerContext ctx, Object msg) throws Exception {

        JsonMsg jsonMsg = new JsonMsg();

        ModbusMsgReceive clientMsgReceive = ClientHandler.modbusMsgReceive;

        String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();


        ByteBuf byteBuf = (ByteBuf) msg;

        String msgs =  CloneUtils.clone(MsgUtil.convertByteBufToString(byteBuf));

        System.out.println("接收传入的数据值：" + msgs);

        if (msgs.equals(CodeConfig.UsrHeartbeat)) {//有人模块的心跳
            //返回响应消息报文
            byte[] bytes = msgs.getBytes(CharsetUtil.UTF_8);
            ByteBuf buf = Unpooled.wrappedBuffer(bytes);
            ctx.channel().writeAndFlush(buf);

            return;
        }

        Integer TransActionId = null;
        Integer protocal = null;
        Integer msg_len = null;
        Integer slave_id = null;
        Integer funcotion_code = null;
        Integer registerBeginAddress = null;//寄存器起始地址

        Integer registerLength = null;//寄存器数量


        try {

            TransActionId = Integer.valueOf(StringUtil.hexToDecimal( CloneUtils.clone(msgs.substring(0, 4))));//交互（通信）标识：2个字节 为此次通信事务处理标识符，一般每次通信之后将被要求加1以区别不同的通信数据报文。
            protocal = Integer.valueOf(StringUtil.hexToDecimal(msgs.substring(4, 8)));//协议标识：2个字节 表示该条指令遵循ModbusTCP协议，一般都为00 00
            msg_len = Integer.valueOf(StringUtil.hexToDecimal(msgs.substring(8, 12)));//报文长度：2个字节 表示后面数据的长度，有几个字节，高字节在前
            slave_id = Integer.valueOf(msgs.substring(12, 14));//设备标识 1个字节

            funcotion_code = Integer.valueOf(msgs.substring(14, 16));//功能码 1个字节


            Queue<JsonMsg> queue = ModbusSendSyncMsgHandler.msgQueue.get(host + ":" + post);

            if (queue != null && queue.size() > 0) {

                JsonMsg queueMsg = queue.peek(); // 返回队首元素，但是不删除。

                if ((String.valueOf(TransActionId) + protocal).equals(queueMsg.getUuid())) {
                    registerBeginAddress = CloneUtils.clone(queueMsg.getRegisterBeginAddress());//深度拷贝值,避免被修改
                    registerLength = CloneUtils.clone(queueMsg.getRegisterLength());//深度拷贝值,避免被修改
                }

            }

            System.out.println(registerLength);

            jsonMsg.setUuid(String.valueOf(TransActionId) + protocal);
            jsonMsg.setIp(host);
            jsonMsg.setPost(post);
            msgReceiptHandler(jsonMsg);

        } catch (Exception e) {
            return;
        }

        refreshDevice();


//        System.out.println("接收传入的数据值：" + msgs);
        //判断功能码是否正确
        //可参考网址:https://blog.csdn.net/weixin_46304253/article/details/107960184?spm=1001.2101.3001.6650.8&utm_medium=distribute.pc_relevant.none-task-blog-2~default~BlogCommendFromBaidu~Rate-8-107960184-blog-78174071.pc_relevant_aa&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2~default~BlogCommendFromBaidu~Rate-8-107960184-blog-78174071.pc_relevant_aa&utm_relevant_index=17
        if (funcotion_code != ModbusFunctions.COIL_STATUS &&
                funcotion_code != ModbusFunctions.INPUT_STATUS &&
                funcotion_code != ModbusFunctions.HOLDING_REGISTER &&
                funcotion_code != ModbusFunctions.INPUT_REGISTER &&
                funcotion_code != ModbusFunctions.WRITE_ONE_COIL &&
                funcotion_code != ModbusFunctions.WRITE_ONE_HOLDING_REGISTER &&
                funcotion_code != ModbusFunctions.WRITE_MULTIPLE_COIL &&
                funcotion_code != ModbusFunctions.WRITE_MULTIPLE_HOLDING_REGISTER) {

            return;
        }

        if (funcotion_code == ModbusFunctions.WRITE_MULTIPLE_HOLDING_REGISTER) {
            //控制指令下发成功
            clientMsgReceive.sendMessageBoolen(true);
            return;
        }

        //遍历配置产品,取出所有的网关设备(tcp)
        List<Equipment> equipmentList = getEquipmentTCPList();
        if (equipmentList.size() == 0) {
            return;
        }

        try {
            Long equipmentId = null;
            Long sonEquipmentId = null;

            for (Equipment equipment : equipmentList) {
                String HOST = equipment.getIpAddress();
                Integer PORT = Integer.parseInt(equipment.getPortNum());
                if (!host.equals(HOST) || post != PORT) {
                    continue;
                }
                equipmentId = equipment.getId();
            }

            if (equipmentId == null) {
                return;
            }

            //获取当前产品下所有的设备
            Long productId = null;
            Map<String, Equipment> equipmentLists = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

            for (Equipment equipment : equipmentLists.values()) {
                if (equipment.getpId() == null) {//网关设备或者直连设备
                    continue;
                }
                if (!equipment.getpId().equals(equipmentId)) {//判断设备的父节点是否等于网关设备或者直连设备
                    continue;
                }
                if (equipment.getSonAddress().intValue() != slave_id) {//子设备地址是否为推送的地址
                    continue;
                }

                //websocket推送设备在线离线状态
                clientMsgReceive.deviceState(host, post, equipment.getId(), true);
                sonEquipmentId = equipment.getId();
                productId = equipment.getProductId();

            }

            //获取设备所有的数据项
            Map<String, ProductItemData> productItemDataList =
                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);

            List<ProductItemData> productItemDataLists = new ArrayList<>();

            /**使用stream流进行过滤排序*/
            Long finalProductId = productId;
            productItemDataLists = productItemDataList.values().stream()
                    .filter(itemData -> itemData.getProductId() == finalProductId)
                    .filter(itemData -> itemData.getItemDataId() == null)
                    .sorted(Comparator.comparing(ProductItemData::getSortNum))
                    .collect(Collectors.toList());

            Integer lestLength = msgs.substring(17, msgs.length() - 1).length();


            if (lestLength % 4 != 0) {//不能被4整除,说明回复的消息有误,数据不完整

                ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
                msg1.setCode(0);
                msg1.setIndex(4);

                clientMsgReceive.getMassgaeState(WebSocketEvent.MODBUS_DEVICE, msg1);
                return;
            } else {
                if (registerLength != (lestLength / 4)) {//发送的寄存器数量和回复的寄存器数量不相等,说明数据不完整

                    ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
                    msg1.setCode(0);
                    msg1.setIndex(4);

                    clientMsgReceive.getMassgaeState(WebSocketEvent.MODBUS_DEVICE, msg1);
                    return;
                }
            }

            int size = 0;

            for (ProductItemData productItemData : productItemDataLists) {
                if (size >= registerLength) {
                    continue;
                }


                Long sortNum = productItemData.getSortNum();//寄存器地址
                if (sortNum.intValue() < registerBeginAddress) {//寄存器地址小于起始地址,跳出当前循环
                    continue;
                }

                /**减去寄存器起始地址*/
                if (sortNum.intValue() - registerBeginAddress > registerLength) {//寄存器地址减去寄存器起始地址,大于寄存器数量时,跳出循环
                    continue;
                }

                String highLow = productItemData.getHighLow();//高低位 0-否;1-是;

                String binarySystem = productItemData.getBinarySystem();//二进制 0-否;1-是;


                int dataType = Integer.valueOf(productItemData.getDataType());//数据类型

                Integer registersNum = productItemData.getRegistersNum() == null ? 1 : productItemData.getRegistersNum();//读取的寄存器数量
                /**根据寄存器数量增加每次读取的字节*/
                size = size + registersNum;

                //todo 目前先不判断高低位中有2进制数据
                //判断是否高低位
                if (highLow.equals("1")) {//是

                    /**根据寄存器地址截取数据*/
                    Long beginNum = 18 + (sortNum * 4);
                    Long endNum = 18 + (sortNum * 4) + 2;
                    /**减去起始数量*/

                    beginNum = beginNum - (registerBeginAddress * 4);
                    endNum = endNum - (registerBeginAddress * 4);
                    /**获取高位数据项*/
                    Integer highData = Integer.valueOf(StringUtil.hexToDecimal(msgs.substring(beginNum.intValue(), endNum.intValue())));//高位数据
                    /**获取到高位数据项配置处理数据项*/
                    if (productItemData.getHighDetail() == null
                            || productItemData.getHighDetail().size() == 0
                            || productItemData.getLowDetail() == null
                            || productItemData.getLowDetail().size() == 0) {
                        log.error(productItemData.getName() + ":该数据项高低位配置不完整");
                        continue;
                    }
                    /**暂不考虑二进制数据，默认获取非二进制唯一的高低位参数配置*/
                    ProductItemData highitem = productItemData.getHighDetail().get(0);
                    /**处理数据*/
                    Double highDoubleData = Double.valueOf(highData.toString());
                    executeSaveItemDate(highDoubleData, highitem, sonEquipmentId);

                    /**处理底位数据*/
                    /**根据寄存器地址截取数据*/

                    beginNum = beginNum + 2;
                    endNum = endNum + 2;
                    ProductItemData lowitem = productItemData.getLowDetail().get(0);

                    Integer lowData = Integer.valueOf(StringUtil.hexToDecimal(msgs.substring(beginNum.intValue(), endNum.intValue())));//低位
                    Double lowDoubleData = Double.valueOf(lowData.toString());
                    executeSaveItemDate(lowDoubleData, lowitem, sonEquipmentId);
                    continue;
                }

                //判断数据类型
                if (dataType == 7) {//结构体
                    /**根据寄存器地址截取数据*/
                    Long beginNum = 18 + (sortNum * 4);
                    Long endNum = 18 + (sortNum * 4) + 4;
                    /**减去起始数量*/
                    beginNum = beginNum - (registerBeginAddress * 4);
                    endNum = endNum - (registerBeginAddress * 4);
                    String dataValue = "";
                    /**判断是否需要转为二进制*/
                    String hex = msgs.substring(beginNum.intValue(), endNum.intValue());

                    if (binarySystem.equals("0")) {
                        //否 转为10进制
                        dataValue = StringUtil.hexToDecimal(hex);
                        // TODO: 2023/3/30 暂时不做十进制处理
                    } else {//是
                        dataValue = StringUtil.hex2Binary(hex);
                    }
                    /**根据配置的通道数量处理*/
                    List<ProductItemData> structDetailList = productItemData.getStructDetail();

                    List<Integer> hex2List = new ArrayList<>();
                    /**切割为char数组*/
                    char[] chars = dataValue.toCharArray();
                    for (char value : chars) {
                        hex2List.add(Integer.parseInt(value + ""));
                    }

                    if (hex2List.size() != structDetailList.size()) {
                        /**数据不对应代表前面的二进制数字为0*/
                        int diff = structDetailList.size() - hex2List.size();
                        for (int i = 0; i < diff; i++) {
                            /**插入头部*/
                            hex2List.add(0, 0);
                        }
                    }
                    /**倒置数据*/
                    Collections.reverse(hex2List);
                    for (int index = 0; index < structDetailList.size(); index++) {
                        Double data = Double.valueOf(hex2List.get(index).toString());
                        executeSaveItemDate(data, structDetailList.get(index), sonEquipmentId);
                    }
                    continue;
                }


                /**非其他数据处理*/
                Long beginNum = 18 + (sortNum * 4);
                /**前缀长度加（寄存器地址*每个寄存器的字节数）+（字节数*每次读取的寄存器数量）*/
                Long endNum = 18 + (sortNum * 4) + (4 * registersNum);
                /**减去起始数量*/
                beginNum = beginNum - (registerBeginAddress * 4);
                endNum = endNum - (registerBeginAddress * 4);
                String substr = msgs.substring(beginNum.intValue(), endNum.intValue());

                //folat类型
                if (dataType == 2) {

                    Float data = StringUtil.hexStrToFloat(substr);

                    BigDecimal b = new BigDecimal(data.toString());
                    Double doubleData = b.doubleValue();
                    executeSaveItemDate(doubleData, productItemData, sonEquipmentId);
                    continue;

                }

                Integer dataValue = Integer.valueOf(StringUtil.hexToDecimal(substr));

                Double data = Double.valueOf(dataValue.toString());
                executeSaveItemDate(data, productItemData, sonEquipmentId);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param data     数据值
     * @param itemData 数据项配置
     * @description:处理数据项数据
     * @author: sunshangeng
     * @date: 2023/3/30 14:40
     * @return: void
     **/
    public void executeSaveItemDate(Double data, ProductItemData itemData, Long equipmentId) {
        Date cjsj = new Date();

        ModbusMsgReceive clientMsgReceive = ClientHandler.modbusMsgReceive;

        PointDataResponse pointDataResponse = new PointDataResponse();


        List<PointDataResponse> pointDataResponseList = new ArrayList<>();

        pointDataResponseList.add(pointDataResponse);


        System.out.println("处理数据数据项：" + itemData.getName() + ",数据值：" + data);
        data = data * Integer.parseInt(itemData.getRatioSize());
        int pow = (int) Math.pow(10, (double) Integer.valueOf(itemData.getRadixPoint()));
        float floatData = (float) (data / pow);

        DecimalFormat df1 = new DecimalFormat("0.00");//保留两位小数，如果是零点几，小数点后几个零就保留几位

        String stringData = rvZeroAndDot(Float.toString(Float.parseFloat(df1.format(floatData))));
        /**处理场景联动设备触发*/
        SceneLinkHandler.deviceTrigger(equipmentId+"_"+itemData.getId(),"1",SceneLinkHandler.SCENE_LINK_DEVICEACTION_ATTRIBUTE,stringData);

        pointDataResponse.setModbusId(itemData.getId() + "+" + equipmentId);
        pointDataResponse.setValue(stringData);

        ReceiveMsg<List<PointDataResponse>> msg1 = new ReceiveMsg<>();
        msg1.setData(pointDataResponseList);
        msg1.setCode(1);
        msg1.setIndex(4);

        clientMsgReceive.getMassgaeState(WebSocketEvent.MODBUS_DEVICE, msg1);
        BigDecimal bdata = new BigDecimal(stringData);

        itemData.setDataValue(bdata);

        /**判断是否存储数据*/
        if (itemData.getPreserveType().equals("1")) {


            /**判断是否需要存储第三方能源表*/
            if (energyTypeList.contains(itemData.getEnergyCode())) {

                Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipmentId);


                executeSaveBranch(cjsj, itemData, equipment);
            }
            /**插入数据项数据表*/
            HistoryItemData historyItemData = new HistoryItemData(itemData.getId(), itemData.getDataValue(), equipmentId);
            historyItemData.setCreateTime(cjsj);
            equipmentService.insertHistoryData(historyItemData);
            /**存储*/
        }
        /**处理缓存item*/
        ProductItemRealTimeData productItemRealTimeData = ProductItemRealTimeData.CreateRealTimeData(itemData.getId(), equipmentId, stringData);
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_RealTime, productItemRealTimeData.getId(), productItemRealTimeData);

    }

    public static String rvZeroAndDot(String val) {

        if (val.indexOf(".") > 0) {

            // 去掉多余的0

            val = val.replaceAll("0+?$", "");

            // 如最后一位是.则去掉

            val = val.replaceAll("[.]$", "");
        }
        return val;

    }

    //刷新设备
    public void refreshDevice() {

        getProductLists().clear();
        equipmentTCPList.clear();

        //遍历配置产品,取出所有的网关设备(tcp)
        Map<String, Product> productList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product);
        if (productList == null || productList.size() == 0) {
            return;
        }

        productList.values().forEach(val -> {
            if (val.getIotType().equals("0") || val.getIotType().equals("1")) {//直连设备或者网关设备
                getProductLists().add(val);
            }
        });

        if (getProductLists().size() == 0) {
            return;
        }

        Map<String, Equipment> controllerList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

        if (controllerList == null || controllerList.size() == 0) {
            return;
        }

        controllerList.values().forEach(val -> {
            if (val.getNetworkType() == null) {
                return;
            }
            if (val.getNetworkType().equals("1")) {//0-下位机客户端;1-下位机服务端
                return;
            }
            getProductLists().forEach(val1 -> {
                if (val1.getId() == val.getProductId()) {
                    equipmentTCPList.add(val);
                }
            });

        });
    }


    /**
     * 消息回执处理
     * 1.根据通道id获取消息队列
     * 2.获取队列中的消息
     * 3.如果消息是队列中的回复消息，则弹出该消息队列
     * 4.如果队列中有下一个消息则直接发送
     *
     * @param msg
     */
    public static void msgReceiptHandler(JsonMsg msg) {
        if (msg == null) {
            return;
        }

        String uuid = msg.getUuid();
        String ip = msg.getIp();
        Integer port = msg.getPost();

        if (uuid == null || uuid.isEmpty() || ip == null || ip.isEmpty()) {
            return;
        }

        Queue<JsonMsg> queue = ModbusSendSyncMsgHandler.msgQueue.get(ip + ":" + port);

        if (queue == null || queue.isEmpty()) {
            return;
        }

        JsonMsg queueMsg = queue.peek(); // 返回队首元素，但是不删除。

        if (uuid.equals(queueMsg.getUuid())) {
            queue.poll(); // 从队首删除并返回该元素

            JsonMsg nextMsg = queue.peek();

            if (nextMsg == null) {
                return;
            }

            ModbusSendSyncMsgHandler.postEvent(ip, port, nextMsg);
        } else {
            ModbusSendSyncMsgHandler.postEvent(ip, port, queueMsg);
        }

    }

    public static List<Product> getProductLists() {
        return productLists;
    }

    public static void setProductLists(List<Product> productLists) {
        ModbusServerHandlers.productLists = productLists;
    }

    public static List<Equipment> getEquipmentTCPList() {
        return equipmentTCPList;
    }

    public static void setEquipmentTCPList(List<Equipment> equipmentTCPList) {
        ModbusServerHandlers.equipmentTCPList = equipmentTCPList;
    }


    /***
     * @description:保存支路数据
     * @author: sunshangeng
     * @date: 2023/4/20 11:42
     * @param: [data, itemData, equipmentId]
     * @return: void
     **/
    private void executeSaveBranch(Date cjsj, ProductItemData itemData, Equipment equipment) {
        List<OtherCalculateData> otherCalculateDataList = new ArrayList<>();
        OtherCalculateData calculateData = new OtherCalculateData();
        calculateData.setCjsj(new Date());
        calculateData.setEquipmentId(equipment.getId());
        calculateData.setDataValue(itemData.getDataValue());
        calculateData.setItemDataId(itemData.getId());
        calculateData.setEnergyCode(itemData.getEnergyCode());
        otherCalculateDataList.add(calculateData);
        String strCjsj = DateUtils.parseDateToStr("yyyy-MM-dd hh:mm:ss", cjsj);

        /**第三方设备报警逻辑 gaojikun*/
        //查询父设备信息
        String controllName = "";
        if (equipment.getpId() != null) {
            Equipment equipmentParents = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
            controllName = equipmentParents.getName();
        }
        //取出告警策略缓存遍历信息
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics).values();
        boolean resultBoolean = false;
        for (Object item : values) {
            AlarmTactics alarmTactics = (AlarmTactics) item;
            //根据设备和数据项ID获取策略
            if (alarmTactics.getDeviceType() == null || alarmTactics.getDeviceId() == null || equipment.getId() == null) {
                continue;
            }
            if (alarmTactics.getDeviceType() == 5 && alarmTactics.getDeviceId().equals(String.valueOf(equipment.getId()))) {
                String[] itemStringArr = alarmTactics.getItemDataId().split(",");
                Set<String> set = new HashSet<>(Arrays.asList(itemStringArr));
                if (set.contains(String.valueOf(itemData.getId()))) {
                    //是告警使能 是告警触发
                    if (/*alarmTactics.getInformType() == null ||*/ alarmTactics.getActive() == null) {
                        continue;
                    }
                    if (/*1 == alarmTactics.getInformType() &&*/ 1 == alarmTactics.getActive()) {
                        //根据策略范围类型进行数据对比
                        Integer rangeType = alarmTactics.getRangeType();
                        if (rangeType == 1) {
                            //1：确认值
                            if (0 != itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getPrecise()))) {
                                //添加报警记录
                                resultBoolean = insertAlarmRealtimeData(alarmTactics, itemData, equipment,
                                        alarmTactics.getName() + "报警", String.valueOf(alarmTactics.getPrecise()),
                                        controllName + ":" + equipment.getName() + ":" + itemData.getName() + ":" + alarmTactics.getName() + "报警",
                                        String.valueOf(alarmTactics.getLevel()), controllName);
                            }
                        } else if (rangeType == 2) {
                            //2.阈值
                            if (0 > itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getUnder()))
                                    || 0 < itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getOver()))) {
                                //添加报警记录
                                resultBoolean = insertAlarmRealtimeData(alarmTactics, itemData, equipment,
                                        alarmTactics.getName() + "报警", String.valueOf(alarmTactics.getUnder()),
                                        controllName + ":" + equipment.getName() + ":" + itemData.getName() + ":" + alarmTactics.getName() + ",实时值不在阈值范围内",
                                        String.valueOf(alarmTactics.getLevel()), controllName);
                            }
                        } else if (rangeType == 3) {
                            //3：上线
                            if (0 < itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getOver()))) {
                                //添加报警记录
                                resultBoolean = insertAlarmRealtimeData(alarmTactics, itemData, equipment,
                                        alarmTactics.getName() + "报警", String.valueOf(alarmTactics.getOver()),
                                        controllName + ":" + equipment.getName() + ":" + itemData.getName() + ":" + alarmTactics.getName() + ",实时值超出上限值",
                                        String.valueOf(alarmTactics.getLevel()), controllName);
                            }
                        } else if (rangeType == 4) {
                            //4：下限
                            if (0 > itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getUnder()))) {
                                //添加报警记录
                                resultBoolean = insertAlarmRealtimeData(alarmTactics, itemData, equipment,
                                        alarmTactics.getName() + "报警", String.valueOf(alarmTactics.getUnder()),
                                        controllName + ":" + equipment.getName() + ":" + itemData.getName() + ":" + alarmTactics.getName() + ",实时值低于下限值",
                                        String.valueOf(alarmTactics.getLevel()), controllName);
                            }
                        } else {
                            continue;
                        }

                    } else {
                        continue;
                    }
                }
            }
        }

        /**插入第三方能源表*/
        jobManagerMapper.insertOtherCalculateData(otherCalculateDataList);

        /**电价设置*/
        BigDecimal electricPrice = EnergyCollectHandler.calculationElectricityPrice(strCjsj);


        /**差值计算*/
        /**保存上一次数据*/
        Map<String, Map<String, Object>> beforeControllerData = new HashMap<>();
        beforeControllerData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData, equipment.getId());
        Map<String, Object> dataMap = new HashMap<>();
        Double betweenValue = 0.0;
        if (beforeControllerData != null && beforeControllerData.size() != 0) {
            //采集器中是否有当前 数据项
            if (beforeControllerData.containsKey(itemData.getId().toString())) {
                Map<String, Object> beforeElectricData = beforeControllerData.get(itemData.getId().toString());

                Double dataNew = itemData.getDataValue().doubleValue();
                Double dataBefore = Double.parseDouble(beforeElectricData.get("data").toString());

                BigDecimal dataNew1 = new BigDecimal(String.valueOf(dataNew));
                BigDecimal dataBefore2 = new BigDecimal(String.valueOf(dataBefore));

                betweenValue = dataNew1.subtract(dataBefore2).doubleValue();

                //差值负数时不存差值数据,不更新缓存, 但是插入错误日志表athena_bes_monitoring_error_log
                if (betweenValue < 0) {
                    MonitoringErrorLog monitoringErrorLog = new MonitoringErrorLog();
                    monitoringErrorLog.setSysName(equipment.getId() + "");
                    monitoringErrorLog.setElectricId(itemData.getId() + "");
                    monitoringErrorLog.setMeterType("0");
                    monitoringErrorLog.setBeforeData(dataBefore);
                    monitoringErrorLog.setNewData(dataNew);
                    monitoringErrorLog.setDiffData(betweenValue);

                    besJobManagerMapper.insertMonitoringErrorLog(monitoringErrorLog);
                    log.warn("差值运算，数据错误，能耗值不能是负数");
                    /***/
//                            newData.remove(item);
//                            removelist.add(item);
                    return;
                }
            } else {
                log.warn("数据项:" + itemData.getName() + "没有能耗数据缓存,本次不进行差值计算");
                betweenValue = itemData.getDataValue().doubleValue();
                /**判断数据项*/

            }

        } else {
            beforeControllerData = new HashMap<>();
            /**当前缓存未存入*/
            log.warn("设备:" + equipment.getName() + "没有能耗数据缓存,本次不进行差值计算");
            betweenValue = itemData.getDataValue().doubleValue();
        }

        /**缓存组装*/
        dataMap.put("id", UUIDUtil.getRandom32BeginTimePK());
        dataMap.put("meteruuid", equipment.getId());
        dataMap.put("data", itemData.getDataValue().toString());
        dataMap.put("l_time", cjsj);
        dataMap.put("parkid", equipment.getParkCode());
        dataMap.put("fNybh", itemData.getEnergyCode());
        dataMap.put("meterId", equipment.getId());
        dataMap.put("electricId", itemData.getId());
        /**电价处理*/
        beforeControllerData.put(itemData.getId() + "", dataMap);
        BigDecimal electricPriceSum = new BigDecimal(electricPrice.toString()).multiply(new BigDecimal(betweenValue));
        electricPriceSum = electricPriceSum.setScale(10, BigDecimal.ROUND_UP);
        dataMap.put("electricPriceSum", electricPriceSum);
        //更新能耗数据缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_MeterAcquisitionData, equipment.getId(), beforeControllerData);
        String timeHour = DataUtil.formatDate((String) strCjsj, StatisticalTypeEnum.HOUR);
        String timeDay = DataUtil.formatDate((String) strCjsj, StatisticalTypeEnum.DAY);
        String timeMonth = DataUtil.formatDate((String) strCjsj, StatisticalTypeEnum.MONTH);
        String timeYear = DataUtil.formatDate((String) strCjsj, StatisticalTypeEnum.YEAR);

        /**获取当前电表绑定支路ID*/
        Map<String, AthenaBranchMeterLink> linkMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchMeterLink);
        Double finalBetweenValue = betweenValue;
        BigDecimal finalElectricPriceSum = electricPriceSum;
//        List<AthenaBranchMeterLink> collect =
        linkMap.values().stream()
                .filter(link -> link.getMeterId().equals(equipment.getId()))//过滤掉非当前电表的支路关系
//                .forEach(link->{
//                    System.out.println(link.getElectricParam()+"-----"+itemData.getId()+"--------"+link.getElectricParam().indexOf(itemData.getId() + ""));
//                });
                .filter(link -> link.getElectricParam().indexOf(itemData.getId() + "") != -1)//过滤掉非当前数据项的支路关系
//                        .collect(Collectors.toList());//过滤掉非当前数据项的支路关系
//        collect
                .forEach(link -> {
                    /**保存支路和园区操作*/
                    AthenaBranchConfig besBranchConf = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyInfo_BranchConfig, link.getBranchId());
                    String f_zlbh = besBranchConf.getBranchId() + "";

                    if (besBranchConf != null && !StringUtils.hasText(besBranchConf.getParentId().toString())) {
                        // 计算总能耗
                        EnergyCalculateHandler.saveEnergyCalculate(itemData.getEnergyCode(), finalBetweenValue, equipment.getParkCode(), timeHour, StatisticalTypeEnum.HOUR, strCjsj);
                        EnergyCalculateHandler.saveEnergyCalculate(itemData.getEnergyCode(), finalBetweenValue, equipment.getParkCode(), timeDay, StatisticalTypeEnum.DAY, strCjsj);
                        EnergyCalculateHandler.saveEnergyCalculate(itemData.getEnergyCode(), finalBetweenValue, equipment.getParkCode(), timeMonth, StatisticalTypeEnum.MONTH, strCjsj);
                        EnergyCalculateHandler.saveEnergyCalculate(itemData.getEnergyCode(), finalBetweenValue, equipment.getParkCode(), timeYear, StatisticalTypeEnum.YEAR, strCjsj);
                    }


                    /**保存支路能耗*/
                    saveCalculateData(f_zlbh, itemData.getEnergyCode(), timeHour, StatisticalTypeEnum.HOUR, finalBetweenValue, equipment.getParkCode(), strCjsj, finalElectricPriceSum); // 按小时计算
                    saveCalculateData(f_zlbh, itemData.getEnergyCode(), timeDay, StatisticalTypeEnum.DAY, finalBetweenValue, equipment.getParkCode(), strCjsj, finalElectricPriceSum); // 按天计算
                    saveCalculateData(f_zlbh, itemData.getEnergyCode(), timeMonth, StatisticalTypeEnum.MONTH, finalBetweenValue, equipment.getParkCode(), strCjsj, finalElectricPriceSum); // 按月计算
                    saveCalculateData(f_zlbh, itemData.getEnergyCode(), timeYear, StatisticalTypeEnum.YEAR, finalBetweenValue, equipment.getParkCode(), strCjsj, finalElectricPriceSum); // 按年计算

                });


    }

    /***
     * @description:插入支路数据表
     * @author: sunshangeng
     * @date: 2023/4/20 11:38
     * @return: void
     **/
    private void saveCalculateData(String f_zlbh,
                                   String f_dnbh,
                                   String time,
                                   StatisticalTypeEnum typeEnum,
                                   Double value,
                                   String f_yqbh, String cjsj,
                                   BigDecimal electricPriceSum) {


        if (null == f_zlbh
                || null == f_dnbh
                || null == time
                || null == typeEnum
                || null == f_yqbh
                || null == value
                || null == electricPriceSum) {
            return;
        }

        BesBranchData besBranchData = new BesBranchData();
        besBranchData.setfZlbh(f_zlbh);
        besBranchData.setfCjsj(time);
        besBranchData.setfType(typeEnum.getCode().toString());
        besBranchData.setfData(value);
        besBranchData.setfAccurateCjsj(cjsj);
        besBranchData.setElectricPriceSum(electricPriceSum);//电价总金额

        /**
         * 能耗存库
         * 方案一：首先查询该记录是否存在，不存则新增数据，存在则更新数据
         * 方案二：首先更新数据，更新的数据不存在则新增（√）
         */
        // String count = besBranchDataMapper.queryBranchExists(besBranchData).getRows();

        try {
            if (!energyDataMapper.updateBranchData(besBranchData)) {
                besBranchData.setfId(UUIDUtil.getRandom32BeginTimePK());
                besBranchData.setfDnbh(f_dnbh);

                energyDataMapper.saveBranchData(besBranchData);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 获取所有的分项编号
        List<SubitemBranchLink> subentryNumbers = subitemBranchRlglCache.getCacheByBranchId(f_zlbh);

        // 保存分项能耗数据
        SubentryCalculateHandler.saveCalculateData(subentryNumbers, time, value, typeEnum, f_yqbh);

        // 获取所有的分户编号
        List<AthenaBesHouseholdBranchLink> householdNumbers = householdBranchRlglCache.getCacheByBranchId(f_zlbh);

        // 保存分户能耗数据
        HouseholdCalculateHandler.saveCalculateData(householdNumbers, time, value, typeEnum, f_yqbh, electricPriceSum);

        // 保存分户分项数据
        HouseholdSubentryCalculateHandler.saveCalculateData(householdNumbers, subentryNumbers, time, value, typeEnum, f_yqbh, electricPriceSum);

    }

    /**
     * 每分钟触发
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendmsg() {

    }

    /**
     * 添加到实时报警记录
     */
    public boolean insertAlarmRealtimeData(AlarmTactics alarmTactics, ProductItemData itemData, Equipment equipment,
                                           String alarmName, String planVal, String promptMsg, String level, String controllName) {
        //是否是第一次实时报警
        AlarmRealtimeData alarmRealtimeDataSelect = new AlarmRealtimeData(alarmTactics.getId(), controllName + ":" + equipment.getName() + ":" + itemData.getName(), planVal, "5");
        List<AlarmRealtimeData> alarmRealtimeDataCount = alarmRealtimeDataMapper.selectAlarmRealtimeDataStateList(alarmRealtimeDataSelect);

        Integer countNumber = 1;
        Date nowDate = null;
        Date finalDate = new Date();
        int i = 0;
        //告警策略ID 告警策略Name 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型
        AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData(alarmTactics.getId(), alarmTactics.getName(), String.valueOf(itemData.getDataValue()),
                nowDate, finalDate, countNumber, 0L,
                controllName + ":" + equipment.getName() + ":" + itemData.getName(), alarmName,
                planVal, promptMsg, level, "5");

        if (alarmRealtimeDataCount == null || alarmRealtimeDataCount.size() == 0) {
            nowDate = finalDate;
            //添加实时报警
            alarmRealtimeData.setCreateTime(finalDate);
            alarmRealtimeData.setEquipmentId(equipment.getId());
            i = alarmRealtimeDataMapper.insertAlarmRealtimeData(alarmRealtimeData);
        } else {
            countNumber = countNumber + alarmRealtimeDataCount.get(0).getAmount();
            //告警策略ID 告警策略Name 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型
            AlarmRealtimeData alarmRealtimeDataUpdate = new AlarmRealtimeData();
            alarmRealtimeDataUpdate.setId(alarmRealtimeDataCount.get(0).getId());
            alarmRealtimeDataUpdate.setAmount(countNumber);
            alarmRealtimeDataUpdate.setLastTime(finalDate);
            //修改实时报警
            i = alarmRealtimeDataMapper.updateAlarmRealtimeDataById(alarmRealtimeDataUpdate);
        }

        if (i != 1) {
            log.info("============================ERROR：第三方设备添加到实时报警记录失败============================");
            return false;
        }


        //是否是第一次告警工单
        AlarmWorkOrder alarmWorkOrderSelect = new AlarmWorkOrder(alarmTactics.getId(), controllName + ":" + equipment.getName() + ":" + itemData.getName(), planVal, "5");
        List<AlarmWorkOrder> alarmWorkOrderCount = alarmWorkOrderMapper.selectAlarmWorkOrderStateList(alarmWorkOrderSelect);

        if (alarmWorkOrderCount == null || alarmWorkOrderCount.size() == 0) {
            String userId = "";
            //根据策略查询用户ID
            Map<String, String> userIdsMap = alarmWorkOrderMapper.selectUserIdByAlarmTacticsId(alarmTactics.getId());
            if (userIdsMap != null && userIdsMap.get("userId") != null && userIdsMap.get("userId").length() > 0) {
                if (Arrays.asList(userIdsMap.get("userId").split(",")).contains("1")) {
                    userId = userIdsMap.get("userId");
                } else {
                    userId = "1," + userIdsMap.get("userId");
                }
            } else {
                userId = "1";
            }

            //告警策略ID 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型 所属用户id 状态
            AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder(alarmTactics.getId(), alarmTactics.getName(), String.valueOf(itemData.getDataValue()),
                    finalDate, finalDate, 1, 0L,
                    controllName + ":" + equipment.getName() + ":" + itemData.getName(), alarmName,
                    planVal, promptMsg, level, "5", userId, "0");
            //添加告警工单
            alarmWorkOrder.setCreateTime(finalDate);
            alarmWorkOrder.setEquipmentId(equipment.getId());
            i = alarmWorkOrderMapper.insertAlarmWorkOrder(alarmWorkOrder);
            if (i != 1) {
                log.info("============================ERROR：第三方设备添加到实时报警记录失败============================");
                return false;
            }
        } else {
            //告警策略ID 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型 所属用户id 状态
            AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder();
            alarmWorkOrder.setId(alarmWorkOrderCount.get(0).getId());
            alarmWorkOrder.setAmount(1 + alarmWorkOrderCount.get(0).getAmount());
            alarmWorkOrder.setStatus("0");
            alarmWorkOrder.setLastTime(finalDate);
            alarmWorkOrder.setRemark("");
            alarmWorkOrder.setUpdateName(null);
            alarmWorkOrder.setUpdateCode(null);
            alarmWorkOrder.setUpdateTime(null);
            //修改告警工单
            boolean isUpdate = alarmWorkOrderMapper.updateAlarmWorkOrder(alarmWorkOrder);
            if (!isUpdate) {
                log.info("============================ERROR：第三方设备添加到实时报警记录失败============================");
                return false;
            }
        }


        //告警通知操作
        //告警策略Id 不为空 进行以下操作
        if (null != alarmRealtimeData.getAlarmTacticsId()) {
            AlarmTactics alarmTacticsNotic = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, alarmRealtimeData.getAlarmTacticsId());

            if (alarmTacticsNotic == null) {
                AlarmTactics dataMapper = alarmTacticsMapper.selectAlarmTacticsById(alarmRealtimeData.getAlarmTacticsId());
                if (dataMapper != null) {
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics, dataMapper.getAlarmTypeId(), dataMapper);
                    alarmTacticsNotic = dataMapper;
                }
            }

            //查询出来该报警策略关联了那些告警接收人
            List<AlarmNotifier> alarmNotifiers = alarmTacticsMapper.selectAlarmNotifierByAlarmTacticsId(alarmTacticsNotic.getId());

            String contextJson = createJson(alarmRealtimeData.getAlarmName(), "告警触发", alarmRealtimeData.getPromptMsg()
                    , alarmRealtimeData.getAlarmValue(), alarmRealtimeData.getPlanVal());

            //判断是否发送消息通知  0：否  1：是  通知类型  为0：全部 或者  1：告警触发
            if (1 == alarmTacticsNotic.getIsSendInform() && (0 == alarmTacticsNotic.getInformType() || 1 == alarmTacticsNotic.getInformType())) {
                //告警播报  0：否  1：是
                if (1 == alarmTacticsNotic.getAlarmSound()) {

                    /**获取当前绑定的语音播报通知配置*/
                    AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 3);

                    for (AlarmNotifier alarmNotifier : alarmNotifiers) {
                        if (!Arrays.asList(alarmNotifier.getUserId().split(",")).contains("1")) {
                            alarmNotifier.setUserId("1," + alarmNotifier.getUserId());
                        }
                        ResultMap resultMap = NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM,alarmTactics.getId()+"");
                        String resultJson = resultMap.get("msg").toString();

                        List<String> tokens = getTokenList(alarmNotifier.getUserId());
                        for (String token : tokens) {
                            // 推送消息到web客户端
                            WebSocketService.postEvent(token, WebSocketEvent.ALARMMSG, resultJson);
                        }
                    }
                }

                //发送邮件  0：否  1：是
                if (1 == alarmTacticsNotic.getSendEmail()) {

                    /**查询绑定的邮件通知*/
                    AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 2);
                    //循环发送邮件
                    for (AlarmNotifier alarmNotifier : alarmNotifiers) {
                        if (!Arrays.asList(alarmNotifier.getUserId().split(",")).contains("1")) {
                            alarmNotifier.setUserId("1," + alarmNotifier.getUserId());
                        }
                        try {
//                            for (String userId : strings) {
                            //获取用户邮箱
//                            SysUser userInfo = alarmTacticsMapper.selectUserInfoById(userId);
//                            if (userInfo != null && !com.ruoyi.common.utils.StringUtils.isEmpty(userInfo.getEmail())) {
                            //发送邮件
                            NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM, alarmTactics.getId() + "");
//                                    //业务id暂定
//                                    SmsParam smsParam = new SmsParam(userInfo.getEmail(), context, "", alarmRealtimeData.getAlarmName());
//                                    emailServer.sendMessage(smsParam);
                            //添加通知记录
                            AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTacticsNotic.getId(), 1L, DateUtils.getNowDate(), 1L, contextJson, alarmTactics.getName());
                            alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                            notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
//                            } else {
//                                log.error("用户信息未获取到邮箱信息！");
//                                throw new Exception();
//                            }
//                            }
                        } catch (Exception e) {
                            log.error("发送邮件失败！");
                            //添加通知记录
                            AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTacticsNotic.getId(), 1L, DateUtils.getNowDate(), 0L, contextJson, alarmTactics.getName());
                            alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                            notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                        }
                    }
                }

                //发送短信  0：否  1：是
                if (1 == alarmTacticsNotic.getTextSb()) {
                    /**查询绑定的短信通知*/
                    AlarmNoticeLink alarmNoticeLink = noticeLinkMapper.selectAlarmNoticeLinkByType(alarmRealtimeData.getAlarmTacticsId(), 1);
                    //循环发送短信
                    for (AlarmNotifier alarmNotifier : alarmNotifiers) {
                        if (!Arrays.asList(alarmNotifier.getUserId().split(",")).contains("1")) {
                            alarmNotifier.setUserId("1," + alarmNotifier.getUserId());
                        }
                        try {
//                            for (String userId : strings) {
//                                //获取用户电话
//                                SysUser userInfo = alarmTacticsMapper.selectUserInfoById(userId);
//                                if (userInfo != null && !com.ruoyi.common.utils.StringUtils.isEmpty(userInfo.getPhonenumber())) {
                            NoticeHandler.sendNotice(alarmNoticeLink.getNoticeConfigid(), alarmNoticeLink.getNoticeTemplateid(), alarmNotifier.getUserId().split(","), contextJson, NoticeTableConstants.BES_ALARM, alarmTactics.getId() + "");
//                                    //业务id暂定
//                                    SmsParam smsParam = new SmsParam(userInfo.getPhonenumber(), context, "", alarmRealtimeData.getAlarmName());
//                                    //发送短信
//                                    smsServer.send(smsParam);
                            //添加通知记录
                            AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTacticsNotic.getId(), 2L, DateUtils.getNowDate(), 1L, contextJson, alarmTactics.getName());
                            alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                            notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
//                                } else {
//                                    log.error("用户信息未获取到短信信息！");
//                                    throw new Exception();
//                                }
//                        }
                        } catch (Exception e) {
                            log.error("发送短信失败！");
                            AlarmNotificationRecord alarmNotificationRecord = new AlarmNotificationRecord(alarmTacticsNotic.getId(), 2L, DateUtils.getNowDate(), 0L, contextJson, alarmTactics.getName());
                            alarmNotificationRecord.setCreateTime(DateUtils.getNowDate());
                            notificationRecordMapper.insertAlarmNotificationRecord(alarmNotificationRecord);
                        }
                    }

                }
            }
        }

        //推送前端实时条数
        Map<String, Object> msgMap = new HashedMap();
        DataReception dataReception = getNoRecoverCount();//查询报警的条数
        msgMap.put("alarmRealtimeCount", dataReception.getData());
        // 推送消息到web客户端
        WebSocketService.broadcast(WebSocketEvent.ALARM, msgMap);
        return true;

    }

    /**
     * 组装通知数据
     * gaojikun
     **/
    private String strMessage(String name, String msg, String val, String planVal) {
        String str = "报警名称:" + name + ",报警描述:" + msg + ",报警值:" + val + ",计划值:" + planVal + "！";
        return str;
    }

    /**
     * 组装通知数据
     * gaojikun
     **/
    private String createJson(String name, String triggerMode, String msg, String val, String planVal) {
//        String str = "报警名称:" + name + ",报警描述:" + msg + ",报警值:" + val + ",计划值:" + planVal;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("triggerMode", triggerMode);
        jsonObject.put("msg", msg);
        jsonObject.put("val", val);
        jsonObject.put("planVal", planVal);
        return jsonObject.toJSONString();
    }

    /**
     * 获得token列表
     * gaojikun
     **/
    private List<String> getTokenList(String userids) {
        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        List<String> list = new ArrayList<>();
        keys.forEach(key -> {
            LoginUser loginUser = redisCache.getCacheObject(key);
            /**sunshangeng 修改根据新的接收组获取用户token*/
            String[] array = userids.split(",");
            for (String userid : array) {
                SysUser sysUser = loginUser.getUser();
                if (sysUser.getUserId() == Long.parseLong(userid)) {
                    list.add(loginUser.getToken());
                }
            }
        });

        return list;
    }

    /**
     * 查询报警条数
     * gaojikun
     **/
    public DataReception getNoRecoverCount() {
        try {
            Integer count = alarmRealtimeDataMapper.selectAlarmRealtimeDataCount();
            return new DataReception(true, count);
        } catch (Exception e) {
            return new DataReception(false);
        }
    }

    /**
     * 每分钟触发,测试报警逻辑
     * gaojikun
     */
//    @Scheduled(cron = "0 */1 * * * ?")
    public void testScheduled() {
        //设备离线报警
        deviceState("10.168.56.88", 7799, 126L, false);
        //第三方设备数据报警
        Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, 126L);
        ProductItemData itemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, 142L);
        itemData.setDataValue(new BigDecimal(51));
        /**第三方设备报警逻辑 gaojikun*/
        //查询父设备信息
        String controllName = "";
        if (equipment.getpId() != null) {
            Equipment equipmentParents = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
            controllName = equipmentParents.getName();
        }
        //取出告警策略缓存遍历信息
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SafetyWarning_AlarmTactics).values();
        boolean resultBoolean = false;
        for (Object item : values) {
            AlarmTactics alarmTactics = (AlarmTactics) item;
            //根据设备和数据项ID获取策略
            if (alarmTactics.getDeviceType() == null || alarmTactics.getDeviceId() == null || equipment.getId() == null) {
                continue;
            }
            if (alarmTactics.getDeviceType() == 5 && alarmTactics.getDeviceId().equals(String.valueOf(equipment.getId()))) {
                String[] itemStringArr = alarmTactics.getItemDataId().split(",");
                Set<String> set = new HashSet<>(Arrays.asList(itemStringArr));
                if (set.contains(String.valueOf(itemData.getId()))) {
                    //是告警使能 是告警触发
                    if (/*alarmTactics.getInformType() == null ||*/ alarmTactics.getActive() == null) {
                        continue;
                    }
                    if (/*1 == alarmTactics.getInformType() &&*/ 1 == alarmTactics.getActive()) {
                        //根据策略范围类型进行数据对比
                        Integer rangeType = alarmTactics.getRangeType();
                        if (rangeType == 1) {
                            //1：确认值
                            if (0 != itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getPrecise()))) {
                                //添加报警记录
                                resultBoolean = insertAlarmRealtimeData(alarmTactics, itemData, equipment,
                                        alarmTactics.getName() + "报警", String.valueOf(alarmTactics.getPrecise()),
                                        controllName + ":" + equipment.getName() + ":" + itemData.getName() + ":" + alarmTactics.getName() + "报警",
                                        String.valueOf(alarmTactics.getLevel()), controllName);
                            }
                        } else if (rangeType == 2) {
                            //2.阈值
                            if (0 > itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getUnder()))
                                    || 0 < itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getOver()))) {
                                //添加报警记录
                                resultBoolean = insertAlarmRealtimeData(alarmTactics, itemData, equipment,
                                        alarmTactics.getName() + "报警", String.valueOf(alarmTactics.getUnder()),
                                        controllName + ":" + equipment.getName() + ":" + itemData.getName() + ":" + alarmTactics.getName() + ",实时值不在阈值范围内",
                                        String.valueOf(alarmTactics.getLevel()), controllName);
                            }
                        } else if (rangeType == 3) {
                            //3：上线
                            if (0 < itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getOver()))) {
                                //添加报警记录
                                resultBoolean = insertAlarmRealtimeData(alarmTactics, itemData, equipment,
                                        alarmTactics.getName() + "报警", String.valueOf(alarmTactics.getOver()),
                                        controllName + ":" + equipment.getName() + ":" + itemData.getName() + ":" + alarmTactics.getName() + ",实时值超出上限值",
                                        String.valueOf(alarmTactics.getLevel()), controllName);
                            }
                        } else if (rangeType == 4) {
                            //4：下限
                            if (0 > itemData.getDataValue().compareTo(new BigDecimal(alarmTactics.getUnder()))) {
                                //添加报警记录
                                resultBoolean = insertAlarmRealtimeData(alarmTactics, itemData, equipment,
                                        alarmTactics.getName() + "报警", String.valueOf(alarmTactics.getUnder()),
                                        controllName + ":" + equipment.getName() + ":" + itemData.getName() + ":" + alarmTactics.getName() + ",实时值低于下限值",
                                        String.valueOf(alarmTactics.getLevel()), controllName);
                            }
                        } else {
                            continue;
                        }

                    } else {
                        continue;
                    }
                }
            }
        }

    }

    public void deviceState(String ip, int post, Long id, Boolean state) {
        if (!StringUtils.hasText(ip)) {
            log.warn("ip 地址不存在");
            return;
        }

        try {

            Map<String, Equipment> controllerList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

            if (controllerList == null || controllerList.size() == 0) {
                return;
            }
            controllerList.values().forEach(val -> {

                if (val.getId().equals(id)) {

                    String deviceState = val.getState();

                    if (!state && deviceState.equals("1")) {//"0"离线  "1" 在线
                        val.setState("0");
                        if ("1".equals(val.getOfflineAlarm())) {
//                            //是设备离线报警 则添加报警记录
                            Date nowDate = new Date();
                            Integer countNumber = 1;
                            String controllName = "";
                            if (val.getpId() != null) {
                                Equipment cacheMapValue = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, val.getpId());
                                controllName = cacheMapValue.getName();
                            }
                            //是否由设备离线报警记录
                            AlarmRealtimeData alarmRealtimeDataSelect = new AlarmRealtimeData(null, controllName + ":" + val.getName(), "1", "5");
                            alarmRealtimeDataSelect.setEquipmentId(val.getId());
                            List<AlarmRealtimeData> alarmRealtimeDataCount = alarmRealtimeDataMapper.selectAlarmRealtimeDataStateList(alarmRealtimeDataSelect);
                            if (alarmRealtimeDataCount == null || alarmRealtimeDataCount.size() == 0) {
                                //告警策略ID 告警策略Name 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型
                                AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData(null, "设备离线报警", "0",
                                        nowDate, nowDate, countNumber, 0L, controllName + ":" + val.getName(), "设备离线报警",
                                        "1", controllName + ":" + val.getName() + ",设备离线报警", "1", "5");
                                //添加实时报警
                                alarmRealtimeData.setCreateTime(nowDate);
                                alarmRealtimeData.setEquipmentId(val.getId());
                                alarmRealtimeDataMapper.insertAlarmRealtimeData(alarmRealtimeData);
                            } else {
                                countNumber = countNumber + alarmRealtimeDataCount.get(0).getAmount();
                                //告警策略ID 告警策略Name 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型
                                AlarmRealtimeData alarmRealtimeData = new AlarmRealtimeData();
                                alarmRealtimeData.setId(alarmRealtimeDataCount.get(0).getId());
                                alarmRealtimeData.setAmount(countNumber);
                                alarmRealtimeData.setLastTime(nowDate);
                                //修改实时报警
                                alarmRealtimeDataMapper.updateAlarmRealtimeDataById(alarmRealtimeData);
                            }

                            //是否是第一次告警工单
                            AlarmWorkOrder alarmWorkOrderSelect = new AlarmWorkOrder(null, controllName + ":" + val.getName(), "1", "5");
                            List<AlarmWorkOrder> alarmWorkOrderCount = alarmWorkOrderMapper.selectAlarmWorkOrderStateList(alarmWorkOrderSelect);

                            if (alarmWorkOrderCount == null || alarmWorkOrderCount.size() == 0) {
                                //根据设备-园区-查出用户
                                String userId = "1";
                                Park park = parkMapper.selectParkByCode(val.getParkCode());
                                if (!"1".equals(park.getUserName())) {
                                    userId = userId + "," + park.getUserName();
                                }

                                //告警策略ID 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型 所属用户id 状态
                                AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder(null, "设备离线报警", "0",
                                        nowDate, nowDate, 1, 0L, controllName + ":" + val.getName(), "设备离线报警",
                                        "1", controllName + ":" + val.getName() + ",设备离线报警", "1", "5", userId, "0");

                                //添加告警工单
                                alarmWorkOrder.setCreateTime(nowDate);
                                alarmWorkOrder.setEquipmentId(val.getId());
                                alarmWorkOrderMapper.insertAlarmWorkOrder(alarmWorkOrder);
                            } else {
                                //告警策略ID 告警值 第一次产生时间 最后一次产生时间 告警次数 确认状态 告警位置 报警名称 计划值 提示信息 告警等级 所属告警类型 所属用户id 状态
                                AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder();
                                alarmWorkOrder.setId(alarmWorkOrderCount.get(0).getId());
                                alarmWorkOrder.setAmount(1 + alarmWorkOrderCount.get(0).getAmount());
                                alarmWorkOrder.setLastTime(nowDate);

                                //修改告警工单
                                alarmWorkOrderMapper.updateAlarmWorkOrder(alarmWorkOrder);
                            }

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
