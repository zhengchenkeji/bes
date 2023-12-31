package com.zc.efounder.JEnterprise.service.energyInfo.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.zc.common.constant.RedisKeyConstants;
import com.zc.efounder.JEnterprise.domain.baseData.Equipment;
import com.zc.efounder.JEnterprise.domain.energyCollection.CollMethod;
import com.zc.efounder.JEnterprise.domain.energyCollection.ElectricParams;
import com.zc.efounder.JEnterprise.domain.energyInfo.Park;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmWorkOrder;
import com.zc.efounder.JEnterprise.mapper.energyCollection.CollMethodMapper;
import com.zc.efounder.JEnterprise.mapper.energyCollection.ElectricParamsMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.EnergyTypeMapper;
import com.zc.efounder.JEnterprise.mapper.energyInfo.ParkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmWorkOrderMapper;
import com.zc.efounder.JEnterprise.service.energyInfo.IParkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.ruoyi.common.core.domain.AjaxResult.error;
import static com.ruoyi.common.core.domain.AjaxResult.success;

/**
 * 园区Service业务层处理
 *
 * @author ruoyi
 * @date 2022-09-08
 */
@Service
public class ParkServiceImpl /*extends AssociatedService*/ implements IParkService/*, ForeignKeyService*/ {
    @Resource
    private RedisCache redisCache;
    @Autowired
    private ParkMapper parkMapper;
    @Autowired
    private EnergyTypeMapper energyTypeMapper;
    @Autowired
    private ElectricParamsMapper electricParamsMapper;
    @Autowired
    private CollMethodMapper collMethodMapper;
    @Autowired
    private AlarmWorkOrderMapper alarmWorkOrderMapper;

    /**
     * 查询园区
     *
     * @param code 园区主键
     * @return 园区
     */
    @Override
    public Park selectParkByCode(String code) {
        return parkMapper.selectParkByCode(code);
    }

    /**
     * 查询园区列表
     *
     * @param park 园区
     * @return 园区
     */
    @Override
    public List<Park> selectParkList(Park park) {
        return parkMapper.selectParkList(park);
    }

    /**
     * 新增园区
     *
     * @param park 园区
     * @return 结果
     */
    @Override
    public int insertPark(Park park) {
        park.setCreateTime(DateUtils.getNowDate());
        DecimalFormat df = new DecimalFormat("0000");
        List<Park> parklist = parkMapper.findAllPark();
        int parkLen = parklist.size();
        if (parkLen == 0) {
            park.setCode("0000");
        } else {
            String maxBmbh = parkMapper.findMaxYqbh();
            Integer cuBESParkbh = (Integer.parseInt(maxBmbh) + 1);
            String currentBESParkbh = df.format(cuBESParkbh);
            park.setCode(currentBESParkbh);
        }
        return parkMapper.insertPark(park);
    }

    /**
     * 修改园区
     *
     * @param park 园区
     * @return 结果
     */
    @Override
    public AjaxResult updatePark(Park park) {
        if (StringUtils.isEmpty(park.getCode())) {
            return AjaxResult.error("园区编号不能为空！");
        }
        park.setUpdateTime(DateUtils.getNowDate());
        //修改告警工单 - 设备离线报警（无策略ID） - 园区code - 设备ID - 修改userId
        List<Long> equipmentIds = new ArrayList<>();
        Collection<Object> values = redisCache.getCacheMap(RedisKeyConstants.BES_BasicData_Equipment).values();
        for (Object object : values) {
            Equipment equipment = (Equipment) object;
            if (equipment.getParkCode() != null && equipment.getParkCode().equals(park.getCode())) {
                equipmentIds.add(equipment.getId());
            }
        }

        AlarmWorkOrder alarmWorkOrder = new AlarmWorkOrder();
        if (equipmentIds.size() > 0) {
            alarmWorkOrder.setEquipmentIds(equipmentIds);
            if (!"1".equals(park.getUserName())) {
                alarmWorkOrder.setUserId("1," + park.getUserName());
            } else {
                alarmWorkOrder.setUserId(park.getUserName());
            }
            alarmWorkOrderMapper.updateWorkOrderUserId(alarmWorkOrder);
        }


        parkMapper.updatePark(park);
        return AjaxResult.success("修改成功！");
    }

    /**
     * 批量删除园区
     *
     * @param codes 需要删除的园区主键
     * @return 结果
     */
    @Override
    public AjaxResult deleteParkByCodes(String[] codes) {
        if (StringUtils.isEmpty(codes)) {
            return error("参数不能为空!");
        }
        for (String code : codes) {
            //具有外键所关联的信息，无法删除息
            List<Map<String, Object>> enerys = energyTypeMapper.selectEnergyConfigListByCode(code, "");
            if (enerys.size() > 0) {
                return error("园区已被园区能耗配置关联，请先删除相关信息");
            }
            ElectricParams electricParam = new ElectricParams();
            electricParam.setParkCode(code);
            List<ElectricParams> electricParams = electricParamsMapper.selectElectricParamsList(electricParam);
            if (electricParams.size() > 0) {
                return error("园区已被采集参数定义关联，请先删除相关信息");
            }
            CollMethod collMethod = new CollMethod();
            collMethod.setParkCode(code);
            List<CollMethod> collMethods = collMethodMapper.selectCollMethodList(collMethod);
            if (collMethods.size() > 0) {
                return error("园区已被采集方案定义关联，请先删除相关信息");
            }
        }
        boolean isDeleteParkByCodes = parkMapper.deleteParkByCodes(codes);
        if (isDeleteParkByCodes) {
            return success("删除成功");
        }
        return error("删除失败");
    }

}
