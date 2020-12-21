package com.library.demo.controller;

import cn.hutool.db.sql.Order;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.library.demo.common.Result;
import com.library.demo.entity.Book;
import com.library.demo.entity.Orders;
import com.library.demo.entity.User;
import com.library.demo.mapper.OrdersMapper;
import com.library.demo.service.IBookService;
import com.library.demo.service.IOrdersService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xjx
 * @since 2020-11-26
 */
@RestController
@RequestMapping("/order")
public class    OrdersController {
    @Autowired
    IOrdersService ordersService;
    @Autowired
    IBookService bookService;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    //查询个人借阅历史--用户
    @RequiresAuthentication
    @GetMapping("/oneuserDetail")
    public Result getUserhistory(@RequestParam(required = true,value = "userId")Integer userId,
                             @RequestParam(required = true,value = "username")String username,
                             @RequestParam(required = false,value = "startTime")String startTime,
                             @RequestParam(required = false,value = "endTime")String endTime,
                             @RequestParam(required = false,value = "curPage",defaultValue = "0")Integer curPage,
                             @RequestParam(required = false,value = "pageSize",defaultValue = "6")Integer pageSize){
        PageInfo pageInfo=null;
        //检验是否是本人
        User user = (User) redisTemplate.opsForValue().get(userId.toString());

        if(user==null || !user.getUsername().equals(username))
            return Result.fail("不是本人不能进行查询！！！");
        else {
            if(StringUtils.isEmpty(startTime)){
                Long registTime=user.getCreateTime().getTime();
                Date date = new Date(registTime);
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                startTime= format.format(date);
            }
        }
        pageInfo=ordersService.listHistory(startTime,endTime,userId,curPage,pageSize);
        if(pageInfo!=null)
            return Result.success("查询成功",pageInfo);
        else
            return Result.fail("您还没有借阅记录！");
    }


    //查询所有人的借阅历史---管理员
    @RequiresPermissions("user:history")
    @GetMapping("/allUserDetail")
    public Result getAllUserHistory(@RequestParam(required = false,value = "userID")Integer userId,
                                    @RequestParam(required = false,value = "curPage",defaultValue = "0")Integer curPage,
                                    @RequestParam(required = false,value = "pageSize",defaultValue = "6")Integer pageSize){
        PageInfo pageInfo=null;
        //userId为空则为查询所有
        if(userId==null)
            pageInfo=ordersService.listAllUserHistory(curPage,pageSize);
        //查询某个人
        else{
            pageInfo=ordersService.listHistory(null,null,userId,curPage,pageSize);
        }
        return Result.success(pageInfo);
    }



    //查询某本书的借阅记录---管理员
    @RequiresPermissions("book:history")
    @GetMapping("/bookDetail")
    public Result getBookhistory(@RequestParam(required = true,value = "isbn")String isbn,
                             @RequestParam(required = false,value = "startTime")String startTime,
                             @RequestParam(required = false,value = "endTime")String endTime,
                             @RequestParam(required = false,value = "curPage",defaultValue = "0")Integer curPage,
                             @RequestParam(required = false,value = "pageSize",defaultValue = "6")Integer pageSize){
        PageInfo pageInfo=ordersService.listBookHistory(startTime,endTime,isbn,curPage,pageSize);
        if(pageInfo.getTotal()==0)
            return Result.fail("没有查询到该书的借阅记录");
        return Result.success(pageInfo);
    }


    //筛选过期未还图书，修改用户status
    @GetMapping("/scanExpired")
    public Result scanExpired(){
        List<Orders> list = ordersService.listExpiredOrder();
        if(list.size()>0)
           //根据订单内容修改对应用户账号状态
           ordersService.modifyUserByOrder(list,2);
        return Result.success();
    }
}
