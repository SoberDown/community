package com.lxc.community;

import com.lxc.community.dao.DiscussPostMapper;
import com.lxc.community.dao.LoginTicketMapper;
import com.lxc.community.dao.MessageMapper;
import com.lxc.community.dao.UserMapper;
import com.lxc.community.entity.DiscussPost;
import com.lxc.community.entity.LoginTicket;
import com.lxc.community.entity.Message;
import com.lxc.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println(rows);
    }

    @Test
    public void setSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    /**
    添加登录凭证测试
     */
    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("123aaa");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000*60*10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    /**
     查找登录凭证/更新状态测试
     */
    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("123aaa");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("123aaa",1);
        loginTicket = loginTicketMapper.selectByTicket("123aaa");
        System.out.println(loginTicket);
    }

    /**
     私信列表测试
     */
    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list){
            System.out.println(message);
        }
//        System.out.println(" * * * * * * * * * * * * * * * * * * * * *");

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
//        System.out.println(" * * * * * * * * * * * * * * * * * * * * *");

        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message1 : list){
            System.out.println(message1);
        }
//        System.out.println(" * * * * * * * * * * * * * * * * * * * * *");

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
//        System.out.println(" * * * * * * * * * * * * * * * * * * * * *");

        count = messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(count);
    }

}
