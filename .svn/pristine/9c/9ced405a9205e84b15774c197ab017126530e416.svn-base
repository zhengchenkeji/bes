package com.zc.efounder.JEnterprise.service.noticeManage;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeConfig;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeTemplate;
import com.zc.efounder.JEnterprise.domain.noticeManage.vo.DebugModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 通知模板配置Service接口
 *
 * @author sunshangeng
 * @date 2023-02-09
 */
public interface NoticeTemplateService
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
    AjaxResult insertNoticeTemplate(NoticeTemplate noticeTemplate, MultipartFile files[],String path);

    /**
     * 修改通知模板配置
     *
     * @param noticeTemplate 通知模板配置
     * @return 结果
     */
    AjaxResult updateNoticeTemplate(NoticeTemplate noticeTemplate, MultipartFile files[],String path);

    /**
     * 批量删除通知模板配置
     *
     * @param ids 需要删除的通知模板配置主键集合
     * @return 结果
     */
    int deleteNoticeTemplateByIds(Long[] ids);

    /**
     * 删除通知模板配置信息
     *
     * @param id 通知模板配置主键
     * @return 结果
     */
    int deleteNoticeTemplateById(Long id);
    /**
     * @description:根据模板获取配置
     * @author: sunshangeng
     * @date: 2023/2/10 14:48
     * @param: [noticeConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult getConfigbyTemplate(NoticeTemplate template);


    AjaxResult debugTemplate(DebugModel debugModel);


}
