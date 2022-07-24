package com.our.community;

import com.our.community.dao.mapper.DiscussPostMapper;
import com.our.community.dao.mapper.LoginTicketMapper;
import com.our.community.dao.mapper.MessageMapper;
import com.our.community.dao.mapper.UserMapper;
import com.our.community.entity.DiscussPost;
import com.our.community.entity.LoginTicket;
import com.our.community.entity.Message;
import com.our.community.entity.User;
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
        user.setPassword("test");
        user.setSalt("test");
        user.setEmail("test");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        //修改状态
        int rows = userMapper.updateStatus(150, 1);
        System.out.println(rows);

        //修改头像
        rows = userMapper.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

        //修改密码
        rows = userMapper.updatePassword(150, "123456");
        System.out.println(rows);
    }

    @Test
    public void testSelectDiscussPost() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    /**
     * 测试登录凭证的增加
     */
    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);

    }

    /**
     * 测试登录凭证状态查询及修改
     */
    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    /**
     * 测试message的各个测试方法
     */
    @Test
    public void testSelectMessage() {
//        //1
        List<Message> messageList1 = messageMapper.selectConversations(111, 0, 20);
        for (Message m : messageList1) {
            System.out.println(m.getContent());
        }
        //2
        System.out.println(messageMapper.selectConversationCount(111));
//        //3
        List<Message> messageList2 = messageMapper.selectDetailByConversationId("111_112", 0, 10);
        for (Message m : messageList2) {
            System.out.println(m.getContent());
        }
//        //4
        System.out.println(messageMapper.selectDetailConversationCount("111_112"));
//        //5
        System.out.println(messageMapper.selectUnreadConversation(111, null));
    }

}
