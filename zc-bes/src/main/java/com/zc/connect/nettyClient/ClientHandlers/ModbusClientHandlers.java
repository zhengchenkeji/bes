package com.zc.connect.nettyClient.ClientHandlers;

import com.ruoyi.common.constant.AcrelCommandCode;
import com.ruoyi.common.constant.AcrelDeviceTypeCode;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.ApplicationContextProvider;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.connect.business.handler.CrcUtil;
import com.zc.connect.business.handler.ModbusMsgReceive;
import com.zc.connect.nettyClient.ClientHandler;
import com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler.ModbusServerHandlers;
import com.zc.connect.util.MsgUtil;
import com.zc.connect.util.StringUtil;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import com.zc.efounder.JEnterprise.domain.baseData.ProductItemData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 14:01 2023/2/23
 * @Modified By:
 */
@Component
public class ModbusClientHandlers {
    private static final Logger log = LoggerFactory.getLogger(ModbusClientHandlers.class);

    private SendSyncMsgHandler sendSyncMsgHandler = ApplicationContextProvider.getBean(SendSyncMsgHandler.class);

    //redis
    private RedisCache redisCache = ApplicationContextProvider.getBean(RedisCache.class);

    ModbusMsgReceive clientMsgReceive = ClientHandler.modbusMsgReceive;

    private ModbusServerHandlers modbusServerHandlers = ApplicationContextProvider.getBean(ModbusServerHandlers.class);

    /**
     * 回令业务处理
     * 解析
     */
    public void response_analysis(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        ByteBuf byteBuf = (ByteBuf) msg;
        List<Equipment> equipmentList = refreshDevice();
        if (equipmentList == null || equipmentList.size() == 0) {
            return;
        }
        String msgs = MsgUtil.convertByteBufToString(byteBuf);

        System.out.println(msgs);


        String TransActionId = msgs.substring(0, 4);//帧起始符

        if ("55AA".equalsIgnoreCase(TransActionId)) {//acrel自定义协议

            acrelReturnInstructions(ctx);

        } else if ("55BB".equalsIgnoreCase(TransActionId)) {

            ByteBuf byteBufs = Unpooled.buffer();
            ctx.channel().writeAndFlush(byteBufs);

        } else {

            acrelReturnInstructions(ctx);
        }

        String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();


        //根据ip和端口判断是否是特殊协议设备(例如安科瑞自有协议)
        AjaxResult ajaxResult = getProductNumber(host, post);

        if ((Integer) ajaxResult.get("code") != 200) {
            return;
        }
        //获取产品的编号(根据编号判断自有协议或者标准modbus协议等等)
        Map data = (Map) ajaxResult.get("data");

        String productCode = String.valueOf(data.get("productCode"));
        Equipment equipment = (Equipment) data.get("equipment");

        if (productCode.equalsIgnoreCase("acrel_Custom")) {//安科瑞自有协议

            acrelCustomChild(ctx, msgs, equipment);
            return;
        }
    }

