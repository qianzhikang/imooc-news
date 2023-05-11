package com.imooc.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.enums.UserStatus;
import com.imooc.pojo.AppUser;
import com.imooc.user.mapper.AppUserMapper;
import com.imooc.user.service.AppUserMngService;
import com.imooc.user.service.AppUserService;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Date 2023-05-11-14-01
 * @Author qianzhikang
 */
@Service
public class AppUserMngServiceImpl extends ServiceImpl<AppUserMapper, AppUser>
        implements AppUserMngService {

    @Resource
    public AppUserMapper appUserMapper;

    @Override
    public PagedGridResult queryAllUserList(String nickname,
                                            Integer status,
                                            Date startDate,
                                            Date endDate,
                                            Integer page,
                                            Integer pageSize) {

        LambdaQueryWrapper<AppUser> lambda = new LambdaQueryWrapper<>();
        // 创建时间倒排序
        lambda.orderByDesc(AppUser::getCreatedTime);

        if (StringUtils.isNotBlank(nickname)) {
            lambda.like(AppUser::getNickname,nickname);
        }

        if (UserStatus.isUserStatusValid(status)) {
            lambda.eq(AppUser::getActiveStatus,status);
        }

        if (startDate != null) {
            // 大于开始时间
            lambda.ge(AppUser::getCreatedTime, startDate);
        }
        if (endDate != null) {
            // 小于结束时间
            lambda.le(AppUser::getCreatedTime,endDate);
        }

        Page<AppUser> pageParams = new Page<>(page, pageSize);
        Page<AppUser> appUserPage = appUserMapper.selectPage(pageParams, lambda);
        PagedGridResult result = new PagedGridResult();
        result.setPage((int) (appUserPage.getCurrent()));
        result.setRecords(appUserPage.getTotal());
        result.setRows(appUserPage.getRecords());
        result.setTotal(appUserPage.getSize());
        return result;
    }

    @Transactional
    @Override
    public void freezeUserOrNot(String userId, Integer doStatus) {
        AppUser user = new AppUser();
        user.setId(userId);
        user.setActiveStatus(doStatus);
        appUserMapper.updateById(user);
    }
}
