package com.hap.common.center.constant;

/**
 * @author Administrator
 * @Title: RedisConstants
 * @ProjectName project
 * @Description:Resis常量
 * @date 2018/12/26 002610:33
 */
public class RedisConstants {
    /**
     * 登录认证
     */
    public static final String AUTHORIZATION = "Authorization";
    /**
     * 用户信息
     **/
    public static final String USER_CACHE_NAMESPACE = "U:USER:INFO:";

    /**
     * 用户信息Token
     **/
    public static final String USERTOKEN_CACHE_NAMESPACE = "U:USER:TOKEN:";

    /**
     * 获取当前登录用户信息
     */
    public static final String USERLOGIN_CACHE_NAMESPACE = "U:USER:CURR:";

}
