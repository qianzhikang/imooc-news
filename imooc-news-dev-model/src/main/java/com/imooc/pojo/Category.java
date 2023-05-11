package com.imooc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 新闻资讯文章的分类（或者称之为领域）
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名，比如：科技，人文，历史，汽车等等
     */
    @TableField(value = "name")
    private String name;

    /**
     * 标签颜色
     */
    @TableField(value = "tag_color")
    private String tagColor;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}