package com.zc.efounder.JEnterprise.controller.safetyWarning;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.sms.server.AlTokenServer;
import com.zc.config.Manager.MqttClientServer;
import com.zc.connect.business.constant.DDCCmd;
import com.zc.connect.nettyServer.ChildChannelHandler.ModbusHandler.ModbusSendSyncMsgHandler;
import com.zc.efounder.JEnterprise.commhandler.MsgSubPubHandler;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmTactics;
import com.zc.efounder.JEnterprise.service.safetyWarning.AlarmTacticsService;
import com.zc.efounder.JEnterprise.service.subrealtimedatamanage.SubRealTimeDataService;
import io.swagger.annotations.*;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 告警策略Controller
 *
 * @author sunshangeng
 * @date 2022-09-16
 */
@RestController
@RequestMapping("/safetyWarning/alarmTactics")
@Api(value = "AlarmTacticsController", tags = {"告警策略"})
@ApiSupport(order = 25,author = "sunshangeng")
public class AlarmTacticsController extends BaseController
{
    @Resource
    private AlarmTacticsService AlarmTacticsService;

    @Resource
    private AlTokenServer alTokenServer;


    @Resource
    private Environment environment;

    @Resource
    private SubRealTimeDataService subRealTimeDataService;
    @Resource
    MqttClientServer mqttClientServer;

    @Resource
    ModbusSendSyncMsgHandler modbusSendSyncMsgHandler;
    /**
     * 查询告警策略列表
     */
    @ApiOperation(value = "查询告警策略列表")
    @PreAuthorize("@ss.hasPermi('safetyWarning:alarmTactics:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "告警名称"),
            @ApiImplicitParam(name = "active",value = "告警使能",dataType = "int"),
            @ApiImplicitParam(name = "level",value = "告警等级 1=一般,2=较大,3=严重",dataType = "int"),
            @ApiImplicitParam(name = "rangeType",value = "范围类型",dataType = "int"),
            @ApiImplicitParam(name = "isSendInform",value = "是否发送消息通知 0：否、1：是",dataType = "int"),
            @ApiImplicitParam(name = "deviceType",value = "策略类型",dataType = "int"),
            @ApiImplicitParam(name = "deviceName",value = "所属设备"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(AlarmTactics alarmTactics)
    {
//        try {
//            mqttClientServer.pub("MQTT","秦德华老哥牛逼");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        subRealTimeDataService.subscribe(22);

//        subRealTimeDataService.unsubscribe(22);

        startPage();
        List<AlarmTactics> list = AlarmTacticsService.selectAlarmTacticsList(alarmTactics);
        return getDataTable(list);
    }

    /**
     * 导出告警策略列表
     */
    @ApiOperation(value = "导出告警策略列表")
    @PreAuthorize("@ss.hasPermi('safetyWarning:alarmTactics:export')")
    @Log(title = "告警策略", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "告警名称"),
            @ApiImplicitParam(name = "active",value = "告警使能",dataType = "int"),
            @ApiImplicitParam(name = "level",value = "告警等级 1=一般,2=较大,3=严重",dataType = "int"),
            @ApiImplicitParam(name = "rangeType",value = "范围类型",dataType = "int"),
            @ApiImplicitParam(name = "isSendInform",value = "是否发送消息通知 0：否、1：是",dataType = "int"),
            @ApiImplicitParam(name = "deviceType",value = "策略类型",dataType = "int"),
            @ApiImplicitParam(name = "deviceName",value = "所属设备"),
    })
    public void export(HttpServletResponse response, AlarmTactics alarmTactics)
    {
        List<AlarmTactics> list = AlarmTacticsService.selectAlarmTacticsList(alarmTactics);
        ExcelUtil<AlarmTactics> util = new ExcelUtil<>(AlarmTactics.class);
        util.exportExcel(response, list, "告警策略数据");
    }
    /**
     * 获取告警策略详细信息
     */
    @ApiOperation(value = "获取告警策略详细信息")
    @PreAuthorize("@ss.hasPermi('safetyWarning:alarmTactics:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "告警策略ID",required = true,dataType = "long")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(AlarmTacticsService.selectAlarmTacticsById(id));
    }
    /**
     * 新增告警策略
     */
    @ApiOperation(value = "新增告警策略")
    @PreAuthorize("@ss.hasPermi('safetyWarning:alarmTactics:add')")
    @Log(title = "告警策略", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = "id")
    @PostMapping
    public AjaxResult add( @Valid @RequestBody AlarmTactics alarmTactics)
    {
        return AlarmTacticsService.insertAlarmTactics(alarmTactics);
    }

