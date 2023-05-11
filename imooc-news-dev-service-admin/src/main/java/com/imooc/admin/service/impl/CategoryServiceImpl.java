package com.imooc.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.admin.mapper.CategoryMapper;
import com.imooc.admin.service.CategoryService;
import com.imooc.pojo.Category;
import org.springframework.stereotype.Service;

/**
* @author qianzhikang
* @description 针对表【category(新闻资讯文章的分类（或者称之为领域）)】的数据库操作Service实现
* @createDate 2023-05-11 13:39:53
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService {

}




