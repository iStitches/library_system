package com.library.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author xjx
 * @since 2020-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 归属菜单码
     */
    @TableField
    private String menuCode;

    /**
     * 归属菜单名
     */
    @TableField
    private String menuName;

    /**
     * 权限码
     */
    @TableField
    private String permissionCode;

    /**
     * 权限名
     */
    @TableField
    private String permissionName;

    /**
     * 1-必备权限  2-非必须
     */
    @TableField
    private Integer requiredPermission;
}
