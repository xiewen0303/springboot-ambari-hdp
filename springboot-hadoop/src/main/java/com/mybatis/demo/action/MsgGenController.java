package com.mybatis.demo.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.mybatis.demo.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatis.demo.model.User;
import com.mybatis.demo.services.impl.UserServiceImpl;

@Controller
@RequestMapping(value = "/messages")
public class MsgGenController {


    
    @ResponseBody
    @RequestMapping(value = "/LoginSgin",produces = {"application/json;charset=UTF-8"})
    private String loginSgin(HttpServletRequest request){
		ObjectMapper mapper = new ObjectMapper();
		String str = "";
		Map<String,Object> result = new HashMap<>();
		BufferedReader reader = null;
		InputStreamReader  isr= null;
		StringBuffer sb = new StringBuffer();
		try {
			isr = new InputStreamReader(request.getInputStream());
			reader = new BufferedReader(isr);
			String msg = null;
			while((msg = reader.readLine()) != null){
                sb.append(msg);
            }

			System.out.println("req params = "+sb);

			JsonNode rootNode = mapper.readTree(sb.toString());

			String code  = rootNode.get("code").textValue();

			String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wx9797bbc1c51568bd&secret=02af7eede8d852458df4a4ff270e0e85&js_code="+code+"&grant_type=authorization_code";
			String data = HttpClientMocker.requestMockGet(url);

			if(data ==null || "".equals(data)){
				result.put("code",-1);
			}else{
				JsonNode resultNode  = mapper.readTree(data);
				result.put("openid",resultNode.get("openid").textValue());
				result.put("tick",System.currentTimeMillis());
				result.put("sign",HttpUtil.sign(result));
				result.put("ip","116.62.137.42");
				result.put("port",8801);
				result.put("code",0);
			}
			str = mapper.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(str);
    	return str;
    }
}
