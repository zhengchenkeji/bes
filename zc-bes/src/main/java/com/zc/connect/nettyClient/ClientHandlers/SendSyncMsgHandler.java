package com.zc.connect.nettyClient.ClientHandlers;

import com.ruoyi.common.core.redis.RedisCache;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.secret.smutil.Util;
import com.zc.connect.business.handler.CrcUtil;
import com.zc.connect.config.SyncFuture;
import com.zc.connect.nettyClient.NettyClient;
import com.zc.connect.util.StringUtil;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.ProductFunction;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * 下位机消息发送类
 *
 * @author xiepufeng
 * @date 2020/4/15 8:26
 */
@Component
public class SendSyncMsgHandler {

    private static final Logger log = Logger.getLogger(SendSyncMsgHandler.class.getName());

    @Autowired
    private RedisCache redisCache;

    /**
     * @param message
     * @Description: 消息发送
     * @Author:杨攀
     * @Since: 2019年9月12日下午5:08:47
     */
    public void sendMsg(String message, String HOST, int PORT) {


        ChannelId channelId1 = NettyClient.channels.get(HOST + ":" + PORT);
        Channel channel1 = NettyClient.clients.find(channelId1);


        ByteBuf byteBufs = Unpooled.buffer();
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("55")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("aa")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("80")));
        byteBufs.writeShort(Integer.valueOf(StringUtil.hexToDecimal("ffff")));
        byteBufs.writeShort(Integer.valueOf(StringUtil.hexToDecimal("012d")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("0")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("6")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("0")));

        byteBufs.writeShort(Integer.valueOf(StringUtil.hexToDecimal(CrcUtil.validateStringH("80ffff012d000600"))));
//        byteBufs.writeShort(Integer.valueOf(StringUtil.hexToDecimal(String.valueOf(CrcUtil.getCRC(bytes1)))));
//        ctx.channel().writeAndFlush(byteBufs);
//        //netty需要用ByteBuf传输
//        ByteBuf byteBuf = Unpooled.buffer();
//        // 字符串传输
//        byteBuf.writeShort(10);
//        byteBuf.writeShort(0);
//        byteBuf.writeShort(6);
//        byte slave_id = 1;
//        byteBuf.writeByte(slave_id);
//        byte funcotion_code = 3;
//        byteBuf.writeByte(funcotion_code);
//        byteBuf.writeShort(0);
//        byteBuf.writeShort(10);

        ChannelFuture future = channel1.writeAndFlush(byteBufs);

