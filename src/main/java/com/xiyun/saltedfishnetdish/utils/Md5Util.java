package com.xiyun.saltedfishnetdish.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
    // 禁止实例化
    private Md5Util() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * 将字符串进行MD5加密
     * @param input 原始字符串
     * @return 32位小写MD5哈希值，null输入返回null
     */
    public static String getMD5String(String input) {
        if (input == null) {
            return null;
        }

        try {
            //获取MD5摘要实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            //计算哈希值（使用UTF-8编码处理字符串）
            byte[] hashBytes = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            //将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            //理论上不会发生，MD5是所有Java实现必须支持的算法
            throw new RuntimeException("MD5算法不可用", e);
        }
    }
}
