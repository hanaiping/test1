package com.hap.common.center.config;


import com.hap.common.center.filter.MarketFilter;
import com.hap.common.center.permission.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class BaseConfig extends WebMvcConfigurerAdapter {


    @Value("${fitlerUrl}")
    protected String filterUrl;

    @Value("${fitlerLoginEnable}")
    protected boolean fitlerLoginEnable;

    @Value("${fitlerStaticResource}")
    protected String fitlerStaticResource;

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;


    //login过滤器
    @Bean
    public FilterRegistrationBean loginFilter() {
        FilterRegistrationBean f = new FilterRegistrationBean(new MarketFilter(JwtHelper.APP_EXPIRE_TIME, filterUrl, fitlerStaticResource, fitlerLoginEnable, stringRedisTemplate));
        f.addUrlPatterns("/*");
        f.setName("marketFilter");
        return f;
    }


}
