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
    //验证用户身份（登录用）
    boolean isUSer(String username,String password);
    //根据用户名查询用户
    @Select("select * from user where username = #{username}")
    User findByUserName(String username);
    //获取用户元数据
    @Update({"UPDATE user set email=#{email}, nickname=#{nickname} where user_id = #{userId} "})
    void update(User user);

    @Update({"update user set password_hash=#{s} where user_id = #{userId}"})
    void updatePassword(Integer userId, String s);

    @Update({"update user set avatar_url = #{avatar} where user_id = #{userId}"})
    void updateAvatar(Integer userId, String avatar);

}
