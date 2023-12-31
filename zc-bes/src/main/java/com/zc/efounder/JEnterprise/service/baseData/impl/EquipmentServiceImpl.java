package com.zc.efounder.JEnterprise.service.baseData.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.connect.util.StringUtil;
import com.zc.efounder.JEnterprise.domain.baseData.*;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.efounder.JEnterprise.mapper.baseData.EquipmentMapper;
import com.zc.efounder.JEnterprise.mapper.electricPowerTranscription.PowerDataMapper;
import com.zc.efounder.JEnterprise.service.baseData.EquipmentService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 物联设备Service业务层处理
 *
 * @author gaojikun
 * @date 2023-03-08
 */
@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Resource
    private EquipmentMapper athenaBesEquipmentMapper;
    @Resource
    private PowerDataMapper powerDataMapper;

    @Resource
    private RedisCache redisCache;

    @PostConstruct
    public void init() {
        /**
         * 添加数据到 redis 缓存
         */
        addEquipmentCache();

    }

    /**
     * 添加数据到 redis 缓存
     */
    public void addEquipmentCache() {
        // 获取全部物联设备列表数据
        List<Equipment> equipments = athenaBesEquipmentMapper.selectAthenaBesEquipmentList(null);

        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_Equipment);

        if (equipments == null || equipments.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        equipments.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, val.getId(), val);
        });
    }

    /**
     * 查询物联设备
     *
     * @param id 物联设备主键
     * @return 物联设备
     */
    @Override
    public AjaxResult selectAthenaBesEquipmentById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误");
        }
        Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, id);
        return AjaxResult.success(equipment);
    }

    /**
     * 查询物联设备详情
     *
     * @param id 物联设备主键
     * @return 物联设备详情
     */
    @Override
    public AjaxResult selectAthenaBesEquipmentInfoById(Long id) {
        if (id == null) {
            return AjaxResult.error("参数错误");
        }
        Map<String, Object> map = athenaBesEquipmentMapper.selectAthenaBesEquipmentInfoById(id);
        Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, id);
        //设备上线离线状态
        map.put("state", equipment.getState());
        //子设备
        if (map.get("pId") != null && !"".equals(map.get("pId").toString())) {
            Map<String, Object> mapParent = athenaBesEquipmentMapper.selectAthenaBesEquipmentInfoById(Long.parseLong(map.get("pId").toString()));
            if (mapParent.get("communication") != null && !"".equals(mapParent.get("communication").toString())) {
                map.put("communicationParent", mapParent.get("communication").toString());
            } else {
                map.put("communicationParent", "");
            }
            if (mapParent.get("message") != null && !"".equals(mapParent.get("message").toString())) {
                map.put("messageParent", mapParent.get("message").toString());
            } else {
                map.put("messageParent", "");
            }
            if (mapParent.get("ipAddress") != null && !"".equals(mapParent.get("ipAddress").toString())) {
                map.put("ipAddressParent", mapParent.get("ipAddress").toString());
            } else {
                map.put("ipAddressParent", "");
            }
            if (mapParent.get("portNum") != null && !"".equals(mapParent.get("portNum").toString())) {
                map.put("portNumParent", mapParent.get("portNum").toString());
            } else {
                map.put("portNumParent", "");
            }
        }
        return AjaxResult.success(map);
    }


    /**
     * 查询物联设备所有数据项及数据项参数实时数据
     *
     * @param id 物联设备主键
     * @return 物联设备详情
     */
    @Override
    public AjaxResult selectAthenaBesEquipmentActualTimeById(Long id) {

        List<EquipmentItemData> equipmentItemDataList = new ArrayList<>();

        if (id != null) {
            //获取数据库中设备对应的数据项别名
            equipmentItemDataList = athenaBesEquipmentMapper.selectEquipmentItemDataList(id);
        }

        Equipment e = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, id);
        if (e == null) {
            return AjaxResult.error("缓存未取到设备信息");
        }

        HistoryItemData h = new HistoryItemData();
        h.setEquipmentId(id);
        List<Map<String, Object>> returnList = new ArrayList<>();
        Map<String, ProductItemData> productItemDataMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);

        List<Map.Entry<String, ProductItemData>> list = new ArrayList<Map.Entry<String, ProductItemData>>(productItemDataMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, ProductItemData>>() {
            //升序排序
            public int compare(Map.Entry<String, ProductItemData> o1,
                               Map.Entry<String, ProductItemData> o2) {
                return o1.getValue().getId().compareTo(o2.getValue().getId());
            }

        });


        for (Map.Entry<String, ProductItemData> mapping : list) {

            if (mapping.getValue().getProductId().equals(e.getProductId())) {

                if ((mapping.getValue().getHighDetail() == null || mapping.getValue().getHighDetail().size() == 0) &&
                        (mapping.getValue().getLowDetail() == null || mapping.getValue().getLowDetail().size() == 0) &&
                        (mapping.getValue().getStructDetail() == null || mapping.getValue().getStructDetail().size() == 0) &&
                        mapping.getValue().getItemDataId() == null) {

                    for (EquipmentItemData equipmentItemData : equipmentItemDataList) {
                        if (mapping.getValue().getId().equals(equipmentItemData.getItemDataId())) {
                            mapping.getValue().setItemDataCustomName(equipmentItemData.getItemDataCustomName());
                        }
                    }

                    Map<String, Object> map = new HashMap<String, Object>() {{
                        if (mapping.getValue().getItemDataCustomName() != null) {
                            put("label", mapping.getValue().getItemDataCustomName());
                        } else {
                            put("label", mapping.getValue().getName());
                        }

                        put("code", mapping.getValue().getDataItemNum());
                        put("unit", mapping.getValue().getDataUnit());
                    }};
                    //实时数据取历史表最后一条数据 //最后上传时间
                    h.setItemDataId(mapping.getValue().getId());
                    map = selectHistoryItemDataMap(map, h, mapping.getValue());
                    returnList.add(map);
                }


                //添加数据项参数
                if (mapping.getValue().getHighDetail() != null && mapping.getValue().getHighDetail().size() > 0) {
                    List<ProductItemData> highDetail = mapping.getValue().getHighDetail();
                    returnList = selectHistoryItemDataSonMap(highDetail, returnList, h, mapping.getValue().getName());
                }

                if (mapping.getValue().getLowDetail() != null && mapping.getValue().getLowDetail().size() > 0) {
                    List<ProductItemData> lowDetail = mapping.getValue().getLowDetail();
                    returnList = selectHistoryItemDataSonMap(lowDetail, returnList, h, mapping.getValue().getName());
                }

                if (mapping.getValue().getStructDetail() != null && mapping.getValue().getStructDetail().size() > 0) {
                    List<ProductItemData> structDetail = mapping.getValue().getStructDetail();
                    returnList = selectHistoryItemDataSonMap(structDetail, returnList, h, mapping.getValue().getName());
                }
            }
        }

        return AjaxResult.success(returnList);
    }

    /**
     * 查询物联设备数据项最新实时数据及时间
     *
     * @param map                  返回的实时数据信息
     * @param historyItemDataParam 实时数据参数
     * @return Map<String, Object>
     */
    public Map<String, Object> selectHistoryItemDataMap(Map<String, Object> map, HistoryItemData historyItemDataParam, ProductItemData productItemData) {

        if ("0".equals(productItemData.getPreserveType())) {
            Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_RealTime).values();
            for (Object j : values) {
                ProductItemRealTimeData productItemRealTimeData = (ProductItemRealTimeData) j;
                if (productItemRealTimeData.getEquipmentId().equals(historyItemDataParam.getEquipmentId()) && productItemRealTimeData.getItemDataId().equals(productItemData.getId())) {
                    if (productItemRealTimeData.getDataValue() == null) {
                        map.put("value", "-");
                    } else {
                        map.put("value", productItemRealTimeData.getDataValue());
                    }
                    if (productItemRealTimeData.getCreateTime() == null) {
                        map.put("time", "-");
                    } else {
                        map.put("time", productItemRealTimeData.getCreateTime());
                    }
                }
            }

            if (map.get("value") == null) {
                map.put("value", "-");
            }
            if (map.get("time") == null) {
                map.put("time", "-");
            }
        } else {
            HistoryItemData historyItemData = athenaBesEquipmentMapper.selectEquipmentLastData(historyItemDataParam);
            if (historyItemData == null || historyItemData.getDataValue() == null) {
                map.put("value", "-");
            } else {
                map.put("value", historyItemData.getDataValue());
            }

            if (historyItemData == null || historyItemData.getTime() == null) {
                map.put("time", "-");
            } else {
                map.put("time", historyItemData.getTime());
            }
        }

        return map;
    }

    /**
     * 查询物联设备数据项参数最新实时数据及时间
     *
     * @param paramsDetail         子参数集合
     * @param returnList           返回的参数集合
     * @param historyItemDataParam 查询参数
     * @param pName                父参数名称
     * @return List<Map < String, Object>>
     */
    public List<Map<String, Object>> selectHistoryItemDataSonMap(List<ProductItemData> paramsDetail, List<Map<String, Object>> returnList, HistoryItemData historyItemDataParam, String pName) {
        for (ProductItemData p : paramsDetail) {
            Map<String, Object> map = new HashMap<String, Object>() {{
                put("label", pName + "-" + p.getName());
                put("code", p.getDataItemNum());
                put("unit", p.getDataUnit());
            }};
            if ("0".equals(p.getPreserveType())) {
                Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData_RealTime).values();
                for (Object j : values) {
                    ProductItemRealTimeData productItemRealTimeData = (ProductItemRealTimeData) j;
                    if (productItemRealTimeData.getEquipmentId().equals(historyItemDataParam.getEquipmentId()) && productItemRealTimeData.getItemDataId().equals(p.getId())) {
                        if (productItemRealTimeData.getDataValue() == null) {
                            map.put("value", "-");
                        } else {
                            map.put("value", productItemRealTimeData.getDataValue());
                        }
                        if (productItemRealTimeData.getCreateTime() == null) {
                            map.put("time", "-");
                        } else {
                            map.put("time", productItemRealTimeData.getCreateTime());
                        }
                    }
                }

                if (map.get("value") == null) {
                    map.put("value", "-");
                }
                if (map.get("time") == null) {
                    map.put("time", "-");
                }

                returnList.add(map);
            } else {
                //实时数据取历史表最后一条数据 //最后上传时间
                historyItemDataParam.setItemDataId(p.getId());
                HistoryItemData historyItemData = athenaBesEquipmentMapper.selectEquipmentLastData(historyItemDataParam);
                if (historyItemData == null || historyItemData.getDataValue() == null) {
                    map.put("value", "-");
                } else {
                    map.put("value", historyItemData.getDataValue());
                }

                if (historyItemData == null || historyItemData.getTime() == null) {
                    map.put("time", "-");
                } else {
                    map.put("time", historyItemData.getTime());
                }
                returnList.add(map);
            }
        }

        return returnList;
    }

    /**
     * 获取物联设备是保存的数据项列表
     *
     * @param id 物联设备主键
     * @return 物联设备详情
     */
    @Override
    public AjaxResult getActualTimePreserve(Long id) {
        Equipment e = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, id);
        if (e == null) {
            return AjaxResult.error("缓存未取到设备信息");
        }

        HistoryItemData h = new HistoryItemData();
        h.setEquipmentId(id);
        List<Map<String, Object>> returnList = new ArrayList<>();
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
        for (Object j : values) {
            ProductItemData p = (ProductItemData) j;
            if (p.getProductId().equals(e.getProductId())) {
                //添加数据项参数
                if (p.getHighDetail() != null && p.getHighDetail().size() > 0) {
                    List<ProductItemData> highDetail = p.getHighDetail();
                    returnList = getActualTimePreserveMap(highDetail, returnList, p.getName());
                }

                if (p.getLowDetail() != null && p.getLowDetail().size() > 0) {
                    List<ProductItemData> lowDetail = p.getLowDetail();
                    returnList = getActualTimePreserveMap(lowDetail, returnList, p.getName());
                }

                if (p.getStructDetail() != null && p.getStructDetail().size() > 0) {
                    List<ProductItemData> structDetail = p.getStructDetail();
                    returnList = getActualTimePreserveMap(structDetail, returnList, p.getName());
                }

                if ((p.getHighDetail() == null || p.getHighDetail().size() == 0) &&
                        (p.getLowDetail() == null || p.getLowDetail().size() == 0) &&
                        (p.getStructDetail() == null || p.getStructDetail().size() == 0) &&
                        "1".equals(p.getPreserveType()) && p.getItemDataId() == null) {
                    //添加该数据项
                    Map<String, Object> map = new HashMap<String, Object>() {{
                        put("label", p.getName());
                        put("code", p.getDataItemNum());
                    }};

                    returnList.add(map);
                }
            }
        }

        return AjaxResult.success(returnList);
    }

    /**
     * 获取物联设备是保存的数据项参数列表
     *
     * @param paramsDetail 子参数集合
     * @param returnList   返回的参数集合
     * @param pName        父参数名称
     * @return List<Map < String, Object>>
     */
    public List<Map<String, Object>> getActualTimePreserveMap(List<ProductItemData> paramsDetail, List<Map<String, Object>> returnList, String pName) {
        for (ProductItemData p : paramsDetail) {
            if ("1".equals(p.getPreserveType())) {
                Map<String, Object> map = new HashMap<String, Object>() {{
                    put("label", pName + "-" + p.getName());
                    put("code", p.getDataItemNum());
                }};
                returnList.add(map);
            }
        }

        return returnList;
    }

    /**
     * 查询物联设备历史数据
     *
     * @param equ 物联设备
     * @return 物联设备详情
     */
    @Override
    public List<Map<String, Object>> selectItemDataHistoryListById(Equipment equ) {
        List<Map<String, Object>> list = new ArrayList<>();
        Equipment e = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equ.getId());
        if (e == null) {
            return list;
        }
        List<Long> idList = new ArrayList<>();
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
        for (Object j : values) {
            ProductItemData p = (ProductItemData) j;
            if (p.getProductId().equals(e.getProductId())) {
                idList.add(p.getId());
                if (p.getHighDetail() != null && p.getHighDetail().size() > 0) {
                    List<ProductItemData> highDetail = p.getHighDetail();
                    for (ProductItemData ph : highDetail) {
                        idList.add(ph.getId());
                    }
                }
                if (p.getLowDetail() != null && p.getLowDetail().size() > 0) {
                    List<ProductItemData> lowDetail = p.getLowDetail();
                    for (ProductItemData pl : lowDetail) {
                        idList.add(pl.getId());
                    }
                }
                if (p.getStructDetail() != null && p.getStructDetail().size() > 0) {
                    List<ProductItemData> structDetail = p.getStructDetail();
                    for (ProductItemData ps : structDetail) {
                        idList.add(ps.getId());
                    }
                }

            }
        }
        if (idList.size() > 0) {
            List<HistoryItemData> hList;
            if ("1".equals(e.getMeterState())) {
                hList = athenaBesEquipmentMapper.selectMemterHistoryListById(idList, equ.getStartDate(), equ.getEndDate(), e.getCode());
            } else {
                hList = athenaBesEquipmentMapper.selectItemDataHistoryListById(idList, equ.getStartDate(), equ.getEndDate(), equ.getId());
            }

            if (hList.size() == 0) {
                return list;
            }

            //数据组装
            Map<String, List<HistoryItemData>> groupMap = hList.stream().collect(Collectors.groupingBy(HistoryItemData::getTime));
            //排序
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Map<String, List<HistoryItemData>> sortMap = groupMap.entrySet().stream()
                    .sorted(new Comparator<Map.Entry<String, List<HistoryItemData>>>() {
                        @Override
                        public int compare(Map.Entry<String, List<HistoryItemData>> o1, Map.Entry<String, List<HistoryItemData>> o2) {
                            try {
                                Date key1 = simpleDateFormat.parse(o1.getKey());
                                Date key2 = simpleDateFormat.parse(o2.getKey());
                                return key2.compareTo(key1);
                            } catch (Exception exception) {
                                return 0;
                            }
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));


            List<Map<String, Object>> returnList = new ArrayList<>();
            sortMap.forEach((time, dateList) -> {
                Map<String, Object> addMap = new HashMap();
                addMap.put("time", dateList.get(0).getTime());
                dateList.forEach((map) -> {
                    addMap.put(map.getCode(), map.getDataValue());
                });
                returnList.add(addMap);
            });

            return returnList;

        } else {
            return list;
        }

    }

    /**
     * 查询物联设备报警历史数据
     *
     * @param equ 物联设备
     * @return 物联设备详情
     */
    @Override
    public List<WarnItemData> selectItemDataWarnDataListById(Equipment equ) {
        return athenaBesEquipmentMapper.selectItemDataWarnDataListById(equ.getId(), equ.getStartDate(), equ.getEndDate());
    }

    /**
     * 查询物联设备列表
     *
     * @param athenaBesEquipment 物联设备
     * @return 物联设备
     */
    @Override
    public List<Equipment> selectAthenaBesEquipmentList(Equipment athenaBesEquipment) {
        List<Equipment> list = new ArrayList<>();
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
        for (Object j : values) {
            Equipment e = (Equipment) j;
            //名称 、编号、 品类
            if (athenaBesEquipment != null && athenaBesEquipment.getCategoryId() != null
                    && !StringUtils.isEmpty(athenaBesEquipment.getName())
                    && !StringUtils.isEmpty(athenaBesEquipment.getCode())) {
                if (e.getCategoryId().equals(athenaBesEquipment.getCategoryId())
                        && (e.getName().contains(athenaBesEquipment.getName()) || e.getName().equals(athenaBesEquipment.getName()))
                        && (e.getCode().contains(athenaBesEquipment.getCode()) || e.getCode().equals(athenaBesEquipment.getCode()))) {
                    Product p = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, e.getProductId());
                    e.setProductCode(p.getCode());
                    //子设备
                    if (athenaBesEquipment.getpId() != null) {
                        if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                            Equipment eParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, e.getpId());
                            e.setParentCode(eParent.getCode());
                            list.add(e);
                        }
                    } else {
                        if (e.getpId() == null) {
                            list.add(e);
                        }
                    }
                }
                continue;
            }
            //名称 、 品类
            if (athenaBesEquipment != null && athenaBesEquipment.getCategoryId() != null
                    && !StringUtils.isEmpty(athenaBesEquipment.getName())) {
                if (e.getCategoryId().equals(athenaBesEquipment.getCategoryId())
                        && (e.getName().contains(athenaBesEquipment.getName()) || e.getName().equals(athenaBesEquipment.getName()))) {
                    Product p = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, e.getProductId());
                    e.setProductCode(p.getCode());
                    //子设备
                    if (athenaBesEquipment.getpId() != null) {
                        if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                            Equipment eParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, e.getpId());
                            e.setParentCode(eParent.getCode());
                            list.add(e);
                        }
                    } else {
                        if (e.getpId() == null) {
                            list.add(e);
                        }
                    }
                }
                continue;
            }
            //编号 、 品类
            if (athenaBesEquipment != null && athenaBesEquipment.getCategoryId() != null
                    && !StringUtils.isEmpty(athenaBesEquipment.getCode())) {
                if (e.getCategoryId().equals(athenaBesEquipment.getCategoryId())
                        && (e.getCode().contains(athenaBesEquipment.getCode()) || e.getCode().equals(athenaBesEquipment.getCode()))) {
                    Product p = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, e.getProductId());
                    e.setProductCode(p.getCode());
                    //子设备
                    if (athenaBesEquipment.getpId() != null) {
                        if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                            Equipment eParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, e.getpId());
                            e.setParentCode(eParent.getCode());
                            list.add(e);
                        }
                    } else {
                        if (e.getpId() == null) {
                            list.add(e);
                        }
                    }
                }
                continue;
            }
            //编号 、 名称
            if (athenaBesEquipment != null && !StringUtils.isEmpty(athenaBesEquipment.getName())
                    && !StringUtils.isEmpty(athenaBesEquipment.getCode())) {
                if ((e.getName().contains(athenaBesEquipment.getName()) || e.getName().equals(athenaBesEquipment.getName()))
                        && (e.getCode().contains(athenaBesEquipment.getCode()) || e.getCode().equals(athenaBesEquipment.getCode()))) {
                    Product p = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, e.getProductId());
                    e.setProductCode(p.getCode());
                    //子设备
                    if (athenaBesEquipment.getpId() != null) {
                        if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                            Equipment eParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, e.getpId());
                            e.setParentCode(eParent.getCode());
                            list.add(e);
                        }
                    } else {
                        if (e.getpId() == null) {
                            list.add(e);
                        }
                    }
                }
                continue;
            }
            //编号
            if (athenaBesEquipment != null && !StringUtils.isEmpty(athenaBesEquipment.getName())) {
                if (e.getName().contains(athenaBesEquipment.getName()) || e.getName().equals(athenaBesEquipment.getName())) {
                    Product p = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, e.getProductId());
                    e.setProductCode(p.getCode());
                    //子设备
                    if (athenaBesEquipment.getpId() != null) {
                        if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                            Equipment eParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, e.getpId());
                            e.setParentCode(eParent.getCode());
                            list.add(e);
                        }
                    } else {
                        if (e.getpId() == null) {
                            list.add(e);
                        }
                    }
                }
                continue;
            }
            //名称
            if (athenaBesEquipment != null && !StringUtils.isEmpty(athenaBesEquipment.getCode())) {
                if (e.getCode().contains(athenaBesEquipment.getCode()) || e.getCode().equals(athenaBesEquipment.getCode())) {
                    Product p = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, e.getProductId());
                    e.setProductCode(p.getCode());
                    //子设备
                    if (athenaBesEquipment.getpId() != null) {
                        if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                            Equipment eParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, e.getpId());
                            e.setParentCode(eParent.getCode());
                            list.add(e);
                        }
                    } else {
                        if (e.getpId() == null) {
                            list.add(e);
                        }
                    }
                }
                continue;
            }
            //品类
            if (athenaBesEquipment != null && athenaBesEquipment.getCategoryId() != null) {
                if (e.getCategoryId().equals(athenaBesEquipment.getCategoryId())) {
                    Product p = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, e.getProductId());
                    e.setProductCode(p.getCode());
                    //子设备
                    if (athenaBesEquipment.getpId() != null) {
                        if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                            Equipment eParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, e.getpId());
                            e.setParentCode(eParent.getCode());
                            list.add(e);
                        }
                    } else {
                        if (e.getpId() == null) {
                            list.add(e);
                        }
                    }
                }
                continue;
            }
            //无条件查询
            if (athenaBesEquipment == null || (athenaBesEquipment.getCategoryId() == null
                    && StringUtils.isEmpty(athenaBesEquipment.getName())
                    && StringUtils.isEmpty(athenaBesEquipment.getCode()))) {
                Product p = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, e.getProductId());
                e.setProductCode(p.getCode());
                //子设备
                if (athenaBesEquipment.getpId() != null) {
                    if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                        Equipment eParent = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, e.getpId());
                        e.setParentCode(eParent.getCode());
                        list.add(e);
                    }
                } else {
                    if (e.getpId() == null) {
                        list.add(e);
                    }
                }
                continue;
            }
        }

        if (list.size() > 0) {
            // 排序.reversed())
            if (list.get(0).getSonAddress() == null) {
                list = list.stream().sorted(Comparator.comparing(Equipment::getCreateTime))
                        .collect(Collectors.toList());
            } else {
                list = list.stream().sorted(Comparator.comparing(Equipment::getSonAddress))
                        .collect(Collectors.toList());
            }
        }

        return list;
    }

    /**
     * 新增物联设备
     *
     * @param athenaBesEquipment 物联设备
     * @return 结果
     */
    @Override
    public AjaxResult insertAthenaBesEquipment(Equipment athenaBesEquipment) {
        if (StringUtils.isEmpty(athenaBesEquipment.getCode()) ||
                StringUtils.isEmpty(athenaBesEquipment.getName()) ||
                athenaBesEquipment.getProductId() == null ||
                StringUtils.isEmpty(athenaBesEquipment.getParkCode())) {
            return AjaxResult.error("参数错误");
        }
        if (athenaBesEquipment.getpId() == null) {
            if (StringUtils.isEmpty(athenaBesEquipment.getIpAddress()) ||
                    StringUtils.isEmpty(athenaBesEquipment.getPortNum())) {
                return AjaxResult.error("参数错误");
            }
        } else {
            if (athenaBesEquipment.getSonAddress() == null ||
                    StringUtils.isEmpty(athenaBesEquipment.getMeterState())) {
                return AjaxResult.error("参数错误");
            }
        }

        if (StringUtils.isEmpty(athenaBesEquipment.getState())) {
            athenaBesEquipment.setState("0");
        }

        //查重产品iD/编号
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
        for (Object j : values) {
            Equipment e = (Equipment) j;
            if (e.getCode().equals(athenaBesEquipment.getCode()) /*|| e.getProductId().equals(athenaBesEquipment.getProductId())*/) {
                return AjaxResult.error(/*产品/*/"编号重复");
            }
            if (e.getCode().equals(athenaBesEquipment.getName()) && !e.getId().equals(athenaBesEquipment.getId())) {
                if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                    return AjaxResult.error("名称重复");
                }
            }
            if (e.getSonAddress() != null && e.getSonAddress().equals(athenaBesEquipment.getSonAddress()) && !e.getId().equals(athenaBesEquipment.getId())) {
                if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                    return AjaxResult.error("设备地址重复");
                }
            }
        }
        //是否与电表名称重复
        if (athenaBesEquipment.getpId() != null && "1".equals(athenaBesEquipment.getMeterState())) {
            Collection<Object> valueMeter = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter).values();
            for (Object me : valueMeter) {
                AthenaElectricMeter meter = (AthenaElectricMeter) me;
                if (meter.getSysName().equals(athenaBesEquipment.getCode())) {
                    return AjaxResult.error("设备编号与电表系统名称重复");
                }
            }
        }

        athenaBesEquipment.setCreateName(SecurityUtils.getUsername());
        athenaBesEquipment.setCreateTime(DateUtils.getNowDate());
        boolean isADD = athenaBesEquipmentMapper.insertAthenaBesEquipment(athenaBesEquipment);
        if (isADD) {

            //获取自定义数据项别名
            List<ProductItemData> productItemDataList = athenaBesEquipment.getItemDataList();
            if (productItemDataList != null && productItemDataList.size() > 0) {

                List<EquipmentItemData> equipmentItemDataList = new ArrayList<>();
                for (ProductItemData productItemData : productItemDataList) {

                    String itemDataCustomName = productItemData.getItemDataCustomName();
                    if (itemDataCustomName == null) {
                        continue;
                    }
                    EquipmentItemData equipmentItemData = new EquipmentItemData();
                    equipmentItemData.setEquipmentId(athenaBesEquipment.getId());
                    equipmentItemData.setItemDataId(productItemData.getItemDataId());
                    equipmentItemData.setItemDataCustomName(productItemData.getItemDataCustomName());

                    /**获取当前用户名*/
                    String username = SecurityUtils.getUsername();
                    equipmentItemData.setCreateBy(username);
                    equipmentItemData.setCreateTime(DateUtils.getNowDate());

                    equipmentItemDataList.add(equipmentItemData);
                }

                athenaBesEquipmentMapper.insertAthenaBesEquipmentItemData(equipmentItemDataList);
            }

            if (athenaBesEquipment.getpId() != null) {
                Equipment parentEqu = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, athenaBesEquipment.getpId());
                Integer sonNum = parentEqu.getSonNum() + 1;
                parentEqu.setSonNum(sonNum);
                redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, athenaBesEquipment.getpId(), parentEqu);
            }
            //查询所有产品信息
            List<Equipment> equipmentsRedis = athenaBesEquipmentMapper.selectAthenaBesEquipmentList(athenaBesEquipment);
            athenaBesEquipment = equipmentsRedis.get(0);
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, athenaBesEquipment.getId(), athenaBesEquipment);
            return AjaxResult.success("添加成功");
        } else {
            return AjaxResult.error("添加失败");
        }
    }


    /**
     * 修改物联设备离线报警状态
     *
     * @param athenaBesEquipment 物联设备
     * @return 结果
     */
    @Override
    public AjaxResult updateAthenaBesEquipmentOfflineAlarm(Equipment athenaBesEquipment) {
        if (athenaBesEquipment.getId() == null ||
                StringUtils.isEmpty(athenaBesEquipment.getOfflineAlarm())) {
            return AjaxResult.error("参数错误");
        }
        Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, athenaBesEquipment.getId());
        equipment.setOfflineAlarm(athenaBesEquipment.getOfflineAlarm());
        boolean isUpdtae = athenaBesEquipmentMapper.updateAthenaBesEquipment(athenaBesEquipment);
        if (isUpdtae) {
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getId(), equipment);
            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.error("修改失败");
        }
    }

    /**
     * 修改物联设备
     *
     * @param athenaBesEquipment 物联设备
     * @return 结果
     */
    @Override
    public AjaxResult updateAthenaBesEquipment(Equipment athenaBesEquipment) {
        if (athenaBesEquipment.getId() == null ||
                StringUtils.isEmpty(athenaBesEquipment.getCode()) ||
                StringUtils.isEmpty(athenaBesEquipment.getName()) ||
                athenaBesEquipment.getProductId() == null ||
                StringUtils.isEmpty(athenaBesEquipment.getParkCode())) {
            return AjaxResult.error("参数错误");
        }
        if (athenaBesEquipment.getpId() == null) {
            if (StringUtils.isEmpty(athenaBesEquipment.getIpAddress()) || StringUtils.isEmpty(athenaBesEquipment.getPortNum())) {
                return AjaxResult.error("参数错误");
            }
        } else {
            if (athenaBesEquipment.getSonAddress() == null ||
                    StringUtils.isEmpty(athenaBesEquipment.getMeterState())) {
                return AjaxResult.error("参数错误");
            }
        }

        if (StringUtils.isEmpty(athenaBesEquipment.getState())) {
            athenaBesEquipment.setState("0");
        }

        //查重产品iD/编号
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
        for (Object j : values) {
            Equipment e = (Equipment) j;
            /*|| e.getProductId().equals(athenaBesEquipment.getProductId())*/
            if (e.getCode().equals(athenaBesEquipment.getCode()) && !e.getId().equals(athenaBesEquipment.getId())) {
                return AjaxResult.error(/*产品/*/"编号重复");
            }
            if (e.getCode().equals(athenaBesEquipment.getName()) && !e.getId().equals(athenaBesEquipment.getId())) {
                if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                    return AjaxResult.error("名称重复");
                }
            }
            if (e.getSonAddress() != null && e.getSonAddress().equals(athenaBesEquipment.getSonAddress()) && !e.getId().equals(athenaBesEquipment.getId())) {
                if (e.getpId() != null && e.getpId().equals(athenaBesEquipment.getpId())) {
                    return AjaxResult.error("设备地址重复");
                }
            }
        }
        //是否与电表名称重复
        if (athenaBesEquipment.getpId() != null && "1".equals(athenaBesEquipment.getMeterState())) {
            Collection<Object> valueMeter = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_DeviceTree_Meter).values();
            for (Object me : valueMeter) {
                AthenaElectricMeter meter = (AthenaElectricMeter) me;
                if (meter.getSysName().equals(athenaBesEquipment.getCode())) {
                    return AjaxResult.error("设备编号与电表系统名称重复");
                }
            }
        }

        athenaBesEquipment.setUpdateName(SecurityUtils.getUsername());
        athenaBesEquipment.setUpdateTime(DateUtils.getNowDate());
        boolean isUpdtae = athenaBesEquipmentMapper.updateAthenaBesEquipment(athenaBesEquipment);
        if (isUpdtae) {

            //获取自定义数据项别名
            List<ProductItemData> productItemDataList = athenaBesEquipment.getItemDataList();
            if (productItemDataList != null && productItemDataList.size() > 0) {

                List<EquipmentItemData> equipmentItemDataList = new ArrayList<>();
                for (ProductItemData productItemData : productItemDataList) {

                    String itemDataCustomName = productItemData.getItemDataCustomName();
                    if (itemDataCustomName == null) {
                        continue;
                    }
                    EquipmentItemData equipmentItemData = new EquipmentItemData();
                    equipmentItemData.setEquipmentId(athenaBesEquipment.getId());
                    equipmentItemData.setItemDataId(productItemData.getItemDataId());
                    equipmentItemData.setItemDataCustomName(productItemData.getItemDataCustomName());

                    /**获取当前用户名*/
                    String username = SecurityUtils.getUsername();
                    equipmentItemData.setCreateBy(username);
                    equipmentItemData.setCreateTime(DateUtils.getNowDate());

                    equipmentItemDataList.add(equipmentItemData);
                }
                if (equipmentItemDataList.size() != 0) {
                    //先删除再添加
                    Boolean boo = athenaBesEquipmentMapper.deleteAthenaBesEquipmentItemData(athenaBesEquipment.getId());
                    athenaBesEquipmentMapper.insertAthenaBesEquipmentItemData(equipmentItemDataList);
                }


            }

            //查询所有产品信息
            List<Equipment> equipmentsRedis = athenaBesEquipmentMapper.selectAthenaBesEquipmentList(athenaBesEquipment);
            athenaBesEquipment = equipmentsRedis.get(0);
            //添加缓存
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, athenaBesEquipment.getId(), athenaBesEquipment);
            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.error("修改失败");
        }
    }

    /**
     * 批量删除物联设备
     *
     * @param ids 需要删除的物联设备主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteAthenaBesEquipmentByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return AjaxResult.error("参数错误");
        }
        boolean isDelete;
        //查询对应的子设备
        for (Long id : ids) {
            //是否有子设备，有子设备先删除子设备
            Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
            for (Object j : values) {
                Equipment e = (Equipment) j;
                if (e.getpId() != null && e.getpId().equals(id)) {
                    return AjaxResult.error("请先删除该设备下的子设备");
                }
            }

            isDelete = athenaBesEquipmentMapper.deleteAthenaBesEquipmentById(id);
            if (isDelete) {

                athenaBesEquipmentMapper.deleteAthenaBesEquipmentItemData(id);

                //修改父缓存
                Equipment sonEqu = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, id);
                if (sonEqu.getpId() != null) {
                    Equipment parentEqu = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, sonEqu.getpId());
                    Integer sonNum = parentEqu.getSonNum() - 1;
                    parentEqu.setSonNum(sonNum);
                    redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, sonEqu.getpId(), parentEqu);
                }
                //删除缓存
                redisCache.delCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, id);
            } else {
                return AjaxResult.error("删除失败");
            }
        }

        return AjaxResult.success("删除成功");
    }

    /**
     * 导入物联设备
     **/
    @Override
    public String importEquipment(List<Equipment> equipmentList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(equipmentList) || equipmentList.size() == 0) {
            throw new ServiceException("导入物联设备数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        a:
        for (Equipment equipment : equipmentList) {
            try {
                //非空判断
                if (StringUtils.isEmpty(equipment.getProductCode())) {
                    failureNum++;
                    String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 产品编号为空：";
                    failureMsg.append(msg);
                    continue a;
                }
                if (StringUtils.isEmpty(equipment.getCode())) {
                    failureNum++;
                    String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getName() + " 设备编号为空：";
                    failureMsg.append(msg);
                    continue a;
                }
                if (StringUtils.isEmpty(equipment.getName())) {
                    failureNum++;
                    String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/ 设备名称为空：";
                    failureMsg.append(msg);
                    continue a;
                }
                if (StringUtils.isEmpty(equipment.getParentCode())) {
                    //网关/直连
                    if (StringUtils.isEmpty(equipment.getNetworkType())) {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 网络类型为空(或未填写客户端/服务器)：";
                        failureMsg.append(msg);
                        continue a;
                    }
                    if (StringUtils.isEmpty(equipment.getIpAddress())) {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " ip地址为空：";
                        failureMsg.append(msg);
                        continue a;
                    }
                    if (StringUtils.isEmpty(equipment.getPortNum())) {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 端口为空：";
                        failureMsg.append(msg);
                        continue a;
                    }
                }


                if (StringUtils.isEmpty(equipment.getState())) {
                    equipment.setState("0");
                }
                // 验证是否存在这个物联设备
                Equipment equipment1 = athenaBesEquipmentMapper.getInfoByCode(equipment.getCode());
                // 查重处理
//                List<Equipment> checkList = athenaBesEquipmentMapper.selectAthenaBesEquipmentListCheck(equipment);
//                if(checkList.size()>0){
//                    successMsg.append("<br/>" + successNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 导入失败，编号/名称重复");
//                    return successMsg.toString();
//                }

                if (StringUtils.isNull(equipment1)) {
                    //查重名称/编号
                    Collection<Object> valuesCheck = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
                    b:
                    for (Object j : valuesCheck) {
                        Equipment e = (Equipment) j;
                        if (e.getCode().equals(equipment.getCode())) {
                            failureNum++;
                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 设备编号重复：";
                            failureMsg.append(msg);
                            continue a;
                        }
                        if (e.getName().equals(equipment.getName())) {
                            failureNum++;
                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 设备名称重复：";
                            failureMsg.append(msg);
                            continue a;
                        }
                    }


                    //根据产品code获取产品ID
                    Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product).values();
                    e:
                    for (Object j : values) {
                        Product p = (Product) j;
                        if (p.getCode().equals(equipment.getProductCode())) {
                            equipment.setProductId(p.getId());
                            break e;
                        }
                    }
                    if (equipment.getProductId() == null) {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 产品编号有误：";
                        failureMsg.append(msg);
                        continue a;
                    }
                    if (!StringUtils.isEmpty(equipment.getParentCode())) {
                        //根据父设备code获取父ID
                        Collection<Object> values1 = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
                        f:
                        for (Object j : values1) {
                            Equipment eq = (Equipment) j;
                            if (eq.getCode().equals(equipment.getParentCode())) {
                                equipment.setpId(eq.getId());
                                if (equipment.getSonAddress() != null) {
                                    //查重地址
                                    for (Object obj : valuesCheck) {
                                        Equipment equ = (Equipment) obj;
                                        if (equ.getpId() != null && equ.getpId().equals(equipment.getpId()) && equ.getSonAddress() != null &&
                                                equ.getSonAddress().equals(equipment.getSonAddress())) {
                                            failureNum++;
                                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + "设备地址重复：";
                                            failureMsg.append(msg);
                                            continue a;
                                        }
                                    }
                                } else {
                                    failureNum++;
                                    String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + "设备地址为空：";
                                    failureMsg.append(msg);
                                    continue a;
                                }
                                break f;
                            }
                        }
                        if (equipment.getpId() == null) {
                            failureNum++;
                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 父设备编号有误：";
                            failureMsg.append(msg);
                            continue a;
                        }
                    }
                    equipment.setCreateName(operName);
                    equipment.setCreateTime(DateUtils.getNowDate());
                    boolean isADD = athenaBesEquipmentMapper.insertAthenaBesEquipment(equipment);
                    if (isADD) {
                        if (equipment.getpId() != null) {
                            Equipment parentEqu = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
                            Integer sonNum = parentEqu.getSonNum() + 1;
                            parentEqu.setSonNum(sonNum);
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId(), parentEqu);
                        }
                        //查询所有产品信息
                        List<Equipment> equipmentsRedis = athenaBesEquipmentMapper.selectAthenaBesEquipmentList(equipment);
                        equipment = equipmentsRedis.get(0);
                        //添加缓存
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getId(), equipment);
                        successNum++;
                        successMsg.append("<br/>" + successNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 导入成功");
                    } else {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 添加进数据库失败：";
                        failureMsg.append(msg);
                        continue a;
                    }
                } else if (isUpdateSupport) {
                    equipment.setId(equipment1.getId());
                    //查重名称/编号
                    Collection<Object> valuesCheck = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
                    c:
                    for (Object j : valuesCheck) {
                        Equipment e = (Equipment) j;
                        if (e.getCode().equals(equipment.getCode()) && !e.getId().equals(equipment.getId())) {
                            failureNum++;
                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 设备编号重复：";
                            failureMsg.append(msg);
                            continue a;
                        }
                        if (e.getName().equals(equipment.getName()) && !e.getId().equals(equipment.getId())) {
                            failureNum++;
                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 设备名称重复：";
                            failureMsg.append(msg);
                            continue a;
                        }
                    }

                    //根据产品code获取产品ID
                    Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product).values();
                    g:
                    for (Object j : values) {
                        Product p = (Product) j;
                        if (p.getCode().equals(equipment.getProductCode())) {
                            equipment.setProductId(p.getId());
                            break g;
                        }
                    }
                    if (equipment.getProductId() == null) {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 产品编号有误：";
                        failureMsg.append(msg);
                        continue a;
                    }
                    if (!StringUtils.isEmpty(equipment.getParentCode())) {
                        //根据父设备code获取父ID
                        Collection<Object> values1 = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
                        h:
                        for (Object j : values1) {
                            Equipment eq = (Equipment) j;
                            if (eq.getCode().equals(equipment.getParentCode())) {
                                equipment.setpId(eq.getId());
                                if (equipment.getSonAddress() != null) {
                                    //查重地址
                                    for (Object obj : valuesCheck) {
                                        Equipment equ = (Equipment) obj;
                                        if (equ.getpId() != null && equ.getpId().equals(equipment.getpId()) && equ.getSonAddress() != null &&
                                                equ.getSonAddress().equals(equipment.getSonAddress())) {
                                            failureNum++;
                                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + "设备地址重复：";
                                            failureMsg.append(msg);
                                            continue a;
                                        }
                                    }
                                } else {
                                    failureNum++;
                                    String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + "设备地址为空：";
                                    failureMsg.append(msg);
                                    continue a;
                                }
                                break h;
                            }
                        }
                        if (equipment.getpId() == null) {
                            failureNum++;
                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 父设备编号有误：";
                            failureMsg.append(msg);
                            continue a;
                        }
                    }

                    equipment.setUpdateName(operName);
                    equipment.setUpdateTime(DateUtils.getNowDate());
                    boolean isUpdate = athenaBesEquipmentMapper.updateAthenaBesEquipment(equipment);
                    if (isUpdate) {
                        //查询所有产品信息
                        List<Equipment> equipmentsRedis = athenaBesEquipmentMapper.selectAthenaBesEquipmentList(equipment);
                        equipment = equipmentsRedis.get(0);
                        //添加缓存
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getId(), equipment);
                        successNum++;
                        successMsg.append("<br/>" + successNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 更新成功");
                    } else {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 修改数据库失败：";
                        failureMsg.append(msg);
                        continue a;
                    }
                } else {
                    //查重名称/编号
                    Collection<Object> valuesCheck = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
                    d:
                    for (Object j : valuesCheck) {
                        Equipment e = (Equipment) j;
                        if (e.getCode().equals(equipment.getCode())) {
                            failureNum++;
                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 设备编号重复：";
                            failureMsg.append(msg);
                            continue a;
                        }
                        if (e.getName().equals(equipment.getName())) {
                            failureNum++;
                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 设备名称重复：";
                            failureMsg.append(msg);
                            continue a;
                        }
                    }


                    //根据产品code获取产品ID
                    Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product).values();
                    i:
                    for (Object j : values) {
                        Product p = (Product) j;
                        if (p.getCode().equals(equipment.getProductCode())) {
                            equipment.setProductId(p.getId());
                            break i;
                        }
                    }
                    if (equipment.getProductId() == null) {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 产品编号有误：";
                        failureMsg.append(msg);
                        continue a;
                    }
                    if (!StringUtils.isEmpty(equipment.getParentCode())) {
                        //根据父设备code获取父ID
                        Collection<Object> values1 = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment).values();
                        j:
                        for (Object j : values1) {
                            Equipment eq = (Equipment) j;
                            if (eq.getCode().equals(equipment.getParentCode())) {
                                equipment.setpId(eq.getId());
                                if (equipment.getSonAddress() != null) {
                                    //查重地址
                                    for (Object obj : valuesCheck) {
                                        Equipment equ = (Equipment) obj;
                                        if (equ.getpId() != null && equ.getpId().equals(equipment.getpId()) && equ.getSonAddress() != null &&
                                                equ.getSonAddress().equals(equipment.getSonAddress())) {
                                            failureNum++;
                                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + "设备地址重复：";
                                            failureMsg.append(msg);
                                            continue a;
                                        }
                                    }
                                } else {
                                    failureNum++;
                                    String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + "设备地址为空：";
                                    failureMsg.append(msg);
                                    continue a;
                                }
                                break j;
                            }
                        }
                        if (equipment.getpId() == null) {
                            failureNum++;
                            String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 父设备编号有误：";
                            failureMsg.append(msg);
                            continue a;
                        }
                    }
                    equipment.setCreateName(operName);
                    equipment.setCreateTime(DateUtils.getNowDate());
                    boolean isADD = athenaBesEquipmentMapper.insertAthenaBesEquipment(equipment);
                    if (isADD) {
                        if (equipment.getpId() != null) {
                            Equipment parentEqu = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId());
                            Integer sonNum = parentEqu.getSonNum() + 1;
                            parentEqu.setSonNum(sonNum);
                            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getpId(), parentEqu);
                        }
                        //查询所有产品信息
                        List<Equipment> equipmentsRedis = athenaBesEquipmentMapper.selectAthenaBesEquipmentList(equipment);
                        equipment = equipmentsRedis.get(0);
                        //添加缓存
                        redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, equipment.getId(), equipment);
                        successNum++;
                        successMsg.append("<br/>" + successNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 导入成功");
                    } else {
                        failureNum++;
                        String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 添加进数据库失败：";
                        failureMsg.append(msg);
                        continue a;
                    }
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、物联设备 " + equipment.getCode() + "/" + equipment.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                continue a;
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 新增历史数据
     */
    public boolean insertHistoryData(HistoryItemData historyItemData) {
        historyItemData.setCreateTime(DateUtils.getNowDate());
        boolean b = athenaBesEquipmentMapper.insertHistoryData(historyItemData);
        return b;
    }

    /**
     * @Description: 物联设备功能结构树
     * @auther: wanghongjie
     * @date: 15:52 2023/3/30
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult equipmentFunctionTree() {

        List<Equipment> equipmentList = new ArrayList<>();

        Map<String, Equipment> equipmentMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

        if (equipmentMap == null || equipmentMap.size() == 0) {
            return AjaxResult.error("无数据");
        }

        List<Map.Entry<String, Equipment>> entryList = new ArrayList<Map.Entry<String, Equipment>>(equipmentMap.entrySet());

        //排序
        Collections.sort(entryList, new Comparator<Map.Entry<String, Equipment>>() {
            public int compare(Map.Entry<String, Equipment> o1, Map.Entry<String, Equipment> o2) {
                return (Integer.valueOf(o1.getKey()) - Integer.valueOf(o2.getKey()));
//                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        Map<String, ProductFunction> productFunctionMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function);


        List<Map.Entry<String, ProductFunction>> ProductFunctionList = new ArrayList<Map.Entry<String, ProductFunction>>(productFunctionMap.entrySet());

        //排序
        Collections.sort(ProductFunctionList, new Comparator<Map.Entry<String, ProductFunction>>() {
            public int compare(Map.Entry<String, ProductFunction> o1, Map.Entry<String, ProductFunction> o2) {
                return (Integer.valueOf(o1.getKey()) - Integer.valueOf(o2.getKey()));
//                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        int i = 1;

        for (Map.Entry<String, Equipment> equipment : entryList) {

            equipment.getValue().setDoubleId((double) equipment.getValue().getId());
            equipmentList.add(equipment.getValue());

            Long productId = equipment.getValue().getProductId();//产品主键
            //根据产品主键查询产品信息
            Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, productId);
            String iotType = product.getIotType();
            switch (iotType) {
                case "0"://直连设备
                case "2"://2网关子设备
                    if (iotType.equals("0")) {
                        equipment.getValue().setpId(Long.valueOf("0"));
                        equipment.getValue().setDoubleId(Double.valueOf("0"));
                    }

                    //获取所有的自定义数据项别名
                    List<EquipmentItemData> equipmentItemDataList = new ArrayList<>();
                    equipmentItemDataList = athenaBesEquipmentMapper.selectEquipmentItemData();


                    for (Map.Entry<String, ProductFunction> productFunction : ProductFunctionList) {
                        Equipment equipments = new Equipment();
                        if (productFunction.getValue().getProductId().equals(productId)) {

//                            equipments.setId((long) (Integer.valueOf(entryList.get(entryList.size() - 1).getKey()) + i));
                            equipments.setDoubleId(((double) equipment.getValue().getId()
                                    + ((double) productFunction.getValue().getId() / Math.pow(10, (double) productFunction.getValue().getId().toString().length())))
                            );
                            equipments.setpId(equipment.getValue().getId());
                            equipments.setFunctionId(productFunction.getValue().getId());
                            equipments.setName(productFunction.getValue().getName());
                            equipments.setFunctionName(productFunction.getValue().getName());
                            equipments.setFunctionNum(productFunction.getValue().getFunctionNum());
                            equipments.setFunctionType(productFunction.getValue().getType());
                            equipments.setIssuedType(productFunction.getValue().getIssuedType());

                            //查询功能绑定的数据项读写类型`
                            ProductItemData productItemData =
                                    redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData, productFunction.getValue().getDataItem());

                            if (productItemData != null) {
                                //数据项下发功能
                                if (!StringUtils.isEmpty(productItemData.getReadType())) {
                                    equipments.setReadType(productItemData.getReadType());
                                }

                                if ((productItemData.getHighDetail() == null || productItemData.getHighDetail().size() == 0)
                                        && (productItemData.getLowDetail() == null || productItemData.getLowDetail().size() == 0)
                                        && (productItemData.getStructDetail() == null || productItemData.getStructDetail().size() == 0)) {
                                    equipments.setHaveParams("0");
                                } else {
                                    equipments.setHaveParams("1");
                                }

                                //给功能后面添加数据项自定义别名
                                if (equipmentItemDataList.size() != 0) {
                                    for (EquipmentItemData equipmentItemData : equipmentItemDataList) {
                                        Long equipmentId = equipmentItemData.getEquipmentId();//设备id
                                        Long itemDataId = equipmentItemData.getItemDataId();//数据项id
                                        if (equipment.getValue().getId().equals(equipmentId) && productItemData.getId().equals(itemDataId)) {
                                            equipments.setName(productFunction.getValue().getName() + "(" + equipmentItemData.getItemDataCustomName() + ")");
                                            break;
                                        }
                                    }
                                }
                            } else {
                                //指令下发
                                String instruct = productFunction.getValue().getInstruct();
                                //剔除错误信息
                                if (instruct == null || instruct.length() < 12) {
                                    equipments.setReadType("");
                                    equipments.setHaveParams("0");
                                    equipmentList.add(equipments);
                                    continue;
                                }

                                //读写功能码判断
                                //1.根据寄存器起始地址获取数据项
                                int itemDataIndex = Integer.parseInt(StringUtil.hexToDecimal(instruct.substring(3, 7)));
                                Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
                                a:
                                for (Object o : values) {
                                    ProductItemData productItemDataFor = (ProductItemData) o;
                                    if (productItemDataFor.getSortNum() != null && productItemDataFor.getSortNum().intValue() == itemDataIndex) {
                                        //设置读写类型为数据项读写类型
                                        equipments.setReadType(productItemDataFor.getReadType());
                                        break a;
                                    }
                                }

                                //未匹配到数据项
                                if (equipments.getReadType() == null) {
                                    equipments.setReadType("");
                                    equipments.setHaveParams("0");
                                    equipmentList.add(equipments);
                                    continue;
                                }

                                //是否是一个寄存器
                                int number = Integer.parseInt(StringUtil.hexToDecimal(instruct.substring(7, 11)));
                                if (number == 1) {
                                    equipments.setHaveParams("0");
                                } else {
                                    equipments.setHaveParams("1");
                                }
                            }

                            equipmentList.add(equipments);
                            i++;
                        }
                    }
                    break;
                case "1"://1网关设备
                    equipment.getValue().setpId(Long.valueOf("0"));
                    break;
                default:
                    break;
            }
        }
        return AjaxResult.success("获取成功", equipmentList);
    }

    //    @Override
//    public AjaxResult equipmentFunctionTreeByJob() {
//        Equipment equipment=new Equipment();
//        Map<String, Equipment> equipmentMaps = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);
//        Map<String, ProductFunction> functionMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function);
//
//
//        List<TreeSelect> treeSelectList=new ArrayList<>();
//        List<Park> allPark = powerDataMapper.getAllPark();
//
//        /**便利所有园区*/
//        equipmentMaps.values().stream()
//                .filter(item->item.getpId()==null)//过滤获得所有父设备
//                .filter(item-> StringUtils.isNotBlank(item.getParkCode()))//去除园区编号为空的集合
//                .filter(distinctByKey(item->item.getParkCode()))//过滤去重
//                .forEach(item->{
//                    /**type 0为园区*/
//                    for (Park park : allPark) {
//                        if(park.getCode().equals((item.getParkCode()))){
//                            treeSelectList.add(new TreeSelect(park.getCode(),park.getName(),"0",true));
//                        }
//                    }
//                });
//
//        treeSelectList.forEach(item->{
//            /**构建父级设备*/
//            List<TreeSelect> ftreeSelect=new ArrayList<>();
//            equipmentMaps.values().stream()
//                    .filter(equipmentitem->equipmentitem.getpId()==null)//过滤获得所有父设备
//                    .filter(equipmentitem-> StringUtils.isNotBlank(equipmentitem.getParkCode()))//去除园区编号为空的集合
//                    .filter(equipmentitem->equipmentitem.getParkCode().equals(item.getId())) //获得所有当前园区下的设备
//                    .forEach(equipmentitem->{
//                        Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, equipmentitem.getProductId());
//                        /**type 1为父级设备*/
//
//                        TreeSelect treeSelect = new TreeSelect(equipmentitem.getId(), equipmentitem.getName(), "1",true);
//                        switch (product.getIotType()) {
//                            case "0":
//                                //直连设备
//                                break;
//                            case "1":
//                                //网关设备
//                                /**如果是网关设备这继续查询网关子设备*/
//                                List<TreeSelect> ctreeSelect=new ArrayList<>();
//                                equipmentMaps.values().stream()
//                                        .filter(ChildItem-> ChildItem.getpId()!=null)
//                                        .filter(ChildItem-> ChildItem.getpId().equals(equipmentitem.getId()))
//                                        .forEach(ChildItem->{
//                                            /**查询出来网关子设备根据产品功能定义，构建设备功能*/
//                                            /**type 2为子级设备*/
//                                            TreeSelect childTree = new TreeSelect(ChildItem.getId(), ChildItem.getName(), "2",true);
//                                            List<TreeSelect> functiontreeList=new ArrayList<>();
//                                            functionMap.values().stream()
//                                                    .filter(function->function.getProductId().equals(ChildItem.getProductId()))
//                                                    .forEach(function->{
//                                                        /**type 3为产品功能
//                                                         * 插入产品功能信息*/
//                                                        functiontreeList.add(new TreeSelect(ChildItem.getId()+"_"+function.getId(),function.getName(),"3",false,equipmentitem.getId()));
//                                                    });
//                                            childTree.setChildren(functiontreeList);
//                                            ctreeSelect.add(childTree);
//                                            /**构建设备功能*/
//
//                                        });
//                                treeSelect.setChildren(ctreeSelect);
//                                break;
//                        }
//                        ftreeSelect.add(treeSelect);
//
//                    });
//            /**将设备信息填充至园区集合中*/
//            item.setChildren(ftreeSelect);
//
//        });
//        return AjaxResult.success(treeSelectList);
//    }
//
//

    /***
     * @description:同步能耗设备树
     * @author: sunshangeng
     * @date: 2023/4/23 17:03
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult equipmentFunctionTreeByJob() {
        Map<String, Equipment> equipmentMaps = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);
        Map<String, ProductFunction> functionMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_Function);
        List<Equipment> equipmentList = new ArrayList<>();


        List<TreeSelect> treeSelectList = new ArrayList<>();
        List<Park> allPark = powerDataMapper.getAllPark();

        /**获得所有园区*/
        equipmentMaps.values().stream()
                .filter(item -> item.getpId() == null)//过滤获得所有父设备
                .filter(item -> StringUtils.isNotBlank(item.getParkCode()))//去除园区编号为空的集合
                .filter(distinctByKey(item -> item.getParkCode()))//过滤去重
                .forEach(item -> {
                    /**type 0为园区*/
                    for (Park park : allPark) {
                        if (park.getCode().equals((item.getParkCode()))) {
                            treeSelectList.add(new TreeSelect(park.getCode(), park.getName(), true, 0));
                        }
                    }
                });
        /**组装设备和功能*/
        equipmentMaps.values().stream()
                .forEach(item -> {
                    Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, item.getProductId());
                    switch (product.getIotType()) {
                        case "0":
                            //直连设备 默认禁用点击 且将园区设置为父级
                            treeSelectList.add(new TreeSelect(item.getId(), item.getName(), true, item.getParkCode()));
                            break;
                        case "1":
                            //网关设备  默认禁用点击 且将园区设置为父级
                            treeSelectList.add(new TreeSelect(item.getId(), item.getName(), true, item.getParkCode()));
                            break;
                        case "2"://2网关子设备  默认禁用点击 且将网关设备设置为父级
                            treeSelectList.add(new TreeSelect(item.getId(), item.getName(), true, item.getpId()));
                            functionMap.values().stream()
                                    //获取所有对应的功能
                                    .filter(function -> function.getProductId().equals(item.getProductId()))
                                    .forEach(function -> {
                                        if (function.getType() != null && function.getType().equals("1")) {
                                            treeSelectList.add(new TreeSelect(item.getId() + "_" + function.getId(), function.getName(), false, item.getpId(), item.getId()));
                                            /**采集*/
                                            //将对应的功能插入 默认开启点击 且将网关子设备设置为父级 网关设备设置为控制器
//                                                treeSelectList.add(new TreeSelect(item.getId()+"_"+function.getId(),function.getName(),true,item.getpId(),item.getId()));

                                        }
                                    });

                            break;
                    }
                });


        return AjaxResult.success(treeSelectList);
    }

    /***
     * @description:用于支路查询电表信息。
     * @author: sunshangeng
     * @date: 2023/4/17 11:28
     * @param: [园区ID, 能耗类型]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult getEquipmentListByBranch(String parkCode, String energyCode) {

        List<Equipment> equipmentList = new ArrayList<>();
        /**获取所有的数据项*/
        Map<String, ProductItemData> ItemListMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);
        Map<String, Equipment> equipmentMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

        List<ProductItemData> productItemDataList = ItemListMap.values().stream()
                .filter(item -> StringUtils.isNotBlank(item.getEnergyCode())) //过滤掉能源类型为空的情况
                .filter(item -> item.getEnergyCode().equals(energyCode)) //过滤掉其他能源类型
                .filter(itemData -> "1".equals(itemData.getPreserveType()))//过滤掉不保存的数据
                .filter(distinctByKey(item -> item.getProductId()))//过滤去重 产品ID
                .collect(Collectors.toList());

        /**处理当前关联的所有设备*/
        productItemDataList.forEach(itemData -> {
            /**获取到所有复合条件的设备信息*/
            equipmentMap.values().stream()
                    .filter(equipment -> itemData.getProductId().equals(equipment.getProductId()))//过滤其他产品数据
                    .filter(equipment -> parkCode.equals(equipment.getParkCode()))//过滤其他园区数据
                    .forEach(equipment -> {
                        List<ProductItemData> list = ItemListMap.values().stream()
                                .filter(item -> StringUtils.isNotBlank(item.getEnergyCode())) //过滤掉能源类型为空的情况
                                .filter(item -> item.getProductId().equals(equipment.getProductId()))//获取获得相同产品ID的数据
                                .filter(item -> item.getEnergyCode().equals(energyCode)) //过滤掉其他能源类型
                                .collect(Collectors.toList());
                        /**将数据项存储在设备中*/
                        equipment.setItemDataList(list);
                        equipmentList.add(equipment);
                    });
            /**添加进设备集合*/
//            equipmentList.addAll(List);
        });

        return AjaxResult.success(equipmentList);
    }

    /**
     * @description:场景联动第三方设备树
     * @author: sunshangeng
     * @date: 2023/4/23 17:04
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult getEquipmentListByScene() {
        Map<String, Equipment> equipmentMaps = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);
        Map<String, ProductItemData> itemDataMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData);

        List<TreeSelect> treeSelectList = new ArrayList<>();
        List<Park> allPark = powerDataMapper.getAllPark();

        /**获得所有园区*/
        equipmentMaps.values().stream()
                .filter(item -> item.getpId() == null)//过滤获得所有父设备
                .filter(item -> StringUtils.isNotBlank(item.getParkCode()))//去除园区编号为空的集合
                .filter(distinctByKey(item -> item.getParkCode()))//过滤去重
                .forEach(item -> {
                    /**type 0为园区*/
                    for (Park park : allPark) {
                        if (park.getCode().equals((item.getParkCode()))) {
                            treeSelectList.add(new TreeSelect(park.getCode(), park.getName(), true, 0));
                        }
                    }
                });

        /**组装设备和功能*/
        equipmentMaps.values().stream()
                .forEach(item -> {
                    Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, item.getProductId());
                    switch (product.getIotType()) {
                        case "0":
                            //直连设备 默认禁用点击 且将园区设置为父级
                            treeSelectList.add(new TreeSelect(item.getId(), item.getName(), false, item.getParkCode()));
                            break;
                        case "1":
                            //网关设备  默认禁用点击 且将园区设置为父级
                            treeSelectList.add(new TreeSelect(item.getId(), item.getName(), false, item.getParkCode()));
                            break;
                        case "2"://2网关子设备  默认禁用点击 且将网关设备设置为父级
                            treeSelectList.add(new TreeSelect(item.getId(), item.getName(), true, item.getpId()));
                            itemDataMap.values().stream()
                                    //获取所有对应的功能
                                    .filter(itemData -> itemData.getProductId().equals(item.getProductId()))
                                    .forEach(itemData -> {
                                        treeSelectList.add(new TreeSelect(item.getId() + "_" + itemData.getId(), itemData.getName(), false, item.getpId(), item.getId()));
                                        /**采集*/
                                        //将对应的功能插入 默认开启点击 且将网关子设备设置为父级 网关设备设置为控制器
//                                                treeSelectList.add(new TreeSelect(item.getId()+"_"+function.getId(),function.getName(),true,item.getpId(),item.getId()));

                                    });
                            break;
                    }
                });


        return AjaxResult.success(treeSelectList);
    }


    /**
     * 给集合去重的方法
     */
    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        //putIfAbsent方法添加键值对，如果map集合中没有该key对应的值，则直接添加，并返回null，如果已经存在对应的值，则依旧为原来的值。
        //如果返回null表示添加数据成功(不重复)，不重复(null==null :TRUE)
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    public static void main(String[] args) {
        long a = (long) 10.11;
        System.out.println(a);
    }


    /**
     * @Description: 物联设备结构树
     * @auther: gaojikun
     * @date: 15:52 2023/3/30
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult equipmentTree() {

        List<Equipment> equipmentList = new ArrayList<>();

        Map<String, Equipment> equipmentMap = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment);

        if (equipmentMap == null || equipmentMap.size() == 0) {
            return AjaxResult.error("无数据");
        }

        List<Map.Entry<String, Equipment>> entryList = new ArrayList<Map.Entry<String, Equipment>>(equipmentMap.entrySet());

        //排序
        Collections.sort(entryList, new Comparator<Map.Entry<String, Equipment>>() {
            @Override
            public int compare(Map.Entry<String, Equipment> o1, Map.Entry<String, Equipment> o2) {
                return (Integer.valueOf(o1.getKey()) - Integer.valueOf(o2.getKey()));
            }
        });

        for (Map.Entry<String, Equipment> equipment : entryList) {
            Equipment equipment1 = equipment.getValue();
            if (equipment1.getpId() == null) {
                equipment1.setpId(0L);
            }
            equipmentList.add(equipment1);
        }
        equipmentList.add(new Equipment(0L, "第三方设备树", null));
        return AjaxResult.success("获取成功", equipmentList);
    }

    /**
     * @description:获取数据项
     * @author: gaojikun
     * @param: [设备id]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    @Override
    public List<ProductItemData> getElectricParamsDatalistOther(Long id) {
        List<ProductItemData> returnList = new ArrayList<>();
        Equipment equipment = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Equipment, id);
        Product product = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product, equipment.getProductId());
        Collection<Object> values = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_Product_ItemData).values();
        for (Object o : values) {
            ProductItemData p = (ProductItemData) o;
            if (p.getProductId().equals(product.getId())) {
                //有重命名的数据项获取重命名
                Map<String, String> nameMap = athenaBesEquipmentMapper.selectNameByEquipment(id, p.getId());
                if (nameMap == null || nameMap.size() == 0) {
                    returnList.add(p);
                } else {
                    p.setName(nameMap.get("name"));
                    returnList.add(p);
                }
            }
        }

        return returnList;
    }
}
