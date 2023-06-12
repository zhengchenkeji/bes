package com.zc.connect.nettyClient;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.connect.nettyClient.ClientHandlers.SendSyncMsgHandler;
import com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler.ModbusSendSyncMsgHandler;
import com.zc.connect.util.StringUtil;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.baseData.Product;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wanghongjie
 * @Description:netty4的客户端
 * @Date: Created in 9:32 2023/2/23
 * @Modified By:
 */
@Component
public class NettyClient {

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    /**
     * 定义Map放click链接
     */
    public static Map<String, ChannelId> channels = new ConcurrentHashMap<>();

    /**
     * 用于记录和管理所有客户端的Channel
     */
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 服务类
     */
    private static final Bootstrap bootstrap = new Bootstrap();


    private static final NioEventLoopGroup group = new NioEventLoopGroup();

    private static List<Product> productLists = new ArrayList<>();

    private static List<Equipment> equipmentTCPList = new ArrayList<>();

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SendSyncMsgHandler sendSyncMsgHandler;

    @Autowired
    private ModbusSendSyncMsgHandler modbusSendSyncMsgHandler;


    //    static final int MAX_RETRY = 6;
    static final String HOST = "127.0.0.1";
    static final int PORT = 9002;
    static final int PORT1 = 9003;

    public static List<Equipment> getEquipmentTCPList() {
        return equipmentTCPList;
    }

    public static void setEquipmentTCPList(List<Equipment> equipmentTCPList) {
        NettyClient.equipmentTCPList = equipmentTCPList;
    }

    public static List<Product> getProductLists() {
        return productLists;
    }


    public static void setProductLists(List<Product> productLists) {
        NettyClient.productLists = productLists;
    }

    /**
     * 退出后释放连接（防止服务器重启造成的channel脏数据）
     */
    @PreDestroy
    public void setEmpty() {
        log.info("释放设备连接!");
        //清空连接缓存
        clients.clear();
        //释放线程资源
        group.shutdownGracefully();
        log.info("释放设备连接");
    }

    @PostConstruct
    private void init() {

        //设置线程池
        bootstrap.group(group);
        //设置socket工厂
        bootstrap.channel(NioSocketChannel.class);

        // 表示是否开启TCP底层心跳机制，true表示开启
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        // 是否开启Nagle算法，如果要求高实时性，有数据就马上发送，则为true
        // 如果需要减少发送次数，减少网络交互，就设置为false
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        //设置管道
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {

                // 解码器 StringDecoder，将字符串自动转成utf8编码
//                ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                // 编码器 StringEncoder，将字符串自动转成utf8编码
//                ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));

                // 解码器 LineBasedFrameDecoder，使用换行符\n或者\r\n作为依据，遇到\n或者\r\n都认为是一条完整的消息。
                ch.pipeline().addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));

                //检测连接有效性（心跳）,此处功能：5秒内read()未被调用则触发一次useEventTrigger()方法
                ch.pipeline().addLast(new IdleStateHandler(5, 5, 0, TimeUnit.SECONDS));
                // 业务处理器
                ch.pipeline().addLast(new ClientHandler());
            }
        });

        //准备进行连接
        readyConnect();


//        timer.schedule(timerTask, 20 * 1000, 10 * 1000);