    /**
     * 修改告警策略
     */
    @ApiOperation(value = "修改告警策略")
    @PreAuthorize("@ss.hasPermi('safetyWarning:alarmTactics:edit')")
    @Log(title = "告警策略", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit( @Valid @RequestBody AlarmTactics alarmTactics)
    {
        return AlarmTacticsService.updateAlarmTactics(alarmTactics);
    }

    /**
     * 删除告警策略
     */
    @ApiOperation(value = "删除告警策略")
    @PreAuthorize("@ss.hasPermi('safetyWarning:alarmTactics:remove')")
    @Log(title = "告警策略", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "告警策略ID集合(以逗号隔开例如 1,2)",required = true,allowMultiple = true,dataType = "long")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return AlarmTacticsService.deleteAlarmTacticsByIds(ids);
    }

    /**
     * 查询告警策略字典
     */
    @ApiOperation(value = "查询告警策略字典")
    @PreAuthorize("@ss.hasPermi('safetyWarning:alarmTactics:list')")
    @GetMapping("/getAlarmTacticsDicData")
    public AjaxResult getAlarmTacticsDicData()
    {
        return AjaxResult.success(AlarmTacticsService.selectAlarmTacticsDicData());
    }


    /**
     * 获取支路，分户 分项，电表 树结构
     *sunshangeng
     */
    @ApiOperation(value = "获取支路，分户 分项，电表 树结构")
    @GetMapping("/treeSelect")
    public AjaxResult treeSelect()
    {
        return  AlarmTacticsService.selectTree(null);
//        return AjaxResult.success(AlarmTacticsService.selectAlarmTacticsDicData());
    }
    /**
     * 获取alibabatoken
     *  sunshangeng
     */
    @ApiOperation(value = "获取alibaba  Token")
    @GetMapping("/getToken")
    public AjaxResult getToken()
    {
        String alibabaToken = alTokenServer.getAlibabaToken();
        if(StringUtils.isNotBlank(alibabaToken)){
            return AjaxResult.success("获取成功",alibabaToken);
        }else{
            return AjaxResult.error("获取失败");
        }
    }


    /**
     * 查询报警配置根据告警类型
     *
     * @param alarmTypeId 报警类型id
     * @return {@code AjaxResult }
     * @Author qindehua
     * @Date 2022/11/22
     **/
    @ApiOperation(value = "查询报警配置根据告警类型")
    @PreAuthorize("@ss.hasPermi('alarmRealtime:data:list')")
    @GetMapping("/byAlarmTypeId")
    public AjaxResult byAlarmTypeId(@RequestParam("alarmTypeId") @ApiParam(value = "告警类型",required = true) Long alarmTypeId) {
        return AjaxResult.success(AlarmTacticsService.selectAlarmTacticsByAlarmTypeId(alarmTypeId));
    }
    /**
     * @description:获取当前语音播报环境
     * @author: sunshangeng
     * @date: 2022/11/29 19:07
     * @return:
     **/
    @ApiOperation(value = "获取当前语音播报环境")
    @GetMapping("/getAudioEnvironment")
    public AjaxResult getAudioEnvironment() {
        Map<String ,String > result=new HashMap<>();
        result.put("audioType",environment.getProperty("audio.type"));
        result.put("baidutoken",environment.getProperty("audio.baidutoken"));
        return AjaxResult.success(result);
    }
    @ApiOperation(value = "根据策略和通知类型获取通知配置")
    @GetMapping("/getNoticeLinkBytype")
    public AjaxResult getNoticeLinkBytype(Long alarmTacticsid,Integer noticeType) {
       return AlarmTacticsService.getNoticeLinkBytype(alarmTacticsid,noticeType);
    }
}
