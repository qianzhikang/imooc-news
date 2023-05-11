package com.imooc.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.article.mapper.ArticleMapper;
import com.imooc.article.service.ArticlePortalService;
import com.imooc.enums.ArticleReviewStatus;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Article;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @Description 门户相关服务
 * @Date 2023-05-11-16-24
 * @Author qianzhikang
 */
@Service
public class ArticlePortalServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticlePortalService {

    @Override
    public PagedGridResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Article> lambda = new LambdaQueryWrapper<>();
        lambda.orderByDesc(Article::getCreateTime);

        lambda.eq(Article::getIsAppoint,YesOrNo.NO.type);
        lambda.eq(Article::getIsDelete,YesOrNo.NO.type);
        lambda.eq(Article::getArticleStatus,ArticleReviewStatus.SUCCESS.type);

        if (StringUtils.isNotBlank(keyword)) {
            lambda.like(Article::getTitle, keyword);
        }
        if (category != null) {
            lambda.eq(Article::getCategoryId, category);
        }
        PagedGridResult result = new PagedGridResult();
        Page<Article> pageParams = new Page<>(page, pageSize);
        Page<Article> data = page(pageParams, lambda);
        result.setPage((int) (data.getCurrent()));
        result.setRecords(data.getTotal());
        result.setRows(data.getRecords());
        result.setTotal(data.getSize());
        return result;
    }
}
