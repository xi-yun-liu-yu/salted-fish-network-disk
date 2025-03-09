package com.xiyun.saltedfishnetdish.controller;

import com.xiyun.saltedfishnetdish.pojo.Result;
import com.xiyun.saltedfishnetdish.pojo.User;
import com.xiyun.saltedfishnetdish.service.UserService;
import com.xiyun.saltedfishnetdish.utils.JwtUtil;
import com.xiyun.saltedfishnetdish.utils.Md5Util;
import com.xiyun.saltedfishnetdish.utils.ThreadLocalUtil;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //注册
    @PostMapping({"/api/users/register/{username}/{password}"})
    private Result register(@PathVariable String username, @PathVariable String password) {
        User u = userService.findByUserName(username);
        if (u == null) {
            userService.register(username, password);
            return Result.success();
        } else {
            return Result.error("用户名已被占用");
        }
    }
    //登录
    @GetMapping({"/api/users/login/{username}/{password}"})
    private Result<String> login(@PathVariable String username, @PathVariable String password) {
        User u = userService.findByUserName(username);
        if (u == null) {
            return Result.error("用户名不存在");
        } else if (u.getPasswordHash().equals(Md5Util.getMD5String(password))) {
            Map<String, Object> claims = new HashMap();
            claims.put("id", u.getUserId());
            claims.put("username", u.getUsername());
            claims.put("userPermId", u.getUserPermId());
            String token = JwtUtil.genToken(claims);
            stringRedisTemplate.opsForValue().set(token, token,12, TimeUnit.HOURS);
            return Result.success(token);
        } else {
            return Result.error("密码错误");
        }
    }
    //查询用户信息
    @GetMapping({"/api/users/info"})
    private Result<User> userInfo() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String)map.get("username");
        User user = userService.findByUserName(username);
        return Result.success(user);
    }
    //更新用户信息
    @PutMapping({"/api/users/update"})
    public Result update(@RequestBody User user) {
        userService.update(user);
        return Result.success("已更新用户信息");
    }
    //更新用户头像
    @PatchMapping({"/api/users/avatar"})
    public Result updateAvatar(@RequestParam @URL String avatar) {
        userService.updateAvatar(avatar);
        return Result.success();
    }
    //更新用户密码
    @PutMapping({"/api/users/password/{password}/{newPassword}"})
    public Result updatePassword(@PathVariable String password,@PathVariable String newPassword,@RequestHeader("Authorization") String token) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String)map.get("username");
        User u = userService.findByUserName(username);
        if (u.getPasswordHash().equals(Md5Util.getMD5String(password))) {
            stringRedisTemplate.opsForValue().getOperations().delete(token);
            userService.password(newPassword);
            return Result.success("密码重置成功！");
        }
        return Result.error("原密码错误");

    }
    //更新用户权限等级
    @PutMapping({"/api/admin/users/prem"})
    public Result updatePremId(@RequestBody String premId) {
        userService.updatePremId(premId);
        return Result.success("用户权限已更新");
    }
    //调整存储配额
}
