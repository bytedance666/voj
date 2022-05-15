package com.simplefanc.voj.backend.dao.user.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simplefanc.voj.backend.common.constants.AccountConstant;
import com.simplefanc.voj.backend.common.constants.RoleEnum;
import com.simplefanc.voj.backend.common.utils.RedisUtil;
import com.simplefanc.voj.backend.dao.user.UserInfoEntityService;
import com.simplefanc.voj.backend.mapper.UserInfoMapper;
import com.simplefanc.voj.backend.pojo.dto.RegisterDto;
import com.simplefanc.voj.common.pojo.entity.user.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @Author: chenfan
 * @since 2020-10-23
 */
@Service
public class UserInfoEntityServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoEntityService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Boolean addUser(RegisterDto registerDto) {
        return userInfoMapper.addUser(registerDto) == 1;
    }

    @Override
    public List<String> getSuperAdminUidList() {
        String cacheKey = AccountConstant.SUPER_ADMIN_UID_LIST_CACHE;
        List<String> superAdminUidList = (List<String>) redisUtil.get(cacheKey);
        if (superAdminUidList == null) {
            superAdminUidList = userInfoMapper.getSuperAdminUidList(RoleEnum.ROOT.getId());
            redisUtil.set(cacheKey, superAdminUidList, 12 * 3600);
        }
        return superAdminUidList;
    }

}