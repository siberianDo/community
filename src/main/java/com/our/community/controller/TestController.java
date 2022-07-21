package com.our.community.controller;

import com.our.community.service.AlphaService;
import com.our.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 测试是否能连通
 */

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Test Spring Boot.";
    }

    //测试依赖注入
    @RequestMapping("/getData")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    //获取请求、响应对象
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        System.out.println(request.getMethod());
        //请求访问路径
        System.out.println(request.getServletPath());
        //请求行（多条）
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try (
                PrintWriter writer = response.getWriter();
        ) {
            writer.write("<h1>我们的社区</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //封装get请求

    //eg.模拟分页,加入method后限制处理请求类型
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody//简单响应
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,//required可以不传该参数
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {//defaultValue默认值
        System.out.println(current + "--------------" + limit);
        return "some students";
    }

    //eg.将需要的数据编排到路径当中,查询学生编号
    @RequestMapping(path = "/students/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudentId(@PathVariable("id") int id) {//@PathVariable路径变量
        System.out.println(id);
        return "student id";
    }

    //post请求
    @RequestMapping(path = "student", method = RequestMethod.POST)
    @ResponseBody
    public String postStudentInfo(String name, int age) {
        //获取post的参数数据
        System.out.println(name + ":" + age);
        return "registered successfully";
    }

    //响应HTML数据
    @RequestMapping(path = "teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        //要响应的数据
        modelAndView.addObject("name", "张三");
        modelAndView.addObject("age", 20);
        /*
        数据上传到模板
        xmlns:th="http://www.thymeleaf.org"声明当前网页是一个模板以及模板来源
         */
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "清华大学");
        model.addAttribute("age", 111);
        return "/demo/view";
    }

    //响应JSON数据（异步请求）Java <= JSON => Js对象
    @RequestMapping(path = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "里斯");
        info.put("age", 20);
        info.put("salary", 7000.00);
        return info;
    }

    //多个类似类型数据
    @RequestMapping(path = "/infos", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getInfos() {
        List<Map<String, Object>> infosList = new ArrayList<>();
        Map<String, Object> info = new HashMap<>();
        info.put("name", "里斯");
        info.put("age", 20);
        info.put("salary", 7000.00);
        infosList.add(info);

        info = new HashMap<>();
        info.put("name", "张三");
        info.put("age", 21);
        info.put("salary", 7200.00);
        infosList.add(info);

        info = new HashMap<>();
        info.put("name", "王五");
        info.put("age", 21);
        info.put("salary", 7300.00);
        infosList.add(info);

        return infosList;
    }

    /**
     * 生成cookie
     * 存储在客户端，容量小，不安全
     *
     * @param response
     * @return
     */
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        //创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置生效范围
        cookie.setPath("/community/test");
        //设置cookie生存时间
        cookie.setMaxAge(60 * 10);
        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    /**
     * 服务端获取cookie
     *
     * @param code
     * @return
     */
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    /**
     * 生成session
     * 存储在服务端，容量大，安全，会增加服务器内存压力
     *
     * @param session
     * @return
     */
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    /**
     * 获取session
     *
     * @param session
     * @return
     */
    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }


    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        return CommunityUtil.getJSONString(0, "传输完成");
    }


}
