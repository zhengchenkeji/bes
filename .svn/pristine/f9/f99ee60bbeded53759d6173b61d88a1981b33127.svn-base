package com.zc.efounder.JEnterprise.controller.safetyWarning;

import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.zc.efounder.JEnterprise.domain.safetyWarning.AlarmNotifier;
import com.zc.efounder.JEnterprise.service.safetyWarning.AlarmNotifierService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 告警接收组Controller
 *
 * @author sunshangeng
 * @date 2022-09-15
 */
@RestController
@RequestMapping("/safetyWarning/AlarmNotifier")
@Api(value = "AlarmNotifierController", tags = {"告警接收组"})
@ApiSupport(order = 28,author = "sunshangeng")
public class AlarmNotifierController extends BaseController
{
    @Autowired
    private AlarmNotifierService alarmNotifierService;

    /**
     * sunshangeng
     * 查询告警接收组列表
     */
    @ApiOperation(value = "查询告警接收组列表")
    @PreAuthorize("@ss.hasPermi('safetyWarning:AlarmNotifier:list')")
    @GetMapping("/list")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "组名"),
            @ApiImplicitParam(name = "deptId",value = "所属部门id",dataType = "long"),
            @ApiImplicitParam(name = "pageNum", value = "当前页(默认1)",  paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示行数(默认10)",  paramType = "query",dataType = "int"),
    })
    public TableDataInfo list(AlarmNotifier alarmNotifier)
    {
//        alarmNotifier.setTacticsId("6");
        startPage();
        List<AlarmNotifier> list = alarmNotifierService.selectAlarmNotifierList(alarmNotifier);
        return getDataTable(list);
    }

    /**
     * 导出告警接收组列表
     * sunshangeng
     */
    @ApiOperation(value = "导出告警接收组列表")
    @PreAuthorize("@ss.hasPermi('safetyWarning:AlarmNotifier:export')")
    @Log(title = "告警接收组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperationSupport(ignoreParameters = "id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name",value = "姓名"),
            @ApiImplicitParam(name = "homeAddress",value = "家庭地址"),
            @ApiImplicitParam(name = "tel",value = "联系电话"),
            @ApiImplicitParam(name = "email",value = "邮箱"),
            @ApiImplicitParam(name = "company",value = "公司名称"),
            @ApiImplicitParam(name = "companyTel",value = "公司联系电话"),
            @ApiImplicitParam(name = "post",value = "岗位"),
            @ApiImplicitParam(name = "groupId",value = "所属组织id",dataType = "long"),
    })
    public void export(HttpServletResponse response, AlarmNotifier alarmNotifier)
    {
        List<AlarmNotifier> list = alarmNotifierService.selectAlarmNotifierList(alarmNotifier);
        ExcelUtil<AlarmNotifier> util = new ExcelUtil<>(AlarmNotifier.class);
        util.exportExcel(response, list, "告警接收组数据");
    }
    /**
     * 获取告警接收组详细信息
     * sunshangeng
     */
    @ApiOperation(value = "获取告警接收组详细信息")
    @PreAuthorize("@ss.hasPermi('safetyWarning:AlarmNotifier:query')")
    @GetMapping(value = "/{id}")
    @ApiImplicitParam(name = "id",value = "告警接收组ID",required = true,paramType = "path",dataType = "long")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(alarmNotifierService.selectAlarmNotifierById(id));
    }
    /**
     * 新增告警接收组
     * sunshangeng
     */
    @ApiOperation(value = "新增告警接收组")
    @PreAuthorize("@ss.hasPermi('safetyWarning:AlarmNotifier:add')")
    @Log(title = "告警接收组", businessType = BusinessType.INSERT)
    @ApiOperationSupport(ignoreParameters = "id")
    @PostMapping
    public AjaxResult add(@Valid @RequestBody AlarmNotifier alarmNotifier)
    {
        return alarmNotifierService.insertAlarmNotifier(alarmNotifier);
    }

    /**
     * 修改告警接收组
     * sunshangeng
     */
    @ApiOperation(value = "修改告警接收组")
    @PreAuthorize("@ss.hasPermi('safetyWarning:AlarmNotifier:edit')")
    @Log(title = "告警接收组", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Valid @RequestBody AlarmNotifier alarmNotifier)
    {
        return alarmNotifierService.updateAlarmNotifier(alarmNotifier);
    }

    /**
     * 删除告警接收组
     * sunshangeng
     */
    @ApiOperation(value = "删除告警接收组")
    @PreAuthorize("@ss.hasPermi('safetyWarning:AlarmNotifier:remove')")
    @Log(title = "告警接收组", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    @ApiImplicitParam(name = "ids",value = "告警接收组ID集合(以逗号隔开例如 1,2)",required = true,allowMultiple = true,paramType = "path",dataType = "long")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        int resultRows = alarmNotifierService.deleteAlarmNotifierByIds(ids);
        if(resultRows==0){
            return  AjaxResult.error("删除失败，请核对信息");
        }
        return toAjax(resultRows);
    }

    /**
     * @Description 获取部门下的用户
     *
     * @author liuwenge
     * @date 2023/2/28 16:59
     * @param deptId 部门id
     * @return com.ruoyi.common.core.domain.AjaxResult
     */
    @ApiOperation(value = "获取部门下的用户")
    @PreAuthorize("@ss.hasPermi('safetyWarning:AlarmNotifier:getUserList')")
    @GetMapping("/getUserList")
    public AjaxResult getUserList(@RequestParam("deptId") @ApiParam(name = "deptId", value = "部门ID", required = true) String deptId)
    {
        return AjaxResult.success(alarmNotifierService.getUserList(deptId));
    }


}
