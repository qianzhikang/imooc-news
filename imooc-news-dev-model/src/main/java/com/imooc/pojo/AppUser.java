package com.imooc.pojo;



import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 网站用户
 * @TableName app_user
 */
@TableName(value ="app_user")
@Data
public class AppUser implements Serializable {
    /**
     * 
     */
    @TableId(value = "id")
    private String id;

    /**
     * 手机号
     */
    @TableField(value = "mobile")
    private String mobile;

    /**
     * 昵称，媒体号
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 头像
     */
    @TableField(value = "face")
    private String face;

    /**
     * 真实姓名
     */
    @TableField(value = "realname")
    private String realname;

    /**
     * 邮箱地址
     */
    @TableField(value = "email")
    private String email;

    /**
     * 性别 1:男  0:女  2:保密
     */
    @TableField(value = "sex")
    private Integer sex;

    /**
     * 生日
     */
    @TableField(value = "birthday")
    private Date birthday;

    /**
     * 省份
     */
    @TableField(value = "province")
    private String province;

    /**
     * 城市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 区县
     */
    @TableField(value = "district")
    private String district;

    /**
     * 用户状态：0：未激活。 1：已激活：基本信息是否完善，真实姓名，邮箱地址，性别，生日，住址等，如果没有完善，则用户不能在作家中心操作，不能关注。2：已冻结。
     */
    @TableField(value = "active_status")
    private Integer activeStatus;

    /**
     * 累计已结算的收入金额，也就是已经打款的金额，每次打款后再此累加
     */
    @TableField(value = "total_income")
    private Integer totalIncome;

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