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
 * @Description 重复发送短信拦截
 * @Date 2023-05-09-09-24
 * @Author qianzhikang
 */

public class PassportInterceptor implements HandlerInterceptor {

    @Resource
    private RedisOperator redisOperator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 用户ip
        String userIp = IPUtil.getRequestIp(request);
        // 判断是否存在用户ip
        boolean keyIsExist = redisOperator.keyIsExist(BaseController.MOBILE_SMSCODE + ":" + userIp);
        if (keyIsExist) {
            // 抛异常，评率过高
            GraceException.display(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
            System.err.println("发送评率过高！" + userIp);
            return false;
        }
        return true;


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
