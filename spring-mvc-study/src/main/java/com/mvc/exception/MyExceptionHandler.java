package com.mvc.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

//@Component
public class MyExceptionHandler implements HandlerExceptionResolver {

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,Exception ex) {
        ModelAndView result = new ModelAndView( new MappingJackson2JsonView());

        // 根据不同错误转向不同页面
        if(ex instanceof BusinessException) {
            Map<String,Object> model = new HashMap<>();
            return new ModelAndView("/error-business", model);  //这个是返回页面的方式
        }else if(ex instanceof ParameterException) {
            ParameterException parameterException = (ParameterException)ex;
            result.addObject("errorMessage", parameterException.getMessage()); //这是返回json格式的。
            return result;
        } else {
            //默认错误
            return new ModelAndView("error");
        }
    }
}