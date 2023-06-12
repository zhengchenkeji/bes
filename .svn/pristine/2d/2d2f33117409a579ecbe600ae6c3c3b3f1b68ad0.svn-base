package com.zc.efounder.JEnterprise.service.safetyWarning.impl;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmTacticsAlarmNotifierLinkMapper;
import com.zc.efounder.JEnterprise.mapper.safetyWarning.AlarmNotifierMapper;
import com.zc.efounder.JEnterprise.service.safetyWarning.AlarmNotifierService;
import org.springframework.stereotype.Service;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 告警接收组Service业务层处理
 *
 * @author sunshangeng
 * @date 2022-09-15
 */
@Service
public class AlarmNotifierServiceImpl implements AlarmNotifierService
{
    @Resource
    private AlarmNotifierMapper alarmNotifierMapper;

    @Resource
    private AlarmTacticsAlarmNotifierLinkMapper tacticsAlarmNotifierLinkMapper;
    /**
     * 查询告警接收组
     *sunshangeng
     * @param id 告警接收组主键
     * @return 告警接收组
     */
    @Override
    public AlarmNotifier selectAlarmNotifierById(Long id)
    {
        return alarmNotifierMapper.selectAlarmNotifierById(id);
    }

   /**
    * @description:查询告警接收组列表
    * @author: sunshangeng
    * @date: 2022/9/22 17:32
    * @param: [alarmNotifier]
    * @return: java.util.List<com.ruoyi.safetyWarning.alarmNotifier.domain.AlarmNotifier>
    **/
    @Override
    public List<AlarmNotifier> selectAlarmNotifierList(AlarmNotifier alarmNotifier)
    {
        if(alarmNotifier.getIslink()!=null){
            if(alarmNotifier.getIslink()==0){
                /**查询未关联 告警策略的告警接收组*/
                return alarmNotifierMapper.selectTacticsNotifierNotLinkList(alarmNotifier);
            }
            /**查询已关联 告警策略的告警接收组*/
            return alarmNotifierMapper.selectTacticsNotifierLinkList(alarmNotifier);
        }
        /**查询所有告警接收组*/
        return alarmNotifierMapper.selectAlarmNotifierList(alarmNotifier);

    }

    /**
     * 新增告警接收组
     *sunshangeng
     * @param alarmNotifier 告警接收组
     * @return 结果
     */
    @Override
    public AjaxResult insertAlarmNotifier(AlarmNotifier alarmNotifier)
    {
        String name = alarmNotifier.getName();
        String deptId = alarmNotifier.getDeptId();
        String userId = alarmNotifier.getUserId();
        if (!StringUtils.hasText(name) || !StringUtils.hasText(deptId) || !StringUtils.hasText(userId)){
            return AjaxResult.error("参数错误,新增失败");
        }

        alarmNotifier.setCreateTime(DateUtils.getNowDate());

        int row = alarmNotifierMapper.insertAlarmNotifier(alarmNotifier);
        if (row < 1){
            return AjaxResult.error("新增失败");
        }
        return AjaxResult.success("新增成功");
    }

    /**
     * 修改告警接收组
     *sunshangeng
     * @param alarmNotifier 告警接收组
     * @return 结果
     */
    @Override
    public AjaxResult updateAlarmNotifier(AlarmNotifier alarmNotifier)
    {
        String id = alarmNotifier.getId().toString();
        String name = alarmNotifier.getName();
        String deptId = alarmNotifier.getDeptId();
        String userId = alarmNotifier.getUserId();

        if (!StringUtils.hasText(id)
                || !StringUtils.hasText(name)
                || !StringUtils.hasText(deptId)
                || !StringUtils.hasText(userId)){
            return AjaxResult.error("参数错误,修改失败");
        }
        alarmNotifier.setUpdateTime(DateUtils.getNowDate());
        int row = alarmNotifierMapper.updateAlarmNotifier(alarmNotifier);
        if (row < 1){
            return AjaxResult.error("修改失败");
        }
        return AjaxResult.success("修改成功");
    }

    /**
     * @description:批量删除告警接收组 并删除关联关系
     * @author: sunshangeng
     * @date: 2022/9/22 17:56
     * @param: [ids]
     * @return: int
     **/
    @Override
    @Transactional
    public int deleteAlarmNotifierByIds(Long[] ids)
    {
        if(ids==null|| ids.length==0){
            return  0;
        }
        tacticsAlarmNotifierLinkMapper.deleteByAlarmNotifierIdsBoolean(ids);
        return alarmNotifierMapper.deleteAlarmNotifierByIds(ids);
    }
    /**
     * @description:删除告警接收组信息并删除关联关系
     * @author: sunshangeng
     * @date: 2022/9/22 17:55
     * @param: [id]
     * @return: int
     **/
    @Override
    @Transactional
    public int deleteAlarmNotifierById(Long id)
    {
        Long[] ids={id};
        tacticsAlarmNotifierLinkMapper.deleteByAlarmNotifierIdsBoolean(ids);
        return alarmNotifierMapper.deleteAlarmNotifierById(id);
    }

    /**
     * @Description 获取部门下的用户
     *
     * @author liuwenge
     * @date 2023/2/28 17:09
     * @param deptId
     * @return java.util.List<com.ruoyi.common.core.domain.entity.SysUser>
     */
    @Override
    public List<SysUser> getUserList(String deptId){
        return alarmNotifierMapper.getUserList(deptId);
    }
}
