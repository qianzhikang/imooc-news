package com.imooc.article.html;

import com.imooc.api.config.RabbitMQConfig;
import com.imooc.article.html.controller.ArticleHTMLComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class RabbitMQConsumer {

    final static Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @Resource
    private ArticleHTMLComponent articleHTMLComponent;

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_DOWNLOAD_HTML})
    public void watchQueue(String payload, Message message) {
        logger.warn("resolve MQ payload = {}",payload);
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

       if (routingKey.equalsIgnoreCase("article.download.do")) {
            String articleId = payload.split(",")[0];
            String articleMongoId = payload.split(",")[1];

            try {
                articleHTMLComponent.download(articleId, articleMongoId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
       if (routingKey.equalsIgnoreCase("article.html.download.do")) {

            String articleId = payload;
            try {
                articleHTMLComponent.delete(articleId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
