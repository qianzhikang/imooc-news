package com.imooc.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description 登陆注册传输类
 * @Date 2023-05-09-09-40
 * @Author qianzhikang
 */
@Data
public class RegistLoginBO {
    @NotBlank(message = "手机号不能为空！")
    private String mobile;
    @NotBlank(message = "短信验证码不能为空！")
    private String smsCode;
}
