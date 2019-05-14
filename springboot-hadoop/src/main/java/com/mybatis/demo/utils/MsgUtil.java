package com.mybatis.demo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author wind
 * @date 2018年8月15日
 **/
public class MsgUtil {
	
	public static Object genLock = new Object();
	
	public static void printLog(InputStream stream) {
		InputStreamReader inputStreamReader = null;
		BufferedReader br = null;
		try {
			inputStreamReader = new InputStreamReader(stream, "utf-8");
			br = new BufferedReader(inputStreamReader);
			String msg = null;
			while((msg = br.readLine())!=null){
				System.out.println("errorMsg:===="+msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(br != null){
					br.close();
				}
				if(inputStreamReader != null){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
