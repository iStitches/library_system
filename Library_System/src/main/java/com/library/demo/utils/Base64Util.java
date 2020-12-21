package com.library.demo.utils;



import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Base64 加密解密
 */
public class Base64Util {
     //解密
     public static String decode(String str){
         Base64.Decoder decoder = Base64.getDecoder();
         try {
             return new String(decoder.decode(str.getBytes("utf-8")));
         } catch (UnsupportedEncodingException e) {
             e.printStackTrace();
         }
         return null;
     }

     //解密
    public static String decodeThrowException(String str) throws UnsupportedEncodingException {
         Base64.Decoder decoder=Base64.getDecoder();
         return new String(decoder.decode(str.getBytes("utf-8")));
    }

    //加密
    public static String encode(String str){
         Base64.Encoder encoder=Base64.getEncoder();
        try {
            return new String(encoder.encode(str.getBytes("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //加密
    public static  String encodeThrowException(String str) throws UnsupportedEncodingException {
         Base64.Encoder encoder=Base64.getEncoder();
        String res = new String(encoder.encode(str.getBytes("utf-8")));
        return res;
    }
}
