package com.imooc.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @Description 跨域配置
 * @Date 2023-05-09-08-57
 * @Author qianzhikang
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration config = new CorsConfiguration();
        // 1. 添加cors信息
        config.addAllowedOrigin("*");
        // 2. 发送cookie信息
        config.setAllowCredentials(true);
        // 3. 允许所有方式
        config.addAllowedMethod("*");
        // 4. 设置请求头
        config.addAllowedHeader("*");
        // 5. 匹配路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**",config);
        return new CorsFilter(corsSource);
    }
}
