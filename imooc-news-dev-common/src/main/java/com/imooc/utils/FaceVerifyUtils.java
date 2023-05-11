package com.imooc.utils;

import cn.hutool.json.JSONUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.facebody.model.v20191230.CompareFaceRequest;
import com.aliyuncs.facebody.model.v20191230.CompareFaceResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.utils.Base64Helper;
import com.google.gson.Gson;
import com.imooc.enums.FaceVerifyType;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.extend.AliyunResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class FaceVerifyUtils {

    final static Logger logger = LoggerFactory.getLogger(FaceVerifyUtils.class);

    @Autowired
    private AliyunResource aliyunResource;

    /**
     * 使用base64图片对比人脸
     * @param imgA  base64图片A
     * @param imgB  base64图片B
     * @return 返回值参数字符串
     */
    public String contrastFaceBase64(String imgA, String imgB) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai",
                aliyunResource.getAccessKeyId(),
                aliyunResource.getAccessKeySecret());

        IAcsClient client = new DefaultAcsClient(profile);

        CompareFaceRequest request = new CompareFaceRequest();

        request.setImageDataA(imgA);
        request.setImageDataB(imgB);
        try {
            CompareFaceResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
            return new Gson().toJson(response);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
        return null;
    }


    /**
     * 使用base64图片对比人脸
     * @param urlA  图片A 链接
     * @param urlB  图片B 链接
     * @return 返回值参数字符串
     */
    public String contrastFaceUrl(String urlA, String urlB) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai",
                aliyunResource.getAccessKeyId(),
                aliyunResource.getAccessKeySecret());

        IAcsClient client = new DefaultAcsClient(profile);

        CompareFaceRequest request = new CompareFaceRequest();

        request.setImageURLA(urlA);
        request.setImageURLB(urlB);
        try {
            CompareFaceResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
            return new Gson().toJson(response);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
        return null;
    }


    /**
     * 解析返回数据，取出相似度和目标相似度（80）对比
     * @param json 调用服务返回的json串
     * @return 是否大于75的相似度
     */
    public boolean verifiedFace(String json){
        HashMap resultMap = JSONUtil.toBean(json, HashMap.class);
        String dataJson = resultMap.get("data").toString();
        HashMap dataMap = JSONUtil.toBean(dataJson, HashMap.class);
        String confidence = dataMap.get("confidence").toString();
        double source = Double.parseDouble(confidence);
        return source >= 75.00;
    }



}

