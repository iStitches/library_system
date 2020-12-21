package com.library.demo.controller;


import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.demo.common.Result;
import com.library.demo.dto.LoginDto;
import com.library.demo.entity.Orders;
import com.library.demo.entity.User;
import com.library.demo.service.IOrdersService;
import com.library.demo.service.IPermissionService;
import com.library.demo.service.IUserService;
import com.library.demo.utils.JWTUtils;
import com.library.demo.utils.SaltUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xjx
 * @since 2020-11-22
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userService;
    @Autowired
    IPermissionService permissionService;
    @Autowired
    IOrdersService ordersService;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    JWTUtils jwtUtils;

    //登录
    @PostMapping("/login")
    public Result userLogin(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response){
        String username = loginDto.getUsername();
        QueryWrapper query=new QueryWrapper<User>().eq("username",username);
        User one = userService.getOne(query);
        Assert.notNull(one,"用户不存在！");

        if(SaltUtils.judgePassword(one.getPassword(),loginDto.getPassword(),one.getSalt(),20)){
            Long currenttime=new Date().getTime();
            String token = jwtUtils.generateToken(username, currenttime);
            //存储用户信息、用户的refreshToken 进redis
            redisTemplate.opsForValue().set(username,currenttime);
            redisTemplate.expire(username,jwtUtils.refreshExpiretime, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(one.getId().toString(),one);
            response.setHeader("Authorization",token);
            response.setHeader("Access-Control-Expose-Headers","Authorization");

            one.setPassword(null);
            one.setSalt(null);
            return Result.success("用户登录成功",one);
        }
        else
            return Result.fail("用户名或密码错误");
    }

    //注册
    @PostMapping("/register")
    public Result userRegist(@Validated @RequestBody User user){
        User one = userService.getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if(one!=null)
            return Result.fail("当前用户已经存在！");
        //用户密码加密
        String salt = SaltUtils.getSalt();
        Md5Hash md5Hash=new Md5Hash(user.getPassword(),salt,20);
        user.setPassword(md5Hash.toHex());
        user.setStatus(1);
        user.setSalt(salt);
        boolean flag = userService.save(user);
        if(flag){
            return Result.success("注册成功");
        }else{
            return Result.fail("注册失败请重试");
        }
    }

    //用户退出----更新上次登录时间
    @GetMapping("/logout")
    public Result userLogout(@RequestParam(value = "userId")Integer userId ){
        User user = (User) redisTemplate.opsForValue().get(userId.toString());
        Long currentTime =(Long) redisTemplate.opsForValue().get(user.getUsername());
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("last_time",new Date(currentTime)).eq("id",userId);
        userService.update(user, wrapper);
        //清空缓存信息
        redisTemplate.delete(userId.toString());
        redisTemplate.delete(user.getUsername());
        return Result.success("成功退出");
    }

    //更新个人信息
    @RequiresAuthentication
    @PostMapping("/edit")
    public Result userEdit(@RequestBody User user){
        Integer id=user.getId();
        User one= (User)redisTemplate.opsForValue().get(id.toString());  //当前登录的对象
        if(one==null)
            return Result.fail("用户不存在！");
        else if(!user.getUsername().equals(one.getUsername()))
            return Result.fail("非法操作！");
        String salt=SaltUtils.getSalt();
        Md5Hash hash=new Md5Hash(user.getPassword(),salt,20);
        user.setPassword(hash.toHex());
        boolean flag=userService.updateById(user);
        if(flag)
            return Result.success("更新成功");
        else
            return Result.fail("更新失败");
    }

    //用户查询个人信息
    @RequiresAuthentication
    @GetMapping("/getPersonInfo")
    public Result getPersonInfo(@RequestParam(required = true,value = "id")Integer id,
                                @RequestParam(required = true,value = "username")String username){
        User one = (User)redisTemplate.opsForValue().get(id.toString());   //缓存中的当前用户
        if(!one.getUsername().equals(username))
            throw new AuthorizationException("没有查询权限！");
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("id",id);
        if(StringUtils.isNotEmpty(username))
            queryWrapper.eq("username",username);
        User user=userService.getOne(queryWrapper);
        String msg=null;
        if(user==null)
            msg="系统中不存在该用户！";
        else
            msg="查询成功！";
        return Result.success(msg,user);
    }

    //管理员查看用户个人信息
    @GetMapping("/getAdminPersonInfo")
    @RequiresPermissions("user:listStatus")
    public Result adminGetPersonInfo(@RequestParam(required = true,value = "id")Integer id,
                                @RequestParam(required = true,value = "username")String username){
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("id",id);
        if(StringUtils.isNotEmpty(username))
            queryWrapper.eq("username",username);
        queryWrapper.select("id","username","nickname","email","phone","address","create_time","status");
        User user=userService.getOne(queryWrapper);
        String msg=null;
        if(user==null)
            msg="系统中不存在该用户！";
        else
            msg="查询成功！";
        return Result.success(msg,user);
    }

    //查询个人权限信息
    @RequiresAuthentication
    @GetMapping("/getPermission")
    public Result getPermission(@RequestParam String username){
         Map res = permissionService.getUserPermission(username);
         return Result.success(res);
    }

    //查询所有读者列表
    @RequiresPermissions("user:list")
    @GetMapping("/listUsers")
    public Result getAll(@RequestParam (required = false,defaultValue = "0")Integer curPage,
                         @RequestParam (required = false,defaultValue = "5")Integer size){
        Page<User> page=new Page<>(curPage,size);
        QueryWrapper<User> select = new QueryWrapper<User>().select("id", "username","nickname", "create_time", "last_time", "status").ne("username","admin");
        IPage<User> res = userService.page(page, select);
        return Result.success(res);
    }

    //注销账号
    @RequiresPermissions(value = "user:delete")
    @GetMapping("/delete")
    public Result removeUser(@RequestParam String username){
        //查询用户是否存在
        User one = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if(one==null)
            return Result.fail("用户不存在，注销失败！");
        //查看是否存在未还书籍
        List<Orders> list = ordersService.list(new QueryWrapper<Orders>().eq("user_id", one.getUsername()).eq("expireStatus", 2));
        if(list.size()>0)
            return Result.fail("还存在未还书籍，请先归还图书");
        boolean res = userService.remove(new QueryWrapper<User>().eq("username", username));
        if(res)
            return Result.success("注销成功！");
        else
            return Result.fail("注销失败！");
    }

    //批量修改用户借阅权限
    @RequiresPermissions("user:modify")
    @RequestMapping("/modifyUser")
    public Result modifyUser(@RequestParam List<Integer> list,@RequestParam Integer status){
        //查询是否存在该用户
        boolean flag=true;
        User temp=null;
        for(Integer id:list){
            temp=userService.getById(id);
            if(temp==null)
            {
                flag=false;
                break;
            }
        }
        if(!flag)
           return Result.fail("用户不存在!!!");
        else{
           ordersService.modifyUserStatus(list,status);
        }
        return Result.success("更新状态成功！");
    }
}
