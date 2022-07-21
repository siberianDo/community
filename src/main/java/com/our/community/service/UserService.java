package com.our.community.service;

import com.our.community.dao.mapper.LoginTicketMapper;
import com.our.community.dao.mapper.UserMapper;
import com.our.community.entity.LoginTicket;
import com.our.community.entity.User;
import com.our.community.util.CommunityConstant;
import com.our.community.util.CommunityUtil;
import com.our.community.util.MailClient;
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
    private MailClient mailClient;

    /**
     * 模板引擎
     */
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 激活码链接   域名
     */
    @Value("${community.path.domain}")
    private String domain;

    /**
     * 项目路径
     */
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //验证是否存在该账号
        User oldUser = userMapper.selectByName(user.getUsername());
        if (oldUser != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }

        //验证邮箱
        oldUser = userMapper.selectByEmail(user.getEmail());
        if (oldUser != null) {
            map.put("emailMsg", "该邮箱已存在！");
            return map;
        }

        //注册用户
//        密码加密----1.生成随机5位字符串
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
//        2.加密,覆盖用户密码，传入数据库
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //其他信息录入
        //默认用户类型
        user.setType(0);
        //默认状态
        user.setStatus(0);
        //激活码，随机字符串
        user.setActivationCode(CommunityUtil.generateUUID());
        //随机头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //创建时间
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context = new Context();
        //传输数据给模板
        context.setVariable("email", user.getEmail());
        //激活路径：http://localhost:8080/community/activation/激活用户id/激活码
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活社区账号", content);

        return map;
    }

    /**
     * 返回激活状态
     * 数据：激活码，用户ID
     *
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
//            更新用户状态
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 用户登录生成登录凭证
     *
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        //验证账号状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    /**
     * 用户退出，更新凭证表上面的状态
     *
     * @param ticket
     */
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }


    /**
     * 查询凭证
     *
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }


    /**
     * 更新用户头像地址
     *
     * @param userId
     * @param headerUrl
     * @return
     */
    public int UpdateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    /**
     * 重置密码
     * @param email
     * @param password
     * @return
     */
    public Map<String, Object> resetPassword(String email, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        } else if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        //检查邮箱
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "邮箱输入不正确或未注册！");
            return map;
        }

        //以上检查正确后开始重置密码
        password =CommunityUtil.md5(password+user.getSalt());
        userMapper.updatePassword(user.getId(),password);

        map.put("user",user);
        return map;
    }

}
