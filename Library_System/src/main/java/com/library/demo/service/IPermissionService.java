package com.library.demo.service;

import com.library.demo.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xjx
 * @since 2020-11-25
 */
public interface IPermissionService extends IService<Permission> {
     Map<String,Object> getUserPermission(String username);
}
