import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.facebody.model.v20191230.CompareFaceRequest;
import com.aliyuncs.facebody.model.v20191230.CompareFaceResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.imooc.utils.FaceVerifyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;

/**
 * @Description TODO
 * @Date 2023-05-11-08-39
 * @Author qianzhikang
 */
public class TestAliyun {
    public static void main(String[] args) throws IOException {

    //    // 获取图片
    //    String pathA = "/Users/qianzhikang/Code/imooc-news-dev/temp_face/bot.png";
    //    String pathB = "/Users/qianzhikang/Code/imooc-news-dev/temp_face/test.png";
    //    byte[] bytesA = Files.readAllBytes(Paths.get(pathA));
    //    String imgA = Base64.getEncoder().encodeToString(bytesA);
    //    byte[] bytesB = Files.readAllBytes(Paths.get(pathB));
    //    String imgB = Base64.getEncoder().encodeToString(bytesB);
    //
    //    String accessKeyId = "LTAI5tPntpDwGoPNZpmYyHkc";
    //    String accessKeySecret = "lTAZAozufKNqWASYglfjfuGNPzEVn3";
    //    DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai",
    //            accessKeyId,
    //            accessKeySecret);
    //
    //    IAcsClient client = new DefaultAcsClient(profile);
    //
    //    CompareFaceRequest request = new CompareFaceRequest();
    //
    //    request.setImageDataA(imgA);
    //    request.setImageDataB(imgB);
    //    try {
    //        CompareFaceResponse response = client.getAcsResponse(request);
    //        System.out.println(new Gson().toJson(response));
    //    } catch (ServerException e) {
    //        e.printStackTrace();
    //    } catch (ClientException e) {
    //        System.out.println("ErrCode:" + e.getErrCode());
    //        System.out.println("ErrMsg:" + e.getErrMsg());
    //        System.out.println("RequestId:" + e.getRequestId());
    //    }

        String json = "{\"requestId\":\"D0559CDC-21C6-593B-8C66-5DE81371BA81\",\"data\":{\"confidence\":89.926315,\"qualityScoreA\":99.394356,\"qualityScoreB\":99.01318,\"isMaskA\":0,\"isMaskB\":0,\"thresholds\":[61.0,69.0,75.0],\"rectBList\":[156,102,146,176],\"rectAList\":[170,94,139,169],\"landmarksAList\":[206,140,275,138,240,175,218,218,266,217],\"landmarksBList\":[193,169,264,161,230,207,212,247,265,241]}}";
        HashMap hashMap = JSONUtil.toBean(json, HashMap.class);
        Object data = hashMap.get("data");
        HashMap hashMap1 = JSONUtil.toBean(data.toString(), HashMap.class);
        Double aDouble = Double.valueOf(hashMap1.get("confidence").toString());
        System.out.println(aDouble);
    }
}
