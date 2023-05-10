package com.imooc.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 粉丝表，用户与粉丝的关联关系，粉丝本质也是用户。
关联关系保存到es中，粉丝数方式和用户点赞收藏文章一样。累加累减都用redis来做。
字段与用户表有些冗余，主要用于数据可视化，数据一旦有了之后，用户修改性别和省份无法影响此表，只认第一次的数据。


 * @TableName fans
 */
@TableName(value ="fans")
@Data
public class Fans implements Serializable {
    /**
     * 
     */
    @TableId(value = "id")
    private String id;

    /**
     * 作家用户id
     */
    @TableField(value = "writer_id")
    private String writerId;

    /**
     * 粉丝用户id
     */
    @TableField(value = "fan_id")
    private String fanId;

    /**
     * 粉丝头像
     */
    @TableField(value = "face")
    private String face;

    /**
     * 粉丝昵称
     */
    @TableField(value = "fan_nickname")
    private String fanNickname;

    /**
     * 粉丝性别
     */
    @TableField(value = "sex")
    private Integer sex;

    /**
     * 省份
     */
    @TableField(value = "province")
    private String province;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}