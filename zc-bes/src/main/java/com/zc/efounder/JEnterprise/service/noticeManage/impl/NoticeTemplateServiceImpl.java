package com.zc.efounder.JEnterprise.service.noticeManage.impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.DateUtils;

import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.common.utils.sms.model.ALSmSParam;
import com.ruoyi.common.utils.sms.model.EmailParam;
import com.ruoyi.common.utils.sms.model.SmsResult;
import com.ruoyi.common.utils.sms.server.NoticeServer;
import com.zc.common.constant.NoticeTableConstants;
import com.zc.connect.util.StringUtil;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeConfig;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeTemplate;
import com.zc.efounder.JEnterprise.domain.noticeManage.vo.DebugModel;
import com.zc.efounder.JEnterprise.mapper.noticeManage.NoticeConfigMapper;
import com.zc.efounder.JEnterprise.mapper.noticeManage.NoticeTemplateMapper;
import com.zc.efounder.JEnterprise.service.noticeManage.NoticeTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * 通知模板配置Service业务层处理
 *
 * @author sunshangeng
 * @date 2023-02-09
 */
@Service
public class NoticeTemplateServiceImpl implements NoticeTemplateService {
    @Resource
    private NoticeTemplateMapper noticeTemplateMapper;


    @Resource
    private NoticeConfigMapper configMapper;


    @Resource
    private NoticeServer noticeServer;


    /**处理定时任务类*/
    @Resource
    private Scheduler scheduler;

    /**
     * 查询通知模板配置
     *
     * @param id 通知模板配置主键
     * @return 通知模板配置
     */
    @Override
    public NoticeTemplate selectNoticeTemplateById(Long id) {
        return noticeTemplateMapper.selectNoticeTemplateById(id);
    }

    /**
     * 查询通知模板配置列表
     *
     * @param noticeTemplate 通知模板配置
     * @return 通知模板配置
     */
    @Override
    public List<NoticeTemplate> selectNoticeTemplateList(NoticeTemplate noticeTemplate) {
        return noticeTemplateMapper.selectNoticeTemplateList(noticeTemplate);
    }

    /**
     * 新增通知模板配置
     *
     * @param noticeTemplate 通知模板配置
     * @return 结果
     */
    @Override
    public AjaxResult insertNoticeTemplate(NoticeTemplate noticeTemplate, MultipartFile files[], String path) {
        if (StringUtils.isBlank(noticeTemplate.getNoticetype())
                || StringUtils.isBlank(noticeTemplate.getTemplatename())) {
            return AjaxResult.error("参数传入不完整");
        }
        /**非播报模板处理*/
        if(!noticeTemplate.getNoticetype().equals("3")){
            if(StringUtils.isBlank(noticeTemplate.getServicefactory())){
                return AjaxResult.error("参数传入不完整");
            }

        /**短信*/
        if (noticeTemplate.getServicefactory().equals("21")) {
            if (StringUtils.isBlank(noticeTemplate.getTemplatecode()) ||
                    StringUtils.isBlank(noticeTemplate.getTemplatesign())
            ) {
                return AjaxResult.error("模板签名或编码不能为空");
            }

        }
        /**邮箱*/
        if (noticeTemplate.getServicefactory().equals("11")) {
            if (StringUtils.isBlank(noticeTemplate.getContent())
            ) {
                return AjaxResult.error("邮箱内容模板不能为空");
            }
        }
        }
        String filePath = "";

        /**有文件上传*/
        if (files!=null&&files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                /*处理文件上传*/
                try {
                    String filename = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), files[i], MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);


                    filePath = path + filename + ";";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (StringUtils.isNotBlank(filePath)) {
            noticeTemplate.setFilePath(filePath);
        }
        noticeTemplate.setCreateTime(DateUtils.getNowDate());
        int row = noticeTemplateMapper.insertNoticeTemplate(noticeTemplate);
        if (row > 0) {
            return AjaxResult.success("保存成功");
        } else {
            return AjaxResult.success("保存失败");

        }
    }

