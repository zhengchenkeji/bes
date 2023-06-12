package com.zc.common.core.license;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 自定义需要校验的License参数
 *
 * @author Athena-xiepufeng
 */
public class LicenseCheckParam implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 可被允许的IP地址
     */
    private List<String> ipAddress;

    /**
     * 可被允许的MAC地址
     */
    private List<String> macAddress;

    /**
     * 可被允许的CPU序列号
     */
    private String cpuSerial;

    /**
     * 可被允许的主板序列号
     */
    private String mainBoardSerial;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    public List<String> getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(List<String> ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<String> getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(List<String> macAddress) {
        this.macAddress = macAddress;
    }

    public String getCpuSerial() {
        return cpuSerial;
    }

    public void setCpuSerial(String cpuSerial) {
        this.cpuSerial = cpuSerial;
    }

    public String getMainBoardSerial() {
        return mainBoardSerial;
    }

    public void setMainBoardSerial(String mainBoardSerial) {
        this.mainBoardSerial = mainBoardSerial;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        ipAddress.forEach(ipAddress -> {
            stringBuilder.append(ipAddress);
            stringBuilder.append(",");
        });

        macAddress.forEach(macAddress -> {
            stringBuilder.append(macAddress);
            stringBuilder.append(",");
        });

        stringBuilder.append(cpuSerial);
        stringBuilder.append(",");

        stringBuilder.append(mainBoardSerial);
        stringBuilder.append(",");

        stringBuilder.append(startDate);

        if (endDate != null) {
            stringBuilder.append(",");
            stringBuilder.append(endDate);
        }

        return stringBuilder.toString();

    }
}
