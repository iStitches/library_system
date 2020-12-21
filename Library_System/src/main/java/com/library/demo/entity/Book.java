package com.library.demo.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

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
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 索书号
     */
    @NotEmpty(message = "索书号不能为空")
    private String isbn;

    /**
     * 类别
     */
    private String type;

    /**
     * 书名
     */
    @NotEmpty(message = "书名不能为空")
    private String bookname;

    /**
     * 作者
     */
    private String author;

    private String imgpath;

    private String publisher;

    private Integer storenum;

    private Float price;

    /**
     * 描述
     */
    private String description;


}
