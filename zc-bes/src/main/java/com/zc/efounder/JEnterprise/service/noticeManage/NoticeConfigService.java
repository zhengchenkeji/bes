package com.zc.efounder.JEnterprise.service.noticeManage;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.sms.domain.AthenaBesSmsLog;
import com.ruoyi.common.utils.sms.mapper.SmsLogMapper;
import com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeConfig;
import com.zc.efounder.JEnterprise.domain.noticeManage.vo.DebugModel;

import java.util.Date;
import java.util.List;

/**
 * description:通知配置服务层
 * author: sunshangeng
 * date:2023/2/8 11:51
 **/
public interface NoticeConfigService {
    /**
     * @description:新增通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:15
     * @return: com.ruoyi.common.core.domain.AjaxResult
     *
     * @param noticeConfig*/
    AjaxResult insertNoticeConfig(NoticeConfig noticeConfig);


    /**
     * @description:查询通知配置列表
     * @author: sunshangeng
     * @date: 2023/2/8 14:25
     * @param: [noticeConfig]
     * @return: java.util.List<com.ruoyi.efounder.JEnterprise.domain.noticeManage.NoticeConfig>
     **/
    List<NoticeConfig> selectNoticeConfigList(NoticeConfig noticeConfig);



    /**
     * @description:根据id查询通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:27
     * @param: [id]
     * @return: com.ruoyi.efounder.JEnterprise.domain.noticeManage.NoticeConfig
     **/
     NoticeConfig selectNoticeConfigById(Long id);


    /**
     * @description:修改通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:27
     * @param: [noticeConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult updateNoticeConfig(NoticeConfig noticeConfig);


    /**
     * @description:删除告警配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:30
     * @param: [ids]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult deleteNoticeConfigByIds(Long[] ids);


    /**
     * @description:获取所有服务厂商列表
     * @author: sunshangeng
     * @date: 2023/2/8 14:30
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult getAllServiceFactory();


    /**
     * @description:根据配置获取模板
     * @author: sunshangeng
     * @date: 2023/2/10 14:48
     * @param: [noticeConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult getNoticeTemplatebyConfig(NoticeConfig noticeConfig);



    /**
     * @description:调试通知配置
     * @author: sunshangeng
     * @date: 2023/2/13 10:39
     * @param: [debugModel]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult debugingCofig(DebugModel debugModel);



    /**
     * @description:查询通知日志
     * @author: sunshangeng
     * @date: 2023/2/24 9:37
     * @param: [smsLogMapper]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    List<AthenaBesSmsLog> getNoticeLogList(String isSuccess, String configId, Date[] noticeLogTime,String type,String templateId);



    /**
     * @description:删除上传的模板文件
     * @author: sunshangeng
     * @date: 2023/2/23 9:38
     * @param: [path]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    AjaxResult deleteTemplateFile(String path);


    /**
     * @description:根据通知类型查询配置
     * @author: sunshangeng
     * @date: 2023/3/3 15:17
     * @param: [noticeType]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity>
     **/
     List<DicDataEntity> getNoticeConfigList(String noticeType);

     List<DicDataEntity> getNoticeTemPlateListByConfig(Long configId);

    AjaxResult getLogInfo(Long logId);

}
