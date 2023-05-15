package com.imooc.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章评论表
 * @TableName comments
 */
@TableName(value ="comments")
@Data
public class Comments implements Serializable {
    /**
     * 
     */
    @TableId(value = "id")
    private String id;

    /**
     * 评论的文章是哪个作者的关联id
     */
    @TableField(value = "writer_id")
    private String writerId;

    /**
     * 如果是回复留言，则本条为子留言，需要关联查询
     */
    @TableField(value = "father_id")
    private String fatherId;

    /**
     * 回复的那个文章id
     */
    @TableField(value = "article_id")
    private String articleId;

    /**
     * 冗余文章标题，宽表处理，非规范化的sql思维，对于几百万文章和几百万评论的关联查询来讲，性能肯定不行，所以做宽表处理，从业务角度来说，文章发布以后不能随便修改标题和封面的
     */
    @TableField(value = "article_title")
    private String articleTitle;

    /**
     * 文章封面
     */
    @TableField(value = "article_cover")
    private String articleCover;

    /**
     * 发布留言的用户id
     */
    @TableField(value = "comment_user_id")
    private String commentUserId;

    /**
     * 冗余用户昵称，非一致性字段，用户修改昵称后可以不用同步
     */
    @TableField(value = "comment_user_nickname")
    private String commentUserNickname;

    /**
     * 冗余的用户头像
     */
    @TableField(value = "comment_user_face")
    private String commentUserFace;

    /**
     * 留言内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 留言时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}