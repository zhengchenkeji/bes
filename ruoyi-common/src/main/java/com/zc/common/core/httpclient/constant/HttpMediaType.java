package com.zc.common.core.httpclient.constant;


import okhttp3.MediaType;

/**
 * @author Athena-xiepufeng
 * @date 2020/12/29 16:05
 */
public class HttpMediaType
{

    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType X_WWW_FORM_URLENCODED_TYPE = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");

    public static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");
}
