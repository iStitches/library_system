package com.library.demo.shiro;

import lombok.Data;

import java.io.Serializable;

/**
 * 认证通过后存储在SimpleAuthenticationInfo
 */
@Data
public class ProfileUser implements Serializable {
    private Integer id;
    private String username;
    private String password;
}
