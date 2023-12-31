package com.zc.efounder.JEnterprise.controller.noticeManage;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

import com.ruoyi.common.utils.sms.domain.AthenaBesSmsLog;
import com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity;
import com.zc.efounder.JEnterprise.domain.noticeManage.NoticeConfig;
import com.zc.efounder.JEnterprise.domain.noticeManage.vo.DebugModel;
import com.zc.efounder.JEnterprise.service.noticeManage.NoticeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * description:
 * author: sunshangeng
 * date:2023/2/8 11:49
 */
@RestController
@Api(value = "NoticeConfigController", tags = {"通知配置"})
@RequestMapping("/noticeManage/noticeConfig")
public class NoticeConfigController extends BaseController {


    @Resource
    private NoticeConfigService noticeConfigService;

    /**
     * @description:新增通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:32
     * @param: [noticeConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @ApiOperation(value = "新增通知配置")
    @Log(title = "通知配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NoticeConfig noticeConfig) {
        return noticeConfigService.insertNoticeConfig(noticeConfig);
    }


    /**
     * @description:查询通知配置列表
     * @author: sunshangeng
     * @date: 2023/2/8 14:31
     * @param: [noticeConfig]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    @ApiOperation(value = "查询通知配置")

    @GetMapping("/list")
    public TableDataInfo list(NoticeConfig noticeConfig) {
        startPage();
        List<NoticeConfig> list = noticeConfigService.selectNoticeConfigList(noticeConfig);
        return getDataTable(list);
    }


    /**
     * @description:导出通知配置数据
     * @author: sunshangeng
     * @date: 2023/2/8 14:36
     * @param: [response, NoticeConfig]
     * @return: void
     **/
    @ApiOperation(value = "导出通知配置")

    @Log(title = "通知配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NoticeConfig NoticeConfig) {
        List<NoticeConfig> list = noticeConfigService.selectNoticeConfigList(NoticeConfig);
        ExcelUtil<NoticeConfig> util = new ExcelUtil<>(NoticeConfig.class);
        util.exportExcel(response, list, "通知配置数据");
    }


    /**
     * @description:根据id查询通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:36
     * @param: [id]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @ApiOperation(value = "获取通知配置详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(noticeConfigService.selectNoticeConfigById(id));
    }

    /**
     * @description:修改通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:37
     * @param: [NoticeConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @ApiOperation(value = "修改通知配置信息")
    @Log(title = "通知配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NoticeConfig NoticeConfig) {
        return noticeConfigService.updateNoticeConfig(NoticeConfig);
    }

    /**
     * @description:删除通知配置
     * @author: sunshangeng
     * @date: 2023/2/8 14:38
     * @param: [ids]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @ApiOperation(value = "删除通知配置")
    @Log(title = "通知配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return noticeConfigService.deleteNoticeConfigByIds(ids);
    }


    /**
     * @description:获取所有服务厂商集合
     * @author: sunshangeng
     * @date: 2023/2/8 14:31
     * @param: [noticeConfig]
     * @return: com.ruoyi.common.core.page.TableDataInfo
     **/
    @ApiOperation(value = "获取所有厂商集合")
    @GetMapping("/getAllServiceFactory")
    public AjaxResult getAllServiceFactory(NoticeConfig noticeConfig) {

        return noticeConfigService.getAllServiceFactory();
    }


    /***
     * @description:获取当前配置所属模板
     * @author: sunshangeng
     * @date: 2023/2/10 14:44
     * @param:
     * @return:
     **/
    @ApiOperation(value = "根据通知配置获取模板集合")
    @GetMapping("/getNoticeTemplatebyConfig")
    public AjaxResult getNoticeTemplatebyConfig(NoticeConfig noticeConfig) {
        return noticeConfigService.getNoticeTemplatebyConfig(noticeConfig);
    }


    /**
     * @description:通知配置调试
     * @author: sunshangeng
     * @date: 2023/2/8 14:37
     * @param: [NoticeConfig]
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
//    @Log(title = "通知配置调试", businessType = BusinessType.INSERT)
    @ApiOperation(value = "通知配置调试")

    @PostMapping("/debugConfig")
    public AjaxResult debugingConfig(@RequestBody DebugModel debugModel) {
        return noticeConfigService.debugingCofig(debugModel);
    }


    /**
     * @description:获取通知日志
     * @author: sunshangeng
     * @date: 2023/2/23 9:36
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @ApiOperation(value = "获取通知日志")

    @GetMapping("getNoticeLogList")
    public TableDataInfo getNoticeLogList(String isSuccess,String type,String noticeTemplate, String noticeConfig, Date[] noticeLogTime) {


        startPage();
        List<AthenaBesSmsLog> noticeLogList = noticeConfigService.getNoticeLogList(isSuccess, noticeConfig, noticeLogTime,type,noticeTemplate);
        return getDataTable(noticeLogList);
//        return noticeConfigService.debugingCofig(debugModel);
    }


    /**
     * @description:删除模板文件
     * @author: sunshangeng
     * @date: 2023/2/23 9:36
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @DeleteMapping("deleteTemplateFile")
    public AjaxResult deleteTemplateFile(String path) {
        return null;
    }
    /**
     * @description:根据通知类型查询所有通知配置字典
     * @author: sunshangeng
     * @date: 2023/3/3 15:15
     * @param: [noticeType]
     * @return: com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity
     **/
    @ApiOperation(value = "根据类型查询通知配置字典")
    @GetMapping("getNoticeConfigListByType")
    public AjaxResult getNoticeConfigList( String noticeType){
        List<DicDataEntity> noticeConfigList = noticeConfigService.getNoticeConfigList(noticeType);
        return AjaxResult.success(noticeConfigList);
    }

    /**
     * @description:根据通知配置id查询通知模板字典
     * @author: sunshangeng
     * @date: 2023/3/3 15:15
     * @param: [noticeType]
     * @return: com.zc.efounder.JEnterprise.domain.besCommon.DicDataEntity
     **/
    @ApiOperation(value = "根据配置id查询通知模板字典")

    @GetMapping("getNoticeTemplateListByConfig")
    public AjaxResult getNoticeTemplateListByConfig(@RequestParam("configId") Long configId){
        List<DicDataEntity> noticeConfigList = noticeConfigService.getNoticeTemPlateListByConfig(configId);
        return AjaxResult.success(noticeConfigList);
    }



    /**
     * @description:获取通知日志详情
     * @author: sunshangeng
     * @date: 2023/2/23 9:36
     * @param: []
     * @return: com.ruoyi.common.core.domain.AjaxResult
     **/
    @ApiOperation(value = "获取消息日志详情")
    @GetMapping("getlogInfo")
    public AjaxResult getlogInfo(@RequestParam("logId") Long logId) {



        return  noticeConfigService.getLogInfo(logId);
    }

}
