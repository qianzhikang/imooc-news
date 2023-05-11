package com.imooc.admin.controller;

import com.imooc.admin.service.CategoryService;
import com.imooc.api.controller.BaseController;
import com.imooc.api.controller.admin.CategoryMngControllerApi;
import com.imooc.bo.SaveCategoryBO;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Category;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class CategoryMngController extends BaseController implements CategoryMngControllerApi {

    final static Logger logger = LoggerFactory.getLogger(CategoryMngController.class);

    @Autowired
    private CategoryService categoryService;

    @Resource
    private RedisOperator redisOperator;

    @Override
    public GraceJSONResult saveOrUpdateCategory(@Valid SaveCategoryBO saveCategoryBO,
                                                BindingResult result) {

        // 判断BindingResult是否保存错误的验证信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }

        Category newCat = new Category();
        BeanUtils.copyProperties(saveCategoryBO, newCat);
        // id为空新增，不为空修改
        if (saveCategoryBO.getId() == null) {
            // 查询新增的分类名称不能重复存在
            Category one = categoryService.lambdaQuery().eq(Category::getName, newCat.getName()).one();
            if (one == null) {
                // 新增到数据库
                categoryService.save(newCat);
            } else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            }
        } else {
            // 查询修改的分类名称不能重复存在
            Category one = categoryService.lambdaQuery().eq(Category::getName, saveCategoryBO.getOldName()).one();
            if (one != null) {
                // 修改到数据库
                categoryService.updateById(newCat);
            } else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            }
        }
        redisOperator.del(REDIS_ALL_CATEGORY);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getCatList() {
        List<Category> categoryList = categoryService.query().list();
        return GraceJSONResult.ok(categoryList);
    }

    @Override
    public GraceJSONResult getCats() {
        // 先从redis中查询，如果有，则返回，如果没有，则查询数据库库后先放缓存，放返回
        String allCatJson = redisOperator.get(REDIS_ALL_CATEGORY);

        List<Category> categoryList = null;
        if (StringUtils.isBlank(allCatJson)) {
            categoryList = categoryService.list();
            redisOperator.set(REDIS_ALL_CATEGORY, JsonUtils.objectToJson(categoryList));
        } else {
            categoryList = JsonUtils.jsonToList(allCatJson, Category.class);
        }

        return GraceJSONResult.ok(categoryList);
    }
}
