package com.zc.common.core.msg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.common.ClientException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class alimsg {

    /**
     * 发送短信接口
     * @return
     */
    public static int sendSms() {
        String constOk = "OK";
        int result = 0;
        // 测试的参数
        SmsModel smsModel = getSmsData();
        // 判断手机号还是座机号
        String[] phone = smsModel.getPhoneNumbers().split(",");
        String[] phoneNames = smsModel.getPhoneName().split(",");
        String phoneNumbers = "";
        String phoneName = "";
        for (int i = 0; i < phone.length; i++) {
            if (phone[i].length() != 11 || phone[i].contains("-")) {
                continue;
            } else {
                phoneNumbers += phone[i] + ",";
                phoneName += phoneNames[i] + ",";
            }
            if ((i == phone.length - 1) && !"".equals(phoneNumbers)) {
                phoneNumbers = phoneNumbers.substring(0, phoneNumbers.length() - 1);
                phoneName = phoneName.substring(0, phoneName.length() - 1);
            }
        }
        if ("".equals(phoneNumbers)) {
            return 0;
        }
        // 发送短信
        DefaultProfile profile = DefaultProfile.getProfile(smsModel.getRegionId(), smsModel.getAccessKeyId(),
                smsModel.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", smsModel.getRegionId());
        // 手机号
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        // 短信签名名称
        request.putQueryParameter("SignName", smsModel.getSignName());
        // 短信模板ID
        request.putQueryParameter("TemplateCode", smsModel.getTemplateCode());
        // 短信模板变量对应的实际值，JSON格式
        request.putQueryParameter("TemplateParam", smsModel.getTemplateParam());
        try {
            CommonResponse response = client.getCommonResponse(request);
            String responseData = response.getData();
            JSONObject obj = (JSONObject) JSON.parse(responseData);
            String msg = (String) obj.get("Message");
            if (constOk.equals(msg)){
                System.out.println("发送成功");
            }
            result = 1;
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (com.aliyuncs.exceptions.ClientException e) {
            e.printStackTrace();
        }
        return result;

    }
    /**
     * 获取发送信息
     * @return xfyjSmsModel 短信对象
     */
    private static SmsModel getSmsData() {

        StringBuilder phoneNumbers = new StringBuilder();
        StringBuilder phoneName = new StringBuilder();
        StringBuilder canCall = new StringBuilder();
        phoneNumbers.append("17606181518");
        phoneName.append("长河");
        // 获取当前时间转String
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(new Date());
        SmsModel xfyjSmsModel = new SmsModel();
        xfyjSmsModel.setAccessKeyId("LTAI4FrhrPwc38KRqLKvpBcH");
        xfyjSmsModel.setAccessSecret("ikycWq1csVVgaWzah5ORagRhugReWG");
        xfyjSmsModel.setRegionId("cn-hangzhou");
        xfyjSmsModel.setSignName("山东正晨科技");
        xfyjSmsModel.setTemplateCode("SMS_228135693");
        xfyjSmsModel.setPhoneNumbers(phoneNumbers.toString());
        xfyjSmsModel.setPhoneName(phoneName.toString());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "谢朴峰大帅哥");
        jsonObject.addProperty("store", "后宫佳丽三千");
        jsonObject.addProperty("product", "更新了");
        xfyjSmsModel.setTemplateParam(jsonObject.toString());
        return xfyjSmsModel;
    }

    public static int singleCallByTts() {
        int result = 0;
        SmsModel smsModel = getSmsData();
        DefaultProfile profile = DefaultProfile.getProfile(smsModel.getRegionId(), smsModel.getAccessKeyId(),
                smsModel.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);
        if (smsModel.getPhoneNumbers() == null) {
            return result;
        }
        String[] phone = smsModel.getPhoneNumbers().split(",");
        String[] phoneName = smsModel.getPhoneName().split(",");
        for (int i = 0; i < phone.length; i++) {
            CommonRequest request = new CommonRequest();
            request.setMethod(MethodType.POST);
            request.setDomain("dyvmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SingleCallByTts");
            request.putQueryParameter("RegionId", smsModel.getRegionId());
            // 主叫号码
            request.putQueryParameter("CalledShowNumber", "051068584652");
            // 被叫号码
            request.putQueryParameter("CalledNumber", phone[i]);
            // 文本转语音ID
            request.putQueryParameter("TtsCode", "TTS_228135027");
            request.putQueryParameter("TtsParam", smsModel.getTemplateParam());
            // 语音通知的播放次数
            request.putQueryParameter("PlayTimes", "2");
            try {
                CommonResponse response = client.getCommonResponse(request);
                System.out.println(response.getData());
                String responseData = response.getData();
                JSONObject obj = (JSONObject) JSON.parse(responseData);
                String msg = (String) obj.get("Message");
                // 如果发送成功
                if ("OK".equals(msg)) {
                    // 保存CallId
                    String callId = (String) obj.get("CallId");
                    System.out.println("callId-" + callId + ",已发送语音成功");
                }
                result = 1;
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (com.aliyuncs.exceptions.ClientException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    public static void main(String[] args) {
        sendSms();
    }

}
