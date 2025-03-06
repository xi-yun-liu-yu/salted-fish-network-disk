package com.xiyun.saltedfishnetdish.dao;

import com.xiyun.saltedfishnetdish.pojo.Users;

public interface UserMapper {
    //注册用户
    boolean addUser(Users user);
    //验证用户身份（登录用）
    boolean isUSer(String username,String password);
    //获取用户元数据


}
