package com.zc.efounder.JEnterprise.controller.noticeManage;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeConfig;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeTemplate;
import com.zc.efounder.JEnterprise.domain.noticeManage.vo.DebugModel;
import com.zc.efounder.JEnterprise.service.noticeManage.NoticeTemplateService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 通知模板配置Controller
 *
 * @author sunshangeng
 * @date 2023-02-09
 */
@RestController
@RequestMapping("/noticeManage/noticeTemplate")
public class NoticeTemplateController extends BaseController
{
    @Resource
    private NoticeTemplateService noticeTemplateService;

//    @Resource
//    private EmailServer emailServer;
    /**
     * 查询通知模板配置列表
     */
    @GetMapping("/list")
    public TableDataInfo list(NoticeTemplate noticeTemplate)
    {
//        SmsParam smsParam = new SmsParam("2507825709@qq.com", "<p><strong>厉害了哈哈哈哈  <em>老猪</em></strong></p><ol><li><strong><em>胖</em></strong></li><li><strong><em>黑</em></strong></li><li><strong><em>臭</em></strong></li></ol><p><s>老肥 </s>  <a href=\"http://baidu.com\" rel=\"noopener noreferrer\" target=\"_blank\">黑猪</a></p>", "", "你是一只大胖猪");

//        emailServer.sendMessage(smsParam);
        startPage();
        List<NoticeTemplate> list = noticeTemplateService.selectNoticeTemplateList(noticeTemplate);
        return getDataTable(list);
    }

    @GetMapping("/listall")
    public TableDataInfo listall()
    {
//        SmsParam smsParam = new SmsParam("2507825709@qq.com", "<p><strong>厉害了哈哈哈哈  <em>老猪</em></strong></p><ol><li><strong><em>胖</em></strong></li><li><strong><em>黑</em></strong></li><li><strong><em>臭</em></strong></li></ol><p><s>老肥 </s>  <a href=\"http://baidu.com\" rel=\"noopener noreferrer\" target=\"_blank\">黑猪</a></p>", "", "你是一只大胖猪");

//        emailServer.sendMessage(smsParam);
        List<NoticeTemplate> list = noticeTemplateService.selectNoticeTemplateList(null);
        return getDataTable(list);
    }

    /**
     * 导出通知模板配置列表
     */
    @Log(title = "通知模板配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NoticeTemplate noticeTemplate)
    {
        List<NoticeTemplate> list = noticeTemplateService.selectNoticeTemplateList(noticeTemplate);
        ExcelUtil<NoticeTemplate> util = new ExcelUtil<>(NoticeTemplate.class);
        util.exportExcel(response, list, "通知模板配置数据");
    }

    /**
     * 获取通知模板配置详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(noticeTemplateService.selectNoticeTemplateById(id));
    }

    /**
     * 新增通知模板配置
     */
    @Log(title = "通知模板配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add( MultipartFile files[],
                          @RequestParam("formJson") String json,
                          @RequestParam("path") String path)
    {
        NoticeTemplate noticeTemplate= JSONObject.parseObject(json,NoticeTemplate.class);

        return noticeTemplateService.insertNoticeTemplate(noticeTemplate,files,path);
    }

    /**
     * 修改通知模板配置
     */
    @Log(title = "通知模板配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit( MultipartFile files[],
                           @RequestParam("formJson") String json,
                           @RequestParam("path") String path)
    {

        NoticeTemplate noticeTemplate= JSONObject.parseObject(json,NoticeTemplate.class);

//        return null;
        return noticeTemplateService.updateNoticeTemplate(noticeTemplate,files,path);
    }

    /**
     * 删除通知模板配置
     */
    @Log(title = "通知模板配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(noticeTemplateService.deleteNoticeTemplateByIds(ids));
    }



    /**
     * 根据模板获取所有通知配置
     */
    @GetMapping("/getNoticeConfigbyTemplate")
    public AjaxResult getNoticeConfigbyTemplate(NoticeTemplate noticeTemplate)
    {
        return noticeTemplateService.getConfigbyTemplate(noticeTemplate);
    }

    @PostMapping("/debugTemplate")
    public AjaxResult debugTemplate( @RequestBody DebugModel debugModel)
    {
        return noticeTemplateService.debugTemplate(debugModel);
    }
}
