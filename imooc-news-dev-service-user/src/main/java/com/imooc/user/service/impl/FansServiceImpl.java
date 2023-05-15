package com.imooc.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.enums.Sex;
import com.imooc.pojo.AppUser;
import com.imooc.pojo.Fans;
import com.imooc.user.mapper.FansMapper;
import com.imooc.user.service.AppUserService;
import com.imooc.user.service.FansService;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.RegionRatioVO;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static com.imooc.api.controller.BaseController.REDIS_MY_FOLLOW_COUNTS;
import static com.imooc.api.controller.BaseController.REDIS_WRITER_FANS_COUNTS;

/**
 * @author qianzhikang
 * @description 针对表【fans(粉丝表，用户与粉丝的关联关系，粉丝本质也是用户。
 * 关联关系保存到es中，粉丝数方式和用户点赞收藏文章一样。累加累减都用redis来做。
 * 字段与用户表有些冗余，主要用于数据可视化，数据一旦有了之后，用户修改性别和省份无法影响此表，只认第一次的数据。
 * <p>
 * )】的数据库操作Service实现
 * @createDate 2023-05-08 15:08:35
 */
@Service
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans>
        implements FansService {

    @Resource
    private AppUserService appUserService;

    @Resource
    private RedisOperator redisOperator;

    @Resource
    private Sid sid;

    @Override
    public boolean isMeFollowThisWriter(String writerId, String fanId) {
        Fans one = lambdaQuery().eq(Fans::getWriterId, writerId).eq(Fans::getFanId, fanId).one();
        return one != null;
    }

    @Override
    @Transactional
    public void follow(String writerId, String fanId) {
        // 获得粉丝用户的信息
        AppUser fanInfo = appUserService.getById(fanId);

        String fanPkId = sid.nextShort();

        Fans fans = new Fans();
        fans.setId(fanPkId);
        fans.setFanId(fanId);
        fans.setWriterId(writerId);

        fans.setFace(fanInfo.getFace());
        fans.setFanNickname(fanInfo.getNickname());
        fans.setSex(fanInfo.getSex());
        fans.setProvince(fanInfo.getProvince());

        save(fans);

        // redis 作家粉丝数累加
        redisOperator.increment(REDIS_WRITER_FANS_COUNTS + ":" + writerId, 1);
        // redis 当前用户的（我的）关注数累加
        redisOperator.increment(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);
    }

    @Override
    @Transactional
    public void unfollow(String writerId, String fanId) {

        LambdaQueryChainWrapper<Fans> eq = lambdaQuery().eq(Fans::getWriterId, writerId).eq(Fans::getFanId, fanId);
        remove(eq);
        // redis 作家粉丝数累减
        redisOperator.decrement(REDIS_WRITER_FANS_COUNTS + ":" + writerId, 1);
        // redis 当前用户的（我的）关注数累减
        redisOperator.decrement(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);
    }

    @Override
    public Object queryMyFansList(String writerId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Fans> lambda = new LambdaQueryWrapper<>();
        lambda.eq(Fans::getWriterId, writerId);
        Page<Fans> pageParam = new Page<>(page,pageSize);
        Page<Fans> fansPage = page(pageParam, lambda);
        PagedGridResult result = new PagedGridResult();
        result.setPage((int) (fansPage.getCurrent()));
        result.setRecords(fansPage.getTotal());
        result.setRows(fansPage.getRecords());
        result.setTotal(fansPage.getSize());
        return result;
    }

    @Override
    public int queryFansCounts(String writerId, Sex man) {
        LambdaQueryWrapper<Fans> lambda = new LambdaQueryWrapper<>();
        lambda.eq(Fans::getWriterId, writerId).eq(Fans::getSex, man);
        long count = count(lambda);
        return (int) count;
    }

    public static final String[] regions = {"北京", "天津", "上海", "重庆",
            "河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "台湾",
            "内蒙古", "广西", "西藏", "宁夏", "新疆",
            "香港", "澳门"};

    @Override
    public Object queryRegionRatioCounts(String writerId) {
        List<RegionRatioVO> list = new ArrayList<>();
        for (String r : regions) {
            LambdaQueryWrapper<Fans> lambda = new LambdaQueryWrapper<>();
            lambda.eq(Fans::getWriterId,writerId).eq(Fans::getProvince,r);
            long count = count(lambda);
            RegionRatioVO regionRatioVO = new RegionRatioVO();
            regionRatioVO.setName(r);
            regionRatioVO.setValue((int)count);
            list.add(regionRatioVO);
        }
        return list;
    }
}




