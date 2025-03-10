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
            claims.put("userId", u.getUserId());
            claims.put("username", u.getUsername());
            claims.put("userPermId", u.getUserPermId());
            String token = JwtUtil.genToken(claims);
            stringRedisTemplate.opsForValue().set(token, token,12, TimeUnit.HOURS);
            stringRedisTemplate.opsForValue().set("userStorageLimit", String.valueOf(u.getStorageLimit()),12, TimeUnit.HOURS);
            stringRedisTemplate.opsForValue().set("userStorage", String.valueOf(u.getUsedStorage()),12, TimeUnit.HOURS);
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
            System.out.println(newPassword);
            return Result.success("密码重置成功！");
        }
        return Result.error("原密码错误");

    }
    //更新用户权限等级
    @PutMapping({"/api/admin/users/prem/{premId}"})
    public Result updatePremId(@PathVariable Integer premId) {
        userService.updatePremId(premId);
        return Result.success("用户权限已更新");
    }
    //调整存储配额
    @PutMapping({"/api/admin/users/storage/{operation}/{value}"})
    public Result setStorageLimit(@PathVariable String operation,@PathVariable Long value){
        String userStorage = stringRedisTemplate.opsForValue().get("userStorage");
        String userStorageLimit = stringRedisTemplate.opsForValue().get("userStorageLimit");
        String storage;
        if (userStorage != null && userStorageLimit != null) {
            switch (operation) {
                case "INCREASE":
                    storage = String.valueOf(Long.parseLong(userStorageLimit) + value);
                    break;
                case "DECREASE":
                    if (Long.parseLong(userStorageLimit) - value >= Long.parseLong(userStorage)) {
                        storage = String.valueOf(Long.parseLong(userStorageLimit) - value);
                    }else {
                        return Result.error("未使用的空间不足");
                    }
                    break;
                case "SET":
                    storage = String.valueOf(value);
                    break;
                default:
                    return Result.error("非法操作！！！");
            }
            userService.updateStorageLimit(Long.parseLong(storage));
            stringRedisTemplate.opsForValue().set("userStorageLimit", storage);
            return Result.success("内存上限调整成功！");
        }
        return Result.error("用户信息缺失，请重新登录或联系管理员。");

    }

    //更新已用存储空间
//    @PutMapping({"/api/internal/users/storage/{operation}/{value}"})
    public Result setStorage( String operation, Long value){
        String userStorage = stringRedisTemplate.opsForValue().get("userStorage");
        String userStorageLimit = stringRedisTemplate.opsForValue().get("userStorageLimit");
        String storage;
        if (userStorage != null && userStorageLimit != null) {
            switch (operation) {
                case "UPLOAD":
                    if (Long.parseLong(userStorage) + value <= Long.parseLong(userStorageLimit)){
                        storage = String.valueOf(Long.parseLong(userStorage) + value);
                    }else {
                        return Result.error("未使用的空间不足");
                    }

                    break;
                case "DELETE":
                    if (Long.parseLong(userStorage) - value >= 0) {
                        storage = String.valueOf(Long.parseLong(userStorage) - value);
                    }else {
                        return Result.error("非法参数");
                    }
                    break;
                case "SYNC":
                    storage = String.valueOf(value);
                    break;
                default:
                    return Result.error("非法操作！！！");
            }
            userService.userStorage(Long.parseLong(storage));
            stringRedisTemplate.opsForValue().set("userStorage", storage);
            return Result.success("内存占有量调整成功！");
        }
        return Result.error("用户信息缺失，请重新登录或联系管理员。");

    }
}
