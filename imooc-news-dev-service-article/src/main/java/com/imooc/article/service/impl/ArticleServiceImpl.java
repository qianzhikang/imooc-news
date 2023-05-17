package com.imooc.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.api.config.RabbitMQDelayConfig;
import com.imooc.article.mapper.ArticleMapper;
import com.imooc.article.service.ArticleService;
import com.imooc.bo.NewArticleBO;
import com.imooc.enums.ArticleAppointType;
import com.imooc.enums.ArticleReviewLevel;
import com.imooc.enums.ArticleReviewStatus;
import com.imooc.enums.YesOrNo;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Article;
import com.imooc.pojo.Category;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author qianzhikang
 * @description 针对表【article(文章资讯表)】的数据库操作Service实现
 * @createDate 2023-05-11 14:33:41
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private Sid sid;

    @Override
    public void createArticle(NewArticleBO newArticleBO, Category temp) {
        String articleId = sid.nextShort();

        Article article = new Article();
        BeanUtils.copyProperties(newArticleBO, article);

        article.setId(articleId);
        article.setCategoryId(temp.getId());
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        article.setCommentCounts(0);
        article.setReadCounts(0);

        article.setIsDelete(YesOrNo.NO.type);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        if (article.getIsAppoint().equals(ArticleAppointType.TIMING.type)) {
            article.setPublishTime(newArticleBO.getPublishTime());
            // TODO: 2023/5/17 发送一条延迟消息到消息队列 通知进行文章定时发布
            Date endDate = newArticleBO.getPublishTime();
            Date startDate = new Date();


            //int delayTimes = (int)(endDate.getTime() - startDate.getTime());
            // FIXME: 为了测试方便，写死10s
            int delayTimes = 10 * 1000;
            MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    // 设置消息的持久
                    message.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    // 设置消息延迟的时间，单位ms毫秒
                    message.getMessageProperties()
                            .setDelay(delayTimes);
                    return message;
                }
            };
            // 发送消息
            rabbitTemplate.convertAndSend(
                    RabbitMQDelayConfig.EXCHANGE_DELAY,
                    "publish.delay.display",
                    articleId,
                    messagePostProcessor);
            System.out.println("延迟消息-定时发布文章：" + new Date());

        } else if (article.getIsAppoint().equals(ArticleAppointType.IMMEDIATELY.type)) {
            article.setPublishTime(new Date());
        }
        save(article);
        String reviewTextResult = ArticleReviewLevel.REVIEW.type;

        if (reviewTextResult
                .equalsIgnoreCase(ArticleReviewLevel.PASS.type)) {
            // 修改当前的文章，状态标记为审核通过
            this.updateArticleStatus(articleId, ArticleReviewStatus.SUCCESS.type);
        } else if (reviewTextResult
                .equalsIgnoreCase(ArticleReviewLevel.REVIEW.type)) {
            // 修改当前的文章，状态标记为需要人工审核
            this.updateArticleStatus(articleId, ArticleReviewStatus.WAITING_MANUAL.type);
        } else if (reviewTextResult
                .equalsIgnoreCase(ArticleReviewLevel.BLOCK.type)) {
            // 修改当前的文章，状态标记为审核未通过
            this.updateArticleStatus(articleId, ArticleReviewStatus.FAILED.type);
        }
    }

    @Override
    public PagedGridResult queryMyArticleList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Article> lambda = new LambdaQueryWrapper<>();
        lambda.orderByDesc(Article::getCreateTime);
        lambda.eq(Article::getPublishUserId, userId);

        if (StringUtils.isNotBlank(keyword)) {
            lambda.like(Article::getTitle, keyword);
        }

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            lambda.eq(Article::getArticleStatus, status);
        }

        if (status != null && status == 12) {
            lambda.eq(Article::getArticleStatus, ArticleReviewStatus.REVIEWING.type)
                    .or().eq(Article::getArticleStatus, ArticleReviewStatus.WAITING_MANUAL.type);
        }

        lambda.eq(Article::getIsDelete, YesOrNo.NO.type);

        if (startDate != null) {
            lambda.ge(Article::getPublishTime,startDate);
        }
        if (endDate != null) {
            lambda.le(Article::getPublishTime,endDate);
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

    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        LambdaQueryWrapper<Article> lambda = new LambdaQueryWrapper<>();
        lambda.eq(Article::getId,articleId);

        Article pendingArticle = new Article();
        pendingArticle.setArticleStatus(pendingStatus);

        boolean update = update(pendingArticle, lambda);
        if (!update) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
    }

    @Override
    public PagedGridResult queryAllArticleListAdmin(Integer status, Integer page, Integer pageSize) {




        LambdaQueryWrapper<Article> lambda = new LambdaQueryWrapper<>();
        lambda.orderByDesc(Article::getCreateTime);


        if (ArticleReviewStatus.isArticleStatusValid(status)) {
           lambda.eq(Article::getArticleStatus, status);
        }

        // 审核中是机审和人审核的两个状态，所以需要单独判断
        if (status != null && status == 12) {
            lambda.eq(Article::getArticleStatus, ArticleReviewStatus.REVIEWING.type)
                    .or().eq(Article::getArticleStatus, ArticleReviewStatus.WAITING_MANUAL.type);
        }

        //isDelete 必须是0
        lambda.eq(Article::getIsDelete, YesOrNo.NO.type);

        PagedGridResult result = new PagedGridResult();
        Page<Article> pageParams = new Page<>(page, pageSize);
        Page<Article> data = page(pageParams, lambda);
        result.setPage((int) (data.getCurrent()));
        result.setRecords(data.getTotal());
        result.setRows(data.getRecords());
        result.setTotal(data.getSize());
        return result;
    }

    @Override
    public void deleteArticle(String userId, String articleId) {
        Article pending = new Article();
        pending.setId(articleId);
        pending.setPublishUserId(userId);
        pending.setIsDelete(YesOrNo.YES.type);
        updateById(pending);
    }

    @Override
    public void withdrawArticle(String userId, String articleId) {
        Article pending = new Article();
        pending.setId(articleId);
        pending.setPublishUserId(userId);
        pending.setArticleStatus(ArticleReviewStatus.WITHDRAW.type);
        updateById(pending);
    }

    @Override
    public void updateAppointToPublish() {
        articleMapper.updateAppointToPublish();
    }

    @Override
    public void updateArticleToGridFS(String articleId, String articleHTMLToGridFS) {
        Article article = new Article();
        article.setId(articleId);
        article.setMongoFileId(articleHTMLToGridFS);
        updateById(article);
    }


}




