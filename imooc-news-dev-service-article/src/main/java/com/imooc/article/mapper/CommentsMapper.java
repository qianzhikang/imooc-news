package com.imooc.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Comments;
import com.imooc.vo.CommentsVO;

import java.util.List;

/**
* @author qianzhikang
* @description 针对表【comments(文章评论表)】的数据库操作Mapper
* @createDate 2023-05-15 13:49:21
* @Entity com.imooc.pojo.Comments
*/
public interface CommentsMapper extends BaseMapper<Comments> {

    Page<CommentsVO> queryByArticleId(Page page, String articleId);
}




