package com.hap.common.center.constant;

/**
 * @author robin
 * @date 2017/10/29
 */


public interface CommonConstant {
    /**
     * token请求头名称
     */
    String REQ_HEADER = "Authorization";

    /**
     * token分割符
     */
    String TOKEN_SPLIT = "Bearer ";

    /**
     * jwt签名
     */
    String SIGN_KEY = "PIG";
    /**
     * 删除
     */
    Integer STATUS_DEL = 1;
    /**
     * 正常
     * status
     */
    Integer STATUS_NORMAL = 0;

    /**
     * 消息已读
     */
    Integer IS_READ = 1;

    /**
     * 消息未读
     */
    Integer UN_READ = 0;

    /**
     * 锁定
     */
    Integer STATUS_LOCK = 9;

    /**
     * 菜单
     */
    Integer MENU = 0;

    /**
     * 按钮
     */
    Integer BUTTON = 1;

    /**
     * 删除标记
     */
    String DEL_FLAG = "del_flag";

    /**
     * 状态标记
     */
    String STATUS = "status";

    /**
     * 编码
     */
    String UTF8 = "UTF-8";

    /**
     * JSON 资源
     */
    String CONTENT_TYPE = "application/json; charset=utf-8";

    /**
     * 阿里大鱼
     */
    String ALIYUN_SMS = "aliyun_sms";

    /**
     * 路由信息Redis保存的key
     */
    String ROUTE_KEY = "_ROUTE_KEY";

    /**
     * 图床地址
     */
    String SM_MS_URL = "https://sm.ms/api/upload";

    public static final String HEADER_KEY = "access-token";
    public static final String HEADER_SERVICE_ID = "SERVICE-ID";



}
