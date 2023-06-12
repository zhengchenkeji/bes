package com.zc.efounder.JEnterprise.service.subrealtimedatamanage;

import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wanghongjie
 * @Description:客户端实时数据订阅
 * @Date: Created in 14:24 2022/9/24
 * @Modified By:
 */

public interface SubRealTimeDataService {

    AjaxResult subscribe(String event);

    AjaxResult unsubscribe(String event);

    AjaxResult subscribeList(List<String> eventList);

    AjaxResult unsubscribeList(List<String> eventList);

    AjaxResult subscribeByEqId(String event);

    AjaxResult unsubscribeByEqid(String event);

}
