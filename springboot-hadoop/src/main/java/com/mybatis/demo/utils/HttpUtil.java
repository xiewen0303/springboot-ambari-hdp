package com.mybatis.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.TreeMap;

public class HttpUtil{
	
	private static final String SIGN_PARAM_NAME = "sign";


	public static void main(String[] args) {
		String code  = "033wUSh32FyEjN0Avjh32SZHh32wUSh3";//rootNode.get("code").textValue();
		String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wxbbfd22d239d212b4&secret=bd7f42ac36f26a195f762d1d2074ca6a&js_code="+code+"&grant_type=authorization_code";
		String data = HttpClientMocker.requestMockGet(url);
		System.out.println(data);
	}

	/**
	 * 拼装http服务器请求需要的url参数[工具方法],带签名
	 * @param paramObject
	 * @return
	 */
	public static String paramsMapSignToString(Map<String,Object> paramObject,String sign){
		//空判定
		if(paramObject == null){
			return "";
		}
		
		StringBuffer buf = new StringBuffer();
		for (Map.Entry<String, Object> entry : paramObject.entrySet()) {
			buf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		
		return buf.append(SIGN_PARAM_NAME).append("=").append(sign).toString();
	}
	
	/**
	 * 拼装http服务器请求需要的url参数[工具方法]
	 * @param paramObject
	 * @return
	 */
	public static String paramsMapToString(Map<String,Object> paramObject){
		//空判定
		if(paramObject == null){
			return "";
		}
		
		StringBuffer buf = new StringBuffer();
		for (Map.Entry<String, Object> entry : paramObject.entrySet()) {
			buf.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		
		return buf.toString();
	}
	
	
	/**
	 * 执行http调用 [工具方法]
	 * @param url	游戏服务器url
	 * @param paramStr	参数
	 * @return
	 */
	public static String excuteHttpCall(String url,String paramStr){
		return HttpClientMocker.requestMockPost(url, paramStr);
	}

	public static final String secret = "wind";

	public static String sign(Map<String,Object> data){
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String,Object> t = new TreeMap<>();
			t.putAll(data);
			return Md5Utils.md5To32(mapper.writeValueAsString(t)+secret); // MD5加密
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
