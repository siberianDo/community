package com.our.community.controller;

import com.our.community.annotation.LoginRequired;
import com.our.community.dao.mapper.UserMapper;
import com.our.community.entity.User;
import com.our.community.service.UserService;
import com.our.community.util.CommunityUtil;
import com.our.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserMapper userMapper;

    /**
     * 前往设置界面
     *
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }


    /**
     * 保存头像路径信息
     *
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "空文件!");
            return "/site/setting";
        }
        //getOriginalFilename获取原始文件名
        String fileName = headerImage.getOriginalFilename();
        String suffix = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf("."));//保留文件后缀名
        if (
                StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确!");
            return "/site/setting";
        }
        //生成文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" + fileName);
        System.out.println(dest);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常", e);
        }
        //更新用户当前用户头像路径(并不是头像保存在本地磁盘的路径)
        // localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.UpdateHeader(user.getId(), headerUrl);
        //重定向到首页
        return "redirect:/index";
    }

    /**
     * 显示头像信息
     *
     * @param fileName
     * @param response
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //服务器存放路径：uploadPath/fileName
        fileName =uploadPath+"/"+fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int b;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     * @param model
     * @return
     */
    @RequestMapping(path = "/update", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        if (oldPassword == null) {
            model.addAttribute("oldPasswordMsg", "原密码不能为空！");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        //加密输入的原密码，再与数据库密码相比
        String password = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!Objects.equals(password, user.getPassword())) {
            model.addAttribute("oldPasswordMsg", "原密码不正确！");
            return "/site/setting";
        }
        if (oldPassword.equals(newPassword)) {
            model.addAttribute("newPasswordMsg", "新密码不能与原密码一致！");
            return "/site/setting";
        }
        //加密密码，然后更新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(user.getId(), newPassword);
        return "redirect:/login";
    }


}
