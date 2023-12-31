package com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler;

import cn.hutool.core.convert.Convert;
import com.ruoyi.common.constant.ModbusFunctions;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.secret.smutil.Util;
import com.zc.connect.business.dto.JsonMsg;
import com.zc.connect.util.MsgUtil;
import com.zc.connect.util.StringUtil;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.FunctionItemData;
import com.zc.efounder.JEnterprise.domain.baseData.ProductFunction;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import com.zc.efounder.JEnterprise.domain.deviceTree.PointControlCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 下位机消息发送类
 *
 * @author xiepufeng
 * @date 2020/4/15 8:26
 */
@Component
public class ModbusSendSyncMsgHandler {

    private static final Logger log = Logger.getLogger(ModbusSendSyncMsgHandler.class.getName());


    public static HashedWheelTimer timer = new HashedWheelTimer();//时间轮的度刻

    // 消息队列
    public final static Map<String, Queue<JsonMsg>> msgQueue = new HashMap<>();

    @Autowired
    private RedisCache redisCache;


    /**
     * 发送数据到客户端
     *
     * @param channelID 通道id
     * @param jsonMsg   消息体
     * @return
     */
    static boolean postEvent(String channelID, int port, JsonMsg jsonMsg) {

        if (channelID == null || channelID.isEmpty() || jsonMsg == null) {
            return false;
        }

        /**
         * 添加定时任务，清理队列中的超时任务
         *
         * 3秒后执行定时任务，检查此消息是否回复，
         * 如果队列中消息还存在则说明此消息发送后没有
         * 回复，那么就把此消息弹出消息队列，查看队列
         * 中是否有下一个消息，如果存在则发送出去
         *
         */
        TimerTask task = timeout ->
        {

            String uuid = jsonMsg.getUuid();

            Queue<JsonMsg> queue = msgQueue.get(channelID + ":" + port);

            if (queue == null || queue.isEmpty()) {
                return;
            }

            JsonMsg queueMsg = queue.peek();

            if (queueMsg == null) {
                return;
            }

            if (uuid.equals(queueMsg.getUuid())) {
                // 如果该消息还没有回复，则清除该消息
                queue.poll();

                JsonMsg nextMsg = queue.peek();

                if (nextMsg == null) {
                    return;
                }

                postEvent(channelID, port, nextMsg);

            }
        };

        // 三秒后执行定时任务
        timer.newTimeout(task, 3, TimeUnit.SECONDS);

        ChannelId channelId = ServerHandler_modbus.channels.get(channelID + ":" + port);
        Channel channel = ServerHandler_modbus.clients.find(channelId);

        if (channel == null) {
            log.warning("Post event failed, because the channel for event subscriber:" + channelID + " is not exists");
            return false;
        }

        ByteBuf msg = (ByteBuf) jsonMsg.getData();

//        msg = CrcUtil.addVerifyCRC(msg);

        channel.writeAndFlush(msg);

        return true;
    }

    /**
     * 发送数据到客户端
     *
     * @param channelID 通道id
     * @param jsonMsg   消息体
     * @return
     */
    static boolean sendEvent(String channelID, JsonMsg jsonMsg) {

        if (channelID == null || channelID.isEmpty() || jsonMsg == null) {
            return false;
        }

        ChannelId channelId = ServerHandler_modbus.channels.get(channelID);
        Channel channel = ServerHandler_modbus.clients.find(channelId);

        if (channel == null) {
            log.warning("Post event failed, because the channel for event subscriber:" + channelID + " is not exists");
            return false;
        }

        ByteBuf msg = (ByteBuf) jsonMsg.getData();

//        msg = CrcUtil.addVerifyCRC(msg);

        channel.writeAndFlush(msg);

        return true;
    }


