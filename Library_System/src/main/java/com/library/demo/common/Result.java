package com.library.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一结果封装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {
    private String code; //状态码
    private String msg;   //响应信息
    private Object data;  //响应返回的数据

    //成功
    public static Result success(String msg,Object data){
        Result res=new Result();
        res.setCode(Constants.SUCCESS);
        res.setMsg(msg);
        res.setData(data);
        return res;
    }
    public static Result success(Object data){
        return success("响应成功",data);
    }
    public static Result success(String msg){
        return success(msg,null);
    }

    public static Result success(){
        return success(null);
    }

    //失败
    public static Result fail(String msg,Object data){
        Result res=new Result();
        res.setCode(Constants.SUCCESS);
        res.setMsg(msg);
        res.setData(data);
        return res;
    }
    public static Result fail(String code,String msg){
        Result res=new Result();
        res.setCode(code);
        res.setMsg(msg);
        return res;
    }
    public static Result fail(String msg){
        return fail(Constants.ERROR,msg);
    }
}
