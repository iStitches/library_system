package com.library.demo.realm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.demo.entity.Permission;
import com.library.demo.entity.User;
import com.library.demo.service.IPermissionService;
import com.library.demo.service.IUserService;
import com.library.demo.shiro.JWTToken;
import com.library.demo.shiro.ProfileUser;
import com.library.demo.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.PrincipalMap;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserRealm extends AuthorizingRealm {
    @Autowired
    JWTUtils jwtUtils;
    @Autowired
    IUserService userService;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    IPermissionService permissionService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        ProfileUser profileUser=(ProfileUser)principalCollection.getPrimaryPrincipal();
        //这里  principalCollection存储的就是 doGetAuthenticationInfo存放进SimpleAuthenticationInfo的 ProfileUser对象
        String username = profileUser.getUsername();
        Map<String, Object> userPermission = permissionService.getUserPermission(username);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        List<Permission> list=(List<Permission>) userPermission.get("permissionList");
        list.forEach(x->{
            authorizationInfo.addStringPermission(x.getPermissionCode());
        });
        return authorizationInfo;
    }

    /**
     * 判断用户是否存在，账号状态是否可用
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        if(StringUtils.isEmpty(token))
            throw new AuthenticationException("token 不能为空");

        Claim claim=jwtUtils.getClaimByToken(token,"account");
        if(claim==null)
            throw new AuthenticationException("token 中账号为空");

        String account=claim.asString();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username",account);
        User user = userService.getOne(queryWrapper);
        if(user == null)
            throw new UnknownAccountException("用户不存在");
        else if(user.getStatus() == 2)
            throw new LockedAccountException("账号被锁定");

        //jwt在进入之前已经被处理好
       ProfileUser profileUser = new ProfileUser();
        BeanUtil.copyProperties(user,profileUser);
        log.info("profile-----{}",profileUser.toString());
        return new SimpleAuthenticationInfo(profileUser,token, ByteSource.Util.bytes(user.getSalt()),getName());
    }
}
