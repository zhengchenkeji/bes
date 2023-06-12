package com.zc.efounder.JEnterprise.mapper.noticeManage;

import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知模板配置Mapper接口
 *
 * @author sunshangeng
 * @date 2023-02-09
 */
public interface NoticeTemplateMapper
{
    /**
     * 查询通知模板配置
     *
     * @param id 通知模板配置主键
     * @return 通知模板配置
     */
    public NoticeTemplate selectNoticeTemplateById(Long id);

    /**
     * 查询通知模板配置列表
     *
     * @param noticeTemplate 通知模板配置
     * @return 通知模板配置集合
     */
    List<NoticeTemplate> selectNoticeTemplateList(NoticeTemplate noticeTemplate);

    /**
     * 新增通知模板配置
     *
     * @param noticeTemplate 通知模板配置
     * @return 结果
     */
    int insertNoticeTemplate(NoticeTemplate noticeTemplate);

    /**
     * 修改通知模板配置
     *
     * @param noticeTemplate 通知模板配置
     * @return 结果
     */
    int updateNoticeTemplate(NoticeTemplate noticeTemplate);

    /**
     * 删除通知模板配置
     *
     * @param id 通知模板配置主键
     * @return 结果
     */
    int deleteNoticeTemplateById(Long id);

    /**
     * 批量删除通知模板配置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteNoticeTemplateByIds(Long[] ids);


    List<NoticeTemplate> getNoticeTemplatebyConfig(@Param("serviceFactory") String serviceFactory,@Param("noticeType") String NoticeType);


}
