package com.library.demo.mapper;

import com.library.demo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xjx
 * @since 2020-11-22
 */
public interface UserMapper extends BaseMapper<User> {
     public List<User> listAll();
}
