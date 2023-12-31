package com.zc.efounder.JEnterprise.Cache;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.zc.common.constant.DeviceTreeConstants;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.commhandler.ClientEventCallback;
import com.zc.efounder.JEnterprise.domain.deviceTree.AthenaElectricMeter;
import com.zc.efounder.JEnterprise.domain.deviceTree.Controller;
import com.zc.efounder.JEnterprise.domain.deviceTree.DeviceTree;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceLink;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSeason;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceSetting;
import com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceTime;
import com.zc.efounder.JEnterprise.mapper.deviceTree.AthenaElectricMeterMapper;
import com.zc.efounder.JEnterprise.mapper.systemSetting.ElectricityPriceSeasonMapper;
import com.zc.efounder.JEnterprise.mapper.systemSetting.TimeOfUsePriceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liuwenge
 * @Description 分时电价缓存方法
 * @date 2023/2/23 10:29
 */
@Component
public class ElectricityPriceLinkCache {
    @Resource
    private RedisCache redisCache;
    @Resource
    private TimeOfUsePriceMapper timeOfUsePriceMapper;


    /**
     * @param seasonId
     * @param timeId
     * @return com.zc.efounder.JEnterprise.domain.systemSetting.ElectricityPriceLink
     * @Description 根据季节id和时间id获取分时电价
     * @author liuwenge
     * @date 2023/2/23 10:43
     */
    public ElectricityPriceLink getCacheBySeasonIdAndTimeId(String seasonId, String timeId) {
        if (seasonId == null || "".equals(seasonId) || timeId == null || "".equals(timeId)) {
            return null;
        }

        Map<String, ElectricityPriceLink> electricityPriceLinkList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceLink);
        if (electricityPriceLinkList == null || electricityPriceLinkList.size() == 0) {
            return null;
        }

        for (ElectricityPriceLink electricityPriceLink : electricityPriceLinkList.values()) {

            if (seasonId.equals(electricityPriceLink.getSeasonId()) && timeId.equals(electricityPriceLink.getTimeId())) {
                return electricityPriceLink;
            }
        }

        return null;
    }

    /**
     * @param dateTime
     * @return ElectricityPriceSeason
     * @Description 根据月份获取季节id
     * @author gaojikun
     * @date 2023/2/23 10:43
     */
    public String getSeasonIdCache(String dateTime) {

        if (StringUtils.isEmpty(dateTime)) {
            return null;
        }

        Map<String, ElectricityPriceSeason> electricityPriceSeasonList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceSeason);
        if (electricityPriceSeasonList == null || electricityPriceSeasonList.size() == 0) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //当前时间
            Date time = simpleDateFormat.parse(dateTime);
            Calendar date = Calendar.getInstance();
            date.setTime(time);
            //当前年份,方便后面组装
            String nowYear = String.valueOf(date.getWeekYear());

            for (ElectricityPriceSeason electricityPriceSeason : electricityPriceSeasonList.values()) {
                //组装成当前年份的日期范围
                String startDate = electricityPriceSeason.getStartDate().substring(5, 10);
                Calendar begin = Calendar.getInstance();
                begin.setTime(simpleDateFormat.parse(nowYear + "-" + startDate + " 00:00:00"));

                String endDate = electricityPriceSeason.getEndDate().substring(5, 10);
                Calendar end = Calendar.getInstance();
                end.setTime(simpleDateFormat.parse(nowYear + "-" + endDate + " 23:59:59"));

                //如果在范围内则返回该季节
                if (date.getTime().equals(begin.getTime())
                        || date.getTime().equals(end.getTime())
                        || (date.before(end) && date.after(begin))) {
                    return electricityPriceSeason.getId().toString();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    /**
     * @param hourInt
     * @return ElectricityPriceTime
     * @Description 根据月份获取时间id
     * @author gaojikun
     * @date 2023/2/23 10:43
     */
    public String getTimeIdCache(Integer hourInt) {
        if (hourInt == null) {
            return null;
        }

        Map<String, ElectricityPriceTime> electricityPriceTimeList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPriceTime);
        if (electricityPriceTimeList == null || electricityPriceTimeList.size() == 0) {
            return null;
        }

        for (ElectricityPriceTime electricityPriceTime : electricityPriceTimeList.values()) {

            Integer hourDataInt = Integer.parseInt(electricityPriceTime.getName().substring(0, 2));
            if (hourDataInt.equals(hourInt)) {
                return electricityPriceTime.getId().toString();
            }
        }

        return null;
    }

    /**
     * @param priceType
     * @param yearMonth
     * @return ElectricityPriceSetting
     * @Description 根据字段名和时间获取当前电价
     * @author gaojikun
     * @date 2023/2/23 10:43
     */
    public BigDecimal getElectricityPrice(String priceType, String yearMonth) {

        BigDecimal returnValue = new BigDecimal("0");

        if (StringUtils.isEmpty(priceType) || StringUtils.isEmpty(yearMonth)) {
            return null;
        }

        Map<String, ElectricityPriceSetting> electricityPriceSettingList = redisCache.getCacheMapValue(RedisKeyConstants.BES_BasicData_SystemSetting_ElectricityPrice);
        if (electricityPriceSettingList == null || electricityPriceSettingList.size() == 0) {
            return null;
        }

        int forNum = 0;
        for (ElectricityPriceSetting electricityPriceSetting : electricityPriceSettingList.values()) {
            forNum++;
            Date month = electricityPriceSetting.getMonthDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String monthStr = formatter.format(month);
            try {
                if (yearMonth.equals(monthStr.substring(0, 7))) {
                    returnValue = getElectricityPriceValue(priceType, electricityPriceSetting);
                    return returnValue;
                }
                //如果没有对应的月份则用最后一条月份数据
                if (forNum == electricityPriceSettingList.values().size()) {
                    returnValue = getElectricityPriceValue(priceType, electricityPriceSetting);
                    return returnValue;
                }
            } catch (Exception e) {
                System.out.println("根据字段名和时间获取当前电价，出现异常_______>" + e);
                return returnValue;
            }
        }
        return returnValue;
    }

    private BigDecimal getElectricityPriceValue(String priceType, ElectricityPriceSetting electricityPriceSetting) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String name = priceType.substring(0, 1).toUpperCase() + priceType.substring(1);
        Method method = electricityPriceSetting.getClass().getMethod("get" + name);
        BigDecimal returnVlaue = (BigDecimal) method.invoke(electricityPriceSetting);
        if (returnVlaue == null) {
            return new BigDecimal("0");
        } else {
            return returnVlaue;
        }
    }


}
