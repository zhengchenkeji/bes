package com.zc.efounder.JEnterprise.service.noticeManage.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.sms.domain.AthenaBesSmsLog;
import com.ruoyi.common.utils.sms.mapper.SmsLogMapper;
import com.ruoyi.common.utils.sms.model.EmailParam;
import com.ruoyi.common.utils.sms.model.ALSmSParam;
import com.ruoyi.common.utils.sms.model.SmsResult;
import com.ruoyi.common.utils.sms.server.EmailServer;

import com.ruoyi.common.utils.sms.server.NoticeServer;
import com.ruoyi.common.utils.sms.server.SmsServer;
import com.zc.common.constant.NoticeTableConstants;
import com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeConfig;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeTemplate;
import com.zc.efounder.JEnterprise.domain.noticeManage.vo.DebugModel;
import com.zc.efounder.JEnterprise.mapper.noticeManage.NoticeConfigMapper;
import com.zc.efounder.JEnterprise.mapper.noticeManage.NoticeTemplateMapper;
import com.zc.efounder.JEnterprise.service.noticeManage.NoticeConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * description:通知配置服务层实现类
 * author: sunshangeng
 * date:2023/2/8 11:51
 */
@Service
public class NoticeConfigServiceImpl implements NoticeConfigService {


    @Resource
    private  SmsLogMapper smsLogMapper;

    @Resource
    private NoticeConfigMapper noticeConfigMapper;

    @Resource
    private NoticeTemplateMapper noticeTemplateMapper;

    @Resource
    private NoticeServer noticeServer;


    /**
     * @description:新增通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:17
     * @param: [noticeConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult insertNoticeConfig(NoticeConfig noticeConfig) {

        if (StringUtils.isBlank(noticeConfig.getName()) ||
                StringUtils.isBlank(noticeConfig.getServicefactory()) ||
                StringUtils.isBlank(noticeConfig.getNoticetype())) {
            return AjaxResult.error("传入的参数不完整");
        }
        /**邮箱*/
        if (noticeConfig.getServicefactory().equals("11")) {
            if (StringUtils.isBlank(noticeConfig.getFromemail()) ||
                    StringUtils.isBlank(noticeConfig.getFromemailpwd()) ||
                    StringUtils.isBlank(noticeConfig.getEmailServerHost()) ||
                    StringUtils.isBlank(noticeConfig.getEmailServerPort())) {
                return AjaxResult.error("邮箱配置信息不完整");

            }
        }
        /**百度语音*/
        if (noticeConfig.getServicefactory().equals("32")) {
            if (StringUtils.isBlank(noticeConfig.getToken())
            ) {
                return AjaxResult.error("百度语音配置信息不完整");

            }

        }
        /**阿里云语音*/
        if (noticeConfig.getServicefactory().equals("31")) {
            if (StringUtils.isBlank(noticeConfig.getSecret()) ||
                    StringUtils.isBlank(noticeConfig.getAccesskeyid())
            ) {
                return AjaxResult.error("阿里云语音配置信息不完整");

            }

        }

        /**阿里云短信配置*/
        if (noticeConfig.getServicefactory().equals("31")) {
            if (StringUtils.isBlank(noticeConfig.getSecret()) ||
                    StringUtils.isBlank(noticeConfig.getAccesskeyid()) ||
                    StringUtils.isBlank(noticeConfig.getRegionid())||
                    StringUtils.isBlank(noticeConfig.getAppkey())

            ) {
                return AjaxResult.error("阿里云短信配置信息不完整");

            }

        }


