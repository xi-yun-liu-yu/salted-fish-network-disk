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
    //登录
    @Override
    public void register(String username, String password) {
        //加密
        String s = Md5Util.getMD5String(password);
        //添加
        userMapper.addUser(username,s);
    }
    //更新用户信息
    @Override
    public void update(User user) {
        this.userMapper.update(user);
    }
    //更新用户密码
    @Override
    public void password(String password) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("userId");
        String s = Md5Util.getMD5String(password);
        this.userMapper.updatePassword(userId, s);
    }
    //更新用户头像
    @Override
    public void updateAvatar(String avatar) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer)map.get("userId");
        this.userMapper.updateAvatar(userId, avatar);
    }
    //更新用户权限
    @Override
    public void updatePremId(Integer PremId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        this.userMapper.updatePremId(userId, PremId);
    }

    @Override
    public void updateStorageLimit(long value) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        this.userMapper.updateStorageLimit(userId, value);
    }

    @Override
    public void userStorage(long value) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        this.userMapper.updateStorage(userId, value);
    }
}
