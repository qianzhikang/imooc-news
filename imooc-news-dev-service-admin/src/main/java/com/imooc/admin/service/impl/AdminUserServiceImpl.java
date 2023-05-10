package com.imooc.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.admin.mapper.AdminUserMapper;
import com.imooc.admin.service.AdminUserService;
import com.imooc.pojo.AdminUser;
import org.springframework.stereotype.Service;

/**
* @author qianzhikang
* @description 针对表【admin_user(运营管理平台的admin级别用户)】的数据库操作Service实现
* @createDate 2023-05-09 16:46:17
*/
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser>
    implements AdminUserService {

}




