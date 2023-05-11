package com.imooc.article.task;

import com.imooc.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @Description 定时任务发布文章
 * @Date 2023-05-11-15-44
 * @Author qianzhikang
 */
//@Configuration
//@EnableScheduling  // 开启定时任务
public class TaskPublishArticles {
    @Resource
    private ArticleService articleService;

    // 添加定时任务，注明定时任务的表达式
    @Scheduled(cron = "0/3 * * * * ?")
    private void publishArticles() {
        System.out.println("执行定时任务：" + LocalDateTime.now());

        // 调用文章service，把当前时间应该发布的定时文章，状态改为即时
        articleService.updateAppointToPublish();
    }
}
