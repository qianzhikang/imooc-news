package com.imooc.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.imooc.utils.extend.AliyunResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description 短信服务工具
 * @Date 2023-05-08-16-00
 * @Author qianzhikang
 */
@Component
public class SMSUtils {
    @Resource
    private AliyunResource aliyunResource;

    public void sendSMS(String mobile,String code){
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                aliyunResource.getAccessKeyId(),
                aliyunResource.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        /*签名*/
        request.setSignName("阿里云短信测试");
        /*短信模版码*/
        request.setTemplateCode("SMS_154950909");
        /*手机*/
        request.setPhoneNumbers(mobile);
        /*验证吗*/
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }
}