    /**
     * 修改通知模板配置
     *
     * @param noticeTemplate 通知模板配置
     * @return 结果
     */
    @Override
    public AjaxResult updateNoticeTemplate(NoticeTemplate noticeTemplate, MultipartFile files[], String path) {
        if (StringUtils.isBlank(noticeTemplate.getNoticetype())
                || StringUtils.isBlank(noticeTemplate.getTemplatename())) {
            return AjaxResult.error("参数传入不完整");
        }
        /**非播报模板处理*/
        if(!noticeTemplate.getNoticetype().equals("3")){
            if(StringUtils.isBlank(noticeTemplate.getServicefactory())){
                return AjaxResult.error("参数传入不完整");
            }

        /**短信*/
        if (noticeTemplate.getServicefactory().equals("21")) {
            if (StringUtils.isBlank(noticeTemplate.getTemplatecode()) ||
                    StringUtils.isBlank(noticeTemplate.getTemplatesign())
            ) {
                return AjaxResult.error("模板签名或编码不能为空");
            }

        }

        /**邮箱*/
        if (noticeTemplate.getServicefactory().equals("11")) {
            if (StringUtils.isBlank(noticeTemplate.getContent())
            ) {
                return AjaxResult.error("邮箱内容模板不能为空");
            }
        }
        }
        String filePath = "";
        /**删除文件*/
        NoticeTemplate oldTemPlate = noticeTemplateMapper.selectNoticeTemplateById(noticeTemplate.getId());
        String[] oldpath={};
        if(StringUtils.isNotBlank(oldTemPlate.getFilePath())){
             oldpath = oldTemPlate.getFilePath().split(";");

        }
        String[] newpath = {};

        if(StringUtils.isNotBlank(oldTemPlate.getFilePath())) {
             newpath = noticeTemplate.getFilePath().split(";");

        }
        List<String> delpathlist = new ArrayList<>();

        /**双重循环判断是否需要删除文件*/
        for (int i = 0; i < oldpath.length; i++) {
            boolean isdel = false;
            for (int j = 0; j < newpath.length; j++) {
                if (oldpath[i].equals(newpath[j])) {
                    filePath = filePath + oldpath[i]+";";
                    /**不需要做删除*/
                    isdel = true;
                    break;
                }
            }

            if (!isdel) {
                /**删除文件*/
                delpathlist.add(oldpath[i]);
            }
        }
        /**有文件上传*/
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                /*处理文件上传*/
                try {
                    String filename = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), files[i]);
                    filePath = filePath + path + filename + ";";
                } catch (Exception e) {

                    e.printStackTrace();
                    return AjaxResult.error(e.getMessage());
                }
            }
        }

        if (StringUtils.isNotBlank(filePath)) {
            noticeTemplate.setFilePath(filePath);
        }
        noticeTemplate.setUpdateTime(DateUtils.getNowDate());
        noticeTemplate.setCreateTime(DateUtils.getNowDate());
        int row = noticeTemplateMapper.updateNoticeTemplate(noticeTemplate);

        if (row > 0) {

            /**修改成功执行删除文件操作 无论是否删除成功，都不会报错，继续执行  */
            try {
                for (String delpath : delpathlist) {
                    delpath=delpath.substring(delpath.indexOf("/upload"), delpath.length());
                    FileUtils.deleteFile(RuoYiConfig.getProfile()+delpath);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return AjaxResult.success("修改成功");
        } else {
            return AjaxResult.success("修改失败");

        }
    }

    /**
     * 批量删除通知模板配置
     *
     * @param ids 需要删除的通知模板配置主键
     * @return 结果
     */
    @Override
    public int deleteNoticeTemplateByIds(Long[] ids) {
        List<String> delpath=new ArrayList<>();


        for (int i = 0; i <ids.length ; i++) {
            NoticeTemplate noticeTemplate = noticeTemplateMapper.selectNoticeTemplateById(ids[i]);
            if(StringUtils.isNotBlank(noticeTemplate.getFilePath())){
                String[] pathArray = noticeTemplate.getFilePath().split(";");
                for (int j = 0; j < pathArray.length; j++) {
                    if(StringUtils.isNotBlank(pathArray[j])){
                        delpath.add(pathArray[j]);
                    }
                }
            }
        }

        int delnum = noticeTemplateMapper.deleteNoticeTemplateByIds(ids);

        if(delnum>0){
            /**执行删除文件操作*/
            delpath.forEach(item->{
                item=item.substring(item.indexOf("/upload"), item.length());
                FileUtils.deleteFile(RuoYiConfig.getProfile()+item);

            });
        }
        return delnum;
    }

    /**
     * 删除通知模板配置信息
     *
     * @param id 通知模板配置主键
     * @return 结果
     */
    @Override
    public int deleteNoticeTemplateById(Long id) {
        return noticeTemplateMapper.deleteNoticeTemplateById(id);
    }

    @Override
    public AjaxResult getConfigbyTemplate(NoticeTemplate template) {
        List<NoticeConfig> noticeConfigList = configMapper.getNoticeConfigbyTemplate(template.getServicefactory(), template.getNoticetype());
        return AjaxResult.success(noticeConfigList);
    }

    @Override
    public AjaxResult debugTemplate(DebugModel debugModel) {

        if (StringUtils.isBlank(debugModel.getConfigId()) ||
                StringUtils.isBlank(debugModel.getContent())) {
            return AjaxResult.error("传入的信息不完整！");
        }
        JSONObject ContentObject = new JSONObject();

        if (StringUtils.isNotBlank(debugModel.getContent()) && debugModel.getContent().indexOf("{") != -1) {
            ContentObject = JSONObject.parseObject(debugModel.getContent());
        }
        /**获取配置信息*/
        NoticeConfig config = configMapper.selectNoticeConfigById(Long.parseLong(debugModel.getConfigId()));
        /**获取模板信息*/
        NoticeTemplate noticeTemplate = noticeTemplateMapper.selectNoticeTemplateById(Long.parseLong(debugModel.getTemplateId()));
        if (config.getNoticetype().equals("1")) {
            /**短信*/
            ALSmSParam ALSmSParam = new ALSmSParam(debugModel.getRecipient(),
                    debugModel.getContent(),
                    noticeTemplate.getId().toString(),
                    NoticeTableConstants.BES_NOTICETEMPLATE,
                    config.getRegionid(),
                    config.getAccesskeyid(),
                    config.getSecret(),
                    noticeTemplate.getTemplatecode(),
                    noticeTemplate.getTemplatesign(),
                    config.getId().intValue(),
                    noticeTemplate.getId().intValue());
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
                    filepath=RuoYiConfig.getProfile()+filepath;
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
                        , noticeTemplate.getId().toString()
                        , NoticeTableConstants.BES_NOTICETEMPLATE
                        , noticeTemplate.getTitle()
                        , config.getEmailServerHost()
                        , Integer.parseInt(config.getEmailServerPort())
                        , config.getFromemail()
                        , config.getFromemailpwd()
                        ,config.getId().intValue()
                        ,noticeTemplate.getId().intValue()
                        , fileList
                );
                 smsResult = noticeServer.sendMessageByConfigFiles(emailParam);

            }else{
                /**无需发送附件的*/
                emailParam = new EmailParam(debugModel.getRecipient()
                        , Content
                        , noticeTemplate.getId().toString()
                        ,NoticeTableConstants.BES_NOTICETEMPLATE
                        , noticeTemplate.getTitle()
                        , config.getEmailServerHost()
                        , Integer.parseInt(config.getEmailServerPort())
                        , config.getFromemail()
                        , config.getFromemailpwd()
                        ,config.getId().intValue()
                        ,noticeTemplate.getId().intValue());
                 smsResult = noticeServer.sendMessageByConfig(emailParam);

            }


            if (smsResult.isSuccess()) {
                return AjaxResult.success();
            } else {
                return AjaxResult.error(smsResult.getMessage());
            }
        } else if (config.getNoticetype().equals("3")) {

            if (noticeTemplate.getServicefactory().equals("31")) {

                /**语音播报*/
                ALSmSParam Param = new ALSmSParam(config.getAccesskeyid(), config.getSecret(), noticeTemplate.getId().toString());
                SmsResult result = noticeServer.getAlibabaToken(Param);
                if (result.isSuccess()) {
                    return AjaxResult.success(result.getMessage());
                } else {
                    return AjaxResult.error(result.getMessage());
                }
            } else if (noticeTemplate.getServicefactory().equals("32")) {
                return AjaxResult.success(config.getToken());
            } else {
                return AjaxResult.error("调试失败，未定义当前语音类型配置信息！");
            }
        } else {
            return AjaxResult.error("调试失败，未定义当前类型配置信息！");
        }
    }

}
