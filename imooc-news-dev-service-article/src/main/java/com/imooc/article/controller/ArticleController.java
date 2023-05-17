package com.imooc.article.controller;

import com.imooc.api.config.RabbitMQConfig;
import com.imooc.api.controller.BaseController;
import com.imooc.api.controller.article.ArticleControllerApi;
import com.imooc.article.service.ArticleService;
import com.imooc.bo.NewArticleBO;
import com.imooc.enums.ArticleCoverType;
import com.imooc.enums.ArticleReviewStatus;
import com.imooc.enums.YesOrNo;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Category;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import com.imooc.vo.AppUserVO;
import com.imooc.vo.ArticleDetailVO;
import com.mongodb.client.gridfs.GridFSBucket;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.*;
import java.util.*;

@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    final static Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    @Resource
    private RedisOperator redisOperator;

    @Value("${freemarker.html.article}")
    private String htmlTarget;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public GraceJSONResult createArticle(@Valid NewArticleBO newArticleBO,
                                         BindingResult result) {

        // 判断BindingResult是否保存错误的验证信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }

        // 判断文章封面类型，单图必填，纯文字则设置为空
        if (newArticleBO.getArticleType().equals(ArticleCoverType.ONE_IMAGE.type)) {
            if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
            }
        } else if (newArticleBO.getArticleType().equals(ArticleCoverType.WORDS.type)) {
            newArticleBO.setArticleCover("");
        }

        // 判断分类id是否存在
        String allCatJson = redisOperator.get(REDIS_ALL_CATEGORY);
        Category temp = null;
        if (StringUtils.isBlank(allCatJson)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        } else {
            List<Category> catList =
                    JsonUtils.jsonToList(allCatJson, Category.class);
            for (Category c : catList) {
                if (c.getId().equals(newArticleBO.getCategoryId())) {
                    temp = c;
                    break;
                }
            }
            if (temp == null) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
        }


        articleService.createArticle(newArticleBO, temp);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryMyList(String userId,
                                       String keyword,
                                       Integer status,
                                       Date startDate,
                                       Date endDate,
                                       Integer page,
                                       Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        // 查询我的列表，调用service
        PagedGridResult grid = articleService.queryMyArticleList(userId,
                keyword,
                status,
                startDate,
                endDate,
                page,
                pageSize);

        return GraceJSONResult.ok(grid);
    }

    @Override
    public GraceJSONResult queryAllList(Integer status, Integer page, Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = articleService.queryAllArticleListAdmin(status, page, pageSize);

        return GraceJSONResult.ok(gridResult);
    }

    @Override
    public GraceJSONResult doReview(String articleId, Integer passOrNot) {

        Integer pendingStatus;
        if (passOrNot.equals(YesOrNo.YES.type)) {
            // 审核成功
            pendingStatus = ArticleReviewStatus.SUCCESS.type;
        } else if (passOrNot.equals(YesOrNo.NO.type)) {
            // 审核失败
            pendingStatus = ArticleReviewStatus.FAILED.type;
        } else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        // 保存到数据库，更改文章的状态为审核成功或者失败
        articleService.updateArticleStatus(articleId, pendingStatus);

        if (pendingStatus.equals(ArticleReviewStatus.SUCCESS.type)) {
            // 审核成功，生成静态化页面
            try {
                // 将文件上传到mongo的文件数据库返回主健
                String articleHTMLToGridFS = createArticleHTMLToGridFS(articleId);
                // 将mongo中的文件id存入数据库
                articleService.updateArticleToGridFS(articleId, articleHTMLToGridFS);
                // doDownloadArticleHTML(articleId, articleHTMLToGridFS);
                // 通知消费者下载
                doDownloadArticleHTMLByMQ(articleId, articleHTMLToGridFS);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return GraceJSONResult.ok();
    }


    @Resource
    private RabbitTemplate rabbitTemplate;

    private void doDownloadArticleHTMLByMQ(String articleId, String articleMongoId) {
        logger.warn("do MQ mongoId = {}",articleMongoId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE,
                "article.download.do",
                articleId + "," + articleMongoId);

    }


    private void doDownloadArticleHTML(String articleId, String articleMongoId) {

        String url =
                "http://html.imoocnews.com:8002/article/html/download?articleId="
                        + articleId +
                        "&articleMongoId="
                        + articleMongoId;
        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        int status = responseEntity.getBody();
        if (status != HttpStatus.OK.value()) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }
    }

    @Override
    public GraceJSONResult delete(String userId, String articleId) {
        articleService.deleteArticle(userId, articleId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult withdraw(String userId, String articleId) {
        articleService.withdrawArticle(userId, articleId);
        return GraceJSONResult.ok();
    }


    @Resource
    private GridFSBucket gridFSBucket;

    // 文章生成HTML
    public String createArticleHTMLToGridFS(String articleId) throws Exception {
        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        Template template = cfg.getTemplate("detail.ftl", "utf-8");

        // 获得文章的详情数据
        ArticleDetailVO detailVO = getArticleDetail(articleId);
        Map<String, Object> map = new HashMap<>();
        map.put("articleDetail", detailVO);

        String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        InputStream inputStream = IOUtils.toInputStream(htmlContent);
        ObjectId fileId = gridFSBucket.uploadFromStream(detailVO.getId() + ".html", inputStream);
        return fileId.toString();
    }


    private void createHtml(String articleId) throws IOException, TemplateException {
        // 配置freemarker的基本环境
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 获取classpath的绝对路径
        String classpath = this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath + "templates"));
        Template template = configuration.getTemplate("detail.ftl", "utf-8");
        File file = new File(htmlTarget);
        if (!file.exists()) {
            file.mkdirs();
        }
        Writer out = new FileWriter(htmlTarget + File.separator + articleId + ".html");
        ArticleDetailVO articleDetail = getArticleDetail(articleId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("articleDetail", articleDetail);
        template.process(map, out);
        out.close();
    }

    public ArticleDetailVO getArticleDetail(String articleId) {
        String url
                = "http://www.imoocnews.com:8001/portal/article/detail?articleId=" + articleId;
        ResponseEntity<GraceJSONResult> responseEntity
                = restTemplate.getForEntity(url, GraceJSONResult.class);
        GraceJSONResult bodyResult = responseEntity.getBody();
        ArticleDetailVO detail = null;
        if (bodyResult.getStatus() == 200) {
            String json = JsonUtils.objectToJson(bodyResult.getData());
            detail = JsonUtils.jsonToPojo(json, ArticleDetailVO.class);
        }
        return detail;
    }
}
