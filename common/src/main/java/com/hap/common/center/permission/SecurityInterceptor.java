package com.hap.common.center.permission;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.hap.common.center.exception.ExceptionEnum;
import com.hap.common.center.exception.MyException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author jacy
 * @description 与拦截器结合使用 验证权限
 * @date 2019年9月7日
 * @since 1.0
 */
public class SecurityInterceptor implements HandlerInterceptor {

//    @Autowired
//    private AdminUserService adminUserService;

    //供应商角色ID
    private final String SUPPLIERTYPE ="15";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws MyException {

        try {
            String a= IoUtil.read(request.getInputStream(),"utf-8");
            System.out.println("as:"+a);
        } catch (IOException e) {
            e.printStackTrace();
        }



        // 验证权限
        if (this.hasPermission(handler)) {
            return true;
        }

        //  null == request.getHeader("x-requested-with") TODO 暂时用这个来判断是否为ajax请求
        // 如果没有权限 则抛403异常 springboot会处理，跳转到 /error/403 页面
//        response.sendError(HttpStatus.FORBIDDEN.value(), "无权限");
//        return false;
        throw new MyException(ExceptionEnum.NO_PERMISSION_OPTION);
    }

    /**
     * 是否有权限
     *
     * @param handler
     * @return
     */
    private boolean hasPermission(Object handler) {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取方法上的注解
            RequiredPermission requiredPermission = handlerMethod.getMethod().getAnnotation(RequiredPermission.class);
            // 如果方法上的注解为空 则获取类的注解
            if (requiredPermission == null) {
                requiredPermission = handlerMethod.getMethod().getDeclaringClass().getAnnotation(
                        RequiredPermission.class);
            }
            // 如果标记了注解，则判断权限
            if (requiredPermission != null && StrUtil.isNotBlank(requiredPermission.value())) {
                String role = JwtHelper.getRole();
                if(role==null){
                    throw new MyException(ExceptionEnum.MANAGER_TOKEN_ERROR);
                }
                //供应商认证通过则不做校验
                if(role.contains(SUPPLIERTYPE)){
                    return true;
                }
                // redis或数据库 中获取该用户的权限信息 并判断是否有权限
                String[] permissionSet = JwtHelper.getPermission().split(",");
                //TODO 取到用户的权限信息
//                adminUserService.getPermissionSet();
//                JwtHelper.getRole();
                if (permissionSet==null || permissionSet.length==0){
                    return false;
                }
                String controllerPermission =requiredPermission.value();
                return Arrays.stream(permissionSet).filter(o -> controllerPermission.equals(o)).count()>0;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {



        // TODO
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // TODO
    }
}
