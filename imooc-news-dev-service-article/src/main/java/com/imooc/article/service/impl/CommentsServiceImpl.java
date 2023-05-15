package com.imooc.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.article.mapper.CommentsMapper;
import com.imooc.article.service.ArticlePortalService;
import com.imooc.article.service.CommentsService;
import com.imooc.pojo.Comments;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.ArticleDetailVO;
import com.imooc.vo.CommentsVO;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.imooc.api.controller.BaseController.REDIS_ARTICLE_COMMENT_COUNTS;

/**
 * @author qianzhikang
 * @description 针对表【comments(文章评论表)】的数据库操作Service实现
 * @createDate 2023-05-15 13:49:21
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments>
        implements CommentsService {
    @Resource
    private Sid sid;

    @Resource
    private RedisOperator redisOperator;

    @Resource
    private ArticlePortalService articlePortalService;


    @Resource
    private CommentsMapper commentsMapper;

    @Override
    public void createComment(String articleId, String fatherId, String content, String userId, String nickname
            , String commentUserFace) {
        String commentId = sid.nextShort();
        ArticleDetailVO article
                = articlePortalService.queryDetail(articleId);

        Comments comments = new Comments();
        comments.setId(commentId);

        comments.setWriterId(article.getPublishUserId());
        comments.setArticleTitle(article.getTitle());
        comments.setArticleCover(article.getCover());
        comments.setArticleId(articleId);

        comments.setFatherId(fatherId);
        comments.setCommentUserId(userId);
        comments.setCommentUserFace(commentUserFace);
        comments.setCommentUserNickname(nickname);

        comments.setContent(content);
        comments.setCreateTime(new Date());

        save(comments);

        // 评论数累加
        redisOperator.increment(REDIS_ARTICLE_COMMENT_COUNTS + ":" + articleId, 1);
    }

    @Override
    public PagedGridResult queryArticleComments(String articleId, Integer page, Integer pageSize) {
        Page<Comments> pageParam = new Page<>(page,pageSize);
        Page<CommentsVO> list = commentsMapper.queryByArticleId(pageParam,articleId);
        PagedGridResult result = new PagedGridResult();
        result.setPage((int) (list.getCurrent()));
        result.setRecords(list.getTotal());
        result.setRows(list.getRecords());
        result.setTotal(list.getSize());
        return result;
    }
}




