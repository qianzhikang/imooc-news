package com.imooc.article.controller.test;

import com.imooc.pojo.Article;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @Description freemarker测试控制器
 * @Date 2023-05-15-15-48
 * @Author qianzhikang
 */
@Controller
@RequestMapping("free")
public class FreemarkerController {

    @GetMapping("/hello")
    public String hello(Model model) {
        String str = "qzk.com";
        model.addAttribute("there", str);
        makeModel(model);
        return "stu";
    }

    private Model makeModel(Model model) {

        Stu stu = new Stu();
        stu.setUid("10010");
        stu.setUsername("imooc1");
        stu.setAmount(88.86f);
        stu.setAge(18);
        stu.setHaveChild(true);
        stu.setBirthday(new Date());

        Spouse spouse = new Spouse();
        spouse.setUsername("Lucy");
        spouse.setAge(25);

        stu.setSpouse(spouse);

        stu.setArticleList(getArticles());
        stu.setParents(getParents());
        model.addAttribute("stu", stu);


        return model;
    }

    private List<Article> getArticles() {
        Article article1 = new Article();
        article1.setId("1001");
        article1.setTitle("今天天气不错");

        Article article2 = new Article();
        article2.setId("1002");
        article2.setTitle("今天下雨了");

        Article article3 = new Article();
        article3.setId("1003");
        article3.setTitle("昨天下雨了");

        List<Article> list = new ArrayList<>();
        list.add(article1);
        list.add(article2);
        list.add(article3);
        return list;
    }

    private Map<String, String> getParents() {
        Map<String, String> parents = new HashMap<>();
        parents.put("father", "LiLei");
        parents.put("mother", "HanMeimei");
        return parents;
    }


    @Value("${freemarker.html.target}")
    private String htmlTarget;

    @GetMapping("/createHtml")
    @ResponseBody
    public String createHtml(Model model) throws Exception {
        // 0. 配置freemarker基本环境
        Configuration cfg = new Configuration(Configuration.getVersion());
        String classpath = this.getClass().getResource("/").getPath();
        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        // 1. 获得现有的模板ftl文件
        Template template = cfg.getTemplate("stu.ftl", "utf-8");

        // 2. 获得动态数据
        String stranger = "qzk";
        model.addAttribute("there", stranger);
        model = makeModel(model);

        // 3. 融合动态数据和ftl，生成html
        File tempDic = new File(htmlTarget);
        if (!tempDic.exists()) {
            tempDic.mkdirs();
        }

        Writer out = new FileWriter(htmlTarget + File.separator + "10010" + ".html");
        template.process(model, out);
        out.close();
        return "ok";
    }
}
