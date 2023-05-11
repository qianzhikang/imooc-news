package com.imooc.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.AppUser;
import com.imooc.utils.PagedGridResult;

import java.util.Date;

/**
 * @Description service
 * @Date 2023-05-11-14-01
 * @Author qianzhikang
 */
public interface AppUserMngService extends IService<AppUser> {
    /**
     * 查询管理员列表
     */
    PagedGridResult queryAllUserList(String nickname,
                                            Integer status,
                                            Date startDate,
                                            Date endDate,
                                            Integer page,
                                            Integer pageSize);

    /**
     * 冻结用户账号，或者解除冻结状态
     */
    void freezeUserOrNot(String userId, Integer doStatus);
}
