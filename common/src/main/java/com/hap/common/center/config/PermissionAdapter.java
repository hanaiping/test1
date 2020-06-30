package com.hap.common.center.config;

import com.hap.common.center.permission.SecurityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author jacy
 * @description 与拦截器结合使用 验证权限
 * @date 2019年9月7日
 * @since 1.0
 */
@Configuration
public class PermissionAdapter extends WebMvcConfigurerAdapter
{
    @Bean
    public SecurityInterceptor securityInterceptor() {
        return new SecurityInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor()).excludePathPatterns("/static/*")
                .excludePathPatterns("/error").addPathPatterns("/**");
    }
}