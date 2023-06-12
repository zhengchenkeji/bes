package com.zc.common.core.httpclient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zc.common.core.httpclient.constant.HttpMediaType;
import com.zc.common.core.httpclient.exception.HttpException;
import com.zc.common.core.httpclient.request.RequestParams;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Athena-xiepufeng
 * @function http 请求封装
 */
public class OkHttp
{
    private int timeOut = 5; // 默认超时时间 5 秒

    private static okhttp3.OkHttpClient okHttpClient;

    private String url;

    private RequestParams requestParams = new RequestParams();

    private Request.Builder requestBuilder;

    public OkHttp()
    {
        init();
    }

    public OkHttp(int timeOut)
    {
        this.timeOut = timeOut;
        init();
    }

    private void init()
    {
        if (okHttpClient == null)
        {
            okhttp3.OkHttpClient.Builder okHttpClientBuilder = new okhttp3.OkHttpClient.Builder();
            okHttpClientBuilder.hostnameVerifier((hostname, session) -> true);

            okHttpClientBuilder.connectTimeout(timeOut, TimeUnit.SECONDS); // 连接超时时间
            okHttpClientBuilder.readTimeout(timeOut, TimeUnit.SECONDS);
            okHttpClientBuilder.writeTimeout(timeOut, TimeUnit.SECONDS);
            okHttpClient = okHttpClientBuilder.build();
        }

        requestBuilder = new Request.Builder();
    }

    /**
     * 设置超时时间
     * @param time
     * @return
     */
    public OkHttp timeOut(int time)
    {
        if (time == 0) return this;


        return this;
    }


    public OkHttp url(String url) throws HttpException
    {
        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }

        this.url = url;

