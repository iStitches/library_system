package com.library.demo.service.impl;

import com.library.demo.entity.User;
import com.library.demo.mapper.UserMapper;
import com.library.demo.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xjx
 * @since 2020-11-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> listAll() {
        return userMapper.listAll();
    }
}
