package com.hap.common.center.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;

import com.hap.common.center.constant.JwtUser;
import com.hap.common.center.constant.RedisConstants;
import com.hap.common.center.constant.UserTypeConstant;
import com.hap.common.center.exception.ExceptionEnum;
import com.hap.common.center.exception.MyException;
import com.hap.common.center.permission.JwtHelper;
import com.hap.common.center.response.Response;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @author jacy
 * @date 2019年8月12日
 * 过滤器基础
 */
public abstract class BaseFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(BaseFilter.class);

    private String terminal = null;
    /**
     * 微信类型
     */
    public static String WX_DEVICE = "wechat";
    /**
     * app类型
     */
    public static String APP_DEVICE = "app";
    /**
     * 小程序类型
     */
    public static String MINI_DEVICE = "mini";
    /**
     * PC端类型
     */
    public static String PC_DEVICE = "pc";
    /**
     * 其他类型，未识别类型
     */
    public static String NOT_DEVICE = "notDevice";

    /**
     * Jwt 超时时间
     */
    private Long cookieDuration;

    protected StringRedisTemplate redisTemplate;


    public BaseFilter(Long cookieDuration) {
        this.cookieDuration = cookieDuration;
    }

    public BaseFilter() {
    }

    public boolean doJwt(ServletRequest req, ServletResponse res) throws MyException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        try {
//            String requestURI = request.getRequestURI();
//            String authHeader = request.getHeader(JwtHelper.HEADER_KEY);
//            Map<String, Object> map = new HashMap<>(request.getParameterMap());
//            logger.error("url:{},token:{},parameter:{}", requestURI,authHeader,map);
        	logger.info("requestURI:{}",request.getRequestURI());
		} catch (Exception e) {
			logger.error("Exception:{}", e.getMessage());
		}
        
        if (redisTemplate == null) {
            //       logger.info("redisTemplate=====null");
            redisTemplate = new StringRedisTemplate();
        }
        //   logger.info("redisTemplate====="+redisTemplate);

        String authHeader = JwtHelper.getJwt(request);
        try {
        	logger.info("token:{}",authHeader);
		} catch (Exception e) {
			logger.warn("Exception:{}",e.getMessage());
		}        
        //   logger.info("authHeader====="+authHeader);

        //   logger.info("request===getHeaders==acctss_token======="+request.getHeader("acctss_token"));

        //jwt不存在，跳转到登录页
        if (authHeader == null) {
            String url = ((HttpServletRequest) req).getRequestURI();
            logger.error("缺少token验证参数 url:{}", url);
            goLogin(request, response);
            return false;
        } else {
            //解析jwt
            Claims claim = resolve(authHeader);
            if (claim == null) {
                logger.error("claim is null authHeader:{}", authHeader);
                goLogin(request, response);
                return false;
            }
            //获取userId，放入线程缓存中
            Object uid = claim.get("userId");
            //     logger.error("uid====="+uid);
            if (uid == null) {
                logger.error("doJwt error:{}", claim);
                goLogin(request, response);
                return false;
            }
            Long userId = Long.parseLong(uid.toString());
            logger.info("userId=====" + userId);
            Object roleo = claim.get("role");
            //管理员请求不做过滤
            if (UserTypeConstant.ADMIN.equals(roleo)) {
                JwtHelper.setUserId(userId);
                return true;
            }
            //取到userId后匹配redis 中的最新token 来进行判断
            String keys = String.format("%s:%s", RedisConstants.USERTOKEN_CACHE_NAMESPACE, userId);
            String redisToken = redisTemplate.opsForValue().get(keys);

            if (StrUtil.isEmpty(redisToken)) {
                logger.error("用户token无效 redisToken is null :{}", claim);
                goLogin(request, response);
                return false;
            }
            //解析redis中的token
            claim = resolve(redisToken);

            //       logger.info("解析redis中的token==claim==="+claim);

            if (claim == null) {
                logger.error("解析redis中的token==claim===:{}", redisToken);
                goLogin(request, response);
                return false;
            }
            Integer status = (Integer) claim.get("status");
            if (status == 1) {
                logger.error("用户状态异常 status:{}", status);
                //                throw new MyException("用户状态异常");
                goLogin(request, response);
                return false;
            }

            String role = "1";// String.valueOf(roleo);
            if (roleo != null) {
                role = roleo.toString();
            } else {
                logger.error("jwt role is error claim :{}", claim);
            }
            JwtHelper.setUserId(userId);
            JwtHelper.setRole(role);
            Long operatorId = null;
            if (claim.get("operatorId") != null) {
                operatorId = Long.parseLong(claim.get("operatorId").toString());
            }

            Long supplierId = null;
            if (claim.get("supplierId") != null) {
                supplierId = Long.parseLong(claim.get("supplierId").toString());
            }
            JwtUser jwtHelper = JwtUser.builder().userId(userId).roles(role).status(status).empId(operatorId).supplierId(supplierId).build();
            JwtHelper.setUser(jwtHelper);
            //  log.error("user1:{}",user);
            // userLocal.set(user);

            //shiro 验证后期加入
//            StatelessToken token = new StatelessToken(userId.toString(), map, userId.toString());
//            getSubject(req, res).login(token);

            //大于超时时间的一半，更新jwt，重设超时时间
            Date expirationDate = claim.getExpiration();
            //默认30天
            this.cookieDuration = (this.cookieDuration == null || this.cookieDuration < 0) ? 30 * (60 * 1000 * 60 * 24) : this.cookieDuration;

            //     logger.info("expirationDate==="+expirationDate);

            if (expirationDate == null) {
                logger.error("过期时间不能为空，请重新登录 authHeader:{}", authHeader);
                goLogin(request, response, ExceptionEnum.NO_LOGIN_OPTION);
                return false;
            }

            if (expirationDate.getTime() - System.currentTimeMillis() < 0) {
                logger.error("过期时间不能为空，请重新登录 authHeader:{},expirationDate:{},cookieDuration:{}", authHeader, expirationDate, this.cookieDuration);
                goLogin(request, response, ExceptionEnum.NO_LOGIN_OPTION);
                return false;
            }

            if (expirationDate != null && (expirationDate.getTime() - System.currentTimeMillis()) < (this.cookieDuration / 2)) {
                authHeader = JwtHelper.createJwt(userId, role, this.cookieDuration);
                response.addHeader(JwtHelper.HEADER_KEY, authHeader);
                Cookie cookie = new Cookie(JwtHelper.HEADER_KEY, authHeader);
                cookie.setMaxAge((int) (cookieDuration / 1000));
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            return true;
        }
    }

    protected Claims resolve(String authHeader) {
        //解析jwt
        Claims claim = null;
        try {
            claim = Jwts.parser()
                    .setSigningKey(JwtHelper.SECRET)
                    .parseClaimsJws(authHeader)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("登录超时:header:{}", authHeader, e);
            System.out.println("登录超时");
//            goLogin(request,response);
            return null;
            // JWT为空抛IllegalArgumentException异常，解析错误抛MalformedJwtException异常
        } catch (IllegalArgumentException | MalformedJwtException e) {
            logger.error("Jwt格式错误,header:{}", authHeader, e);
//            goLogin(request,response);
            return null;
        }
        return claim;
    }

    /**
     * 判断请求来自哪个端的逻辑
     */
    protected String getTerminal(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        //微信端来的请求
        if (userAgent.contains("MicroMessenger")) {
            if ("MiniProgram".equals(request.getHeader("imfrom"))) {
                this.terminal = MINI_DEVICE;
            } else {
                this.terminal = WX_DEVICE;
            }
            //移动端来的请求
        } else if (userAgent.contains("Android") || userAgent.contains("iPhone")) {
            this.terminal = APP_DEVICE;
            //PC端来的请求
        } else if (userAgent.contains("Windows") || userAgent.contains("Mac OS")) {
            this.terminal = PC_DEVICE;
        } else {
            this.terminal = NOT_DEVICE;
        }
        return this.terminal;
    }

    /**
     * 返回拦截器处理的端类型
     *
     * @param request
     * @return
     */
    protected abstract String handleTerminal(HttpServletRequest request);

    /**
     * 跳转到登录页
     */
    protected abstract void goLogin(ServletRequest request, ServletResponse response, ExceptionEnum exceptionEnum) throws MyException;

    protected abstract void goLogin(ServletRequest request, ServletResponse response) throws MyException;


    @Override
    public void destroy() {
        //按照阿里的规范，threadLocal对象必须要手动回收
        JwtHelper.removeUserId();
        JwtHelper.removeRole();
    }

    protected void doRedirect(HttpServletRequest request, HttpServletResponse response, String loginUrl) throws MyException {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.setContentType("text/html;charset=UTF-8");
            Response my = new Response();
            my.setCode(302);
            my.setData(loginUrl);
            PrintWriter pw = null;
            try {
                pw = response.getWriter();
            } catch (IOException e) {
                throw new MyException(ExceptionEnum.SERVER_EXCEPTION);
            }
            pw.write(JSON.toJSONString(my));
            pw.flush();
            pw.close();
            //页面请求
        } else {
            try {
                response.sendRedirect(loginUrl);
            } catch (IOException e) {
                logger.info("发生异常", e);
                e.printStackTrace();

                Response my = new Response();
                my.setCode(302);
                my.setData(loginUrl);
                PrintWriter pw = null;
                try {
                    pw = response.getWriter();
                } catch (IOException ex) {
                    logger.info("发生异常", e);
                }

                pw.write(JSON.toJSONString(my));
                pw.flush();
                pw.close();

                //throw new MyException(CustomExceptionEnum.SERVER_EXCEPTION);
            }
        }
    }
}
