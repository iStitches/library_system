package com.library.demo.utils;

import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.UUID;

/**
 * 生成用户密码加密所用的盐、校验加密后的用户密码是否正确
 */
public class SaltUtils {
    //生成盐
    public static String getSalt(){
        String salt = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return salt;
    }

    //验证密码
    public static boolean judgePassword(String userPsw,String inputPsw,String salt,Integer iterations){
        Md5Hash md5Hash = new Md5Hash(inputPsw,salt,iterations);
        String md5Psw = md5Hash.toHex();
        return userPsw.equals(md5Psw);
    }
}
