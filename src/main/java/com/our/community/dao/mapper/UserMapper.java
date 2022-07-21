package com.our.community.dao.mapper;

import com.our.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

//    根据ID查询用户
    User selectById(int id);
//    根据用户名查询用户
    User selectByName(String username);
//    根据邮箱查询用户
    User selectByEmail(String email);
//    添加用户
    int insertUser(User user);
//    修改用户状态根据用户ID
    int updateStatus(int id,int status);
//    修改用户头像根据用户ID
    int updateHeader(int id,String headerUrl);
//    修改用户密码根据用户ID
    int updatePassword(int id,String password);

}
