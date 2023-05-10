package com.imooc.user.controller;

import com.imooc.api.controller.user.HelloControllerApi;
import com.imooc.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 测试
 * @Date 2023-05-08-14-39
 * @Author qianzhikang
 */
@RestController
public class HelloController implements HelloControllerApi {
    public Object hello(){
        return GraceJSONResult.ok();
    }
}
