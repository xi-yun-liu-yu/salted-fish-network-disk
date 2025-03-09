package com.xiyun.saltedfishnetdish.dao;

import com.xiyun.saltedfishnetdish.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    //添加用户
    @Insert("insert into user(username, password_hash, created_at) " +
            " values(#{username},#{password},now()) ")
    void addUser(String username, String password);
    //根据用户名查询用户
    @Select("select * from user where username = #{username}")
    User findByUserName(String username);
    //更新用户信息
    @Update({"UPDATE user set email=#{email}, nickname=#{nickname} where user_id = #{userId} "})
    void update(User user);
    //更新用户密码
    @Update({"update user set password_hash=#{s} where user_id = #{userId}"})
    void updatePassword(Integer userId, String s);
    //更新用户头像
    @Update({"update user set avatar_url = #{avatar} where user_id = #{userId}"})
    void updateAvatar(Integer userId, String avatar);
    //更新用户权限
    @Update({"update user set user_perm_id = #{premId} where user_id = #{userId}"})
    void updatePremId(Integer userId, Integer premId);
    //更新用户存储配额
    @Update({"update user set storage_limit = #{value} where user_id = #{userId}"})
    void updateStorageLimit(Integer userId, long value);
    //更新用户存储用量
    @Update({"update user set used_storage = #{value} where user_id = #{userId}"})
    void updateStorage(Integer userId, long value);
}
