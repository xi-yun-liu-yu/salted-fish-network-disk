package com.xiyun.saltedfishnetdish.utils;

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
