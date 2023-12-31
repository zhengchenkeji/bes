package com.zc.efounder.JEnterprise.service.energyCollection.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.zc.efounder.JEnterprise.domain.energyCollection.MeterType;
import com.zc.efounder.JEnterprise.mapper.energyCollection.MeterTypeMapper;
import com.zc.efounder.JEnterprise.service.energyCollection.MeterTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 电表类型定义Service业务层处理
 *
 * @author ruoyi
 * @date 2022-09-07
 */
@Service
public class MeterTypeServiceImpl implements MeterTypeService {
    @Autowired
    private MeterTypeMapper meterTypeMapper;

    /**
     * 查询电表类型定义
     *
     * @param id 电表类型定义主键
     * @return 电表类型定义
     */
    @Override
    public MeterType selectMeterTypeById(Long id) {
        return meterTypeMapper.selectMeterTypeById(id);
    }

    /**
     * 查询电表类型定义列表
     *
     * @param meterType 电表类型定义
     * @return 电表类型定义
     */
    @Override
    public List<MeterType> selectMeterTypeList(MeterType meterType) {
        return meterTypeMapper.selectMeterTypeList(meterType);
    }

    /*
     * 导入电表类型定义
     * */
    @Override
    public String importMeterType(List<MeterType> meterTypeList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(meterTypeList) || meterTypeList.size() == 0) {
            throw new ServiceException("导入电表类型数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (MeterType meterType : meterTypeList) {
            try {
                // 验证是否存在这个电表类型
                MeterType meterType1 = meterTypeMapper.getInfoByCode(meterType.getCode());
                //查重处理
                List<MeterType> meterTypes = meterTypeMapper.selectMeterTypeByMeterType(meterType);
                if(meterTypes.size()>0){
                    successMsg.append("<br/>" + successNum + "、电表类型 " + meterType.getCode() + "/" + meterType.getName() + " 导入失败，编号/名称重复");
                    return successMsg.toString();
                }
                if (StringUtils.isNull(meterType1)) {
                    meterType.setCreateBy(operName);
                    this.insertMeterType(meterType);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、电表类型 " + meterType.getCode() + "/" + meterType.getName() + " 导入成功");
                } else if (isUpdateSupport) {
                    meterType.setUpdateBy(operName);
                    meterType.setId(meterType1.getId());
                    this.updateMeterType(meterType);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、电表类型 " + meterType.getCode() + "/" + meterType.getName() + " 更新成功");
                } else {
                    meterType.setCreateBy(operName);
                    this.insertMeterType(meterType);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、电表类型 " + meterType.getCode() + "/" + meterType.getName() + " 导入成功");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、电表类型 " + meterType.getCode() + "/" + meterType.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
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
     * 新增电表类型定义
     *
     * @param meterType 电表类型定义
     * @return 结果
     */
    @Override
    public int insertMeterType(MeterType meterType) {
        meterType.setCreateTime(DateUtils.getNowDate());
        return meterTypeMapper.insertMeterType(meterType);
    }

    /**
     * 修改电表类型定义
     *
     * @param meterType 电表类型定义
     * @return 结果
     */
    @Override
    public int updateMeterType(MeterType meterType) {
        meterType.setUpdateTime(DateUtils.getNowDate());
        return meterTypeMapper.updateMeterType(meterType);
    }

    /**
     * 批量删除电表类型定义
     *
     * @param ids 需要删除的电表类型定义主键
     * @return 结果
     */
    @Override
    public int deleteMeterTypeByIds(Long[] ids) {
        return meterTypeMapper.deleteMeterTypeByIds(ids);
    }

    /**
     * 删除电表类型定义信息
     *
     * @param id 电表类型定义主键
     * @return 结果
     */
    @Override
    public int deleteMeterTypeById(Long id) {
        return meterTypeMapper.deleteMeterTypeById(id);
    }

}
