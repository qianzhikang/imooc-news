package com.imooc.user.controller;

import cn.hutool.core.bean.BeanUtil;
import com.imooc.api.controller.BaseController;
import com.imooc.api.controller.user.AppUserMngControllerApi;
import com.imooc.enums.UserStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.AppUser;
import com.imooc.user.service.AppUserMngService;
import com.imooc.user.service.AppUserService;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.PublisherVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class AppUserMngController extends BaseController implements AppUserMngControllerApi {

    final static Logger logger = LoggerFactory.getLogger(AppUserMngController.class);

    @Autowired
    private AppUserMngService appUserMngService;

    @Autowired
    private AppUserService appUserService;

    @Resource
    private RedisOperator redisOperator;

    @Override
    public GraceJSONResult queryAll(String nickname,
                                    Integer status,
                                    Date startDate,
                                    Date endDate,
                                    Integer page,
                                    Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult result = appUserMngService.queryAllUserList(nickname,
                                            status,
                                            startDate,
                                            endDate,
                                            page,
                                            pageSize);

        return GraceJSONResult.ok(result);
    }

    @Override
    public GraceJSONResult userDetail(String userId) {
        return GraceJSONResult.ok(appUserService.getById(userId));
    }

    @Override
    public GraceJSONResult freezeUserOrNot(String userId, Integer doStatus) {
        if (!UserStatus.isUserStatusValid(doStatus)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_STATUS_ERROR);
        }
        appUserMngService.freezeUserOrNot(userId, doStatus);

        // 刷新用户状态：
        // 1. 删除用户会话，从而保障用户需要重新登录以后再来刷新他的会话状态
        // 2. 查询最新用户的信息，重新放入redis中，做一次更新
        redisOperator.del(REDIS_USER_INFO + ":" + userId);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryAll(String userIds) {
        if (StringUtils.isBlank(userIds)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        List<String> userIdList = JsonUtils.jsonToList(userIds, String.class);
        List<AppUser> list = appUserService.lambdaQuery().in(AppUser::getId, userIdList).list();
        List<PublisherVO> userList = new ArrayList<>();
        list.forEach(item -> {
            PublisherVO publisherVO = BeanUtil.copyProperties(item, PublisherVO.class);
            userList.add(publisherVO);
        });

        return GraceJSONResult.ok(userList);
    }
}