    /**
     * @Description: 安科瑞自有协议解析
     * @auther: wanghongjie
     * @date: 17:08 2023/6/7
     * @param: [ctx, msg]
     * @return: void
     */
    public void acrelCustomChild(ChannelHandlerContext ctx, String msg, Equipment equipment) throws InterruptedException {

        String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
        int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();


        String TransActionId = msg.substring(0, 4);//帧起始符

        if ("55AA".equalsIgnoreCase(TransActionId)) {//acrel自定义协议

            //拆包判断
            String[] msgs = msg.split("55aa");
            for (int i = 0; i < msgs.length; i++) {
                if (i == 0) {
                    continue;
                }

                //获取设备类型
                String deviceTypeCode = msgs[i].substring(10, 12);

                //获取命令码
                String CommandCode = msgs[i].substring(12, 14);

                if ((deviceTypeCode.equalsIgnoreCase(AcrelDeviceTypeCode.IP_PROTOCOL_CONVERTER) &&//42  IP协议转换器
                        CommandCode.equalsIgnoreCase(AcrelCommandCode.HEART_BEAT))//  6  心跳
                ) {//心跳

//                    acrelReturnInstructions(ctx);

                }

                if ((deviceTypeCode.equalsIgnoreCase(AcrelDeviceTypeCode.HOST_DEVICE) &&// 10  主机设备
                        CommandCode.equalsIgnoreCase(AcrelCommandCode.READ_GROUP_OBJECT_STATUS))//89  读取组对象状态
                        ||
                        (deviceTypeCode.equalsIgnoreCase(AcrelDeviceTypeCode.HOST_DEVICE) &&//10 主机设备
                                CommandCode.equalsIgnoreCase(AcrelCommandCode.ACTION_GROUP_OBJECT))//88 操作组对象
                        ||
                        (deviceTypeCode.equalsIgnoreCase(AcrelDeviceTypeCode.SENSOR) &&//30 传感器
                                CommandCode.equalsIgnoreCase(AcrelCommandCode.RESET_DEVICE))//08 RESET 设备  上行/下行
                        ||
                        (deviceTypeCode.equalsIgnoreCase(AcrelDeviceTypeCode.SENSOR) &&//30 传感器
                                CommandCode.equalsIgnoreCase(AcrelCommandCode.CONTROL_LOOP))//8 控制回路
                ) {//心跳

//                    acrelReturnInstructions(ctx);

                }


                //判断传感器指令动作
                if (deviceTypeCode.equalsIgnoreCase(AcrelDeviceTypeCode.SENSOR) &&  //30 传感器
                        CommandCode.equalsIgnoreCase(AcrelCommandCode.ACTION_GROUP_OBJECT)) {// 88 操作组对象

//                    acrelReturnInstructions(ctx);

                    //截取人体光照度设备地址
                    String address = msgs[i].substring(2, 6);

                    //获取当前产品下所有的设备
                    Map<String, Equipment> equipmentLists = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

                    for (Equipment equipments : equipmentLists.values()) {
                        if (equipments.getpId() == null) {//网关设备或者直连设备
                            continue;
                        }
                        if (!equipments.getpId().equals(equipment.getId())) {//判断设备的父节点是否等于网关设备或者直连设备
                            continue;
                        }
                        if (equipments.getSonAddress().intValue() != Integer.valueOf(StringUtil.hexToDecimal(address))) {//子设备地址是否为推送的地址
                            continue;
                        }

                        //websocket推送设备在线离线状态
                        clientMsgReceive.deviceState(host, post, equipments.getId(), true);
                    }

                    Thread.sleep(1000);

                    //获取控制码
                    String controlCode = msgs[i].substring(0, 2);
                    if (controlCode.equalsIgnoreCase("40") ||
                            controlCode.equalsIgnoreCase("45") ||
                            controlCode.equalsIgnoreCase("46")) {
                        //如果传感器有指令动作,则下发获取实时值
                        sendSyncMsgHandler.sendMsg(null, host, post);
                    }

                }
                //获取光照度值
                if (deviceTypeCode.equalsIgnoreCase(AcrelDeviceTypeCode.SENSOR) &&  //30 传感器
                        CommandCode.equalsIgnoreCase(AcrelCommandCode.HEART_BEAT)) {// 06 心跳

                    //处理人体光照度实时值
                    headerRealTimeValueOfHumanBodyIllumination(host, post, msgs[i]);

                }
            }
            return;
        } else if ("55BB".equalsIgnoreCase(TransActionId)) {

            return;
        } else {

            return;
        }
    }

    //处理人体光照度实时值
    //ip, 端口,消息
    public void headerRealTimeValueOfHumanBodyIllumination(String host, int post, String msg) {

        //获取人体光照度地址
        String address = StringUtil.hexToDecimal(msg.substring(2, 6));
        //遍历配置产品,取出所有的网关设备(tcp)
        List<Equipment> equipmentList = refreshDevice();
        if (equipmentList == null || equipmentList.size() == 0) {
            return;
        }

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
            if (equipment.getSonAddress().intValue() != Integer.valueOf(address)) {//子设备地址是否为推送的地址
                continue;
            }

            //websocket推送设备在线离线状态
            clientMsgReceive.deviceState(host, post, equipment.getId(), true);
            sonEquipmentId = equipment.getId();
            productId = equipment.getProductId();

        }

