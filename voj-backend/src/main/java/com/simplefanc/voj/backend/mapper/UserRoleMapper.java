package com.simplefanc.voj.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplefanc.voj.backend.pojo.vo.UserRolesVo;
import com.simplefanc.voj.common.pojo.entity.user.Role;
import com.simplefanc.voj.common.pojo.entity.user.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author: chenfan
 * @since 2021-10-23
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    UserRolesVo getUserRoles(@Param("uid") String uid, @Param("username") String username);

    List<Role> getRolesByUid(@Param("uid") String uid);

    IPage<UserRolesVo> getUserList(Page<UserRolesVo> page, @Param("limit") int limit,
                                   @Param("currentPage") int currentPage, @Param("keyword") String keyword,
                                   @Param("roleId") Long roleId, @Param("status") Integer status);

    IPage<UserRolesVo> getAdminUserList(Page<UserRolesVo> page,
                                        @Param("limit") int limit,
                                        @Param("currentPage") int currentPage,
                                        @Param("keyword") String keyword,
                                        @Param("roleIdList") List roleIdList);
}
