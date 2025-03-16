package com.xiyun.saltedfishnetdish.controller;

import com.alibaba.fastjson.JSON;
import com.xiyun.saltedfishnetdish.pojo.FileNode;
import com.xiyun.saltedfishnetdish.pojo.Result;
import com.xiyun.saltedfishnetdish.pojo.User;
import com.xiyun.saltedfishnetdish.service.FileNodeService;
import com.xiyun.saltedfishnetdish.service.UserService;
import com.xiyun.saltedfishnetdish.utils.AliOssUtil;
import com.xiyun.saltedfishnetdish.utils.JwtUtil;
import com.xiyun.saltedfishnetdish.utils.Md5Util;
import com.xiyun.saltedfishnetdish.utils.ThreadLocalUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.apache.ibatis.annotations.Param;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Validated
@RestController
public class UserController {
//    @Autowired
//    private UserService userService;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private FileNodeService fileNodeService;
    //注册
    @Valid
    @PostMapping({"/api/users/register/{username}/{password}"})
    public Result register(@PathVariable @Pattern(regexp = "^\\S.{5,20}$") String username, @PathVariable @Pattern(regexp = "^\\S[a-zA-Z0-9]{8,32}$") String password) {
        User u = userService.findByUserName(username);
        if (u == null) {
            userService.register(username, password);
            Integer userId = userService.findByUserName(username).getUserId();
            fileNodeService.addNode(new FileNode(String.valueOf(userId),"root","folder",null, 0L,null,null));
            return Result.success();
        } else {
            return Result.error("用户名已被占用");
        }
    }
    //登录
    @Valid
    @GetMapping({"/api/users/login/{username}/{password}"})
    public Result<String> login(@PathVariable @Pattern(regexp = "^\\S.{4,20}$") String username, @PathVariable @Pattern(regexp = "^\\S[a-zA-Z0-9]{7,32}$") String password) {
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
    public Result userInfo() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String)map.get("username");
        User user = userService.findByUserName(username);
        return Result.success(JSON.toJSON(user));
    }
    //更新用户信息
    @PutMapping({"/api/users/update"})
    public Result update(@RequestParam String email, @RequestParam String nickname) {
        userService.update(email,nickname);
        return Result.success("已更新用户信息");
    }
    //更新用户头像
    @PatchMapping({"/api/users/avatar/{type}/{url}"})
    public Result updateAvatar(MultipartFile file,@PathVariable String type,@PathVariable String url) throws Exception {
        String filename = UUID.randomUUID().toString() + '.' + type;
        String newUrl = AliOssUtil.uploadFile(filename, file.getInputStream());
        AliOssUtil.deleteFile(url);
        userService.updateAvatar(newUrl);
        return Result.success(newUrl);
    }
    //更新用户密码
    @Valid
    @PutMapping({"/api/users/password/{password}/{newPassword}"})
    public Result updatePassword(@PathVariable @Pattern(regexp = "^[a-zA-Z0-9]{8,32}$") String password,@PathVariable @Pattern(regexp = "^[a-zA-Z0-9]{8,32}$") String newPassword,@RequestHeader("Authorization") String token) {
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

    //验证密码
    @Valid
    @GetMapping({"/api/users/verifyPassword/{password}"})
    public Result verifyPassword(@PathVariable @Pattern(regexp = "^[a-zA-Z0-9]{8,32}$") String password) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String)map.get("username");
        User user = userService.findByUserName(username);
        if (user.getPasswordHash().equals(Md5Util.getMD5String(password))) {
            return Result.success("验证成功");
        }else {
            return Result.error("验证失败");
        }
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
