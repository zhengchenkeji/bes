package com.zc.efounder.JEnterprise.mapper.noticeManage;



import com.ruoyi.common.core.domain.entity.SysUser;
import com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeConfig;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知配置Mapper接口
 *
 * @author ruoyi
 * @date 2023-02-08
 */
public interface NoticeConfigMapper
{
    /**
     * 查询通知配置
     *
     * @param id 通知配置主键
     * @return 通知配置
     */
    public NoticeConfig selectNoticeConfigById(Long id);

    /**
     * 查询通知配置列表
     *
     * @param NoticeConfig 通知配置
     * @return 通知配置集合
     */
    List<NoticeConfig> selectNoticeConfigList(NoticeConfig NoticeConfig);

    /**
     * 新增通知配置
     *
     * @param NoticeConfig 通知配置
     * @return 结果
     */
    int insertNoticeConfig(NoticeConfig NoticeConfig);

    /**
     * 修改通知配置
     *
     * @param NoticeConfig 通知配置
     * @return 结果
     */
    int updateNoticeConfig(NoticeConfig NoticeConfig);

    /**
     * 删除通知配置
     *
     * @param id 通知配置主键
     * @return 结果
     */
    int deleteNoticeConfigById(Long id);

    /**
     * 批量删除通知配置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteNoticeConfigByIds(Long[] ids);

    List<NoticeConfig> getNoticeConfigbyTemplate(@Param("serviceFactory") String serviceFactory, @Param("noticeType") String NoticeType);


    /**
     * @description:
     * @author: sunshangeng
     * @date: 2023/3/3 15:22
     * @param: [noticeType]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity>
     **/
    List<DicDataEntity> getNoticeConfigList(@Param("noticeType")String noticeType);


    /**
     * @description:根据用户id查询用户信息
     * @author: sunshangeng
     * @date: 2023/3/6 15:34
     * @param: [userId]
     * @return: com.ruoyi.common.core.domain.entity.SysUser
     **/
    SysUser selectUserById(@Param("userId") Long userId);


}
