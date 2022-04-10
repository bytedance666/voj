package com.simplefanc.voj.service.admin.account.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.simplefanc.voj.common.exception.StatusAccessDeniedException;
import com.simplefanc.voj.common.exception.StatusFailException;
import com.simplefanc.voj.dao.user.SessionEntityService;
import com.simplefanc.voj.dao.user.UserRoleEntityService;
import com.simplefanc.voj.pojo.dto.LoginDto;
import com.simplefanc.voj.pojo.entity.user.Session;
import com.simplefanc.voj.pojo.vo.UserRolesVo;
import com.simplefanc.voj.service.admin.account.AdminAccountService;
import com.simplefanc.voj.utils.IpUtils;
import com.simplefanc.voj.utils.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author: chenfan
 * @Date: 2022/3/9 10:22
 * @Description:
 */

@Service
public class AdminAccountServiceImpl implements AdminAccountService {

    @Autowired
    private SessionEntityService sessionEntityService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRoleEntityService userRoleEntityService;

    @Override
    public Map<Object, Object> login(LoginDto loginDto) {
        UserRolesVo userRoles = userRoleEntityService.getUserRoles(null, loginDto.getUsername());

        if (userRoles == null) {
            throw new StatusFailException("用户名不存在");
        }

        if (!userRoles.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
            throw new StatusFailException("密码不正确");
        }

        if (userRoles.getStatus() != 0) {
            throw new StatusFailException("该账户已被封禁，请联系管理员进行处理！");
        }

        // 查询用户角色
        List<String> rolesList = new LinkedList<>();
        userRoles.getRoles().stream()
                .forEach(role -> rolesList.add(role.getRole()));

        // 超级管理员或管理员、题目管理员
        if (rolesList.contains("admin") || rolesList.contains("root") || rolesList.contains("problem_admin")) {
            String jwt = jwtUtils.generateToken(userRoles.getUid());

            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            HttpServletResponse response = servletRequestAttributes.getResponse();

            // 放到信息头部
            response.setHeader("Authorization", jwt);
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            // 会话记录
            sessionEntityService.save(new Session().setUid(userRoles.getUid())
                    .setIp(IpUtils.getUserIpAddr(request)).setUserAgent(request.getHeader("User-Agent")));
            // 异步检查是否异地登录
            sessionEntityService.checkRemoteLogin(userRoles.getUid());
            return MapUtil.builder()
                    .put("uid", userRoles.getUid())
                    .put("username", userRoles.getUsername())
                    .put("nickname", userRoles.getNickname())
                    .put("avatar", userRoles.getAvatar())
                    .put("email", userRoles.getEmail())
                    .put("number", userRoles.getNumber())
                    .put("school", userRoles.getSchool())
                    .put("course", userRoles.getCourse())
                    .put("signature", userRoles.getSignature())
                    .put("realname", userRoles.getRealname())
                    .put("roleList", rolesList)
                    .map();
        } else {
            throw new StatusAccessDeniedException("该账号并非管理员账号，无权登录！");
        }
    }

    @Override
    public void logout() {
        SecurityUtils.getSubject().logout();
    }
}