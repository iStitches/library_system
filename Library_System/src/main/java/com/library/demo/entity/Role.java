package com.library.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author xjx
 * @since 2020-11-26
 */
@Data
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色编号 1-系统管理员  2-图书管理员 3-借阅者
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色名
     */
    @TableField
    private String roleName;

    /**
     * 创建时间
     */
    @TableField
    private Date createTime;

    /**
     * 账户状态
     */
    @TableField
    private Integer status;

    /**
     * 一个角色对应多种权限
     */
    @TableField(select = false)
    private List<Permission> permissionList;
}