        //获取设备所有的数据项
        Map<String, ProductItemData> productItemDataList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);

        List<ProductItemData> productItemDataLists = new ArrayList<>();

        /**使用stream流进行过滤排序*/
        Long finalProductId = productId;
        productItemDataLists = productItemDataList.values().stream()
                .filter(itemData -> itemData.getProductId() == finalProductId)
                .filter(itemData -> itemData.getItemDataId() == null)
                .sorted(Comparator.comparing(ProductItemData::getSortNum))
                .collect(Collectors.toList());


        for (ProductItemData productItemData : productItemDataLists) {
            Long sortNum = productItemData.getSortNum();//寄存器地址
            String dataType = productItemData.getDataType();//数据类型
            Integer registersNum = productItemData.getRegistersNum();//寄存器数量
            if (sortNum == 1) {//XX:Bit8:光感状态，Bit1：传感器实时状态 Bit0:传感器状态
                //判断当前数据项是否是结构体
                if (!dataType.equalsIgnoreCase("7")) {
                    continue;
                }
                List<ProductItemData> productItemDatas = productItemData.getStructDetail();//获取结构体集合

                /**过滤排序数据*/
                productItemDatas = productItemDatas.stream()
                        .filter(itemData -> itemData.getItemDataId() != null)
                        .sorted(Comparator.comparing(ProductItemData::getId))
                        .collect(Collectors.toList());

                String humanBodyMsg = msg.substring(16, 18);//XX:Bit8:光感状态，Bit1：传感器实时状态 Bit0:传感器状态
                //16进制转2进制
                String humanBody = StringUtil.hex2Binary(humanBodyMsg);
                System.out.println(humanBody);

                while (humanBody.length() < 8) {
                    humanBody = "0" + humanBody;
                }
                System.out.println(humanBody);
                //截取传感器实时状态值
//                55aa80012dffff300605030b086f02ef14
                String humanBodyRealTime = humanBody.substring(6, 7);
                //截取传感器状态
                String humanBodyState = humanBody.substring(7, 8);

                for (int i = 0; i < productItemDatas.size(); i++) {
                    if (i == 0) {
                        modbusServerHandlers.executeSaveItemDate(Double.valueOf(humanBodyRealTime), productItemDatas.get(i), sonEquipmentId);
                    } else if (i == 1) {
                        modbusServerHandlers.executeSaveItemDate(Double.valueOf(humanBodyState),  productItemDatas.get(i), sonEquipmentId);
                    }
                }
            } else if (sortNum == 2) {//YY YY：每秒采集的光照度值，低字节在前，高字节在后
                //截取光照度(分钟)的高位数据
                String high = msg.substring(20, 22);
                //截取光照度(分钟)的低位数据
                String low = msg.substring(18, 20);

                String illuminanceMinute = high + low;

                String illuminanceMinuteData = StringUtil.hexToDecimal(illuminanceMinute);
                modbusServerHandlers.executeSaveItemDate(Double.valueOf(illuminanceMinuteData), productItemData, sonEquipmentId);
            } else if (sortNum == 3) {//ZZ ZZ：光照度实时值，低字节在前，高字节在后
                //截取光照度(分钟)的高位数据
                String high = msg.substring(24, 26);
                //截取光照度(分钟)的低位数据
                String low = msg.substring(22, 24);

                String illuminanceSecond = high + low;

                String illuminanceSecondData = StringUtil.hexToDecimal(illuminanceSecond);
                modbusServerHandlers.executeSaveItemDate(Double.valueOf(illuminanceSecondData), productItemData, sonEquipmentId);
            }
        }
    }

    //安科瑞统一回复指令
    public void acrelReturnInstructions(ChannelHandlerContext ctx) {
        ByteBuf byteBufs = Unpooled.buffer();
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("55")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("bb")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("1")));
        byteBufs.writeShort(Integer.valueOf(StringUtil.hexToDecimal("0")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("20")));
        ctx.channel().writeAndFlush(byteBufs);
    }


    //获取人体光照度实时值
    public void getAcrelHumanBodyAndLightIntensityRealData(ChannelHandlerContext ctx, String address) {


        ByteBuf byteBufs = Unpooled.buffer();
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("55")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("aa")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("80")));
        byteBufs.writeShort(Integer.valueOf(StringUtil.hexToDecimal("ffff")));
        byteBufs.writeShort(Integer.valueOf(StringUtil.hexToDecimal(address)));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("0")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("6")));
        byteBufs.writeByte(Integer.valueOf(StringUtil.hexToDecimal("0")));

        System.out.println(CrcUtil.validateStringH("80ffff012d000600"));
        byteBufs.writeShort(Integer.valueOf(StringUtil.hexToDecimal(CrcUtil.validateStringH("80ffff012d000600"))));
