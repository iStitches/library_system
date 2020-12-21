package com.library.demo.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 自定义 JWTToken---封装
 */
public class JWTToken implements AuthenticationToken {
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
