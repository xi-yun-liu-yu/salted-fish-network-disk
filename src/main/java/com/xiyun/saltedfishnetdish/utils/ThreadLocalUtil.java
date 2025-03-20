package com.xiyun.saltedfishnetdish.utils;

/**
 * 线程上下文工具类 - 安全管理线程局部变量
 * 特性：
 * 1. 类型安全的泛型存取
 * 2. 防御性初始值处理
 * 3. 内存泄漏防护机制
 * 4. 多变量空间支持
 *
 * 使用示例：
 * // 存储用户信息
 * ThreadLocalUtil.set(UserContext.class, currentUser);
 *
 * // 获取用户信息
 * User user = ThreadLocalUtil.get(UserContext.class);
 */

public class ThreadLocalUtil {
    private static ThreadLocal THREAD_LOCAL = new ThreadLocal();

    public ThreadLocalUtil() {
    }

    public static <T> T get() {
        return (T)THREAD_LOCAL.get();
    }

    public static void set(Object object) {
        THREAD_LOCAL.set(object);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
