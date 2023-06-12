package com.zc.license.service;


import com.zc.license.domain.SysLicenseInfo;

/**
 * License
 * @author Athena-xiepufeng
 */
public interface ISysLicenseService
{

    /**
     * 获取申请码
     * @return 申请吗
     */
    String getIdentityCode();

    /**
     * 用授权码授权
     * @param authCode 授权码
     * @return
     */
    Boolean authorization(String authCode);

    /**
     * 获取数据库授权码
     * @return 授权码
     */
    String getAuthCode();

    /**
     * 获取 license 信息
     * @return SysLicenseInfo
     */
    SysLicenseInfo getLicenseInfo();
}
