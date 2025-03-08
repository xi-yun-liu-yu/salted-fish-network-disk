package com.xiyun.saltedfishnetdish.service.impl;

import com.xiyun.saltedfishnetdish.dao.UserMapper;
import com.xiyun.saltedfishnetdish.pojo.User;
import com.xiyun.saltedfishnetdish.service.UserService;
import com.xiyun.saltedfishnetdish.utils.Md5Util;
import com.xiyun.saltedfishnetdish.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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
    public void update(User user) {
        this.userMapper.update(user);
    }
    @Override
    public void password(String password) {
        Map<String, Object> map = (Map) ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("userId");
        String s = Md5Util.getMD5String(password);
        this.userMapper.updatePassword(userId, s);
    }
    @Override
    public void updateAvatar(String avatar) {
        Map<String, Object> map = (Map)ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("userId");
        this.userMapper.updateAvatar(userId, avatar);
    }
}
