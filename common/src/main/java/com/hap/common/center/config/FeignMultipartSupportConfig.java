package com.hap.common.center.config;


import com.hap.common.center.constant.CommonConstant;
import com.hap.common.center.util.JsonUtils;
import com.hap.common.center.util.StringUtil;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.RequestInterceptor;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

@Configuration
public class FeignMultipartSupportConfig {

    private Logger logger= LoggerFactory.getLogger(FeignMultipartSupportConfig.class);


    @Bean
    public FeignHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
        return new FeignHystrixConcurrencyStrategy();
    }

    private static final String HEADER_KEY = "access-token";

    @Bean
    public RequestInterceptor headerInterceptor() {
        return template -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes!=null){
                HttpServletRequest request = attributes.getRequest();
                Enumeration<String> headerNames = request.getHeaderNames();
                String values=request.getHeader(CommonConstant.HEADER_KEY);
                if(values!=null){
                    template.header(CommonConstant.HEADER_KEY,values);
                }
            }
            template.header(CommonConstant.HEADER_SERVICE_ID,CommonConstant.HEADER_SERVICE_ID);
        };
    }

    @Bean
    public ErrorDecoder errorDecoder(){
          return new UserErrorDecoder();
    }

    class UserErrorDecoder implements ErrorDecoder {
        private Logger logger= LoggerFactory.getLogger(UserErrorDecoder.class);
        @Override
        public Exception decode(String methodKey, Response response) {
            Exception exception=null;
            try{
                String json= Util.toString(response.body().asReader());
                logger.error("feign erorr:{}",json);
                exception=new RuntimeException(json);
                Map<String,Object> result= JsonUtils.fromJson(json, Map.class);
                Integer code= MapUtils.getInteger(result,"status");
                if(code==null){
                    code= MapUtils.getInteger(result,"code");
                }
                String message=MapUtils.getString(result,"message");
                if(StringUtil.isNull(message)){
                    message=message=MapUtils.getString(result,"msg");
                }
                String path=MapUtils.getString(result,"path");
                com.hap.common.center.response.Response r= JsonUtils.fromJson(json, com.hap.common.center.response.Response.class);
                if(code!=200){
//                         exception=new RuntimeException(message);
//                         exception = new RrkException(message);
                    exception = new HystrixBadRequestException(message);
                }
            }catch (IOException e){
                logger.error(e.getMessage(),e);
            }

            return exception;
        }
    }
}
