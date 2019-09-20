package com.util;


import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

public class Base64Utils {
    /**
     * 将二进制数据编码为BASE64字符串
     * @param binaryData
     * @return
     */
    public static byte[] encode(byte[] binaryData) {
    	return Base64.encodeBase64(binaryData);
    }
    
    /**
     * 将BASE64字符串恢复为二进制数据
     * @param base64String ASCII
     * @return
     */
    public static byte[] decode(String base64String) {
    	try {
			byte[] b = base64String.getBytes("ASCII");
			byte[] ret = Base64.decodeBase64(b);
			
			return ret;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
    }
}