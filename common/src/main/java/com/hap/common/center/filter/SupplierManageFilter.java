package com.hap.common.center.filter;
import com.alibaba.fastjson.JSON;
import com.hap.common.center.constant.JwtUser;
import com.hap.common.center.constant.UserTypeConstant;
import com.hap.common.center.exception.ExceptionEnum;
import com.hap.common.center.exception.MyException;
import com.hap.common.center.permission.JwtHelper;
import com.hap.common.center.response.Response;
import com.hap.common.center.util.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SupplierManageFilter extends BaseFilter {

    private Logger logger = LoggerFactory.getLogger(SupplierManageFilter.class);

    public SupplierManageFilter() {
    }
    private String[] fitlerStr;
    private String[] resources;
    private boolean strEnable;


    public SupplierManageFilter(Long cookieDuration) {
        super(cookieDuration);
    }

    public SupplierManageFilter(Long cookieDuration, String fitlerStrs, String resourcess, boolean strEnable, StringRedisTemplate stringRedisTemplate) {
        super(cookieDuration);
        this.fitlerStr = fitlerStrs.split(",");
        this.strEnable = strEnable;
        this.resources = resourcess.split(",");
        super.redisTemplate=stringRedisTemplate;
    }

    @Override
    protected String handleTerminal(HttpServletRequest request) {
        return APP_DEVICE;
    }

    @Override
    protected void goLogin(ServletRequest request, ServletResponse response, ExceptionEnum exceptionEnum) {
        Response my = new Response();
        my.code(exceptionEnum);
        HttpServletResponse res= (HttpServletResponse) response;
        res.setContentType(ContentType.APPLICATION_JSON.toString());
        res.setStatus(500);
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
    protected void goLogin(ServletRequest request, ServletResponse response)    {
        goLogin(request,response,ExceptionEnum.NO_PERMISSION_OPTION);
    }




    //TODO 重写shiro方法，shiro接入时再调整
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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest res =(HttpServletRequest) request;
        System.out.println("============================ url:" + res.getRequestURI());
        logger.debug("url:" + res.getRequestURI());
        //过滤swagger
        if ((res.getRequestURI().contains("swagger") || res.getRequestURI().contains("api-docs") ||
                ((HttpServletRequest) request).getRequestURI().contains("actuator/health")
        ))
        {
            chain.doFilter(request,response);
            return;
        }
        //根据配置来做过滤
        try
        {
            //不开启过滤则放过所有请求
            if (!this.strEnable) {
                chain.doFilter(request,response);
                return;
            }
            for(String item : resources){
                if(res.getRequestURI().endsWith(item)){
                    chain.doFilter(request,response);
                    return;
                }
            }
            for(String item : fitlerStr){
                if(res.getRequestURI().contains(item)){
                    chain.doFilter(request,response);
                    return;
                }
            }
        }catch (Exception e){
            logger.error("error:",e);
            throw new MyException(e.getMessage(), e);
        }
        String serviceid=res.getHeader("SERVICE-ID");
        if(StringUtil.isNotNull(serviceid)){
            logger.debug("serviceid:{}",serviceid);
            chain.doFilter(request,response);
            return;
        }
        if(doJwt(request,response))
        {
            chain.doFilter(request,response);
            return;
        }else{
            //没权限 login
            System.out.println("url not auth:" + res.getRequestURI());
            logger.error("url not auth：" + res.getRequestURI());
            goLogin(request,response);
        }
    }

    @Override
    public boolean doJwt(ServletRequest req, ServletResponse res) throws MyException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        String authHeader = JwtHelper.getJwt(request);
        //jwt不存在，跳转到登录页
        if (authHeader == null) {
            String url=((HttpServletRequest) req).getRequestURI();
            logger.error("缺少token验证参数 url:{}",url);
            goLogin(request,response);
            return false;
        }else {
            //解析jwt
            Claims claim = null;
            try {
                claim = Jwts.parser()
                        .setSigningKey(JwtHelper.SECRET)
                        .parseClaimsJws(authHeader)
                        .getBody();
            } catch (ExpiredJwtException e) {
                logger.error("登录超时",e);
                goLogin(request,response);
                return false;
            } catch (IllegalArgumentException | MalformedJwtException e){
                logger.error("Jwt格式错误,header:{}",authHeader,e);
                goLogin(request,response);
                return false;
            }
            Integer status = (Integer) claim.get("status");
            if(status == 1){
                logger.error("用户状态异常 status:{}",status);
                goLogin(request,response);
                return false;
            }
            //获取userId，放入线程缓存中
            Long userId = Long.parseLong(claim.get("userId").toString()) ;
            Object roleo=claim.get("role");
            String role ="1";
            JwtUser.JwtUserBuilder jwtUserBuilder=JwtUser.builder();
            if(roleo!=null){
                role=roleo.toString();
            }else{
                logger.error("jwt role is error claim :{}",claim);
            }
            //管理员请求不做过滤
            if(UserTypeConstant.ADMIN.equals(roleo)){
                JwtHelper.setUserId(userId);
                return true;
            }

            Object operatorId=claim.get("operatorId");
            if(operatorId!=null){
                JwtHelper.setOperatorId(Long.parseLong(operatorId.toString()));
                jwtUserBuilder.userId(userId).empId(Long.parseLong(operatorId.toString()));
            }
            JwtHelper.setUserId(userId);
            JwtHelper.setRole(role);


            if(claim.get("permission")!=null){
                JwtHelper.setPermission(claim.get("permission").toString());
            }
            if(claim.get("permission")!=null){
                JwtHelper.setPermission(claim.get("supplie").toString());
            }
            String  permission =  claim.get("permission").toString() ;
            Long operatorId1 = Long.parseLong(claim.get("operatorId").toString()) ;
            Long supplierId = Long.parseLong(claim.get("supplierId").toString()) ;
            JwtUser user=JwtUser.builder().userId(userId).roles(role).status(status).empId(operatorId1).supplierId(supplierId).build();
            JwtHelper.setUser(user);



//            Map<String, String[]> map = new HashMap<>(req.getParameterMap());
            return true;
        }
    }

}
