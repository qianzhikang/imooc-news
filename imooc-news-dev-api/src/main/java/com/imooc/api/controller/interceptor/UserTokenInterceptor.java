package com.imooc.api.controller.interceptor;

import com.imooc.api.controller.BaseController;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.utils.IPUtil;
import com.imooc.utils.RedisOperator;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 用户登陆拦截器
 * @Date 2023-05-09-09-24
 * @Author qianzhikang
 */

public class UserTokenInterceptor extends BaseInterceptor implements HandlerInterceptor {

    @Resource
    private RedisOperator redisOperator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String token = request.getHeader("headerUserToken");
        return verifyUserIdToken(userId,token,REDIS_USER_TOKEN);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
