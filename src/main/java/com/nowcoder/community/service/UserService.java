package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.LoginTicketMapper;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient; // 注入邮件客户端

    @Autowired
    private TemplateEngine templateEngine; // 注入模板引擎

    // 发的注册邮件中要包含激活码，其中要包含域名和项目，所以得把配置文件中的域名也给注入进来
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 对空值处理
        if(user == null)
            throw new IllegalArgumentException("参数不能为空！");

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        // 验证账号是否重复
        User u = userMapper.selectByName(user.getUsername());
        if(u != null) {
            map.put("usernameMsg", "该账号已经存在");
            return map;
        }

        // 验证邮箱是否重复
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 到这里，账号，密码，邮箱都不为空且账号和邮箱都没被注册过

        // 注册用户 （实际上就是把数据传入到数据库中）

        // 1. 对用户注册设置的密码进行加密，覆盖注册的密码
        user.setSalt(CommunityUtil.generateUUID().substring(0,5)); // [0,...,4]
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // 2. 因为传进来的只有账号，邮箱和密码，我们还要对user的其他字段进行设置
        user.setType(0); // 普通用户
        user.setStatus(0); // 未激活
        user.setActivationCode(CommunityUtil.generateUUID()); // 激活码
        user.setHeaderUrl(String.format(
                "http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        // 给用户设置一个随机头像（用的牛客网的头像库）  %: 占位符
        user.setCreateTime(new Date()); // 当前时间
        // 3. 将user添加到数据库中
        userMapper.insertUser(user);

        // 4. 给用户发送激活邮件
        Context context = new Context();
        context.setVariable("username", user.getUsername()); // <b th:text="${username}">xxx@xxx.com</b>, 您好!
        // http://localhost:8080/community/activation/101/activationCode 激活链接
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        // 注册的时候传进来的user是没有id的，但是最后我们调用insertUser将user添加到了数据库中，mybatis会自动生成它的id对它进行回填
        // 配置文件里面的自动生成id：mybatis.configuration.use-generated-keys=true
        // 配置文件里写的：
        // community.path.domain=http://localhost:8080
        // server.servlet.context-path=/community （配置文件最上面写的这个项目名）
        // 然后我们把他们注入到了UserService里面：
        //    @Value("${community.path.domain}")
        //    private String domain;
        //
        //    @Value("${server.servlet.context-path}")
        //    private String contextPath;

        context.setVariable("url",url);    //<a th:href="${url}">此链接</a>
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活邮件",content);
        // 点进去sendEmail方法我们会发现这个：helper.setText(content, true); 所以它会自动判断是不是html然后转换

        // 激活

        return map; // 前面都成功的话最后会返回一个空的map，不然上面判断那里就return了
    }

    public int activation(int userId, String code) {

        User user = userMapper.selectById(userId);

        if(user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (code.equals(user.getActivationCode())) {
            userMapper.updateStatus(userId,1); // 将用户的激活状态改为激活--1
            return ACTIVATION_SUCCESS;
        } else { // 激活状态未激活--0，并且传进来的code不等于激活码 （避免有人可能伪造code）
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds) {

        Map<String, Object> map = new HashMap<>();

        // 空值判断

        // 账号和密码是否为空我就不写了，因为前端也有这个逻辑

        // 判断账号是否存在
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg","该账号不存在！");
            return map;
        }

        // 到这一步说明账号存在，不然直接returnmap了
        // 验证账号是否激活
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password+user.getSalt()); // md5加密算法对同一个字符串返回的是同一个值
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "该密码不正确！");
            return map;
        }

        // 生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        // 转换成毫秒要乘1000，这句话意思是把loginTicket里的expired即过期时间设为当前时间+expiredSeconds秒后过期
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket()); // 最终要把凭证ticket发给客户端
        return map;
    }

    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId,headerUrl);
    }

    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword) {

        Map<String, Object> map = new HashMap<>();

        // 验证原始密码
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "密码输错了！笨蛋");
            return map;
        }

        // 更新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPassword);

        return map;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

}
