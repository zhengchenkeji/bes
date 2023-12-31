import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.sun.istack.NotNull;
import com.zc.common.core.httpclient.OkHttp;
import com.zc.common.core.httpclient.exception.HttpException;
import com.zc.common.core.httpclient.request.RequestParams;
import okhttp3.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Okhttp 测试
 */
public class OkhttpTest {

    /**
     * get 同步请求
     * @throws HttpException
     * @throws IOException
     */
    @Test
    public void testGetSync() throws HttpException, IOException {

        OkHttp okHttp = new OkHttp();

        RequestParams requestParams = new RequestParams();

        requestParams.put("java", "okhttp");

        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "text/html;charset=utf-8");

        Response response // 请求响应
                = okHttp
                .headers(headers) // 请求头，可省略
                .url("https://www.baidu.com/") // 请求地址
                .data(requestParams) // 请求参数
                .get(); // 请求方法

        System.out.println(response.body().string());
    }

    /**
     * get 异步请求
     * @throws HttpException
     * @throws IOException
     */
    @Test
    public void testGetAsync() throws HttpException, IOException {

        OkHttp okHttp = new OkHttp();

        okHttp.url("https://www.baidu.com/") // 请求地址
            .data(new RequestParams("java", "okhttp")) // 请求参数
            .get(new Callback() {

                // 请求失败回调
                @Override
                public void onFailure(Call call, IOException e) { }

                // 请求成功回调
                @Override
                public void onResponse(Call call, Response response) throws IOException { }
            }); // 请求方法

    }
    /**
     * post 同步请求
     * @throws HttpException
     * @throws IOException
     */
    @Test
    public void testPostSync() throws HttpException, IOException {

        OkHttp okHttp = new OkHttp();

        RequestParams requestParams = new RequestParams();

        Map<String, String> data = new HashMap<>();

        data.put("uName", "admin");
        data.put("uPwd","Hf@20201018");

        requestParams.params.put("data", data);

        RequestParams requestParams1 = new RequestParams();

        Map<String, String> data1 = new HashMap<>();

        data1.put("ammeterName", "NHCJ_15F_DB1");

        requestParams1.params.put("data", data1);
        Response requst = okHttp
//                .addHeader("Cookie",cookie)
                .url("http://10.168.56.87:8081/BESServer/issp/v1.0/login")
                .data(requestParams) // 请求参数
                .post();
        String re = requst.body().string();
        System.out.println(re);

        JSONObject object = JSONObject.parseObject(re);


        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie","JSESSIONID=" + object.get("token"));
        Response requst1 = okHttp
                .headers(headers)
//                .addHeader("Cookie",cookie)
                .url("http://10.168.56.87:8081/BESServer/api/public/ammeterRealTimeDataInfo")
                .data(requestParams1) // 请求参数
                .post();
        System.out.println(requst1.body().string());

        Response requst2 = okHttp
//                .addHeader("Cookie",cookie)
                .url("http://10.168.56.87:8081/BESServer/api/public/ammeterRealTimeDataInfo")
                .data(requestParams1) // 请求参数
                .post();
        System.out.println(requst2.body().string());



        RequestParams requestParams2 = new RequestParams();
        requestParams2.put("airconditioningUnitName","测试1");

        Response response // 请求响应
                = okHttp
                .url("http://10.168.56.87:8081/BESServer/api/v1.0/deviceFunctions") // 请求地址
                .data(requestParams2) // 请求参数
                .get(); // 请求方法

        System.out.println(response.body().string());


//        Response response // 请求响应
//                = okHttp
//                .url("http://10.168.56.87:8081/BESServer/issp/v1.0/login") // 请求地址
//                .data(requestParams) // 请求参数
//                .post1(); // 请求方法
//        System.out.println(response.body().string());
    }

    /**
     * 文件 post 异步请求
     * @throws HttpException
     * @throws IOException
     */
    @Test
    public void testFilePostAsync() throws HttpException, IOException {

        OkHttp okHttp = new OkHttp();
        RequestParams requestParams = new RequestParams();
        Map<String, String> data = new HashMap<>();
        data.put("java", "okhttp");
        requestParams.put("data", data);

        okHttp.url("https://www.baidu.com/") // 请求地址
            .data(requestParams) // 请求参数
            .post(new Callback() {
                // 请求失败回调
                @Override
                public void onFailure(Call call, IOException e) { }

                // 请求成功回调
                @Override
                public void onResponse(Call call, Response response) throws IOException { }
            }); // 请求方法

    }
    /**
     * 文件 post 同步请求
     * @throws HttpException
     * @throws IOException
     */
    @Test
    public void testFilePostSync() throws HttpException, IOException {

        OkHttp okHttp = new OkHttp();

        RequestParams requestParams = new RequestParams();

        requestParams.put("文件参数1", "1234");
        requestParams.put("文件1", new File("/file"));

        Response response // 请求响应
                = okHttp
                .url("https://www.baidu.com/") // 请求地址
                .data(requestParams) // 请求参数
                .filePost(); // 请求方法

        System.out.println(response.body().string());
    }

    /**
     * post 异步请求
     * @throws HttpException
     * @throws IOException
     */
    @Test
    public void testPostAsync() throws HttpException, IOException {

        OkHttp okHttp = new OkHttp();
        RequestParams requestParams = new RequestParams();
        requestParams.put("文件参数1", "1234");
        requestParams.put("文件1", new File("/file1"));
        requestParams.put("文件2", new File("/file2"));
        okHttp.url("https://www.baidu.com/") // 请求地址
            .data(requestParams) // 请求参数
            .filePost(new Callback() {
                // 请求失败回调
                @Override
                public void onFailure(Call call, IOException e) { }

                // 请求成功回调
                @Override
                public void onResponse(Call call, Response response) throws IOException { }
            }); // 请求方法

    }
}
