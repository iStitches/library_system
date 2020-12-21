package com.library.demo.service;

import com.library.demo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xjx
 * @since 2020-11-22
 */
public interface IUserService extends IService<User> {
    public List<User> listAll();
}
