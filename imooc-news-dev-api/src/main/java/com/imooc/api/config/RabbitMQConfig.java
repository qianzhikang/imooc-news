package com.imooc.api.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description rabbitmq config
 * @Date 2023-05-16-15-12
 * @Author qianzhikang
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 1.定义交换机的名字
     */
    public static final String EXCHANGE_ARTICLE = "exchange_article";

    /**
     * 2.定义队列的名字
     */
    public static final String QUEUE_DOWNLOAD_HTML = "queue_article";


    /**
     * 3. 创建交换机
     */
    @Bean(EXCHANGE_ARTICLE)
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_ARTICLE).durable(true).build();
    }

    /**
     * 4. 创建队列
     */
    @Bean(QUEUE_DOWNLOAD_HTML)
    public Queue queue() {
        return new Queue(QUEUE_DOWNLOAD_HTML);
    }

    /**
     * 队列绑定交换机
     */
    @Bean
    public Binding binding(
            @Qualifier(EXCHANGE_ARTICLE) Exchange exchange,
            @Qualifier(QUEUE_DOWNLOAD_HTML) Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("article.#.do")
                .noargs();
    }


}
