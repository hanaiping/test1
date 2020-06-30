package com.hap.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Api(value="用户模块",tags = "用户模块")
public class UserController {

    @GetMapping("/get")
    @ApiOperation("测试demo")
    public String getDemo() {
        return "hello";
    }
}