    /**
     * 消息入栈等待发送
     * <p>
     * 1.判断通告是否存在，不存在则返回 false
     * 2.如果该通道没有消息队列，则创建
     * 3.如果消息队列中没有消息则直接发送消息
     * 4.把消息添加到消息队列
     *
     * @param channelID
     * @param jsonMsg
     * @return
     */
    public static boolean pushStack(String channelID, int port, JsonMsg jsonMsg) {

        if (channelID == null || channelID.isEmpty() || jsonMsg == null) {
            return false;
        }

        ChannelId channelId = ServerHandler_modbus.channels.get(channelID + ":" + port);
        if (channelId == null) {
            return false;
        }
        Channel channel = ServerHandler_modbus.clients.find(channelId);

        Queue<JsonMsg> queue = msgQueue.computeIfAbsent(channelID + ":" + port, k -> new ConcurrentLinkedQueue<>());

        if (channel == null) {
            queue.clear();
            log.warning("Post event failed, because the channel for event subscriber:" + channelID + " is not exists");
            return false;
        }

        if (queue.isEmpty()) {
            if (!ModbusSendSyncMsgHandler.postEvent(channelID, port, jsonMsg)) {
                return false;
            }
        }

        return queue.offer(jsonMsg);
    }

    /**
     * @Description: 发送给modbus服务端消息(网关设备)
     * @auther: wanghongjie
     * @date: 9:02 2023/3/15
     * @param: [equipment, host, port]
     * @return: void
     */
    public void sendModbusGatewaySyncMsg(Equipment equipment, String host, Integer port) {

        //获取当前产品下所有的设备
        Map<String, Equipment> equipmentList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

        List<Equipment> equipmentLists = new ArrayList<>();

        equipmentList.values().forEach(val1 -> {
            if (val1.getpId() == null) {
                return;
            }
            if (!val1.getpId().equals(equipment.getId())) {
                return;
            }
            equipmentLists.add(val1);


        });

        if (equipmentLists == null || equipmentLists.size() == 0) {//当前网关下没有子设备

            return;
        }

        //获取设备所有的数据项
        Map<String, ProductItemData> productItemDataList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);

