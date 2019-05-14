package com.mybatis.demo.action;

import com.log.LoggerHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/tools")
public class ToolsController {

    @ResponseBody
    @RequestMapping(value = "/verify",produces = {"application/json;charset=UTF-8"})
    public String verify(HttpServletRequest request){

        String remoteAddr = request.getRemoteAddr();
        LoggerHandler.debug("request verify , remote ip={}",remoteAddr);
        return "OK";
    }
}
