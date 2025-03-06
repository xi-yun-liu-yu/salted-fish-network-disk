package com.xiyun.saltedfishnetdish.service.impl;

import com.xiyun.saltedfishnetdish.dao.UserMapper;
import com.xiyun.saltedfishnetdish.pojo.User;
import com.xiyun.saltedfishnetdish.service.UserService;
import com.xiyun.saltedfishnetdish.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User findByUserName(String username) {
        return userMapper.findByUserName(username);
    }

    @Override
    public void register(String username, String password) {
        //加密
        String s = Md5Util.getMD5String(password);
        //添加
        userMapper.addUser(username,s);
    }

    @Override
    public void login(String username, String password) {
        String s = Md5Util.getMD5String(password);
        userMapper.findByUserName(username);
    }
}