        //网关设备
        equipmentLists.forEach(val -> {
            try {
                issued(host, port, val, productItemDataList);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * @Description: 发送给modbus服务端消息(直连设备)
     * @auther: wanghongjie
     * @date: 9:02 2023/3/15
     * @param: [equipment, host, port]
     * @return: void
     */
    public void sendModbusDirectConnectionSyncMsg(Equipment val, String host, Integer port) throws NoSuchAlgorithmException {


        //获取设备所有的数据项
        Map<String, ProductItemData> productItemDataList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);

        issued(host, port, val, productItemDataList);
    }


    public void issued(String host, Integer port, Equipment val, Map<String, ProductItemData> productItemDataList) throws NoSuchAlgorithmException {
        if (1 == 1) {
            return;
        }
        Map<Integer, List<ProductItemData>> productItemDataMap = new HashMap<>();
        productItemDataList.values().forEach(val2 -> {
            if (val2.getProductId() == val.getProductId()) {

                if (!productItemDataMap.containsKey(Integer.valueOf(val2.getFunctionCode()))) {//当前key不存在
                    List<ProductItemData> productItemData = new ArrayList<>();
                    productItemData.add(val2);
                    productItemDataMap.put(Integer.valueOf(val2.getFunctionCode()), productItemData);
                } else {
                    List<ProductItemData> productItemData = productItemDataMap.get(Integer.valueOf(val2.getFunctionCode()));
                    productItemData.add(val2);
                }
            }

        });

        Set<Integer> keys = productItemDataMap.keySet();
        for (Integer key : keys) {//遍历功能码

            List<ProductItemData> productItemData = productItemDataMap.get(key);
            if (productItemData.size() != 0) {


                /**过滤排序数据*/
                productItemData = productItemData.stream()
                        .filter(itemData -> itemData.getItemDataId() == null)
                        .sorted(Comparator.comparing(ProductItemData::getSortNum))
                        .collect(Collectors.toList());

                if (productItemData.size() == 0) {
                    continue;
                }
//                        .collect(Collectors.toList());
//                Collections.sort(productItemData, new Comparator<ProductItemData>() {
//                    @Override
//                    public int compare(ProductItemData o1, ProductItemData o2) {
//                        return o1.getSortNum().intValue() - o2.getSortNum().intValue();
//                    }
//                });

                if (productItemData == null || productItemData.size() == 0) {
                    continue;
                }

                //获取起始字节
                int beginByte = Integer.valueOf(productItemData.get(0).getSortNum().intValue());
                //获取读取的字节数
//                int byteSize = Integer.valueOf(productItemData.get(productItemData.size() - 1).getSortNum());
                int byteSize = 20;
                //获取设备地址
                int slaveId = val.getSonAddress().intValue();
                //netty需要用ByteBuf传输
                ByteBuf byteBuf = Unpooled.buffer();

                // 创建 SecureRandom 对象，并设置加密算法
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

                int TransActionId = random.nextInt(65535);
                int protocal = random.nextInt(65535);
                byteBuf.writeShort(TransActionId);//交互（通信）标识：2个字节 为此次通信事务处理标识符，一般每次通信之后将被要求加1以区别不同的通信数据报文。
                byteBuf.writeShort(protocal);//协议标识：2个字节 表示该条指令遵循ModbusTCP协议，一般都为00 00
                byteBuf.writeShort(6);//报文长度：2个字节 表示后面数据的长度，有几个字节，高字节在前
                byteBuf.writeByte(slaveId);//设备标识 1个字节
                byteBuf.writeByte(3);//功能码 1个字节
                byteBuf.writeShort(beginByte);//起始地址
                byteBuf.writeShort(byteSize + 1);//读取多少个数据
                JsonMsg jsonMsg = new JsonMsg();
                jsonMsg.setUuid(String.valueOf(TransActionId) + String.valueOf(protocal));
                jsonMsg.setData(byteBuf);
                jsonMsg.setRegisterBeginAddress(beginByte);
                jsonMsg.setRegisterLength(byteSize + 1);
                pushStack(host, port, jsonMsg);

            }
        }
    }

    /**
     * @Description: 自定义数据下发(采集)
     * @auther: wanghongjie
     * @date: 15:23 2023/4/10
     * @param: [host, port, equipment, functionCode, beginAddress, addressSize, valueList]
     * @return: void
     */
    public synchronized void issued2(String host, Integer port, Equipment equipment, Integer functionCode, Integer beginAddress,
                                     Integer addressSize, Boolean booHighlLow, List<PointControlCommand> valueList) throws NoSuchAlgorithmException {
        if (StringUtils.isEmpty(host) || port == null || functionCode == null || beginAddress == null || addressSize == null) {
            return;
        }
        //获取设备地址
        Long sloveId = equipment.getSonAddress();
        if (sloveId == null) {
            return;
        }
        // 创建 SecureRandom 对象，并设置加密算法
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        //netty需要用ByteBuf传输
        ByteBuf byteBuf = Unpooled.buffer();

        int TransActionId = random.nextInt(65535);
        int protocal = random.nextInt(65535);
        byteBuf.writeShort(TransActionId);
        byteBuf.writeShort(protocal);


        switch (functionCode) {
            case ModbusFunctions.COIL_STATUS:
            case ModbusFunctions.INPUT_STATUS:
            case ModbusFunctions.HOLDING_REGISTER:
            case ModbusFunctions.INPUT_REGISTER:

                byteBuf.writeShort(6);//后面的数据长度
                byteBuf.writeByte(sloveId.intValue());//设备地址
                byteBuf.writeByte(functionCode);//功能码
                byteBuf.writeShort(beginAddress);//起始寄存器地址
                byteBuf.writeShort(addressSize); //寄存器数量
                break;

            case ModbusFunctions.WRITE_ONE_COIL:
            case ModbusFunctions.WRITE_ONE_HOLDING_REGISTER:
            case ModbusFunctions.WRITE_MULTIPLE_COIL:
            case ModbusFunctions.WRITE_MULTIPLE_HOLDING_REGISTER:

                byteBuf.writeShort(7 + (2 * addressSize));//后面的数据长度
                byteBuf.writeByte(sloveId.intValue());//设备地址
                byteBuf.writeByte(functionCode);//功能码
                byteBuf.writeShort(beginAddress);//起始寄存器地址
                byteBuf.writeShort(addressSize); //寄存器数量
                byteBuf.writeByte(2 * addressSize);//后面的字节数
                for (PointControlCommand pointControlCommand : valueList) {
                    if (!booHighlLow) {//不是高低位
                        byteBuf.writeShort(pointControlCommand.getValue().intValue());

                    } else {
                        byteBuf.writeByte(pointControlCommand.getValue().intValue());
                    }
                }
                break;
        }


        JsonMsg jsonMsg = new JsonMsg();

        //有数据时开始读数据包
        jsonMsg.setUuid(String.valueOf(TransActionId) + String.valueOf(protocal));
        jsonMsg.setData(byteBuf);
//        System.out.println(MsgUtil.convertByteBufToString(byteBuf));
        jsonMsg.setRegisterBeginAddress(beginAddress);
        jsonMsg.setRegisterLength(addressSize);
        pushStack(host, port, jsonMsg);
    }

    /**
     * @Description:发送到下位机
     * @auther: wanghongjie
     * @date: 17:55 2023/3/27
     * @param: [host, port, val]
     * @return: void
     */
    public synchronized void issued1(String host, Integer port, Equipment equipment, Integer code, Long functionId, List<PointControlCommand> valueList) throws NoSuchAlgorithmException {

        if (StringUtils.isEmpty(host) || port == null || functionId == null) {
            return;
        }
        //获取设备地址
        Long sloveId = equipment.getSonAddress();
        if (sloveId == null) {
            return;
        }

        //获取设备所有的功能
        ProductFunction productFunction = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, functionId);

        String type = productFunction.getType();//功能类型;0-控制;1-采集;

        String issuedType = productFunction.getIssuedType();//下发方式;0-指令下发;1-数据项下发

        Long productId = productFunction.getProductId();//产品主键

        Long registerNumber = productFunction.getRegisterNumber();//寄存器数量

        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(issuedType)) {
            return;
        }

        // 创建 SecureRandom 对象，并设置加密算法
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        //netty需要用ByteBuf传输
        ByteBuf byteBuf = Unpooled.buffer();

        int TransActionId = random.nextInt(65535);
        int protocal = random.nextInt(65535);
        byteBuf.writeShort(TransActionId);
        byteBuf.writeShort(protocal);
        //判断是指令下发还是数据项下发
        //如果是指令下发，解析以及拼接指令
        if (issuedType.equals("0") ) {
            String instruct = productFunction.getInstruct();//指令码
            String instructFinal = productFunction.getInstruct();//指令码
            if (StringUtils.isEmpty(instruct)) {
                return;
            }
            String functionCode = instruct.substring(1, 3);
            int functionCodeInt = Integer.parseInt(functionCode, 16);

            String beginAddress = null;
            String addressSize = null;
            int beginAddressInt = 0;
            int addressSizeInt = 0;


            if (code != null) {
                functionCodeInt = code;
            }


            switch (functionCodeInt) {
                case ModbusFunctions.COIL_STATUS:
                case ModbusFunctions.INPUT_STATUS:
                case ModbusFunctions.HOLDING_REGISTER:
                case ModbusFunctions.INPUT_REGISTER:

                    beginAddress = instruct.substring(3, 7);
                    addressSize = instruct.substring(7, 11);
                    beginAddressInt = Integer.parseInt(beginAddress, 16);
                    addressSizeInt = Integer.parseInt(addressSize, 16);

                    System.out.println("beginAddressInt:" + beginAddressInt);
                    System.out.println("addressSizeInt:" + addressSizeInt);
                    issued2(host, port, equipment, functionCodeInt, beginAddressInt, addressSizeInt, false, null);

                    break;
                case ModbusFunctions.WRITE_ONE_COIL:
                case ModbusFunctions.WRITE_ONE_HOLDING_REGISTER:
                case ModbusFunctions.WRITE_MULTIPLE_COIL:
                case ModbusFunctions.WRITE_MULTIPLE_HOLDING_REGISTER:

                    beginAddress = instruct.substring(3, 7);
                    addressSize = instruct.substring(7, 11);
                    beginAddressInt = Integer.parseInt(beginAddress, 16);
                    addressSizeInt = Integer.parseInt(addressSize, 16);


                    List<PointControlCommand> valueLists = new ArrayList<>();
                    //设置年月日时分秒
                    Date d = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    String timeStr = sdf.format(d);
                    instruct = instruct.replace("YYMMDDHHmmSS", timeStr.substring(2));
                    //设置周几
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    String[] weekDays = {"07", "01", "02", "03", "04", "05", "06"};
                    int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    if (w < 0) {
                        w = 0;
                    }
                    instruct = instruct.replace("WW", weekDays[w]);


                    for (int i = 0; i < addressSizeInt; i++) {

                        //判断当前寄存器是否高低位

                        //根据功能id和数据项id查询数据value
                        Map<String, ProductItemData> productItemDataMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);


                        if (productItemDataMap == null) {
                            return;
                        }

                        List<ProductItemData> productItemDataList = new ArrayList<>();

                        for (ProductItemData productItemData : productItemDataMap.values()) {
                            if (productItemData.getProductId().equals(productId) && productItemData.getSortNum() != null) {//产品主键是否相等
                                productItemDataList.add(productItemData);
                            }
                        }

                        //寄存器排序
                        Collections.sort(productItemDataList, new Comparator<ProductItemData>() {
                            @Override
                            public int compare(ProductItemData o1, ProductItemData o2) {
                                return o1.getSortNum().intValue() - o2.getSortNum().intValue();
                            }
                        });

                        PointControlCommand pointControlCommand = new PointControlCommand();

                        Boolean booDistribute = false;

                        for (ProductItemData productItemData : productItemDataList) {
                            if (productItemData.getSortNum().intValue() == (beginAddressInt + i)) {
                                //获取高低位
                                String highLow = productItemData.getHighLow();

                                if (highLow.equals("1")) {//是高低位
                                    PointControlCommand highPointControlCommand = new PointControlCommand();

                                    PointControlCommand lowPointControlCommand = new PointControlCommand();

                                    highPointControlCommand.setValue(Double.valueOf(instruct.substring(13 + (i * 4), 15 + (i * 4))));
                                    lowPointControlCommand.setValue(Double.valueOf(instruct.substring(15 + (i * 4), 17 + (i * 4))));

                                    valueLists.add(highPointControlCommand);
                                    valueLists.add(lowPointControlCommand);

                                    if (valueList != null && valueList.size() > 0) {
                                        valueLists = valueList.subList(0 + (i * 2), 2 + (i * 2));
                                    }

                                    if (i == addressSizeInt-1 && instructFinal.contains("YYMMDDHHmmSS")) {
                                        issued2(host, port, equipment, functionCodeInt, beginAddressInt, i + 1, true, valueLists);
                                    }else if(!instructFinal.contains("YYMMDDHHmmSS")){
                                        issued2(host, port, equipment, functionCodeInt, beginAddressInt + i, i + 1, true, valueLists);
                                    }


                                    booDistribute = true;

                                }
                            }
                        }

                        if (!booDistribute) {
                            String StrValue = instruct.substring(13 + (i * 4), 17 + (i * 4));
//                            System.out.println("指令信息："+instruct+",截取的值："+ StrValue +",首次截取："+(13 + (i * 4))+",二次截取："+(17 + (i * 4)));
                            pointControlCommand.setValue(Double.valueOf(StringUtil.hexToDecimal(StrValue)));
//                            System.out.println("存入pointControlllerCommand的值："+pointControlCommand.getValue());
                            valueLists.add(pointControlCommand);

                            if (valueList != null && valueList.size() > 0) {
//                                valueLists = valueList.subList(0 + i, 1 + i);
                                issued2(host, port, equipment, functionCodeInt, beginAddressInt + i, 1, false, valueList.subList(0 + i, 1 + i));
                            } else {
                                issued2(host, port, equipment, functionCodeInt, beginAddressInt + i, 1, false, valueLists);
                            }


                        }

                    }


                    break;
                default:
                    break;
            }
        }

