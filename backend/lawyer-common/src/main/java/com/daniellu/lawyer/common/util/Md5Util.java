package com.daniellu.lawyer.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 * 用于生成字符串的MD5值
 *
 * @author Daniel Lu
 * @since 2026-01-16
 */
public class Md5Util {

    private static final String MD5_ALGORITHM = "MD5";
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * 生成字符串的MD5值
     */
    public static String generateMd5(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        try {
            MessageDigest md = MessageDigest.getInstance(MD5_ALGORITHM);
            byte[] hash = md.digest(input.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_CHARS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }
}