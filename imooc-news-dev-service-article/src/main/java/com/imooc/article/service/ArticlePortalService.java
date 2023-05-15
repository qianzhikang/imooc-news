package com.imooc.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Article;
import com.imooc.utils.PagedGridResult;
import com.imooc.vo.ArticleDetailVO;

/**
 * @Description 门户相关服务接口
 * @Date 2023-05-11-16-24
 * @Author qianzhikang
 */
public interface ArticlePortalService extends IService<Article> {
    PagedGridResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize);

    PagedGridResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize);

    PagedGridResult queryGoodArticleListOfWriter(String writerId);

    ArticleDetailVO queryDetail(String articleId);
}
