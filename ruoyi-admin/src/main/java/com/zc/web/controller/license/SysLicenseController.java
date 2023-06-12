package com.zc.web.controller.license;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.zc.license.domain.SysLicenseInfo;
import com.zc.license.service.ISysLicenseService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * License
 * @author Athena-xiepufeng
 */
@RestController
@RequestMapping("/license")
public class SysLicenseController extends BaseController
{

    @Resource
    private ISysLicenseService iSysLicenseService;

    /**
     * 获取申请码
     */
    @GetMapping(value = "/identityCode")
    public AjaxResult getIdentityCode()
    {
        return AjaxResult.success(iSysLicenseService.getIdentityCode());
    }
    /**
     * 获取 license 信息
     */
    @GetMapping(value = "/licenseInfo")
    public AjaxResult getLicenseInfo()
    {
        return AjaxResult.success(iSysLicenseService.getLicenseInfo());
    }

    /**
     * 用授权码授权
     * @param sysLicenseInfo 授权码
     * @return
     */
    @PostMapping("/authentication")
    public AjaxResult authentication(@RequestBody SysLicenseInfo sysLicenseInfo)
    {
        return AjaxResult.success(iSysLicenseService.authorization(sysLicenseInfo.getAuthCode()));
    }

    /**
     * 用授权码授权(需要登录授权)
     * @param sysLicenseInfo 授权码
     * @return
     */
    @PostMapping("/authorize")
    public AjaxResult authorize(@RequestBody SysLicenseInfo sysLicenseInfo)
    {
        return AjaxResult.success(iSysLicenseService.authorization(sysLicenseInfo.getAuthCode()));
    }


}
