package com.library.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * <p>
 * 
 * </p>
 *
 * @author xjx
 * @since 2020-11-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    @TableField
    @Length(min = 5,message = "用户名必须大于5位")
    private String username;

    /**
     * 密码
     */
    @TableField
    @Pattern(regexp = "^[a-z A-Z 0-9]{6,}",message = "密码必须为大于6位的字母数字组合")
    private String password;

    /**
     * 昵称
     */
    @TableField
    @Length(min = 5,max = 15,message = "昵称必须大于5位小于15位")
    private String nickname;

    /**
     * 邮箱
     */
    @TableField
    @Email(message = "不是规则的邮箱格式！")
    private String email;

    /**
     * 电话号码
     */
    @TableField
    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,1,2,5-9])|(177))\\d{8}$",message = "不符合电话号码规范")
    private String phone;

    /**
     * 住址
     */
    @TableField
    private String address;

    /**
     * 性别  1-男  2-女
     */
    @TableField
    private Integer sex;

    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastTime;

    /**
     * 账户状态 1-有效   2-无效
     */
    @TableField
    private Integer status;

    /**
     * 角色
     */
    @TableField
    private Integer roleId;

    /**
     * 密码加密的盐
     */
    @TableField
    private String salt;

    /**
     * 一个用户多份借书单
     */
    @TableField(select = false)
    private List<Orders> ordersList;

    /**
     * 一个用户对应一个角色
     */
    @TableField(select = false)
    private Role role;
}
