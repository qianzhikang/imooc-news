package com.imooc.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pojo.AppUser;
import com.imooc.user.mapper.AppUserMapper;
import com.imooc.user.service.AppUserService;
import org.springframework.stereotype.Service;

/**
* @author qianzhikang
* @description 针对表【app_user(网站用户)】的数据库操作Service实现
* @createDate 2023-05-08 15:08:34
*/
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser>
    implements AppUserService {

}




