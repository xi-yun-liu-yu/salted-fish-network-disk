package com.xiyun.saltedfishnetdish.service;

import com.xiyun.saltedfishnetdish.pojo.User;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {
    //根据用户名查询用户
    User findByUserName(String username);
    //注册用户
    void register(String username, String password);
    //更新用户信息
    void update(String email, String nickname);
    //更新用户密码
    void password(String password);
    //更新用户头像
    void updateAvatar(String avatarUrl);
    //更新用户权限
    void updatePremId(Integer premId);
    //更新存储上限
    void updateStorageLimit(long value);
    //更新存储用量
    void userStorage(long value);
}
