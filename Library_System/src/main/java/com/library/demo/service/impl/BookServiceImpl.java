package com.library.demo.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.library.demo.entity.Book;
import com.library.demo.entity.Orders;
import com.library.demo.mapper.BookMapper;
import com.library.demo.service.IBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xjx
 * @since 2020-11-25
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {
    @Autowired
    BookMapper bookMapper;

    @Override
    public PageInfo<Orders> hasRent(List<Book> bookList,int curPage) {
         PageHelper.startPage(curPage,6);
        List<Orders> ordersList = bookMapper.queryRent(bookList);
        PageInfo<Orders> pageInfo = new PageInfo<>(ordersList);
        return pageInfo;
    }
}
