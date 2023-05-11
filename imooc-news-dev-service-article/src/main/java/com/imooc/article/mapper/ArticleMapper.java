package com.imooc.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.pojo.Article;

/**
* @author qianzhikang
* @description 针对表【article(文章资讯表)】的数据库操作Mapper
* @createDate 2023-05-11 14:33:41
* @Entity com.imooc.pojo.Article
*/
public interface ArticleMapper extends BaseMapper<Article> {

    void updateAppointToPublish();

}