//        byteBufs.writeShort(Integer.valueOf(StringUtil.hexToDecimal(String.valueOf(CrcUtil.getCRC(bytes1)))));
        ctx.channel().writeAndFlush(byteBufs);

    }


    //刷新设备
    public List<Equipment> refreshDevice() {

        List<Product> productLists = new ArrayList<>();

        List<Equipment> equipmentTCPList = new ArrayList<>();


        //遍历配置产品,取出所有的网关设备(tcp)
        Map<String, Product> productList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product);
        if (productList == null || productList.size() == 0) {
            return null;
        }

        productList.values().forEach(val -> {
            if (val.getIotType().equals("0") || val.getIotType().equals("1")) {//直连设备或者网关设备
                productLists.add(val);
            }
        });

        if (productLists.size() == 0) {
            return null;
        }

        Map<String, Equipment> controllerList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

        if (controllerList == null || controllerList.size() == 0) {
            return null;
        }

        controllerList.values().forEach(val -> {
            if (val.getNetworkType() == null) {
                return;
            }
            if (!val.getNetworkType().equals("1")) {//0-下位机客户端;1-下位机服务端
                return;
            }
            productLists.forEach(val1 -> {
                if (val1.getId() == val.getProductId()) {
                    equipmentTCPList.add(val);
                }
            });

        });
        return equipmentTCPList;
    }

    public AjaxResult getProductNumber(String host, int post) {

        List<Equipment> equipmentList = refreshDevice();
        if (equipmentList == null || equipmentList.size() == 0) {
            return AjaxResult.error("查询失败");
        }

        Long equipmentId = null;

        for (Equipment equipment : equipmentList) {
            String HOST = equipment.getIpAddress();
            Integer PORT = Integer.parseInt(equipment.getPortNum());
            if (!host.equals(HOST) || post != PORT) {
                continue;
            }
            equipmentId = equipment.getId();
        }

        if (equipmentId == null) {
            return AjaxResult.error("查询失败");
        }

        //获取当前设备的缓存信息
        Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipmentId);

        if (equipment == null) {
            return AjaxResult.error("查询失败");
        }
        //获取产品主键id
        Long productId = equipment.getProductId();

        if (productId == null) {
            return AjaxResult.error("查询失败");
        }
        //根据产品主键id获取产品信息
        Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, productId);

        if (product == null) {
            return AjaxResult.error("查询失败");
        }
        //获取产品的编号(根据编号判断自有协议或者标准modbus协议等等)
        String productCode = product.getCode();

        Map<String, Object> map = new HashMap<>();
        map.put("productCode", productCode);
        map.put("equipment", equipment);
        return AjaxResult.success("操作成功", map);
    }

    public static void main(String[] args) {
        String aa = "80012dffff300605817400280ffd29";
        System.out.println(aa.substring(23, 25));
    }
}
