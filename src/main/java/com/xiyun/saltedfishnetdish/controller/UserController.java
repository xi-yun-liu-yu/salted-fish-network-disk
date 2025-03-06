package com.xiyun.saltedfishnetdish.controller;

import com.xiyun.saltedfishnetdish.pojo.Result;
import com.xiyun.saltedfishnetdish.pojo.User;
import com.xiyun.saltedfishnetdish.service.UserService;
import com.xiyun.saltedfishnetdish.utils.JwtUtil;
import com.xiyun.saltedfishnetdish.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/api/users/register")
    private Result register(String username, String password) {
        //查询用户
        User u = userService.findByUserName(username);
        if (u == null) {
            //注册
            userService.register(username,password);
            return Result.success();
        }else {
            //用户名被占用
            return Result.error("用户名已被占用");
        }

    }

    @PostMapping("/api/users/login")
    private Result<String> login(String username, String password) {
        //查询用户
        User u = userService.findByUserName(username);
        if (u == null) {
            //让用户去注册
            return Result.error("用户名不存在");
        }else {
            //用户存在
            if (u.getPasswordHash().equals(Md5Util.getMD5String(password))) {
                Map<String,Object> claims = new HashMap<>();
                claims.put("id",u.getUserId());
                claims.put("username",u.getUsername());
                String token = JwtUtil.genToken(claims);
                return Result.success(token);
            }
            return Result.error("密码错误");

        }

    }
}
