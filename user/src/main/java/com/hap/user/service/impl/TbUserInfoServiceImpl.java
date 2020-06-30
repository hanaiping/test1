package com.hap.user.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hap.common.center.modules.user.dao.TbUserInfoMapper;
import com.hap.common.center.modules.user.entity.TbUserInfo;
import com.hap.user.service.TbUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author aiPing
 * @since 2019-08-08
 */
@Service
@Slf4j
public class TbUserInfoServiceImpl extends ServiceImpl<TbUserInfoMapper, TbUserInfo> implements TbUserInfoService {


    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public TbUserInfo getUserInfo(Long userId) {
        //如果不存在，从数据库中查询
        TbUserInfo entity = new TbUserInfo();
        entity.setUserId(userId);
        return this.baseMapper.selectOne(entity);
    }


}
