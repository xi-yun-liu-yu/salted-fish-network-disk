package com.xiyun.saltedfishnetdish.service;

import com.xiyun.saltedfishnetdish.pojo.User;

public interface UserService {
    //根据用户名查询用户
    User findByUserName(String username);
    //注册用户
    void register(String username, String password);
    //更新用户信息
    void update(User user);
    //更新用户密码
    void password(String password);
    //更新用户头像
    void updateAvatar(String avatar);
    //更新用户权限
    void updatePremId(Integer premId);

    void updateStorageLimit(long value);
}
