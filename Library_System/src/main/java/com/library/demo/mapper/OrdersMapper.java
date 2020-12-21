package com.library.demo.mapper;

import cn.hutool.db.sql.Order;
import com.library.demo.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xjx
 * @since 2020-11-26
 */
public interface OrdersMapper extends BaseMapper<Orders> {
     //查询某本书的借阅历史、 用户查询自己的借阅历史
     List<Orders> listHistory(@Param("startTime")String startTime,@Param("endTime")String endTime,@Param("userId")Integer userId,
                              @Param("isbn")String isbn);

     //批量更新用户借阅状态
     void modifyUserStatus(@Param("ids")List<Integer> ids, @Param("status") Integer status);

     //结合orders更新借阅状态
     void modifyUserByOrder(@Param("orders")List<Orders> orders,@Param("status")Integer status);
}
