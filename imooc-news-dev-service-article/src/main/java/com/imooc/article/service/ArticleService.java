package com.imooc.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.bo.NewArticleBO;
import com.imooc.pojo.Article;
import com.imooc.pojo.Category;
import com.imooc.utils.PagedGridResult;

import java.util.Date;

/**
* @author qianzhikang
* @description 针对表【article(文章资讯表)】的数据库操作Service
* @createDate 2023-05-11 14:33:41
*/
public interface ArticleService extends IService<Article> {

    void createArticle(NewArticleBO newArticleBO, Category temp);

    PagedGridResult queryMyArticleList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize);

    void updateArticleStatus(String articleId, Integer pendingStatus);

    PagedGridResult queryAllArticleListAdmin(Integer status, Integer page, Integer pageSize);

    void deleteArticle(String userId, String articleId);

    void withdrawArticle(String userId, String articleId);

    void updateAppointToPublish();

    void updateArticleToGridFS(String articleId, String articleHTMLToGridFS);
}