        //如果是数据项下发，给数据项赋值
        if ((issuedType.equals("1") && valueList == null) || issuedType.equals("1") || (valueList != null && valueList.size() > 0)) {
            int beginAddressInt = 0;

            String dataItem = String.valueOf(productFunction.getDataItem());//获取数据项主键

            if (dataItem == null || "null".equals(dataItem)) {
                String instruct = productFunction.getInstruct();
                //寄存器地址
                int startIndex = Integer.parseInt(StringUtil.hexToDecimal(instruct.substring(3, 7)));
                //根据寄存器地址获取数据项
                Collection<Object> valuesItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
                for (Object o : valuesItemData) {
                    ProductItemData productItemDataFor = (ProductItemData) o;
                    if (productItemDataFor.getSortNum() != null && productItemDataFor.getSortNum().intValue() == startIndex
                            && productItemDataFor.getProductId().equals(equipment.getProductId())) {
                        //数据项ID
                        dataItem = productItemDataFor.getId().toString();
                    }
                }
            }


            if (StringUtils.isEmpty(dataItem)) {
                return;
            }

            ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, Long.valueOf(dataItem));

            if (productItemData == null) {
                return;
            }

            //获取寄存器地址
            beginAddressInt = productItemData.getSortNum().intValue();


            String functionType = productFunction.getType();//获取功能类型

