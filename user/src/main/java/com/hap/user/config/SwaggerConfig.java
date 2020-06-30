package com.hap.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @description: swagger配置
 * @author: ladlee
 * @date: 2019-08-06 14:03
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("用户模块swagger接口文档")
                .apiInfo(new ApiInfoBuilder().title("用户模块swagger接口文档").description("用户相关接口文档")
                        .contact(new Contact("aiPing", "", "zkyhap@163.com")).version("1.0").build())
                .select().paths(PathSelectors.any()).build();
    }
}
