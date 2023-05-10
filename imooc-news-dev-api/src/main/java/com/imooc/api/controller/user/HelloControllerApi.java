package com.imooc.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description 测试
 * @Api 2023-05-08-14-39
 * @Author qianzhikang
 */
@Api(value = "Controller标题",tags = {"xx功能"})
public interface HelloControllerApi {
    /**
     * api作为抽象接口层
     * 服务层进行实现
     */
    @ApiOperation(value = "hello方法的接口",notes = "hello方法接口",httpMethod = "GET")
    @GetMapping("/hello")
    public Object hello();

}
