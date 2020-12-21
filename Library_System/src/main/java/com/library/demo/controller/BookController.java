package com.library.demo.controller;


import cn.hutool.db.sql.Order;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.library.demo.common.Result;
import com.library.demo.entity.Book;
import com.library.demo.entity.Orders;
import com.library.demo.entity.User;
import com.library.demo.service.IBookService;
import com.library.demo.service.IOrdersService;
import com.library.demo.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xjx
 * @since 2020-11-25
 */
@RestController
@RequestMapping("/book")
public class BookController {
     @Autowired
     IBookService bookService;
     @Autowired
     IOrdersService ordersService;
     @Autowired
     IUserService userService;

     //分页查询书籍
     @GetMapping("/list")
     public Result list(@RequestParam(required = false)String bookname,
                        @RequestParam(required = false)String isbn,
                        @RequestParam(required = false)String author,
                        @RequestParam(required = false)String publisher,
                        @RequestParam (defaultValue = "1",value = "curPage")Integer curPage,
                        @RequestParam (required = false,defaultValue = "7",value = "size")Integer size){
          Page<Book> page=new Page<>(curPage,size);
          QueryWrapper<Book> query=new QueryWrapper<>();
          if(StringUtils.isNotEmpty(bookname)){
                    query.like("bookname",bookname);
          }
          if(StringUtils.isNotEmpty(isbn)){
               query.eq("isbn",isbn);
          }
          if(StringUtils.isNotEmpty(author)){
               query.eq("author",author);
          }
          if(StringUtils.isNotEmpty(publisher)){
               query.like("publisher",publisher);
          }
          IPage<Book> bookdata = bookService.page(page, query);
          return Result.success(bookdata);
     }

     //新增书籍
     @RequiresAuthentication
     @RequiresPermissions(value = "book:add")
     @PostMapping("/add")
     public Result addBook(@Validated  @RequestBody Book book){
          Book one = bookService.getOne(new QueryWrapper<Book>().eq("bookname", book.getBookname()));
          boolean flag=true;
          if(one!=null)
               flag=bookService.update(new UpdateWrapper<Book>().eq("bookname",one.getBookname()).set("storenum",one.getStorenum()+1));
          else
          {
               book.setStorenum(1);
               flag=bookService.save(book);
          }
          if(flag){
               return Result.success("新增成功");
          }
          else
               return Result.fail("操作失败，请重试");
     }

     /**
      * 下架图书
      * 当订单中该图书被借阅时不能下架，返回给前端正在借阅的读者信息
      * @return
      */
     @RequiresAuthentication
     @RequiresPermissions(value = "book:delete")
     @GetMapping("/delete")
     public Result deleteBook(@RequestParam String bookname,
                              @RequestParam(required = false,defaultValue = "0") int curPage){
          //查询书名对应的Isbn列表
          QueryWrapper<Book> bookquery = new QueryWrapper<Book>().eq("bookname", bookname);
          List<Book> list = bookService.list(bookquery);

          //检测是否被借阅
          PageInfo<Orders> ordersPageInfo = bookService.hasRent(list, curPage);
          if(ordersPageInfo!=null && ordersPageInfo.getTotal()>0)
               return Result.fail("下架失败，已有用户借阅该书籍",ordersPageInfo);
          else{
               boolean remove = bookService.remove(bookquery);
               if(remove)
                    return Result.success("成功下架图书");
               else
                    return Result.fail("下架失败");
          }
     }

     //借书
     @RequiresAuthentication
     @RequiresPermissions(value = "book:borrow")
     @PostMapping("/borrow")
     public Result rentBook(@RequestBody Orders orders){
          User user = userService.getOne(new QueryWrapper<User>().eq("id",orders.getUserId()).select("status"));
          if(user.getStatus()==2)
               return Result.fail("您还有过期未归还图书，不能借阅");
          Date now = new Date();
          orders.setBorrowTime(now);
          Calendar calendar=Calendar.getInstance();
          calendar.setTime(now);
          calendar.add(Calendar.MONTH,1);
          orders.setDueTime(calendar.getTime());
          orders.setExpireStatus(2);
          //更新图书库存
          Book one = bookService.getOne(new QueryWrapper<Book>().eq("isbn",orders.getIsbn()));
          UpdateWrapper<Book> updateWrapper = new UpdateWrapper<>();
          updateWrapper.eq("isbn",orders.getIsbn());
          updateWrapper.set("storenum",one.getStorenum()-1);
          bookService.update(updateWrapper);
          //添加订单
          ordersService.save(orders);
          return Result.success("借阅成功，记得还书哦");
     }

     //还书
     @GetMapping("/return")
     public Result returnBook(@RequestParam String isbn){
          //1.更新库存
          Book book = bookService.getOne(new QueryWrapper<Book>().eq("isbn", isbn));
          bookService.update(new UpdateWrapper<Book>().eq("isbn",isbn).set("storenum",book.getStorenum()+1));

          //2.查询订单
          Orders ord=ordersService.getOne(new QueryWrapper<Orders>().eq("isbn",isbn).eq("expireStatus",2));
          Integer userId=ord.getUserId();

          //3.更新订单
          UpdateWrapper<Orders> wrapper=new UpdateWrapper<Orders>().eq("user_id",userId).eq("isbn",isbn).eq("expireStatus",2)
                  .set("return_time",new Date()).set("expireStatus",1);
          boolean orderupdate = ordersService.update(wrapper);

          //4.更新用户借阅权限
          User user=userService.getOne(new QueryWrapper<User>().eq("id",userId).select("status,username"));
          SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          String nowTime = format.format(new Date());
          int count = ordersService.count(new QueryWrapper<Orders>().eq("user_id",userId)
                  .lt("due_time", nowTime).eq("expireStatus", 2));
          //重新赋予权限
          if(count==0 && user.getStatus()==2) {
               userService.update(new UpdateWrapper<User>().eq("id",userId).set("status", 1));
          }

          //5.响应
          if(orderupdate)
               return Result.success("还书成功");
          else
               return Result.fail("还书失败，请重试");
     }
}
