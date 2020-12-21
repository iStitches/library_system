package com.library.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.library.demo.entity.Orders;
import com.library.demo.mapper.OrdersMapper;
import com.library.demo.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.MultiPixelPackedSampleModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xjx
 * @since 2020-11-26
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService
{
    @Autowired
    OrdersMapper mapper;


    @Override
    public void modifyUserByOrder(List<Orders> orders, Integer status) {
        mapper.modifyUserByOrder(orders,status);
    }

    @Override
    public void modifyUserStatus(List<Integer> ids,Integer status) {
        mapper.modifyUserStatus(ids,status);
    }


    @Override
    public List<Orders> listExpiredOrder() {
        //查询未还而且过期的订单
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = format.format(new Date(System.currentTimeMillis()));
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("expireStatus",2).gt("due_time",nowTime).select("distinct user_id");
        return mapper.selectList(queryWrapper);
    }

    @Override
    public PageInfo listHistory(String startTime, String endTime,Integer userId,Integer curPage, Integer pageSize) {
        PageInfo res=null;
        List<Orders> ordersList=null;
        PageHelper.startPage(curPage,pageSize);
        if(userId==null)
            ordersList = mapper.listHistory(startTime, endTime, null,null);
        else
            ordersList = mapper.listHistory(startTime, endTime, userId,null);
        res=new PageInfo(ordersList);
        return res;
    }

    @Override
    public PageInfo listBookHistory(String startTime, String endTime, String isbn, Integer curPage, Integer pageSize) {
        PageInfo res=null;
        PageHelper.startPage(curPage,pageSize);
        List<Orders> ordersList =mapper.listHistory(startTime,endTime,null,isbn);
        res=new PageInfo(ordersList);
        return res;
    }

    @Override
    public PageInfo listAllUserHistory(Integer curPage,Integer pageSize) {
        PageInfo res=null;
        PageHelper.startPage(curPage,pageSize);
        List<Orders> ordersList=mapper.listHistory(null,null,null,null);
        res=new PageInfo(ordersList);
        return res;
    }

}
