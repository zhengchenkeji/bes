package com.zc.config.Manager;

/**
 * @Author: wanghongjie
 * @Description:
 * @Date: Created in 9:56 2023/2/10
 * @Modified By:
 */

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 *httpClient的get请求方式
 * 使用GetMethod来访问一个URL对应的网页实现步骤：
 * 1.生成一个HttpClient对象并设置相应的参数；
 * 2.生成一个GetMethod对象并设置响应的参数；
 * 3.用HttpClient生成的对象来执行GetMethod生成的Get方法；
 * 4.处理响应状态码；
 * 5.若响应正常，处理HTTP响应内容；
 * 6.释放连接。
 * @author hsq
 */
public class HttpClientUtil {

    /**
     * @param url
     * @param charset
     * @return
     */
    public static String doGet(String url, String charset){
        /**
         * 1.生成HttpClient对象并设置参数
         */
        HttpClient httpClient = new HttpClient();
        //设置Http连接超时为5秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

        /**
         * 2.生成GetMethod对象并设置参数
         */
        GetMethod getMethod = new GetMethod(url);
        //设置get请求超时为5秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        //设置请求重试处理，用的是默认的重试处理：请求三次
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

        String response = "";

        /**
         * 3.执行HTTP GET 请求
         */
        try {
            int statusCode = httpClient.executeMethod(getMethod);

            /**
             * 4.判断访问的状态码
             */
            if (statusCode != HttpStatus.SC_OK){
                System.err.println("请求出错：" + getMethod.getStatusLine());
            }

            /**
             * 5.处理HTTP响应内容
             */
            //HTTP响应头部信息，这里简单打印
            Header[] headers = getMethod.getResponseHeaders();
            for (Header h: headers){
                System.out.println(h.getName() + "---------------" + h.getValue());
            }
            //读取HTTP响应内容，这里简单打印网页内容
            //读取为字节数组
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, charset);
            System.out.println("-----------response:" + response);
            //读取为InputStream，在网页内容数据量大时候推荐使用
            //InputStream response = getMethod.getResponseBodyAsStream();

        } catch (HttpException e) {
            //发生致命的异常，可能是协议不对或者返回的内容有问题
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e){
            //发生网络异常
            System.out.println("发生网络异常!");
        } finally {
            /**
             * 6.释放连接
             */
            getMethod.releaseConnection();
        }
        return response;
    }

    /**
     * post请求
     * @param url
     * @param json
     * @return
     */
    public static String doPost(String url,List<String> list, JSONObject json,String aa){
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);

        postMethod.addRequestHeader("accept", "*/*");
        postMethod.addRequestHeader("connection", "Keep-Alive");
        //设置json格式传送
        postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        if (!aa.equals("")) {
            postMethod.addRequestHeader("Cookie",aa);
        } else {

        }
        //必须设置下面这个Header
        postMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        //添加请求参数
        postMethod.addParameter(list.get(0), json.getString(list.get(0)));

        String res = "";
        try {
            int code = httpClient.executeMethod(postMethod);
            if (code == 200){
                res = postMethod.getResponseBodyAsString();
                System.out.println(res);
            } else {
               byte[] res1 = postMethod.getResponseBody();
                System.out.println(res1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String post11 (String url,RequestBody requestBody,String cookie) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appId","park_energy");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> paramMap = new HashMap<>();
//        paramMap.put("appId","park_energy");
        paramMap.put("ammeterName","NHCJ_15F_DB1");


        Request requst = new Request.Builder()
                .addHeader("Cookie",cookie)
                .url(url)
                .post(requestBody)
                .build();

        String result = client.newCall(requst).execute().body().string();
        System.out.println(result);
        return result;
    }


    public static void post12 (String url,RequestBody requestBody,String cookie) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request requst = new Request.Builder()
                .addHeader("Cookie",cookie)
                .url(url)
                .post(requestBody)
                .build();

//        String result = client.newCall(requst).execute().body().string();
        client.newCall(requst).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                System.out.println(responseData);
            }
        });
    }
    public static void main(String[] args) throws IOException {

        String aaa = null;
        RequestBody requestBody = new FormBody.Builder()
//                .add("appId","park_energy")
                .add("uName","admin")
                .add("uPwd","Hf@20201018")
                .build();
//        String aa = post11("http://111.14.222.51:9001/energy/issp/v1.0/login",requestBody,"");
        post12("http://10.168.56.87:8081/BESServer/issp/v1.0/login",requestBody,"");

//        JSONObject object = JSONObject.parseObject(aa);

//        aaa = "JSESSIONID=" + object.get("token");


        RequestBody requestBody1 = new FormBody.Builder()
                .add("ammeterName","NHCJ_15F_DB1,NHCJ_15F_DB2")
                .build();

        RequestBody requestBody2 = new FormBody.Builder()
                .build();

        RequestBody requestBody3 = new FormBody.Builder()
                .add("nhlx","01000")
                .add("sjkld","1")
                .add("time_end","2023-01-01 23:00:00")
                .add("time_start","2023-01-01 00:00:00")
                .add("zlbh","061,062")
                .build();

//        post11("http://111.14.222.51:9001/energy/api/public/ammeterRealTimeDataInfo",requestBody1,aaa);
//        post11("http://111.14.222.51:9001/energy/issp/v1.0/getCameraBaseTree",requestBody2,aaa);
//        post11("http://111.14.222.51:9001/energy/api/public/branchRoad/getBranchRoadEnergyData",requestBody3,aaa);

        RequestBody requestBody4 = new FormBody.Builder()
                .add("id","")
                .add("model","true")
                .add("point","true")
                .build();

        RequestBody requestBody5 = new FormBody.Builder()
                .add("f_name","11111")
                .build();

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("f_name","11111");

        JSONObject json = new JSONObject();
        json.put("f_name","11111");
        RequestBody requestBod6 = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), String.valueOf(json));
