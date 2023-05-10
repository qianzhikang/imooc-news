package com.imooc.user.controller;

import com.imooc.api.controller.BaseController;
import com.imooc.api.controller.user.PassPortControllerApi;
import com.imooc.bo.RegistLoginBO;
import com.imooc.enums.Sex;
import com.imooc.enums.UserStatus;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.AppUser;
import com.imooc.user.service.AppUserService;
import com.imooc.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @Description 接口实现
 * @Date 2023-05-08-16-37
 * @Author qianzhikang
 */
@RestController
@RequestMapping("/passport")
public class PassPortController extends BaseController implements PassPortControllerApi {

    @Resource
    private SMSUtils smsUtils;

    @Resource
    private RedisOperator redisOperator;

    @Resource
    private AppUserService appUserService;

    @Resource
    private Sid sid;


    @Override
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest request) {
        String userIp = IPUtil.getRequestIp(request);
        // 根据ip限制60秒内不能发送短信 (redis setnx方式)
        redisOperator.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);
        // 随机验证码
        String random = (int) ((Math.random() * 9 + 1) * 100000) + "";
        // 发送短信 todo 暂时不发短信
        //smsUtils.sendSMS(mobile,random);
        // 存储验证码 5分钟有效
        redisOperator.set(MOBILE_SMSCODE + ":" + mobile, random, 300);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult doLogin(RegistLoginBO registLoginBO, BindingResult result,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        // 实体类校验
        if (result.hasErrors()) {
            HashMap<String, String> errors = getErrors(result);
            return GraceJSONResult.errorMap(errors);
        }
        // 手机号
        String mobile = registLoginBO.getMobile();
        // sms验证码
        String smsCode = registLoginBO.getSmsCode();
        // 1. 校验验证码是否匹配
        String redisSMSCode = redisOperator.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisSMSCode) || !redisSMSCode.equalsIgnoreCase(smsCode)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }
        // 2. 数据库查询用户是否存在
        AppUser user = appUserService.lambdaQuery().eq(AppUser::getMobile, mobile).one();
        if (user != null && user.getActiveStatus().equals(UserStatus.FROZEN.type)) {
            // 如果用户不为空，并且状态为冻结，则直接抛出异常，禁止登录
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        } else if (user == null) {
            // 如果用户没有注册过，则为null，需要注册信息入库
            user = new AppUser();
            user.setId(sid.nextShort());
            user.setMobile(mobile);
            user.setNickname("用户:" + DesensitizationUtil.commonDisplay(mobile));
            user.setFace("https://api.r10086.com/樱道随机图片api接口.php?自适应图片系列=Fate");
            user.setBirthday(new Date());
            user.setSex(Sex.secret.type);
            user.setActiveStatus(UserStatus.INACTIVE.type);
            user.setTotalIncome(0);
            user.setCreatedTime(new Date());
            user.setUpdatedTime(new Date());
            appUserService.save(user);
        }

        // 3. 保存用户分布式会话的相关操作
        int userActiveStatus = user.getActiveStatus();
        if (userActiveStatus != UserStatus.FROZEN.type) {
            // 保存token到redis
            String uToken = UUID.randomUUID().toString();
            redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken, 30 * 60);
            redisOperator.set(REDIS_USER_INFO + ":" + user.getId(), JsonUtils.objectToJson(user), 30 * 60);

            // 保存用户id和token到cookie中
            setCookie(request, response, "utoken", uToken, COOKIE_MONTH);
            setCookie(request, response, "uid", user.getId(), COOKIE_MONTH);
        }

        // 4. 用户登录或注册成功以后，需要删除redis中的短信验证码，验证码只能使用一次，用过后则作废
        redisOperator.del(MOBILE_SMSCODE + ":" + mobile);
        // 5. 返回用户状态
        return GraceJSONResult.ok(userActiveStatus);
    }


    @Override
    public GraceJSONResult logout(String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        setCookie(request, response, "utoken", "", COOKIE_DELETE);
        setCookie(request, response, "uid", "", COOKIE_DELETE);

        return GraceJSONResult.ok();
    }
}
