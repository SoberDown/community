package com.lxc.community;

import com.lxc.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    //模板引擎
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("xz3123920544@gmail.com","Test","welcome");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","SanJin");

        String htmlMail = templateEngine.process("/mail/Demo", context);
        System.out.println(htmlMail);

        mailClient.sendMail("xz3123920544@gmail.com","HtmlTest",htmlMail);

    }



}