            if (code != null) {
                functionType = "1";
            }

            //获取遍历缓存中的数据项id
            Map<String, ProductItemData> productItemDataMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);

            Long itemDataId = productItemData.getId();//获取寄存器的数据项id

            //判断功能类型，"0=控制,1=采集"
            switch (functionType) {
                case "0"://控制

                    List<ProductItemData> productItemDataList = new ArrayList<>();

                    List<PointControlCommand> valueLists = new ArrayList<>();



                    //获取高低位
                    String highLow = productItemData.getHighLow();
                    if (highLow.equals("1")) {//是高低位

                        for (ProductItemData productItemData1 : productItemDataMap.values()) {
                            if (productItemData1.getItemDataId() == null) {
                                continue;
                            }

                            if (itemDataId.equals(productItemData1.getItemDataId())) {
                                productItemDataList.add(productItemData1);
                            }
                        }

                        //高低位排序
                        Collections.sort(productItemDataList, new Comparator<ProductItemData>() {
                            @Override
                            public int compare(ProductItemData o1, ProductItemData o2) {
                                return Integer.parseInt(o1.getParamsType()) - Integer.parseInt(o2.getParamsType());
                            }
                        });

                        //获取高位数据项的id
                        for (ProductItemData productItemData1 : productItemDataList) {
                            Long highDetailId = productItemData1.getId();
                            //根据功能id和数据项id查询数据value
                            Map<String, FunctionItemData> functionItemDataMap =
                                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData);

                            if (functionItemDataMap == null) {
                                continue;
                            }

                            for (FunctionItemData functionItemData : functionItemDataMap.values()) {
                                if (functionItemData.getFunctionId().equals(functionId)
                                        && functionItemData.getItemDataId().equals(highDetailId)) {

                                    Integer values = functionItemData.getSendValue().intValue();

                                    PointControlCommand pointControlCommand = new PointControlCommand();
                                    pointControlCommand.setValue((double) values);
                                    valueLists.add(pointControlCommand);
                                    break;

                                }
                            }
                        }

                        if (valueList != null && valueList.size() > 0) {
                            valueLists = valueList;
                        }

                        issued2(host, port, equipment, Integer.valueOf(productItemData.getWriteFunctionCode()), beginAddressInt, 1, true, valueLists);
                    }

                    if (productItemData.getDataType().equals("7")) {//结构体

                        for (ProductItemData productItemData1 : productItemDataMap.values()) {
                            if (productItemData1.getItemDataId() == null) {
                                continue;
                            }

                            if (itemDataId.equals(productItemData1.getItemDataId())) {
                                productItemDataList.add(productItemData1);
                            }
                        }

                        //结构体排序
                        Collections.sort(productItemDataList, new Comparator<ProductItemData>() {
                            @Override
                            public int compare(ProductItemData o1, ProductItemData o2) {
                                return o2.getId().intValue() - o1.getId().intValue();
                            }
                        });

                        //遍历数据项所对应的结构体
                        for (ProductItemData productItemData1 : productItemDataList) {

                            //根据功能id和数据项id查询数据value
                            Map<String, FunctionItemData> functionItemDataMap =
                                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData);

                            if (functionItemDataMap == null) {
                                continue;
                            }

                            String itemData = "";
                            for (FunctionItemData functionItemData : functionItemDataMap.values()) {
                                if (functionItemData.getFunctionId().equals(functionId)
                                        && functionItemData.getItemDataId().equals(productItemData1.getId())) {
                                    Integer values = functionItemData.getSendValue().intValue();
                                    itemData = itemData + values;
                                }
                            }
                            Integer itemDataInt = Util.binaryToAlgorism(itemData);


                            PointControlCommand pointControlCommand = new PointControlCommand();
                            pointControlCommand.setValue((double) itemDataInt);
                            valueLists.add(pointControlCommand);

                        }

                        if (valueList != null && valueList.size() > 0) {
                            valueLists = valueList;
                        }

                        issued2(host, port, equipment, Integer.valueOf(productItemData.getWriteFunctionCode()), beginAddressInt, 1, false, valueLists);
                    }

                    if (!highLow.equals("1") && !productItemData.getDataType().equals("7")) {//不是高低位也不是结构体,说明就是单独寄存器十进制的值
                        //根据功能id和数据项id查询数据value
                        Map<String, FunctionItemData> functionItemDataMap =
                                redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData);

                        if (functionItemDataMap == null) {
                            return;
                        }

                        for (FunctionItemData functionItemData : functionItemDataMap.values()) {

                            if (functionItemData.getFunctionId().equals(functionId)
                                    && functionItemData.getItemDataId().equals(itemDataId)) {

                                Integer values = functionItemData.getSendValue().intValue();
                                PointControlCommand pointControlCommand = new PointControlCommand();
                                pointControlCommand.setValue((double) values);
                                valueLists.add(pointControlCommand);

                            }
                        }

                        if (valueList != null && valueList.size() > 0) {
                            valueLists = valueList;
                        }

                        issued2(host, port, equipment, Integer.valueOf(productItemData.getWriteFunctionCode()), beginAddressInt, 1, false, valueLists);
                    }

                    break;
                case "1"://采集

                    //获取寄存器数量
//                    Integer sortSize = productFunction.getRegisterNumber().intValue();

                    if (code == null) {
                        code = Integer.valueOf(productItemData.getWriteFunctionCode());
                    }

                    //获取寄存器数量
                    registerNumber = productItemData.getRegistersNum().longValue();
                    issued2(host, port, equipment, code, beginAddressInt, registerNumber.intValue(), false, null);
//                    byteBuf.writeShort(6);
//                    byteBuf.writeByte(sloveId.intValue());
//                    byteBuf.writeByte(3);
//                    byteBuf.writeShort(beginAddressInt);
//                    byteBuf.writeShort(sortSize);
                    break;
                default:
                    break;
            }
        }
    }
}
