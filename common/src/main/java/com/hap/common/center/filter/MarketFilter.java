package com.hap.common.center.filter;


import com.alibaba.fastjson.JSON;

import com.hap.common.center.exception.ExceptionEnum;
import com.hap.common.center.exception.MyException;
import com.hap.common.center.response.Response;
import com.hap.common.center.util.StringUtil;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MarketFilter extends BaseFilter {

    private Logger logger = LoggerFactory.getLogger(MarketFilter.class);

    public MarketFilter() {
    }

    private String[] fitlerStr;

    private String[] resources;

    private boolean strEnable;

    private PathMatcher pathMatcher = new AntPathMatcher();

    public MarketFilter(Long cookieDuration) {
        super(cookieDuration);
    }

    public MarketFilter(Long cookieDuration, String fitlerStrs, String resourcess, boolean strEnable, StringRedisTemplate stringRedisTemplate) {
        super(cookieDuration);
        this.fitlerStr = fitlerStrs.split(",");
        this.strEnable = strEnable;
        this.resources = resourcess.split(",");
        super.redisTemplate = stringRedisTemplate;
    }

    @Override
    protected String handleTerminal(HttpServletRequest request) {
        return APP_DEVICE;
    }

    @Override
    protected void goLogin(ServletRequest request, ServletResponse response, ExceptionEnum exceptionEnum) {
        Response my = new Response();
        my.code(exceptionEnum);
        HttpServletResponse res = (HttpServletResponse) response;
        res.setContentType(ContentType.APPLICATION_JSON.toString());
        res.setStatus(200);
        PrintWriter pw = null;
        try {
            pw = res.getWriter();
            pw.write(JSON.toJSONString(my));
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void goLogin(ServletRequest request, ServletResponse response) {
        goLogin(request, response, ExceptionEnum.NO_PERMISSION_OPTION);
//        R my = new R();
//        my.code(NO_PERMISSION_OPTION);
//        HttpServletResponse res= (HttpServletResponse) response;
//        res.setContentType(ContentType.APPLICATION_JSON.toString());
//        res.setStatus(500);
//        PrintWriter pw = null;
//        try {
//            pw = res.getWriter();
//            pw.write(JSON.toJSONString(my));
//            pw.flush();
//            pw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    // 重写shiro方法，shiro接入时再调整
//    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return doJwt(servletRequest, servletResponse);
    }

    //    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        goLogin(request, response);
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws MyException {
        HttpServletRequest res = (HttpServletRequest) request;
//         logger.debug("================= url:" + res.getRequestURI());
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = res.getRequestURI();
        //根据配置来做过滤
        try {
            //不开启过滤则放过所有请求
            if (!this.strEnable) {
                chain.doFilter(request, response);
                return;
            }
            for (String item : resources) {
                if (uri.endsWith(item) || pathMatcher.match(item, uri)) {
                    logger.debug("ignore url:{},  matcher:{}", uri, item);
                    chain.doFilter(request, response);
                    return;
                }
            }
            for (String item : fitlerStr) {
                if (res.getRequestURI().contains(item) || pathMatcher.match(item, uri)) {
                    logger.debug("ignore url:{},  matcher:{}", uri, item);
                    chain.doFilter(request, response);
                    return;
                }
            }
            boolean api = uri.contains("/api/");

            //排除直播前端接口
            boolean liveapi = uri.contains("/v1/api/live/");
            boolean payapi = uri.contains("/api/pay/");
            String serviceid = res.getHeader("SERVICE-ID");
            if (StringUtil.isNotNull(serviceid)) {
                logger.debug("ignore serviceid:{}", serviceid);
                chain.doFilter(request, response);
                return;
            }
//            if(!liveapi && !payapi){
//                if(api && StringUtil.isNull(serviceid)){
//                    logger.error(" 非法api请访问 liveapi:{},url:{}",liveapi,res.getRequestURI());
//                    goLogin(request,response,CustomExceptionEnum.API_ILLEGAL_OPTION);
//                    return;
//                }
//            }


            logger.debug("url jwt check ");
            if (doJwt(request, response)) {
                chain.doFilter(request, response);
                return;
            } else {
                //没权限 login
//                System.out.println("url not auth:" + res.getRequestURI());
                logger.error("url not auth：" + res.getRequestURI());
                goLogin(request, response);
            }

        } catch (Exception e) {
            logger.error("error: uri:{},erro:{}", uri, e);
            throw new MyException(e.getMessage(), e);
        } finally {
            logger.debug("end url:", uri);
        }
    }


}
