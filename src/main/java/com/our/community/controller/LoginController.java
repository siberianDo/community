package com.our.community.controller;

import com.google.code.kaptcha.Producer;
import com.our.community.dao.mapper.UserMapper;
import com.our.community.entity.User;
import com.our.community.service.UserService;
import com.our.community.util.CommunityConstant;
import com.our.community.util.CommunityUtil;
import com.our.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer producer;

    /**
     * 访问路径注入
     *
     * @return
     */
    @Value("server.servlet.context-path")
    private String contextPath;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private UserMapper userMapper;

    @Value("community.path.domain")
    private String domain;

    /**
     * 模板引擎
     */
    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 访问注册页面
     *
     * @return
     */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }


    /**
     * 访问登录页面
     *
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * 注册过程
     *
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            //返回信息
            model.addAttribute("msg", "注册成功，已经向您的邮箱发送了激活邮件，请在激活后登录");
            //跳转界面
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 邮件确认激活后后端进行激活信息确认
     *
     * @param model
     * @param userId
     * @param code
     * @return
     */
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            //返回信息
            model.addAttribute("msg", "激活成功,您的账号已经能正常使用。正在跳转登录界面，请稍后~~~");
            //跳转界面
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            //返回信息
            model.addAttribute("msg", "您已经激活过该账号，请前往登录页面登录");
            //跳转界面
            model.addAttribute("target", "/index");
        } else {
            //返回信息
            model.addAttribute("msg", "激活失败，激活码不正确！");
            //跳转界面
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    /**
     * 生成验证码图片，并传输给前台页面
     *
     * @param response
     * @param session
     */
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
//        生成验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

//        将验证码存入session
        session.setAttribute("kaptcha", text);
//        将图片输送给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }

    /**
     * 获取登录信息
     *
     * @param username
     * @param password
     * @param code
     * @param rememberMe
     * @param session
     * @param response
     * @return
     */
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(String username, String password, String code,
                        boolean rememberMe, Model model,
                        HttpSession session, HttpServletResponse response) {
        //检查验证码
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) ||
                !kaptcha.equalsIgnoreCase(code)) {//前二判断是否为空，后一比较二者是否相等（忽略大小写）
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";//否则返回登录界面
        }

        //检查账号，密码，以及登录凭证过期时间（勾选记住我，过期时间长，反之则短）
        int expired = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;//判断过期时间
        Map<String, Object> map = userService.login(username, password, expired);
        if (map.containsKey("ticket")) {//如果map返回值中包含ticket则表明登录成功，重定向到首页
            //将凭证保存到客户端
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());//cookie中存的都是字符串
            cookie.setPath(contextPath);//设置访问路径
            cookie.setMaxAge(expired);//cookie过期时间与登录凭证过期时间一致
            response.addCookie(cookie);//将cookie传送给前台页面
            return "redirect:/index";
        } else {//返回值中不包含ticket，登录失败，返回错误信息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));//用户账号出错
            model.addAttribute("passwordMsg", map.get("passwordMsg"));//用户密码出错
            return "/site/login";
        }
    }

    /**
     * 退出登录，修改登录凭证状态
     *
     * @param ticket
     * @return
     */
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";//默认get请求
    }

    /**
     * 跳转至忘记密码界面
     *
     * @return
     */
    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String goForget() {
        return "/site/forget";
    }

    /**
     * 忘记密码---发送邮件
     *
     * @param email
     * @param session
     * @return
     */
    @RequestMapping(path = "/forget/code", method = RequestMethod.GET)
    @ResponseBody
    public String getKaptcha(String email, HttpSession session, Model model) {
        /**
         * 邮箱判定问题（待完善）
        if (StringUtils.isBlank(email)) {
            model.addAttribute("emailMsg", "邮箱不能为空！");
            return "/forget";
        }
        User user=userMapper.selectByEmail(email);
        if (user==null){
            model.addAttribute("emailMsg","邮箱输入错误或未注册！");
            return "/forget";
        }
         */
        //发送邮件
        Context context = new Context();
        context.setVariable("email", email);
        //生成验证码用于重置密码
        String code = CommunityUtil.generateUUID().substring(0, 4);
        context.setVariable("verifyCode", code);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email, "找回密码", content);

        //将验证码保存到客户端
        session.setAttribute("verifyCode", code);
        return "/forget";

    }


    /**
     * 重置密码
     * @param email
     * @param model
     * @param verifyCode
     * @param session
     * @param password
     * @return
     */
    @RequestMapping(path = "/forget/password", method = RequestMethod.POST)
    public String changePassword(String email, Model model, String verifyCode,
                                 HttpSession session, String password) {
        //从客户端获取验证码
        String code = (String) session.getAttribute("verifyCode");
        //核对验证码是否正确
        if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(code)) {
            model.addAttribute("codeMsg", "验证码不能为空!");
            return "/site/forget";
        }
        if (!code.equalsIgnoreCase(verifyCode)) {
            model.addAttribute("codeMsg", "验证码输入不正确！");
            return "/site/forget";
        }
        //使用Map装用户信息
        Map<String, Object> map = userService.resetPassword(email, password);
        if (map.containsKey("user")) {
            return "redirect:/login";
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }
    }


}
