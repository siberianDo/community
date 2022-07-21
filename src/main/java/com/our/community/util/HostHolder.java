package com.our.community.util;

import com.our.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 用于代替Session对象，持有用户
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users=new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