//        ByteBuf byteBuf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
//        ChannelFuture future = socketChannel.writeAndFlush(byteBuf);

        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

                if (future.isSuccess()) {
                    System.out.println("===========发送成功");
                } else {
                    System.out.println("------------------发送失败");
                }
            }
        });
    }

    /**
     * @param message
     * @Description: 发送同步消息
     * @Author:杨攀
     * @Since: 2019年9月12日下午5:08:47
     */
    public String sendSyncMsg(String message, String HOST, int PORT, SyncFuture<String> syncFuture) {

        String result = "";

        try {

            ChannelId channelId1 = NettyClient.channels.get(HOST + ":" + PORT);
            Channel channel1 = NettyClient.clients.find(channelId1);

            //netty需要用ByteBuf传输
            ByteBuf byteBuf = Unpooled.buffer();
            // 字符串传输
            byteBuf.writeShort(10);
            byteBuf.writeShort(0);
            byteBuf.writeShort(6);
            byte slave_id = 1;
            byteBuf.writeByte(slave_id);
            byte funcotion_code = 3;
            byteBuf.writeByte(funcotion_code);
            byteBuf.writeShort(0);
            byteBuf.writeShort(10);

            ChannelFuture future = channel1.writeAndFlush(byteBuf);

            future.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isSuccess()) {
                        System.out.println("===========发送成功");
                    } else {
                        System.out.println("------------------发送失败");
                    }
                }
            });

            // 等待 8 秒
            result = syncFuture.get(8, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * @Description: 发送给modbus服务端消息(网关设备)
     * @auther: wanghongjie
     * @date: 9:02 2023/3/15
     * @param: [equipment, host, port]
     * @return: void
     */
    public void sendModbusGatewaySyncMsg(Equipment equipment, String host, Integer port, SyncFuture<String> syncFuture) {

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
                issued(host,port,val,productItemDataList);
            } catch (InterruptedException e) {
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
    public void sendModbusDirectConnectionSyncMsg(Equipment val, String host, Integer port, SyncFuture<String> syncFuture) throws InterruptedException {


        //获取设备所有的数据项
        Map<String, ProductItemData> productItemDataList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);

        issued(host,port,val,productItemDataList);
    }


    /**
     *
     * @Description: 下发获取指令
     *
     * @auther: wanghongjie
     * @date: 15:45 2023/3/17
     * @param: [host, port, val, productItemDataList]
     * @return: void
     *
     */
    public void issued(String host, Integer port, Equipment val, Map<String, ProductItemData> productItemDataList) throws InterruptedException {
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
                Collections.sort(productItemData, new Comparator<ProductItemData>() {
                    @Override
                    public int compare(ProductItemData o1, ProductItemData o2) {
                        return Integer.valueOf(o1.getSortNum().intValue()) - Integer.valueOf(o2.getSortNum().intValue());
                    }
                });


                //获取起始字节
                int beginByte = Integer.valueOf(productItemData.get(0).getSortNum().intValue());
                //获取读取的字节数
                int byteSize = Integer.valueOf(productItemData.get(productItemData.size() - 1).getSortNum().intValue());
                //获取设备地址
                int slaveId = val.getSonAddress().intValue();
                //netty需要用ByteBuf传输
                ByteBuf byteBuf = Unpooled.buffer();
                byteBuf.writeShort(10);//交互（通信）标识：2个字节 为此次通信事务处理标识符，一般每次通信之后将被要求加1以区别不同的通信数据报文。
                byteBuf.writeShort(0);//协议标识：2个字节 表示该条指令遵循ModbusTCP协议，一般都为00 00
                byteBuf.writeShort(6);//报文长度：2个字节 表示后面数据的长度，有几个字节，高字节在前
                byteBuf.writeByte(slaveId);//设备标识 1个字节
                byteBuf.writeByte(key);//功能码 1个字节
                byteBuf.writeShort(beginByte);//起始地址
                byteBuf.writeShort(byteSize + 1);//读取多少个数据
                ChannelId channelId = NettyClient.channels.get(host + ":" + port);
                Channel channel = NettyClient.clients.find(channelId);
                ChannelFuture future = channel.writeAndFlush(byteBuf);

                Thread.sleep(500);
                future.addListener(new ChannelFutureListener() {

                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {

                        if (future.isSuccess()) {
//                            System.out.println("===========发送成功");
                        } else {
//                            System.out.println("------------------发送失败");
                        }
                    }
                });

            }
        }
    }


    /**
     *
     * @Description: 下发控制指令
     *
     * @auther: wanghongjie
     * @date: 11:18 2023/3/20
     * @param: [equipmentId(网关或者直连设备id), equipmentChildId(子设备id), productId(产品id), functionNum(功能编号), productFunctionId(功能定义编号)]
     * @return: void
     *
     */
    public void sendControlModbusGatewaySyncMsg(Long equipmentId,Long equipmentChildId,Long productId,String functionNum,Long productFunctionId) throws InterruptedException {

        AtomicReference<ProductFunction> productFunction = new AtomicReference<>(new ProductFunction());
        if (productFunctionId != null) {//产品功能id不为空
             productFunction.set(redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, productFunctionId));
        } else {

            if (productId == null || functionNum == null) {
                return;
            }
            //获取设备所有的功能定义
            Map<String, ProductFunction> productFunctionMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function);
            if(productFunctionMap == null || productFunctionMap.size() == 0) {
                return;
            }
            productFunctionMap.values().forEach(val -> {
                if (val.getProductId() == productId && val.getFunctionNum().equals(functionNum)) {
                    productFunction.set(val);
                }
            });
        }

        if (equipmentId == null || equipmentChildId == null) {
            return;
        }
        //获取网关或者直连设备
        Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,equipmentId);
        if (equipment == null) {
            return;
        }
        String host = equipment.getIpAddress();
        String port = equipment.getPortNum();

        //获取具体设备
        Equipment childEquipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,equipmentChildId);
        if (childEquipment == null) {
            return;
        }
        String deviceSlaveId = childEquipment.getIpAddress();
        if (deviceSlaveId == null) {
            return;
        }
        Integer slaveId = Integer.valueOf(deviceSlaveId);
        //获取功能类型;0-控制;1-采集
        String type = productFunction.get().getType();
        //获取下发方式;0-指令下发;1-数据项下发
        String issuedType = productFunction.get().getIssuedType();


        if (type == null || issuedType == null) {
            return;
        }


        Integer beginByte = null;

        Integer value = null;

        switch (issuedType) {
            case "0"://指令下发

                beginByte = Util.hexStringToAlgorism(productFunction.get().getInstruct().substring(1,5));
                value = Util.hexStringToAlgorism(productFunction.get().getInstruct().substring(5,9));;
                break;
            case "1"://数据项下发

                //获取数据项信息
                Long dataItem = productFunction.get().getDataItem();
                if (dataItem == null) {
                    return;
                }
                ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, dataItem);
                beginByte = Integer.valueOf(productItemData.getSortNum().intValue());
                value = Integer.valueOf(productFunction.get().getItemValue().toString()) ;
                break;
            default:
                break;
        }

        Integer code = null;
        switch (type) {
            case "0"://控制
                code = 6;
                break;
            case "1"://采集
                code = 3;
                break;

            default:
                break;
        }

        //netty需要用ByteBuf传输
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeShort(10);//交互（通信）标识：2个字节 为此次通信事务处理标识符，一般每次通信之后将被要求加1以区别不同的通信数据报文。
        byteBuf.writeShort(0);//协议标识：2个字节 表示该条指令遵循ModbusTCP协议，一般都为00 00
        byteBuf.writeShort(6);//报文长度：2个字节 表示后面数据的长度，有几个字节，高字节在前
        byteBuf.writeByte(slaveId);//设备标识 1个字节
        byteBuf.writeByte(code);//功能码 1个字节
        byteBuf.writeShort(beginByte);//寄存器地址
        byteBuf.writeShort(value);//下发值
        ChannelId channelId = NettyClient.channels.get(host + ":" + port);
        Channel channel = NettyClient.clients.find(channelId);
        ChannelFuture future = channel.writeAndFlush(byteBuf);

        Thread.sleep(500);
        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

                if (future.isSuccess()) {
                    System.out.println("===========发送成功");
                } else {
                    System.out.println("------------------发送失败");
                }
            }
        });
    }
}
