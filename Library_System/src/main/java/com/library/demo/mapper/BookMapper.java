package com.library.demo.mapper;

import com.library.demo.entity.Book;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.demo.entity.Orders;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xjx
 * @since 2020-11-25
 */
public interface BookMapper extends BaseMapper<Book> {
    List<Orders> queryRent(List<Book> bookList);
}
