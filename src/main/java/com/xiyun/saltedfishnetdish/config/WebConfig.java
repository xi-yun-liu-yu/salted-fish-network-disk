package com.xiyun.saltedfishnetdish.config;

import com.xiyun.saltedfishnetdish.Interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //放行登录与注册
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/api/users/login/{username}/{password}","/api/users/register/{username}/{password}");
    }
}
