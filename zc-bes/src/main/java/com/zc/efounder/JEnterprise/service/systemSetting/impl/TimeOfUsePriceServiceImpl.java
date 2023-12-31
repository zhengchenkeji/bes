package com.zc.efounder.JEnterprise.service.systemSetting.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceLink;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceTime;
import com.zc.efounder.JEnterprise.domain.systemSetting.TimeOfUsePrice;
import com.zc.efounder.JEnterprise.mapper.systemSetting.TimeOfUsePriceMapper;
import com.zc.efounder.JEnterprise.service.systemSetting.TimeOfUsePriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分时电价Service业务层处理
 *
 * @author liuwenge
 * @date 2023-02-20
 */
@Service
public class TimeOfUsePriceServiceImpl implements TimeOfUsePriceService
{
    @Autowired
    private TimeOfUsePriceMapper timeOfUsePriceMapper;

    @Resource
    private RedisCache redisCache;

    @PostConstruct
    public void init() {
        /**
         * 添加数据到 redis 缓存
         */
        addCache();
    }

    /**
     * 添加季节缓存
     */
    public void addCache() {
        // 获取全部数据
        List<ElectricityPriceLink> electricityPriceLinkList = timeOfUsePriceMapper.queryElectricityPriceLink();
        // 清除 redis 缓存数据
        redisCache.deleteObject(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceLink);

        if (electricityPriceLinkList == null || electricityPriceLinkList.isEmpty()) {
            return;
        }

        // 添加 redis 缓存数据
        electricityPriceLinkList.forEach(val -> {
            redisCache.setCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceLink, val.getId(), val);
        });
    }

    /**
     * 查询分时电价列表
     *
     * @author liuwenge
     * @return 分时电价列表
     */
    @Override
    public AjaxResult selectList(){
        Map<String,Object> result = new HashMap<>();

        List<Map<String,String>> columnList = new ArrayList<>();
        List<Map<String,Object>> dataList = new ArrayList<>();

        //查询季节列表,组成表头
        List<ElectricityPriceSeason> seasonList = timeOfUsePriceMapper.querySeasonList();
        if (seasonList.size() > 0){
            seasonList.forEach(item ->{
                Map<String,String> columnItem = new HashMap<>();
                columnItem.put("code",item.getId().toString());
                columnItem.put("name",item.getName());
                columnList.add(columnItem);
            });
        }

        //查询时间列表,组装行数据
        List<ElectricityPriceTime> timeList = timeOfUsePriceMapper.queryTimeList();
        if (timeList.size() > 0){
            timeList.forEach(item ->{
                Map<String,Object> rowItem = new HashMap<>();
                rowItem.put("time",item.getName());
                rowItem.put("timeId",item.getId().toString());
                rowItem.put("isEdit",false);

                for (Map<String, String> column : columnList) {
                    rowItem.put(column.get("code"),"");
                }

                dataList.add(rowItem);
            });
        }

        //查询所有配置,完善行数据
        List<ElectricityPriceLink> electricityPriceLinkList = timeOfUsePriceMapper.queryElectricityPriceLink();
        if (electricityPriceLinkList.size() > 0){
            electricityPriceLinkList.forEach(item ->{
                String seasonId = item.getSeasonId();
                String timeId = item.getTimeId();
                String priceType = item.getPriceType();

                for (int i = 0;i < dataList.size(); i++){
                    if (dataList.get(i).get("timeId").equals(timeId)){
                        dataList.get(i).put(seasonId,priceType);
                        return;
                    }
                }
            });
        }

        result.put("columnList",columnList);
        result.put("dataList",dataList);

        return AjaxResult.success("",result);
    }

    /**
     * @Description 修改分时电价
     *
     * @author liuwenge
     * @date 2023/2/21 15:48
     * @param timeOfUsePrice
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @Override
    public AjaxResult update(TimeOfUsePrice timeOfUsePrice){

        List<Map<String,String>> columnList = timeOfUsePrice.getColumn();
        List<Map<String,String>> dataList = timeOfUsePrice.getDataList();
        if (columnList.size() < 1 || dataList.size() < 1){
            return AjaxResult.error("参数错误");
        }

        //先清空之前的
        List<ElectricityPriceLink> electricityPriceLinkOld = timeOfUsePriceMapper.queryElectricityPriceLink();
        if (electricityPriceLinkOld.size() > 0){
            boolean flag = timeOfUsePriceMapper.delete(electricityPriceLinkOld);
            if (!flag){
                return AjaxResult.error("修改失败");
            }
        }


        //组装数据,重新添加
        List<ElectricityPriceLink> electricityPriceLinkList = new ArrayList<>();
        for (Map<String, String> data : dataList) {
            for (Map<String,String> column : columnList){
                ElectricityPriceLink electricityPriceLink = new ElectricityPriceLink();
                electricityPriceLink.setSeasonId(column.get("code"));
                electricityPriceLink.setSeasonName(column.get("name"));
                electricityPriceLink.setTimeId(data.get("timeId"));
                electricityPriceLink.setTimeName(data.get("time"));
                electricityPriceLink.setPriceType(data.get(column.get("code")));

                electricityPriceLinkList.add(electricityPriceLink);
            }
        }

        if (electricityPriceLinkList.size() > 0){
            timeOfUsePriceMapper.insertByList(electricityPriceLinkList);

            //查询添加缓存
            this.addCache();

        } else {
            return AjaxResult.success("修改失败");
        }

        return AjaxResult.success("修改成功");
    }

    /**
     * @Description 导出分时电价
     *
     * @author liuwenge
     * @date 2023/2/21 15:48
     * @param
     * @return java.util.Map<java.lang.String,java.util.List<java.util.Map<java.lang.String,java.lang.Object>>>
     */
    public Map<String,List<Map<String,Object>>> exportTable(){

        Map<String,List<Map<String,Object>>> result = new HashMap<>();
        List<Map<String,Object>> columnList = new ArrayList<>();
        List<Map<String,Object>> dataList = new ArrayList<>();

        //查询季节列表,组成表头
        List<ElectricityPriceSeason> seasonList = timeOfUsePriceMapper.querySeasonList();
        if (seasonList.size() > 0){
            seasonList.forEach(item ->{
                Map<String,Object> columnItem = new HashMap<>();
                columnItem.put("code",item.getId().toString());
                columnItem.put("name",item.getName());
                columnList.add(columnItem);
            });
        }

        //查询时间列表,组装行数据
        List<ElectricityPriceTime> timeList = timeOfUsePriceMapper.queryTimeList();
        if (timeList.size() > 0){
            timeList.forEach(item ->{
                Map<String,Object> rowItem = new HashMap<>();
                rowItem.put("time",item.getName());
                rowItem.put("timeId",item.getId().toString());
                rowItem.put("isEdit",false);

                for (Map<String, Object> column : columnList) {
                    rowItem.put(column.get("code").toString(),"");
                }

                dataList.add(rowItem);
            });
        }

        //查询所有配置,完善行数据
        List<ElectricityPriceLink> electricityPriceLinkList = timeOfUsePriceMapper.queryExportData();
        if (electricityPriceLinkList.size() > 0){
            electricityPriceLinkList.forEach(item ->{
                String seasonId = item.getSeasonId();
                String timeId = item.getTimeId();
                String priceType = item.getPriceTypeName();

                for (int i = 0;i < dataList.size(); i++){
                    if (dataList.get(i).get("timeId").equals(timeId)){
                        dataList.get(i).put(seasonId,priceType);
                        return;
                    }
                }
            });
        }

        result.put("columnList",columnList);
        result.put("dataList",dataList);

        return result;

    }


}
