package com.imooc.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Comments;
import com.imooc.utils.PagedGridResult;

/**
* @author qianzhikang
* @description 针对表【comments(文章评论表)】的数据库操作Service
* @createDate 2023-05-15 13:49:21
*/
public interface CommentsService extends IService<Comments> {

    void createComment(String articleId, String fatherId, String content, String userId, String nickname,String commentUserFace);

    PagedGridResult queryArticleComments(String articleId, Integer page, Integer pageSize);

    PagedGridResult queryWriterCommentsMng(String writerId, Integer page, Integer pageSize);
}
