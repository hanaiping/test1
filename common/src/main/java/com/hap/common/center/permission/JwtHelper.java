package com.hap.common.center.permission;

import com.hap.common.center.constant.JwtUser;
import com.hap.common.center.constant.RedisConstants;
import com.hap.common.center.dto.LoginUserDto;
import com.hap.common.center.exception.ExceptionEnum;
import com.hap.common.center.exception.MyException;
import com.hap.common.center.util.JsonUtils;
import com.hap.common.center.util.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtHelper {
    /**
     * cookie名
     */
    public static final String HEADER_KEY = "access-token";
    /**
     * 密钥
     */
    public static final String SECRET = "access-token";
    /**
     * 微信超时时间30分钟
     */
    public static final long WX_EXPIRE_TIME = 30 * 60 * 1000;
    /**
     * PC端超时时间3天
     */
    public static final long PC_EXPIRE_TIME = 3 *24 * 60 * 60 * 1000;

    /**
     * PC端临时登陆时间为3天
     */
    public static final long PC_TEMP_EXPIRE_TIME = 30 * 60 * 1000;

    /**
     * APP超时时间30天，不加L会默认为Integer，导致超出Integer的范围
     */
    public static final long APP_EXPIRE_TIME = 30 * 24 * 60 * 60 * 1000L;
    private static ThreadLocal<Long> userIdLocal = new ThreadLocal<Long>();
    private static ThreadLocal<String> roleLocal = new ThreadLocal<String>();
    private static ThreadLocal<String> permissionLocal = new ThreadLocal<String>();
    private static ThreadLocal<Long> operatorIdLocal = new ThreadLocal<>();
    private static ThreadLocal<JwtUser> userLocal=new ThreadLocal<>();


    public static void setUserId(Long userId) {
        userIdLocal.set(userId);
    }
    public static void setUser(JwtUser u) {
        userLocal.set(u);
    }

    public static Long getUserId()
    {
        Long userId =userIdLocal.get();
        if(userId==null){
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes!=null){
                HttpServletRequest request = attributes.getRequest();
                if(request!=null){
                    userId=getUserId(request);
                }
            }
        }
        return userId;
    }


    public static LoginUserDto getCurrentUser(RedisTemplate redisHelper){
        try{
             String json= (String) redisHelper.opsForValue().get(RedisConstants.USERLOGIN_CACHE_NAMESPACE.concat(getUserId().toString()));
             if(json!=null){
                 LoginUserDto loginUserDto= JsonUtils.fromJson(json,LoginUserDto.class);
                 return loginUserDto;
             }

        }catch (Exception e){
            log.error("get curr user LoginUserDto",e);
        }
        return null;
    }

    public static JwtUser getUser(){
        JwtUser user=userLocal.get();
        if(user==null){
            log.error("getUser");
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes!=null){
                HttpServletRequest request = attributes.getRequest();
                if(request!=null){
                    String authHeader = request.getHeader(JwtHelper.HEADER_KEY);
                    if(StringUtil.isNull(authHeader)){
                        log.error("authHeader is null ,请登录后重试");
                        throw new MyException(ExceptionEnum.NO_LOGIN_OPTION);
                    }
                    Claims claim = getClaimsFromToken(authHeader);
                     if(claim == null ||claim.get("userId") == null ){
                        throw new MyException(ExceptionEnum.NO_PERMISSION_OPTION);
                    }
//                    Long userId = Long.parseLong(claim.get("userId").toString()) ;
//                    String  role =  claim.get("role").toString() ;
//                    Object opermission=claim.get("permission");
//                    Object ostatus=claim.get("status");
//                    String  permission="";
//                    if(opermission!=null){
//                        permission=opermission.toString();
//                    }
//                    Integer status= null;
//                    if(ostatus!=null){
//                        status=Integer.parseInt(ostatus.toString());
//                    }
//                     Long operatorId = Long.parseLong(claim.get("operatorId").toString()) ;
//                    Long supplierId = Long.parseLong(claim.get("supplierId").toString()) ;
//                    user=JwtUser.builder().userId(userId).roles(role).status(status).empId(operatorId).supplierId(supplierId).build();

                     return converUser(claim);
                 }
            }
        }
        return user;
    }

    public static JwtUser converUser(Claims claim ){
        if(claim==null) {
            return null;
        }
        Long userId = Long.parseLong(claim.get("userId").toString()) ;
        String  role =  claim.get("role").toString() ;
        Object opermission=claim.get("permission");
        Object ooperatorId=claim.get("operatorId");
        Object osupplierId=claim.get("supplierId");
        Object ostatus=claim.get("status");
        String  permission="";
        if(opermission!=null){
            permission=opermission.toString();
        }
        Integer status= null;
        if(ostatus!=null){
            status=Integer.parseInt(ostatus.toString());
        }

        Long operatorId = null;
        if(ooperatorId!=null){
            operatorId=Long.parseLong(ooperatorId.toString());
        }
         Long supplierId =null;
        if(osupplierId!=null){
            supplierId= Long.parseLong(osupplierId.toString()) ;
        }
        JwtUser user=JwtUser.builder().userId(userId).roles(role).status(status).empId(operatorId).supplierId(supplierId).permission(permission).build();
        return user;
    }

    public static Long getUserId(HttpServletRequest request)
    {
        Long userId =userIdLocal.get();
        if(userId!=null && userId>0L){
            return userId;
        }
        //从请求头部获取jwt信息
        String authHeader = request.getHeader(JwtHelper.HEADER_KEY);
        if(StringUtil.isNull(authHeader)){
            throw new MyException(ExceptionEnum.NO_LOGIN_OPTION);
        }

        if(userId ==null || userId<0){
            Claims claim = getClaimsFromToken(authHeader);
            if(claim == null ||claim.get("userId") == null ){
                throw new MyException(ExceptionEnum.NO_PERMISSION_OPTION);
            }
            userId = Long.parseLong(claim.get("userId").toString()) ;
//            Integer status = (Integer) claim.get("status");
        }
        return userId;
    }

    public static Long getOperatorId(HttpServletRequest request)
    {
        Long operatorId =operatorIdLocal.get();
        if(operatorId!=null && operatorId>0L){
            return operatorId;
        }
        //从请求头部获取jwt信息
        String authHeader = request.getHeader(JwtHelper.HEADER_KEY);
        if(StringUtil.isNull(authHeader)){
            throw new MyException(ExceptionEnum.NO_LOGIN_OPTION);
        }
        if(operatorId ==null || operatorId<0){
            Claims claim = getClaimsFromToken(authHeader);
            operatorId = Long.parseLong(claim.get("operatorId").toString()) ;
            if(operatorId!=null){
                setOperatorId(operatorId);
            }
        }
        return operatorId;
    }

    public static void setPermission(String permission) {
        permissionLocal.set(permission);
    }

    public static String getPermission() {
        return permissionLocal.get();
    }

    public static Long getOperatorId() {
        Long operatorId= operatorIdLocal.get();
        if(operatorId==null){
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes!=null){
                HttpServletRequest request = attributes.getRequest();
                if(request!=null){
                    operatorId=getOperatorId(request);
                    if(operatorId!=null){
                        setOperatorId(operatorId);
                    }
                }
            }
        }

        return operatorId;
    }


    public static void removeUserId() {
        userIdLocal.remove();
    }

    public static void setRole(String role) {
        roleLocal.set(role);
    }

    public static void setOperatorId(Long operatorId) {
        operatorIdLocal.set(operatorId);
    }

    public static String getRole() {
        return roleLocal.get();
    }

    public static Integer getUserType() {
        String role = getRole();
        Integer userType = 1;

        return userType;
    }

    public static void removeRole() {
        roleLocal.remove();
    }

    public static String getJwt(HttpServletRequest request){
        //从请求头部获取jwt信息
        String authHeader = request.getHeader(JwtHelper.HEADER_KEY);
        //从url中获取jwt信息
        if(authHeader==null){
            authHeader=request.getParameter(JwtHelper.HEADER_KEY);
        }

        //从cookie中获取jwt信息
        if(authHeader==null){
            //去掉cookeis 从header中获取
//            Cookie[] cookies=request.getCookies();
//            if(cookies!=null) {
//                for (Cookie cookie : cookies) {
//                    if (JwtHelper.HEADER_KEY.equals(cookie.getName())) {
//                        authHeader = cookie.getValue();
//                        break;
//                    }
//                }
//            }
        }
        return authHeader;
    }

    public static String createJwt(Long userId, String role) {
        return createJwt(userId,role,0,WX_EXPIRE_TIME);
    }


    public static String createJwt(Long userId, String role ,long cookieDuration) {
        return createJwt(userId,role,0,cookieDuration);
    }

    /***
     * 获得jwt内容
     * @param token
     * @return
     */
    public static Claims getClaimsFromToken(String token) {
        Claims claim;
        try {
            claim = Jwts.parser()
                    .setSigningKey(JwtHelper.SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("解析token 失败,error token:"+token,e);
            claim = null;
        }
        return claim;
    }


    public static String createJwt(Long userId, String role,Integer status ,long cookieDuration) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("role", role);
        map.put("status", status);
        String authHeader = Jwts.builder()
                .setClaims(map)
//                    .setSubject(userId)
                //30分钟失效
                .setExpiration(new Date(System.currentTimeMillis() + cookieDuration))
                .signWith(SignatureAlgorithm.HS256, JwtHelper.SECRET)
                .compact();
        return authHeader;
    }


    public static String createJwt(Long userId,Long sellerId, String role,Integer status ,String permission,Long cookieDuration) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);//店主的用户id
        map.put("role", role);
        map.put("status", status);
        map.put("operatorId", sellerId);//当前店员的id
        map.put("permission", permission);
        String authHeader = Jwts.builder()
                .setClaims(map)
                //30分钟失效
                .setExpiration(new Date(System.currentTimeMillis() + cookieDuration))
                .signWith(SignatureAlgorithm.HS256, JwtHelper.SECRET)
                .compact();
        return authHeader;
    }

    public static String createJwt(JwtUser jwtUser) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", jwtUser.getUserId());//店主的用户id
        map.put("role", jwtUser.getRoles());
        map.put("status", jwtUser.getStatus());
        map.put("operatorId", jwtUser.getEmpId());//当前店员的id
        map.put("supplierId", jwtUser.getSupplierId());//供货商ID
        map.put("permission", jwtUser.getPermission());
        String authHeader = Jwts.builder()
                .setClaims(map)
                //30分钟失效
                .setExpiration(new Date(System.currentTimeMillis() + jwtUser.getCookieDuration()))
                .signWith(SignatureAlgorithm.HS256, JwtHelper.SECRET)
                .compact();
        return authHeader;
    }


    public static void main(String[] args) {
 //       String a=createJwt(4258, RoleType.DOCTOR_MISSINFO, APP_EXPIRE_TIME);
        //模拟生成用户jwt
        //String a=createJwt(2L,"1", APP_EXPIRE_TIME);
        //System.out.println(a);

        String s="eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiMSwxNSIsInN1cHBsaWVySWQiOjUwLCJwZXJtaXNzaW9uIjpudWxsLCJleHAiOjE1OTIwMTE5OTksInVzZXJJZCI6NDk3LCJvcGVyYXRvcklkIjo4NCwic3RhdHVzIjowfQ.g_GKGpAeLVtEwyAHG0ShVxuso1P9MMdaueZoWwhUCjk";
        Claims c=  JwtHelper.getClaimsFromToken(s);
        System.out.println("userId:"+c.get("userId"));
        System.out.println("supplierId:"+c.get("supplierId"));
//
        //普通用户
       //String a=createJwt(1L,"1", APP_EXPIRE_TIME);

        //供应商token
        //String a = createJwt(498L,357L, "1,11",0, "",JwtHelper.PC_EXPIRE_TIME);

        //System.out.println(a);
    }

    public static String getHeader(String key) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes!=null) {
            HttpServletRequest request = attributes.getRequest();
            return request.getHeader(key);
        }
        return null;
    }
}
