package com.library.demo.service;

import cn.hutool.db.sql.Order;
import com.github.pagehelper.PageInfo;
import com.library.demo.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xjx
 * @since 2020-11-26
 */
public interface IOrdersService extends IService<Orders> {
    //根据orders更新用户状态
    void modifyUserByOrder(List<Orders> orders,Integer status);

    //批量更新用户借阅状态
    void modifyUserStatus(List<Integer> ids,Integer status);

    //列举出所有用户已过期的订单
    List<Orders> listExpiredOrder();

    //用户查询自己的历史记录
    PageInfo listHistory(String startTime, String endTime, Integer userId,Integer curPage, Integer pageSize);

    //查询图书借阅历史
    PageInfo listBookHistory(String startTime,String endTime,String isbn,Integer curPage,Integer pageSize);

    //管理员查询所有借阅记录
    PageInfo listAllUserHistory(Integer curPage,Integer pageSize);
}

