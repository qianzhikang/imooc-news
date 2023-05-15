package com.imooc.api.controller.interceptor;

import com.imooc.utils.IPUtil;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ArticleReadInterceptor extends BaseInterceptor implements HandlerInterceptor {

    @Resource
    private RedisOperator redisOperator;
    public static final String REDIS_ALREADY_READ = "redis_already_read";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String articleId = request.getParameter("articleId");

        String userIp = IPUtil.getRequestIp(request);
        boolean isExist = redisOperator.keyIsExist(REDIS_ALREADY_READ + ":" +  articleId + ":" + userIp);

        if (isExist) {
            return false;
        }

        return true;
    }
}
