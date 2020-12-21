package com.library.demo.mapper;

import com.library.demo.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.demo.entity.User;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xjx
 * @since 2020-11-25
 */
public interface PermissionMapper extends BaseMapper<Permission> {
      //通过用户名查询权限
      User getPermission(String username);
}
