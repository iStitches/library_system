package com.library.demo.service.impl;

import com.library.demo.entity.Permission;
import com.library.demo.entity.User;
import com.library.demo.mapper.PermissionMapper;
import com.library.demo.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xjx
 * @since 2020-11-25
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {
    @Autowired
    PermissionMapper permissionMapper;

    @Override
    public Map<String,Object> getUserPermission(String username) {
        HashMap<String, Object> res = new HashMap<>();
        User userPermission = permissionMapper.getPermission(username);

        res.put("nickname",userPermission.getNickname());
        res.put("roleId",userPermission.getRole().getId());
        res.put("roleName",userPermission.getRole().getRoleName());
        res.put("permissionList",userPermission.getRole().getPermissionList());
        return res;
    }
}
