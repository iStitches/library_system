package com.library.demo.utils;

import cn.hutool.core.codec.Base64Decoder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 工具类
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Slf4j
public class JWTUtils {
    public static Long expireTime;
    public static Long refreshExpiretime;
    private static String encrypt;

    @Value("${jwt.expireTime}")
    public void setExpireTime(Long expireTime){
        JWTUtils.expireTime=expireTime;
    }

    @Value("${jwt.refreshExpiretime}")
    public void setRefreshEExpiretime(Long refreshExpiretime){
        JWTUtils.refreshExpiretime=refreshExpiretime;
    }

    @Value("${jwt.encrypt}")
    public void setncrypt(String encrypt){
        JWTUtils.encrypt=encrypt;
    }

    //生成token
    public static String generateToken(String account,Long currenttime){
        String token = null;
        try {
            Date expire=new Date(currenttime+expireTime);
            String secret=account+Base64Util.decode(encrypt);
            token= JWT.create()
                    .withExpiresAt(expire)
                    .withClaim("account",account)
                    .withClaim("currentTime",currenttime)
                    .sign(Algorithm.HMAC256(secret));
            System.out.println(getClaimByToken(token, "currentTime").asLong());

        } catch (IllegalArgumentException e) {
            log.error("token生成错误----{}",e.getMessage());
        } catch (JWTCreationException e) {
            log.error("token生成错误----{}",e.getMessage());
        }
        return token;
    }

    //校验token
    public static boolean verify(String token){
        try {
            String secret=JWTUtils.getClaimByToken(token,"account").asString()+Base64Util.decodeThrowException(encrypt);
            JWTVerifier jwtVerifier=JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT verify = jwtVerifier.verify(token);
            return true;
        } catch (UnsupportedEncodingException e) {
            System.out.println("JWTToken认证解密出现UnsupportedEncodingException异常:" + e.getMessage());
        }
        return false;
    }

    //获取token中的数据
    public static Claim getClaimByToken(String token, String target){
        try {
            DecodedJWT decodedJWT=JWT.decode(token);
            return decodedJWT.getClaim(target);
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}
