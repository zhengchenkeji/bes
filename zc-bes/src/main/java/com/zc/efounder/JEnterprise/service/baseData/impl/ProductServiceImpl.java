package com.zc.efounder.JEnterprise.service.baseData.impl;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.constant.ModbusFunctions;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.EquipmentConstant;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.common.core.model.DataReception;
import com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler.ModbusSendSyncMsgHandler;
import com.zc.connect.util.StringUtil;
import com.zc.efounder.JEnterprise.commhandler.MqttHandler;
import com.zc.efounder.JEnterprise.domain.baseData.*;
import com.zc.efounder.JEnterprise.domain.deviceTree.PointControlCommand;
import com.zc.efounder.JEnterprise.domain.energyInfo.EnergyType;
import com.zc.efounder.JEnterprise.mapper.baseData.AgreementMapper;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.mapper.baseData.EquipmentMapper;
import com.zc.efounder.JEnterprise.mapper.baseData.ProductMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.EnergyTypeMapper;
import com.zc.efounder.JEnterprise.service.baseData.ProductService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 产品定义Service业务层处理
 *
 * @author gaojikun
 * @date 2023-03-07
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductMapper productMapper;

    @Resource
    private EquipmentMapper athenaBesEquipmentMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private AgreementMapper agreementMapper;

    @Autowired
    private ModbusSendSyncMsgHandler modbusSendSyncMsgHandler;

    @Autowired
    private EnergyTypeMapper energyTypeMapper;

    @PostConstruct
    public void init() {

        /**清空实时数据缓存*/
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_Product_ItemData_RealTime);
        //添加数据到 redis 缓存
        addProductCache();

        addProductCacheItemDataParams();
        addProductCacheItemData();

        addProductCacheFunctionParams();
        addProductCacheFunction();
        addAgreementCache();
    }


    /**
     * 添加数据到 redis 缓存
     */
    public void addAgreementCache() {
        /**清除缓存*/
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_Agreement);
        /**存储缓存*/
        List<Agreement> agreements = agreementMapper.selectAgreementList(null);
        if (agreements == null || agreements.isEmpty()) {
            return;
        }
        agreements.forEach(item -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Agreement, item.getId(), item);
        });
    }


    /**
     * 添加数据到 redis 缓存
     */
    public void addProductCache() {
        // 获取全部产品定义列表数据
        List<Product> products = productMapper.selectAthenaBesProductList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_Product);

        if (products == null || products.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        products.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product, val.getId(), val);
        });
    }

    public void addProductCacheItemData() {
        // 获取全部产品数据项列表数据
        List<ProductItemData> productItemDatas = productMapper.selectAthenaBesProductItemDataList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_Product_ItemData);

        if (productItemDatas == null || productItemDatas.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        productItemDatas.forEach(val -> {

            List<ProductItemData> hDetils = new ArrayList<>();
            List<ProductItemData> lDetils = new ArrayList<>();
            List<ProductItemData> sDetils = new ArrayList<>();
            List<ParamsItemData> eDetils = new ArrayList<>();

            Collection<Object> paramCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params).values();
            for (Object el : paramCache) {
                ParamsItemData paramsItemData = (ParamsItemData) el;
                if (paramsItemData.getItemDataId().equals(val.getId()) && "3".equals(paramsItemData.getType())) {
                    eDetils.add(paramsItemData);
                }
            }

            // 获取全部产品数据项列表数据
            List<ProductItemData> productItemDataParams = productMapper.selectAthenaBesProductItemDataOtherParamsList(null);
            for (ProductItemData pro : productItemDataParams) {
                if (pro.getItemDataId().equals(val.getId()) && "1".equals(pro.getParamsType())) {
                    List<ParamsItemData> ehDetils = new ArrayList<>();
                    Collection<Object> paramCacheHigh = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params).values();
                    for (Object el : paramCacheHigh) {
                        ParamsItemData paramsItemData = (ParamsItemData) el;
                        if (paramsItemData.getItemDataId().equals(pro.getId()) && "3".equals(paramsItemData.getType())) {
                            ehDetils.add(paramsItemData);
                        }
                    }
                    pro.setEnumDetail(ehDetils);
                    hDetils.add(pro);
                }
                if (pro.getItemDataId().equals(val.getId()) && "2".equals(pro.getParamsType())) {
                    List<ParamsItemData> elDetils = new ArrayList<>();
                    Collection<Object> paramCacheHigh = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params).values();
                    for (Object el : paramCacheHigh) {
                        ParamsItemData paramsItemData = (ParamsItemData) el;
                        if (paramsItemData.getItemDataId().equals(pro.getId()) && "3".equals(paramsItemData.getType())) {
                            elDetils.add(paramsItemData);
                        }
                    }
                    pro.setEnumDetail(elDetils);
                    lDetils.add(pro);
                }
                if (pro.getItemDataId().equals(val.getId()) && "4".equals(pro.getParamsType())) {
                    List<ParamsItemData> esDetils = new ArrayList<>();
                    Collection<Object> paramCacheHigh = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params).values();
                    for (Object el : paramCacheHigh) {
                        ParamsItemData paramsItemData = (ParamsItemData) el;
                        if (paramsItemData.getItemDataId().equals(pro.getId()) && "3".equals(paramsItemData.getType())) {
                            esDetils.add(paramsItemData);
                        }
                    }
                    pro.setEnumDetail(esDetils);
                    sDetils.add(pro);
                }
            }


            val.setHighDetail(hDetils);
            val.setLowDetail(lDetils);
            val.setEnumDetail(eDetils);
            val.setStructDetail(sDetils);

            //能源名称
            if(val.getEnergyCode() != null){
                val.setEnergyName(setEnergyName(val));
            }

            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, val.getId(), val);
        });
    }

    //设置数据项能源名称
    private String setEnergyName(ProductItemData val){
        EnergyType energy = new EnergyType();
        energy.setCode(val.getEnergyCode());
        List<EnergyType> energyTypes = energyTypeMapper.selectEnergyTypeListByCode(energy);
        return energyTypes.get(0).getName();
    }

    public void addProductCacheItemDataParams() {
        // 获取全部产品数据项参数列表数据
        List<ParamsItemData> paramsItemDatas = productMapper.selectAthenaBesProductItemDataParamsList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_Product_ItemData_Params);

        if (paramsItemDatas == null || paramsItemDatas.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        paramsItemDatas.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params, val.getId(), val);
        });
    }

    public void addProductCacheFunctionParams() {

        List<FunctionItemData> functionItemDatas = productMapper.selectProductFunctionParamsList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_Product_Function_ItemData);

        if (functionItemDatas == null || functionItemDatas.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        functionItemDatas.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData, val.getId(), val);
        });
    }

    public void addProductCacheFunction() {
        // 获取全部产品功能列表数据
        List<ProductFunction> productFunctions = productMapper.selectAthenaBesProductFunctionList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_Product_Function);

        if (productFunctions == null || productFunctions.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        productFunctions.forEach(val -> {

            List<FunctionItemData> paramsList = new ArrayList<>();

            Collection<Object> paramCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData).values();
            for (Object el : paramCache) {
                FunctionItemData functionItemData = (FunctionItemData) el;
                if (functionItemData.getFunctionId().equals(val.getId())) {
                    paramsList.add(functionItemData);
                }
            }
            val.setParamsList(paramsList);

            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, val.getId(), val);
        });
    }

    /**
     * 查询产品定义
     *
     * @param id 产品定义主键
     * @return AjaxResult
     */
    @Override
    public AjaxResult selectAthenaBesProductById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误");
        }
        Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, id);
        return AjaxResult.success(product);
    }

    /**
     * 查询产品数据项
     *
     * @param id 产品数据项主键
     * @return AjaxResult
     */
    @Override
    public AjaxResult selectAthenaBesProductItemDataById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误");
        }
        ProductItemData productItemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, id);

        return AjaxResult.success(productItemData);
    }

    /**
     * 查询产品配置-功能定义
     *
     * @param id 产品配置-功能定义主键
     * @return AjaxResult
     */
    @Override
    public AjaxResult selectAthenaBesProductFunctionById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误");
        }
        ProductFunction productFunction = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, id);
        return AjaxResult.success(productFunction);
    }

    /**
     * 查询产品定义列表
     *
     * @param product 产品定义
     * @return List<Product>
     */
    @Override
    public List<Product> selectAthenaBesProductList(Product product) {
        List<Product> list = new ArrayList<>();
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product).values();
        for (Object j : values) {
            Product p = (Product) j;
            list.add(p);
        }
        //查询条件
        if (list.size() > 0 && product != null) {
            Iterator<Product> it = list.iterator();
            while (it.hasNext()) {
                Product p = it.next();
                //产品名称
                if (!StringUtils.isEmpty(product.getName())) {
                    if (!p.getName().contains(product.getName()) && !p.getName().equals(product.getName())) {
                        it.remove();
                        continue;
                    }
                }
                //产品编号
                if (!StringUtils.isEmpty(product.getCode())) {
                    if (!p.getCode().contains(product.getCode()) && !p.getCode().equals(product.getCode())) {
                        it.remove();
                        continue;
                    }
                }
                //品类
                if (product.getCategoryId() != null) {
                    if (!p.getCategoryId().equals(product.getCategoryId())) {
                        it.remove();
                        continue;
                    }
                }
                //启用状态
                if (!StringUtils.isEmpty(product.getState())) {
                    if (!p.getState().equals(product.getState())) {
                        it.remove();
                        continue;
                    }
                }
                //通讯协议
                if (!StringUtils.isEmpty(product.getCommunicationProtocol())) {
                    if (StringUtils.isEmpty(p.getCommunicationProtocol())) {
                        it.remove();
                        continue;
                    }
                    if (!p.getCommunicationProtocol().equals(product.getCommunicationProtocol())) {
                        it.remove();
                    }
                }
            }
        }

        // 排序.reversed()
        list = list.stream().sorted(Comparator.comparing(Product::getId))
                .collect(Collectors.toList());
        return list;
    }

    /**
     * @Author:gaojikun
     * @Date:2023-03-08 16:03
     * @Description:查询所有产品
     * @Return:AjaxResult
     */
    @Override
    public AjaxResult selectAllProductList(Product product) {
        List<Product> list = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product).values();
        if (product == null || product.getIotType() == null) {
            //查询非子设备产品
            String ipValue = DictUtils.getDictValue(EquipmentConstant.Equipment_Type, EquipmentConstant.Equipment_Ip_Label);
            String linkValue = DictUtils.getDictValue(EquipmentConstant.Equipment_Type, EquipmentConstant.Equipment_Link_Label);
            ids.add(ipValue);
            ids.add(linkValue);
            for (Object j : values) {
                Product p = (Product) j;
                if (ids.contains(p.getIotType())) {
                    list.add(p);
                }
            }
        } else {
            //查询子设备产品
            String sonValue = DictUtils.getDictValue(EquipmentConstant.Equipment_Type, EquipmentConstant.Equipment_Son_Label);
            ids.add(sonValue);
            for (Object j : values) {
                Product p = (Product) j;
                if (ids.contains(p.getIotType())) {
                    list.add(p);
                }
            }
        }
        // 排序.reversed()
        list = list.stream().sorted(Comparator.comparing(Product::getId).reversed())
                .collect(Collectors.toList());
        return AjaxResult.success(list);
    }

    /**
     * 查询产品数据项列表
     *
     * @param productItemData 产品数据项
     * @return 产品数据项集合
     */
    @Override
    public List<ProductItemData> selectAthenaBesProductItemDataList(ProductItemData productItemData) {
        List<ProductItemData> list = new ArrayList<>();

        List<EquipmentItemData> equipmentItemDataList = new ArrayList<>();

        //获取设备id
        Long equipmentId = productItemData.getEquipmentId();

        if (equipmentId != null) {
            //获取数据库中设备对应的数据项别名
            equipmentItemDataList = athenaBesEquipmentMapper.selectEquipmentItemDataList(equipmentId);
        }
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
        for (Object j : values) {
            ProductItemData p = (ProductItemData) j;
            if (p.getProductId().equals(productItemData.getProductId()) && p.getItemDataId() == null) {

                Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, p.getProductId());
                p.setProductCode(product.getCode());

                for (EquipmentItemData equipmentItemData : equipmentItemDataList) {
                    if (p.getId().equals(equipmentItemData.getItemDataId())) {
                        p.setItemDataCustomName(equipmentItemData.getItemDataCustomName());
                    }
                }
                list.add(p);
            }
        }
        // 排序.reversed()
        list = list.stream().sorted(Comparator.comparing(ProductItemData::getCreateTime))
                .collect(Collectors.toList());
        return list;
    }

    /**
     * @description:根据设备获取数据项
     * @author: sunshangeng
     * @date: 2023/5/25 16:50
     * @param: [ModId]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.baseData.ProductItemData>
     **/
    @Override
    public List<ProductItemData> getProductItemDataListByEqId(Long ModId) {
        Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, ModId);
        Long productId = equipment.getProductId();
        Map<String, ProductItemData> itemDataMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);
        List<ProductItemData> list = itemDataMap.values().stream().filter(itemData -> itemData.getProductId().equals(productId))
                .sorted(Comparator.comparing(ProductItemData::getSortNum))
                .collect(Collectors.toList());

        list.forEach(itemData -> {
//            System.out.println(itemData.getId()+"" + ModId);
            ProductItemRealTimeData data = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_RealTime, itemData.getId() +""+ ModId);
            if(data!=null){
                itemData.setDataValue( new BigDecimal(data.getDataValue()));

            }
        });
        return list;
    }

    /**
     * @description:根据数据项 下发值
     * @author: sunshangeng
     * @date: 2023/5/25 17:33
     * @param: [ModId, Value]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.baseData.ProductItemData>
     **/
    @Override
    public AjaxResult debugpoint(PointControlCommand  pointControlCommand) {
        ProductItemData itemData=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData,pointControlCommand.getId()+"");
        if (itemData==null) {
            return  AjaxResult.error("传入的数据项有误！");
        }
        Map<String, ProductFunction> functionMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function);
        List<ProductFunction> functionList = functionMap.values().stream()
                .filter(fun -> fun.getProductId().equals(itemData.getProductId()))
                .filter(fun-> fun.getIssuedType().equals("1"))//只保留数据项下发的功能
                .filter(fun -> itemData.getId().equals(fun.getDataItem())).collect(Collectors.toList());
        if(functionList==null||functionList.size()==0){
            return  AjaxResult.error("当前数据项未绑定功能");
        }
        /**默认获取第一个*/
        ProductFunction function=functionList.get(0);

        /*获取产品获取设备类型*/
        Product product=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product,itemData.getProductId());
        if ("2".equals(product.getIotType())) {

            Equipment equipment=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,pointControlCommand.getEquipmentId().longValue());
            if (equipment==null) {
                return  AjaxResult.error("传入的设备有误！");
            }
            Equipment pequipment=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,equipment.getpId());
            List<PointControlCommand> valueList=new ArrayList<>();
            valueList.add(pointControlCommand);
            try {
                modbusSendSyncMsgHandler.issued1(pequipment.getIpAddress(), Integer.parseInt(pequipment.getPortNum()), equipment,null, (long) function.getId(), valueList);
            } catch (Exception e) {
                e.printStackTrace();
                return  AjaxResult.error("下发失败："+e.getMessage());
            }
        }



        return AjaxResult.success();
    }

    /**
     * @description:根据设备下发
     * @author: sunshangeng
     * @date: 2023/5/25 18:04
     * @param: 设备ID，功能ID
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult debugDevice(Long deviceId, String funCode) {
        Equipment equipment=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,deviceId);
        if (equipment==null) {
            return  AjaxResult.error("传入的设备有误！");
        }
        Map<String, ProductFunction> functionMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function);
//        ProductFunction function= cacheMapValue;

        List<ProductFunction> functionList = functionMap.values().stream()
                .filter(item -> item.getProductId().equals(equipment.getProductId()))
                .filter(item -> item.getFunctionNum().equals(funCode)).collect(Collectors.toList());

        if(functionList==null||functionList.size()==0){
            return  AjaxResult.error("传入的设备功能有误 ！");

        }
        Equipment pequipment=redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment,equipment.getpId());
        try {
            modbusSendSyncMsgHandler.issued1(pequipment.getIpAddress(), Integer.parseInt(pequipment.getPortNum()), equipment,null, (long) functionList.get(0).getId(), null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return  AjaxResult.error("下发失败："+e.getMessage());
        }
        return AjaxResult.success();
    }

    /**
     * 查询产品配置-功能定义列表
     *
     * @param productFunction 产品配置-功能定义
     * @return List<ProductFunction>
     */
    @Override
    public List<ProductFunction> selectAthenaBesProductFunctionList(ProductFunction productFunction) {
        List<ProductFunction> list = new ArrayList<>();
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function).values();
        for (Object j : values) {
            ProductFunction p = (ProductFunction) j;
            if (p.getProductId().equals(productFunction.getProductId())) {
                Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, p.getProductId());
                p.setProductCode(product.getCode());
                list.add(p);
            }
        }
        // 排序.reversed()
        list = list.stream().sorted(Comparator.comparing(ProductFunction::getCreateTime))
                .collect(Collectors.toList());
        return list;
    }

    /**
     * 新增产品定义
     *
     * @param product 产品定义
     * @return AjaxResult
     */
    @Override
    public AjaxResult insertAthenaBesProduct(Product product) {
        if (product.getCategoryId() == null ||
                product.getCode() == null ||
                StringUtils.isEmpty(product.getName()) ||
                StringUtils.isEmpty(product.getState()) ||
                product.getIotType() == null) {
            return AjaxResult.error("参数错误");
        }
        if (EquipmentConstant.HTTP_VALUE.equals(product.getCommunicationProtocol()) || EquipmentConstant.TCP_VALUE.equals(product.getCommunicationProtocol())) {
            //数据接入
            if (StringUtils.isEmpty(product.getDataAccess())) {
                return AjaxResult.error("参数错误");
            }
        }

        //code查重
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product).values();
        for (Object j : values) {
            Product e = (Product) j;
            if (e.getCode().equals(product.getCode())) {
                return AjaxResult.error("编号重复");
            }
            if (e.getName().equals(product.getName())) {
                return AjaxResult.error("名称重复");
            }
        }

        product.setCreateName(SecurityUtils.getUsername());
        product.setCreateTime(DateUtils.getNowDate());
        boolean isAdd = productMapper.insertAthenaBesProduct(product);

        if (isAdd) {
            //查询所有产品信息
            List<Product> productsRedis = productMapper.selectAthenaBesProductList(product);
            product = productsRedis.get(0);
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product, product.getId(), product);
            return AjaxResult.success("添加成功");
        } else {
            return AjaxResult.error("添加失败");
        }
    }

    /**
     * 新增产品配置-功能定义
     *
     * @param productFunction 产品配置-功能定义
     * @return AjaxResult
     */
    @Override
    public AjaxResult insertAthenaBesProductFunction(ProductFunction productFunction) {
        if (StringUtils.isEmpty(productFunction.getType()) ||
                productFunction.getFunctionNum() == null ||
                StringUtils.isEmpty(productFunction.getName())
        ) {
            return AjaxResult.error("参数错误");
        }

        //如果是指令下发
        if ("0".equals(productFunction.getIssuedType()) && "1".equals(productFunction.getProofreadingState())) {
            //*10000c0001020000
            //*(固定开头)10(功能码)000c(寄存器起始地址)0001(寄存器数量)02(数据字节数)0000(数据)
            //功能码验证 截取功能码
            String functionCode = productFunction.getInstruct().substring(1, 3);
            //1.16进制转成10进制查看功能吗是不是存在ModbusFunctions类中
            String tenFunctionCode = StringUtil.hexToDecimal(functionCode);
            //根据反射判断是否属于这个类
            boolean functionBoolean = isExistField(Integer.parseInt(tenFunctionCode));
            if (!functionBoolean) {
                //不存在直接返回
                return AjaxResult.error("功能码错误");
            }
            //2.如果是写的话
            if (isWrite(tenFunctionCode)) {
                //寄存器数量 十进制
                String numberStr = StringUtil.hexToDecimal(productFunction.getInstruct().substring(7, 11));
                //数据字节数 十进制
                String byteStr = StringUtil.hexToDecimal(productFunction.getInstruct().substring(11, 13));
                //数据 十进制
                String dataStr = productFunction.getInstruct().substring(13);

                //1.验证寄存器地址数量
                if (4 * Integer.parseInt(numberStr) != dataStr.length()) {
                    return AjaxResult.error("寄存器数量/数据错误");
                }

                //2.验证寄存器字节数
                if (2 * Integer.parseInt(byteStr) != dataStr.length()) {
                    //字节数/数据错误
                    return AjaxResult.error("数据字节数/数据错误");
                }
            }
        }
//        else {
//            if (productFunction.getDataItem() == null) {
//                return AjaxResult.error("参数错误");
//            }
//        }

        //code查重
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function).values();
        for (Object j : values) {
            ProductFunction e = (ProductFunction) j;
            if (e.getFunctionNum().equals(productFunction.getFunctionNum()) && e.getProductId().equals(productFunction.getProductId())) {
                return AjaxResult.error("编号重复");
            }
            if (e.getName().equals(productFunction.getName()) && e.getProductId().equals(productFunction.getProductId())) {
                return AjaxResult.error("名称重复");
            }
        }

        productFunction.setCreateName(SecurityUtils.getUsername());
        productFunction.setCreateTime(DateUtils.getNowDate());
        boolean isAdd = productMapper.insertAthenaBesProductFunction(productFunction);
        if (isAdd) {

            //添加功能定义参数
            List<FunctionItemData> paramsList = productFunction.getParamsList();
            for (FunctionItemData functionItemData : paramsList) {
                functionItemData.setFunctionId(productFunction.getId());
                isAdd = productMapper.insertAthenaBesProductFunctionParams(functionItemData);
                //添加缓存
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData, functionItemData.getId(), functionItemData);
            }

            //查询所有产品信息
            List<ProductFunction> productFunctionsRedis = productMapper.selectAthenaBesProductFunctionList(productFunction);
            productFunction = productFunctionsRedis.get(0);

            productFunction.setParamsList(paramsList);
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, productFunction.getId(), productFunction);

            return AjaxResult.success("添加成功");
        } else {
            return AjaxResult.error("添加失败");
        }
    }

    /**
     * 新增产品数据项
     *
     * @param productItemData 产品数据项
     * @return AjaxResult
     */
    @Override
    public AjaxResult insertAthenaBesProductItemData(ProductItemData productItemData) {
        if (productItemData.getProductId() == null ||
                productItemData.getDataItemNum() == null ||
                productItemData.getDataItemMark() == null ||
                productItemData.getSortNum() == null ||
                StringUtils.isEmpty(productItemData.getName()) ||
                productItemData.getDataType() == null||
                productItemData.getRegistersNum()==null
        ) {
            return AjaxResult.error("参数错误");
        }
        if ("1".equals(productItemData.getPreserveType())) {
            if (StringUtils.isEmpty(productItemData.getEnergyCode())) {
                return AjaxResult.error("参数错误");
            }
        }

        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
        for (Object j : values) {
            ProductItemData e = (ProductItemData) j;
            if (e.getSortNum() != null && e.getSortNum().equals(productItemData.getSortNum()) && e.getProductId().equals(productItemData.getProductId())
                    && !StringUtils.isEmpty(e.getFunctionCode()) && !StringUtils.isEmpty(productItemData.getFunctionCode())
                    && e.getFunctionCode().equals(productItemData.getFunctionCode())
            ) {
                return AjaxResult.error("寄存器地址重复");
            }

            if (e.getSortNum() != null && e.getSortNum().equals(productItemData.getSortNum()) && e.getProductId().equals(productItemData.getProductId())
                    && !StringUtils.isEmpty(e.getWriteFunctionCode()) && !StringUtils.isEmpty(productItemData.getWriteFunctionCode())
                    && e.getWriteFunctionCode().equals(productItemData.getWriteFunctionCode())
            ) {
                return AjaxResult.error("寄存器地址重复");
            }

            if (e.getDataItemNum().equals(productItemData.getDataItemNum()) && e.getProductId().equals(productItemData.getProductId())) {
                return AjaxResult.error("编号重复");
            }
            if (e.getDataItemMark().equals(productItemData.getDataItemMark()) && e.getProductId().equals(productItemData.getProductId())) {
                return AjaxResult.error("标识重复");
            }
            if (e.getSortNum() != null && e.getName().equals(productItemData.getName()) && e.getProductId().equals(productItemData.getProductId())) {
                return AjaxResult.error("名称重复");
            }
            if (productItemData.getHighDetail().size() > 0) {
                List<ProductItemData> highDetail = productItemData.getHighDetail();
                for (ProductItemData p : highDetail) {
                    if (e.getDataItemNum().equals(p.getDataItemNum()) && e.getProductId().equals(p.getProductId())) {
                        return AjaxResult.error("高位参数编号重复");
                    }
                    if (e.getDataItemMark().equals(p.getDataItemMark()) && e.getProductId().equals(p.getProductId())) {
                        return AjaxResult.error("高位参数标识重复");
                    }
                    if (e.getItemDataId() != null && e.getName().equals(p.getName()) && e.getItemDataId().equals(p.getItemDataId())) {
                        return AjaxResult.error("高位参数名称重复");
                    }
                }
            }
            if (productItemData.getLowDetail().size() > 0) {
                List<ProductItemData> lowDetail = productItemData.getLowDetail();
                for (ProductItemData p : lowDetail) {
                    if (e.getDataItemNum().equals(p.getDataItemNum()) && e.getProductId().equals(p.getProductId())) {
                        return AjaxResult.error("低位参数编号重复");
                    }
                    if (e.getDataItemMark().equals(p.getDataItemMark()) && e.getProductId().equals(p.getProductId())) {
                        return AjaxResult.error("低位参数标识重复");
                    }
                    if (e.getItemDataId() != null && e.getName().equals(p.getName()) && e.getItemDataId().equals(p.getItemDataId())) {
                        return AjaxResult.error("低位参数名称重复");
                    }
                }
            }
            if (productItemData.getStructDetail().size() > 0) {
                List<ProductItemData> structDetail = productItemData.getStructDetail();
                for (ProductItemData p : structDetail) {
                    if (e.getDataItemNum().equals(p.getDataItemNum()) && e.getProductId().equals(p.getProductId())) {
                        return AjaxResult.error("结构体参数编号重复");
                    }
                    if (e.getDataItemMark().equals(p.getDataItemMark()) && e.getProductId().equals(p.getProductId())) {
                        return AjaxResult.error("结构体参数标识重复");
                    }
                    if (e.getItemDataId() != null && e.getName().equals(p.getName()) && e.getItemDataId().equals(p.getItemDataId())) {
                        return AjaxResult.error("结构体参数名称重复");
                    }
                }
            }
        }

        //与能耗参数编号查重
        Collection<Object> valuesParam = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams).values();
        for (Object el : valuesParam) {
            ElectricParams electricParams = (ElectricParams) el;
            if (electricParams.getCode().equals(productItemData.getDataItemNum())) {
                return AjaxResult.error("编号与能耗参数编号重复");
            }
        }

        productItemData.setCreateName(SecurityUtils.getUsername());
        productItemData.setCreateTime(DateUtils.getNowDate());
        boolean isAdd = productMapper.insertAthenaBesProductItemData(productItemData);
        if (isAdd) {
            //查询所有产品信息
            List<ProductItemData> productItemDatasRedis = productMapper.selectAthenaBesProductItemDataList(productItemData);
            ProductItemData productItemDataAdd = productItemDatasRedis.get(0);

            //获取当前数据项缓存-添加无缓存
            ProductItemData productItemDataRedis = new ProductItemData();

            //修改数据项枚举参数
            //1.获取到现在的枚举参数
            List<ParamsItemData> enumDetailParams = productItemData.getEnumDetail();
            //2.获取到缓存的枚举参数
            List<ParamsItemData> enumDetailRedis = productItemDataRedis.getEnumDetail();
            //3.修改枚举参数数据库及缓存
            changeEnumDetails(enumDetailParams, enumDetailRedis, productItemData);
            //4.设置当前枚举参数
            productItemDataAdd.setEnumDetail(enumDetailParams);

            //如果高位参数不为空
            if (productItemData.getHighDetail().size() > 0) {
                //1.获取到现在的高位参数
                List<ProductItemData> highDetailParams = productItemData.getHighDetail();
                //2.获取到缓存的高位参数
                List<ProductItemData> highDetailRedis = productItemDataRedis.getHighDetail();
                //3.修改高位参数数据库及缓存
                changeHighDetails(highDetailParams, highDetailRedis, productItemData);

                productItemDataAdd.setHighDetail(highDetailParams);
            }

            //如果低位参数不为空
            if (productItemData.getLowDetail().size() > 0) {
                //1.获取到现在的低位参数
                List<ProductItemData> lowDetailParams = productItemData.getLowDetail();
                //2.获取到缓存的低位参数
                List<ProductItemData> lowDetailRedis = productItemDataRedis.getLowDetail();
                //3.修改低位参数数据库及缓存
                changeHighDetails(lowDetailParams, lowDetailRedis, productItemData);

                productItemDataAdd.setLowDetail(lowDetailParams);
            }

            //如果结构体参数不为空
            if (productItemData.getStructDetail().size() > 0) {
                //1.获取到现在的结构体参数
                List<ProductItemData> structDetailParams = productItemData.getStructDetail();
                //2.获取到缓存的结构体参数
                List<ProductItemData> structDetailRedis = productItemDataRedis.getStructDetail();
                //3.修改结构体参数数据库及缓存
                changeHighDetails(structDetailParams, structDetailRedis, productItemData);

                productItemDataAdd.setStructDetail(structDetailParams);
            }
            /**判断是否需要订阅mqtt主题*/
            if (StringUtils.isNotBlank(productItemDataAdd.getSubscribeAddress())) {
                /**说明需要订阅mqtt主题*/

                try {
                    MqttHandler.ChangeSub(productItemDataAdd.getId(), productItemDataAdd.getSubscribeAddress());
                } catch (Exception e) {
                    log.error(productItemDataAdd.getName() + "数据项订阅mqtt时出错", e);
                }

            }

            //能源名称
            if(productItemData.getEnergyCode() != null){
                productItemData.setEnergyName(setEnergyName(productItemData));
            }

            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productItemData.getId(), productItemDataAdd);
            return AjaxResult.success("添加成功");
        } else {
            return AjaxResult.error("添加失败");
        }
    }

    /**
     * 修改产品定义
     *
     * @param product 产品定义
     * @return AjaxResult
     */
    @Override
    public AjaxResult updateAthenaBesProduct(Product product) {
        if (product.getId() == null ||
                product.getCategoryId() == null ||
                product.getCode() == null ||
                StringUtils.isEmpty(product.getName()) ||
                StringUtils.isEmpty(product.getState()) ||
                product.getIotType() == null) {
            return AjaxResult.error("参数错误");
        }
        if (EquipmentConstant.HTTP_VALUE.equals(product.getCommunicationProtocol()) || EquipmentConstant.TCP_VALUE.equals(product.getCommunicationProtocol())) {
            //数据接入
            if (StringUtils.isEmpty(product.getDataAccess())) {
                return AjaxResult.error("参数错误");
            }
        }

        //code查重
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product).values();
        for (Object j : values) {
            Product e = (Product) j;
            if (!e.getId().equals(product.getId())) {
                if (e.getCode().equals(product.getCode())) {
                    return AjaxResult.error("编号重复");
                }
                if (e.getName().equals(product.getName())) {
                    return AjaxResult.error("名称重复");
                }
            }
        }

        product.setUpdateName(SecurityUtils.getUsername());
        product.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdate = productMapper.updateAthenaBesProduct(product);
        if (isUpdate) {
            //查询所有产品信息
            List<Product> productsRedis = productMapper.selectAthenaBesProductList(product);
            product = productsRedis.get(0);
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product, product.getId(), product);

            //修改关联产品的设备
            Collection<Object> eValues = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
            for (Object j : eValues) {
                Equipment equ = (Equipment) j;
                if (equ.getProductId().equals(product.getId())) {
                    //更新设备缓存
                    List<Equipment> equipmentsRedis = athenaBesEquipmentMapper.selectAthenaBesEquipmentList(equ);
                    equ = equipmentsRedis.get(0);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equ.getId(), equ);
                }
            }

            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.error("修改失败");
        }
    }

    /**
     * 修改产品配置-功能定义
     *
     * @param productFunction 产品配置-功能定义
     * @return AjaxResult
     */
    @Override
    public AjaxResult updateAthenaBesProductFunction(ProductFunction productFunction) {
        if (StringUtils.isEmpty(productFunction.getType()) ||
                productFunction.getId() == null ||
                productFunction.getFunctionNum() == null ||
                StringUtils.isEmpty(productFunction.getName())
        ) {
            return AjaxResult.error("参数错误");
        }

        //如果是指令下发
        if ("0".equals(productFunction.getIssuedType()) && "1".equals(productFunction.getProofreadingState())) {
            //*10000c0001020000
            //*(固定开头)10(功能码)000c(寄存器起始地址)0001(寄存器数量)02(数据字节数)0000(数据)
            //功能码验证 截取功能码
            String functionCode = productFunction.getInstruct().substring(1, 3);
            //1.16进制转成10进制查看功能吗是不是存在ModbusFunctions类中
            String tenFunctionCode = StringUtil.hexToDecimal(functionCode);
            //根据反射判断是否属于这个类
            boolean functionBoolean = isExistField(Integer.parseInt(tenFunctionCode));
            if (!functionBoolean) {
                //不存在直接返回
                return AjaxResult.error("功能码错误");
            }
            //2.如果是写的话
            if (isWrite(tenFunctionCode)) {
                //寄存器数量 十进制
                String numberStr = StringUtil.hexToDecimal(productFunction.getInstruct().substring(7, 11));
                //数据字节数 十进制
                String byteStr = StringUtil.hexToDecimal(productFunction.getInstruct().substring(11, 13));
                //数据
                String dataStr = productFunction.getInstruct().substring(13);

                //1.验证寄存器地址数量
                if (4 * Integer.parseInt(numberStr) != dataStr.length()) {
                    return AjaxResult.error("寄存器数量/数据错误");
                }

                //2.验证寄存器字节数
                if (2 * Integer.parseInt(byteStr) != dataStr.length()) {
                    //字节数/数据错误
                    return AjaxResult.error("数据字节数/数据错误");
                }
            }
        }
//        else {
//            if (productFunction.getDataItem() == null) {
//                return AjaxResult.error("参数错误");
//            }
//        }


        //code查重
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function).values();
        for (Object j : values) {
            ProductFunction e = (ProductFunction) j;
            if (!e.getId().equals(productFunction.getId())) {
                if (e.getFunctionNum().equals(productFunction.getFunctionNum()) && e.getProductId().equals(productFunction.getProductId())) {
                    return AjaxResult.error("编号重复");
                }
                if (e.getName().equals(productFunction.getName()) && e.getProductId().equals(productFunction.getProductId())) {
                    return AjaxResult.error("名称重复");
                }
            }
        }

        productFunction.setUpdateName(SecurityUtils.getUsername());
        productFunction.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdate = productMapper.updateAthenaBesProductFunction(productFunction);
        if (isUpdate) {

            //获取缓存就
            ProductFunction productFunctionRedis = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, productFunction.getId());


            //修改功能参数
            //1.获取到现在的功能参数
            List<FunctionItemData> listParams = productFunction.getParamsList();
            //2.获取到缓存的功能参数
            List<FunctionItemData> listRedis = productFunctionRedis.getParamsList();

            //查询所有产品信息
            List<ProductFunction> productFunctionsRedis = productMapper.selectAthenaBesProductFunctionList(productFunction);
            productFunction = productFunctionsRedis.get(0);
            if (productFunctionRedis.getDataItem() != null) {
                //3.如果数据项修改，则全部删除再添加
                if (!productFunctionRedis.getDataItem().equals(productFunction.getDataItem())) {
                    //全部删除数据库缓存
                    delFunctionParams(listRedis);
                    //全部添加数据库缓存
                    addFunctionParams(listParams, productFunction);
                } else {
                    //4.如果数据项未变 修改功能参数数据库及缓存
                    changeFunctionList(listParams, listRedis, productFunction);
                }


                //5.设置当前功能参数
                productFunction.setParamsList(listParams);
            }


            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, productFunction.getId(), productFunction);
            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.error("修改失败");
        }
    }

    //修改枚举参数数据库及缓存
    public boolean changeFunctionList(List<FunctionItemData> listParams, List<FunctionItemData> listRedis, ProductFunction productFunction) {
        boolean b = false;

        if ((listParams == null || listParams.size() == 0) && (listRedis != null && listRedis.size() > 0)) {
            //缓存有功能参数，直接删除
            b = delFunctionParams(listRedis);
            if (!b) {
                return b;
            }
        } else if ((listParams != null && listParams.size() > 0) && (listRedis == null || listRedis.size() == 0)) {
            //缓存没有功能参数，直接添加
            b = addFunctionParams(listParams, productFunction);
            if (!b) {
                return b;
            }
        } else if ((listParams != null && listParams.size() > 0) && (listRedis != null && listRedis.size() > 0)) {
            //遍历枚举参数,无id走添加、有id走修改
            listParams.forEach(iteam -> {
                if (iteam.getId() == null) {
                    addFunctionParam(iteam, productFunction);
                } else {
                    updateFunctionParam(iteam, productFunction);
                }
            });
            //删除已经删除的信息
            listRedis.forEach(functionItemData -> {
                boolean flag = listParams.stream().anyMatch(i -> i.getId().equals(functionItemData.getId()));
                //如果flag为false则不在list中,需要删除
                if (!flag) {
                    //删除数据项枚举参数
                    productMapper.deleteParamsItemDataById(functionItemData.getId());
                    //删除缓存
                    redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData, functionItemData.getId());
                }
            });
        }
        return true;
    }


    /**
     * 判断你一个类是否存在某个属性（字段）
     *
     * @param field 字段
     * @return true:存在，false:不存在, null:参数不合法
     */
    public Boolean isExistField(Integer field) {
        if (field == ModbusFunctions.COIL_STATUS ||
                field == ModbusFunctions.INPUT_STATUS ||
                field == ModbusFunctions.HOLDING_REGISTER ||
                field == ModbusFunctions.INPUT_REGISTER ||
                field == ModbusFunctions.WRITE_ONE_COIL ||
                field == ModbusFunctions.WRITE_ONE_HOLDING_REGISTER ||
                field == ModbusFunctions.WRITE_MULTIPLE_COIL ||
                field == ModbusFunctions.WRITE_MULTIPLE_HOLDING_REGISTER) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 功能码是否是写操作
     *
     * @param tenFunctionCode 功能吗
     */
    public Boolean isWrite(String tenFunctionCode) {
        if (Integer.parseInt(tenFunctionCode) == ModbusFunctions.WRITE_ONE_COIL ||
                Integer.parseInt(tenFunctionCode) == ModbusFunctions.WRITE_ONE_HOLDING_REGISTER ||
                Integer.parseInt(tenFunctionCode) == ModbusFunctions.WRITE_MULTIPLE_COIL ||
                Integer.parseInt(tenFunctionCode) == ModbusFunctions.WRITE_MULTIPLE_HOLDING_REGISTER) {
            return true;
        } else {
            return false;
        }

    }

    //删除功能参数数据库、缓存
    public boolean delFunctionParams(List<FunctionItemData> listRedis) {
        boolean b = false;
        if (listRedis != null && listRedis.size() > 0) {
            for (FunctionItemData p : listRedis) {
                //删除数据库枚举参数
                b = productMapper.deleteProductFunctionParamById(p.getId());
                if (!b) {
                    return b;
                }
                //删除高位参数缓存
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData, p.getId());
            }
            return b;
        }
        return true;
    }

    //添加功能参数至数据库、缓存
    public boolean addFunctionParams(List<FunctionItemData> listParams, ProductFunction productFunction) {
        if (listParams != null && listParams.size() > 0) {
            for (FunctionItemData p : listParams) {
                boolean b = addFunctionParam(p, productFunction);
                if (!b) {
                    return false;
                }
            }
        }
        return true;
    }

    //添加功能参数至数据库、缓存
    public boolean addFunctionParam(FunctionItemData p, ProductFunction productFunction) {
        p.setFunctionId(productFunction.getId());
        boolean b = productMapper.insertAthenaBesProductFunctionParams(p);
        if (b) {
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData, p.getId(), p);
            return true;
        } else {
            return false;
        }
    }

    //编辑功能参数至数据库、缓存
    public boolean updateFunctionParam(FunctionItemData p, ProductFunction productFunction) {
        p.setFunctionId(productFunction.getId());
        boolean b = productMapper.updateAthenaBesProductFunctionParams(p);
        if (b) {
            //修改缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params, p.getId(), p);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 修改产品配置-数据项
     *
     * @param productItemData 产品配置-数据项
     * @return AjaxResult
     */
    @Override
    public AjaxResult updateAthenaBesProductItemData(ProductItemData productItemData) {
        //验证参数
        if (productItemData.getId() == null ||
                productItemData.getProductId() == null ||
                productItemData.getDataItemNum() == null ||
                productItemData.getDataItemMark() == null ||
                productItemData.getSortNum() == null ||
                StringUtils.isEmpty(productItemData.getName()) ||
                productItemData.getDataType() == null||
                productItemData.getRegistersNum()==null
        ) {
            return AjaxResult.error("参数错误");
        }
        if ("1".equals(productItemData.getPreserveType())) {
            if (StringUtils.isEmpty(productItemData.getEnergyCode())) {
                return AjaxResult.error("参数错误");
            }
        }

        //寄存器地址、编号、标识、名称查重
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
        for (Object j : values) {
            ProductItemData e = (ProductItemData) j;
            //数据项查重
            if (!e.getId().equals(productItemData.getId())) {
                if (e.getSortNum() != null && e.getSortNum().equals(productItemData.getSortNum()) && e.getProductId().equals(productItemData.getProductId())
                        && !StringUtils.isEmpty(e.getWriteFunctionCode()) && !StringUtils.isEmpty(productItemData.getWriteFunctionCode())
                        && e.getWriteFunctionCode().equals(productItemData.getWriteFunctionCode())
                ) {
                    return AjaxResult.error("寄存器地址重复");
                }

                if (e.getSortNum() != null && e.getSortNum().equals(productItemData.getSortNum()) && e.getProductId().equals(productItemData.getProductId())
                        && !StringUtils.isEmpty(e.getFunctionCode()) && !StringUtils.isEmpty(productItemData.getFunctionCode())
                        && e.getFunctionCode().equals(productItemData.getFunctionCode())
                ) {
                    return AjaxResult.error("寄存器地址重复");
                }

                if (e.getDataItemNum().equals(productItemData.getDataItemNum()) && e.getProductId().equals(productItemData.getProductId())) {
                    return AjaxResult.error("编号重复");
                }
                if (e.getDataItemMark().equals(productItemData.getDataItemMark()) && e.getProductId().equals(productItemData.getProductId())) {
                    return AjaxResult.error("标识重复");
                }
                if (e.getSortNum() != null && e.getName().equals(productItemData.getName()) && e.getProductId().equals(productItemData.getProductId())) {
                    return AjaxResult.error("名称重复");
                }
            }
            //高位参数查重
            if (productItemData.getHighDetail().size() > 0) {
                List<ProductItemData> highDetail = productItemData.getHighDetail();
                for (ProductItemData p : highDetail) {
                    if (e.getDataItemNum().equals(p.getDataItemNum()) && e.getProductId().equals(p.getProductId()) && !e.getId().equals(p.getId())) {
                        return AjaxResult.error("高位参数编号重复");
                    }
                    if (e.getDataItemMark().equals(p.getDataItemMark()) && e.getProductId().equals(p.getProductId()) && !e.getId().equals(p.getId())) {
                        return AjaxResult.error("高位参数标识重复");
                    }
                    if (e.getItemDataId() != null && e.getName().equals(p.getName()) && e.getItemDataId().equals(p.getItemDataId())
                            && !e.getId().equals(p.getId())) {
                        return AjaxResult.error("高位参数名称重复");
                    }
                }
            }
            //低位参数查重
            if (productItemData.getLowDetail().size() > 0) {
                List<ProductItemData> lowDetail = productItemData.getLowDetail();
                for (ProductItemData p : lowDetail) {
                    if (e.getDataItemNum().equals(p.getDataItemNum()) && e.getProductId().equals(p.getProductId()) && !e.getId().equals(p.getId())) {
                        return AjaxResult.error("低位参数编号重复");
                    }
                    if (e.getDataItemMark().equals(p.getDataItemMark()) && e.getProductId().equals(p.getProductId()) && !e.getId().equals(p.getId())) {
                        return AjaxResult.error("低位参数标识重复");
                    }
                    if (e.getItemDataId() != null && e.getName().equals(p.getName()) && e.getItemDataId().equals(p.getItemDataId())
                            && !e.getId().equals(p.getId())) {
                        return AjaxResult.error("低位参数名称重复");
                    }
                }
            }
            //结构体参数查重
            if (productItemData.getStructDetail().size() > 0) {
                List<ProductItemData> structDetail = productItemData.getStructDetail();
                for (ProductItemData p : structDetail) {
                    if (e.getDataItemNum().equals(p.getDataItemNum()) && e.getProductId().equals(p.getProductId()) && !e.getId().equals(p.getId())) {
                        return AjaxResult.error("结构体参数编号重复");
                    }
                    if (e.getDataItemMark().equals(p.getDataItemMark()) && e.getProductId().equals(p.getProductId()) && !e.getId().equals(p.getId())) {
                        return AjaxResult.error("结构体参数标识重复");
                    }
                    if (e.getItemDataId() != null && e.getName().equals(p.getName()) && e.getItemDataId().equals(p.getItemDataId())
                            && !e.getId().equals(p.getId())) {
                        return AjaxResult.error("结构体参数名称重复");
                    }
                }
            }
        }

        //与能耗参数编号查重
        Collection<Object> valuesParam = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_EnergyCollection_ElectricParams).values();
        for (Object el : valuesParam) {
            ElectricParams electricParams = (ElectricParams) el;
            if (electricParams.getCode().equals(productItemData.getDataItemNum())) {
                return AjaxResult.error("编号与能耗参数编号重复");
            }
        }

        //修改数据库
        productItemData.setUpdateName(SecurityUtils.getUsername());
        productItemData.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdate = productMapper.updateAthenaBesProductItemData(productItemData);
        if (isUpdate) {

            //查询所有产品信息
            List<ProductItemData> productItemDatasRedis = productMapper.selectAthenaBesProductItemDataList(productItemData);
            ProductItemData productItemDataUpdate = productItemDatasRedis.get(0);

            //获取当前数据项缓存
            ProductItemData productItemDataRedis = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productItemData.getId());

            //修改数据项枚举参数
            //1.获取到现在的枚举参数
            List<ParamsItemData> enumDetailParams = productItemData.getEnumDetail();
            //2.获取到缓存的枚举参数
            List<ParamsItemData> enumDetailRedis = productItemDataRedis.getEnumDetail();
            //3.修改枚举参数数据库及缓存
            changeEnumDetails(enumDetailParams, enumDetailRedis, productItemData);
            //4.设置当前枚举参数
            productItemDataUpdate.setEnumDetail(enumDetailParams);

            //如果高位参数不为空
//            if (productItemData.getHighDetail().size() > 0) {
            //1.获取到现在的高位参数
            List<ProductItemData> highDetailParams = productItemData.getHighDetail();
            //2.获取到缓存的高位参数
            List<ProductItemData> highDetailRedis = productItemDataRedis.getHighDetail();
            //3.修改高位参数数据库及缓存
            changeHighDetails(highDetailParams, highDetailRedis, productItemData);

            productItemDataUpdate.setHighDetail(highDetailParams);
//            }

            //如果低位参数不为空
//            if (productItemData.getLowDetail().size() > 0) {
            //1.获取到现在的低位参数
            List<ProductItemData> lowDetailParams = productItemData.getLowDetail();
            //2.获取到缓存的低位参数
            List<ProductItemData> lowDetailRedis = productItemDataRedis.getLowDetail();
            //3.修改低位参数数据库及缓存
            changeHighDetails(lowDetailParams, lowDetailRedis, productItemData);

            productItemDataUpdate.setLowDetail(lowDetailParams);
//            }

            //如果结构体参数不为空
//            if (productItemData.getStructDetail().size() > 0) {
            //1.获取到现在的结构体参数
            List<ProductItemData> structDetailParams = productItemData.getStructDetail();
            //2.获取到缓存的结构体参数
            List<ProductItemData> structDetailRedis = productItemDataRedis.getStructDetail();
            //3.修改结构体参数数据库及缓存
            changeHighDetails(structDetailParams, structDetailRedis, productItemData);

            productItemDataUpdate.setStructDetail(structDetailParams);
//            }

            /**判断是否需要变更订阅mqtt主题*/
            if (StringUtils.isNotBlank(productItemDataUpdate.getSubscribeAddress())) {
                /**说明需要订阅mqtt主题*/

                try {
                    MqttHandler.ChangeSub(productItemDataUpdate.getId(), productItemDataUpdate.getSubscribeAddress());
                } catch (Exception e) {
                    log.error(productItemDataUpdate.getName() + "数据项订阅mqtt时出错", e);
                }

            }

            //能源名称
            if(productItemData.getEnergyCode() != null){
                productItemData.setEnergyName(setEnergyName(productItemData));
            }

            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productItemData.getId(), productItemDataUpdate);
            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.error("修改失败");
        }
    }

    //修改枚举参数数据库及缓存
    public boolean changeEnumDetails(List<ParamsItemData> enumDetailParams, List<ParamsItemData> enumDetailRedis, ProductItemData productItemData) {
        boolean b = false;
        if ((enumDetailParams == null || enumDetailParams.size() == 0) && (enumDetailRedis != null && enumDetailRedis.size() > 0)) {
            //缓存有枚举参数，直接删除
            b = delEnumDetails(enumDetailRedis);
            if (!b) {
                return b;
            }
        } else if ((enumDetailParams != null && enumDetailParams.size() > 0) && (enumDetailRedis == null || enumDetailRedis.size() == 0)) {
            //缓存没有枚举参数，直接添加
            b = addEnumDetails(enumDetailParams, productItemData);
            if (!b) {
                return b;
            }
        } else if ((enumDetailParams != null && enumDetailParams.size() > 0) && (enumDetailRedis != null && enumDetailRedis.size() > 0)) {
            //遍历枚举参数,无id走添加、有id走修改
            enumDetailParams.forEach(iteam -> {
                if (iteam.getId() == null) {
                    addEnumDetail(iteam, productItemData);
                } else {
                    updateEnumDetail(iteam, productItemData);
                }
            });
            //删除已经删除的信息
            enumDetailRedis.forEach(paramsItemData -> {
                boolean flag = enumDetailParams.stream().anyMatch(i -> i.getId().equals(paramsItemData.getId()));
                //如果flag为false则不在list中,需要删除
                if (!flag) {
                    //删除数据项枚举参数
                    productMapper.deleteParamsItemDataById(paramsItemData.getId());
                    //删除缓存
                    redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params, paramsItemData.getId());
                }
            });
        }
        return true;
    }

    //删除枚举参数数据库、缓存
    public boolean delEnumDetails(List<ParamsItemData> enumDetailRedis) {
        boolean b = false;
        for (ParamsItemData p : enumDetailRedis) {
            //删除数据库枚举参数
            b = productMapper.deleteParamsItemDataById(p.getId());
            if (!b) {
                return b;
            }
            //删除高位参数缓存
            redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params, p.getId());
        }
        return b;
    }

    //添加枚举参数至数据库、缓存
    public boolean addEnumDetails(List<ParamsItemData> enumDetail, ProductItemData productItemData) {
        for (ParamsItemData p : enumDetail) {
            boolean b = addEnumDetail(p, productItemData);
            if (!b) {
                return false;
            }
        }
        return true;
    }

    //添加枚举参数至数据库、缓存
    public boolean addEnumDetail(ParamsItemData p, ProductItemData productItemData) {
        p.setItemDataId(productItemData.getId());
        boolean b = productMapper.insertAthenaBesProductItemDataParams(p);
        if (b) {
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params, p.getId(), p);
            return true;
        } else {
            return false;
        }
    }

    //编辑枚举参数至数据库、缓存
    public boolean updateEnumDetail(ParamsItemData p, ProductItemData productItemData) {
        p.setItemDataId(productItemData.getId());
        boolean b = productMapper.updateAthenaBesProductItemDataParams(p);
        if (b) {
            //修改缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params, p.getId(), p);
            return true;
        } else {
            return false;
        }
    }

    //修改数据项参数数据库及缓存
    public boolean changeHighDetails(List<ProductItemData> highDetailParams, List<ProductItemData> highDetailRedis, ProductItemData productItemData) {
        boolean b = false;
        if ((highDetailParams == null || highDetailParams.size() == 0) && (highDetailRedis != null && highDetailRedis.size() > 0)) {
            //现在没有高位参数，若缓存有直接全部删除
            b = delHighDetails(highDetailRedis);
            if (!b) {
                return false;
            }
        } else if ((highDetailParams != null && highDetailParams.size() > 0) && ((highDetailRedis == null || highDetailRedis.size() == 0))) {
            //现在有高位参数，若缓存没有直接全部添加
            b = addHighDetails(highDetailParams, productItemData);
            if (!b) {
                return false;
            }

        } else if ((highDetailParams != null && highDetailParams.size() > 0) && (highDetailRedis != null && highDetailRedis.size() > 0)) {
            //遍历高位参数,无id走添加、有id走修改
            highDetailParams.forEach(iteam -> {
                if (iteam.getId() == null) {
                    addHighDetail(iteam, productItemData);
                } else {
                    updateHighDetail(iteam);
                }
            });
            //删除已经删除的信息
            highDetailRedis.forEach(p -> {
                boolean flag = highDetailParams.stream().anyMatch(i -> i.getId().equals(p.getId()));
                //如果flag为false则不在list中,需要删除
                if (!flag) {
                    delHighDetail(p);
                }
            });
        }

        return true;
    }

    //删除数据项高位参数及缓存
    public boolean delHighDetails(List<ProductItemData> highDetailRedis) {
        boolean b = false;
        for (ProductItemData p : highDetailRedis) {
            b = delHighDetail(p);
            if (!b) {
                return b;
            }
        }
        return b;
    }

    //删除数据项高位参数及缓存
    public boolean delHighDetail(ProductItemData productItemData) {
        boolean b = false;
        //删除数据库高位参数
        b = productMapper.deleteParamsItemDataOtherById(productItemData.getId());
        if (!b) {
            return b;
        }
        //删除高位参数缓存
        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productItemData.getId());
        //删除高位参数关联参数数据库
        b = productMapper.deleteAthenaBesParamsItemDataById(productItemData.getId());
        if (!b) {
            return b;
        }
        //删除高位参数关联参数缓存
        Collection<Object> valuesParam = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params).values();
        for (Object el : valuesParam) {
            ParamsItemData paramsItemData = (ParamsItemData) el;
            if (paramsItemData.getItemDataId().equals(productItemData.getId())) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params, paramsItemData.getId());
            }
        }

        //删除高位参数关联功能参数数据库
        b = productMapper.deleteProductFunctionParamsById(productItemData.getId());
        if (!b) {
            return b;
        }
        //删除高位参数关联参数缓存
        Collection<Object> valuesFunctionParam = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData).values();
        for (Object el : valuesFunctionParam) {
            FunctionItemData functionItemData = (FunctionItemData) el;
            if (functionItemData.getItemDataId().equals(productItemData.getId())) {
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, functionItemData.getId());
            }
        }
        return b;
    }

    //添加数据项高位参数及缓存
    public boolean addHighDetails(List<ProductItemData> highDetailParams, ProductItemData productItemData) {
        boolean b = false;
        for (ProductItemData p : highDetailParams) {
            b = addHighDetail(p, productItemData);
            if (!b) {
                return b;
            }
        }
        return b;
    }

    //添加数据项高位参数及缓存
    public boolean addHighDetail(ProductItemData productItemData, ProductItemData productItemDataP) {
        productItemData.setCreateName(SecurityUtils.getUsername());
        productItemData.setCreateTime(DateUtils.getNowDate());
        productItemData.setItemDataId(productItemDataP.getId());
        boolean b = productMapper.insertAthenaBesProductItemData(productItemData);
        if (b) {
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productItemData.getId(), productItemData);
            //如果高位参数有枚举参数，则添加枚举参数
            List<ParamsItemData> enumDetail = productItemData.getEnumDetail();
            if (enumDetail != null && enumDetail.size() > 0) {
                b = addEnumDetails(enumDetail, productItemData);
                if (!b) {
                    return b;
                }
            }
        } else {
            return b;
        }
        return b;
    }

    //修改数据项高位参数及缓存
    public boolean updateHighDetail(ProductItemData productItemData) {
        boolean b = false;
        //获取数据项参数缓存
        ProductItemData productItemDataRedis = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productItemData.getId());
        //获取到缓存中的枚举参数
        List<ParamsItemData> enumDetailRedis = productItemDataRedis.getEnumDetail();
        List<ParamsItemData> enumDetailParams = productItemData.getEnumDetail();
        //修改数据项参数数据库
        b = productMapper.updateAthenaBesProductItemData(productItemData);
        if (!b) {
            return b;
        }
        //修改缓存
        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productItemData.getId(), productItemData);

        //修改数据项参数枚举
        b = changeEnumDetails(enumDetailParams, enumDetailRedis, productItemData);

        return b;
    }


    /**
     * 批量删除产品定义
     *
     * @param ids 需要删除的产品定义主键
     * @return AjaxResult
     */
    @Override
    public AjaxResult deleteAthenaBesProductByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        for (Long id : ids) {
            //数据项+产品配置
            Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
            for (Object o : values) {
                ProductItemData p = (ProductItemData) o;
                if (p.getProductId().equals(id)) {
                    return AjaxResult.error("请先删除当前产品下的数据项");
                }
            }

            Collection<Object> values1 = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function).values();
            for (Object o : values1) {
                ProductFunction pf = (ProductFunction) o;
                if (pf.getProductId().equals(id)) {
                    return AjaxResult.error("请先删除当前产品下的功能");
                }
            }

            Collection<Object> eValues = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
            for (Object j : eValues) {
                Equipment equ = (Equipment) j;
                if (equ.getProductId().equals(id)) {
                    return AjaxResult.error("请先删除当前产品关联的设备");
                }
            }

            boolean isDelete = productMapper.deleteAthenaBesProductById(id);
            if (isDelete) {
                //删除缓存
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product, id);
            } else {
                return AjaxResult.error("删除失败");
            }
        }

        return AjaxResult.success("删除成功");
    }

    /**
     * 批量删除产品配置-数据项
     *
     * @param ids 需要删除的产品配置-数据项主键
     * @return AjaxResult
     */
    @Override
    public AjaxResult deleteAthenaBesProductItemDataByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        //是否被功能绑定
        List<ProductItemData> productItemDatasList = productMapper.selectParamsItemDataByIds(ids);
        for (int i = 0; i < ids.length; i++) {
            ProductItemData add = new ProductItemData();
            add.setId(ids[i]);
            productItemDatasList.add(add);
        }

        List<FunctionItemData> functionItemDataList = productMapper.selectFunctionItemDataByIds(productItemDatasList);
        if (functionItemDataList.size() > 0) {
            return AjaxResult.error("已被功能绑定，请先删除功能定义");
        }

        boolean isDelete = productMapper.deleteAthenaBesProductItemDataByIds(ids);
        if (isDelete) {
            for (Long id : ids) {
                ProductItemData itemData = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, id);

                /**判断是否需要取消订阅mqtt主题*/
                if (StringUtils.isNotBlank(itemData.getSubscribeAddress())) {
                    /**说明需要订阅mqtt主题*/
                    try {
                        MqttHandler.cancelSub(itemData.getId(), itemData.getSubscribeAddress());
                    } catch (Exception e) {
                        log.error(itemData.getName() + "数据项取消订阅mqtt时出错", e);
                    }
                }

                //获取所有数据项参数信息
                List<ProductItemData> idList = productMapper.selectParamsItemDataById(id);
                //删除所有数据项参数枚举参数
                if (idList.size() > 0) {
                    for (ProductItemData delParam : idList) {
                        productMapper.deleteAthenaBesParamsItemDataById(delParam.getId());
                    }
                }
                //删除数据项枚举参数
                productMapper.deleteAthenaBesParamsItemDataById(id);
                //删除所有数据项参数
                productMapper.deleteAthenaBesParamsItemDataOtherById(id);

                //删除缓存
                Collection<Object> valuesParam = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params).values();
                for (Object el : valuesParam) {
                    ParamsItemData paramsItemData = (ParamsItemData) el;
                    //删除数据项枚举参数缓存
                    if (paramsItemData.getItemDataId().equals(id)) {
                        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params, paramsItemData.getId());
                    }
                    //删除数据项参数枚举参数缓存
                    if (idList.size() > 0) {
                        for (ProductItemData delParam : idList) {
                            if (paramsItemData.getItemDataId().equals(delParam.getId())) {
                                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_Params, paramsItemData.getId());
                            }
                        }
                    }
                }
                //参数数据项参数缓存
                Collection<Object> otherParamsCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
                for (Object el : otherParamsCache) {
                    ProductItemData productItemData1 = (ProductItemData) el;
                    if (productItemData1.getItemDataId() != null && productItemData1.getItemDataId().equals(id)) {
                        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productItemData1.getId());
                    }
                }

                //删除数据项缓存
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, id);
            }
            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.error("删除失败");
        }
    }

    /**
     * 批量删除产品配置-功能定义
     *
     * @param ids 需要删除的产品配置-功能定义主键
     * @return AjaxResult
     */
    @Override
    public AjaxResult deleteAthenaBesProductFunctionByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        boolean isDelete = productMapper.deleteAthenaBesProductFunctionByIds(ids);
        if (isDelete) {
            for (Long id : ids) {
                //删除所有参数
                isDelete = productMapper.deleteAthenaBesProductFunctionParamsById(id);
                //删除参数缓存
                Collection<Object> paramsCache = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData).values();
                for (Object j : paramsCache) {
                    FunctionItemData e = (FunctionItemData) j;
                    if (e.getFunctionId().equals(id)) {
                        redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function_ItemData, e.getId());
                    }
                }

                //删除缓存
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, id);
            }
            return AjaxResult.success("删除成功");
        } else {
            return AjaxResult.error("删除失败");
        }
    }

    /**
     * 查询数据类型list
     *
     * @return List<Map < String, Object>>
     */
    @Override
    public List<Map<String, Object>> getAllIotTypeList(String dictType, String id) {
        return productMapper.getAllIotTypeList(dictType, id);
    }

    /**
     * 查询物联类型/消息协议list
     */
    @Override
    public List<Map<String, Object>> getAllMessageIdList(String dictType, String id) {
        return productMapper.getAllMessageIdList(dictType, id);
    }

    /**
     * 查询对应的消息协议list
     */
    @Override
    public AjaxResult getMessageListById(String id) {
        List<Agreement> list = new ArrayList<>();
        if (StringUtils.isEmpty(id)) {
            return AjaxResult.error("参数错误");
        }
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Agreement).values();
        for (Object j : values) {
            Agreement a = (Agreement) j;
            if (id.equals(a.getAgreementType().toString())) {
                list.add(a);
            }
        }
        return AjaxResult.success(list);
    }


    /**
     * @description:根据产品id查询消息协议
     * @author: sunshangeng
     * @date: 2023/3/15 15:05
     * @param: [productId]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult getAgreeMentByProductId(Long productId) {
        Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, productId);
        if (product == null) {
            return AjaxResult.error("查询不到相关产品定义");
        }
        if (product.getMessageProtocol() == null) {
            return AjaxResult.error("查询不到相关消息协议");
        }
        Agreement agreement = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Agreement, product.getMessageProtocol());

        if (agreement == null) {
            return AjaxResult.error("查询不到相关消息协议");
        }
        return AjaxResult.success(agreement);
    }

    /*
    查询所有能源类型
    */
    @Override
    public AjaxResult energyTypeList() {
        List<Map<String, Object>> list = productMapper.energyTypeList();
        return AjaxResult.success(list);
    }

    /**
     * @Description:查询数据项及相应参数配置
     * @Author:gaojikun
     * @Return: AjaxResult
     */
    @Override
    public AjaxResult selectItemDataList(PointControlCommand pointControlCommand) {

        if (pointControlCommand.getId() == null || pointControlCommand.getEquipmentId() == null || pointControlCommand.getEquipmentId() == 0) {
            return AjaxResult.error("查询功能数据项信息参数错误");
        }

        //初始化设计器页面加载
        if (pointControlCommand.getId() != null) {
            //功能
            ProductFunction productFunction = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, (long) pointControlCommand.getId());
            //设备
            Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, (long) pointControlCommand.getEquipmentId());

            if (productFunction == null) {
                return AjaxResult.error("缓存中未查询到功能信息");
            }
            //返回的参数list
            List<ProductItemData> itemDataList = new ArrayList<>();
            //查询功能绑定的数据项
            ProductItemData productItemData =
                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productFunction.getDataItem());

            //设置的参数list
            List<FunctionItemData> paramsList = productFunction.getParamsList();
            //是否绑定了数据项
            if (productItemData != null) {

                if (productItemData.getDataType().equalsIgnoreCase("3")) {//枚举

                    List<ParamsItemData> paramsItemDataList = productItemData.getEnumDetail();

                    if (paramsItemDataList.size() > 0) {
                        ProductItemData productItemData1 = new ProductItemData();
                        productItemData1.setParamsType(productItemData.getDataType());
                        productItemData1.setName(productItemData.getName());
                        productItemData1.setSendValue(productFunction.getItemValue());
                        productItemData1.setEnumDetail(paramsItemDataList);
                        itemDataList.add(productItemData1);

//                        for (ParamsItemData paramsItemData : paramsItemDataList) {
//
//                            productItemData1.setParamsType(productItemData.getParamsType());
//
//                            productItemData1.setSendValue(paramsItemData.getDataValue());
//                            itemDataList.add(productItemData1);
//                        }
                    }
                    //返回的参数list
//                    List<ParamsItemData> itemDataList = new ArrayList<>();
                    return AjaxResult.success(itemDataList);

                }
                //查询数据项参数
                itemDataList = selectParamsListByItemData(paramsList, productItemData, itemDataList);
                return AjaxResult.success(itemDataList);
            } else {
                //指令下发

                //*10000c0001020000
                //*(固定开头)10(功能码)000c(寄存器起始地址)0001(寄存器数量)02(数据字节数)0000(数据)
                //*         10       000c              0001          02          0001
                String instruct = productFunction.getInstruct();
                //寄存器起始地址
                int startIndex = Integer.parseInt(StringUtil.hexToDecimal(instruct.substring(3, 7)));
                //根据寄存器地址获取数据项
                Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
                for (Object o : values) {
                    ProductItemData productItemDataFor = (ProductItemData) o;
                    if (productItemDataFor.getSortNum() != null && productItemDataFor.getSortNum().intValue() == startIndex
                            && productItemDataFor.getProductId().equals(equipment.getProductId())) {
                        //根据数据项获取数据项参数
                        itemDataList = selectParamsListByItemData(null, productItemDataFor, itemDataList);
                        return AjaxResult.success(instruct, itemDataList);
                    }
                }
                return AjaxResult.success(instruct, itemDataList);
            }
        }
        //没有走分支返回空
        return AjaxResult.success(null);
    }

    /**
     * @Description:点位数据项是否含有读功能
     * @Author:gaojikun
     * @Return: AjaxResult
     */
    @Override
    public AjaxResult queryItemDataInfoByFunctionId(PointControlCommand pointControlCommand) {
        ProductFunction productFunction = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function, (long) pointControlCommand.getId());
        if (productFunction == null) {
            return AjaxResult.error("功能缓存中未查询到功能信息");
        }
        //查询功能绑定的数据项读写类型
        ProductItemData productItemData =
                redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productFunction.getDataItem());

        if (productItemData != null) {
            //数据项
            if (!StringUtils.isEmpty(productItemData.getReadType())) {
                //如果包含读操作
                if ("1".equals(productItemData.getReadType())) {
                    //下发采集指令
                    //equipment
                    Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, (long) pointControlCommand.getEquipmentId());
                    //host
                    String host = null;
                    if (equipment.getIpAddress() != null) {
                        host = equipment.getIpAddress();
                    } else {
                        Equipment equipmentParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
                        host = equipmentParent.getIpAddress();
                    }
                    //port
                    Integer port = null;
                    if (equipment.getPortNum() != null) {
                        port = Integer.valueOf(equipment.getPortNum());
                    } else {
                        Equipment equipmentParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
                        port = Integer.valueOf(equipmentParent.getPortNum());
                    }
                    //功能码
                    Integer functionCodeInt = 3;
                    //寄存起始地址
                    Integer beginAddressInt = productItemData.getSortNum().intValue();
                    if (beginAddressInt == 6 || beginAddressInt == 7) {
                        /***************************************按钮照明状态下发对应的寄存器start*********************************************/
                        this.distributeLightInstruct(host, port, equipment, functionCodeInt, beginAddressInt, productItemData);
                        /***************************************按钮照明状态下发对应的寄存器end*********************************************/
                    } else {
                        //寄存器数量
                        Integer addressSizeInt = 1;
                        try {
                            modbusSendSyncMsgHandler.issued2(host, port, equipment, functionCodeInt, beginAddressInt, addressSizeInt, false, null);
                        } catch (Exception e) {
                            System.out.println("按钮下发采集指令出现异常——————————————————————————>" + e);
                        }
                    }
                }
                return AjaxResult.success(productItemData);
            }
        } else {
            //指令
            String instruct = productFunction.getInstruct();
            //剔除错误信息
            if (instruct == null || instruct.length() < 12) {
                return AjaxResult.error("功能指令格式错误");
            }

            //读写功能码判断
            //1.根据寄存器起始地址获取数据项
            int itemDataIndex = Integer.parseInt(StringUtil.hexToDecimal(instruct.substring(3, 7)));
            Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
            for (Object o : values) {
                ProductItemData productItemDataFor = (ProductItemData) o;
                if (productItemDataFor.getSortNum() != null && productItemDataFor.getSortNum().intValue() == itemDataIndex) {
                    //equipment
                    Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, (long) pointControlCommand.getEquipmentId());
                    //host
                    String host = null;
                    if (equipment.getIpAddress() != null) {
                        host = equipment.getIpAddress();
                    } else {
                        Equipment equipmentParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
                        host = equipmentParent.getIpAddress();
                    }
                    //port
                    Integer port = null;
                    if (equipment.getPortNum() != null) {
                        port = Integer.valueOf(equipment.getPortNum());
                    } else {
                        Equipment equipmentParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
                        port = Integer.valueOf(equipmentParent.getPortNum());
                    }
                    //功能码
                    Integer functionCodeInt = 3;
                    //寄存起始地址
                    Integer beginAddressInt = productItemDataFor.getSortNum().intValue();
                    //寄存器数量
                    Integer addressSizeInt = 1;
                    if (instruct.contains("YYMMDDHHmmSS")) {
                        addressSizeInt = 4;
                    }
                    try {
                        modbusSendSyncMsgHandler.issued2(host, port, equipment, functionCodeInt, beginAddressInt, addressSizeInt, false, null);
                    } catch (Exception e) {
                        System.out.println("按钮下发出现异常——————————————————————————>" + e);
                    }
                    return AjaxResult.success(productItemDataFor);
                }
            }
        }

        return AjaxResult.error("未获取到功能绑定的数据项读写信息");
    }

    /**
     * @Description:按钮照明状态下发对应的寄存器
     * @Author:gaojikun
     * @Return: void
     */
    private void distributeLightInstruct(String host, Integer port, Equipment equipment, Integer functionCodeInt,
                                         Integer beginAddressInt, ProductItemData productItemData) {
        //寄存器数量
        Integer addressSizeInt = productItemData.getStructDetail().size();
        //寄存器起始地址
        Integer startAddress = 12;

        if (beginAddressInt == 7 && addressSizeInt <= 16) {
            //7 1-16
            try {
                modbusSendSyncMsgHandler.issued2(host, port, equipment, functionCodeInt, startAddress, addressSizeInt, false, null);
            } catch (Exception e) {
                System.out.println("灯光按钮下发采集指令出现异常——————————————————————————>" + e);
            }
        } else if (beginAddressInt == 6 && addressSizeInt <= 8) {
            Integer startAddressMore = 28;
            //6 17-24
            try {
                modbusSendSyncMsgHandler.issued2(host, port, equipment, functionCodeInt, startAddressMore, addressSizeInt, false, null);
            } catch (Exception e) {
                System.out.println("灯光按钮下发采集指令出现异常——————————————————————————>" + e);
            }
        } else if (beginAddressInt == 7 && addressSizeInt > 16) {
            //7 1-24
            //7 1-16
            try {
                modbusSendSyncMsgHandler.issued2(host, port, equipment, functionCodeInt, startAddress, addressSizeInt, false, null);
            } catch (Exception e) {
                System.out.println("灯光按钮下发采集指令出现异常——————————————————————————>" + e);
            }
            Integer startAddressMore = 28;
            //6 17-24
            try {
                modbusSendSyncMsgHandler.issued2(host, port, equipment, functionCodeInt, startAddressMore, addressSizeInt - 16, false, null);
            } catch (Exception e) {
                System.out.println("灯光按钮下发采集指令出现异常——————————————————————————>" + e);
            }
        }


    }

    /**
     * 根据数据项查询所有数据项参数list
     */
    private List<ProductItemData> selectParamsListByItemData(List<FunctionItemData> paramsList, ProductItemData productItemData, List<ProductItemData> itemDataList) {

        //数据项有参数  添加参数列表
        if (productItemData.getHighDetail() != null && productItemData.getHighDetail().size() > 0) {
            //功能设置的参数值
            if (paramsList != null) {
                productItemData.getHighDetail().forEach(item -> {
                    paramsList.forEach(iteam -> {
                        if (iteam.getItemDataId().equals(item.getId())) {
                            item.setSendValue(iteam.getSendValue());
                        }
                    });
                });
            }
            //添加高位参数
            itemDataList.addAll(productItemData.getHighDetail());
        }
        if (productItemData.getLowDetail() != null && productItemData.getLowDetail().size() > 0) {
            //功能设置的参数值
            if (paramsList != null) {
                productItemData.getLowDetail().forEach(item -> {
                    paramsList.forEach(iteam -> {
                        if (iteam.getItemDataId().equals(item.getId())) {
                            item.setSendValue(iteam.getSendValue());
                        }
                    });
                });
            }
            //添加低位参数
            itemDataList.addAll(productItemData.getLowDetail());
        }
        if (productItemData.getStructDetail() != null && productItemData.getStructDetail().size() > 0) {
            //功能设置的参数值
            if (paramsList != null) {
                productItemData.getStructDetail().forEach(item -> {
                    paramsList.forEach(iteam -> {
                        if (iteam.getItemDataId().equals(item.getId())) {
                            item.setSendValue(iteam.getSendValue());
                        }
                    });
                });
            }
            //添加结构体参数
            itemDataList.addAll(productItemData.getStructDetail());
        }

        if (paramsList == null && itemDataList.size() == 0) {
            //指令下发且没有参数  返回自身参数
            itemDataList.add(productItemData);
        }
        return itemDataList;
    }
}
