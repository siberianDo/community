package com.our.community.controller.interceptor;

import com.our.community.entity.LoginTicket;
import com.our.community.entity.User;
import com.our.community.service.UserService;
import com.our.community.util.CookieUtil;
import com.our.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 拦截器
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从Cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效（状态，是否过期）
            if (loginTicket.getTicket() != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //根据凭证查找用户
                User user = userService.findUserById(loginTicket.getUserId());
                //让本次请求持有该用户（注意线程问题）
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    //在模板之前就需要使用User，所以要重写postHandle
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user= hostHolder.getUser();
        if (user !=null && modelAndView!=null){
                modelAndView.addObject("loginUser",user);
        }
    }

    //清理信息


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
