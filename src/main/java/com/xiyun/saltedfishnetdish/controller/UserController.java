package com.xiyun.saltedfishnetdish.controller;

import com.xiyun.saltedfishnetdish.pojo.Result;
import com.xiyun.saltedfishnetdish.pojo.User;
import com.xiyun.saltedfishnetdish.service.UserService;
import com.xiyun.saltedfishnetdish.utils.JwtUtil;
import com.xiyun.saltedfishnetdish.utils.Md5Util;
import com.xiyun.saltedfishnetdish.utils.ThreadLocalUtil;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping({"/api/users/register/{username}/{password}"})
    private Result register(@PathVariable String username, @PathVariable String password) {
        User u = this.userService.findByUserName(username);
        if (u == null) {
            this.userService.register(username, password);
            return Result.success();
        } else {
            return Result.error("用户名已被占用");
        }
    }

    @GetMapping({"/api/users/login/{username}/{password}"})
    private Result<String> login(@PathVariable String username, @PathVariable String password) {
        System.out.println(username);
        User u = this.userService.findByUserName(username);
        if (u == null) {
            return Result.error("用户名不存在");
        } else if (u.getPasswordHash().equals(Md5Util.getMD5String(password))) {
            Map<String, Object> claims = new HashMap();
            claims.put("id", u.getUserId());
            claims.put("username", u.getUsername());
            String token = JwtUtil.genToken(claims);
            return Result.success(token);
        } else {
            return Result.error("密码错误");
        }
    }

    @GetMapping({"/api/users/info"})
    private Result<User> userInfo() {
        Map<String, Object> map = (Map) ThreadLocalUtil.get();
        String username = (String)map.get("username");
        User user = this.userService.findByUserName(username);
        return Result.success(user);
    }

    @PutMapping({"/api/users/update"})
    public Result update(@RequestBody User user) {
        this.userService.update(user);
        return Result.success("已更新用户信息");
    }

    @PatchMapping({"/api/users/avatar"})
    public Result updateAvatar(@RequestParam @URL String avatar) {
        this.userService.updateAvatar(avatar);
        return Result.success();
    }

    @PutMapping({"/api/users/password/{password}"})
    public Result updatePassword(@PathVariable String password) {
        this.userService.password(password);
        return Result.success("密码重置成功！");
    }
}
