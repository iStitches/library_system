package com.library.demo.service;

import com.github.pagehelper.PageInfo;
import com.library.demo.entity.Book;
import com.baomidou.mybatisplus.extension.service.IService;
import com.library.demo.entity.Orders;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xjx
 * @since 2020-11-25
 */
public interface IBookService extends IService<Book> {
    //查询对应isbn 集合中的已经被借阅的书籍
    PageInfo<Orders> hasRent(List<Book> bookList,int curPage);
}
