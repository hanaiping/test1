package com.hap.user.service;

import com.baomidou.mybatisplus.service.IService;
import com.hap.common.center.modules.user.entity.TbUserInfo;


/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author aiPing
 * @since 2019-08-08
 */
public interface TbUserInfoService extends IService<TbUserInfo> {
    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @return
     */
    TbUserInfo getUserInfo(Long userId);


}