//        String serverURL = "tcp://127.0.0.1:1883";
//        String mqttTopic = "MQTT";
//        String clientId = "client11";
//        //产品id
//        String productId = "";
//
//        String mqttUsername = "mqtt-test";
//        String mqttPassWord = "mqtt-test";
//        MqttClientThread client = new MqttClientThread(serverURL,mqttUsername,mqttPassWord,mqttTopic,clientId,productId);
//        client.run();
    }

    //准备进行连接
    public void readyConnect() {

        // 创建任务队列
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(10);


        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {

                List<Equipment> equipmentList = refreshDevice();
                if (equipmentList == null || equipmentList.size() == 0) {
                    return;
                }

                //遍历配置产品,取出所有的网关设备(tcp)
                equipmentList.forEach(val -> {
                    Set keySet = NettyClient.channels.keySet();
                    String HOST = val.getIpAddress();
                    Integer PORT = Integer.parseInt(val.getPortNum());

                    Boolean connent = true;
                    for (Object keyName : keySet) {
                        if ((HOST + ":" + PORT).equals(keyName)) {
                            connent = false;
                        }
                    }

                    if (connent) {
                        connect(bootstrap, HOST, PORT);
                    }


                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 5, 10, TimeUnit.SECONDS);


        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {


//                临时代码,定时发送客户端指令
                Long id = (long) 134;
//                Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,id);
                //ServerHandler_modbus.post
//                modbusSendSyncMsgHandler.sendModbusGatewaySyncMsg(equipment,equipment.getIpAddress(), Integer.valueOf(equipment.getPortNum()));

                //临时代码,获取光照度值


                //遍历配置产品,取出所有的网关设备(tcp),进行连接
                refreshDevice();

                if (equipmentTCPList.size() == 0) {
                    return;
                }

//                equipmentTCPList.forEach(val -> {
//
//                    String HOST = val.getIpAddress();
//                    Integer PORT = Integer.parseInt(val.getPortNum());
//
//                    if (!channels.containsKey(HOST + ":" + PORT)) {
//                        connect(bootstrap, HOST, PORT);
//                    } else {
//                        //线程池轮询
//                        ThreadPool.executor.execute(() -> {
//                            try {
//                                SyncFuture<String> syncFuture = new SyncFuture<String>();
//
//                                //判断是网关设备还是直连设备
//                                Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product,val.getProductId());
//                                if (product == null) {
//                                    return;
//                                }
//                                if (product.getIotType().equals("0")) {//直连设备
//                                    sendSyncMsgHandler.sendModbusDirectConnectionSyncMsg(val, HOST, PORT, syncFuture);
//
//                                } else if (product.getIotType().equals("1")) {//网关设备
//                                    sendSyncMsgHandler.sendModbusGatewaySyncMsg(val, HOST, PORT, syncFuture);
//                                }
//
//
//                                Thread.sleep(1000 * 1);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        });
//                    }
//
//                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }, 10, 10, TimeUnit.SECONDS);


    }


    public static void connect(final Bootstrap bootstrap, final String host, final int port) {

        try {
            bootstrap.remoteAddress(host, port);
            ChannelFuture channelFuture = bootstrap.connect();
            //使用最新的ChannelFuture -> 开启最新的监听器
            //使用future监听结果，执行异步操作结束后的回调.
            channelFuture.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(final ChannelFuture f) throws Exception {
                    boolean succeed = f.isSuccess();
                    if (!succeed) {
                        log.info(host + ":" + port + "客户端连接失败。。。");
                        f.channel().pipeline().fireChannelInactive();
                    } else {
                        log.info(host + ":" + port + "客户端连接成功。。。");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

//        channelFuture.addListener((ChannelFutureListener) future -> {
//            if (future.cause() != null) {
////                log.error(host + ":" + port + "连接失败。。。");
////                future.channel().eventLoop().schedule(() -> connect(bootstrap,host,port), 3, TimeUnit.SECONDS);
//            } else {
//
//
//                log.info(host + ":" + port + "客户端连接成功。。。");
//            }
//        });


//        bootstrap.connect(host, port).addListener(future -> {
//            if (future.isSuccess()) {
//                System.out.println("连接成功！");
//            } else {
//                System.out.println("连接失败！");
//            }
//            else if (retry == 0) {
//                System.err.println("重试次数已经使用完毕");
//
//
//
//            } else {
//                // 第几次重试
//                int order = (MAX_RETRY - retry) + 1;
//                // 本次的重试间隔
//                int delay = 1 << order;
//                System.out.println(new Date() + "： 连接失败，第" + order + "次重连...");
//                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
//            }
//        });
    }

    //重连
    public static void connect(final String host, final int port) {
        connect(bootstrap, host, port);
    }

    public class LineBasedFrameDecoder extends ByteToMessageDecoder {


        static final int PACKET_SIZE = Integer.MAX_VALUE;

        // 用来临时保留没有处理过的请求报文
        ByteBuf tempMsg = Unpooled.buffer();


        public LineBasedFrameDecoder(int maxValue) {
        }


        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            String host = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
            int post = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();

            System.out.println(Thread.currentThread() + "收到了一次数据包，长度是：" + in.readableBytes());

            AjaxResult ajaxResult = getProductNumber(host, post);

            if ((Integer) ajaxResult.get("code") != 200) {
                return;
            }
            //获取产品的编号(根据编号判断自有协议或者标准modbus协议等等)
            Map data = (Map) ajaxResult.get("data");

            String productCode = String.valueOf(data.get("productCode"));
            Equipment equipment = (Equipment) data.get("equipment");

            if (productCode.equalsIgnoreCase("acrel_Custom")) {//安科瑞自有协议

                int len = in.readableBytes();       //这里得到可读取的字节长度
                in.markReaderIndex();               //包头做标记位，后面可以重新回到数据包头开始读数据
                //有数据时开始读数据包
                if (len > 0) {
                    byte[] src = new byte[len];
                    in.readBytes(src);          //把数据读到字节数组中(读取完之后指针会到最后一个数据)
                    in.resetReaderIndex();      //重置当前指针到标记位(包头),用于重新读取接收的数据，直至接收完完整数据包


                    if (len > 1) {

                        String msg = "";
                        for (byte b : src) {
                            msg = msg + String.format("%02X", b);
                        }
                        //判断包头是否为约定的字节
                        if ((msg.substring(0,4).equalsIgnoreCase("55aa")) ||
                                (msg.substring(0,4).equalsIgnoreCase("55bb"))){



                        }else {
                            log.warn("无法识别的包头，转换为16进制的包头为：{}", msg);
                            // 包头不对，直接断开连接
                            ctx.close();
                        }

                        int beginIndex = 0;
                        if ((msg.substring(0,4).equalsIgnoreCase("55aa"))) {
                            beginIndex = msg.lastIndexOf("55AA");
                            //截取从beginIndex 到最后的字符串
                            String aa = msg.substring(beginIndex,msg.length());
                            //获取数据长度
                            if (aa.length() < 20) {//如果截取的标准报文小于20,则说明报文数据不全
                                return;
                            }

                            String dataLength = StringUtil.hexToDecimal(aa.substring(18,20));//截取数据长度字节
                            if (aa.length() < (20 + ((Integer.valueOf(dataLength) * 2) + 4))  ) {//如果数据长度小于正常报文长度,则说明报文数据不全
                                return;
                            }

                        } else if (msg.substring(0,4).equalsIgnoreCase("55bb")){
                            beginIndex = msg.lastIndexOf("55BB");
                            //截取从beginIndex 到最后的字符串
                            String bb = msg.substring(beginIndex,msg.length());
                            //获取数据长度
                            if (bb.length() < 12) {//如果截取的标准报文小于20,则说明报文数据不全
                                return;
                            }
                        }
                        in.skipBytes(in.readableBytes());//标记已读取完毕
                        //收到了尾部字节，完成接收
                        ByteBuf buf = Unpooled.wrappedBuffer(src);
                        out.add(buf);
                    }
                    //非结尾字节，继续接收,当数据包的长度不够时直接return，netty在缓冲区有数据时会一直调用decode方法，所以我们只需要等待下一个数据包传输过来一起解析(解决半包问题)
                    return;
                } else {
                    // 发空数据包过来的家伙直接断开连接
                    ctx.close();
                }
            } else {
                // 合并报文
                ByteBuf message = null;
                int tmpMsgSize = tempMsg.readableBytes();
                // 如果暂存有上一次余下的请求报文，则合并
                if (tmpMsgSize > 0) {
                    message = Unpooled.buffer();
                    message.writeBytes(tempMsg);
                    message.writeBytes(in);
//                System.out.println("合并：上一数据包余下的长度为：" + tmpMsgSize + ",合并后长度为:" + message.readableBytes());
                } else {
                    message = in;
                }

                int size = message.readableBytes();
                int counter = size / PACKET_SIZE;
//            for (int i = 0; i < counter; i++) {
//                byte[] request = new byte[PACKET_SIZE];
//                // 每次从总的消息中读取220个字节的数据
//                message.readBytes(request);
//
//                // 将拆分后的结果放入out列表中，交由后面的业务逻辑去处理
//                out.add(Unpooled.copiedBuffer(request));
//            }

                byte[] request = new byte[size];
                message.readBytes(request);
                out.add(Unpooled.copiedBuffer(request));

                // 多余的报文存起来
                // 第一个报文： i+  暂存
                // 第二个报文： 1 与第一次
//            size = message.readableBytes();
//            if (size != 0) {
//                System.out.println("多余的数据长度：" + size);
//                // 剩下来的数据放到tempMsg暂存
//                tempMsg.clear();
//                tempMsg.writeBytes(message.readBytes(size));
//            }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            System.err.println("--------数据读异常----------: ");
            cause.printStackTrace();
            ctx.close();
        }
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
}
