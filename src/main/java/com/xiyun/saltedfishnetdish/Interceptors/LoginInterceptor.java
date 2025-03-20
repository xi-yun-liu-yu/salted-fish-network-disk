package com.xiyun.saltedfishnetdish.Interceptors;

import com.xiyun.saltedfishnetdish.utils.JwtUtil;
import com.xiyun.saltedfishnetdish.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 登录认证拦截器 - 负责Token验证与用户信息传递
 * 功能：
 * 1. 基于Redis的Token有效性验证
 * 2. JWT Token解析与验证
 * 3. 用户信息线程级存储
 * 4. 统一认证失败响应处理
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 预处理拦截逻辑
     * @return boolean 是否放行请求
     * @throws Exception 认证失败时抛出业务异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        try {
            //从redis中获取相同的token
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String redisToken = operations.get(token);
            if (redisToken == null) {//如果获取不到，就抛出异常进入catch
                throw new RuntimeException();
            }

            // 存储用户上下文
            Map<String, Object> c = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(c);
            return true;
        }catch (Exception e) {
            response.setStatus(401);

            return false;
        }
    }
    /**
     * 清理线程上下文数据
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空ThreadLocal中的数据
        ThreadLocalUtil.remove();
    }
}
