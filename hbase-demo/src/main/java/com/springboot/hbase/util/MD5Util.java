package com.springboot.hbase.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    private static final Logger log = LoggerFactory.getLogger(MD5Util.class);

    private MD5Util() {
    }

    public static String md5(String srcStr) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var3) {
            log.error("create md5 error ,stacktrack:", var3);
        }

        md.update(srcStr.getBytes());
        byte[] resultData = md.digest();
        return byteArrayToHex(resultData);
    }

    public static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        byte[] var4 = byteArray;
        int var5 = byteArray.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            byte b = var4[var6];
            resultCharArray[index++] = hexDigits[b >>> 4 & 15];
            resultCharArray[index++] = hexDigits[b & 15];
        }

        return new String(resultCharArray);
    }
}