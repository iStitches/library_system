package com.library.demo.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo.Exception.CustomException;
import com.library.demo.common.Result;
import com.library.demo.shiro.JWTToken;
import com.library.demo.utils.JWTUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.naming.AuthenticationException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JWTFilter extends BasicHttpAuthenticationFilter {
    @Autowired
    JWTUtils jwtUtils;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    //判断是否登录请求
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");
        return !StringUtils.isEmpty(authorization);
    }

    //拦截请求处理
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if(isLoginAttempt(request,response)){
            try {
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                String token = httpServletRequest.getHeader("Authorization");
                try {
                    jwtUtils.verify(token);
                } catch (Exception e) {
                    String msg=null;
                    Boolean flag=true;
                   if(e!=null && e instanceof SignatureVerificationException){
                       flag=false;
                       msg="token 认证失败，密匙不对"+e.getMessage();
                   }
                   else if(e!=null && e instanceof TokenExpiredException){
                        if((flag=this.refreshToken(request,response))==false){
                            msg="token 更新出现了问题"+e.getMessage();
                        }
                    }
                   else {
                       msg="其它问题"+e.getMessage();
                       flag=false;
                   }
                   if(!flag)
                   {
                       this.responseError(response,msg);
                       return false;
                   }
                }
                this.executeLogin(request, response);
            }catch (Exception e) {
                System.out.println("其余问题哈哈哈");
            }
        }
        return true;
    }

    //登录授权判断
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest=(HttpServletRequest)request;
        String authorization = httpServletRequest.getHeader("Authorization");
        JWTToken jwtToken=new JWTToken(authorization);
        this.getSubject(request,response).login(jwtToken);  //交给UserRealm处理，有错它会处理
        return true;
    }

    //处理失败
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        this.sendChallenge(request,response);
        return false;
    }

    //跨域处理
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers",
                httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    //刷新token
    public boolean refreshToken(ServletRequest request,ServletResponse response){
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String token=httpServletRequest.getHeader("Authorization");
        String account = jwtUtils.getClaimByToken(token, "account").asString();
        Long tokenCurrenttime=jwtUtils.getClaimByToken(token, "currentTime").asLong();
        if(redisTemplate.hasKey(account)) {
            Long refreshCurrenttime=(Long)redisTemplate.opsForValue().get(account);
            if(tokenCurrenttime.equals(refreshCurrenttime)){
                Long now=new Date().getTime();
                redisTemplate.opsForValue().set(account,now);
                redisTemplate.expire(account,jwtUtils.refreshExpiretime, TimeUnit.SECONDS);
                String newToken=jwtUtils.generateToken(account,now);
                httpServletResponse.setHeader("Authorization",newToken);
                httpServletResponse.setHeader("Access-Control-Expose-Headers","Authorization");
                return true;
            }
        }
        return false;
    }

    //JWTFilter抛出异常后的处理
    /**
     * 由于JWTFilter 的顶层是Filter过滤器，它会先于全局异常处理器执行，所以JWTFilter抛出的异常不能进行全局异常捕获
     */
    public void responseError(ServletResponse response,String msg){
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out=null;
        try {
            out=httpServletResponse.getWriter();
            Result failure = Result.fail(String.valueOf(HttpStatus.UNAUTHORIZED.value()), msg);
            ObjectMapper mapper=new ObjectMapper();
            String res = mapper.writeValueAsString(failure);
            out.append(res);
        } catch (IOException e) {
            throw new CustomException("返回给用户错误信息时出现异常"+e.getMessage());
        } finally {
            out.close();
        }
    }
}
