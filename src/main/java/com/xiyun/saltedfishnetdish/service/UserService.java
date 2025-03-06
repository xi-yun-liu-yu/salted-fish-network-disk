package com.xiyun.saltedfishnetdish.service;

import com.xiyun.saltedfishnetdish.pojo.User;

public interface UserService {
    //根据用户名查询用户
    User findByUserName(String username);
    //注册用户
    void register(String username, String password);
    //用户登录
    void login(String username, String password);
}