        noticeConfig.setCreateTime(new Date());
        noticeConfigMapper.insertNoticeConfig(noticeConfig);
        return AjaxResult.success();
    }

    /**
     * @description:查询通知配置列表
     * @author: sunshangeng
     * @date: 2023/2/8 14:29
     * @param: [noticeConfig]
     * @return: java.util.List<com.ruoyi.efounder.JEnterprise.domain.noticeManage.NoticeConfig>
     **/
    @Override
    public List<NoticeConfig> selectNoticeConfigList(NoticeConfig noticeConfig) {


        return noticeConfigMapper.selectNoticeConfigList(noticeConfig);
    }

    /**
     * @description:根据id查询通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:29
     * @param: [id]
     * @return: com.ruoyi.efounder.JEnterprise.domain.noticeManage.NoticeConfig
     **/
    @Override
    public NoticeConfig selectNoticeConfigById(Long id) {
        return noticeConfigMapper.selectNoticeConfigById(id);
    }

    /**
     * @description:修改告警配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:29
     * @param: [noticeConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult updateNoticeConfig(NoticeConfig noticeConfig) {
        noticeConfigMapper.updateNoticeConfig(noticeConfig);
        return AjaxResult.success();
    }

    /***
     * @description:删除通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:31
     * @param: [ids]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult deleteNoticeConfigByIds(Long[] ids) {
        return null;
    }

    /**
     * 获取所有若依集合
     */
    @Override
    public AjaxResult getAllServiceFactory() {
        List<Map> mapList = new ArrayList<>();
        List<SysDictData> dictCache = DictUtils.getDictCache("bes_notice_sms_factory");

        dictCache.addAll(DictUtils.getDictCache("bes_notice_email_factory"));
        dictCache.addAll(DictUtils.getDictCache("bes_notice_voice_factory"));
        dictCache.forEach(item -> {
            Map<String, String> map = new HashMap<>();
            map.put("label", item.getDictLabel());
            map.put("value", item.getDictValue());
            mapList.add(map);
        });
        return AjaxResult.success(mapList);
    }

    /**
     * @description:根据配置获取模板
     * @author: sunshangeng
     * @date: 2023/2/10 14:49
     * @param: [noticeConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult getNoticeTemplatebyConfig(NoticeConfig noticeConfig) {

        List<NoticeTemplate> noticeTemplateList = noticeTemplateMapper.getNoticeTemplatebyConfig(noticeConfig.getServicefactory(), noticeConfig.getNoticetype());
        return AjaxResult.success(noticeTemplateList);
    }

    /***
     * @description:调试通知配置
     * @author: sunshangeng
     * @date: 2023/2/13 10:42
     * @param: [debugModel]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult debugingCofig(DebugModel debugModel) {

        if (StringUtils.isBlank(debugModel.getConfigId()) ||
                StringUtils.isBlank(debugModel.getContent())) {
            return AjaxResult.error("传入的信息不完整！");
        }
        JSONObject ContentObject = new JSONObject();

        if (StringUtils.isNotBlank(debugModel.getContent()) && debugModel.getContent().indexOf("{") != -1) {
            ContentObject = JSONObject.parseObject(debugModel.getContent());
        }
        /**获取配置信息*/
        NoticeConfig config = noticeConfigMapper.selectNoticeConfigById(Long.parseLong(debugModel.getConfigId()));
        /**获取模板信息*/
        NoticeTemplate noticeTemplate=new NoticeTemplate();
        if(StringUtils.isNotBlank(debugModel.getTemplateId())){


             noticeTemplate = noticeTemplateMapper.selectNoticeTemplateById(Long.parseLong(debugModel.getTemplateId()));
        }
        if (config.getNoticetype().equals("1")) {
            /**短信*/
            ALSmSParam ALSmSParam = new ALSmSParam(debugModel.getRecipient(),
                    debugModel.getContent(),
                    config.getId().toString(),
                    NoticeTableConstants.BES_BOTICECONFIG,
                    config.getRegionid(),
                    config.getAccesskeyid(),
                    config.getSecret(),
                    noticeTemplate.getTemplatecode(),
                    noticeTemplate.getTemplatesign()
                    , config.getId().intValue()
                    , noticeTemplate.getId().intValue());
            SmsResult smsResult = noticeServer.sendbyConfig(ALSmSParam);
            if (smsResult.isSuccess()) {
                return AjaxResult.success();
            } else {
                return AjaxResult.error(smsResult.getMessage());
            }
        } else if (config.getNoticetype().equals("2")) {
            /**邮箱*/

            /**提取模板用于填充数据*/
            String Content = noticeTemplate.getContent();
            /**获取到所在的模板是否有变量*/
            int number = noticeTemplate.getContent().indexOf("#{");
            /**传入的变量信息为空！*/
            if (ContentObject == null && number != -1) {
                /**未进行变量赋值*/
                return AjaxResult.error("当前模板存在变量，但未赋值！");
            } else {
                /**循环替换模板内容*/
                for (String key : ContentObject.keySet()) {
                    Content = Content.replace("#{" + key + "}", ContentObject.getString(key));
                }
            }
            number = Content.indexOf("#{");
            /**说明变量未全部替换完成*/
            if (number != -1) {
                return AjaxResult.error("定义的模板变量和传入的变量信息无法对应！");
            }
            EmailParam emailParam=null;
            SmsResult smsResult=null;
            /**判断是否需要发送附件*/
            if(StringUtils.isNotBlank(noticeTemplate.getFilePath())){
                /**需要发送附件的*/
                String[] filesPath = noticeTemplate.getFilePath().split(";");


                List<File> fileList=new ArrayList<>();
                for (int i = 0; i < filesPath.length; i++) {
                    /**拼接实际存储路径*/
                    String  filepath=filesPath[i].substring(filesPath[i].indexOf("/upload"), filesPath[i].length());
                    filepath= RuoYiConfig.getProfile()+filepath;
                    /**读取文件*/
                    File file = new File(filepath);
                    /**判断是否有文件*/
                    if (file.isFile() && file.exists())
                    {
                        fileList.add(file);
                    }

                }
                emailParam = new EmailParam(debugModel.getRecipient()
                        , Content
                        , config.getId().toString()
                        , NoticeTableConstants.BES_BOTICECONFIG
                        , noticeTemplate.getTitle()
                        , config.getEmailServerHost()
                        , Integer.parseInt(config.getEmailServerPort())
                        , config.getFromemail()
                        , config.getFromemailpwd()
                        , config.getId().intValue()
                        , noticeTemplate.getId().intValue()
                        , fileList
                );
                smsResult = noticeServer.sendMessageByConfigFiles(emailParam);

            }else{
                /**无需发送附件的*/
                emailParam = new EmailParam(debugModel.getRecipient()
                        , Content
                        , config.getId().toString()
                        ,NoticeTableConstants.BES_BOTICECONFIG
                        , noticeTemplate.getTitle()
                        , config.getEmailServerHost()
                        , Integer.parseInt(config.getEmailServerPort())
                        , config.getFromemail()
                        , config.getFromemailpwd()
                        , config.getId().intValue()
                        , noticeTemplate.getId().intValue()
                );
                smsResult = noticeServer.sendMessageByConfig(emailParam);

            }


            if (smsResult.isSuccess()) {
                return AjaxResult.success();
            } else {
                return AjaxResult.error(smsResult.getMessage());
            }
        } else if(config.getNoticetype().equals("3")){


            if(config.getServicefactory().equals("31")){
                /**语音播报*/
                ALSmSParam Param = new ALSmSParam(config.getAccesskeyid(),config.getSecret(),config.getId().toString());
                SmsResult result = noticeServer.getAlibabaToken(Param);
                if (result.isSuccess()) {
                    return AjaxResult.success(result.getMessage());
                } else {
                    return AjaxResult.error(result.getMessage());
                }
            }else if (config.getServicefactory().equals("32")){
                return AjaxResult.success(config.getToken());
            }else{
                return AjaxResult.error("调试失败，未定义当前语音类型配置信息！");
            }

        }else{
            return AjaxResult.error("调试失败，未定义当前类型配置信息！");
        }
    }

    /**
     * @description:查询通知日志
     * @author: sunshangeng
     * @date: 2023/2/24 9:38
     * @param: [smsLogMapper]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public List<AthenaBesSmsLog> getNoticeLogList(String isSuccess, String configId, Date[] noticeLogTime,String type,String templateId) {

        QueryWrapper<AthenaBesSmsLog> wrapper = new QueryWrapper<>();

        if(StringUtils.isNotBlank(type)){
            wrapper.eq("type",type);
        }

        if(StringUtils.isNotBlank(templateId)){
            wrapper.eq("notice_template",templateId);
        }
        if(StringUtils.isNoneBlank(configId)){
            wrapper.eq("notice_config",configId);

        }
        if(noticeLogTime!=null&&noticeLogTime.length>1){
            wrapper.between("send_time",noticeLogTime[0],noticeLogTime[1]);
        }
        if(StringUtils.isNotBlank(isSuccess)){
            wrapper.eq("is_success",isSuccess);
        }
        wrapper.orderByDesc("send_time");
//        wrapper.eq("yw_table",NoticeTableConstants.BES_BOTICECONFIG);
        List<AthenaBesSmsLog> smsLogList = smsLogMapper.selectList(wrapper);

        return smsLogList;
    }

    @Override
    public AjaxResult deleteTemplateFile(String path) {
        return null;
    }

    /**
     * @description:根据通知类型查询通知配置
     * @author: sunshangeng
     * @date: 2023/3/3 15:17
     * @param: [noticeType]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity>
     **/
    @Override
    public List<DicDataEntity> getNoticeConfigList(String noticeType) {
        List<DicDataEntity> noticeConfigList = noticeConfigMapper.getNoticeConfigList(noticeType);
        return noticeConfigList;

    }
    /**
     * @description:根据通知配置查询模板字典
     * @author: sunshangeng
     * @date: 2023/3/3 15:28
     * @param: [configId]
     * @return: java.util.List<com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity>
     **/
    @Override
    public List<DicDataEntity> getNoticeTemPlateListByConfig(Long configId) {
        NoticeConfig noticeConfig = noticeConfigMapper.selectNoticeConfigById(configId);
        List<NoticeTemplate> noticeTemplateList=new ArrayList<>();
        if(noticeConfig.getNoticetype().equals("3")){
            NoticeTemplate template=new NoticeTemplate();
            template.setNoticetype("3");
             noticeTemplateList = noticeTemplateMapper.selectNoticeTemplateList(template);

        }else{
            noticeTemplateList = noticeTemplateMapper.getNoticeTemplatebyConfig(noticeConfig.getServicefactory(), noticeConfig.getNoticetype());

        }
        List<DicDataEntity> list=new ArrayList<>();
        for (NoticeTemplate template : noticeTemplateList) {
            list.add(new DicDataEntity(template.getId()+"",template.getTemplatename()));
        }
        return list;
    }

    /**
     * @description:
     * @author: sunshangeng
     * @date: 2023/5/9 10:53
     * @param: [logId]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @Override
    public AjaxResult getLogInfo(Long logId) {
        AthenaBesSmsLog athenaBesSmsLog = smsLogMapper.selectById(logId);
        if(athenaBesSmsLog==null){
            return  AjaxResult.error("失败,传入的数据有误");
        }
        return AjaxResult.success(athenaBesSmsLog);
    }
}