        return this;
    }

    public OkHttp data(RequestParams requestParams)
    {
        this.requestParams = requestParams;
        return this;
    }

    /**
     * 请求头信息
     * @return
     */
    public OkHttp headers(Map<String, String> headerMap)
    {
        if (headerMap == null || headerMap.isEmpty()) return this;

        headerMap.forEach((name, value) -> requestBuilder.header(name, value));

        return this;
    }

    /**
     * 请求 method：post（异步）
     * @param callback
     * @return
     * @throws HttpException
     */
    public OkHttp post(Callback callback) throws HttpException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }


        RequestBody body = RequestBody.create(HttpMediaType.JSON_TYPE, toJson(requestParams.params));

        Call call = okHttpClient.newCall(requestBuilder.url(url).post(body).build());
        call.enqueue(callback);

        return this;
    }

    /**
     * 请求 method：post（同步）
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public Response post() throws HttpException, IOException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }

        FormBody.Builder builder = new FormBody.Builder();
        Map<String, String> params1 = (Map<String, String>) requestParams.params.get("data");
                //遍历集合,map集合遍历方式

        for (String key : params1.keySet()) {
            builder.add(key, params1.get(key));
        }
//        RequestBody body = RequestBody.create(HttpMediaType.X_WWW_FORM_URLENCODED_TYPE, toJson(requestParams.params.get("data")));

        Call call = okHttpClient.newCall(requestBuilder.url(url).post(builder.build()).build());

        return call.execute();
    }

    /**
     * 请求 method：get（异步）
     * @param callback
     * @return
     * @throws HttpException
     */
    public OkHttp get(Callback callback) throws HttpException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }

        StringBuilder urlBuilder = new StringBuilder(url).append("?");

        if (requestParams != null)
        {
            requestParams.params.forEach((key, value) ->
            {
                urlBuilder.append(key).append("=").append(toJson(value)).append("&");

            });
        }

        Call call = okHttpClient.newCall(requestBuilder.url(urlBuilder.substring(0, urlBuilder.length() - 1)).get().build());
        call.enqueue(callback);

        return this;
    }

    public Response get() throws HttpException, IOException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }

        StringBuilder urlBuilder = new StringBuilder(url).append("?");

        if (requestParams != null)
        {
            requestParams.params.forEach((key, value) ->
            {
                urlBuilder.append(key).append("=").append(toJson(value)).append("&");

            });
        }

        Call call = okHttpClient.newCall(requestBuilder.url(urlBuilder.substring(0, urlBuilder.length() - 1)).get().build());

        return call.execute();
    }

    /**
     * 请求 method：put（异步）
     * @param callback
     * @return
     * @throws HttpException
     */
    public OkHttp put(Callback callback) throws HttpException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }


        RequestBody body = RequestBody.create(HttpMediaType.JSON_TYPE, toJson(requestParams.params));

        Call call = okHttpClient.newCall(requestBuilder.url(url).put(body).build());
        call.enqueue(callback);

        return this;
    }

    /**
     * 请求 method：put（同步）
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public Response put() throws HttpException, IOException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }


        RequestBody body = RequestBody.create(HttpMediaType.JSON_TYPE, toJson(requestParams.params));

        Call call = okHttpClient.newCall(requestBuilder.url(url).put(body).build());

        return call.execute();
    }

    /**
     * 请求 method：patch（异步）
     * @param callback
     * @return
     * @throws HttpException
     */
    public OkHttp patch(Callback callback) throws HttpException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }


        RequestBody body = RequestBody.create(HttpMediaType.JSON_TYPE, toJson(requestParams.params));

        Call call = okHttpClient.newCall(requestBuilder.url(url).patch(body).build());
        call.enqueue(callback);

        return this;
    }

    /**
     * 请求 method：patch（同步）
     * @return
     * @throws HttpException
     */
    public Response patch() throws HttpException, IOException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }

        RequestBody body = RequestBody.create(HttpMediaType.JSON_TYPE, toJson(requestParams.params));

        Call call = okHttpClient.newCall(requestBuilder.url(url).patch(body).build());

        return call.execute();
    }

    /**
     * 请求 method：delete（异步）
     * @param callback
     * @return
     * @throws HttpException
     */
    public OkHttp delete(Callback callback) throws HttpException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }


        RequestBody body = RequestBody.create(HttpMediaType.JSON_TYPE, toJson(requestParams.params));

        Call call = okHttpClient.newCall(requestBuilder.url(url).delete(body).build());
        call.enqueue(callback);

        return this;
    }

    /**
     * 请求 method：delete（同步）
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public Response delete() throws HttpException, IOException
    {

        if (url == null)
        {
            throw new HttpException("OkHttp: url 不存在！");
        }


        RequestBody body = RequestBody.create(HttpMediaType.JSON_TYPE, toJson(requestParams.params));

        Call call = okHttpClient.newCall(requestBuilder.url(url).delete(body).build());

        return call.execute();
    }

    /**
     *  文件请求（异步）
     * @param callback
     * @return
     * @throws HttpException
     */
    public OkHttp filePost(Callback callback) throws HttpException
    {

        if (url == null)
        {
            throw new HttpException("url 不存在！");
        }

        MultipartBody.Builder requestBody = new MultipartBody.Builder();

        requestBody.setType(MultipartBody.FORM);

        if (requestParams != null)
        {
            requestParams.params.forEach((key, value) ->
            {
                if (value instanceof File)
                {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(HttpMediaType.FILE_TYPE, (File) value));
                } else if (value instanceof String)
                {

                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(null, toJson(value)));
                }
            });
        }


        Call call = okHttpClient.newCall(requestBuilder.url(url).post(requestBody.build()).build());
        call.enqueue(callback);

        return this;
    }

    /**
     * 文件请求（同步）
     * @return
     * @throws HttpException
     * @throws IOException
     */
    public Response filePost() throws HttpException, IOException
    {

        if (url == null)
        {
            throw new HttpException("url 不存在！");
        }

        MultipartBody.Builder requestBody = new MultipartBody.Builder();

        requestBody.setType(MultipartBody.FORM);

        if (requestParams != null)
        {
            requestParams.params.forEach((key, value) ->
            {
                if (value instanceof File)
                {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(HttpMediaType.FILE_TYPE, (File) value));
                } else if (value instanceof String)
                {

                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(null, toJson(value)));
                }
            });
        }


        Call call = okHttpClient.newCall(requestBuilder.url(url).post(requestBody.build()).build());

        return call.execute();
    }

    private String toJson(Object object)
    {
        if (object == null)
        {
            return "";
        }

        if (object instanceof String)
        {
            return (String) object;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Object>()
        {
        }.getType();

        return gson.toJson(object, type);
    }

}