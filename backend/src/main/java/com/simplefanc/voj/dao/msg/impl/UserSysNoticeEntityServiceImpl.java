package com.simplefanc.voj.dao.msg.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.simplefanc.voj.dao.msg.UserSysNoticeEntityService;
import com.simplefanc.voj.mapper.UserSysNoticeMapper;
import com.simplefanc.voj.pojo.entity.msg.UserSysNotice;
import com.simplefanc.voj.pojo.vo.SysMsgVo;

import javax.annotation.Resource;

/**
 * @Author: chenfan
 * @Date: 2021/10/1 20:35
 * @Description:
 */
@Service
public class UserSysNoticeEntityServiceImpl extends ServiceImpl<UserSysNoticeMapper, UserSysNotice> implements UserSysNoticeEntityService {

    @Resource
    private UserSysNoticeMapper userSysNoticeMapper;

    @Override
    public IPage<SysMsgVo> getSysNotice(int limit, int currentPage, String uid) {
        Page<SysMsgVo> page = new Page<>(currentPage, limit);
        return userSysNoticeMapper.getSysOrMineNotice(page, uid, "Sys");
    }

    @Override
    public IPage<SysMsgVo> getMineNotice(int limit, int currentPage, String uid) {
        Page<SysMsgVo> page = new Page<>(currentPage, limit);
        return userSysNoticeMapper.getSysOrMineNotice(page, uid, "Mine");
    }

}