package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.dao.mapper.DiscussPostMapper;
import com.nowcoder.community.dao.mapper.LoginTicketMapper;
import com.nowcoder.community.dao.mapper.UserMapper;
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

    @Test
    public void testSelectUser(){
        User user1 = userMapper.selectById(101);
        System.out.println(user1);

        User user2 = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user2);

        User user3 = userMapper.selectByName("liubei");
        System.out.println(user3);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("123");
        user.setEmail("lala@163.com");
        user.setSalt("abc");
        user.setType(0);
        user.setStatus(0);
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
    }

    @Test
    public void updateUserPassword(){
        User user1 = userMapper.selectById(101);
        System.out.println(user1);

        userMapper.updatePassword(101,"liubei123");

        System.out.println(user1);

        User user2 = userMapper.selectById(101);
        System.out.println(user2);
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }

    @Test
    public void testInsertTickets(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(111);
        loginTicket.setTicket("aaa");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10)); // 10分钟后到期

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("aaa");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("aaa",1);
        loginTicket = loginTicketMapper.selectByTicket("aaa");
        System.out.println(loginTicket);
    }

}
