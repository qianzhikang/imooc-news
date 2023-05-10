package com.imooc.api.controller.user;

import com.imooc.bo.RegistLoginBO;
import com.imooc.grace.result.GraceJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 用户登陆注册
 * @Date 2023-05-08-16-34
 * @Author qianzhikang
 */
@Api(value = "用户登陆注册",tags = {"用户登陆注册的controller"})
public interface PassPortControllerApi {
    @ApiOperation(value = "获取短信验证码",notes = "获取短信验证码")
    @GetMapping("/getSMSCode")
    GraceJSONResult getSMSCode(@RequestParam("mobile") String mobile, HttpServletRequest request);

    @ApiOperation(value = "登陆注册接口",notes = "登陆注册接口")
    @PostMapping("/doLogin")
    GraceJSONResult doLogin(@RequestBody RegistLoginBO registLoginBO, BindingResult result,HttpServletRequest request,
                            HttpServletResponse response);

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    GraceJSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response);
}
