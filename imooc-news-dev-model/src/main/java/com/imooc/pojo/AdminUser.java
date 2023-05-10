package com.imooc.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 运营管理平台的admin级别用户
 * @TableName admin_user
 */
@TableName(value ="admin_user")
@Data
public class AdminUser implements Serializable {
    /**
     * 
     */
    @TableId(value = "id")
    private String id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 人脸入库图片信息，该信息保存到mongoDB的gridFS中
     */
    @TableField(value = "face_id")
    private String faceId;

    /**
     * 管理人员的姓名
     */
    @TableField(value = "admin_name")
    private String adminName;

    /**
     * 创建时间 创建时间
     */
    @TableField(value = "created_time")
    private Date createdTime;

    /**
     * 更新时间 更新时间
     */
    @TableField(value = "updated_time")
    private Date updatedTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}