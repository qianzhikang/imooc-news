# 幕课新闻

## 运行环境

- apache-tomcat-9.0.74
- mongodb
- jdk8
- mysql8.0
- minio
- rabbitMQ
- 云服务器

## 目录说明

```
imooc-news-dev-api       //api接口定义集合
imooc-news-dev-common    //公共
imooc-news-dev-model     //model模型类
imooc-news-dev-service-admin //后台管理 
imooc-news-dev-service-file  //文件上传
imooc-news-dev-service-user  //用户模块
imooc-news-dev-service-article //文章服务
imooc-news-dev-service-article-html //静态化文章服务
imooc-news       // 前端页面包
```

## Switch-host 本地域名配置

```host
192.168.6.121 www.imoocnews.com
192.168.6.121 writer.imoocnews.com
192.168.6.121 admin.imoocnews.com

192.168.6.121 article.imoocnews.com
192.168.6.121 user.imoocnews.com
192.168.6.121 files.imoocnews.com
```

## 部署运行

1. 启动tomcat，修改为9090端口
2. 将imooc-news复制入tomcat的webapp目录
3. 启动tomcat
4. 运行项目中五个Application启动类



作家登陆：writer.imoocnews.com:9090/imooc-news/writer/passport.html

管理登陆：admin.imoocnews.com:9090/imooc-news/admin/login.html