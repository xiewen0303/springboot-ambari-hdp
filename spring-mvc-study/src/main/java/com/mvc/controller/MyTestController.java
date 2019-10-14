package com.mvc.controller;

import com.mvc.exception.AssertUtil;
import com.mvc.exception.BusinessException;
import com.mvc.exception.ParameterException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class MyTestController {

    @RequestMapping("/testParameterException")
    public String testParameterException(String target) throws Exception {
        AssertUtil.assertNull(target,"target 不能为空！");
        return "";
    }

    @RequestMapping("/testBusinessException")
    public String testBusinessException(@RequestParam("target") String target) throws Exception {
        if("1".equals(target)){
            throw new BusinessException();
        }
        return "SUCCESS";
    }

    @RequestMapping("/helloWorld")
    public String testHelloWorld(){
        return "hello world";
    }
}
