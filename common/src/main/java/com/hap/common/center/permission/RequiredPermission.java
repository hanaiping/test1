package com.hap.common.center.permission;

import java.lang.annotation.*;

/**
 * @author jacy
 * @description 与拦截器结合使用 验证权限
 * @date 2019年9月7日
 * @since 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequiredPermission {
    String value();
}