//        post11("http://10.168.56.87:8081/BESServer/api/public/schedulingInter/getScene",requestBody4,aaa);
//        post11("http://10.168.56.87:8081/BESServer/api/public/schedulingInter/addScene",requestBod6,aaa);
//        post12("http://10.168.56.87:8081/BESServer/api/public/schedulingInter/addScene",paramMap,aaa);
//        String url = "http://111.14.222.51:9001/energy/issp/v1.0/login";

//        client.newCall(requst).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                System.out.println(e);
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                System.out.println("onResponse: " + response.body().string());
//            }
//        });


//        Map<String,Object> user = new HashMap<>();
//        String aaa = "";
//        user.put("appId","park_energy");
////        JSONObject jsonObject = new JSONObject();
////        String s1 = JSONUtil.toJsonStr(user);
////        jsonObject.put("param",s1);
////        String postString = HttpClientUtil.doPost("http://111.14.222.51:9001/energy/issp/v1.0/login", jsonObject);
////        System.out.println("post方法:"+postString);
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("appId", "park_energy");
//        List<String> list0 = new ArrayList<>();
//        list0.add("appId");
////        Post("http://111.14.222.51:9001/energy/issp/v1.0/login", jsonObject);
//        String aa = doPost("http://111.14.222.51:9001/energy/issp/v1.0/login",list0, jsonObject,"");
//        JSONObject object = JSONObject.parseObject(aa);
//        aaa = "JSESSIONID=" + object.get("token");
//
////        String res = doPost("http://111.14.222.51:9001/energy/issp/v1.0/getCameraBaseTree", jsonObject,aaa);
//
//        JSONObject jsonObject1 = new JSONObject();
//        List<String> list1 = new ArrayList<>();
//        list1.add("ammeterName");
//        jsonObject1.put("ammeterName", "NHCJ_15F_DB1,NHCJ_15F_DB2");
//        String res1 = doPost("http://111.14.222.51:9001/energy/api/public/ammeterRealTimeDataInfo",list1, jsonObject1,aaa);
//
//        JSONObject jsonObject2 = new JSONObject();
//        List<String> list2 = new ArrayList<>();
//        list2.add("nhlx");
//        list2.add("sjkld");
//        list2.add("time_end");
//        list2.add("time_start");
//        list2.add("zlbh");
//        jsonObject2.put("nhlx", "01000");
//        jsonObject2.put("sjkld", "1");
//        jsonObject2.put("time_end", "2023-01-01 23:00:00");
//        jsonObject2.put("time_start", "2023-01-01 00:00:00");
//        jsonObject2.put("zlbh", "061");
//        String res2 = doPost("http://111.14.222.51:9001/energy/api/public/branchRoad/getBranchRoadEnergyData",list2, jsonObject2,aaa);
//        if (res1.equals("")) {
//            String aaaa = doPost("http://111.14.222.51:9001/energy/issp/v1.0/login", jsonObject,"");
//        }
//        doPost("http://111.14.222.51:9001/energy/issp/v1.0/login", jsonObject);
    }
}

