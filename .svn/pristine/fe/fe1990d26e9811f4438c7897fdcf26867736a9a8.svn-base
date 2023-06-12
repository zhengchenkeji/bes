package com.zc.license.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.zc.common.core.license.*;
import com.zc.license.domain.SysLicenseInfo;
import com.zc.license.mapper.SysLicenseServiceMapper;
import com.zc.license.service.ISysLicenseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * License
 *
 * @author Athena-xiepufeng
 */
@Service
public class SysLicenseServiceImpl implements ISysLicenseService {

    private static final Logger logger = LogManager.getLogger(SysLicenseServiceImpl.class);

    @Resource
    public SysLicenseServiceMapper sysLicenseServiceMapper;

    /**
     * 获取申请码
     *
     * @return 申请吗
     */
    @Override
    public String getIdentityCode() {

        String applicationCode;

        try {

            Gson gson = new Gson();
            Type type = new TypeToken<LicenseCheckParam>() {
            }.getType();

            String msg = gson.toJson(getServerInfos(), type);

            applicationCode = DESUtils.encrypt(msg);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("申请码获取失败", e);
            throw new ServiceException("申请码获取失败");
        }

        return applicationCode;
    }

    /**
     * 用授权码授权
     *
     * @param authCode 授权码
     * @return
     */
    @Override
    public Boolean authorization(String authCode) {

        if (authCode == null) {
            throw new ServiceException("授权码不能为空");
        }

        try {

            LicenseCheckParam outerLicenseCheckParam = decryptToLicenseCheckParam(authCode);

            LicenseCheckParam innerLicenseCheckParam = getServerInfos();

            for (String ipAddress : innerLicenseCheckParam.getIpAddress()) {
                if (!outerLicenseCheckParam.getIpAddress().contains(ipAddress)) {
                    return false;
                }
            }

            for (String macAddress : innerLicenseCheckParam.getMacAddress()) {
                if (!outerLicenseCheckParam.getMacAddress().contains(macAddress)) {
                    return false;
                }
            }

            if (!innerLicenseCheckParam.getCpuSerial().equals(outerLicenseCheckParam.getCpuSerial())) {
                return false;
            }

            if (!innerLicenseCheckParam.getMainBoardSerial().equals(outerLicenseCheckParam.getMainBoardSerial())) {
                return false;
            }

            String endDate = outerLicenseCheckParam.getEndDate();

            if (endDate == null) {
                return false;
            }

            if (getEffectiveDay(LocalDate.now().toString(), endDate) >= 1) {
                updateLicenseCode(authCode); // 更新授权码
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    /**
     * 获取授权码
     */
    @Override
    public String getAuthCode() {
        List<SysLicenseInfo> sysLicenseInfos = sysLicenseServiceMapper.selectCode();
        if (sysLicenseInfos != null && !sysLicenseInfos.isEmpty()) {
            return sysLicenseInfos.get(0).getAuthCode();
        }

        return null;
    }

    /**
     * 获取 license 信息
     *
     * @return SysLicenseInfo
     */
    @Override
    public SysLicenseInfo getLicenseInfo() {

        String authCode = getAuthCode();

        if (authCode == null) {
            throw new ServiceException("license 信息获取失败");
        }

        LicenseCheckParam licenseCheckParam;

        try {
            licenseCheckParam = decryptToLicenseCheckParam(authCode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("license 信息获取失败");
        }

        SysLicenseInfo sysLicenseInfo = new SysLicenseInfo();

        sysLicenseInfo.setAuthCode(authCode);
        sysLicenseInfo.setAuthStartDate(licenseCheckParam.getStartDate());
        sysLicenseInfo.setAuthEndDate(licenseCheckParam.getEndDate());
        sysLicenseInfo.setEffectiveDay(getEffectiveDay(LocalDate.now().toString(), licenseCheckParam.getEndDate()));


        Gson gson = new Gson();
        Type type = new TypeToken<LicenseCheckParam>() {
        }.getType();

        licenseCheckParam.setEndDate(null);
        licenseCheckParam.setStartDate(LocalDate.now().toString());

        String msg = gson.toJson(licenseCheckParam, type);

        String applicationCode;
        try {
            applicationCode = DESUtils.encrypt(msg);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("license 信息获取失败");
        }


        sysLicenseInfo.setIdentityCode(applicationCode);

        return sysLicenseInfo;
    }

    /**
     * 解码成 LicenseCheckParam 对象
     * @param authCode 授权码
     * @return
     */
    public LicenseCheckParam decryptToLicenseCheckParam(String authCode) throws Exception {

        if (authCode == null) {
            throw new ServiceException("授权码不能为空");
        }

        String decryptCode = DESUtils.decrypt(authCode);

        Type type = new TypeToken<LicenseCheckParam>() {
        }.getType();

        Gson gson = new Gson();

        return gson.fromJson(decryptCode, type);
    }

    /**
     * 获取有效天数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 天数
     */
    public int getEffectiveDay(String startDate, String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate startDateInclusive = LocalDate.parse(startDate, formatter);
        LocalDate endDateExclusive = LocalDate.parse(endDate, formatter);

        long days = endDateExclusive.toEpochDay() - startDateInclusive.toEpochDay();
        // Period period = Period.between(startDateInclusive, endDateExclusive);

        return (int)(days + 1);
    }

    /**
     * 保存授权码
     */
    private int saveLicenseCode(String code) {

        if (code == null) {
            throw new ServiceException("授权码不能为空");
        }

        return sysLicenseServiceMapper.insertCode(code);
    }

    /**
     * 更新授权码
     */
    private int updateLicenseCode(String code) {

        if (code == null) {
            throw new ServiceException("授权码不能为空");
        }

        String authCode = getAuthCode();

        if (authCode == null) {
            return saveLicenseCode(code);
        }

        List<SysLicenseInfo> sysLicenseInfos = sysLicenseServiceMapper.selectCode();
        if (sysLicenseInfos == null || sysLicenseInfos.isEmpty()) {
            return 0;
        }

        SysLicenseInfo sysLicenseInfo = sysLicenseInfos.get(0);
        sysLicenseInfo.setAuthCode(code);
        return sysLicenseServiceMapper.updateCode(sysLicenseInfo);

    }



    /**
     * 获取服务器硬件信息
     *
     * @return License参数
     */
    private static LicenseCheckParam getServerInfos() {

        String osName = System.getProperty("os.name").toLowerCase();

        AbstractServerInfos serverInfos;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            serverInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            serverInfos = new LinuxServerInfos();
        } else {//其他服务器类型
            serverInfos = new LinuxServerInfos();
        }

        return serverInfos.getServerInfos();
    }

}
