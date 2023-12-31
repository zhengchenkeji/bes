package com.zc.efounder.JEnterprise.controller.subrealtimedatamanage;

import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.efounder.JEnterprise.service.subrealtimedatamanage.SubRealTimeDataService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: wanghongjie
 * @Description:客户端实时数据订阅
 * @Date: Created in 14:11 2022/9/24
 * @Modified By:
 */
@RestController
@RequestMapping("/basedatamanage/pubsubmanage")
public class SubRealTimeDataController {

    @Resource
    private SubRealTimeDataService subRealTimeDataService;


    /**
     * 客户端订阅消息
     * @param event
     * @return
     */
    @RequestMapping(value = "/subscribe", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult subscribe(String event) {
        AjaxResult ajaxResult = new AjaxResult();

        ajaxResult = subRealTimeDataService.subscribe(event);

        return ajaxResult;
    }

    /**
     * 客户端批量订阅消息
     * @param eventList
     * @return
     */
    @RequestMapping(value = "/subscribeList", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult subscribeList(@RequestBody List<String> eventList) {
        AjaxResult ajaxResult = new AjaxResult();

        ajaxResult = subRealTimeDataService.subscribeList(eventList);

        return ajaxResult;
    }

    /**
     * 客户端取消订阅
     * @param event
     * @return
     */
    @RequestMapping(value = "/unsubscribe", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult unsubscribe(String event) {
        AjaxResult ajaxResult = new AjaxResult();

        ajaxResult = subRealTimeDataService.unsubscribe(event);

        return ajaxResult;
    }

    /**
     * 客户端批量取消订阅
     * @param eventList
     * @return
     */
    @RequestMapping(value = "/unsubscribeList", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult unsubscribeList(@RequestBody List<String> eventList) {
        AjaxResult ajaxResult = new AjaxResult();

        ajaxResult = subRealTimeDataService.unsubscribeList(eventList);

        return ajaxResult;
    }
}
